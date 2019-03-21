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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.onap.aai.setup.ConfigTranslator;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = "classpath:schema-ingest.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${schema.ingest.file}", ignoreResourceNotFound = true)

public class TestUtilConfigTranslator extends ConfigTranslator {

    public static final SchemaVersion LATEST = new SchemaVersion("v15");

    public TestUtilConfigTranslator(SchemaLocationsBean bean, SchemaVersions schemaVersions) {
        super(bean, schemaVersions);
    }

    @Override
    public Map<SchemaVersion, List<String>> getNodeFiles() {
        List<String> files10 = new ArrayList<>();
        files10.add("src/test/resources/oxm/test_network_v10.xml");
        files10.add("src/test/resources/oxm/test_business_v10.xml");

        List<String> files11 = new ArrayList<>();
        files11.add("src/test/resources/oxm/test_network_v11.xml");
        files11.add("src/test/resources/oxm/test_business_v11.xml");

        Map<SchemaVersion, List<String>> input = new TreeMap<>();
        input.put(new SchemaVersion("v10"), files10);
        input.put(new SchemaVersion("v11"), files11);

        List<String> files13 = new ArrayList<>();
        files13.add("src/test/resources/oxm/business_oxm_v13.xml");
        files13.add("src/test/resources/oxm/common_oxm_v13.xml");
        files13.add("src/test/resources/oxm/serviceDesign_oxm_v13.xml");
        files13.add("src/test/resources/oxm/network_oxm_v13.xml");

        input.put(new SchemaVersion("v10"), files10);
        input.put(new SchemaVersion("v11"), files11);
        input.put(new SchemaVersion("v13"), files13);
        return input;
    }

    @Override
    public Map<SchemaVersion, List<String>> getEdgeFiles() {
        List<String> files = new ArrayList<>();
        files.add("src/test/resources/edgeRules/test.json");
        files.add("src/test/resources/edgeRules/test2.json");
        files.add("src/test/resources/edgeRules/otherTestRules.json");
        Map<SchemaVersion, List<String>> input = new TreeMap<>();
        input.put(LATEST, files);

        List<String> files2 = new ArrayList<>();
        files2.add("src/test/resources/edgeRules/test.json");
        input.put(new SchemaVersion("v10"), files2);

        List<String> files3 = new ArrayList<>();
        files3.add("src/test/resources/edgeRules/test3.json");
        files3.add("src/test/resources/edgeRules/defaultEdgesTest.json");
        input.put(new SchemaVersion("v11"), files3);
        return input;
    }
}
