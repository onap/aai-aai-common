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
package org.onap.aai.introspection.validation;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.*;

import org.onap.aai.introspection.tools.IntrospectorValidator;
import org.onap.aai.introspection.tools.Issue;
import org.onap.aai.introspection.tools.IssueType;
import org.onap.aai.serialization.queryformats.QueryFormatTestHelper;
import org.onap.aai.util.AAIConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class IntrospectorValidationTest extends AAISetup {

    private final static ModelType introspectorFactoryType = ModelType.MOXY;
    private Loader loader;
    private IntrospectorValidator validator;
    @Autowired
    private LoaderFactory loaderFactory;

    @Before
    public void createValidator() throws Exception {
        System.setProperty("AJSC_HOME", ".");
        System.setProperty("BUNDLECONFIG_DIR", "bundleconfig-local");
        loader = loaderFactory.createLoaderForVersion(introspectorFactoryType, schemaVersions.getRelatedLinkVersion());
        validator = new IntrospectorValidator.Builder()
                .validateRequired(false)
                .restrictDepth(10000)
                .build();
    }
    @Ignore
    @Test
    public void verifySuccessWhenEmpty() throws AAIException {
        Introspector obj = loader.introspectorFromName("generic-vnf");
        obj.setValue("vnf-id", "key1");
        validator.validate(obj);
        List<Issue> issues = validator.getIssues();
        assertEquals("no issues found", true, issues.isEmpty());
    }

    @Ignore
    @Test
    public void verifyRequiresSingleFieldFailure() throws AAIException {
        Introspector obj = loader.introspectorFromName("generic-vnf");
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
        Introspector obj = loader.introspectorFromName("generic-vnf");
        obj.setValue("vnf-id", "key1");
        obj.setValue("model-invariant-id", "id1");
        obj.setValue("model-version-id", "version-id1");
        validator.validate(obj);
        List<Issue> issues = validator.getIssues();
        assertEquals("no issues found", true, issues.isEmpty());
    }
}
