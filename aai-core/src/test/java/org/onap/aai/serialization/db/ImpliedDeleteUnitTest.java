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

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.serialization.engines.query.QueryEngine;
import org.onap.aai.util.AAIConstants;
import org.springframework.boot.test.rule.OutputCapture;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.eq;

public class ImpliedDeleteUnitTest {

    private TransactionalGraphEngine mockEngine;
    private DBSerializer mockSerializer;

    private ImpliedDelete impliedDelete;

    @Rule
    public final OutputCapture outputCapture = new OutputCapture();

    @Before
    public void setup(){
        mockEngine     = Mockito.mock(TransactionalGraphEngine.class);
        mockSerializer = Mockito.mock(DBSerializer.class);
        impliedDelete  = Mockito.spy(new ImpliedDelete(mockEngine, mockSerializer));
    }

    // aai.implied.delete.whitelist.sdnc=*
    @Test
    public void testImpliedDeleteWhenUserIsAllowedToInvokeAnyMethod() throws AAIException {

        QueryEngine mockQueryEngine = Mockito.mock(QueryEngine.class);

        Mockito
            .doReturn("*")
            .when(impliedDelete)
            .get(AAIConstants.AAI_IMPLIED_DELETE_WHITELIST + "sdnc","");

        Mockito.when(mockEngine.getQueryEngine()).thenReturn(mockQueryEngine);

        TinkerGraph graph = TinkerGraph.open();

        Vertex vserver = graph.addVertex("vertex");
        vserver.property("vserver-id", "some-id");

        Vertex volume = graph.addVertex("vertex");
        volume.property("volume-id", "some-id");

        List<Vertex> vertices = new ArrayList<>();
        vertices.add(volume);

        impliedDelete.execute(vserver.id(), "SDNC", "vserver", vertices);

    }

    // aai.implied.delete.whitelist.sdnc='vserver'
    @Test
    public void testImpliedDeleteWhenUserIsAllowedToInvokeCertainMethod() throws AAIException {

        QueryEngine mockQueryEngine = Mockito.mock(QueryEngine.class);

        Mockito
            .doReturn("'vserver'")
            .when(impliedDelete)
            .get(AAIConstants.AAI_IMPLIED_DELETE_WHITELIST + "sdnc","");

        Mockito.when(mockEngine.getQueryEngine()).thenReturn(mockQueryEngine);

        TinkerGraph graph = TinkerGraph.open();

        Vertex vserver = graph.addVertex("vertex");
        vserver.property("vserver-id", "some-id");

        Vertex volume = graph.addVertex("vertex");
        volume.property("volume-id", "some-id");

        List<Vertex> vertices = new ArrayList<>();
        vertices.add(volume);

        impliedDelete.execute(vserver.id(), "SDNC", "vserver", vertices);
    }

    // aai.implied.delete.whitelist.sdnc='vserver'
    @Test
    public void testImpliedDeleteWhenUserIsAllowedAndTryVariationsOfSOTValueSDNC() throws AAIException {

        QueryEngine mockQueryEngine = Mockito.mock(QueryEngine.class);

        Mockito
            .doReturn("'vserver'")
            .when(impliedDelete)
            .get(AAIConstants.AAI_IMPLIED_DELETE_WHITELIST + "sdnc","");

        Mockito.when(mockEngine.getQueryEngine()).thenReturn(mockQueryEngine);

        TinkerGraph graph = TinkerGraph.open();

        Vertex vserver = graph.addVertex("vertex");
        vserver.property("vserver-id", "some-id");

        Vertex volume = graph.addVertex("vertex");
        volume.property("volume-id", "some-id");

        List<Vertex> vertices = new ArrayList<>();
        vertices.add(volume);

        impliedDelete.execute(vserver.id(), "SDNC", "vserver", vertices);
        impliedDelete.execute(vserver.id(), "sDNC", "vserver", vertices);
        impliedDelete.execute(vserver.id(), "sdNC", "vserver", vertices);
        impliedDelete.execute(vserver.id(), "sdnC", "vserver", vertices);
        impliedDelete.execute(vserver.id(), "sdnc", "vserver", vertices);
    }

    // aai.implied.delete.whitelist.sdnc='vserver','vce','pserver'
    @Test
    public void testImpliedDeleteWhenUserIsAllowedToInvokeMultipleMethods() throws AAIException {

        QueryEngine mockQueryEngine = Mockito.mock(QueryEngine.class);

        Mockito
            .doReturn("'vce','pserver','vserver','cloud-region'")
            .when(impliedDelete)
            .get(AAIConstants.AAI_IMPLIED_DELETE_WHITELIST + "sdnc","");

        Mockito.when(mockEngine.getQueryEngine()).thenReturn(mockQueryEngine);

        TinkerGraph graph = TinkerGraph.open();

        Vertex vserver = graph.addVertex("vertex");
        vserver.property("vserver-id", "some-id");

        Vertex volume = graph.addVertex("vertex");
        volume.property("volume-id", "some-id");

        List<Vertex> vertices = new ArrayList<>();
        vertices.add(volume);

        impliedDelete.execute(vserver.id(), "SDNC", "vserver", vertices);
    }

