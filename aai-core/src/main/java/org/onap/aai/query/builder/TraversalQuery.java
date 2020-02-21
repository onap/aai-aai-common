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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.tinkerpop.gremlin.process.traversal.Step;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.parsers.query.TraversalStrategy;

/**
 * The Class TraversalQuery.
 */
public class TraversalQuery<E> extends GraphTraversalBuilder<E> {

    /**
     * Instantiates a new traversal query.
     *
     * @param loader the loader
     */

    public TraversalQuery(Loader loader, GraphTraversalSource source) {
        super(loader, source);
        this.factory = new TraversalStrategy(this.loader, this);
    }

    /**
     * Instantiates a new traversal query.
     *
     * @param loader the loader
     * @param start the start
     */
    public TraversalQuery(Loader loader, GraphTraversalSource source, Vertex start) {
        super(loader, source, start);
        this.factory = new TraversalStrategy(this.loader, this);
    }

    protected TraversalQuery(GraphTraversal<Vertex, E> traversal, Loader loader, GraphTraversalSource source,
            GraphTraversalBuilder<E> gtb) {
        super(loader, source);
        this.traversal = traversal;
        this.stepIndex = gtb.getStepIndex();
        this.parentStepIndex = gtb.getParentStepIndex();
        this.containerStepIndex = gtb.getContainerStepIndex();
        this.factory = new TraversalStrategy(this.loader, this);
        this.start = gtb.getStart();
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
        return new TraversalQuery<>(loader, source, start);
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<E> newInstance() {
        return new TraversalQuery<>(loader, source);
    }

    @Override
    public QueryBuilder<E> fold() {
        return this;
    }

    @Override
    public QueryBuilder<E> id() {
        return this;
    }

    @Override
    protected QueryBuilder<E> cloneQueryAtStep(int index) {
        GraphTraversal.Admin<Vertex, E> cloneAdmin = getCloneAdmin(index);
        return new TraversalQuery<>(cloneAdmin, loader, source, this);
    }

    protected GraphTraversal.Admin<Vertex, E> getCloneAdmin(int index) {
        int idx = index;

        if (idx == 0) {
            idx = stepIndex;
        }

        GraphTraversal<Vertex, E> clone = this.traversal.asAdmin().clone();
        GraphTraversal.Admin<Vertex, E> cloneAdmin = clone.asAdmin();
        List<Step> steps = cloneAdmin.getSteps();

        for (int i = steps.size() - 1; i >= idx; i--) {
            cloneAdmin.removeStep(i);
        }
        return cloneAdmin;
    }

    @Override
    protected QueryBuilder<E> removeQueryStepsBetween(int start, int end) {
        GraphTraversal<Vertex, E> clone = this.traversal.asAdmin().clone();
        GraphTraversal.Admin<Vertex, E> cloneAdmin = clone.asAdmin();

        for (int i = end - 2; i >= start; i--) {
            cloneAdmin.removeStep(i);
        }
        return new TraversalQuery<>(cloneAdmin, loader, source, this);
    }
}
