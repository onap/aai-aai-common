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

import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import org.onap.aai.aailog.logs.ServiceName;
import org.onap.logging.filter.base.Constants;
import org.onap.logging.filter.base.MDCSetup;
import org.onap.logging.ref.slf4j.ONAPLogConstants;
import org.slf4j.*;

import javax.ws.rs.core.MultivaluedMap;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class RestControllerClientLoggingInterceptor extends ClientFilter {
    private static final Logger logger = LoggerFactory.getLogger(RestControllerClientLoggingInterceptor.class);
    private static final Marker INVOKE_RETURN = MarkerFactory.getMarker("INVOKE-RETURN");
    private final MDCSetup mdcSetup;
    private final String partnerName;

    public RestControllerClientLoggingInterceptor () {
        mdcSetup = new MDCSetup();
        partnerName = getPartnerName();
    }

    @Override
    public ClientResponse handle(ClientRequest clientRequest) throws ClientHandlerException {
        ClientResponse clientResponse = null;
        pre(clientRequest);
        // Call the next client handler in the filter chain
        ClientHandler nextHandler = getNext();
        if (nextHandler != null) {
            clientResponse = nextHandler.handle(clientRequest);
        }
        if (clientResponse != null) {
            post(clientResponse);
        }
        return clientResponse;
    }

    protected  String getTargetServiceName(ClientRequest clientRequest) {
        return getServiceName(clientRequest);
    }

    protected  String getServiceName(ClientRequest clientRequest) {
        String path = clientRequest.getURI().getRawPath();
        return ServiceName.extractServiceName(path);
    }

    protected int getHttpStatusCode(ClientResponse response) {
        return response.getStatus();
    }

    protected  String getResponseCode(ClientResponse clientResponse) {
        return String.valueOf(clientResponse.getStatus());
    }


    protected  String getTargetEntity(ClientRequest ClientRequest) {
        return Constants.DefaultValues.UNKNOWN_TARGET_ENTITY;
    };

    protected void pre(ClientRequest clientRequest) {
        try {
            setInvocationId(clientRequest);
            setupMDC(clientRequest);
            setupHeaders(clientRequest);
            logger.info(ONAPLogConstants.Markers.INVOKE, "Invoke");
        } catch (Exception e) {
            logger.warn("Error in RestControllerClientLoggingInterceptor pre", e.getMessage());
        }
    }
    public void setInvocationId(ClientRequest clientRequest) {
        String invocationId = null;
        MultivaluedMap<String, Object> requestHeaders = clientRequest.getHeaders();
        Object id = requestHeaders.get(ONAPLogConstants.Headers.INVOCATION_ID);
        if (id != null) {
            invocationId = (String)id;
        }
        requestHeaders.remove(ONAPLogConstants.Headers.INVOCATION_ID);
        if (invocationId == null) {
            invocationId = UUID.randomUUID().toString();
        }
        MDC.put(ONAPLogConstants.MDCs.INVOCATION_ID, invocationId);
    }
    protected void setupHeaders(ClientRequest clientRequest) {
        String requestId = extractRequestID(clientRequest);
        MultivaluedMap<String, Object> requestHeaders = clientRequest.getHeaders();
        addHeader(requestHeaders, ONAPLogConstants.Headers.REQUEST_ID, requestId);
        addHeader(requestHeaders, Constants.HttpHeaders.HEADER_REQUEST_ID, requestId);
        Object requestIdObj = requestHeaders.getFirst(Constants.HttpHeaders.TRANSACTION_ID);
        if (requestIdObj == null) {
            addHeader(requestHeaders, Constants.HttpHeaders.TRANSACTION_ID, requestId);
        }
        addHeader(requestHeaders, Constants.HttpHeaders.ECOMP_REQUEST_ID, requestId);
        addHeader(requestHeaders, ONAPLogConstants.Headers.INVOCATION_ID, MDC.get(ONAPLogConstants.MDCs.INVOCATION_ID));
        if (partnerName != null && (!partnerName.isEmpty())){
            addHeader(requestHeaders, ONAPLogConstants.Headers.PARTNER_NAME, partnerName);
        }
    }

    protected void setupMDC(ClientRequest clientRequest) {
        MDC.put(ONAPLogConstants.MDCs.INVOKE_TIMESTAMP,
            ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
        MDC.put(ONAPLogConstants.MDCs.TARGET_SERVICE_NAME, getTargetServiceName(clientRequest));
        MDC.put(ONAPLogConstants.MDCs.RESPONSE_STATUS_CODE, ONAPLogConstants.ResponseStatus.INPROGRESS.toString());
        mdcSetup.setInvocationIdFromMDC();

        if (MDC.get(ONAPLogConstants.MDCs.TARGET_ENTITY) == null) {
            String targetEntity = getTargetEntity(clientRequest);
            if (targetEntity != null) {
                MDC.put(ONAPLogConstants.MDCs.TARGET_ENTITY, targetEntity);
            } else {
                MDC.put(ONAPLogConstants.MDCs.TARGET_ENTITY, Constants.DefaultValues.UNKNOWN_TARGET_ENTITY);
            }
        }

        if (MDC.get(ONAPLogConstants.MDCs.SERVICE_NAME) == null) {
            MDC.put(ONAPLogConstants.MDCs.SERVICE_NAME, getServiceName(clientRequest));
        }
        mdcSetup.setServerFQDN();
    }

    protected String extractRequestID(ClientRequest clientRequest) {
        String requestId = MDC.get(ONAPLogConstants.MDCs.REQUEST_ID);
        if (requestId == null || requestId.isEmpty()) {
            MultivaluedMap<String, Object> requestHeaders = clientRequest.getHeaders();
            Object requestIdObj = requestHeaders.getFirst(Constants.HttpHeaders.TRANSACTION_ID);
            if (requestIdObj != null) {
                requestId = (String)requestIdObj;
            }
            if ( requestId == null || requestId.isEmpty() ) {
                requestId = UUID.randomUUID().toString();
            }
            mdcSetup.setLogTimestamp();
            mdcSetup.setElapsedTimeInvokeTimestamp();
            logger.warn("No value found in MDC when checking key {} value will be set to {}",
                ONAPLogConstants.MDCs.REQUEST_ID, requestId);
            MDC.put(ONAPLogConstants.MDCs.REQUEST_ID, requestId);
        }
        return requestId;
    }

    protected void post(ClientResponse clientResponse) {
        try {
            mdcSetup.setLogTimestamp();
            mdcSetup.setElapsedTimeInvokeTimestamp();
            mdcSetup.setResponseStatusCode(getHttpStatusCode(clientResponse));
            mdcSetup.setResponseDescription(getHttpStatusCode(clientResponse));
            MDC.put(ONAPLogConstants.MDCs.RESPONSE_CODE, getResponseCode(clientResponse));
            logger.info(INVOKE_RETURN, "InvokeReturn");
            mdcSetup.clearClientMDCs();
        } catch (Exception e) {
            logger.warn("Error in RestControllerClientLoggingInterceptor post", e.getMessage());
        }
    }
    protected String getPartnerName() {
        return mdcSetup.getProperty(Constants.Property.PARTNER_NAME);
    }

    protected void addHeader(MultivaluedMap<String, Object> requestHeaders, String headerName, String headerValue) {
        requestHeaders.add(headerName, headerValue);
    }
}
