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

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
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
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.engines.JanusGraphDBEngine;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;

@RunWith(value = Parameterized.class)

public class OwnerCheckTest extends AAISetup {

    private static JanusGraph graph;
    private final static ModelType introspectorFactoryType = ModelType.MOXY;
    private static Loader loader;
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
            .addV("pnf")
            .property("aai-node-type", "pnf")
            .property("pnf-name", "my-pnf")
            .property(AAIProperties.AAI_URI, "/network/pnfs/pnf/my-pnf")
            .property("model-invariant-id", "key1")
            .as("v1")
            .addV("owning-entity")
            .property("aai-node-type", "owning-entity")
            .property("owning-entity-name", "OE-Generic")
            .property("owning-entity-id", "367c897c-8cec-47ba-b7f5-4b6139f06691")
            .property(AAIProperties.AAI_URI,"/network/pnfs/pnf/my-pnf/business/owning-entities/owning-entity/367c897c-8cec-47ba-b7f5-4b6139f06691")
            .as("oe")
            .addE("org.onap.relationships.inventory.BelongsTo").to("v1").from("oe")
            .property(EdgeProperty.CONTAINS.toString(), true)
            .addV("model-ver")
            .property("aai-node-type", "model-ver")
            .property("model-ver", "myValue")
            .property("model-version-id", "key2")
            .property("model-version", "testValue")
            .property(AAIProperties.AAI_URI, "/network/pnfs/pnf/my-pnf/model-vers/model-ver/key2")
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
    public void shouldFailComparisonWithDiffOwningEntity() throws Exception  {

        final Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDefaultVersion());
        final Introspector obj = loader.introspectorFromName("pnf");
        obj.setValue("pnf-name", "my-pnf");
        obj.setValue("model-invariant-id", "key1");
        obj.setValue("model-version-id", "key2");
        //obj.setValue("owning-entity-id", "367c897c-8cec-47ba-b7f5-4b6139f06691");
        TransactionalGraphEngine spy = spy(dbEngine);
        TransactionalGraphEngine.Admin adminSpy = spy(dbEngine.asAdmin());
        Graph g = graph.newTransaction();
        GraphTraversalSource traversal = g.traversal();
        when(spy.asAdmin()).thenReturn(adminSpy);
        when(adminSpy.getTraversalSource()).thenReturn(traversal);
        DBSerializer serializer =
                new DBSerializer(schemaVersions.getDefaultVersion(),
                    spy, introspectorFactoryType,
                    "AAI_TEST", new HashSet<>(Arrays.asList("OE-GenericI", "OE-GenericII")));

        Vertex selfV = g.traversal().V().has("aai-node-type", "pnf").next();

        OwnerCheck ownerCheck = new OwnerCheck(obj, selfV, spy, serializer);

        thrown.expect(AAIException.class);
        thrown.expectMessage("Group(s) :[OE-GenericI, OE-GenericII] not authorized to perform function");
        ownerCheck.execute();
        g.tx().rollback();

    }

    @Test
    public void shouldPassIfOwningEntityEqual() throws Exception  {

        final Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDefaultVersion());
        final Introspector obj = loader.introspectorFromName("pnf");
        obj.setValue("pnf-name", "my-pnf");
        obj.setValue("model-invariant-id", "key1");
        obj.setValue("model-version-id", "key2");
        //obj.setValue("owning-entity-id", "367c897c-8cec-47ba-b7f5-4b6139f06691");
        TransactionalGraphEngine spy = spy(dbEngine);
        TransactionalGraphEngine.Admin adminSpy = spy(dbEngine.asAdmin());
        Graph g = graph.newTransaction();
        GraphTraversalSource traversal = g.traversal();
        when(spy.tx()).thenReturn(g);
        when(spy.asAdmin()).thenReturn(adminSpy);
        when(adminSpy.getTraversalSource()).thenReturn(traversal);

        Vertex selfV = g.traversal().V().has("aai-node-type", "pnf").next();

        DBSerializer serializer =
            new DBSerializer(schemaVersions.getDefaultVersion(),
                spy, introspectorFactoryType,
                "AAI_TEST", new HashSet<>(Arrays.asList("OE-Generic", "OE-GenericII")));

        OwnerCheck ownerCheck = new OwnerCheck(obj, selfV, spy, serializer);

        ownerCheck.execute();


        g.tx().rollback();
    }

    @Test
    public void shouldPassIfUserOwningEntityEmptyl() throws Exception  {

        final Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDefaultVersion());
        final Introspector obj = loader.introspectorFromName("pnf");
        obj.setValue("pnf-name", "my-pnf");
        obj.setValue("model-invariant-id", "key1");
        obj.setValue("model-version-id", "key2");
        //obj.setValue("owning-entity-id", "367c897c-8cec-47ba-b7f5-4b6139f06691");
        TransactionalGraphEngine spy = spy(dbEngine);
        TransactionalGraphEngine.Admin adminSpy = spy(dbEngine.asAdmin());
        Graph g = graph.newTransaction();
        GraphTraversalSource traversal = g.traversal();
        when(spy.asAdmin()).thenReturn(adminSpy);
        when(adminSpy.getTraversalSource()).thenReturn(traversal);
        DBSerializer serializer =
            new DBSerializer(schemaVersions.getDefaultVersion(),
                spy, introspectorFactoryType,
                "AAI_TEST");

        Vertex selfV = g.traversal().V().has("aai-node-type", "pnf").next();

        OwnerCheck ownerCheck = new OwnerCheck(obj, selfV, spy, serializer);

        ownerCheck.execute();
        g.tx().rollback();
    }
}
