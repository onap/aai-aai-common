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

package org.onap.aai.serialization.engines;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.strategy.verification.ReadOnlyStrategy;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.janusgraph.core.JanusGraph;
import org.onap.aai.dbmap.DBConnectionType;
import org.onap.aai.introspection.Loader;
import org.onap.aai.query.builder.*;
import org.onap.aai.serialization.db.InMemoryGraphSingleton;
import org.onap.aai.serialization.engines.query.GraphTraversalQueryEngine;
import org.onap.aai.serialization.engines.query.QueryEngine;

public class InMemoryDBEngine extends TransactionalGraphEngine {

    /**
     * Instantiates a new JanusGraph DB engine.
     *
     * @param style
     *        the style
     * @param loader
     *        the loader
     */
    private JanusGraph graph = null;

    private static final TransactionalGraphEngine.Admin admin = null;

    public InMemoryDBEngine(QueryStyle style, DBConnectionType connectionType, Loader loader, JanusGraph graph) {
        super(style, loader, connectionType, InMemoryGraphSingleton.getInstance(graph));
        this.graph = graph;
    }

    /**
     * Instantiates a new JanusGraph DB engine.
     *
     * @param style
     *        the style
     * @param loader
     *        the loader
     * @param connect
     *        the connect
     */
    public InMemoryDBEngine(QueryStyle style, Loader loader, boolean connect, JanusGraph graph) {
        super(style, loader);
        if (connect) {
            this.singleton = InMemoryGraphSingleton.getInstance(graph);
        }
        this.graph = graph;
    }

    @Override
    public QueryEngine getQueryEngine() {

        if (style.equals(QueryStyle.TRAVERSAL) || style.equals(QueryStyle.TRAVERSAL_URI)) {

            GraphTraversalSource traversalSource = graph.traversal();
            return new GraphTraversalQueryEngine(traversalSource);

        } else {
            throw new IllegalArgumentException("Query Engine type not recognized");
        }

    }

    @Override
    public QueryBuilder<Vertex> getQueryBuilder(QueryStyle style, Loader loader) {
        if (style.equals(QueryStyle.GREMLIN_TRAVERSAL)) {
            return new GremlinTraversal<>(loader, graph.traversal());
        } else if (style.equals(QueryStyle.TRAVERSAL)) {
            return new TraversalQuery<>(loader, graph.traversal());
        } else if (style.equals(QueryStyle.TRAVERSAL_URI)) {
            return new TraversalURIOptimizedQuery<>(loader, graph.traversal());
        } else {
            throw new IllegalArgumentException("Query Builder type is Not recognized");
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setListProperty(Vertex v, String name, List<?> objs) {

        // clear out list full replace style

        Iterator<VertexProperty<Object>> iterator = v.properties(name);
        while (iterator.hasNext()) {
            iterator.next().remove();
        }
        if (objs != null) {
            for (Object obj : objs) {
                v.property(name, obj);
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> getListProperty(Vertex v, String name) {

        List<Object> result = new ArrayList<>();

        Iterator<VertexProperty<Object>> iterator = v.properties(name);

        while (iterator.hasNext()) {
            result.add(iterator.next().value());
        }

        if (result.isEmpty()) {
            result = null;
        }

        return result;

    }

    @Override
    public QueryBuilder<Vertex> getQueryBuilder() {
        return getQueryBuilder(this.loader);
    }

    @Override
    public QueryBuilder<Vertex> getQueryBuilder(Loader loader) {
        if (style.equals(QueryStyle.GREMLIN_TRAVERSAL)) {
            return new GremlinTraversal<>(loader, this.asAdmin().getTraversalSource());
        } else if (style.equals(QueryStyle.GREMLIN_UNIQUE)) {
            return new GremlinUnique<>(loader, this.asAdmin().getTraversalSource());
        } else if (style.equals(QueryStyle.TRAVERSAL)) {
            return new TraversalQuery<>(loader, graph.traversal());
        } else if (style.equals(QueryStyle.TRAVERSAL_URI)) {
            return new TraversalURIOptimizedQuery<>(loader, graph.traversal());
        } else {
            throw new IllegalArgumentException("Query Builder type not recognized");
        }

    }

    @Override
    public QueryBuilder<Vertex> getQueryBuilder(Vertex start) {
        return getQueryBuilder(this.loader, start);
    }

    public GraphTraversalSource getTraversalSource() {
        return graph.traversal();
    }

    @Override
    public QueryBuilder<Vertex> getQueryBuilder(Loader loader, Vertex start) {
        if (style.equals(QueryStyle.GREMLIN_TRAVERSAL)) {
            return new GremlinTraversal<>(loader, graph.traversal(), start);
        } else if (style.equals(QueryStyle.GREMLIN_UNIQUE)) {
            return new GremlinUnique<>(loader, this.asAdmin().getTraversalSource(), start);
        } else if (style.equals(QueryStyle.TRAVERSAL)) {
            return new TraversalQuery<>(loader, graph.traversal(), start);
        } else if (style.equals(QueryStyle.TRAVERSAL_URI)) {
            return new TraversalURIOptimizedQuery<>(loader, graph.traversal(), start);
        } else {
            throw new IllegalArgumentException("Query Builder type not recognized");
        }

    }

    @Override
    public Graph startTransaction() {
        if (this.tx() == null) {
            this.currentTx = graph.newTransaction();
            this.currentTraversal = this.tx().traversal();
            this.readOnlyTraversal =
                    this.tx().traversal(GraphTraversalSource.build().with(ReadOnlyStrategy.instance()));
        }
        return currentTx;
    }

}
