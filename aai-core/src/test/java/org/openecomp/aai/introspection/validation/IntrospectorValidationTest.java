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

package org.openecomp.aai.introspection.validation;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Introspector;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.introspection.LoaderFactory;
import org.openecomp.aai.introspection.ModelType;
import org.openecomp.aai.introspection.Version;
import org.openecomp.aai.introspection.tools.IntrospectorValidator;
import org.openecomp.aai.introspection.tools.Issue;
import org.openecomp.aai.introspection.tools.IssueType;
import org.openecomp.aai.serialization.queryformats.QueryFormatTestHelper;
import org.openecomp.aai.util.AAIConstants;

public class IntrospectorValidationTest {

	
	private final static Version version = Version.v10;
	private final static ModelType introspectorFactoryType = ModelType.MOXY;
	private static Loader loader;
	private IntrospectorValidator validator;
	@BeforeClass
	public static void setUp() throws NoSuchFieldException, SecurityException, Exception {
		System.setProperty("AJSC_HOME", ".");
		System.setProperty("BUNDLECONFIG_DIR", "bundleconfig-local");
		QueryFormatTestHelper.setFinalStatic(AAIConstants.class.getField("AAI_HOME_ETC_OXM"), "src/test/resources/org/openecomp/aai/introspection/");
		
		loader = LoaderFactory.createLoaderForVersion(introspectorFactoryType, version);

	}
	@Before
	public void createValidator() {
		validator = new IntrospectorValidator.Builder()
				.validateRequired(false)
				.restrictDepth(10000)
				.build();
	}
	@Ignore
	@Test
	public void verifySuccessWhenEmpty() throws AAIException {
		Introspector obj = loader.introspectorFromName("test-object");
		obj.setValue("vnf-id", "key1");
		validator.validate(obj);
		List<Issue> issues = validator.getIssues();
		assertEquals("no issues found", true, issues.isEmpty());
	}

	@Ignore
	@Test
	public void verifyRequiresSingleFieldFailure() throws AAIException {
		Introspector obj = loader.introspectorFromName("test-object");
		obj.setValue("vnf-id", "key1");
		obj.setValue("model-invariant-id", "id1");
		validator.validate(obj);
		List<Issue> issues = validator.getIssues();
		assertEquals("issues found", true, issues.size() == 1);
		Issue issue = issues.get(0);
		assertEquals("found expected issue", IssueType.DEPENDENT_PROP_NOT_FOUND, issue.getType());
	}
	@Ignore
	@Test
	public void verifyRequiresSuccess() throws AAIException {
		Introspector obj = loader.introspectorFromName("test-object");
		obj.setValue("vnf-id", "key1");
		obj.setValue("model-invariant-id", "id1");
		obj.setValue("model-version-id", "version-id1");
		validator.validate(obj);
		List<Issue> issues = validator.getIssues();
		assertEquals("no issues found", true, issues.isEmpty());
	}
}
