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
package org.onap.aai.introspection.tools;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.introspection.*;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.springframework.test.annotation.DirtiesContext;

import static junit.framework.TestCase.assertNotNull;
import static org.eclipse.persistence.jpa.jpql.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class IntrospectorValidatorTest extends AAISetup {

    private Loader loader;
    private Issue issue;
    private Introspector introspector;
    private IntrospectorValidator.Builder b;
    private IntrospectorValidator iv;

    @Before
    public void setup() {
        loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDefaultVersion());
        issue = new Issue();
        try {
            introspector = loader.introspectorFromName("pserver");
        }catch(Exception e){
            fail("Introspector instantiation call threw an exception " + e);
        }
        b = new IntrospectorValidator.Builder();
        iv = b.build();

        b.addResolver(new IssueResolver() {
            @Override
            public boolean resolveIssue(Issue issue) {
                return true;
            }
        });
        //this method does nothing
        iv.processPrimitiveList("TEST",introspector);
    }

    public void setupIssue(String message, IssueType type, String propName, Introspector introspector){
        issue.setDetail(message);
        issue.setType(type);
        issue.setPropName(propName);
        issue.setIntrospector(introspector);
    }

    @Test
    public void testIntrospectorValidatorMaxDepth() throws AAIUnknownObjectException {
        setupIssue("Some message", IssueType.MISSING_REQUIRED_PROP, "hostname", introspector );
        b.restrictDepth(4);
        assertEquals("Maximum Depth should be 4", 4, b.getMaximumDepth());
    }

    @Test
    public void testIntrospectorValidatorValidationRequired() throws AAIUnknownObjectException {
        setupIssue("Some message", IssueType.MISSING_REQUIRED_PROP, "hostname", introspector );
        b.validateRequired(true);
        assertTrue("Validation should be required", b.getValidateRequired());
    }

    @Test
    public void testIntrospectorValidatorValidatedFalse() throws AAIUnknownObjectException{
        setupIssue("Some message", IssueType.MISSING_REQUIRED_PROP, "hostname", introspector );
        try {
            assertFalse("Not currently validated", iv.validate(introspector));
        }catch (Exception e){
            fail("Introspector validate call threw an exception " + e);
        }
    }

    @Test
    public void testIntrospectorValidatorResolveIssues() throws AAIUnknownObjectException{
        setupIssue("Some message", IssueType.MISSING_REQUIRED_PROP, "hostname", introspector );
        assertTrue("Introspector call to resolve issues should return true", iv.resolveIssues());
    }

    @Test
    public void testIntrospectorValidatorGetIssues() throws AAIUnknownObjectException{
        setupIssue("Some message", IssueType.MISSING_REQUIRED_PROP, "hostname", introspector );
        iv.getIssues();
    }

    @Test
    public void testIntrospectorValidatorProcessComplexObject() throws AAIUnknownObjectException{
        setupIssue("Some message", IssueType.MISSING_REQUIRED_PROP, "hostname", introspector );
        iv.processComplexObj(introspector);
    }

    @Test
    public void testIntrospectorValidatorCreateComplexListSize() throws AAIUnknownObjectException{
        setupIssue("Some message", IssueType.MISSING_REQUIRED_PROP, "hostname", introspector );
        assertEquals("create complex list size should return 0", 0, iv.createComplexListSize(introspector, introspector));
    }

    @Test
    public void testIntrospectorValidatorGetResolvers() throws AAIUnknownObjectException{
        setupIssue("Some message", IssueType.MISSING_REQUIRED_PROP, "hostname", introspector );
        assertNotNull("Get resolvers should not be null",  b.getResolvers());
    }

}
