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

import java.io.UnsupportedEncodingException;
import java.net.URI;

import javax.ws.rs.core.MultivaluedMap;

import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.query.builder.QueryBuilder;

/**
 * The Class QueryParserStrategy.
 */
public abstract class QueryParserStrategy {

    protected Loader loader = null;

    protected QueryBuilder builder = null;

    /**
     * Instantiates a new query parser strategy.
     *
     * @param loader the loader
     * @param builder the builder
     */
    public QueryParserStrategy(Loader loader, QueryBuilder builder) {

        this.loader = loader;
        this.builder = builder;
    }

    /**
     * Builds the URI parser.
     *
     * @param uri the uri
     * @return the query parser
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    public abstract QueryParser buildURIParser(URI uri) throws UnsupportedEncodingException, AAIException;

    /**
     * Builds the URI parser.
     *
     * @param uri the uri
     * @param queryParams the query params
     * @return the query parser
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    public abstract QueryParser buildURIParser(URI uri, MultivaluedMap<String, String> queryParams)
            throws UnsupportedEncodingException, AAIException;

    /**
     * Builds the relationship parser.
     *
     * @param obj the obj
     * @return the query parser
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    public abstract QueryParser buildRelationshipParser(Introspector obj)
            throws UnsupportedEncodingException, AAIException;

    /**
     * Builds an ObjectNameQueryParser.
     * 
     * @param objName - the name of the object type as used in the database
     * @return
     */
    public abstract QueryParser buildObjectNameParser(String objName);
}
