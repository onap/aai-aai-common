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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry; 
import java.util.zip.ZipInputStream;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.schemaif.SchemaProvider;
import org.onap.aai.schemaif.SchemaProviderException;
import org.onap.aai.schemaif.SchemaProviderMsgs;
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


public class JsonSchemaProvider implements SchemaProvider {
    Logger logger = LoggerFactory.getInstance().getLogger(JsonSchemaProvider.class.getName());
    
    private JsonSchemaProviderConfig config;
    private Map<String,SchemaInstance> schemaCache = new ConcurrentHashMap<>();
    private RestTemplate restTemplate = null;
            
    public JsonSchemaProvider(JsonSchemaProviderConfig config) {
        this.config = config;

        SecureClientHttpRequestFactory fac = new SecureClientHttpRequestFactory(config); 
        fac.setBufferRequestBody(false);
        this.restTemplate = new RestTemplate(fac);
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
            edgeList = new HashSet<EdgeSchema>();
        }
        
        return edgeList;
    }

    @Override
    public Set<EdgeSchema> getEdgeSchemaForSourceTarget(String sourceType, String targetType, String version)
            throws SchemaProviderException {
        SchemaInstance inst = getSchemaVersion(version);

        if (inst == null) {
            throw new SchemaProviderException("Unable to find schema version " + version);
        }
        
        Set<EdgeSchema> edgeList = inst.getEdgeSchemas(sourceType, targetType);
        if (edgeList == null) {
            edgeList = new HashSet<EdgeSchema>();
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
        
        HttpEntity <String> entity = new HttpEntity<String>(headers);
        
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
        
        
        if (response.getStatusCodeValue() == HttpStatus.NOT_FOUND.value()) {
            logger.warn(SchemaProviderMsgs.SCHEMA_LOAD_ERROR, "version " + version + " not found");
            throw new SchemaProviderException("Schema version " + version + " not found");
        }
        else if (response.getStatusCodeValue() != HttpStatus.OK.value()) {
            logger.error(SchemaProviderMsgs.SCHEMA_LOAD_ERROR, "failed to load version " + version + ": " + response.getBody());
            throw new SchemaProviderException("Error getting schema version " + version + ":" + response.getBody());
        }

        try {
            SchemaServiceResponse resp = SchemaServiceResponse.fromJson(unzipAndGetJSONString(response));
            loadSchema(resp.getData().toJson(), version);
        }
        catch (Exception ex) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            logger.error(SchemaProviderMsgs.SCHEMA_LOAD_ERROR, "failed to load version " + version + ": " 
                    + response.getBody() + "\n" + writer.toString());
            throw new SchemaProviderException("Error loading schema version " + version + ":" + ex.getMessage());

        }

        logger.info(SchemaProviderMsgs.LOADED_SCHEMA_FILE, version);
    }
    
    private String unzipAndGetJSONString(ResponseEntity<byte[]> response) throws IOException {
        StringBuffer sb = new StringBuffer("");

        ZipInputStream zipStream = null;
        try {

            zipStream = new ZipInputStream(new ByteArrayInputStream(response.getBody()));
            ZipEntry entry = null;
            while ((entry = zipStream.getNextEntry()) != null) {
                Scanner sc = new Scanner(zipStream);
                while (sc.hasNextLine()) {
                    sb.append(sc.nextLine());
                }

            }
        } finally {
            try {
                zipStream.closeEntry();
                zipStream.close();
            } catch (Exception e) {
                logger.warn(SchemaProviderMsgs.SCHEMA_LOAD_ERROR, e.toString());

            }
        }

        return sb.toString();
    }

    private SchemaInstance getSchemaVersion(String version) throws SchemaProviderException {
        // TODO:  For now, we are only supporting a single version of the schema.  Load that
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
