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

package org.onap.aai.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class KeyValueListTest {
    KeyValueList kv;
    KeyValueList kv1;

    @BeforeEach
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
        assertEquals("val1", kv.getAdditionalProperties().get("name1"), "Additional properties added");
    }

    @Test
    public void hashCodeTest() {
        assertEquals(kv.hashCode(), kv.hashCode(), "Hashing function returns the same code");
        assertNotEquals(kv.hashCode(), kv1.hashCode(), "Hashing function returns different code for different objects");
    }

    @Test
    public void equalsTest() {
        KeyValueList kv2 = new KeyValueList();
        kv2.setKey("key");
        kv2.setValue("value");
        kv2.setAdditionalProperty("name1", "val1");
        assertTrue(kv.equals(kv2), "Equal KeyValueList objects");
        assertFalse(kv.equals(kv1), "Non-equal KeyValueList objects");
    }

    @Test
    public void toStringTest() {
        assertNotEquals(kv.toString(), kv1.toString(), "Different objects should return different strings");
    }
}
