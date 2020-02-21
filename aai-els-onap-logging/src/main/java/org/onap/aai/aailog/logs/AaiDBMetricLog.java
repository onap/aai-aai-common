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

package org.onap.aai.aailog.logs;

import org.onap.logging.filter.base.AbstractMetricLogFilter;
import org.onap.logging.filter.base.ONAPComponents;
import org.onap.logging.ref.slf4j.ONAPLogConstants;
import org.slf4j.*;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class AaiDBMetricLog
        extends AbstractMetricLogFilter<DBRequestWrapper, Response, MultivaluedMap<String, Object>> {

    protected static final Logger logger = LoggerFactory.getLogger(AaiDBMetricLog.class);
    private final String partnerName;
    private static final Marker INVOKE_RETURN = MarkerFactory.getMarker("INVOKE-RETURN");
    private static final String TARGET_ENTITY = ONAPComponents.AAI.toString() + ".DB";
    public AaiDBMetricLog(String subcomponent) {
        partnerName = getPartnerName(subcomponent);
    }

    @Override
    protected void addHeader(MultivaluedMap<String, Object> requestHeaders, String headerName, String headerValue) {
        requestHeaders.add(headerName, headerValue);
    }

    @Override
    protected String getTargetServiceName(DBRequestWrapper request) {
        return (getServiceName(request));
    }

    @Override
    protected String getServiceName(DBRequestWrapper request) {
        String path = request.getUri().getRawPath();
        return ServiceName.extractServiceName(path);
    }

    @Override
    protected int getHttpStatusCode(Response response) {
        int intCode = response.getStatus();
        return intCode;
    }

    @Override
    protected String getResponseCode(Response response) {
        return String.valueOf(response.getStatus());
    }

    @Override
    protected String getTargetEntity(DBRequestWrapper request) {
        return TARGET_ENTITY;
    }

    protected String getPartnerName(String subcomponent) {
        StringBuilder sb = new StringBuilder(ONAPComponents.AAI.toString()).append(subcomponent);
        return (sb.toString());
    }

    public void pre(DBRequestWrapper request) {
        try {
            setupMDC(request);
            setLogTimestamp();
            logger.info(ONAPLogConstants.Markers.INVOKE, "Invoke");
        } catch (Exception e) {
            logger.warn("Error in AaiDBMetricLog pre", e);
        }
    }
    public void post(DBRequestWrapper request, Response response) {
        try {
            setLogTimestamp();
            setElapsedTimeInvokeTimestamp();
            setResponseStatusCode(getHttpStatusCode(response));
            setResponseDescription(getHttpStatusCode(response));
            MDC.put(ONAPLogConstants.MDCs.RESPONSE_CODE, getResponseCode(response));
            logger.info(INVOKE_RETURN, "InvokeReturn");
            clearClientMDCs();
        } catch (Exception e) {
            logger.warn("Error in AaiDBMetricLog post", e);
        }
    }
    @Override
    public void setResponseStatusCode(int code) {
        String statusCode;
        if (code / 100 == 2) {
            statusCode = ONAPLogConstants.ResponseStatus.COMPLETE.toString();
        }
        else {
            statusCode = ONAPLogConstants.ResponseStatus.ERROR.toString();
            setErrorCode(code);
            setErrorDesc(code);
        }
        MDC.put(ONAPLogConstants.MDCs.RESPONSE_STATUS_CODE, statusCode);
    }
}
