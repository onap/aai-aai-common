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
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.introspection.Version;
import org.onap.aai.serialization.db.EdgeRules;
import org.onap.aai.serialization.db.EdgeType;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class SimplePathTest extends AAISetup {

	public Loader loader;
	
	@Before
	public void setup() throws Exception {
		loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, Version.getLatest());
	}

	private QueryBuilder<Vertex> buildTestQuery(QueryBuilder<Vertex> qb) throws AAIException {
		return qb.createEdgeTraversal(EdgeType.TREE, "generic-vnf", "l-interface")
				.until(qb.newInstance().getVerticesByProperty("aai-node-type", "generic-vnf"))
				.repeat(qb.newInstance().union(
							qb.newInstance().createEdgeTraversal(EdgeType.TREE, "generic-vnf", "l-interface"),
							qb.newInstance().createEdgeTraversal(EdgeType.TREE, "l-interface", "generic-vnf"),
							qb.newInstance().createEdgeTraversal(EdgeType.COUSIN, "l-interface", "logical-link"),
							qb.newInstance().createEdgeTraversal(EdgeType.COUSIN, "logical-link", "l-interface")
						).simplePath())
				.store("x").cap("x").unfold().dedup();
	}
	
	private GraphTraversalSource setupGraph() throws AAIException{
		Graph graph = TinkerGraph.open();
		GraphTraversalSource g = graph.traversal();
		EdgeRules rules = EdgeRules.getInstance();
		
		Vertex gvnf1 = graph.addVertex(T.label, "generic-vnf", T.id, "00", "aai-node-type", "generic-vnf", 
				"vnf-id", "gvnf1", "vnf-name", "genvnfname1", "nf-type", "sample-nf-type");

		Vertex lint1 = graph.addVertex(T.label, "l-interface", T.id, "10", "aai-node-type", "l-interface",
						"interface-name", "lint1", "is-port-mirrored", "true", "in-maint", "true", "is-ip-unnumbered", "false");
		
		Vertex loglink1 = graph.addVertex(T.label, "logical-link", T.id, "20", "aai-node-type", "logical-link",
						"link-name", "loglink1", "in-maint", "false", "link-type", "sausage");
		
		Vertex lint2 = graph.addVertex(T.label, "l-interface", T.id, "11", "aai-node-type", "l-interface",
						"interface-name", "lint2", "is-port-mirrored", "true", "in-maint", "true", "is-ip-unnumbered", "false");
		
		Vertex lint3 = graph.addVertex(T.label, "l-interface", T.id, "12", "aai-node-type", "l-interface",
				"interface-name", "lint3", "is-port-mirrored", "true", "in-maint", "true", "is-ip-unnumbered", "false");
		
		Vertex gvnf2 = graph.addVertex(T.label, "generic-vnf", T.id, "01", "aai-node-type", "generic-vnf", 
				"vnf-id", "gvnf2", "vnf-name", "genvnfname2", "nf-type", "sample-nf-type");
		
		rules.addTreeEdge(g, gvnf1, lint1);
		rules.addEdge(g, lint1, loglink1);
		rules.addEdge(g, loglink1, lint2);
		rules.addEdge(g, loglink1, lint3);
		rules.addTreeEdge(g, gvnf2, lint3);
		
		return g;
	}
	
	@Test
	public void gremlinQueryTest() throws AAIException {
		GraphTraversalSource g = setupGraph();
		List<Vertex> expected = g.V("01").toList();
		Vertex start = g.V("00").toList().get(0);
		
		GremlinTraversal<Vertex> qb = new GremlinTraversal<>(loader, g, start);
		QueryBuilder<Vertex> q = buildTestQuery(qb);
		List<Vertex> results = q.toList();
		assertTrue("results match", expected.containsAll(results) && results.containsAll(expected));
	}

	@Test
	public void traversalQueryTest() throws AAIException {
		GraphTraversalSource g = setupGraph();
		List<Vertex> expected = g.V("01").toList();
		Vertex start = g.V("00").toList().get(0);
		
		TraversalQuery<Vertex> qb = new TraversalQuery<>(loader, g, start);
		QueryBuilder<Vertex> q = buildTestQuery(qb);
		List<Vertex> results = q.toList();
		assertTrue("results match", expected.containsAll(results) && results.containsAll(expected));
	}
}
