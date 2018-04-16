/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.aai.config;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.Server;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class HttpPingImpl implements HttpPing, IPing {

    private static final EELFLogger logger = EELFManager.getInstance().getLogger(HttpPingImpl.class);

    private static final Base64.Encoder base64Encoder = Base64.getEncoder();
    private static final String UNABLE_TO_ESTABLISH = "Successfully connected by workaround due to unable to read own topic {}";

    // This is a workaround for the topics that the user
    // does not have the access to read their own topic status
    private static final String MR_STATUS_PATTERN = ".*\"mrstatus\`":\\s*4002.*";

    private static final int HTTPS_PORT = 3905;
    private static final int DEFAULT_TIMEOUT = 2;

    private String healthCheckEndpoint;
    private String username;
    private String password;

    private int timeout;

    private final RestTemplate restTemplate;

    public HttpPingImpl(String healthCheckEndpoint) {
        this(new RestTemplate());
        this.healthCheckEndpoint = healthCheckEndpoint;
        this.timeout = DEFAULT_TIMEOUT;
    }

    public HttpPingImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.healthCheckEndpoint = "";
        this.timeout = DEFAULT_TIMEOUT;
    }

    public HttpPingImpl() {
        this("");
    }

    public HttpPingImpl(IClientConfig clientConfig) {

        if (!(clientConfig instanceof DefaultClientConfigImpl)) {
            throw new UnsupportedOperationException("Unable to support the client config implementation: " + clientConfig.getClass().getName());
        }

        DefaultClientConfigImpl defaultClientConfig = (DefaultClientConfigImpl) clientConfig;

        Map<String, Object> map = defaultClientConfig.getProperties();

        this.setCredentials(map.get("username").toString(), map.get("password").toString());
        this.setHealthCheckEndpoint(map.get("health.endpoint").toString());
        this.setTimeoutInSecs(Integer.valueOf(map.get("pingport.timeout").toString()));

        this.restTemplate = new RestTemplate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHealthCheckEndpoint(String endpoint) {
        this.healthCheckEndpoint = endpoint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHealthCheckEndpoint() {
        return healthCheckEndpoint;
    }

    @Override
    public void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setTimeoutInSecs(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public Optional<String> getAuthorization() {

        if (username == null && password == null) {
            return Optional.empty();
        }

        if (username == null || username.isEmpty()) {
            logger.error("Username is null while the password is not correctly set");
            return Optional.empty();
        }

        if (password == null || password.isEmpty()) {
            logger.error("Password is null while the username is not correctly set");
            return Optional.empty();
        }

        String auth = String.format("%s:%s", username, password);
        return Optional.ofNullable("Basic " + base64Encoder.encodeToString(auth.getBytes()));
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public boolean isAlive(Server server) {

        String url = null;

        // If unable to ping the port then return immediately
        if (!pingPort(server)) {
            return false;
        }

        if (server.getPort() == HTTPS_PORT) {
            url = "https://";
        } else {

            url = "http://";
        }

        url = url + server.getId();
        url = url + this.getHealthCheckEndpoint();

        boolean isAlive = false;

        Optional<String> authorization = getAuthorization();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        if (authorization.isPresent()) {
            httpHeaders.add("Authorization", authorization.get());
        }

        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
        try {

            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);

            HttpStatus httpStatus = responseEntity.getStatusCode();

            if (httpStatus == HttpStatus.OK) {
                isAlive = true;
                logger.info("Successfully established connection to the following url {}", url);
                return isAlive;
            }

            logger.warn("Unable to establish a connection the following url {} due to HTTP Code {}, and reason {}",
                    url, httpStatus.value(), httpStatus.getReasonPhrase());

        } catch (HttpClientErrorException ex) {
            HttpStatus httpStatus = ex.getStatusCode();
            if (httpStatus == HttpStatus.FORBIDDEN) {
                // This is a workaround being in play for the topics
                // that are unable to read themselves for this user
                // In the case of the username and password being
                // wrong the response would be unauthorized (401) but if the
                // user is authorized but unable to read this topic, then
                // we get back the (403) with the message mrstatus 4002
                // This is a temporary workaround to properly identify which server is down
                String body = ex.getResponseBodyAsString();
                if (body.matches(MR_STATUS_PATTERN)) {
                    isAlive = true;
                    logger.info("Successfully connected by workaround due to unable to read own topic {}", url);
                    return isAlive;
                } else {
                    logger.warn(UNABLE_TO_ESTABLISH, server.getHostPort(), ex.getMessage());
                }
            } else {
                logger.warn(UNABLE_TO_ESTABLISH, server.getHostPort(), ex.getMessage());
            }
        } catch (Exception ex) {
            logger.warn(UNABLE_TO_ESTABLISH, server.getHostPort(), ex.getMessage());
        }

        return isAlive;
    }

    /**
     * Returns true if it can connect to the host and port within
     * the given timeout from the given server parameter
     *
     * @param server - server that will be taken from the src/main/resources/application.yml file
     * @return true if it can make a successful socket connection to the port on the host
     */
    public boolean pingPort(Server server) {

        String host = server.getHost();
        Integer port = server.getPort();

        boolean success = false;
        SocketAddress socketAddress = new InetSocketAddress(host, port);

        try (Socket socket = new Socket()) {
            socket.connect(socketAddress, timeout * 1000);
            if (socket.isConnected()) {
                success = true;
            }
        } catch (IOException e) {
            logger.warn("Unable to connect to the host {} on port {} due to {}", host, port, e.getLocalizedMessage());
            success = false;
        }

        return success;
    }
}
