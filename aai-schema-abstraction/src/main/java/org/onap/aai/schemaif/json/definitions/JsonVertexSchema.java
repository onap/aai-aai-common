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
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonVertexSchema {
    private static final Gson gson = new GsonBuilder().create();
    
    private String name;
    private String description;
    private List<JsonPropertySchema> properties;
    private Map<String,String> annotations;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<JsonPropertySchema> getProperties() {
        return properties;
    }

    public void setProperties(List<JsonPropertySchema> properties) {
        this.properties = properties;
    }
    
    public Map<String,String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Map<String,String> annotations) {
        this.annotations = annotations;
    }

    public String toJson() {
        return gson.toJson(this);
    }
    
    public static JsonVertexSchema fromJson(String json) {
        return gson.fromJson(json, JsonVertexSchema.class);
    }
}
