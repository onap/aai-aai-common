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

package org.onap.aai.query.builder;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.parsers.query.TraversalStrategy;

import javax.ws.rs.core.MultivaluedMap;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class GremlinTraversal.
 */
public class HistoryGremlinTraversal<E> extends GremlinTraversal<E> {

    /**
     * Instantiates a new gremlin traversal.
     *
     * @param loader the loader
     */
    public HistoryGremlinTraversal(Loader loader, GraphTraversalSource source) {
        super(loader, source);
    }

    /**
     * Instantiates a new gremlin traversal.
     *
     * @param loader the loader
     * @param start the start
     */
    public HistoryGremlinTraversal(Loader loader, GraphTraversalSource source, Vertex start) {
        super(loader, source, start);
    }


    protected HistoryGremlinTraversal(List<String> traversal, Loader loader, GraphTraversalSource source,
                                      GremlinQueryBuilder<E> gtb) {
        super(traversal, loader, source, gtb);
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryParser createQueryFromURI(URI uri) throws UnsupportedEncodingException, AAIException {
        return factory.buildURIParser(uri);
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryParser createQueryFromRelationship(Introspector relationship)
            throws UnsupportedEncodingException, AAIException {
        return factory.buildRelationshipParser(relationship);
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryParser createQueryFromURI(URI uri, MultivaluedMap<String, String> queryParams)
            throws UnsupportedEncodingException, AAIException {
        return factory.buildURIParser(uri, queryParams);
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryParser createQueryFromObjectName(String objName) {
        return factory.buildObjectNameParser(objName);
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<E> newInstance(Vertex start) {
        return new HistoryGremlinTraversal<>(loader, source, start);
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<E> newInstance() {
        return new HistoryGremlinTraversal<>(loader, source);
    }

    @Override
    protected QueryBuilder<E> cloneQueryAtStep(int index) {

        int idx = index;

        if (idx == 0) {
            idx = stepIndex;
        }

        List<String> newList = new ArrayList<>();
        for (int i = 0; i < idx; i++) {
            newList.add(this.list.get(i));
        }

        return new HistoryGremlinTraversal<>(newList, loader, source, this);
    }

    @Override
    protected void vertexHas(String key, Object value) {
        super.vertexHas(key, value);
        touchHistoryProperties(key, value);
    }

    @Override
    protected void vertexHasNot(String key) {
        super.vertexHasNot(key);
        touchHistoryProperties(key);

    }

    @Override
    protected void vertexHas(String key) {
        super.vertexHas(key);
        touchHistoryProperties(key);
    }

    /*
     * This is required for the subgraphstrategies to work
     */
    private void touchHistoryProperties(String key){
        if(key != null && !key.isEmpty() && !key.equals(AAIProperties.NODE_TYPE)) {
            list.add(".where(__.properties('" + key + "'))");
        }

    }

    private void touchHistoryProperties(String key, Object value) {
        if(key != null && !key.isEmpty() && !key.equals(AAIProperties.NODE_TYPE)) {
            list.add(".where(__.properties('" + key + "').hasValue(" + value + "))");
        }
    }
}
