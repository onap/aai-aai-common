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

package org.onap.aai.serialization.db;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.edges.EdgeRule;
import org.onap.aai.edges.EdgeRuleQuery;
import org.onap.aai.edges.enums.EdgeField;
import org.onap.aai.edges.enums.EdgeProperty;
import org.onap.aai.edges.enums.EdgeType;
import org.onap.aai.edges.enums.MultiplicityRule;
import org.onap.aai.edges.exceptions.AmbiguousRuleChoiceException;
import org.onap.aai.edges.exceptions.EdgeRuleNotFoundException;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.serialization.db.exceptions.EdgeMultiplicityException;
import org.onap.aai.serialization.db.exceptions.MultipleEdgeRuleFoundException;
import org.onap.aai.serialization.db.exceptions.NoEdgeRuleFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

@Component
public class EdgeSerializer {

    @Autowired
    private EdgeIngestor edgerules;

    public EdgeSerializer(EdgeIngestor ei) {
        this.edgerules = ei;
    }

    /**
     * Adds the tree edge.
     *
     * @param aVertex the out vertex
     * @param bVertex the in vertex
     * @return the edge
     * @throws AAIException the AAI exception
     */
    public Edge addTreeEdge(GraphTraversalSource traversalSource, Vertex aVertex, Vertex bVertex) throws AAIException {
        return this.addEdge(EdgeType.TREE, traversalSource, aVertex, bVertex, false, null);
    }

    /**
     * Adds the edge.
     *
     * @param aVertex the out vertex
     * @param bVertex the in vertex
     * @return the edge
     * @throws AAIException the AAI exception
     */
    public Edge addEdge(GraphTraversalSource traversalSource, Vertex aVertex, Vertex bVertex) throws AAIException {
        return this.addEdge(traversalSource, aVertex, bVertex, null);
    }

    public Edge addEdge(GraphTraversalSource traversalSource, Vertex aVertex, Vertex bVertex, String label)
            throws AAIException {
        return this.addEdge(EdgeType.COUSIN, traversalSource, aVertex, bVertex, false, label);
    }

    public Edge addPrivateEdge(GraphTraversalSource traversalSource, Vertex aVertex, Vertex bVertex, String label)
            throws AAIException, EdgeRuleNotFoundException, AmbiguousRuleChoiceException {
        return this.addEdge(EdgeType.COUSIN, traversalSource, aVertex, bVertex, false, label, true);
    }

    private Edge addEdge(EdgeType type, GraphTraversalSource traversalSource, Vertex aVertex, Vertex bVertex,
            boolean isBestEffort, String label, boolean isPrivateEdge)
            throws AAIException, EdgeRuleNotFoundException, AmbiguousRuleChoiceException {

        EdgeRule rule = null;

        String aType = aVertex.<String>property(AAIProperties.NODE_TYPE).orElse(null);
        String bType = bVertex.<String>property(AAIProperties.NODE_TYPE).orElse(null);
        EdgeRuleQuery edgeQuery =
                new EdgeRuleQuery.Builder(aType, bType).label(label).setPrivate(isPrivateEdge).build();

        rule = edgerules.getRule(edgeQuery);

        if (rule.isPrivateEdge() != isPrivateEdge) {
            return null;
        }

        Edge e = null;

        Optional<String> message = this.validateMultiplicity(rule, traversalSource, aVertex, bVertex);

        if (message.isPresent() && !isBestEffort) {
            throw new EdgeMultiplicityException(message.get());
        }
        if (!message.isPresent()) {
            if (rule.getDirection().equals(Direction.OUT)) {
                e = aVertex.addEdge(rule.getLabel(), bVertex);
            } else if (rule.getDirection().equals(Direction.IN)) {
                e = bVertex.addEdge(rule.getLabel(), aVertex);
            }

            this.addProperties(e, rule);
        }
        return e;
    }

    /**
     * Adds the tree edge.
     *
     * @param aVertex the out vertex
     * @param bVertex the in vertex
     * @return the edge
     * @throws AAIException the AAI exception
     */
    public Edge addTreeEdgeIfPossible(GraphTraversalSource traversalSource, Vertex aVertex, Vertex bVertex)
            throws AAIException {
        return this.addEdge(EdgeType.TREE, traversalSource, aVertex, bVertex, true, null);
    }

    /**
     * Adds the edge.
     *
     * @param aVertex the out vertex
     * @param bVertex the in vertex
     * @return the edge
     * @throws AAIException the AAI exception
     */
    public Edge addEdgeIfPossible(GraphTraversalSource traversalSource, Vertex aVertex, Vertex bVertex)
            throws AAIException {
        return this.addEdgeIfPossible(traversalSource, aVertex, bVertex, null);
    }

    public Edge addEdgeIfPossible(GraphTraversalSource traversalSource, Vertex aVertex, Vertex bVertex, String label)
            throws AAIException {
        return this.addEdge(EdgeType.COUSIN, traversalSource, aVertex, bVertex, true, label);
    }

