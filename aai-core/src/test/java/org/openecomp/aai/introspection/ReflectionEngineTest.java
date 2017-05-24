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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBException;

import org.junit.BeforeClass;
import org.junit.Test;

import org.openecomp.aai.introspection.exceptions.AAIUnknownObjectException;
import org.openecomp.aai.restcore.CustomJacksonJaxBJsonProvider;

public class ReflectionEngineTest extends IntrospectorTestSpec {

	/**
	 * Configure.
	 */
	@BeforeClass
	public static void configure() {
		System.setProperty("AJSC_HOME", "./src/test/resources");
		System.setProperty("BUNDLECONFIG_DIR", "bundleconfig-local");
	}
	
	/**
	 * Container object.
	 *
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws AAIUnknownObjectException 
	 */
	@Test
	public void containerObject() throws InstantiationException, IllegalAccessException, ClassNotFoundException, AAIUnknownObjectException {
		Object javaObj = null;
		String className = "org.openecomp.aai.domain.yang.PortGroups";
		javaObj = Class.forName(className).newInstance();
        
        Introspector obj = IntrospectorFactory.newInstance(ModelType.POJO, javaObj);

//        this.containerTestSet(obj);
	}
}
