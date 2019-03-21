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

package org.onap.aai.testutils;

import java.util.*;

import org.onap.aai.setup.ConfigTranslator;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;

/**
 * Good oxm, bad edge rules for rainy day edge rule validation testing
 */
public class BadEdgeConfigForValidationTest extends ConfigTranslator {

    public static final SchemaVersion LATEST = new SchemaVersion("v14");

    public BadEdgeConfigForValidationTest(SchemaLocationsBean bean, SchemaVersions schemaVersions) {
        super(bean, schemaVersions);
    }

    @Override
    public Map<SchemaVersion, List<String>> getNodeFiles() {
        List<String> files = new ArrayList<>();
        files.add("src/test/resources/oxm/goodConfigForValidationTest_oxm.xml");
        Map<SchemaVersion, List<String>> input = new HashMap<>();
        input.put(LATEST, files);
        return input;
    }

    @Override
    public Map<SchemaVersion, List<String>> getEdgeFiles() {
        Map<SchemaVersion, List<String>> input = new TreeMap<>();
        List<String> files = new ArrayList<>();
        files.add("src/test/resources/edgeRules/test3-butbad.json");
        input.put(LATEST, files);
        return input;
    }

}
