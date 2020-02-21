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

package org.onap.aai.setup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.onap.aai.edges.JsonIngestor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Converts the contents of the schema config file
 * (which lists which schema files to be loaded) to
 * the format the Ingestors can work with.
 * 
 */
public abstract class ConfigTranslator extends Translator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigTranslator.class);

    protected SchemaLocationsBean bean;

    @Autowired
    public ConfigTranslator(SchemaLocationsBean schemaLocationbean, SchemaConfigVersions schemaVersions) {
        super(schemaVersions);
        this.bean = schemaLocationbean;

    }

    /**
     * Translates the contents of the schema config file
     * into the input for the NodeIngestor
     * 
     * @return Map of Version to the list of (string) filenames to be
     *         ingested for that version
     */
    public abstract Map<SchemaVersion, List<String>> getNodeFiles();

    public List<InputStream> getVersionNodeStream(SchemaVersion version) {

        Map<SchemaVersion, List<String>> filesToIngest = getNodeFiles();
        List<InputStream> streams = new ArrayList<>();

        if (!filesToIngest.containsKey(version)) {
            return streams;
        }
        List<String> versionFiles = filesToIngest.get(version);

        for (String name : versionFiles) {
            try {
                InputStream stream = new FileInputStream(new File(name));
                String value = IOUtils.toString(stream, Charset.defaultCharset());
                InputStream bis = (IOUtils.toInputStream(value, Charset.defaultCharset()));
                streams.add(bis);
            } catch (FileNotFoundException e) {
                // TODO This may have to be cascaded
                LOGGER.warn("File Not Found" + e.getMessage());
            } catch (IOException e) {
                LOGGER.warn("IOException while reading files" + e.getMessage());
            }
        }
        return streams;
    }

    @Override
    public List<String> getJsonPayload(SchemaVersion version) {
        Map<SchemaVersion, List<String>> filesToIngest = getEdgeFiles();
        List<String> jsonPayloads = new ArrayList<>();
        if (!filesToIngest.containsKey(version)) {
            return jsonPayloads;
        }
        List<String> versionFiles = filesToIngest.get(version);
        JsonIngestor ji = new JsonIngestor();
        for (String rulesFilename : versionFiles) {
            jsonPayloads.add(ji.readInJsonFile(rulesFilename));

        }

        return jsonPayloads;
    }

    /**
     * Translates the contents of the schema config file
     * into the input for the EdgeIngestor
     * 
     * @return Map of Version to the List of (String) filenames to be
     *         ingested for that version
     */
    public abstract Map<SchemaVersion, List<String>> getEdgeFiles();

}
