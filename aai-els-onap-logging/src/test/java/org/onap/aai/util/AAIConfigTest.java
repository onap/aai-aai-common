/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 *  Modifications Copyright © 2018 IBM.
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

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.exceptions.AAIException;

import static org.junit.Assert.*;

public class AAIConfigTest {

    @Before
    public void setup() throws AAIException {
        AAIConfig.init();
    }

    @Test
    public void getValueWithDefaultTest() throws AAIException {
        assertEquals("default-value", AAIConfig.get("non-existing-key", "default-value"));
    }

    @Test
    public void getValueTest() throws AAIException {
        assertEquals("10", AAIConfig.get("aai.logging.maxStackTraceEntries"));
    }
    @Test
    public void getIntValueTest() throws AAIException {
        assertTrue(10 == AAIConfig.getInt("aai.logging.maxStackTraceEntries"));
    }

    @Test
    public void getIntValueWithDefaultTest() throws AAIException {
        assertTrue(9999 == AAIConfig.getInt("non-existing-key", "9999"));
    }

    @Test
    public void getNodeNameTest() throws AAIException {
        assertNotNull(AAIConfig.getNodeName());
    }

    @Test
    public void notEmptyTest() throws AAIException {
        String value = "test";
        assertFalse(AAIConfig.isEmpty(value));
    }

    @Test
    public void emptyTest() throws AAIException {
        String value = null;
        assertTrue(AAIConfig.isEmpty(value));
        value = "";
        assertTrue(AAIConfig.isEmpty(value));
    }
}
