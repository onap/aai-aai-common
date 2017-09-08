/*-
 * ============LICENSE_START=======================================================
 * org.openecomp.aai
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

package org.openecomp.aai.introspection.sideeffect;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openecomp.aai.db.props.AAIProperties;
import org.openecomp.aai.dbmap.DBConnectionType;
import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.*;
import org.openecomp.aai.parsers.query.QueryParser;
import org.openecomp.aai.serialization.db.DBSerializer;
import org.openecomp.aai.serialization.db.EdgeProperty;
import org.openecomp.aai.serialization.engines.QueryStyle;
import org.openecomp.aai.serialization.engines.TitanDBEngine;
import org.openecomp.aai.serialization.engines.TransactionalGraphEngine;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class DataLinkTest {

	private static TitanGraph graph;
	private final static Version version = Version.getLatest();
	private final static ModelType introspectorFactoryType = ModelType.MOXY;
	private final static QueryStyle queryStyle = QueryStyle.TRAVERSAL;
	private final static DBConnectionType type = DBConnectionType.REALTIME;
	private static Loader loader;
	private static TransactionalGraphEngine dbEngine;
	@Mock private QueryParser parser;
	@Mock private Vertex self;
	@Mock private VertexProperty<String> prop;
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	
	@BeforeClass
	public static void setup() throws NoSuchFieldException, SecurityException, Exception {
		graph = TitanFactory.build().set("storage.backend","inmemory").open();
		System.setProperty("AJSC_HOME", ".");
		System.setProperty("BUNDLECONFIG_DIR", "src/test/resources/bundleconfig-local");
		loader = LoaderFactory.createLoaderForVersion(introspectorFactoryType, version);
		dbEngine = new TitanDBEngine(
				queryStyle,
				type,
				loader);
		
		graph.traversal().addV("aai-node-type", "vpn-binding", "vpn-id", "addKey").as("v1")
		.addV("aai-node-type", "vpn-binding", "vpn-id", "modifyKey").as("v2")
		.addV("aai-node-type", "route-target", "global-route-target", "modifyTargetKey", "route-target-role", "modifyRoleKey", "linked", true).addInE("has", "v2", EdgeProperty.CONTAINS.toString(), true)
		.addV("aai-node-type", "vpn-binding", "vpn-id", "deleteKey").as("v3")
		.addV("aai-node-type", "route-target", "global-route-target", "deleteTargetKey", "route-target-role", "deleteRoleKey", "linked", true).addInE("has", "v3", EdgeProperty.CONTAINS.toString(), true)
		.addV("aai-node-type", "vpn-binding", "vpn-id", "getKey").as("v4")
		.addV("aai-node-type", "route-target", "global-route-target", "getTargetKey", "route-target-role", "getRoleKey", "linked", true).addInE("has", "v4", EdgeProperty.CONTAINS.toString(), true)
		.addV("aai-node-type", "vpn-binding", "vpn-id", "getKeyNoLink").as("v5")
		.addV("aai-node-type", "route-target", "global-route-target", "getTargetKeyNoLink", "route-target-role", "getRoleKeyNoLink").addInE("has", "v5", EdgeProperty.CONTAINS.toString(), true)
		.next();
		graph.tx().commit();
	}
	
	@AfterClass
	public static void tearDown() {
		graph.tx().rollback();
		graph.close();
	}
	
	@Before
	public void initMock() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void verifyCreationOfVertex() throws URISyntaxException, AAIException, UnsupportedEncodingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, InstantiationException, NoSuchMethodException, MalformedURLException {
		
		final Loader loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, Version.v9);
		final Introspector obj = loader.introspectorFromName("vpn-binding");
		obj.setValue("vpn-id", "addKey");
		obj.setValue("global-route-target", "key1");
		obj.setValue("route-target-role", "key2");
		TransactionalGraphEngine spy = spy(dbEngine);
		TransactionalGraphEngine.Admin adminSpy = spy(dbEngine.asAdmin());
		Graph g = graph.newTransaction();
		GraphTraversalSource traversal = g.traversal();
		when(spy.asAdmin()).thenReturn(adminSpy);
		when(adminSpy.getTraversalSource()).thenReturn(traversal);
		when(spy.tx()).thenReturn(g);
		when(self.<String>property(AAIProperties.AAI_URI)).thenReturn(prop);
		when(prop.orElse(null)).thenReturn(obj.getURI());
		DBSerializer serializer = new DBSerializer(version, spy, introspectorFactoryType, "AAI_TEST");
		SideEffectRunner runner = new SideEffectRunner
				.Builder(spy, serializer).addSideEffect(DataLinkWriter.class).build();
		
		runner.execute(obj, self);

		assertEquals("route-target vertex found", true, traversal.V()
				.has(AAIProperties.NODE_TYPE, "route-target").has("global-route-target", "key1").has("route-target-role", "key2").has("linked", true).hasNext());
		
		g.tx().rollback();
		
	}
	
	@Test
	public void verifyModificationOfVertex() throws URISyntaxException, AAIException, UnsupportedEncodingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, InstantiationException, NoSuchMethodException, MalformedURLException {
		
		final Loader loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, Version.v9);
		final Introspector obj = loader.introspectorFromName("vpn-binding");
		obj.setValue("vpn-id", "modifyKey");
		obj.setValue("global-route-target", "modifyTargetKey2");
		obj.setValue("route-target-role", "modifyRoleKey2");
		TransactionalGraphEngine spy = spy(dbEngine);
		TransactionalGraphEngine.Admin adminSpy = spy(dbEngine.asAdmin());
		Graph g = graph.newTransaction();
		GraphTraversalSource traversal = g.traversal();
		when(spy.asAdmin()).thenReturn(adminSpy);
		when(adminSpy.getTraversalSource()).thenReturn(traversal);
		when(spy.tx()).thenReturn(g);
		when(self.<String>property(AAIProperties.AAI_URI)).thenReturn(prop);
		when(prop.orElse(null)).thenReturn(obj.getURI());
		DBSerializer serializer = new DBSerializer(version, spy, introspectorFactoryType, "AAI_TEST");
		SideEffectRunner runner = new SideEffectRunner
				.Builder(spy, serializer).addSideEffect(DataLinkWriter.class).build();
		
		runner.execute(obj, self);

		assertEquals("route-target vertex found", true, traversal.V()
				.has(AAIProperties.NODE_TYPE, "route-target").has("global-route-target", "modifyTargetKey2").has("route-target-role", "modifyRoleKey2").has("linked", true).hasNext());
		assertEquals("previous link removed", true, traversal.V()
				.has(AAIProperties.NODE_TYPE, "route-target").has("global-route-target", "modifyTargetKey").has("route-target-role", "modifyRoleKey").hasNot("linked").hasNext());
		g.tx().rollback();
		
	}
	
	@Test
	public void verifyDeleteOfVertex() throws URISyntaxException, AAIException, UnsupportedEncodingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, InstantiationException, NoSuchMethodException, MalformedURLException {
		
		final Loader loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, Version.v9);
		final Introspector obj = loader.introspectorFromName("vpn-binding");
		obj.setValue("vpn-id", "deleteKey");
		TransactionalGraphEngine spy = spy(dbEngine);
		TransactionalGraphEngine.Admin adminSpy = spy(dbEngine.asAdmin());
		Graph g = graph.newTransaction();
		GraphTraversalSource traversal = g.traversal();
		when(spy.asAdmin()).thenReturn(adminSpy);
		when(adminSpy.getTraversalSource()).thenReturn(traversal);
		when(adminSpy.getReadOnlyTraversalSource()).thenReturn(traversal);
		when(spy.tx()).thenReturn(g);
		when(self.<String>property(AAIProperties.AAI_URI)).thenReturn(prop);
		when(prop.orElse(null)).thenReturn(obj.getURI());
		DBSerializer serializer = new DBSerializer(version, spy, introspectorFactoryType, "AAI_TEST");
		SideEffectRunner runner = new SideEffectRunner
				.Builder(spy, serializer).addSideEffect(DataLinkWriter.class).build();
		
		runner.execute(obj, self);

		assertEquals("route-target vertex not found", false, traversal.V()
				.has(AAIProperties.NODE_TYPE, "route-target").has("global-route-target", "deleteTargetKey").has("route-target-role", "deleteRoleKey").has("linked", true).hasNext());
		g.tx().rollback();
		
	}
	
	@Test
	public void verifyPropertyPopulation() throws URISyntaxException, AAIException, UnsupportedEncodingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, InstantiationException, NoSuchMethodException, MalformedURLException {
		
		final Loader loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, Version.v9);
		final Introspector obj = loader.introspectorFromName("vpn-binding");
		obj.setValue("vpn-id", "getKey");
		TransactionalGraphEngine spy = spy(dbEngine);
		TransactionalGraphEngine.Admin adminSpy = spy(dbEngine.asAdmin());
		Graph g = graph.newTransaction();
		GraphTraversalSource traversal = g.traversal();
		when(spy.asAdmin()).thenReturn(adminSpy);
		when(adminSpy.getTraversalSource()).thenReturn(traversal);
		when(spy.tx()).thenReturn(g);
		when(self.<String>property(AAIProperties.AAI_URI)).thenReturn(prop);
		when(prop.orElse(null)).thenReturn(obj.getURI());
		DBSerializer serializer = new DBSerializer(version, spy, introspectorFactoryType, "AAI_TEST");
		SideEffectRunner runner = new SideEffectRunner
				.Builder(spy, serializer).addSideEffect(DataLinkReader.class).build();
		
		runner.execute(obj, self);

		assertEquals("both properties have been populated in target object", true, obj.getValue("global-route-target").equals("getTargetKey") && obj.getValue("route-target-role").equals("getRoleKey"));
		g.tx().rollback();
		
	}
	
	@Test
	public void verifyPropertyPopulationWithV10OnlyPut() throws URISyntaxException, AAIException, UnsupportedEncodingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, InstantiationException, NoSuchMethodException, MalformedURLException {
		final Introspector obj = loader.introspectorFromName("vpn-binding");
		obj.setValue("vpn-id", "getKeyNoLink");
		final Introspector routeTargets = loader.introspectorFromName("route-targets");
		obj.setValue("route-targets", routeTargets.getUnderlyingObject());
		List<Object> targets = routeTargets.getValue("route-target");
		final Introspector routeTargetOne = loader.introspectorFromName("route-target");
		routeTargetOne.setValue("global-route-target", "getTargetKeyNoLink");
		routeTargetOne.setValue("route-target-role", "getRoleKeyNoLink");
		targets.add(routeTargetOne.getUnderlyingObject());
		final Introspector routeTargetTwo = loader.introspectorFromName("route-target");
		routeTargetTwo.setValue("global-route-target", "getTargetKeyNoLink2");
		routeTargetTwo.setValue("route-target-role", "getRoleKeyNoLink2");
		targets.add(routeTargetTwo.getUnderlyingObject());
		TransactionalGraphEngine spy = spy(dbEngine);
		TransactionalGraphEngine.Admin adminSpy = spy(dbEngine.asAdmin());
		Graph g = graph.newTransaction();
		GraphTraversalSource traversal = g.traversal();
		when(spy.tx()).thenReturn(g);
		when(spy.asAdmin()).thenReturn(adminSpy);
		when(adminSpy.getTraversalSource()).thenReturn(traversal);
		when(spy.tx()).thenReturn(g);
		when(parser.isDependent()).thenReturn(false);
		when(self.<String>property(AAIProperties.AAI_URI)).thenReturn(prop);
		when(prop.orElse(null)).thenReturn(obj.getURI());
		DBSerializer serializer = new DBSerializer(version, spy, introspectorFactoryType, "AAI_TEST");
		Vertex v = serializer.createNewVertex(obj);
		serializer.serializeToDb(obj, v, parser, obj.getURI(), "testing");
		Vertex routeTargetOneV = traversal.V().has("global-route-target", "getTargetKeyNoLink").next();
		Vertex routeTargetTwoV = traversal.V().has("global-route-target", "getTargetKeyNoLink2").next();

		assertEquals("first route target put has linked", true, routeTargetOneV.property(AAIProperties.LINKED).orElse(false));
		assertEquals("second route target put does not have linked", false, routeTargetTwoV.property(AAIProperties.LINKED).orElse(false));

		g.tx().rollback();
		
	}
}
