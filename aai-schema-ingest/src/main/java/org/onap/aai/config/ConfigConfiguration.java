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

package org.onap.aai.config;

import org.onap.aai.setup.SchemaConfigVersions;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.setup.SchemaVersions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConditionalOnProperty(prefix = "schema.translator.list", value = "config", matchIfMissing = true)
@PropertySource(value = "classpath:schema-ingest.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${schema.ingest.file}", ignoreResourceNotFound = true)
public class ConfigConfiguration {

    @Bean(name = "schemaConfigVersions")
    @ConditionalOnMissingBean
    public SchemaConfigVersions schemaConfigVersions() {
        return new SchemaConfigVersions();
    }

    @Bean(name = "schemaVersions")
    public SchemaVersions schemaVersions() {
        return schemaConfigVersions();
    }

    @Bean(name = "schemaLocationsBean")
    public SchemaLocationsBean schemaLocationsBean() {
        return new SchemaLocationsBean();
    }
}
