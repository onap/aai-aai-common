/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 *  Modifications Copyright © 2018 IBM.
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
package org.onap.aai.util;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.exceptions.AAIException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AAIApplicationConfigTest {

    @Before
    public void setup() {
        AAIApplicationConfig.init();
    }

    @Test
    public void truststoreTest() throws AAIException {
        assertEquals("truststore.jks", AAIApplicationConfig.getTruststore());
    }
    @Test
    public void keystoreTest() throws AAIException {
        assertEquals("keystore.jks", AAIApplicationConfig.getKeystore());
    }

    @Test
    public void getKeystorePkcs12Test() throws AAIException {
        assertEquals("keystore.pkcs12", AAIApplicationConfig.getKeystorePkcs12());
    }

    @Test
    public void getValueWithDefaultTest() throws AAIException {
        assertEquals("default-value", AAIApplicationConfig.get("non-existing-key", "default-value"));
    }

    @Test
    public void getValueTest() throws AAIException {
        assertEquals("certificates", AAIApplicationConfig.get("server.certs.location"));
    }
    @Test
    public void getIntValueTest() throws AAIException {
        assertTrue(8446 == AAIApplicationConfig.getInt("server.port"));
    }

    @Test
    public void getIntValueWithDefaultTest() throws AAIException {
        assertTrue(9999 == AAIApplicationConfig.getInt("non-existing-key", "9999"));
    }

    @Test
    public void getValueWithReplacementTest() throws AAIException {
        assertEquals("/opt/app/aai/etc/auth/aai-client-cert.p12", AAIApplicationConfig.get("schema.service.ssl.key-store"));
    }
}
