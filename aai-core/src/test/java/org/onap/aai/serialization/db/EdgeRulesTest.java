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
 * http://www.apache.org/licenses/LICENSE-2.0
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

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

import com.google.common.collect.Multimap;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onap.aai.AAISetup;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.edges.EdgeRule;
import org.onap.aai.edges.EdgeRuleQuery;
import org.onap.aai.edges.enums.EdgeType;
import org.onap.aai.edges.exceptions.AmbiguousRuleChoiceException;
import org.onap.aai.edges.exceptions.EdgeRuleNotFoundException;
import org.onap.aai.setup.SchemaVersion;
import org.springframework.beans.factory.annotation.Autowired;

public class EdgeRulesTest extends AAISetup {

    // set thrown.expect to whatever a specific test needs
    // this establishes a default of expecting no exceptions to be thrown
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private EdgeIngestor edgeIngestor;

    @Test
    public void verifyOutDirection()
        throws EdgeRuleNotFoundException, AmbiguousRuleChoiceException {

        EdgeRuleQuery ruleQuery =
            new EdgeRuleQuery.Builder("cloud-region", "flavor").edgeType(EdgeType.TREE).build();

        EdgeRule rule = edgeIngestor.getRule(ruleQuery);

        assertEquals("out direction", rule.getDirection(), Direction.IN);
    }

    @Test
    public void verifyOutFlippedDirection()
        throws EdgeRuleNotFoundException, AmbiguousRuleChoiceException {

        EdgeRuleQuery ruleQuery =
            new EdgeRuleQuery.Builder("flavor", "cloud-region").edgeType(EdgeType.TREE).build();

        EdgeRule rule = edgeIngestor.getRule(ruleQuery);

        assertEquals("in direction", rule.getDirection(), Direction.OUT);
    }

    @Test
    public void verifyMultipleGet() throws EdgeRuleNotFoundException {

        EdgeRuleQuery ruleQuery =
            new EdgeRuleQuery.Builder("model-element", "model-ver").edgeType(EdgeType.TREE).build();

        Multimap<String, EdgeRule> ruleMap = edgeIngestor.getRules(ruleQuery);

        for (EdgeRule edgeRule : ruleMap.get("model|model-ver")) {
            assertEquals("has isA rule", "org.onap.relationships.inventory.IsA",
                edgeRule.getLabel());
        }

    }

    @Test
    public void verifyAllRules() throws EdgeRuleNotFoundException {

        // This will cause every rule in the real json files to be verified
        // so if any required properties are missing, the verification builds
        // will catch it and incorrect rules can't get merged in.
        for (SchemaVersion v : schemaVersions.getVersions()) {
            Multimap<String, EdgeRule> all =
                edgeIngestor.getAllRules(schemaVersions.getDefaultVersion());

            // this part verifies the default properties
            // 1) can have only at most 1 containment edge between same node type pair
            // 2) if there is at least 1 cousin edge, there must be exactly 1 cousin edge with
            // default=true
            for (String key : all.keySet()) {

                Collection<EdgeRule> edgeRuleCollection = all.get(key);

                boolean foundContainment = false; // can have at most 1 containment rel btwn same
                                                  // pair of node types
                boolean foundCousin = false;
                boolean cousinDefault = false; // if there is a cousin edge there must be at least 1
                                               // default cousin defined
                Set<String> labels = new HashSet<>(); // all edges between the same pair must have
                                                      // different labels
                int cousinCount = 0;

                for (EdgeRule rule : edgeRuleCollection) {
                    EdgeRule match = rule;
                    // check containment
                    if (!("NONE".equals(match.getContains()))) {
                        if (foundContainment) {
                            fail("more than one containment edge defined for " + v.toString() + " "
                                + key);
                        } else {
                            foundContainment = true;
                        }
                    } else { // check cousin stuff
                        foundCousin = true;
                        cousinCount++;
                        if (match.isDefault()) {
                            if (!cousinDefault) {
                                cousinDefault = true;
                            } else {
                                fail("more than one cousin edge defined as default for "
                                    + v.toString() + " " + key);
                            }
                        }
                    }

                    // check labels
                    String label = match.getLabel();
                    if (labels.contains(label)) {
                        fail("same label found for multiple edges for " + v.toString() + " " + key);
                    } else {
                        labels.add(label);
                    }
                }
                if (foundCousin && !cousinDefault && cousinCount > 1) {
                    fail(
                        "there is at least one cousin edge but none are designated the default for "
                            + v.toString() + " " + key);
                }
            }
        }
    }
}
