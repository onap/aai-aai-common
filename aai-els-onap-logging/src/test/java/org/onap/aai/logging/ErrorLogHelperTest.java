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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.aai.domain.restPolicyException.PolicyException;
import org.onap.aai.domain.restPolicyException.RESTResponse;
import org.onap.aai.domain.restPolicyException.RequestError;
import org.onap.aai.domain.restServiceException.ServiceException;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.util.LogFile;
import org.onap.aai.util.MapperUtil;
import org.slf4j.MDC;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class ErrorLogHelperTest {

    private static final String ErrorLogFileName = "error.log";

    @Before
    public void init() {
        System.setProperty("AJSC_HOME", ".");

    }
    @After
    public void cleanup() throws IOException{
        MDC.clear();
        LogFile.deleteContents(ErrorLogFileName);
    }
    @Test
    public void logErrorTest() throws IOException, InterruptedException {
        //||main|UNKNOWN||||ERROR|500|Node cannot be deleted:3100:Bad Request:|ERR.5.4.6110
        ErrorLogHelper.logError("AAI_6110");
        sleep(5000);
        String logContents = LogFile.getContents(ErrorLogFileName);

        assertNotNull(logContents);

        String logContentParts[] = logContents.split("\\|");

        assertTrue(logContentParts.length >= 11 );
        assertEquals ("ERROR", logContentParts[7]);
        assertEquals (AaiElsErrorCode.BUSINESS_PROCESS_ERROR, logContentParts[8]);
        assertTrue (logContentParts[10].startsWith("ERR.5.4.6110"));
    }

    @Test
    public void logErrorWithMessageTest() throws IOException, InterruptedException {
        //||main|UNKNOWN||||ERROR|500|Node cannot be deleted:3100:Bad Request:|ERR.5.4.6110 message
        String errorMessage = "Object is referenced by additional objects";
        ErrorLogHelper.logError("AAI_6110", errorMessage);
        sleep(5000);
        String logContents = LogFile.getContents(ErrorLogFileName);

        assertNotNull(logContents);

        String logContentParts[] = logContents.split("\\|");

        assertTrue(logContentParts.length >= 11 );
        assertTrue (logContentParts[9].contains(errorMessage));
        assertTrue (logContentParts[10].startsWith("ERR.5.4.6110"));
    }

    @Test
    public void getRESTAPIPolicyErrorResponseTest() throws AAIException{
        //AAI_3002=5:1:WARN:3002:400:3002:Error writing output performing %1 on %2:300
        ArrayList<MediaType> headers = new ArrayList<MediaType>(Arrays.asList(MediaType.APPLICATION_JSON_TYPE));
        ArrayList<String> args = new ArrayList<String>(Arrays.asList("PUT", "resource"));

        AAIException aaie = new AAIException("AAI_3002");
        String errorResponse = ErrorLogHelper.getRESTAPIErrorResponse(headers, aaie, args);
        assertNotNull(errorResponse);

        RESTResponse resp = MapperUtil.readAsObjectOf(RESTResponse.class, errorResponse);
        RequestError requestError = resp.getRequestError();
        assertNotNull(requestError);
        PolicyException policyException = requestError.getPolicyException();
        assertNotNull(policyException);
        assertEquals("POL3002", policyException.getMessageId());

        List<String> vars = policyException.getVariables();
        assertTrue(vars.contains("PUT"));
        assertTrue(vars.contains("resource"));
    }
    @Test
    public void getRESTAPIServiceErrorResponseTest() throws AAIException{
        //AAI_3009=5:6:WARN:3009:400:3009:Malformed URL:300
        ArrayList<MediaType> headers = new ArrayList<MediaType>(Arrays.asList(MediaType.APPLICATION_JSON_TYPE));
        ArrayList<String> args = new ArrayList<String>();

        AAIException aaie = new AAIException("AAI_3009");
        String errorResponse = ErrorLogHelper.getRESTAPIErrorResponse(headers, aaie, args);
        assertNotNull(errorResponse);

        org.onap.aai.domain.restServiceException.RESTResponse resp = MapperUtil.readAsObjectOf(org.onap.aai.domain.restServiceException.RESTResponse.class, errorResponse);
        org.onap.aai.domain.restServiceException.RequestError requestError = resp.getRequestError();
        assertNotNull(requestError);
        ServiceException serviceException = requestError.getServiceException();
        assertNotNull(serviceException);
        assertEquals("SVC3009", serviceException.getMessageId());

    }
    @Test
    public void getRESTAPIServiceErrorResponseWithLoggingTest() throws IOException, InterruptedException{
        //AAI_3009=5:6:WARN:3009:400:3009:Malformed URL:300
        ArrayList<MediaType> headers = new ArrayList<MediaType>(Arrays.asList(MediaType.APPLICATION_JSON_TYPE));
        ArrayList<String> args = new ArrayList<String>();

        AAIException aaie = new AAIException("AAI_3009");
        String errorResponse = ErrorLogHelper.getRESTAPIErrorResponseWithLogging(headers, aaie, args);
        sleep(5000);
        assertNotNull(errorResponse);
        String logContents = LogFile.getContents(ErrorLogFileName);

        assertNotNull(logContents);
        String logContentParts[] = logContents.split("\\|");

        assertTrue(logContentParts.length >= 11 );
        assertTrue (logContentParts[10].startsWith("ERR.5.6.3009"));

    }

}
