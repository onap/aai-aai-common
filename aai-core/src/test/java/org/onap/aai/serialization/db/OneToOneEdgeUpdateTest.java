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
import java.util.Arrays;
import java.util.Collections;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class OneToOneEdgeUpdateTest extends AAISetup {

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
    private static final String lintUri = gvnfAUri + "/l-interfaces/l-interface/lint";
    private static final String lintAUri = gvnfAUri + "/l-interfaces/l-interface/lint-a";
    private static final String sriovVfUri = lintUri + "/sriov-vfs/sriov-vf/sriov-vf";
    private static final String sriovVfAUri = lintAUri + "/sriov-vfs/sriov-vf/sriov-vf-a";

    private static final String gvnfBUri = "/network/generic-vnfs/generic-vnf/gvnf-b" + SOURCE_OF_TRUTH;
    private static final String lIntBUri = gvnfBUri + "/l-interfaces/l-interface/lint-b";

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
     * Using latest version (see schema-ingest.properties) - sriov-vf to l-interface is one ot one
     * Create generic-vnf with l-interface that has an sriov-vf
     * Create generic-vnf with l-interface relationship the sriov-vf
     */
    private void initData() throws UnsupportedEncodingException, AAIException, URISyntaxException {
        engine.startTransaction();
        DBSerializer serializer = new DBSerializer(version, engine, introspectorFactoryType, SOURCE_OF_TRUTH, AAIProperties.MINIMUM_DEPTH);


        Introspector gvnf = loader.introspectorFromName("generic-vnf");
        gvnf.setValue("vnf-id", "gvnf-a" + SOURCE_OF_TRUTH);
        gvnf.setValue("vnf-name", "gvnf" + SOURCE_OF_TRUTH + "-name");

        Introspector lint = loader.introspectorFromName("l-interface");
        lint.setValue("interface-name", "lint");

        Introspector sriovVf = loader.introspectorFromName("sriov-vf");
        sriovVf.setValue("pci-id", "sriov-vf");

        Introspector sriovVfs = loader.introspectorFromName("sriov-vfs");
        sriovVfs.setValue("sriov-vf", Collections.singletonList(sriovVf.getUnderlyingObject()));
        lint.setValue("sriov-vfs", sriovVfs.getUnderlyingObject());

        Introspector lintA = loader.introspectorFromName("l-interface");
        lintA.setValue("interface-name", "lint-a");

        Introspector sriovVfA = loader.introspectorFromName("sriov-vf");
        sriovVfA.setValue("pci-id", "sriov-vf-a");

        sriovVfs = loader.introspectorFromName("sriov-vfs");
        sriovVfs.setValue("sriov-vf", Collections.singletonList(sriovVfA.getUnderlyingObject()));
        lintA.setValue("sriov-vfs", sriovVfs.getUnderlyingObject());

        Introspector lints = loader.introspectorFromName("l-interfaces");
        lints.setValue("l-interface", Arrays.asList(lint.getUnderlyingObject(), lintA.getUnderlyingObject()));
        gvnf.setValue("l-interfaces", lints.getUnderlyingObject());


        System.out.println(gvnf.marshal(true));
        Vertex gvnfV = serializer.createNewVertex(gvnf);
        QueryParser uriQuery = engine.getQueryBuilder().createQueryFromURI(new URI(gvnfAUri));
        serializer.serializeToDb(gvnf, gvnfV, uriQuery, "generic-vnf", gvnf.marshal(false));

        assertTrue("generic-vnf-a created", engine.tx().traversal().V()
            .has(AAIProperties.AAI_URI, gvnfAUri)
            .hasNext());
        assertTrue("l-int created", engine.tx().traversal().V()
            .has(AAIProperties.AAI_URI, lintUri)
            .hasNext());
        assertTrue("l-int-a created", engine.tx().traversal().V()
            .has(AAIProperties.AAI_URI, lintAUri)
            .hasNext());
        assertTrue("sriov-vf created", engine.tx().traversal().V()
            .has(AAIProperties.AAI_URI, sriovVfUri)
            .hasNext());
        assertTrue("sriov-vf-a created", engine.tx().traversal().V()
            .has(AAIProperties.AAI_URI, sriovVfAUri)
            .hasNext());


        gvnf = loader.introspectorFromName("generic-vnf");
        gvnf.setValue("vnf-id", "gvnf-b" + SOURCE_OF_TRUTH);
        gvnf.setValue("vnf-name", "gvnf" + SOURCE_OF_TRUTH + "-name");

        lint = loader.introspectorFromName("l-interface");
        lint.setValue("interface-name", "lint-b");
        Introspector relationship = loader.introspectorFromName("relationship");
        relationship.setValue("related-link", sriovVfUri);
        Introspector relationshipList = loader.introspectorFromName("relationship-list");
        relationshipList.setValue("relationship", Collections.singletonList(relationship.getUnderlyingObject()));
        lint.setValue("relationship-list", relationshipList.getUnderlyingObject());

        lints = loader.introspectorFromName("l-interfaces");
        lints.setValue("l-interface", Collections.singletonList(lint.getUnderlyingObject()));
        gvnf.setValue("l-interfaces", lints.getUnderlyingObject());

        gvnfV = serializer.createNewVertex(gvnf);
        uriQuery = engine.getQueryBuilder().createQueryFromURI(new URI(gvnfAUri));
        serializer.serializeToDb(gvnf, gvnfV, uriQuery, "generic-vnf", gvnf.marshal(false));

        engine.tx().traversal().V().forEachRemaining(v -> System.out.println(v.<String>value(AAIProperties.AAI_URI)));
        assertTrue("generic-vnf-b created", engine.tx().traversal().V()
            .has(AAIProperties.AAI_URI, gvnfBUri)
            .hasNext());
        assertTrue("l-int-b created", engine.tx().traversal().V()
            .has(AAIProperties.AAI_URI, lIntBUri)
            .hasNext());
        assertTrue("l-interface relationship sriov-vf created", engine.tx().traversal().V()
            .has(AAIProperties.AAI_URI, lIntBUri)
            .both()
            .has(AAIProperties.AAI_URI, sriovVfUri)
            .hasNext());

    }


    @Test
    public void verifyReadOfGenericVnfATest() throws AAIException, UnsupportedEncodingException {
        DBSerializer serializer = new DBSerializer(version, engine, introspectorFactoryType, SOURCE_OF_TRUTH, AAIProperties.MINIMUM_DEPTH);

        String gvnfALatestView = serializer.getLatestVersionView(
            engine.tx().traversal().V().has(AAIProperties.AAI_URI, gvnfAUri).next()).marshal(false);

        assertThat(gvnfALatestView,
            hasJsonPath(
                "$.l-interfaces.l-interface[*]",
                hasSize(2)
            ));
        assertThat(gvnfALatestView,
            hasJsonPath(
                "$.l-interfaces.l-interface[*].sriov-vfs.sriov-vf[*]",
                hasSize(2)
            ));
        assertThat(gvnfALatestView,
            hasJsonPath(
                "$.l-interfaces.l-interface[*].sriov-vfs.sriov-vf[*].relationship-list.relationship[*].related-link",
                containsInAnyOrder(
                    "/aai/" + schemaVersions.getDefaultVersion() + lIntBUri
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
                "$.l-interfaces.l-interface[*]",
                hasSize(1)
            ));
        assertThat(gvnfBLatestView,
            not(hasJsonPath(
                "$.l-interfaces.l-interface[*].sriov-vfs.sriov-vf[*]"
            )));
        assertThat(gvnfBLatestView,
            hasJsonPath(
                "$.l-interfaces.l-interface[*].relationship-list.relationship[*].related-link",
                containsInAnyOrder(
                    "/aai/" + schemaVersions.getDefaultVersion() + sriovVfUri
                )
            ));
    }

    @Test
    public void replaceRelationshipToSriovVfTest() throws AAIException, UnsupportedEncodingException, URISyntaxException {
        DBSerializer serializer = new DBSerializer(version, engine, introspectorFactoryType, SOURCE_OF_TRUTH, AAIProperties.MINIMUM_DEPTH);

        Introspector lint = serializer.getLatestVersionView(
            engine.tx().traversal().V().has(AAIProperties.AAI_URI, lIntBUri).next());
        String lintView = lint.marshal(false);

        assertThat(lintView,
            hasJsonPath(
                "$.relationship-list.relationship[*].related-link",
                containsInAnyOrder(
                    "/aai/" + schemaVersions.getDefaultVersion() + sriovVfUri
                )
            ));

        Introspector relationship = loader.introspectorFromName("relationship");
        relationship.setValue("related-link", sriovVfAUri);
        Introspector relationshipList = loader.introspectorFromName("relationship-list");
        relationshipList.setValue("relationship", Collections.singletonList(relationship.getUnderlyingObject()));
        lint.setValue("relationship-list", relationshipList.getUnderlyingObject());

        QueryParser uriQuery = engine.getQueryBuilder().createQueryFromURI(new URI(lIntBUri));
        serializer.serializeToDb(lint, engine.tx().traversal().V().has(AAIProperties.AAI_URI, lIntBUri).next(),
            uriQuery, "generic-vnf", lint.marshal(false));
    }


    @Test
    public void createRelationshipForNonExistentRuleTest() throws AAIException, UnsupportedEncodingException, URISyntaxException {
        DBSerializer serializer = new DBSerializer(version, engine, introspectorFactoryType, SOURCE_OF_TRUTH, AAIProperties.MINIMUM_DEPTH);

        Vertex gvnfAV = engine.tx().traversal().V().has(AAIProperties.AAI_URI, gvnfAUri).next();
        Introspector gvnfA = serializer.getLatestVersionView(gvnfAV);
        Introspector relationship = loader.introspectorFromName("relationship");
        relationship.setValue("related-link", gvnfBUri);
        Introspector relationshipList = loader.introspectorFromName("relationship-list");
        relationshipList.setValue("relationship", Collections.singletonList(relationship.getUnderlyingObject()));
        gvnfA.setValue("relationship-list", relationshipList.getUnderlyingObject());
        QueryParser uriQuery = engine.getQueryBuilder().createQueryFromURI(new URI(gvnfAUri));
        try {
            serializer.serializeToDb(gvnfA, gvnfAV, uriQuery, "generic-vnf", gvnfA.marshal(false));
        } catch (AAIException e) {
            assertEquals("AAI_6120", e.getCode());
            assertThat(e.getMessage(), containsString("generic-vnf"));
        }
    }

}
