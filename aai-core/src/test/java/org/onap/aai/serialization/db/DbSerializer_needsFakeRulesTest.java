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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraphFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.aai.config.ConfigConfiguration;
import org.onap.aai.config.IntrospectionConfig;
import org.onap.aai.config.SpringContextAware;
import org.onap.aai.config.XmlFormatTransformerConfiguration;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.serialization.engines.JanusGraphDBEngine;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.serialization.queryformats.QueryFormatTestHelper;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;
import org.onap.aai.util.AAIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

//@RunWith(value = Parameterized.class) TODO replace this functionality
@SpringJUnitConfig(
        classes = {ConfigConfiguration.class, AAICoreFakeEdgesConfigTranslator.class, NodeIngestor.class,
                EdgeIngestor.class, EdgeSerializer.class, SpringContextAware.class, IntrospectionConfig.class,
                XmlFormatTransformerConfiguration.class})
@TestPropertySource(
        properties = {"schema.translator.list = config", "schema.nodes.location=src/test/resources/onap/oxm",
                "schema.edges.location=src/test/resources/onap/dbedgerules"})
public class DbSerializer_needsFakeRulesTest {

    protected static Graph graph;

    @Autowired
    protected EdgeSerializer edgeSer;
    @Autowired
    protected EdgeIngestor ei;
    @Autowired
    protected SchemaVersions schemaVersions;

    private SchemaVersion version;
    private final ModelType introspectorFactoryType = ModelType.MOXY;
    private Loader loader;
    private TransactionalGraphEngine dbEngine;
    private TransactionalGraphEngine engine; // for tests that aren't mocking the engine
    private DBSerializer dbser;
    TransactionalGraphEngine spy;
    TransactionalGraphEngine.Admin adminSpy;

    public QueryStyle queryStyle = QueryStyle.TRAVERSAL;

    @BeforeAll
    public static void init() throws Exception {
        graph = JanusGraphFactory.build().set("storage.backend", "inmemory").open();
        System.setProperty("AJSC_HOME", ".");
        System.setProperty("BUNDLECONFIG_DIR", "src/test/resources/bundleconfig-local");
        QueryFormatTestHelper.setFinalStatic(AAIConstants.class.getField("AAI_HOME_ETC_OXM"),
                "src/test/resources/bundleconfig-local/etc/oxm/");

    }

    @BeforeEach
    public void setup() throws Exception {
        version = schemaVersions.getDefaultVersion();
        loader = SpringContextAware.getBean(LoaderFactory.class).createLoaderForVersion(introspectorFactoryType,
                version);
        dbEngine = new JanusGraphDBEngine(queryStyle, loader);
        spy = spy(dbEngine);
        adminSpy = spy(dbEngine.asAdmin());

        engine = new JanusGraphDBEngine(queryStyle, loader);
        dbser = new DBSerializer(version, engine, introspectorFactoryType, "AAI-TEST");
    }

    @AfterEach
    public void tearDown() throws Exception {
        engine.rollback();
    }

    @AfterAll
    public static void destroy() throws Exception {
        graph.close();
    }

