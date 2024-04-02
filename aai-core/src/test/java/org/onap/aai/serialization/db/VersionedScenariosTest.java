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

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraphFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class VersionedScenariosTest extends AAISetup {

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

    public static final String SOURCE_OF_TRUTH = "VersionedScenariosTest";
    private static final String gvnfUri = "/network/generic-vnfs/generic-vnf/gvnf" + SOURCE_OF_TRUTH;
    private static final String llDefaultUri = "/network/logical-links/logical-link/llDefault";
    private static final String lintSourceUri = gvnfUri + "/l-interfaces/l-interface/source";
    private static final String lintDestinationUri = gvnfUri + "/l-interfaces/l-interface/destination";
    private static final String llLabeledUri = "/network/logical-links/logical-link/llLabeled";

    @BeforeAll
    public static void init() {
        graph = JanusGraphFactory.build().set("storage.backend", "inmemory").open();

    }

    @BeforeEach
    public void setup() throws UnsupportedEncodingException, AAIException, URISyntaxException {
        version = schemaVersions.getDefaultVersion();
        loader = loaderFactory.createLoaderForVersion(introspectorFactoryType, version);
        engine = new JanusGraphDBEngine(queryStyle, loader);
        initData();
    }

    @AfterEach
    public void cleanup() {
        engine.rollback();
    }

    /**
     * Using latest version (see schema-ingest.properties)
     * Create generic-vnf with l-interfaces source, destination
     * Create logical-link with relationship to interfaces source, destination (will use the default labels)
     * Create logical-link with relationship to interfaces source, destination with specific labels
     */
    private void initData() throws UnsupportedEncodingException, AAIException, URISyntaxException {
        engine.startTransaction();
        DBSerializer serializer = new DBSerializer(version, engine, introspectorFactoryType, SOURCE_OF_TRUTH,
                AAIProperties.MINIMUM_DEPTH);

        Introspector gvnf = loader.introspectorFromName("generic-vnf");
        gvnf.setValue("vnf-id", "gvnf" + SOURCE_OF_TRUTH);
        gvnf.setValue("vnf-name", "gvnf" + SOURCE_OF_TRUTH + "-name");

        Introspector lintSource = loader.introspectorFromName("l-interface");
        lintSource.setValue("interface-name", "source");

        Introspector lintDestination = loader.introspectorFromName("l-interface");
        lintDestination.setValue("interface-name", "destination");

        List<Object> lIntList = new ArrayList<>();
        lIntList.add(lintSource.getUnderlyingObject());
        lIntList.add(lintDestination.getUnderlyingObject());
        Introspector lints = loader.introspectorFromName("l-interfaces");
        lints.setValue("l-interface", lIntList);
        gvnf.setValue("l-interfaces", lints.getUnderlyingObject());

        Vertex gvnfV = serializer.createNewVertex(gvnf);
        QueryParser uriQuery = engine.getQueryBuilder().createQueryFromURI(new URI(gvnfUri));
        serializer.serializeToDb(gvnf, gvnfV, uriQuery, "generic-vnf", gvnf.marshal(false));

        assertTrue(engine.tx().traversal().V().has(AAIProperties.AAI_URI, gvnfUri).hasNext(), "generic-vnf created");
        assertTrue(engine.tx().traversal().V().has(AAIProperties.AAI_URI, lintSourceUri).hasNext(), "source created");
        assertTrue(engine.tx().traversal().V().has(AAIProperties.AAI_URI, lintDestinationUri).hasNext(),
                "destination created");

        Introspector llDefault = loader.introspectorFromName("logical-link");
        llDefault.setValue("link-name", "llDefault");
        List<Object> relList = new ArrayList<>();
        Introspector relationship = loader.introspectorFromName("relationship");
        // relationship.setValue("related-to", "l-interface");
        relationship.setValue("related-link", lintSourceUri);
        relList.add(relationship.getUnderlyingObject());
        relationship = loader.introspectorFromName("relationship");
        // relationship.setValue("related-to", "l-interface");
        relationship.setValue("related-link", lintDestinationUri);
        relList.add(relationship.getUnderlyingObject());
        Introspector relationshipList = loader.introspectorFromName("relationship-list");
        relationshipList.setValue("relationship", relList);
        llDefault.setValue("relationship-list", relationshipList.getUnderlyingObject());

        Vertex llDefaultV = serializer.createNewVertex(llDefault);
        uriQuery = engine.getQueryBuilder().createQueryFromURI(new URI(llDefaultUri));
        serializer.serializeToDb(llDefault, llDefaultV, uriQuery, "logical-link", llDefault.marshal(false));

        assertTrue(engine.tx().traversal().V().has(AAIProperties.AAI_URI, llDefaultUri).hasNext(),
                "logical-link created");
        assertTrue(engine.tx().traversal().V()
                .has(AAIProperties.AAI_URI, llDefaultUri).both().has(AAIProperties.AAI_URI, lintSourceUri).hasNext(), "default source relationship created");
        assertTrue(engine.tx().traversal().V().has(AAIProperties.AAI_URI, llDefaultUri).both()
                        .has(AAIProperties.AAI_URI, lintDestinationUri).hasNext(),
                "default destination relationship created");

        Introspector llLabeled = loader.introspectorFromName("logical-link");
        llLabeled.setValue("link-name", "llLabeled");
        relList = new ArrayList<>();
        relationship = loader.introspectorFromName("relationship");
        relationship.setValue("related-to", "l-interface");
        relationship.setValue("relationship-label", "org.onap.relationships.inventory.Source");
        relationship.setValue("related-link", lintSourceUri);
        relList.add(relationship.getUnderlyingObject());
        relationship = loader.introspectorFromName("relationship");
        relationship.setValue("related-to", "l-interface");
        relationship.setValue("relationship-label", "org.onap.relationships.inventory.Destination");
        relationship.setValue("related-link", lintDestinationUri);
        relList.add(relationship.getUnderlyingObject());
        relationshipList = loader.introspectorFromName("relationship-list");
        relationshipList.setValue("relationship", relList);
        llLabeled.setValue("relationship-list", relationshipList.getUnderlyingObject());

        Vertex llLabeledV = serializer.createNewVertex(llLabeled);
        uriQuery = engine.getQueryBuilder().createQueryFromURI(new URI(llLabeledUri));
        serializer.serializeToDb(llLabeled, llLabeledV, uriQuery, "logical-link", llLabeled.marshal(false));

        assertTrue(engine.tx().traversal().V().has(AAIProperties.AAI_URI, llLabeledUri).hasNext(),
                "logical-link created");
        assertTrue(engine.tx().traversal().V().has(AAIProperties.AAI_URI, llLabeledUri)
                        .both("org.onap.relationships.inventory.Source").has(AAIProperties.AAI_URI, lintSourceUri)
                        .hasNext(),
                "labeled source relationship created");
        assertTrue(engine.tx().traversal().V().has(AAIProperties.AAI_URI, llLabeledUri)
                        .both("org.onap.relationships.inventory.Destination")
                        .has(AAIProperties.AAI_URI, lintDestinationUri).hasNext(),
                "labeled destination relationship created");
    }

    @Test
    public void verifyRelsOfLatestViewOfGenericVnf() throws AAIException, UnsupportedEncodingException {
        DBSerializer serializer = new DBSerializer(version, engine, introspectorFactoryType, SOURCE_OF_TRUTH,
                AAIProperties.MINIMUM_DEPTH);

        String gvnfLatestView =
                serializer.getLatestVersionView(engine.tx().traversal().V().has(AAIProperties.AAI_URI, gvnfUri).next())
                        .marshal(false);
        assertThat(gvnfLatestView,
                hasJsonPath("$.l-interfaces.l-interface[*].relationship-list.relationship[*]", hasSize(4)));
        assertThat(gvnfLatestView,
                hasJsonPath("$.l-interfaces.l-interface[*].relationship-list.relationship[*].related-link",
                        containsInAnyOrder("/aai/" + schemaVersions.getDefaultVersion() + llDefaultUri,
                                "/aai/" + schemaVersions.getDefaultVersion() + llDefaultUri,
                                "/aai/" + schemaVersions.getDefaultVersion() + llLabeledUri,
                                "/aai/" + schemaVersions.getDefaultVersion() + llLabeledUri)));
        assertThat(gvnfLatestView,
                hasJsonPath("$.l-interfaces.l-interface[*].relationship-list.relationship[*].relationship-label",
                        containsInAnyOrder("tosca.relationships.network.LinksTo", "tosca.relationships.network.LinksTo",
                                "org.onap.relationships.inventory.Source",
                                "org.onap.relationships.inventory.Destination")));
    }

    @Test
    public void verifyRelsOfLatestViewOfLLDefault() throws AAIException, UnsupportedEncodingException {
        DBSerializer serializer = new DBSerializer(version, engine, introspectorFactoryType, SOURCE_OF_TRUTH,
                AAIProperties.MINIMUM_DEPTH);

        String llDefaultLatestView = serializer
                .getLatestVersionView(engine.tx().traversal().V().has(AAIProperties.AAI_URI, llDefaultUri).next())
                .marshal(false);
        assertThat(llDefaultLatestView, hasJsonPath("$.relationship-list.relationship[*]", hasSize(2)));
        assertThat(llDefaultLatestView,
                hasJsonPath("$.relationship-list.relationship[*].related-link",
                        containsInAnyOrder("/aai/" + schemaVersions.getDefaultVersion() + lintSourceUri,
                                "/aai/" + schemaVersions.getDefaultVersion() + lintDestinationUri)));
        assertThat(llDefaultLatestView, hasJsonPath("$.relationship-list.relationship[*].relationship-label",
                containsInAnyOrder("tosca.relationships.network.LinksTo", "tosca.relationships.network.LinksTo")));
        assertThat(llDefaultLatestView, hasJsonPath("$.relationship-list.relationship[*].relationship-label", not(
                contains("org.onap.relationships.inventory.Source", "org.onap.relationships.inventory.Destination"))));

    }

    @Test
    public void verifyRelsOfLatestViewOfLLLabeled() throws AAIException, UnsupportedEncodingException {
        DBSerializer serializer = new DBSerializer(version, engine, introspectorFactoryType, SOURCE_OF_TRUTH,
                AAIProperties.MINIMUM_DEPTH);

        String llLabeledLatestView = serializer
                .getLatestVersionView(engine.tx().traversal().V().has(AAIProperties.AAI_URI, llLabeledUri).next())
                .marshal(false);
        assertThat(llLabeledLatestView, hasJsonPath("$.relationship-list.relationship[*]", hasSize(2)));
        assertThat(llLabeledLatestView,
                hasJsonPath("$.relationship-list.relationship[*].related-link",
                        containsInAnyOrder("/aai/" + schemaVersions.getDefaultVersion() + lintSourceUri,
                                "/aai/" + schemaVersions.getDefaultVersion() + lintDestinationUri)));
        assertThat(llLabeledLatestView, hasJsonPath("$.relationship-list.relationship[*].relationship-label",
                not(containsInAnyOrder("tosca.relationships.network.LinksTo", "tosca.relationships.network.LinksTo"))));
        assertThat(llLabeledLatestView, hasJsonPath("$.relationship-list.relationship[*].relationship-label",
                contains("org.onap.relationships.inventory.Source", "org.onap.relationships.inventory.Destination")));

    }

    @Test
    public void verifyRelsOfOldViewOfGenericVnf() throws AAIException, UnsupportedEncodingException {
        SchemaVersion oldVersion = new SchemaVersion("v11");
        Loader oldLoader = loaderFactory.getLoaderStrategy(introspectorFactoryType, oldVersion);
        DBSerializer oldSerializer = new DBSerializer(oldVersion, engine, introspectorFactoryType, SOURCE_OF_TRUTH,
                AAIProperties.MINIMUM_DEPTH);

        String gvnfOldView = oldSerializer
                .dbToObject(
                        Collections
                                .singletonList(engine.tx().traversal().V().has(AAIProperties.AAI_URI, gvnfUri).next()),
                        oldLoader.introspectorFromName("generic-vnf"), AAIProperties.MAXIMUM_DEPTH, false, "false")
                .marshal(false);
        assertThat(gvnfOldView,
                hasJsonPath("$.l-interfaces.l-interface[*].relationship-list.relationship[*]", hasSize(2)));
        assertThat(gvnfOldView,
                hasJsonPath("$.l-interfaces.l-interface[*].relationship-list.relationship[*].relationship-label",
                        emptyCollectionOf(String.class)));
        assertThat(gvnfOldView,
                hasJsonPath("$.l-interfaces.l-interface[*].relationship-list.relationship[*].related-link",
                        containsInAnyOrder("/aai/" + oldVersion + llDefaultUri, "/aai/" + oldVersion + llDefaultUri)));
    }

    @Test
    public void verifyRelsOfOldViewOfLLDefault() throws AAIException, UnsupportedEncodingException {
        SchemaVersion oldVersion = new SchemaVersion("v11");
        Loader oldLoader = loaderFactory.getLoaderStrategy(introspectorFactoryType, oldVersion);
        DBSerializer oldSerializer = new DBSerializer(oldVersion, engine, introspectorFactoryType, SOURCE_OF_TRUTH,
                AAIProperties.MINIMUM_DEPTH);

        String llDefaultOldView = oldSerializer
                .dbToObject(
                        Collections.singletonList(
                                engine.tx().traversal().V().has(AAIProperties.AAI_URI, llDefaultUri).next()),
                        oldLoader.introspectorFromName("logical-link"), AAIProperties.MAXIMUM_DEPTH, false, "false")
                .marshal(false);
        assertThat(llDefaultOldView, hasJsonPath("$.relationship-list.relationship[*]", hasSize(2)));
        assertThat(llDefaultOldView,
                hasJsonPath("$.relationship-list.relationship[*].relationship-label", emptyCollectionOf(String.class)));
        assertThat(llDefaultOldView, hasJsonPath("$.relationship-list.relationship[*].related-link",
                containsInAnyOrder("/aai/" + oldVersion + lintSourceUri, "/aai/" + oldVersion + lintDestinationUri)));

    }

    @Test
    public void verifyRelsOfOldViewOfLLLabeled() throws AAIException, UnsupportedEncodingException {
        SchemaVersion oldVersion = new SchemaVersion("v11");
        Loader oldLoader = loaderFactory.getLoaderStrategy(introspectorFactoryType, oldVersion);
        DBSerializer oldSerializer = new DBSerializer(oldVersion, engine, introspectorFactoryType, SOURCE_OF_TRUTH,
                AAIProperties.MINIMUM_DEPTH);

        String llLabeledtOldView = oldSerializer
                .dbToObject(
                        Collections.singletonList(
                                engine.tx().traversal().V().has(AAIProperties.AAI_URI, llLabeledUri).next()),
                        oldLoader.introspectorFromName("logical-link"), AAIProperties.MAXIMUM_DEPTH, false, "false")
                .marshal(false);
        assertThat(llLabeledtOldView, not(hasJsonPath("$.relationship-list.relationship[*]")));
    }

    @Test
    public void useOldVersionToUpdatedGenericVnfAndVerifyLatestVersionRels()
            throws AAIException, UnsupportedEncodingException, URISyntaxException {
        SchemaVersion oldVersion = new SchemaVersion("v11");
        Loader oldLoader = loaderFactory.getLoaderStrategy(introspectorFactoryType, oldVersion);
        DBSerializer oldSerializer = new DBSerializer(oldVersion, engine, introspectorFactoryType, SOURCE_OF_TRUTH,
                AAIProperties.MINIMUM_DEPTH);

        Vertex oldGvnfV = engine.tx().traversal().V().has(AAIProperties.AAI_URI, gvnfUri).next();
        Introspector oldGvnf = oldSerializer.dbToObject(Collections.singletonList(oldGvnfV),
                oldLoader.introspectorFromName("generic-vnf"), AAIProperties.MAXIMUM_DEPTH, false, "false");
        assertThat(oldGvnf.marshal(false),
                hasJsonPath("$.l-interfaces.l-interface[*].relationship-list.relationship[*].related-link",
                        containsInAnyOrder("/aai/" + oldVersion + llDefaultUri, "/aai/" + oldVersion + llDefaultUri)));
        oldGvnf.setValue("in-maint", true);
        QueryParser uriQuery = engine.getQueryBuilder().createQueryFromURI(new URI(gvnfUri));
        oldSerializer.serializeToDb(oldGvnf, oldGvnfV, uriQuery, "generic-vnf", oldGvnf.marshal(false));

        verifyRelsOfLatestViewOfGenericVnf();
        verifyRelsOfOldViewOfGenericVnf();
    }

}
