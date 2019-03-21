/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-18 AT&T Intellectual Property. All rights reserved.
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

package org.onap.aai.edges;

import static org.junit.Assert.*;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.edges.enums.EdgeType;

public class EdgeRuleQueryTest {
    private DocumentContext testRules;
    private String readStart = "$.rules.[?]";

    /* **** DATA SETUP **** */
    @Before
    public void setup() {
        InputStream is = getClass().getResourceAsStream("/edgeRules/test.json");

        Scanner scanner = new Scanner(is);
        String json = scanner.useDelimiter("\\Z").next();
        scanner.close();

        this.testRules = JsonPath.parse(json);
    }

    /* **** TESTS **** */
    @Test
    public void testFromToSingle() {
        // rule defined from quux to foo
        EdgeRuleQuery q = new EdgeRuleQuery.Builder("quux", "foo").build();
        List<Object> results = testRules.read(readStart, q.getFilter());

        assertTrue(results.size() == 1);
    }

    @Test
    public void testToFromSingle() {
        // rule defined from quux to foo, this is flipped
        EdgeRuleQuery q = new EdgeRuleQuery.Builder("foo", "quux").build();
        List<Object> results = testRules.read(readStart, q.getFilter());

        assertTrue(results.size() == 1);
    }

    @Test
    public void testFromToMultiple() {
        // rules have two from foo to bar
        EdgeRuleQuery q = new EdgeRuleQuery.Builder("foo", "bar").build();
        List<Object> results = testRules.read(readStart, q.getFilter());
        assertTrue(results.size() == 2);
    }

    @Test
    public void testToFromMultiple() {
        // rules have two from foo to bar
        EdgeRuleQuery q = new EdgeRuleQuery.Builder("bar", "foo").build();
        List<Object> results = testRules.read(readStart, q.getFilter());

        assertTrue(results.size() == 2);
        assertTrue(!(results.get(0).toString().equals(results.get(1).toString())));
    }

    @Test
    public void testJustFrom() {
        // there are 4 foo rules (foo>bar, foo>bar, foo>baz, quux>foo)
        EdgeRuleQuery q = new EdgeRuleQuery.Builder("foo").build();
        List<Object> results = testRules.read(readStart, q.getFilter());
        assertTrue(results.size() == 4);

        // there are 2 bar rules
        EdgeRuleQuery q2 = new EdgeRuleQuery.Builder("bar").build();
        List<Object> results2 = testRules.read(readStart, q2.getFilter());
        assertTrue(results2.size() == 2);
    }

    @Test
    public void testWithLabel() {
        // there are foo >eats> bar and foo >eatz> bar
        EdgeRuleQuery q = new EdgeRuleQuery.Builder("foo", "bar").label("eatz").build();
        List<Object> results = testRules.read(readStart, q.getFilter());
        assertTrue(results.size() == 1);
        assertTrue(results.get(0).toString().contains("eatz"));
    }

    @Test
    public void testCousin() {
        EdgeRuleQuery q = new EdgeRuleQuery.Builder("foo").edgeType(EdgeType.COUSIN).build();
        List<Object> results = testRules.read(readStart, q.getFilter());
        assertTrue(results.size() == 2);

        EdgeRuleQuery q2 =
            new EdgeRuleQuery.Builder("foo", "bar").edgeType(EdgeType.COUSIN).label("eats").build();
        List<Object> results2 = testRules.read(readStart, q2.getFilter());
        assertTrue(results2.size() == 1);
        assertTrue(results2.get(0).toString().contains("eats"));

        EdgeRuleQuery q3 =
            new EdgeRuleQuery.Builder("foo", "quux").edgeType(EdgeType.COUSIN).build();
        List<Object> results3 = testRules.read(readStart, q3.getFilter());
        assertTrue(results3.isEmpty());
    }

    @Test
    public void testTree() {
        EdgeRuleQuery q = new EdgeRuleQuery.Builder("foo").edgeType(EdgeType.TREE).build();
        List<Object> results = testRules.read(readStart, q.getFilter());
        assertTrue(results.size() == 2);
    }
}
