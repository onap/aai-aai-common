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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.onap.aai.schemaif.SchemaProviderException;
import org.onap.aai.schemaif.definitions.PropertySchema;
import org.onap.aai.schemaif.definitions.VertexSchema;
import org.onap.aai.schemaif.json.definitions.DataTypeDefinition;
import org.onap.aai.schemaif.json.definitions.JsonPropertySchema;
import org.onap.aai.schemaif.json.definitions.JsonVertexSchema;


public class FromJsonVertexSchema extends VertexSchema {
    public void fromJson(JsonVertexSchema jsonVertex, List<DataTypeDefinition> dataTypes) throws SchemaProviderException {
        name = jsonVertex.getName();
        properties = new HashMap<String,PropertySchema>();
        annotations = new HashMap<String,String>();

        // Populate property schema
        if (jsonVertex.getProperties() != null) {
            for (JsonPropertySchema pSchema : jsonVertex.getProperties()) {
                FromJsonPropertySchema propSchema = new FromJsonPropertySchema();
                propSchema.fromJson(pSchema, dataTypes);
                properties.put(propSchema.getName(), propSchema);
            }
        }
        
        // Populate annotation schema
        if (jsonVertex.getAnnotations() != null) {
            for (Map.Entry<String,String> entry : jsonVertex.getAnnotations().entrySet()) {
                annotations.put(entry.getKey(), entry.getValue());
            }
        }
        
        // The searchable and indexed annotations, need to grab these from the property annotations 
        // and store them at the vertex level as well (backwards compatibility with OXM)
        StringBuilder searchableVal = new StringBuilder();
        StringBuilder indexedVal = new StringBuilder();
        for (PropertySchema pSchema : properties.values()) {
            if ( (pSchema.getAnnotationValue("searchable") != null) 
                    && (pSchema.getAnnotationValue("searchable").equalsIgnoreCase("true")) ) {
                if (searchableVal.length() > 0) {
                    searchableVal.append(",");
                }
                searchableVal.append(pSchema.getName());
            }
            if ( (pSchema.getAnnotationValue("indexed") != null) 
                    && (pSchema.getAnnotationValue("indexed").equalsIgnoreCase("true")) ) {
                if (indexedVal.length() > 0) {
                    indexedVal.append(",");
                }
                indexedVal.append(pSchema.getName());
            }
        }
        
        if (searchableVal.length() > 0) {
            annotations.put("searchable", searchableVal.toString());
        }
        if (indexedVal.length() > 0) {
            annotations.put("indexedProps", indexedVal.toString());
        }
    }
}
