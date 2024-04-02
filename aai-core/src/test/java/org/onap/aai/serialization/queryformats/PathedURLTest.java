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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.google.gson.JsonObject;

import java.util.Optional;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.strategy.verification.ReadOnlyStrategy;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class PathedURLTest extends AAISetup {

    @Mock
    private UrlBuilder urlBuilder;

    private Graph graph;
    private TransactionalGraphEngine dbEngine;
    private Loader loader;
    private PathedURL pathedURL;
    private final ModelType factoryType = ModelType.MOXY;

    @Autowired
    private EdgeSerializer rules;

    private SchemaVersion version;
    private Vertex pserver;
    private Vertex complex;
    private DBSerializer serializer;

    @BeforeEach
    public void setUp() throws Exception {

        version = schemaVersions.getDefaultVersion();

        MockitoAnnotations.openMocks(this);

        graph = TinkerGraph.open();

        Vertex pserver1 = graph.addVertex(T.label, "pserver", T.id, "2", "aai-node-type", "pserver", "hostname",
                "hostname-1", "resource-version", System.currentTimeMillis());

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

    private void createLoaderEngineSetup() throws AAIException {

        if (loader == null) {
            loader = loaderFactory.createLoaderForVersion(factoryType, version);
            dbEngine = spy(new JanusGraphDBEngine(QueryStyle.TRAVERSAL, loader));
            serializer = new DBSerializer(version, dbEngine, factoryType, "Junit");

            TransactionalGraphEngine.Admin spyAdmin = spy(dbEngine.asAdmin());

            when(dbEngine.tx()).thenReturn(graph);
            when(dbEngine.asAdmin()).thenReturn(spyAdmin);

            when(spyAdmin.getReadOnlyTraversalSource())
                    .thenReturn(graph.traversal().withStrategies(ReadOnlyStrategy.instance()));
            when(spyAdmin.getTraversalSource()).thenReturn(graph.traversal());
        }
    }

    @Test
    public void testPathedUrlReturnsResourceVersionWhenSet() throws AAIFormatVertexException, AAIException {

        pathedURL = new PathedURL.Builder(loader, serializer, urlBuilder).includeUrl().build();
        when(urlBuilder.pathed(pserver)).thenReturn("/aai/v14/cloud-infrastructure/pservers/pserver/hostname-1");
        Optional<JsonObject> jsonObjectOptional = pathedURL.getJsonFromVertex(pserver);

        if (!jsonObjectOptional.isPresent()) {
            fail("Expecting an json object returned from pathed url but returned none");
        }

        JsonObject pserverObject = jsonObjectOptional.get();

        assertNotNull(pserverObject.get("resource-type"), "Expecting the pserver object to contain resource type");
        assertThat(pserverObject.get("resource-type").getAsString(), CoreMatchers.is("pserver"));
        assertNotNull(pserverObject.get("resource-link"), "Expecting the pserver object to contain resource link");
        assertThat(pserverObject.get("resource-link").getAsString(),
                CoreMatchers.is("/aai/v14/cloud-infrastructure/pservers/pserver/hostname-1"));
        assertNotNull(pserverObject.get("resource-version"),
                "Expecting the pserver object to contain resource version");
    }

    @Test
    public void testPathedUrlReturnsResourceVersionWhenIncludeUrlIsNotSet()
            throws AAIFormatVertexException, AAIException {

        pathedURL = new PathedURL.Builder(loader, serializer, urlBuilder).build();
        when(urlBuilder.pathed(pserver)).thenReturn("/aai/v14/cloud-infrastructure/pservers/pserver/hostname-1");
        Optional<JsonObject> jsonObjectOptional = pathedURL.getJsonFromVertex(pserver);

        if (!jsonObjectOptional.isPresent()) {
            fail("Expecting an json object returned from pathed url but returned none");
        }

        JsonObject pserverObject = jsonObjectOptional.get();

        assertNotNull(pserverObject.get("resource-type"), "Expecting the pserver object to contain resource type");
        assertThat(pserverObject.get("resource-type").getAsString(), CoreMatchers.is("pserver"));
        assertNotNull(pserverObject.get("resource-link"), "Expecting the pserver object to contain resource link");
        assertThat(pserverObject.get("resource-link").getAsString(),
                CoreMatchers.is("/aai/v14/cloud-infrastructure/pservers/pserver/hostname-1"));
        assertNull(pserverObject.get("resource-version"),
                "Expecting the pserver object to not contain resource version");
    }
}
