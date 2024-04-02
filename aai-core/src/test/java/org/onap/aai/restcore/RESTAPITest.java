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

package org.onap.aai.restcore;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.exceptions.AAIException;

public class RESTAPITest extends AAISetup {
    private static RESTAPI restapi;
    private static HttpHeaders httpHeaders;
    private static Callable<Response> callable;
    private static UriInfo info;
    private static Response response;

    public static final String AAI_TIMEOUT_ENABLED = "aai.timeout.enabled";
    public static final String AAI_TIMEOUT_BY_APP = "aai.timeout.by.app";
    public static final String AAI_TIMEOUT_DEFAULT_LIMIT = "aai.timeout.default.limit";

    @BeforeAll
    public static void setUp() {
        restapi = new RESTAPI();
        httpHeaders = mock(HttpHeaders.class);
        callable = mock(Callable.class);
        info = mock(UriInfo.class);
        response = mock(Response.class);
    }

    @Test
    public void testGetFromAppId() throws AAIException {
        List<String> fromAppIdList = new ArrayList<>();
        fromAppIdList.add("from-app-id-01");
        when(httpHeaders.getRequestHeader("X-FromAppId")).thenReturn(fromAppIdList);

        String fromAppId = restapi.getFromAppId(httpHeaders);
        Assertions.assertEquals("from-app-id-01", fromAppId);
    }

    @Test
    public void testGetFromAppId_throwAAIException() throws AAIException {
        assertThrows(AAIException.class, () -> {
            when(httpHeaders.getRequestHeader("X-FromAppId")).thenReturn(null);
            restapi.getFromAppId(httpHeaders);
        });
    }

    @Test
    public void testGetTransId() throws AAIException {
        List<String> transactionIdList = new ArrayList<>();
        transactionIdList.add("transaction-id-01");
        when(httpHeaders.getRequestHeader("X-TransactionId")).thenReturn(transactionIdList);

        String transId = restapi.getTransId(httpHeaders);
        Assertions.assertEquals("transaction-id-01", transId);
    }

    @Test
    public void testGetTransId_throwAAIException() throws AAIException {
        assertThrows(AAIException.class, () -> {
            when(httpHeaders.getRequestHeader("X-TransactionId")).thenReturn(null);
            String transId = restapi.getTransId(httpHeaders);
        });
    }

    @Test
    public void testRunner() throws AAIException, Exception {
        MultivaluedMap<String, String> requestHeaders = new MultivaluedHashMap<String, String>();
        requestHeaders.add("X-FromAppId", "from-app-id-01");
        requestHeaders.add("X-TransactionId", "transaction-id-01");
        when(httpHeaders.getRequestHeaders()).thenReturn(requestHeaders);
        when(callable.call()).thenReturn(response);

        Response resp = restapi.runner(AAI_TIMEOUT_ENABLED, AAI_TIMEOUT_BY_APP, AAI_TIMEOUT_DEFAULT_LIMIT, httpHeaders,
                info, HttpMethod.GET, callable);
        Assertions.assertNotNull(resp);
    }
}
