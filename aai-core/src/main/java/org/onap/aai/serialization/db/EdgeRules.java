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
package org.onap.aai.serialization.db;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Version;
import org.onap.aai.serialization.db.exceptions.EdgeMultiplicityException;
import org.onap.aai.serialization.db.exceptions.NoEdgeRuleFoundException;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;

public class EdgeRules {
	
	private EELFLogger logger = EELFManager.getInstance().getLogger(EdgeRules.class);

	private DocumentContext rulesDoc;
	
	/**
	 * Loads the most recent DbEdgeRules json file for later parsing.
	 * Only need most recent version for actual A&AI operations that call this class; 
	 *   the old ones are only used in tests.
	 */
	private EdgeRules() {

		String json = this.getEdgeRuleJson(Version.getLatest());
		rulesDoc = JsonPath.parse(json);
		
	}
	
	private EdgeRules(String rulesFilename) {
		String json = this.getEdgeRuleJson(rulesFilename);
		rulesDoc = JsonPath.parse(json);
	}

	private String getEdgeRuleJson(String rulesFilename) {
		InputStream is = getClass().getResourceAsStream(rulesFilename);

		Scanner scanner = new Scanner(is);
		String json = scanner.useDelimiter("\\Z").next();
		scanner.close();

		return json;
	}

	/**
	 * Loads the versioned DbEdgeRules json file for later parsing.
	 */
	@SuppressWarnings("unchecked")
	private EdgeRules(Version version) {
		String json = this.getEdgeRuleJson(version);
		rulesDoc = JsonPath.parse(json);
	}
	
	private String getEdgeRuleJson(Version version) {
		InputStream is = getClass().getResourceAsStream("/dbedgerules/DbEdgeRules_" + version.toString() + ".json");

		Scanner scanner = new Scanner(is);
		String json = scanner.useDelimiter("\\Z").next();
		scanner.close();
		
		return json;
	}
	
	private static class Helper {
		private static final EdgeRules INSTANCE = new EdgeRules();
		private static final Map<Version, EdgeRules> INSTANCEMAP = new ConcurrentHashMap<>();

		private static EdgeRules getEdgeRulesByFilename(String rulesFilename) {
			return new EdgeRules(rulesFilename);
		}

		private static EdgeRules getVersionedEdgeRules(Version v) {
			if (Version.isLatest(v)) {
				return INSTANCE;
			}
			if (!INSTANCEMAP.containsKey(v)) {
				INSTANCEMAP.put(v, new EdgeRules(v));
			}
			return INSTANCEMAP.get(v);
		}
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
	 * Gets the versioned instance of EdgeRules.
	 *
	 * @return versioned instance of EdgeRules
	 */
	public static EdgeRules getInstance(Version v) {
		return Helper.getVersionedEdgeRules(v);

	}
	
	/**
	 * Loads edge rules from the given file.
	 *
	 * @param rulesFilename - name of the file to load rules from
	 * @return the EdgeRules instance
	 */
	public static EdgeRules getInstance(String rulesFilename) {
		return Helper.getEdgeRulesByFilename(rulesFilename);
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
		return this.addEdge(EdgeType.TREE, traversalSource, aVertex, bVertex, false);
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
		return this.addEdge(EdgeType.COUSIN, traversalSource, aVertex, bVertex, false);
	}
	
	/**
	 * Adds the tree edge.
	 *
	 * @param aVertex the out vertex
	 * @param bVertex the in vertex
	 * @return the edge
	 * @throws AAIException the AAI exception
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
	 */
	private Edge addEdge(EdgeType type, GraphTraversalSource traversalSource, Vertex aVertex, Vertex bVertex, boolean isBestEffort) throws AAIException {

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
		Map<EdgeProperty, String> propMap = rule.getEdgeProperties();
		
		for (Entry<EdgeProperty, String> entry : propMap.entrySet()) {
			edge.property(entry.getKey().toString(), entry.getValue());
		}
	}
	
	/**
	 * Checks if any edge rules exist between the two given nodes, in either A|B or B|A order.
	 *
	 * @param nodeA - node at one end of the edge
	 * @param nodeB - node at the other end
	 * @return true, if any such rules exist
	 */
	public boolean hasEdgeRule(String nodeA, String nodeB) {
		Filter aToB = filter(
				where("from").is(nodeA).and("to").is(nodeB)
				);
		Filter bToA = filter(
				where("from").is(nodeB).and("to").is(nodeA)
				);
		
		List<Map<String, String>> results = readRules(aToB);
		results.addAll(readRules(bToA));

		return !results.isEmpty();
		
	}
	
