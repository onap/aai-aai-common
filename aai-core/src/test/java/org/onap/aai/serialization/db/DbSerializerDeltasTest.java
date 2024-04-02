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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraphFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.onap.aai.AAISetup;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.serialization.engines.JanusGraphDBEngine;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.util.delta.DeltaAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestPropertySource(properties = {"delta.events.enabled=true",})
public class DbSerializerDeltasTest extends AAISetup {

    protected static Graph graph;

    @Autowired
    protected EdgeSerializer edgeSer;
    @Autowired
    protected EdgeIngestor ei;

    private SchemaVersion version;
    private final ModelType introspectorFactoryType = ModelType.MOXY;
    private Loader loader;
    private TransactionalGraphEngine dbEngine;
    private TransactionalGraphEngine engine;
    public QueryStyle queryStyle;

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {{QueryStyle.TRAVERSAL}, {QueryStyle.TRAVERSAL_URI}});
    }

    @BeforeAll
    public static void init() throws Exception {
        graph = JanusGraphFactory.build().set("storage.backend", "inmemory").open();

    }

    @BeforeEach
    public void setup() throws Exception {
        // createGraph();
        version = schemaVersions.getDefaultVersion();
        loader = loaderFactory.createLoaderForVersion(introspectorFactoryType, version);
        dbEngine = new JanusGraphDBEngine(queryStyle, loader);
        engine = new JanusGraphDBEngine(queryStyle, loader);
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void createTopLevelThenUpdateTest(QueryStyle queryStyle) throws AAIException, UnsupportedEncodingException, URISyntaxException {
        initDbSerializerDeltasTest(queryStyle);
        engine.startTransaction();

        DBSerializer dbserLocal =
                new DBSerializer(version, engine, introspectorFactoryType, "AAI-TEST", AAIProperties.MINIMUM_DEPTH);
        Introspector gvnf = loader.introspectorFromName("generic-vnf");
        Vertex gvnfVert = dbserLocal.createNewVertex(gvnf);
        QueryParser uriQuery =
                dbEngine.getQueryBuilder().createQueryFromURI(new URI("/network/generic-vnfs/generic-vnf/myvnf"));

        gvnf.setValue("vnf-id", "myvnf");
        gvnf.setValue("vnf-type", "typo");
        dbserLocal.serializeToDb(gvnf, gvnfVert, uriQuery, "generic-vnf", gvnf.marshal(false));
        assertTrue(engine.tx().traversal().V().has("aai-node-type", "generic-vnf")
                .has("vnf-id", "myvnf").has("vnf-type", "typo").hasNext(), "Original created vertex exists");

        assertEquals(DeltaAction.CREATE,
                dbserLocal.getObjectDeltas().get("/network/generic-vnfs/generic-vnf/myvnf").getAction());
        assertEquals(4L, dbserLocal.getObjectDeltas().get("/network/generic-vnfs/generic-vnf/myvnf").getPropertyDeltas()
                .values().stream().filter(d -> d.getAction().equals(DeltaAction.STATIC)).count());
        assertEquals(5L, dbserLocal.getObjectDeltas().get("/network/generic-vnfs/generic-vnf/myvnf").getPropertyDeltas()
                .values().stream().filter(d -> d.getAction().equals(DeltaAction.CREATE)).count());
        dbserLocal.getObjectDeltas().values().forEach(od -> {
            if (!od.getPropertyDeltas().containsKey(AAIProperties.AAI_UUID)) {
                fail(od.getUri() + " is missing " + AAIProperties.AAI_UUID);
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID) == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " is null");
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID).getValue() == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " value is null");
            }
        });

        gvnf = dbserLocal.getLatestVersionView(gvnfVert);
        gvnf.setValue("vnf-type", "new-typo");
        dbserLocal =
                new DBSerializer(version, engine, introspectorFactoryType, "AAI-TEST", AAIProperties.MINIMUM_DEPTH);
        dbserLocal.serializeToDb(gvnf, gvnfVert, uriQuery, "generic-vnf", gvnf.marshal(false));
        assertTrue(engine.tx().traversal().V().has("aai-node-type", "generic-vnf")
                .has("vnf-id", "myvnf").has("vnf-type", "new-typo").hasNext(), "Vertex is updated");

        assertEquals(DeltaAction.UPDATE,
                dbserLocal.getObjectDeltas().get("/network/generic-vnfs/generic-vnf/myvnf").getAction());
        assertEquals(4L, dbserLocal.getObjectDeltas().get("/network/generic-vnfs/generic-vnf/myvnf").getPropertyDeltas()
                .values().stream().filter(d -> d.getAction().equals(DeltaAction.STATIC)).count());
        assertEquals(4L, dbserLocal.getObjectDeltas().get("/network/generic-vnfs/generic-vnf/myvnf").getPropertyDeltas()
                .values().stream().filter(d -> d.getAction().equals(DeltaAction.UPDATE)).count());
        dbserLocal.getObjectDeltas().values().forEach(od -> {
            if (!od.getPropertyDeltas().containsKey(AAIProperties.AAI_UUID)) {
                fail(od.getUri() + " is missing " + AAIProperties.AAI_UUID);
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID) == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " is null");
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID).getValue() == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " value is null");
            }
        });
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void createTopLevelThenCreateChildTest(QueryStyle queryStyle)
            throws AAIException, UnsupportedEncodingException, URISyntaxException {
        initDbSerializerDeltasTest(queryStyle);
        engine.startTransaction();

        DBSerializer dbserLocal =
                new DBSerializer(version, engine, introspectorFactoryType, "AAI-TEST", AAIProperties.MINIMUM_DEPTH);
        Introspector gvnf = loader.introspectorFromName("generic-vnf");
        Vertex gvnfVert = dbserLocal.createNewVertex(gvnf);
        final String vnfUri = "/network/generic-vnfs/generic-vnf/myvnf";
        QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(new URI(vnfUri));

        gvnf.setValue("vnf-id", "myvnf");
        gvnf.setValue("vnf-type", "typo");
        dbserLocal.serializeToDb(gvnf, gvnfVert, uriQuery, "generic-vnf", gvnf.marshal(false));
        assertTrue(engine.tx().traversal().V().has("aai-node-type", "generic-vnf")
                .has("vnf-id", "myvnf").has("vnf-type", "typo").hasNext(), "Original created vertex exists");

        assertEquals(DeltaAction.CREATE, dbserLocal.getObjectDeltas().get(vnfUri).getAction());
        assertEquals(4L, dbserLocal.getObjectDeltas().get(vnfUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.STATIC)).count());
        assertEquals(5L, dbserLocal.getObjectDeltas().get(vnfUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.CREATE)).count());
        dbserLocal.getObjectDeltas().values().forEach(od -> {
            if (!od.getPropertyDeltas().containsKey(AAIProperties.AAI_UUID)) {
                fail(od.getUri() + " is missing " + AAIProperties.AAI_UUID);
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID) == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " is null");
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID).getValue() == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " value is null");
            }
        });

        dbserLocal =
                new DBSerializer(version, engine, introspectorFactoryType, "AAI-TEST", AAIProperties.MINIMUM_DEPTH);
        Introspector vf = loader.introspectorFromName("vf-module");
        Vertex vfVertex = dbserLocal.createNewVertex(vf);
        final String vfUri = "/network/generic-vnfs/generic-vnf/myvnf/vf-modules/vf-module/myvf";
        uriQuery = engine.getQueryBuilder(gvnfVert).createQueryFromURI(new URI(vfUri));

        vf.setValue("vf-module-id", "myvf");
        dbserLocal.serializeToDb(vf, vfVertex, uriQuery, "vf-module", vf.marshal(false));
        assertTrue(engine.tx().traversal().V().has("aai-node-type", "vf-module").has("vf-module-id", "myvf").hasNext(),
                "Vertex is creted");
        assertTrue(engine.tx().traversal().V().has("aai-node-type", "vf-module").has("vf-module-id", "myvf").both()
                        .has("aai-node-type", "generic-vnf").has("vnf-id", "myvnf").hasNext(),
                "Vf module has edge to gvnf");

        assertEquals(DeltaAction.CREATE, dbserLocal.getObjectDeltas().get(vfUri).getAction());
        assertEquals(4L, dbserLocal.getObjectDeltas().get(vfUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.STATIC)).count());
        assertEquals(4L, dbserLocal.getObjectDeltas().get(vfUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.CREATE)).count());
        dbserLocal.getObjectDeltas().values().forEach(od -> {
            if (!od.getPropertyDeltas().containsKey(AAIProperties.AAI_UUID)) {
                fail(od.getUri() + " is missing " + AAIProperties.AAI_UUID);
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID) == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " is null");
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID).getValue() == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " value is null");
            }
        });
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void createTopWithChildThenDeleteTopTest(QueryStyle queryStyle)
            throws AAIException, UnsupportedEncodingException, URISyntaxException {
        initDbSerializerDeltasTest(queryStyle);
        engine.startTransaction();

        DBSerializer dbserLocal =
                new DBSerializer(version, engine, introspectorFactoryType, "AAI-TEST", AAIProperties.MINIMUM_DEPTH);
        Introspector gvnf = loader.introspectorFromName("generic-vnf");
        Vertex gvnfVert = dbserLocal.createNewVertex(gvnf);
        final String vnfUri = "/network/generic-vnfs/generic-vnf/myvnf";
        QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(new URI(vnfUri));

        gvnf.setValue("vnf-id", "myvnf");
        gvnf.setValue("vnf-type", "typo");

        Introspector vf = loader.introspectorFromName("vf-module");
        vf.setValue("vf-module-id", "myvf");
        final String vfUri = "/network/generic-vnfs/generic-vnf/myvnf/vf-modules/vf-module/myvf";

        Introspector vfs = loader.introspectorFromName("vf-modules");
        vfs.setValue("vf-module", Collections.singletonList(vf.getUnderlyingObject()));
        gvnf.setValue("vf-modules", vfs.getUnderlyingObject());

        dbserLocal.serializeToDb(gvnf, gvnfVert, uriQuery, "generic-vnf", gvnf.marshal(false));

        Gson gson = new GsonBuilder().create();
        System.out.println(gson.toJsonTree(dbserLocal.getObjectDeltas().values()));

        assertTrue(engine.tx().traversal().V().has("aai-node-type", "generic-vnf")
                .has("vnf-id", "myvnf").has("vnf-type", "typo").hasNext(), "Original created vertex exists");
        assertTrue(engine.tx().traversal().V().has("aai-node-type", "vf-module").has("vf-module-id", "myvf").hasNext(),
                "Vertex is creted");
        assertTrue(engine.tx().traversal().V().has("aai-node-type", "vf-module").has("vf-module-id", "myvf").both()
                        .has("aai-node-type", "generic-vnf").has("vnf-id", "myvnf").hasNext(),
                "Vf module has edge to gvnf");

        assertEquals(DeltaAction.CREATE, dbserLocal.getObjectDeltas().get(vnfUri).getAction());
        assertEquals(4L, dbserLocal.getObjectDeltas().get(vnfUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.STATIC)).count());
        assertEquals(5L, dbserLocal.getObjectDeltas().get(vnfUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.CREATE)).count());
        assertEquals(DeltaAction.CREATE, dbserLocal.getObjectDeltas().get(vfUri).getAction());
        assertEquals(4L, dbserLocal.getObjectDeltas().get(vfUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.STATIC)).count());
        assertEquals(4L, dbserLocal.getObjectDeltas().get(vfUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.CREATE)).count());
        assertEquals(1L, dbserLocal.getObjectDeltas().get(vfUri).getRelationshipDeltas().stream()
                .filter(d -> d.getAction().equals(DeltaAction.CREATE_REL)).count());
        dbserLocal.getObjectDeltas().values().forEach(od -> {
            if (!od.getPropertyDeltas().containsKey(AAIProperties.AAI_UUID)) {
                fail(od.getUri() + " is missing " + AAIProperties.AAI_UUID);
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID) == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " is null");
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID).getValue() == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " value is null");
            }
        });

        dbserLocal =
                new DBSerializer(version, engine, introspectorFactoryType, "AAI-TEST", AAIProperties.MINIMUM_DEPTH);
        gvnf = dbserLocal.getLatestVersionView(gvnfVert);
        String rv = gvnf.getValue(AAIProperties.RESOURCE_VERSION);
        dbserLocal.delete(engine.tx().traversal().V(gvnfVert).next(), rv, true);
        System.out.println(gson.toJsonTree(dbserLocal.getObjectDeltas().values()));

        assertFalse(engine.tx().traversal().V().has("aai-node-type", "generic-vnf").hasNext(),
                "generic-vnf no longer exists");
        assertFalse(engine.tx().traversal().V().has("aai-node-type", "vf-module").hasNext(),
                "vf-module no longer exists");

        assertEquals(DeltaAction.DELETE, dbserLocal.getObjectDeltas().get(vnfUri).getAction());
        assertEquals(12L, dbserLocal.getObjectDeltas().get(vnfUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.DELETE)).count());
        assertEquals(DeltaAction.DELETE, dbserLocal.getObjectDeltas().get(vfUri).getAction());
        assertEquals(11L, dbserLocal.getObjectDeltas().get(vfUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.DELETE)).count());
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void createComplexPserverWithRelDeleteRel(QueryStyle queryStyle)
            throws AAIException, UnsupportedEncodingException, URISyntaxException {
        initDbSerializerDeltasTest(queryStyle);
        engine.startTransaction();

        DBSerializer dbserLocal =
                new DBSerializer(version, engine, introspectorFactoryType, "AAI-TEST", AAIProperties.MINIMUM_DEPTH);
        Introspector complex = loader.introspectorFromName("complex");
        Vertex complexV = dbserLocal.createNewVertex(complex);
        final String complexUri = "/cloud-infrastructure/complexes/complex/c-id";
        QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(new URI(complexUri));

        complex.setValue("physical-location-id", "c-id");
        complex.setValue("physical-location-type", "type");
        complex.setValue("street1", "streetA");
        complex.setValue("city", "cityA");
        complex.setValue("postal-code", "11111");
        complex.setValue("country", "abc");
        complex.setValue("region", "ef");
        dbserLocal.serializeToDb(complex, complexV, uriQuery, "complex", complex.marshal(false));
        assertTrue(engine.tx().traversal().V().has("aai-node-type", "complex")
                .has("physical-location-id", "c-id").hasNext(), "Complex created");

        assertEquals(DeltaAction.CREATE, dbserLocal.getObjectDeltas().get(complexUri).getAction());
        assertEquals(4L, dbserLocal.getObjectDeltas().get(complexUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.STATIC)).count());
        assertEquals(10L, dbserLocal.getObjectDeltas().get(complexUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.CREATE)).count());
        dbserLocal.getObjectDeltas().values().forEach(od -> {
            if (!od.getPropertyDeltas().containsKey(AAIProperties.AAI_UUID)) {
                fail(od.getUri() + " is missing " + AAIProperties.AAI_UUID);
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID) == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " is null");
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID).getValue() == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " value is null");
            }
        });

        dbserLocal =
                new DBSerializer(version, engine, introspectorFactoryType, "AAI-TEST", AAIProperties.MINIMUM_DEPTH);
        Introspector pserver = loader.introspectorFromName("pserver");
        Vertex pserverV = dbserLocal.createNewVertex(pserver);
        final String pserverUri = "/cloud-infrastructure/pservers/pserver/ps";
        uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(new URI(pserverUri));

        Introspector relationship = loader.introspectorFromName("relationship");
        relationship.setValue("related-to", "complex");
        relationship.setValue("related-link", complexUri);
        Introspector relationshipList = loader.introspectorFromName("relationship-list");
        relationshipList.setValue("relationship", Collections.singletonList(relationship.getUnderlyingObject()));

        pserver.setValue("relationship-list", relationshipList.getUnderlyingObject());
        pserver.setValue("hostname", "ps");

        System.out.println(pserver.marshal(true));

        dbserLocal.serializeToDb(pserver, pserverV, uriQuery, "pserver", pserver.marshal(false));
        assertTrue(engine.tx().traversal().V().has("aai-node-type", "pserver").has("hostname", "ps").hasNext(),
                "Pserver created");
        assertTrue(engine.tx().traversal().V().has("aai-node-type", "pserver")
                .has("hostname", "ps").bothE().otherV().has("aai-node-type", "complex").hasNext(), "Pserver has edge to complex");

        assertEquals(DeltaAction.CREATE, dbserLocal.getObjectDeltas().get(pserverUri).getAction());
        assertEquals(4L, dbserLocal.getObjectDeltas().get(pserverUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.STATIC)).count());
        assertEquals(4L, dbserLocal.getObjectDeltas().get(pserverUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.CREATE)).count());
        assertEquals(1L, dbserLocal.getObjectDeltas().get(pserverUri).getRelationshipDeltas().stream()
                .filter(d -> d.getAction().equals(DeltaAction.CREATE_REL)).count());
        dbserLocal.getObjectDeltas().values().forEach(od -> {
            if (!od.getPropertyDeltas().containsKey(AAIProperties.AAI_UUID)) {
                fail(od.getUri() + " is missing " + AAIProperties.AAI_UUID);
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID) == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " is null");
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID).getValue() == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " value is null");
            }
        });

        dbserLocal =
                new DBSerializer(version, engine, introspectorFactoryType, "AAI-TEST", AAIProperties.MINIMUM_DEPTH);
        dbserLocal.touchStandardVertexProperties(pserverV, false);
        dbserLocal.deleteEdge(relationship, pserverV);
        assertFalse(engine.tx().traversal().V().has("aai-node-type", "pserver")
                .has("hostname", "ps").bothE().otherV().has("aai-node-type", "complex").hasNext(), "Pserver no longer has edge to complex");

        assertEquals(DeltaAction.UPDATE, dbserLocal.getObjectDeltas().get(pserverUri).getAction());
        assertEquals(4L, dbserLocal.getObjectDeltas().get(pserverUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.STATIC)).count());
        assertEquals(3L, dbserLocal.getObjectDeltas().get(pserverUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.UPDATE)).count());
        assertEquals(1L, dbserLocal.getObjectDeltas().get(pserverUri).getRelationshipDeltas().stream()
                .filter(d -> d.getAction().equals(DeltaAction.DELETE_REL)).count());
        dbserLocal.getObjectDeltas().values().forEach(od -> {
            if (!od.getPropertyDeltas().containsKey(AAIProperties.AAI_UUID)) {
                fail(od.getUri() + " is missing " + AAIProperties.AAI_UUID);
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID) == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " is null");
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID).getValue() == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " value is null");
            }
        });
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void createComplexPserverWithRelUpdatePserverToDeleteRelAddPinterfaceThenDeleteComplex(QueryStyle queryStyle)
            throws AAIException, UnsupportedEncodingException, URISyntaxException {
        initDbSerializerDeltasTest(queryStyle);
        engine.startTransaction();

        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create();

        DBSerializer dbserLocal = new DBSerializer(version, engine, introspectorFactoryType, "create-complex",
                AAIProperties.MINIMUM_DEPTH);
        Introspector complex = loader.introspectorFromName("complex");
        Vertex complexV = dbserLocal.createNewVertex(complex);
        final String complexUri = "/cloud-infrastructure/complexes/complex/c-id-b";
        QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(new URI(complexUri));

        complex.setValue("physical-location-id", "c-id-b");
        complex.setValue("physical-location-type", "type");
        complex.setValue("street1", "streetA");
        complex.setValue("city", "cityA");
        complex.setValue("postal-code", "11111");
        complex.setValue("country", "abc");
        complex.setValue("region", "ef");
        dbserLocal.serializeToDb(complex, complexV, uriQuery, "complex", complex.marshal(false));

        System.out.println("Create Complex");
        System.out.println(gson.toJsonTree(dbserLocal.getObjectDeltas().values()));

        assertTrue(engine.tx().traversal().V().has("aai-node-type", "complex")
                .has("physical-location-id", "c-id-b").hasNext(), "Complex created");
        assertEquals(DeltaAction.CREATE, dbserLocal.getObjectDeltas().get(complexUri).getAction());
        assertEquals(4L, dbserLocal.getObjectDeltas().get(complexUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.STATIC)).count());
        assertEquals(10L, dbserLocal.getObjectDeltas().get(complexUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.CREATE)).count());
        dbserLocal.getObjectDeltas().values().forEach(od -> {
            if (!od.getPropertyDeltas().containsKey(AAIProperties.AAI_UUID)) {
                fail(od.getUri() + " is missing " + AAIProperties.AAI_UUID);
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID) == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " is null");
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID).getValue() == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " value is null");
            }
        });

        dbserLocal = new DBSerializer(version, engine, introspectorFactoryType, "create-pserver",
                AAIProperties.MINIMUM_DEPTH);
        Introspector pserver = loader.introspectorFromName("pserver");
        Vertex pserverV = dbserLocal.createNewVertex(pserver);
        final String pserverUri = "/cloud-infrastructure/pservers/pserver/ps-b";
        uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(new URI(pserverUri));

        Introspector relationship = loader.introspectorFromName("relationship");
        relationship.setValue("related-to", "complex");
        relationship.setValue("related-link", complexUri);
        Introspector relationshipList = loader.introspectorFromName("relationship-list");
        relationshipList.setValue("relationship", Collections.singletonList(relationship.getUnderlyingObject()));

        pserver.setValue("relationship-list", relationshipList.getUnderlyingObject());
        pserver.setValue("hostname", "ps-b");
        pserver.setValue("number-of-cpus", 20);

        Introspector pint = loader.introspectorFromName("p-interface");
        pint.setValue("interface-name", "pint-1");
        final String pintUri = pserverUri + "/p-interfaces/p-interface/pint-1";

        Introspector pints = loader.introspectorFromName("p-interfaces");
        pints.setValue("p-interface", Collections.singletonList(pint.getUnderlyingObject()));
        pserver.setValue("p-interfaces", pints.getUnderlyingObject());

        dbserLocal.serializeToDb(pserver, pserverV, uriQuery, "pserver", pserver.marshal(false));

        System.out.println("Create Pserver with pinterface and relationship to complex ");
        System.out.println(gson.toJsonTree(dbserLocal.getObjectDeltas().values()));

        assertTrue(engine.tx().traversal().V().has("aai-node-type", "pserver").has("hostname", "ps-b").hasNext(),
                "Pserver created");
        assertTrue(engine.tx().traversal().V().has("aai-node-type", "pserver")
                .has("hostname", "ps-b").bothE().otherV().has("aai-node-type", "complex").hasNext(), "Pserver has edge to complex");
        assertTrue(engine.tx().traversal().V().has("aai-node-type", "pserver")
                .has("hostname", "ps-b").bothE().otherV().has("aai-node-type", "p-interface").hasNext(), "Pserver has edge to pinterface");

        assertEquals(DeltaAction.CREATE, dbserLocal.getObjectDeltas().get(pserverUri).getAction());
        assertEquals(4L, dbserLocal.getObjectDeltas().get(pserverUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.STATIC)).count());
        assertEquals(5L, dbserLocal.getObjectDeltas().get(pserverUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.CREATE)).count());
        assertEquals(1L, dbserLocal.getObjectDeltas().get(pserverUri).getRelationshipDeltas().stream()
                .filter(d -> d.getAction().equals(DeltaAction.CREATE_REL)).count());
        assertEquals(DeltaAction.CREATE, dbserLocal.getObjectDeltas().get(pintUri).getAction());
        assertEquals(4L, dbserLocal.getObjectDeltas().get(pintUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.STATIC)).count());
        assertEquals(4L, dbserLocal.getObjectDeltas().get(pintUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.CREATE)).count());
        assertEquals(1L, dbserLocal.getObjectDeltas().get(pintUri).getRelationshipDeltas().stream()
                .filter(d -> d.getAction().equals(DeltaAction.CREATE_REL)).count());
        dbserLocal.getObjectDeltas().values().forEach(od -> {
            if (!od.getPropertyDeltas().containsKey(AAIProperties.AAI_UUID)) {
                fail(od.getUri() + " is missing " + AAIProperties.AAI_UUID);
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID) == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " is null");
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID).getValue() == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " value is null");
            }
        });

        dbserLocal = new DBSerializer(version, engine, introspectorFactoryType, "update-pserver",
                AAIProperties.MINIMUM_DEPTH);
        pserver = dbserLocal.getLatestVersionView(pserverV);
        relationshipList = loader.introspectorFromName("relationship-list");
        relationshipList.setValue("relationship", Collections.emptyList());
        pserver.setValue("relationship-list", relationshipList.getUnderlyingObject());
        pserver.setValue("equip-type", "server-a");
        pserver.setValue("number-of-cpus", 99);

        dbserLocal.serializeToDb(pserver, pserverV, uriQuery, "pserver", pserver.marshal(false));

        System.out.println("Update pserver removing relationship to complex");
        System.out.println(gson.toJsonTree(dbserLocal.getObjectDeltas().values()));

        assertFalse(engine.tx().traversal().V().has("aai-node-type", "pserver")
                .has("hostname", "ps-b").bothE().otherV().has("aai-node-type", "complex").hasNext(), "Pserver no longer has edge to complex");

        assertEquals(DeltaAction.UPDATE, dbserLocal.getObjectDeltas().get(pserverUri).getAction());
        assertEquals(4L, dbserLocal.getObjectDeltas().get(pserverUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.STATIC)).count());
        assertEquals(4L, dbserLocal.getObjectDeltas().get(pserverUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.UPDATE)).count());
        assertEquals(1L, dbserLocal.getObjectDeltas().get(pserverUri).getRelationshipDeltas().stream()
                .filter(d -> d.getAction().equals(DeltaAction.DELETE_REL)).count());
        assertFalse(dbserLocal.getObjectDeltas().containsKey(pintUri));
        dbserLocal.getObjectDeltas().values().forEach(od -> {
            if (!od.getPropertyDeltas().containsKey(AAIProperties.AAI_UUID)) {
                fail(od.getUri() + " is missing " + AAIProperties.AAI_UUID);
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID) == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " is null");
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID).getValue() == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " value is null");
            }
        });

        dbserLocal = new DBSerializer(version, engine, introspectorFactoryType, "delete-pserver",
                AAIProperties.MINIMUM_DEPTH);
        pserver = dbserLocal.getLatestVersionView(pserverV);
        String rv = pserver.getValue(AAIProperties.RESOURCE_VERSION);
        dbserLocal.delete(engine.tx().traversal().V(pserverV).next(), rv, true);

        System.out.println("Delete pserver");
        System.out.println(gson.toJsonTree(dbserLocal.getObjectDeltas().values()));

        assertFalse(engine.tx().traversal().V().has("aai-node-type", "pserver").hasNext(), "pserver no longer exists");

        assertEquals(DeltaAction.DELETE, dbserLocal.getObjectDeltas().get(pserverUri).getAction());
        assertEquals(12L, dbserLocal.getObjectDeltas().get(pserverUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.DELETE)).count());
        assertEquals(DeltaAction.DELETE, dbserLocal.getObjectDeltas().get(pintUri).getAction());
        assertEquals(10L, dbserLocal.getObjectDeltas().get(pintUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.DELETE)).count());
        dbserLocal.getObjectDeltas().values().forEach(od -> {
            if (!od.getPropertyDeltas().containsKey(AAIProperties.AAI_UUID)) {
                fail(od.getUri() + " is missing " + AAIProperties.AAI_UUID);
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID) == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " is null");
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID).getValue() == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " value is null");
            }
        });
    }

    // /network/ipsec-configurations/ipsec-configuration/{ipsec-configuration-id}/vig-servers/vig-server/{vig-address-type}
    // ipaddress-v4-vig
    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void createNodeWithListTest(QueryStyle queryStyle) throws AAIException, UnsupportedEncodingException, URISyntaxException {
        initDbSerializerDeltasTest(queryStyle);
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create();

        engine.startTransaction();

        /*
         * Create the parent ipsec-configuration
         */
        DBSerializer dbserLocal =
                new DBSerializer(version, engine, introspectorFactoryType, "create-ipsec", AAIProperties.MINIMUM_DEPTH);
        Introspector ipsec = loader.introspectorFromName("ipsec-configuration");
        Vertex ipsecVert = dbserLocal.createNewVertex(ipsec);
        final String ipsecUri = "/network/ipsec-configurations/ipsec-configuration/ipsec";
        QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(new URI(ipsecUri));

        ipsec.setValue("ipsec-configuration-id", "ipsec");
        dbserLocal.serializeToDb(ipsec, ipsecVert, uriQuery, "generic-vnf", ipsec.marshal(false));
        assertTrue(engine.tx().traversal().V()
                .has("aai-node-type", "ipsec-configuration").has("ipsec-configuration-id", "ipsec").hasNext(), "Original created vertex exists");

        System.out.println(gson.toJsonTree(dbserLocal.getObjectDeltas().values()));
        assertEquals(DeltaAction.CREATE, dbserLocal.getObjectDeltas().get(ipsecUri).getAction());
        assertEquals(4L, dbserLocal.getObjectDeltas().get(ipsecUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.STATIC)).count());
        assertEquals(4L, dbserLocal.getObjectDeltas().get(ipsecUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.CREATE)).count());
        dbserLocal.getObjectDeltas().values().forEach(od -> {
            if (!od.getPropertyDeltas().containsKey(AAIProperties.AAI_UUID)) {
                fail(od.getUri() + " is missing " + AAIProperties.AAI_UUID);
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID) == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " is null");
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID).getValue() == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " value is null");
            }
        });

        /*
         * Create child vig-server with list property vig-address-type
         */
        dbserLocal = new DBSerializer(version, engine, introspectorFactoryType, "create-child-vig-server",
                AAIProperties.MINIMUM_DEPTH);
        Introspector vig = loader.introspectorFromName("vig-server");
        Vertex vigVertex = dbserLocal.createNewVertex(vig);
        final String vigUri = "/network/ipsec-configurations/ipsec-configuration/ipsec/vig-servers/vig-server/vig";
        uriQuery = engine.getQueryBuilder(ipsecVert).createQueryFromURI(new URI(vigUri));

        vig.setValue("vig-address-type", "vig");
        List<String> list = new ArrayList<>();
        list.add("address-1");
        list.add("address-2");
        vig.setValue("ipaddress-v4-vig", list);
        dbserLocal.serializeToDb(vig, vigVertex, uriQuery, "vf-module", vig.marshal(false));
        assertTrue(engine.tx().traversal().V().has("aai-node-type", "vig-server")
                .has("vig-address-type", "vig").hasNext(), "Vertex is creted");
        assertTrue(engine.tx().traversal().V().has("aai-node-type", "vig-server").has("vig-address-type", "vig").both()
                        .has("aai-node-type", "ipsec-configuration").has("ipsec-configuration-id", "ipsec").hasNext(),
                "Vf module has edge to gvnf");

        System.out.println(gson.toJsonTree(dbserLocal.getObjectDeltas().values()));
        assertEquals(DeltaAction.CREATE, dbserLocal.getObjectDeltas().get(vigUri).getAction());
        assertEquals(4L, dbserLocal.getObjectDeltas().get(vigUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.STATIC)).count());
        assertEquals(5L, dbserLocal.getObjectDeltas().get(vigUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.CREATE)).count());
        assertThat(dbserLocal.getObjectDeltas().get(vigUri).getPropertyDeltas().get("ipaddress-v4-vig").getValue(),
                instanceOf(List.class));
        dbserLocal.getObjectDeltas().values().forEach(od -> {
            if (!od.getPropertyDeltas().containsKey(AAIProperties.AAI_UUID)) {
                fail(od.getUri() + " is missing " + AAIProperties.AAI_UUID);
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID) == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " is null");
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID).getValue() == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " value is null");
            }
        });

        /*
         * Update child vig-server with new list for vig-address-type
         */
        dbserLocal = new DBSerializer(version, engine, introspectorFactoryType, "update-child-vig-server",
                AAIProperties.MINIMUM_DEPTH);
        vig = dbserLocal.getLatestVersionView(vigVertex);
        uriQuery = engine.getQueryBuilder(ipsecVert).createQueryFromURI(new URI(vigUri));

        new ArrayList<>();
        list.add("address-3");
        list.add("address-4");
        vig.setValue("ipaddress-v4-vig", list);
        dbserLocal.serializeToDb(vig, vigVertex, uriQuery, "vf-module", vig.marshal(false));
        assertTrue(engine.tx().traversal().V().has("aai-node-type", "vig-server")
                .has("vig-address-type", "vig").hasNext(), "Vertex is still there");
        assertTrue(engine.tx().traversal().V().has("aai-node-type", "vig-server").has("vig-address-type", "vig").both()
                        .has("aai-node-type", "ipsec-configuration").has("ipsec-configuration-id", "ipsec").hasNext(),
                "Vf module has edge to gvnf");

        System.out.println(gson.toJsonTree(dbserLocal.getObjectDeltas().values()));
        assertEquals(DeltaAction.UPDATE, dbserLocal.getObjectDeltas().get(vigUri).getAction());
        assertEquals(4L, dbserLocal.getObjectDeltas().get(vigUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.STATIC)).count());
        assertEquals(4L, dbserLocal.getObjectDeltas().get(vigUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.UPDATE)).count());
        assertThat(dbserLocal.getObjectDeltas().get(vigUri).getPropertyDeltas().get("ipaddress-v4-vig").getValue(),
                instanceOf(List.class));
        assertThat(dbserLocal.getObjectDeltas().get(vigUri).getPropertyDeltas().get("ipaddress-v4-vig").getOldValue(),
                instanceOf(List.class));
        dbserLocal.getObjectDeltas().values().forEach(od -> {
            if (!od.getPropertyDeltas().containsKey(AAIProperties.AAI_UUID)) {
                fail(od.getUri() + " is missing " + AAIProperties.AAI_UUID);
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID) == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " is null");
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID).getValue() == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " value is null");
            }
        });
        /*
         * Delete top level
         */
        dbserLocal =
                new DBSerializer(version, engine, introspectorFactoryType, "delete-ipsec", AAIProperties.MINIMUM_DEPTH);
        ipsec = dbserLocal.getLatestVersionView(ipsecVert);
        String rv = ipsec.getValue(AAIProperties.RESOURCE_VERSION);
        dbserLocal.delete(engine.tx().traversal().V(ipsecVert).next(), rv, true);
        System.out.println(gson.toJsonTree(dbserLocal.getObjectDeltas().values()));

        assertFalse(engine.tx().traversal().V().has("aai-node-type", "ipsec-configuration").hasNext(),
                "ipsec-configuration no longer exists");
        assertFalse(engine.tx().traversal().V().has("aai-node-type", "vig-server").hasNext(),
                "vig-server no longer exists");

        assertEquals(DeltaAction.DELETE, dbserLocal.getObjectDeltas().get(ipsecUri).getAction());
        assertEquals(9L, dbserLocal.getObjectDeltas().get(ipsecUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.DELETE)).count());
        assertEquals(DeltaAction.DELETE, dbserLocal.getObjectDeltas().get(vigUri).getAction());
        assertEquals(10L, dbserLocal.getObjectDeltas().get(vigUri).getPropertyDeltas().values().stream()
                .filter(d -> d.getAction().equals(DeltaAction.DELETE)).count());
        dbserLocal.getObjectDeltas().values().forEach(od -> {
            if (!od.getPropertyDeltas().containsKey(AAIProperties.AAI_UUID)) {
                fail(od.getUri() + " is missing " + AAIProperties.AAI_UUID);
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID) == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " is null");
            } else if (od.getPropertyDeltas().get(AAIProperties.AAI_UUID).getValue() == null) {
                fail(od.getUri() + " " + AAIProperties.AAI_UUID + " value is null");
            }
        });
    }

    public void initDbSerializerDeltasTest(QueryStyle queryStyle) {
        this.queryStyle = queryStyle;
    }

}
