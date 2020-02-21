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

package org.onap.aai.introspection.sideeffect;

import org.apache.commons.io.IOUtils;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.aai.AAISetup;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.edges.enums.EdgeProperty;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.introspection.sideeffect.exceptions.AAIMissingRequiredPropertyException;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.engines.JanusGraphDBEngine;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(value = Parameterized.class)

public class DataCopyTest extends AAISetup {

    private static JanusGraph graph;
    private final static ModelType introspectorFactoryType = ModelType.MOXY;
    private static Loader loader;
    private static TransactionalGraphEngine dbEngine;
    @Mock
    private Vertex self;
    @Mock
    private VertexProperty<String> prop;
    @Mock
    private QueryParser uriQuery;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Parameterized.Parameter
    public QueryStyle queryStyle;

    @Parameterized.Parameters(name = "QueryStyle.{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {{QueryStyle.TRAVERSAL}, {QueryStyle.TRAVERSAL_URI}});
    }

    @BeforeClass
    public static void setup() {
        graph = JanusGraphFactory.build().set("storage.backend", "inmemory").open();
        System.setProperty("AJSC_HOME", ".");
        System.setProperty("BUNDLECONFIG_DIR", "src/test/resources/bundleconfig-local");

        graph.traversal()
            .addV("model")
            .property("aai-node-type", "model")
            .property("model-invariant-id", "key1")
            .property(AAIProperties.AAI_URI,"/service-design-and-creation/models/model/key1")
            .as("v1")
            .addV("model-ver")
            .property("aai-node-type", "model-ver")
            .property("model-ver", "myValue")
            .property("model-version-id", "key2")
            .property("model-version", "testValue")
            .property(AAIProperties.AAI_URI, "/service-design-and-creation/models/model/key1/model-vers/model-ver/key2")
            .as("v2")
            .addE("org.onap.relationships.inventory.BelongsTo").to("v1").from("v2")
            .property(EdgeProperty.CONTAINS.toString(), true)
            .addV("model")
            .property("aai-node-type", "model")
            .property("model-invariant-id", "key3")
            .property(AAIProperties.AAI_URI, "/service-design-and-creation/models/model/key3")
            .as("v3")
            .addV()
            .property("aai-node-type", "model-ver")
            .property("model-ver", "myValue")
            .property("model-version-id", "key4")
            .property(AAIProperties.AAI_URI, "/service-design-and-creation/models/model/key3/model-vers/model-ver/key4")
            .as("v4")
            .addE("org.onap.relationships.inventory.BelongsTo").to("v3").from("v4")
            .property(EdgeProperty.CONTAINS.toString(), true)
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
        loader = loaderFactory.createLoaderForVersion(introspectorFactoryType, schemaVersions.getDefaultVersion());
        MockitoAnnotations.initMocks(this);
        dbEngine = new JanusGraphDBEngine(queryStyle, loader);
    }

    @Test
    public void runPopulatePersonaModelVer() throws AAIException, UnsupportedEncodingException,
            IllegalArgumentException, SecurityException {

        final Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDefaultVersion());
        final Introspector obj = loader.introspectorFromName("generic-vnf");
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
        DBSerializer serializer =
                new DBSerializer(schemaVersions.getDefaultVersion(), spy, introspectorFactoryType, "AAI_TEST");
        SideEffectRunner runner = new SideEffectRunner.Builder(spy, serializer).addSideEffect(DataCopy.class).build();

        runner.execute(obj, self);

        assertEquals("value populated", "testValue", obj.getValue("persona-model-version"));

        g.tx().rollback();

    }

    @Test
    public void verifyNestedSideEffect() throws AAIException, IllegalArgumentException, SecurityException, IOException {

        final Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDefaultVersion());
        final Introspector obj = loader.unmarshal("customer", this.getJsonString("nested-case.json"));
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
        DBSerializer serializer =
                new DBSerializer(schemaVersions.getDefaultVersion(), spy, introspectorFactoryType, "AAI_TEST");
        Vertex v = serializer.createNewVertex(obj);
        serializer.serializeToDb(obj, v, uriQuery, obj.getURI(), "test");

        assertEquals("nested value populated", "testValue", g.traversal().V()
                .has("service-instance-id", "nested-instance-key").next().property("persona-model-version").orElse(""));

        g.tx().rollback();

    }

    @Test
    public void expectedMissingPropertyExceptionInURI() throws AAIException, UnsupportedEncodingException {

        final Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDefaultVersion());
        final Introspector obj = loader.introspectorFromName("generic-vnf");
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
        DBSerializer serializer =
                new DBSerializer(schemaVersions.getDefaultVersion(), spy, introspectorFactoryType, "AAI_TEST");
        SideEffectRunner runner = new SideEffectRunner.Builder(spy, serializer).addSideEffect(DataCopy.class).build();

        thrown.expect(AAIMissingRequiredPropertyException.class);
        runner.execute(obj, self);
    }

    @Test
    public void expectedMissingPropertyExceptionForResultingObject() throws AAIException, UnsupportedEncodingException {
        final Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDefaultVersion());
        final Introspector obj = loader.introspectorFromName("generic-vnf");
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
        DBSerializer serializer =
                new DBSerializer(schemaVersions.getDefaultVersion(), spy, introspectorFactoryType, "AAI_TEST");
        SideEffectRunner runner = new SideEffectRunner.Builder(spy, serializer).addSideEffect(DataCopy.class).build();

        thrown.expect(AAIMissingRequiredPropertyException.class);
        runner.execute(obj, self);
    }

    @Test
    public void expectNoProcessingWithNoProperties() throws AAIException, UnsupportedEncodingException {
        final Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDefaultVersion());
        final Introspector obj = loader.introspectorFromName("generic-vnf");
        obj.setValue("vnf-id", "myId");

        TransactionalGraphEngine spy = spy(dbEngine);
        TransactionalGraphEngine.Admin adminSpy = spy(dbEngine.asAdmin());
        Graph g = graph.newTransaction();
        GraphTraversalSource traversal = g.traversal();
        when(spy.asAdmin()).thenReturn(adminSpy);
        when(adminSpy.getTraversalSource()).thenReturn(traversal);
        when(self.<String>property(AAIProperties.AAI_URI)).thenReturn(prop);
        when(prop.orElse(null)).thenReturn(obj.getURI());
        DBSerializer serializer =
                new DBSerializer(schemaVersions.getDefaultVersion(), spy, introspectorFactoryType, "AAI_TEST");
        SideEffectRunner runner = new SideEffectRunner.Builder(spy, serializer).addSideEffect(DataCopy.class).build();

        runner.execute(obj, self);

        assertNull("no model-version-id", obj.getValue("model-version-id"));
        assertNull("no model-invariant-id", obj.getValue("model-invariant-id"));

    }

    private String getJsonString(String filename) throws IOException {

        FileInputStream is = new FileInputStream("src/test/resources/oxm/sideeffect/" + filename);
        String s = IOUtils.toString(is, StandardCharsets.UTF_8);
        IOUtils.closeQuietly(is);

        return s;
    }
}
