/**
* ============LICENSE_START=======================================================
* org.onap.aai
* ================================================================================
* Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
* ================================================================================
*  * Modifications Copyright © 2024 DEUTSCHE TELEKOM AG.
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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.apache.tinkerpop.gremlin.process.traversal.translator.GroovyTranslator;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.Pop;
import org.apache.tinkerpop.gremlin.process.traversal.Scope;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal.Admin;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.Tree;
import org.apache.tinkerpop.gremlin.process.traversal.util.TraversalHelper;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.edges.EdgeRule;
import org.onap.aai.edges.EdgeRuleQuery;
import org.onap.aai.edges.enums.EdgeType;
import org.onap.aai.edges.exceptions.EdgeRuleNotFoundException;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.query.entities.PaginationResult;
import org.onap.aai.schema.enums.ObjectMetadata;
import org.onap.aai.schema.enums.PropertyMetadata;
import org.onap.aai.serialization.db.exceptions.NoEdgeRuleFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class GraphTraversalBuilder.
 */
public abstract class GraphTraversalBuilder<E> extends QueryBuilder<E> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphTraversalBuilder.class);

    private final GroovyTranslator groovyTranslator = GroovyTranslator.of("source");

    protected GraphTraversal<Vertex, E> traversal = null;
    protected Admin<Vertex, E> completeTraversal = null;

    protected QueryBuilder<E> containerQuery;
    protected QueryBuilder<E> parentQuery;

    /**
     * Instantiates a new graph traversal builder.
     *
     * @param loader the loader
     */
    public GraphTraversalBuilder(Loader loader, GraphTraversalSource source) {
        super(loader, source);
        traversal = (GraphTraversal<Vertex, E>) __.<E>start();

    }

    public GraphTraversalBuilder(Loader loader, GraphTraversalSource source, GraphTraversal<Vertex, E> traversal) {
        super(loader, source);
        this.traversal = traversal;

    }

    /**
     * Instantiates a new graph traversal builder.
     *
     * @param loader the loader
     * @param start the start
     */
    public GraphTraversalBuilder(Loader loader, GraphTraversalSource source, Vertex start) {
        super(loader, source, start);

        traversal = (GraphTraversal<Vertex, E>) __.__(start);

    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<Vertex> getVerticesByProperty(String key, Object value) {

        // correct value call because the index is registered as an Integer
        this.vertexHas(key, this.correctObjectType(value));
        stepIndex++;
        return (QueryBuilder<Vertex>) this;
    }

    @Override
    protected void vertexHas(String key, Object value) {
        traversal.has(key, value);
    }

    @Override
    protected void vertexHasNot(String key) {
        traversal.hasNot(key);
    }

    @Override
    protected void vertexHas(String key) {
        traversal.has(key);
    }

    // TODO: Remove this once we test this - at this point i dont thib this is required
    // because predicare is an object
    /*
     * @Override
     * protected void vertexHas(final String key, final P<?> predicate) {
     * traversal.has(key, predicate);
     * }
     */

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<Vertex> getVerticesByProperty(final String key, final List<?> values) {

        // this is because the index is registered as an Integer
        List<Object> correctedValues = new ArrayList<>();
        for (Object item : values) {
            correctedValues.add(this.correctObjectType(item));
        }

        this.vertexHas(key, P.within(correctedValues));
        stepIndex++;
        return (QueryBuilder<Vertex>) this;
    }

    /**
     * @{inheritDoc}
     */
    public QueryBuilder<Vertex> getVerticesByCommaSeperatedValue(String key, String value) {
        ArrayList<String> values = new ArrayList<>(Arrays.asList(value.split(",")));
        int size = values.size();
        for (int i = 0; i < size; i++) {
            values.set(i, values.get(i).trim());
        }
        this.vertexHas(key, P.within(values));

        stepIndex++;
        return (QueryBuilder<Vertex>) this;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<Vertex> getVerticesStartsWithProperty(String key, Object value) {

        // correct value call because the index is registered as an Integer
        // TODO Check if this needs to be in QB and add these as internal
        this.vertexHas(key, org.janusgraph.core.attribute.Text.textPrefix(value));

        stepIndex++;
        return (QueryBuilder<Vertex>) this;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<Vertex> getVerticesByProperty(String key) {
        this.vertexHas(key);
        stepIndex++;
        return (QueryBuilder<Vertex>) this;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<Vertex> getVerticesExcludeByProperty(String key) {
        this.vertexHasNot(key);
        stepIndex++;
        return (QueryBuilder<Vertex>) this;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<Vertex> getVerticesExcludeByProperty(String key, Object value) {

        // correct value call because the index is registered as an Integer
        this.vertexHas(key, P.neq(this.correctObjectType(value)));
        stepIndex++;
        return (QueryBuilder<Vertex>) this;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<Vertex> getVerticesExcludeByProperty(final String key, final List<?> values) {

        // this is because the index is registered as an Integer
        List<Object> correctedValues = new ArrayList<>();
        for (Object item : values) {
            correctedValues.add(this.correctObjectType(item));
        }

        this.vertexHas(key, P.without(correctedValues));
        stepIndex++;
        return (QueryBuilder<Vertex>) this;
    }

    @Override
    public QueryBuilder<Vertex> getVerticesGreaterThanProperty(final String key, Object value) {
        this.vertexHas(key, P.gte(this.correctObjectType(value)));

        stepIndex++;
        return (QueryBuilder<Vertex>) this;
    }

    @Override
    public QueryBuilder<Vertex> getVerticesLessThanProperty(final String key, Object value) {
        this.vertexHas(key, P.lte(this.correctObjectType(value)));

        stepIndex++;
        return (QueryBuilder<Vertex>) this;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<Vertex> getChildVerticesFromParent(String parentKey, String parentValue, String childType) {
        traversal.has(parentKey, parentValue).has(AAIProperties.NODE_TYPE, childType);
        stepIndex++;
        return (QueryBuilder<Vertex>) this;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<Vertex> getTypedVerticesByMap(String type, Map<String, String> map) {

        for (Map.Entry<String, String> es : map.entrySet()) {
            this.vertexHas(es.getKey(), es.getValue());
            stepIndex++;
        }
        traversal.has(AAIProperties.NODE_TYPE, type);
        stepIndex++;
        return (QueryBuilder<Vertex>) this;
    }

    @Override
    public QueryBuilder<Vertex> getVerticesByBooleanProperty(String key, Object value) {

        if (value != null && !"".equals(value)) {
            boolean bValue = false;

            if (value instanceof String) {// "true"
                bValue = Boolean.valueOf(value.toString());
            } else if (value instanceof Boolean boolean1) {// true
                bValue = boolean1;
            }

            this.vertexHas(key, bValue);
            stepIndex++;
        }
        return (QueryBuilder<Vertex>) this;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<Vertex> createKeyQuery(Introspector obj) {
        Set<String> keys = obj.getKeys();
        Object val;
        for (String key : keys) {
            val = obj.getValue(key);
            Optional<String> metadata = obj.getPropertyMetadata(key, PropertyMetadata.DB_ALIAS);
            if (metadata.isPresent()) {
                // use the db name for the field rather than the object model
                key = metadata.get();
            }
            if (val != null) {
                // this is because the index is registered as an Integer
                if (val.getClass().equals(Long.class)) {
                    this.vertexHas(key, Integer.valueOf(val.toString()));
                } else {
                    this.vertexHas(key, val);
                }
                stepIndex++;
            }
        }
        return (QueryBuilder<Vertex>) this;
    }

    @Override
    public QueryBuilder<Vertex> exactMatchQuery(Introspector obj) {
        this.createKeyQuery(obj);
        allPropertiesQuery(obj);
        this.createContainerQuery(obj);
        return (QueryBuilder<Vertex>) this;
    }

    private void allPropertiesQuery(Introspector obj) {
        Set<String> props = obj.getProperties();
        Set<String> keys = obj.getKeys();
        Object val;
        for (String prop : props) {
            if (obj.isSimpleType(prop) && !keys.contains(prop)) {
                val = obj.getValue(prop);
                if (val != null) {
                    Optional<String> metadata = obj.getPropertyMetadata(prop, PropertyMetadata.DB_ALIAS);
                    if (metadata.isPresent()) {
                        // use the db name for the field rather than the object model
                        prop = metadata.get();
                    }
                    // this is because the index is registered as an Integer
                    if (val.getClass().equals(Long.class)) {
                        this.vertexHas(prop, Integer.valueOf(val.toString()));
                    } else {
                        this.vertexHas(prop, val);
                    }
                    stepIndex++;
                }
            }
        }
    }

    @Override
    public QueryBuilder<Vertex> createContainerQuery(Introspector obj) {
        String type = obj.getChildDBName();
        String abstractType = obj.getMetadata(ObjectMetadata.ABSTRACT);
        if (abstractType != null) {
            String[] inheritors = obj.getMetadata(ObjectMetadata.INHERITORS).split(",");
            traversal.has(AAIProperties.NODE_TYPE, P.within(inheritors));
        } else {
            traversal.has(AAIProperties.NODE_TYPE, type);
        }
        stepIndex++;
        markContainer();
        return (QueryBuilder<Vertex>) this;
    }

    /**
     * @throws NoEdgeRuleFoundException
     * @throws AAIException
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<Vertex> createEdgeTraversal(EdgeType type, Introspector parent, Introspector child)
            throws AAIException {
        createTraversal(type, parent, child, false);
        return (QueryBuilder<Vertex>) this;

    }

    @Override
    public QueryBuilder<Vertex> createPrivateEdgeTraversal(EdgeType type, Introspector parent, Introspector child)
            throws AAIException {
        this.createTraversal(type, parent, child, true);
        return (QueryBuilder<Vertex>) this;
    }

    private void createTraversal(EdgeType type, Introspector parent, Introspector child, boolean isPrivateEdge)
            throws AAIException {
        String isAbstractType = parent.getMetadata(ObjectMetadata.ABSTRACT);
        if ("true".equals(isAbstractType)) {
            markParentBoundary();
            traversal.union(handleAbstractEdge(type, parent, child, isPrivateEdge));
            stepIndex++;
        } else {
            this.edgeQueryToVertex(type, parent, child, null);
        }
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<Vertex> createEdgeTraversalWithLabels(EdgeType type, Introspector out, Introspector in,
            List<String> labels) throws AAIException {
        this.edgeQueryToVertex(type, out, in, labels);
        return (QueryBuilder<Vertex>) this;
    }

    private Traversal<Vertex, Vertex>[] handleAbstractEdge(EdgeType type, Introspector abstractParent,
            Introspector child, boolean isPrivateEdge) throws AAIException {
        String childName = child.getDbName();
        String inheritorMetadata = abstractParent.getMetadata(ObjectMetadata.INHERITORS);
        String[] inheritors = inheritorMetadata.split(",");
        List<Traversal<Vertex, Vertex>> unionTraversals = new ArrayList<>(inheritors.length);

        for (int i = 0; i < inheritors.length; i++) {
            String inheritor = inheritors[i];
            EdgeRuleQuery.Builder qB = new EdgeRuleQuery.Builder(inheritor, childName);
            if (edgeRules.hasRule(qB.build())) {
                Multimap<String, EdgeRule> rules = ArrayListMultimap.create();
                try {
                    rules = edgeRules.getRules(qB.edgeType(type).build());
                } catch (EdgeRuleNotFoundException e) {
                    throw new NoEdgeRuleFoundException(e);
                }

                GraphTraversal<Vertex, Vertex> innerTraversal = __.start();

                final List<String> inLabels = new ArrayList<>();
                final List<String> outLabels = new ArrayList<>();

                rules.values().forEach(rule -> {
                    if (rule.getDirection().equals(Direction.IN)) {
                        inLabels.add(rule.getLabel());
                    } else {
                        outLabels.add(rule.getLabel());
                    }
                });

                if (inLabels.isEmpty() && !outLabels.isEmpty()) {
                    innerTraversal.out(outLabels.toArray(new String[outLabels.size()]));
                } else if (outLabels.isEmpty() && !inLabels.isEmpty()) {
                    innerTraversal.in(inLabels.toArray(new String[inLabels.size()]));
                } else {
                    innerTraversal.union(__.out(outLabels.toArray(new String[outLabels.size()])),
                            __.in(inLabels.toArray(new String[inLabels.size()])));
                }

                innerTraversal.has(AAIProperties.NODE_TYPE, childName);
                unionTraversals.add(innerTraversal);
            }
        }

        return unionTraversals.toArray(new Traversal[unionTraversals.size()]);
    }

    public QueryBuilder<Edge> getEdgesBetweenWithLabels(EdgeType type, String outNodeType, String inNodeType,
            List<String> labels) throws AAIException {
        Introspector outObj = loader.introspectorFromName(outNodeType);
        Introspector inObj = loader.introspectorFromName(inNodeType);
        this.edgeQuery(type, outObj, inObj, labels);

        return (QueryBuilder<Edge>) this;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<E> union(QueryBuilder... builder) {
        GraphTraversal<Vertex, Vertex>[] traversals = new GraphTraversal[builder.length];
        for (int i = 0; i < builder.length; i++) {
            traversals[i] = (GraphTraversal<Vertex, Vertex>) builder[i].getQuery();
        }
        this.traversal.union(traversals);
        stepIndex++;

        return this;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<E> where(QueryBuilder... builder) {
        for (int i = 0; i < builder.length; i++) {
            this.traversal.where((GraphTraversal<Vertex, Vertex>) builder[i].getQuery());
            stepIndex++;
        }

        return this;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<E> or(QueryBuilder... builder) {
        GraphTraversal<Vertex, Vertex>[] traversals = new GraphTraversal[builder.length];
        for (int i = 0; i < builder.length; i++) {
            traversals[i] = (GraphTraversal<Vertex, Vertex>) builder[i].getQuery();
        }
        this.traversal.or(traversals);
        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<E> store(String name) {

        this.traversal.aggregate(Scope.local , name);
        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<E> cap(String name) {
        this.traversal.cap(name);
        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<E> unfold() {
        this.traversal.unfold();
        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<E> dedup() {

        this.traversal.dedup();
        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<E> emit() {

        this.traversal.emit();
        stepIndex++;

        return this;

    }

    @Override
    public QueryBuilder<E> repeat(QueryBuilder<E> builder) {

        this.traversal.repeat((GraphTraversal<Vertex, E>) builder.getQuery());
        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<E> until(QueryBuilder<E> builder) {
        this.traversal.until((GraphTraversal<Vertex, E>) builder.getQuery());
        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<E> groupCount() {
        this.traversal.groupCount();
        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<E> both() {
        this.traversal.both();
        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<Tree> tree() {

        this.traversal.tree();
        stepIndex++;

        return (QueryBuilder<Tree>) this;
    }

    @Override
    public QueryBuilder<E> by(String name) {
        this.traversal.by(name);
        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<E> valueMap() {
        this.traversal.valueMap();
        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<E> valueMap(String... names) {
        this.traversal.valueMap(names);
        stepIndex++;

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryBuilder<E> simplePath() {
        this.traversal.simplePath();
        stepIndex++;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryBuilder<Path> path() {
        this.traversal.path();
        stepIndex++;
        return (QueryBuilder<Path>) this;
    }

    @Override
    public QueryBuilder<Edge> outE() {
        this.traversal.outE();
        stepIndex++;
        return (QueryBuilder<Edge>) this;
    }

    @Override
    public QueryBuilder<Edge> inE() {
        this.traversal.inE();
        stepIndex++;
        return (QueryBuilder<Edge>) this;
    }

    @Override
    public QueryBuilder<Vertex> outV() {
        this.traversal.outV();
        stepIndex++;
        return (QueryBuilder<Vertex>) this;
    }

    @Override
    public QueryBuilder<Vertex> inV() {
        this.traversal.inV();
        stepIndex++;
        return (QueryBuilder<Vertex>) this;
    }

    @Override
    public QueryBuilder<E> as(String name) {
        this.traversal.as(name);

        stepIndex++;
        return this;
    }

    @Override
    public QueryBuilder<E> not(QueryBuilder<E> builder) {
        this.traversal.not(builder.getQuery());

        stepIndex++;
        return this;
    }

    @Override
    public QueryBuilder<E> select(String name) {
        this.traversal.select(name);

        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<E> select(Pop pop, String name) {
        this.traversal.select(pop, name);

        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<E> select(String... names) {
        if (names.length == 1) {
            this.traversal.select(names[0]);
        } else if (names.length == 2) {
            this.traversal.select(names[0], names[1]);
        } else if (names.length > 2) {
            String[] otherNames = Arrays.copyOfRange(names, 2, names.length);
            this.traversal.select(names[0], names[1], otherNames);
        }

        stepIndex++;

        return this;
    }

    /**
     * Edge query.
     *
     * @param outObj the out type
     * @param inObj the in type
     * @throws NoEdgeRuleFoundException
     * @throws AAIException
     */
    private void edgeQueryToVertex(EdgeType type, Introspector outObj, Introspector inObj, List<String> labels)
            throws AAIException {
        String outType = outObj.getDbName();
        String inType = inObj.getDbName();

        if (outObj.isContainer()) {
            outType = outObj.getChildDBName();
        }
        if (inObj.isContainer()) {
            inType = inObj.getChildDBName();
        }
        markParentBoundary();
        Multimap<String, EdgeRule> rules = ArrayListMultimap.create();
        EdgeRuleQuery.Builder qB = new EdgeRuleQuery.Builder(outType, inType).edgeType(type);

        if (labels == null) {
            try {
                rules.putAll(edgeRules.getRules(qB.build()));
            } catch (EdgeRuleNotFoundException e) {
                // is ok per original functioning of this section
                // TODO add "best try" sort of flag to the EdgeRuleQuery
                // to indicate if the exception should be thrown or not
            }
        } else {
            for (String label : labels) {
                try {
                    rules.putAll(edgeRules.getRules(qB.label(label).build()));
                } catch (EdgeRuleNotFoundException e) {
                    throw new NoEdgeRuleFoundException(e);
                }
            }
            if (rules.isEmpty()) {
                throw new NoEdgeRuleFoundException(
                        "No edge rules found for " + outType + " and " + inType + " of type " + type.toString());
            }
        }

        final List<String> inLabels = new ArrayList<>();
        final List<String> outLabels = new ArrayList<>();

        for (EdgeRule rule : rules.values()) {
            if (labels != null && !labels.contains(rule.getLabel())) {
                return;
            } else {
                if (Direction.IN.equals(rule.getDirection())) {
                    inLabels.add(rule.getLabel());
                } else {
                    outLabels.add(rule.getLabel());
                }
            }
        }

        if (inLabels.isEmpty() && !outLabels.isEmpty()) {
            traversal.out(outLabels.toArray(new String[outLabels.size()]));
        } else if (outLabels.isEmpty() && !inLabels.isEmpty()) {
            traversal.in(inLabels.toArray(new String[inLabels.size()]));
        } else {
            traversal.union(__.out(outLabels.toArray(new String[outLabels.size()])),
                    __.in(inLabels.toArray(new String[inLabels.size()])));
        }

        stepIndex++;

        this.createContainerQuery(inObj);

    }

    /**
     * Edge query.
     *
     * @param outObj the out type
     * @param inObj the in type
     * @throws NoEdgeRuleFoundException
     * @throws AAIException
     */
    private void edgeQuery(EdgeType type, Introspector outObj, Introspector inObj, List<String> labels)
            throws AAIException {
        String outType = outObj.getDbName();
        String inType = inObj.getDbName();

        if (outObj.isContainer()) {
            outType = outObj.getChildDBName();
        }
        if (inObj.isContainer()) {
            inType = inObj.getChildDBName();
        }

        markParentBoundary();
        Multimap<String, EdgeRule> rules = ArrayListMultimap.create();
        EdgeRuleQuery.Builder qB = new EdgeRuleQuery.Builder(outType, inType).edgeType(type);

        try {
            if (labels == null) {
                rules.putAll(edgeRules.getRules(qB.build()));
            } else {
                for (String label : labels) {
                    rules.putAll(edgeRules.getRules(qB.label(label).build()));
                }
            }
        } catch (EdgeRuleNotFoundException e) {
            throw new NoEdgeRuleFoundException(e);
        }

        final List<String> inLabels = new ArrayList<>();
        final List<String> outLabels = new ArrayList<>();

        for (EdgeRule rule : rules.values()) {
            if (labels != null && !labels.contains(rule.getLabel())) {
                return;
            } else {
                if (Direction.IN.equals(rule.getDirection())) {
                    inLabels.add(rule.getLabel());
                } else {
                    outLabels.add(rule.getLabel());
                }
            }
        }

        if (inLabels.isEmpty() && !outLabels.isEmpty()) {
            traversal.outE(outLabels.toArray(new String[outLabels.size()]));
        } else if (outLabels.isEmpty() && !inLabels.isEmpty()) {
            traversal.inE(inLabels.toArray(new String[inLabels.size()]));
        } else {
            traversal.union(__.outE(outLabels.toArray(new String[outLabels.size()])),
                    __.inE(inLabels.toArray(new String[inLabels.size()])));
        }
    }

    @Override
    public QueryBuilder<E> limit(long amount) {
        traversal.limit(amount);
        return this;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public <E2> E2 getQuery() {
        return (E2) this.traversal;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<E> getParentQuery() {
        return this.parentQuery != null
            ? this.parentQuery
            : cloneQueryAtStep(parentStepIndex);
    }

    @Override
    public QueryBuilder<E> getContainerQuery() {

        if (this.parentStepIndex == 0) {
            return removeQueryStepsBetween(0, containerStepIndex);
        } else {
            return this.containerQuery;
        }
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public void markParentBoundary() {
        this.parentQuery = cloneQueryAtStep(stepIndex);
        parentStepIndex = stepIndex;
    }

    @Override
    public void markContainer() {
        this.containerQuery = cloneQueryAtStep(stepIndex);
        containerStepIndex = stepIndex;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public Vertex getStart() {
        return this.start;
    }

    protected int getParentStepIndex() {
        return parentStepIndex;
    }

    protected int getContainerStepIndex() {
        return containerStepIndex;
    }

    protected int getStepIndex() {
        return stepIndex;
    }

    /**
     * end is exclusive
     *
     * @param start
     * @param end
     * @return
     */
    protected abstract QueryBuilder<E> removeQueryStepsBetween(int start, int end);

    protected void executeQuery() {

        Admin<Vertex, Vertex> admin;
        if (start != null) {
            this.completeTraversal = traversal.asAdmin();
        } else {
            boolean queryLoggingEnabled = false;
            if(queryLoggingEnabled) {
                String query = groovyTranslator.translate(traversal.asAdmin().getBytecode()).getScript();
                LOGGER.info("Query: {}", query);
            }

            admin = source.V().asAdmin();
            TraversalHelper.insertTraversal(admin.getEndStep(), traversal.asAdmin(), admin);

            this.completeTraversal = (Admin<Vertex, E>) admin;

        }

    }

    @Override
    public boolean hasNext() {
        if (this.completeTraversal == null) {
            executeQuery();
        }

        return this.completeTraversal.hasNext();
    }

    @Override
    public E next() {
        if (this.completeTraversal == null) {
            executeQuery();
        }

        return this.completeTraversal.next();
    }

    @Override
    public List<E> toList() {
        if (this.completeTraversal == null) {
            executeQuery();
        }
        return this.completeTraversal.toList();
    }

    @Override
    public QueryBuilder<E> sort(Sort sort) {
        Order order = sort.getDirection() == Sort.Direction.ASC ? Order.asc : Order.desc;
        traversal.order().by(sort.getProperty(), order);
        stepIndex++;
        return this;
    }

    public PaginationResult<E> toPaginationResult(Pageable pageable) {
        int page = pageable.getPage();
        int pageSize = pageable.getPageSize();
        if(pageable.isIncludeTotalCount()) {
            return paginateWithTotalCount(page, pageSize);
        } else {
            return paginateWithoutTotalCount(page, pageSize);
        }
    }

    private PaginationResult<E> paginateWithoutTotalCount(int page, int pageSize) {
        int startIndex = page * pageSize;
        traversal.range(startIndex, startIndex + pageSize);

        if (this.completeTraversal == null) {
            executeQuery();
        }
        return completeTraversal.hasNext()
            ? new PaginationResult<E>(completeTraversal.toList())
            : new PaginationResult<E>(Collections.emptyList());
    }

    private PaginationResult<E> paginateWithTotalCount(int page, int pageSize) {
       int startIndex = page * pageSize;
       traversal.fold().as("results","count")
               .select("results","count").
                   by(__.range(Scope.local, startIndex, startIndex + pageSize)).
                   by(__.count(Scope.local));

       if (this.completeTraversal == null) {
           executeQuery();
       }
       try {
           return mapPaginationResult((Map<String,Object>) completeTraversal.next());
       // .next() will throw an IllegalArguementException if there are no vertices of the given type
       } catch (NoSuchElementException | IllegalArgumentException e) {
           return new PaginationResult<>(Collections.emptyList(), 0L);
       }
    }

    private PaginationResult<E> mapPaginationResult(Map<String,Object> result) {
        Object objCount = result.get("count");
        Object vertices = result.get("results");
        if(vertices == null) {
            return new PaginationResult<E>(Collections.emptyList() ,0L);
        }
        List<E> results = null;
        if(vertices instanceof List) {
            results = (List<E>) vertices;
        } else if (vertices instanceof Vertex) {
            results = Collections.singletonList((E) vertices);
        } else {
            String msg = "Results must be a list or a vertex, but was %s".formatted(vertices.getClass().getName());
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
        long totalCount = parseCount(objCount);
        return new PaginationResult<E>(results, totalCount);
    }

    private long parseCount(Object count) {
        if(count instanceof String string) {
            return Long.parseLong(string);
        } else if(count instanceof Integer integer) {
            return Long.valueOf(integer);
        } else if (count instanceof Long long1) {
            return long1;
        } else {
            throw new IllegalArgumentException("Count must be a string, integer, or long");
        }
    }

    protected QueryBuilder<Edge> has(String key, String value) {
        traversal.has(key, value);

        return (QueryBuilder<Edge>) this;
    }

}
