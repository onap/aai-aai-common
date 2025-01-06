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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.aai.exceptions.AAIException;

public class AAIApplicationConfigTest {

    @BeforeEach
    public void setup() {
        AAIApplicationConfig.init();
    }

    @Test
    public void getValueWithDefaultTest() throws AAIException {
        assertEquals("default-value", AAIApplicationConfig.get("non-existing-key", "default-value"));
    }

    @Test
    public void getValueTest() throws AAIException {
        assertEquals("8446", AAIApplicationConfig.get("server.port"));
    }

    @Test
    public void getIntValueTest() throws AAIException {
        assertTrue(8446 == AAIApplicationConfig.getInt("server.port"));
    }

    @Test
    public void getIntValueWithDefaultTest() throws AAIException {
        assertTrue(9999 == AAIApplicationConfig.getInt("non-existing-key", "9999"));
    }
}
