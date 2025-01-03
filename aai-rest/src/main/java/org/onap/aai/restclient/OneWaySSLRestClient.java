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

import jakarta.annotation.PostConstruct;
import org.apache.hc.client5.http.classic.HttpClient;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.onap.aai.aailog.filter.RestClientLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

public abstract class OneWaySSLRestClient extends RestClient {

    private static Logger logger = LoggerFactory.getLogger(OneWaySSLRestClient.class);

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

        char[] trustStorePassword = getTruststorePassword();

        String trustStore = getTruststorePath();

        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(ResourceUtils.getFile(trustStore), trustStorePassword).build();

        PoolingHttpClientConnectionManager connectionManager =
                PoolingHttpClientConnectionManagerBuilder.create()
                        .setTlsSocketStrategy(new DefaultClientTlsStrategy(sslContext, (s, sslSession) -> true))
                        .build();

        HttpClient client =
                HttpClients.custom()
                        .setConnectionManager(connectionManager)
                        .build();
        return client;
    }

    protected abstract String getTruststorePath();

    protected abstract char[] getTruststorePassword();

    @Override
    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

}
