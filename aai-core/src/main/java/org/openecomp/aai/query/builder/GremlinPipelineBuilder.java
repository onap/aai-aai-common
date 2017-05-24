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

/*
package org.openecomp.aai.query.builder;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.tinkerpop.gremlin.structure.Vertex;

import org.openecomp.aai.db.AAIProperties;
import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Introspector;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.serialization.db.EdgeRule;
import org.openecomp.aai.serialization.db.EdgeRules;

public abstract class GremlinPipelineBuilder extends QueryBuilder {

	private GremlinPipeline pipeline = null;
	private EdgeRules edgeRules = EdgeRules.getInstance();
	private int parentStepIndex = 0;
	private int stepIndex = 0;
	
	public GremlinPipelineBuilder(Loader loader) {
		super(loader);
		
		pipeline = new GremlinPipeline(new IdentityPipe()).V();
		
	}
	
	public GremlinPipelineBuilder(Loader loader, Vertex start) {
		super(loader, start);
		
		pipeline = new GremlinPipeline(start);
		
	}

	@Override
	public QueryBuilder getVerticesByIndexedProperty(String key, Object value) {
	
		return this.getVerticesByProperty(key, value);
	}

	@Override
	public QueryBuilder getVerticesByProperty(String key, Object value) {
		
		//this is because the index is registered as an Integer
		if (value != null && value.getClass().equals(Long.class)) {
			pipeline.has(key,new Integer(value.toString()));
		} else {
			pipeline.has(key, value);
		}
		stepIndex++;
		return this;
	}

	@Override
	public QueryBuilder getChildVerticesFromParent(String parentKey, String parentValue, String childType) {
		pipeline.has(parentKey, parentValue).has(AAIProperties.NODE_TYPE, childType);
		stepIndex++;
		return this;
	}

	@Override
	public QueryBuilder getTypedVerticesByMap(String type, LinkedHashMap<String, String> map) {
		
		for (String key : map.keySet()) {
			pipeline.has(key, map.get(key));
			stepIndex++;
		}
		pipeline.has(AAIProperties.NODE_TYPE, type);
		stepIndex++;
		return this;
	}

	@Override
	public QueryBuilder createDBQuery(Introspector obj) {
		this.createKeyQuery(obj);
		this.createContainerQuery(obj);
		return this;
	}

	@Override
	public QueryBuilder createKeyQuery(Introspector obj) {
		List<String> keys = obj.getKeys();
		Object val = null;
		for (String key : keys) {
			val = obj.getValue(key);
			//this is because the index is registered as an Integer
			if (val != null && val.getClass().equals(Long.class)) {
				pipeline.has(key,new Integer(val.toString()));
			} else {
				pipeline.has(key, val);
			}
			stepIndex++;
		}
		return this;
	}

	@Override
	
	public QueryBuilder createContainerQuery(Introspector obj) {
		String type = obj.getChildDBName();
		String abstractType = obj.getMetadata("abstract");
		if (abstractType != null) {
			String[] inheritors = obj.getMetadata("inheritors").split(",");
			GremlinPipeline[] pipes = new GremlinPipeline[inheritors.length];
			for (int i = 0; i < inheritors.length; i++) {
				pipes[i] = new GremlinPipeline(new IdentityPipe()).has(AAIProperties.NODE_TYPE, inheritors[i]);
			}
			pipeline.or(pipes);
		} else {
			pipeline.has(AAIProperties.NODE_TYPE, type);
		}
		stepIndex++;
		return this;
	}

	@Override
	public QueryBuilder createEdgeTraversal(Introspector parent, Introspector child) {
		String parentName = parent.getDbName();
		String childName = child.getDbName();
		String isAbstractType = parent.getMetadata("abstract");
		if ("true".equals(isAbstractType)) {
			formBoundary();
			pipeline.outE().has("isParent", true).inV();
		} else {
			if (parent.isContainer()) {
				parentName = parent.getChildDBName();
			}
			if (child.isContainer()) {
				childName = child.getChildDBName();
			}
			this.edgeQuery(parentName, childName);
		}
		return this;
			
	}
	
	@Override
	public QueryBuilder createEdgeTraversal(Vertex parent, Introspector child) {
		
		String nodeType = parent.getProperty(AAIProperties.NODE_TYPE);
		this.edgeQuery(nodeType, child.getDbName());
		return this;
			
	}
	
	private void edgeQuery(String outType, String inType) {
		formBoundary();
		EdgeRule rule;
		String label = "";
		try {
			rule = edgeRules.getEdgeRule(outType, inType);
			label = rule.getLabel();
		} catch (AAIException e) {
			// TODO Auto-generated catch block
		}
		pipeline = pipeline.out(label);
		stepIndex++;
	}

	@Override
	public Object getQuery() {
		return this.pipeline;
	}
	
	@Override
	public Object getParentQuery() {
		GremlinPipeline parent = new GremlinPipeline();
		if (parentStepIndex == 0) {
			parentStepIndex = stepIndex;
		}
		List<Pipe> pipes = this.pipeline.getPipes();
		//add two for the garbage identity pipes
		for (int i = 0; i < parentStepIndex + 2; i++) {
			parent.add(pipes.get(i));
		}

		return parent;
	}
	
	@Override
	public void formBoundary() {
		parentStepIndex = stepIndex;
	}
	
	
	@Override
	public Vertex getStart() {
		return this.start;
	}
	
}
*/
