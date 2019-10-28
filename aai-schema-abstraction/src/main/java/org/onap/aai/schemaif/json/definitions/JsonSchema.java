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
    
    @SerializedName("common_node_properties")
    private List<JsonPropertySchema> commonNodeProps;

    
    public void setRelationshipTypes(List<JsonEdgeSchema> relationshipTypes) {
		this.relationshipTypes = relationshipTypes;
	}

	public void setNodeTypes(List<JsonVertexSchema> nodeTypes) {
		this.nodeTypes = nodeTypes;
	}

	public void setDataTypes(List<DataTypeDefinition> dataTypes) {
		this.dataTypes = dataTypes;
	}

	public List<JsonEdgeSchema> getRelationshipTypes() {
        return relationshipTypes;
    }
    
    public List<JsonVertexSchema> getNodeTypes() {
        return nodeTypes;
    }
    
    public List<DataTypeDefinition> getDataTypes() {
        return dataTypes;
    }
    
    public List<JsonPropertySchema> getCommonProperties() {
        return commonNodeProps;
    }

    public void setCommonProperties(List<JsonPropertySchema> properties) {
        this.commonNodeProps = properties;
    }
    
    public void validate() throws SchemaProviderException {
        if (getNodeTypes() != null) {
            for (JsonVertexSchema vertexSchema : getNodeTypes()) {
                vertexSchema.validate();
            }
        }
        
        // Validate edges
        if (getRelationshipTypes() != null) {
            for (JsonEdgeSchema edgeSchema : getRelationshipTypes()) {
                edgeSchema.validate();
            }
        }
        
        // Validate data types
        if (getDataTypes() != null) {
            for (DataTypeDefinition typeSchema : getDataTypes()) {
                typeSchema.validate();
            }
        }
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

    @Override
    public String toString() {
        return "JsonSchema [relationshipTypes=" + relationshipTypes + ", nodeTypes=" + nodeTypes
            + ", dataTypes=" + dataTypes + ", commonNodeProps=" + commonNodeProps + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((commonNodeProps == null) ? 0 : commonNodeProps.hashCode());
        result = prime * result + ((dataTypes == null) ? 0 : dataTypes.hashCode());
        result = prime * result + ((nodeTypes == null) ? 0 : nodeTypes.hashCode());
        result = prime * result + ((relationshipTypes == null) ? 0 : relationshipTypes.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        JsonSchema other = (JsonSchema) obj;
        if (commonNodeProps == null) {
            if (other.commonNodeProps != null)
                return false;
        } else if (!commonNodeProps.equals(other.commonNodeProps))
            return false;
        if (dataTypes == null) {
            if (other.dataTypes != null)
                return false;
        } else if (!dataTypes.equals(other.dataTypes))
            return false;
        if (nodeTypes == null) {
            if (other.nodeTypes != null)
                return false;
        } else if (!nodeTypes.equals(other.nodeTypes))
            return false;
        if (relationshipTypes == null) {
            if (other.relationshipTypes != null)
                return false;
        } else if (!relationshipTypes.equals(other.relationshipTypes))
            return false;
        return true;
    }

    
}
