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
import java.util.Map;

import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.onap.aai.schemaif.SchemaProviderException;
import org.onap.aai.schemaif.definitions.PropertySchema;
import org.onap.aai.schemaif.definitions.VertexSchema;

import com.google.common.base.CaseFormat;

public class FromOxmVertexSchema extends VertexSchema {
    public void fromOxm(String vertexType, DynamicJAXBContext jaxbContext, HashMap<String, DynamicType> xmlElementLookup) throws SchemaProviderException {
        name = vertexType;
        properties = new HashMap<String,PropertySchema>();
        annotations = new HashMap<String,String>();

        String javaTypeName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, vertexType);
        DynamicType modelObjectType = jaxbContext.getDynamicType(javaTypeName);

        if (modelObjectType == null) {
            // Try to lookup by xml root element by exact match
            modelObjectType = xmlElementLookup.get(vertexType);
        }

        if (modelObjectType == null) {
            // Try to lookup by xml root element by lowercase
            modelObjectType = xmlElementLookup.get(vertexType.toLowerCase());
        }

        if (modelObjectType == null) {
            // Direct lookup as java-type name
            modelObjectType = jaxbContext.getDynamicType(vertexType);
        }

        if (modelObjectType == null) {
            // Vertex isn't found in the OXM
            throw new SchemaProviderException("Vertex " + vertexType + " not found in OXM");
        }
        
        // Check annotations
        Map<String, Object> oxmProps = modelObjectType.getDescriptor().getProperties();
        for (Map.Entry<String, Object> entry : oxmProps.entrySet()) {
            if (entry.getValue() instanceof String) {
                annotations.put(entry.getKey(), (String)entry.getValue());
            }
        }

        // Regular props
        for (DatabaseMapping mapping : modelObjectType.getDescriptor().getMappings()) {
            if (mapping.isAbstractDirectMapping()) {
                FromOxmPropertySchema propSchema = new FromOxmPropertySchema();
                propSchema.fromOxm(mapping, modelObjectType);
                properties.put(propSchema.getName(), propSchema);
            }
        }

        // Reserved Props
        final DynamicType reservedType = jaxbContext.getDynamicType("ReservedPropNames");
        for (DatabaseMapping mapping : reservedType.getDescriptor().getMappings()) {
            if (mapping.isAbstractDirectMapping()) {
                FromOxmPropertySchema propSchema = new FromOxmPropertySchema();
                propSchema.fromOxm(mapping, reservedType);
                properties.put(propSchema.getName(), propSchema);
            }
        } 
    }
}
