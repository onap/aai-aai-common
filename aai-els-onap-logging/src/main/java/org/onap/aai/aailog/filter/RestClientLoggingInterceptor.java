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

import org.onap.aai.aailog.logs.ServiceName;
import org.onap.logging.filter.base.AbstractMetricLogFilter;
import org.onap.logging.filter.base.Constants;
import org.onap.logging.filter.base.MDCSetup;
import org.onap.logging.ref.slf4j.ONAPLogConstants;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.HttpHeaders;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class RestClientLoggingInterceptor extends AbstractMetricLogFilter<HttpRequest, ClientHttpResponse, HttpHeaders> implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException
    {
        this.setInvocationId(request.getHeaders());
        pre(request, request.getHeaders());
        ClientHttpResponse resp = execution.execute(request, body);
        post(request, resp);
        return resp;

    }
    protected void pre(HttpRequest request, HttpHeaders requestHeaders) {
        try {
            setupMDC(request);
            setupHeaders(request, requestHeaders);
            super.logInvoke();
        } catch (Exception e) {
            logger.warn("Error in RestClientLoggingInterceptor pre", e);
        }
    }
    protected void setupHeaders(HttpRequest clientRequest, HttpHeaders requestHeaders) {
        String requestId = extractRequestID(requestHeaders);
        addHeader(requestHeaders, ONAPLogConstants.Headers.REQUEST_ID, requestId);
        addHeader(requestHeaders, Constants.HttpHeaders.HEADER_REQUEST_ID, requestId);
        if (requestHeaders.getFirst(Constants.HttpHeaders.TRANSACTION_ID) == null ||
            requestHeaders.getFirst(Constants.HttpHeaders.TRANSACTION_ID).isEmpty()) {
            addHeader(requestHeaders, Constants.HttpHeaders.TRANSACTION_ID, requestId);
        }
        addHeader(requestHeaders, Constants.HttpHeaders.ECOMP_REQUEST_ID, requestId);
        addHeader(requestHeaders, ONAPLogConstants.Headers.INVOCATION_ID, MDC.get(ONAPLogConstants.MDCs.INVOCATION_ID));
        String pName = getProperty(Constants.Property.PARTNER_NAME);
        if (pName != null && (!pName.isEmpty())) {
            addHeader(requestHeaders, ONAPLogConstants.Headers.PARTNER_NAME, pName);
        }
    }
    protected String extractRequestID(HttpHeaders requestHeaders) {
        String requestId = MDC.get(ONAPLogConstants.MDCs.REQUEST_ID);
        if (requestId == null || requestId.isEmpty()) {
            requestId = requestHeaders.getFirst(Constants.HttpHeaders.TRANSACTION_ID);
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }
            MDC.put(ONAPLogConstants.MDCs.REQUEST_ID, requestId);
        }
        return requestId;
    }
    public void setInvocationId(HttpHeaders headers) {
        String invocationId = null;

        List<String> headerList = headers.get(ONAPLogConstants.Headers.INVOCATION_ID);
        if (headerList != null && (!headerList.isEmpty())) {
            for (String h : headerList) {
                if ( h != null && (!h.isEmpty()) ) {
                    invocationId = h;
                    break;
                }
            }
            headers.remove(ONAPLogConstants.Headers.INVOCATION_ID);
        }
        if (invocationId == null) {
            invocationId = UUID.randomUUID().toString();
        }
        MDC.put(ONAPLogConstants.MDCs.INVOCATION_ID, invocationId);
    }
    @Override
    protected void addHeader(HttpHeaders requestHeaders, String headerName, String headerValue) {
        requestHeaders.add(headerName, headerValue);
    }

    protected String getTargetServiceName(HttpRequest request) {
        return (getServiceName(request));
    }
    protected String getServiceName(HttpRequest request){
        String path = request.getURI().getRawPath();
        return(ServiceName.extractServiceName(path));
    }

    protected int getHttpStatusCode(ClientHttpResponse response) {
        int result = 0;
        if (response != null ) {
            try {
                result = response.getStatusCode().value();
            }
            catch (IOException e) {
                logger.warn("Error in RestClientLoggingInterceptor getHttpStatusCode {}", e.getMessage());
            }
        }
        return result;
    }

    protected String getResponseCode(ClientHttpResponse response) {
        String result = "";
        if (response != null ) {
            try {
                result = response.getStatusCode().toString();
            }
            catch (IOException e) {
                logger.warn("Error in RestClientLoggingInterceptor getResponseCode {}", e.getMessage());
            }
        }
        return result;
    }

    protected String getTargetEntity(HttpRequest request) {
        //TODO where do we get this from?
        return Constants.DefaultValues.UNKNOWN_TARGET_ENTITY;
    }
}
