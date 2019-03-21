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

package org.onap.aai.query.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.Tree;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Ignore;
import org.junit.Test;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.edges.enums.EdgeType;
import org.onap.aai.exceptions.AAIException;

public class TraversalQueryTest extends QueryBuilderTestAbstraction {

    @Override
    protected QueryBuilder<Edge> getNewEdgeTraversalWithTestEdgeRules(Vertex v) {
        return new TraversalQuery<>(loader, g, v);
    }

    @Override
    protected QueryBuilder<Edge> getNewEdgeTraversalWithTestEdgeRules() {
        return new TraversalQuery<>(loader, g);
    }

    @Override
    protected QueryBuilder<Vertex> getNewVertexTraversalWithTestEdgeRules(Vertex v) {
        return new TraversalQuery<>(loader, g, v);
    }

    @Override
    protected QueryBuilder<Vertex> getNewVertexTraversalWithTestEdgeRules() {
        return new TraversalQuery<>(loader, g);
    }

    protected QueryBuilder<Vertex> getNewVertexTraversal() {
        return new TraversalQuery<>(loader, g);
    }

    @Override
    protected QueryBuilder<Tree> getNewTreeTraversalWithTestEdgeRules(Vertex v) {
        return new TraversalQuery<>(loader, g, v);
    }

    @Override
    protected QueryBuilder<Tree> getNewTreeTraversalWithTestEdgeRules() {
        return new TraversalQuery<>(loader, g);
    }

    @Override
    protected QueryBuilder<Path> getNewPathTraversalWithTestEdgeRules(Vertex v) {
        return new TraversalQuery<>(loader, g, v);
    }

    @Override
    protected QueryBuilder<Path> getNewPathTraversalWithTestEdgeRules() {
        return new TraversalQuery<>(loader, g);
    }

    @Test
    public void unionQuery() {
        QueryBuilder<Vertex> tQ = getNewVertexTraversal();
        QueryBuilder<Vertex> tQ2 = getNewVertexTraversal();
        QueryBuilder<Vertex> tQ3 = getNewVertexTraversal();
        tQ.union(tQ2.getVerticesByProperty("test1", "value1"),
            tQ3.getVerticesByIndexedProperty("test2", "value2"));

        GraphTraversal<Vertex, Vertex> expected =
            __.<Vertex>start().union(__.has("test1", "value1"), __.has("test2", "value2"));

        assertEquals("they are equal", expected, tQ.getQuery());

    }

    @Test
    public void traversalClones()
        throws UnsupportedEncodingException, AAIException, URISyntaxException {
        QueryBuilder<Vertex> tQ = getNewVertexTraversal();
        QueryBuilder<Vertex> builder = tQ
            .createQueryFromURI(new URI("network/generic-vnfs/generic-vnf/key1")).getQueryBuilder();
        GraphTraversal<Vertex, Vertex> expected =
            __.<Vertex>start().has("vnf-id", "key1").has("aai-node-type", "generic-vnf");
        GraphTraversal<Vertex, Vertex> containerExpected =
            __.<Vertex>start().has("aai-node-type", "generic-vnf");

        assertEquals("query object", expected.toString(), builder.getQuery().toString());
        assertEquals("container query object", containerExpected.toString(),
            builder.getContainerQuery().getQuery().toString());

    }

    // TODO - Identify why this unit test is failing and if this
    // is going to cause any problems
    @Test
    @Ignore("Not working ever since the change to using model driven development")
    public void nestedTraversalClones()
        throws UnsupportedEncodingException, AAIException, URISyntaxException {

        QueryBuilder<Vertex> tQ = getNewVertexTraversal();
        QueryBuilder<Vertex> builder = tQ
            .createQueryFromURI(
                new URI("network/generic-vnfs/generic-vnf/key1/l-interfaces/l-interface/key2"))
            .getQueryBuilder();
        GraphTraversal<Vertex, Vertex> expected = __.<Vertex>start().has("vnf-id", "key1")
            .has("aai-node-type", "generic-vnf").in("org.onap.relationships.inventory.BelongsTo")
            .has(AAIProperties.NODE_TYPE, "l-interface").has("interface-name", "key2");
        GraphTraversal<Vertex, Vertex> containerExpected = __.<Vertex>start().has("vnf-id", "key1")
            .has("aai-node-type", "generic-vnf").in("org.onap.relationships.inventory.BelongsTo")
            .has(AAIProperties.NODE_TYPE, "l-interface");

        assertEquals("query object", expected.toString(), builder.getQuery().toString());
        assertEquals("container query object", containerExpected.toString(),
            builder.getContainerQuery().getQuery().toString());

    }

    @Test
    public void abstractEdgeToVertexTraversalTest() throws AAIException {

        Vertex gvnf = g.addV("aai-node-type", "generic-vnf", "vnf-id", "gvnf").next();
        Vertex vnfc1 = g.addV("aai-node-type", "vnfc", "vnfc-name", "a-name").next();

        testEdgeSer.addEdge(g, gvnf, vnfc1);

        QueryBuilder<Vertex> tQ = getNewVertexTraversalWithTestEdgeRules(gvnf);
        tQ.createEdgeTraversal(EdgeType.COUSIN, "vnf", "vnfc");

        List<Vertex> list = tQ.toList();

        assertEquals("Has 1 vertexes ", 1, list.size());
        assertTrue("Has vertex on the default edge ", list.contains(vnfc1));

    }

