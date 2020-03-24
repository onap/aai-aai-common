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

package org.onap.aai.audit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.setup.SchemaVersion;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ListEndpointsTest extends AAISetup {

    private Properties properties;
    private SchemaVersion version;
    private ListEndpoints listEndpoints;

    @Before
    public void setUp() {
        properties = new Properties();
        version = schemaVersions.getDefaultVersion();
        listEndpoints = new ListEndpoints(basePath, schemaVersions.getDefaultVersion());
    }

    @Test
    public void testGetEndpoints() {
        Assert.assertNotNull(listEndpoints);
        List<String> list = listEndpoints.getEndpoints();
        Assert.assertTrue(list != null && !list.isEmpty());

        for (String endpoint : list) {
            System.out.println("endpoints: " + endpoint);
        }
    }

    @Test
    public void testGetEndpointsWithParam() {
        Assert.assertNotNull(listEndpoints);
        List<String> list = listEndpoints.getEndpoints();
        Assert.assertTrue(list != null && !list.isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void testGetEndpoints_throwException() {
        new ListEndpoints(basePath, null);
    }

    @Test
    public void testGetLogicalNames() {
        Assert.assertNotNull(listEndpoints);
        Map<String, String> map = listEndpoints.getLogicalNames();
        Assert.assertTrue(map != null && !map.isEmpty());
    }

    @Test
    public void testToStrinWithParam() {
        Assert.assertNotNull(listEndpoints);
        String endpoints = listEndpoints.toString("complex");
        Assert.assertTrue(!endpoints.contains("complex"));
    }
}
