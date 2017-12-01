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
package org.onap.aai.introspection.sideeffect;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import org.apache.commons.io.IOUtils;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.dbmap.DBConnectionType;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.*;
import org.onap.aai.introspection.sideeffect.exceptions.AAIMissingRequiredPropertyException;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.db.EdgeProperty;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TitanDBEngine;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class DataCopyTest {

	private static TitanGraph graph;
	private final static Version version = Version.getLatest();
	private final static ModelType introspectorFactoryType = ModelType.MOXY;
	private final static QueryStyle queryStyle = QueryStyle.TRAVERSAL;
	private final static DBConnectionType type = DBConnectionType.REALTIME;
	private static Loader loader;
	private static TransactionalGraphEngine dbEngine;
	@Mock private Vertex self;
	@Mock private VertexProperty<String> prop;
	@Mock private QueryParser uriQuery;
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
		
		graph.traversal().addV("aai-node-type", "model", "model-invariant-id", "key1").as("v1")
		.addV("aai-node-type", "model-ver", "model-ver", "myValue", "model-version-id", "key2", "model-version", "testValue").addInE("has", "v1", EdgeProperty.CONTAINS.toString(), true)
		.addV("aai-node-type", "model", "model-invariant-id", "key3").as("v2")
		.addV("aai-node-type", "model-ver", "model-ver", "myValue", "model-version-id", "key4").addInE("has", "v2", EdgeProperty.CONTAINS.toString(), true)
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
	public void runPopulatePersonaModelVer() throws URISyntaxException, AAIException, UnsupportedEncodingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, InstantiationException, NoSuchMethodException, MalformedURLException {
		
		final Loader loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, Version.getLatest());
		final Introspector obj = loader.introspectorFromName("test-object");
		obj.setValue("vnf-id", "myId");
		obj.setValue("model-invariant-id", "key1");
		obj.setValue("model-version-id", "key2");
		TransactionalGraphEngine spy = spy(dbEngine);
		TransactionalGraphEngine.Admin adminSpy = spy(dbEngine.asAdmin());
		Graph g = graph.newTransaction();
		GraphTraversalSource traversal = g.traversal();
		when(spy.asAdmin()).thenReturn(adminSpy);
		when(adminSpy.getTraversalSource()).thenReturn(traversal);
		when(self.<String>property(AAIProperties.AAI_URI)).thenReturn(prop);
		when(prop.orElse(null)).thenReturn(obj.getURI());
		DBSerializer serializer = new DBSerializer(version, spy, introspectorFactoryType, "AAI_TEST");
		SideEffectRunner runner = new SideEffectRunner
				.Builder(spy, serializer).addSideEffect(DataCopy.class).build();
		
		runner.execute(obj, self);

		assertEquals("value populated", "testValue", obj.getValue("persona-model-ver"));
		
		g.tx().rollback();
		
		
	}
	
	@Test
	public void runPopulateModelVersionId() throws URISyntaxException, AAIException, UnsupportedEncodingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, InstantiationException, NoSuchMethodException, MalformedURLException {
		
		final Loader loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, Version.v9);
		final Introspector obj = loader.introspectorFromName("test-object");
		obj.setValue("vnf-id", "myId");
		obj.setValue("persona-model-id", "key1");
		obj.setValue("persona-model-version", "testValue");
		TransactionalGraphEngine spy = spy(dbEngine);
		TransactionalGraphEngine.Admin adminSpy = spy(dbEngine.asAdmin());
		Graph g = graph.newTransaction();
		GraphTraversalSource traversal = g.traversal();
		when(spy.asAdmin()).thenReturn(adminSpy);
		when(adminSpy.getTraversalSource()).thenReturn(traversal);
		when(self.<String>property(AAIProperties.AAI_URI)).thenReturn(prop);
		when(prop.orElse(null)).thenReturn(obj.getURI());
		DBSerializer serializer = new DBSerializer(version, spy, introspectorFactoryType, "AAI_TEST");
		SideEffectRunner runner = new SideEffectRunner
				.Builder(spy, serializer).addSideEffect(DataCopy.class).build();
		
		runner.execute(obj, self);
		
		assertEquals("value populated", "key2", obj.getValue("model-version-id"));
		
		g.tx().rollback();
	}
	
	@Test
	public void verifyNestedSideEffect() throws URISyntaxException, AAIException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, InstantiationException, NoSuchMethodException, IOException {
		
		final Loader loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, Version.getLatest());
		final Introspector obj = loader.unmarshal("customer", this.getJsonString("nested-case.json"));
		//System.out.println(obj.marshal(true));
		TransactionalGraphEngine spy = spy(dbEngine);
		TransactionalGraphEngine.Admin adminSpy = spy(dbEngine.asAdmin());
		Graph g = graph.newTransaction();
		GraphTraversalSource traversal = g.traversal();
		when(spy.tx()).thenReturn(g);
		when(spy.asAdmin()).thenReturn(adminSpy);
		when(adminSpy.getTraversalSource()).thenReturn(traversal);
		when(self.<String>property(AAIProperties.AAI_URI)).thenReturn(prop);
		when(prop.orElse(null)).thenReturn(obj.getURI());
		when(uriQuery.isDependent()).thenReturn(false);
		DBSerializer serializer = new DBSerializer(version, spy, introspectorFactoryType, "AAI_TEST");
		Vertex v= serializer.createNewVertex(obj);
		serializer.serializeToDb(obj, v, uriQuery, obj.getURI(), "test");
		
		assertEquals("nested value populated", "testValue", g.traversal().V().has("service-instance-id", "nested-instance-key").next().property("persona-model-version").orElse(""));

		g.tx().rollback();

	}
	
	@Test
	public void expectedMissingPropertyExceptionInURI() throws AAIException, UnsupportedEncodingException {
		
		final Loader loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, Version.getLatest());
		final Introspector obj = loader.introspectorFromName("test-object");
		obj.setValue("vnf-id", "myId");
		obj.setValue("model-invariant-id", "key1");

		TransactionalGraphEngine spy = spy(dbEngine);
		TransactionalGraphEngine.Admin adminSpy = spy(dbEngine.asAdmin());
		Graph g = graph.newTransaction();
		GraphTraversalSource traversal = g.traversal();
		when(spy.asAdmin()).thenReturn(adminSpy);
		when(adminSpy.getTraversalSource()).thenReturn(traversal);
		when(self.<String>property(AAIProperties.AAI_URI)).thenReturn(prop);
		when(prop.orElse(null)).thenReturn(obj.getURI());
		DBSerializer serializer = new DBSerializer(version, spy, introspectorFactoryType, "AAI_TEST");
		SideEffectRunner runner = new SideEffectRunner
				.Builder(spy, serializer).addSideEffect(DataCopy.class).build();
		
		thrown.expect(AAIMissingRequiredPropertyException.class);
		runner.execute(obj, self);
	}
	
	@Test
	public void expectedMissingPropertyExceptionForResultingObject() throws AAIException, UnsupportedEncodingException {
		final Loader loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, Version.getLatest());
		final Introspector obj = loader.introspectorFromName("test-object");
		obj.setValue("vnf-id", "myId");
		obj.setValue("model-invariant-id", "key3");
		obj.setValue("model-version-id", "key4");

		TransactionalGraphEngine spy = spy(dbEngine);
		TransactionalGraphEngine.Admin adminSpy = spy(dbEngine.asAdmin());
		Graph g = graph.newTransaction();
		GraphTraversalSource traversal = g.traversal();
		when(spy.asAdmin()).thenReturn(adminSpy);
		when(adminSpy.getTraversalSource()).thenReturn(traversal);
		when(self.<String>property(AAIProperties.AAI_URI)).thenReturn(prop);
		when(prop.orElse(null)).thenReturn(obj.getURI());
		DBSerializer serializer = new DBSerializer(version, spy, introspectorFactoryType, "AAI_TEST");
		SideEffectRunner runner = new SideEffectRunner
				.Builder(spy, serializer).addSideEffect(DataCopy.class).build();
		
		thrown.expect(AAIMissingRequiredPropertyException.class);
		runner.execute(obj, self);
	}
	
	@Test
	public void expectNoProcessingWithNoProperties() throws AAIException, UnsupportedEncodingException {
		final Loader loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, Version.getLatest());
		final Introspector obj = loader.introspectorFromName("test-object");
		obj.setValue("vnf-id", "myId");

		TransactionalGraphEngine spy = spy(dbEngine);
		TransactionalGraphEngine.Admin adminSpy = spy(dbEngine.asAdmin());
		Graph g = graph.newTransaction();
		GraphTraversalSource traversal = g.traversal();
		when(spy.asAdmin()).thenReturn(adminSpy);
		when(adminSpy.getTraversalSource()).thenReturn(traversal);
		when(self.<String>property(AAIProperties.AAI_URI)).thenReturn(prop);
		when(prop.orElse(null)).thenReturn(obj.getURI());
		DBSerializer serializer = new DBSerializer(version, spy, introspectorFactoryType, "AAI_TEST");
		SideEffectRunner runner = new SideEffectRunner
				.Builder(spy, serializer).addSideEffect(DataCopy.class).build();
		
		runner.execute(obj, self);
		
		assertEquals("no model-version-id", true, obj.getValue("model-version-id") == null);
		assertEquals("no model-invariant-id", true, obj.getValue("model-invariant-id") == null);
		
	}
	
	private String getJsonString(String filename) throws IOException {
		
		
		FileInputStream is = new FileInputStream("src/test/resources/bundleconfig-local/etc/oxm/sideeffect/" + filename);
		String s =  IOUtils.toString(is, "UTF-8"); 
		IOUtils.closeQuietly(is);
		
		return s;
	}
}
