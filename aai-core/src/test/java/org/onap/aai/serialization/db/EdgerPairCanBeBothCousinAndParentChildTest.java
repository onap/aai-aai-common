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

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraphFactory;
import org.junit.*;
import org.junit.rules.ExpectedException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class EdgerPairCanBeBothCousinAndParentChildTest extends AAISetup {

    // to use, set thrown.expect to whatever your test needs
    // this line establishes default of expecting no exception to be thrown
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
    private TransactionalGraphEngine engine;

    public QueryStyle queryStyle = QueryStyle.TRAVERSAL_URI;

    public static final String SOURCE_OF_TRUTH = "EdgerPairCanBeBothCousinAndParentChildTest";
    private static final String gvnfAUri = "/network/generic-vnfs/generic-vnf/gvnf-a" + SOURCE_OF_TRUTH;
    private static final String lagIntAUri = gvnfAUri + "/lag-interfaces/lag-interface/lagint-a";
    private static final String lintUri = lagIntAUri + "/l-interfaces/l-interface/lint";

    private static final String gvnfBUri = "/network/generic-vnfs/generic-vnf/gvnf-b" + SOURCE_OF_TRUTH;
    private static final String lagIntBUri = gvnfBUri + "/lag-interfaces/lag-interface/lagint-b";

    @BeforeClass
    public static void init() {
        graph = JanusGraphFactory.build().set("storage.backend", "inmemory").open();

    }

    @Before
    public void setup() throws UnsupportedEncodingException, AAIException, URISyntaxException {
        version = schemaVersions.getDefaultVersion();
        loader = loaderFactory.createLoaderForVersion(introspectorFactoryType, version);
        engine = new JanusGraphDBEngine(queryStyle, loader);
        initData();
    }

    @After
    public void cleanup() {
        engine.rollback();
    }

    /**
     * Using latest version (see schema-ingest.properties)
     * Create generic-vnf with lag-interface that has an l-interface
     * Create generic-vnf with lag-interface relationship the l-interface
     */
    private void initData() throws UnsupportedEncodingException, AAIException, URISyntaxException {
        engine.startTransaction();
        DBSerializer serializer = new DBSerializer(version, engine, introspectorFactoryType, SOURCE_OF_TRUTH, AAIProperties.MINIMUM_DEPTH);


        Introspector gvnf = loader.introspectorFromName("generic-vnf");
        gvnf.setValue("vnf-id", "gvnf-a" + SOURCE_OF_TRUTH);
        gvnf.setValue("vnf-name", "gvnf" + SOURCE_OF_TRUTH + "-name");

        Introspector lagInt = loader.introspectorFromName("lag-interface");
        lagInt.setValue("interface-name", "lagint-a");

        Introspector lint = loader.introspectorFromName("l-interface");
        lint.setValue("interface-name", "lint");

        Introspector lagints = loader.introspectorFromName("lag-interfaces");
        Introspector lints = loader.introspectorFromName("l-interfaces");

        lints.setValue("l-interface", Collections.singletonList(lint.getUnderlyingObject()));
        lagInt.setValue("l-interfaces", lints.getUnderlyingObject());
        lagints.setValue("lag-interface", Collections.singletonList(lagInt.getUnderlyingObject()));
        gvnf.setValue("lag-interfaces", lagints.getUnderlyingObject());



        Vertex gvnfV = serializer.createNewVertex(gvnf);
        QueryParser uriQuery = engine.getQueryBuilder().createQueryFromURI(new URI(gvnfAUri));
        serializer.serializeToDb(gvnf, gvnfV, uriQuery, "generic-vnf", gvnf.marshal(false));

        assertTrue("generic-vnf-a created", engine.tx().traversal().V()
            .has(AAIProperties.AAI_URI, gvnfAUri)
            .hasNext());
        assertTrue("lag-int-a created", engine.tx().traversal().V()
            .has(AAIProperties.AAI_URI, lagIntAUri)
            .hasNext());
        assertTrue("l-int created", engine.tx().traversal().V()
            .has(AAIProperties.AAI_URI, lintUri)
            .hasNext());



        gvnf = loader.introspectorFromName("generic-vnf");
        gvnf.setValue("vnf-id", "gvnf-b" + SOURCE_OF_TRUTH);
        gvnf.setValue("vnf-name", "gvnf" + SOURCE_OF_TRUTH + "-name");

        lagInt = loader.introspectorFromName("lag-interface");
        lagInt.setValue("interface-name", "lagint-b");
        Introspector relationship = loader.introspectorFromName("relationship");
        relationship.setValue("related-link", lintUri);
        Introspector relationshipList = loader.introspectorFromName("relationship-list");
        relationshipList.setValue("relationship", Collections.singletonList(relationship.getUnderlyingObject()));
        lagInt.setValue("relationship-list", relationshipList.getUnderlyingObject());

        lagints = loader.introspectorFromName("lag-interfaces");
        lagints.setValue("lag-interface", Collections.singletonList(lagInt.getUnderlyingObject()));
        gvnf.setValue("lag-interfaces", lagints.getUnderlyingObject());

        gvnfV = serializer.createNewVertex(gvnf);
        uriQuery = engine.getQueryBuilder().createQueryFromURI(new URI(gvnfAUri));
        serializer.serializeToDb(gvnf, gvnfV, uriQuery, "generic-vnf", gvnf.marshal(false));

        engine.tx().traversal().V().forEachRemaining(v -> System.out.println(v.<String>value(AAIProperties.AAI_URI)));
        assertTrue("generic-vnf-b created", engine.tx().traversal().V()
            .has(AAIProperties.AAI_URI, gvnfBUri)
            .hasNext());
        assertTrue("lag-int-b created", engine.tx().traversal().V()
            .has(AAIProperties.AAI_URI, lagIntBUri)
            .hasNext());
        assertTrue("lag-interface relationship l-interface created", engine.tx().traversal().V()
            .has(AAIProperties.AAI_URI, lagIntBUri)
            .both()
            .has(AAIProperties.AAI_URI, lintUri)
            .hasNext());
    }


    @Test
    public void verifyReadOfGenericVnfATest() throws AAIException, UnsupportedEncodingException {
        DBSerializer serializer = new DBSerializer(version, engine, introspectorFactoryType, SOURCE_OF_TRUTH, AAIProperties.MINIMUM_DEPTH);

        String gvnfALatestView = serializer.getLatestVersionView(
            engine.tx().traversal().V().has(AAIProperties.AAI_URI, gvnfAUri).next()).marshal(false);

        assertThat(gvnfALatestView,
            hasJsonPath(
                "$.lag-interfaces.lag-interface[*]",
                hasSize(1)
            ));
        assertThat(gvnfALatestView,
            hasJsonPath(
                "$.lag-interfaces.lag-interface[*].l-interfaces.l-interface[*]",
                hasSize(1)
            ));
        assertThat(gvnfALatestView,
            hasJsonPath(
                "$.lag-interfaces.lag-interface[*].l-interfaces.l-interface[*].relationship-list.relationship[*].related-link",
                containsInAnyOrder(
                    "/aai/" + schemaVersions.getDefaultVersion() + lagIntBUri
                )
            ));
    }

    @Test
    public void verifyReadOfGenericVnfBTest() throws AAIException, UnsupportedEncodingException {
        DBSerializer serializer = new DBSerializer(version, engine, introspectorFactoryType, SOURCE_OF_TRUTH, AAIProperties.MINIMUM_DEPTH);

        String gvnfBLatestView = serializer.getLatestVersionView(
            engine.tx().traversal().V().has(AAIProperties.AAI_URI, gvnfBUri).next()).marshal(false);

        assertThat(gvnfBLatestView,
            hasJsonPath(
                "$.lag-interfaces.lag-interface[*]",
                hasSize(1)
            ));
        assertThat(gvnfBLatestView,
            not(hasJsonPath(
                "$.lag-interfaces.lag-interface[*].l-interfaces.l-interface[*]"
            )));
        assertThat(gvnfBLatestView,
            hasJsonPath(
                "$.lag-interfaces.lag-interface[*].relationship-list.relationship[*].related-link",
                containsInAnyOrder(
                    "/aai/" + schemaVersions.getDefaultVersion() + lintUri
                )
            ));
    }

}
