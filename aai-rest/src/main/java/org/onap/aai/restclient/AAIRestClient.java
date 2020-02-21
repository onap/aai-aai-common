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

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

@Component(value = ClientType.AAI)
public class AAIRestClient extends TwoWaySSLRestClient {

    private static Logger logger = LoggerFactory.getLogger(AAIRestClient.class);

    @Value("${aai.base.url}")
    private String baseUrl;

    @Value("${aai.ssl.key-store}")
    private String keystorePath;

    @Value("${aai.ssl.trust-store}")
    private String truststorePath;

    @Value("${aai.ssl.key-store-password}")
    private String keystorePassword;

    @Value("${aai.ssl.trust-store-password}")
    private String truststorePassword;

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    protected String getKeystorePath() {
        return keystorePath;
    }

    @Override
    protected String getTruststorePath() {
        return truststorePath;
    }

    @Override
    protected char[] getKeystorePassword() {
        return keystorePassword.toCharArray();
    }

    @Override
    protected char[] getTruststorePassword() {
        return truststorePassword.toCharArray();
    }

    @Override
    public MultiValueMap<String, String> getHeaders(Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("Real-Time", "true");
        headers.forEach(httpHeaders::add);
        return httpHeaders;
    }

}
