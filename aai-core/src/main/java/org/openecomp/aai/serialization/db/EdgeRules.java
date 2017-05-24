/*-
 * ============LICENSE_START=======================================================
 * org.openecomp.aai
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

package org.openecomp.aai.serialization.db;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import org.openecomp.aai.db.props.AAIProperties;
import org.openecomp.aai.dbmodel.DbEdgeRules;
import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.serialization.db.exceptions.EdgeMultiplicityException;
import org.openecomp.aai.serialization.db.exceptions.NoEdgeRuleFoundException;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class EdgeRules {

	private Multimap<String, String> rules = DbEdgeRules.EdgeRules;
	private Multimap<String, String> deleteScope = 	DbEdgeRules.DefaultDeleteScope;
	private final int EDGE_NAME = 0;
	private final int DIRECTION = 1;
	private final int MULTIPLICITY_RULE = 2;
	private final int IS_PARENT = 3;
	private final int USES_RESOURCE = 4;
	private final int HAS_DEL_TARGET = 5;
	private final int SVC_INFRA = 6;
	
	/**
	 * Instantiates a new edge rules.
	 */
	private EdgeRules() {
	
	}
	private static class Helper {
		private static final EdgeRules INSTANCE = new EdgeRules();
		
	}
	
	/**
	 * Gets the single instance of EdgeRules.
	 *
	 * @return single instance of EdgeRules
	 */
	public static EdgeRules getInstance() {
		return Helper.INSTANCE;

	}
	
	/**
	 * Adds the tree edge.
	 *
	 * @param aVertex the out vertex
	 * @param bVertex the in vertex
	 * @return the edge
	 * @throws AAIException the AAI exception
	 * @throws NoEdgeRuleFoundException 
	 */
	public Edge addTreeEdge(GraphTraversalSource traversalSource, Vertex aVertex, Vertex bVertex) throws AAIException {
		return this.addEdge(EdgeType.TREE, traversalSource, aVertex, bVertex, false);
	}
	
	/**
	 * Adds the edge.
	 *
	 * @param aVertex the out vertex
	 * @param bVertex the in vertex
	 * @return the edge
	 * @throws AAIException the AAI exception
	 * @throws NoEdgeRuleFoundException 
	 */
	public Edge addEdge(GraphTraversalSource traversalSource, Vertex aVertex, Vertex bVertex) throws AAIException {
		return this.addEdge(EdgeType.COUSIN, traversalSource, aVertex, bVertex, false);
	}
	
	/**
	 * Adds the tree edge.
	 *
	 * @param aVertex the out vertex
	 * @param bVertex the in vertex
	 * @return the edge
	 * @throws AAIException the AAI exception
	 * @throws NoEdgeRuleFoundException 
	 */
	public Edge addTreeEdgeIfPossible(GraphTraversalSource traversalSource, Vertex aVertex, Vertex bVertex) throws AAIException {
		return this.addEdge(EdgeType.TREE, traversalSource, aVertex, bVertex, true);
	}
	
	/**
	 * Adds the edge.
	 *
	 * @param aVertex the out vertex
	 * @param bVertex the in vertex
	 * @return the edge
	 * @throws AAIException the AAI exception
	 * @throws NoEdgeRuleFoundException 
	 */
	public Edge addEdgeIfPossible(GraphTraversalSource traversalSource, Vertex aVertex, Vertex bVertex) throws AAIException {
		return this.addEdge(EdgeType.COUSIN, traversalSource, aVertex, bVertex, true);
	}
	
	/**
	 * Adds the edge.
	 *
	 * @param type the type
	 * @param aVertex the out vertex
	 * @param bVertex the in vertex
	 * @return the edge
	 * @throws AAIException the AAI exception
	 * @throws NoEdgeRuleFoundException 
	 */
	private Edge addEdge(EdgeType type, GraphTraversalSource traversalSource, Vertex aVertex, Vertex bVertex, boolean isBestEffort) throws AAIException, NoEdgeRuleFoundException {

		EdgeRule rule = this.getEdgeRule(type, aVertex, bVertex);

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
		
		// In DbEdgeRules.EdgeRules -- What we have as "edgeRule" is a comma-delimited set of strings.
		// The first item is the edgeLabel.
		// The second in the list is always "direction" which is always OUT for the way we've implemented it.
		// Items starting at "firstTagIndex" and up are all assumed to be booleans that map according to 
		// tags as defined in EdgeInfoMap.
		// Note - if they are tagged as 'reverse', that means they get the tag name with "-REV" on it
		Map<String, String> propMap = rule.getEdgeProperties();
		
		for (String key : propMap.keySet()) {
			String revKeyname = key + "-REV";
			String triple = propMap.get(key);
			if(triple.equals("true")){
				edge.property(key, true);
				edge.property(revKeyname,false);
			} else if (triple.equals("false")) {
				edge.property(key, false);
				edge.property(revKeyname,false);
			} else if (triple.equals("reverse")) {
				edge.property(key, false);
				edge.property(revKeyname,true);
			}
		}
	}
	
	/**
	 * Checks for edge rule.
	 *
	 * @param outType the out type
	 * @param inType the in type
	 * @return true, if successful
	 */
	public boolean hasEdgeRule(String outType, String inType) {
		
		Collection<String> collection = rules.get(outType + "|" + inType);

		return !collection.isEmpty();
		
	}
	
	/**
	 * Checks for edge rule.
	 *
	 * @param aVertex the out vertex
	 * @param bVertex the in vertex
	 * @return true, if successful
	 */
	public boolean hasEdgeRule(Vertex aVertex, Vertex bVertex) {
		String outType = (String)aVertex.<String>property("aai-node-type").orElse(null);
		String inType = (String)bVertex.<String>property("aai-node-type").orElse(null);
		
		return this.hasEdgeRule(outType, inType);
		
	}
	
	public Map<String, EdgeRule> getEdgeRules(String outType, String inType) throws AAIException {
		Map<String, EdgeRule> result = new HashMap<>();
		EdgeRule rule = null;
		for (EdgeType type : EdgeType.values()) {
			try {
				rule = this.getEdgeRule(type, outType, inType);
				result.put(rule.getLabel(), rule);
			} catch (NoEdgeRuleFoundException e) {
				continue;
			}
		}
		
		return result;
	}
	/**
	 * Gets the edge rule.
	 *
	 * @param outType the out type
	 * @param inType the in type
	 * @return the edge rule
	 * @throws AAIException the AAI exception
	 */
	public EdgeRule getEdgeRule(EdgeType type, String outType, String inType) throws AAIException {
		EdgeRule rule = new EdgeRule();
		Collection<String> collection = null;
		boolean isFlipped = false;
		if (this.hasEdgeRule(outType, inType) || this.hasEdgeRule(inType, outType)) {
		} else {
			String detail = "No EdgeRule found for passed nodeTypes: " + outType + ", " + inType + ".";
			throw new AAIException("AAI_6120", detail); 
		}
		String key = outType + "|" + inType;
		collection = rules.get(key);

		String[] info = null;
		Iterator<String> iterator = collection.iterator();
		info = this.findRuleForContext(type, key, iterator);
		if (info == null) { //didn't find anything in that order, look again
			key = inType + "|" + outType;
			collection = rules.get(key);
			iterator = collection.iterator();
			info = this.findRuleForContext(type, key, iterator);
			isFlipped = true;
		}
		if (info == null) {
			throw new NoEdgeRuleFoundException("No EdgeRule found for EdgeType: " + type + " and node types: " + outType + " " + inType);
		}
		rule.setLabel(info[this.EDGE_NAME]);
		rule.setMultiplicityRule(MultiplicityRule.valueOf(info[this.MULTIPLICITY_RULE].toUpperCase()));
		rule.setHasDelTarget(info[this.HAS_DEL_TARGET]);
		rule.setUsesResource(info[this.USES_RESOURCE]);
		rule.setIsParent(info[this.IS_PARENT]);
		rule.setServiceInfrastructure(info[this.SVC_INFRA]);
		Direction direction = Direction.valueOf(info[this.DIRECTION]);
		if (isFlipped && direction.equals(Direction.OUT)) {
			rule.setDirection(Direction.IN);
		} else if (isFlipped && direction.equals(Direction.IN)){
			rule.setDirection(Direction.OUT);
		} else {
			rule.setDirection(direction);
		}

		return rule;
	}
	
	private String[] findRuleForContext (EdgeType type, String key, Iterator<String> itr) {
		String[] result = null;
		String s = "";
		String isParent = "";
		String[] info = new String[10];
		while (itr.hasNext()) {
			s = itr.next();
			info = s.split(",");
			isParent = info[this.IS_PARENT];
			//lazily stop iterating if we find a match
			//should there be a mismatch between type and isParent,
			//the caller will receive something.
			//this operates on the assumption that there are at most two rules
			//for a given vertex pair
			if (type.equals(EdgeType.TREE) && (isParent.equals("true") || isParent.equals("reverse"))) {
				result = info;
				break;
			} else if (type.equals(EdgeType.COUSIN) && isParent.equals("false")) {
				result = info;
				break;
			}
		}
		
		
		return result;
	}
	/**
	 * Gets the edge rule.
	 *
	 * @param aVertex the out vertex
	 * @param bVertex the in vertex
	 * @return the edge rule
	 * @throws AAIException the AAI exception
	 * @throws NoEdgeRuleFoundException 
	 */
	public EdgeRule getEdgeRule(EdgeType type, Vertex aVertex, Vertex bVertex) throws AAIException, NoEdgeRuleFoundException {
		String outType = (String)aVertex.<String>property(AAIProperties.NODE_TYPE).orElse(null);
		String inType = (String)bVertex.<String>property(AAIProperties.NODE_TYPE).orElse(null);
		
		return this.getEdgeRule(type, outType, inType);

		
	}
	
	/**
	 * Gets the delete semantic.
	 *
	 * @param nodeType the node type
	 * @return the delete semantic
	 */
	public DeleteSemantic getDeleteSemantic(String nodeType) {
		Collection<String> semanticCollection = deleteScope.get(nodeType);
		String semantic = semanticCollection.iterator().next();
		
		return DeleteSemantic.valueOf(semantic);
		
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
	private Optional<String> validateMultiplicity(EdgeRule rule, GraphTraversalSource traversalSource, Vertex aVertex, Vertex bVertex) {

		if (rule.getDirection().equals(Direction.OUT)) {
			
		} else if (rule.getDirection().equals(Direction.IN)) {
			Vertex tempV = bVertex;
			bVertex = aVertex;
			aVertex = tempV;
		}
				
		String aVertexType = aVertex.<String>property(AAIProperties.NODE_TYPE).orElse(null);
		String bVertexType =  bVertex.<String>property(AAIProperties.NODE_TYPE).orElse(null);
		String label = rule.getLabel();
		MultiplicityRule multiplicityRule = rule.getMultiplicityRule();
		List<Edge> outEdges = traversalSource.V(aVertex).outE(label).where(__.inV().has(AAIProperties.NODE_TYPE, bVertexType)).toList();
		List<Edge> inEdges = traversalSource.V(bVertex).inE(label).where(__.outV().has(AAIProperties.NODE_TYPE, aVertexType)).toList();
		String detail = "";
		if (multiplicityRule.equals(MultiplicityRule.ONE2ONE)) {
			if (inEdges.size() >= 1 || outEdges.size() >= 1 ) {
				detail = "multiplicity rule violated: only one edge can exist with label: " + label + " between " + aVertexType + " and " + bVertexType;
			}
		} else if (multiplicityRule.equals(MultiplicityRule.ONE2MANY)) {
			if (inEdges.size() >= 1) {
				detail = "multiplicity rule violated: only one edge can exist with label: " + label + " between " + aVertexType + " and " + bVertexType;
			}
		} else if (multiplicityRule.equals(MultiplicityRule.MANY2ONE)) {
			if (outEdges.size() >= 1) {
				detail = "multiplicity rule violated: only one edge can exist with label: " + label + " between " + aVertexType + " and " + bVertexType;
			}
		} else {
			
		}
		
		if (!detail.equals("")) {
			return Optional.of(detail);
		} else  {
			return Optional.empty();
		}
		
				
	}
	
	public Multimap<String, EdgeRule> getAllRules() throws AAIException {
		
		Multimap<String, EdgeRule> result = ArrayListMultimap.create();
		
		for (String key : this.rules.keySet()) {
			String outType = "";
			String inType = "";
			String[] split = key.split("\\|");
			outType = split[0];
			inType = split[1];
			result.putAll(key,this.getEdgeRules(outType, inType).values());
		}
		
		return result;
	}
	
}
