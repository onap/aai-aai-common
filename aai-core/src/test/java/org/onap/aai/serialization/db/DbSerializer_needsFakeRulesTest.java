/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-18 AT&T Intellectual Property. All rights reserved.
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
package org.onap.aai.serialization.db;

import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;
import org.onap.aai.util.AAIConstants;
import org.janusgraph.core.JanusGraphFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.*;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.onap.aai.config.IntrospectionConfig;
import org.onap.aai.config.SpringContextAware;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.dbmap.DBConnectionType;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.*;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.JanusGraphDBEngine;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.serialization.queryformats.QueryFormatTestHelper;
import org.onap.aai.setup.SchemaLocationsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

//@RunWith(value = Parameterized.class) TODO replace this functionality
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
		SchemaLocationsBean.class,
        SchemaVersions.class,
		AAICoreFakeEdgesConfigTranslator.class,
		NodeIngestor.class,
		EdgeIngestor.class,
		EdgeSerializer.class,
		SpringContextAware.class,
		IntrospectionConfig.class
})
@TestPropertySource(properties = {
    "schema.translator.list = config"
})
public class DbSerializer_needsFakeRulesTest {

	//to use, set thrown.expect to whatever your test needs
	//this line establishes default of expecting no exception to be thrown
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	protected static Graph graph;
	
	@Autowired
	protected EdgeSerializer edgeSer;
	@Autowired
	protected EdgeIngestor ei;
	@Autowired
	protected SchemaVersions schemaVersions;

	private SchemaVersion version;
	private final ModelType introspectorFactoryType = ModelType.MOXY;
	private final DBConnectionType type = DBConnectionType.REALTIME;
	private Loader loader;
	private TransactionalGraphEngine dbEngine;
	private TransactionalGraphEngine engine; //for tests that aren't mocking the engine
	private DBSerializer dbser;
	TransactionalGraphEngine spy;
	TransactionalGraphEngine.Admin adminSpy;

	//@Parameterized.Parameter(value = 0)
	public QueryStyle queryStyle = QueryStyle.TRAVERSAL;

	/*@Parameterized.Parameters(name = "QueryStyle.{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][]{
				{QueryStyle.TRAVERSAL},
				{QueryStyle.TRAVERSAL_URI}
		});
	}*/

	@BeforeClass
	public static void init() throws Exception {
		graph = JanusGraphFactory.build().set("storage.backend", "inmemory").open();
		System.setProperty("AJSC_HOME", ".");
        System.setProperty("BUNDLECONFIG_DIR", "src/test/resources/bundleconfig-local");
        QueryFormatTestHelper.setFinalStatic(AAIConstants.class.getField("AAI_HOME_ETC_OXM"), "src/test/resources/bundleconfig-local/etc/oxm/");
  
	}

	@Before
	public void setup() throws Exception {
		//createGraph();
        version = schemaVersions.getDefaultVersion();
		loader = SpringContextAware.getBean(LoaderFactory.class).createLoaderForVersion(introspectorFactoryType, version);
		dbEngine = new JanusGraphDBEngine(queryStyle, type, loader);
		spy = spy(dbEngine);
		adminSpy = spy(dbEngine.asAdmin());


		engine = new JanusGraphDBEngine(queryStyle, type, loader);
		dbser = new DBSerializer(version, engine, introspectorFactoryType, "AAI-TEST");
	}

	@After
	public void tearDown() throws Exception {
	    engine.rollback();
	}

	@AfterClass
	public static void destroy() throws Exception {
		graph.close();
	}

	public void subnetSetup() throws AAIException {
		/*
		 * This setus up the test graph, For future junits , add more vertices
		 * and edges
		 */

		Vertex l3interipv4addresslist_1 = graph.traversal().addV("aai-node-type", "l3-interface-ipv4-address-list",
				"l3-interface-ipv4-address", "l3-interface-ipv4-address-1").next();
		Vertex subnet_2 = graph.traversal().addV("aai-node-type", "subnet", "subnet-id", "subnet-id-2").next();
		Vertex l3interipv6addresslist_3 = graph.traversal().addV("aai-node-type", "l3-interface-ipv6-address-list",
				"l3-interface-ipv6-address", "l3-interface-ipv6-address-3").next();
		Vertex subnet_4 = graph.traversal().addV("aai-node-type", "subnet", "subnet-id", "subnet-id-4").next();
		Vertex subnet_5 = graph.traversal().addV("aai-node-type", "subnet", "subnet-id", "subnet-id-5").next();
		Vertex l3network_6 = graph.traversal()
				.addV("aai-node-type", "l3-network", "network-id", "network-id-6", "network-name", "network-name-6")
				.next();

		GraphTraversalSource g = graph.traversal();
		edgeSer.addEdge(g, l3interipv4addresslist_1, subnet_2);
		edgeSer.addEdge(g, l3interipv6addresslist_3, subnet_4);
		edgeSer.addTreeEdge(g, subnet_5, l3network_6);
	}

