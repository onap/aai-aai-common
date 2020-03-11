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

import com.sun.jersey.api.client.ClientRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.onap.logging.filter.base.Constants;
import org.onap.logging.ref.slf4j.ONAPLogConstants;
import org.slf4j.MDC;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class RestControllerClientLoggingInterceptorTest {

        private ClientRequest clientRequest;

        @Spy
        @InjectMocks
        private RestControllerClientLoggingInterceptor restControllerClientLoggingInterceptor;

        @Before
        public void init() throws URISyntaxException {
            System.setProperty("javax.ws.rs.ext.RuntimeDelegate", "com.sun.ws.rs.ext.RuntimeDelegateImpl");
            clientRequest = ClientRequest.create().build(new URI("https://localhost:9999/aai/v1/cloud-infrastructure/complexes/complex/complex-1"),
                "GET");
        }

        @After
        public void tearDown() {
            MDC.clear();
        }

        @Test
        public void setupHeadersTest() throws java.net.URISyntaxException {

            String transId="37b3ab2a-e57e-4fe8-8d8f-eee3019efce6";
            MultivaluedMap<String, Object> requestHeaders = new MultivaluedHashMap<String, Object>();
            requestHeaders.add(Constants.HttpHeaders.TRANSACTION_ID, transId);
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
        public void getServiceNameTest()  {
            String serviceName = restControllerClientLoggingInterceptor.getServiceName(clientRequest);
            assertEquals("/aai/v1/cloud-infrastructure/complexes", serviceName);
        }

       @Test
        public void setupMDCTest() throws URISyntaxException {
           restControllerClientLoggingInterceptor.pre(clientRequest);
           assertEquals("/aai/v1/cloud-infrastructure/complexes", MDC.get(ONAPLogConstants.MDCs.TARGET_SERVICE_NAME) );
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
