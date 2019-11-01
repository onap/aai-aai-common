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

public class JsonEdgeSchema {
    private static final Gson gson = new GsonBuilder().create();
    
    private String from;
    private String to;
    private String label;
    private Map<String,String> annotations;
    
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    
    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }
    
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    
    public Map<String,String> getAnnotations() {
        return annotations;
    }
    public void setAnnotations(Map<String,String> annotations) {
        this.annotations = annotations;
    }
    
    public void validate() throws SchemaProviderException {
        if ( (getTo() == null) || (getTo().isEmpty()) ) {
            throw new SchemaProviderException("Edge definition missing 'to'");
        }
        
        if ( (getFrom() == null) || (getFrom().isEmpty()) ) {
            throw new SchemaProviderException("Edge definition missing 'from'");
        }
        
        if ( (getLabel() == null) || (getLabel().isEmpty()) ) {
            throw new SchemaProviderException("Edge definition missing 'label'");
        }
    }
    
    public String toJson() {
        return gson.toJson(this);
    }
    
    public static JsonEdgeSchema fromJson(String json) {
        return gson.fromJson(json, JsonEdgeSchema.class);
    }
    @Override
    public String toString() {
        return "JsonEdgeSchema [from=" + from + ", to=" + to + ", label=" + label + ", annotations="
            + annotations + "]";
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((annotations == null) ? 0 : annotations.hashCode());
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((to == null) ? 0 : to.hashCode());
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
        JsonEdgeSchema other = (JsonEdgeSchema) obj;
        if (annotations == null) {
            if (other.annotations != null)
                return false;
        } else if (!annotations.equals(other.annotations))
            return false;
        if (from == null) {
            if (other.from != null)
                return false;
        } else if (!from.equals(other.from))
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (to == null) {
            if (other.to != null)
                return false;
        } else if (!to.equals(other.to))
            return false;
        return true;
    }
    
}
