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

package org.openecomp.aai.parsers.query;

import java.net.URI;

import org.openecomp.aai.db.props.AAIProperties;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.introspection.LoaderFactory;
import org.openecomp.aai.query.builder.QueryBuilder;

/**
 * The Class QueryParser.
 */
public abstract class QueryParser {

	protected Loader loader = null;
	protected Loader latestLoader = null;
	protected QueryBuilder queryBuilder = null;
	
	protected QueryBuilder parentQueryBuilder = null;
	
	protected URI uri = null;
	
	protected String resultResource = "";
	
	protected String parentResourceType = "";
	
	protected String containerResource = "";
		
	/**
	 * Instantiates a new query parser.
	 *
	 * @param loader the loader
	 * @param queryBuilder the query builder
	 * @param uri the uri
	 */
	protected QueryParser(Loader loader, QueryBuilder queryBuilder, URI uri) {
		this.uri = uri;
		this.queryBuilder = queryBuilder;
		this.loader = loader;
		this.latestLoader = LoaderFactory.createLoaderForVersion(loader.getModelType(), AAIProperties.LATEST);
	}
	
	/**
	 * Instantiates a new query parser.
	 *
	 * @param loader the loader
	 * @param queryBuilder the query builder
	 */
	protected QueryParser(Loader loader, QueryBuilder queryBuilder) {
		this.queryBuilder = queryBuilder;
		this.loader = loader;
		this.latestLoader = LoaderFactory.createLoaderForVersion(loader.getModelType(), AAIProperties.LATEST);
	}
	
	/**
	 * Gets the container type.
	 *
	 * @return the container type
	 */
	public String getContainerType() {
		
		return this.containerResource;
	}
	
	/**
	 * Gets the parent result type.
	 *
	 * @return the parent result type
	 */
	public String getParentResultType() {
		return this.parentResourceType;
	}
	
	/**
	 * Gets the result type.
	 *
	 * @return the result type
	 */
	public String getResultType() {
		return this.resultResource;
	}
	
	/**
	 * Gets the query builder.
	 *
	 * @return the query builder
	 */
	public QueryBuilder getQueryBuilder() {
		return this.queryBuilder;
	}
	
	/**
	 * Gets the uri.
	 *
	 * @return the uri
	 */
	public URI getUri() {
		return this.uri;
	}
	
	/**
	 * Gets the parent query builder.
	 *
	 * @return the parent query builder
	 */
	public QueryBuilder getParentQueryBuilder() {
		if (this.parentQueryBuilder != null) {
			return this.parentQueryBuilder;
		} else {
			return this.queryBuilder;
		}
	}
	
	/**
	 * Checks if is dependent.
	 *
	 * @return true, if is dependent
	 */
	public boolean isDependent() {
		return !this.queryBuilder.getQuery().toString().equals(this.queryBuilder.getParentQuery().getQuery().toString());
	}

}


