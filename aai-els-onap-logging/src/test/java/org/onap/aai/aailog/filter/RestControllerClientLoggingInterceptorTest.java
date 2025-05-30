/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright Â© 2017-2018 AT&T Intellectual Property. All rights reserved.
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

package org.onap.aai.aailog.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.onap.logging.filter.base.Constants;
import org.onap.logging.ref.slf4j.ONAPLogConstants;
import org.slf4j.MDC;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

@MockitoSettings(strictness = Strictness.WARN)
@ExtendWith(MockitoExtension.class)
public class RestControllerClientLoggingInterceptorTest {

    @Spy
    private ClientRequestContext clientRequest;

    @Spy
    @InjectMocks
    private RestControllerClientRequestLoggingInterceptor restControllerClientLoggingInterceptor;

    @BeforeEach
    public void init() throws URISyntaxException {
        System.setProperty("jakarta.ws.rs.ext.RuntimeDelegate", "com.sun.ws.rs.ext.RuntimeDelegateImpl");
        when(clientRequest.getHeaders()).thenReturn(new MultivaluedHashMap<String, Object>());
        when(clientRequest.getUri())
                .thenReturn(new URI("https://localhost:9999/aai/v1/cloud-infrastructure/complexes/complex/complex-1"));

        // clientRequest = ClientRequest.create().build(
        // new
        // URI("https://localhost:9999/aai/v1/cloud-infrastructure/complexes/complex/complex-1"),
        // "GET");
    }

    @AfterEach
    public void tearDown() {
        MDC.clear();
    }

    @Test
    public void setupHeadersTest() throws java.net.URISyntaxException {

        String transId = "37b3ab2a-e57e-4fe8-8d8f-eee3019efce6";
        MultivaluedMap<String, Object> requestHeaders = new MultivaluedHashMap<String, Object>();
        requestHeaders.add(Constants.HttpHeaders.TRANSACTION_ID, transId);
        when(clientRequest.getHeaders()).thenReturn(requestHeaders);
        when(clientRequest.getUri())
                .thenReturn(new URI("https://localhost:9999/aai/v1/cloud-infrastructure/complexes/complex/complex-1"));

        clientRequest.getHeaders().putAll(requestHeaders);
        restControllerClientLoggingInterceptor.pre(clientRequest);
        MultivaluedMap<String, Object> headers = clientRequest.getHeaders();

        assertEquals(transId, headers.getFirst(Constants.HttpHeaders.TRANSACTION_ID));
        assertEquals(transId, headers.getFirst(Constants.HttpHeaders.ECOMP_REQUEST_ID));
        assertEquals(transId, headers.getFirst(ONAPLogConstants.Headers.REQUEST_ID));
        assertEquals(transId, headers.getFirst(Constants.HttpHeaders.HEADER_REQUEST_ID));
        assertNotNull(headers.getFirst(ONAPLogConstants.Headers.INVOCATION_ID));
    }

    @Test
    public void getServiceNameTest() {
        String serviceName = restControllerClientLoggingInterceptor.getServiceName(clientRequest);
        assertEquals("/aai/v1/cloud-infrastructure/complexes", serviceName);
    }

    @Test
    public void setupMDCTest() throws URISyntaxException {
        restControllerClientLoggingInterceptor.pre(clientRequest);
        assertEquals("/aai/v1/cloud-infrastructure/complexes", MDC.get(ONAPLogConstants.MDCs.TARGET_SERVICE_NAME));
        assertEquals("INPROGRESS", MDC.get(ONAPLogConstants.MDCs.RESPONSE_STATUS_CODE));
        String serverFQDN = "";
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
            serverFQDN = addr.getCanonicalHostName();

        } catch (UnknownHostException e) {
            serverFQDN = "";
        }
        assertEquals(serverFQDN, MDC.get(ONAPLogConstants.MDCs.SERVER_FQDN));
    }
}