	public String testDelete(Vertex v) throws AAIException {

		GraphTraversalSource traversal = graph.traversal();
		when(spy.asAdmin()).thenReturn(adminSpy);
		when(adminSpy.getTraversalSource()).thenReturn(traversal);
		when(adminSpy.getReadOnlyTraversalSource()).thenReturn(traversal);

		String exceptionMessage = "";
		DBSerializer serializer = new DBSerializer(version, spy, introspectorFactoryType, "AAI_TEST");
		try {
			serializer.delete(v, "resourceVersion", false);
		} catch (AAIException exception) {
			exceptionMessage = exception.getMessage();
		}
		return exceptionMessage;

	}

	@Test
	public void serializeToDbNewVertexAndEdgeAAIUUIDTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, URISyntaxException {
		String testName = new Object() {}.getClass().getEnclosingMethod().getName();
		DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();

		engine.startTransaction();

		engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","vnfc-" + testName, AAIProperties.AAI_URI, "/network/vnfcs/vnfc/vnfc-" + testName);

		Introspector relationship = loader.introspectorFromName("relationship");
		relationship.setValue("related-to", "vnfc");
		relationship.setValue("related-link", "/network/vnfcs/vnfc/vnfc-" + testName);

		Introspector relationshipList = loader.introspectorFromName("relationship-list");
		relationshipList.setValue("relationship", Collections.singletonList(relationship.getUnderlyingObject()));

		Introspector gvnfObj = loader.introspectorFromName("generic-vnf");
		Vertex gvnf = localDbser.createNewVertex(gvnfObj);
		gvnfObj.setValue("relationship-list", relationshipList.getUnderlyingObject());
		gvnfObj.setValue("vnf-id", "vnf-" + testName);

		QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(new URI("/network/generic-vnfs/generic-vnf/vnf-" + testName));

		localDbser.serializeToDb(gvnfObj, gvnf, uriQuery, null, "test");
		assertTrue("Generic-vnf has uuid ", gvnf.property(AAIProperties.AAI_UUID).isPresent());
		assertTrue("Edge has uuid ", gvnf.edges(Direction.BOTH).next().property(AAIProperties.AAI_UUID).isPresent());

	}

	@Test
	public void createEdgeWithValidLabelTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();

		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","myvnf", "aai-uri", "/network/generic-vnfs/generic-vnf/myvnf");
		Vertex vnfc = engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name", "aai-uri", "/network/vnfcs/vnfc/a-name");

		//sunny day case
		Introspector relData = loader.introspectorFromName("relationship-data");
		relData.setValue("relationship-key", "vnfc.vnfc-name");
		relData.setValue("relationship-value", "a-name");
		Introspector relationship = loader.introspectorFromName("relationship");
		relationship.setValue("related-to", "vnfc");
		relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");
		relationship.setValue("relationship-data",relData);
		relationship.setValue("relationship-label", "over-uses");