    // aai.implied.delete.whitelist.sdnc='vserver','vce','pserver'
    @Test
    public void testImpliedDeleteWhenUserIsAllowedToInvokeMultipleMethodsAndDeletableReturnsMultipleVertexes()
        throws AAIException, UnsupportedEncodingException {

        QueryEngine mockQueryEngine = Mockito.mock(QueryEngine.class);

        // On a spy the syntax should be doReturn => when => method to spy
        // On a mock the syntax should be when => thenReturn|thenAnswer
        Mockito
            .doReturn("'vserver'")
            .when(impliedDelete)
            .get(AAIConstants.AAI_IMPLIED_DELETE_WHITELIST + "sdnc","");


        Introspector mockIntrospector = Mockito.mock(Introspector.class);

        Mockito.when(mockEngine.getQueryEngine()).thenReturn(mockQueryEngine);

        TinkerGraph graph = TinkerGraph.open();

        Vertex vserver = graph.addVertex("vertex");
        vserver.property("vserver-id", "some-id");

        Vertex volume1 = graph.addVertex("vertex");
        volume1.property("volume-id", "volume-1");

        Vertex volume2 = graph.addVertex("vertex");
        volume1.property("volume-id", "volume-2");

        Vertex volume3 = graph.addVertex("vertex");
        volume1.property("volume-id", "volume-3");

        Vertex volume4 = graph.addVertex("vertex");
        volume1.property("volume-id", "volume-4");

        List<Vertex> vertices = new ArrayList<>();

        vertices.add(volume1);
        vertices.add(volume2);
        vertices.add(volume3);
        vertices.add(volume4);

        Mockito
            .when(mockQueryEngine.findDeletable(Mockito.anyList()))
            .thenReturn(vertices);

        Mockito
            .when(mockSerializer.getLatestVersionView(Mockito.anyObject()))
            .thenReturn(mockIntrospector);

        Mockito
            .when(mockIntrospector.marshal(false))
            .thenReturn("{\"volume-id\":\"volume-1\"}")
            .thenReturn("{\"volume-id\":\"volume-2\"}")
            .thenReturn("{\"volume-id\":\"volume-3\"}")
            .thenReturn("{\"volume-id\":\"volume-4\"}");

        impliedDelete.execute(vserver.id(), "SDNC", "vserver", vertices);
    }

    // aai.implied.delete.whitelist.sdnc=
    @Test(expected = AAIException.class)
    public void testImpliedDeleteWhenUserIsNotAllowedToDelete() throws AAIException {

        QueryEngine mockQueryEngine = Mockito.mock(QueryEngine.class);

        Mockito
            .doReturn("")
            .when(impliedDelete)
            .get(AAIConstants.AAI_IMPLIED_DELETE_WHITELIST + "sdnc","");

        Mockito.when(mockEngine.getQueryEngine()).thenReturn(mockQueryEngine);

        TinkerGraph graph = TinkerGraph.open();

        Vertex vserver = graph.addVertex("vertex");
        vserver.property("vserver-id", "some-id");

        Vertex volume = graph.addVertex("vertex");
        volume.property("volume-id", "some-id");

        List<Vertex> vertices = new ArrayList<>();
        vertices.add(volume);

        impliedDelete.execute(vserver.id(), "SDNC", "vserver", vertices);
    }

    // aai.implied.delete.whitelist.sdnc='vce'
    @Test(expected = AAIException.class)
    public void testImpliedDeleteWhenUserIsAllowedToDeleteVceChildrenButRequestedToDeleteVserverChildren() throws AAIException {

        QueryEngine mockQueryEngine = Mockito.mock(QueryEngine.class);

        Mockito
            .doReturn("'vce'")
            .when(impliedDelete)
            .get(AAIConstants.AAI_IMPLIED_DELETE_WHITELIST + "sdnc","");

        Mockito.when(mockEngine.getQueryEngine()).thenReturn(mockQueryEngine);

        TinkerGraph graph = TinkerGraph.open();

        Vertex vserver = graph.addVertex("vertex");
        vserver.property("vserver-id", "some-id");

        Vertex volume = graph.addVertex("vertex");
        volume.property("volume-id", "some-id");

        List<Vertex> vertices = new ArrayList<>();
        vertices.add(volume);

        impliedDelete.execute(vserver.id(), "SDNC", "vserver", vertices);
    }

    @Test
    public void testImpliedDeleteWhenUserIsAllowedToDeleteAndPrintingDeletingVertexItThrowsExceptionVerifyLog() throws AAIException, UnsupportedEncodingException {

        QueryEngine mockQueryEngine = Mockito.mock(QueryEngine.class);

        // On a spy the syntax should be doReturn => when => method to spy
        // On a mock the syntax should be when => thenReturn|thenAnswer
        Mockito
            .doReturn("'vserver'")
            .when(impliedDelete)
            .get(AAIConstants.AAI_IMPLIED_DELETE_WHITELIST + "sdnc","");


        Introspector mockIntrospector = Mockito.mock(Introspector.class);

        Mockito.when(mockEngine.getQueryEngine()).thenReturn(mockQueryEngine);

        TinkerGraph graph = TinkerGraph.open();

        Vertex vserver = graph.addVertex("vertex");
        vserver.property("vserver-id", "some-id");

        Vertex volume1 = graph.addVertex("vertex");
        volume1.property("volume-id", "volume-1");

        List<Vertex> vertices = new ArrayList<>();

        vertices.add(volume1);

        Mockito
            .when(mockQueryEngine.findDeletable(Mockito.anyList()))
            .thenReturn(vertices);

        Mockito
            .when(mockSerializer.getLatestVersionView(Mockito.anyObject()))
            .thenThrow(new RuntimeException("Unable to find node"));

        impliedDelete.execute(vserver.id(), "SDNC", "vserver", vertices);

        outputCapture.expect(
            CoreMatchers.containsString("Encountered an exception during retrieval of vertex properties with vertex-id " + vserver.id())
        );
    }
}
