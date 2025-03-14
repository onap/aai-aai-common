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

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Providers;

import org.onap.logging.ref.slf4j.ONAPLogConstants;
import org.slf4j.MDC;

@Priority(1)
public class AuditLogContainerFilter extends AbstractAuditLogFilter<ContainerRequestContext, ContainerResponseContext>
        implements ContainerRequestFilter, ContainerResponseFilter {

    @Context
    private HttpServletRequest httpServletRequest;

    @Context
    private Providers providers;

    @Override
    public void filter(ContainerRequestContext containerRequest) {
        SimpleMap headers = new SimpleJaxrsHeadersMap(containerRequest.getHeaders());
        pre(headers, containerRequest, httpServletRequest);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        post(responseContext);
    }

    @Override
    protected void setServiceName(ContainerRequestContext containerRequest) {
        MDC.put(ONAPLogConstants.MDCs.SERVICE_NAME, containerRequest.getUriInfo().getPath());
    }

    @Override
    protected int getResponseCode(ContainerResponseContext response) {
        return response.getStatus();
    }

}
