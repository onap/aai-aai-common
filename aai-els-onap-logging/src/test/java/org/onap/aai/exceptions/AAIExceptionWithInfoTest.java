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
package org.onap.aai.exceptions;

import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AAIExceptionWithInfoTest {

    private AAIExceptionWithInfo aaiException;
    private static final String testInfo = "Test Info";
    private static final String testDetails = "Test Details";

    @Test
    public void aaiExceptionWithInfoTest() {

        aaiException = new AAIExceptionWithInfo(new HashMap<String, Object>(), testInfo );

        assertEquals(testInfo, aaiException.getInfo());
        assertNotNull(aaiException.getInfoHash());
    }

    @Test
    public void aaiExceptionWithCodeInfoTest() {
        aaiException = new AAIExceptionWithInfo("AAI_3300", new HashMap<String, Object>(), testInfo );

        assertEquals(testInfo, aaiException.getInfo());
        assertEquals("AAI_3300", aaiException.getCode());
        assertNotNull(aaiException.getInfoHash());
    }

    @Test
    public void aaiExceptionWithCodeDetailsInfoTest() {

        aaiException = new AAIExceptionWithInfo("AAI_3300", testDetails, new HashMap<String, Object>(), testInfo );

        assertEquals(testInfo, aaiException.getInfo());
        assertEquals("AAI_3300", aaiException.getCode());
        assertEquals(testDetails, aaiException.getMessage());
        assertNotNull(aaiException.getInfoHash());
    }

    @Test
    public void aaiExceptionWithCodeThrowableDetailsInfoTest() {

        aaiException = new AAIExceptionWithInfo("AAI_3300", new IOException("File not found"), testDetails, new HashMap<String, Object>(), testInfo );

        assertEquals(testInfo, aaiException.getInfo());
        assertEquals("AAI_3300", aaiException.getCode());
        assertEquals(testDetails, aaiException.getMessage());
        assertNotNull(aaiException.getInfoHash());
        Throwable t = aaiException.getCause();
        assertEquals("java.io.IOException: File not found", t.toString());
    }

}

