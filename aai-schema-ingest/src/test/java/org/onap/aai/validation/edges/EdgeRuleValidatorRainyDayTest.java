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

package org.onap.aai.validation.edges;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.onap.aai.config.NodesConfiguration;
import org.onap.aai.testutils.BadEdgeConfigForValidationTest;
import org.onap.aai.validation.CheckEverythingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@ContextConfiguration(
        classes = {NodesConfiguration.class, BadEdgeConfigForValidationTest.class, CheckEverythingStrategy.class,
                DefaultEdgeFieldsValidationModule.class, UniqueLabelValidationModule.class,
                SingleContainmentValidationModule.class, CousinDefaultingValidationModule.class,
                NodeTypesValidationModule.class, EdgeRuleValidator.class})
@TestPropertySource(
        properties = {
                "schema.ingest.file = src/test/resources/forWiringTests/schema-ingest-wiring-test-local.properties"})
@SpringBootTest
public class EdgeRuleValidatorRainyDayTest {
    @Autowired
    EdgeRuleValidator validator;

    @Test
    public void test() {
        assertNotNull(validator); // verify spring wiring OK
        assertFalse(validator.validate());
        String errors = validator.getErrorMsg();
        assertTrue(errors.contains("missing required fields: delete-other-v"));
        assertTrue(errors.contains("has multiple rules using the same label: org.onap.relationships.inventory.Source"));
        assertTrue(errors.contains("Invalid node type(s) found: gooble"));
    }
}
