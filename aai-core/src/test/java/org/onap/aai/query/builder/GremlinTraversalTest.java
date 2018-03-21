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
package org.onap.aai.query.builder;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class GremlinTraversalTest extends QueryBuilderTestAbstraction {
	
	@Override
	protected QueryBuilder<Edge> getNewEdgeTraversal(Vertex v) {
		return new GremlinTraversal<>(loader, g, v, testEdgeRules);
	}
	
	@Override
	protected QueryBuilder<Edge> getNewEdgeTraversal() {
		return new GremlinTraversal<>(loader, g, testEdgeRules);
	}
	
	@Override
	protected QueryBuilder<Vertex> getNewVertexTraversal(Vertex v) {
		return new GremlinTraversal<>(loader, g, v, testEdgeRules);
	}
	
	@Override
	protected QueryBuilder<Vertex> getNewVertexTraversal() {
		return new GremlinTraversal<>(loader, g, testEdgeRules);
	}

		
}
