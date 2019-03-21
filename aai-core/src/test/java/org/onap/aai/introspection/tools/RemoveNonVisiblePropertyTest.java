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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.introspection.*;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class RemoveNonVisiblePropertyTest extends AAISetup {

    private Loader loader;
    private Issue issue;
    private RemoveNonVisibleProperty rn;

    @Before
    public void setup() {
        rn = new RemoveNonVisibleProperty();
        loader = loaderFactory.createLoaderForVersion(ModelType.MOXY,
            schemaVersions.getDefaultVersion());
    }

    @Test
    public void testNonVisiblePropertyResolved() throws AAIUnknownObjectException {
        Introspector introspector = loader.introspectorFromName("pserver");
        issue = new Issue();
        issue.setDetail("Some message");
        issue.setType(IssueType.PROPERTY_NOT_VISIBLE);
        issue.setPropName("in-maint");
        issue.setIntrospector(introspector);
        assertTrue("Nonvisible property should be removed", rn.resolveIssue(issue));
        assertNull("Introspector did not remove the non visible property",
            introspector.getValue("in-maint"));
    }

    @Test
    public void testNonVisiblePropertyNotResolved() throws AAIUnknownObjectException {
        Introspector introspector = loader.introspectorFromName("pserver");
        issue = new Issue();
        issue.setDetail("Some message");
        issue.setType(IssueType.MISSING_REQUIRED_PROP);
        issue.setPropName("in-maint");
        issue.setIntrospector(introspector);
        assertFalse("Nonvisible property not present so should not have been removed",
            rn.resolveIssue(issue));
    }

}
