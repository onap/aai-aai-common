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


public class EdgeSchema {
    protected String name;
    protected String source;
    protected String target;
    protected Multiplicity multiplicity;
    protected Map<String,String> annotations;
    protected Map<String,PropertySchema> properties;

    public enum Multiplicity {
        MANY_2_MANY,
        MANY_2_ONE,
        ONE_2_MANY,
        ONE_2_ONE
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public Multiplicity getMultiplicity() {
        return multiplicity;
    }

    public PropertySchema getPropertySchema(String propName) {
        return properties.get(propName.toLowerCase());
    }

    public Map<String,PropertySchema> getPropertySchemaList() {
        return properties;
    }

    public String getAnnotationValue(String annotation) {
        return annotations.get(annotation.toLowerCase());
    }

    public Map<String,String> getAnnotations() {
        return annotations;
    }

    @Override
    public int hashCode() {
        String key = source + target + name;
        return key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        EdgeSchema other = (EdgeSchema) obj;

        return (source.equals(other.getSource()))
            && (target.equals(other.getTarget()))
            && (name.equals(other.getName()));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("edge: " + getSource() + " -> " + getTarget() + "\n");
        sb.append("  type: " + getName() + "\n");
        sb.append("  multiplicity: " + getMultiplicity() + "\n");

        sb.append("  annotations: " + "\n");
        for (Map.Entry<String, String> entry : annotations.entrySet()) {
            sb.append("    " + entry.getKey() + ": " + entry.getValue() + "\n");
        }
        sb.append("  properties: " + "\n");
        for (PropertySchema attrSchema : getPropertySchemaList().values()) {
            sb.append(attrSchema.toString());
        }

        return sb.toString();
    }

}
