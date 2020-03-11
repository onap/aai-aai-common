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

package org.onap.aai.logging;

import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class ErrorObjectTest {
    private ErrorObject errorObject;
    private static final String ERROR_DISPOSITION = "5";
    private static final String ERROR_SEVERITY = "ERROR";
    private static final String ERROR_CATEGORY = "4";
    private static final Integer ERROR_HTTP_RESPONSE_CODE = new Integer(401);
    private static final String ERROR_REST_CODE = "3300";
    private static final String ERROR_CODE = "4000";
    private static final String ERROR_TEXT = "Test data error";
    private static final String ERROR_PATTERN = "ERR.5.4.4000";
    private static final String ERROR_SEVERITY_CODE = "2";
    @Test
    public void errorObjectDefaultConstructorTest() {
        errorObject = new ErrorObject();
        assertEquals("3002", errorObject.getRESTErrorCode());
        assertEquals(Response.Status.fromStatusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()), errorObject.getHTTPResponseCode());
        assertEquals(AaiElsErrorCode.UNKNOWN_ERROR,errorObject.getAaiElsErrorCode());
        assertEquals(ERROR_SEVERITY_CODE, errorObject.getSeverityCode(ERROR_SEVERITY));
    }
    @Test
    public void errorObjectConstructor7Test() {
        errorObject = new ErrorObject(ERROR_DISPOSITION, ERROR_CATEGORY, ERROR_SEVERITY, ERROR_HTTP_RESPONSE_CODE,
            ERROR_REST_CODE, ERROR_CODE, ERROR_TEXT);
        assertEquals(ERROR_DISPOSITION, errorObject.getDisposition());
        assertEquals(ERROR_SEVERITY, errorObject.getSeverity());
        assertEquals(ERROR_CATEGORY, errorObject.getCategory());
        assertEquals(Response.Status.fromStatusCode(ERROR_HTTP_RESPONSE_CODE), errorObject.getHTTPResponseCode());
        assertEquals(ERROR_REST_CODE, errorObject.getRESTErrorCode());
        assertEquals(ERROR_CODE, errorObject.getErrorCode());
        assertEquals(ERROR_TEXT, errorObject.getErrorText());
        assertEquals(AaiElsErrorCode.UNKNOWN_ERROR,errorObject.getAaiElsErrorCode());
        assertEquals(ERROR_SEVERITY_CODE, errorObject.getSeverityCode(ERROR_SEVERITY));
    }

    @Test
    public void errorObjectConstructor5Test() {
        errorObject = new ErrorObject(ERROR_SEVERITY, ERROR_CODE, ERROR_TEXT, ERROR_DISPOSITION, ERROR_CATEGORY);
        assertEquals(ERROR_DISPOSITION, errorObject.getDisposition());
        assertEquals(ERROR_SEVERITY, errorObject.getSeverity());
        assertEquals(ERROR_CATEGORY, errorObject.getCategory());
        assertEquals(Response.Status.fromStatusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()), errorObject.getHTTPResponseCode());
        assertEquals("3002", errorObject.getRESTErrorCode());
        assertEquals(ERROR_CODE, errorObject.getErrorCode());
        assertEquals(ERROR_TEXT, errorObject.getErrorText());
        assertEquals(AaiElsErrorCode.UNKNOWN_ERROR,errorObject.getAaiElsErrorCode());
        assertEquals(ERROR_PATTERN, errorObject.getErrorCodeString());
        assertEquals(ERROR_SEVERITY_CODE, errorObject.getSeverityCode(ERROR_SEVERITY));
    }

    @Test
    public void errorObjectConstructor6Test() {
        errorObject = new ErrorObject(ERROR_SEVERITY, ERROR_HTTP_RESPONSE_CODE, ERROR_CODE, ERROR_TEXT, ERROR_DISPOSITION, ERROR_CATEGORY);
        assertEquals(ERROR_DISPOSITION, errorObject.getDisposition());
        assertEquals(ERROR_SEVERITY, errorObject.getSeverity());
        assertEquals(ERROR_CATEGORY, errorObject.getCategory());
        assertEquals(Response.Status.fromStatusCode(ERROR_HTTP_RESPONSE_CODE), errorObject.getHTTPResponseCode());
        assertEquals("3002", errorObject.getRESTErrorCode());
        assertEquals(ERROR_CODE, errorObject.getErrorCode());
        assertEquals(ERROR_TEXT, errorObject.getErrorText());
        assertEquals(AaiElsErrorCode.UNKNOWN_ERROR, errorObject.getAaiElsErrorCode());
        assertEquals(ERROR_PATTERN, errorObject.getErrorCodeString());
        assertEquals(ERROR_SEVERITY_CODE, errorObject.getSeverityCode(ERROR_SEVERITY));

    }


}
