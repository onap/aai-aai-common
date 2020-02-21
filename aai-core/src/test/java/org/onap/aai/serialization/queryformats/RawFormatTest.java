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
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.aai.AAISetup;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.db.EdgeSerializer;
import org.onap.aai.serialization.engines.JanusGraphDBEngine;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatVertexException;
import org.onap.aai.serialization.queryformats.utils.UrlBuilder;
import org.onap.aai.setup.SchemaVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class RawFormatTest extends AAISetup {

    @Mock
    private UrlBuilder urlBuilder;

    private Graph graph;
    private TransactionalGraphEngine dbEngine;
    private Loader loader;
    private RawFormat rawFormat;
    private final ModelType factoryType = ModelType.MOXY;

    @Autowired
    private EdgeSerializer rules;

    private SchemaVersion version;
    private Vertex pserver;
    private Vertex complex;

    @Before
    public void setUp() throws Exception {

        version = schemaVersions.getDefaultVersion();

        MockitoAnnotations.initMocks(this);

        graph = TinkerGraph.open();

        Vertex pserver1 =
                graph.addVertex(T.label, "pserver", T.id, "2", "aai-node-type", "pserver", "hostname", "hostname-1");
        Vertex complex1 = graph.addVertex(T.label, "complex", T.id, "3", "aai-node-type", "complex",
                "physical-location-id", "physical-location-id-1", "country", "US");

        GraphTraversalSource g = graph.traversal();
        rules.addEdge(g, pserver1, complex1);

        pserver = pserver1;
        complex = complex1;

        System.setProperty("AJSC_HOME", ".");
        System.setProperty("BUNDLECONFIG_DIR", "src/test/resources/bundleconfig-local");

        createLoaderEngineSetup();
    }

    @Test
    public void verifyPserverRelatedToHasEdgeLabel()
            throws AAIFormatVertexException {
        assertEquals("org.onap.relationships.inventory.LocatedIn", rawFormat.createRelationshipObject(pserver).get(0).getAsJsonObject().get("relationship-label").getAsString());
    }

    @Test
    public void verifyPserverRelatedToComplexLabel()
            throws AAIFormatVertexException {
        assertEquals("complex", rawFormat.createRelationshipObject(pserver).get(0).getAsJsonObject().get("node-type").getAsString());
    }

    @Test
    public void verifyComplexRelatedToHasEdgeLabel()
            throws AAIFormatVertexException {
        assertEquals("org.onap.relationships.inventory.LocatedIn", rawFormat.createRelationshipObject(complex).get(0).getAsJsonObject().get("relationship-label").getAsString());
    }

    @Test
    public void verifyComplexRelatedToPserverLabel()
            throws AAIFormatVertexException {
        assertEquals("pserver", rawFormat.createRelationshipObject(complex).get(0).getAsJsonObject().get("node-type").getAsString());
    }

    private void createLoaderEngineSetup() throws AAIException {

        if (loader == null) {
            loader = loaderFactory.createLoaderForVersion(factoryType, version);
            dbEngine = spy(new JanusGraphDBEngine(QueryStyle.TRAVERSAL, loader));
            DBSerializer serializer = new DBSerializer(version, dbEngine, factoryType, "Junit");
            rawFormat = new RawFormat.Builder(loader, serializer, urlBuilder).build();

            TransactionalGraphEngine.Admin spyAdmin = spy(dbEngine.asAdmin());

            when(dbEngine.tx()).thenReturn(graph);
            when(dbEngine.asAdmin()).thenReturn(spyAdmin);

            when(spyAdmin.getReadOnlyTraversalSource())
                    .thenReturn(graph.traversal().withStrategies(ReadOnlyStrategy.instance()));
            when(spyAdmin.getTraversalSource()).thenReturn(graph.traversal());
        }
    }

    @Test
    public void run() throws AAIFormatVertexException {
        assertNotNull(dbEngine.tx());
        System.out.println(dbEngine.tx());
        assertNotNull(graph.traversal());
        JsonObject json = rawFormat.createPropertiesObject(pserver).get();
        json.entrySet().forEach((System.out::println));
        assertTrue(json.has("hostname"));
        Map<String, List<String>> propMap = new HashMap<>();
        List<String> selectedProps = new ArrayList<String>( Arrays.asList("'physical-location-id'"));
        propMap.put("complex",selectedProps);
        JsonObject json1 = rawFormat.createSelectedPropertiesObject(complex, propMap).get();
        json1.entrySet().forEach((System.out::println));
        assertFalse(json1.has("aai-node-type"));
        assertTrue(json1.has("physical-location-id"));
    }


}
