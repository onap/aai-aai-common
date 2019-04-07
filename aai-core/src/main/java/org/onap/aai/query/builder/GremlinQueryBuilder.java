/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright © 2018 IBM.
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

import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.Tree;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.restcore.search.GremlinGroovyShell;
import org.onap.aai.schema.enums.ObjectMetadata;
import org.onap.aai.edges.EdgeRule;
import org.onap.aai.edges.EdgeRuleQuery;
import org.onap.aai.edges.enums.EdgeType;
import org.onap.aai.edges.exceptions.EdgeRuleNotFoundException;
import org.onap.aai.serialization.db.exceptions.NoEdgeRuleFoundException;

import java.util.*;

/**
 * The Class GremlinQueryBuilder.
 */
public abstract class GremlinQueryBuilder<E> extends QueryBuilder<E> {

    private static final String ARGUMENT2 = "#!#argument#!#";
    private static final String HAS = ".has('";
    private GremlinGroovyShell gremlinGroovy = new GremlinGroovyShell();
    private GraphTraversal<?, ?> completeTraversal = null;
    protected List<String> list = null;

    /**
     * Instantiates a new gremlin query builder.
     *
     * @param loader the loader
     */
    public GremlinQueryBuilder(Loader loader, GraphTraversalSource source) {
        super(loader, source);
        list = new ArrayList<>();
    }

    /**
     * Instantiates a new gremlin query builder.
     *
     * @param loader the loader
     * @param start the start
     */
    public GremlinQueryBuilder(Loader loader, GraphTraversalSource source, Vertex start) {
        super(loader, source, start);
        list = new ArrayList<>();
    }

