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

import com.jayway.jsonpath.DocumentContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.aai.edges.JsonIngestor;
import org.onap.aai.setup.SchemaVersion;

public class CousinDefaultingValidationModuleTest {
    private static List<DocumentContext> ctxs;
    private static CousinDefaultingValidationModule validator;

    @BeforeClass
    public static void setUpBeforeClass() {
        Map<SchemaVersion, List<String>> testRules = new HashMap<>();
        List<String> testFiles = new ArrayList<>();
        testFiles.add("src/test/resources/edgeRules/cousinDefaultValidationTest.json");

        SchemaVersion LATEST_VERSION = new SchemaVersion("v14");
        testRules.put(LATEST_VERSION, testFiles);

        JsonIngestor ji = new JsonIngestor();
        ctxs = ji.ingest(testRules).get(LATEST_VERSION);
        validator = new CousinDefaultingValidationModule();
    }

    @Test
    public void testValidCousins() {
        assertTrue("".equals(validator.validate("boop|beep", ctxs)));
    }

    @Test
    public void testValidBoth() {
        assertTrue("".equals(validator.validate("monster|human", ctxs)));
    }

    @Test
    public void testValidSingleContains() {
        assertTrue("".equals(validator.validate("family|baby", ctxs)));
    }

    @Test
    public void testInvalidTooManyDefaults() {
        assertTrue(validator.validate("sheep|wool", ctxs).contains("Multiple set"));
    }

    @Test
    public void testInvalidNoDefaults() {
        assertTrue(validator.validate("cloth|thread", ctxs).contains("None set"));
    }
}
