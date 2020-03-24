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
import org.onap.aai.parsers.relationship.RelationshipToURI;
import org.onap.aai.query.builder.QueryBuilder;

import java.io.UnsupportedEncodingException;

/**
 * The Class UniqueRelationshipQueryParser.
 */
public class UniqueRelationshipQueryParser extends UniqueURIQueryParser {

    /**
     * Instantiates a new unique relationship query parser.
     *
     * @param loader the loader
     * @param queryBuilder the query builder
     * @param obj the obj
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws AAIException the AAI exception
     */
    public UniqueRelationshipQueryParser(Loader loader, QueryBuilder queryBuilder, Introspector obj)
            throws UnsupportedEncodingException, IllegalArgumentException, AAIException {
        super(loader, queryBuilder);
        RelationshipToURI rToUri = new RelationshipToURI(loader, obj);
        UniqueURIQueryParser parser = new UniqueURIQueryParser(loader, queryBuilder, rToUri.getUri());
        this.containerResource = parser.getContainerType();
        this.resultResource = parser.getResultType();
        this.queryBuilder = parser.getQueryBuilder();
        this.parentQueryBuilder = parser.getParentQueryBuilder();
    }

}
