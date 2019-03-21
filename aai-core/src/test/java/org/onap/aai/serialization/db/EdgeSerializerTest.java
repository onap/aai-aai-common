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

package org.onap.aai.serialization.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onap.aai.AAISetup;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.serialization.db.exceptions.EdgeMultiplicityException;
import org.springframework.beans.factory.annotation.Autowired;

public class EdgeSerializerTest extends AAISetup {
    @Autowired
    EdgeSerializer rules;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void addTreeEdgeTest() throws AAIException {
        Graph graph = TinkerGraph.open();
        Vertex v1 = graph.addVertex(T.id, "1", "aai-node-type", "cloud-region");
        Vertex v2 = graph.addVertex(T.id, "10", "aai-node-type", "tenant");
        GraphTraversalSource g = graph.traversal();
        rules.addTreeEdge(g, v1, v2);
        assertEquals(true, g.V(v1).in("org.onap.relationships.inventory.BelongsTo")
            .has("aai-node-type", "tenant").hasNext());

        Vertex v3 = graph.addVertex(T.id, "2", "aai-node-type", "cloud-region");
        assertEquals(null, rules.addTreeEdgeIfPossible(g, v3, v2));
    }

    @Test
    public void addCousinEdgeTest() throws AAIException {
        Graph graph = TinkerGraph.open();
        Vertex v1 = graph.addVertex(T.id, "1", "aai-node-type", "flavor");
        Vertex v2 = graph.addVertex(T.id, "10", "aai-node-type", "vserver");
        GraphTraversalSource g = graph.traversal();
        rules.addEdge(g, v1, v2);
        assertEquals(true, g.V(v2).out("org.onap.relationships.inventory.Uses")
            .has("aai-node-type", "flavor").hasNext());

        Vertex v3 = graph.addVertex(T.id, "2", "aai-node-type", "flavor");
        assertEquals(null, rules.addEdgeIfPossible(g, v3, v2));
    }

    @Test
    public void multiplicityViolationTest() throws AAIException {
        thrown.expect(EdgeMultiplicityException.class);
        thrown.expectMessage(
            "multiplicity rule violated: only one edge can exist with label: org.onap.relationships.inventory.Uses between vf-module and volume-group");

        Graph graph = TinkerGraph.open();
        Vertex v1 = graph.addVertex(T.id, "1", "aai-node-type", "vf-module");
        Vertex v2 = graph.addVertex(T.id, "10", "aai-node-type", "volume-group");
        GraphTraversalSource g = graph.traversal();

        rules.addEdge(g, v2, v1);
        Vertex v3 = graph.addVertex(T.id, "3", "aai-node-type", "vf-module");
        rules.addEdge(g, v2, v3);
    }

    @Test
    public void addEdgeVerifyAAIUUIDCousinTest() throws AAIException {
        Graph graph = TinkerGraph.open();
        Vertex v1 = graph.addVertex(T.id, "1", "aai-node-type", "flavor");
        Vertex v2 = graph.addVertex(T.id, "10", "aai-node-type", "vserver");
        GraphTraversalSource g = graph.traversal();
        Edge e = rules.addEdge(g, v1, v2);
        assertTrue(e.property(AAIProperties.AAI_UUID).isPresent());
        // assertTrue(e.property(AAIProperties.AAI_UUID).value().toString().matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"));
    }

    @Test
    public void addEdgeVerifyAAIUUIDTreeTest() throws AAIException {
        Graph graph = TinkerGraph.open();
        Vertex v1 = graph.addVertex(T.id, "1", "aai-node-type", "tenant");
        Vertex v2 = graph.addVertex(T.id, "10", "aai-node-type", "vserver");
        GraphTraversalSource g = graph.traversal();
        Edge e = rules.addTreeEdge(g, v1, v2);
        assertTrue(e.property(AAIProperties.AAI_UUID).isPresent());
        // assertTrue(e.property(AAIProperties.AAI_UUID).value().toString().matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"));
    }

}
