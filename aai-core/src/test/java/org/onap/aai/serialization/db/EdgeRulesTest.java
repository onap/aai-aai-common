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


import org.apache.tinkerpop.gremlin.structure.Direction;
import org.junit.Test;
import org.onap.aai.AAISetup;
import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Set;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Version;
import org.onap.aai.serialization.db.exceptions.EdgeMultiplicityException;
import org.onap.aai.serialization.db.exceptions.NoEdgeRuleFoundException;

import com.google.common.collect.Multimap;

public class EdgeRulesTest extends AAISetup {

	//set thrown.expect to whatever a specific test needs
	//this establishes a default of expecting no exceptions to be thrown
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void verifyOutDirection() throws AAIException, NoEdgeRuleFoundException {
		EdgeRules rules = EdgeRules.getInstance();
		EdgeRule rule = rules.getEdgeRule(EdgeType.TREE, "cloud-region", "flavor");
		
		assertEquals("out direction", rule.getDirection(), Direction.OUT);
	}
	
	@Test
	public void verifyOutFlippedDirection() throws AAIException, NoEdgeRuleFoundException {
		EdgeRules rules = EdgeRules.getInstance();
		EdgeRule rule = rules.getEdgeRule(EdgeType.TREE, "flavor", "cloud-region");
		
		assertEquals("in direction", rule.getDirection(), Direction.IN);
	}
	
	@Test
	public void verifyInDirection() throws AAIException, NoEdgeRuleFoundException {
		EdgeRules rules = EdgeRules.getInstance();
		EdgeRule rule = rules.getEdgeRule(EdgeType.COUSIN, "model-ver", "model-element");
		
		assertEquals("in direction", rule.getDirection(), Direction.IN);
	}
	
	@Test
	public void verifyInFlippedDirection() throws AAIException, NoEdgeRuleFoundException {
		EdgeRules rules = EdgeRules.getInstance();
		EdgeRule rule = rules.getEdgeRule(EdgeType.COUSIN, "model-element", "model-ver");
		
		assertEquals("out direction", rule.getDirection(), Direction.OUT);
	}
	@Test
	public void verifyMultipleGet() throws AAIException {
		EdgeRules rules = EdgeRules.getInstance();
		Map<String, EdgeRule> ruleMap = rules.getEdgeRules("model-element", "model-ver");
		assertEquals("has isA rule", "isA", ruleMap.get("isA").getLabel());
		assertEquals("has startsWith rule", "startsWith", ruleMap.get("startsWith").getLabel());
	}
	
	@Test
	public void verifyMultipleGetSingleRule() throws AAIException {
		EdgeRules rules = EdgeRules.getInstance();
		Map<String, EdgeRule> ruleMap = rules.getEdgeRules("availability-zone", "complex");
		assertEquals("has groupsResourcesIn rule", "groupsResourcesIn", ruleMap.get("groupsResourcesIn").getLabel());
	}
	
