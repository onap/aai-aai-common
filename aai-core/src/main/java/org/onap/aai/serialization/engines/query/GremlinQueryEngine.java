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

//
// package org.onap.aai.serialization.engines.query;
//
// import java.util.List;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
//
// import org.apache.commons.collections.IteratorUtils;
//
// import org.onap.aai.db.AAIProperties;
// import org.onap.aai.query.builder.QueryBuilder;
// import org.onap.aai.serialization.engines.TransactionalGraphEngine;
// import com.tinkerpop.blueprints.Direction;
// import com.tinkerpop.blueprints.Graph;
// import com.tinkerpop.blueprints.TransactionalGraph;
// import com.tinkerpop.blueprints.Vertex;
// import com.tinkerpop.gremlin.groovy.Gremlin;
// import com.tinkerpop.gremlin.java.GremlinPipeline;
// import com.tinkerpop.pipes.Pipe;
// import com.tinkerpop.pipes.util.iterators.SingleIterator;
//
// public class GremlinQueryEngine extends QueryEngine {
//
// public GremlinQueryEngine (TransactionalGraphEngine engine) {
// super(engine);
//
// }
//
//
// @Override
// public List<Vertex> executeQuery(TransactionalGraph g, QueryBuilder query) {
// List<Vertex> result = null;
// Vertex start = query.getStart();
// if (start != null) {
// result = this.executeQuery(start, (String)query.getQuery());
// } else {
// result = this.processGremlinQuery((String)query.getQuery());
// }
// return result;
//
// }
//
// @Override
// public List<Vertex> executeParentQuery(TransactionalGraph g, QueryBuilder query) {
//
// List<Vertex> result = null;
// Vertex start = query.getStart();
// if (start != null) {
// result = this.executeQuery(start, (String)query.getParentQuery());
// } else {
// result = this.processGremlinQuery((String)query.getParentQuery());
// }
// return result;
// }
//
// private List<Vertex> executeQuery(Vertex startVertex, String query) {
//
// return this.processGremlinQuery(startVertex, "_()" + query);
//
// }
//
// @Override
// public List<Vertex> findParents(Vertex start) {
//
// String findAllParents = ".as('x').inE.has('isParent', true).outV"
// + ".loop('x'){it.object.inE.has('isParent',true).count()==1}{true}";
//
// List<Vertex> results = this.executeQuery(start, findAllParents);
// results.add(0, start);
// return results;
//
// }
//
// @Override
// public List<Vertex> findChildren(Vertex start) {
// String findAllChildren = ".as('x').outE.has('isParent', true).inV"
// + ".loop('x'){it.object.outE.has('isParent', true).count() >= 1}{true}";
//
// List<Vertex> results = this.executeQuery(start, findAllChildren);
// results.add(0, start);
// return results;
//
// }
//
// @Override
// public List<Vertex> findDeletable(Vertex start) {
// String findAllChildren = ".as('x').outE.or(_().has('isParent', true), _().has('hasDelTarget', true)).inV"
// + ".loop('x'){it.object.outE.or(_().has('isParent', true), _().has('hasDelTarget', true)).count() >= 1}{true}";
//
// List<Vertex> results = this.executeQuery(start, findAllChildren);
// results.add(0, start);
// return results;
// }
// private List<Vertex> processGremlinQuery(String query) {
//
// Pattern firstHasSet = Pattern.compile("^(\\.has\\(.*?\\))(\\.has\\(.*?\\))*(?!\\.has)");
// Pattern p = Pattern.compile("\\.has\\('(.*?)',\\s?'(.*?)'\\)");
// Matcher m = firstHasSet.matcher(query);
// List<Vertex> results = null;
// GremlinPipeline<Graph, Vertex> pipe = new GremlinPipeline<>(dbEngine.getGraph());
// if (m.find()) {
// String hasSet = m.group();
// query = query.replace(m.group(0), "");
// m = p.matcher(hasSet);
// pipe.V();
// while (m.find()) {
// pipe.has(m.group(1), m.group(2));
// }
// results = processGremlinQuery(pipe.toList(), "_()" + query);
// }
//
// return results;
//
// }
// private List<Vertex> processGremlinQuery(Vertex startVertex, String query) {
//
// Pipe pipe = Gremlin.compile(query);
// pipe.setStarts(new SingleIterator<Vertex>(startVertex));
//
// return (List<Vertex>)IteratorUtils.toList(pipe.iterator());
// }
// private List<Vertex> processGremlinQuery(List<Vertex> list, String query) {
//
// Pipe pipe = Gremlin.compile(query);
//
// pipe.setStarts(list);
//
// return (List<Vertex>)IteratorUtils.toList(pipe.iterator());
// }
//
//
// @Override
// public List<Vertex> findRelatedVertices(Vertex start, Direction direction, String label, String nodeType) {
// String findRelatedVertices = "_()";
// switch (direction) {
// case OUT:
// findRelatedVertices += ".out('" + label + "')";
// break;
// case IN:
// findRelatedVertices += ".in('" + label + "')";
// break;
// case BOTH:
// findRelatedVertices += ".both('" + label + "')";
// break;
// default:
// break;
// }
// findRelatedVertices += ".has('" + AAIProperties.NODE_TYPE + "', '" + nodeType + "').dedup()";
// List<Vertex> results = this.executeQuery(start, findRelatedVertices);
// results.add(0, start);
// return results;
// }
//
// }
//