	/**
	 * Checks if any edge rules exist between the two given nodes, in either A|B or B|A order.
	 *
	 * @param aVertex - node at one end of the edge
	 * @param bVertex - node at the other end
	 * @return true, if any such rules exist
	 */
	public boolean hasEdgeRule(Vertex aVertex, Vertex bVertex) {
		String outType = aVertex.<String>property("aai-node-type").orElse(null);
		String inType = bVertex.<String>property("aai-node-type").orElse(null);
		
		return this.hasEdgeRule(outType, inType);
		
	}
	
	/**
	 * Gets all the edge rules that exist between the given node types.
	 * The rules will be phrased in terms of out|in, though this will
	 * also find rules defined as in|out (it will flip the direction in
	 * the EdgeRule object returned accordingly to match out|in).
	 * 
	 * @param outType 
	 * @param inType
	 * @return Map<String edgeLabel, EdgeRule rule> where edgeLabel is the label name
	 * @throws AAIException
	 */
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
	 * Gets the edge rule of the given type that exists between A and B.
	 * Will check B|A as well, and flips the direction accordingly if that succeeds
	 * to match the expected A|B return.
	 *
	 * @param type - the type of edge you're looking for
	 * @param nodeA - first node type
	 * @param nodeB - second node type
	 * @return EdgeRule describing the rule in terms of A|B, if there is any such rule
	 * @throws AAIException if no such edge exists
	 */
	public EdgeRule getEdgeRule(EdgeType type, String nodeA, String nodeB) throws AAIException {
		//try A to B
		List<Map<String, String>> aToBEdges = readRules(buildFilter(type, nodeA, nodeB));
		if (!aToBEdges.isEmpty()) {
			//lazily stop iterating if we find a match
			//should there be a mismatch between type and isParent,
			//the caller will receive something.
			//this operates on the assumption that there are at most two rules
			//for a given vertex pair
			verifyRule(aToBEdges.get(0));
			return buildRule(aToBEdges.get(0));
		}
		
		//we get here if there was nothing for A to B, so let's try B to A
		List<Map<String, String>> bToAEdges = readRules(buildFilter(type, nodeB, nodeA));
		if (!bToAEdges.isEmpty()) {
			verifyRule(bToAEdges.get(0));
			return flipDirection(buildRule(bToAEdges.get(0))); //bc we need to return as A|B, so flip the direction to match
		}
		
		//found none
		throw new NoEdgeRuleFoundException("no " + type.toString() + " edge between " + nodeA + " and " + nodeB);
	}
	
	/**
	 * Builds a JsonPath filter to search for an edge from nodeA to nodeB with the given edge type (cousin or parent/child)
	 * 
	 * @param type
	 * @param nodeA - start node
	 * @param nodeB - end node
	 * @return
	 */
	private Filter buildFilter(EdgeType type, String nodeA, String nodeB) {
		if (EdgeType.COUSIN.equals(type)) {
			return filter(
					where("from").is(nodeA).and("to").is(nodeB).and(EdgeProperty.CONTAINS.toString()).is(AAIDirection.NONE.toString())
					);
		} else {
			return filter(
					where("from").is(nodeA).and("to").is(nodeB).and(EdgeProperty.CONTAINS.toString()).is("${direction}")).or(
							where("from").is(nodeA).and("to").is(nodeB).and(EdgeProperty.CONTAINS.toString()).is("!${direction}")	
					);
		}
	}
	
	/**
	 * Puts the give edge rule information into an EdgeRule object. 
	 * 
	 * @param edge - the edge information returned from JsonPath
	 * @return EdgeRule containing that information
	 */
	private EdgeRule buildRule(Map<String, String> map) {
		Map<String, String> edge = new EdgePropertyMap<>();
		edge.putAll(map);
		
		EdgeRule rule = new EdgeRule();
		rule.setLabel(edge.get("label"));
		rule.setDirection(edge.get("direction"));
		rule.setMultiplicityRule(edge.get("multiplicity"));
		rule.setContains(edge.get(EdgeProperty.CONTAINS.toString()));
		rule.setDeleteOtherV(edge.get(EdgeProperty.DELETE_OTHER_V.toString()));
		rule.setServiceInfrastructure(edge.get(EdgeProperty.SVC_INFRA.toString()));
		rule.setPreventDelete(edge.get(EdgeProperty.PREVENT_DELETE.toString()));
		
		return rule;
	}
	
