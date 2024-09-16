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

import java.io.IOException;
import java.util.UUID;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.MultivaluedMap;

import org.onap.logging.filter.base.Constants;
import org.onap.logging.filter.base.MDCSetup;
import org.onap.logging.ref.slf4j.ONAPLogConstants;
import org.slf4j.*;

public class RestControllerClientResponseLoggingInterceptor implements ClientResponseFilter {
    private static final Logger logger = LoggerFactory.getLogger(RestControllerClientRequestLoggingInterceptor.class);
    private static final Marker INVOKE_RETURN = MarkerFactory.getMarker("INVOKE-RETURN");
    private final MDCSetup mdcSetup;
    private final String partnerName;

    public RestControllerClientResponseLoggingInterceptor() {
        mdcSetup = new MDCSetup();
        partnerName = getPartnerName();
    }

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        post(responseContext);
    }

    protected void post(ClientResponseContext responseContext) {
        try {
            mdcSetup.setLogTimestamp();
            mdcSetup.setElapsedTimeInvokeTimestamp();
            mdcSetup.setResponseStatusCode(getHttpStatusCode(responseContext));
            mdcSetup.setResponseDescription(getHttpStatusCode(responseContext));
            MDC.put(ONAPLogConstants.MDCs.RESPONSE_CODE, getResponseCode(responseContext));
            logger.info(INVOKE_RETURN, "InvokeReturn");
            mdcSetup.clearClientMDCs();
        } catch (Exception e) {
            logger.warn("Error in RestControllerClientLoggingInterceptor post", e.getMessage());
        }
    }

    protected int getHttpStatusCode(ClientResponseContext responseContext) {
        return responseContext.getStatus();
    }

    protected String getResponseCode(ClientResponseContext responseContext) {
        return String.valueOf(responseContext.getStatus());
    }

    public void setInvocationId(ClientRequestContext requestContext) {
        String invocationId = null;
        MultivaluedMap<String, Object> requestHeaders = requestContext.getHeaders();
        Object id = requestHeaders.get(ONAPLogConstants.Headers.INVOCATION_ID);
        if (id != null) {
            invocationId = (String) id;
        }
        requestHeaders.remove(ONAPLogConstants.Headers.INVOCATION_ID);
        if (invocationId == null) {
            invocationId = UUID.randomUUID().toString();
        }
        MDC.put(ONAPLogConstants.MDCs.INVOCATION_ID, invocationId);
    }

    protected String getPartnerName() {
        return mdcSetup.getProperty(Constants.Property.PARTNER_NAME);
    }
}
