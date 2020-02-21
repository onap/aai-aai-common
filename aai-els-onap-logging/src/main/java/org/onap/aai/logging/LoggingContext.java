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

package org.onap.aai.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoggingContext {

    private static final Logger logger = LoggerFactory.getLogger(LoggingContext.class);

    // Response codes from Logging Guidelines
    public static final String SUCCESS = "0";
    public static final String PERMISSION_ERROR = "100";
    public static final String AVAILABILITY_TIMEOUT_ERROR = "200";
    public static final String DATA_ERROR = "300";
    public static final String SCHEMA_ERROR = "400";
    public static final String BUSINESS_PROCESS_ERROR = "500";
    public static final String UNKNOWN_ERROR = "900";

    public static final Map<String, String> responseMap = new HashMap();


    // Specific Log Event Fields
    public static enum LoggingField {
        START_TIME("startTime"), REQUEST_ID("requestId"), SERVICE_INSTANCE_ID("serviceInstanceId"), SERVER_NAME(
                "serverName"), SERVICE_NAME("serviceName"), PARTNER_NAME("partnerName"), STATUS_CODE(
                        "statusCode"), RESPONSE_CODE("responseCode"), RESPONSE_DESCRIPTION(
                                "responseDescription"), INSTANCE_UUID("instanceUUID"), SEVERITY(
                                        "severity"), SERVER_IP_ADDRESS(
                                                "serverIpAddress"), ELAPSED_TIME("elapsedTime"), SERVER(
                                                        "server"), CLIENT_IP_ADDRESS("clientIpAddress"), UNUSED(
                                                                "unused"), PROCESS_KEY("processKey"), CUSTOM_FIELD_1(
                                                                        "customField1"), CUSTOM_FIELD_2(
                                                                                "customField2"), CUSTOM_FIELD_3(
                                                                                        "customField3"), CUSTOM_FIELD_4(
                                                                                                "customField4"),

        // Specific Metric Log Event Fields
        TARGET_ENTITY("targetEntity"), TARGET_SERVICE_NAME("targetServiceName"),
        // A&AI Specific Log Event Fields
        COMPONENT("component"), STOP_WATCH_START("stopWatchStart");

        private final String text;

        private LoggingField(final String text) {
            this.text = text;
        }

        public String toString() {
            return text;
        }
    }

    public static void init() {
        LoggingContext.clear();

    }

    public static void elapsedTime(long elapsedTime, TimeUnit timeUnit) {
        MDC.put(LoggingField.ELAPSED_TIME.toString(),
                String.valueOf(TimeUnit.MILLISECONDS.convert(elapsedTime, timeUnit)));
    }

    public static boolean isStopWatchStarted() {
        final String rawStopWatchStart = MDC.get(LoggingField.STOP_WATCH_START.toString());
        if (rawStopWatchStart == null) {
            return false;
        }
        return true;
    }

    public static void stopWatchStart() {
        MDC.put(LoggingField.STOP_WATCH_START.toString(), String.valueOf(System.nanoTime()));
    }

    public static double stopWatchStop() {
        final long stopWatchEnd = System.nanoTime();
        final String rawStopWatchStart = MDC.get(LoggingField.STOP_WATCH_START.toString());

        if (rawStopWatchStart == null)
            throw new StopWatchNotStartedException();

        final Long stopWatchStart = Long.valueOf(rawStopWatchStart);

        MDC.remove(LoggingField.STOP_WATCH_START.toString());

        final double elapsedTimeMillis = (stopWatchEnd - stopWatchStart) / 1000.0 / 1000.0;

        LoggingContext.elapsedTime((long) elapsedTimeMillis, TimeUnit.MILLISECONDS);

        return elapsedTimeMillis;
    }

    public static void put(String key, String value) {
        MDC.put(key, value);
    }

    public static void clear() {
        MDC.clear();
    }

    public static void remove(String key) {
        MDC.remove(key);
    }

}
