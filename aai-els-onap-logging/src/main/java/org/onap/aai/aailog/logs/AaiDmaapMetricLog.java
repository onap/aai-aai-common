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

import org.onap.aai.logging.AaiElsErrorCode;
import org.onap.logging.filter.base.MDCSetup;
import org.onap.logging.ref.slf4j.ONAPLogConstants;
import org.slf4j.*;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.PatternSyntaxException;

public class AaiDmaapMetricLog extends MDCSetup {

    protected static final Logger logger = LoggerFactory.getLogger(AaiDmaapMetricLog.class);
    private static final Marker INVOKE_RETURN = MarkerFactory.getMarker("INVOKE-RETURN");
    private static final String TARGET_ENTITY = "DMaaP";

    public AaiDmaapMetricLog() {
    }
    public void pre(String targetServiceName, String event, String transactionId, String serviceName) {

        try {
            MDC.put(ONAPLogConstants.MDCs.INVOKE_TIMESTAMP,
                ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
            setLogTimestamp();
            setElapsedTimeInvokeTimestamp();
            MDC.put(ONAPLogConstants.MDCs.TARGET_SERVICE_NAME, targetServiceName);
            MDC.put(ONAPLogConstants.MDCs.RESPONSE_STATUS_CODE, ONAPLogConstants.ResponseStatus.INPROGRESS.toString());
            MDC.put(ONAPLogConstants.MDCs.TARGET_ENTITY, TARGET_ENTITY);
            if (transactionId != null && !(transactionId.isEmpty())) {
                MDC.put(ONAPLogConstants.MDCs.REQUEST_ID, transactionId);
            }
            if (serviceName != null && !(serviceName.isEmpty())) {
                MDC.put(ONAPLogConstants.MDCs.SERVICE_NAME, serviceName);
            }
            setInvocationIdFromMDC();
            logger.info(ONAPLogConstants.Markers.INVOKE, event );

        } catch (Exception e) {
            logger.warn("Error in AaiDmaapMetricLog pre", e.getMessage());
        }
    }

    public void post(String responseCode, String errorDescription) {

        try {
            setLogTimestamp();
            setElapsedTimeInvokeTimestamp();
            setResponseStatusCode(responseCode, errorDescription);
            logger.info(INVOKE_RETURN, "InvokeReturn");
            clearClientMDCs();
        } catch (Exception e) {
            logger.warn("Error in AaiDmaapMetricLog post", e.getMessage());
        }
    }

    public void setResponseStatusCode(String aaiElsErrorCode, String errorDescription) {
        String statusCode;
        if (AaiElsErrorCode.SUCCESS.equals(aaiElsErrorCode)) {
            statusCode = ONAPLogConstants.ResponseStatus.COMPLETE.toString();
        }
        else {
            statusCode = ONAPLogConstants.ResponseStatus.ERROR.toString();
            MDC.put(ONAPLogConstants.MDCs.ERROR_CODE, aaiElsErrorCode);
            MDC.put(ONAPLogConstants.MDCs.ERROR_DESC, errorDescription);
        }
        MDC.put(ONAPLogConstants.MDCs.RESPONSE_STATUS_CODE, statusCode);
        MDC.put(ONAPLogConstants.MDCs.RESPONSE_CODE, aaiElsErrorCode);

    }
}
