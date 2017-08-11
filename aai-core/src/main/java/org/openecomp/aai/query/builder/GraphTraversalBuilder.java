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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal.Admin;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.DefaultGraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.process.traversal.util.TraversalHelper;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.openecomp.aai.db.props.AAIProperties;
import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Introspector;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.schema.enums.ObjectMetadata;
import org.openecomp.aai.schema.enums.PropertyMetadata;
import org.openecomp.aai.serialization.db.EdgeRule;
import org.openecomp.aai.serialization.db.EdgeRules;
import org.openecomp.aai.serialization.db.EdgeType;
import org.openecomp.aai.serialization.db.exceptions.NoEdgeRuleFoundException;

/**
 * The Class GraphTraversalBuilder.
 */
public abstract class GraphTraversalBuilder<E> extends QueryBuilder<E> {

	protected GraphTraversal<Vertex, E> traversal = null;
	protected Admin<Vertex, E> completeTraversal = null;
	private EdgeRules edgeRules = EdgeRules.getInstance();
	
	protected int parentStepIndex = 0;
	protected int containerStepIndex = 0;
	protected int stepIndex = 0;
	
	/**
	 * Instantiates a new graph traversal builder.
	 *
	 * @param loader the loader
	 */
	public GraphTraversalBuilder(Loader loader, GraphTraversalSource source) {
		super(loader, source);
		
		traversal = new DefaultGraphTraversal<>();
		
	}
	
