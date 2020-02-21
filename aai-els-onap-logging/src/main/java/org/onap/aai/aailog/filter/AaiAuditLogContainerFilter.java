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

import org.onap.aai.aailog.logs.ServiceName;
import org.onap.logging.filter.base.AuditLogContainerFilter;
import org.onap.logging.filter.base.Constants;
import org.onap.logging.filter.base.SimpleMap;
import org.onap.logging.ref.slf4j.ONAPLogConstants;
import org.slf4j.MDC;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

@PreMatching
@Priority(1)
public class AaiAuditLogContainerFilter extends AuditLogContainerFilter {
    @Override
    public void setMDCPartnerName(SimpleMap headers) {
        logger.trace("Checking X-ONAP-PartnerName header for partnerName.");
        String partnerName = headers.get(ONAPLogConstants.Headers.PARTNER_NAME);
        if (partnerName == null || partnerName.isEmpty()) {
            logger.trace("No valid X-ONAP-PartnerName header value. Checking X-FromAppId header for partnerName.");
            partnerName = headers.get (Constants.HttpHeaders.HEADER_FROM_APP_ID);
            if (partnerName == null || partnerName.isEmpty()) {
                logger.trace("No valid X-FromAppId header value. Checking User-Agent header for partnerName.");
                partnerName = headers.get(HttpHeaders.USER_AGENT);
                if (partnerName == null || partnerName.isEmpty()) {
                    logger.trace("No valid User-Agent header value. Checking X-ClientID header for partnerName.");
                    partnerName = headers.get(Constants.HttpHeaders.CLIENT_ID);
                    if (partnerName == null || partnerName.isEmpty()) {
                        logger.trace("No valid partnerName headers. Defaulting partnerName to UNKNOWN.");
                        partnerName = Constants.DefaultValues.UNKNOWN;
                    }
                }
            }
        }
        MDC.put(ONAPLogConstants.MDCs.PARTNER_NAME, partnerName);
    }
    @Override
    protected void setServiceName(ContainerRequestContext containerRequest) {
        UriInfo uriInfo = containerRequest.getUriInfo();
        String serviceName = ServiceName.extractServiceName(uriInfo.getAbsolutePath().getRawPath());
        MDC.put(ONAPLogConstants.MDCs.SERVICE_NAME, serviceName);
    }

    @Override
    protected void pre(SimpleMap headers, ContainerRequestContext request, HttpServletRequest httpServletRequest) {
        try {
            String requestId = getRequestId(headers);
            MDC.put(ONAPLogConstants.MDCs.REQUEST_ID, requestId);
            // handle the case where the request ID value was invalid and we had to generate a new one
            addRequestIdHeader(request, requestId);
            setInvocationId(headers);
            setServiceName(request);
            setMDCPartnerName(headers);
            setServerFQDN();
            setClientIPAddress(httpServletRequest);
            setInstanceID();
            setEntryTimeStamp();
            MDC.put(ONAPLogConstants.MDCs.RESPONSE_STATUS_CODE, ONAPLogConstants.ResponseStatus.INPROGRESS.toString());
            additionalPreHandling(request);
            setLogTimestamp();
            setElapsedTime();
            logger.info(ONAPLogConstants.Markers.ENTRY, "Entering");
        } catch (Exception e) {
            logger.warn("Error in AaiAuditContainerFilter pre", e);
        }
    }

    protected void addRequestIdHeader(ContainerRequestContext containerRequest, String requestId) {
        if (containerRequest.getHeaders().get(ONAPLogConstants.Headers.REQUEST_ID) != null) {
            containerRequest.getHeaders().get(ONAPLogConstants.Headers.REQUEST_ID).clear();
        }
        containerRequest.getHeaders().add(ONAPLogConstants.Headers.REQUEST_ID, requestId);
    };
}
