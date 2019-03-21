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

import java.util.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.aai.edges.JsonIngestor;
import org.onap.aai.setup.SchemaVersion;

public class SingleContainmentValidationModuleTest {
    private static List<DocumentContext> ctxs;
    private static SingleContainmentValidationModule validator;
    public static final SchemaVersion LATEST = new SchemaVersion("v14");

    @BeforeClass
    public static void setUpBeforeClass() {
        Map<SchemaVersion, List<String>> testRules = new TreeMap<>();
        List<String> testFiles = new ArrayList<>();
        testFiles.add("src/test/resources/edgeRules/containsValidationTest.json");
        testRules.put(LATEST, testFiles);

        JsonIngestor ji = new JsonIngestor();
        ctxs = ji.ingest(testRules).get(LATEST);
        validator = new SingleContainmentValidationModule();
    }

    @Test
    public void testValid() {
        assertTrue("".equals(validator.validate("human|monster", ctxs)));
    }

    @Test
    public void testValidWithNone() {
        assertTrue("".equals(validator.validate("bread|cheese", ctxs)));
    }

    @Test
    public void testInvalid() {
        assertTrue(validator.validate("box|cat", ctxs).contains("has multiple containment rules"));
    }
}