	/**
	 * Instantiates a new graph traversal builder.
	 *
	 * @param loader the loader
	 * @param start the start
	 */
	public GraphTraversalBuilder(Loader loader, GraphTraversalSource source, Vertex start) {
		super(loader, source, start);
		
		traversal = new DefaultGraphTraversal<>();
		
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public QueryBuilder<Vertex> getVerticesByIndexedProperty(String key, Object value) {
	
		return this.getVerticesByProperty(key, value);
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public QueryBuilder<Vertex> getVerticesByIndexedProperty(String key, List<?> values) {
		return this.getVerticesByProperty(key, values);
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	public QueryBuilder<Vertex> getVerticesByProperty(String key, Object value) {
		
		//this is because the index is registered as an Integer
		value = this.correctObjectType(value);
		
		traversal.has(key, value);
		
		stepIndex++;
		return (QueryBuilder<Vertex>) this;
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	public QueryBuilder<Vertex> getVerticesByProperty(final String key, final List<?> values) {
		
		//this is because the index is registered as an Integer
		List<Object> correctedValues = new ArrayList<>();
		for (Object item : values) {
			correctedValues.add(this.correctObjectType(item));
		}
		
		traversal.has(key, P.within(correctedValues));
		
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
	public QueryBuilder<Vertex> getTypedVerticesByMap(String type, LinkedHashMap<String, String> map) {
		
		for (String key : map.keySet()) {
			traversal.has(key, map.get(key));
			stepIndex++;
		}
		traversal.has(AAIProperties.NODE_TYPE, type);
		stepIndex++;
		return (QueryBuilder<Vertex>) this;
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public QueryBuilder<Vertex> createDBQuery(Introspector obj) {
		this.createKeyQuery(obj);
		this.createContainerQuery(obj);
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
				//use the db name for the field rather than the object model
				key = metadata.get();
			}
			if (val != null) {
				//this is because the index is registered as an Integer
				if (val.getClass().equals(Long.class)) {
					traversal.has(key,new Integer(val.toString()));
				} else {
					traversal.has(key, val);
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
						//use the db name for the field rather than the object model
						prop = metadata.get();
					}
					//this is because the index is registered as an Integer
					if (val != null && val.getClass().equals(Long.class)) {
						traversal.has(prop,new Integer(val.toString()));
					} else {
						traversal.has(prop, val);
					}
					stepIndex++;
				}
			}
		}
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
	public QueryBuilder<Vertex> createEdgeTraversal(EdgeType type, Introspector parent, Introspector child) throws AAIException, NoEdgeRuleFoundException {
		String isAbstractType = parent.getMetadata(ObjectMetadata.ABSTRACT);
		if ("true".equals(isAbstractType)) {
			markParentBoundary();
			traversal.union(handleAbstractEdge(type, parent, child));
			stepIndex += 1;
		} else {
			this.edgeQueryToVertex(type, parent, child);
		}
		return (QueryBuilder<Vertex>) this;
			
	}
	
	private Traversal<Vertex, Vertex>[] handleAbstractEdge(EdgeType type, Introspector abstractParent, Introspector child) throws AAIException, NoEdgeRuleFoundException {
		String childName = child.getDbName();
		String inheritorMetadata = abstractParent.getMetadata(ObjectMetadata.INHERITORS);
		String[] inheritors = inheritorMetadata.split(",");
		Traversal<Vertex, Vertex>[] unionTraversals = new Traversal[inheritors.length];
		int traversalIndex = 0;
		for (int i = 0; i < inheritors.length; i++) {
			String inheritor = inheritors[i];
			if (edgeRules.hasEdgeRule(inheritor, childName) || edgeRules.hasEdgeRule(childName, inheritor)) {
				EdgeRule rule = edgeRules.getEdgeRule(type, inheritor, childName);
				GraphTraversal<Vertex, Vertex> innerTraversal = __.start();
				if (rule.getDirection().equals(Direction.OUT)) {
					innerTraversal.out(rule.getLabel());
				} else {
					innerTraversal.in(rule.getLabel());
				}
				innerTraversal.has(AAIProperties.NODE_TYPE, childName);
				unionTraversals[traversalIndex] = innerTraversal;
				traversalIndex++;
			}
		}
		if (traversalIndex < inheritors.length) {
			Traversal<Vertex, Vertex>[] temp = Arrays.copyOfRange(unionTraversals, 0, traversalIndex);
			unionTraversals = temp;
		}
		return unionTraversals;
	}
	/**
	 * @throws NoEdgeRuleFoundException 
	 * @throws AAIException 
	 * @{inheritDoc}
	 */
	@Override
	public QueryBuilder<Vertex> createEdgeTraversal(EdgeType type, Vertex parent, Introspector child) throws AAIException, NoEdgeRuleFoundException {
		
		String nodeType = parent.<String>property(AAIProperties.NODE_TYPE).orElse(null);
		Introspector parentObj = loader.introspectorFromName(nodeType);
		this.edgeQueryToVertex(type, parentObj, child);
		return (QueryBuilder<Vertex>) this;
			
	}
	
	@Override
	public QueryBuilder<Edge> getEdgesBetween(EdgeType type, String outNodeType, String inNodeType) throws AAIException {
		Introspector outObj = loader.introspectorFromName(outNodeType);
		Introspector inObj = loader.introspectorFromName(inNodeType);
		this.edgeQuery(type, outObj, inObj);
		
		return (QueryBuilder<Edge>)this;

	}
	/**
	 * @{inheritDoc}
	 */
	@Override
	public QueryBuilder<E> union(QueryBuilder... builder) {
		GraphTraversal<Vertex, Vertex>[] traversals = new GraphTraversal[builder.length];
		for (int i = 0; i < builder.length; i++) {
			traversals[i] = (GraphTraversal<Vertex, Vertex>)builder[i].getQuery();
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
		GraphTraversal<Vertex, Vertex>[] traversals = new GraphTraversal[builder.length];
		for (int i = 0; i < builder.length; i++) {
			this.traversal.where((GraphTraversal<Vertex, Vertex>)builder[i].getQuery());
			stepIndex++;
		}
		
		return this;
	}
	
	@Override
	public QueryBuilder<E> store(String name) {
		
		this.traversal.store(name);
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
		
		this.traversal.repeat((GraphTraversal<Vertex, E>)builder.getQuery());
		stepIndex++;

		return this;
	}
	
	@Override
	public QueryBuilder<Edge> outE() {
		this.traversal.outE();
		stepIndex++;
		return (QueryBuilder<Edge>)this;
	}
	
	@Override
	public QueryBuilder<Edge> inE() {
		this.traversal.inE();
		stepIndex++;
		return (QueryBuilder<Edge>)this;
	}
	
	@Override
	public QueryBuilder<Vertex> outV() {
		this.traversal.outV();
		stepIndex++;
		return (QueryBuilder<Vertex>)this;
	}
	
	@Override
	public QueryBuilder<Vertex> inV() {
		this.traversal.inV();
		stepIndex++;
		return (QueryBuilder<Vertex>)this;
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
	
	/**
	 * Edge query.
	 *
	 * @param outType the out type
	 * @param inType the in type
	 * @throws NoEdgeRuleFoundException 
	 * @throws AAIException 
	 */
	private void edgeQueryToVertex(EdgeType type, Introspector outObj, Introspector inObj) throws AAIException, NoEdgeRuleFoundException {
		String outType = outObj.getDbName();
		String inType = inObj.getDbName();
		
		if (outObj.isContainer()) {
			outType = outObj.getChildDBName();
		}
		if (inObj.isContainer()) {
			inType = inObj.getChildDBName();
		}
		markParentBoundary();
		EdgeRule rule = edgeRules.getEdgeRule(type, outType, inType);
		if (rule.getDirection().equals(Direction.OUT)) {
			traversal.out(rule.getLabel());
		} else {
			traversal.in(rule.getLabel());
		}
		stepIndex++;
		this.createContainerQuery(inObj);
		
	}
	
	/**
	 * Edge query.
	 *
	 * @param outType the out type
	 * @param inType the in type
	 * @throws NoEdgeRuleFoundException 
	 * @throws AAIException 
	 */
	private void edgeQuery(EdgeType type, Introspector outObj, Introspector inObj) throws AAIException, NoEdgeRuleFoundException {
		String outType = outObj.getDbName();
		String inType = inObj.getDbName();
		
		if (outObj.isContainer()) {
			outType = outObj.getChildDBName();
		}
		if (inObj.isContainer()) {
			inType = inObj.getChildDBName();
		}
		markParentBoundary();
		EdgeRule rule = edgeRules.getEdgeRule(type, outType, inType);
		if (rule.getDirection().equals(Direction.OUT)) {
			traversal.outE(rule.getLabel());
		} else {
			traversal.inE(rule.getLabel());
		}
		stepIndex++;
		
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
		return (E2)this.traversal;
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
		
		if (this.parentStepIndex == 0) {
			return removeQueryStepsBetween(0, containerStepIndex);
		} else {
			return cloneQueryAtStep(containerStepIndex);
		}
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

	protected abstract QueryBuilder<E> cloneQueryAtStep(int index);
	/**
	 * end is exclusive
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	protected abstract QueryBuilder<E> removeQueryStepsBetween(int start, int end);
	
	private void executeQuery() {
		
		Admin<Vertex, Vertex> admin;
		if (start != null) {
			admin = source.V(start).asAdmin();
		} else {
			admin = source.V().asAdmin();

		}
		
		TraversalHelper.insertTraversal(admin.getEndStep(), traversal.asAdmin(), admin);
		
		this.completeTraversal = (Admin<Vertex, E>) admin;
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

}