	/**
	 * If getEdgeRule gets a request for A|B, and it finds something as B|A, the caller still expects
	 * the returned EdgeRule to reflect A|B directionality. This helper method flips B|A direction to
	 * match this expectation.
	 * 
	 * @param rule whose direction needs flipped
	 * @return the updated rule
	 */
	private EdgeRule flipDirection(EdgeRule rule) {
		if (Direction.IN.equals(rule.getDirection())) {
			rule.setDirection(Direction.OUT);
			return rule;
		} else if (Direction.OUT.equals(rule.getDirection())) {
			rule.setDirection(Direction.IN);
			return rule;
		} else { //direction is BOTH, flipping both is still both
			return rule; 
		}
	}
	
	/**
	 * Gets the edge rule of the given type that exists between A and B.
	 * Will check B|A as well, and flips the direction accordingly if that succeeds
	 * to match the expected A|B return.
	 *
	 * @param type - the type of edge you're looking for
	 * @param aVertex - first node type
	 * @param bVertex - second node type
	 * @return EdgeRule describing the rule in terms of A|B, if there is any such rule
	 * @throws AAIException if no such edge exists
	 */
	public EdgeRule getEdgeRule(EdgeType type, Vertex aVertex, Vertex bVertex) throws AAIException {
		String outType = aVertex.<String>property(AAIProperties.NODE_TYPE).orElse(null);
		String inType = bVertex.<String>property(AAIProperties.NODE_TYPE).orElse(null);
		
		return this.getEdgeRule(type, outType, inType);

		
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
		
		if (!"".equals(detail)) {
			return Optional.of(detail);
		} else  {
			return Optional.empty();
		}
		
				
	}
	
	/**
	 * Verifies that all required properties are defined in the given edge rule.
	 * If they are not, throws a RuntimeException.
	 *
	 * @param rule - Map<String edge property, String edge property value> representing
	 * an edge rule
	 */
	private void verifyRule(Map<String, String> rule) {
		for (EdgeProperty prop : EdgeProperty.values()) {
			if (!rule.containsKey(prop.toString())) {
				/* Throws RuntimeException as rule definition errors
				 * cannot be recovered from, and should never happen anyway
				 * because these are configuration files, so requiring all
				 * downstream code to check for this exception seems inappropriate.
				 * It's instantiated with an AAIException to make sure all
				 * relevant information is present in the error message.
				 */
				throw new RuntimeException(new AAIException("AAI_4005",
						"Rule between " + rule.get("from") + " and " + rule.get("to") +
						" is missing property " + prop + "."));
			}
		}
	}

	/**
	 * Reads all the edge rules from the loaded json file.
	 *
	 * @return List<Map<String edge property, String edge property value>>
	 *  Each map represents a rule read from the json.
	 */
	private List<Map<String, String>> readRules() {
		return readRules(null);
	}

	/**
	 * Reads the edge rules from the loaded json file, using the given filter
	 * to get specific rules. If filter is null, will get all rules.
	 *
	 * @param filter - may be null to indicate get all
	 * @return List<Map<String edge property, String edge property value>>
	 *  Each map represents a rule read from the json.
	 */
	private List<Map<String, String>> readRules(Filter filter) {
		List<Map<String, String>> results;
		if (filter == null) { //no filter means get all
			results = rulesDoc.read("$.rules.*");
		} else {
			results = rulesDoc.read("$.rules.[?]", filter);
		}
		for (Map<String, String> result : results) {
			verifyRule(result);
		}
		return results;
	}

	/**
	 * Gets all the edge rules we define.
	 * 
	 * @return Multimap<String "from|to", EdgeRule rule>
	 */
	public Multimap<String, EdgeRule> getAllRules() {
		Multimap<String, EdgeRule> result = ArrayListMultimap.create();
		
		List<Map<String, String>> rules = readRules();
		for (Map<String, String> rule : rules) {
			EdgeRule er = buildRule(rule);
			String name = rule.get("from") + "|" + rule.get("to");
			result.put(name, er);
		}
		
		return result;
	}
	
	/**
	 * Gets all edge rules that define a child relationship from
	 * the given node type.
	 *
	 * @param nodeType
	 * @return
	 */
	public Set<EdgeRule> getChildren(String nodeType) {
		
		final Filter filter = filter(
				where("from").is(nodeType).and(EdgeProperty.CONTAINS.toString()).is("${direction}")
				).or(where("to").is(nodeType).and(EdgeProperty.CONTAINS.toString()).is("!${direction}"));
		
		final List<Map<String, String>> rules = readRules(filter);
		final Set<EdgeRule> result = new HashSet<>();
		rules.forEach(item -> {
			verifyRule(item);
			result.add(buildRule(item));
		});
	
		return result;
		
	}
}
