/*-
 * ============LICENSE_START=======================================================
 * org.openecomp.aai
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.aai.query.builder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.tinkerpop.gremlin.process.traversal.Step;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Introspector;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.parsers.query.QueryParser;
import org.openecomp.aai.parsers.query.TraversalStrategy;

/**
 * The Class TraversalQuery.
 */
public class TraversalQuery extends GraphTraversalBuilder {

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
	
	protected TraversalQuery(GraphTraversal<Vertex, Vertex> traversal, Loader loader, GraphTraversalSource source, GraphTraversalBuilder gtb) {
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
		return new TraversalQuery(loader, source, start);
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	public QueryBuilder newInstance() {
		return new TraversalQuery(loader, source);
	}
	
	@Override
	protected QueryBuilder cloneQueryAtStep(int index) {
		if (index == 0) {
			index = stepIndex;
		}
		GraphTraversal<Vertex, Vertex> clone = this.traversal.asAdmin().clone();
		GraphTraversal.Admin<Vertex, Vertex> cloneAdmin = clone.asAdmin();
		List<Step> steps = cloneAdmin.getSteps();

		for (int i = steps.size()-1; i >= index; i--) {
			cloneAdmin.removeStep(i);
		}
		return new TraversalQuery(cloneAdmin, loader, source, this);
	}
	
	@Override
	protected QueryBuilder removeQueryStepsBetween(int start, int end) {
		GraphTraversal<Vertex, Vertex> clone = this.traversal.asAdmin().clone();
		GraphTraversal.Admin<Vertex, Vertex> cloneAdmin = clone.asAdmin();

		for (int i = end-2; i >= start; i--) {
			cloneAdmin.removeStep(i);
		}
		return new TraversalQuery(cloneAdmin, loader, source, this);
	}
}
