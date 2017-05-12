/*-
 * ============LICENSE_START=======================================================
 * org.openecomp.aai
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.aai.query.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import org.openecomp.aai.db.props.AAIProperties;
import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Introspector;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.restcore.search.GremlinGroovyShellSingleton;
import org.openecomp.aai.schema.enums.ObjectMetadata;
import org.openecomp.aai.serialization.db.EdgeRule;
import org.openecomp.aai.serialization.db.EdgeRules;
import org.openecomp.aai.serialization.db.EdgeType;
import org.openecomp.aai.serialization.db.exceptions.NoEdgeRuleFoundException;
import com.google.common.base.Joiner;

/**
 * The Class GremlinQueryBuilder.
 */
public abstract class GremlinQueryBuilder extends QueryBuilder {
	
	private EdgeRules edgeRules = EdgeRules.getInstance();
	private GremlinGroovyShellSingleton gremlinGroovy = GremlinGroovyShellSingleton.getInstance();
	private GraphTraversal<?, ?> completeTraversal = null;
	protected List<String> list = null;
	
	protected int parentStepIndex = 0;
	protected int containerStepIndex = 0;
	protected int stepIndex = 0;
	
	/**
	 * Instantiates a new gremlin query builder.
	 *
	 * @param loader the loader
	 */
	public GremlinQueryBuilder(Loader loader, GraphTraversalSource source) {
		super(loader, source);
		list = new ArrayList<String>();
	}
	
