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

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.janusgraph.core.Cardinality;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.aai.DataLinkSetup;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.edges.enums.EdgeProperty;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.engines.JanusGraphDBEngine;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(value = Parameterized.class)
public class DataLinkTest extends DataLinkSetup {

    private static JanusGraph graph;
    private final static ModelType introspectorFactoryType = ModelType.MOXY;
    private static Loader loader;
    private static TransactionalGraphEngine dbEngine;
    @Mock
    private QueryParser parser;
    @Mock
    private Vertex self;
    @Mock
    private VertexProperty<String> prop;
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
        JanusGraphManagement graphMgt = graph.openManagement();
        graphMgt.makePropertyKey(AAIProperties.CREATED_TS).dataType(Long.class).cardinality(Cardinality.SINGLE)
            .make();
        graphMgt.makePropertyKey(AAIProperties.LAST_MOD_TS).dataType(Long.class).cardinality(Cardinality.SINGLE)
            .make();
        graphMgt.commit();

        graph.traversal()
            .addV()
            .property("aai-node-type", "vpn-binding")
            .property("vpn-id", "addKey")
            .property(AAIProperties.AAI_URI, "/network/vpn-bindings/vpn-binding/addKey")
            .property(AAIProperties.AAI_UUID, UUID.randomUUID().toString())
            .property(AAIProperties.CREATED_TS, 123)
            .property(AAIProperties.SOURCE_OF_TRUTH, "sot")
            .property(AAIProperties.RESOURCE_VERSION, "123")
            .property(AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, "lmsot")
            .property(AAIProperties.LAST_MOD_TS, 333)
            .as("v1")
            .addV()
            .property("aai-node-type", "vpn-binding")
            .property("vpn-id", "modifyKey")
            .property(AAIProperties.AAI_URI, "/network/vpn-bindings/vpn-binding/modifyKey")
            .property(AAIProperties.AAI_UUID, UUID.randomUUID().toString())
            .property(AAIProperties.CREATED_TS, 123)
            .property(AAIProperties.SOURCE_OF_TRUTH, "sot")
            .property(AAIProperties.RESOURCE_VERSION, "123")
            .property(AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, "lmsot")
            .property(AAIProperties.LAST_MOD_TS, 333)
            .as("v2")
            .addV()
            .property("aai-node-type", "route-target")
            .property("global-route-target", "modifyTargetKey")
            .property("route-target-role", "modifyRoleKey")
            .property("linked", true)
            .property(AAIProperties.AAI_URI, "/network/vpn-bindings/vpn-binding/modifyKey/route-targets/route-target/modifyTargetKey/modifyRoleKey")
            .property(AAIProperties.AAI_UUID, UUID.randomUUID().toString())
            .property(AAIProperties.CREATED_TS, 123)
            .property(AAIProperties.SOURCE_OF_TRUTH, "sot")
            .property(AAIProperties.RESOURCE_VERSION, "123")
            .property(AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, "lmsot")
            .property(AAIProperties.LAST_MOD_TS, 333)
            .as("v3")
            .addE("org.onap.relationships.inventory.BelongsTo").to("v2").from("v3")
            .property(EdgeProperty.CONTAINS.toString(), true)
            .addV()
            .property("aai-node-type", "vpn-binding")
            .property("vpn-id", "deleteKey")
            .property(AAIProperties.AAI_URI, "/network/vpn-bindings/vpn-binding/deleteKey")
            .property(AAIProperties.AAI_UUID, UUID.randomUUID().toString())
            .property(AAIProperties.CREATED_TS, 123)
            .property(AAIProperties.SOURCE_OF_TRUTH, "sot")
            .property(AAIProperties.RESOURCE_VERSION, "123")
            .property(AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, "lmsot")
            .property(AAIProperties.LAST_MOD_TS, 333)
            .as("v4")
            .addV()
            .property("aai-node-type", "route-target")
            .property("global-route-target", "deleteTargetKey")
            .property("route-target-role", "deleteRoleKey")
            .property("linked", true)
            .property(AAIProperties.AAI_URI, "/network/vpn-bindings/vpn-binding/deleteKey/route-targets/route-target/deleteTargetKey/deleteRoleKey")
            .property(AAIProperties.AAI_UUID, UUID.randomUUID().toString())
            .property(AAIProperties.CREATED_TS, 123)
            .property(AAIProperties.SOURCE_OF_TRUTH, "sot")
            .property(AAIProperties.RESOURCE_VERSION, "123")
            .property(AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, "lmsot")
            .property(AAIProperties.LAST_MOD_TS, 333)
            .as("v5")
            .addE("org.onap.relationships.inventory.BelongsTo").to("v4").from("v5")
            .property(EdgeProperty.CONTAINS.toString(), true)
            .addV()
            .property("aai-node-type", "vpn-binding")
            .property("vpn-id", "getKey")
            .property(AAIProperties.AAI_URI, "/network/vpn-bindings/vpn-binding/getKey")
            .property(AAIProperties.AAI_UUID, UUID.randomUUID().toString())
            .property(AAIProperties.CREATED_TS, 123)
            .property(AAIProperties.SOURCE_OF_TRUTH, "sot")
            .property(AAIProperties.RESOURCE_VERSION, "123")
            .property(AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, "lmsot")
            .property(AAIProperties.LAST_MOD_TS, 333)
            .as("v6")
            .addV()
            .property("aai-node-type", "route-target")
            .property("global-route-target", "getTargetKey")
            .property("route-target-role", "getRoleKey")
            .property("linked", true)
            .property(AAIProperties.AAI_URI, "/network/vpn-bindings/vpn-binding/getKey/route-targets/route-target/getTargetKeyNoLink/getRoleKeyNoLink")
            .property(AAIProperties.AAI_UUID, UUID.randomUUID().toString())
            .property(AAIProperties.CREATED_TS, 123)
            .property(AAIProperties.SOURCE_OF_TRUTH, "sot")
            .property(AAIProperties.RESOURCE_VERSION, "123")
            .property(AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, "lmsot")
            .property(AAIProperties.LAST_MOD_TS, 333)
            .as("v7")
            .addE("org.onap.relationships.inventory.BelongsTo").to("v6").from("v7")
            .property(EdgeProperty.CONTAINS.toString(), true)
            .addV()
            .property("aai-node-type", "vpn-binding")
            .property("vpn-id", "getKeyNoLink")
            .property(AAIProperties.AAI_URI, "/network/vpn-bindings/vpn-binding/getKeyNoLink")
            .property(AAIProperties.AAI_UUID, UUID.randomUUID().toString())
            .property(AAIProperties.CREATED_TS, 123)
            .property(AAIProperties.SOURCE_OF_TRUTH, "sot")
            .property(AAIProperties.RESOURCE_VERSION, "123")
            .property(AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, "lmsot")
            .property(AAIProperties.LAST_MOD_TS, 333)
            .as("v8")
            .addV()
            .property("aai-node-type", "route-target")
            .property("global-route-target", "getTargetKeyNoLink")
            .property("route-target-role", "getRoleKeyNoLink")
            .property(AAIProperties.AAI_URI, "/network/vpn-bindings/vpn-binding/getKeyNoLink/route-targets/route-target/getTargetKeyNoLink/getRoleKeyNoLink")
            .property(AAIProperties.AAI_UUID, UUID.randomUUID().toString())
            .property(AAIProperties.CREATED_TS, 123)
            .property(AAIProperties.SOURCE_OF_TRUTH, "sot")
            .property(AAIProperties.RESOURCE_VERSION, "123")
            .property(AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, "lmsot")
            .property(AAIProperties.LAST_MOD_TS, 333)
            .as("v9")
            .addE("org.onap.relationships.inventory.BelongsTo").to("v8").from("v9")
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
    public void verifyCreationOfVertex() throws AAIException, UnsupportedEncodingException,
            IllegalArgumentException, SecurityException {

        final Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDepthVersion());
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
        DBSerializer serializer =
                new DBSerializer(schemaVersions.getDefaultVersion(), spy, introspectorFactoryType, "AAI_TEST");
        SideEffectRunner runner =
                new SideEffectRunner.Builder(spy, serializer).addSideEffect(DataLinkWriter.class).build();

