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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.serialization.engines.query;/*-
                                                 * ============LICENSE_START=======================================================
                                                 * org.onap.aai
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

/*
 * package org.onap.aai.serialization.engines.query;
 * 
 * import java.util.HashSet;
 * import java.util.List;
 * import java.util.Set;
 * 
 * import org.onap.aai.db.AAIProperties;
 * import org.onap.aai.query.builder.QueryBuilder;
 * import org.onap.aai.serialization.engines.TransactionalGraphEngine;
 * import com.tinkerpop.blueprints.Direction;
 * import com.tinkerpop.blueprints.TransactionalGraph;
 * import com.tinkerpop.blueprints.Vertex;
 * import com.tinkerpop.gremlin.java.GremlinPipeline;
 * import com.tinkerpop.pipes.IdentityPipe;
 * import com.tinkerpop.pipes.PipeFunction;
 * import com.tinkerpop.pipes.branch.LoopPipe;
 * 
 * public class GremlinPipelineQueryEngine extends QueryEngine {
 * 
 * public GremlinPipelineQueryEngine(TransactionalGraphEngine graphEngine) {
 * super(graphEngine);
 * }
 * 
 * @Override
 * public List<Vertex> executeQuery(TransactionalGraph g, QueryBuilder query) {
 * List<Vertex> results = null;
 * Vertex start = query.getStart();
 * if (start != null) {
 * results = ((GremlinPipeline)query.getQuery()).cast(Vertex.class).toList();
 * } else {
 * GremlinPipeline pipe = new GremlinPipeline(g);
 * results = process(pipe, (GremlinPipeline)query.getQuery());
 * 
 * }
 * return results;
 * }
 * 
 * @Override
 * public List<Vertex> executeParentQuery(TransactionalGraph g, QueryBuilder query) {
 * List<Vertex> results = null;
 * Vertex start = query.getStart();
 * if (start != null) {
 * results = ((GremlinPipeline)query.getParentQuery()).cast(Vertex.class).toList();
 * } else {
 * GremlinPipeline pipe = new GremlinPipeline(g);
 * results = process(pipe, (GremlinPipeline)query.getParentQuery());
 * 
 * }
 * return results;
 * }
 * 
 * @Override
 * public List<Vertex> findParents(Vertex start) {
 * GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline(start).as("x").inE()
 * .has("isParent", true).outV().loop("x", new PipeFunction<LoopPipe.LoopBundle<Vertex>, Boolean>()
 * {
 * 
 * @Override
 * public Boolean compute(LoopPipe.LoopBundle<Vertex> argument) {
 * GremlinPipeline<Vertex, Long> pipe = new GremlinPipeline<>(argument.getObject());
 * return pipe.inE().has("isParent", true).count() == 1 || argument.getLoops() < 100;
 * }
 * 
 * }, new PipeFunction<LoopPipe.LoopBundle<Vertex>, Boolean>() {
 * 
 * @Override
 * public Boolean compute(LoopPipe.LoopBundle<Vertex> argument) {
 * return true;
 * }
 * 
 * });
 * 
 * List<Vertex> results = pipe.toList();
 * results.add(0, start);
 * return results;
 * }
 * 
 * @Override
 * public List<Vertex> findChildren(Vertex start) {
 * Set<Vertex> seen = new HashSet<>();
 * seen.add(start);
 * GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline(start).as("x").outE().has("isParent",
 * true).inV()
 * .except(seen).store(seen).loop("x", new PipeFunction<LoopPipe.LoopBundle<Vertex>, Boolean>() {
 * 
 * @Override
 * public Boolean compute(LoopPipe.LoopBundle<Vertex> argument) {
 * GremlinPipeline<Vertex, Long> pipe = new GremlinPipeline<>(argument.getObject());
 * return pipe.outE().has("isParent", true).count() >= 1 || argument.getLoops() < 100;
 * }
 * 
 * }, new PipeFunction<LoopPipe.LoopBundle<Vertex>, Boolean>() {
 * 
 * @Override
 * public Boolean compute(LoopPipe.LoopBundle<Vertex> argument) {
 * return true;
 * }
 * 
 * });
 * 
 * List<Vertex> results = pipe.toList();
 * results.add(0, start);
 * return results;
 * }
 * 
 * @Override
 * public List<Vertex> findDeletable(Vertex start) {
 * Set<Vertex> seen = new HashSet<>();
 * seen.add(start);
 * GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<Vertex,
 * Vertex>(start).as("x").outE().or(
 * new GremlinPipeline(new IdentityPipe()).has("isParent", true),
 * new GremlinPipeline(new IdentityPipe()).has("hasDelTarget", true)).inV()
 * .except(seen).store(seen).loop("x", new PipeFunction<LoopPipe.LoopBundle<Vertex>, Boolean>() {
 * 
 * @Override
 * public Boolean compute(LoopPipe.LoopBundle<Vertex> argument) {
 * GremlinPipeline<Vertex, Long> pipe = new GremlinPipeline<>(argument.getObject());
 * return pipe.outE().or(
 * new GremlinPipeline(new IdentityPipe()).has("isParent", true),
 * new GremlinPipeline(new IdentityPipe()).has("hasDelTarget", true)).count() >= 1 ||
 * argument.getLoops() < 100;
 * }
 * 
 * }, new PipeFunction<LoopPipe.LoopBundle<Vertex>, Boolean>() {
 * 
 * @Override
 * public Boolean compute(LoopPipe.LoopBundle<Vertex> argument) {
 * return true;
 * }
 * 
 * });
 * List<Vertex> results = pipe.toList();
 * results.add(0, start);
 * 
 * return results;
 * }
 * 
 * private List<Vertex> process(GremlinPipeline start, GremlinPipeline pipe) {
 * 
 * 
 * return start.add(pipe).cast(Vertex.class).toList();
 * }
 * 
 * @Override
 * public List<Vertex> findRelatedVertices(Vertex start, Direction direction, String label, String
 * nodeType) {
 * GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<Vertex, Vertex>(start);
 * switch (direction) {
 * case OUT:
 * pipe.out(label);
 * break;
 * case IN:
 * pipe.in(label);
 * break;
 * case BOTH:
 * pipe.both(label);
 * break;
 * default:
 * break;
 * }
 * 
 * pipe.has(AAIProperties.NODE_TYPE, nodeType).dedup();
 * List<Vertex> result = pipe.toList();
 * return result;
 * }
 * 
 * }
 */
