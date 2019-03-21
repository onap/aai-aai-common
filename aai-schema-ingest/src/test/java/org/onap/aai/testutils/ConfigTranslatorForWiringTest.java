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

public class ConfigTranslatorForWiringTest extends ConfigTranslator {

    public ConfigTranslatorForWiringTest(SchemaLocationsBean bean, SchemaVersions schemaVersions) {
        super(bean, schemaVersions);
    }

    @Override
    public Map<SchemaVersion, List<String>> getNodeFiles() {

        String f = bean.getNodeDirectory() + "test_business_v10.xml";
        List<String> files = new ArrayList<>();
        files.add(f);
        Map<SchemaVersion, List<String>> mp = new TreeMap<>();
        mp.put(new SchemaVersion("v10"), files);
        return mp;
    }

    @Override
    public Map<SchemaVersion, List<String>> getEdgeFiles() {
        String f = bean.getEdgeDirectory() + "test.json";
        List<String> files = new ArrayList<>();
        files.add(f);
        Map<SchemaVersion, List<String>> mp = new TreeMap<>();
        mp.put(new SchemaVersion("v10"), files);
        return mp;
    }

}
