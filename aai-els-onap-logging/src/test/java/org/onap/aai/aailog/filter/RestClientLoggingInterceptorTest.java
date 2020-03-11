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

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.onap.logging.filter.base.Constants;
import org.onap.logging.ref.slf4j.ONAPLogConstants;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class RestClientLoggingInterceptorTest {

        @Mock
        private HttpRequest httpRequest;

        @Spy
        @InjectMocks
        private RestClientLoggingInterceptor restClientLoggingInterceptor;

        @After
        public void tearDown() {
            MDC.clear();
        }

        @Test
        public void setupHeadersTest() {
            String transId="37b3ab2a-e57e-4fe8-8d8f-eee3019efce6";
            HttpHeaders headers = new HttpHeaders();
            headers.add(Constants.HttpHeaders.TRANSACTION_ID, transId);
            restClientLoggingInterceptor.setupHeaders(httpRequest, headers);

            assertEquals(transId, headers.getFirst(Constants.HttpHeaders.TRANSACTION_ID));
            assertEquals(transId, headers.getFirst(Constants.HttpHeaders.ECOMP_REQUEST_ID));
            assertEquals(transId, headers.getFirst(ONAPLogConstants.Headers.REQUEST_ID));
            assertEquals(transId, headers.getFirst(Constants.HttpHeaders.HEADER_REQUEST_ID));
        }

        @Test
        public void getServiceNameTest() throws URISyntaxException {
            URI uri = null;
            try {
                uri = new URI("https://localhost:9999/aai/v1/cloud-infrastructure/complexes/complex/complex-1");
            } catch (URISyntaxException e) {
                throw e;
            }
            doReturn(uri).when(httpRequest).getURI();
            String serviceName = restClientLoggingInterceptor.getServiceName(httpRequest);

            assertEquals("/aai/v1/cloud-infrastructure/complexes", serviceName);
        }

       @Test
        public void setupMDCTest() throws URISyntaxException {
           URI uri = new URI("https://localhost:9999/aai/v1/cloud-infrastructure/complexes/complex/complex-1");
           doReturn(uri).when(httpRequest).getURI();
           HttpHeaders headers = new HttpHeaders();
           restClientLoggingInterceptor.pre(httpRequest, headers);
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
           assertNotNull(headers.getFirst(ONAPLogConstants.Headers.INVOCATION_ID));
        }
}
