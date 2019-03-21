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

import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.Tree;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

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
}
