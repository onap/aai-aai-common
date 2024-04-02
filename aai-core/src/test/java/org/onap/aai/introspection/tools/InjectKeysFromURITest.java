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

import static org.eclipse.persistence.jpa.jpql.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.introspection.*;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class InjectKeysFromURITest extends AAISetup {

    private Loader loader;
    private Issue issue;
    private InjectKeysFromURI ik;

    @BeforeEach
    public void setup() {
        loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDefaultVersion());
        issue = new Issue();
    }

    @Test
    public void testInjectKeysFromURIOfPserverIsNotResolved() throws AAIUnknownObjectException {
        try {
            ik = new InjectKeysFromURI(loader, new URI("/aai/v12/cloud-infrastructure/complexes"));
        } catch (Exception e) {
            fail("InjectKeys instantiation threw an exception");
        }

        Introspector pserver = loader.introspectorFromName("pserver");
        pserver.setValue("in-maint", false);
        pserver.setValue("hostname", "pserver2");
        assertNotNull(pserver, "Unable to load the template introspector");

        issue.setDetail("Some message");
        issue.setType(IssueType.MISSING_KEY_PROP);
        issue.setPropName("in-maint");
        issue.setIntrospector(pserver);

        Boolean issueResolved = ik.resolveIssue(issue);

        assertFalse(issueResolved, "Unable to resolve the pserver in-maint issue");
        assertEquals(false,
                pserver.getValue("in-maint"),
                "Introspector didn't successfully modify the pserver in-maint");
    }

    @Test
    public void testInjectKeysFromURIOfPserverSuccessfullyResolved() throws AAIUnknownObjectException {
        try {
            ik = new InjectKeysFromURI(loader, new URI("/aai/v12/cloud-infrastructure/pservers/pserver/pserver1"));
        } catch (Exception e) {
            fail("InjectKeys instantiation threw an exception");
        }

        Introspector pserver = loader.introspectorFromName("pserver");
        assertNotNull(pserver, "Unable to load the template introspector");

        issue.setDetail("Some message");
        issue.setType(IssueType.MISSING_KEY_PROP);
        issue.setPropName("hostname");
        issue.setIntrospector(pserver);

        Boolean issueResolved = ik.resolveIssue(issue);

        assertTrue(issueResolved, "Unable to resolve the pserver hostname default field issue");
        assertEquals("pserver1",
                pserver.getValue("hostname"),
                "Introspector didn't successfully modify the pserver hostname");
    }

}
