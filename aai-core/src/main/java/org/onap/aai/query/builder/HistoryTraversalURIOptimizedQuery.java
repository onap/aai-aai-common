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

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Step;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.process.traversal.util.TraversalHelper;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.schema.enums.ObjectMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HistoryTraversalURIOptimizedQuery<E> extends TraversalURIOptimizedQuery {

    protected Map<Integer, String> stepToAaiUri = new HashMap<>();

    public HistoryTraversalURIOptimizedQuery(Loader loader, GraphTraversalSource source) {
        super(loader, source);
    }

    public HistoryTraversalURIOptimizedQuery(Loader loader, GraphTraversalSource source, Vertex start) {
        super(loader, source, start);
    }

    protected HistoryTraversalURIOptimizedQuery(GraphTraversal traversal, Loader loader, GraphTraversalSource source,
                                                GraphTraversalBuilder gtb) {
        super(traversal, loader, source, gtb);
    }

    protected HistoryTraversalURIOptimizedQuery(GraphTraversal traversal, Loader loader, GraphTraversalSource source,
                                                GraphTraversalBuilder gtb, Map<Integer, String> stepToAaiUri) {
        super(traversal, loader, source, gtb, stepToAaiUri);
    }

    @Override
    protected void vertexHas(String key, Object value) {
        super.vertexHas(key, value);
        touchHistoryProperties(key, value);
    }

    @Override
    protected void vertexHasNot(String key) {
        super.vertexHasNot(key);
        touchHistoryProperties(key);
    }

    @Override
    protected void vertexHas(String key) {
        super.vertexHas(key);
        touchHistoryProperties(key);
    }

    private void touchHistoryProperties(String key){
        if(key != null && !key.isEmpty() && !key.equals(AAIProperties.NODE_TYPE)) {
            traversal.where(__.properties(key));
        }
    }

    private void touchHistoryProperties(String key, Object value){
        if(key != null && !key.isEmpty() && !key.equals(AAIProperties.NODE_TYPE)) {
            traversal.where(__.properties(key).hasValue(value));
        }
    }
}
