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

import static org.junit.Assert.*;

import com.jayway.jsonpath.DocumentContext;

import java.util.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.aai.edges.JsonIngestor;
import org.onap.aai.setup.SchemaVersion;

public class UniqueLabelValidationModuleTest {
    private static List<DocumentContext> ctxs;
    private static UniqueLabelValidationModule validator;
    public static final SchemaVersion LATEST = new SchemaVersion("v14");

    @BeforeClass
    public static void setup() {
        Map<SchemaVersion, List<String>> testRules = new TreeMap<>();
        List<String> testFiles = new ArrayList<>();
        testFiles.add("src/test/resources/edgeRules/labelValidationTest1.json");
        testFiles.add("src/test/resources/edgeRules/labelValidationTest2.json");
        testRules.put(LATEST, testFiles);

        JsonIngestor ji = new JsonIngestor();
        ctxs = ji.ingest(testRules).get(LATEST);
        validator = new UniqueLabelValidationModule();
    }

    @Test
    public void testValidSetOneFile() {
        assertTrue("".equals(validator.validate("human|monster", ctxs)));
        assertTrue("".equals(validator.validate("monster|human", ctxs)));
    }

    @Test
    public void testValidDupLabelButDiffPairs() {
        assertTrue("".equals(validator.validate("human|strange-and-interesting-plant", ctxs)));
        assertTrue("".equals(validator.validate("strange-and-interesting-plant|human", ctxs)));
    }

    @Test
    public void testValidAcrossFiles() {
        assertTrue("".equals(validator.validate("human|toaster", ctxs)));
        assertTrue("".equals(validator.validate("toaster|human", ctxs)));
    }

    @Test
    public void testInvalidSetOneFileBothTypes() {
        assertTrue(validator.validate("sphinx|monster", ctxs).contains("has multiple rules using the same label"));
        assertTrue(validator.validate("monster|sphinx", ctxs).contains("has multiple rules using the same label"));
    }

    @Test
    public void testInvalidSetOneFileJustCousins() {
        assertTrue(validator.validate("griffin|hippogriff", ctxs).contains("has multiple rules using the same label"));
        assertTrue(validator.validate("hippogriff|griffin", ctxs).contains("has multiple rules using the same label"));
    }

    @Test
    public void testInvalidSetMultipleFiles() {
        assertTrue(validator.validate("lava|floor", ctxs).contains("has multiple rules using the same label"));
        assertTrue(validator.validate("floor|lava", ctxs).contains("has multiple rules using the same label"));
    }

    @Test
    public void testInvalidCopyInOtherFile() {
        assertTrue(validator.validate("badger|mushroom", ctxs).contains("has multiple rules using the same label"));
    }
}
