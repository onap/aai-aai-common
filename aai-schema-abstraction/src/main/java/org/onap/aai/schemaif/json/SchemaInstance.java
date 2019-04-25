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

import org.onap.aai.schemaif.definitions.EdgeSchema;
import org.onap.aai.schemaif.definitions.VertexSchema;

public class SchemaInstance {
    // vertex-name -> vertex-schema
    private Map<String, VertexSchema> vertexMap = new HashMap<>();
    
    // source:target:type -> edge-schema
    private Map<String, EdgeSchema> edgeKeyMap = new HashMap<>();
    
    // source:target -> edge-schema
    private Map<String, List<EdgeSchema>> edgeSourceTargetMap = new HashMap<>();
    
    // vertex-name -> edge-schema
    private Map<String, List<EdgeSchema>> vertexEdgeMap = new HashMap<>();
    
    public VertexSchema getVertexSchema(String vertexName) {
        return vertexMap.get(vertexName);
    }
    
    public EdgeSchema getEdgeSchema(String source, String target, String type) {
        return edgeKeyMap.get(generateEdgeKey(source, target, type));
    }
    
    public List<EdgeSchema> getEdgeSchemas(String source, String target) {
        return edgeSourceTargetMap.get(source + ":" + target);
    }
    
    public List<EdgeSchema> getEdgeSchema(String vertexName) {
        return vertexEdgeMap.get(vertexName);
    }
    
    public void addVertex(VertexSchema v) {
        vertexMap.put(v.getName(), v);
    }
    
    public void addEdge(EdgeSchema e) {
        edgeKeyMap.put(generateEdgeKey(e.getSource(), e.getTarget(), e.getName()), e);
        
        List<EdgeSchema> edgeListSource = vertexEdgeMap.get(e.getSource());
        List<EdgeSchema> edgeListTarget = vertexEdgeMap.get(e.getTarget());
        
        if (edgeListSource == null) {
            edgeListSource = new ArrayList<EdgeSchema>();
        }
        if (edgeListTarget == null) {
            edgeListTarget = new ArrayList<EdgeSchema>();
        }
        
        edgeListSource.add(e);
        edgeListTarget.add(e);
        vertexEdgeMap.put(e.getSource(), edgeListSource);
        vertexEdgeMap.put(e.getTarget(), edgeListTarget);
        
        String sourceTargetKey = e.getSource() + ":" + e.getTarget();
        List<EdgeSchema> edgeList = edgeSourceTargetMap.get(sourceTargetKey);
        if (edgeList == null) {
            edgeList = new ArrayList<EdgeSchema>();
        }
        
        edgeList.add(e);
        edgeSourceTargetMap.put(sourceTargetKey, edgeList);
    }
    
    private String generateEdgeKey(String source, String target, String type) {
        return source + ":" + target + ":" + type;
    }
}