    @Test
    public void abstractEdgeToVertexTraversalSingleOutRuleTest() throws AAIException {

        Vertex vce = g.addV("aai-node-type", "vce", "vnf-id", "vce").next();
        Vertex vnfc1 = g.addV("aai-node-type", "vnfc", "vnfc-name", "a-name").next();

        testEdgeSer.addEdge(g, vce, vnfc1);

        QueryBuilder<Vertex> tQ1 = getNewVertexTraversalWithTestEdgeRules(vce);
        tQ1.createEdgeTraversal(EdgeType.COUSIN, "vnf", "vnfc");

        QueryBuilder<Vertex> tQ2 = getNewVertexTraversalWithTestEdgeRules(vnfc1);
        tQ2.createEdgeTraversal(EdgeType.COUSIN, "vnfc", "vnf");

        List<Vertex> list1 = tQ1.toList();
        List<Vertex> list2 = tQ2.toList();

        assertEquals("1 - Has 1 vertexes ", 1, list1.size());
        assertTrue("1 - traversal results in vnfc ", list1.contains(vnfc1));
        assertEquals("2 - Has 1 vertexes ", 1, list2.size());
        assertTrue("2 - traversal results in vce ", list2.contains(vce));

    }

    @Test
    public void abstractEdgeToVertexTraversalSingleInRuleTest() throws AAIException {

        Vertex vce = g.addV("aai-node-type", "vce", "vnf-id", "vce").next();
        Vertex pserver = g.addV("aai-node-type", "pserver", "hostname", "a-name").next();

        testEdgeSer.addEdge(g, vce, pserver);

        QueryBuilder<Vertex> tQ1 = getNewVertexTraversalWithTestEdgeRules(vce);
        tQ1.createEdgeTraversal(EdgeType.COUSIN, "vnf", "pserver");

        List<Vertex> list = tQ1.toList();

        assertEquals("1 - Has 1 vertexes ", 1, list.size());
        assertTrue("1 - traversal results in vnfc ", list.contains(pserver));

    }

    @Test
    public void abstractEdgeToVertexMultiRuleTraversalTest() throws AAIException {

        Vertex gvnf = g.addV("aai-node-type", "generic-vnf", "vnf-id", "gvnf").next();
        Vertex vnfc1 = g.addV("aai-node-type", "vnfc", "vnfc-name", "a-name").next();
        Vertex vnfc2 = g.addV("aai-node-type", "vnfc", "vnfc-name", "b-name").next();

        testEdgeSer.addEdge(g, gvnf, vnfc1);
        testEdgeSer.addEdge(g, gvnf, vnfc2, "re-uses");

        QueryBuilder<Vertex> tQ = getNewVertexTraversalWithTestEdgeRules(gvnf);
        tQ.createEdgeTraversal(EdgeType.COUSIN, "vnf", "vnfc");

        List<Vertex> list = tQ.toList();

        assertEquals("Has 2 vertexes ", 2, list.size());
        assertTrue("Has vertex on the default edge ", list.contains(vnfc1));
        assertTrue("Has vertex on the re-uses edge ", list.contains(vnfc2));

    }

    @Test
    public void abstractEdgeToVertexMultiRuleOutTraversalTest() throws AAIException {

        Vertex gvnf = g.addV("aai-node-type", "generic-vnf", "vnf-id", "gvnf").next();
        Vertex pserver = g.addV("aai-node-type", "pserver", "hostname", "a-name").next();

        testEdgeSer.addEdge(g, gvnf, pserver);
        testEdgeSer.addEdge(g, gvnf, pserver, "generic-vnf-pserver-B");

        QueryBuilder<Vertex> tQ = getNewVertexTraversalWithTestEdgeRules(gvnf);
        tQ.createEdgeTraversal(EdgeType.COUSIN, "vnf", "pserver");

        List<Vertex> list = tQ.toList();

        assertEquals("Has 2 vertexes ", 2, list.size());
        assertTrue("result has pserver ", list.contains(pserver));

    }

    @Test
    public void abstractEdgeToVertexMultiRuleInTraversalTest() throws AAIException {

        Vertex gvnf = g.addV("aai-node-type", "generic-vnf", "vnf-id", "gvnf").next();
        Vertex complex =
            g.addV("aai-node-type", "complex", "physical-location-id", "a-name").next();

        testEdgeSer.addEdge(g, gvnf, complex);
        testEdgeSer.addEdge(g, gvnf, complex, "complex-generic-vnf-B");

        QueryBuilder<Vertex> tQ = getNewVertexTraversalWithTestEdgeRules(gvnf);
        tQ.createEdgeTraversal(EdgeType.COUSIN, "vnf", "complex");

        List<Vertex> list = tQ.toList();

        assertEquals("Has 2 vertexes ", 2, list.size());
        assertTrue("result has pserver ", list.contains(complex));

    }

}
