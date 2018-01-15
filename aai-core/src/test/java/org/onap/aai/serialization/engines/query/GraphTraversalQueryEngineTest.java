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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.Tree;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerVertex;
import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.introspection.Version;
import org.onap.aai.serialization.db.EdgeRules;

public class GraphTraversalQueryEngineTest extends AAISetup {
	
	@Test
	public void testFindParents() throws AAIException {
		//setup
		EdgeRules rules = EdgeRules.getInstance();
		Graph graph = TinkerGraph.open();
		
		Vertex cloudreg = graph.addVertex(T.id, "00", "aai-node-type", "cloud-region");
		Vertex tenant = graph.addVertex(T.id, "10", "aai-node-type", "tenant");
		Vertex vserver = graph.addVertex(T.id, "20", "aai-node-type", "vserver");
		
		GraphTraversalSource g = graph.traversal();
		
		rules.addTreeEdge(g, cloudreg, tenant);
		rules.addTreeEdge(g, tenant, vserver);
		
		//expect start vertex back plus any parents
		List<Vertex> crExpected = new ArrayList<>(Arrays.asList(cloudreg)); //no parents
		List<Vertex> tenExpected = new ArrayList<>(Arrays.asList(tenant, cloudreg)); //only has the immediate parent
		List<Vertex> vsExpected = new ArrayList<>(Arrays.asList(vserver, tenant, cloudreg)); //parent & grandparent
		
		GraphTraversalQueryEngine engine = new GraphTraversalQueryEngine(g);
		
		//test
		List<Vertex> crRes = engine.findParents(cloudreg);
		assertTrue(crRes.containsAll(crExpected) && crExpected.containsAll(crRes));
		
		List<Vertex> tenRes = engine.findParents(tenant);
		assertTrue(tenRes.containsAll(tenExpected) && tenExpected.containsAll(tenRes));
		
		List<Vertex> vsRes = engine.findParents(vserver);
		assertTrue(vsRes.containsAll(vsExpected) && vsExpected.containsAll(vsRes));
		//verify expected ordering - start, parent, grandparent
		assertTrue(vsRes.get(0).equals(vserver));
		assertTrue(vsRes.get(1).equals(tenant));
		assertTrue(vsRes.get(2).equals(cloudreg));
	}
	
	@Test
	public void testFindAllChildren() throws AAIException {
		//setup
		EdgeRules rules = EdgeRules.getInstance();
		Graph graph = TinkerGraph.open();
		
		Vertex cloudreg = graph.addVertex(T.id, "00", "aai-node-type", "cloud-region");
		Vertex tenant = graph.addVertex(T.id, "10", "aai-node-type", "tenant");
		Vertex vserver = graph.addVertex(T.id, "20", "aai-node-type", "vserver");
		Vertex vserver2 = graph.addVertex(T.id, "21", "aai-node-type", "vserver");
		Vertex oam = graph.addVertex(T.id, "30", "aai-node-type", "oam-network");
		
		GraphTraversalSource g = graph.traversal();
		
		rules.addTreeEdge(g, cloudreg, tenant);
		rules.addTreeEdge(g, tenant, vserver);
		rules.addTreeEdge(g, tenant, vserver2);
		rules.addTreeEdge(g, cloudreg, oam);
		
		List<Vertex> crExpected = new ArrayList<>(Arrays.asList(cloudreg, tenant, vserver, vserver2, oam));
		List<Vertex> vsExpected = new ArrayList<>(Arrays.asList(vserver));
		
		GraphTraversalQueryEngine engine = new GraphTraversalQueryEngine(g);
		
		//test
		List<Vertex> crRes = engine.findAllChildren(cloudreg);
		assertTrue(crRes.containsAll(crExpected) && crExpected.containsAll(crRes));
		
		List<Vertex> vsRes = engine.findAllChildren(vserver);
		assertTrue(vsRes.containsAll(vsExpected) && vsExpected.containsAll(vsRes));
	}
	
