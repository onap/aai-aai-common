/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright © 2024 Deutsche Telekom.
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

package org.onap.aai.query.builder;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.Step;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.Tree;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.LoaderUtil;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;

@Tag("TinkerpopUpgrade")
public class TraversalURIOptimizedQueryTest extends TraversalQueryTest {

    @Override
    protected QueryBuilder<Edge> getNewEdgeTraversalWithTestEdgeRules(Vertex v) {
        return new TraversalURIOptimizedQuery<>(loader, g, v);
    }

    @Override
    protected QueryBuilder<Edge> getNewEdgeTraversalWithTestEdgeRules() {
        return new TraversalURIOptimizedQuery<>(loader, g);
    }

    @Override
    protected QueryBuilder<Vertex> getNewVertexTraversalWithTestEdgeRules(Vertex v) {
        return new TraversalURIOptimizedQuery<>(loader, g, v);
    }

    @Override
    protected QueryBuilder<Vertex> getNewVertexTraversalWithTestEdgeRules() {
        return new TraversalURIOptimizedQuery<>(loader, g);
    }

    @Override
    protected QueryBuilder<Vertex> getNewVertexTraversal() {
        return new TraversalURIOptimizedQuery<>(loader, g);
    }

    @Override
    protected QueryBuilder<Tree> getNewTreeTraversalWithTestEdgeRules(Vertex v) {
        return new TraversalURIOptimizedQuery<>(loader, g, v);
    }

    @Override
    protected QueryBuilder<Tree> getNewTreeTraversalWithTestEdgeRules() {
        return new TraversalURIOptimizedQuery<>(loader, g);
    }

    @Override
    protected QueryBuilder<Path> getNewPathTraversalWithTestEdgeRules(Vertex v) {
        return new TraversalURIOptimizedQuery<>(loader, g, v);
    }

    @Override
    protected QueryBuilder<Path> getNewPathTraversalWithTestEdgeRules() {
        return new TraversalURIOptimizedQuery<>(loader, g);
    }

    @Test
    public void thatTraversalIsPivotedWithinHasContainer() throws AAIUnknownObjectException {
        JanusGraph graph = JanusGraphFactory.build().set("storage.backend", "inmemory").open();
        GraphTraversalSource source = graph.newTransaction().traversal();
        final Loader loader = LoaderUtil.getLatestVersion();
        GraphTraversal<Vertex, Vertex> traversal = (GraphTraversal<Vertex, Vertex>) __.<Vertex>start();
        GraphTraversalBuilder graphTraversalBuilder = new TraversalURIOptimizedQuery<>(loader, source);
        Map<Integer, String> stepToAaiUri = Collections.singletonMap(6, "/smth");
        TraversalURIOptimizedQuery traversalQuery = new TraversalURIOptimizedQuery<>(traversal, loader, source,
                graphTraversalBuilder, stepToAaiUri);
        traversalQuery.limit(1);
        traversalQuery.has("propertyKey", "value");
        traversalQuery.has("propertyKey2", "value2");
        traversalQuery.limit(2);
        traversalQuery.has("propertyKey3", "value3");
        traversalQuery.has("propertyKey4", "value4");
        traversalQuery.has("propertyKey5", "value5");
        traversalQuery.limit(3);
        traversalQuery.limit(4);

        traversalQuery.executeQuery();
        String query = traversalQuery.completeTraversal.getSteps().toString();
        assertEquals(
                "[GraphStep(vertex,[]), HasStep([aai-uri.eq(/smth)]), HasStep([propertyKey5.eq(value5)]), RangeGlobalStep(0,3), RangeGlobalStep(0,4)]",
                query);
    }

    @Test
    public void thatTraversalIsPivottedAtRegularStep() throws AAIUnknownObjectException {
        JanusGraph graph = JanusGraphFactory.build().set("storage.backend", "inmemory").open();
        GraphTraversalSource source = graph.newTransaction().traversal();
        final Loader loader = LoaderUtil.getLatestVersion();
        GraphTraversal<Vertex, Vertex> traversal = (GraphTraversal<Vertex, Vertex>) __.<Vertex>start();
        GraphTraversalBuilder graphTraversalBuilder = new TraversalURIOptimizedQuery<>(loader, source);
        Map<Integer, String> stepToAaiUri = Collections.singletonMap(3, "/smth");
        TraversalURIOptimizedQuery traversalQuery = new TraversalURIOptimizedQuery<>(traversal, loader, source,
                graphTraversalBuilder, stepToAaiUri);
        traversalQuery.limit(1);
        traversalQuery.has("propertyKey", "value");
        traversalQuery.has("propertyKey2", "value2");
        traversalQuery.limit(2);
        traversalQuery.has("propertyKey3", "value3");
        traversalQuery.has("propertyKey4", "value4");
        traversalQuery.has("propertyKey5", "value5");
        traversalQuery.limit(3);
        traversalQuery.limit(4);

        GraphTraversal<Vertex, Vertex> expected = __.<Vertex>start()
                .has(AAIProperties.AAI_URI, "/smth")
                .limit(2)
                .has("propertyKey3", "value3")
                .has("propertyKey4", "value4")
                .has("propertyKey5", "value5")
                .limit(3)
                .limit(4);
        List<Step> expectedSteps = expected.asAdmin().getSteps();
        traversalQuery.executeQuery();
        List<Step> rawActual = traversalQuery.completeTraversal.getSteps();
        List<Step> actualStep = rawActual.subList(1, rawActual.size()); // remove GraphStep since I found no way to add
                                                                        // it to the expected traversal
        assertArrayEquals(expectedSteps.toArray(), actualStep.toArray());
    }
}
