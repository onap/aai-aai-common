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
import java.util.stream.Collectors;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.tinkerpop.gremlin.process.traversal.Step;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.step.filter.HasStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.HasContainer;
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

    public TraversalQuery(Loader loader, GraphTraversalSource source, GraphTraversal<Vertex, E> traversal) {
        super(loader, source, traversal);
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
        if(start > 0) {
            throw new IllegalArgumentException("A start index > 0 is currently not supported");
        }
        GraphTraversal<Vertex, E> clone = this.traversal.asAdmin().clone();
        GraphTraversal.Admin<Vertex, E> cloneAdmin = clone.asAdmin();

        List<Step> steps = cloneAdmin.getSteps();

        // TODO: Use containerAdjusted start index to support start > 0
        // TraversalQueryTest#removeQueryStepsBetweenTest27 can guide the implementation
        int containerAdjusted = start > 0
            ? getContainerAdjustedStart(cloneAdmin, steps, start)
            : start;
        for (int i = start; i < end - 1; i++) {
            Step step = steps.get(start);
            if (step instanceof HasStep) {
                List<HasContainer> hasContainers = ((HasStep) step).getHasContainers();
                int hasContainerSize = hasContainers.size();
                boolean isEndWithinHasContainer = isEndWithinHasContainer(end, i, hasContainers);
                if (isEndWithinHasContainer) {
                    int splitPosition = end - i - 1;
                    splitHasContainerAtPosition(cloneAdmin, hasContainers, start, splitPosition);
                    i += (hasContainerSize - splitPosition);
                } else {
                    cloneAdmin.removeStep(start);
                    i += (hasContainerSize - 1);
                }
            } else {
                cloneAdmin.removeStep(start);
            }
        }
        return new TraversalQuery<>(cloneAdmin, loader, source, this);
    }

    private boolean isEndWithinHasContainer(int end, int i, List<HasContainer> hasContainers) {
        return (i + hasContainers.size()) >= end - 1;
    }

    /**
     * Since the has-step inlining that was introduced with tinkerpop version 3.2.4,
     * a Step can be a HasContainer that can contain multiple steps.
     * The start index needs to be adjusted to account for this fact
     * @param cloneAdmin
     * @param steps
     * @param start
     * @return
     */
    private int getContainerAdjustedStart(GraphTraversal.Admin<Vertex, E> cloneAdmin, List<Step> steps, int start) {
        int adjustedIndex = start;
        for (int i = 0; i < start; i++) {
            Step step = steps.get(i);
            if (step instanceof HasStep) {
                if(isEndWithinHasContainer(adjustedIndex, i, ((HasStep) step).getHasContainers())){
                    adjustedIndex -= 1;
                }
                adjustedIndex -= ((HasStep) step).getHasContainers().size();
            }
        }
        return adjustedIndex;
    }

    /**
     * Split the hasContainer at the provided position and append everything
     * after it to the step Array
     * @param cloneAdmin
     * @param hasContainers
     * @param splitPosition
     */
    private void splitHasContainerAtPosition(GraphTraversal.Admin<Vertex, E> cloneAdmin,
            List<HasContainer> hasContainers, int start, int splitPosition) {
        List<HasContainer> newContainers = hasContainers.stream()
                .skip(splitPosition)
                .collect(Collectors.toList());
        int replaceIndex = start;
        cloneAdmin.removeStep(replaceIndex);
        for (HasContainer hasContainer : newContainers) {
            cloneAdmin.addStep(replaceIndex, new HasStep<>(cloneAdmin, hasContainer));
            replaceIndex++;
        }
    }

}
