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
package org.onap.aai.restclient;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Component(value="no-auth-service-rest-client")
public class SchemaServiceNoAuthClient extends NoAuthRestClient{

	private static EELFLogger logger = EELFManager.getInstance().getLogger(SchemaServiceNoAuthClient.class);

	@Value("${schema.service.base.url}")
	private String baseUrl;

    @PostConstruct
    public void init () throws Exception {
        super.init();
    }

	@Override
	public String getBaseUrl() {
       return baseUrl;
	}

    @Override
	public MultiValueMap<String, String> getHeaders(Map<String, String> headers) {
		HttpHeaders httpHeaders = new HttpHeaders();

        String defaultAccept = headers.getOrDefault(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString());
        String defaultContentType = headers.getOrDefault(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());

        if(headers.isEmpty()){
            httpHeaders.setAccept(Collections.singletonList(MediaType.parseMediaType(defaultAccept)));
            httpHeaders.setContentType(MediaType.parseMediaType(defaultContentType));
        }

		httpHeaders.add("X-FromAppId", appName);
		httpHeaders.add("X-TransactionId", UUID.randomUUID().toString());
        headers.forEach(httpHeaders::add);
		return httpHeaders;
	}

	@Override
	public EELFLogger getLogger() {
		return logger;
	}
}
