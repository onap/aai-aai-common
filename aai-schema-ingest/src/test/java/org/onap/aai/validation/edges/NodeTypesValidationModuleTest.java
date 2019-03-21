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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.validation.edges;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.config.NodesConfiguration;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersionsBean;
import org.onap.aai.testutils.TestUtilConfigTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {NodesConfiguration.class, TestUtilConfigTranslator.class,
        NodeTypesValidationModule.class})
@TestPropertySource(
    properties = {
        "schema.ingest.file = src/test/resources/forWiringTests/schema-ingest-wiring-test-local.properties"})
@SpringBootTest
public class NodeTypesValidationModuleTest {
    @Autowired
    NodeTypesValidationModule validator;

    @Test
    public void test() {
        List<String> testPairs = new ArrayList<>();
        testPairs.add("bar|foo");
        testPairs.add("foo|foo");
        testPairs.add("foo|quux");
        assertTrue("".equals(validator.validate(testPairs, new SchemaVersion("v11"))));
        assertTrue(validator.validate(testPairs, new SchemaVersion("v10"))
            .contains("Invalid node type(s) found: quux")); // bc no quux in v10
    }

    @Test
    public void testWeirdCases() {
        List<String> testPairs = new ArrayList<>();
        testPairs.add("bar|");
        testPairs.add("|foo");
        testPairs.add("|");
        assertTrue("".equals(validator.validate(testPairs, new SchemaVersion("v11")))); // bc empty
                                                                                        // just
                                                                                        // ignored
    }
}
