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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class KeyValueListTest {
    KeyValueList kv;
    KeyValueList kv1;

    @Before
    public void setup() {
        String key = "key";
        String value = "value";
        kv = new KeyValueList();
        kv.setKey(key);
        kv.setValue(value);
        kv.setAdditionalProperty("name1", "val1");
        kv1 = new KeyValueList();
        kv1.setKey("key1");
        kv1.setValue("value1");
    }

    @Test
    public void getSetTest() {
        assertEquals("key", kv.getKey());
        assertEquals("value", kv.getValue());
    }

    @Test
    public void additionalPropertyTest() {
        assertEquals("Additional properties added", "val1",
            kv.getAdditionalProperties().get("name1"));
    }

    @Test
    public void hashCodeTest() {
        assertEquals("Hashing function returns the same code", kv.hashCode(), kv.hashCode());
        assertNotEquals("Hashing function returns different code for different objects",
            kv.hashCode(), kv1.hashCode());
    }

    @Test
    public void equalsTest() {
        KeyValueList kv2 = new KeyValueList();
        kv2.setKey("key");
        kv2.setValue("value");
        kv2.setAdditionalProperty("name1", "val1");
        assertTrue("Equal KeyValueList objects", kv.equals(kv2));
        assertFalse("Non-equal KeyValueList objects", kv.equals(kv1));
    }

    @Test
    public void toStringTest() {
        assertNotEquals("Different objects should return different strings", kv.toString(),
            kv1.toString());
    }
}
