/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2023 Deutsche Telekom SA.
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

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.aai.domain.errorResponse.ErrorMessage;
import org.onap.aai.domain.errorResponse.ExceptionType;
import org.onap.aai.domain.errorResponse.Fault;
import org.onap.aai.domain.errorResponse.Info;
import org.onap.aai.domain.errorResponse.ServiceFault;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.util.LogFile;
import org.slf4j.MDC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@SuppressWarnings("deprecation")
public class ErrorLogHelperTest {

    private static final String errorLogFileName = "error.log";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final XmlMapper xmlMapper = new XmlMapper();

    @Before
    public void init() {
        System.setProperty("AJSC_HOME", ".");
    }

    @After
    public void cleanup() throws IOException {
        MDC.clear();
        LogFile.deleteContents(errorLogFileName);
    }

    @Test
    public void logErrorTest() throws IOException, InterruptedException {
        // ||main|UNKNOWN||||ERROR|500|Node cannot be deleted:3100:Bad Request:|ERR.5.4.6110
        ErrorLogHelper.logError("AAI_6110");
        sleep(2000);
        String logContents = LogFile.getContents(errorLogFileName);

        assertNotNull(logContents);

        String logContentParts[] = logContents.split("\\|");

        assertTrue(logContentParts.length >= 11);
        assertEquals("ERROR", logContentParts[7]);
        assertEquals(AaiElsErrorCode.BUSINESS_PROCESS_ERROR, logContentParts[8]);
        assertTrue(logContentParts[10].startsWith("ERR.5.4.6110"));
    }

    @Test
    public void logErrorWithMessageTest() throws IOException, InterruptedException {
        // ||main|UNKNOWN||||ERROR|500|Node cannot be deleted:3100:Bad Request:|ERR.5.4.6110 message
        String errorMessage = "Object is referenced by additional objects";
        ErrorLogHelper.logError("AAI_6110", errorMessage);
        // TODO: Add a dynamic wait mechanism here
        sleep(5000); // reducing the wait leads to test flakiness in pipeline
        String logContents = LogFile.getContents(errorLogFileName);

        assertNotNull(logContents);

        String logContentParts[] = logContents.split("\\|");

        assertTrue(logContentParts.length >= 11);
        assertTrue(logContentParts[9].contains(errorMessage));
        assertTrue(logContentParts[10].startsWith("ERR.5.4.6110"));
    }

    @Test
    public void getRESTAPIPolicyErrorResponseXmlTest() throws AAIException, JsonMappingException, JsonProcessingException {
        // AAI_3009=5:6:WARN:3009:400:3009:Malformed URL:300
        List<MediaType> headers = Collections.singletonList(MediaType.APPLICATION_XML_TYPE);
        ArrayList<String> variables = new ArrayList<String>();

        AAIException aaiException = new AAIException("AAI_3102");
        String errorResponse = ErrorLogHelper.getRESTAPIErrorResponse(headers, aaiException, variables);
        assertNotNull(errorResponse);
        assertTrue(errorResponse.contains("<Fault>"));
        assertTrue(errorResponse.contains("<policyException>"));
        assertTrue(errorResponse.contains("<variables>"));
        assertTrue(errorResponse.contains("<variable>"));

        Fault restResponse = xmlMapper.readValue(errorResponse, Fault.class);
        assertNotNull(restResponse);

        ErrorMessage policyErrorMessage = restResponse.getRequestError().get(ExceptionType.POLICY);
        assertEquals("POL3102", policyErrorMessage.getMessageId());
        assertEquals("Error parsing input performing %1 on %2 (msg=%3) (ec=%4)", policyErrorMessage.getText());
        assertEquals("null", policyErrorMessage.getVariables().get(0));
        assertEquals("null", policyErrorMessage.getVariables().get(1));
        assertEquals("Error parsing input performing %1 on %2", policyErrorMessage.getVariables().get(2));
        assertEquals("ERR.5.1.3102", policyErrorMessage.getVariables().get(3));
    }

