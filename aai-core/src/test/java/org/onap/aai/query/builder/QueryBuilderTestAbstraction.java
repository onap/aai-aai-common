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

import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.BulkSet;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.Tree;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraphFactory;
import org.junit.*;
import org.junit.runner.RunWith;
import org.onap.aai.config.IntrospectionConfig;
import org.onap.aai.config.SpringContextAware;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.serialization.db.EdgeSerializer;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.edges.enums.EdgeType;
import org.onap.aai.serialization.db.exceptions.NoEdgeRuleFoundException;
import org.onap.aai.serialization.queryformats.QueryFormatTestHelper;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.setup.SchemaVersions;
import org.onap.aai.util.AAIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.onap.aai.db.props.AAIProperties;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
		SchemaLocationsBean.class,
		SchemaVersions.class,
		QueryTestsConfigTranslator.class,
		NodeIngestor.class,
		EdgeIngestor.class,
		EdgeSerializer.class,
		SpringContextAware.class,
		IntrospectionConfig.class
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource(properties = {
    "schema.translator.list = config",
    "schema.nodes.location=src/test/resources/onap/oxm",
    "schema.edges.location=src/test/resources/onap/dbedgerules"
})
public abstract class QueryBuilderTestAbstraction {

	protected   Loader loader;
	protected static Graph graph;
	protected GraphTraversalSource g;

	@Autowired
	protected EdgeSerializer testEdgeSer;

	@Autowired
	protected LoaderFactory loaderFactory;

	@Autowired
	protected SchemaVersions schemaVersions;

	@BeforeClass
	public static void setup() throws Exception {
		System.setProperty("AJSC_HOME", ".");
		System.setProperty("BUNDLECONFIG_DIR", "src/test/resources/bundleconfig-local");
		QueryFormatTestHelper.setFinalStatic(AAIConstants.class.getField("AAI_HOME_ETC_OXM"), "src/test/resources/bundleconfig-local/etc/oxm/");
		graph = JanusGraphFactory.build().set("storage.backend", "inmemory").open();
	}

	@Before
	public void configure() throws Exception {
	    loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDefaultVersion());


