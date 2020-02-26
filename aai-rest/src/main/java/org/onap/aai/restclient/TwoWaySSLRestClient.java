/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.aai.restclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;
import org.onap.aai.aailog.filter.RestClientLoggingInterceptor;

public abstract class TwoWaySSLRestClient extends RestClient {

    private static Logger logger = LoggerFactory.getLogger(TwoWaySSLRestClient.class);

    private RestTemplate restTemplate;

    @PostConstruct
    public void init() throws Exception {
        restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(this.getHttpRequestFactory());
        restTemplate.setErrorHandler(new RestClientResponseErrorHandler());
        RestClientLoggingInterceptor loggingInterceptor = new RestClientLoggingInterceptor();
        restTemplate.getInterceptors().add(loggingInterceptor);

    }

    protected HttpComponentsClientHttpRequestFactory getHttpRequestFactory() throws Exception {
        return new HttpComponentsClientHttpRequestFactory(this.getClient());
    }

    protected HttpClient getClient() throws Exception {

        char[] keyStorePassword = getKeystorePassword();
        char[] trustStorePassword = getTruststorePassword();

        String keyStore = getKeystorePath();
        String trustStore = getTruststorePath();

        SSLContext sslContext =
            SSLContextBuilder.create().loadKeyMaterial(loadPfx(keyStore, keyStorePassword), keyStorePassword)
                .loadTrustMaterial(ResourceUtils.getFile(trustStore), trustStorePassword).build();

        HttpClient client =
            HttpClients.custom().setSSLContext(sslContext).setSSLHostnameVerifier((s, sslSession) -> true).build();

        return client;
    }

    private KeyStore loadPfx(String file, char[] password) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        File key = ResourceUtils.getFile(file);
        try (InputStream in = new FileInputStream(key)) {
            keyStore.load(in, password);
        }
        return keyStore;
    }

    protected abstract String getKeystorePath();

    protected abstract String getTruststorePath();

    protected abstract char[] getTruststorePassword();

    protected abstract char[] getKeystorePassword();

    @Override
    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

}
