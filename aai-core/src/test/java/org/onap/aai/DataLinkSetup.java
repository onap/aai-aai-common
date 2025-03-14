/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
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

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.onap.aai.config.*;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.introspection.MoxyLoader;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.rest.db.HttpEntry;
import org.onap.aai.rest.notification.NotificationService;
import org.onap.aai.serialization.db.EdgeSerializer;
import org.onap.aai.serialization.queryformats.QueryFormatTestHelper;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;
import org.onap.aai.testutils.TestUtilConfigTranslatorforDataLink;
import org.onap.aai.util.AAIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

@ContextConfiguration(
        classes = {ConfigConfiguration.class, TestUtilConfigTranslatorforDataLink.class, EdgeIngestor.class,
                EdgeSerializer.class, NodeIngestor.class, SpringContextAware.class, IntrospectionConfig.class,
                RestBeanConfig.class, XmlFormatTransformerConfiguration.class, LoaderFactory.class, NotificationService.class, KafkaConfig.class})
@TestPropertySource(
        properties = {"schema.uri.base.path = /aai", "schema.xsd.maxoccurs = 5000", "schema.version.api.default = v4",
                "schema.version.edge.label.start = v4", "schema.version.depth.start = v3",
                "schema.version.app.root.start = v4", "schema.version.related.link.start = v4",
                "schema.version.namespace.change.start = v4", "schema.version.list = v1,v2,v3,v4",
                "schema.translator.list = config","aai.notifications.enabled = false"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public abstract class DataLinkSetup {

    @ClassRule
    public static final SpringClassRule springClassRule = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    protected Map<SchemaVersion, MoxyLoader> moxyLoaderInstance;

    @Autowired
    protected HttpEntry traversalHttpEntry;

    @Autowired
    protected HttpEntry traversalUriHttpEntry;

    @Autowired
    protected NodeIngestor nodeIngestor;

    @Autowired
    protected LoaderFactory loaderFactory;

    @Autowired
    protected SchemaVersions schemaVersions;

    @Value("${schema.uri.base.path}")
    protected String basePath;

    @Value("${schema.xsd.maxoccurs}")
    protected String maxOccurs;

    protected static final String SERVICE_NAME = "JUNIT";

    @BeforeClass
    public static void setupBundleconfig() throws Exception {
        System.setProperty("AJSC_HOME", ".");
        System.setProperty("BUNDLECONFIG_DIR", "src/test/resources/bundleconfig-local");
        System.setProperty("aai.service.name", SERVICE_NAME);
    }

}
