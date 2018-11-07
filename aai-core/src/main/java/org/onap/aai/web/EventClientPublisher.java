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
package org.onap.aai.web;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

@Configuration
public class EventClientPublisher {

    private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(EventClientPublisher.class);

    @Value("${dmaap.ribbon.listOfServers}")
    private String hosts;

    @Value("${dmaap.ribbon.username:}")
    private String username;

    @Value("${dmaap.ribbon.password:}")
    private String password;

    @Value("${dmaap.ribbon.topic:AAI-EVENT}")
    private String topic;

    @Value("${dmaap.ribbon.batchSize:100}")
    private int maxBatchSize;

    @Value("${dmaap.ribbon.maxAgeMs:250}")
    private int maxAgeMs;

    @Value("${dmaap.ribbon.delayBetweenBatches:100}")
    private int delayBetweenBatches;

    @Value("${dmaap.ribbon.protocol:http}")
    private String protocol;

    @Value("${dmaap.ribbon.transportType:http}")
    private String transportType;

    @Value("${dmaap.ribbon.contentType:application/json}")
    private String contentType;

    @Value("${server.ssl.trust-store:aai_keystore}")
    private String trustStoreFile;

    @Value("${server.ssl.trust-store-password:somepass}")
    private String trustStorePass;

    @Bean(name="dmaapRestTemplate")
    public RestTemplate dmaapRestTemplate() throws Exception {

        if(transportType.equals("https")){

            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

            SSLContext sslContext = SSLContextBuilder
                    .create()
                    .loadTrustMaterial(ResourceUtils.getFile(trustStoreFile), trustStorePass.toCharArray())
                    .build();

            HttpClient client = HttpClients
                    .custom()
                    .setSSLContext(sslContext)
                    .build();

            LOGGER.info("Creating a dmaap rest template with https using truststore {}", trustStoreFile);
            return restTemplateBuilder
                    .requestFactory(new HttpComponentsClientHttpRequestFactory(client))
                    .build();
        }

        LOGGER.info("Creating a dmaap rest template using http");
        return new RestTemplate();
    }

    @Bean(name="dmaapHeaders")
    public HttpHeaders dmaapHeaders() throws UnsupportedEncodingException
    {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        if(username != null && password != null){

            if(!StringUtils.EMPTY.equals(username) && !StringUtils.EMPTY.equals(password)){

                byte[] userPass = (username + ":" + password).getBytes("UTF-8");

                httpHeaders.set("Authorization", "Basic " + Base64.getEncoder().encodeToString(userPass));
            }
        }

        return httpHeaders;
    }

}
