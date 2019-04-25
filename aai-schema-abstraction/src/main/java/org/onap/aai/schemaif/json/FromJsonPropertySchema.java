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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.onap.aai.schemaif.SchemaProviderException;
import org.onap.aai.schemaif.definitions.PropertySchema;
import org.onap.aai.schemaif.definitions.types.BooleanDataType;
import org.onap.aai.schemaif.definitions.types.ComplexDataType;
import org.onap.aai.schemaif.definitions.types.DataType;
import org.onap.aai.schemaif.definitions.types.IntDataType;
import org.onap.aai.schemaif.definitions.types.ListDataType;
import org.onap.aai.schemaif.definitions.types.StringDataType;
import org.onap.aai.schemaif.json.definitions.DataTypeDefinition;
import org.onap.aai.schemaif.json.definitions.JsonPropertySchema;

public class FromJsonPropertySchema extends PropertySchema {

    public void fromJson(JsonPropertySchema pSchema, List<DataTypeDefinition> dataTypes) throws SchemaProviderException {
        name = pSchema.getName();
        defaultValue = pSchema.getDefaultValue() == null ? "" : pSchema.getDefaultValue();
        required = pSchema.getRequired();
        isKey = pSchema.getUnique();
        dataType = resolveDataType(pSchema.getDataType(), dataTypes);

        // Populate annotations
        annotations = new HashMap<String,String>();
        if (pSchema.getAnnotations() != null) {
            for (Map.Entry<String,String> entry : pSchema.getAnnotations().entrySet()) {
                annotations.put(entry.getKey(), entry.getValue());
            }
        }
    }
    
    private DataType resolveDataType(String typeString, List<DataTypeDefinition> dataTypes) throws SchemaProviderException {
        if (typeString.equalsIgnoreCase("string")) { 
            return new StringDataType();
        }
        
        if (typeString.equalsIgnoreCase("integer")) {
            return new IntDataType();
        }
        
        if (typeString.equalsIgnoreCase("boolean")) {
            return new BooleanDataType();
        }
        
        if (typeString.startsWith("list:")) {
            String segments[] = typeString.split(":");
            DataType subType = resolveDataType(segments[1], dataTypes);
            return new ListDataType(subType);
        }
        
        // Must be a complex type
        return resolveComplexDataType(typeString, dataTypes);
    }
    
    private DataType resolveComplexDataType(String typeString, List<DataTypeDefinition> dataTypes) throws SchemaProviderException {
        // It must be a custom/complex type.
        DataTypeDefinition dType = null;
        for (DataTypeDefinition d : dataTypes) {
            if ( (d.getName().equals(typeString)) ) {
                dType = d;
                break;
            }
        }
        
        if (dType == null) {
            throw new SchemaProviderException("Invalid data type: " + typeString);
        }
        
        List<PropertySchema> propList = new ArrayList<PropertySchema>();
        for (JsonPropertySchema p : dType.getProperties()) {
            FromJsonPropertySchema pSchema = new FromJsonPropertySchema();
            pSchema.fromJson(p, dataTypes);
            propList.add(pSchema);
        }
        
        return new ComplexDataType(propList);
    }
}