	@Test
	public void testFindChildrenOfType() throws AAIException {
		//setup
		EdgeRules rules = EdgeRules.getInstance();
		Graph graph = TinkerGraph.open();
		
		Vertex gv = graph.addVertex(T.id, "00", "aai-node-type", "generic-vnf");
		Vertex lint1 = graph.addVertex(T.id, "10", "aai-node-type", "l-interface");
		Vertex lint2 = graph.addVertex(T.id, "11", "aai-node-type", "l-interface");
		Vertex lag = graph.addVertex(T.id, "20", "aai-node-type", "lag-interface");
		Vertex lint3 = graph.addVertex(T.id, "12", "aai-node-type", "l-interface");
		
		GraphTraversalSource g = graph.traversal();
		
		rules.addTreeEdge(g, gv, lint1);
		rules.addTreeEdge(g, gv, lint2);
		rules.addTreeEdge(g, gv, lag);
		rules.addTreeEdge(g, lag, lint3);
		
		List<Vertex> expected = new ArrayList<>(Arrays.asList(lint1, lint2));
		
		GraphTraversalQueryEngine engine = new GraphTraversalQueryEngine(g);
		
		//test
		List<Vertex> results = engine.findChildrenOfType(gv, "l-interface");
		assertTrue(results.containsAll(expected) && expected.containsAll(results));
	}
	
	@Test
	public void testFindChildren() throws AAIException {
		//setup
		EdgeRules rules = EdgeRules.getInstance();
		Graph graph = TinkerGraph.open();
		
		Vertex gv = graph.addVertex(T.id, "00", "aai-node-type", "generic-vnf");
		Vertex lint1 = graph.addVertex(T.id, "10", "aai-node-type", "l-interface");
		Vertex lint2 = graph.addVertex(T.id, "11", "aai-node-type", "l-interface");
		Vertex lag = graph.addVertex(T.id, "20", "aai-node-type", "lag-interface");
		Vertex lint3 = graph.addVertex(T.id, "12", "aai-node-type", "l-interface");
		
		GraphTraversalSource g = graph.traversal();
		
		rules.addTreeEdge(g, gv, lint1);
		rules.addTreeEdge(g, gv, lint2);
		rules.addTreeEdge(g, gv, lag);
		rules.addTreeEdge(g, lag, lint3);
		
		List<Vertex> expected = new ArrayList<>(Arrays.asList(lint1, lint2, lag));
		
		GraphTraversalQueryEngine engine = new GraphTraversalQueryEngine(g);
		
		//test
		List<Vertex> results = engine.findChildren(gv);
		assertTrue(results.containsAll(expected) && expected.containsAll(results));
	}

	@Test
	public void testFindDeletable() throws AAIException {
		//setup
		EdgeRules rules = EdgeRules.getInstance("/dbedgerules/DbEdgeRules_test.json");
		
		Graph graph = TinkerGraph.open();
		Vertex parent = graph.addVertex(T.id, "00", "aai-node-type", "test-parent");
		Vertex child = graph.addVertex(T.id, "10", "aai-node-type", "test-child");
		Vertex cousin = graph.addVertex(T.id, "20", "aai-node-type", "test-cousin");
		Vertex grandchild = graph.addVertex(T.id, "30", "aai-node-type", "test-grandchild");

		GraphTraversalSource g = graph.traversal();
		
		rules.addTreeEdge(g, parent, child); //delete-other-v=none, no cascade
		rules.addTreeEdge(g, child, grandchild); //d-o-v=out, yes from child
		rules.addEdge(g, cousin, child); //d-o-v=out, yes from cousin
		
		List<Vertex> parentExpected = new ArrayList<>(Arrays.asList(parent));
		List<Vertex> childExpected = new ArrayList<>(Arrays.asList(child, grandchild));
		List<Vertex> cousinExpected = new ArrayList<>(Arrays.asList(cousin, child, grandchild));
		
		GraphTraversalQueryEngine engine = new GraphTraversalQueryEngine(g);
		
		//tests
		List<Vertex> parentDeletes = engine.findDeletable(parent);
		assertTrue(parentExpected.containsAll(parentDeletes) && parentDeletes.containsAll(parentExpected));
		
		List<Vertex> childDeletes = engine.findDeletable(child);
		assertTrue(childExpected.containsAll(childDeletes) && childDeletes.containsAll(childExpected));
		
		List<Vertex> cousinDeletes = engine.findDeletable(cousin);
		assertTrue(cousinExpected.containsAll(cousinDeletes) && cousinDeletes.containsAll(cousinExpected));
	}
	
