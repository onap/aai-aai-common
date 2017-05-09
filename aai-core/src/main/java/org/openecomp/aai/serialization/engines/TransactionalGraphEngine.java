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

package org.openecomp.aai.serialization.engines;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.strategy.verification.ReadOnlyStrategy;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import org.openecomp.aai.dbmap.DBConnectionType;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.query.builder.GremlinTraversal;
import org.openecomp.aai.query.builder.GremlinUnique;
import org.openecomp.aai.query.builder.QueryBuilder;
import org.openecomp.aai.query.builder.TraversalQuery;
import org.openecomp.aai.serialization.db.GraphSingleton;
import org.openecomp.aai.serialization.engines.query.GraphTraversalQueryEngine;
import org.openecomp.aai.serialization.engines.query.QueryEngine;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanTransaction;
import com.thinkaurelius.titan.core.schema.TitanManagement;

public abstract class TransactionalGraphEngine {
	
	protected GraphSingleton singleton = null;
	protected QueryEngine queryEngine = null;
	protected QueryBuilder queryBuilder = null;
	protected QueryStyle style = null;
	protected final DBConnectionType connectionType;
	protected final Loader loader;
	protected TitanTransaction currentTx = null;
	protected GraphTraversalSource currentTraversal = null;
	protected GraphTraversalSource readOnlyTraversal = null;
	private final Admin admin;
	/**
	 * Instantiates a new transactional graph engine.
	 *
	 * @param style the style
	 * @param loader the loader
	 */
	public TransactionalGraphEngine (QueryStyle style, Loader loader, DBConnectionType connectionType, GraphSingleton singleton) {
		this.loader = loader;
		this.style = style;
		this.singleton = singleton;
		this.connectionType = connectionType;
		admin = new Admin();
		
	}
	
	public TransactionalGraphEngine (QueryStyle style, Loader loader) {
		this.loader = loader;
		this.style = style;
		this.connectionType = DBConnectionType.REALTIME;
		admin = new Admin();
	}

	/**
	 * Sets the list property.
	 *
	 * @param v the v
	 * @param name the name
	 * @param obj the obj
	 * @return true, if successful
	 */
	public abstract boolean setListProperty(Vertex v, String name, List<?> obj);
	
	/**
	 * Gets the list property.
	 *
	 * @param v the v
	 * @param name the name
	 * @return the list property
	 */
	public abstract List<Object> getListProperty(Vertex v, String name);
	
	/**
	 * Gets the graph.
	 *
	 * @return the graph
	 */
	private TitanGraph getGraph() {
		return singleton.getTxGraph(this.connectionType);
	}
	
	/**
	 * Gets the count.
	 *
	 * @return the count
	 */
	public AtomicInteger getCount() {
		return singleton.getCount();
	}
	
	
	/**
	 * Gets the query engine.
	 *
	 * @return the query engine
	 */
	public QueryEngine getQueryEngine() {
		QueryEngine engine = null;
		if (style.equals(QueryStyle.GREMLIN_TRAVERSAL)) {
			//this.queryEngine = new GremlinQueryEngine(this);
		} else if (style.equals(QueryStyle.GREMLIN_UNIQUE)) {
			//this.queryEngine = new GremlinQueryEngine(this);
		} else if (style.equals(QueryStyle.GREMLINPIPELINE_TRAVERSAL)) {
			//this.queryEngine = new GremlinPipelineQueryEngine(this);
		} else if (style.equals(QueryStyle.TRAVERSAL)) {
			
			return new GraphTraversalQueryEngine(this.asAdmin().getTraversalSource());
			
		} else {
			throw new IllegalArgumentException("Query Engine type not recognized");
		}
		
		return engine;
	}
	
	/**
	 * Gets the query builder.
	 *
	 * @return the query builder
	 */
	public QueryBuilder getQueryBuilder() {
		return getQueryBuilder(this.loader);
	}
	
	public QueryBuilder getQueryBuilder(Loader loader) {
		if (style.equals(QueryStyle.GREMLIN_TRAVERSAL)) {
			return new GremlinTraversal(loader, this.asAdmin().getTraversalSource());
		} else if (style.equals(QueryStyle.GREMLIN_UNIQUE)) {
			return new GremlinUnique(loader, this.asAdmin().getTraversalSource());
		} else if (style.equals(QueryStyle.GREMLINPIPELINE_TRAVERSAL)) {
			//return new GremlinPipelineTraversal(loader);
		} else if (style.equals(QueryStyle.TRAVERSAL)) {
			return new TraversalQuery(loader, this.asAdmin().getTraversalSource());
		}  else {
			throw new IllegalArgumentException("Query Builder type not recognized");
		}
		return queryBuilder;
	}
	/**
	 * Gets the query builder.
	 *
	 * @param start the start
	 * @return the query builder
	 */
	public QueryBuilder getQueryBuilder(Vertex start) {
		return getQueryBuilder(this.loader, start);
	}
	
	public QueryBuilder getQueryBuilder(Loader loader, Vertex start) {
		if (style.equals(QueryStyle.GREMLIN_TRAVERSAL)) {
			return new GremlinTraversal(loader, this.asAdmin().getTraversalSource(), start);
		} else if (style.equals(QueryStyle.GREMLIN_UNIQUE)) {
			return new GremlinUnique(loader, this.asAdmin().getTraversalSource(), start);
		} else if (style.equals(QueryStyle.GREMLINPIPELINE_TRAVERSAL)) {
			//return new GremlinPipelineTraversal(loader,start);
		} else if (style.equals(QueryStyle.TRAVERSAL)) {
			return new TraversalQuery(loader, this.asAdmin().getTraversalSource(), start);
		} else {
			throw new IllegalArgumentException("Query Builder type not recognized");
		}
		return queryBuilder;
	}
	
	public TitanTransaction startTransaction() {
		if (this.tx() == null) {
			this.currentTx = this.getGraph().newTransaction();
			this.currentTraversal = this.tx().traversal();
			this.readOnlyTraversal = this.tx().traversal(GraphTraversalSource.build().with(ReadOnlyStrategy.instance()));
		}
		return currentTx;
	}
	
	public void rollback() {
		if (this.tx() != null) {
			this.tx().rollback();
			this.currentTx = null;
			this.currentTraversal = null;
			this.readOnlyTraversal = null;
		}
	}
	public void commit() {
		if (this.tx() != null) {
			this.tx().commit();
			this.currentTx = null;
			this.currentTraversal = null;
			this.readOnlyTraversal = null;
		}
	}
	
	public TitanTransaction tx() {
		return this.currentTx;
	}
	
	public Admin asAdmin() {
		return admin;
	}
	
	public class Admin {
		
		public GraphTraversalSource getTraversalSource() {
			return currentTraversal;
		}
		public GraphTraversalSource getReadOnlyTraversalSource() {
			return readOnlyTraversal;
		}
		
		public TitanManagement getManagementSystem() {
			return getGraph().openManagement();
		}
	}
}
