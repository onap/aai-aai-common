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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.onap.aai.AAISetup;

import java.util.HashSet;
import java.util.Set;

@Ignore("Not a used/flushed out feature")
public class JSONStrategyTest extends AAISetup {
    private JSONStrategy jsonStrategy;
    private JSONStrategy jsonStrategyContainer;
    private JSONStrategy jsonStrategyComplex;

    @Before
    public void setup() {
        try {
            JSONObject pserver = new JSONObject();
            pserver.put("hostname", "value1");
            pserver.put("numberofCpus", 4);
            jsonStrategy = new JSONStrategy(pserver, "pserver-type");

            // The values of this object are arrays containing JSONObjects
            JSONArray pservers = new JSONArray();
            pservers.add(pserver);
            JSONObject container = new JSONObject();
            container.put("pservers", pservers);
            jsonStrategyContainer = new JSONStrategy(container, "pservers-type");

            // The values of this object are JSONObjects
            JSONObject complex = new JSONObject();
            complex.put("pserver", pserver);
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
        Assert.assertEquals("org.json.simple.JSONObject", jsonStrategy.getJavaClassName());
        Assert.assertEquals("org.json.simple.JSONObject", jsonStrategyContainer.getJavaClassName());
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
