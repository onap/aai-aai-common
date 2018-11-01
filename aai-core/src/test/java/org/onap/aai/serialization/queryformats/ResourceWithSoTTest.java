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
package org.onap.aai.serialization.queryformats;

import com.google.gson.JsonObject;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.strategy.verification.ReadOnlyStrategy;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.aai.AAISetup;
import org.onap.aai.dbmap.DBConnectionType;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.engines.JanusGraphDBEngine;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatVertexException;
import org.onap.aai.serialization.queryformats.utils.UrlBuilder;
import org.onap.aai.setup.SchemaVersion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class ResourceWithSoTTest extends AAISetup {
    @Mock
    private UrlBuilder urlBuilder;

    private Graph graph;
    private Vertex putVertex;
    private Vertex patchVertex1;
    private Vertex patchVertex2;

    private JsonObject jsonPutObj = new JsonObject() ;
    private JsonObject jsonPatchObj1 = new JsonObject() ;
    private JsonObject jsonPatchObj2 = new JsonObject() ;

    private SchemaVersion version;
    private ResourceWithSoT resourceWithSoT;

    private TransactionalGraphEngine dbEngine;
    private Loader loader;
    private DBSerializer serializer;
    private final ModelType factoryType = ModelType.MOXY;

    @Before
    public void setUp() throws Exception {

        version = schemaVersions.getDefaultVersion();
        MockitoAnnotations.initMocks(this);

        graph = TinkerGraph.open();

        Long currentTimeMs = System.currentTimeMillis();
        String timeNowInMs = Long.toString(currentTimeMs);

        // PUT / CREATE
        jsonPutObj.addProperty("aai-created-ts", timeNowInMs);
        jsonPutObj.addProperty("aai-last-mod-ts", timeNowInMs);
        jsonPutObj.addProperty("source-of-truth", "user_a");
        jsonPutObj.addProperty("last-mod-source-of-truth", "user_a");
        jsonPutObj.addProperty("last-action-performed", "Created");

        putVertex = graph.addVertex(
            "aai-created-ts", timeNowInMs,
            "aai-last-mod-ts", timeNowInMs,
            "source-of-truth", "user_a",
            "last-mod-source-of-truth", "user_a"
        );

        // PATCH / MODIFY with differing source of truths
        jsonPatchObj1.addProperty("aai-created-ts", timeNowInMs);
        jsonPatchObj1.addProperty("aai-last-mod-ts", timeNowInMs);
        jsonPatchObj1.addProperty("source-of-truth", "user_a");
        jsonPatchObj1.addProperty("last-mod-source-of-truth", "user_b");
        jsonPatchObj1.addProperty("last-action-performed", "Modified");

        patchVertex1 = graph.addVertex(
            "aai-created-ts", timeNowInMs,
            "aai-last-mod-ts", timeNowInMs,
            "source-of-truth", "user_a",
            "last-mod-source-of-truth", "user_b"
        );

        // PATCH / MODIFY with differing time stamps
        jsonPatchObj2.addProperty("aai-created-ts", timeNowInMs);
        jsonPatchObj2.addProperty("aai-last-mod-ts", Long.toString(currentTimeMs + 1000));
        jsonPatchObj2.addProperty("source-of-truth", "user_a");
        jsonPatchObj2.addProperty("last-mod-source-of-truth", "user_a");
        jsonPatchObj2.addProperty("last-action-performed", "Modified");

        patchVertex2 = graph.addVertex(
            "aai-created-ts", timeNowInMs,
            "aai-last-mod-ts", Long.toString(currentTimeMs + 1000),
            "source-of-truth", "user_a",
            "last-mod-source-of-truth", "user_a"
        );

        graph = TinkerGraph.open();
        createLoaderEngineSetup();
    }

    // This test is to simulate a PUT request
    @Test
    public void testGetJsonFromVertexWithCreateVertex() throws AAIFormatVertexException, AAIException {
        if (putVertex == null)
            assertTrue("The vertex used for this test is null. Fail immediately.", false);

        JsonObject json = resourceWithSoT.getJsonFromVertex(putVertex).get();
        assertEquals(jsonPutObj, json);
    }

    // This test is to simulate PATCH requests
    @Test
    public void testGetJsonFromVertexWithModifyVertex() throws AAIFormatVertexException, AAIException {
        if (patchVertex1 == null)
            assertTrue("The vertex 1 used for this test is null. Fail immediately.", false);
        if (patchVertex2 == null)
            assertTrue("The vertex 2 used for this test is null. Fail immediately.", false);

        // Differing Source of Truths will indicate that the action performed modified the vertex
        JsonObject json1 = resourceWithSoT.getJsonFromVertex(patchVertex1).get();
        assertEquals(jsonPatchObj1, json1);

        // Timestamps that have a large span in time difference will (likely) indicate that the transaction was not a create (thus, modify)
        JsonObject json2 = resourceWithSoT.getJsonFromVertex(patchVertex2).get();
        assertEquals(jsonPatchObj2, json2);
    }

    @Test
    public void testGetJsonFromVertexWithNullVertex() throws AAIFormatVertexException, AAIException {
        // Null check, will return null.
        assertNull(resourceWithSoT.getJsonFromVertex(null));
    }

    public void createLoaderEngineSetup() throws AAIException {

        if (loader == null) {
            loader = loaderFactory.createLoaderForVersion(factoryType, version);
            //loader = LoaderFactory.createLoaderForVersion(factoryType, version);
            dbEngine = spy(new JanusGraphDBEngine(QueryStyle.TRAVERSAL, DBConnectionType.CACHED, loader));
            serializer = new DBSerializer(version, dbEngine, factoryType, "Junit");
            resourceWithSoT = new ResourceWithSoT.Builder(loader, serializer, urlBuilder).build();

            TransactionalGraphEngine.Admin spyAdmin = spy(dbEngine.asAdmin());

            when(dbEngine.tx()).thenReturn(graph);
            when(dbEngine.asAdmin()).thenReturn(spyAdmin);

            when(spyAdmin.getReadOnlyTraversalSource())
                .thenReturn(graph.traversal(GraphTraversalSource.build().with(ReadOnlyStrategy.instance())));
            when(spyAdmin.getTraversalSource()).thenReturn(graph.traversal());
        }
    }
}
