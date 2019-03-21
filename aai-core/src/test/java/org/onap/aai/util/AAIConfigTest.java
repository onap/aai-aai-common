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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.eclipse.jetty.util.security.Password;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.exceptions.AAIException;

public class AAIConfigTest extends AAISetup {

    @BeforeClass
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

    @Test(expected = AAIException.class)
    public void testGetStringInvalidKey() throws AAIException {
        AAIConfig.get("key");
    }

    @Test(expected = AAIException.class)
    public void testGetStringEmptyResponse() throws AAIException {
        AAIConfig.get("aai.response.null");
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

    @Test(expected = NumberFormatException.class)
    public void testGetIntInvalidInput() throws AAIException {
        AAIConfig.getInt("aai.example.string");
    }

    @Test
    public void testGetInt() throws AAIException {
        int res = AAIConfig.getInt("aai.example.int");
        assertNotNull(res);
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