    @Override
    public QueryBuilder<Vertex> exactMatchQuery(Introspector obj) {
        // TODO not implemented because this is implementation is no longer used
        this.createKeyQuery(obj);
        this.createContainerQuery(obj);
        return (QueryBuilder<Vertex>) this;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<Vertex> getVerticesByProperty(String key, Object value) {

        String term = "";
        if (value != null && !(value instanceof String) ) {
            term = value.toString();
        } else {
            term = "'" + value + "'";
        }
        list.add(HAS + key + "', " + term + ")");
        stepIndex++;
        return (QueryBuilder<Vertex>) this;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<Vertex> getVerticesByNumberProperty(String key, Object value) {
        list.add(HAS + key + "', " + value + ")");
        stepIndex++;
        return (QueryBuilder<Vertex>) this;
    }

    @Override
    public QueryBuilder<Vertex> getVerticesByBooleanProperty(String key, Object value) {
    	
    	if(value!=null && !"".equals(value)) {
    		boolean bValue = false;
	        if(value instanceof String){
	            bValue = Boolean.valueOf(value.toString());
	        } else if(value instanceof Boolean){
	            bValue = (Boolean) value;
	        }
	
	        list.add(HAS + key + "', " + bValue + ")");
	        stepIndex++;
    	}
        return (QueryBuilder<Vertex>) this;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<Vertex> getVerticesByProperty(String key, List<?> values) {

        String predicate = "P.within(#!#argument#!#)";
        List<String> arguments = new ArrayList<>();
        for (Object item : values) {
            if (item != null && !(item instanceof String)) {
                arguments.add(item.toString());
            } else {
                arguments.add("'" + item + "'");
            }
        }
        String argument = Joiner.on(",").join(arguments);
        predicate = predicate.replace(ARGUMENT2, argument);
        list.add(HAS + key + "', " + predicate + ")");
        stepIndex++;
        return (QueryBuilder<Vertex>) this;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<Vertex> getVerticesByProperty(String key) {

        list.add(HAS + key + "')");
        stepIndex++;
        return (QueryBuilder<Vertex>) this;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<Vertex> getVerticesExcludeByProperty(String key) {

        list.add(".hasNot('" + key + "')");
        stepIndex++;
        return (QueryBuilder<Vertex>) this;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<Vertex> getVerticesStartsWithProperty(String key, Object value) {

        String term = "";
        String predicate = "org.janusgraph.core.attribute.Text.textPrefix(#!#argument#!#)";
        if (value != null && !(value instanceof String) ) {
            term = value.toString();
        } else {
            term = "'" + value + "'";
        }
        predicate = predicate.replace(ARGUMENT2, term);
        list.add(HAS + key + "', " + predicate + ")");
        stepIndex++;
        return (QueryBuilder<Vertex>) this;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<Vertex> getVerticesExcludeByProperty(String key, Object value) {

        String term = "";
        String predicate = "P.neq(#!#argument#!#)";
        if (value != null && !(value instanceof String) ) {
            term = value.toString();
        } else {
            term = "'" + value + "'";
        }
        predicate = predicate.replace(ARGUMENT2, term);
        list.add(HAS + key + "', " + predicate + ")");
        stepIndex++;
        return (QueryBuilder<Vertex>) this;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<Vertex> getVerticesExcludeByProperty(String key, List<?> values) {

        String predicate = "P.without(#!#argument#!#)";
        List<String> arguments = new ArrayList<>();
        for (Object item : values) {
            if (item != null && !(item instanceof String)) {
                arguments.add(item.toString());
            } else {
                arguments.add("'" + item + "'");
            }
        }
        String argument = Joiner.on(",").join(arguments);
        predicate = predicate.replace(ARGUMENT2, argument);
        list.add(HAS + key + "', " + predicate + ")");
        stepIndex++;
        return (QueryBuilder<Vertex>) this;
    }

    @Override
    public QueryBuilder<Vertex> getVerticesGreaterThanProperty(String key, Object value) {
        String predicate = "P.gte(#!#argument1#!#)";
        String term;
        if (value != null && !(value instanceof String) ) {
            term = value.toString();
        } else {
            term = "'" + value + "'";
        }
        predicate = predicate.replace("#!#argument1#!#", term);
        list.add(HAS + key + "', " + predicate + ")");
        stepIndex++;
        return (QueryBuilder<Vertex>) this;
    }

    @Override
    public QueryBuilder<Vertex> getVerticesLessThanProperty(String key, Object value) {
        String predicate = "P.lte(#!#argument1#!#)";
        String term;
        if (value != null && !(value instanceof String) ) {
            term = value.toString();
        } else {
            term = "'" + value + "'";
        }
        predicate = predicate.replace("#!#argument1#!#", term);
        list.add(HAS + key + "', " + predicate + ")");
        stepIndex++;
        return (QueryBuilder<Vertex>) this;
    }




    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<Vertex> getChildVerticesFromParent(String parentKey, String parentValue, String childType) {
        //TODO
        return (QueryBuilder<Vertex>) this;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<Vertex> getTypedVerticesByMap(String type, Map<String, String> map) {

        for (Map.Entry<String, String> es : map.entrySet()) {
            list.add(HAS + es.getKey() + "', '" + es.getValue() + "')");
            stepIndex++;
        }
        list.add(".has('aai-node-type', '" + type + "')");
        stepIndex++;
        return (QueryBuilder<Vertex>) this;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<Vertex> createKeyQuery(Introspector obj) {
        Set<String> keys = obj.getKeys();

        for (String key : keys) {

            this.getVerticesByProperty(key, obj.<Object>getValue(key));

        }
        return (QueryBuilder<Vertex>) this;
    }

    /**
     * @throws NoEdgeRuleFoundException
     * @throws AAIException
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder createEdgeTraversal(EdgeType type, Introspector parent, Introspector child) throws AAIException {
        String parentName = parent.getDbName();
        String childName = child.getDbName();
        if (parent.isContainer()) {
            parentName = parent.getChildDBName();
        }
        if (child.isContainer()) {
            childName = child.getChildDBName();
        }
        this.edgeQueryToVertex(type, parentName, childName, null);
        return this;

    }

    @Override
    public QueryBuilder createPrivateEdgeTraversal(EdgeType type, Introspector parent, Introspector child) throws AAIException{
        String parentName = parent.getDbName();
        String childName = child.getDbName();
        if (parent.isContainer()) {
            parentName = parent.getChildDBName();
        }
        if (child.isContainer()) {
            childName = child.getChildDBName();
        }
        this.edgeQueryToVertex(type, parentName, childName, null, true);
        return this;
    }

    /**
     *
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<Vertex> createEdgeTraversalWithLabels(EdgeType type, Introspector out, Introspector in, List<String> labels) throws AAIException {
        String parentName = out.getDbName();
        String childName = in.getDbName();
        if (out.isContainer()) {
            parentName = out.getChildDBName();
        }
        if (in.isContainer()) {
            childName = in.getChildDBName();
        }
        this.edgeQueryToVertex(type, parentName, childName, labels);
        return (QueryBuilder<Vertex>) this;
    }


    public QueryBuilder<Edge> getEdgesBetweenWithLabels(EdgeType type, String outNodeType, String inNodeType, List<String> labels) throws AAIException {
        this.edgeQuery(type, outNodeType, inNodeType, labels);
        return (QueryBuilder<Edge>)this;
    }

    private void edgeQueryToVertex(EdgeType type, String outType, String inType, List<String> labels) throws AAIException {
        this.edgeQueryToVertex(type, outType, inType, labels, false);
    }

    /**
     * Edge query.
     *
     * @param outType the out type
     * @param inType the in type
     * @throws NoEdgeRuleFoundException
     * @throws AAIException
     */
    private void edgeQueryToVertex(EdgeType type, String outType, String inType, List<String> labels, boolean isPrivateEdge) throws AAIException {
        markParentBoundary();
        Multimap<String, EdgeRule> rules = ArrayListMultimap.create();
        EdgeRuleQuery.Builder qB = new EdgeRuleQuery.Builder(outType, inType).edgeType(type).setPrivate(isPrivateEdge);

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
                    if(inType.equals(outType)) {//code to handle when a type edges to itself, to add both in and out
                        outLabels.add(rule.getLabel());
                    }
                } else {
                    outLabels.add(rule.getLabel());
                    if(inType.equals(outType)) {//code to handle when a type edges to itself, to add both in and out
                        inLabels.add(rule.getLabel());
                    }
                }
            }
        }

        if(inLabels.isEmpty() && outLabels.isEmpty()) {
            throw new NoEdgeRuleFoundException("no " + type.toString() + " edge rule between " + outType + " and " + inType );
        } else if (inLabels.isEmpty() && !outLabels.isEmpty()) {
            list.add(".out('" + String.join("','", outLabels) + "')");
        } else if (outLabels.isEmpty() && !inLabels.isEmpty()) {
            list.add(".in('" + String.join("','", inLabels) + "')");
        } else {
            list.add(".union(__.in('" + String.join("','", inLabels) + "')" + ", __.out('" + String.join("','", outLabels) + "'))");
        }
        stepIndex++;
        list.add(HAS + AAIProperties.NODE_TYPE + "', '" + inType + "')");
        stepIndex++;

    }

    /**
     * Edge query.
     *
     * @param outType the out type
     * @param inType the in type
     * @throws NoEdgeRuleFoundException
     * @throws AAIException
     */
    private void edgeQuery(EdgeType type, String outType, String inType, List<String> labels) throws AAIException {
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

        if(inLabels.isEmpty() && outLabels.isEmpty()) {
            throw new NoEdgeRuleFoundException("no " + type.toString() + " edge rule between " + outType + " and " + inType );
        } else if (inLabels.isEmpty() && !outLabels.isEmpty()) {
            list.add(".outE('" + String.join("','", outLabels) + "')");
        } else if (outLabels.isEmpty() && !inLabels.isEmpty()) {
            list.add(".inE('" + String.join("','", inLabels) + "')");
        } else {
            list.add(".union(__.inE('" + String.join("','", inLabels) + "')" + ", __.outE('" + String.join("','", outLabels) + "'))");
        }

        stepIndex++;

    }
    @Override
    public QueryBuilder<E> limit(long amount) {
        list.add(".limit(" + amount + ")");
        return this;
    }
    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<Vertex> createContainerQuery(Introspector obj) {
        String type = obj.getChildDBName();
        String abstractType = obj.getMetadata(ObjectMetadata.ABSTRACT);
        if (abstractType != null) {
            String[] inheritors = obj.getMetadata(ObjectMetadata.INHERITORS).split(",");
            String[] wrapped = new String[inheritors.length];
            StringBuilder command = new StringBuilder();
            command.append("P.within(");
            for (int i = 0; i < inheritors.length; i++) {
                wrapped[i] = "'" + inheritors[i] + "'";
            }
            command.append(Joiner.on(",").join(wrapped));
            command.append(")");
            list.add(".has('aai-node-type', " + command + ")");

        } else {
            list.add(".has('aai-node-type', '" + type + "')");
        }
        stepIndex++;
        this.markContainer();
        return (QueryBuilder<Vertex>) this;
    }

    @Override
    public QueryBuilder<E> union(QueryBuilder<E>... builder) {
        markParentBoundary();
        String[] traversals = new String[builder.length];
        StringBuilder command = new StringBuilder();
        for (int i = 0; i < builder.length; i++) {
            traversals[i] = "__" + builder[i].getQuery();
        }
        command.append(".union(");
        command.append(Joiner.on(",").join(traversals));
        command.append(")");
        list.add(command.toString());
        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<E> where(QueryBuilder<E>... builder) {
        markParentBoundary();
        List<String> traversals = new ArrayList<>();
        for (int i = 0; i < builder.length; i++) {
            traversals.add(".where(__" + builder[i].getQuery() + ")");
            stepIndex++;
        }
        list.addAll(traversals);


        return this;
    }

    @Override
    public QueryBuilder<E> or(QueryBuilder<E>... builder) {
        markParentBoundary();
        String[] traversals = new String[builder.length];
        StringBuilder command = new StringBuilder();
        for (int i = 0; i < builder.length; i++) {
            traversals[i] = "__" + builder[i].getQuery();
        }
        command.append(".or(");
        command.append(Joiner.on(",").join(traversals));
        command.append(")");
        list.add(command.toString());
        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<E> store(String name) {
        this.list.add(".store('"+ name + "')");
        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<E> cap(String name) {
        this.list.add(".cap('"+ name + "')");
        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<E> unfold() {
        this.list.add(".unfold()");
        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<E> dedup() {
        this.list.add(".dedup()");
        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<E> emit() {
        this.list.add(".emit()");
        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<E> repeat(QueryBuilder<E> builder) {
        this.list.add(".repeat(__" + builder.getQuery()  + ")");
        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<E> until(QueryBuilder<E> builder) {
        this.list.add(".until(__" + builder.getQuery() + ")");
        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<E> groupCount() {
        this.list.add(".groupCount()");
        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<E> both() {
        this.list.add(".both()");
        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<Tree> tree() {
        this.list.add(".tree()");
        stepIndex++;

        return (QueryBuilder<Tree>)this;
    }

    @Override
    public QueryBuilder<E> by(String name) {
        this.list.add(".by('"+ name + "')");
        stepIndex++;

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryBuilder<E> simplePath(){
        this.list.add(".simplePath()");
        stepIndex++;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryBuilder<Path> path(){
        this.list.add(".path()");
        stepIndex++;
        return (QueryBuilder<Path>)this;
    }

    @Override
    public QueryBuilder<Edge> outE() {
        this.list.add(".outE()");
        stepIndex++;

        return (QueryBuilder<Edge>)this;
    }

    @Override
    public QueryBuilder<Edge> inE() {
        this.list.add(".inE()");
        stepIndex++;

        return (QueryBuilder<Edge>)this;
    }

    @Override
    public QueryBuilder<Vertex> outV() {
        this.list.add(".outV()");
        stepIndex++;

        return (QueryBuilder<Vertex>)this;
    }

    @Override
    public QueryBuilder<Vertex> inV() {
        this.list.add(".inV()");
        stepIndex++;

        return (QueryBuilder<Vertex>)this;
    }

    @Override
    public QueryBuilder<E> not(QueryBuilder<E> builder) {
        this.list.add(".not(" + "__" + builder.getQuery() + ")");
        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<E> as(String name) {
        this.list.add(".as('" + name + "')");
        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<E> select(String name) {
        this.list.add(".select('" + name + "')");
        stepIndex++;

        return this;
    }

    @Override
    public QueryBuilder<E> select(String... names) {
    	String stepString = ".select('";
    	for(int i = 0; i<names.length; i++) {
    		stepString = stepString + names[i] +"'";
    		if(i!=(names.length-1)) {
    			stepString = stepString + ",'";
    		}
    	}
    	stepString = stepString + ")";
    	this.list.add(stepString);
        stepIndex++;

        return this;
    }
    /**
     * @{inheritDoc}
     */
    @Override
    public QueryBuilder<E> getParentQuery() {
        return cloneQueryAtStep(parentStepIndex);
    }

    @Override
    public QueryBuilder<E> getContainerQuery() {
        return cloneQueryAtStep(containerStepIndex);
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public <T2> T2 getQuery() {
        StringBuilder sb = new StringBuilder();

        for (String piece : this.list) {
            sb.append(piece);
        }

        return (T2)sb.toString();
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public void markParentBoundary() {
        parentStepIndex = stepIndex;
    }

    @Override
    public void markContainer() {
        this.containerStepIndex = stepIndex;
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

    private void executeQuery() {
        String queryString = "g" + Joiner.on("").join(list);
        Map<String, Object> params = new HashMap<>();
        if (this.start == null) {
            params.put("g", source.V());
        } else {
            params.put("g", source.V(this.start));
        }
        this.completeTraversal = this.gremlinGroovy.executeTraversal(queryString, params);
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

        return (E)this.completeTraversal.next();
    }

    @Override
    public List<E> toList() {
        if (this.completeTraversal == null) {
            executeQuery();
        }

        return (List<E>)this.completeTraversal.toList();
    }

    protected QueryBuilder<Edge> has(String key, String value) {
        this.list.add(HAS + key + "','" + value + "')");

        return (QueryBuilder<Edge>)this;
    }

}
