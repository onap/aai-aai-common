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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.core.MediaType;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.restclient.client.RestClient;
import org.onap.aai.restclient.enums.RestAuthenticationMode;
import org.onap.aai.schemaif.SchemaProvider;
import org.onap.aai.schemaif.SchemaProviderException;
import org.onap.aai.schemaif.SchemaProviderMsgs;
import org.onap.aai.schemaif.definitions.EdgeSchema;
import org.onap.aai.schemaif.definitions.VertexSchema;
import org.onap.aai.schemaif.json.definitions.JsonEdgeSchema;
import org.onap.aai.schemaif.json.definitions.JsonSchema;
import org.onap.aai.schemaif.json.definitions.JsonVertexSchema;
import org.springframework.http.HttpStatus;


public class JsonSchemaProvider implements SchemaProvider {
    Logger logger = LoggerFactory.getInstance().getLogger(JsonSchemaProvider.class.getName());
    
    private JsonSchemaProviderConfig config;
    private Map<String,SchemaInstance> schemaCache = new ConcurrentHashMap<>();
    private RestClient restClient;
    
    public JsonSchemaProvider(JsonSchemaProviderConfig config) {
        this.restClient = new RestClient().authenticationMode(RestAuthenticationMode.SSL_CERT).validateServerHostname(false)
                .validateServerCertChain(false).clientCertFile(config.getSchemaServiceCertFile())
                .clientCertPassword(config.getSchemaServiceCertPwd());    
        this.config = config;
    }
    
    @Override
    public void loadSchema() throws SchemaProviderException {
        // Load the latest schema version
        fetchSchemaVersion(getLatestSchemaVersion());
    }
        
    @Override
    public String getLatestSchemaVersion() throws SchemaProviderException {
        return "DEFAULT-SCHEMA";
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
    public List<EdgeSchema> getAdjacentEdgeSchema(String vertexType, String version) throws SchemaProviderException {
        SchemaInstance inst = getSchemaVersion(version);
        
        List<EdgeSchema> edgeList = inst.getEdgeSchema(vertexType);
        if (edgeList == null) {
            edgeList = new ArrayList<EdgeSchema>();
        }
        
        return edgeList;
    }

    @Override
    public List<EdgeSchema> getEdgeSchemaForSourceTarget(String sourceType, String targetType, String version)
            throws SchemaProviderException {
        SchemaInstance inst = schemaCache.get(version);

        if (inst == null) {
            throw new SchemaProviderException("Unable to find schema version " + version);
        }
        
        List<EdgeSchema> edgeList = inst.getEdgeSchemas(sourceType, targetType);
        if (edgeList == null) {
            edgeList = new ArrayList<EdgeSchema>();
        }
        
        return edgeList;
    }
    
    public void loadSchema(String payload, String version) throws SchemaProviderException {
        JsonSchema jsonSchema = JsonSchema.fromJson(payload);
        SchemaInstance schemaInst = new SchemaInstance();
        
        for (JsonVertexSchema jsonVertex : jsonSchema.getNodeTypes()) {
            FromJsonVertexSchema vSchema = new FromJsonVertexSchema();
            vSchema.fromJson(jsonVertex, jsonSchema.getDataTypes());
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
        
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("X-FromAppId", Arrays.asList(config.getServiceName()));
        headers.put("X-TransactionId", Arrays.asList(java.util.UUID.randomUUID().toString()));
        
        OperationResult response = restClient.get(config.getSchemaServiceBaseUrl() + "/" + version, headers, MediaType.APPLICATION_JSON_TYPE); 
        
        if (response.getResultCode() == HttpStatus.NOT_FOUND.value()) {
            logger.warn(SchemaProviderMsgs.SCHEMA_LOAD_ERROR, "version " + version + " not found");
            throw new SchemaProviderException("Schema version " + version + " not found");
        }
        else if (response.getResultCode() != HttpStatus.OK.value()) {
            logger.error(SchemaProviderMsgs.SCHEMA_LOAD_ERROR, "failed to load version " + version + ": " + response.getFailureCause());
            throw new SchemaProviderException("Error getting schema version " + version + ":" + response.getFailureCause());
        }
        
        SchemaServiceResponse resp = SchemaServiceResponse.fromJson(response.getResult());
        loadSchema(resp.getDataDecoded(), version);
        
        logger.info(SchemaProviderMsgs.LOADED_SCHEMA_FILE, version);
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
    
}
