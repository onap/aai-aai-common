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
import org.onap.aai.domain.yang.CloudRegion;
import org.onap.aai.domain.yang.VolumeGroup;
import org.onap.aai.schema.enums.ObjectMetadata;

import static org.junit.Assert.assertEquals;

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

	@Test
	public void testGetWithBogusValue(){
		Introspector cloudregion = IntrospectorFactory.newInstance(ModelType.POJO, new CloudRegion());
		assertEquals(null, cloudregion.get("test"));
	}

	@Test
	public void testSetWithValue(){
		Introspector cloudregion = IntrospectorFactory.newInstance(ModelType.POJO, new CloudRegion());
		cloudregion.set("cloudOwner", "test");
		assertEquals("test", cloudregion.get("cloudOwner"));
	}

	@Test
	public void testHasProperty(){
		Introspector cloudregion = IntrospectorFactory.newInstance(ModelType.POJO, new CloudRegion());
		assertEquals( true, cloudregion.hasProperty("test"));
	}

	@Test
	public void testMethodsforExceptions(){
		PojoStrategy ps = new PojoStrategy(new CloudRegion());
		Exception ex  = null;
		Boolean exceptionThrownForKeys = false;
		Boolean exceptionThrownForProcessKeys = false;
		try{
			ps.findKey();
			ps.getKeys();
		}catch(Exception e){
			exceptionThrownForKeys= true;
		}
		try {
			ps.getProperties();
			ps.getRequiredProperties();
			ps.getClass("TEST");
			ps.getGenericTypeClass("TEST");
			ps.getJavaClassName();
			ps.getUnderlyingObject();
			ps.clone();
			ps.getChildName();
			ps.getObjectId();
			ps.getPropertyMetadata("TEST");
			ps.getName();
			ps.getVersion();
		}catch(Exception e){
			ex = e;
		}


		try{
			ps.preProcessKey("TEST/TEST/TEST");
		}catch(Exception e){
			exceptionThrownForProcessKeys = true;
		}
		try{
			ps.set("TEST", new Object());
		}catch(Exception e){

		}
		assertEquals(null, ex);
		assertEquals(true, exceptionThrownForKeys);
		assertEquals(true, exceptionThrownForProcessKeys);
	}


}
