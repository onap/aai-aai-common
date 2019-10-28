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

package org.onap.aai.schemaif.definitions.types;

import java.util.List;

import org.onap.aai.schemaif.definitions.PropertySchema;

public class ComplexDataType extends DataType {    
    private List<PropertySchema> subProperties;
    
    public ComplexDataType(List<PropertySchema> subProperties) {
        super(Type.COMPLEX);
        this.subProperties = subProperties;
    }

    public List<PropertySchema> getSubProperties() {
        return subProperties;
    }
    
    @Override
    public Object validateValue(String value) {
        // TODO: Validate the complex type against the subProperties
        return value;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("COMPLEX[ ");
        
        for (PropertySchema prop : getSubProperties()) {
            sb.append(prop.getDataType().toString() + " ");
        }
        
        sb.append("]");
        
        return sb.toString();
    }
}
