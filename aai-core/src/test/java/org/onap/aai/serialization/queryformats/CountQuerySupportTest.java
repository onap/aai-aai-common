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
import org.junit.After;
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
import org.onap.aai.serialization.db.EdgeSerializer;
import org.onap.aai.serialization.engines.JanusGraphDBEngine;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatQueryResultFormatNotSupported;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatVertexException;
import org.onap.aai.serialization.queryformats.utils.UrlBuilder;
import org.onap.aai.setup.SchemaVersion;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class CountQuerySupportTest extends AAISetup {

    @Autowired
    private EdgeSerializer edgeSer;

    private Graph graph;
    private TransactionalGraphEngine dbEngine;
    private Loader loader;
    private final ModelType factoryType = ModelType.MOXY;
    
    private SchemaVersion version;
    Vertex pserver1;
    Vertex complex1;
    Vertex complex2;

    private DBSerializer serializer;
    
    private FormatFactory ff;
    private Formatter formatter;

    
    
    @Before
    public void setUp() throws Exception {

        version = schemaVersions.getDefaultVersion();
        MockitoAnnotations.initMocks(this);

        graph = TinkerGraph.open();

        pserver1 = graph.addVertex(T.label, "pserver", T.id, "2", "aai-node-type", "pserver", "hostname",
                "hostname-1");
        complex1 = graph.addVertex(T.label, "complex", T.id, "3", "aai-node-type", "complex",
                "physical-location-id", "physical-location-id-1", "country", "US");
        
        complex2 = graph.addVertex(T.label, "complex", T.id, "4", "aai-node-type", "complex",
                "physical-location-id", "physical-location-id-2", "country", "US");

        GraphTraversalSource g = graph.traversal();
        edgeSer.addEdge(g, pserver1, complex1);
        
        createLoaderEngineSetup();

    }
    
    @After
    public void tearDown() throws Exception {
        graph.close();
    }

    @Test
    public void verifyComplexVertexCountTest1() throws AAIFormatVertexException, AAIException, AAIFormatQueryResultFormatNotSupported {
        List<Object> complexList = Arrays.asList(this.complex1, this.complex2 );
        JsonObject jo = this.formatter.output(complexList);
        assertEquals(2, jo.get("results").getAsJsonArray().get(0).getAsJsonObject().get("complex").getAsInt());
    }
    
    @Test
    public void verifyPserverVertexCountTest1() throws AAIFormatVertexException, AAIException, AAIFormatQueryResultFormatNotSupported {
        List<Object> pserverList = Arrays.asList(this.pserver1 );
        JsonObject jo = this.formatter.output(pserverList);
        assertEquals(1, jo.get("results").getAsJsonArray().get(0).getAsJsonObject().get("pserver").getAsInt());
    }
    
    @Test
    public void verifyComplexVertexCountTest2() throws AAIFormatVertexException, AAIException, AAIFormatQueryResultFormatNotSupported {
        List<Object> list = Arrays.asList(this.complex1, this.pserver1, this.complex2 );
        JsonObject jo = this.formatter.output(list);
        assertEquals(2, jo.get("results").getAsJsonArray().get(0).getAsJsonObject().get("complex").getAsInt());
    }
    
    @Test
    public void verifyPserverVertexCountTest2() throws AAIFormatVertexException, AAIException, AAIFormatQueryResultFormatNotSupported {
        List<Object> list = Arrays.asList(this.complex1, this.pserver1, this.complex2 );
        JsonObject jo = this.formatter.output(list);
        assertEquals(1, jo.get("results").getAsJsonArray().get(0).getAsJsonObject().get("pserver").getAsInt());
    }
    
    @Test
    public void verifyLongTest() throws AAIFormatVertexException, AAIException, AAIFormatQueryResultFormatNotSupported {
        List<Object> complexList = Arrays.asList(Long.valueOf(22L) );
        JsonObject jo = this.formatter.output(complexList);
        assertEquals(22, jo.get("results").getAsJsonArray().get(0).getAsJsonObject().get("count").getAsInt());
    }


    public void createLoaderEngineSetup() throws AAIException {

        if (loader == null) {
            loader = loaderFactory.createLoaderForVersion(factoryType, version);
            //loader = LoaderFactory.createLoaderForVersion(factoryType, version);
            dbEngine = spy(new JanusGraphDBEngine(QueryStyle.TRAVERSAL, DBConnectionType.CACHED, loader));
            serializer = new DBSerializer(version, dbEngine, factoryType, "Junit");
            
            ff = new FormatFactory(loader, serializer, schemaVersions, basePath);
            formatter = ff.get(Format.count);

            
            TransactionalGraphEngine.Admin spyAdmin = spy(dbEngine.asAdmin());

            when(dbEngine.tx()).thenReturn(graph);
            when(dbEngine.asAdmin()).thenReturn(spyAdmin);

            when(spyAdmin.getReadOnlyTraversalSource())
                    .thenReturn(graph.traversal(GraphTraversalSource.build().with(ReadOnlyStrategy.instance())));
            when(spyAdmin.getTraversalSource()).thenReturn(graph.traversal());
        }
    }
}
