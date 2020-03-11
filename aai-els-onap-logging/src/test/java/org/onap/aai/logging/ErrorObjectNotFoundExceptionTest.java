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

import java.io.IOException;

import static org.junit.Assert.*;

public class ErrorObjectNotFoundExceptionTest {

    @Test
    public void defaultExceptionTest() {
        ErrorObjectNotFoundException e = new ErrorObjectNotFoundException();
        assertTrue(e instanceof Exception);
    }
    @Test
    public void errorObjectNotFoundExceptionWithMessageTest() {
        ErrorObjectNotFoundException e = new ErrorObjectNotFoundException("Error Message");
        assertTrue(e instanceof Exception);
        assertEquals("Error Message", e.getMessage());
    }
    @Test
    public void errorObjectNotFoundExceptionWithCauseTest() {
        ErrorObjectNotFoundException e = new ErrorObjectNotFoundException(new ArithmeticException());
        assertTrue(e instanceof Exception);
        assertTrue(e.getCause() instanceof ArithmeticException);
    }
    @Test
    public void errorObjectNotFoundExceptionWithMsgCauseTest() {
        ErrorObjectNotFoundException e = new ErrorObjectNotFoundException("Error Message", new ArithmeticException());
        assertTrue(e instanceof Exception);
        assertTrue(e.getCause() instanceof ArithmeticException);
        assertEquals("Error Message", e.getMessage());
    }
    @Test
    public void errorObjectNotFoundExceptionTest() {

        ErrorObjectNotFoundException e = new ErrorObjectNotFoundException("Error Message", new ArithmeticException(), true, true);
        assertTrue(e instanceof Exception);
        assertTrue(e.getCause() instanceof ArithmeticException);
        assertEquals("Error Message", e.getMessage());
        e.addSuppressed(new IOException("Test IO Exception"));
        assertTrue((e.getSuppressed())[0] instanceof IOException);
        assertEquals("Test IO Exception", (e.getSuppressed())[0].getMessage());

        ErrorObjectNotFoundException e1 = new ErrorObjectNotFoundException("Error Message", new ArithmeticException(), false, true);
        e1.addSuppressed(new IOException("Test IO Exception"));
        assertTrue((e1.getSuppressed() == null) || e1.getSuppressed().length == 0);

    }

    @Test
    public void errorObjectNotFoundExceptionStackTraceTest() {

        ErrorObjectNotFoundException e1 = new ErrorObjectNotFoundException("Error Message", new ArithmeticException(), true, true);
        try {
            throw e1;
        }
        catch (ErrorObjectNotFoundException e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            assertNotNull(stackTrace);
            assertTrue(stackTrace.length > 0);
        }

        ErrorObjectNotFoundException e2 = new ErrorObjectNotFoundException("Error Message", new ArithmeticException(), true, false);
        try {
            throw e2;
        }
        catch (ErrorObjectNotFoundException e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            assertTrue(stackTrace == null || stackTrace.length == 0);
        }

    }
    /*public ErrorObjectNotFoundException(String message, Throwable cause, boolean enableSuppression,
                                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }*/
}
