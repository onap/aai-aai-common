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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.setup.Version;
import org.onap.aai.testutils.ConfigTranslatorForWiringTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SchemaLocationsBean.class, ConfigTranslatorForWiringTest.class, NodeIngestor.class})
@TestPropertySource(properties = {"schemaIngestPropLoc = src/test/resources/forWiringTests/schemaIngestWiringTest.properties"})
@SpringBootTest
public class NodeIngestorWiringTest {
	@Autowired
	NodeIngestor ni;
	
	@Test
	public void test() {
		DynamicJAXBContext ctx10 = ni.getContextForVersion(Version.V10);
		
		//should work bc Bar is valid in test_business_v10 schema
		DynamicEntity bar10 = ctx10.newDynamicEntity("Bar");
		bar10.set("barId","bar2");
		assertTrue("bar2".equals(bar10.get("barId")));
	}
}
