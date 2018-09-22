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
package org.onap.aai.serialization.engines.query;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.serialization.db.AAICoreFakeEdgesConfigTranslator;
import org.onap.aai.serialization.db.EdgeSerializer;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.setup.SchemaVersions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        SchemaLocationsBean.class,
        SchemaVersions.class,
        AAICoreFakeEdgesConfigTranslator.class,
        EdgeIngestor.class,
        EdgeSerializer.class
})
@DirtiesContext
public class GraphTraversalQueryEngine_needsFakeEdgeRulesTest {
    @Autowired
    EdgeSerializer edgeSer;
    
    @Test
    public void testFindDeletable() throws AAIException {
        //setup
        Graph graph = TinkerGraph.open();
        Vertex parent = graph.addVertex(T.id, "00", "aai-node-type", "test-parent");
        Vertex child = graph.addVertex(T.id, "10", "aai-node-type", "test-child");
        Vertex cousin = graph.addVertex(T.id, "20", "aai-node-type", "test-cousin");
        Vertex grandchild = graph.addVertex(T.id, "30", "aai-node-type", "test-grandchild");

        GraphTraversalSource g = graph.traversal();
        
        edgeSer.addTreeEdge(g, parent, child); //delete-other-v=none, no cascade
        edgeSer.addTreeEdge(g, child, grandchild); //d-o-v=out, yes from child
        edgeSer.addEdge(g, cousin, child); //d-o-v=out, yes from cousin
        
        List<Vertex> parentExpected = new ArrayList<>(Arrays.asList(parent));
        List<Vertex> childExpected = new ArrayList<>(Arrays.asList(child, grandchild));
        List<Vertex> cousinExpected = new ArrayList<>(Arrays.asList(cousin, child, grandchild));
        
        GraphTraversalQueryEngine engine = new GraphTraversalQueryEngine(g);
        
        //tests
        List<Vertex> parentDeletes = engine.findDeletable(parent);
        assertTrue(parentExpected.containsAll(parentDeletes) && parentDeletes.containsAll(parentExpected));
        
        List<Vertex> childDeletes = engine.findDeletable(child);
        assertTrue(childExpected.containsAll(childDeletes) && childDeletes.containsAll(childExpected));
        
        List<Vertex> cousinDeletes = engine.findDeletable(cousin);
        assertTrue(cousinExpected.containsAll(cousinDeletes) && cousinDeletes.containsAll(cousinExpected));
    }
}