		assertTrue(localDbser.createEdge(relationship, gvnf));
		assertTrue(engine.tx().traversal().V(gvnf).both("over-uses").hasNext());
		assertTrue(engine.tx().traversal().V(vnfc).both("over-uses").hasNext());

	}

	@Test
	public void createEdgeWithValidLabelWhenSameEdgeExistsTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();
		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","myvnf", "aai-uri", "/network/generic-vnfs/generic-vnf/myvnf");
		Vertex vnfc = engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name", "aai-uri", "/network/vnfcs/vnfc/a-name");
		edgeSer.addEdge(graph.traversal(), gvnf, vnfc, "re-uses");

		Introspector relData = loader.introspectorFromName("relationship-data");
		relData.setValue("relationship-key", "vnfc.vnfc-name");
		relData.setValue("relationship-value", "a-name");
		Introspector relationship = loader.introspectorFromName("relationship");
		relationship.setValue("related-to", "vnfc");
		relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");
		relationship.setValue("relationship-data",relData);
		relationship.setValue("relationship-label", "re-uses");

		assertTrue(localDbser.createEdge(relationship, gvnf));
		assertTrue(engine.tx().traversal().V(gvnf).both("re-uses").hasNext());
		assertTrue(engine.tx().traversal().V(vnfc).both("re-uses").hasNext());
		assertEquals("Number of edges between vertexes is 1", Long.valueOf(1), engine.tx().traversal().V(vnfc).both().count().next());

	}

	@Test
	public void createEdgeWithValidLabelWhenDiffEdgeExistsTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();
		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","myvnf", "aai-uri", "/network/generic-vnfs/generic-vnf/myvnf");
		Vertex vnfc = engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name", "aai-uri", "/network/vnfcs/vnfc/a-name");
		edgeSer.addEdge(graph.traversal(), gvnf, vnfc, "uses");

		Introspector relData = loader.introspectorFromName("relationship-data");
		relData.setValue("relationship-key", "vnfc.vnfc-name");
		relData.setValue("relationship-value", "a-name");
		Introspector relationship = loader.introspectorFromName("relationship");
		relationship.setValue("related-to", "vnfc");
		relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");
		relationship.setValue("relationship-data",relData);
		relationship.setValue("relationship-label", "uses");
		localDbser.createEdge(relationship, gvnf);

		relationship.setValue("relationship-label", "re-uses");

		assertTrue(localDbser.createEdge(relationship, gvnf));
		assertTrue(engine.tx().traversal().V(gvnf).both("re-uses").hasNext());
		assertTrue(engine.tx().traversal().V(vnfc).both("re-uses").hasNext());
		assertTrue(engine.tx().traversal().V(gvnf).both("uses").hasNext());
		assertTrue(engine.tx().traversal().V(vnfc).both("uses").hasNext());
		assertEquals("Number of edges between vertexes is 2", Long.valueOf(2), engine.tx().traversal().V(vnfc).both().count().next());
		assertEquals("Number of edges between vertexes is 2", Long.valueOf(2), engine.tx().traversal().V(gvnf).both().count().next());

	}

	@Test
	public void createEdgeWithNoLabelTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();
		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","myvnf", "aai-uri", "/network/generic-vnfs/generic-vnf/myvnf");
		Vertex vnfc = engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name", "aai-uri", "/network/vnfcs/vnfc/a-name");

		Introspector relData = loader.introspectorFromName("relationship-data");
		relData.setValue("relationship-key", "vnfc.vnfc-name");
		relData.setValue("relationship-value", "a-name");
		Introspector relationship = loader.introspectorFromName("relationship");
		relationship.setValue("related-to", "vnfc");
		relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");
		relationship.setValue("relationship-data",relData);
		localDbser.createEdge(relationship, gvnf);

		assertTrue(localDbser.createEdge(relationship, gvnf));
		assertTrue(engine.tx().traversal().V(gvnf).both("uses").hasNext());
		assertTrue(engine.tx().traversal().V(vnfc).both("uses").hasNext());
		assertEquals("Number of edges between vertexes is 1", Long.valueOf(1), engine.tx().traversal().V(vnfc).both().count().next());
		assertEquals("Number of edges between vertexes is 1", Long.valueOf(1), engine.tx().traversal().V(gvnf).both().count().next());


	}

	@Test
	public void deleteEdgeWithNoLabelWhenMultipleExistsTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();
		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","myvnf", "aai-uri", "/network/generic-vnfs/generic-vnf/myvnf");
		Vertex vnfc = engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name", "aai-uri", "/network/vnfcs/vnfc/a-name");
		edgeSer.addEdge(graph.traversal(), gvnf, vnfc, "uses");
		edgeSer.addEdge(graph.traversal(), gvnf, vnfc, "re-uses");
		edgeSer.addEdge(graph.traversal(), gvnf, vnfc, "over-uses");

		Introspector relData = loader.introspectorFromName("relationship-data");
		relData.setValue("relationship-key", "vnfc.vnfc-name");
		relData.setValue("relationship-value", "a-name");
		Introspector relationship = loader.introspectorFromName("relationship");
		relationship.setValue("related-to", "vnfc");
		relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");
		relationship.setValue("relationship-data",relData);

		assertTrue(localDbser.deleteEdge(relationship, gvnf));
		assertFalse("generic-vnf has no edge uses", engine.tx().traversal().V(gvnf).both("uses").hasNext());
		assertFalse("vnfc has no edge uses", engine.tx().traversal().V(vnfc).both("uses").hasNext());
		assertTrue("generic-vnf has edge re-uses", engine.tx().traversal().V(gvnf).both("re-uses").hasNext());
		assertTrue("vnfc has edge re-uses", engine.tx().traversal().V(vnfc).both("re-uses").hasNext());
		assertTrue("generic-vnf has edge re-uses", engine.tx().traversal().V(gvnf).both("over-uses").hasNext());
		assertTrue("vnfc has edge re-uses", engine.tx().traversal().V(vnfc).both("over-uses").hasNext());
		assertEquals("Number of edges between vertexes is 2", Long.valueOf(2), engine.tx().traversal().V(vnfc).both().count().next());
		assertEquals("Number of edges between vertexes is 2", Long.valueOf(2), engine.tx().traversal().V(gvnf).both().count().next());

	}

	@Test
	public void deleteEdgeWithValidLabelWhenMultipleExistsTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();

		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","myvnf", "aai-uri", "/network/generic-vnfs/generic-vnf/myvnf");
		Vertex vnfc = engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name", "aai-uri", "/network/vnfcs/vnfc/a-name");
		edgeSer.addEdge(graph.traversal(), gvnf, vnfc, "uses");
		edgeSer.addEdge(graph.traversal(), gvnf, vnfc, "re-uses");
		edgeSer.addEdge(graph.traversal(), gvnf, vnfc, "over-uses");

		Introspector relData = loader.introspectorFromName("relationship-data");
		relData.setValue("relationship-key", "vnfc.vnfc-name");
		relData.setValue("relationship-value", "a-name");
		Introspector relationship = loader.introspectorFromName("relationship");
		relationship.setValue("related-to", "vnfc");
		relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");
		relationship.setValue("relationship-data",relData);
		relationship.setValue("relationship-label", "re-uses");

		assertTrue(localDbser.deleteEdge(relationship, gvnf));
		assertTrue("generic-vnf has edge uses", engine.tx().traversal().V(gvnf).both("uses").hasNext());
		assertTrue("vnfc has edge uses", engine.tx().traversal().V(vnfc).both("uses").hasNext());
		assertFalse("generic-vnf has no edge re-uses", engine.tx().traversal().V(gvnf).both("re-uses").hasNext());
		assertFalse("vnfc has no edge re-uses", engine.tx().traversal().V(vnfc).both("re-uses").hasNext());
		assertTrue("generic-vnf has edge re-uses", engine.tx().traversal().V(gvnf).both("over-uses").hasNext());
		assertTrue("vnfc has edge re-uses", engine.tx().traversal().V(vnfc).both("over-uses").hasNext());
		assertEquals("Number of edges between vertexes is 2", Long.valueOf(2), engine.tx().traversal().V(vnfc).both().count().next());
		assertEquals("Number of edges between vertexes is 2", Long.valueOf(2), engine.tx().traversal().V(gvnf).both().count().next());

	}

	@Test
	public void deleteEdgeWithValidInvalidLabelWhenMultipleExistsTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();

		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","myvnf", "aai-uri", "/network/generic-vnfs/generic-vnf/myvnf");
		Vertex vnfc = engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name", "aai-uri", "/network/vnfcs/vnfc/a-name");
		edgeSer.addEdge(graph.traversal(), gvnf, vnfc, "uses");
		edgeSer.addEdge(graph.traversal(), gvnf, vnfc, "re-uses");
		edgeSer.addEdge(graph.traversal(), gvnf, vnfc, "over-uses");

		Introspector relData = loader.introspectorFromName("relationship-data");
		relData.setValue("relationship-key", "vnfc.vnfc-name");
		relData.setValue("relationship-value", "a-name");
		Introspector relationship = loader.introspectorFromName("relationship");
		relationship.setValue("related-to", "vnfc");
		relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");
		relationship.setValue("relationship-data",relData);
		relationship.setValue("relationship-label", "NA");

		thrown.expect(AAIException.class);
		thrown.expectMessage("No rule found");
		thrown.expectMessage("node type: generic-vnf, node type: vnfc, label: NA, type: COUSIN");
		localDbser.deleteEdge(relationship, gvnf);
	}

	@Test
	public void serializeToDbWithLabelTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, URISyntaxException {

		DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();

		engine.startTransaction();

		engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name", "aai-uri", "/network/vnfcs/vnfc/a-name");

		Introspector relationship = loader.introspectorFromName("relationship");
		relationship.setValue("related-to", "vnfc");
		relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");
		relationship.setValue("relationship-label", "re-uses");
		Introspector relationshipList = loader.introspectorFromName("relationship-list");
		relationshipList.setValue("relationship", Collections.singletonList(relationship.getUnderlyingObject()));

		Introspector gvnfObj = loader.introspectorFromName("generic-vnf");
		Vertex gvnf = localDbser.createNewVertex(gvnfObj);
		gvnfObj.setValue("relationship-list", relationshipList.getUnderlyingObject());
		gvnfObj.setValue("vnf-id", "myvnf");

		QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(new URI("/network/generic-vnfs/generic-vnf/myvnf"));

		localDbser.serializeToDb(gvnfObj, gvnf, uriQuery, null, "test");

		assertTrue("vertex with vnf-id myvnf exists", engine.tx().traversal().V().has("vnf-id", "myvnf").hasNext());
		assertTrue("vertex with vnfc-name a-name exists", engine.tx().traversal().V().has("vnfc-name", "a-name").hasNext());
		assertFalse("generic-vnf has no edge re-uses", engine.tx().traversal().V().has("vnf-id", "myvnf").both("uses").hasNext());
		assertFalse("vnfc has no edge re-uses", engine.tx().traversal().V().has("vnfc-name", "a-name").both("uses").hasNext());
		assertTrue("generic-vnf has edge re-uses", engine.tx().traversal().V().has("vnf-id", "myvnf").both("re-uses").hasNext());
		assertTrue("vnfc has edge re-uses", engine.tx().traversal().V().has("vnfc-name", "a-name").both("re-uses").hasNext());
		assertFalse("generic-vnf has no edge re-uses", engine.tx().traversal().V().has("vnf-id", "myvnf").both("over-uses").hasNext());
		assertFalse("vnfc has no edge re-uses", engine.tx().traversal().V().has("vnfc-name", "a-name").both("over-uses").hasNext());
		assertEquals("Number of edges between vertexes is 1", Long.valueOf(1), engine.tx().traversal().V().has("vnfc-name", "a-name").both().count().next());
		assertEquals("Number of edges between vertexes is 1", Long.valueOf(1), engine.tx().traversal().V().has("vnf-id", "myvnf").both().count().next());

	}

	@Test
	public void serializeToDbWithoutLabelTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, URISyntaxException {

		DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();

		engine.startTransaction();

		engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name", "aai-uri", "/network/vnfcs/vnfc/a-name");

		Introspector relationship = loader.introspectorFromName("relationship");
		relationship.setValue("related-to", "vnfc");
		relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");

		Introspector relationshipList = loader.introspectorFromName("relationship-list");
		relationshipList.setValue("relationship", Collections.singletonList(relationship.getUnderlyingObject()));

		Introspector gvnfObj = loader.introspectorFromName("generic-vnf");
		Vertex gvnf = localDbser.createNewVertex(gvnfObj);
		gvnfObj.setValue("relationship-list", relationshipList.getUnderlyingObject());
		gvnfObj.setValue("vnf-id", "myvnf");

		QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(new URI("/network/generic-vnfs/generic-vnf/myvnf"));

		localDbser.serializeToDb(gvnfObj, gvnf, uriQuery, null, "test");

		assertTrue("vertex with vnf-id myvnf exists", engine.tx().traversal().V().has("vnf-id", "myvnf").hasNext());
		assertTrue("vertex with vnfc-name a-name exists", engine.tx().traversal().V().has("vnfc-name", "a-name").hasNext());
		assertTrue("generic-vnf has edge uses", engine.tx().traversal().V().has("vnf-id", "myvnf").both("uses").hasNext());
		assertTrue("vnfc has edge uses", engine.tx().traversal().V().has("vnfc-name", "a-name").both("uses").hasNext());
		assertFalse("generic-vnf has no edge re-uses", engine.tx().traversal().V().has("vnf-id", "myvnf").both("re-uses").hasNext());
		assertFalse("vnfc has no edge re-uses", engine.tx().traversal().V().has("vnfc-name", "a-name").both("re-uses").hasNext());
		assertFalse("generic-vnf has no edge over-uses", engine.tx().traversal().V().has("vnf-id", "myvnf").both("over-uses").hasNext());
		assertFalse("vnfc has no edge over-uses", engine.tx().traversal().V().has("vnfc-name", "a-name").both("over-uses").hasNext());
		assertEquals("Number of edges between vertexes is 1", Long.valueOf(1), engine.tx().traversal().V().has("vnfc-name", "a-name").both().count().next());
		assertEquals("Number of edges between vertexes is 1", Long.valueOf(1), engine.tx().traversal().V().has("vnf-id", "myvnf").both().count().next());

	}

	@Test
	public void serializeToDbWithInvalidLabelTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, URISyntaxException {

		DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();

		engine.startTransaction();

		engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name", "aai-uri", "/network/vnfcs/vnfc/a-name");

		Introspector relationship = loader.introspectorFromName("relationship");
		relationship.setValue("related-to", "vnfc");
		relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");
		relationship.setValue("relationship-label", "NA");
		Introspector relationshipList = loader.introspectorFromName("relationship-list");
		relationshipList.setValue("relationship", Collections.singletonList(relationship.getUnderlyingObject()));

		Introspector gvnfObj = loader.introspectorFromName("generic-vnf");
		Vertex gvnf = localDbser.createNewVertex(gvnfObj);
		gvnfObj.setValue("relationship-list", relationshipList.getUnderlyingObject());
		gvnfObj.setValue("vnf-id", "myvnf");

		QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(new URI("/network/generic-vnfs/generic-vnf/myvnf"));

		thrown.expect(AAIException.class);
		thrown.expectMessage("No EdgeRule found for passed nodeTypes: generic-vnf, vnfc with label NA.");
		localDbser.serializeToDb(gvnfObj, gvnf, uriQuery, null, "test");

	}

	@Test
	public void serializeToDbWithLabelAndEdgeExistsTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, URISyntaxException {

		DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();

		engine.startTransaction();
		engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name", "aai-uri", "/network/vnfcs/vnfc/a-name");

		Introspector relationship;
		Introspector relationshipList;
		List<Object> relList = new ArrayList<>();

		// create generic-vnf
		Introspector gvnfObj = loader.introspectorFromName("generic-vnf");
		Vertex gvnf = localDbser.createNewVertex(gvnfObj);
		gvnfObj.setValue("vnf-id", "myvnf");
		QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(new URI("/network/generic-vnfs/generic-vnf/myvnf"));

		// create relationship to vnfc
		relationship = loader.introspectorFromName("relationship");
		relationship.setValue("related-to", "vnfc");
		relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");
		relList.add(relationship.getUnderlyingObject());
		relationshipList = loader.introspectorFromName("relationship-list");
		relationshipList.setValue("relationship", relList);
		gvnfObj.setValue("relationship-list", relationshipList.getUnderlyingObject());

		// add gvnf to graph
		localDbser.serializeToDb(gvnfObj, gvnf, uriQuery, null, "test");

		// add second relationship
		relationship = loader.introspectorFromName("relationship");
		relationship.setValue("related-to", "vnfc");
		relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");
		relationship.setValue("relationship-label", "re-uses");
		relList.add(relationship.getUnderlyingObject());
		relationshipList = loader.introspectorFromName("relationship-list");
		relationshipList.setValue("relationship", relList);
		gvnfObj.setValue("relationship-list", relationshipList.getUnderlyingObject());

		localDbser.serializeToDb(gvnfObj, gvnf, uriQuery, null, "test");

		assertTrue("vertex with vnf-id myvnf exists", engine.tx().traversal().V().has("vnf-id", "myvnf").hasNext());
		assertTrue("vertex with vnfc-name a-name exists", engine.tx().traversal().V().has("vnfc-name", "a-name").hasNext());
		assertTrue("generic-vnf has  edge uses", engine.tx().traversal().V().has("vnf-id", "myvnf").both("uses").hasNext());
		assertTrue("vnfc has  edge uses", engine.tx().traversal().V().has("vnfc-name", "a-name").both("uses").hasNext());
		assertTrue("generic-vnf has edge re-uses", engine.tx().traversal().V().has("vnf-id", "myvnf").both("re-uses").hasNext());
		assertTrue("vnfc has edge re-uses", engine.tx().traversal().V().has("vnfc-name", "a-name").both("re-uses").hasNext());
		assertFalse("generic-vnf has no edge over-uses", engine.tx().traversal().V().has("vnf-id", "myvnf").both("over-uses").hasNext());
		assertFalse("vnfc has no edge over-uses", engine.tx().traversal().V().has("vnfc-name", "a-name").both("over-uses").hasNext());
		assertEquals("Number of edges between vertexes is 2", Long.valueOf(2), engine.tx().traversal().V().has("vnfc-name", "a-name").both().count().next());
		assertEquals("Number of edges between vertexes is 2", Long.valueOf(2), engine.tx().traversal().V().has("vnf-id", "myvnf").both().count().next());

	}

	@Test
	public void serializeToDbWithLabelDroppingRelationshipTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, URISyntaxException {

		DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();

		engine.startTransaction();
		engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name", "aai-uri", "/network/vnfcs/vnfc/a-name");


		Introspector relationship;
		Introspector relationshipList;
		List<Object> relList = new ArrayList<>();

		// create generic-vnf
		Introspector gvnfObj = loader.introspectorFromName("generic-vnf");
		Vertex gvnf = localDbser.createNewVertex(gvnfObj);
		gvnfObj.setValue("vnf-id", "myvnf");
		QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(new URI("/network/generic-vnfs/generic-vnf/myvnf"));

		// create relationship to vnfc
		relationship = loader.introspectorFromName("relationship");
		relationship.setValue("related-to", "vnfc");
		relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");
		relList.add(relationship.getUnderlyingObject());
		// add second relationship
		relationship = loader.introspectorFromName("relationship");
		relationship.setValue("related-to", "vnfc");
		relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");
		relationship.setValue("relationship-label", "re-uses");
		relList.add(relationship.getUnderlyingObject());
		relationshipList = loader.introspectorFromName("relationship-list");
		relationshipList.setValue("relationship", relList);
		gvnfObj.setValue("relationship-list", relationshipList.getUnderlyingObject());

		// add gvnf to graph
		localDbser.serializeToDb(gvnfObj, gvnf, uriQuery, null, "test");

		// drop second relationship
		relList.remove(1);
		relationshipList = loader.introspectorFromName("relationship-list");
		relationshipList.setValue("relationship", relList);
		gvnfObj.setValue("relationship-list", relationshipList.getUnderlyingObject());

		localDbser.serializeToDb(gvnfObj, gvnf, uriQuery, null, "test");

		assertTrue("vertex with vnf-id myvnf exists", engine.tx().traversal().V().has("vnf-id", "myvnf").hasNext());
		assertTrue("vertex with vnfc-name a-name exists", engine.tx().traversal().V().has("vnfc-name", "a-name").hasNext());
		assertTrue("generic-vnf has  edge uses", engine.tx().traversal().V().has("vnf-id", "myvnf").both("uses").hasNext());
		assertTrue("vnfc has  edge uses", engine.tx().traversal().V().has("vnfc-name", "a-name").both("uses").hasNext());
		assertFalse("generic-vnf no longer has edge re-uses", engine.tx().traversal().V().has("vnf-id", "myvnf").both("re-uses").hasNext());
		assertFalse("vnfc no longer has edge re-uses", engine.tx().traversal().V().has("vnfc-name", "a-name").both("re-uses").hasNext());
		assertFalse("generic-vnf has no edge over-uses", engine.tx().traversal().V().has("vnf-id", "myvnf").both("over-uses").hasNext());
		assertFalse("vnfc has no edge over-uses", engine.tx().traversal().V().has("vnfc-name", "a-name").both("over-uses").hasNext());
		assertEquals("Number of edges between vertexes is 1", Long.valueOf(1), engine.tx().traversal().V().has("vnfc-name", "a-name").both().count().next());
		assertEquals("Number of edges between vertexes is 1", Long.valueOf(1), engine.tx().traversal().V().has("vnf-id", "myvnf").both().count().next());

	}

	private DBSerializer getDBSerializerWithSpecificEdgeRules()
			throws NoSuchFieldException, AAIException, IllegalAccessException {
		
		
		DBSerializer localDbser = new DBSerializer(version, engine, introspectorFactoryType, "AAI-TEST");
		return localDbser;
	}
}
