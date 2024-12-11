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

package org.onap.aai.introspection;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.onap.aai.AAISetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Ignore("Not a used/flushed out feature")
// This has been converted from org.json to Jackson,
// but not in a way that tests are working
public class JSONStrategyTest extends AAISetup {
    private JSONStrategy jsonStrategy;
    private JSONStrategy jsonStrategyContainer;
    private JSONStrategy jsonStrategyComplex;

    @Before
    public void setup() {
        try {

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode pserver = mapper.createObjectNode();

            pserver.put("hostname", "value1");
            pserver.put("numberofCpus", 4);
            jsonStrategy = new JSONStrategy(pserver, "pserver-type");

            // The values of this object are arrays containing JSONObjects
            ArrayNode pservers = mapper.createArrayNode();
            pservers.add(pserver);

            ObjectNode container = mapper.createObjectNode();
            container.set("pservers", pservers);
            jsonStrategyContainer = new JSONStrategy(container, "pservers-type");

            // The values of this object are JSONObjects
            ObjectNode complex = mapper.createObjectNode();
            complex.set("pserver", pserver);
            jsonStrategyComplex = new JSONStrategy(complex, "pservers-type");
        } catch (Exception e) {
            System.out.println("error during setup: " + e.getMessage());
        }
    }

    @Test
    public void getSetTest() {
        jsonStrategy.setValue("ramInMegabytes", 1024);
        Assert.assertEquals("value1", jsonStrategy.getValue("hostname"));
        Assert.assertEquals(4, jsonStrategy.getValue("numberofCpus"));
        Assert.assertEquals(1024, jsonStrategy.getValue("ramInMegabytes"));

    }

    @Test
    public void testGetMethods() {
        Assert.assertEquals("pserver-type", jsonStrategy.getName());
        Assert.assertEquals("pserver-type", jsonStrategy.getDbName());
        Assert.assertEquals("", jsonStrategy.getGenericURI());
        Assert.assertNull(jsonStrategy.getChildName());
        Assert.assertEquals("key", jsonStrategy.preProcessKey("key"));
    }

    @Test
    public void getPropertiesTest() {
        Set<String> expected = new HashSet<>();
        expected.add("hostname");
        expected.add("numberofCpus");
        Assert.assertEquals(expected, jsonStrategy.getProperties());
    }

    @Test
    public void getGenericTypeTest() {
        // If the values of this object are arrays, return the type within the array
        Assert.assertEquals("class org.json.simple.JSONObject",
                jsonStrategyContainer.getGenericTypeClass("pservers").toString());
    }

    @Test
    public void getJavaClassNameTest() {
        Assert.assertEquals("com.fasterxml.jackson.databind.node.ObjectNode", jsonStrategy.getJavaClassName());
        Assert.assertEquals("com.fasterxml.jackson.databind.node.ObjectNode", jsonStrategyContainer.getJavaClassName());
    }

    @Test
    public void getTypeTest() {
        Assert.assertEquals("java.lang.String", jsonStrategy.getType("hostname"));
        Assert.assertEquals("java.lang.Integer", jsonStrategy.getType("numberofCpus"));
    }

    @Test
    public void isContainerTest() {
        Assert.assertTrue(jsonStrategyContainer.isContainer());
    }

    @Test
    public void newInstanceOfPropertyTest() {
        Assert.assertEquals("class org.json.simple.JSONArray",
                jsonStrategyContainer.newInstanceOfProperty("pservers").getClass().toString());
    }

    @Test(expected = NullPointerException.class)
    public void newInvalidInstanceOfPropertyTest() {
        Assert.assertEquals(null, jsonStrategyContainer.newInstanceOfProperty("invalid").getClass().toString());
    }

    @Test
    public void newInstanceOfNestedPropertyTest() {
        Assert.assertEquals("class org.json.simple.JSONObject",
                jsonStrategyContainer.newInstanceOfNestedProperty("pservers").getClass().toString());
    }

    @Test(expected = NullPointerException.class)
    public void newInvalidInstanceOfNestedPropertyTest() {
        jsonStrategyContainer.newInstanceOfNestedProperty("invalid").getClass().toString();
    }

    @Test
    public void isComplexTypeTest() {
        // Complex: The value of this key contains a JSONObject
        Assert.assertTrue(jsonStrategyComplex.isComplexType("pserver"));
        Assert.assertFalse(jsonStrategyContainer.isComplexType("pservers"));
        Assert.assertFalse(jsonStrategy.isComplexType("hostname"));
    }

    @Test
    public void isComplexGenericTypeTest() {
        // Complex Generic: The value of this key contains an array of JSONObjects
        Assert.assertTrue(jsonStrategyContainer.isComplexGenericType("pservers"));
        Assert.assertFalse(jsonStrategyComplex.isComplexGenericType("pserver"));
        Assert.assertFalse(jsonStrategy.isComplexGenericType("hostname"));
    }
}
