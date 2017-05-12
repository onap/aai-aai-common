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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Introspector;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.parsers.query.QueryParser;
import org.openecomp.aai.parsers.query.TraversalStrategy;

/**
 * The Class GremlinTraversal.
 */
public class GremlinTraversal extends GremlinQueryBuilder {

	/**
	 * Instantiates a new gremlin traversal.
	 *
	 * @param loader the loader
	 */
	public GremlinTraversal(Loader loader, GraphTraversalSource source) {
		super(loader, source);
		this.factory = new TraversalStrategy(this.loader, this);
	}
	
	/**
	 * Instantiates a new gremlin traversal.
	 *
	 * @param loader the loader
	 * @param start the start
	 */
	public GremlinTraversal(Loader loader, GraphTraversalSource source, Vertex start) {
		super(loader, source, start);
		this.factory = new TraversalStrategy(this.loader, this);
	}

	protected GremlinTraversal(List<String> traversal, Loader loader, GraphTraversalSource source, GremlinQueryBuilder gtb) {
		super(loader, source);
		this.list = traversal;
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
	public QueryParser createQueryFromRelationship(Introspector relationship) throws UnsupportedEncodingException, AAIException {
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
	public QueryBuilder newInstance(Vertex start) {
		return new GremlinTraversal(loader, source, start);
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public QueryBuilder newInstance() {
		return new GremlinTraversal(loader, source);
	}
	
	@Override
	protected QueryBuilder cloneQueryAtStep(int index) {
		if (index == 0) {
			index = stepIndex;
		}
		List<String> newList = new ArrayList<>();
		for (int i = 0; i < index; i++) {
			newList.add(this.list.get(i));
		}
		
		return new GremlinTraversal(newList, loader, source, this);
	}
}
