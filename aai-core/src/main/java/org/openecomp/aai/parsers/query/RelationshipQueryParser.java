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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Introspector;
import org.openecomp.aai.introspection.IntrospectorFactory;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.introspection.ModelType;
import org.openecomp.aai.parsers.relationship.RelationshipToURI;
import org.openecomp.aai.parsers.uri.URIParser;
import org.openecomp.aai.query.builder.QueryBuilder;
import org.openecomp.aai.serialization.db.EdgeRules;
import com.google.common.base.CaseFormat;

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
