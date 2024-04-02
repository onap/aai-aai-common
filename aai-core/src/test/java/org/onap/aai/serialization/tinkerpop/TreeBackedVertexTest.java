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

package org.onap.aai.serialization.tinkerpop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.Tree;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.onap.aai.edges.enums.EdgeProperty;
import org.onap.aai.serialization.engines.query.GraphTraversalQueryEngine;

@Disabled
public class TreeBackedVertexTest {

    private Graph graph = TinkerGraph.open();
    private Object startKey = null;
    private Tree<Element> tree = null;
    private Tree<Element> treeDepth1 = null;
    private Tree<Element> treeDepth0NodeOnly = null;

    @BeforeEach
    public void configure() {
        GraphTraversalSource g = graph.traversal();

        startKey = g.addV("vserver").as("v1").property("test", "hello").addV("vserver").as("v2").addV("interface")
                .property("name", "interface 1").as("v10").addE("hasChild").from("v2")
                .property(EdgeProperty.CONTAINS.toString(), true).addV("pserver").property("name", "pserver 1").as("v4")
                .addE("runsOn").to("v1").property(EdgeProperty.CONTAINS.toString(), false).addV("interface")
                .property("name", "interface 2").as("v3").addE("hasChild").from("v1")
                .property(EdgeProperty.CONTAINS.toString(), true).addV("address").property("name", "address 1")
                .addE("hasChild").from("v3").property(EdgeProperty.CONTAINS.toString(), true).addV("address")
                .property("name", "address 2").addE("hasChild").from("v3")
                .property(EdgeProperty.CONTAINS.toString(), true).addV("complex").property("name", "complex 1")
                .addE("locatedIn").from("v4").property(EdgeProperty.CONTAINS.toString(), false).addV("interface")
                .property("name", "interface 3").addE("hasChild").from("v4")
                .property(EdgeProperty.CONTAINS.toString(), true).addV("subnet").property("name", "subnet 1").as("v5")
                .addE("in").from("v3").property(EdgeProperty.CONTAINS.toString(), false).addV("address")
                .property("name", "address 3").as("v6").addE("hasChild").from("v5")
                .property(EdgeProperty.CONTAINS.toString(), true).select("v1").next();

        tree = new GraphTraversalQueryEngine(g).findSubGraph((Vertex) startKey);
        treeDepth1 = new GraphTraversalQueryEngine(g).findSubGraph((Vertex) startKey, 1, false);
        treeDepth0NodeOnly = new GraphTraversalQueryEngine(g).findSubGraph((Vertex) startKey, 0, true);
    }

    @Disabled
    @Test
    public void oneHopViaEdges() {

        // BulkSet set = (BulkSet)result;
        TreeBackedVertex v = new TreeBackedVertex((Vertex) tree.getObjectsAtDepth(1).iterator().next(), tree);

        assertEquals(v.edges(Direction.OUT).next().inVertex().property("name").orElse(""),
                "interface 2",
                "locate child");
        assertEquals(v.edges(Direction.IN).next().outVertex().property("name").orElse(""),
                "pserver 1",
                "locate cousin");

    }

    @Disabled
    @Test
    public void oneHopViaVertices() {

        // BulkSet set = (BulkSet)result;
        TreeBackedVertex v = new TreeBackedVertex((Vertex) tree.getObjectsAtDepth(1).iterator().next(), tree);

        assertEquals("interface 2", v.vertices(Direction.OUT).next().property("name").orElse(""), "locate child");
        assertEquals("pserver 1", v.vertices(Direction.IN).next().property("name").orElse(""), "locate cousin");

    }

    @Disabled
    @Test
    public void twoHopCousinViaVertices() {

        // BulkSet set = (BulkSet)result;
        TreeBackedVertex v = new TreeBackedVertex((Vertex) tree.getObjectsAtDepth(1).iterator().next(), tree);

        assertEquals("subnet 1",
                v.vertices(Direction.OUT).next().vertices(Direction.OUT, "in").next().property("name").orElse(""),
                "locate child");

    }

    @Test
    public void walkVerticesRestrictedDepth() {

        // BulkSet set = (BulkSet)result;
        TreeBackedVertex v =
                new TreeBackedVertex((Vertex) treeDepth1.getObjectsAtDepth(1).iterator().next(), treeDepth1);

        assertEquals(false,
                v.vertices(Direction.OUT).next().vertices(Direction.OUT, "hasChild").hasNext(),
                "nothing returned");

    }

    @Test
    public void walkVertices() {
        TreeBackedVertex v = new TreeBackedVertex((Vertex) tree.getObjectsAtDepth(1).iterator().next(), tree);
        assertEquals("address 2", v.vertices(Direction.OUT).next().vertices(Direction.OUT, "hasChild")
                .next().property("name").orElse(""), "locate child");
    }

    @Test
    public void walkEdges() {
        TreeBackedVertex v = new TreeBackedVertex((Vertex) tree.getObjectsAtDepth(1).iterator().next(), tree);

        assertEquals("address 2", v.edges(Direction.OUT).next().inVertex()
                .edges(Direction.OUT, "hasChild").next().inVertex().property("name").orElse(""), "locate child");
    }

    @Test
    public void noEdgesFoudWithLabelVertices() {
        TreeBackedVertex v = new TreeBackedVertex((Vertex) tree.getObjectsAtDepth(1).iterator().next(), tree);

        assertEquals(false, v.vertices(Direction.OUT, "hello").hasNext(), "missing hello label");
    }

    @Test
    public void noEdgesFoudWithLabelEdges() {
        TreeBackedVertex v = new TreeBackedVertex((Vertex) tree.getObjectsAtDepth(1).iterator().next(), tree);

        assertEquals(false, v.edges(Direction.OUT, "hello").hasNext(), "missing hello label");
    }

    @Test
    public void depthZeroNodeOnly() {
        TreeBackedVertex v = new TreeBackedVertex((Vertex) treeDepth0NodeOnly.getObjectsAtDepth(1).iterator().next(),
                treeDepth0NodeOnly);
        assertEquals(false, v.edges(Direction.BOTH).hasNext(), "no edges returned");
    }

}
