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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.serialization.queryformats.QueryFormatTestHelper;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.testutils.TestUtilConfigTranslatorforEdges;
import org.onap.aai.util.AAIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

@ContextConfiguration(
    classes = {SchemaLocationsBean.class, TestUtilConfigTranslatorforEdges.class,
        EdgeIngestor.class, NodeIngestor.class})

@TestPropertySource(
    properties = {
        "schemaIngestPropLoc = src/test/resources/schemaIngest/schemaIngestTest.properties"})
public abstract class AAISetupForSwagger {

    @ClassRule
    public static final SpringClassRule springClassRule = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    protected NodeIngestor nodeIngestor;

    protected static final String SERVICE_NAME = "JUNIT";

    @BeforeClass
    public static void setupBundleconfig() throws Exception {
        System.setProperty("AJSC_HOME", ".");
        System.setProperty("BUNDLECONFIG_DIR", "src/test/resources/bundleconfig-local");
        System.setProperty("aai.service.name", SERVICE_NAME);
        QueryFormatTestHelper.setFinalStatic(AAIConstants.class.getField("AAI_HOME_ETC_OXM"),
            "src/test/resources/bundleconfig-local/etc/oxm/");
    }

}
