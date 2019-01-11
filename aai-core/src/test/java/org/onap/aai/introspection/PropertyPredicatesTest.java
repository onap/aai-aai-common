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

import org.junit.Before;

import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Set;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class PropertyPredicatesTest extends AAISetup {

	private Loader loader;
	private ModelType introspectorFactoryType = ModelType.MOXY;
	private Introspector obj;
	
	@Before
	public void setup() throws Exception {
		loader = loaderFactory.createLoaderForVersion(introspectorFactoryType, schemaVersions.getDefaultVersion());
		obj = loader.introspectorFromName("generic-vnf");
	}
	
	@Test
	public void includeInTestGeneration() throws AAIUnknownObjectException {
		
		Set<String> props = obj.getProperties(PropertyPredicates.includeInTestGeneration());

		assertThat("props not found", props,
				not(hasItems("model-invariant-id", "model-version-id")));
	}
	
	@Test
	public void isVisible() throws AAIUnknownObjectException {
		
		Set<String> props = obj.getProperties(PropertyPredicates.isVisible());

		assertThat("props not found", props, hasItems("model-invariant-id", "model-version-id"));
	}
	
	@Test
	public void all() throws AAIUnknownObjectException {
		
		Set<String> props = obj.getProperties();

		assertThat("all found", props, hasItems("model-invariant-id", "model-version-id"));
	}
	

}
