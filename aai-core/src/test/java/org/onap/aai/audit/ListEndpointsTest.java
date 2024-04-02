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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.setup.SchemaVersion;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ListEndpointsTest extends AAISetup {

    private Properties properties;
    private SchemaVersion version;
    private ListEndpoints listEndpoints;

    @BeforeEach
    public void setUp() {
        properties = new Properties();
        version = schemaVersions.getDefaultVersion();
        listEndpoints = new ListEndpoints(basePath, schemaVersions.getDefaultVersion());
    }

    @Test
    public void testGetEndpoints() {
        Assertions.assertNotNull(listEndpoints);
        List<String> list = listEndpoints.getEndpoints();
        Assertions.assertTrue(list != null && !list.isEmpty());

        for (String endpoint : list) {
            System.out.println("endpoints: " + endpoint);
        }
    }

    @Test
    public void testGetEndpointsWithParam() {
        Assertions.assertNotNull(listEndpoints);
        List<String> list = listEndpoints.getEndpoints();
        Assertions.assertTrue(list != null && !list.isEmpty());
    }

    @Test
    public void testGetEndpoints_throwException() {
        assertThrows(RuntimeException.class, () -> {
            new ListEndpoints(basePath, null);
        });
    }

    @Test
    public void testGetLogicalNames() {
        Assertions.assertNotNull(listEndpoints);
        Map<String, String> map = listEndpoints.getLogicalNames();
        Assertions.assertTrue(map != null && !map.isEmpty());
    }

    @Test
    public void testToStrinWithParam() {
        Assertions.assertNotNull(listEndpoints);
        String endpoints = listEndpoints.toString("complex");
        Assertions.assertTrue(!endpoints.contains("complex"));
    }
}
