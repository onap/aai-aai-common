/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.setup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.onap.aai.restclient.MockProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(
    properties = {
        "schema.ingest.file = src/test/resources/forWiringTests/schema-ingest-ss-wiring-test.properties"})
@ContextConfiguration(classes = {MockProvider.class, SchemaVersionsBean.class})
@SpringBootTest
public class SchemaVersionsBeanTest {

    // set thrown.expect to whatever a specific test needs
    // this establishes a default of expecting no exceptions to be thrown
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Autowired
    SchemaVersionsBean SchemaVersionsBean;

    @Test
    public void testGetContextForVersion() throws IOException {

        SchemaVersions versions = SchemaVersionsBean.getSchemaVersions();
        assertEquals(versions.getDefaultVersion(), new SchemaVersion("v15"));
    }

    @Test
    public void testGetVersions() throws IOException {

        List<SchemaVersion> versions = SchemaVersionsBean.getVersions();
        assertNotNull(versions);
    }

    @Test
    public void testGetters() throws IOException {

        List<SchemaVersion> versionsList = SchemaVersionsBean.getVersions();
        assertNotNull(versionsList);
        SchemaVersions versions = SchemaVersionsBean.getSchemaVersions();
        /*
         * //assertEquals(versions.getAppRootVersion(), new SchemaVersion("v15"));
         * assertEquals(versions.getAppRootVersion(), new SchemaVersion("v11"));
         * assertEquals(versions.getDepthVersion(), new SchemaVersion("v10"));
         * assertEquals(versions.getEdgeLabelVersion(), new SchemaVersion("v12"));
         * assertEquals(versions.getNamespaceChangeVersion(), new SchemaVersion("v11"));
         * assertEquals(versions.getRelatedLinkVersion(), new SchemaVersion("v10"));
         */

        assertEquals(versions.getAppRootVersion(), new SchemaVersion("v15"));
        assertEquals(versions.getDepthVersion(), new SchemaVersion("v15"));
        assertEquals(versions.getEdgeLabelVersion(), new SchemaVersion("v15"));
        assertEquals(versions.getNamespaceChangeVersion(), new SchemaVersion("v15"));
        assertEquals(versions.getRelatedLinkVersion(), new SchemaVersion("v15"));

    }

}
