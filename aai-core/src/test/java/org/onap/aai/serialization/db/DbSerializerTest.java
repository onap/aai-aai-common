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
package org.onap.aai.serialization.db;

import org.janusgraph.core.JanusGraphFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.*;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.onap.aai.AAISetup;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.dbmap.DBConnectionType;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.edges.enums.EdgeType;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.*;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.JanusGraphDBEngine;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.setup.SchemaVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(value = Parameterized.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class DbSerializerTest extends AAISetup {

	//to use, set thrown.expect to whatever your test needs
	//this line establishes default of expecting no exception to be thrown
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	protected static Graph graph;

	@Autowired
	protected EdgeSerializer edgeSer;
	@Autowired
	protected EdgeIngestor ei;

	private SchemaVersion version;
	private final ModelType introspectorFactoryType = ModelType.MOXY;
	private final DBConnectionType type = DBConnectionType.REALTIME;
	private Loader loader;
	private TransactionalGraphEngine dbEngine;
	private TransactionalGraphEngine engine; //for tests that aren't mocking the engine
	private DBSerializer dbser;
	TransactionalGraphEngine spy;
	TransactionalGraphEngine.Admin adminSpy;

	@Parameterized.Parameter(value = 0)
	public QueryStyle queryStyle;

	@Parameterized.Parameters(name = "QueryStyle.{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][]{
				{QueryStyle.TRAVERSAL},
				{QueryStyle.TRAVERSAL_URI}
		});
	}

	@BeforeClass
	public static void init() throws Exception {
		graph = JanusGraphFactory.build().set("storage.backend", "inmemory").open();

	}

	@Before
	public void setup() throws Exception {
		//createGraph();
        version = schemaVersions.getDefaultVersion();
		loader = loaderFactory.createLoaderForVersion(introspectorFactoryType, version);
		dbEngine = new JanusGraphDBEngine(queryStyle, type, loader);
		spy = spy(dbEngine);
		adminSpy = spy(dbEngine.asAdmin());


		engine = new JanusGraphDBEngine(queryStyle, type, loader);
		dbser = new DBSerializer(version, engine, introspectorFactoryType, "AAI-TEST");
	}

	@Test
	public void testFindDeletableDoesNotReturnDuplicates() throws AAIException {

		Vertex genericVnf1 = graph.addVertex("aai-node-type", "generic-vnf", "vnf-id", "vnf1", "vnf-name", "vnfName1");

		Vertex lInterface1 = graph.addVertex("aai-node-type", "l-interface", "interface-name", "lInterface1");
		Vertex lInterface2 = graph.addVertex("aai-node-type", "l-interface", "interface-name", "lInterface2");

		Vertex logicalLink1 = graph.addVertex("aai-node-type", "logical-link", "link-name", "logicalLink1");
		Vertex logicalLink2 = graph.addVertex("aai-node-type", "logical-link", "link-name", "logicalLink2");

		GraphTraversalSource g = graph.traversal();

		edgeSer.addTreeEdge(g, genericVnf1, lInterface1);
		edgeSer.addTreeEdge(g, genericVnf1, lInterface2);
		edgeSer.addEdge(g, lInterface1, logicalLink1);
		edgeSer.addEdge(g, lInterface1, logicalLink2);
		// This line will cause the logical link2 to be found twice under linterface 1
		// and also under the linterface 2 and since in the past deletable returned
		// duplicates this test checks that it shouldn't return duplicates
		edgeSer.addEdge(g, lInterface2, logicalLink2);

		when(spy.asAdmin()).thenReturn(adminSpy);
		when(adminSpy.getTraversalSource()).thenReturn(g);
		when(adminSpy.getReadOnlyTraversalSource()).thenReturn(g);

		List<Vertex> deletableVertexes = spy.getQueryEngine().findDeletable(genericVnf1);
		Set<Vertex> vertexSet = new HashSet<>();

		for (Vertex deletableVertex : deletableVertexes) {
		    if(!vertexSet.contains(deletableVertex)){
		        vertexSet.add(deletableVertex);
		    } else {
		    	fail("Find deletable is returning a list of duplicate vertexes");
		    }
		}
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


	public void l3NetworkSetup() throws AAIException {
		/*
		 * This setus up the test graph, For future junits , add more vertices
		 * and edges
		 */

		Vertex l3network1 = graph.addVertex("aai-node-type", "l3-network", "network-id", "network-id-v1", "network-name", "network-name-v1");
		Vertex l3network2 = graph.addVertex("aai-node-type", "l3-network", "network-id", "network-id-v2", "network-name", "network-name-v2");
		Vertex subnet1 = graph.addVertex("aai-node-type", "subnet", "subnet-id", "subnet-id-v1");
		Vertex subnet2 = graph.addVertex("aai-node-type", "subnet", "subnet-id", "subnet-id-v2");

		Vertex l3interipv4addresslist_1 = graph.addVertex("aai-node-type", "l3-interface-ipv4-address-list",
				"l3-interface-ipv4-address", "l3-intr-v1");
		Vertex l3interipv6addresslist_1 =  graph.addVertex("aai-node-type", "l3-interface-ipv6-address-list",
				"l3-interface-ipv6-address", "l3-interface-ipv6-v1");





		GraphTraversalSource g = graph.traversal();
		edgeSer.addTreeEdge(g, subnet1, l3network1);
		edgeSer.addEdge(g, l3interipv4addresslist_1, subnet1);
		edgeSer.addEdge(g, l3interipv6addresslist_1, subnet1);

		edgeSer.addTreeEdge(g, subnet2, l3network2);



	}

	public void vserverSetup() throws AAIException {
		/*
		 * This setus up the test graph, For future junits , add more vertices
		 * and edges
		 */

		Vertex vserver1 = graph.addVertex("aai-node-type", "vserver", "vserver-id", "vss1",
				AAIProperties.AAI_URI.toString(),
					"/cloud-infrastructure/cloud-regions/cloud-region/me/123/tenants/tenant/453/vservers/vserver/vss1");

		Vertex lInterface1 = graph.addVertex("aai-node-type", "l-interface", "interface-name", "lIntr1");
		Vertex lInterface2 = graph.addVertex("aai-node-type", "l-interface", "interface-name", "lIntr2");

		Vertex logicalLink1 = graph.addVertex("aai-node-type", "logical-link", "link-name", "logLink1");
		Vertex logicalLink2 = graph.addVertex("aai-node-type", "logical-link", "link-name", "logLink2");

		Vertex l3interipv4addresslist_1 = graph.addVertex("aai-node-type", "l3-interface-ipv4-address-list",
				"l3-interface-ipv4-address", "l3-intr-ipv4-address-1");
		Vertex l3interipv6addresslist_2 = graph.addVertex("aai-node-type", "l3-interface-ipv6-address-list",
				"l3-interface-ipv4-address", "l3-intr-ipv6-address-1");

		GraphTraversalSource g = graph.traversal();

		edgeSer.addTreeEdge(g, lInterface1, vserver1);
		edgeSer.addTreeEdge(g, lInterface2, vserver1);
		edgeSer.addTreeEdge(g, l3interipv4addresslist_1, lInterface1);
		edgeSer.addTreeEdge(g, l3interipv6addresslist_2, lInterface2);

		edgeSer.addEdge(g, lInterface1, logicalLink1);
		edgeSer.addEdge(g, lInterface2, logicalLink2);
	}

	@Test
	public void subnetDelWithInEdgesIpv4Test() throws AAIException {
		subnetSetup();
		String expected_message = "Object is being reference by additional objects preventing it from being deleted. Please clean up references from the following types [l3-interface-ipv4-address-list]";

		/*
		 * This subnet has in-edges with l3-ipv4 and NOT ok to delete
		 */
		Vertex subnet = graph.traversal().V().has("aai-node-type", "subnet").has("subnet-id", "subnet-id-2").next();

		String exceptionMessage = testCascadeDelete(subnet);
		assertEquals(expected_message, exceptionMessage);

	}

	@Test
	public void subnetDelWithInEdgesIpv6Test() throws AAIException {
		subnetSetup();
		String expected_message = "Object is being reference by additional objects preventing it from being deleted. Please clean up references from the following types [l3-interface-ipv6-address-list]";

		/*
		 * This subnet has in-edges with l3-ipv6 and NOT ok to delete
		 */
		Vertex subnet = graph.traversal().V().has("aai-node-type", "subnet").has("subnet-id", "subnet-id-4").next();
		String exceptionMessage = testCascadeDelete(subnet);
		assertEquals(expected_message, exceptionMessage);

	}

	@Test
	public void subnetDelWithInEdgesL3network() throws AAIException {
		subnetSetup();
		String expected_message = "";

		/*
		 * This subnet has in-edges with l3-network and ok to delete
		 */
		Vertex subnet = graph.traversal().V().has("aai-node-type", "subnet").has("subnet-id", "subnet-id-5").next();

		String exceptionMessage = testCascadeDelete(subnet);
		assertEquals(expected_message, exceptionMessage);

	}

	public String testCascadeDelete(Vertex v) throws AAIException {

		GraphTraversalSource traversal = graph.traversal();
		when(spy.asAdmin()).thenReturn(adminSpy);
		when(adminSpy.getTraversalSource()).thenReturn(traversal);
		when(adminSpy.getReadOnlyTraversalSource()).thenReturn(traversal);

		String exceptionMessage = "";
		DBSerializer serializer = new DBSerializer(version, spy, introspectorFactoryType, "AAI_TEST");
		List<Vertex> deletableVertices = spy.getQueryEngine().findDeletable(v);

		try {
			serializer.delete(v, deletableVertices, "resourceVersion", false);
		} catch (AAIException exception) {
			exceptionMessage = exception.getMessage();
		}
		return exceptionMessage;

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
	public void createNewVertexTest() throws AAIException {
		engine.startTransaction();

		Introspector testObj = loader.introspectorFromName("generic-vnf");

		Vertex testVertex = dbser.createNewVertex(testObj);
		Vertex fromGraph = engine.tx().traversal().V().has("aai-node-type","generic-vnf").toList().get(0);
		assertEquals(testVertex.id(), fromGraph.id());
		assertEquals("AAI-TEST", fromGraph.property(AAIProperties.SOURCE_OF_TRUTH.toString()).value());

	}

    @Test
	public void touchStandardVertexPropertiesTest() throws AAIException, InterruptedException {
		engine.startTransaction();

        //if this test runs through too fast the value may not change, causing the test to fail. sleeping ensures a different value
        Thread.sleep(2);
		DBSerializer dbser2 = new DBSerializer(version, engine, introspectorFactoryType, "AAI-TEST-2");
		Vertex vert = graph.addVertex("aai-node-type", "generic-vnf");

		// Upon first creation of the Vertex and the DBSerializer
        // the source of truth and created-ts should be the same as their modified counterparts
        dbser2.touchStandardVertexProperties(vert, true);
        String createTS = (String)vert.property(AAIProperties.CREATED_TS).value();
        String modTS = (String)vert.property(AAIProperties.LAST_MOD_TS).value();
        String sot = (String)vert.property(AAIProperties.SOURCE_OF_TRUTH).value();
        String lastModSOT = (String)vert.property(AAIProperties.LAST_MOD_SOURCE_OF_TRUTH).value();
        assertTrue(createTS.equals(modTS));
        assertTrue(sot.equals(lastModSOT));

        //if this test runs through too fast the value may not change, causing the test to fail. sleeping ensures a different value
        Thread.sleep(2);

        // Not new vertex && new DBSerializer (A new serializer since a new one will be created per transaction)
        // Here the vertex will be modified by a different source of truth
        DBSerializer dbser3 = new DBSerializer(version, engine, introspectorFactoryType, "AAI-TEST-3");
        dbser3.touchStandardVertexProperties(vert, false);
        createTS = (String)vert.property(AAIProperties.CREATED_TS).value();
        modTS = (String)vert.property(AAIProperties.LAST_MOD_TS).value();
        sot = (String)vert.property(AAIProperties.SOURCE_OF_TRUTH).value();
        lastModSOT = (String)vert.property(AAIProperties.LAST_MOD_SOURCE_OF_TRUTH).value();
        assertFalse(createTS.equals(modTS));
        assertFalse(sot.equals(lastModSOT));

        //if this test runs through too fast the value may not change, causing the test to fail. sleeping ensures a different value
        Thread.sleep(2);

        // The currentTimeMillis used for the created-ts and modified-ts is created at DBSerializer instantiation
        // Every REST transaction should create a new DBSerializer - thus a new currentTimeMillis is used at the time of transaction.
        // Using an existing vertex, but treating it as new && using an older DBSerializer
		dbser.touchStandardVertexProperties(vert, true);
		String resverStart = (String)vert.property(AAIProperties.RESOURCE_VERSION).value();
        String lastModTimeStart = (String)vert.property(AAIProperties.LAST_MOD_TS).value();
        createTS = (String)vert.property(AAIProperties.CREATED_TS).value();
        modTS = (String)vert.property(AAIProperties.LAST_MOD_TS).value();
        assertTrue(createTS.equals(modTS));
        assertEquals("AAI-TEST", vert.property(AAIProperties.LAST_MOD_SOURCE_OF_TRUTH).value());

        //if this test runs through too fast the value may not change, causing the test to fail. sleeping ensures a different value
        Thread.sleep(2);

		dbser2.touchStandardVertexProperties(vert, false);
		String resourceVer = (String)vert.property(AAIProperties.RESOURCE_VERSION).value();
		String lastModTs = (String)vert.property(AAIProperties.LAST_MOD_TS).value();
		String lastModSoT = (String)vert.property(AAIProperties.LAST_MOD_SOURCE_OF_TRUTH).value();

		assertFalse(resverStart.equals(resourceVer));
		assertFalse(lastModTimeStart.equals(lastModTs));
		assertEquals("AAI-TEST-2", lastModSoT);
	}

	@Test
	public void touchStandardVertexPropertiesAAIUUIDTest() throws AAIException, InterruptedException {
		engine.startTransaction();

		Graph graph = TinkerGraph.open();
		Vertex v = graph.addVertex("aai-node-type", "generic-vnf");

		dbser.touchStandardVertexProperties(v, true);

		assertTrue(v.property(AAIProperties.AAI_UUID).isPresent());
		try {
			UUID.fromString((String)v.property(AAIProperties.AAI_UUID).value());
		} catch (IllegalArgumentException e) {
			fail("Vertex uuid is not valid uuid");
		}
	}

	@Test
	public void verifyResourceVersion_SunnyDayTest() throws AAIException {
		engine.startTransaction();

		assertTrue(dbser.verifyResourceVersion("delete", "vnfc", "abc", "abc", "vnfcs/vnfc/vnfcId"));

	}

	@Test
	public void verifyResourceVersion_CreateWithRVTest() throws AAIException {
		engine.startTransaction();

		thrown.expect(AAIException.class);
		thrown.expectMessage("resource-version passed for create of generic-vnfs/generic-vnf/myid");
		dbser.verifyResourceVersion("create", "generic-vnf", null, "old-res-ver", "generic-vnfs/generic-vnf/myid");

	}

	@Test
	public void verifyResourceVersion_MissingRVTest() throws AAIException {
		engine.startTransaction();

		thrown.expect(AAIException.class);
		thrown.expectMessage("resource-version not passed for update of generic-vnfs/generic-vnf/myid");
		dbser.verifyResourceVersion("update", "generic-vnf", "current-res-ver", null, "generic-vnfs/generic-vnf/myid");

	}

	@Test
	public void verifyResourceVersion_MismatchRVTest() throws AAIException {
		engine.startTransaction();

		thrown.expect(AAIException.class);
		thrown.expectMessage("resource-version MISMATCH for update of generic-vnfs/generic-vnf/myid");
		dbser.verifyResourceVersion("update", "generic-vnf", "current-res-ver", "old-res-ver", "generic-vnfs/generic-vnf/myid");

	}

	@Test
	public void trimClassNameTest() throws AAIException {
		assertEquals("GenericVnf", dbser.trimClassName("GenericVnf"));
		assertEquals("GenericVnf", dbser.trimClassName("org.onap.aai.GenericVnf"));
	}

	@Test
	public void getURIForVertexTest() throws AAIException, URISyntaxException, UnsupportedEncodingException {
		engine.startTransaction();

		Vertex cr = engine.tx().addVertex("aai-node-type", "cloud-region", "cloud-owner", "me", "cloud-region-id", "123", "aai-uri", "/cloud-infrastructure/cloud-regions/cloud-region/me/123");
		Vertex ten = engine.tx().addVertex("aai-node-type", "tenant", "tenant-id", "453");

		edgeSer.addTreeEdge(engine.tx().traversal(), cr, ten);

		ten.property("aai-uri", "/cloud-infrastructure/cloud-regions/cloud-region/me/123/tenants/tenant/453");

		URI compare = new URI("/cloud-infrastructure/cloud-regions/cloud-region/me/123/tenants/tenant/453");
		assertEquals(compare, dbser.getURIForVertex(ten));

		URI compareFailure = new URI("/unknown-uri");
		ten.property("aai-uri").remove();
		assertEquals(compareFailure, dbser.getURIForVertex(ten));

	}

	@Test
	public void getVertexPropertiesTest() throws AAIException, UnsupportedEncodingException {
		engine.startTransaction();

		Vertex cr = engine.tx().addVertex("aai-node-type", "cloud-region", "cloud-owner", "me", "cloud-region-id", "123");

		Introspector crIntro = dbser.getVertexProperties(cr);
		assertEquals("cloud-region", crIntro.getDbName());
		assertEquals("me", crIntro.getValue("cloud-owner"));
		assertEquals("123", crIntro.getValue("cloud-region-id"));

	}

	@Test
	public void getEdgeBetweenTest() throws AAIException {
		engine.startTransaction();

		Vertex cr = engine.tx().addVertex("aai-node-type", "cloud-region", "cloud-owner", "me", "cloud-region-id", "123");
		Vertex ten = engine.tx().addVertex("aai-node-type", "tenant", "tenant-id", "453");

		edgeSer.addTreeEdge(engine.tx().traversal(), cr, ten);

		Edge e = dbser.getEdgeBetween(EdgeType.TREE, ten, cr, null);
		assertEquals("org.onap.relationships.inventory.BelongsTo", e.label());

	}

	@Test
	public void deleteEdgeTest() throws AAIException, UnsupportedEncodingException {
		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","myvnf", "aai-uri", "/network/generic-vnfs/generic-vnf/myvnf");
		Vertex vnfc = engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name", "aai-uri", "/network/vnfcs/vnfc/a-name");

		edgeSer.addEdge(engine.tx().traversal(), gvnf, vnfc);

		Introspector relData = loader.introspectorFromName("relationship-data");
		relData.setValue("relationship-key", "vnfc.vnfc-name");
		relData.setValue("relationship-value", "a-name");
		Introspector relationship = loader.introspectorFromName("relationship");
		relationship.setValue("related-to", "vnfc");
		relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");
		relationship.setValue("relationship-data",relData);

		assertTrue(dbser.deleteEdge(relationship, gvnf));

		assertFalse(engine.tx().traversal().V(gvnf).both("uses").hasNext());
		assertFalse(engine.tx().traversal().V(vnfc).both("uses").hasNext());

	}

	@Test
	public void createEdgeTest() throws AAIException, UnsupportedEncodingException {
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

		assertTrue(dbser.createEdge(relationship, gvnf));
		assertTrue(engine.tx().traversal().V(gvnf).both("org.onap.relationships.inventory.BelongsTo").hasNext());
		assertTrue(engine.tx().traversal().V(vnfc).both("org.onap.relationships.inventory.BelongsTo").hasNext());

	}

	@Test
	public void createCousinEdgeThatShouldBeTreeTest() throws AAIException, UnsupportedEncodingException, URISyntaxException {
		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","myvnf", "aai-uri", "/network/generic-vnfs/generic-vnf/myvnf");
		Vertex vf = engine.tx().addVertex("aai-node-type","vf-module","vf-module-id","vf-id", "aai-uri", "/network/generic-vnfs/generic-vnf/myvnf/vf-modules/vf-module/vf-id");

		edgeSer.addTreeEdge(engine.tx().traversal(), gvnf, vf);

		Introspector relationship = loader.introspectorFromName("relationship");
		relationship.setValue("related-to", "vf-module");
		relationship.setValue("related-link", dbser.getURIForVertex(vf).toString());
		Introspector relationshipList = loader.introspectorFromName("relationship-list");
		relationshipList.setValue("relationship", Collections.singletonList(relationship.getUnderlyingObject()));

		Introspector gvnfObj = loader.introspectorFromName("generic-vnf");
		Vertex gvnf2 = dbser.createNewVertex(gvnfObj);
		gvnfObj.setValue("relationship-list", relationshipList.getUnderlyingObject());
		gvnfObj.setValue("vnf-id", "myvnf-1");

		QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(new URI("/network/generic-vnfs/generic-vnf/myvnf-1"));

		try {
			dbser.serializeToDb(gvnfObj, gvnf2, uriQuery, null, "test");
		} catch (AAIException e) {
			assertEquals("AAI_6145", e.getCode());
		}
	}

	@Test
	public void createEdgeNodeDoesNotExistExceptionTest() throws AAIException, UnsupportedEncodingException {
		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","myvnf", "aai-uri", "/network/generic-vnfs/generic-vnf/myvnf");

		//rainy day case, edge to non-existent object
		Introspector relData = loader.introspectorFromName("relationship-data");
		relData.setValue("relationship-key", "vnfc.vnfc-name");
		relData.setValue("relationship-value", "b-name");
		Introspector relationship = loader.introspectorFromName("relationship");
		relationship.setValue("related-to", "vnfc");
		relationship.setValue("related-link", "/network/vnfcs/vnfc/b-name");
		relationship.setValue("relationship-data",relData);

		thrown.expect(AAIException.class);
		thrown.expectMessage("Node of type vnfc. Could not find object at: /network/vnfcs/vnfc/b-name");
		dbser.createEdge(relationship, gvnf);

	}

	@Test
	public void serializeSingleVertexTopLevelTest() throws AAIException, UnsupportedEncodingException {
		engine.startTransaction();

		Introspector gvnf = loader.introspectorFromName("generic-vnf");
		Vertex gvnfVert = dbser.createNewVertex(gvnf);

		gvnf.setValue("vnf-id", "myvnf");
		dbser.serializeSingleVertex(gvnfVert, gvnf, "test");
		assertTrue(engine.tx().traversal().V().has("aai-node-type","generic-vnf").has("vnf-id","myvnf").hasNext());

	}

	@Test
	public void serializeSingleVertexChildTest() throws AAIException, UnsupportedEncodingException {
		engine.startTransaction();

		Vertex cr = engine.tx().addVertex("aai-node-type", "cloud-region", "cloud-owner", "me", "cloud-region-id", "123", "aai-uri", "/cloud-infrastructure/cloud-regions/cloud-region/me/123");
		Introspector tenIn = loader.introspectorFromName("tenant");
		Vertex ten = dbser.createNewVertex(tenIn);
		ten.property("aai-uri", cr.property("aai-uri").value().toString() + "/tenants/tenant/453");

		edgeSer.addTreeEdge(engine.tx().traversal(), cr, ten);

		tenIn.setValue("tenant-id", "453");
		tenIn.setValue("tenant-name", "mytenant");

		dbser.serializeSingleVertex(ten, tenIn, "test");

		assertTrue(engine.tx().traversal().V().has("aai-node-type","tenant").has("tenant-id","453").has("tenant-name","mytenant").hasNext());

	}


	@Test
	public void getVertexPropertiesRelationshipHasLabelTest() throws AAIException, UnsupportedEncodingException {
		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","vnf-123","aai-uri", "/network/generic-vnfs/generic-vnf/vnf-123");
		Vertex vnfc = engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","vnfc-123","aai-uri", "/network/vnfcs/vnfc/vnfc-123");

		edgeSer.addEdge(engine.tx().traversal(), gvnf, vnfc);

		Introspector obj = loader.introspectorFromName("generic-vnf");
		obj = this.dbser.dbToObject(Arrays.asList(gvnf), obj, AAIProperties.MAXIMUM_DEPTH, false, "false");


		MarshallerProperties properties = new MarshallerProperties
				.Builder(org.onap.aai.restcore.MediaType.getEnum("application/json")).formatted(true).build();
		System.out.println(obj.marshal(properties));

		assertEquals("edge label between generic-vnf and vnfs is uses",
				"org.onap.relationships.inventory.BelongsTo",
				obj.getWrappedValue("relationship-list")
					.getWrappedListValue("relationship")
					.get(0)
					.getValue("relationship-label")
		);


	}

	@Test
	public void getVertexPropertiesRelationshipOldVersionNoEdgeLabelTest() throws AAIException, UnsupportedEncodingException {

		SchemaVersion version = schemaVersions.getAppRootVersion();
		DBSerializer dbser = new DBSerializer(version, engine, introspectorFactoryType, "AAI-TEST");
		Loader loader = loaderFactory.createLoaderForVersion(introspectorFactoryType, version);

		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","vnf-123", "aai-uri", "/network/generic-vnfs/generic-vnf/vnf-123");
		Vertex vnfc = engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","vnfc-123", "aai-uri", "/network/vnfcs/vnfc/vnfc-123");

		edgeSer.addEdge(engine.tx().traversal(), gvnf, vnfc);

		Introspector obj = loader.introspectorFromName("generic-vnf");
		obj = dbser.dbToObject(Arrays.asList(gvnf), obj, AAIProperties.MAXIMUM_DEPTH, false, "false");

		assertEquals("Relationship does not contain edge-property", false, obj.getWrappedValue("relationship-list").getWrappedListValue("relationship").get(0).hasProperty("relationship-label"));


	}

	@Test
	public void createEdgeWithInvalidLabelTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","myvnf", "aai-uri", "/network/generic-vnfs/generic-vnf/myvnf");
		engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name", "aai-uri", "/network/vnfcs/vnfc/a-name");

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
		dbser.createEdge(relationship, gvnf);

	}

	@Test
	public void addRelatedToPropertyTest() throws AAIException {
		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf",
				"vnf-id","myname",
				"vnf-name","myname",
				"aai-uri", "/network/generic-vnfs/generic-vnf/myname"
		);
		engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name", "aai-uri", "/network/vnfcs/vnfc/a-name");
		Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getAppRootVersion());
		Introspector gv = loader.introspectorFromName("generic-vnf");
		gv.setValue("vnf-name", "myname");

		Introspector rel = loader.introspectorFromName("relationship");
		DBSerializer dbser = new DBSerializer(schemaVersions.getAppRootVersion(), dbEngine,
												ModelType.MOXY, "AAI-TEST");
		dbser.addRelatedToProperty(rel, gvnf, "generic-vnf");
		List<Introspector> relToProps = rel.getWrappedListValue("related-to-property");
		assertThat(relToProps.size(), is(1));
		Introspector relToProp = relToProps.get(0);
		assertThat(relToProp.getValue("property-key"), is("generic-vnf.vnf-name"));
		assertThat(relToProp.getValue("property-value"), is("myname"));
	}

	@Test
	public void dbToObjectContainerMismatchTest() throws AAIException, UnsupportedEncodingException {
		DBSerializer dbser = new DBSerializer(schemaVersions.getAppRootVersion(), dbEngine,
				ModelType.MOXY, "AAI-TEST");
		Graph vertexMaker = TinkerGraph.open();
		Vertex a = vertexMaker.addVertex(T.id, "0");
		Vertex b = vertexMaker.addVertex(T.id, "1");
		List<Vertex> vertices = Arrays.asList(a,b);

		Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getAppRootVersion());
		Introspector intro = loader.introspectorFromName("image"); //just need any non-container object

		thrown.expect(AAIException.class);
		thrown.expectMessage("query object mismatch: this object cannot hold multiple items.");

		dbser.dbToObject(vertices, intro, Integer.MAX_VALUE, true, "doesn't matter");
	}

	@Test
	public void dbToObjectTest() throws AAIException, UnsupportedEncodingException {
		engine.startTransaction();

		DBSerializer dbser = new DBSerializer(version, engine,
				ModelType.MOXY, "AAI-TEST");
		Vertex gv1 = engine.tx().addVertex("aai-node-type", "generic-vnf", "vnf-id", "id1");
		Vertex gv2 = engine.tx().addVertex("aai-node-type", "generic-vnf", "vnf-id", "id2");
		List<Vertex> vertices = Arrays.asList(gv1, gv2);

		Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, version);
		Introspector gvContainer = loader.introspectorFromName("generic-vnfs");

		Introspector res = dbser.dbToObject(vertices, gvContainer, 0, true, "true");
		List<Introspector> gvs = res.getWrappedListValue("generic-vnf");
		assertTrue(gvs.size() == 2);
		for (Introspector i : gvs) {
			String vnfId = i.getValue("vnf-id");
			assertTrue("id1".equals(vnfId) || "id2".equals(vnfId));
		}


	}

	@Test
	public void getEdgeBetweenNoLabelTest() throws AAIException {
		DBSerializer dbser = new DBSerializer(version, engine,
				ModelType.MOXY, "AAI-TEST");
		engine.startTransaction();
		Vertex gv = engine.tx().addVertex("aai-node-type", "generic-vnf", "vnf-id", "id1");
		Vertex lint = engine.tx().addVertex("aai-node-type", "l-interface", "interface-name", "name1");
		edgeSer.addTreeEdge(engine.tx().traversal(), gv, lint);

		Edge res = dbser.getEdgeBetween(EdgeType.TREE, gv, lint);
		assertEquals("org.onap.relationships.inventory.BelongsTo", res.label());

	}

	@Test
	public void deleteItemsWithTraversal() throws AAIException {
		DBSerializer dbser = new DBSerializer(version, engine,
				ModelType.MOXY, "AAI-TEST");
		engine.startTransaction();
		Vertex gv = engine.tx().addVertex("aai-node-type", "generic-vnf", "vnf-id", "id1");
		Vertex lint = engine.tx().addVertex("aai-node-type", "l-interface", "interface-name", "name1");

		assertTrue(engine.tx().traversal().V().has("vnf-id", "id1").hasNext());
		assertTrue(engine.tx().traversal().V().has("interface-name", "name1").hasNext());

		dbser.deleteItemsWithTraversal(Arrays.asList(gv, lint));

		assertTrue(!engine.tx().traversal().V().has("vnf-id", "id1").hasNext());
		assertTrue(!engine.tx().traversal().V().has("interface-name", "name1").hasNext());


	}

	@Test
	public void serializeToDbWithParentTest() throws AAIException, UnsupportedEncodingException, URISyntaxException {
		DBSerializer dbser = new DBSerializer(version, engine,
				ModelType.MOXY, "AAI-TEST");
		engine.startTransaction();
		Vertex gv = engine.tx().addVertex("aai-node-type", "generic-vnf", "vnf-id", "id1", "aai-uri", "/network/generic-vnfs/generic-vnf/id1");
		Vertex lint = engine.tx().addVertex("aai-node-type", "l-interface");
		edgeSer.addTreeEdge(engine.tx().traversal(), gv, lint);

		Introspector lintIntro = loader.introspectorFromName("l-interface");
		lintIntro.setValue("interface-name", "name1");
		lintIntro.setValue("interface-role", "actor");
		URI lintURI = new URI("/network/generic-vnfs/generic-vnf/id1/l-interfaces/l-interface/name1");
		QueryParser uriQuery = engine.getQueryBuilder(gv).createQueryFromURI(lintURI);
		dbser.serializeToDb(lintIntro, lint, uriQuery, "test-identifier", "AAI-TEST");

		assertTrue(engine.tx().traversal().V(lint).has("interface-role", "actor").hasNext());


	}

	@Test
	public void getLatestVersionViewTest() throws AAIException, UnsupportedEncodingException {
		DBSerializer dbser = new DBSerializer(version, engine,
				ModelType.MOXY, "AAI-TEST");
		engine.startTransaction();
		Vertex phys = engine.tx().addVertex("aai-node-type", "physical-link", "link-name", "zaldo",
												"speed-value", "very-fast", "service-provider-bandwidth-up-units", "things");

		Introspector res = dbser.getLatestVersionView(phys);
		assertTrue("zaldo".equals(res.getValue("link-name")));
		assertTrue("very-fast".equals(res.getValue("speed-value")));
		assertTrue("things".equals(res.getValue("service-provider-bandwidth-up-units")));
	}

	@Test
	public void cascadeVserverDeleteTest() throws AAIException {
		vserverSetup();
		String expected_message = "";

		/* vserver-->l-interface -->logical-link
		 *                       -->l3-ipvx-list
		 */
		Vertex vserver = graph.traversal().V().has("aai-node-type", "vserver").has("vserver-id", "vss1").next();

		String exceptionMessage = testCascadeDelete(vserver);
		assertEquals(expected_message, exceptionMessage);

	}

	@Test
	public void cascadeL3NetworkPreventDeleteTest() throws AAIException {
		l3NetworkSetup();
		ArrayList  expected_messages = new ArrayList<String>();
		expected_messages.add("Object is being reference by additional objects preventing it from being deleted. Please clean up references from the following types [l3-interface-ipv4-address-list, l3-interface-ipv6-address-list]");
		expected_messages.add("Object is being reference by additional objects preventing it from being deleted. Please clean up references from the following types [l3-interface-ipv6-address-list, l3-interface-ipv4-address-list]");

		/* vserver-->l-interface -->logical-link
		 *                       -->l3-ipvx-list
		 */
		Vertex l3network = graph.traversal().V().has("aai-node-type", "l3-network").has("network-id", "network-id-v1").next();

		String exceptionMessage = testCascadeDelete(l3network);
		assertTrue(expected_messages.contains(exceptionMessage));

	}

	@Test
	public void cascadeL3NetworkDeleteTest() throws AAIException {
		l3NetworkSetup();
		String expected_message = "";

		/* vserver-->l-interface -->logical-link
		 *                       -->l3-ipvx-list
		 */
		Vertex l3network = graph.traversal().V().has("aai-node-type", "l3-network").has("network-id", "network-id-v2").next();

		String exceptionMessage = testCascadeDelete(l3network);
		assertEquals(expected_message, exceptionMessage);

	}
}
