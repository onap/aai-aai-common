/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright © 2018 IBM.
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

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

@Component(value = "schema-service-rest-client")
public class SchemaServiceRestClient extends TwoWaySSLRestClient {
    private static EELFLogger logger = EELFManager.getInstance().getLogger(SchemaServiceRestClient.class);

    @Value("${schema.service.base.url}")
    private String baseUrl;

    @Value("${schema.service.ssl.key-store}")
    private String keystorePath;

    @Value("${schema.service.ssl.trust-store}")
    private String truststorePath;

    @Value("${schema.service.ssl.key-store-password}")
    private String keystorePassword;

    @Value("${schema.service.ssl.trust-store-password}")
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
