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

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.setup.SchemaVersions;
import org.onap.aai.setup.Translator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Import({SchemaServiceConfiguration.class, ConfigConfiguration.class, TranslatorConfiguration.class})
@PropertySource(value = "classpath:schema-ingest.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${schema.ingest.file}", ignoreResourceNotFound = true)
@Configuration
public class NodesConfiguration {

    private static final String CONFIG_TRANSLATOR = "config";
    private static final String SCHEMA_SERVICE_TRANSLATOR = "schema-service";
    private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(NodesConfiguration.class);

    @Autowired(required = false)
    SchemaServiceConfiguration schemaConfiguration;

    @Autowired(required = false)
    ConfigConfiguration configConfiguration;

    @Autowired(required = false)
    TranslatorConfiguration translatorConfiguration;

    @Value("${schema.translator.list}")
    private String[] translatorArray;

    public Set<Translator> translators() {
        Set<Translator> translators = new HashSet<>();

        List<String> translatorList = Arrays.asList(translatorArray);
        if (translatorList.contains(SCHEMA_SERVICE_TRANSLATOR)) {
            LOGGER.info("Translator is SchemaServiceTranslator");
            translators.add(schemaConfiguration.schemaServiceTranslator());
        } else {
            LOGGER.info("Translator is ConfigTranslator");
            translators.add(translatorConfiguration.configTranslator);
        }
        return translators;
    }

    @Bean(name = "nodeIngestor")
    public NodeIngestor nodeIngestor() {
        return new NodeIngestor(translators());
    }

}
