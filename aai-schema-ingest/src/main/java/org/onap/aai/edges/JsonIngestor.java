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

package org.onap.aai.edges;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.onap.aai.setup.SchemaVersion;

/**
 * JsonIngestor produces DocumentContexts from json files
 */
public class JsonIngestor {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonIngestor.class);

    /**
     * Reads in given json files to queryable DocumentContexts.
     *
     * @param filesToIngest - map of filenames to ingest
     *        per Version
     * @return Map<SchemaVersion, List<DocumentContext>> - map of DocumentContexts per Version
     */
    public Map<SchemaVersion, List<DocumentContext>> ingest(Map<SchemaVersion, List<String>> filesToIngest) {
        Map<SchemaVersion, List<DocumentContext>> result = new HashMap<>();

        for (Entry<SchemaVersion, List<String>> verFiles : filesToIngest.entrySet()) {
            SchemaVersion v = verFiles.getKey();
            List<String> files = verFiles.getValue();

            List<DocumentContext> docs = new ArrayList<>();
            for (String rulesFilename : files) {
                String fileContents = readInJsonFile(rulesFilename);
                docs.add(JsonPath.parse(fileContents));
            }
            result.put(v, docs);
        }

        return result;
    }

    public Map<SchemaVersion, List<DocumentContext>> ingestContent(Map<SchemaVersion, List<String>> filesToIngest) {
        Map<SchemaVersion, List<DocumentContext>> result = new HashMap<>();

        for (Entry<SchemaVersion, List<String>> verFiles : filesToIngest.entrySet()) {
            SchemaVersion v = verFiles.getKey();
            List<String> files = verFiles.getValue();

            List<DocumentContext> docs = new ArrayList<>();
            for (String jsonPayload : files) {
                docs.add(JsonPath.parse(jsonPayload));
            }
            result.put(v, docs);
        }
        return result;
    }

    /**
     * Reads the json file at the given filename into an in-memory String.
     *
     * @param rulesFilename - json file to be read (must include path to the file)
     * @return String json contents of the given file
     */
    public String readInJsonFile(String rulesFilename) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(rulesFilename))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            LOGGER.warn("Exception in file" + e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
        return sb.toString();
    }
}
