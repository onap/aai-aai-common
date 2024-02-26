/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright © 2024 DEUTSCHE TELEKOM AG.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.setup;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

/**
 * <b>AAIConfigTranslator</b> is responsible for looking at the
 * schema files and edge files based on the available versions
 * Also has the ability to exclude them based on the node.exclusion.pattern
 */
@Component
public class AAIConfigTranslator extends ConfigTranslator {

    public AAIConfigTranslator(SchemaLocationsBean schemaLocationsBean, SchemaConfigVersions schemaConfigVersions) {
        super(schemaLocationsBean, schemaConfigVersions);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.aai.setup.ConfigTranslator#getNodeFiles()
     */
    @Override
    public Map<SchemaVersion, List<String>> getNodeFiles() {

        Map<SchemaVersion, List<String>> files = new TreeMap<>();
        for (SchemaVersion v : schemaVersions.getVersions()) {
            List<String> container = getVersionNodeFiles(v);
            files.put(v, container);
        }

        return files;
    }

    private List<String> getVersionNodeFiles(SchemaVersion v) {
        return getVersionFiles(schemaLocationsBean.getNodeDirectory(), v, () -> schemaLocationsBean.getNodesInclusionPattern().stream(),
                () -> schemaLocationsBean.getNodesExclusionPattern().stream());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.aai.setup.ConfigTranslator#getEdgeFiles()
     */
    @Override
    public Map<SchemaVersion, List<String>> getEdgeFiles() {

        Map<SchemaVersion, List<String>> files = new TreeMap<>();
        for (SchemaVersion v : schemaVersions.getVersions()) {
            List<String> container = getVersionEdgeFiles(v);
            files.put(v, container);
        }

        return files;
    }

    private List<String> getVersionEdgeFiles(SchemaVersion v) {

        return getVersionFiles(schemaLocationsBean.getEdgeDirectory(), v, () -> schemaLocationsBean.getEdgesInclusionPattern().stream(),
                () -> schemaLocationsBean.getEdgesExclusionPattern().stream());
    }

    private List<String> getVersionFiles(String startDirectory, SchemaVersion schemaVersion,
           Supplier<Stream<String>> inclusionPattern, Supplier<Stream<String>> exclusionPattern) {

       final File versionDirectory = new File(startDirectory + "/" + schemaVersion.toString());
       final List<String> container = Arrays.stream(versionDirectory.listFiles())
           .filter(Objects::nonNull)    
           .map(File::getName)
           .filter(versionFileName -> inclusionPattern
               .get()
               .anyMatch(versionFileName::matches))
           .map(versionFileName -> versionDirectory.getAbsolutePath() + "/" + versionFileName)
           .filter(versionFilePath -> exclusionPattern
               .get()
               .noneMatch(versionFilePath::matches))
           .collect(Collectors.toList());

        return container;
    }
}
