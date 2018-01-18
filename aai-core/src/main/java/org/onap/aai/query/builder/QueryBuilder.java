/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai.query.builder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.parsers.query.QueryParserStrategy;
import org.onap.aai.serialization.db.EdgeType;

/**
 * The Class QueryBuilder.
 */
public abstract class QueryBuilder<E> implements Iterator<E> {

	protected final GraphTraversalSource source;
	protected QueryParserStrategy factory = null;
	protected Loader loader = null;
	protected boolean optimize = false;
	protected Vertex start = null;
	
	/**
	 * Instantiates a new query builder.
	 *
	 * @param loader the loader
	 */
	public QueryBuilder(Loader loader, GraphTraversalSource source) {
		this.loader = loader;
		this.source = source;
	}
	
	/**
	 * Instantiates a new query builder.
	 *
	 * @param loader the loader
	 * @param start the start
	 */
	public QueryBuilder(Loader loader, GraphTraversalSource source, Vertex start) {
		this.loader = loader;
		this.start = start;
		this.source = source;
	}
	
	/**
	 * Gets the vertices by indexed property.
	 *
	 * @param key the key
	 * @param value the value
	 * @return the vertices by indexed property
	 */
	public QueryBuilder<Vertex> getVerticesByIndexedProperty(String key, Object value) {
		return this.getVerticesByProperty(key, value);
	}
	
	/**
	 * Gets the vertices by property.
	 *
	 * @param key the key
	 * @param value the value
	 * @return the vertices by property
	 */
	public abstract QueryBuilder<Vertex> getVerticesByProperty(String key, Object value);
	
	/**
	 * filters by all the values for this property
	 * @param key
	 * @param values
	 * @return vertices that match these values
	 */
	public QueryBuilder<Vertex> getVerticesByIndexedProperty(String key, List<?> values) {
		return this.getVerticesByProperty(key, values);
	}

	/**
	 * filters by all the values for this property
	 * @param key
	 * @param values
	 * @return vertices that match these values
	 */
	public abstract QueryBuilder<Vertex> getVerticesByProperty(String key, List<?> values);

	/**
	 * Gets the vertices that are excluded by property.
	 *
	 * @param key the key
	 * @param value the value
	 * @return the vertices by property
	 */
	public abstract QueryBuilder<Vertex> getVerticesExcludeByProperty(String key, Object value);

	/**
	 * filters by all the values for this property and excludes the vertices
	 * @param key
	 * @param values
	 * @return vertices that match these values
	 */
	public QueryBuilder<Vertex> getVerticesExcludeByIndexedProperty(String key, List<?> values) {
		return this.getVerticesExcludeByProperty(key, values);
	}

	/**
	 * filters by all the values for this property and excludes the vertices
	 * @param key
	 * @param values
	 * @return vertices that match these values
	 */
	public abstract QueryBuilder<Vertex> getVerticesExcludeByProperty(String key, List<?> values);

	/**
	 * Gets the child vertices from parent.
	 *
	 * @param parentKey the parent key
	 * @param parentValue the parent value
	 * @param childType the child type
	 * @return the child vertices from parent
	 */
	public abstract QueryBuilder<Vertex> getChildVerticesFromParent(String parentKey, String parentValue, String childType);
		
	/**
	 * Gets the typed vertices by map.
	 *
	 * @param type the type
	 * @param map the map
	 * @return the typed vertices by map
	 */
	public abstract QueryBuilder<Vertex> getTypedVerticesByMap(String type, Map<String, String> map);

	/**
	 * Creates the DB query.
	 *
	 * @param obj the obj
	 * @return the query builder
	 */
	public QueryBuilder<Vertex> createDBQuery(Introspector obj) {
		this.createKeyQuery(obj);
		this.createContainerQuery(obj);
		return (QueryBuilder<Vertex>) this;
	}
	
	/**
	 * Creates the key query.
	 *
	 * @param obj the obj
	 * @return the query builder
	 */
	public abstract QueryBuilder<Vertex> createKeyQuery(Introspector obj);
	
	/**
	 * Creates the container query.
	 *
	 * @param obj the obj
	 * @return the query builder
	 */
	public abstract QueryBuilder<Vertex> createContainerQuery(Introspector obj);
	
	/**
	 * Creates the edge traversal.
	 *
	 * @param parent the parent
	 * @param child the child
	 * @return the query builder
	 */
	public abstract QueryBuilder<Vertex> createEdgeTraversal(EdgeType type, Introspector parent, Introspector child) throws AAIException;
	
	/**
	 * Creates the edge traversal.
	 *
	 * @param parent the parent
	 * @param child the child
	 * @return the query builder
	 */
	public QueryBuilder<Vertex> createEdgeTraversal(EdgeType type, Vertex parent, Introspector child) throws AAIException {
		String nodeType = parent.<String>property(AAIProperties.NODE_TYPE).orElse(null);
		this.createEdgeTraversal(type, nodeType, child.getDbName());
		return (QueryBuilder<Vertex>) this;

	}

	/**
	 *
	 * @param type
	 * @param outNodeType
	 * @param inNodeType
	 * @return
	 * @throws AAIException
	 */
	public QueryBuilder<Vertex> createEdgeTraversal(EdgeType type, String outNodeType, String inNodeType) throws AAIException {
		Introspector out = loader.introspectorFromName(outNodeType);
		Introspector in = loader.introspectorFromName(inNodeType);

		return createEdgeTraversal(type, out, in);
	}

