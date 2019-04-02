/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.aai.serialization.engines.query;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.Tree;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.introspection.Loader;

import java.util.List;

public abstract class QueryEngine {

	final protected GraphTraversalSource g;
	protected double dbTimeMsecs = 0;
	/**
	 * Instantiates a new query engine.
	 *
     * @param g     graph traversal source to traverse the graph
	 */
	public QueryEngine (GraphTraversalSource g) {
		this.g = g;
	}

	/**
	 * Finds all the parents/grandparents/etc of the given start node.
	 *
	 * @param start - the start vertex whose parent chain you want
	 * @return the list of start and start's parent, grandparent, etc, in
	 * 			order (ie {start, parent, grandparent, etc}
	 */
	public abstract List<Vertex> findParents(Vertex start);

    /**
     * Finds all the parents/grandparents/etc of the given start node.
     *
     * This method should be used in place of the #findParents(Vertex)
     * as since the aai-uri is added the lookup for finding the parents
     * using the given list of aai-uri will be much faster than using
     * a traversal to follow a start vertex and keep repeating since
     * as the number of different type of edges keeps growing that call
     * will be more expensive than using the aai-uri's as they are fast lookup
     *
     * @param uris  - list of the uris representing the aai-uris of
     *                 parent, grandparent, etc
     * @return the list of start and start's parent, grandparent, etc, in
     * 			order (ie {start, parent, grandparent, etc}
     */
    public abstract List<Vertex> findParents(String [] uris);

	/**
	 * Finds all children, grandchildren, etc of start
	 *
	 * @param start the start vertex
	 * @return the list of child/grandchild/etc vertices
	 */
	public abstract List<Vertex> findAllChildren(Vertex start);

	/**
	 * Finds all immediate children of start (no grandchildren or so forth) of the given type
	 * @param start - the start vertex
	 * @param type - the desired aai-node-type
	 * @return the list of immediate child vertices of given type
	 */
	public abstract List<Vertex> findChildrenOfType(Vertex start, String type);

	/**
	 * Finds all immediate children of start (no grandchildren or so forth)
	 * @param start - the start vertex
	 * @return the list of immediate child vertices
	 */
	public abstract List<Vertex> findChildren(Vertex start);

	/**
	 * Find all vertices that should be deleted in a cascade from a delete of start
	 *
	 * @param start - the start vertex
	 * @return the list of vertices to be deleted when start is deleted
	 */
	public abstract List<Vertex> findDeletable(Vertex start);

    /**
     * Find all vertices that should be deleted in a cascade from a delete of start vertexes
     *
     * @param   startVertexes   Specifies the list of start vertexes
     *
     * @return  the list of vertices to be deleted when start list of vertexes is deleted
     */
    public abstract List<Vertex> findDeletable(List<Vertex> startVertexes);

	/**
	 * Finds the subgraph under start, including cousins as well as start's children/grandchildren/etc.
	 * More specifically, this includes start, all its descendants, start's cousins, and start's
	 * descendants' cousins (but not any of the cousins' cousins or descendants), and the edges
	 * connecting them.
	 *
	 * @param start - the start vertex
	 * @return - Tree containing nodes and edges of the subgraph
	 */
	public Tree<Element> findSubGraph(Vertex start) {
		return findSubGraph(start, AAIProperties.MAXIMUM_DEPTH, false);
	}

	/**
	 * Finds the subgraph under start, including cousins as well as start's children/grandchildren/etc.
	 * More specifically, this includes start, all its descendants, start's cousins, and start's
	 * descendants' cousins (but not any of the cousins' cousins or descendants), and the edges
	 * connecting them.
	 *
	 * @param start - the start vertex
	 * @param iterations - depth of the subgraph, this limits how many generations of
	 * 						descendants are included
	 * @param nodeOnly - if true the subgraph will NOT include the cousins
	 * @return Tree containing nodes and edges of the subgraph
	 */
	public abstract Tree<Element> findSubGraph(Vertex start, int iterations, boolean nodeOnly);

	/**
	 * Find vertices of type nodeType related to start by edges of the given
	 *  direction and label.
	 *
	 * @param start - the start vertex
	 * @param direction - the direction of edges to traverse from start
	 * @param label - the label of edges to traverse from start
	 * @param nodeType - the node type the results should be
	 * @return the list of related vertices
	 */
	public abstract List<Vertex> findRelatedVertices(Vertex start, Direction direction, String label, String nodeType);

	/**
	 * Finds cousin edges connecting start to other vertices only of types defined in an old version.
	 * The idea is that if a user is using an old version, they won't understand any new node types in
	 * subsequent versions. Thus, revealing edges to new types will cause problems. This methods
	 * filters any such edges out.
	 *
	 * @param start - the start vertex
	 * @param loader - loader for retrieving the list of allowed node types for the desired version
	 * 					(version is set when the loader was instantiated)
	 * @return list of cousin edges between start and any node types understood by the version specified in loader
	 */
	public abstract List<Edge> findEdgesForVersion(Vertex start, Loader loader);

	/**
	 * Finds all cousins of start.
	 *
	 * @param start - the start vertex
	 * @return list of start's cousin vertices
	 */
	public abstract List<Vertex> findCousinVertices(Vertex start, String... labels);

	public abstract double getDBTimeMsecs();

}