	@Test
	public void testFindRelatedVertices() throws AAIException {
		//setup
		EdgeRules rules = EdgeRules.getInstance();
		
		Graph graph = TinkerGraph.open();
		
		Vertex gv = graph.addVertex(T.id, "00", "aai-node-type", "generic-vnf");
		Vertex lint = graph.addVertex(T.id, "10", "aai-node-type", "l-interface");
		Vertex lint2 = graph.addVertex(T.id, "11", "aai-node-type", "l-interface");
		Vertex log = graph.addVertex(T.id, "20", "aai-node-type", "logical-link");
		
		GraphTraversalSource g = graph.traversal();
		
		rules.addTreeEdge(g, gv, lint);
		rules.addEdge(g, lint, log);
		rules.addEdge(g, log, lint2);
		
		List<Vertex> outExpected = new ArrayList<>(Arrays.asList(lint));
		List<Vertex> inExpected = new ArrayList<>(Arrays.asList(lint, lint2));
		List<Vertex> bothExpected = new ArrayList<>(Arrays.asList(log));
		
		GraphTraversalQueryEngine engine = new GraphTraversalQueryEngine(g);
			
		//test
		List<Vertex> outRes = engine.findRelatedVertices(gv, Direction.OUT, "hasLInterface", "l-interface");
		assertTrue(outRes.containsAll(outExpected) && outExpected.containsAll(outRes));
		
		List<Vertex> inRes = engine.findRelatedVertices(log, Direction.IN, "usesLogicalLink", "l-interface");
		assertTrue(inRes.containsAll(inExpected) && inExpected.containsAll(inRes));
		
		List<Vertex> bothRes = engine.findRelatedVertices(lint, Direction.BOTH, "usesLogicalLink", "logical-link");
		assertTrue(bothRes.containsAll(bothExpected) && bothExpected.containsAll(bothRes));
	}
	
	@Test
	public void testFindSubGraph() throws AAIException {
		//setup
		EdgeRules rules = EdgeRules.getInstance();
		Graph graph = TinkerGraph.open();
		
		Vertex cr = graph.addVertex(T.id, "00", "aai-node-type", "cloud-region");
		Vertex ten = graph.addVertex(T.id, "10", "aai-node-type", "tenant");
		Vertex ten2 = graph.addVertex(T.id, "11", "aai-node-type", "tenant");
		Vertex vs = graph.addVertex(T.id, "20", "aai-node-type", "vserver");
		Vertex vs2 = graph.addVertex(T.id, "21", "aai-node-type", "vserver");
		Vertex lint = graph.addVertex(T.id, "30", "aai-node-type", "l-interface");
		Vertex comp = graph.addVertex(T.id, "40", "aai-node-type", "complex");
		Vertex ctag = graph.addVertex(T.id, "50", "aai-node-type", "ctag-pool");
		Vertex gv = graph.addVertex(T.id, "60", "aai-node-type", "generic-vnf");
		Vertex lag = graph.addVertex(T.id, "70", "aai-node-type", "lag-interface");
		Vertex lint2 = graph.addVertex(T.id, "31", "aai-node-type", "l-interface");
		Vertex log = graph.addVertex(T.id, "80", "aai-node-type", "logical-link");
		Vertex vnfc = graph.addVertex(T.id, "90", "aai-node-type", "vnfc");
		
		GraphTraversalSource g = graph.traversal();
		
		Edge crTen = rules.addTreeEdge(g, cr, ten);
		Edge crTen2 = rules.addTreeEdge(g, cr, ten2);
		Edge tenVs = rules.addTreeEdge(g, ten, vs);
		Edge tenVs2 = rules.addTreeEdge(g, ten, vs2);
		Edge vsLInt = rules.addTreeEdge(g, vs, lint);
		Edge lintLog = rules.addEdge(g, lint, log);
		Edge vsGv = rules.addEdge(g, vs, gv);
		rules.addEdge(g, gv, vnfc);
		
		rules.addTreeEdge(g, gv, lag);
		rules.addTreeEdge(g, lag, lint2);
		
		rules.addTreeEdge(g, comp, ctag);
		Edge crComp = rules.addEdge(g, cr, comp);
		
		//findSubGraph(cr, 0, true)
		List<Element> expected1 = new ArrayList<>(Arrays.asList(cr));
		//findSubGraph(cr, 2, true)
		List<Element> expected2 = new ArrayList<>(Arrays.asList(cr, ten, ten2, vs, vs2,
																	crTen, crTen2, tenVs, tenVs2));
		//findSubGraph(cr)
		List<Element> expected3 = new ArrayList<>(Arrays.asList(cr, ten, ten2, comp, vs, vs2, lint, gv, log,
																crTen, crTen2, crComp, tenVs, tenVs2, vsLInt, 
																vsGv, lintLog));
		
		GraphTraversalQueryEngine engine = new GraphTraversalQueryEngine(g);
		
		//test
		Tree<Element> res1 = engine.findSubGraph(cr, 0, true);
		Set<Element> resList1 = treeToList(res1);
		assertTrue(resList1.containsAll(expected1) && expected1.containsAll(resList1));
		
		Tree<Element> res2 = engine.findSubGraph(cr, 2, true);
		Set<Element> resList2 = treeToList(res2);
		assertTrue(resList2.containsAll(expected2) && expected2.containsAll(resList2));
		
		Tree<Element> res3 = engine.findSubGraph(cr);
		Set<Element> resList3 = treeToList(res3);
		assertTrue(resList3.containsAll(expected3) && expected3.containsAll(resList3));
	}
	