    public void subnetSetup() throws AAIException {
        /*
         * This setus up the test graph, For future junits , add more vertices
         * and edges
         */

        Vertex l3interipv4addresslist_1 = graph.addVertex("aai-node-type", "l3-interface-ipv4-address-list",
                "l3-interface-ipv4-address", "l3-interface-ipv4-address-1");
        Vertex subnet_2 = graph.addVertex("aai-node-type", "subnet", "subnet-id", "subnet-id-2");
        Vertex l3interipv6addresslist_3 = graph.addVertex("aai-node-type", "l3-interface-ipv6-address-list",
                "l3-interface-ipv6-address", "l3-interface-ipv6-address-3");
        Vertex subnet_4 = graph.addVertex("aai-node-type", "subnet", "subnet-id", "subnet-id-4");
        Vertex subnet_5 = graph.addVertex("aai-node-type", "subnet", "subnet-id", "subnet-id-5");
        Vertex l3network_6 = graph.addVertex("aai-node-type", "l3-network", "network-id", "network-id-6",
                "network-name", "network-name-6");

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
    public void serializeToDbNewVertexAndEdgeAAIUUIDTest()
            throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException, URISyntaxException {
        String testName = new Object() {}.getClass().getEnclosingMethod().getName();
        DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();

        engine.startTransaction();

        engine.tx().addVertex("aai-node-type", "vnfc", "vnfc-name", "vnfc-" + testName, AAIProperties.AAI_URI,
                "/network/vnfcs/vnfc/vnfc-" + testName, AAIProperties.AAI_UUID, UUID.randomUUID().toString(),
                AAIProperties.CREATED_TS, 123, AAIProperties.SOURCE_OF_TRUTH, "sot", AAIProperties.RESOURCE_VERSION,
                "123", AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, "lmsot", AAIProperties.LAST_MOD_TS, 333);

        Introspector relationship = loader.introspectorFromName("relationship");
        relationship.setValue("related-to", "vnfc");
        relationship.setValue("related-link", "/network/vnfcs/vnfc/vnfc-" + testName);

        Introspector relationshipList = loader.introspectorFromName("relationship-list");
        relationshipList.setValue("relationship", Collections.singletonList(relationship.getUnderlyingObject()));

        Introspector gvnfObj = loader.introspectorFromName("generic-vnf");
        Vertex gvnf = localDbser.createNewVertex(gvnfObj);
        gvnfObj.setValue("relationship-list", relationshipList.getUnderlyingObject());
        gvnfObj.setValue("vnf-id", "vnf-" + testName);

        QueryParser uriQuery = dbEngine.getQueryBuilder()
                .createQueryFromURI(new URI("/network/generic-vnfs/generic-vnf/vnf-" + testName));

        localDbser.serializeToDb(gvnfObj, gvnf, uriQuery, null, "test");
        assertTrue(gvnf.property(AAIProperties.AAI_UUID).isPresent(), "Generic-vnf has uuid ");
        assertTrue(gvnf.edges(Direction.BOTH).next().property(AAIProperties.AAI_UUID).isPresent(), "Edge has uuid ");

    }

    @Test
    public void createEdgeWithValidLabelTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException {

        DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();

        engine.startTransaction();

        Vertex gvnf = engine.tx().addVertex("aai-node-type", "generic-vnf", "vnf-id", "myvnf", "aai-uri",
                "/network/generic-vnfs/generic-vnf/myvnf", "aai-uuid", "a", AAIProperties.CREATED_TS, 123,
                AAIProperties.SOURCE_OF_TRUTH, "sot", AAIProperties.RESOURCE_VERSION, "123",
                AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, "lmsot", AAIProperties.LAST_MOD_TS, 333);
        Vertex vnfc = engine.tx().addVertex("aai-node-type", "vnfc", "vnfc-name", "a-name", "aai-uri",
                "/network/vnfcs/vnfc/a-name", "aai-uuid", "b", AAIProperties.CREATED_TS, 123,
                AAIProperties.SOURCE_OF_TRUTH, "sot", AAIProperties.RESOURCE_VERSION, "123",
                AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, "lmsot", AAIProperties.LAST_MOD_TS, 333);

        // sunny day case
        Introspector relData = loader.introspectorFromName("relationship-data");
        relData.setValue("relationship-key", "vnfc.vnfc-name");
        relData.setValue("relationship-value", "a-name");
        Introspector relationship = loader.introspectorFromName("relationship");
        relationship.setValue("related-to", "vnfc");
        relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");
        relationship.setValue("relationship-data", relData);
        relationship.setValue("relationship-label", "over-uses");

        assertNotNull(dbser.createEdge(relationship, gvnf));
        assertTrue(engine.tx().traversal().V(gvnf).both("over-uses").hasNext());
        assertTrue(engine.tx().traversal().V(vnfc).both("over-uses").hasNext());

    }

    @Test
    public void createEdgeWithValidLabelWhenSameEdgeExistsTest() throws AAIException, UnsupportedEncodingException,
            NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

        DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();
        engine.startTransaction();

        Vertex gvnf = engine.tx().addVertex("aai-node-type", "generic-vnf", "vnf-id", "myvnf", "aai-uri",
                "/network/generic-vnfs/generic-vnf/myvnf");
        Vertex vnfc = engine.tx().addVertex("aai-node-type", "vnfc", "vnfc-name", "a-name", "aai-uri",
                "/network/vnfcs/vnfc/a-name");
        edgeSer.addEdge(graph.traversal(), gvnf, vnfc, "re-uses");

        Introspector relData = loader.introspectorFromName("relationship-data");
        relData.setValue("relationship-key", "vnfc.vnfc-name");
        relData.setValue("relationship-value", "a-name");
        Introspector relationship = loader.introspectorFromName("relationship");
        relationship.setValue("related-to", "vnfc");
        relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");
        relationship.setValue("relationship-data", relData);
        relationship.setValue("relationship-label", "re-uses");

        assertNotNull(dbser.createEdge(relationship, gvnf));
        assertTrue(engine.tx().traversal().V(gvnf).both("re-uses").hasNext());
        assertTrue(engine.tx().traversal().V(vnfc).both("re-uses").hasNext());
        assertEquals(Long.valueOf(1),
                engine.tx().traversal().V(vnfc).both().count().next(),
                "Number of edges between vertexes is 1");

    }

