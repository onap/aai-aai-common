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

package org.onap.aai.serialization.tinkerpop;

import static org.junit.Assert.assertEquals;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.Tree;
import org.apache.tinkerpop.gremlin.structure.*;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.onap.aai.edges.enums.EdgeProperty;
import org.onap.aai.serialization.engines.query.GraphTraversalQueryEngine;

@Ignore
public class TreeBackedVertexTest {

    private Graph graph = TinkerGraph.open();
    private Object startKey = null;
    private Tree<Element> tree = null;
    private Tree<Element> treeDepth1 = null;
    private Tree<Element> treeDepth0NodeOnly = null;

    @Before
    public void configure() {
        GraphTraversalSource g = graph.traversal();

        startKey =
            g.addV(T.label, "vserver").as("v1").property("test", "hello").addV(T.label, "vserver")
                .as("v2").addV(T.label, "interface").property("name", "interface 1").as("v10")
                .addInE("hasChild", "v2").property(EdgeProperty.CONTAINS.toString(), true)
                .addV(T.label, "pserver").property("name", "pserver 1").as("v4")
                .addOutE("runsOn", "v1").property(EdgeProperty.CONTAINS.toString(), false)
                .addV(T.label, "interface").property("name", "interface 2").as("v3")
                .addInE("hasChild", "v1").property(EdgeProperty.CONTAINS.toString(), true)
                .addV(T.label, "address").property("name", "address 1").addInE("hasChild", "v3")
                .property(EdgeProperty.CONTAINS.toString(), true).addV(T.label, "address")
                .property("name", "address 2").addInE("hasChild", "v3")
                .property(EdgeProperty.CONTAINS.toString(), true).addV(T.label, "complex")
                .property("name", "complex 1").addInE("locatedIn", "v4")
                .property(EdgeProperty.CONTAINS.toString(), false).addV(T.label, "interface")
                .property("name", "interface 3").addInE("hasChild", "v4")
                .property(EdgeProperty.CONTAINS.toString(), true).addV(T.label, "subnet")
                .property("name", "subnet 1").as("v5").addInE("in", "v3")
                .property(EdgeProperty.CONTAINS.toString(), false).addV(T.label, "address")
                .property("name", "address 3").as("v6").addInE("hasChild", "v5")
                .property(EdgeProperty.CONTAINS.toString(), true).select("v1").next();

        tree = new GraphTraversalQueryEngine(g).findSubGraph((Vertex) startKey);
        treeDepth1 = new GraphTraversalQueryEngine(g).findSubGraph((Vertex) startKey, 1, false);
        treeDepth0NodeOnly =
            new GraphTraversalQueryEngine(g).findSubGraph((Vertex) startKey, 0, true);
    }

    @Ignore
    @Test
    public void oneHopViaEdges() {

        // BulkSet set = (BulkSet)result;
        TreeBackedVertex v =
            new TreeBackedVertex((Vertex) tree.getObjectsAtDepth(1).iterator().next(), tree);

        assertEquals("locate child",
            v.edges(Direction.OUT).next().inVertex().property("name").orElse(""), "interface 2");
        assertEquals("locate cousin",
            v.edges(Direction.IN).next().outVertex().property("name").orElse(""), "pserver 1");

    }

    @Ignore
    @Test
    public void oneHopViaVertices() {

        // BulkSet set = (BulkSet)result;
        TreeBackedVertex v =
            new TreeBackedVertex((Vertex) tree.getObjectsAtDepth(1).iterator().next(), tree);

        assertEquals("locate child", "interface 2",
            v.vertices(Direction.OUT).next().property("name").orElse(""));
        assertEquals("locate cousin", "pserver 1",
            v.vertices(Direction.IN).next().property("name").orElse(""));

    }

    @Ignore
    @Test
    public void twoHopCousinViaVertices() {

        // BulkSet set = (BulkSet)result;
        TreeBackedVertex v =
            new TreeBackedVertex((Vertex) tree.getObjectsAtDepth(1).iterator().next(), tree);

        assertEquals("locate child", "subnet 1", v.vertices(Direction.OUT).next()
            .vertices(Direction.OUT, "in").next().property("name").orElse(""));

    }

    @Test
    public void walkVerticesRestrictedDepth() {

        // BulkSet set = (BulkSet)result;
        TreeBackedVertex v = new TreeBackedVertex(
            (Vertex) treeDepth1.getObjectsAtDepth(1).iterator().next(), treeDepth1);

        assertEquals("nothing returned", false,
            v.vertices(Direction.OUT).next().vertices(Direction.OUT, "hasChild").hasNext());

    }

    @Test
    public void walkVertices() {
        TreeBackedVertex v =
            new TreeBackedVertex((Vertex) tree.getObjectsAtDepth(1).iterator().next(), tree);
        assertEquals("locate child", "address 2", v.vertices(Direction.OUT).next()
            .vertices(Direction.OUT, "hasChild").next().property("name").orElse(""));
    }

    @Test
    public void walkEdges() {
        TreeBackedVertex v =
            new TreeBackedVertex((Vertex) tree.getObjectsAtDepth(1).iterator().next(), tree);

        assertEquals("locate child", "address 2", v.edges(Direction.OUT).next().inVertex()
            .edges(Direction.OUT, "hasChild").next().inVertex().property("name").orElse(""));
    }

    @Test
    public void noEdgesFoudWithLabelVertices() {
        TreeBackedVertex v =
            new TreeBackedVertex((Vertex) tree.getObjectsAtDepth(1).iterator().next(), tree);

        assertEquals("missing hello label", false, v.vertices(Direction.OUT, "hello").hasNext());
    }

    @Test
    public void noEdgesFoudWithLabelEdges() {
        TreeBackedVertex v =
            new TreeBackedVertex((Vertex) tree.getObjectsAtDepth(1).iterator().next(), tree);

        assertEquals("missing hello label", false, v.edges(Direction.OUT, "hello").hasNext());
    }

    @Test
    public void depthZeroNodeOnly() {
        TreeBackedVertex v = new TreeBackedVertex(
            (Vertex) treeDepth0NodeOnly.getObjectsAtDepth(1).iterator().next(), treeDepth0NodeOnly);
        assertEquals("no edges returned", false, v.edges(Direction.BOTH).hasNext());
    }

}
