/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2018-19 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.prevalidation;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;
import org.onap.aai.restclient.NoAuthRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

public class ValidationServiceNoAuthClient extends NoAuthRestClient {


    @Value("${validation.service.base.url}")
    private String baseUrl;

    @Value("${validation.service.timeout-in-milliseconds}")
    private Integer timeout;

    @Override
    protected HttpClient getClient() throws Exception {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultSocketConfig(
            SocketConfig.custom()
                .setSoTimeout(Timeout.of(timeout, TimeUnit.MILLISECONDS))
                .build()
        );

        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(Timeout.of(timeout, TimeUnit.MILLISECONDS))
            .setResponseTimeout(Timeout.of(timeout, TimeUnit.MILLISECONDS))
            .build();

        return HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(requestConfig)
            .build();
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public MultiValueMap<String, String> getHeaders(Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();

        String defaultAccept = headers.getOrDefault(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString());
        String defaultContentType =
                headers.getOrDefault(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());

        if (headers.isEmpty()) {
            httpHeaders.setAccept(Collections.singletonList(MediaType.parseMediaType(defaultAccept)));
            httpHeaders.setContentType(MediaType.parseMediaType(defaultContentType));
        }

        httpHeaders.add("X-FromAppId", appName);
        httpHeaders.add("X-TransactionId", UUID.randomUUID().toString());
        headers.forEach(httpHeaders::add);
        return httpHeaders;
    }

}