    @Test
    public void createEdgeWithValidLabelWhenDiffEdgeExistsTest() throws AAIException, UnsupportedEncodingException,
            NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

        DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();
        engine.startTransaction();

        Vertex gvnf = engine.tx().addVertex("aai-node-type", "generic-vnf", "vnf-id", "myvnf", "aai-uri",
                "/network/generic-vnfs/generic-vnf/myvnf", AAIProperties.AAI_UUID, UUID.randomUUID().toString(),
                AAIProperties.CREATED_TS, 123, AAIProperties.SOURCE_OF_TRUTH, "sot", AAIProperties.RESOURCE_VERSION,
                "123", AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, "lmsot", AAIProperties.LAST_MOD_TS, 333);
        Vertex vnfc = engine.tx().addVertex("aai-node-type", "vnfc", "vnfc-name", "a-name", "aai-uri",
                "/network/vnfcs/vnfc/a-name", AAIProperties.AAI_UUID, UUID.randomUUID().toString(),
                AAIProperties.CREATED_TS, 123, AAIProperties.SOURCE_OF_TRUTH, "sot", AAIProperties.RESOURCE_VERSION,
                "123", AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, "lmsot", AAIProperties.LAST_MOD_TS, 333);
        edgeSer.addEdge(graph.traversal(), gvnf, vnfc, "uses");

        Introspector relData = loader.introspectorFromName("relationship-data");
        relData.setValue("relationship-key", "vnfc.vnfc-name");
        relData.setValue("relationship-value", "a-name");
        Introspector relationship = loader.introspectorFromName("relationship");
        relationship.setValue("related-to", "vnfc");
        relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");
        relationship.setValue("relationship-data", relData);
        relationship.setValue("relationship-label", "uses");
        localDbser.createEdge(relationship, gvnf);

        relationship.setValue("relationship-label", "re-uses");

