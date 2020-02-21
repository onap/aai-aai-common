/**
 * ============LICENSE_START=======================================================
 * org.onap.logging
 * ================================================================================
 * Copyright © 2018 Amdocs
 * All rights reserved.
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

import org.onap.logging.filter.base.MDCSetup;
import org.onap.logging.ref.slf4j.ONAPLogConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import java.util.UUID;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AaiScheduledTaskAuditLog extends MDCSetup {
    protected static Logger logger = LoggerFactory.getLogger(AaiScheduledTaskAuditLog.class);

    public void logBefore(String serviceName, String partnerName) {
        try {
            String requestId = UUID.randomUUID().toString();
            MDC.put(ONAPLogConstants.MDCs.REQUEST_ID, requestId);
            setInvocationIdFromMDC();
            MDC.put(ONAPLogConstants.MDCs.SERVICE_NAME, serviceName);
            MDC.put(ONAPLogConstants.MDCs.PARTNER_NAME, partnerName);
            setServerFQDN();
            setClientIPAddress(null);
            setInstanceID();
            setEntryTimeStamp();
            MDC.put(ONAPLogConstants.MDCs.RESPONSE_STATUS_CODE, ONAPLogConstants.ResponseStatus.INPROGRESS.toString());
            setLogTimestamp();
            setElapsedTime();
            logger.info(ONAPLogConstants.Markers.ENTRY, "Entering");
        } catch (Exception e) {
            logger.warn("Error in AaiScheduledTaskAuditLog logBefore", e.getMessage());
        }

    }

    public void logAfter() {
        try {
            // TODO: how do we know if there was an error
            setResponseStatusCode(Response.Status.OK.getStatusCode());
            setLogTimestamp();
            setElapsedTime();
            logger.info(ONAPLogConstants.Markers.EXIT, "Exiting.");
        } catch (Exception e) {
            logger.warn("Error in AaiScheduledTaskAuditLog logAfter", e.getMessage());
        } finally {
            MDC.clear();
        }
    }
}