	/**
	 *
	 * @param type
	 * @param outNodeType
	 * @param inNodeType
	 * @param labels
	 * @return
	 * @throws AAIException
	 */
	public QueryBuilder<Vertex> createEdgeTraversalWithLabels(EdgeType type, String outNodeType, String inNodeType, List<String> labels) throws AAIException {
		Introspector out = loader.introspectorFromName(outNodeType);
		Introspector in = loader.introspectorFromName(inNodeType);

		return createEdgeTraversalWithLabels(type, out, in, labels);
	}

	/**
	 *
	 * @param type
	 * @param out
	 * @param in
	 * @param labels
	 * @return
	 */
	public abstract QueryBuilder<Vertex> createEdgeTraversalWithLabels(EdgeType type, Introspector out, Introspector in, List<String> labels) throws AAIException;

	/**
	 *
	 * @param type
	 * @param outNodeType
	 * @param inNodeType
	 * @return
	 * @throws AAIException
	 */
	public QueryBuilder<Edge> getEdgesBetween(EdgeType type, String outNodeType, String inNodeType) throws AAIException {
		this.getEdgesBetweenWithLabels(type, outNodeType, inNodeType, null);

		return (QueryBuilder<Edge>)this;

	}
	/**
	 *
	 * @param type
	 * @param outNodeType
	 * @param inNodeType
	 * @param labels
	 * @return
	 * @throws AAIException
	 */
	public abstract QueryBuilder<Edge> getEdgesBetweenWithLabels(EdgeType type, String outNodeType, String inNodeType, List<String> labels) throws AAIException;

	/**
	 * Creates the query from URI.
	 *
	 * @param uri the uri
	 * @return the query parser
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 * @throws AAIException the AAI exception
	 */
	public abstract QueryParser createQueryFromURI(URI uri) throws UnsupportedEncodingException, AAIException;
	
	/**
	 * Creates the query from URI.
	 *
	 * @param uri the uri
	 * @param queryParams the query params
	 * @return the query parser
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 * @throws AAIException the AAI exception
	 */
	public abstract QueryParser createQueryFromURI(URI uri, MultivaluedMap<String, String> queryParams) throws UnsupportedEncodingException, AAIException;

	/**
	 * Creates a queryparser from a given object name.
	 * 
	 * @param objName - name of the object type as it appears in the database
	 * @return
	 */
	public abstract QueryParser createQueryFromObjectName(String objName);
	
	/**
	 * Creates the query from relationship.
	 *
	 * @param relationship the relationship
	 * @return the query parser
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 * @throws AAIException the AAI exception
	 */
	public abstract QueryParser createQueryFromRelationship(Introspector relationship) throws UnsupportedEncodingException, AAIException;

	/**
	 * Gets the parent query.
	 *
	 * @return the parent query
	 */
	public abstract QueryBuilder<E> getParentQuery();
	
	/**
	 * Gets the query.
	 *
	 * @return the query
	 */
	public abstract <E2> E2 getQuery();
	
	/**
	 * Form boundary.
	 */
	public abstract void markParentBoundary();
	
	public abstract QueryBuilder<E> limit(long amount);

	/**
	 * New instance.
	 *
	 * @param start the start
	 * @return the query builder
	 */
	public abstract QueryBuilder<E> newInstance(Vertex start);
	
	/**
	 * New instance.
	 *
	 * @return the query builder
	 */
	public abstract QueryBuilder<E> newInstance();
	
	/**
	 * Gets the start.
	 *
	 * @return the start
	 */
	public abstract Vertex getStart();

	protected Object correctObjectType(Object obj) {
		
		if (obj != null && obj.getClass().equals(Long.class)) {
			return new Integer(obj.toString());
		}
		
		return obj;
	}
	/**
	 * uses all fields in the introspector to create a query
	 * 
	 * @param obj
	 * @return
	 */
	public abstract QueryBuilder<Vertex> exactMatchQuery(Introspector obj);

	/**
	 * lets you join any number of QueryBuilders
	 * <b>be careful about starting with a union it will not use indexes</b>
	 * @param builder
	 * @return
	 */
	public abstract QueryBuilder<E> union(QueryBuilder<E>... builder);
	
	public abstract QueryBuilder<E> where(QueryBuilder<E>... builder);
	
	public abstract QueryBuilder<E> store(String name);
	public abstract QueryBuilder<E> cap(String name);
	public abstract QueryBuilder<E> unfold();
	public abstract QueryBuilder<E> dedup();
	public abstract QueryBuilder<E> emit();
	public abstract QueryBuilder<E> repeat(QueryBuilder<E> builder);
	public abstract QueryBuilder<Edge> outE();
	public abstract QueryBuilder<Edge> inE();
	public abstract QueryBuilder<Vertex> inV();
	public abstract QueryBuilder<Vertex> outV();
	public abstract QueryBuilder<E> not(QueryBuilder<E> builder);
	public abstract QueryBuilder<E> as(String name);
	public abstract QueryBuilder<E> select(String name);
	public abstract QueryBuilder<E> until(QueryBuilder<E> builder);
	public abstract QueryBuilder<E> groupCount();
	public abstract QueryBuilder<E> by(String name);
	public abstract QueryBuilder<E> both();
	
	/**
	 * Used to prevent the traversal from repeating its path through the graph.
	 * See http://tinkerpop.apache.org/docs/3.0.1-incubating/#simplepath-step for more info.
	 * 
	 * @return a QueryBuilder with the simplePath step appended to its traversal
	 */
	public abstract QueryBuilder<E> simplePath();
	
 	public abstract void markContainer();

	public abstract QueryBuilder<E> getContainerQuery();

	public abstract List<E> toList();
	
	public QueryBuilder<Vertex> getVerticesByProperty(String key, MissingOptionalParameter value) {
		return (QueryBuilder<Vertex>) this;
	}

		
}
