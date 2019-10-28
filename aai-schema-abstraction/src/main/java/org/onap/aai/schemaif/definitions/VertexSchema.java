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


public class VertexSchema {
    protected String name;
    protected Map<String,PropertySchema> properties;
    protected Map<String,String> annotations;

    public String getName() {
        return name;
    }

    public PropertySchema getPropertySchema(String propName) {
        return properties.get(propName.toLowerCase());
    }

    public Map<String,PropertySchema> getPropertySchemaList() {
        return properties;
    }

    public Map<String,String> getAnnotationSchemaList() {
      return annotations;
    }
    
    public String getAnnotationValue(String annotation) {
        return annotations.get(annotation.toLowerCase());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("vertex: " + getName() + "\n");
        sb.append("  annotations: " + "\n");
        for (String annotation : annotations.keySet()) {
            sb.append("    " + annotation + ": " + annotations.get(annotation) + "\n");
        }
        sb.append("  properties: " + "\n");
        for (PropertySchema attrSchema : getPropertySchemaList().values()) {
            sb.append(attrSchema.toString());
        }

        return sb.toString();
    }
}
