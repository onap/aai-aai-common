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
package org.onap.aai.workarounds;

import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import static org.junit.Assert.assertEquals;

public class RemoveDME2QueryParamsTest {

	private MultivaluedMap<String, String> hasParams;
	private MultivaluedMap<String, String> doesNotHaveParams;
	private RemoveDME2QueryParams removeParams = new RemoveDME2QueryParams();
	
	/**
	 * Setup.
	 */
	@Before
	public void setup() {
		hasParams = new MultivaluedHashMap<>();
		doesNotHaveParams = new MultivaluedHashMap<>();
		
		hasParams.add("version", "1");
		hasParams.add("envContext", "DEV");
		hasParams.add("routeOffer", "INT1");
		hasParams.add("test1", "peppermints");
		hasParams.add("test2", "amber");
		
		doesNotHaveParams.add("version", "1");
		doesNotHaveParams.add("envContext", "DEV");
		doesNotHaveParams.add("test1", "peppermints");
		doesNotHaveParams.add("test2", "amber");
		
	}
	
	/**
	 * Test removal.
	 */
	@Test
	public void testRemoval() {
		
		if (removeParams.shouldRemoveQueryParams(hasParams)) {
			removeParams.removeQueryParams(hasParams);
		}
		
		assertEquals("no version", false, hasParams.containsKey("version"));
		assertEquals("no envContext", false, hasParams.containsKey("envContext"));
		assertEquals("no routeOffer", false, hasParams.containsKey("routeOffer"));
		assertEquals("has test1", true, hasParams.containsKey("test1"));
		assertEquals("has test2", true, hasParams.containsKey("test2"));
		
	}
	
	/**
	 * Should not remove.
	 */
	@Test
	public void shouldNotRemove() {
		
		if (removeParams.shouldRemoveQueryParams(doesNotHaveParams)) {
			removeParams.removeQueryParams(doesNotHaveParams);
		}
		
		assertEquals("no version", true, doesNotHaveParams.containsKey("version"));
		assertEquals("no envContext", true, doesNotHaveParams.containsKey("envContext"));
		assertEquals("has test1", true, doesNotHaveParams.containsKey("test1"));
		assertEquals("has test2", true, doesNotHaveParams.containsKey("test2"));
	}
}
