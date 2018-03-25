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
	public void testGetDefaultBools() {
		HashMap<String,ArrayList<String>> res = AAIConfig.getDefaultBools();
		assertNotNull(res);
		assertEquals(6, res.size());
		assertEquals("in-maint", res.get("generic-vnf").get(0));
		assertEquals("is-closed-loop-disabled", res.get("generic-vnf").get(1));
		assertEquals("is-bound-to-vpn", res.get("l3-network").get(0));
		assertEquals("in-maint", res.get("pserver").get(0));
		assertEquals("dhcp-enabled", res.get("subnet").get(0));
		assertEquals("in-maint", res.get("vserver").get(0));
		assertEquals("is-closed-loop-disabled", res.get("vserver").get(1));
		assertEquals("in-maint", res.get("vnfc").get(0));
		assertEquals("is-closed-loop-disabled", res.get("vnfc").get(1));
	}

	@Test
	public void testGetConfigFile() {
		String res = AAIConfig.getConfigFile();
		assertNotNull(res);
		assertTrue(res.endsWith("aaiconfig.properties"));
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
	public void testGetServerProps() {
		Properties res = AAIConfig.getServerProps();
		assertNotNull(res);
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
