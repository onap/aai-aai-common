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

package org.onap.aai.schemaif;

import java.util.List;

import org.onap.aai.schemaif.definitions.EdgeSchema;
import org.onap.aai.schemaif.definitions.VertexSchema;

public interface SchemaProvider {
    
    /**
     * Load the schema into memory
     */
    public void loadSchema() throws SchemaProviderException;
    
    /**
     * Get the identifier for the more recent version of the schema
     *
     * @return The schema version identifier
     */
    public String getLatestSchemaVersion() throws SchemaProviderException;
    
    /**
     * Get the schema definition for a vertex
     *
     * @param vertexName - Name of the vertex
     * @param schemaVersion - Version of the schema to use
     * 
     * @return The vertex schema definition
     */
    public VertexSchema getVertexSchema(String vertexName, String schemaVersion) throws SchemaProviderException;
    
    /**
     * Get the schema definition for an edge
     *
     * @param edgeType - Type of the edge
     * @param sourceVertex - The source vertex for the edge
     * @param targetVertex - The target vertex for the edge
     * @param schemaVersion - Version of the schema to use
     * 
     * @return The edge schema definition
     */
    public EdgeSchema getEdgeSchema(String edgeType, String sourceVertex, String targetVertex, String version) throws SchemaProviderException;

    /**
     * Get the list of edge definitions which are adjacent to the given vertex
     *
     * @param vertexType - Type of the vertex
     * @param schemaVersion - Version of the schema to use
     * 
     * @return The list of edge schema definitions
     */
    public List<EdgeSchema> getAdjacentEdgeSchema(String vertexType, String version) throws SchemaProviderException;
    
    /**
     * Get the list of edge definitions which are valid for the given source and target
     *
     * @param sourceType - Type of the source vertex
     * @param targetType - Type of the target vertex
     * @param schemaVersion - Version of the schema to use
     * 
     * @return The list of edge schema definitions
     */
    public List<EdgeSchema> getEdgeSchemaForSourceTarget(String sourceType, String targetType, String version) throws SchemaProviderException;
}
