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
import org.springframework.test.annotation.DirtiesContext;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ResourceFormatTest extends AAISetup {

    @Mock
    private UrlBuilder urlBuilder;

    private Graph graph;
    private TransactionalGraphEngine dbEngine;
    private Loader loader;
    private DBSerializer serializer;
    private Resource resource;
    private Vertex vfModule;
    private Vertex unknown;
    private final ModelType factoryType = ModelType.MOXY;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        graph = TinkerGraph.open();

        vfModule = graph.addVertex(
                T.label, "vf-module",
                T.id, "5",
                "aai-node-type", "vf-module",
                "vf-module-id", "vf-module-id-val-68205",
                "vf-module-name", "example-vf-module-name-val-68205",
                "heat-stack-id", "example-heat-stack-id-val-68205",
                "orchestration-status", "example-orchestration-status-val-68205",
                "is-base-vf-module", "true",
                "resource-version", "1498166571906",
                "model-invariant-id", "fe8aac07-ce6c-4f9f-aa0d-b561c77da9e8",
                "model-invariant-id-local", "fe8aac07-ce6c-4f9f-aa0d-b561c77da9e8",
                "model-version-id", "0d23052d-8ffe-433e-a25d-da5da027bb7c",
                "model-version-id-local", "0d23052d-8ffe-433e-a25d-da5da027bb7c",
                "widget-model-id", "example-widget-model-id-val-68205",
                "widget-model-version", "example-widget--model-version-val-68205",
                "contrail-service-instance-fqdn", "example-contrail-service-instance-fqdn-val-68205"
        );

        unknown = graph.addVertex(T.label, "unknown", T.id, "1", "aai-node-type", "unknown", "vserver-id",
                "vserver-id-1", "vserver-name", "vserver-name-1");
    }

    @Test
    public void testCreatePropertiesObjectReturnsProperProperties() throws AAIFormatVertexException, AAIException {

        createLoaderEngineSetup();
        serializer = new DBSerializer(schemaVersions.getAppRootVersion(), dbEngine, factoryType, "Junit");
        resource = new Resource.Builder(loader, serializer, urlBuilder).build();

        assertNotNull(dbEngine.tx());
        assertNotNull(dbEngine.asAdmin());

        JsonObject json = resource.getJsonFromVertex(vfModule).get();

        assertTrue(json.getAsJsonObject("vf-module").has("model-invariant-id"));
        assertTrue(json.getAsJsonObject("vf-module").has("model-version-id"));

        assertFalse(json.getAsJsonObject("vf-module").has("model-invariant-id-local"));
        assertFalse(json.getAsJsonObject("vf-module").has("model-version-id-local"));

    }

    @Test
    public void testUnknownVertex() throws AAIFormatVertexException, AAIException {

        createLoaderEngineSetup();
        serializer = new DBSerializer(schemaVersions.getAppRootVersion(), dbEngine, factoryType, "Junit");
        resource = new Resource.Builder(loader, serializer, urlBuilder).build();

        assertNotNull(dbEngine.tx());
        assertNotNull(dbEngine.asAdmin());

        assertFalse(resource.getJsonFromVertex(unknown).isPresent());

    }

    @Test
    public void testFormattingUnknownVertex() throws AAIFormatVertexException, AAIException {

        createLoaderEngineSetup();
        serializer = new DBSerializer(schemaVersions.getAppRootVersion(), dbEngine, factoryType, "Junit");

        FormatFactory ff = new FormatFactory(loader, serializer,schemaVersions, basePath);
        MultivaluedMap mvm = new MultivaluedHashMap();
        mvm.add("depth","0");
        Formatter formatter =  ff.get(Format.resource, mvm);

        JsonObject json = formatter.output(Arrays.asList(unknown,vfModule));
        System.out.println(json);

    }

    public void createLoaderEngineSetup(){

        if(loader == null){
            loader = loaderFactory.createLoaderForVersion(factoryType, schemaVersions.getAppRootVersion());
            dbEngine = spy(new JanusGraphDBEngine(QueryStyle.TRAVERSAL, DBConnectionType.CACHED, loader));

            TransactionalGraphEngine.Admin spyAdmin = spy(dbEngine.asAdmin());

            when(dbEngine.tx()).thenReturn(graph);
            when(dbEngine.asAdmin()).thenReturn(spyAdmin);

            when(spyAdmin.getReadOnlyTraversalSource()).thenReturn(graph.traversal(GraphTraversalSource.build().with(ReadOnlyStrategy.instance())));
            when(spyAdmin.getTraversalSource()).thenReturn(graph.traversal());
        }
    }
}
