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
package org.onap.aai.introspection;

import org.junit.Test;
import org.onap.aai.db.props.AAIProperties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import static org.junit.Assert.*;

public class PojoInjestorTest {

	@Test
	public void getVersionTest() {
		String latestVersion = "org.onap.aai.yang.VnfImage";
		PojoInjestor testPI = new PojoInjestor();
		assertEquals("", AAIProperties.LATEST, testPI.getVersion(latestVersion));
		
		String oldVersion = "org.onap.aai.yang.v8.VnfImage";
		assertEquals("", Version.v8, testPI.getVersion(oldVersion));
	}

	@Test
	public void getContextForVersionTest() {
		PojoInjestor testPI = new PojoInjestor();
		JAXBContext context = testPI.getContextForVersion(Version.v9);
		try {
			Marshaller marshaller = context.createMarshaller();
			//this will fail if the context wasn't initialized successfully (I think)
			marshaller.setProperty(org.eclipse.persistence.jaxb.MarshallerProperties.MEDIA_TYPE, "application/json");
		} catch (JAXBException e) {
			e.printStackTrace();
			fail("failed on setting marshaller property");
		}
		//if we get to here that means everything went ok
		assertTrue(true);
	}
}
