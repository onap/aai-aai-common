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

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.junit.BeforeClass;
import org.junit.Test;

import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.serialization.db.exceptions.NoEdgeRuleFoundException;

public class EdgeRulesTest {

	@BeforeClass
	public static void setup() {
		System.setProperty("AJSC_HOME", ".");
		System.setProperty("BUNDLECONFIG_DIR", "src/test/resources/bundleconfig-local");
	}
	
	
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
}
