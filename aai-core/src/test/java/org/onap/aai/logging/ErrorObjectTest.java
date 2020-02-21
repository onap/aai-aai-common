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

import javax.ws.rs.core.Response.Status;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ErrorObjectTest {

    ErrorObject newErrorObject =
            new ErrorObject("disposition", "category", "severity", 200, "restErrorCode", "errorCode", "errorText");

    // Constructor Tests
    @Test
    public void createObjectTest1() {
        // No HTTP Status argument
        ErrorObject errorObject = new ErrorObject("severity", "errorcode", "errortext", "disposition", "category");
        assertNotNull(errorObject);
    }

    @Test
    public void createObjectTest2() {
        // HTTP Status code as integer
        ErrorObject errorObject = new ErrorObject("severity", 200, "errorcode", "errortext", "disposition", "category");
        assertNotNull(errorObject);
    }

    @Test
    public void createObjectTest3() {
        // HTTP Status code as Status
        ErrorObject errorObject =
                new ErrorObject("severity", Status.OK, "errorcode", "errortext", "disposition", "category");
        assertNotNull(errorObject);
    }

    // Disposition Tests
    @Test
    public void getDispositionTest() {
        assertEquals(newErrorObject.getDisposition(), "disposition");
    }

    @Test
    public void setDispositionTest() {
        newErrorObject.setDisposition("newDisposition");
        assertEquals(newErrorObject.getDisposition(), "newDisposition");
    }

    // Category Tests
    @Test
    public void getCategoryTest() {
        assertEquals(newErrorObject.getCategory(), "category");
    }

    @Test
    public void setCategoryTest() {
        newErrorObject.setCategory("newCategory");
        assertEquals(newErrorObject.getCategory(), "newCategory");
    }

    // Severity Tests
    @Test
    public void getSeverityTest() {
        assertEquals(newErrorObject.getSeverity(), "severity");
    }

    @Test
    public void setSeverityTest() {
        newErrorObject.setSeverity("newSeverity");
        assertEquals(newErrorObject.getSeverity(), "newSeverity");
    }

    // Error Code Tests
    @Test
    public void getErrorCodeTest() {
        assertEquals(newErrorObject.getErrorCode(), "errorCode");
    }

    @Test
    public void setErrorCodeTest() {
        newErrorObject.setErrorCode("newErrorCode");
        assertEquals(newErrorObject.getErrorCode(), "newErrorCode");
    }

    // HTTP Response Code Tests
    @Test
    public void getHTTPCodeTest() {
        assertEquals(newErrorObject.getHTTPResponseCode(), Status.OK);
    }

    @Test
    public void setHTTPCodeTest() {
        newErrorObject.setHTTPResponseCode(201);
        assertEquals(newErrorObject.getHTTPResponseCode(), Status.CREATED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidHttpCodeTest() {
        newErrorObject.setHTTPResponseCode(6281723);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidHttpCodeTest2() {
        newErrorObject.setHTTPResponseCode("82901");
    }

    // Rest Error Code Tests
    @Test
    public void getRestErrorCodeTest() {
        assertEquals(newErrorObject.getRESTErrorCode(), "restErrorCode");
    }

    @Test
    public void setRestErrorCodeTest() {
        newErrorObject.setRESTErrorCode("newRestErrorCode");
        assertEquals(newErrorObject.getRESTErrorCode(), "newRestErrorCode");
    }

    // Error Text Tests
    @Test
    public void getErrorTextTest() {
        assertEquals(newErrorObject.getErrorText(), "errorText");
    }

    @Test
    public void setErrorTextTest() {
        newErrorObject.setErrorText("newErrorText");
        assertEquals(newErrorObject.getErrorText(), "newErrorText");
    }

    @Test
    public void getErrorCodeStringTest() {
        assertEquals(newErrorObject.getErrorCodeString(), "disposition.category.errorCode");
    }

    @Test
    public void getErrorCodeStringDisposition5Test() {
        // get Error Code String while Disposition = 5
        newErrorObject.setDisposition("5");
        assertEquals(newErrorObject.getErrorCodeString(), "ERR.5.category.errorCode");
    }

    @Test
    public void getSeverityCodeTest() {
        newErrorObject.setSeverity("WARN");
        assertEquals(newErrorObject.getSeverityCode(newErrorObject.getSeverity()), "1");

        newErrorObject.setSeverity("ERROR");
        assertEquals(newErrorObject.getSeverityCode(newErrorObject.getSeverity()), "2");

        newErrorObject.setSeverity("FATAL");
        assertEquals(newErrorObject.getSeverityCode(newErrorObject.getSeverity()), "3");
    }

    // To String Test
    @Test
    public void toStringTest() {
        assertEquals(newErrorObject.toString(),
                "ErrorObject [errorCode=errorCode, errorText=errorText, restErrorCode=restErrorCode, httpResponseCode=OK, severity=severity, disposition=disposition, category=category]");
    }
}