	/**
	 * convenience helper method to make it easier to check the contents of the tree against 
	 * a list of expected results
	 * @param tree - the tree whose contents you want in collection form
	 * @return set of the contents of the tree
	 */
	private Set<Element> treeToList(Tree<Element> tree) {
		Set<Element> ret = new HashSet<>();
		
		for (Element key : tree.keySet()) {
			ret.add(key);
			ret.addAll(treeToList(tree.get(key)));
		}
		
		return ret;
	}
	
	@Test
	public void testFindEdgesForVersion() throws AAIException {
		//setup
		EdgeRules rules = EdgeRules.getInstance();
		Graph graph = TinkerGraph.open();
		
		Vertex gv = graph.addVertex(T.id, "00", "aai-node-type", "generic-vnf");
		Vertex vnfc = graph.addVertex(T.id, "10", "aai-node-type", "vnfc");
		Vertex lob = graph.addVertex(T.id, "20", "aai-node-type", "line-of-business");
		Vertex lint = graph.addVertex(T.id, "30", "aai-node-type", "l-interface");
		
		GraphTraversalSource g = graph.traversal();
		
		rules.addTreeEdge(g, gv, lint); //tree edge so shouldn't appear in results
		Edge gvVnfc = rules.addEdge(g, gv, vnfc);
		rules.addEdge(g, gv, lob); //v11/12 not v10
		
		List<Edge> expected = new ArrayList<>(Arrays.asList(gvVnfc));
		
		GraphTraversalQueryEngine engine = new GraphTraversalQueryEngine(g);
		
		//test
		Loader loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, Version.v10);
		List<Edge> results = engine.findEdgesForVersion(gv, loader);
		assertTrue(results.containsAll(expected) && expected.containsAll(results));
	}
	
	@Test
	public void testFindCousinVertices() throws AAIException {
		//setup
		EdgeRules rules = EdgeRules.getInstance();
		Graph graph = TinkerGraph.open();
		
		Vertex gv = graph.addVertex(T.id, "00", "aai-node-type", "generic-vnf");
		Vertex vnfc = graph.addVertex(T.id, "10", "aai-node-type", "vnfc");
		Vertex lob = graph.addVertex(T.id, "20", "aai-node-type", "line-of-business");
		Vertex lint = graph.addVertex(T.id, "30", "aai-node-type", "l-interface");
		
		GraphTraversalSource g = graph.traversal();
		
		rules.addTreeEdge(g, gv, lint); //tree edge so shouldn't appear in results
		rules.addEdge(g, gv, vnfc);
		rules.addEdge(g, gv, lob); 
		
		List<Vertex> expected = new ArrayList<>(Arrays.asList(vnfc, lob));
		
		GraphTraversalQueryEngine engine = new GraphTraversalQueryEngine(g);
		
		//test
		List<Vertex> results = engine.findCousinVertices(gv);
		assertTrue(results.containsAll(expected) && expected.containsAll(results));
	}
}
