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

package org.onap.aai.schemaif.definitions;

import java.util.Map;

import org.onap.aai.schemaif.SchemaProviderException;
import org.onap.aai.schemaif.definitions.types.DataType;


public class PropertySchema {
    protected String name;
    protected DataType dataType;
    protected Boolean required;
    protected String defaultValue;
    protected Boolean unique;
    protected Boolean isReserved;
    protected Map<String,String> annotations;

    public String getName() {
        return name;
    }

    public DataType getDataType() {
        return dataType;
    }

    public Boolean isRequired() {
        return required;
    }
    
    public Boolean isKey() {
        return (unique && required);
    }
    
    public Boolean isUnique() {
        return unique;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
    
    public Boolean isReserved() {
        return isReserved;
    }
    
    public String getAnnotationValue(String annotation) {
        return annotations.get(annotation.toLowerCase());
    }
    
    public Map<String, String> getAnnotations() {
      return annotations;
    }

    public Object validateValue(String value) throws SchemaProviderException {
        Object obj = dataType.validateValue(value);
        if (obj == null) {
            throw new SchemaProviderException("Invalid value for porperty '" + name + "': " + value);
        }
        
        return obj; 
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("    property: " + getName() + "\n");
        sb.append("      datatype: " + dataType.toString() + "\n");
        sb.append("      required: " + isRequired() + "\n");
        sb.append("      key: " + isKey() + "\n");
        sb.append("      reserved: " + isReserved() + "\n");
        sb.append("      default: " + getDefaultValue() + "\n");
        sb.append("      annotations: " + "\n");
        
        for (String annotation : annotations.keySet()) {
            sb.append("        " + annotation + ": " + annotations.get(annotation) + "\n");
        }
                
        return sb.toString();
    }
}
