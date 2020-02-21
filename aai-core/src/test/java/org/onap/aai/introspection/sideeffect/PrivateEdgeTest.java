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
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.MockitoAnnotations;
import org.onap.aai.AAISetup;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.edges.enums.EdgeProperty;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.engines.JanusGraphDBEngine;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(value = Parameterized.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class PrivateEdgeTest extends AAISetup {

    private static JanusGraph graph;
    private final static ModelType introspectorFactoryType = ModelType.MOXY;
    private static TransactionalGraphEngine dbEngine;

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
            .addV()
            .property("aai-node-type", "model")
            .property("model-invariant-id", "key1")
            .property(AAIProperties.AAI_URI, "/service-design-and-creation/models/model/key1")
            .as("v1")
            .addV()
            .property("aai-node-type", "model-ver")
            .property("model-ver", "myValue")
            .property("model-version-id", "key2")
            .property("model-version", "testValue")
            .property(AAIProperties.AAI_URI, "/service-design-and-creation/models/model/key1/model-vers/model-ver/key2")
            .as("v2")
            .addE("org.onap.relationships.inventory.BelongsTo").to("v1").from("v2")
            .property(EdgeProperty.CONTAINS.toString(), true)
            .addV()
            .property("aai-node-type", "model")
            .property("model-invariant-id", "key100")
            .property(AAIProperties.AAI_URI, "/service-design-and-creation/models/model/key100")
            .as("v3")
            .addV()
            .property("aai-node-type", "model-ver")
            .property("model-ver", "myValue")
            .property("model-version-id", "key200")
            .property("model-version", "testValue")
            .property(AAIProperties.AAI_URI, "/service-design-and-creation/models/model/key100/model-vers/model-ver/key200")
            .as("v4")
            .addE("org.onap.relationships.inventory.BelongsTo").to("v3").from("v4")
            .property(EdgeProperty.CONTAINS.toString(), true)
            .addV()
            .property("aai-node-type", "model")
            .property("model-invariant-id", "key3")
            .property(AAIProperties.AAI_URI, "/service-design-and-creation/models/model/key3")
            .as("v5")
            .addV()
            .property("aai-node-type", "model-ver")
            .property("model-ver", "myValue")
            .property("model-version-id", "key4")
            .property(AAIProperties.AAI_URI, "/service-design-and-creation/models/model/key3/model-vers/model-ver/key4")
            .as("v6")
            .addE("org.onap.relationships.inventory.BelongsTo").to("v5").from("v6")
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
        Loader loader = loaderFactory.createLoaderForVersion(introspectorFactoryType, schemaVersions.getDefaultVersion());
        MockitoAnnotations.initMocks(this);
        dbEngine = new JanusGraphDBEngine(queryStyle, loader);
    }

    @Test
    public void testWhenPrivateEdgeThrowsExceptionWhenHavingOnlyOnePartOfKey() throws Exception {

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

        Vertex selfV = traversal.addV("generic-vnf")
            .property("aai-node-type", "generic-vnf")
            .property("vnf-id", "myId")
            .property("aai-uri", obj.getURI())
            .property("model-invariant-id", "key1")
            .next();

        thrown.expectMessage(containsString("Cannot complete privateEdge uri"));
        DBSerializer serializer =
                new DBSerializer(schemaVersions.getDefaultVersion(), spy, introspectorFactoryType, "AAI_TEST");
        PrivateEdge privateEdge = new PrivateEdge(obj, selfV, spy, serializer);
        privateEdge.execute();

        List<Edge> edgeList = traversal.E().has("private", true).toList();

        assertNull(edgeList);
        assertThat(edgeList, is(not(empty())));
        assertEquals(1, edgeList.size());

        g.tx().rollback();
    }

}
