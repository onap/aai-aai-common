/*-
 * ============LICENSE_START=======================================================
 * org.openecomp.aai
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.aai.introspection;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openecomp.aai.introspection.exceptions.AAIUnknownObjectException;
import org.openecomp.aai.serialization.queryformats.QueryFormatTestHelper;
import org.openecomp.aai.util.AAIConstants;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

public class MoxyEngineTest extends IntrospectorTestSpec {

	/**
	 * Configure.
	 * @throws Exception 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	@BeforeClass
	public static void configure() throws NoSuchFieldException, SecurityException, Exception {
		System.setProperty("AJSC_HOME", ".");
		System.setProperty("BUNDLECONFIG_DIR", "bundleconfig-local");
		QueryFormatTestHelper.setFinalStatic(AAIConstants.class.getField("AAI_HOME_ETC_OXM"), "src/test/resources/org/openecomp/aai/introspection/");
	}
	
	/**
	 * Container object.
	 * @throws AAIUnknownObjectException
	 */
	@Test
	public void containerObject() throws AAIUnknownObjectException {
		
        Loader loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, Version.v9);
        
        Introspector obj = loader.introspectorFromName("port-groups");

        this.containerTestSet(obj);
        
	}
	
	
	
}