    /**
     * Adds the edge.
     *
     * @param type the type
     * @param aVertex the out vertex
     * @param bVertex the in vertex
     * @return the edge
     * @throws AAIException the AAI exception
     */
    private Edge addEdge(EdgeType type, GraphTraversalSource traversalSource, Vertex aVertex, Vertex bVertex,
            boolean isBestEffort, String label) throws AAIException {
        String aNodeType = (String) aVertex.property(AAIProperties.NODE_TYPE).value();
        String bNodeType = (String) bVertex.property(AAIProperties.NODE_TYPE).value();
        EdgeRuleQuery q = new EdgeRuleQuery.Builder(aNodeType, bNodeType).label(label).edgeType(type).build();
        EdgeRule rule;
        try {
            rule = edgerules.getRule(q);
        } catch (EdgeRuleNotFoundException e1) {
            throw new NoEdgeRuleFoundException(e1);
        } catch (AmbiguousRuleChoiceException e1) {
            throw new MultipleEdgeRuleFoundException(e1);
        }

        Edge e = null;

        Optional<String> message = this.validateMultiplicity(rule, traversalSource, aVertex, bVertex);

        if (message.isPresent() && !isBestEffort) {
            throw new EdgeMultiplicityException(message.get());
        }
        if (!message.isPresent()) {
            if (rule.getDirection().equals(Direction.OUT)) {
                e = aVertex.addEdge(rule.getLabel(), bVertex);
            } else if (rule.getDirection().equals(Direction.IN)) {
                e = bVertex.addEdge(rule.getLabel(), aVertex);
            }

            this.addProperties(e, rule);
        }
        return e;
    }

    /**
     * Adds the properties.
     *
     * @param edge the edge
     * @param rule the rule
     */
    public void addProperties(Edge edge, EdgeRule rule) {
        Map<EdgeProperty, String> propMap = new EnumMap<>(EdgeProperty.class);
        propMap.put(EdgeProperty.CONTAINS, rule.getContains());
        propMap.put(EdgeProperty.DELETE_OTHER_V, rule.getDeleteOtherV());
        propMap.put(EdgeProperty.PREVENT_DELETE, rule.getPreventDelete());

        for (Entry<EdgeProperty, String> entry : propMap.entrySet()) {
            edge.property(entry.getKey().toString(), entry.getValue());
        }

        edge.property(EdgeField.PRIVATE.toString(), rule.isPrivateEdge());
        edge.property(AAIProperties.AAI_UUID, UUID.randomUUID().toString());
    }

    /**
     * Validate multiplicity.
     *
     * @param rule the rule
     * @param aVertex the out vertex
     * @param bVertex the in vertex
     * @return true, if successful
     * @throws AAIException the AAI exception
     */
    private Optional<String> validateMultiplicity(EdgeRule rule, GraphTraversalSource traversalSource, Vertex aVertex,
            Vertex bVertex) {

        Vertex a = aVertex;
        Vertex b = bVertex;

        if (rule.getDirection().equals(Direction.OUT)) {
            a = aVertex;
            b = bVertex;
        } else if (rule.getDirection().equals(Direction.IN)) {
            a = bVertex;
            b = aVertex;
        }

        String aVertexType = a.<String>property(AAIProperties.NODE_TYPE).orElse(null);
        String bVertexType = b.<String>property(AAIProperties.NODE_TYPE).orElse(null);
        String label = rule.getLabel();

        MultiplicityRule multiplicityRule = rule.getMultiplicityRule();

        String detail = "";
        final String msg = "multiplicity rule violated: only one edge can exist with label: ";

        if (multiplicityRule.equals(MultiplicityRule.ONE2ONE)) {
            Long outEdgesCnt = traversalSource.V(a).out(label).has(AAIProperties.NODE_TYPE, bVertexType).count().next();
            Long inEdgesCnt = traversalSource.V(b).in(label).has(AAIProperties.NODE_TYPE, aVertexType).count().next();
            if (aVertexType.equals(bVertexType)) {
                inEdgesCnt = inEdgesCnt
                        + traversalSource.V(a).in(label).has(AAIProperties.NODE_TYPE, aVertexType).count().next();
                outEdgesCnt = outEdgesCnt
                        + traversalSource.V(b).out(label).has(AAIProperties.NODE_TYPE, bVertexType).count().next();
            }
            if ((inEdgesCnt != 0) || (outEdgesCnt != 0)) {
                detail = msg + label + " between " + aVertexType + " and " + bVertexType;
            }
        } else if (multiplicityRule.equals(MultiplicityRule.ONE2MANY)) {
            Long inEdgesCnt = traversalSource.V(b).in(label).has(AAIProperties.NODE_TYPE, aVertexType).count().next();
            if (inEdgesCnt != 0) {
                detail = msg + label + " between " + aVertexType + " and " + bVertexType;
            }
        } else if (multiplicityRule.equals(MultiplicityRule.MANY2ONE)) {
            Long outEdgesCnt = traversalSource.V(a).out(label).has(AAIProperties.NODE_TYPE, bVertexType).count().next();
            if (outEdgesCnt != 0) {
                detail = msg + label + " between " + aVertexType + " and " + bVertexType;
            }
        }

        if (!"".equals(detail)) {
            return Optional.of(detail);
        } else {
            return Optional.empty();
        }
    }
}
