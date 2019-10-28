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

import java.util.Map;

import org.onap.aai.schemaif.SchemaProviderException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public class JsonPropertySchema {
    private static final Gson gson = new GsonBuilder().create();
    
    private String name;
    private Boolean required;
    private Boolean unique;
    
    @SerializedName("type")
    private String dataType;
    
    private String description;
    
    @SerializedName("default")
    private String defaultValue;
    
    private Map<String,String> annotations;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getUnique() {
        return unique;
    }

    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public Map<String,String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Map<String,String> annotations) {
        this.annotations = annotations;
    }

    public void validate() throws SchemaProviderException {
        if ( (getName() == null) || (getName().isEmpty()) ) {
            throw new SchemaProviderException(getName() + " property has no name");
        }

        if ( (getDataType() == null) || (getDataType().isEmpty()) ) {
            throw new SchemaProviderException(getName() + " property has no type");
        }
    }


    public String toJson() {
        return gson.toJson(this);
    }
    
    public static JsonVertexSchema fromJson(String json) {
        return gson.fromJson(json, JsonVertexSchema.class);
    }

    @Override
    public String toString() {
        return "JsonPropertySchema [name=" + name + ", required=" + required + ", unique=" + unique
            + ", dataType=" + dataType + ", description=" + description + ", defaultValue="
            + defaultValue + ", annotations=" + annotations + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((annotations == null) ? 0 : annotations.hashCode());
        result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((required == null) ? 0 : required.hashCode());
        result = prime * result + ((unique == null) ? 0 : unique.hashCode());
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
        JsonPropertySchema other = (JsonPropertySchema) obj;
        if (annotations == null) {
            if (other.annotations != null)
                return false;
        } else if (!annotations.equals(other.annotations))
            return false;
        if (dataType == null) {
            if (other.dataType != null)
                return false;
        } else if (!dataType.equals(other.dataType))
            return false;
        if (defaultValue == null) {
            if (other.defaultValue != null)
                return false;
        } else if (!defaultValue.equals(other.defaultValue))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (required == null) {
            if (other.required != null)
                return false;
        } else if (!required.equals(other.required))
            return false;
        if (unique == null) {
            if (other.unique != null)
                return false;
        } else if (!unique.equals(other.unique))
            return false;
        return true;
    }
    
    
}
