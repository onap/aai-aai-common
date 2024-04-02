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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.onap.aai.AAISetup;

@Disabled("Not a used/flushed out feature")
public class JSONStrategyTest extends AAISetup {
    private JSONStrategy jsonStrategy;
    private JSONStrategy jsonStrategyContainer;
    private JSONStrategy jsonStrategyComplex;

    @BeforeEach
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
        Assertions.assertEquals("value1", jsonStrategy.getValue("hostname"));
        Assertions.assertEquals(4, jsonStrategy.getValue("numberofCpus"));
        Assertions.assertEquals(1024, jsonStrategy.getValue("ramInMegabytes"));

    }

    @Test
    public void testGetMethods() {
        Assertions.assertEquals("pserver-type", jsonStrategy.getName());
        Assertions.assertEquals("pserver-type", jsonStrategy.getDbName());
        Assertions.assertEquals("", jsonStrategy.getGenericURI());
        Assertions.assertNull(jsonStrategy.getChildName());
        Assertions.assertEquals("key", jsonStrategy.preProcessKey("key"));
    }

    @Test
    public void getPropertiesTest() {
        Set<String> expected = new HashSet<>();
        expected.add("hostname");
        expected.add("numberofCpus");
        Assertions.assertEquals(expected, jsonStrategy.getProperties());
    }

    @Test
    public void getGenericTypeTest() {
        // If the values of this object are arrays, return the type within the array
        Assertions.assertEquals("class org.json.simple.JSONObject",
                jsonStrategyContainer.getGenericTypeClass("pservers").toString());
    }

    @Test
    public void getJavaClassNameTest() {
        Assertions.assertEquals("org.json.simple.JSONObject", jsonStrategy.getJavaClassName());
        Assertions.assertEquals("org.json.simple.JSONObject", jsonStrategyContainer.getJavaClassName());
    }

    @Test
    public void getTypeTest() {
        Assertions.assertEquals("java.lang.String", jsonStrategy.getType("hostname"));
        Assertions.assertEquals("java.lang.Integer", jsonStrategy.getType("numberofCpus"));
    }

    @Test
    public void isContainerTest() {
        Assertions.assertTrue(jsonStrategyContainer.isContainer());
    }

    @Test
    public void newInstanceOfPropertyTest() {
        Assertions.assertEquals("class org.json.simple.JSONArray",
                jsonStrategyContainer.newInstanceOfProperty("pservers").getClass().toString());
    }

    @Test
    public void newInvalidInstanceOfPropertyTest() {
        assertThrows(NullPointerException.class, () -> {
            Assertions.assertEquals(null, jsonStrategyContainer.newInstanceOfProperty("invalid").getClass().toString());
        });
    }

    @Test
    public void newInstanceOfNestedPropertyTest() {
        Assertions.assertEquals("class org.json.simple.JSONObject",
                jsonStrategyContainer.newInstanceOfNestedProperty("pservers").getClass().toString());
    }

    @Test
    public void newInvalidInstanceOfNestedPropertyTest() {
        assertThrows(NullPointerException.class, () -> {
            jsonStrategyContainer.newInstanceOfNestedProperty("invalid").getClass().toString();
        });
    }

    @Test
    public void isComplexTypeTest() {
        // Complex: The value of this key contains a JSONObject
        Assertions.assertTrue(jsonStrategyComplex.isComplexType("pserver"));
        Assertions.assertFalse(jsonStrategyContainer.isComplexType("pservers"));
        Assertions.assertFalse(jsonStrategy.isComplexType("hostname"));
    }

    @Test
    public void isComplexGenericTypeTest() {
        // Complex Generic: The value of this key contains an array of JSONObjects
        Assertions.assertTrue(jsonStrategyContainer.isComplexGenericType("pservers"));
        Assertions.assertFalse(jsonStrategyComplex.isComplexGenericType("pserver"));
        Assertions.assertFalse(jsonStrategy.isComplexGenericType("hostname"));
    }
}
