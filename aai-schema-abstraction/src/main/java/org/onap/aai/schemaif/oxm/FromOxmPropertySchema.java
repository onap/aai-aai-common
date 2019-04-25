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
package org.onap.aai.schemaif.oxm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLField;
import org.onap.aai.schemaif.SchemaProviderException;
import org.onap.aai.schemaif.definitions.PropertySchema;
import org.onap.aai.schemaif.definitions.types.BooleanDataType;
import org.onap.aai.schemaif.definitions.types.DataType;
import org.onap.aai.schemaif.definitions.types.FloatDataType;
import org.onap.aai.schemaif.definitions.types.IntDataType;
import org.onap.aai.schemaif.definitions.types.LongDataType;
import org.onap.aai.schemaif.definitions.types.StringDataType;


public class FromOxmPropertySchema extends PropertySchema {

    // Handle vertex properties from OXM
    public void fromOxm(DatabaseMapping mapping, DynamicType dynType) throws SchemaProviderException {
        DatabaseField field = mapping.getField();
        name = field.getName().substring(0, field.getName().indexOf("/"));

        defaultValue = mapping.getProperties().get("defaultValue") == null ? ""
                : mapping.getProperties().get("defaultValue").toString();

        required = ((XMLField) field).isRequired();
        isKey = isPrimaryKeyOxm(name, dynType);

        String oxmType = ((XMLField) field).getTypeName();
        if (oxmType.equalsIgnoreCase("java.lang.String")) { 
            dataType = new StringDataType();
        }
        else if (oxmType.equalsIgnoreCase("java.lang.Long")) {
            dataType = new LongDataType();
        }
        else if (oxmType.equalsIgnoreCase("java.lang.Boolean")) {
            dataType = new BooleanDataType();
        }
        else if (oxmType.equalsIgnoreCase("java.lang.Integer")) {
            dataType = new IntDataType();
        }
        else if (oxmType.equalsIgnoreCase("java.lang.Float")) {
            dataType = new FloatDataType();
        }
        else {
            throw new SchemaProviderException("Invalid OXM property type: " + oxmType);
        }  
        
        // Check annotations
        annotations = new HashMap<String,String>();
        Map<String, Object> oxmProps = mapping.getProperties();
        for (Map.Entry<String, Object> entry : oxmProps.entrySet()) {
            if (entry.getValue() instanceof String) {
                annotations.put(entry.getKey(), (String)entry.getValue());
            }
        }
    }

    // Handle edge properties from DBEdgeRules
    public void fromRelationship(String propName, DataType.Type propDataType) throws SchemaProviderException {
        name = propName;
        required = false;
        defaultValue = "";
        isKey = false;
        annotations = new HashMap<String,String>();
        
        switch (propDataType) {
            case STRING:
                dataType = new StringDataType();
                break;
            case INT:
                dataType = new IntDataType();
                break;
            case FLOAT:
                dataType = new FloatDataType();
                break;
            case LONG:
                dataType = new LongDataType();
                break;
            case BOOL:
                dataType = new BooleanDataType();
                break;
            default:
                throw new SchemaProviderException("Invalid EdgeRule property type: " + propDataType);    
        }
    }
    
    private boolean isPrimaryKeyOxm(String propName, DynamicType dynType) {
        List<String> primaryKeyList = dynType.getDescriptor().getPrimaryKeyFieldNames();
        if ( (primaryKeyList == null) || (primaryKeyList.size() == 0) ) {
            return false;
        }

        for (String key : primaryKeyList) {
            String keyName = key.substring(0, key.indexOf('/'));
            if (keyName.equals(propName)) {
                return true;
            }
        }

        return false;
    }

}
