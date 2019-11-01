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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.onap.aai.schemaif.SchemaProviderException;
import org.onap.aai.schemaif.definitions.EdgeSchema;
import org.onap.aai.schemaif.definitions.VertexSchema;

public class SchemaInstance {
    // vertex-name -> vertex-schema
    private Map<String, VertexSchema> vertexMap = new HashMap<>();
    
    // source:target:type -> edge-schema
    private Map<String, EdgeSchema> edgeKeyMap = new HashMap<>();
    
    // source:target -> edge-schema
    private Map<String, Set<EdgeSchema>> edgeSourceTargetMap = new HashMap<>();
    
    // vertex-name -> edge-schema
    private Map<String, Set<EdgeSchema>> vertexEdgeMap = new HashMap<>();
    
    public VertexSchema getVertexSchema(String vertexName) {
        return vertexMap.get(vertexName.toLowerCase());
    }
    
    public EdgeSchema getEdgeSchema(String source, String target, String type) {
        return edgeKeyMap.get(generateEdgeKey(source, target, type));
    }
    
    public Set<EdgeSchema> getEdgeSchemas(String source, String target) {
        return edgeSourceTargetMap.get(source.toLowerCase() + ":" + target.toLowerCase());
    }
    
    public Set<EdgeSchema> getEdgeSchema(String vertexName) {
        return vertexEdgeMap.get(vertexName.toLowerCase());
    }
    
    public void addVertex(VertexSchema v) {
        vertexMap.put(v.getName().toLowerCase(), v);
    }
    
    public void addEdge(EdgeSchema e) throws SchemaProviderException {
        if (e.getSource().equals(FromJsonEdgeSchema.WILDCARD_CHAR) || 
                e.getTarget().equals(FromJsonEdgeSchema.WILDCARD_CHAR)) {
            // Handle wildcard edges
            for (VertexSchema vertex : vertexMap.values()) {
                addWildcardEdge(e, vertex);
            }
        }
        else {
            addEdgeInternal(e);
        }
    }
    
    private void addWildcardEdge(EdgeSchema e, VertexSchema vertex) throws SchemaProviderException {
        FromJsonEdgeSchema newEdge = new FromJsonEdgeSchema(e);
        newEdge.replaceWildcard(vertex.getName());
        addEdgeInternal(newEdge);
    }

    private void addEdgeInternal(EdgeSchema e) {
        edgeKeyMap.put(generateEdgeKey(e.getSource(), e.getTarget(), e.getName()), e);
        
        Set<EdgeSchema> edgeListSource = vertexEdgeMap.get(e.getSource().toLowerCase());
        Set<EdgeSchema> edgeListTarget = vertexEdgeMap.get(e.getTarget().toLowerCase());
        
        if (edgeListSource == null) {
            edgeListSource = new HashSet<EdgeSchema>();
        }
        if (edgeListTarget == null) {
            edgeListTarget = new HashSet<EdgeSchema>();
        }
        
        edgeListSource.add(e);
        edgeListTarget.add(e);
        vertexEdgeMap.put(e.getSource().toLowerCase(), edgeListSource);
        vertexEdgeMap.put(e.getTarget().toLowerCase(), edgeListTarget);
        
        String sourceTargetKey = e.getSource().toLowerCase() + ":" + e.getTarget().toLowerCase();
        Set<EdgeSchema> edgeList = edgeSourceTargetMap.get(sourceTargetKey);
        if (edgeList == null) {
            edgeList = new HashSet<EdgeSchema>();
        }
        
        edgeList.add(e);
        edgeSourceTargetMap.put(sourceTargetKey, edgeList);
    }
    
    private String generateEdgeKey(String source, String target, String type) {
        String key = source + ":" + target + ":" + type;
        return key.toLowerCase();
    }

    public Map<String, VertexSchema> getVertexMap() {
      return vertexMap;
    }
}
