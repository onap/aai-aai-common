/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
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

package org.onap.aai.util.genxsd;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.onap.aai.setup.ConfigTranslator;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;
import org.springframework.context.annotation.Configuration;

public class ConfigTranslatorForDocs extends ConfigTranslator {

    public ConfigTranslatorForDocs(SchemaLocationsBean bean, SchemaVersions schemaVersions) {
        super(bean, schemaVersions);
    }

    @Override
    public Map<SchemaVersion, List<String>> getNodeFiles() {
        List<SchemaVersion> versionsToGen = new ArrayList<>();
        versionsToGen = schemaVersions.getVersions();
        Collections.sort(versionsToGen);
        Collections.reverse(versionsToGen);
        Map<SchemaVersion, List<String>> mp = new TreeMap<>();
        for (SchemaVersion v : versionsToGen) {
            File dir =
                new File(bean.getNodeDirectory() + File.separator + v.toString().toLowerCase());
            File[] fileSet = dir.listFiles();
            List<String> files = new ArrayList<>();
            for (File f : fileSet) {
                files.add(f.getAbsolutePath());
            }

            mp.put(v, files);
        }
        return mp;
    }

    @Override
    public Map<SchemaVersion, List<String>> getEdgeFiles() {
        List<SchemaVersion> versionsToGen = new ArrayList<>();
        versionsToGen = schemaVersions.getVersions();
        Collections.sort(versionsToGen);
        Collections.reverse(versionsToGen);
        Map<SchemaVersion, List<String>> mp = new TreeMap<>();
        for (SchemaVersion v : versionsToGen) {
            File dir = new File(bean.getEdgeDirectory());
            File[] fileSet = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.contains("_" + v.toString().toLowerCase());
                }
            });
            List<String> files = new ArrayList<>();
            for (File f : fileSet) {
                files.add(f.getAbsolutePath());
            }
            mp.put(v, files);
        }
        return mp;
    }
}
