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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.introspection.*;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class CreateUUIDTest extends AAISetup {

    private CreateUUID createUUID;

    private Loader loader;
    private Issue issue;

    @Before
    public void setup() {
        createUUID = new CreateUUID();
        loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDefaultVersion());
    }

    /**
     * Tests to check if the issue is not resolvable since
     * the property that is being tested doesn't have the auto generated uuid
     * metadata set to true in the oxm xml for the version specified
     *
     * @throws AAIUnknownObjectException - if the object type specified is unable to be found in the oxm
     */
    @Test
    public void testNonResolvableIssueIfMissingPropNameThatIsRequired() throws AAIUnknownObjectException {

        Introspector introspector = loader.introspectorFromName("pserver");

        issue = new Issue();
        issue.setDetail("Some message");
        issue.setType(IssueType.MISSING_KEY_PROP);
        issue.setPropName("hostname");
        issue.setIntrospector(introspector);

        boolean isIssue = createUUID.resolveIssue(issue);

        assertFalse(isIssue);
    }

    /**
     * Tests when there is a resolvable issue when the property
     * looking for, model-element-uuid, has the auto generated uuid
     * metadata attribute associated to it if the data is missing
     *
     * @throws AAIUnknownObjectException - if the object type specified is unable to be found in the oxm
     */
    @Test
    public void testResolvableIssueWhenMissingPropNameAllowsToUseGeneratedUUID() throws AAIUnknownObjectException {

        Introspector introspector = loader.introspectorFromName("model-element");

        issue = new Issue();
        issue.setDetail("Some message");
        issue.setType(IssueType.MISSING_KEY_PROP);
        issue.setPropName("model-element-uuid");
        issue.setIntrospector(introspector);

        boolean isIssue = createUUID.resolveIssue(issue);
        assertTrue(isIssue);
    }

}
