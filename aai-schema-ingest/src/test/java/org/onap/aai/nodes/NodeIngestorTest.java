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
 *   http://www.apache.org/licenses/LICENSE-2.0
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

package org.onap.aai.nodes;

import static org.junit.Assert.*;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.setup.Version;
import org.onap.aai.testutils.TestUtilConfigTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SchemaLocationsBean.class, TestUtilConfigTranslator.class, NodeIngestor.class})
@SpringBootTest
public class NodeIngestorTest {
	@Autowired
	NodeIngestor ni;
	
	//set thrown.expect to whatever a specific test needs
	//this establishes a default of expecting no exceptions to be thrown
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void testGetContextForVersion() {
		DynamicJAXBContext ctx10 = ni.getContextForVersion(Version.V10);
		
		//should work bc Foo is valid in test_network_v10 schema
		DynamicEntity foo10 = ctx10.newDynamicEntity("Foo");
		foo10.set("fooId","bar");
		assertTrue("bar".equals(foo10.get("fooId")));
		
		//should work bc Bar is valid in test_business_v10 schema
		DynamicEntity bar10 = ctx10.newDynamicEntity("Bar");
		bar10.set("barId","bar2");
		assertTrue("bar2".equals(bar10.get("barId")));
		
		
		DynamicJAXBContext ctx11 = ni.getContextForVersion(Version.V11);
		
		//should work bc Foo.quantity is valid in test_network_v11 schema
		DynamicEntity foo11 = ctx11.newDynamicEntity("Foo");
		foo11.set("quantity","12");
		assertTrue("12".equals(foo11.get("quantity")));
		
		DynamicEntity quux11 = ctx11.newDynamicEntity("Quux");
		quux11.set("qManagerName","some guy");
		assertTrue("some guy".equals(quux11.get("qManagerName")));
		
		
		thrown.expect(IllegalArgumentException.class);
		//should fail bc Quux not in v10 test schema
		ctx10.newDynamicEntity("Quux");
	}
	
	@Test
	public void testHasNodeType() {
		assertTrue(ni.hasNodeType("foo", Version.V11));
		assertTrue(ni.hasNodeType("quux", Version.V11));
		assertFalse(ni.hasNodeType("quux", Version.V10));
	}
}
