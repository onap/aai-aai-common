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
package org.onap.aai.parsers.query;

import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.parsers.relationship.RelationshipToURI;
import org.onap.aai.parsers.uri.URIParser;
import org.onap.aai.query.builder.QueryBuilder;
import org.onap.aai.serialization.db.EdgeRules;

import java.io.UnsupportedEncodingException;

/**
 * The Class RelationshipQueryParser.
 */
public class RelationshipQueryParser extends LegacyQueryParser {

	private Introspector relationship = null;
	
	private ModelType modelType = null;
	
	private EdgeRules edgeRules = null;
	
	/**
	 * Instantiates a new relationship query parser.
	 *
	 * @param loader the loader
	 * @param queryBuilder the query builder
	 * @param obj the obj
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 * @throws AAIException the AAI exception
	 */
	public RelationshipQueryParser(Loader loader, QueryBuilder queryBuilder, Introspector obj) throws UnsupportedEncodingException, AAIException {
		super(loader, queryBuilder);
		this.relationship = obj;
		this.modelType = obj.getModelType();
		this.edgeRules = EdgeRules.getInstance();
		RelationshipToURI rToUri = new RelationshipToURI(loader, obj);
		this.uri = rToUri.getUri();
		URIParser parser = new URIParser(loader, uri);
		parser.parse(this);
	}
	
}