	/**
	 * Instantiates a new gremlin query builder.
	 *
	 * @param loader the loader
	 * @param start the start
	 */
	public GremlinQueryBuilder(Loader loader, GraphTraversalSource source, Vertex start) {
		super(loader, source, start);
		list = new ArrayList<String>();
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	public QueryBuilder createDBQuery(Introspector obj) {
		this.createKeyQuery(obj);
		this.createContainerQuery(obj);
		return this;
	}
	
	@Override
	public QueryBuilder exactMatchQuery(Introspector obj) {
		// TODO not implemented because this is implementation is no longer used
		this.createKeyQuery(obj);
		//allPropertiesQuery(obj);
		this.createContainerQuery(obj);
		return this;
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	public QueryBuilder getVerticesByIndexedProperty(String key, Object value) {
		return this.getVerticesByProperty(key, value);
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	public QueryBuilder getVerticesByIndexedProperty(String key, List<?> values) {
		return this.getVerticesByProperty(key, values);
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public QueryBuilder getVerticesByProperty(String key, Object value) {

		String term = "";
		if (value != null && !value.getClass().getName().equals("java.lang.String")) {
			term = value.toString();
		} else {
			term = "'" + value + "'";
		}
		list.add(".has('" + key + "', " + term + ")");
		stepIndex++;
		return this;
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	public QueryBuilder getVerticesByProperty(String key, List<?> values) {

		String term = "";
		String predicate = "P.within(#!#argument#!#)";
		List<String> arguments = new ArrayList<>();
		for (Object item : values) {
			if (item != null && !item.getClass().getName().equals("java.lang.String")) {
				arguments.add(item.toString());
			} else {
				arguments.add("'" + item + "'");
			}
		}
		String argument = Joiner.on(",").join(arguments);
		predicate = predicate.replace("#!#argument#!#", argument);
		list.add(".has('" + key + "', " + predicate + ")");
		stepIndex++;
		return this;
	}
	
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	public QueryBuilder getChildVerticesFromParent(String parentKey, String parentValue, String childType) {
		/*
		String query = ".has('aai-node-type', '" + childType + "')";
		
		return this.processGremlinQuery(parentKey, parentValue, query);
		*/
		//TODO
		return this;
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	public QueryBuilder getTypedVerticesByMap(String type, LinkedHashMap<String, String> map) {
		
		for (String key : map.keySet()) {
			list.add(".has('" + key + "', '" + map.get(key) + "')");
			stepIndex++;
		}
		list.add(".has('aai-node-type', '" + type + "')");
		stepIndex++;
		return this;
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	public QueryBuilder createKeyQuery(Introspector obj) {
		Set<String> keys = obj.getKeys();

		for (String key : keys) {
			
			this.getVerticesByProperty(key, obj.<Object>getValue(key));
			
		}		
		return this;
	}
	
	/**
	 * @throws NoEdgeRuleFoundException 
	 * @throws AAIException 
	 * @{inheritDoc}
	 */
	@Override
	public QueryBuilder createEdgeTraversal(EdgeType type, Introspector parent, Introspector child) throws AAIException, NoEdgeRuleFoundException {
		String parentName = parent.getDbName();
		String childName = child.getDbName();
		if (parent.isContainer()) {
			parentName = parent.getChildDBName();
		}
		if (child.isContainer()) {
			childName = child.getChildDBName();
		}
		this.edgeQuery(type, parentName, childName);
		return this;
			
	}
	
	/**
	 * @throws NoEdgeRuleFoundException 
	 * @throws AAIException 
	 * @{inheritDoc}
	 */
	@Override
	public QueryBuilder createEdgeTraversal(EdgeType type, Vertex parent, Introspector child) throws AAIException, NoEdgeRuleFoundException {
		String nodeType = parent.<String>property(AAIProperties.NODE_TYPE).orElse(null);
		this.edgeQuery(type, nodeType, child.getDbName());
		
		return this;
			
	}
	
	/**
	 * Edge query.
	 *
	 * @param outType the out type
	 * @param inType the in type
	 * @throws NoEdgeRuleFoundException 
	 * @throws AAIException 
	 */
	private void edgeQuery(EdgeType type, String outType, String inType) throws AAIException, NoEdgeRuleFoundException {
		markParentBoundary();
		EdgeRule rule = edgeRules.getEdgeRule(type, outType, inType);
		if (rule.getDirection().equals(Direction.OUT)) {
			list.add(".out('" + rule.getLabel() + "')");
		} else {
			list.add(".in('" + rule.getLabel() + "')");
		}
		list.add(".has('" + AAIProperties.NODE_TYPE + "', '" + inType + "')");
		stepIndex += 2;
	}
	@Override
	public QueryBuilder limit(long amount) {
		list.add(".limit(" + amount + ")");
		return this;
	}
	/**
	 * @{inheritDoc}
	 */
	@Override
	public QueryBuilder createContainerQuery(Introspector obj) {
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
		return this;
	}
	
	@Override
	public QueryBuilder union(QueryBuilder... builder) {
		markParentBoundary();
		String[] traversals = new String[builder.length];
		StringBuilder command = new StringBuilder();
		for (int i = 0; i < builder.length; i++) {
			traversals[i] = "__" + (String)builder[i].getQuery();
		}
		command.append(".union(");
		command.append(Joiner.on(",").join(traversals));
		command.append(")");
		list.add(command.toString());
		stepIndex++;
		
		return this;
	}
	
	@Override
	public QueryBuilder where(QueryBuilder... builder) {
		markParentBoundary();
		List<String> traversals = new ArrayList<>();
		for (int i = 0; i < builder.length; i++) {
			traversals.add(".where(__" + (String)builder[i].getQuery() + ")");
			stepIndex++;
		}
		list.addAll(traversals);
		
		
		return this;
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	public QueryBuilder getParentQuery() {
		return cloneQueryAtStep(parentStepIndex);
	}
	
	@Override
	public QueryBuilder getContainerQuery() {
		return cloneQueryAtStep(containerStepIndex);
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	public <T> T getQuery() {
		StringBuilder sb = new StringBuilder();
		
		for (String piece : this.list) {
			sb.append(piece);
		}
		
		return (T)sb.toString();
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
	
	protected abstract QueryBuilder cloneQueryAtStep(int index);
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
	public Vertex next() {
		if (this.completeTraversal == null) {
			executeQuery();
		}
		
		return (Vertex)this.completeTraversal.next();
	}
	
	@Override
	public List<Vertex> toList() {
		if (this.completeTraversal == null) {
			executeQuery();
		}
		
		return (List<Vertex>)this.completeTraversal.toList();
	}
	
}