	@Test
	public void verifyOldEdgeRule() throws AAIException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		assertEquals(true, EdgeRules.getInstance().hasEdgeRule("model-element", "model-ver"));
		assertEquals(true, EdgeRules.getInstance(Version.v8).hasEdgeRule("pserver", "complex"));
		assertEquals(false, EdgeRules.getInstance(Version.v8).hasEdgeRule("model-element", "model-ver"));
	}

	@Test
	public void hasEdgeRuleVertexTest() {
		Graph graph = TinkerGraph.open();
		Vertex v1 = graph.addVertex("aai-node-type", "cloud-region");
		Vertex v2 = graph.addVertex("aai-node-type", "tenant");
		assertEquals(true, EdgeRules.getInstance().hasEdgeRule(v1, v2));
	}

	@Test
	public void getEdgeRuleByTypeAndVertices() throws AAIException {
		Graph graph = TinkerGraph.open();
		Vertex v1 = graph.addVertex("aai-node-type", "cloud-region");
		Vertex v2 = graph.addVertex("aai-node-type", "tenant");
		EdgeRules rules = EdgeRules.getInstance();
		EdgeRule rule = rules.getEdgeRule(EdgeType.TREE, v1, v2);
		assertEquals(true, "OUT".equalsIgnoreCase(rule.getContains()));
		assertEquals(true, "NONE".equalsIgnoreCase(rule.getDeleteOtherV()));
		assertEquals(true, MultiplicityRule.ONE2MANY.equals(rule.getMultiplicityRule()));
		assertEquals(true,  "IN".equalsIgnoreCase(rule.getServiceInfrastructure()));
		assertEquals(true, "OUT".equalsIgnoreCase(rule.getPreventDelete()));
	}

	@Test
	public void addTreeEdgeTest() throws AAIException {
		Graph graph = TinkerGraph.open();
		Vertex v1 = graph.addVertex(T.id, "1", "aai-node-type", "cloud-region");
		Vertex v2 = graph.addVertex(T.id, "10", "aai-node-type", "tenant");
		EdgeRules rules = EdgeRules.getInstance();
		GraphTraversalSource g = graph.traversal();
		rules.addTreeEdge(g, v1, v2);
		assertEquals(true, g.V(v1).out("has").has("aai-node-type", "tenant").hasNext());

		Vertex v3 = graph.addVertex(T.id, "2", "aai-node-type", "cloud-region");
		assertEquals(null, rules.addTreeEdgeIfPossible(g, v3, v2));
	}

	@Test
	public void addCousinEdgeTest() throws AAIException {
		Graph graph = TinkerGraph.open();
		Vertex v1 = graph.addVertex(T.id, "1", "aai-node-type", "flavor");
		Vertex v2 = graph.addVertex(T.id, "10", "aai-node-type", "vserver");
		EdgeRules rules = EdgeRules.getInstance(Version.getLatest());
		GraphTraversalSource g = graph.traversal();
		rules.addEdge(g, v1, v2);
		assertEquals(true, g.V(v2).out("hasFlavor").has("aai-node-type", "flavor").hasNext());

		Vertex v3 = graph.addVertex(T.id, "2", "aai-node-type", "flavor");
		assertEquals(null, rules.addEdgeIfPossible(g, v3, v2));
	}

	@Test
	public void multiplicityViolationTest() throws AAIException {
		thrown.expect(EdgeMultiplicityException.class);
		thrown.expectMessage("multiplicity rule violated: only one edge can exist with label: uses between vf-module and volume-group");

		Graph graph = TinkerGraph.open();
		Vertex v1 = graph.addVertex(T.id, "1", "aai-node-type", "vf-module");
		Vertex v2 = graph.addVertex(T.id, "10", "aai-node-type", "volume-group");
		EdgeRules rules = EdgeRules.getInstance(Version.getLatest());
		GraphTraversalSource g = graph.traversal();

		rules.addEdge(g, v2, v1);
		Vertex v3 = graph.addVertex(T.id, "3", "aai-node-type", "vf-module");
		rules.addEdge(g, v2, v3);
	}

	@Test
	public void getChildrenTest() {
		EdgeRules rules = EdgeRules.getInstance("/dbedgerules/DbEdgeRules_test.json");
		Set<EdgeRule> children = rules.getChildren("foo");
		assertEquals(2, children.size());
		boolean sawBazRule = false;
		boolean sawQuuxRule = false;
		for (EdgeRule r : children) {
			if ("isVeryHappyAbout".equals(r.getLabel())) {
				sawBazRule = true;
			} else if ("dancesWith".equals(r.getLabel())) {
				sawQuuxRule = true;
			}
		}
		assertEquals(true, sawBazRule && sawQuuxRule);
	}

	@Test
	public void getAllRulesTest() {
		EdgeRules rules = EdgeRules.getInstance("/dbedgerules/DbEdgeRules_test.json");
		Multimap<String, EdgeRule> allRules = rules.getAllRules();
		assertEquals(3, allRules.size());
		assertEquals(true, allRules.containsKey("foo|bar"));
		assertEquals(true, allRules.containsKey("foo|bar"));
		assertEquals(true, allRules.containsKey("quux|foo"));
	}

	@Test
	public void getAllRulesMissingPropertyTest() {
		EdgeRules rules = EdgeRules.getInstance("/dbedgerules/DbEdgeRules_test_broken.json");

		thrown.expect(RuntimeException.class);
		thrown.expectMessage("org.onap.aai.exceptions.AAIException: Rule between foo and bar is missing property delete-other-v.");
		rules.getAllRules();
	}

	@Test
	public void getChildrenMissingPropertyTest() {
		EdgeRules rules = EdgeRules.getInstance("/dbedgerules/DbEdgeRules_test_broken.json");

		thrown.expect(RuntimeException.class);
		thrown.expectMessage("org.onap.aai.exceptions.AAIException: Rule between quux and foo is missing property SVC-INFRA.");
		rules.getChildren("foo");
	}

	@Test
	public void getEdgeRuleMissingPropertyTest() throws AAIException {
		EdgeRules rules = EdgeRules.getInstance("/dbedgerules/DbEdgeRules_test_broken.json");

		thrown.expect(RuntimeException.class);
		thrown.expectMessage("org.onap.aai.exceptions.AAIException: Rule between quux and foo is missing property SVC-INFRA.");
		rules.getEdgeRules("foo", "quux");
	}

	@Test
	public void verifyAllRules() {
		// This will cause every rule in the real json files to be verified
		// so if any required properties are missing, the verification builds
		// will catch it and incorrect rules can't get merged in.
		for (Version v : Version.values()) {
			EdgeRules rules = EdgeRules.getInstance(v);
			rules.getAllRules();
		}
	}
}
