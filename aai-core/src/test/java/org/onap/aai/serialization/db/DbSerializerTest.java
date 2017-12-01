/*-
 * ============LICENSE_START=======================================================
 * org.onap.aai
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

package org.onap.aai.serialization.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onap.aai.AAISetup;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.dbmap.DBConnectionType;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.introspection.Version;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TitanDBEngine;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;

import com.thinkaurelius.titan.core.TitanFactory;

public class DbSerializerTest extends AAISetup {

	//to use, set thrown.expect to whatever your test needs
	//this line establishes default of expecting no exception to be thrown
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	protected Graph graph;
	protected final EdgeRules rules = EdgeRules.getInstance();

	private final Version version = Version.getLatest();
	private final ModelType introspectorFactoryType = ModelType.MOXY;
	private final QueryStyle queryStyle = QueryStyle.TRAVERSAL;
	private final DBConnectionType type = DBConnectionType.REALTIME;
	private Loader loader;
	private TransactionalGraphEngine dbEngine;
	private TransactionalGraphEngine engine; //for tests that aren't mocking the engine
	private DBSerializer dbser;
	TransactionalGraphEngine spy;
	TransactionalGraphEngine.Admin adminSpy;
	
	@Before
	public void setup() throws Exception {
		graph = TitanFactory.build().set("storage.backend", "inmemory").open();
		loader = LoaderFactory.createLoaderForVersion(introspectorFactoryType, version);
		dbEngine = new TitanDBEngine(queryStyle, type, loader);
		spy = spy(dbEngine);
		adminSpy = spy(dbEngine.asAdmin());
	
		createGraph();

		engine = new TitanDBEngine(queryStyle, type, loader);
		dbser = new DBSerializer(version, engine, introspectorFactoryType, "AAI-TEST");
	}

	public void createGraph() throws AAIException {
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
		rules.addEdge(g, l3interipv4addresslist_1, subnet_2);
		rules.addEdge(g, l3interipv6addresslist_3, subnet_4);
		rules.addTreeEdge(g, subnet_5, l3network_6);

	}

	@After
	public void tearDown() throws Exception {
		graph.close();
	}

	@Test
	public void subnetDelwithInEdgesIpv4Test() throws AAIException {
		String expected_message = "Object is being reference by additional objects preventing it from being deleted. Please clean up references from the following types [l3-interface-ipv4-address-list]";

		/*
		 * This subnet has in-edges with l3-ipv4 and NOT ok to delete
		 */
		Vertex subnet = graph.traversal().V().has("aai-node-type", "subnet").has("subnet-id", "subnet-id-2").next();

		String exceptionMessage = testDelete(subnet);
		assertEquals(expected_message, exceptionMessage);

	}

	@Test
	public void subnetDelwithInEdgesIpv6Test() throws AAIException {
		String expected_message = "Object is being reference by additional objects preventing it from being deleted. Please clean up references from the following types [l3-interface-ipv6-address-list]";

		/*
		 * This subnet has in-edges with l3-ipv6 and NOT ok to delete
		 */
		Vertex subnet = graph.traversal().V().has("aai-node-type", "subnet").has("subnet-id", "subnet-id-4").next();
		String exceptionMessage = testDelete(subnet);
		assertEquals(expected_message, exceptionMessage);

	}

	@Test
	public void subnetDelwithInEdgesL3network() throws AAIException {
		String expected_message = "";

		/*
		 * This subnet has in-edges with l3-network and ok to delete
		 */
		Vertex subnet = graph.traversal().V().has("aai-node-type", "subnet").has("subnet-id", "subnet-id-5").next();

		String exceptionMessage = testDelete(subnet);
		assertEquals(expected_message, exceptionMessage);

	}

	public String testDelete(Vertex v) throws AAIException {

		// Graph g_tx = graph.newTransaction();
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
		engine.rollback();
	}

	@Test
	public void touchStandardVertexPropertiesTest() throws AAIException, InterruptedException {
		engine.startTransaction();
		DBSerializer dbser2 = new DBSerializer(Version.getLatest(), engine, introspectorFactoryType, "AAI-TEST-2");

		Graph graph = TinkerGraph.open();
		Vertex vert = graph.addVertex("aai-node-type", "generic-vnf");

		dbser.touchStandardVertexProperties(vert, true);
		String resverStart = (String)vert.property(AAIProperties.RESOURCE_VERSION.toString()).value();
		String lastModTimeStart = (String)vert.property(AAIProperties.LAST_MOD_TS.toString()).value();

		Thread.sleep(10); //bc the resource version is set based on current time in milliseconds,
							//if this test runs through too fast the value may not change
							//causing the test to fail. sleeping ensures a different value

		dbser2.touchStandardVertexProperties(vert, false);
		assertFalse(resverStart.equals((String)vert.property(AAIProperties.RESOURCE_VERSION.toString()).value()));
		assertFalse(lastModTimeStart.equals((String)vert.property(AAIProperties.LAST_MOD_TS.toString()).value()));
		assertEquals("AAI-TEST-2", (String)vert.property(AAIProperties.LAST_MOD_SOURCE_OF_TRUTH.toString()).value());
		engine.rollback();
	}

	@Test
	public void verifyResourceVersion_SunnyDayTest() throws AAIException {
		engine.startTransaction();

		assertTrue(dbser.verifyResourceVersion("delete", "vnfc", "abc", "abc", "vnfcs/vnfc/vnfcId"));
		engine.rollback();
	}

	@Test
	public void verifyResourceVersion_CreateWithRVTest() throws AAIException {
		engine.startTransaction();

		thrown.expect(AAIException.class);
		thrown.expectMessage("resource-version passed for create of generic-vnfs/generic-vnf/myid");
		try {
			dbser.verifyResourceVersion("create", "generic-vnf", null, "old-res-ver", "generic-vnfs/generic-vnf/myid");
		} finally {
			engine.rollback();
		}
	}

	@Test
	public void verifyResourceVersion_MissingRVTest() throws AAIException {
		engine.startTransaction();

		thrown.expect(AAIException.class);
		thrown.expectMessage("resource-version not passed for update of generic-vnfs/generic-vnf/myid");
		try {
			dbser.verifyResourceVersion("update", "generic-vnf", "current-res-ver", null, "generic-vnfs/generic-vnf/myid");
		} finally {
			engine.rollback();
		}
	}

	@Test
	public void verifyResourceVersion_MismatchRVTest() throws AAIException {
		engine.startTransaction();

		thrown.expect(AAIException.class);
		thrown.expectMessage("resource-version MISMATCH for update of generic-vnfs/generic-vnf/myid");
		try {
			dbser.verifyResourceVersion("update", "generic-vnf", "current-res-ver", "old-res-ver", "generic-vnfs/generic-vnf/myid");
		} finally {
			engine.rollback();
		}
	}

	@Test
	public void trimClassNameTest() throws AAIException {
		assertEquals("GenericVnf", dbser.trimClassName("GenericVnf"));
		assertEquals("GenericVnf", dbser.trimClassName("org.onap.aai.GenericVnf"));
	}

	@Test
	public void getURIForVertexTest() throws AAIException, URISyntaxException, UnsupportedEncodingException {
		engine.startTransaction();

		Vertex cr = engine.tx().addVertex("aai-node-type", "cloud-region", "cloud-owner", "me", "cloud-region-id", "123");
		Vertex ten = engine.tx().addVertex("aai-node-type", "tenant", "tenant-id", "453");
		EdgeRules rules = EdgeRules.getInstance();
		rules.addTreeEdge(engine.tx().traversal(), cr, ten);

		URI compare = new URI("/cloud-infrastructure/cloud-regions/cloud-region/me/123/tenants/tenant/453");
		assertEquals(compare, dbser.getURIForVertex(ten));

		cr.property("aai-node-type").remove();
		URI compareFailure = new URI("/unknown-uri");
		assertEquals(compareFailure, dbser.getURIForVertex(ten));
		engine.rollback();
	}

	@Test
	public void getVertexPropertiesTest() throws AAIException, UnsupportedEncodingException {
		engine.startTransaction();

		Vertex cr = engine.tx().addVertex("aai-node-type", "cloud-region", "cloud-owner", "me", "cloud-region-id", "123");

		Introspector crIntro = dbser.getVertexProperties(cr);
		assertEquals("cloud-region", crIntro.getDbName());
		assertEquals("me", crIntro.getValue("cloud-owner"));
		assertEquals("123", crIntro.getValue("cloud-region-id"));
		engine.rollback();
	}

	@Test
	public void setCachedURIsTest() throws AAIException, UnsupportedEncodingException, URISyntaxException {
		engine.startTransaction();

		Vertex cr = engine.tx().addVertex("aai-node-type", "cloud-region", "cloud-owner", "me", "cloud-region-id", "123");
		Vertex ten = engine.tx().addVertex("aai-node-type", "tenant", "tenant-id", "453");
		Vertex vs = engine.tx().addVertex("aai-node-type", "vserver", "vserver-id", "vs1",
												AAIProperties.AAI_URI.toString(),
													"/cloud-infrastructure/cloud-regions/cloud-region/me/123/tenants/tenant/453/vservers/vserver/vs1");
		EdgeRules rules = EdgeRules.getInstance();
		rules.addTreeEdge(engine.tx().traversal(), cr, ten);
		rules.addTreeEdge(engine.tx().traversal(), ten, vs);

		List<Vertex> vertices = new ArrayList<Vertex>(Arrays.asList(cr, ten, vs));
		Introspector crIn = dbser.getVertexProperties(cr);
		Introspector tenIn = dbser.getVertexProperties(ten);
		Introspector vsIn = dbser.getVertexProperties(vs);
		List<Introspector> intros = new ArrayList<Introspector>(Arrays.asList(crIn, tenIn, vsIn));

		dbser.setCachedURIs(vertices, intros);

		assertEquals("/cloud-infrastructure/cloud-regions/cloud-region/me/123",
					(String)cr.property(AAIProperties.AAI_URI.toString()).value());
		assertEquals("/cloud-infrastructure/cloud-regions/cloud-region/me/123/tenants/tenant/453",
				(String)ten.property(AAIProperties.AAI_URI.toString()).value());
		assertEquals("/cloud-infrastructure/cloud-regions/cloud-region/me/123/tenants/tenant/453/vservers/vserver/vs1",
				(String)vs.property(AAIProperties.AAI_URI.toString()).value());
		engine.rollback();
	}

	@Test
	public void getEdgeBetweenTest() throws AAIException {
		engine.startTransaction();

		Vertex cr = engine.tx().addVertex("aai-node-type", "cloud-region", "cloud-owner", "me", "cloud-region-id", "123");
		Vertex ten = engine.tx().addVertex("aai-node-type", "tenant", "tenant-id", "453");
		EdgeRules rules = EdgeRules.getInstance();
		rules.addTreeEdge(engine.tx().traversal(), cr, ten);

		Edge e = dbser.getEdgeBetween(EdgeType.TREE, ten, cr, null);
		assertEquals("has", e.label());
		engine.rollback();
	}

	@Test
	public void deleteEdgeTest() throws AAIException, UnsupportedEncodingException {
		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","myvnf");
		Vertex vnfc = engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name");
		EdgeRules rules = EdgeRules.getInstance();
		rules.addEdge(engine.tx().traversal(), gvnf, vnfc);

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
		engine.rollback();
	}

	@Test
	public void createEdgeTest() throws AAIException, UnsupportedEncodingException {
		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","myvnf");
		Vertex vnfc = engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name");

		//sunny day case
		Introspector relData = loader.introspectorFromName("relationship-data");
		relData.setValue("relationship-key", "vnfc.vnfc-name");
		relData.setValue("relationship-value", "a-name");
		Introspector relationship = loader.introspectorFromName("relationship");
		relationship.setValue("related-to", "vnfc");
		relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");
		relationship.setValue("relationship-data",relData);

		assertTrue(dbser.createEdge(relationship, gvnf));
		assertTrue(engine.tx().traversal().V(gvnf).both("uses").hasNext());
		assertTrue(engine.tx().traversal().V(vnfc).both("uses").hasNext());
		engine.rollback();
	}
	
	@Test
	public void createCousinEdgeThatShouldBeTreeTest() throws AAIException, UnsupportedEncodingException, URISyntaxException {
		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","myvnf");
		Vertex vf = engine.tx().addVertex("aai-node-type","vf-module","vf-module-id","vf-id");

		EdgeRules.getInstance().addTreeEdge(engine.tx().traversal(), gvnf, vf);
		
		Introspector relationship = loader.introspectorFromName("relationship");
		relationship.setValue("related-to", "vf-module");
		relationship.setValue("related-link", dbser.getURIForVertex(vf).toString());
		//relationship.setValue("relationship-label", "");
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
		finally {
			engine.rollback();
		}
	}
	
	@Test
	public void createEdgeNodeDoesNotExistExceptionTest() throws AAIException, UnsupportedEncodingException {
		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","myvnf");

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
		try {
			dbser.createEdge(relationship, gvnf);
		} finally {
			engine.rollback();
		}
	}

	@Test
	public void serializeSingleVertexTopLevelTest() throws AAIException, UnsupportedEncodingException {
		engine.startTransaction();

		Introspector gvnf = loader.introspectorFromName("generic-vnf");
		Vertex gvnfVert = dbser.createNewVertex(gvnf);

		gvnf.setValue("vnf-id", "myvnf");
		dbser.serializeSingleVertex(gvnfVert, gvnf, "test");
		assertTrue(engine.tx().traversal().V().has("aai-node-type","generic-vnf").has("vnf-id","myvnf").hasNext());
		engine.rollback();
	}

	@Test
	public void serializeSingleVertexChildTest() throws AAIException, UnsupportedEncodingException {
		engine.startTransaction();

		Vertex cr = engine.tx().addVertex("aai-node-type", "cloud-region", "cloud-owner", "me", "cloud-region-id", "123");
		Introspector tenIn = loader.introspectorFromName("tenant");
		Vertex ten = dbser.createNewVertex(tenIn);
		EdgeRules rules = EdgeRules.getInstance();
		rules.addTreeEdge(engine.tx().traversal(), cr, ten);

		tenIn.setValue("tenant-id", "453");
		tenIn.setValue("tenant-name", "mytenant");

		dbser.serializeSingleVertex(ten, tenIn, "test");

		assertTrue(engine.tx().traversal().V().has("aai-node-type","tenant").has("tenant-id","453").has("tenant-name","mytenant").hasNext());
		engine.rollback();
	}
	
	
	@Test
	public void getVertexPropertiesRelationshipHasLabelTest() throws AAIException, UnsupportedEncodingException {
		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","vnf-123");
		Vertex vnfc = engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","vnfc-123");
		EdgeRules rules = EdgeRules.getInstance();
		rules.addEdge(engine.tx().traversal(), gvnf, vnfc);
		
		Introspector obj = loader.introspectorFromName("generic-vnf");
		obj = this.dbser.dbToObject(Arrays.asList(gvnf), obj, AAIProperties.MAXIMUM_DEPTH, false, "false");
		
		assertEquals("edge label between generic-vnf and vnfs is uses", "uses", obj.getWrappedValue("relationship-list").getWrappedListValue("relationship").get(0).getValue("relationship-label"));
		
		engine.rollback();
	}
	
	@Test
	public void getVertexPropertiesRelationshipOldVersionNoEdgeLabelTest() throws AAIException, UnsupportedEncodingException {

		Version version = Version.v11;
		DBSerializer dbser = new DBSerializer(version, engine, introspectorFactoryType, "AAI-TEST");
		Loader loader = LoaderFactory.createLoaderForVersion(introspectorFactoryType, version);

		engine.startTransaction();
		
		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","vnf-123");
		Vertex vnfc = engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","vnfc-123");
		EdgeRules rules = EdgeRules.getInstance();
		rules.addEdge(engine.tx().traversal(), gvnf, vnfc);

		Introspector obj = loader.introspectorFromName("generic-vnf");
		obj = dbser.dbToObject(Arrays.asList(gvnf), obj, AAIProperties.MAXIMUM_DEPTH, false, "false");
		
		assertEquals("Relationship does not contain edge-property", false, obj.getWrappedValue("relationship-list").getWrappedListValue("relationship").get(0).hasProperty("relationship-label"));
		
		engine.rollback();
	}
	
	@Test
	public void createEdgeWithValidLabelTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		EdgeRules ers = EdgeRules.getInstance("/dbedgerules/DbEdgeRules_test.json");
		
		DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules(ers);
		
		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","myvnf");
		Vertex vnfc = engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name");

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
		engine.rollback();
	}
	
	@Test
	public void createEdgeWithInvalidLabelTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","myvnf");
		engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name");

		Introspector relData = loader.introspectorFromName("relationship-data");
		relData.setValue("relationship-key", "vnfc.vnfc-name");
		relData.setValue("relationship-value", "a-name");
		Introspector relationship = loader.introspectorFromName("relationship");
		relationship.setValue("related-to", "vnfc");
		relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");
		relationship.setValue("relationship-data",relData);
		relationship.setValue("relationship-label", "NA");

		thrown.expect(AAIException.class);
		thrown.expectMessage("no COUSIN edge rule between generic-vnf and vnfc with label NA");
		try {
			dbser.createEdge(relationship, gvnf);
		} finally {
			engine.rollback();
		}
	}
	
	@Test
	public void createEdgeWithValidLabelWhenSameEdgeExistsTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		EdgeRules ers = EdgeRules.getInstance("/dbedgerules/DbEdgeRules_test.json");
		
		DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules(ers);
		
		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","myvnf");
		Vertex vnfc = engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name");
		ers.addEdge(graph.traversal(), gvnf, vnfc, "re-uses");

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
		engine.rollback();
	}
	
	@Test
	public void createEdgeWithValidLabelWhenDiffEdgeExistsTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		EdgeRules ers = EdgeRules.getInstance("/dbedgerules/DbEdgeRules_test.json");
		DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules(ers);
		
		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","myvnf");
		Vertex vnfc = engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name");
		ers.addEdge(graph.traversal(), gvnf, vnfc, "uses");

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
		engine.rollback();
	}
	
	@Test
	public void createEdgeWithNoLabelTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		EdgeRules ers = EdgeRules.getInstance("/dbedgerules/DbEdgeRules_test.json");
		DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules(ers);
		
		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","myvnf");
		Vertex vnfc = engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name");

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

		engine.rollback();
	}
	
	@Test
	public void deleteEdgeWithNoLabelWhenMultipleExistsTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		EdgeRules ers = EdgeRules.getInstance("/dbedgerules/DbEdgeRules_test.json");
		
		DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules(ers);
		
		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","myvnf");
		Vertex vnfc = engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name");
		ers.addEdge(graph.traversal(), gvnf, vnfc, "uses");
		ers.addEdge(graph.traversal(), gvnf, vnfc, "re-uses");
		ers.addEdge(graph.traversal(), gvnf, vnfc, "over-uses");

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
		engine.rollback();
	}
	
	@Test
	public void deleteEdgeWithValidLabelWhenMultipleExistsTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		EdgeRules ers = EdgeRules.getInstance("/dbedgerules/DbEdgeRules_test.json");
		DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules(ers);
		
		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","myvnf");
		Vertex vnfc = engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name");
		ers.addEdge(graph.traversal(), gvnf, vnfc, "uses");
		ers.addEdge(graph.traversal(), gvnf, vnfc, "re-uses");
		ers.addEdge(graph.traversal(), gvnf, vnfc, "over-uses");

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
		engine.rollback();
	}
	
	@Test
	public void deleteEdgeWithValidInvalidLabelWhenMultipleExistsTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		EdgeRules ers = EdgeRules.getInstance("/dbedgerules/DbEdgeRules_test.json");
		DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules(ers);
		
		engine.startTransaction();

		Vertex gvnf = engine.tx().addVertex("aai-node-type","generic-vnf","vnf-id","myvnf");
		Vertex vnfc = engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name");
		ers.addEdge(graph.traversal(), gvnf, vnfc, "uses");
		ers.addEdge(graph.traversal(), gvnf, vnfc, "re-uses");
		ers.addEdge(graph.traversal(), gvnf, vnfc, "over-uses");

		Introspector relData = loader.introspectorFromName("relationship-data");
		relData.setValue("relationship-key", "vnfc.vnfc-name");
		relData.setValue("relationship-value", "a-name");
		Introspector relationship = loader.introspectorFromName("relationship");
		relationship.setValue("related-to", "vnfc");
		relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");
		relationship.setValue("relationship-data",relData);
		relationship.setValue("relationship-label", "NA");
		
		thrown.expect(AAIException.class);
		thrown.expectMessage("no COUSIN edge rule between generic-vnf and vnfc with label NA");
		try {
			localDbser.deleteEdge(relationship, gvnf);
		} finally {
			engine.rollback();
		}
	}
	
	@Test
	public void serializeToDbWithLabelTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, URISyntaxException {
		
		EdgeRules ers = EdgeRules.getInstance("/dbedgerules/DbEdgeRules_test.json");
		DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules(ers);
		
		engine.startTransaction();
		
		engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name");

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
		engine.rollback();
	}
	
	@Test
	public void serializeToDbWithoutLabelTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, URISyntaxException {
		
		EdgeRules ers = EdgeRules.getInstance("/dbedgerules/DbEdgeRules_test.json");
		DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules(ers);
		
		engine.startTransaction();
		
		engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name");

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
		engine.rollback();
	}
	
	@Test
	public void serializeToDbWithInvalidLabelTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, URISyntaxException {
		
		EdgeRules ers = EdgeRules.getInstance("/dbedgerules/DbEdgeRules_test.json");
		DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules(ers);
		
		engine.startTransaction();
		
		engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name");

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
		try {
			localDbser.serializeToDb(gvnfObj, gvnf, uriQuery, null, "test");
		} finally {
			engine.rollback();
		}
		engine.rollback();
	}
	
	@Test
	public void serializeToDbWithLabelAndEdgeExistsTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, URISyntaxException {
		
		EdgeRules ers = EdgeRules.getInstance("/dbedgerules/DbEdgeRules_test.json");
		DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules(ers);
		
		engine.startTransaction();
		engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name");
		
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
		engine.rollback();
	}
	
	@Test
	public void serializeToDbWithLabelDroppingRelationshipTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, URISyntaxException {
		
		EdgeRules ers = EdgeRules.getInstance("/dbedgerules/DbEdgeRules_test.json");
		DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules(ers);
		
		engine.startTransaction();
		engine.tx().addVertex("aai-node-type","vnfc","vnfc-name","a-name");
		
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
		engine.rollback();
	}

	private DBSerializer getDBSerializerWithSpecificEdgeRules(EdgeRules ers)
			throws NoSuchFieldException, AAIException, IllegalAccessException {
		// reflection to set the edge rules to the test one for DBSerializer
		Field reader = DBSerializer.class.getDeclaredField("edgeRules");
		reader.setAccessible(true);
		DBSerializer localDbser = new DBSerializer(Version.getLatest(), engine, introspectorFactoryType, "AAI-TEST");
		reader.set(localDbser, ers);
		return localDbser;
	}

}