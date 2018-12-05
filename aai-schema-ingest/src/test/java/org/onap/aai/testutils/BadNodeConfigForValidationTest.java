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

package org.onap.aai.testutils;

import org.onap.aai.setup.ConfigTranslator;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * All schema files here are valid for sunny day validator testing
 */
public class BadNodeConfigForValidationTest extends ConfigTranslator {

    public static final SchemaVersion LATEST = new SchemaVersion("v14");

    public BadNodeConfigForValidationTest(SchemaLocationsBean bean, SchemaVersions schemaVersions) {
        super(bean, schemaVersions);
    }

    @Override
    public Map<SchemaVersion, List<String>> getNodeFiles() {
        List<String> files = new ArrayList<>();
        files.add("src/test/resources/oxm/goodConfigForValidationTest_oxm.xml");
        files.add("src/test/resources/oxm/badConfigForValidationTest_oxm.xml");
        Map<SchemaVersion, List<String>> input = new TreeMap<>();
        input.put(LATEST, files);
        return input;
    }

    @Override
    public Map<SchemaVersion, List<String>> getEdgeFiles() {
        Map<SchemaVersion, List<String>> input = new TreeMap<>();
        List<String> files = new ArrayList<>();
        files.add("src/test/resources/edgeRules/test3.json");
        input.put(LATEST, files);
        return input;
    }

    
}
