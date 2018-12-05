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

import org.onap.aai.setup.SchemaServiceTranslator;
import org.onap.aai.setup.SchemaVersions;
import org.onap.aai.setup.SchemaVersionsBean;
import org.onap.aai.setup.Translator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnExpression("'${schema.translator.list}'.contains('schema-service')")
public class SchemaServiceConfiguration {

    @Bean(name = "schemaVersionsBean")
    public SchemaVersionsBean schemaVersionsBean() {
        return new SchemaVersionsBean();
    }

    @Bean(name = "schemaVersions")
    public SchemaVersions schemaVersions() {
        return schemaVersionsBean().getSchemaVersions();
    }

    @Bean(name = "schemaServiceTranslator")
    public Translator schemaServiceTranslator() {
        return new SchemaServiceTranslator(schemaVersions());
    }

}
