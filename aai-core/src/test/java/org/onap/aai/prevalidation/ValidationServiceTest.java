/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2018-19 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.prevalidation;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.onap.aai.PayloadUtil;
import org.onap.aai.domain.notificationEvent.NotificationEvent;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.restclient.RestClient;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.system.OutputCaptureRule;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class ValidationServiceTest {

    private RestClient restClient;

    private ValidationService validationService;

    @Rule
    public OutputCaptureRule capture = new OutputCaptureRule();

    private Gson gson;
    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        mapper.registerModule(new JaxbAnnotationModule());
        gson = new Gson();
        restClient = Mockito.mock(RestClient.class);
        validationService = Mockito.spy(new ValidationService(restClient, "JUNIT", "generic-vnf", null, mapper));
    }

    @Test
    public void testNodeTypeThatIsAllowedAndItShouldReturnTrue() {
        boolean shouldValidate = validationService.shouldValidate("generic-vnf");
        assertThat(shouldValidate, is(true));
    }

    @Test
    public void testNodeTypeThatIsNotAllowedAndItShouldReturnFalse() {
        boolean shouldValidate = validationService.shouldValidate("customer");
        assertThat(shouldValidate, is(false));
    }

    @Test
    public void testPreValidateWithSuccessRequestAndServiceIsDownAndShouldErrorWithConnectionRefused()
            throws IOException, AAIException {

        String pserverRequest = PayloadUtil.getResourcePayload("prevalidation/success-request-with-no-violations.json");
        NotificationEvent notificationEvent = mapper.readValue(pserverRequest, NotificationEvent.class);

        Mockito.when(restClient.execute(eq(ValidationService.VALIDATION_ENDPOINT), eq(HttpMethod.POST), any(),
                anyString())).thenThrow(new RuntimeException(new ConnectException("Connection refused")));

        validationService.preValidate(notificationEvent);

        assertThat(capture.toString(),
                containsString("Connection refused to the validation microservice due to service unreachable"));
                ArgumentCaptor<String> bodyCaptor = forClass(String.class);
        verify(restClient).execute(eq(ValidationService.VALIDATION_ENDPOINT), eq(HttpMethod.POST), any(), bodyCaptor.capture());

        JSONAssert.assertEquals(pserverRequest, bodyCaptor.getValue(), false);
    }

    @Test
    public void testPreValidateWithSuccessRequestAndServiceIsUnreachableAndShouldErrorWithConnectionTimeout()
            throws IOException, AAIException {

        String pserverRequest = PayloadUtil.getResourcePayload("prevalidation/success-request-with-no-violations.json");
        NotificationEvent notificationEvent = mapper.readValue(pserverRequest, NotificationEvent.class);

        Mockito.when(restClient.execute(eq(ValidationService.VALIDATION_ENDPOINT), eq(HttpMethod.POST), any(),
                anyString()))
                .thenThrow(new RuntimeException(new ConnectTimeoutException("Connection timed out")));

        validationService.preValidate(notificationEvent);

        assertThat(capture.toString(), containsString(
                "Connection timeout to the validation microservice as this could indicate the server is unable to reach port"));
        ArgumentCaptor<String> bodyCaptor = forClass(String.class);
        verify(restClient).execute(eq(ValidationService.VALIDATION_ENDPOINT), eq(HttpMethod.POST), any(), bodyCaptor.capture());

        JSONAssert.assertEquals(pserverRequest, bodyCaptor.getValue(), false);
    }

    @Test
    public void testPreValidateWithSuccessRequestAndRespondSuccessfullyWithinAllowedTime()
            throws IOException, AAIException {

        String pserverRequest = PayloadUtil.getResourcePayload("prevalidation/success-request-with-no-violations.json");
        NotificationEvent notificationEvent = mapper.readValue(pserverRequest, NotificationEvent.class);
        String validationResponse =
                PayloadUtil.getResourcePayload("prevalidation/success-response-with-empty-violations.json");

        ResponseEntity responseEntity = Mockito.mock(ResponseEntity.class, Mockito.RETURNS_DEEP_STUBS);

        Mockito.when(restClient.execute(eq(ValidationService.VALIDATION_ENDPOINT), eq(HttpMethod.POST), any(),
                anyString())).thenReturn(responseEntity);

        Mockito.when(responseEntity.getStatusCodeValue()).thenReturn(200);
        Mockito.when(responseEntity.getBody()).thenReturn(validationResponse);

        Mockito.doReturn(true).when(validationService).isSuccess(responseEntity);

        List<String> errorMessages = validationService.preValidate(notificationEvent);
        assertNotNull("Expected the error messages to be not null", errorMessages);
        assertThat(errorMessages.size(), is(0));

        ArgumentCaptor<String> bodyCaptor = forClass(String.class);
        verify(restClient).execute(eq(ValidationService.VALIDATION_ENDPOINT), eq(HttpMethod.POST), any(), bodyCaptor.capture());

        JSONAssert.assertEquals(pserverRequest, bodyCaptor.getValue(), false);
    }

    @Test
    public void testPreValidateWithSuccessRequestAndServiceIsAvailableAndRequestIsTakingTooLongAndClientShouldTimeout()
            throws IOException, AAIException {

        String pserverRequest = PayloadUtil.getResourcePayload("prevalidation/success-request-with-no-violations.json");
        NotificationEvent notificationEvent = mapper.readValue(pserverRequest, NotificationEvent.class);

        Mockito.when(restClient.execute(eq(ValidationService.VALIDATION_ENDPOINT), eq(HttpMethod.POST), any(),
                anyString()))
                .thenThrow(new RuntimeException(
                        new SocketTimeoutException("Request timed out due to taking longer than client expected")));

        validationService.preValidate(notificationEvent);

        assertThat(capture.toString(),
                containsString("Request to validation service took longer than the currently set timeout"));
                ArgumentCaptor<String> bodyCaptor = forClass(String.class);
        verify(restClient).execute(eq(ValidationService.VALIDATION_ENDPOINT), eq(HttpMethod.POST), any(), bodyCaptor.capture());

        JSONAssert.assertEquals(pserverRequest, bodyCaptor.getValue(), false);
    }

    @Test
    public void testExtractViolationsReturnsSuccessfullyAListWhenViolationsAreFound() throws IOException {

        String genericVnfRequest = PayloadUtil.getResourcePayload("prevalidation/failed-response-with-violations.json");

        Validation validation = gson.fromJson(genericVnfRequest, Validation.class);
        List<String> errorMessages = validationService.extractViolations(validation);
        assertNotNull("Expected the error messages to be not null", errorMessages);
        assertThat(errorMessages.size(), is(1));
        assertThat(errorMessages.get(0),
                is("Invalid nf values, check nf-type, nf-role, nf-function, and nf-naming-code"));
    }

    @Test
    public void testErrorMessagesAreEmptyListWhenViolationsReturnEmptyList() throws IOException {

        String genericVnfRequest =
                PayloadUtil.getResourcePayload("prevalidation/success-response-with-empty-violations.json");

        Validation validation = gson.fromJson(genericVnfRequest, Validation.class);
        List<String> errorMessages = validationService.extractViolations(validation);
        assertNotNull("Expected the error messages to be not null", errorMessages);
        assertThat(errorMessages.size(), is(0));
    }

    @Test
    public void testErrorMessagesAreEmptyListWhenViolationsIsNotFoundInJson() throws IOException {

        String genericVnfRequest =
                PayloadUtil.getResourcePayload("prevalidation/success-response-with-exclude-violations.json");

        Validation validation = gson.fromJson(genericVnfRequest, Validation.class);
        List<String> errorMessages = validationService.extractViolations(validation);
        assertNotNull("Expected the error messages to be not null", errorMessages);
        assertThat(errorMessages.size(), is(0));
    }
}
