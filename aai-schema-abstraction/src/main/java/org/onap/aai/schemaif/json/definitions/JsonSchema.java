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
package org.onap.aai.schemaif.json.definitions;

import java.util.List;

import org.onap.aai.schemaif.SchemaProviderException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public class JsonSchema {
    private static final Gson gson = new GsonBuilder().create();

    @SerializedName("relationship_types")
    private List<JsonEdgeSchema> relationshipTypes;
    
    @SerializedName("node_types")
    private List<JsonVertexSchema> nodeTypes;
    
    @SerializedName("data_types")
    private List<DataTypeDefinition> dataTypes;

    public List<JsonEdgeSchema> getRelationshipTypes() {
        return relationshipTypes;
    }
    
    public List<JsonVertexSchema> getNodeTypes() {
        return nodeTypes;
    }
    
    public List<DataTypeDefinition> getDataTypes() {
        return dataTypes;
    }
    
    public String toJson() {
        return gson.toJson(this);
    }

    public static JsonSchema fromJson(String json) throws SchemaProviderException {
        try {
            if (json == null || json.isEmpty()) {
                throw new SchemaProviderException("Empty schema definition");
            }
            
            return gson.fromJson(json, JsonSchema.class);
        } catch (Exception ex) {
            throw new SchemaProviderException("Invalid json: " + ex.getMessage());
        }
    }

}