    @Test
    public void getRESTAPIServiceErrorResponseXmlTest() throws AAIException, JsonMappingException, JsonProcessingException {
        // AAI_3009=5:6:WARN:3009:400:3009:Malformed URL:300
        List<MediaType> headers = Collections.singletonList(MediaType.APPLICATION_XML_TYPE);
        ArrayList<String> variables = new ArrayList<String>();

        AAIException aaiException = new AAIException("AAI_3009");
        String errorResponse = ErrorLogHelper.getRESTAPIErrorResponse(headers, aaiException, variables);
        assertNotNull(errorResponse);

        Fault restResponse = xmlMapper.readValue(errorResponse, Fault.class);
        assertNotNull(restResponse);

        ErrorMessage serviceErrorMessage = restResponse.getRequestError().get(ExceptionType.SERVICE);
        assertEquals("SVC3009", serviceErrorMessage.getMessageId());
        assertEquals("Malformed URL (msg=%1) (ec=%2)", serviceErrorMessage.getText());
        assertEquals("Malformed URL", serviceErrorMessage.getVariables().get(0));
        assertEquals("ERR.5.6.3009", serviceErrorMessage.getVariables().get(1));
    }

    @Test
    public void getRESTAPIPolicyErrorResponseJsonTest() throws AAIException, JsonMappingException, JsonProcessingException {
        // AAI_3002=5:1:WARN:3002:400:3002:Error writing output performing %1 on %2:300
        List<MediaType> headers = Collections.singletonList(MediaType.APPLICATION_JSON_TYPE);
        ArrayList<String> args = new ArrayList<String>(Arrays.asList("PUT", "resource"));

        AAIException aaiException = new AAIException("AAI_3002");
        String errorResponse = ErrorLogHelper.getRESTAPIErrorResponse(headers, aaiException, args);
        assertNotNull(errorResponse);
        assertTrue(errorResponse.contains("policyException"));

        Fault restResponse = objectMapper.readValue(errorResponse, Fault.class);
        assertNotNull(restResponse);

        ErrorMessage policyErrorMessage = restResponse.getRequestError().get(ExceptionType.POLICY);
        assertEquals("POL3002", policyErrorMessage.getMessageId());

        List<String> variables = policyErrorMessage.getVariables();
        assertEquals("PUT", variables.get(0));
        assertEquals("resource", variables.get(1));
        assertEquals("Error writing output performing %1 on %2", variables.get(2));
        assertEquals("ERR.5.1.3002", variables.get(3));
    }

    @Test
    public void getRESTAPIServiceErrorResponseJsonTest() throws AAIException, JsonMappingException, JsonProcessingException {
        // AAI_3009=5:6:WARN:3009:400:3009:Malformed URL:300
        List<MediaType> headers = Collections.singletonList(MediaType.APPLICATION_JSON_TYPE);
        ArrayList<String> variables = new ArrayList<String>();

        AAIException aaiException = new AAIException("AAI_3009");
        String errorResponse = ErrorLogHelper.getRESTAPIErrorResponse(headers, aaiException, variables);
        assertNotNull(errorResponse);
        assertTrue(errorResponse.contains("serviceException"));

        org.onap.aai.domain.errorResponse.Fault restResponse =
            objectMapper.readValue(errorResponse, org.onap.aai.domain.errorResponse.Fault.class);
                
        Map<ExceptionType, ErrorMessage> requestError = restResponse.getRequestError();
        assertNotNull(requestError);
        ErrorMessage errorMessage = requestError.get(ExceptionType.SERVICE);
        assertEquals("SVC3009", errorMessage.getMessageId());
        assertEquals("Malformed URL (msg=%1) (ec=%2)", errorMessage.getText());
        assertEquals("Malformed URL", errorMessage.getVariables().get(0));
        assertEquals("ERR.5.6.3009", errorMessage.getVariables().get(1));

        ServiceFault serviceFault = objectMapper.readValue(errorResponse, ServiceFault.class);
        assertEquals("SVC3009", serviceFault.getRequestError().getServiceException().getMessageId());
    }

