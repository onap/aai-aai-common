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

package org.onap.aai.db;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraphFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.onap.aai.AAISetup;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.parsers.exceptions.AmbiguousMapAAIException;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.db.EdgeSerializer;
import org.onap.aai.serialization.engines.JanusGraphDBEngine;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.setup.SchemaVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class DbMethHelperTest extends AAISetup {
    private DbMethHelper dbMethHelper = new DbMethHelper();
    protected static Graph graph;

    @Autowired
    protected EdgeSerializer edgeSer;

    private SchemaVersion version;
    private final ModelType introspectorFactoryType = ModelType.MOXY;
    private Loader loader;
    private TransactionalGraphEngine dbEngine;
    private TransactionalGraphEngine engine; // for tests that aren't mocking the engine
    private DBSerializer dbser;
    private TransactionalGraphEngine spy;
    private TransactionalGraphEngine.Admin adminSpy;
    public QueryStyle queryStyle;

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {{QueryStyle.TRAVERSAL}});
    }

    @BeforeAll
    public static void init() {
        graph = JanusGraphFactory.build().set("storage.backend", "inmemory").open();
    }

    @BeforeEach
    public void setUp() throws Exception {
        version = schemaVersions.getDefaultVersion();
        loader = loaderFactory.createLoaderForVersion(introspectorFactoryType, version);
        dbEngine = new JanusGraphDBEngine(queryStyle, loader);
        spy = spy(dbEngine);
        adminSpy = spy(dbEngine.asAdmin());

        engine = new JanusGraphDBEngine(queryStyle, loader);
        dbser = new DBSerializer(version, dbEngine, introspectorFactoryType, "AAI-TEST");
        dbMethHelper = new DbMethHelper(loader, spy);

        Vertex pserver1 = graph.addVertex("aai-node-type", "pserver", "hostname",
                "testSearchVertexByIdentityMap-pserver-hostname-01");
        Vertex pserver2 = graph.addVertex("aai-node-type", "pserver", "hostname",
                "testSearchVertexByIdentityMap-pserver-hostname-02");
        Vertex genericVnf1 = graph.addVertex("aai-node-type", "generic-vnf", "vnf-id", "key1", "vnf-name", "vnfName1");
        Vertex complex1 = graph.addVertex("aai-node-type", "complex", "physical-location-id", "id1");
        GraphTraversalSource g = graph.traversal();
        when(spy.asAdmin()).thenReturn(adminSpy);
        when(adminSpy.getTraversalSource()).thenReturn(g);
        when(adminSpy.getReadOnlyTraversalSource()).thenReturn(g);
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void testSearchVertexByIdentityMap(QueryStyle queryStyle) throws Exception {
        initDbMethHelperTest(queryStyle);
        String type = "pserver";
        Map<String, Object> map = new HashMap<>();
        map.put("pserver.hostname", "testSearchVertexByIdentityMap-pserver-hostname-01");

        Optional<Vertex> optionalVertex;
        try {
            optionalVertex = dbMethHelper.searchVertexByIdentityMap(type, map);
        } catch (Exception e) {
            throw new Exception(e);
        }
        Assertions.assertEquals("vp[hostname->testSearchVertexById]", optionalVertex.get().property("hostname").toString());
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void testSearchVertexByIdentityMap_throwAmbiguousMapAAIException(QueryStyle queryStyle)
            throws AmbiguousMapAAIException, Exception {
        initDbMethHelperTest(queryStyle);
        assertThrows(AmbiguousMapAAIException.class, () -> {
            String type = "pserver";
            Map<String, Object> map = new HashMap<>();
            map.put("pserver.hostname", "testSearchVertexByIdentityMap-pserver-hostname-01");
            map.put("complex.physical-location-id", "id1");

            Optional<Vertex> optionalVertex;
            try {
                optionalVertex = dbMethHelper.searchVertexByIdentityMap(type, map);
            } catch (AmbiguousMapAAIException e) {
                throw new AmbiguousMapAAIException(e);
            }
        });
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void testLocateUniqueVertex(QueryStyle queryStyle) throws Exception {
        initDbMethHelperTest(queryStyle);
        String type = "complex";
        Map<String, Object> map = new HashMap<>();
        Vertex complex2 = graph.addVertex("aai-node-type", "complex", "physical-location-id", "id2");
        map.put("physical-location-id", "id2");
        Optional<Vertex> optionalVertex = dbMethHelper.locateUniqueVertex(type, map);
        Assertions.assertEquals("vp[physical-location-id->id2]",
                optionalVertex.get().property("physical-location-id").toString());
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void testLocateUniqueVertex_throwsException(QueryStyle queryStyle) throws AAIException, Exception {
        initDbMethHelperTest(queryStyle);
        assertThrows(AAIException.class, () -> {
            String type = "Pserver";
            Map<String, Object> map = new HashMap<>();
            Introspector obj =
                    loader.unmarshal("relationship", this.getJsonString("related-link-and-relationship-data.json"));
            map.put("hostname", "testSearchVertexByIdentityMap-pserver-hostname-01");
            dbMethHelper.locateUniqueVertex(type, map);
        });
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void testLocateUniqueVertex_returnNull(QueryStyle queryStyle) throws Exception {
        initDbMethHelperTest(queryStyle);
        String type = "complex";
        Map<String, Object> map = new HashMap<>();
        map.put("physical-location-id", "bogusId");
        Optional<Vertex> optionalVertex = dbMethHelper.locateUniqueVertex(type, map);
        Assertions.assertEquals(optionalVertex, Optional.empty());
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void testGetVertexProperties(QueryStyle queryStyle) throws Exception {
        initDbMethHelperTest(queryStyle);
        Vertex pserver3 =
                graph.addVertex("aai-node-type", "pserver", "hostname", "testGetVertexProperties-pserver-hostname-01",
                        "ptnii-equip-name", "testGetVertexProperties-pserver-ptnii-equip-name-01");
        String type = "pserver";
        Map<String, Object> map = new HashMap<>();
        map.put("pserver.hostname", "testGetVertexProperties-pserver-hostname-01");

        Optional<Vertex> optionalVertex;
        try {
            optionalVertex = dbMethHelper.searchVertexByIdentityMap(type, map);
        } catch (Exception e) {
            throw new Exception(e);
        }

        Vertex v = optionalVertex.get();
        List<String> vertexProperties = dbMethHelper.getVertexProperties(v);
        Assertions.assertTrue(vertexProperties.contains("Prop: [aai-node-type], val = [pserver] "));
        Assertions.assertTrue(
                vertexProperties.contains("Prop: [hostname], val = [testGetVertexProperties-pserver-hostname-01] "));
        Assertions.assertTrue(vertexProperties
                .contains("Prop: [ptnii-equip-name], val = [testGetVertexProperties-pserver-ptnii-equip-name-01] "));
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void testGetVertexProperties_nullParameter(QueryStyle queryStyle) {
        initDbMethHelperTest(queryStyle);
        List<String> vertexProperties = dbMethHelper.getVertexProperties(null);
        Assertions.assertTrue(vertexProperties.contains("null Node object passed to showPropertiesForNode()\n"));
        Assertions.assertTrue(vertexProperties.size() == 1);
    }

    private String getJsonString(String filename) throws IOException {
        FileInputStream is = new FileInputStream("src/test/resources/bundleconfig-local/etc/relationship/" + filename);
        String s = IOUtils.toString(is, "UTF-8");
        IOUtils.closeQuietly(is);
        return s;
    }

    @AfterEach
    public void tearDown() throws Exception {
        engine.rollback();
        dbEngine.rollback();
    }

    @AfterAll
    public static void destroy() throws Exception {
        graph.close();
    }

    public void initDbMethHelperTest(QueryStyle queryStyle) {
        this.queryStyle = queryStyle;
    }
}