		g = graph.traversal();
	}

	@After
	public void deConfigure() throws Exception {
		g.tx().rollback();
	}

	@AfterClass
	public static void teardown() throws Exception {
		graph.close();
	}

	@Test
	public void createEdgeGVnfToVnfcTraversal() throws AAIException {

		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","myvnf").next();
		Vertex vnfc = g.addV("aai-node-type","vnfc","vnfc-name","a-name").next();
		testEdgeSer.addEdge(g, gvnf, vnfc, "uses");

		QueryBuilder<Vertex> tQ = getNewVertexTraversalWithTestEdgeRules(gvnf);
		tQ.createEdgeTraversal(EdgeType.COUSIN, "generic-vnf", "vnfc");

		assertEquals(vnfc, tQ.next());


	}

	@Test
	public void createEdgeLinterfaceToLogicalLinkTraversal() throws AAIException {

		Vertex lInterface = g.addV("aai-node-type","l-interface","interface-name","l-interface-a").next();
		Vertex logicalLink = g.addV("aai-node-type","logical-link","link-name","logical-link-a").next();
		testEdgeSer.addEdge(g, lInterface, logicalLink, "sourceLInterface");

		QueryBuilder<Vertex> tQ = getNewVertexTraversalWithTestEdgeRules(lInterface);
		tQ.createEdgeTraversal(EdgeType.COUSIN, "l-interface", "logical-link");

		Vertex next = tQ.next();

		assertEquals(logicalLink, next);


	}

	@SuppressWarnings("rawtypes")
	@Test
	public void createEdgeLinterfaceToLogicalLinkTraversal_tree() throws AAIException {
		Vertex lInterface = g.addV("aai-node-type","l-interface","interface-name","l-interface-a").next();
		Vertex logicalLink = g.addV("aai-node-type","logical-link","link-name","logical-link-a").next();
		testEdgeSer.addEdge(g, lInterface, logicalLink, "sourceLInterface");

		QueryBuilder<Tree> tQ = getNewTreeTraversalWithTestEdgeRules(lInterface).createEdgeTraversal(EdgeType.COUSIN,
				loader.introspectorFromName("l-interface" ), loader.introspectorFromName("logical-link")).tree();

		Vertex lInterfaceExpected = graph.traversal().V().has("aai-node-type","l-interface").has("interface-name","l-interface-a").next();
		Vertex logicalLinkExpected = graph.traversal().V().has("aai-node-type", "logical-link").has("link-name","logical-link-a").next();
		Tree tree = tQ.next();
		assertTrue(tree.containsKey(lInterfaceExpected));
		assertTrue(((Tree) tree.get(lInterfaceExpected)).containsKey(logicalLinkExpected));
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void createEdgeLinterfaceToLogicalLinkTraversal_Path() throws AAIException {
		Vertex pInterface = g.addV("aai-node-type","p-interface","interface-name","p-interface-a").next();
		Vertex lInterface = g.addV("aai-node-type","l-interface","interface-name","l-interface-a").next();
		Vertex logicalLink = g.addV("aai-node-type","logical-link","link-name","logical-link-a").next();
		testEdgeSer.addEdge(g, lInterface, logicalLink);
		testEdgeSer.addTreeEdge(g, pInterface, lInterface);

		QueryBuilder<Path> tQ = getNewPathTraversalWithTestEdgeRules(pInterface).createEdgeTraversal(EdgeType.TREE,
				loader.introspectorFromName("p-interface" ), loader.introspectorFromName("l-interface")).createEdgeTraversal(EdgeType.COUSIN,
				loader.introspectorFromName("l-interface" ), loader.introspectorFromName("logical-link")).path();

		Path path = tQ.next();
		assertThat(path.objects(), contains(pInterface, lInterface, logicalLink));
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void parentVertexTest() throws AAIException {
		Vertex pInterface = g.addV("aai-node-type","p-interface","interface-name","p-interface-a").next();
		Vertex lInterface = g.addV("aai-node-type","l-interface","interface-name","l-interface-a").next();

		testEdgeSer.addTreeEdge(g, pInterface, lInterface);

		QueryBuilder<Vertex> tQ = getNewEdgeTraversalWithTestEdgeRules(lInterface).getParentVertex();

		Vertex parent = tQ.next();
		assertThat(parent, is(pInterface));
	}


	@Test
	public void createEdgeLinterfaceToLogicalLinkIntrospectorTraversal() throws AAIException {

		Vertex lInterface = g.addV("aai-node-type","l-interface","interface-name","l-interface-a").next();
		Vertex logicalLink = g.addV("aai-node-type","logical-link","link-name","logical-link-a").next();
		testEdgeSer.addEdge(g, lInterface, logicalLink, "sourceLInterface");

		QueryBuilder<Vertex> tQ = getNewVertexTraversalWithTestEdgeRules(lInterface);
		tQ.createEdgeTraversal(EdgeType.COUSIN, loader.introspectorFromName("l-interface"), loader.introspectorFromName("logical-link"));

		Vertex next = tQ.next();

		assertEquals(logicalLink, next);


	}

	@Test
	public void createEdgeLinterfaceToLogicalLinkVertexToIntrospectorTraversal() throws AAIException {

		Vertex lInterface = g.addV("aai-node-type","l-interface","interface-name","l-interface-a").next();
		Vertex logicalLink = g.addV("aai-node-type","logical-link","link-name","logical-link-a").next();
		testEdgeSer.addEdge(g, lInterface, logicalLink, "sourceLInterface");

		QueryBuilder<Vertex> tQ = getNewVertexTraversalWithTestEdgeRules(lInterface);
		tQ.createEdgeTraversal(EdgeType.COUSIN, lInterface, loader.introspectorFromName("logical-link"));

		Vertex next = tQ.next();

		assertEquals(logicalLink, next);


	}

	@Test
	public void edgeToVertexTraversalTest() throws AAIException {

		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex vnfc1 = g.addV("aai-node-type","vnfc","vnfc-name","a-name").next();

		testEdgeSer.addEdge(g, gvnf, vnfc1);

		QueryBuilder<Vertex> tQ = getNewVertexTraversalWithTestEdgeRules(gvnf);
		tQ.createEdgeTraversal(EdgeType.COUSIN, "generic-vnf", "vnfc");

		List<Vertex> list = tQ.toList();

		assertEquals("Has 1 vertexes ", 1, list.size());
		assertTrue("Has vertex on the default edge ", list.contains(vnfc1));


	}

	@Test
	public void edgeToVertexTraversalSingleOutRuleTest() throws AAIException {

		Vertex vce = g.addV("aai-node-type","vce","vnf-id","vce").next();
		Vertex vnfc1 = g.addV("aai-node-type","vnfc","vnfc-name","a-name").next();

		testEdgeSer.addEdge(g, vce, vnfc1);

		QueryBuilder<Vertex> tQ1 = getNewVertexTraversalWithTestEdgeRules(vce);
		tQ1.createEdgeTraversal(EdgeType.COUSIN, "vce", "vnfc");

		QueryBuilder<Vertex> tQ2 = getNewVertexTraversalWithTestEdgeRules(vnfc1);
		tQ2.createEdgeTraversal(EdgeType.COUSIN, "vnfc", "vce");

		List<Vertex> list1 = tQ1.toList();
		List<Vertex> list2 = tQ2.toList();

		assertEquals("1 - Has 1 vertexes ", 1, list1.size());
		assertTrue("1 - traversal results in vnfc ", list1.contains(vnfc1));
		assertEquals("2 - Has 1 vertexes ", 1, list2.size());
		assertTrue("2 - traversal results in vce ", list2.contains(vce));


	}

	@Test
	public void edgeToVertexTraversalSingleInRuleTest() throws AAIException {

		Vertex vce = g.addV("aai-node-type","vce","vnf-id","vce").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();

		testEdgeSer.addEdge(g, vce, pserver);

		QueryBuilder<Vertex> tQ1 = getNewVertexTraversalWithTestEdgeRules(vce);
		tQ1.createEdgeTraversal(EdgeType.COUSIN, "vce", "pserver");

		List<Vertex> list = tQ1.toList();

		assertEquals("1 - Has 1 vertexes ", 1, list.size());
		assertTrue("1 - traversal results in vnfc ", list.contains(pserver));


	}

	@Test
	public void edgeToVertexMultiRuleTraversalTest() throws AAIException {

		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex vnfc1 = g.addV("aai-node-type","vnfc","vnfc-name","a-name").next();
		Vertex vnfc2 = g.addV("aai-node-type","vnfc","vnfc-name","b-name").next();

		testEdgeSer.addEdge(g, gvnf, vnfc1);
		testEdgeSer.addEdge(g, gvnf, vnfc2, "re-uses");

		QueryBuilder<Vertex> tQ = getNewVertexTraversalWithTestEdgeRules(gvnf);
		tQ.createEdgeTraversal(EdgeType.COUSIN, "generic-vnf", "vnfc");

		List<Vertex> list = tQ.toList();

		assertEquals("Has 2 vertexes ", 2, list.size());
		assertTrue("Has vertex on the default edge ", list.contains(vnfc1));
		assertTrue("Has vertex on the re-uses edge ", list.contains(vnfc2));


	}

	@Test
	public void edgeToVertexMultiLabelTest() throws AAIException {

		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();
		Vertex vnfc1 = g.addV("aai-node-type","vnfc","vnfc-name","a-name").next();

		testEdgeSer.addEdge(g, gvnf, vnfc1);
		testEdgeSer.addEdge(g, pserver, vnfc1);

		QueryBuilder<Vertex> tQ = getNewVertexTraversalWithTestEdgeRules(vnfc1);
		tQ.createEdgeTraversal(EdgeType.COUSIN, "vnfc", "generic-vnf");

		List<Vertex> list = tQ.toList();

		assertEquals("Has 1 vertexes ", 1, list.size());
		assertTrue("Only returns the generic vnf vertex", list.contains(gvnf));


	}

	@Test
	public void limitTraversalTest() throws AAIException {

		g.addV("aai-node-type","vnfc","vnfc-name","a-name").next();
		g.addV("aai-node-type","vnfc","vnfc-name","b-name").next();

		QueryBuilder<Vertex> tQ = new GremlinTraversal<>(loader, g);
		tQ.getVerticesByProperty("aai-node-type","vnfc").limit(1);

		List<Vertex> list = tQ.toList();

		assertEquals("Has 1 vertexes ", 1, list.size());


	}

	@Test
	public void getVertexesByPropertiesTraversalTest() throws AAIException {

		g.addV("aai-node-type","vnfc","vnfc-name","a-name").next();
		g.addV("aai-node-type","vnfc","vnfc-name","b-name").next();

		QueryBuilder<Vertex> tQ = new GremlinTraversal<>(loader, g);
		tQ.getVerticesByProperty("vnfc-name", Arrays.asList("a-name", "b-name"));

		List<Vertex> list = tQ.toList();

		assertEquals("Has 2 vertexes ", 2, list.size());


	}

	@Test
	public void getVertexesByIndexedPropertyTraversalTest() throws AAIException {

		g.addV("aai-node-type","vnfc","vnfc-name","a-name").next();
		g.addV("aai-node-type","vnfc","vnfc-name","b-name").next();

		QueryBuilder<Vertex> tQ = new GremlinTraversal<>(loader, g);
		tQ.getVerticesByIndexedProperty("aai-node-type","vnfc");

		List<Vertex> list = tQ.toList();

		assertEquals("Has 2 vertexes ", 2, list.size());


	}

	@Test
	public void dedupTraversalTest() throws AAIException {

		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();

		testEdgeSer.addEdge(g, gvnf, pserver);
		testEdgeSer.addEdge(g, gvnf, pserver, "generic-vnf-pserver-B");

		QueryBuilder<Vertex> tQ = getNewVertexTraversalWithTestEdgeRules(gvnf);
		tQ.createEdgeTraversal(EdgeType.COUSIN, "generic-vnf", "pserver").dedup();

		List<Vertex> list = tQ.toList();

		assertEquals("Has 2 vertexes ", 1, list.size());
		assertTrue("result has pserver ", list.contains(pserver));


	}

	@Test
	public void storeCapTraversalTest() throws AAIException {

		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();

		testEdgeSer.addEdge(g, gvnf, pserver);
		testEdgeSer.addEdge(g, gvnf, pserver, "generic-vnf-pserver-B");

		GremlinTraversal<BulkSet<Vertex>> tQ = new GremlinTraversal<>(loader, g, gvnf);
		tQ.createEdgeTraversal(EdgeType.COUSIN, "generic-vnf", "pserver").store("x").cap("x");

		List<BulkSet<Vertex>> list = tQ.toList();

		assertEquals("Has 2 vertexes ", 1, list.size());
		assertEquals("result has pserver ",pserver, list.get(0).iterator().next());


	}

	@Test
	public void storeCapUnfoldTraversalTest() throws AAIException {

		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();

		testEdgeSer.addEdge(g, gvnf, pserver);
		testEdgeSer.addEdge(g, gvnf, pserver, "generic-vnf-pserver-B");

		QueryBuilder<Vertex> tQ = getNewVertexTraversalWithTestEdgeRules(gvnf);
		tQ.createEdgeTraversal(EdgeType.COUSIN, "generic-vnf", "pserver").store("x").cap("x").unfold();

		List<Vertex> list = tQ.toList();

		assertEquals("Has 2 vertexes ", 2, list.size());
		assertTrue("result has pserver ", list.contains(pserver));


	}

	@Test
	public void nextAndHasNextTraversalTest() throws AAIException {

		Vertex v1 = g.addV("aai-node-type","vnfc","vnfc-name","a-name").next();
		Vertex v2 = g.addV("aai-node-type","vnfc","vnfc-name","b-name").next();

		QueryBuilder<Vertex> tQ = new GremlinTraversal<>(loader, g);
		tQ.getVerticesByProperty("aai-node-type","vnfc");

		List<Vertex> list = new ArrayList<>();

		assertTrue("Has next 1 ",tQ.hasNext());
		list.add(tQ.next());
		assertTrue("Has next 2 ",tQ.hasNext());
		list.add(tQ.next());
		assertFalse("Has next 3 ",tQ.hasNext());
		assertTrue("Has all the vertexes", list.contains(v1) && list.remove(v2));

	}

	@Test
	public void edgeToVertexMultiRuleOutTraversalTest() throws AAIException {

		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();

		testEdgeSer.addEdge(g, gvnf, pserver);
		testEdgeSer.addEdge(g, gvnf, pserver, "generic-vnf-pserver-B");

		QueryBuilder<Vertex> tQ = getNewVertexTraversalWithTestEdgeRules(gvnf);
		tQ.createEdgeTraversal(EdgeType.COUSIN, "generic-vnf", "pserver");

		List<Vertex> list = tQ.toList();

		assertEquals("Has 2 vertexes ", 2, list.size());
		assertTrue("result has pserver ", list.contains(pserver));


	}

	@Test
	public void edgeToVertexMultiRuleInTraversalTest() throws AAIException {

		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex complex = g.addV("aai-node-type","complex","physical-location-id","a-name").next();

		testEdgeSer.addEdge(g, gvnf, complex);
		testEdgeSer.addEdge(g, gvnf, complex, "complex-generic-vnf-B");

		QueryBuilder<Vertex> tQ = getNewVertexTraversalWithTestEdgeRules(gvnf);
		tQ.createEdgeTraversal(EdgeType.COUSIN, "generic-vnf", "complex");

		List<Vertex> list = tQ.toList();

		assertEquals("Has 2 vertexes ", 2, list.size());
		assertTrue("result has pserver ", list.contains(complex));


	}

	@Test
	public void edgeTraversalSingleInRuleTest() throws AAIException {

		Vertex vce = g.addV("aai-node-type","vce","vnf-id","vce").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();

		Edge e = testEdgeSer.addEdge(g, vce, pserver);

		QueryBuilder<Edge> tQ1 = getNewEdgeTraversalWithTestEdgeRules(vce);
		tQ1.getEdgesBetween(EdgeType.COUSIN, "vce", "pserver");

		List<Edge> list = tQ1.toList();

		assertEquals("1 - Has 1 edge ", 1, list.size());
		assertTrue("1 - traversal results in edge ", list.contains(e));


	}

	@Test
	public void edgeTraversalSingleOutRuleTest() throws AAIException {

		Vertex vce = g.addV("aai-node-type","vce","vnf-id","vce").next();
		Vertex vnfc1 = g.addV("aai-node-type","vnfc","vnfc-name","a-name").next();

		Edge e = testEdgeSer.addEdge(g, vce, vnfc1);

		QueryBuilder<Edge> tQ1 = getNewEdgeTraversalWithTestEdgeRules(vce);
		tQ1.getEdgesBetween(EdgeType.COUSIN, "vce", "vnfc");

		List<Edge> list1 = tQ1.toList();

		assertEquals("1 - Has 1 edge ", 1, list1.size());
		assertTrue("1 - traversal results in edge ", list1.contains(e));


	}

	@Test
	public void edgeTraversalMultiRuleOutTraversalTest() throws AAIException {

		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();

		Edge e1 = testEdgeSer.addEdge(g, gvnf, pserver);
		Edge e2 = testEdgeSer.addEdge(g, gvnf, pserver, "generic-vnf-pserver-B");

		QueryBuilder<Edge> tQ = getNewEdgeTraversalWithTestEdgeRules(gvnf);
		tQ.getEdgesBetween(EdgeType.COUSIN, "generic-vnf", "pserver");

		List<Edge> list = tQ.toList();

		assertEquals("Has 2 edges ", 2, list.size());
		assertTrue("result has default edge ", list.contains(e1));
		assertTrue("result has other edge ", list.contains(e2));


	}

	@Test
	public void edgeTraversalMultiRuleInTraversalTest() throws AAIException {

		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex complex = g.addV("aai-node-type","complex","physical-location-id","a-name").next();

		Edge e1 = testEdgeSer.addEdge(g, gvnf, complex);
		Edge e2 = testEdgeSer.addEdge(g, gvnf, complex, "complex-generic-vnf-B");

		QueryBuilder<Edge> tQ = getNewEdgeTraversalWithTestEdgeRules(gvnf);
		tQ.getEdgesBetween(EdgeType.COUSIN, "generic-vnf", "complex");

		List<Edge> list = tQ.toList();

		assertEquals("Has 2 edges ", 2, list.size());
		assertTrue("result has default edge ", list.contains(e1));
		assertTrue("result has other edge ", list.contains(e2));


	}

	@Test
	public void edgeTraversalMultiRuleTraversalTest() throws AAIException {

		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex vnfc1 = g.addV("aai-node-type","vnfc","vnfc-name","a-name").next();
		Vertex vnfc2 = g.addV("aai-node-type","vnfc","vnfc-name","b-name").next();

		Edge e1 = testEdgeSer.addEdge(g, gvnf, vnfc1);
		Edge e2 = testEdgeSer.addEdge(g, gvnf, vnfc2, "re-uses");

		QueryBuilder<Edge> tQ = getNewEdgeTraversalWithTestEdgeRules(gvnf);
		tQ.getEdgesBetween(EdgeType.COUSIN, "generic-vnf", "vnfc");

		List<Edge> list = tQ.toList();

		assertEquals("Has 2 edges ", 2, list.size());
		assertTrue("result has default edge ", list.contains(e1));
		assertTrue("result has other edge ", list.contains(e2));


	}

	@Ignore("This test is failing for TraversalQueryTest and Optimized but it passes for GremlinQueryTest")
	@Test (expected = NoEdgeRuleFoundException.class)
	public void getEdgesBetweenWithLabelsEmptyListTest() throws AAIException {

		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();

		testEdgeSer.addEdge(g, gvnf, pserver);
		testEdgeSer.addEdge(g, gvnf, pserver, "generic-vnf-pserver-B");

		QueryBuilder<Edge> tQ = getNewEdgeTraversalWithTestEdgeRules(gvnf);
		tQ.getEdgesBetweenWithLabels(EdgeType.COUSIN, "generic-vnf", "pserver", Collections.emptyList());

	}

	@Test
	public void getEdgesBetweenWithLabelsSingleItemTest() throws AAIException {

		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();

		Edge e1 = testEdgeSer.addEdge(g, gvnf, pserver);
		Edge e2 = testEdgeSer.addEdge(g, gvnf, pserver, "generic-vnf-pserver-B");

		QueryBuilder<Edge> tQ = getNewEdgeTraversalWithTestEdgeRules(gvnf);
		tQ.getEdgesBetweenWithLabels(EdgeType.COUSIN, "generic-vnf", "pserver", Collections.singletonList("generic-vnf-pserver-B"));

		List<Edge> list = tQ.toList();

		assertEquals("Has 1 edges ", 1, list.size());
		assertFalse("result does not have default edge ", list.contains(e1));
		assertTrue("result has other edge ", list.contains(e2));

	}

	@Test
	public void getEdgesBetweenWithLabelsMultipleItemTest() throws AAIException {

		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();

		Edge e1 = testEdgeSer.addEdge(g, gvnf, pserver);
		Edge e2 = testEdgeSer.addEdge(g, gvnf, pserver, "generic-vnf-pserver-B");

		QueryBuilder<Edge> tQ = getNewEdgeTraversalWithTestEdgeRules(gvnf);
		tQ.getEdgesBetweenWithLabels(EdgeType.COUSIN, "generic-vnf", "pserver", Arrays.asList("generic-vnf-pserver-B", "generic-vnf-pserver-A"));

		List<Edge> list = tQ.toList();

		assertEquals("Has 2 edges ", 2, list.size());
		assertTrue("result has generic-vnf-pserver-A edge ", list.contains(e1));
		assertTrue("result has generic-vnf-pserver-B edge ", list.contains(e2));

	}

	@Ignore("This test is failing for TraversalQueryTest and Optimized but it passes for GremlinQueryTest")
	@Test (expected = NoEdgeRuleFoundException.class)
	public void createEdgeTraversalWithLabelsEmptyListTest() throws AAIException {

		Vertex gvnf = getVertex();

		QueryBuilder<Edge> tQ = getNewEdgeTraversalWithTestEdgeRules(gvnf);
		tQ.getEdgesBetweenWithLabels(EdgeType.COUSIN, "generic-vnf", "pserver", Collections.emptyList());

		tQ.toList();


	}

	private Vertex getVertex() throws AAIException {
		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();

		testEdgeSer.addEdge(g, gvnf, pserver);
		testEdgeSer.addEdge(g, gvnf, pserver, "generic-vnf-pserver-B");
		return gvnf;
	}

	@Test
	public void createEdgeTraversalWithLabelsSingleItemTest() throws AAIException {

		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();

		Edge e1 = testEdgeSer.addEdge(g, gvnf, pserver);
		Edge e2 = testEdgeSer.addEdge(g, gvnf, pserver, "generic-vnf-pserver-B");

		QueryBuilder<Edge> tQ = getNewEdgeTraversalWithTestEdgeRules(gvnf);
		tQ.getEdgesBetweenWithLabels(EdgeType.COUSIN, "generic-vnf", "pserver", Collections.singletonList("generic-vnf-pserver-B"));

		List<Edge> list = tQ.toList();

		assertEquals("Has 1 edges ", 1, list.size());
		assertFalse("result does not have default edge ", list.contains(e1));
		assertTrue("result has other edge ", list.contains(e2));

	}

	@Test
	public void createEdgeTraversalWithLabelsMultipleItemTest() throws AAIException {

		Vertex gvnf = g.addV("aai-node-type","generic-vnf","vnf-id","gvnf").next();
		Vertex pserver = g.addV("aai-node-type","pserver","hostname","a-name").next();

		Edge e1 = testEdgeSer.addEdge(g, gvnf, pserver);
		Edge e2 = testEdgeSer.addEdge(g, gvnf, pserver, "generic-vnf-pserver-B");

		QueryBuilder<Edge> tQ = getNewEdgeTraversalWithTestEdgeRules(gvnf);
		tQ.getEdgesBetweenWithLabels(EdgeType.COUSIN, "generic-vnf", "pserver", Arrays.asList("generic-vnf-pserver-B", "generic-vnf-pserver-A"));

		List<Edge> list = tQ.toList();

		assertEquals("Has 2 edges ", 2, list.size());
		assertTrue("result has generic-vnf-pserver-A edge ", list.contains(e1));
		assertTrue("result has generic-vnf-pserver-B edge ", list.contains(e2));

	}

	protected abstract QueryBuilder<Edge> getNewEdgeTraversalWithTestEdgeRules(Vertex v);

	protected abstract QueryBuilder<Edge> getNewEdgeTraversalWithTestEdgeRules();

	protected abstract QueryBuilder<Vertex> getNewVertexTraversalWithTestEdgeRules(Vertex v);

	protected abstract QueryBuilder<Vertex> getNewVertexTraversalWithTestEdgeRules();

	protected abstract QueryBuilder<Tree> getNewTreeTraversalWithTestEdgeRules(Vertex v);

	protected abstract QueryBuilder<Tree> getNewTreeTraversalWithTestEdgeRules();

	protected abstract QueryBuilder<Path> getNewPathTraversalWithTestEdgeRules(Vertex v);

	protected abstract QueryBuilder<Path> getNewPathTraversalWithTestEdgeRules();

}
