/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2025 Deutsche Telekom. All rights reserved.
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

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraphFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
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
import org.onap.aai.util.delta.DeltaEventsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@RunWith(value = Parameterized.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestPropertySource(properties = {"delta.events.enabled=true","delta.events.node-types=generic-vnf,vf-module",
                                    "delta.events.relationship-enabled=false"})
@EnableConfigurationProperties(DeltaEventsConfig.class)
public class DbSerializerDeltasDisabledTest extends AAISetup {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    protected static Graph graph;

    @Autowired
    protected EdgeSerializer edgeSer;
    @Autowired
    protected EdgeIngestor ei;

    private SchemaVersion version;
    private final ModelType introspectorFactoryType = ModelType.MOXY;
    private Loader loader;
    private TransactionalGraphEngine dbEngine;
    private TransactionalGraphEngine engine; // for tests that aren't mocking the engine

    @Parameterized.Parameter(value = 0)
    public QueryStyle queryStyle;

    @Parameterized.Parameters(name = "QueryStyle.{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {{QueryStyle.TRAVERSAL}, {QueryStyle.TRAVERSAL_URI}});
    }

    @BeforeClass
    public static void init() throws Exception {
        graph = JanusGraphFactory.build().set("storage.backend", "inmemory").open();

    }

    @Before
    public void setup() throws Exception {
        // createGraph();
        version = schemaVersions.getDefaultVersion();
        loader = loaderFactory.createLoaderForVersion(introspectorFactoryType, version);
        dbEngine = new JanusGraphDBEngine(queryStyle, loader);
        engine = new JanusGraphDBEngine(queryStyle, loader);
    }

    @Test
    public void verifyDeltaEventsForAbsentNodeType() throws AAIException, UnsupportedEncodingException, URISyntaxException {
        
        engine.startTransaction();
        DBSerializer dbserLocal =
                new DBSerializer(version, engine, introspectorFactoryType, "AAI-TEST", AAIProperties.MINIMUM_DEPTH);
        Introspector pnf = loader.introspectorFromName("pnf");
        Vertex pnfVert = dbserLocal.createNewVertex(pnf);

        QueryParser uriQuery =
                dbEngine.getQueryBuilder().createQueryFromURI(new URI("/network/pnfs/pnf/mypnf"));

        pnf.setValue("pnf-name", "mypnf");
        pnf.setValue("pnf-id", "mypnf-id");
        pnf.setValue("equip-type", "gnb");

        dbserLocal.serializeToDb(pnf, pnfVert, uriQuery, "pnf", pnf.marshal(false));
        assertEquals(dbserLocal.getObjectDeltas().size(), 0);
    }

    @Test
    public void checkRelationshipDeltaDisabledForCreateRel()
            throws AAIException, UnsupportedEncodingException, URISyntaxException {
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

        dbserLocal =
                new DBSerializer(version, engine, introspectorFactoryType, "AAI-TEST", AAIProperties.MINIMUM_DEPTH);
        Introspector vf = loader.introspectorFromName("vf-module");
        Vertex vfVertex = dbserLocal.createNewVertex(vf);
        final String vfUri = "/network/generic-vnfs/generic-vnf/myvnf/vf-modules/vf-module/myvf";
        uriQuery = engine.getQueryBuilder(gvnfVert).createQueryFromURI(new URI(vfUri));

        vf.setValue("vf-module-id", "myvf");
        dbserLocal.serializeToDb(vf, vfVertex, uriQuery, "vf-module", vf.marshal(false));
        assertTrue("Vertex is creted",
                engine.tx().traversal().V().has("aai-node-type", "vf-module").has("vf-module-id", "myvf").hasNext());
        assertTrue("Vf module has edge to gvnf",
                engine.tx().traversal().V().has("aai-node-type", "vf-module").has("vf-module-id", "myvf").both()
                        .has("aai-node-type", "generic-vnf").has("vnf-id", "myvnf").hasNext());
        assertEquals(0L, dbserLocal.getObjectDeltas().get(vfUri).getRelationshipDeltas().size());
    }

    @Test
    public void checkRelationshipDeltaDisabledForUpdateAndDeleteRel()
            throws AAIException, UnsupportedEncodingException, URISyntaxException {
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
        dbserLocal =
                new DBSerializer(version, engine, introspectorFactoryType, "AAI-TEST", AAIProperties.MINIMUM_DEPTH);
        gvnf = dbserLocal.getLatestVersionView(gvnfVert);
        String rv = gvnf.getValue(AAIProperties.RESOURCE_VERSION);
        dbserLocal.delete(engine.tx().traversal().V(gvnfVert).next(), rv, true);

        assertEquals(0L, dbserLocal.getObjectDeltas().get(vfUri).getRelationshipDeltas().size());

    }
    
}
