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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.config;

import org.onap.aai.restclient.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConditionalOnExpression("'${schema.translator.list}'.contains('schema-service')")
@PropertySource(value = "classpath:schema-ingest.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${schema.ingest.file}", ignoreResourceNotFound = true)
public class RestConfiguration {

    private static final String TWO_WAY_SSL = "two-way-ssl";
    private static final String ONE_WAY_SSL = "one-way-ssl";
    private static final String NO_AUTH = "no-auth";

    @Value("${schema.service.client:two-way-ssl}")
    private String schemaServiceClient;

    @Autowired
    private RestClient restClient;

    @Bean
    public RestClientFactory restClientFactory() {

        return new RestClientFactory() {
            @Override
            public RestClient getRestClient(String clientType) {
                return restClient;

            }
        };
    }

    /*
     * In the below cases bean name and method names are different because all of them qualify as
     * restClient
     */
    @Bean(name = "restClient")
    @ConditionalOnProperty(
        name = "schema.service.client",
        havingValue = "two-way-ssl",
        matchIfMissing = true)
    public RestClient getSchemaServiceTwoWayClient() {
        return new SchemaServiceRestClient();
    }

    @Bean(name = "restClient")
    @ConditionalOnProperty(name = "schema.service.client", havingValue = "no-auth")
    public RestClient getSchemaServiceNoAuthClient() {
        return new SchemaServiceNoAuthClient();
    }

    @Bean(name = "restClient")
    @ConditionalOnProperty(name = "schema.service.client", havingValue = "one-way-ssl")
    public RestClient getSchemaServiceOneWayClient() {
        return new SchemaServiceOneWayClient();
    }

}
