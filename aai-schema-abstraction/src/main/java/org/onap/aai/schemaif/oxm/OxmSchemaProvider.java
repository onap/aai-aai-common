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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.onap.aai.edges.EdgeRule;
import org.onap.aai.schemaif.SchemaProvider;
import org.onap.aai.schemaif.SchemaProviderException;
import org.onap.aai.schemaif.definitions.EdgeSchema;
import org.onap.aai.schemaif.definitions.VertexSchema;


public class OxmSchemaProvider implements SchemaProvider {

    @Override
    public void loadSchema() throws SchemaProviderException {
        OxmEdgeRulesLoader.loadModels();
        OxmSchemaLoader.loadModels();
    }
    
    @Override
    public String getLatestSchemaVersion() throws SchemaProviderException {
        return OxmSchemaLoader.getLatestVersion();
    }

    @Override
    public VertexSchema getVertexSchema(String vertexName, String schemaVersion) throws SchemaProviderException {
        DynamicJAXBContext jaxbContext = OxmSchemaLoader.getContextForVersion(schemaVersion);
        FromOxmVertexSchema vs = new FromOxmVertexSchema();

        try {
            vs.fromOxm(vertexName, jaxbContext, OxmSchemaLoader.getXmlLookupMap(schemaVersion));
        }
        catch (SchemaProviderException ex) {
            // Node doesn't exist in schema.  Return null.
            return null;
        }

        return vs;
    }

    @Override
    public EdgeSchema getEdgeSchema(String edgeType, String sourceVertex, String targetVertex, String version)
            throws SchemaProviderException {
        RelationshipSchema relSchema = OxmEdgeRulesLoader.getSchemaForVersion(version);
        String key = sourceVertex + ":" + targetVertex + ":" + edgeType;

        EdgeRule edgeRule = relSchema.lookupEdgeRule(key);
        if (edgeRule == null) {
            return null;
        }
        
        FromOxmEdgeSchema es = new FromOxmEdgeSchema();
        es.fromEdgeRule(edgeRule);
        
        return es;
    }

    @Override
    public List<EdgeSchema> getAdjacentEdgeSchema(String vertexType, String version) throws SchemaProviderException {
        RelationshipSchema relSchema = OxmEdgeRulesLoader.getSchemaForVersion(version);
        List<EdgeSchema> edges = new ArrayList<EdgeSchema>();
        List<EdgeRule> rules = relSchema.lookupAdjacentEdges(vertexType);
        
        for (EdgeRule rule : rules) {
            FromOxmEdgeSchema es = new FromOxmEdgeSchema();
            es.fromEdgeRule(rule);
            edges.add(es);
        }
        
        return edges;
    }
    
    @Override
    public List<EdgeSchema> getEdgeSchemaForSourceTarget(String sourceType, String targetType, String version) throws SchemaProviderException {
        RelationshipSchema relSchema = OxmEdgeRulesLoader.getSchemaForVersion(version);
        List<EdgeSchema> edges = new ArrayList<EdgeSchema>();
        Set<String> relTypes = relSchema.getValidRelationTypes(sourceType, targetType);
        
        for (String type : relTypes) {
            EdgeSchema edgeSchema = getEdgeSchema(type, sourceType, targetType, version);
            if (edgeSchema != null) {
                edges.add(edgeSchema);
            }
        }
        
        return edges;
    }
}
