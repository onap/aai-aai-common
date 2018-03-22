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
package org.onap.aai.query.builder;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Before;
import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.serialization.db.EdgeRules;
import org.onap.aai.serialization.db.EdgeType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class UntilTest extends AAISetup {

	private Loader loader;
	
	@Before
	public void setup() throws Exception {
		loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, AAIProperties.LATEST);
	}
	
	private QueryBuilder<Vertex> buildTestQuery(QueryBuilder<Vertex> qb) throws AAIException{
		return qb.until(qb.newInstance().getVerticesByProperty("aai-node-type", "l-interface")).repeat(
				qb.newInstance().union(
						qb.newInstance().createEdgeTraversal(EdgeType.TREE, "cloud-region", "tenant"),
						qb.newInstance().createEdgeTraversal(EdgeType.TREE, "tenant", "vserver"),
						qb.newInstance().createEdgeTraversal(EdgeType.TREE, "vserver", "l-interface")
			)).store("x").cap("x").unfold().dedup();
	}
	
	@Test
	public void gremlinQueryUntilTest() throws AAIException {
		Graph graph = TinkerGraph.open();
		EdgeRules rules = EdgeRules.getInstance();
		GraphTraversalSource g = graph.traversal();
		
		Vertex v1 = graph.addVertex(T.id, 1, "aai-node-type", "cloud-region");
		Vertex v2 = graph.addVertex(T.id, 2, "aai-node-type", "tenant");
		Vertex v3 = graph.addVertex(T.id, 3, "aai-node-type", "vserver");
		Vertex v4 = graph.addVertex(T.id, 4, "aai-node-type", "l-interface");
		rules.addTreeEdge(g, v1, v2);
		rules.addTreeEdge(g, v2, v3);
		rules.addTreeEdge(g, v3, v4);
		List<Vertex> expected = new ArrayList<>();
		expected.add(v4);
		
		GremlinTraversal<Vertex> qb =  new GremlinTraversal<>(loader, g, v1);
		QueryBuilder q = buildTestQuery(qb);
		
		List<Vertex> results = q.toList();

		assertTrue("results match", expected.containsAll(results) && results.containsAll(expected));
	}

	@Test
	public void traversalQueryUntilTest() throws AAIException {
		Graph graph = TinkerGraph.open();
		EdgeRules rules = EdgeRules.getInstance();
		GraphTraversalSource g = graph.traversal();
		
		Vertex v1 = graph.addVertex(T.id, 1, "aai-node-type", "cloud-region");
		Vertex v2 = graph.addVertex(T.id, 2, "aai-node-type", "tenant");
		Vertex v3 = graph.addVertex(T.id, 3, "aai-node-type", "vserver");
		Vertex v4 = graph.addVertex(T.id, 4, "aai-node-type", "l-interface");
		rules.addTreeEdge(g, v1, v2);
		rules.addTreeEdge(g, v2, v3);
		rules.addTreeEdge(g, v3, v4);
		List<Vertex> expected = new ArrayList<>();
		expected.add(v4);
		
		TraversalQuery<Vertex> qb =  new TraversalQuery<>(loader, g, v1);
		QueryBuilder<Vertex> q = buildTestQuery(qb);
		
		List<Vertex> results = q.toList();

		assertTrue("results match", expected.containsAll(results) && results.containsAll(expected));
	}
	
	

}