    @Test
    public void getRESTAPIServiceErrorResponseWithLoggingTest() throws IOException, InterruptedException {
        // AAI_3009=5:6:WARN:3009:400:3009:Malformed URL:300
        List<MediaType> headers = Collections.singletonList(MediaType.APPLICATION_JSON_TYPE);
        ArrayList<String> variables = new ArrayList<String>();

        AAIException aaiException = new AAIException("AAI_3009");
        String errorResponse = ErrorLogHelper.getRESTAPIErrorResponseWithLogging(headers, aaiException, variables);
        sleep(2000);
        assertNotNull(errorResponse);
        String logContents = LogFile.getContents(errorLogFileName);

        assertNotNull(logContents);
        String logContentParts[] = logContents.split("\\|");

        assertTrue(logContentParts.length >= 11);
        assertTrue(logContentParts[10].startsWith("ERR.5.6.3009"));

    }

    @Test
    public void thatErrorObjectCanBeRetrieved() throws ErrorObjectNotFoundException {
        ErrorObject errorObject = ErrorLogHelper.getErrorObject("AAI_3000");
        assertEquals("3000", errorObject.getErrorCode());
        assertEquals("3000", errorObject.getRESTErrorCode());
        assertEquals("Invalid input performing %1 on %2", errorObject.getErrorText());
        assertEquals("2", errorObject.getCategory());
        assertEquals("INFO", errorObject.getSeverity());
    }

    @Test
    public void thatInvalidErrorCodeWillReturnDefaultException() throws ErrorObjectNotFoundException {
        ErrorObject errorObject = ErrorLogHelper.getErrorObject("AAI_1234");
        assertEquals("4000", errorObject.getErrorCode());
        assertEquals("3002", errorObject.getRESTErrorCode());
        assertEquals("Internal Error", errorObject.getErrorText());
        assertEquals("4", errorObject.getCategory());
        assertEquals("ERROR", errorObject.getSeverity());
    }

    @Test
    public void thatInvalidMediaTypeWillReturnInvalidAcceptHeaderException() throws ErrorObjectNotFoundException, JsonMappingException, JsonProcessingException {
        String errorResponse = ErrorLogHelper.getRESTAPIErrorResponse(Collections.singletonList(MediaType.TEXT_PLAIN_TYPE), new AAIException(), new ArrayList<>());
        
        Fault restResponse = objectMapper.readValue(errorResponse, Fault.class);
        assertNotNull(restResponse);

        ErrorMessage serviceErrorMessage = restResponse.getRequestError().get(ExceptionType.SERVICE);
        List<String> variables = serviceErrorMessage.getVariables();
        assertEquals("SVC3000", serviceErrorMessage.getMessageId());
        assertEquals("null", variables.get(0));
        assertEquals("null", variables.get(1));
        assertEquals("Invalid Accept header", variables.get(2));
        assertEquals("4.0.4014", variables.get(3));
    }

    @Test
    public void thatRestApiInfoResponseCanBeRetrieved() {
        Map<AAIException, ArrayList<String>> aaiExceptionsMap = new HashMap<>();
        aaiExceptionsMap.put(new AAIException("AAI_0002", "OK"), new ArrayList<String>(Arrays.asList("someApp", "someTransactionId")));
        Info info = ErrorLogHelper.getRestApiInfoResponse(aaiExceptionsMap);
        ErrorMessage errorMessage = info.getErrorMessages().get(0);
        assertEquals("INF0001", errorMessage.getMessageId());
        assertEquals("Internal Error (msg=%1) (ec=%2)", errorMessage.getText());
        assertEquals("Successful health check:OK", errorMessage.getVariables().get(0));
        assertEquals("0.0.0002", errorMessage.getVariables().get(1));
    }

}
