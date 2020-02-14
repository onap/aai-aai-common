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

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import javax.annotation.PostConstruct;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public abstract class NoAuthRestClient extends RestClient {

    private static EELFLogger logger = EELFManager.getInstance().getLogger(NoAuthRestClient.class);

    protected RestTemplate restTemplate;

    @PostConstruct
    public void init() throws Exception {
        HttpClient client = HttpClients.createDefault();
        restTemplate =
            new RestTemplateBuilder().requestFactory(() -> new HttpComponentsClientHttpRequestFactory(client)).build();

        restTemplate.setErrorHandler(new RestClientResponseErrorHandler());
    }

    @Override
    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

}
