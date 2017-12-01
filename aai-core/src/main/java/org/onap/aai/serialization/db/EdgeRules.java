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
import org.onap.aai.serialization.db.exceptions.MultipleEdgeRuleFoundException;
import org.onap.aai.serialization.db.exceptions.NoEdgeRuleFoundException;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;

public class EdgeRules {
	
	private static final String LABEL = "label";

	private static final String NOT_DIRECTION_NOTATION = "!${direction}";

	private static final String DIRECTION_NOTATION = "${direction}";

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

	/**
	 * Loads the versioned DbEdgeRules json file for later parsing.
	 */
	private EdgeRules(Version version) {
		String json = this.getEdgeRuleJson(version);
		rulesDoc = JsonPath.parse(json);
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
	
	private String getEdgeRuleJson(String rulesFilename) {
		InputStream is = getClass().getResourceAsStream(rulesFilename);

		Scanner scanner = new Scanner(is);
		String json = scanner.useDelimiter("\\Z").next();
		scanner.close();

		return json;
	}
	
	private String getEdgeRuleJson(Version version) {
		return this.getEdgeRuleJson("/dbedgerules/DbEdgeRules_" + version.toString() + ".json");
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

	public Edge addEdge(GraphTraversalSource traversalSource, Vertex aVertex, Vertex bVertex, String label) throws AAIException {
		return this.addEdge(EdgeType.COUSIN, traversalSource, aVertex, bVertex, false, label);
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
	public Edge addEdgeIfPossible(GraphTraversalSource traversalSource, Vertex aVertex, Vertex bVertex) throws AAIException {
		return this.addEdgeIfPossible(traversalSource, aVertex, bVertex, null);
	}
	
	public Edge addEdgeIfPossible(GraphTraversalSource traversalSource, Vertex aVertex, Vertex bVertex, String label) throws AAIException {
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
	private Edge addEdge(EdgeType type, GraphTraversalSource traversalSource, Vertex aVertex, Vertex bVertex, boolean isBestEffort, String label) throws AAIException {

		EdgeRule rule = this.getEdgeRule(type, aVertex, bVertex, label);

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
	 * Checks if any edge rules exist between the two given node types, in either A|B or B|A order.
	 *
	 * @param nodeA - node at one end of the edge
	 * @param nodeB - node at the other end
	 * @return true, if any such rules exist
	 */
	public boolean hasEdgeRule(String nodeA, String nodeB) {
		return this.hasEdgeRule(nodeA, nodeB, null);
	}
	
	/**
	 * Checks if any edge rules exist between the two given node types with contains-other-v !NONE, in either A|B or B|A order.
	 *
	 * @param nodeA - node at one end of the edge
	 * @param nodeB - node at the other end
	 * @return true, if any such rules exist
	 */
	public boolean hasTreeEdgeRule(String nodeA, String nodeB) {
		return this.hasEdgeRule(EdgeType.TREE, nodeA, nodeB, null);
	}

	/**
	 * Checks if any edge rules exist between the two given node types with contains-other-v NONE, in either A|B or B|A order.
	 *
	 * @param nodeA - node at one end of the edge
	 * @param nodeB - node at the other end
	 * @param label - edge label
	 * @return true, if any such rules exist
	 */
	public boolean hasCousinEdgeRule(String nodeA, String nodeB, String label) {
		return this.hasEdgeRule(EdgeType.COUSIN, nodeA, nodeB, label);
	}
	
	/**
	 * Checks if any edge rules exist between the two given nodes with contains-other-v !NONE, in either A|B or B|A order.
	 *
	 * @param aVertex - node at one end of the edge
	 * @param bVertex - node at the other end
	 * @return true, if any such rules exist
	 */
	public boolean hasTreeEdgeRule(Vertex aVertex, Vertex bVertex) {
		String outType = aVertex.<String>property(AAIProperties.NODE_TYPE).orElse(null);
		String inType = bVertex.<String>property(AAIProperties.NODE_TYPE).orElse(null);
		return this.hasTreeEdgeRule(outType, inType);
	}
	
	/**
	 * Checks if any edge rules exist between the two given nodes with contains-other-v NONE with edge label, in either A|B or B|A order.
	 *
	 * @param aVertex - node at one end of the edge
	 * @param bVertex - node at the other end
	 * @param label - edge label
	 * @return true, if any such rules exist
	 */
	public boolean hasCousinEdgeRule(Vertex aVertex, Vertex bVertex, String label) {
		String outType = aVertex.<String>property(AAIProperties.NODE_TYPE).orElse(null);
		String inType = bVertex.<String>property(AAIProperties.NODE_TYPE).orElse(null);
		return this.hasCousinEdgeRule(outType, inType, label);
	}

	/**
	 * Checks if any edge rules exist between the two given nodes w/ edge label, in either A|B or B|A order.
	 *
	 * @param nodeA - node at one end of the edge
	 * @param nodeB - node at the other end
	 * @param label - edge label
	 * @return true, if any such rules exist
	 */
	public boolean hasEdgeRule(String nodeA, String nodeB, String label) {
		return this.hasEdgeRule(null, nodeA, nodeB, label);
	}
	
	/**
	 * Checks if any edge rules exist between the two given nodes, in either A|B or B|A order.
	 *
	 * @param aVertex - node at one end of the edge
	 * @param bVertex - node at the other end
	 * @return true, if any such rules exist
	 */
	public boolean hasEdgeRule(Vertex aVertex, Vertex bVertex) {
		return this.hasEdgeRule(aVertex, bVertex, null);

	}
	
	/**
	 * Checks if any edge rules exist between the two given nodes with label, in either A|B or B|A order with edge label.
	 *
	 * @param aVertex - node at one end of the edge
	 * @param bVertex - node at the other end
	 * @param label - edge label
	 * @return true, if any such rules exist
	 */
	public boolean hasEdgeRule(Vertex aVertex, Vertex bVertex, String label) {
		String outType = aVertex.<String>property(AAIProperties.NODE_TYPE).orElse(null);
		String inType = bVertex.<String>property(AAIProperties.NODE_TYPE).orElse(null);

		if (label == null) {
			return this.hasEdgeRule(outType, inType);
		} else {
			return this.hasEdgeRule(outType, inType, label);
		}
	}

	/**
	 * Checks if any edge rules exist between the two given node types, in either A|B or B|A order with edge label and edge type.
	 *
	 * @param type - type of edge EdgeType.COUSIN | EdgeType.TREE
	 * @param nodeA - node at one end of the edge
	 * @param nodeB - node at the other end
	 * @param label - edge label
	 * @return true, if any such rules exist
	 */
	public boolean hasEdgeRule(EdgeType type, String nodeA, String nodeB, String label) {
		Filter aToB = filter(
				where("from").is(nodeA)
					.and("to").is(nodeB)
				);
		Filter bToA = filter(
				where("from").is(nodeB)
				.and("to").is(nodeA)
			);

		if (EdgeType.TREE.equals(type)) {
			aToB = aToB.and(where(EdgeProperty.CONTAINS.toString()).ne(AAIDirection.NONE.toString()));
			bToA = bToA.and(where(EdgeProperty.CONTAINS.toString()).ne(AAIDirection.NONE.toString()));
		} else if (EdgeType.COUSIN.equals(type)) {
			aToB = aToB.and(where(EdgeProperty.CONTAINS.toString()).is(AAIDirection.NONE.toString()));
			bToA = bToA.and(where(EdgeProperty.CONTAINS.toString()).is(AAIDirection.NONE.toString()));
		}

		if (label != null) {
			aToB = aToB.and(where(LABEL).is(label));
			bToA = bToA.and(where(LABEL).is(label));
		}

		List<Object> results = rulesDoc.read("$.rules.[?]", aToB);
		results.addAll(rulesDoc.read("$.rules.[?]", bToA));

		return !results.isEmpty();
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
	public Map<String, EdgeRule> getEdgeRules(String outType, String inType) {
		return this.getEdgeRules(outType, inType, null);
	}
	
	/**
	 * Gets all the edge rules that exist between the given node types with given label.
	 * The rules will be phrased in terms of out|in, though this will
	 * also find rules defined as in|out (it will flip the direction in
	 * the EdgeRule object returned accordingly to match out|in).
	 *
	 * @param outType
	 * @param inType
	 * @param label
	 * @return Map<String edgeLabel, EdgeRule rule> where edgeLabel is the label name
	 * @throws AAIException
	 */
	public Map<String, EdgeRule> getEdgeRules(String outType, String inType, String label) {
		final Map<String, EdgeRule> result = new HashMap<>();

		for (EdgeType type : EdgeType.values()) {
			result.putAll(this.getEdgeRules(type, outType, inType, label));
		}

		return result;
	}

	/**
	 * Looks up edge rules for the given node types and the labels specified
	 * @param type
	 * @param outType
	 * @param inType
	 * @param labels
	 * @return
	 * @throws NoEdgeRuleFoundException
	 * @throws MultipleEdgeRuleFoundException
	 */
	public Map<String, EdgeRule> getEdgeRulesWithLabels(EdgeType type, String outType, String inType, List<String> labels) throws NoEdgeRuleFoundException, MultipleEdgeRuleFoundException {
		final Map<String, EdgeRule> result = new HashMap<>();

		if (labels == null || labels.isEmpty()) {
			throw new NoEdgeRuleFoundException("No labels specified");
		}
		for (String label : labels) {
			EdgeRule er = this.getEdgeRule(type, outType, inType, label);
			result.put(er.getLabel(), er);
		}

		return result;
	}

	/**
	 * Gets all the edge rules of that edge type that exist between the given node types with given label.
	 * The rules will be phrased in terms of out|in, though this will
	 * also find rules defined as in|out (it will flip the direction in
	 * the EdgeRule object returned accordingly to match out|in).
	 *
	 * @param type
	 * @param outType
	 * @param inType
	 * @param label
	 * @return
	 * @throws AAIException
	 */
	public Map<String, EdgeRule> getEdgeRules(EdgeType type, String outType, String inType, String label) {
		final Map<String, EdgeRule> result = new HashMap<>();

		this.getEdgeRulesFromJson(type, outType, inType, label).forEach(edgeRuleJson -> {
				EdgeRule edgeRule = this.buildRule(edgeRuleJson);
				result.put(edgeRule.getLabel(), edgeRule);
			});
		this.getEdgeRulesFromJson(type, inType, outType, label).forEach(erj -> {
			EdgeRule edgeRule = this.flipDirection(this.buildRule(erj));
			if (!result.containsKey(edgeRule.getLabel())) {
				result.put(edgeRule.getLabel(), edgeRule);
			}
		});


		return result;
	}
	
	/**
	 * Gets all the edge rules of that edge type that exist between the given node types.
	 * The rules will be phrased in terms of out|in, though this will
	 * also find rules defined as in|out (it will flip the direction in
	 * the EdgeRule object returned accordingly to match out|in).
	 *
	 * @param type
	 * @param outType
	 * @param inType
	 * @return
	 * @throws AAIException
	 */
	public Map<String, EdgeRule> getEdgeRules(EdgeType type, String outType, String inType) {
		return this.getEdgeRules(type, outType, inType, null);
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
		return this.getEdgeRule(type, nodeA, nodeB, null);
	}

	/**
	 * Gets the edge rule of the given type that exists between A and B with edge label.
	 * Will check B|A as well, and flips the direction accordingly if that succeeds
	 * to match the expected A|B return.
	 *
	 * @param type - the type of edge you're looking for
	 * @param nodeA - first node type
	 * @param nodeB - second node type
	 * @param label - edge label
	 * @return EdgeRule describing the rule in terms of A|B, if there is any such rule
	 * @throws MultipleEdgeRuleFoundException
	 * @throws AAIException if no such edge exists
	 */
	public EdgeRule getEdgeRule(EdgeType type, String nodeA, String nodeB, String label) throws NoEdgeRuleFoundException, MultipleEdgeRuleFoundException {

		final StringBuilder errorMsg = new StringBuilder();
		errorMsg.append(type.toString())
			.append(" edge rule between ")
			.append(nodeA).append(" and ").append(nodeB);
		if (label != null) {
			errorMsg.append(" with label ").append(label);
		}

		EdgeRule edgeRule;
		Map<String, EdgeRule> edgeRules = this.getEdgeRules(type, nodeA, nodeB, label);

		//found none
		if (edgeRules.isEmpty()) {

			throw new NoEdgeRuleFoundException("no " + errorMsg);

		} else if (edgeRules.size() == 1) {

			edgeRule = edgeRules.values().iterator().next();

		} else {

			Optional<EdgeRule> optionalEdgeRule = Optional.empty();

			try {
				optionalEdgeRule = this.getDefaultEdgeRule(edgeRules);
			} catch (MultipleEdgeRuleFoundException e) {
				throw new MultipleEdgeRuleFoundException("multiple default edge rule exists " + errorMsg);
			}

			edgeRule = optionalEdgeRule.orElseThrow(() -> new MultipleEdgeRuleFoundException("multiple edge rule exists with no default " + errorMsg));

		}

		return edgeRule;
	}

	private Optional<EdgeRule> getDefaultEdgeRule(Map<String, EdgeRule> edgeRules) throws MultipleEdgeRuleFoundException {

		EdgeRule edgeRule = null;
		int numDefaults = 0;

		for (Map.Entry<String, EdgeRule> entry : edgeRules.entrySet()) {
			if (entry.getValue().isDefault()) {
				edgeRule  = entry.getValue();
				numDefaults++;
			}
		}

		if (numDefaults > 1) {
			throw new MultipleEdgeRuleFoundException("");
		}

		if (edgeRule == null) {
			return Optional.empty();
		} else {
			return Optional.of(edgeRule);
		}
	}

	/**
	 * Gets the rules from the edge rules Json
	 *
	 * @param type - type
	 * @param nodeA - start node
	 * @param nodeB - end node
	 * @param label - edge label to filter on
	 * @return
	 */
	private List<Map<String, String>> getEdgeRulesFromJson(EdgeType type, String nodeA, String nodeB, String label) {
		if (label == null) {
			return rulesDoc.read("$.rules.[?]", buildFilter(type, nodeA, nodeB));
		} else {
			return rulesDoc.read("$.rules.[?]", buildFilter(type, nodeA, nodeB, label));
		}
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
		return this.buildFilter(type, nodeA, nodeB, null);
	}
	
	private Filter buildFilter(EdgeType type, String nodeA, String nodeB, String label) {
		if (EdgeType.COUSIN.equals(type)) {
			Filter f = filter(
					where("from").is(nodeA)
						.and("to").is(nodeB)
						.and(EdgeProperty.CONTAINS.toString()).is(AAIDirection.NONE.toString())
					);
			if (label != null) {
				f = f.and(where(LABEL).is(label));
			}

			return f;
		} else {
			return filter(
					where("from").is(nodeA).and("to").is(nodeB).and(EdgeProperty.CONTAINS.toString()).is(DIRECTION_NOTATION)).or(
							where("from").is(nodeA).and("to").is(nodeB).and(EdgeProperty.CONTAINS.toString()).is(NOT_DIRECTION_NOTATION)
					);
		}
	}

	/**
	 * Puts the give edge rule information into an EdgeRule object.
	 *
	 * @param map edge rule property map
	 * @return EdgeRule containing that information
	 */
	private EdgeRule buildRule(Map<String, String> map) {
		Map<String, String> edge = new EdgePropertyMap<>();
		edge.putAll(map);

		EdgeRule rule = new EdgeRule();
		rule.setLabel(edge.get(LABEL));
		rule.setDirection(edge.get("direction"));
		rule.setMultiplicityRule(edge.get("multiplicity"));
		rule.setContains(edge.get(EdgeProperty.CONTAINS.toString()));
		rule.setDeleteOtherV(edge.get(EdgeProperty.DELETE_OTHER_V.toString()));
		rule.setServiceInfrastructure(edge.get(EdgeProperty.SVC_INFRA.toString()));
		rule.setPreventDelete(edge.get(EdgeProperty.PREVENT_DELETE.toString()));
		if (edge.containsKey("default")) {
			rule.setIsDefault(edge.get("default"));
		}

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
		return this.getEdgeRule(type, aVertex, bVertex, null);
	}
	
	/**
	 * Gets the edge rule of the given type that exists between A and B with label.
	 * Will check B|A as well, and flips the direction accordingly if that succeeds
	 * to match the expected A|B return.
	 *
	 * @param type - the type of edge you're looking for
	 * @param aVertex - first node type
	 * @param bVertex - second node type
	 * @param label - edge label
	 * @return EdgeRule describing the rule in terms of A|B, if there is any such rule
	 * @throws AAIException if no such edge exists
	 */
	public EdgeRule getEdgeRule(EdgeType type, Vertex aVertex, Vertex bVertex, String label) throws AAIException {
		String outType = aVertex.<String>property(AAIProperties.NODE_TYPE).orElse(null);
		String inType = bVertex.<String>property(AAIProperties.NODE_TYPE).orElse(null);

		return this.getEdgeRule(type, outType, inType, label);


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
		String bVertexType =  b.<String>property(AAIProperties.NODE_TYPE).orElse(null);
		String label = rule.getLabel();
		MultiplicityRule multiplicityRule = rule.getMultiplicityRule();
		List<Edge> outEdges = traversalSource.V(a).outE(label).where(__.inV().has(AAIProperties.NODE_TYPE, bVertexType)).toList();
		List<Edge> inEdges = traversalSource.V(b).inE(label).where(__.outV().has(AAIProperties.NODE_TYPE, aVertexType)).toList();
		String detail = "";
		final String msg = "multiplicity rule violated: only one edge can exist with label: ";
		if (multiplicityRule.equals(MultiplicityRule.ONE2ONE)) {
			if (!inEdges.isEmpty() || !outEdges.isEmpty() ) {
				detail = msg + label + " between " + aVertexType + " and " + bVertexType;
			}
		} else if (multiplicityRule.equals(MultiplicityRule.ONE2MANY)) {
			if (!inEdges.isEmpty()) {
				detail = msg + label + " between " + aVertexType + " and " + bVertexType;
			}
		} else if (multiplicityRule.equals(MultiplicityRule.MANY2ONE)) {
			if (!outEdges.isEmpty()) {
				detail = msg + label + " between " + aVertexType + " and " + bVertexType;
			}
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
				where("from").is(nodeType).and(EdgeProperty.CONTAINS.toString()).is(DIRECTION_NOTATION)
				).or(where("to").is(nodeType).and(EdgeProperty.CONTAINS.toString()).is(NOT_DIRECTION_NOTATION));

		final List<Map<String, String>> rules = readRules(filter);
		final Set<EdgeRule> result = new HashSet<>();
		rules.forEach(item -> {
			verifyRule(item);
			result.add(buildRule(item));
		});

		return result;

	}

	private static class Helper {
		private static final EdgeRules INSTANCE = new EdgeRules();
		private static final Map<Version, EdgeRules> INSTANCEMAP = new ConcurrentHashMap<>();

		private Helper() {}

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
}
