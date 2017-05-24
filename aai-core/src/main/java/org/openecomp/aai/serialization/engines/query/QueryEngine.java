/*-
 * ============LICENSE_START=======================================================
 * org.openecomp.aai
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.aai.serialization.engines.query;

import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.Tree;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import org.openecomp.aai.db.props.AAIProperties;
import org.openecomp.aai.introspection.Loader;

public abstract class QueryEngine {

	final protected GraphTraversalSource g;

	/**
	 * Instantiates a new query engine.
	 *
	 * @param graphEngine the graph engine
	 */
	public QueryEngine (GraphTraversalSource g) {
		this.g = g;
	}
	
	/**
	 * Find parents.
	 *
	 * @param start the start
	 * @return the list
	 */
	public abstract List<Vertex> findParents(Vertex start);
	
	/**
	 * Find children.
	 *
	 * @param start the start
	 * @return the list
	 */
	public abstract List<Vertex> findAllChildren(Vertex start);
	
	public abstract List<Vertex> findChildrenOfType(Vertex start, String type);
	
	public abstract List<Vertex> findChildren(Vertex start);
	/**
	 * Find deletable.
	 *
	 * @param start the start
	 * @return the list
	 */
	public abstract List<Vertex> findDeletable(Vertex start);
	
	public Tree<Element> findSubGraph(Vertex start) {
		return findSubGraph(start, AAIProperties.MAXIMUM_DEPTH, false);
	}
	public abstract Tree<Element> findSubGraph(Vertex start, int iterations, boolean nodeOnly);
	/**
	 * Find related vertices.
	 *
	 * @param start the start
	 * @param direction the direction
	 * @param label the label
	 * @param nodeType the node type
	 * @return the list
	 */
	public abstract List<Vertex> findRelatedVertices(Vertex start, Direction direction, String label, String nodeType);

	public abstract List<Edge> findEdgesForVersion(Vertex start, Loader loader);
	
	public abstract List<Vertex> findCousinVertices(Vertex start);

}
