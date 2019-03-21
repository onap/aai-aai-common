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

import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.serialization.db.EdgeSerializer;
import org.onap.aai.setup.AAIConfigTranslator;
import org.onap.aai.setup.ConfigTranslator;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.setup.SchemaVersions;
import org.onap.aai.validation.CheckEverythingStrategy;
import org.onap.aai.validation.SchemaErrorStrategy;
import org.onap.aai.validation.nodes.DefaultDuplicateNodeDefinitionValidationModule;
import org.onap.aai.validation.nodes.DuplicateNodeDefinitionValidationModule;
import org.onap.aai.validation.nodes.NodeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;

@Import({NodesConfiguration.class, EdgesConfiguration.class})
@Configuration
@PropertySource(value = "classpath:schema-ingest.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${schema.ingest.file}", ignoreResourceNotFound = true)
public class SchemaConfiguration {

    @Autowired(required = false)
    NodesConfiguration nodesConfiguration;

    @Autowired(required = false)
    EdgesConfiguration edgesConfiguration;

    @Bean
    public EdgeIngestor edgeIngestor() {
        return edgesConfiguration.edgeIngestor();
    }

    @Bean
    public EdgeSerializer edgeSerializer(EdgeIngestor edgeIngestor) {
        return new EdgeSerializer(edgeIngestor);
    }

    @Bean(name = "nodeIngestor")
    public NodeIngestor nodeIngestor() {
        return nodesConfiguration.nodeIngestor();
    }

    @Bean(name = "configTranslator")
    @ConditionalOnProperty(
        name = "schema.translator.list",
        havingValue = "config",
        matchIfMissing = true)
    public ConfigTranslator configTranslator(SchemaLocationsBean schemaLocationsBean,
        SchemaVersions schemaVersions) {
        return new AAIConfigTranslator(schemaLocationsBean, schemaVersions);
    }

    @Bean
    @ConditionalOnProperty(
        name = "schema.translator.list",
        havingValue = "config",
        matchIfMissing = true)
    public SchemaErrorStrategy schemaErrorStrategy() {
        return new CheckEverythingStrategy();
    }

    @Bean
    @ConditionalOnProperty(
        name = "schema.translator.list",
        havingValue = "config",
        matchIfMissing = true)
    public DuplicateNodeDefinitionValidationModule duplicateNodeDefinitionValidationModule() {
        return new DefaultDuplicateNodeDefinitionValidationModule();
    }

    @Bean
    @ConditionalOnProperty(
        name = "schema.translator.list",
        havingValue = "config",
        matchIfMissing = true)
    public NodeValidator nodeValidator(ConfigTranslator configTranslator,
        SchemaErrorStrategy schemaErrorStrategy,
        DuplicateNodeDefinitionValidationModule duplicateNodeDefinitionValidationModule) {
        return new NodeValidator(configTranslator, schemaErrorStrategy,
            duplicateNodeDefinitionValidationModule);
    }
}
