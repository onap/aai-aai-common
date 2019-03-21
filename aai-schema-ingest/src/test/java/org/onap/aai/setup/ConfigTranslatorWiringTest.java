/** 
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-18 AT&T Intellectual Property. All rights reserved.
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

package org.onap.aai.setup;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.testutils.ConfigTranslatorForWiringTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {SchemaLocationsBean.class, SchemaConfigVersions.class, ConfigTranslatorForWiringTest.class})
@TestPropertySource(
        properties = {
                "schema.ingest.file = src/test/resources/forWiringTests/schema-ingest-wiring-test-local.properties"})
@SpringBootTest
public class ConfigTranslatorWiringTest {
    @Autowired
    ConfigTranslator ct;

    @Test
    public void test() {
        assertNotNull(ct);
        Map<SchemaVersion, List<String>> nodes = ct.getNodeFiles();
        assertTrue(nodes.containsKey(new SchemaVersion("v10")));
        assertTrue(1 == nodes.get(new SchemaVersion("v10")).size());
        assertTrue("src/test/resources/oxm/test_business_v10.xml".equals(nodes.get(new SchemaVersion("v10")).get(0)));

        Map<SchemaVersion, List<String>> edges = ct.getEdgeFiles();
        assertTrue(edges.containsKey(new SchemaVersion("v10")));
        assertTrue(1 == edges.get(new SchemaVersion("v10")).size());
        assertTrue("src/test/resources/edgeRules/test.json".equals(edges.get(new SchemaVersion("v10")).get(0)));
    }

}