        runner.execute(obj, self);

        assertTrue("route-target vertex found", traversal.V().has(AAIProperties.NODE_TYPE, "route-target").has("global-route-target", "key1").has("route-target-role", "key2").has("linked", true).hasNext());
        g.tx().rollback();

    }

    @Test
    public void verifyModificationOfVertex() throws AAIException, UnsupportedEncodingException,
            IllegalArgumentException, SecurityException {

        final Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDepthVersion());
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
        DBSerializer serializer =
                new DBSerializer(schemaVersions.getDefaultVersion(), spy, introspectorFactoryType, "AAI_TEST");
        SideEffectRunner runner =
                new SideEffectRunner.Builder(spy, serializer).addSideEffect(DataLinkWriter.class).build();
        runner.execute(obj, self);

        assertThat("new route-target vertex found with/or without link",
                traversal.V().has(AAIProperties.NODE_TYPE, "route-target")
                        .has("global-route-target", "modifyTargetKey2").has("route-target-role", "modifyRoleKey2")
                        .hasNext(),
                is(true));
        assertThat("new route-target vertex found",
                traversal.V().has(AAIProperties.NODE_TYPE, "route-target")
                        .has("global-route-target", "modifyTargetKey2").has("route-target-role", "modifyRoleKey2")
                        .has("linked", true).hasNext(),
                is(true));
        assertThat("previous link removed",
                traversal.V().has(AAIProperties.NODE_TYPE, "route-target").has("global-route-target", "modifyTargetKey")
                        .has("route-target-role", "modifyRoleKey").has("linked").hasNext(),
                is(not(true)));
        assertThat("previous vertex still exists", traversal.V().has(AAIProperties.NODE_TYPE, "route-target")
                .has("global-route-target", "modifyTargetKey").has("route-target-role", "modifyRoleKey").hasNext(),
                is(true));
        g.tx().rollback();

    }

    @Test
    public void verifyDeleteOfVertex() throws Exception {

        final Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDepthVersion());
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
        DBSerializer serializer =
                new DBSerializer(schemaVersions.getDefaultVersion(), spy, introspectorFactoryType, "AAI_TEST");
        SideEffectRunner runner =
                new SideEffectRunner.Builder(spy, serializer).addSideEffect(DataLinkWriter.class).build();

        runner.execute(obj, self);

        assertFalse("route-target vertex not found", traversal.V().has(AAIProperties.NODE_TYPE, "route-target").has("global-route-target", "deleteTargetKey").has("route-target-role", "deleteRoleKey").has("linked", true).hasNext());

        g.tx().rollback();

    }

    @Test
    public void verifyPropertyPopulation() throws Exception {

        final Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDepthVersion());
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
        DBSerializer serializer =
                new DBSerializer(schemaVersions.getDefaultVersion(), spy, introspectorFactoryType, "AAI_TEST");
        SideEffectRunner runner =
                new SideEffectRunner.Builder(spy, serializer).addSideEffect(DataLinkReader.class).build();

        runner.execute(obj, self);

        assertTrue("both properties have been populated in target object", obj.getValue("global-route-target").equals("getTargetKey") && obj.getValue("route-target-role").equals("getRoleKey"));
        g.tx().rollback();

    }

    @Test
    public void verifyPropertyPopulationWithV10OnlyPut() throws AAIException,
            UnsupportedEncodingException, IllegalArgumentException, SecurityException {
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
        DBSerializer serializer =
                new DBSerializer(schemaVersions.getDefaultVersion(), spy, introspectorFactoryType, "AAI_TEST");
        Vertex v = serializer.createNewVertex(obj);
        serializer.serializeToDb(obj, v, parser, obj.getURI(), "testing");
        Vertex routeTargetOneV = traversal.V().has("global-route-target", "getTargetKeyNoLink").next();
        Vertex routeTargetTwoV = traversal.V().has("global-route-target", "getTargetKeyNoLink2").next();

        assertEquals("first route target put has linked", true,
                routeTargetOneV.property(AAIProperties.LINKED).orElse(false));
        assertEquals("second route target put does not have linked", false,
                routeTargetTwoV.property(AAIProperties.LINKED).orElse(false));

        g.tx().rollback();

    }
}
