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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.introspection.tools;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.introspection.*;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class DefaultFieldsTest extends AAISetup {

    private Loader loader;
    private Issue issue;
    private DefaultFields defaultFields;

    @Before
    public void setup() {
        loader = loaderFactory.createLoaderForVersion(ModelType.MOXY,
            schemaVersions.getDefaultVersion());
        issue = new Issue();
        defaultFields = new DefaultFields();
    }

    @Test
    public void testDefaultFieldOfPserverIsSucccessfullyResolved()
        throws AAIUnknownObjectException {

        Introspector pserver = loader.introspectorFromName("pserver");
        assertNotNull("Unable to load the template introspector", pserver);

        issue.setDetail("Some message");
        issue.setType(IssueType.MISSING_REQUIRED_PROP);
        issue.setPropName("in-maint");
        issue.setIntrospector(pserver);

        boolean isResolved = defaultFields.resolveIssue(issue);

        assertTrue("Unable to resolve the pserver in-maint default field issue", isResolved);
        assertEquals("Introspector didn't successfully modify the pserver in-maint", false,
            pserver.getValue("in-maint"));
    }

    @Test
    public void testDefaultFieldResolverShouldFailWhenResolveOnNonDefaultField()
        throws AAIUnknownObjectException {

        Introspector pserver = loader.introspectorFromName("pserver");
        assertNotNull("Unable to load the template introspector", pserver);

        issue.setDetail("Some message");
        issue.setType(IssueType.MISSING_REQUIRED_PROP);
        issue.setPropName("hostname");
        issue.setIntrospector(pserver);

        boolean isResolved = defaultFields.resolveIssue(issue);

        assertFalse("It shouldn't be resolving this issue as hostname is required key", isResolved);
    }
}
