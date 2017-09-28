/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai.logging;

import org.junit.Test;
import org.onap.aai.logging.CNName;
import org.onap.aai.logging.CustomLogPatternLayout;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CustomLogPatternLayoutTest {
	
	/**
	 * Test null when defaultConverterMap doesn't have corresponding entry.
	 */
	@Test
	public void testNull(){
		String s = CustomLogPatternLayout.defaultConverterMap.get("z");
		assertFalse("Entry not found for key 'z'", CNName.class.getName().equals(s));
	}
	
	/**
	 * Test defaultConverterMap when valid entry exists.
	 */
	@Test
	public void testEntryFor_Z(){
		CustomLogPatternLayout layout = new CustomLogPatternLayout();
		String s = CustomLogPatternLayout.defaultConverterMap.get("z");
		assertTrue("Entry not found for key 'z'", CNName.class.getName().equals(s));
	}

}
