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

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.restclient.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "/schemaService/schema-service-rest.properties")
@ContextConfiguration(
        classes = {RestClientFactoryConfiguration.class, SchemaServiceRestClient.class, RestClientFactory.class,
                PropertyPasswordConfiguration.class})

@SpringBootTest
public class SchemaRestClientTest {

    private String SCHEMA_SERVICE = "schema-service";
    @Autowired
    private RestClientFactory restClientFactory;

    @Test
    public void testGetRequestToSchemaService() {
        ResponseEntity aaiResponse;
        RestClient restClient = null;

        restClient = restClientFactory.getRestClient(SCHEMA_SERVICE);

        String uri = "";
        Map<String, String> headersMap = new HashMap<>();
        String content = "";
        aaiResponse = restClient.execute(uri, HttpMethod.GET, headersMap, content);
        System.out.println("Helo" + aaiResponse.getStatusCode());
    }
}
