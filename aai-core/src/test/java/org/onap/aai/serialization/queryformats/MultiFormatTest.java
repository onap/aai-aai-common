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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.serialization.queryformats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;

import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.Tree;
import org.apache.tinkerpop.gremlin.process.traversal.strategy.verification.ReadOnlyStrategy;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
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
import org.onap.aai.serialization.db.EdgeSerializer;
import org.onap.aai.serialization.engines.JanusGraphDBEngine;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatQueryResultFormatNotSupported;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatVertexException;
import org.onap.aai.serialization.queryformats.utils.UrlBuilder;
import org.onap.aai.setup.SchemaVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class MultiFormatTest extends AAISetup {

    @Mock
    private UrlBuilder urlBuilder;

    private Graph graph;
    private TransactionalGraphEngine dbEngine;
    private Loader loader;
    private IdURL idFormat;
    private final ModelType factoryType = ModelType.MOXY;
    @Autowired
    private EdgeSerializer rules;
    private Tree<?> resultTree;
    private Path resultPath;
    private SchemaVersion version;
    private JsonObject expectedTreeIdFormat = new JsonParser().parse(
        "{\"nodes\":[{\"resource-type\":\"generic-vnf\",\"nodes\":[{\"resource-type\":\"vserver\",\"nodes\":[{\"resource-type\":\"pserver\"}]},{\"resource-type\":\"pserver\",\"nodes\":[{\"resource-type\":\"complex\"}]}]}]}")
        .getAsJsonObject();
    private JsonObject expectedPathIdFormat = new JsonParser().parse(
        "{\"path\":[{\"resource-type\":\"generic-vnf\"},{\"resource-type\":\"vserver\"},{\"resource-type\":\"pserver\"},{\"resource-type\":\"complex\"}]}")
        .getAsJsonObject();

    @Before
    public void setUp() throws Exception {

        version = schemaVersions.getAppRootVersion();
        MockitoAnnotations.initMocks(this);

        graph = TinkerGraph.open();

        Vertex gnvf1 = graph.addVertex(T.label, "generic-vnf", T.id, "0", "aai-node-type",
            "generic-vnf", "vnf-id", "vnf-id-1", "vnf-name", "vnf-name-1");
        Vertex vserver1 = graph.addVertex(T.label, "vserver", T.id, "1", "aai-node-type", "vserver",
            "vserver-id", "vserver-id-1", "vserver-name", "vserver-name-1");
        Vertex pserver1 = graph.addVertex(T.label, "pserver", T.id, "2", "aai-node-type", "pserver",
            "hostname", "hostname-1");
        Vertex complex1 = graph.addVertex(T.label, "complex", T.id, "3", "aai-node-type", "complex",
            "physical-location-id", "physical-location-id-1", "country", "US");

        Vertex pserver2 = graph.addVertex(T.label, "pserver", T.id, "5", "aai-node-type", "pserver",
            "hostname", "hostname-2");
        Vertex complex2 = graph.addVertex(T.label, "complex", T.id, "6", "aai-node-type", "complex",
            "physical-location-id", "physical-location-id-2", "country", "US");

        GraphTraversalSource g = graph.traversal();
        rules.addEdge(g, gnvf1, vserver1);
        rules.addEdge(g, vserver1, pserver1);
        rules.addEdge(g, pserver1, complex1);
        rules.addEdge(g, gnvf1, pserver2);
        rules.addEdge(g, pserver2, complex2);

        resultTree = graph.traversal().V("0").out().out().tree().next();
        resultPath = graph.traversal().V("0").out().hasId("1").out().hasId("2").out().hasId("3")
            .path().next();
    }

    @Test
    public void testTreeResultQueryIdFormat()
        throws AAIFormatVertexException, AAIException, AAIFormatQueryResultFormatNotSupported {

        createLoaderEngineSetup();
        idFormat = new IdURL(loader, urlBuilder);

        assertNotNull(dbEngine.tx());
        assertNotNull(dbEngine.asAdmin());

        JsonObject json = idFormat.formatObject(resultTree).get();

        assertEquals(this.expectedTreeIdFormat, json);

    }

    @Test
    public void testPathResultQueryIdFormat()
        throws AAIFormatVertexException, AAIException, AAIFormatQueryResultFormatNotSupported {

        createLoaderEngineSetup();
        idFormat = new IdURL(loader, urlBuilder);

        assertNotNull(dbEngine.tx());
        assertNotNull(dbEngine.asAdmin());

        JsonObject json = idFormat.formatObject(resultPath).get();

        assertEquals(this.expectedPathIdFormat, json);

    }

    @Test(expected = AAIFormatQueryResultFormatNotSupported.class)
    public void testThrowsExceptionIfObjectNotSupported() throws AAIFormatVertexException,
        AAIException, UnsupportedEncodingException, AAIFormatQueryResultFormatNotSupported {

        loader = mock(Loader.class);
        idFormat = new IdURL(loader, urlBuilder);
        idFormat.formatObject(new String());
    }

    public void createLoaderEngineSetup() {

        if (loader == null) {
            loader = loaderFactory.createLoaderForVersion(factoryType, version);
            // loader = LoaderFactory.createLoaderForVersion(factoryType, version);
            dbEngine =
                spy(new JanusGraphDBEngine(QueryStyle.TRAVERSAL, DBConnectionType.CACHED, loader));

            TransactionalGraphEngine.Admin spyAdmin = spy(dbEngine.asAdmin());

            when(dbEngine.tx()).thenReturn(graph);
            when(dbEngine.asAdmin()).thenReturn(spyAdmin);

            when(spyAdmin.getReadOnlyTraversalSource()).thenReturn(
                graph.traversal(GraphTraversalSource.build().with(ReadOnlyStrategy.instance())));
            when(spyAdmin.getTraversalSource()).thenReturn(graph.traversal());
        }
    }
}
