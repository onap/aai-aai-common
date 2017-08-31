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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import org.openecomp.aai.introspection.exceptions.AAIUnknownObjectException;
import org.openecomp.aai.serialization.queryformats.QueryFormatTestHelper;
import org.openecomp.aai.util.AAIConstants;

@Ignore
public class PropertyPredicatesTest {

	
	private final static Version version = Version.v10;
	private static Loader loader;
	private final static ModelType introspectorFactoryType = ModelType.MOXY;
	private static Introspector obj;
	
	@BeforeClass
	public static void setup() throws NoSuchFieldException, SecurityException, Exception {
		System.setProperty("AJSC_HOME", "./src/test/resources/");
		System.setProperty("BUNDLECONFIG_DIR", "bundleconfig-local");
		QueryFormatTestHelper.setFinalStatic(AAIConstants.class.getField("AAI_HOME_ETC_OXM"), "src/test/resources/org/openecomp/aai/introspection/");
		loader = LoaderFactory.createLoaderForVersion(introspectorFactoryType, version);
		obj = loader.introspectorFromName("test-object");

	}
	
	@Test
	public void includeInTestGeneration() throws AAIUnknownObjectException {
		
		Set<String> props = obj.getProperties(PropertyPredicates.includeInTestGeneration());
		
		assertThat("props not found", props, 
				not(hasItems("persona-model-ver", "not-visible-test-element", "model-invariant-id", "model-version-id")));
	}
	
	@Test
	public void isVisible() throws AAIUnknownObjectException {
		
		Set<String> props = obj.getProperties(PropertyPredicates.isVisible());
		
		assertThat("props not found", props, not(hasItems("persona-model-ver")));
	}
	
	@Test
	public void all() throws AAIUnknownObjectException {
		
		Set<String> props = obj.getProperties();
		
		assertThat("all found", props, hasItems("persona-model-ver", "not-visible-test-element"));
	}
	
	
}
