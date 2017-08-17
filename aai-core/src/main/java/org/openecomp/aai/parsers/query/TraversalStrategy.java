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

package org.openecomp.aai.parsers.query;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import javax.ws.rs.core.MultivaluedMap;

import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Introspector;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.query.builder.QueryBuilder;

/**
 * The Class TraversalStrategy.
 */
public class TraversalStrategy extends QueryParserStrategy {


	/**
	 * Instantiates a new traversal strategy.
	 *
	 * @param loader the loader
	 * @param builder the builder
	 */
	public TraversalStrategy(Loader loader, QueryBuilder builder) {
		super(loader, builder);
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	public QueryParser buildURIParser(URI uri) throws UnsupportedEncodingException, AAIException {
		return new LegacyQueryParser(loader, builder, uri);
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	public QueryParser buildRelationshipParser(Introspector obj) throws UnsupportedEncodingException, AAIException {
		return new RelationshipQueryParser(loader, builder, obj);
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public QueryParser buildURIParser(URI uri, MultivaluedMap<String, String> queryParams)
			throws UnsupportedEncodingException, AAIException {
		return new LegacyQueryParser(loader, builder, uri, queryParams);
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public QueryParser buildObjectNameParser(String objName) {
		return new ObjectNameQueryParser(loader, builder, objName);
	}


}
