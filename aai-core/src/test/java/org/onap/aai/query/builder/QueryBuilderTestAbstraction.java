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
package org.onap.aai.query.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.BulkSet;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
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

import com.thinkaurelius.titan.core.TitanFactory;
import org.onap.aai.serialization.db.exceptions.NoEdgeRuleFoundException;

public abstract class QueryBuilderTestAbstraction extends AAISetup {

	protected Loader loader;
	protected Graph graph;
	protected GraphTraversalSource g;
	
	protected EdgeRules testEdgeRules = EdgeRules.getInstance("/dbedgerules/DbEdgeRules_TraversalQueryTest.json");

	@Before
	public void configure() throws Exception {
		loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, AAIProperties.LATEST);
		graph = TitanFactory.build().set("storage.backend", "inmemory").open();
		g = graph.traversal();
	}
	
	
	@Test
	public void createEdgeGVnfToVnfcTraversal() throws AAIException {
				
		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","myvnf").next();
		Vertex vnfc = g.addV("aai-node-type","vnfc","vnfc-name","a-name").next();
		testEdgeRules.addEdge(g, gvnf, vnfc, "uses");
		
		QueryBuilder<Vertex> tQ = getNewVertexTraversal(gvnf);
		tQ.createEdgeTraversal(EdgeType.COUSIN, "generic-vnf", "vnfc");
		
		assertEquals(vnfc, tQ.next());
		
		g.tx().rollback();
	}
	
	@Test
	public void createEdgeLinterfaceToLogicalLinkTraversal() throws AAIException {
				
		Vertex lInterface = g.addV("aai-node-type","l-interface","interface-name","l-interface-a").next();
		Vertex logicalLink = g.addV("aai-node-type","logical-link","link-name","logical-link-a").next();
		testEdgeRules.addEdge(g, lInterface, logicalLink, "sourceLInterface");
		
		QueryBuilder<Vertex> tQ = getNewVertexTraversal(lInterface);
		tQ.createEdgeTraversal(EdgeType.COUSIN, "l-interface", "logical-link");
		
		Vertex next = tQ.next();
		
		assertEquals(logicalLink, next);
		
		g.tx().rollback();
	}
	
	@Test
	public void createEdgeLinterfaceToLogicalLinkIntrospectorTraversal() throws AAIException {
				
		Vertex lInterface = g.addV("aai-node-type","l-interface","interface-name","l-interface-a").next();
		Vertex logicalLink = g.addV("aai-node-type","logical-link","link-name","logical-link-a").next();
		testEdgeRules.addEdge(g, lInterface, logicalLink, "sourceLInterface");
		
		QueryBuilder<Vertex> tQ = getNewVertexTraversal(lInterface);
		tQ.createEdgeTraversal(EdgeType.COUSIN, loader.introspectorFromName("l-interface"), loader.introspectorFromName("logical-link"));
		
		Vertex next = tQ.next();
		
		assertEquals(logicalLink, next);
		
		g.tx().rollback();
	}
	
	@Test
	public void createEdgeLinterfaceToLogicalLinkVertexToIntrospectorTraversal() throws AAIException {
				
		Vertex lInterface = g.addV("aai-node-type","l-interface","interface-name","l-interface-a").next();
		Vertex logicalLink = g.addV("aai-node-type","logical-link","link-name","logical-link-a").next();
		testEdgeRules.addEdge(g, lInterface, logicalLink, "sourceLInterface");
		
		QueryBuilder<Vertex> tQ = getNewVertexTraversal(lInterface);
		tQ.createEdgeTraversal(EdgeType.COUSIN, lInterface, loader.introspectorFromName("logical-link"));
		
		Vertex next = tQ.next();
		
		assertEquals(logicalLink, next);
		
		g.tx().rollback();
	}
	
	@Test
	public void edgeToVertexTraversalTest() throws AAIException {
				
		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex vnfc1 = g.addV("aai-node-type","vnfc","vnfc-name","a-name").next();
		
		testEdgeRules.addEdge(g, gvnf, vnfc1);
		
		QueryBuilder<Vertex> tQ = getNewVertexTraversal(gvnf);
		tQ.createEdgeTraversal(EdgeType.COUSIN, "generic-vnf", "vnfc");
		
		List<Vertex> list = tQ.toList();

		assertEquals("Has 1 vertexes ", 1, list.size());
		assertTrue("Has vertex on the default edge ", list.contains(vnfc1));
		
		g.tx().rollback();
	}
	
	@Test
	public void edgeToVertexTraversalSingleOutRuleTest() throws AAIException {
				
		Vertex vce = g.addV("aai-node-type","vce","vnf-id","vce").next();
		Vertex vnfc1 = g.addV("aai-node-type","vnfc","vnfc-name","a-name").next();
		
		testEdgeRules.addEdge(g, vce, vnfc1);
		
		QueryBuilder<Vertex> tQ1 = getNewVertexTraversal(vce);
		tQ1.createEdgeTraversal(EdgeType.COUSIN, "vce", "vnfc");
		
		QueryBuilder<Vertex> tQ2 = getNewVertexTraversal(vnfc1);
		tQ2.createEdgeTraversal(EdgeType.COUSIN, "vnfc", "vce");
		
		List<Vertex> list1 = tQ1.toList();
		List<Vertex> list2 = tQ2.toList();
		
		assertEquals("1 - Has 1 vertexes ", 1, list1.size());
		assertTrue("1 - traversal results in vnfc ", list1.contains(vnfc1));
		assertEquals("2 - Has 1 vertexes ", 1, list2.size());
		assertTrue("2 - traversal results in vce ", list2.contains(vce));
		
		g.tx().rollback();
	}
	
	@Test
	public void edgeToVertexTraversalSingleInRuleTest() throws AAIException {
				
		Vertex vce = g.addV("aai-node-type","vce","vnf-id","vce").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();
		
		testEdgeRules.addEdge(g, vce, pserver);
		
		QueryBuilder<Vertex> tQ1 = getNewVertexTraversal(vce);
		tQ1.createEdgeTraversal(EdgeType.COUSIN, "vce", "pserver");
		
		List<Vertex> list = tQ1.toList();

		assertEquals("1 - Has 1 vertexes ", 1, list.size());
		assertTrue("1 - traversal results in vnfc ", list.contains(pserver));
		
		g.tx().rollback();
	}
	
	@Test
	public void edgeToVertexMultiRuleTraversalTest() throws AAIException {
				
		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex vnfc1 = g.addV("aai-node-type","vnfc","vnfc-name","a-name").next();
		Vertex vnfc2 = g.addV("aai-node-type","vnfc","vnfc-name","b-name").next();
		
		testEdgeRules.addEdge(g, gvnf, vnfc1);
		testEdgeRules.addEdge(g, gvnf, vnfc2, "re-uses");
		
		QueryBuilder<Vertex> tQ = getNewVertexTraversal(gvnf);
		tQ.createEdgeTraversal(EdgeType.COUSIN, "generic-vnf", "vnfc");
		
		List<Vertex> list = tQ.toList();

		assertEquals("Has 2 vertexes ", 2, list.size());
		assertTrue("Has vertex on the default edge ", list.contains(vnfc1));
		assertTrue("Has vertex on the re-uses edge ", list.contains(vnfc2));
		
		g.tx().rollback();
	}
	
	@Test
	public void edgeToVertexMultiLabelTest() throws AAIException {
				
		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();
		Vertex vnfc1 = g.addV("aai-node-type","vnfc","vnfc-name","a-name").next();
		
		testEdgeRules.addEdge(g, gvnf, vnfc1);
		testEdgeRules.addEdge(g, pserver, vnfc1);
		
		QueryBuilder<Vertex> tQ = getNewVertexTraversal(vnfc1);
		tQ.createEdgeTraversal(EdgeType.COUSIN, "vnfc", "generic-vnf");
		
		List<Vertex> list = tQ.toList();

		assertEquals("Has 1 vertexes ", 1, list.size());
		assertTrue("Only returns the generic vnf vertex", list.contains(gvnf));
		
		g.tx().rollback();
	}
	
	@Test
	public void limitTraversalTest() throws AAIException {
				
		g.addV("aai-node-type","vnfc","vnfc-name","a-name").next();
		g.addV("aai-node-type","vnfc","vnfc-name","b-name").next();
		
		QueryBuilder<Vertex> tQ = new GremlinTraversal<>(loader, g, testEdgeRules);
		tQ.getVerticesByProperty("aai-node-type","vnfc").limit(1);
		
		List<Vertex> list = tQ.toList();

		assertEquals("Has 1 vertexes ", 1, list.size());
	
		g.tx().rollback();
	}
	
	@Test
	public void getVertexesByPropertiesTraversalTest() throws AAIException {
				
		g.addV("aai-node-type","vnfc","vnfc-name","a-name").next();
		g.addV("aai-node-type","vnfc","vnfc-name","b-name").next();
		
		QueryBuilder<Vertex> tQ = new GremlinTraversal<>(loader, g, testEdgeRules);
		tQ.getVerticesByProperty("vnfc-name", Arrays.asList("a-name", "b-name"));
		
		List<Vertex> list = tQ.toList();

		assertEquals("Has 2 vertexes ", 2, list.size());
	
		g.tx().rollback();
	}
	
	@Test
	public void getVertexesByIndexedPropertyTraversalTest() throws AAIException {
				
		g.addV("aai-node-type","vnfc","vnfc-name","a-name").next();
		g.addV("aai-node-type","vnfc","vnfc-name","b-name").next();
		
		QueryBuilder<Vertex> tQ = new GremlinTraversal<>(loader, g, testEdgeRules);
		tQ.getVerticesByIndexedProperty("aai-node-type","vnfc");
		
		List<Vertex> list = tQ.toList();

		assertEquals("Has 2 vertexes ", 2, list.size());
	
		g.tx().rollback();
	}
	
	@Test
	public void dedupTraversalTest() throws AAIException {
			
		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();
		
		testEdgeRules.addEdge(g, gvnf, pserver);
		testEdgeRules.addEdge(g, gvnf, pserver, "generic-vnf-pserver-B");
		
		QueryBuilder<Vertex> tQ = getNewVertexTraversal(gvnf);
		tQ.createEdgeTraversal(EdgeType.COUSIN, "generic-vnf", "pserver").dedup();
		
		List<Vertex> list = tQ.toList();
	
		assertEquals("Has 2 vertexes ", 1, list.size());
		assertTrue("result has pserver ", list.contains(pserver));
		
		g.tx().rollback();
	}
	
	@Test
	public void storeCapTraversalTest() throws AAIException {
			
		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();
		
		testEdgeRules.addEdge(g, gvnf, pserver);
		testEdgeRules.addEdge(g, gvnf, pserver, "generic-vnf-pserver-B");
		
		GremlinTraversal<BulkSet<Vertex>> tQ = new GremlinTraversal<>(loader, g, gvnf, testEdgeRules);
		tQ.createEdgeTraversal(EdgeType.COUSIN, "generic-vnf", "pserver").store("x").cap("x");
		
		List<BulkSet<Vertex>> list = tQ.toList();
	
		assertEquals("Has 2 vertexes ", 1, list.size());
		assertEquals("result has pserver ",pserver, list.get(0).iterator().next());
		
		g.tx().rollback();
	}
	
	@Test
	public void storeCapUnfoldTraversalTest() throws AAIException {
			
		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();
		
		testEdgeRules.addEdge(g, gvnf, pserver);
		testEdgeRules.addEdge(g, gvnf, pserver, "generic-vnf-pserver-B");
		
		QueryBuilder<Vertex> tQ = getNewVertexTraversal(gvnf);
		tQ.createEdgeTraversal(EdgeType.COUSIN, "generic-vnf", "pserver").store("x").cap("x").unfold();
		
		List<Vertex> list = tQ.toList();
	
		assertEquals("Has 2 vertexes ", 2, list.size());
		assertTrue("result has pserver ", list.contains(pserver));
		
		g.tx().rollback();
	}
	
	@Test
	public void nextAndHasNextTraversalTest() throws AAIException {
				
		Vertex v1 = g.addV("aai-node-type","vnfc","vnfc-name","a-name").next();
		Vertex v2 = g.addV("aai-node-type","vnfc","vnfc-name","b-name").next();
		
		QueryBuilder<Vertex> tQ = new GremlinTraversal<>(loader, g, testEdgeRules);
		tQ.getVerticesByProperty("aai-node-type","vnfc");
		
		List<Vertex> list = new ArrayList<>();
		
		assertTrue("Has next 1 ",tQ.hasNext());
		list.add(tQ.next());
		assertTrue("Has next 2 ",tQ.hasNext());
		list.add(tQ.next());
		assertFalse("Has next 3 ",tQ.hasNext());
		assertTrue("Has all the vertexes", list.contains(v1) && list.remove(v2));
		g.tx().rollback();
	}
	
	@Test
	public void edgeToVertexMultiRuleOutTraversalTest() throws AAIException {
			
		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();
		
		testEdgeRules.addEdge(g, gvnf, pserver);
		testEdgeRules.addEdge(g, gvnf, pserver, "generic-vnf-pserver-B");
		
		QueryBuilder<Vertex> tQ = getNewVertexTraversal(gvnf);
		tQ.createEdgeTraversal(EdgeType.COUSIN, "generic-vnf", "pserver");
		
		List<Vertex> list = tQ.toList();
	
		assertEquals("Has 2 vertexes ", 2, list.size());
		assertTrue("result has pserver ", list.contains(pserver));
		
		g.tx().rollback();
	}
	
	@Test
	public void edgeToVertexMultiRuleInTraversalTest() throws AAIException {
			
		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex complex = g.addV("aai-node-type","complex","physical-location-id","a-name").next();
		
		testEdgeRules.addEdge(g, gvnf, complex);
		testEdgeRules.addEdge(g, gvnf, complex, "complex-generic-vnf-B");
		
		QueryBuilder<Vertex> tQ = getNewVertexTraversal(gvnf);
		tQ.createEdgeTraversal(EdgeType.COUSIN, "generic-vnf", "complex");
		
		List<Vertex> list = tQ.toList();
	
		assertEquals("Has 2 vertexes ", 2, list.size());
		assertTrue("result has pserver ", list.contains(complex));
		
		g.tx().rollback();
	}

	@Test
	public void edgeTraversalSingleInRuleTest() throws AAIException {
				
		Vertex vce = g.addV("aai-node-type","vce","vnf-id","vce").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();
		
		Edge e = testEdgeRules.addEdge(g, vce, pserver);
		
		QueryBuilder<Edge> tQ1 = getNewEdgeTraversal(vce);
		tQ1.getEdgesBetween(EdgeType.COUSIN, "vce", "pserver");
		
		List<Edge> list = tQ1.toList();

		assertEquals("1 - Has 1 edge ", 1, list.size());
		assertTrue("1 - traversal results in edge ", list.contains(e));
		
		g.tx().rollback();
	}
	
	@Test
	public void edgeTraversalSingleOutRuleTest() throws AAIException {
				
		Vertex vce = g.addV("aai-node-type","vce","vnf-id","vce").next();
		Vertex vnfc1 = g.addV("aai-node-type","vnfc","vnfc-name","a-name").next();
		
		Edge e = testEdgeRules.addEdge(g, vce, vnfc1);
		
		QueryBuilder<Edge> tQ1 = getNewEdgeTraversal(vce);
		tQ1.getEdgesBetween(EdgeType.COUSIN, "vce", "vnfc");
		
		List<Edge> list1 = tQ1.toList();
		
		assertEquals("1 - Has 1 edge ", 1, list1.size());
		assertTrue("1 - traversal results in edge ", list1.contains(e));
		
		g.tx().rollback();
	}

	@Test
	public void edgeTraversalMultiRuleOutTraversalTest() throws AAIException {
			
		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();
		
		Edge e1 = testEdgeRules.addEdge(g, gvnf, pserver);
		Edge e2 = testEdgeRules.addEdge(g, gvnf, pserver, "generic-vnf-pserver-B");
		
		QueryBuilder<Edge> tQ = getNewEdgeTraversal(gvnf);
		tQ.getEdgesBetween(EdgeType.COUSIN, "generic-vnf", "pserver");
		
		List<Edge> list = tQ.toList();
	
		assertEquals("Has 2 edges ", 2, list.size());
		assertTrue("result has default edge ", list.contains(e1));
		assertTrue("result has other edge ", list.contains(e2));
		
		g.tx().rollback();
	}
	
	@Test
	public void edgeTraversalMultiRuleInTraversalTest() throws AAIException {
			
		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex complex = g.addV("aai-node-type","complex","physical-location-id","a-name").next();
		
		Edge e1 = testEdgeRules.addEdge(g, gvnf, complex);
		Edge e2 = testEdgeRules.addEdge(g, gvnf, complex, "complex-generic-vnf-B");
		
		QueryBuilder<Edge> tQ = getNewEdgeTraversal(gvnf);
		tQ.getEdgesBetween(EdgeType.COUSIN, "generic-vnf", "complex");
		
		List<Edge> list = tQ.toList();
	
		assertEquals("Has 2 edges ", 2, list.size());
		assertTrue("result has default edge ", list.contains(e1));
		assertTrue("result has other edge ", list.contains(e2));
		
		g.tx().rollback();
	}
	
	@Test
	public void edgeTraversalMultiRuleTraversalTest() throws AAIException {
				
		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex vnfc1 = g.addV("aai-node-type","vnfc","vnfc-name","a-name").next();
		Vertex vnfc2 = g.addV("aai-node-type","vnfc","vnfc-name","b-name").next();
		
		Edge e1 = testEdgeRules.addEdge(g, gvnf, vnfc1);
		Edge e2 = testEdgeRules.addEdge(g, gvnf, vnfc2, "re-uses");
		
		QueryBuilder<Edge> tQ = getNewEdgeTraversal(gvnf);
		tQ.getEdgesBetween(EdgeType.COUSIN, "generic-vnf", "vnfc");
		
		List<Edge> list = tQ.toList();

		assertEquals("Has 2 edges ", 2, list.size());
		assertTrue("result has default edge ", list.contains(e1));
		assertTrue("result has other edge ", list.contains(e2));
		
		g.tx().rollback();
	}

	@Test (expected = NoEdgeRuleFoundException.class)
	public void getEdgesBetweenWithLabelsEmptyListTest() throws AAIException {

		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();

		Edge e1 = testEdgeRules.addEdge(g, gvnf, pserver);
		Edge e2 = testEdgeRules.addEdge(g, gvnf, pserver, "generic-vnf-pserver-B");

		QueryBuilder<Edge> tQ = getNewEdgeTraversal(gvnf);
		tQ.getEdgesBetweenWithLabels(EdgeType.COUSIN, "generic-vnf", "pserver", Collections.emptyList());
		g.tx().rollback();
	}

	@Test
	public void getEdgesBetweenWithLabelsSingleItemTest() throws AAIException {

		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();

		Edge e1 = testEdgeRules.addEdge(g, gvnf, pserver);
		Edge e2 = testEdgeRules.addEdge(g, gvnf, pserver, "generic-vnf-pserver-B");

		QueryBuilder<Edge> tQ = getNewEdgeTraversal(gvnf);
		tQ.getEdgesBetweenWithLabels(EdgeType.COUSIN, "generic-vnf", "pserver", Collections.singletonList("generic-vnf-pserver-B"));

		List<Edge> list = tQ.toList();

		assertEquals("Has 1 edges ", 1, list.size());
		assertFalse("result does not have default edge ", list.contains(e1));
		assertTrue("result has other edge ", list.contains(e2));
		g.tx().rollback();
	}

	@Test
	public void getEdgesBetweenWithLabelsMultipleItemTest() throws AAIException {

		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();

		Edge e1 = testEdgeRules.addEdge(g, gvnf, pserver);
		Edge e2 = testEdgeRules.addEdge(g, gvnf, pserver, "generic-vnf-pserver-B");

		QueryBuilder<Edge> tQ = getNewEdgeTraversal(gvnf);
		tQ.getEdgesBetweenWithLabels(EdgeType.COUSIN, "generic-vnf", "pserver", Arrays.asList("generic-vnf-pserver-B", "generic-vnf-pserver-A"));

		List<Edge> list = tQ.toList();

		assertEquals("Has 2 edges ", 2, list.size());
		assertTrue("result has generic-vnf-pserver-A edge ", list.contains(e1));
		assertTrue("result has generic-vnf-pserver-B edge ", list.contains(e2));
		g.tx().rollback();
	}

	@Test (expected = NoEdgeRuleFoundException.class)
	public void createEdgeTraversalWithLabelsEmptyListTest() throws AAIException {

		Vertex gvnf = getVertex();

		QueryBuilder<Edge> tQ = getNewEdgeTraversal(gvnf);
		tQ.getEdgesBetweenWithLabels(EdgeType.COUSIN, "generic-vnf", "pserver", Collections.emptyList());

		List<Edge> list = tQ.toList();

		g.tx().rollback();
	}

	private Vertex getVertex() throws AAIException {
		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();

		Edge e1 = testEdgeRules.addEdge(g, gvnf, pserver);
		Edge e2 = testEdgeRules.addEdge(g, gvnf, pserver, "generic-vnf-pserver-B");
		return gvnf;
	}

	@Test
	public void createEdgeTraversalWithLabelsSingleItemTest() throws AAIException {

		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();

		Edge e1 = testEdgeRules.addEdge(g, gvnf, pserver);
		Edge e2 = testEdgeRules.addEdge(g, gvnf, pserver, "generic-vnf-pserver-B");

		QueryBuilder<Edge> tQ = getNewEdgeTraversal(gvnf);
		tQ.getEdgesBetweenWithLabels(EdgeType.COUSIN, "generic-vnf", "pserver", Collections.singletonList("generic-vnf-pserver-B"));

		List<Edge> list = tQ.toList();

		assertEquals("Has 1 edges ", 1, list.size());
		assertFalse("result does not have default edge ", list.contains(e1));
		assertTrue("result has other edge ", list.contains(e2));
		g.tx().rollback();
	}

	@Test
	public void createEdgeTraversalWithLabelsMultipleItemTest() throws AAIException {

		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();

		Edge e1 = testEdgeRules.addEdge(g, gvnf, pserver);
		Edge e2 = testEdgeRules.addEdge(g, gvnf, pserver, "generic-vnf-pserver-B");

		QueryBuilder<Edge> tQ = getNewEdgeTraversal(gvnf);
		tQ.getEdgesBetweenWithLabels(EdgeType.COUSIN, "generic-vnf", "pserver", Arrays.asList("generic-vnf-pserver-B", "generic-vnf-pserver-A"));

		List<Edge> list = tQ.toList();

		assertEquals("Has 2 edges ", 2, list.size());
		assertTrue("result has generic-vnf-pserver-A edge ", list.contains(e1));
		assertTrue("result has generic-vnf-pserver-B edge ", list.contains(e2));
		g.tx().rollback();
	}

	protected abstract QueryBuilder<Edge> getNewEdgeTraversal(Vertex v);
	
	protected abstract QueryBuilder<Edge> getNewEdgeTraversal();
	
	protected abstract QueryBuilder<Vertex> getNewVertexTraversal(Vertex v);
	
	protected abstract QueryBuilder<Vertex> getNewVertexTraversal();

		
}
