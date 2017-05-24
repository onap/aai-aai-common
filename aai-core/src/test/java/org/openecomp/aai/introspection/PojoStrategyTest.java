/*-
 * ============LICENSE_START=======================================================
 * org.openecomp.aai
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.aai.introspection;

import static org.junit.Assert.*;

import org.junit.Test;

import org.openecomp.aai.domain.yang.CloudRegion;
import org.openecomp.aai.domain.yang.VolumeGroup;
import org.openecomp.aai.schema.enums.ObjectMetadata;

public class PojoStrategyTest {

	@Test
	public void getMetadataTest() {
		Introspector cloudregion = IntrospectorFactory.newInstance(ModelType.POJO, new CloudRegion());
		assertEquals("cloud-infrastructure", cloudregion.getMetadata(ObjectMetadata.NAMESPACE));
		assertEquals("cloud-regions", cloudregion.getMetadata(ObjectMetadata.CONTAINER));
		
		Introspector volumegroup = IntrospectorFactory.newInstance(ModelType.POJO, new VolumeGroup());
		assertEquals("cloud-region", volumegroup.getMetadata(ObjectMetadata.DEPENDENT_ON));
		assertEquals("", volumegroup.getMetadata(ObjectMetadata.NAMESPACE));
	}

}
