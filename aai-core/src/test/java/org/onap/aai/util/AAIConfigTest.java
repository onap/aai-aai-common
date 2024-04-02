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

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.exceptions.AAIException;

public class AAIConfigTest extends AAISetup {

    @BeforeAll
    public static void setUp() throws AAIException {
        AAIConfig.init();
    }

    @Test
    public void testGetConfigFile() {
        String res = AAIConfig.getConfigFile();
        assertNotNull(res);
        assertTrue(res.endsWith("aaiconfig.properties"));
    }

    @Test
    public void testGetStringString() {
        String res = AAIConfig.get("aai.notificationEvent.default.sourceName", "somerandomvalue");
        assertNotNull(res);
        assertEquals("aai", res);
    }

    @Test
    public void testGetStringStringReturnDefaultvalue() {
        String res = AAIConfig.get("key", "result");
        assertNotNull(res);
        assertEquals("result", res);
    }

    @Test
    public void testGetStringInvalidKey() throws AAIException {
        assertThrows(AAIException.class, () -> {
            AAIConfig.get("key");
        });
    }

    @Test
    public void testGetStringEmptyResponse() throws AAIException {
        assertThrows(AAIException.class, () -> {
            AAIConfig.get("aai.response.null");
        });
    }

    @Test
    public void testGetStringReloadConfig() throws AAIException {
        String res = AAIConfig.get("aai.config.nodename");
        assertNotNull(res);
        assertEquals(AAIConfig.getNodeName(), res);
    }

    @Test
    public void testGetStringPassword() throws AAIException {
        String res = AAIConfig.get("aai.example.passwd");
        assertNotNull(res);
        assertEquals("changeit", res);
    }

    @Test
    public void testGetIntInvalidInput() throws AAIException {
        assertThrows(NumberFormatException.class, () -> {
            AAIConfig.getInt("aai.example.string");
        });
    }

    @Test
    public void testGetInt() throws AAIException {
        int res = AAIConfig.getInt("aai.example.int");
        assertEquals(7748, res);
    }

    @Test
    public void testGetNodeName() throws UnknownHostException {
        InetAddress ip = InetAddress.getLocalHost();
        String res = AAIConfig.getNodeName();
        assertNotNull(res);
        assertEquals(ip.getHostName(), res);
    }

    @Test
    public void testIsEmpty() {
        boolean res = AAIConfig.isEmpty("hllo world");
        assertFalse(res);
    }

    @Test
    public void testIsEmptyEmpty() {
        boolean res = AAIConfig.isEmpty("");
        assertTrue(res);
    }

    @Test
    public void testIsEmptyNull() {
        boolean res = AAIConfig.isEmpty(null);
        assertTrue(res);
    }

}
