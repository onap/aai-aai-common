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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public abstract class RestClient {

    private static EELFLogger log = EELFManager.getInstance().getLogger(RestClient.class);
    @Value("${spring.application.name}")
    protected String appName;

    public ResponseEntity execute(String uri, HttpMethod method, Map<String, String> headers, String body) throws RestClientException {

        HttpEntity httpEntity;
        log.debug("Headers: " + headers.toString());
        if (body == null) {
            httpEntity = new HttpEntity(getHeaders(headers));
        } else {
            httpEntity = new HttpEntity(body, getHeaders(headers));
        }
        String url = getBaseUrl() + uri;
        return getRestTemplate().exchange(url, method, httpEntity, String.class);
    }

    public ResponseEntity executeResource(String uri, HttpMethod method, Map<String, String> headers, String body) throws RestClientException {

        HttpEntity httpEntity;
        log.debug("Headers: " + headers.toString());
        if (body == null) {
            httpEntity = new HttpEntity(getHeaders(headers));
        } else {
            httpEntity = new HttpEntity(body, getHeaders(headers));
        }
        String url = getBaseUrl() + uri;
        return getRestTemplate().exchange(url, method, httpEntity, Resource.class);
    }

    public ResponseEntity execute(String uri, String method, Map<String, String> headers) throws RestClientException {
        return execute(uri, HttpMethod.valueOf(method), headers, null);
    }

    public ResponseEntity getGetRequest(String content, String uri, Map<String, String> headersMap) {
        return this.execute(
            uri,
            HttpMethod.GET,
            headersMap,
            content);

    }

    public ResponseEntity getGetResource(String content, String uri, Map<String, String> headersMap) {
        return this.executeResource(
            uri,
            HttpMethod.GET,
            headersMap,
            content);

    }

    public abstract RestTemplate getRestTemplate();

    public abstract String getBaseUrl();

    protected abstract MultiValueMap<String, String> getHeaders(Map<String, String> headers);

    protected abstract EELFLogger getLogger();

}
