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

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AaiAuditLogContainerFilterTest {
    @Mock
    private ContainerRequestContext containerRequest;

    @Mock
    private ContainerResponseContext containerResponse;

    @Mock
    private UriInfo uriInfo;

    @Spy
    @InjectMocks
    private AaiAuditLogContainerFilter aaiAuditFilter;

    @After
    public void tearDown() {
        MDC.clear();
    }
    @Test
    public void partnerAndServiceNameValueTest() throws java.net.URISyntaxException {

        MultivaluedMap<String, String> headerMap = new MultivaluedHashMap<>();
        headerMap.putSingle(Constants.HttpHeaders.HEADER_FROM_APP_ID, "FROM_APP_ID_TEST");
        when(containerRequest.getHeaders()).thenReturn(headerMap);

        URI uri = null;
        try {
            uri = new URI("https://localhost:9999/onap/aai/network/logical-link");
        }
        catch (java.net.URISyntaxException e) {
            throw e;
        }
        when(uriInfo.getAbsolutePath()).thenReturn(uri);
        when(containerRequest.getUriInfo()).thenReturn(uriInfo);

        aaiAuditFilter.filter(containerRequest);
        assertEquals("FROM_APP_ID_TEST", MDC.get(ONAPLogConstants.MDCs.PARTNER_NAME));
        assertEquals("/onap/aai/network/logical-link", MDC.get(ONAPLogConstants.MDCs.SERVICE_NAME));
    }

}

