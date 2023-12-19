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

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.aai.domain.restPolicyException.PolicyException;
import org.onap.aai.domain.restPolicyException.RESTResponse;
import org.onap.aai.domain.restPolicyException.RequestError;
import org.onap.aai.domain.restServiceException.ServiceException;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.util.LogFile;
import org.slf4j.MDC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

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
        sleep(2000);
        String logContents = LogFile.getContents(errorLogFileName);

        assertNotNull(logContents);

        String logContentParts[] = logContents.split("\\|");

        assertTrue(logContentParts.length >= 11);
        assertTrue(logContentParts[9].contains(errorMessage));
        assertTrue(logContentParts[10].startsWith("ERR.5.4.6110"));
    }

    @Test
    public void getRESTAPIPolicyErrorResponseTest() throws AAIException, JsonMappingException, JsonProcessingException {
        // AAI_3002=5:1:WARN:3002:400:3002:Error writing output performing %1 on %2:300
        List<MediaType> headers = Collections.singletonList(MediaType.APPLICATION_JSON_TYPE);
        ArrayList<String> args = new ArrayList<String>(Arrays.asList("PUT", "resource"));

        AAIException aaiException = new AAIException("AAI_3002");
        String errorResponse = ErrorLogHelper.getRESTAPIErrorResponse(headers, aaiException, args);
        assertNotNull(errorResponse);

        RESTResponse restResponse = objectMapper.readValue(errorResponse, RESTResponse.class);
        RequestError requestError = restResponse.getRequestError();
        assertEquals("POL3002", requestError.getPolicyException().getMessageId());

        List<String> variables = requestError.getPolicyException().getVariables();
        assertEquals("PUT", variables.get(0));
        assertEquals("resource", variables.get(1));
        assertEquals("Error writing output performing %1 on %2", variables.get(2));
        assertEquals("ERR.5.1.3002", variables.get(3));
    }

    @Test
    public void getRESTAPIJsonServiceErrorResponseTest() throws AAIException, JsonMappingException, JsonProcessingException {
        // AAI_3009=5:6:WARN:3009:400:3009:Malformed URL:300
        List<MediaType> headers = Collections.singletonList(MediaType.APPLICATION_JSON_TYPE);
        ArrayList<String> variables = new ArrayList<String>();

        AAIException aaiException = new AAIException("AAI_3009");
        String errorResponse = ErrorLogHelper.getRESTAPIErrorResponse(headers, aaiException, variables);
        assertNotNull(errorResponse);

        org.onap.aai.domain.restServiceException.RESTResponse restResponse =
            objectMapper.readValue(errorResponse, org.onap.aai.domain.restServiceException.RESTResponse.class);
                
        org.onap.aai.domain.restServiceException.RequestError requestError = restResponse.getRequestError();
        assertNotNull(requestError);
        ServiceException serviceException = requestError.getServiceException();
        assertNotNull(serviceException);
        assertEquals("SVC3009", serviceException.getMessageId());

    }

    @Test
    public void getRESTAPIXmlServiceErrorResponseCategoryOneTest() throws AAIException, JsonMappingException, JsonProcessingException {
        // AAI_3009=5:6:WARN:3009:400:3009:Malformed URL:300
        List<MediaType> headers = Collections.singletonList(MediaType.APPLICATION_XML_TYPE);
        ArrayList<String> variables = new ArrayList<String>();

        AAIException aaiException = new AAIException("AAI_3102");
        String errorResponse = ErrorLogHelper.getRESTAPIErrorResponse(headers, aaiException, variables);
        assertNotNull(errorResponse);

        RESTResponse restResponse =
            xmlMapper.readValue(errorResponse, RESTResponse.class);
        assertNotNull(restResponse);
            //         MapperUtil.readAsObjectOf(org.onap.aai.domain.restServiceException.RESTResponse.class, errorResponse);
        PolicyException policyException = restResponse.getRequestError().getPolicyException();
        assertEquals("POL3102", policyException.getMessageId());
        assertEquals("Error parsing input performing %1 on %2 (msg=%3) (ec=%4)", policyException.getText());
        assertEquals("null", policyException.getVariables().get(0));
        assertEquals("null", policyException.getVariables().get(1));
        assertEquals("Error parsing input performing %1 on %2", policyException.getVariables().get(2));
        assertEquals("ERR.5.1.3102", policyException.getVariables().get(3));
    }

    @Test
    public void getRESTAPIXmlServiceErrorResponseCategoryGreaterOneTest() throws AAIException, JsonMappingException, JsonProcessingException {
        // AAI_3009=5:6:WARN:3009:400:3009:Malformed URL:300
        List<MediaType> headers = Collections.singletonList(MediaType.APPLICATION_XML_TYPE);
        ArrayList<String> variables = new ArrayList<String>();

        AAIException aaiException = new AAIException("AAI_3009");
        String errorResponse = ErrorLogHelper.getRESTAPIErrorResponse(headers, aaiException, variables);
        assertNotNull(errorResponse);

        org.onap.aai.domain.restServiceException.RESTResponse restResponse =
            xmlMapper.readValue(errorResponse, org.onap.aai.domain.restServiceException.RESTResponse.class);
        assertNotNull(restResponse);
            //         MapperUtil.readAsObjectOf(org.onap.aai.domain.restServiceException.RESTResponse.class, errorResponse);
        ServiceException serviceException = restResponse.getRequestError().getServiceException();
        assertEquals("SVC3009", serviceException.getMessageId());
        assertEquals("Malformed URL (msg=%1) (ec=%2)", serviceException.getText());
        assertEquals("Malformed URL", serviceException.getVariables().get(0));
        assertEquals("ERR.5.6.3009", serviceException.getVariables().get(1));
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
        String response = ErrorLogHelper.getRESTAPIErrorResponse(Collections.singletonList(MediaType.TEXT_PLAIN_TYPE), new AAIException(), new ArrayList<>());
        
        org.onap.aai.domain.restServiceException.RESTResponse restResponse = objectMapper.readValue(response, org.onap.aai.domain.restServiceException.RESTResponse.class);
        List<String> variables = restResponse.getRequestError().getServiceException().getVariables();
        assertEquals("SVC3000", restResponse.getRequestError().getServiceException().getMessageId());
        assertEquals("null", variables.get(0));
        assertEquals("null", variables.get(1));
        assertEquals("Invalid Accept header", variables.get(2));
        assertEquals("4.0.4014", variables.get(3));
    }

}
