/*-
 * ============LICENSE_START=======================================================
 * ONAP - Logging
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.logging.filter.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.logging.ref.slf4j.ONAPLogConstants;
import org.slf4j.MDC;

@ExtendWith(MockitoExtension.class)
public class AuditLogServletFilterTest {

    @Mock
    private ServletRequest request;

    @Mock
    private ServletResponse response;

    @Mock
    private HttpServletRequest servletRequest;

    @Mock
    private HttpServletResponse servletResponse;

    @Spy
    @InjectMocks
    private AuditLogServletFilter auditLogServletFilter;

    @AfterEach
    public void tearDown() {
        MDC.clear();
    }

    @Test
    public void preTest() {
        when(servletRequest.getRequestURI()).thenReturn("onap/so/serviceInstances");
        auditLogServletFilter.pre(servletRequest);

        assertEquals("onap/so/serviceInstances", MDC.get(ONAPLogConstants.MDCs.SERVICE_NAME));
        assertEquals("INPROGRESS", MDC.get(ONAPLogConstants.MDCs.RESPONSE_STATUS_CODE));

    }

    @Test
    public void getResponseCodeTest() {
        when(servletResponse.getStatus()).thenReturn(200);
        int responseCode = auditLogServletFilter.getResponseCode(servletResponse);

        assertEquals(200, responseCode);
    }

}
