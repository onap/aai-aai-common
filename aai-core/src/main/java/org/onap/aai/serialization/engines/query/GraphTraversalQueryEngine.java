/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai.serialization.engines.query;


import static org.onap.aai.serialization.db.AAIDirection.IN;
import static org.onap.aai.serialization.db.AAIDirection.NONE;
import static org.onap.aai.serialization.db.AAIDirection.OUT;
import static org.onap.aai.serialization.db.EdgeProperty.CONTAINS;
import static org.onap.aai.serialization.db.EdgeProperty.DELETE_OTHER_V;

import java.util.List;
import java.util.Set;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.Tree;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.introspection.Loader;

/*
 * This class needs some big explanation despite its compact size.
 * This controls all the queries performed by the CRUD API in A&AI. 
 * findParents, findChildren, and findDeletable require special attention
 *   These methods use 'repeat'. You cannot use 'emit' with repeat currently
 *   as it is extremely buggy as of tinkerpop-3.0.1-incubating. The way around
 *   it (for now) is to sideEffect all the vertices we traverse into an ArrayList.
 * 
 */
public class GraphTraversalQueryEngine extends QueryEngine {

	/**
	 * Instantiates a new graph traversal query engine.
	 *
	 * @param graphEngine the graph engine
	 */
	public GraphTraversalQueryEngine(GraphTraversalSource g) {
		super(g);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Vertex> findParents(Vertex start) {
		
		@SuppressWarnings("unchecked")
		final GraphTraversal<Vertex, Vertex> pipe = this.g.V(start).emit(v -> true).repeat(__.union(__.inE().has(CONTAINS.toString(), OUT.toString()).outV(), __.outE().has(CONTAINS.toString(), IN.toString()).inV()));
		return pipe.toList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Vertex> findAllChildren(Vertex start) {
		
		@SuppressWarnings("unchecked")
		GraphTraversal<Vertex, Vertex> pipe =  this.g
				.V(start).emit(v -> true).repeat(__.union(__.outE().has(CONTAINS.toString(), OUT.toString()).inV(), __.inE().has(CONTAINS.toString(), IN.toString()).outV()));
		

		return pipe.toList();
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Vertex> findChildrenOfType(Vertex start, String type) {
		@SuppressWarnings("unchecked")
		GraphTraversal<Vertex, Vertex> pipe =  this.g.V(start).union(
					__.outE().has(CONTAINS.toString(), OUT.toString()).inV(),
					__.inE().has(CONTAINS.toString(), IN.toString()).outV()
				).has(AAIProperties.NODE_TYPE, type).dedup();
 
		return pipe.toList();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Vertex> findChildren(Vertex start) {
		@SuppressWarnings("unchecked")
		GraphTraversal<Vertex, Vertex> pipe =  this.g.V(start).union(
					__.outE().has(CONTAINS.toString(), OUT.toString()),
					__.inE().has(CONTAINS.toString(), IN.toString())
				).otherV().dedup();
 
		return pipe.toList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Vertex> findDeletable(Vertex start) {
		@SuppressWarnings("unchecked")
		GraphTraversal<Vertex, Vertex> pipe = this.g
				.V(start).emit(v -> true).repeat(
					__.union(
						__.outE().has(DELETE_OTHER_V.toString(), OUT.toString()).inV(),
						__.inE().has(DELETE_OTHER_V.toString(), IN.toString()).outV()
					)
				);
		
		return pipe.toList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Vertex> findRelatedVertices(Vertex start, Direction direction, String label, String nodeType) {
		GraphTraversal<Vertex, Vertex> pipe = this.g.V(start);
		switch (direction) {
			case OUT:
				pipe.out(label);
				break;
			case IN:
				pipe.in(label);
				break;
			case BOTH:
				pipe.both(label);
				break;
			 default:
				break;
		}
		
		pipe.has(AAIProperties.NODE_TYPE, nodeType).dedup();
		return pipe.toList();
	}
	
	@Override
	public Tree<Element> findSubGraph(Vertex start, int iterations, boolean nodeOnly) {
		final GraphTraversal<Vertex, ?> t = this.g.V(start).emit(v -> true).times(iterations).repeat(
				__.union(
					__.outE().has(CONTAINS.toString(), OUT.toString()).inV(),
					__.inE().has(CONTAINS.toString(), IN.toString()).outV())
				);
			
		if (!nodeOnly) {
			t.union(
					__.identity(),
					__.bothE().has(CONTAINS.toString(), NONE.toString()).dedup().otherV()
			);
		}
		t.tree();
		if (t.hasNext()) {
			return (Tree)t.next();
		} else {
			return new Tree();
		}
	}

	@Override
	public List<Edge> findEdgesForVersion(Vertex start, Loader loader) {
		final Set<String> objects = loader.getAllObjects().keySet();
		GraphTraversal<Vertex, Edge> pipeline = this.g.V(start).union(
				__.inE().has(CONTAINS.toString(), NONE.toString()).where(__.outV().has(AAIProperties.NODE_TYPE, P.within(objects))),
				__.outE().has(CONTAINS.toString(), NONE.toString()).where(__.inV().has(AAIProperties.NODE_TYPE, P.within(objects)))
			).dedup();
		
		return pipeline.toList();
	}


	@Override
	public List<Vertex> findCousinVertices(Vertex start) {
		GraphTraversal<Vertex, Vertex> pipeline = this.g.V(start).union(
				__.inE().has(CONTAINS.toString(), NONE.toString()),
				__.outE().has(CONTAINS.toString(), NONE.toString())).otherV().dedup();
				
		return pipeline.toList();
	}
	
}

