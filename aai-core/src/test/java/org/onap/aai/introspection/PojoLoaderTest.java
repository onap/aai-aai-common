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
package org.onap.aai.introspection;

import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.domain.yang.v9.VnfImage;
import org.onap.aai.introspection.exceptions.AAIUnmarshallingException;
import org.onap.aai.restcore.MediaType;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PojoLoaderTest extends AAISetup {


	@Test
	public void test() {
		Loader pojoLoader = LoaderFactory.createLoaderForVersion(ModelType.POJO, Version.v9);
		String payload = "{\"vnf-image-uuid\":\"myuuid\",\"application\":\"testApp\",\"application-vendor\":\"testVendor\",\"application-version\":\"versionTest\"}";
		try {
			Introspector intro = pojoLoader.unmarshal("vnf-image", payload, MediaType.APPLICATION_JSON_TYPE);
			VnfImage myVnfImage = (VnfImage) intro.getUnderlyingObject();
			assertTrue("myuuid".equals(myVnfImage.getVnfImageUuid()));
			assertTrue("testApp".equals(myVnfImage.getApplication()));
			assertTrue("testVendor".equals(myVnfImage.getApplicationVendor()));
			assertTrue("versionTest".equals(myVnfImage.getApplicationVersion()));
		} catch (AAIUnmarshallingException e) {
			e.printStackTrace();
			fail("AAIUnmarshallingException thrown");
		}
	}

}
