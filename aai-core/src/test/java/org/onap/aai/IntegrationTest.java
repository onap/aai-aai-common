/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2024 Deutsche Telekom. All rights reserved.
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

package org.onap.aai;

import org.junit.jupiter.api.extension.ExtendWith;
import org.onap.aai.config.ConfigConfiguration;
import org.onap.aai.config.GraphConfig;
import org.onap.aai.config.IntrospectionConfig;
import org.onap.aai.config.KafkaConfig;
import org.onap.aai.config.RestBeanConfig;
import org.onap.aai.config.SpringContextAware;
import org.onap.aai.config.XmlFormatTransformerConfiguration;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.prevalidation.ValidationConfiguration;
import org.onap.aai.prevalidation.ValidationService;
import org.onap.aai.rest.notification.NotificationService;
import org.onap.aai.serialization.db.EdgeSerializer;
import org.onap.aai.setup.AAIConfigTranslator;
import org.onap.aai.util.GraphChecker;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {ConfigConfiguration.class, AAIConfigTranslator.class, EdgeIngestor.class, EdgeSerializer.class,
                NodeIngestor.class, SpringContextAware.class, IntrospectionConfig.class, RestBeanConfig.class,
                XmlFormatTransformerConfiguration.class, ValidationService.class, ValidationConfiguration.class,
                KafkaConfig.class, LoaderFactory.class, NotificationService.class})
@TestPropertySource(
        value = "classpath:/application.properties",
        properties = {
                "schema.uri.base.path = /aai", "schema.xsd.maxoccurs = 5000",
                "schema.translator.list=config",
                "schema.nodes.location=src/test/resources/onap/oxm",
                "schema.edges.location=src/test/resources/onap/dbedgerules",
                "aai.notifications.enabled=false","classpath:/application.properties",
        })
public class IntegrationTest {

}
