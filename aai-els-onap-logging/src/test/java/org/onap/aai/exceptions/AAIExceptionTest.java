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
import org.onap.aai.logging.AaiElsErrorCode;
import org.onap.aai.logging.ErrorObject;

import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class AAIExceptionTest {
    private AAIException aaiException;

    @Test
    public void defaultAAIExceptionTest() {
        //AAI_4000=5:4:ERROR:4000:500:3002:Internal Error:900
        aaiException = new AAIException();
        assertEquals(AAIException.DEFAULT_EXCEPTION_CODE, aaiException.getCode());

        ErrorObject errorObject = aaiException.getErrorObject();
        assertEquals(errorObject.getCategory(), "4");
        assertEquals(errorObject.getDisposition(), "5");
        assertEquals(errorObject.getSeverity(), "ERROR");
        assertEquals(errorObject.getHTTPResponseCode(), Response.Status.INTERNAL_SERVER_ERROR);
        assertEquals(errorObject.getRESTErrorCode(), "3002");
        assertEquals(errorObject.getErrorCode(), "4000");
        assertEquals(errorObject.getAaiElsErrorCode(), AaiElsErrorCode.UNKNOWN_ERROR);
        assertEquals(errorObject.getErrorText(), "Internal Error");

    }

    @Test
    public void aaiExceptionTest() {
        //5:1:WARN:3303:403:3300:Too many objects would be returned by this request, please refine your request and retry:500
        aaiException = new AAIException("AAI_3303");
        assertEquals("AAI_3303", aaiException.getCode());

        ErrorObject errorObject = aaiException.getErrorObject();
        assertEquals(errorObject.getCategory(), "1");
        assertEquals(errorObject.getDisposition(), "5");
        assertEquals(errorObject.getSeverity(), "WARN");
        assertEquals(errorObject.getHTTPResponseCode(), Response.Status.FORBIDDEN);
        assertEquals(errorObject.getRESTErrorCode(), "3300");
        assertEquals(errorObject.getErrorCode(), "3303");
        assertEquals(errorObject.getAaiElsErrorCode(), AaiElsErrorCode.BUSINESS_PROCESS_ERROR);
        assertEquals(errorObject.getErrorText(), "Too many objects would be returned by this request, please refine your request and retry");
        assertNotNull(aaiException.getTemplateVars());
    }

    @Test
    public void aaiExceptionTestWithDetails() {
        //5:1:WARN:3303:403:3300:Too many objects would be returned by this request, please refine your request and retry:500
        final String testDetails = "Test details";
        aaiException = new AAIException("AAI_3303", testDetails);
        assertEquals(testDetails, aaiException.getMessage());
        assertEquals(testDetails, aaiException.getErrorObject().getDetails());
        assertNotNull(aaiException.getTemplateVars());
    }

    @Test
    public void aaiExceptionTestWithCause() {
        aaiException = new AAIException("AAI_3303", new IOException("File not found"));
        Throwable t = aaiException.getCause();
        assertEquals("java.io.IOException: File not found", t.toString());
        assertNotNull(aaiException.getTemplateVars());
    }

    @Test
    public void aaiExceptionTestWithCauseDetails() {
        final String testFileName = "TestFileName";
        aaiException = new AAIException("AAI_3303", new IOException("File not found"), testFileName);

        Throwable t = aaiException.getCause();
        assertEquals("java.io.IOException: File not found", t.toString());
        assertEquals(testFileName, aaiException.getMessage());
        assertNotNull(aaiException.getTemplateVars());
    }
}

