/**
 * ﻿============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2019 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2019 Amdocs
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.schemaif.json;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.onap.aai.schemaif.SchemaProvider;
import org.onap.aai.schemaif.SchemaProviderException;
import org.onap.aai.schemaif.definitions.EdgeSchema;
import org.onap.aai.schemaif.definitions.VertexSchema;
import org.onap.aai.schemaif.json.definitions.JsonEdgeSchema;
import org.onap.aai.schemaif.json.definitions.JsonSchema;
import org.onap.aai.schemaif.json.definitions.JsonVertexSchema;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonSchemaProvider implements SchemaProvider {

    private JsonSchemaProviderConfig config;
    private Map<String, SchemaInstance> schemaCache = new ConcurrentHashMap<>();
    private RestTemplate restTemplate = null;

    public JsonSchemaProvider(JsonSchemaProviderConfig config) {
        this.config = config;

        this.restTemplate = new RestTemplate();
    }

    @Override
    public void loadSchema() throws SchemaProviderException {
        // Load the latest schema version
        fetchSchemaVersion(getLatestSchemaVersion());
    }

    @Override
    public String getLatestSchemaVersion() throws SchemaProviderException {
        return "v0";
    }

    @Override
    public VertexSchema getVertexSchema(String vertexName, String schemaVersion) throws SchemaProviderException {
        SchemaInstance inst = getSchemaVersion(schemaVersion);
        return inst.getVertexSchema(vertexName);
    }

    @Override
    public EdgeSchema getEdgeSchema(String edgeType, String sourceVertex, String targetVertex, String version)
            throws SchemaProviderException {
        SchemaInstance inst = getSchemaVersion(version);
        return inst.getEdgeSchema(sourceVertex, targetVertex, edgeType);
    }

    @Override
    public Set<EdgeSchema> getAdjacentEdgeSchema(String vertexType, String version) throws SchemaProviderException {
        SchemaInstance inst = getSchemaVersion(version);

        Set<EdgeSchema> edgeList = inst.getEdgeSchema(vertexType);
        if (edgeList == null) {
            edgeList = new HashSet<>();
        }

        return edgeList;
    }

    @Override
    public Set<EdgeSchema> getEdgeSchemaForSourceTarget(String sourceType, String targetType, String version)
            throws SchemaProviderException {
        SchemaInstance inst = getSchemaVersion(version);

        Set<EdgeSchema> edgeList = inst.getEdgeSchemas(sourceType, targetType);
        if (edgeList == null) {
            edgeList = new HashSet<>();
        }

        return edgeList;
    }

    public void loadSchema(String payload, String version) throws SchemaProviderException {
        JsonSchema jsonSchema = JsonSchema.fromJson(payload);
        SchemaInstance schemaInst = new SchemaInstance();

        for (JsonVertexSchema jsonVertex : jsonSchema.getNodeTypes()) {
            FromJsonVertexSchema vSchema = new FromJsonVertexSchema();
            vSchema.fromJson(jsonVertex, jsonSchema.getDataTypes(), jsonSchema.getCommonProperties());
            schemaInst.addVertex(vSchema);
        }

        for (JsonEdgeSchema jsonEdge : jsonSchema.getRelationshipTypes()) {
            FromJsonEdgeSchema eSchema = new FromJsonEdgeSchema();
            eSchema.fromJson(jsonEdge);
            schemaInst.addEdge(eSchema);
        }

        schemaCache.put(version, schemaInst);
    }

    private synchronized void fetchSchemaVersion(String version) throws SchemaProviderException {
        if (schemaCache.get(version) != null) {
            return;
        }

        String url = config.getSchemaServiceBaseUrl() + "/" + version;

        HttpHeaders headers = new HttpHeaders();
        headers.put("X-FromAppId", Arrays.asList(config.getServiceName()));
        headers.put("X-TransactionId", Arrays.asList(java.util.UUID.randomUUID().toString()));
        headers.setAccept(Arrays.asList(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);

        if (response.getStatusCodeValue() == HttpStatus.NOT_FOUND.value()) {
            log.warn("PVD0500E | Unable to load schema: {}", "version " + version + " not found");
            throw new SchemaProviderException("Schema version " + version + " not found");
        } else if (response.getStatusCodeValue() != HttpStatus.OK.value()) {
            log.error("PVD0500E | Unable to load schema: {}", "version " + version + " not found");
            throw new SchemaProviderException("Error getting schema version " + version + ":" + response.getBody());
        }

        try {
            SchemaServiceResponse resp = SchemaServiceResponse.fromJson(unzipAndGetJSONString(response.getBody()));
            loadSchema(resp.getData().toJson(), version);
        } catch (Exception ex) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            log.error("PVD0500E | Unable to load schema: {}", "failed to load version: " + version + ": "+ response.getBody() + "\n" + writer.toString());
            throw new SchemaProviderException("Error loading schema version " + version + ":" + ex.getMessage());

        }

        log.info("PVD0001I|Successfully loaded schema: {}", version);
    }

    static final int BUFFER = 512;
    static final long TOOBIG = 0x6400000; // Max size of unzipped data, 100MB
    static final int TOOMANY = 1024; // Max number of files

    protected String unzipAndGetJSONString(byte[] inputData) throws java.io.IOException {
        ZipEntry entry;
        String result = "";
        int entries = 0;
        long total = 0;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(inputData);
                ZipInputStream zis = new ZipInputStream(bis)) {
            while ((entry = zis.getNextEntry()) != null) {
                int count;
                byte[] data = new byte[BUFFER];
                if (entry.isDirectory()) {
                    continue;
                }
                ByteArrayOutputStream fos = new ByteArrayOutputStream();
                BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
                while (total + BUFFER <= TOOBIG && (count = zis.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, count);
                    total += count;
                }
                dest.flush();
                result = fos.toString();
                dest.close();
                zis.closeEntry();
                entries++;
                if (entries > TOOMANY) {
                    throw new IllegalStateException("Too many files to unzip.");
                }
                if (total + BUFFER > TOOBIG) {
                    throw new IllegalStateException("File being unzipped is too big.");
                }
            }
        }
        return result;
    }

    private SchemaInstance getSchemaVersion(String version) throws SchemaProviderException {
        // TODO: For now, we are only supporting a single version of the schema. Load that
        // version regardless of what the client asks for.
        String versionToLoad = getLatestSchemaVersion();
        SchemaInstance inst = schemaCache.get(versionToLoad);

        if (inst == null) {
            fetchSchemaVersion(versionToLoad);
            inst = schemaCache.get(versionToLoad);
            if (inst == null) {
                throw new SchemaProviderException("Unable to find schema version " + versionToLoad);
            }
        }

        return inst;
    }

    @Override
    public Map<String, VertexSchema> getVertexMap(String schemaVersion) throws SchemaProviderException {
        return getSchemaVersion(schemaVersion).getVertexMap();
    }

}