        assertNotNull(dbser.createEdge(relationship, gvnf));
        assertTrue(engine.tx().traversal().V(gvnf).both("re-uses").hasNext());
        assertTrue(engine.tx().traversal().V(vnfc).both("re-uses").hasNext());
        assertTrue(engine.tx().traversal().V(gvnf).both("uses").hasNext());
        assertTrue(engine.tx().traversal().V(vnfc).both("uses").hasNext());
        assertEquals(Long.valueOf(2),
                engine.tx().traversal().V(vnfc).both().count().next(),
                "Number of edges between vertexes is 2");
        assertEquals(Long.valueOf(2),
                engine.tx().traversal().V(gvnf).both().count().next(),
                "Number of edges between vertexes is 2");

    }

    @Test
    public void createEdgeWithNoLabelTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException {

        DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();
        engine.startTransaction();

        Vertex gvnf = engine.tx().addVertex("aai-node-type", "generic-vnf", "vnf-id", "myvnf", "aai-uri",
                "/network/generic-vnfs/generic-vnf/myvnf", AAIProperties.AAI_UUID, UUID.randomUUID().toString(),
                AAIProperties.CREATED_TS, 123, AAIProperties.SOURCE_OF_TRUTH, "sot", AAIProperties.RESOURCE_VERSION,
                "123", AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, "lmsot", AAIProperties.LAST_MOD_TS, 333);
        Vertex vnfc = engine.tx().addVertex("aai-node-type", "vnfc", "vnfc-name", "a-name", "aai-uri",
                "/network/vnfcs/vnfc/a-name", AAIProperties.AAI_UUID, UUID.randomUUID().toString(),
                AAIProperties.CREATED_TS, 123, AAIProperties.SOURCE_OF_TRUTH, "sot", AAIProperties.RESOURCE_VERSION,
                "123", AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, "lmsot", AAIProperties.LAST_MOD_TS, 333);

        Introspector relData = loader.introspectorFromName("relationship-data");
        relData.setValue("relationship-key", "vnfc.vnfc-name");
        relData.setValue("relationship-value", "a-name");
        Introspector relationship = loader.introspectorFromName("relationship");
        relationship.setValue("related-to", "vnfc");
        relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");
        relationship.setValue("relationship-data", relData);
        localDbser.createEdge(relationship, gvnf);

        assertNotNull(dbser.createEdge(relationship, gvnf));
        assertTrue(engine.tx().traversal().V(gvnf).both("uses").hasNext());
        assertTrue(engine.tx().traversal().V(vnfc).both("uses").hasNext());
        assertEquals(Long.valueOf(1),
                engine.tx().traversal().V(vnfc).both().count().next(),
                "Number of edges between vertexes is 1");
        assertEquals(Long.valueOf(1),
                engine.tx().traversal().V(gvnf).both().count().next(),
                "Number of edges between vertexes is 1");

    }

    @Test
    public void deleteEdgeWithNoLabelWhenMultipleExistsTest() throws AAIException, UnsupportedEncodingException,
            NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

        DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();
        engine.startTransaction();

        Vertex gvnf = engine.tx().addVertex("aai-node-type", "generic-vnf", "vnf-id", "myvnf", "aai-uri",
                "/network/generic-vnfs/generic-vnf/myvnf", AAIProperties.AAI_UUID, UUID.randomUUID().toString(),
                AAIProperties.CREATED_TS, 123, AAIProperties.SOURCE_OF_TRUTH, "sot", AAIProperties.RESOURCE_VERSION,
                "123", AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, "lmsot", AAIProperties.LAST_MOD_TS, 333);
        Vertex vnfc = engine.tx().addVertex("aai-node-type", "vnfc", "vnfc-name", "a-name", "aai-uri",
                "/network/vnfcs/vnfc/a-name", AAIProperties.AAI_UUID, UUID.randomUUID().toString(),
                AAIProperties.CREATED_TS, 123, AAIProperties.SOURCE_OF_TRUTH, "sot", AAIProperties.RESOURCE_VERSION,
                "123", AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, "lmsot", AAIProperties.LAST_MOD_TS, 333);
        edgeSer.addEdge(graph.traversal(), gvnf, vnfc, "uses");
        edgeSer.addEdge(graph.traversal(), gvnf, vnfc, "re-uses");
        edgeSer.addEdge(graph.traversal(), gvnf, vnfc, "over-uses");

        Introspector relData = loader.introspectorFromName("relationship-data");
        relData.setValue("relationship-key", "vnfc.vnfc-name");
        relData.setValue("relationship-value", "a-name");
        Introspector relationship = loader.introspectorFromName("relationship");
        relationship.setValue("related-to", "vnfc");
        relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");
        relationship.setValue("relationship-data", relData);

        assertTrue(localDbser.deleteEdge(relationship, gvnf).isPresent());
        assertFalse(engine.tx().traversal().V(gvnf).both("uses").hasNext(), "generic-vnf has no edge uses");
        assertFalse(engine.tx().traversal().V(vnfc).both("uses").hasNext(), "vnfc has no edge uses");
        assertTrue(engine.tx().traversal().V(gvnf).both("re-uses").hasNext(), "generic-vnf has edge re-uses");
        assertTrue(engine.tx().traversal().V(vnfc).both("re-uses").hasNext(), "vnfc has edge re-uses");
        assertTrue(engine.tx().traversal().V(gvnf).both("over-uses").hasNext(), "generic-vnf has edge re-uses");
        assertTrue(engine.tx().traversal().V(vnfc).both("over-uses").hasNext(), "vnfc has edge re-uses");
        assertEquals(Long.valueOf(2),
                engine.tx().traversal().V(vnfc).both().count().next(),
                "Number of edges between vertexes is 2");
        assertEquals(Long.valueOf(2),
                engine.tx().traversal().V(gvnf).both().count().next(),
                "Number of edges between vertexes is 2");

    }

    @Test
    public void deleteEdgeWithValidLabelWhenMultipleExistsTest() throws AAIException, UnsupportedEncodingException,
            NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

        DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();

        engine.startTransaction();

        Vertex gvnf = engine.tx().addVertex("aai-node-type", "generic-vnf", "vnf-id", "myvnf", "aai-uri",
                "/network/generic-vnfs/generic-vnf/myvnf", AAIProperties.AAI_UUID, UUID.randomUUID().toString(),
                AAIProperties.CREATED_TS, 123, AAIProperties.SOURCE_OF_TRUTH, "sot", AAIProperties.RESOURCE_VERSION,
                "123", AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, "lmsot", AAIProperties.LAST_MOD_TS, 333);
        Vertex vnfc = engine.tx().addVertex("aai-node-type", "vnfc", "vnfc-name", "a-name", "aai-uri",
                "/network/vnfcs/vnfc/a-name", AAIProperties.AAI_UUID, UUID.randomUUID().toString(),
                AAIProperties.CREATED_TS, 123, AAIProperties.SOURCE_OF_TRUTH, "sot", AAIProperties.RESOURCE_VERSION,
                "123", AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, "lmsot", AAIProperties.LAST_MOD_TS, 333);
        edgeSer.addEdge(graph.traversal(), gvnf, vnfc, "uses");
        edgeSer.addEdge(graph.traversal(), gvnf, vnfc, "re-uses");
        edgeSer.addEdge(graph.traversal(), gvnf, vnfc, "over-uses");

        Introspector relData = loader.introspectorFromName("relationship-data");
        relData.setValue("relationship-key", "vnfc.vnfc-name");
        relData.setValue("relationship-value", "a-name");
        Introspector relationship = loader.introspectorFromName("relationship");
        relationship.setValue("related-to", "vnfc");
        relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");
        relationship.setValue("relationship-data", relData);
        relationship.setValue("relationship-label", "re-uses");

        assertTrue(localDbser.deleteEdge(relationship, gvnf).isPresent());
        assertTrue(engine.tx().traversal().V(gvnf).both("uses").hasNext(), "generic-vnf has edge uses");
        assertTrue(engine.tx().traversal().V(vnfc).both("uses").hasNext(), "vnfc has edge uses");
        assertFalse(engine.tx().traversal().V(gvnf).both("re-uses").hasNext(), "generic-vnf has no edge re-uses");
        assertFalse(engine.tx().traversal().V(vnfc).both("re-uses").hasNext(), "vnfc has no edge re-uses");
        assertTrue(engine.tx().traversal().V(gvnf).both("over-uses").hasNext(), "generic-vnf has edge re-uses");
        assertTrue(engine.tx().traversal().V(vnfc).both("over-uses").hasNext(), "vnfc has edge re-uses");
        assertEquals(Long.valueOf(2),
                engine.tx().traversal().V(vnfc).both().count().next(),
                "Number of edges between vertexes is 2");
        assertEquals(Long.valueOf(2),
                engine.tx().traversal().V(gvnf).both().count().next(),
                "Number of edges between vertexes is 2");

    }

    @Test
    public void deleteEdgeWithValidInvalidLabelWhenMultipleExistsTest()
            throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        Throwable exception = assertThrows(AAIException.class, () -> {

            DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();

            engine.startTransaction();

            Vertex gvnf = engine.tx().addVertex("aai-node-type", "generic-vnf", "vnf-id", "myvnf", "aai-uri",
                    "/network/generic-vnfs/generic-vnf/myvnf");
            Vertex vnfc = engine.tx().addVertex("aai-node-type", "vnfc", "vnfc-name", "a-name", "aai-uri",
                    "/network/vnfcs/vnfc/a-name");
            edgeSer.addEdge(graph.traversal(), gvnf, vnfc, "uses");
            edgeSer.addEdge(graph.traversal(), gvnf, vnfc, "re-uses");
            edgeSer.addEdge(graph.traversal(), gvnf, vnfc, "over-uses");

            Introspector relData = loader.introspectorFromName("relationship-data");
            relData.setValue("relationship-key", "vnfc.vnfc-name");
            relData.setValue("relationship-value", "a-name");
            Introspector relationship = loader.introspectorFromName("relationship");
            relationship.setValue("related-to", "vnfc");
            relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");
            relationship.setValue("relationship-data", relData);
            relationship.setValue("relationship-label", "NA");
            localDbser.deleteEdge(relationship, gvnf);
        });
        assertTrue(exception.getMessage().contains("node type: generic-vnf, node type: vnfc, label: NA, type: COUSIN"));
    }

    @Test
    public void serializeToDbWithLabelTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException, URISyntaxException {

        DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();

        engine.startTransaction();

        engine.tx().addVertex("aai-node-type", "vnfc", "vnfc-name", "a-name", "aai-uri", "/network/vnfcs/vnfc/a-name",
                "aai-uuid", "b", AAIProperties.CREATED_TS, 123, AAIProperties.SOURCE_OF_TRUTH, "sot",
                AAIProperties.RESOURCE_VERSION, "123", AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, "lmsot",
                AAIProperties.LAST_MOD_TS, 333);

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

        QueryParser uriQuery =
                dbEngine.getQueryBuilder().createQueryFromURI(new URI("/network/generic-vnfs/generic-vnf/myvnf"));

        localDbser.serializeToDb(gvnfObj, gvnf, uriQuery, null, "test");

        assertTrue(engine.tx().traversal().V().has("vnf-id", "myvnf").hasNext(), "vertex with vnf-id myvnf exists");
        assertTrue(engine.tx().traversal().V().has("vnfc-name", "a-name").hasNext(),
                "vertex with vnfc-name a-name exists");
        assertFalse(engine.tx().traversal().V().has("vnf-id", "myvnf").both("uses").hasNext(),
                "generic-vnf has no edge re-uses");
        assertFalse(engine.tx().traversal().V().has("vnfc-name", "a-name").both("uses").hasNext(),
                "vnfc has no edge re-uses");
        assertTrue(engine.tx().traversal().V().has("vnf-id", "myvnf").both("re-uses").hasNext(),
                "generic-vnf has edge re-uses");
        assertTrue(engine.tx().traversal().V().has("vnfc-name", "a-name").both("re-uses").hasNext(),
                "vnfc has edge re-uses");
        assertFalse(engine.tx().traversal().V().has("vnf-id", "myvnf").both("over-uses").hasNext(),
                "generic-vnf has no edge re-uses");
        assertFalse(engine.tx().traversal().V().has("vnfc-name", "a-name").both("over-uses").hasNext(),
                "vnfc has no edge re-uses");
        assertEquals(Long.valueOf(1),
                engine.tx().traversal().V().has("vnfc-name", "a-name").both().count().next(),
                "Number of edges between vertexes is 1");
        assertEquals(Long.valueOf(1),
                engine.tx().traversal().V().has("vnf-id", "myvnf").both().count().next(),
                "Number of edges between vertexes is 1");

    }

    @Test
    public void serializeToDbWithoutLabelTest() throws AAIException, UnsupportedEncodingException, NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException, URISyntaxException {

        DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();

        engine.startTransaction();

        engine.tx().addVertex("aai-node-type", "vnfc", "vnfc-name", "a-name", "aai-uri", "/network/vnfcs/vnfc/a-name",
                AAIProperties.AAI_UUID, UUID.randomUUID().toString(), AAIProperties.CREATED_TS, 123,
                AAIProperties.SOURCE_OF_TRUTH, "sot", AAIProperties.RESOURCE_VERSION, "123",
                AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, "lmsot", AAIProperties.LAST_MOD_TS, 333);

        Introspector relationship = loader.introspectorFromName("relationship");
        relationship.setValue("related-to", "vnfc");
        relationship.setValue("related-link", "/network/vnfcs/vnfc/a-name");

        Introspector relationshipList = loader.introspectorFromName("relationship-list");
        relationshipList.setValue("relationship", Collections.singletonList(relationship.getUnderlyingObject()));

        Introspector gvnfObj = loader.introspectorFromName("generic-vnf");
        Vertex gvnf = localDbser.createNewVertex(gvnfObj);
        gvnfObj.setValue("relationship-list", relationshipList.getUnderlyingObject());
        gvnfObj.setValue("vnf-id", "myvnf");

        QueryParser uriQuery =
                dbEngine.getQueryBuilder().createQueryFromURI(new URI("/network/generic-vnfs/generic-vnf/myvnf"));

        localDbser.serializeToDb(gvnfObj, gvnf, uriQuery, null, "test");

        assertTrue(engine.tx().traversal().V().has("vnf-id", "myvnf").hasNext(), "vertex with vnf-id myvnf exists");
        assertTrue(engine.tx().traversal().V().has("vnfc-name", "a-name").hasNext(),
                "vertex with vnfc-name a-name exists");
        assertTrue(engine.tx().traversal().V().has("vnf-id", "myvnf").both("uses").hasNext(),
                "generic-vnf has edge uses");
        assertTrue(engine.tx().traversal().V().has("vnfc-name", "a-name").both("uses").hasNext(), "vnfc has edge uses");
        assertFalse(engine.tx().traversal().V().has("vnf-id", "myvnf").both("re-uses").hasNext(),
                "generic-vnf has no edge re-uses");
        assertFalse(engine.tx().traversal().V().has("vnfc-name", "a-name").both("re-uses").hasNext(),
                "vnfc has no edge re-uses");
        assertFalse(engine.tx().traversal().V().has("vnf-id", "myvnf").both("over-uses").hasNext(),
                "generic-vnf has no edge over-uses");
        assertFalse(engine.tx().traversal().V().has("vnfc-name", "a-name").both("over-uses").hasNext(),
                "vnfc has no edge over-uses");
        assertEquals(Long.valueOf(1),
                engine.tx().traversal().V().has("vnfc-name", "a-name").both().count().next(),
                "Number of edges between vertexes is 1");
        assertEquals(Long.valueOf(1),
                engine.tx().traversal().V().has("vnf-id", "myvnf").both().count().next(),
                "Number of edges between vertexes is 1");

    }

    @Test
    public void serializeToDbWithInvalidLabelTest()
            throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException, URISyntaxException {
        Throwable exception = assertThrows(AAIException.class, () -> {

            DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();

            engine.startTransaction();

            engine.tx().addVertex("aai-node-type", "vnfc", "vnfc-name", "a-name", "aai-uri", "/network/vnfcs/vnfc/a-name");

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

            QueryParser uriQuery =
                    dbEngine.getQueryBuilder().createQueryFromURI(new URI("/network/generic-vnfs/generic-vnf/myvnf"));
            localDbser.serializeToDb(gvnfObj, gvnf, uriQuery, null, "test");

        });
        assertTrue(exception.getMessage().contains("No EdgeRule found for passed nodeTypes: generic-vnf, vnfc with label NA."));

    }

    @Test
    public void serializeToDbWithLabelAndEdgeExistsTest()
            throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException, URISyntaxException {

        DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();

        engine.startTransaction();
        engine.tx().addVertex("aai-node-type", "vnfc", "vnfc-name", "a-name", "aai-uri", "/network/vnfcs/vnfc/a-name",
                AAIProperties.AAI_UUID, UUID.randomUUID().toString(), AAIProperties.CREATED_TS, 123,
                AAIProperties.SOURCE_OF_TRUTH, "sot", AAIProperties.RESOURCE_VERSION, "123",
                AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, "lmsot", AAIProperties.LAST_MOD_TS, 333);

        Introspector relationship;
        Introspector relationshipList;
        List<Object> relList = new ArrayList<>();

        // create generic-vnf
        Introspector gvnfObj = loader.introspectorFromName("generic-vnf");
        Vertex gvnf = localDbser.createNewVertex(gvnfObj);
        gvnfObj.setValue("vnf-id", "myvnf");
        QueryParser uriQuery =
                dbEngine.getQueryBuilder().createQueryFromURI(new URI("/network/generic-vnfs/generic-vnf/myvnf"));

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

        assertTrue(engine.tx().traversal().V().has("vnf-id", "myvnf").hasNext(), "vertex with vnf-id myvnf exists");
        assertTrue(engine.tx().traversal().V().has("vnfc-name", "a-name").hasNext(),
                "vertex with vnfc-name a-name exists");
        assertTrue(engine.tx().traversal().V().has("vnf-id", "myvnf").both("uses").hasNext(),
                "generic-vnf has  edge uses");
        assertTrue(engine.tx().traversal().V().has("vnfc-name", "a-name").both("uses").hasNext(),
                "vnfc has  edge uses");
        assertTrue(engine.tx().traversal().V().has("vnf-id", "myvnf").both("re-uses").hasNext(),
                "generic-vnf has edge re-uses");
        assertTrue(engine.tx().traversal().V().has("vnfc-name", "a-name").both("re-uses").hasNext(),
                "vnfc has edge re-uses");
        assertFalse(engine.tx().traversal().V().has("vnf-id", "myvnf").both("over-uses").hasNext(),
                "generic-vnf has no edge over-uses");
        assertFalse(engine.tx().traversal().V().has("vnfc-name", "a-name").both("over-uses").hasNext(),
                "vnfc has no edge over-uses");
        assertEquals(Long.valueOf(2),
                engine.tx().traversal().V().has("vnfc-name", "a-name").both().count().next(),
                "Number of edges between vertexes is 2");
        assertEquals(Long.valueOf(2),
                engine.tx().traversal().V().has("vnf-id", "myvnf").both().count().next(),
                "Number of edges between vertexes is 2");

    }

    @Test
    public void serializeToDbWithLabelDroppingRelationshipTest()
            throws AAIException, UnsupportedEncodingException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException, URISyntaxException {

        DBSerializer localDbser = getDBSerializerWithSpecificEdgeRules();

        engine.startTransaction();
        engine.tx().addVertex("aai-node-type", "vnfc", "vnfc-name", "a-name", "aai-uri", "/network/vnfcs/vnfc/a-name",
                AAIProperties.AAI_UUID, UUID.randomUUID().toString(), AAIProperties.CREATED_TS, 123,
                AAIProperties.SOURCE_OF_TRUTH, "sot", AAIProperties.RESOURCE_VERSION, "123",
                AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, "lmsot", AAIProperties.LAST_MOD_TS, 333);

        Introspector relationship;
        Introspector relationshipList;
        List<Object> relList = new ArrayList<>();

        // create generic-vnf
        Introspector gvnfObj = loader.introspectorFromName("generic-vnf");
        Vertex gvnf = localDbser.createNewVertex(gvnfObj);
        gvnfObj.setValue("vnf-id", "myvnf");
        QueryParser uriQuery =
                dbEngine.getQueryBuilder().createQueryFromURI(new URI("/network/generic-vnfs/generic-vnf/myvnf"));

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

        assertTrue(engine.tx().traversal().V().has("vnf-id", "myvnf").hasNext(), "vertex with vnf-id myvnf exists");
        assertTrue(engine.tx().traversal().V().has("vnfc-name", "a-name").hasNext(),
                "vertex with vnfc-name a-name exists");
        assertTrue(engine.tx().traversal().V().has("vnf-id", "myvnf").both("uses").hasNext(),
                "generic-vnf has  edge uses");
        assertTrue(engine.tx().traversal().V().has("vnfc-name", "a-name").both("uses").hasNext(),
                "vnfc has  edge uses");
        assertFalse(engine.tx().traversal().V().has("vnf-id", "myvnf").both("re-uses").hasNext(),
                "generic-vnf no longer has edge re-uses");
        assertFalse(engine.tx().traversal().V().has("vnfc-name", "a-name").both("re-uses").hasNext(),
                "vnfc no longer has edge re-uses");
        assertFalse(engine.tx().traversal().V().has("vnf-id", "myvnf").both("over-uses").hasNext(),
                "generic-vnf has no edge over-uses");
        assertFalse(engine.tx().traversal().V().has("vnfc-name", "a-name").both("over-uses").hasNext(),
                "vnfc has no edge over-uses");
        assertEquals(Long.valueOf(1),
                engine.tx().traversal().V().has("vnfc-name", "a-name").both().count().next(),
                "Number of edges between vertexes is 1");
        assertEquals(Long.valueOf(1),
                engine.tx().traversal().V().has("vnf-id", "myvnf").both().count().next(),
                "Number of edges between vertexes is 1");

    }

    private DBSerializer getDBSerializerWithSpecificEdgeRules() throws AAIException {

        return new DBSerializer(version, engine, introspectorFactoryType, "AAI-TEST");
    }
}
