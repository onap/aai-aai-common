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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(value = Parameterized.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class DbSerializerNotificationEventsTest extends AAISetup {

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
        engine = new JanusGraphDBEngine(queryStyle, loader);
    }

    /*
    Create Complex
    Create Pserver with pinterface and relationship to complex
    Update pserver removing relationship to complex
    Update pserver adding a second p-interface
    Add l-interface directly to the 2nd p-interface
    Delete pserver
     */
    @Test
    public void createComplexPserverWithRelUpdatePserverToDeleteRelAddPinterfaceThenDeleteComplexCheckingUpdatedListTest() throws AAIException, UnsupportedEncodingException, URISyntaxException {
        engine.startTransaction();

        System.out.println("Create Complex");
        DBSerializer dbserLocal = new DBSerializer(version, engine, introspectorFactoryType, "create-complex", AAIProperties.MINIMUM_DEPTH);
        Introspector complex = loader.introspectorFromName("complex");
        Vertex complexV = dbserLocal.createNewVertex(complex);
        final String complexUri = "/cloud-infrastructure/complexes/complex/c-id-b";
        QueryParser uriQuery =
            engine.getQueryBuilder().createQueryFromURI(new URI(complexUri));

        complex.setValue("physical-location-id", "c-id-b");
        complex.setValue("physical-location-type", "type");
        complex.setValue("street1", "streetA");
        complex.setValue("city", "cityA");
        complex.setValue("postal-code", "11111");
        complex.setValue("country", "abc");
        complex.setValue("region", "ef");
        dbserLocal.serializeToDb(complex, complexV, uriQuery, "complex", complex.marshal(false));

        assertTrue("Complex created", engine.tx().traversal().V()
            .has("aai-node-type", "complex")
            .has("physical-location-id", "c-id-b")
            .hasNext());
        Map<Vertex, Boolean> updated = getUpdatedVertexes(dbserLocal);
        assertEquals("Number of updated vertexes", 1, updated.size());
        assertThat("Only modified vertexes are in the updated set",
            updated.keySet().stream().map(v -> v.<String>value(AAIProperties.AAI_URI)).collect(Collectors.toSet()),
            is(Collections.singleton(complexUri)));
        List<String> didNotUpdateStandardVertexProps = updated.entrySet().stream()
            .filter(e -> !e.getValue())
            .map(e -> e.getKey().<String>value(AAIProperties.AAI_URI)).collect(Collectors.toList());
        assertThat("Vertexes should all have their standard props updated", didNotUpdateStandardVertexProps, is(Collections.emptyList()));




        System.out.println("Create Pserver with pinterface and relationship to complex ");
        dbserLocal = new DBSerializer(version, engine, introspectorFactoryType, "create-pserver", AAIProperties.MINIMUM_DEPTH);
        Introspector pserver = loader.introspectorFromName("pserver");
        Vertex pserverV = dbserLocal.createNewVertex(pserver);
        final String pserverUri = "/cloud-infrastructure/pservers/pserver/ps-b";
        uriQuery =
            engine.getQueryBuilder().createQueryFromURI(new URI(pserverUri));

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

        assertTrue("Pserver created", engine.tx().traversal().V()
            .has("aai-node-type", "pserver")
            .has("hostname", "ps-b")
            .hasNext());
        assertTrue("Pserver has edge to complex", engine.tx().traversal().V()
            .has("aai-node-type", "pserver")
            .has("hostname", "ps-b")
            .bothE()
            .otherV()
            .has("aai-node-type", "complex")
            .hasNext());
        assertTrue("Pserver has edge to pinterface", engine.tx().traversal().V()
            .has("aai-node-type", "pserver")
            .has("hostname", "ps-b")
            .bothE()
            .otherV()
            .has("aai-node-type", "p-interface")
            .hasNext());
        updated = getUpdatedVertexes(dbserLocal);
        assertEquals("Number of updated vertexes", 3, updated.size());
        assertThat("Only modified vertexes are in the updated set",
            updated.keySet().stream().map(v -> v.<String>value(AAIProperties.AAI_URI)).collect(Collectors.toSet()),
            is(new HashSet<>(Arrays.asList(pserverUri, pintUri, complexUri))));
        didNotUpdateStandardVertexProps = updated.entrySet().stream()
            .filter(e -> !e.getValue())
            .map(e -> e.getKey().<String>value(AAIProperties.AAI_URI)).collect(Collectors.toList());
        assertThat("Vertexes should all have their standard props updated", didNotUpdateStandardVertexProps, is(Collections.emptyList()));


        System.out.println("Update pserver removing relationship to complex");
        dbserLocal = new DBSerializer(version, engine, introspectorFactoryType, "update-pserver", AAIProperties.MINIMUM_DEPTH);
        pserver = dbserLocal.getLatestVersionView(pserverV);
        relationshipList = loader.introspectorFromName("relationship-list");
        relationshipList.setValue("relationship", Collections.emptyList());
        pserver.setValue("relationship-list", relationshipList.getUnderlyingObject());
        pserver.setValue("equip-type", "server-a");
        pserver.setValue("number-of-cpus", 99);
        dbserLocal.serializeToDb(pserver, pserverV, uriQuery, "pserver", pserver.marshal(false));

        assertFalse("Pserver no longer has edge to complex", engine.tx().traversal().V()
            .has("aai-node-type", "pserver")
            .has("hostname", "ps-b")
            .bothE()
            .otherV()
            .has("aai-node-type", "complex")
            .hasNext());
        updated = getUpdatedVertexes(dbserLocal);
        assertEquals("Number of updated vertexes", 2, updated.size());
        assertThat("Only modified vertexes are in the updated set",
            updated.keySet().stream().map(v -> v.<String>value(AAIProperties.AAI_URI)).collect(Collectors.toSet()),
            is(new HashSet<>(Arrays.asList(pserverUri, complexUri))));
        didNotUpdateStandardVertexProps = updated.entrySet().stream()
            .filter(e -> !e.getValue())
            .map(e -> e.getKey().<String>value(AAIProperties.AAI_URI)).collect(Collectors.toList());
        assertThat("Vertexes should all have their standard props updated", didNotUpdateStandardVertexProps, is(Collections.emptyList()));



        System.out.println("Update pserver adding a second p-interface");
        dbserLocal = new DBSerializer(version, engine, introspectorFactoryType, "update-pserver", AAIProperties.MINIMUM_DEPTH);
        pserver = dbserLocal.getLatestVersionView(pserverV);
        Introspector pint2 = loader.introspectorFromName("p-interface");
        pint2.setValue("interface-name", "pint-2");
        pints = pserver.getWrappedValue("p-interfaces");
        List<Object> pintList = pserver.getWrappedValue("p-interfaces").getWrappedListValue("p-interface")
            .stream().map(Introspector::getUnderlyingObject).collect(Collectors.toList());
        pintList.add(pint2.getUnderlyingObject());
        pints.setValue("p-interface", pintList);
        pserver.setValue("p-interfaces", pints.getUnderlyingObject());
        final String pint2Uri = pserverUri + "/p-interfaces/p-interface/pint-2";
        dbserLocal.serializeToDb(pserver, pserverV, uriQuery, "pserver", pserver.marshal(false));

        assertTrue("Pserver has edge to pinterface 2", engine.tx().traversal().V()
            .has("aai-node-type", "pserver")
            .has("hostname", "ps-b")
            .in()
            .has("aai-node-type", "p-interface")
            .has("interface-name","pint-2")
            .hasNext());
        assertTrue("p-interface 2 created", engine.tx().traversal().V()
            .has("aai-node-type", "p-interface")
            .has("interface-name", "pint-2")
            .has(AAIProperties.AAI_URI, pint2Uri)
            .hasNext());
        updated = getUpdatedVertexes(dbserLocal);
        assertEquals("Number of updated vertexes", 1, updated.size());
        assertThat("Only modified vertexes are in the updated set",
            updated.keySet().stream().map(v -> v.<String>value(AAIProperties.AAI_URI)).collect(Collectors.toSet()),
            is(Collections.singleton(pint2Uri)));
        didNotUpdateStandardVertexProps = updated.entrySet().stream()
            .filter(e -> !e.getValue())
            .map(e -> e.getKey().<String>value(AAIProperties.AAI_URI)).collect(Collectors.toList());
        assertThat("Vertexes should all have their standard props updated", didNotUpdateStandardVertexProps, is(Collections.emptyList()));


        System.out.println("Add l-interface directly to the 2nd p-interface");
        dbserLocal = new DBSerializer(version, engine, introspectorFactoryType, "create-pserver", AAIProperties.MINIMUM_DEPTH);
        Introspector lInt = loader.introspectorFromName("l-interface");
        Vertex lIntV = dbserLocal.createNewVertex(lInt);
        final String lIntUri = pint2Uri + "/l-interfaces/l-interface/lint-1";
        uriQuery =
            engine.getQueryBuilder().createQueryFromURI(new URI(lIntUri));
        lInt.setValue("interface-name", "lint-1");
        dbserLocal.serializeToDb(lInt, lIntV, uriQuery, "l-interface", lInt.marshal(false));

        assertTrue("l-interface created", engine.tx().traversal().V()
            .has("aai-node-type", "l-interface")
            .has("interface-name", "lint-1")
            .hasNext());

        assertTrue("Pserver has edge to pinterface to l-interface", engine.tx().traversal().V()
            .has("aai-node-type", "pserver")
            .has("hostname", "ps-b")
            .bothE()
            .otherV()
            .has("aai-node-type", "p-interface")
            .bothE()
            .otherV()
            .has("aai-node-type", "l-interface")
            .hasNext());
        updated = getUpdatedVertexes(dbserLocal);
        assertEquals("Number of updated vertexes", 1, updated.size());
        assertThat("Only modified vertexes are in the updated set",
            updated.keySet().stream().map(v -> v.<String>value(AAIProperties.AAI_URI)).collect(Collectors.toSet()),
            is(new HashSet<>(Collections.singletonList(lIntUri))));
        didNotUpdateStandardVertexProps = updated.entrySet().stream()
            .filter(e -> !e.getValue())
            .map(e -> e.getKey().<String>value(AAIProperties.AAI_URI)).collect(Collectors.toList());
        assertThat("Vertexes should all have their standard props updated", didNotUpdateStandardVertexProps, is(Collections.emptyList()));


        System.out.println("Delete pserver");
        dbserLocal = new DBSerializer(version, engine, introspectorFactoryType, "delete-pserver", AAIProperties.MINIMUM_DEPTH);
        pserver = dbserLocal.getLatestVersionView(pserverV);
        String rv = pserver.getValue(AAIProperties.RESOURCE_VERSION);
        dbserLocal.delete(engine.tx().traversal().V(pserverV).next(), rv, true);

        assertFalse("pserver no longer exists", engine.tx().traversal().V()
            .has("aai-node-type", "pserver")
            .hasNext());
        updated = getUpdatedVertexes(dbserLocal);
        assertEquals("Number of updated vertexes", 0, updated.size());
        didNotUpdateStandardVertexProps = updated.entrySet().stream()
            .filter(e -> !e.getValue())
            .map(e -> e.getKey().<String>value(AAIProperties.AAI_URI)).collect(Collectors.toList());
        assertThat("Vertexes should all have their standard props updated", didNotUpdateStandardVertexProps, is(Collections.emptyList()));


    }

    private Map<Vertex, Boolean> getUpdatedVertexes(DBSerializer dbserLocal) {
        Map<Vertex, Boolean> updated = new LinkedHashMap<>(dbserLocal.getUpdatedVertexes());
        dbserLocal.touchStandardVertexPropertiesForEdges().forEach(v -> updated.putIfAbsent(v, true));
        return updated;
    }


}
