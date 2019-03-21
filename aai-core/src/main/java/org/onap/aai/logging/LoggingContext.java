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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.logging;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.onap.aai.exceptions.AAIException;
import org.slf4j.MDC;

public class LoggingContext {

    public enum StatusCode {
        COMPLETE, ERROR
    }

    private static final EELFLogger LOGGER =
        EELFManager.getInstance().getLogger(LoggingContext.class);

    private static final String PREVIOUS_CONTEXTS_KEY = "_PREVIOUS_CONTEXTS";

    // Response codes from Logging Guidelines
    public static final String SUCCESS = "0";
    public static final String PERMISSION_ERROR = "100";
    public static final String AVAILABILITY_TIMEOUT_ERROR = "200";
    public static final String DATA_ERROR = "200";
    public static final String SCHEMA_ERROR = "400";
    public static final String BUSINESS_PROCESS_ERROR = "500";
    public static final String UNKNOWN_ERROR = "900";

    public static final Map<String, String> responseMap = new HashMap();

    static {
        responseMap.put(SUCCESS, "Success");
        responseMap.put(UNKNOWN_ERROR, "Unknown error");
    }

    // Specific Log Event Fields
    public static enum LoggingField {
        START_TIME("startTime"), REQUEST_ID("requestId"), SERVICE_INSTANCE_ID(
            "serviceInstanceId"), SERVER_NAME("serverName"), SERVICE_NAME(
                "serviceName"), PARTNER_NAME("partnerName"), STATUS_CODE(
                    "statusCode"), RESPONSE_CODE("responseCode"), RESPONSE_DESCRIPTION(
                        "responseDescription"), INSTANCE_UUID(
                            "instanceUUID"), SEVERITY("severity"), SERVER_IP_ADDRESS(
                                "serverIpAddress"), ELAPSED_TIME("elapsedTime"), SERVER(
                                    "server"), CLIENT_IP_ADDRESS("clientIpAddress"), UNUSED(
                                        "unused"), PROCESS_KEY("processKey"), CUSTOM_FIELD_1(
                                            "customField1"), CUSTOM_FIELD_2(
                                                "customField2"), CUSTOM_FIELD_3(
                                                    "customField3"), CUSTOM_FIELD_4("customField4"),

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
        LoggingContext.startTime();
        LoggingContext.server();
        LoggingContext.serverIpAddress();
    }

    public static void startTime() {
        MDC.put(LoggingField.START_TIME.toString(), LogFormatTools.getCurrentDateTime());
    }

    public static UUID requestId() {
        final String sUuid = MDC.get(LoggingField.REQUEST_ID.toString());

        if (sUuid == null)
            return null;

        return UUID.fromString(sUuid);
    }

    public static void requestId(UUID requestId) {
        MDC.put(LoggingField.REQUEST_ID.toString(), requestId.toString());
    }

    public static void requestId(String requestId) {
        try {
            if (requestId.contains(":")) {
                String[] uuidParts = requestId.split(":");
                requestId = uuidParts[0];
            }
            MDC.put(LoggingField.REQUEST_ID.toString(), UUID.fromString(requestId).toString());
        } catch (IllegalArgumentException e) {
            final UUID generatedRequestUuid = UUID.randomUUID();
            MDC.put(LoggingField.REQUEST_ID.toString(), generatedRequestUuid.toString());
            LoggingContext.save();
            // set response code to 0 since we don't know what the outcome of this request is yet
            String responseCode = LoggingContext.DATA_ERROR;
            LoggingContext.responseCode(responseCode);
            LoggingContext.responseDescription(
                "Unable to use UUID " + requestId + " (Not formatted properly) ");
            LoggingContext.statusCode(StatusCode.ERROR);

            LOGGER.warn("Using generated UUID=" + generatedRequestUuid);
            LoggingContext.restore();

        }
    }

    public static void serviceInstanceId(String serviceInstanceId) {
        MDC.put(LoggingField.SERVICE_INSTANCE_ID.toString(), serviceInstanceId);
    }

    public static void serverName(String serverName) {
        MDC.put(LoggingField.SERVER_NAME.toString(), serverName);
    }

    public static void serviceName(String serviceName) {
        MDC.put(LoggingField.SERVICE_NAME.toString(), serviceName);
    }

    public static void partnerName(String partnerName) {
        MDC.put(LoggingField.PARTNER_NAME.toString(), partnerName);
    }

    public static void statusCode(StatusCode statusCode) {
        MDC.put(LoggingField.STATUS_CODE.toString(), statusCode.toString());
    }

    public static String responseCode() {
        return (String) MDC.get(LoggingField.RESPONSE_CODE.toString());
    }

    public static void responseCode(String responseCode) {
        MDC.put(LoggingField.RESPONSE_CODE.toString(), responseCode);
    }

    public static void responseDescription(String responseDescription) {
        MDC.put(LoggingField.RESPONSE_DESCRIPTION.toString(), responseDescription);
    }

    public static Object instanceUuid() {
        return UUID.fromString(MDC.get(LoggingField.INSTANCE_UUID.toString()));
    }

    public static void instanceUuid(UUID instanceUuid) {
        MDC.put(LoggingField.INSTANCE_UUID.toString(), instanceUuid.toString());
    }

    public static void severity(int severity) {
        MDC.put(LoggingField.SEVERITY.toString(), String.valueOf(severity));
    }

    public static void successStatusFields() {
        responseCode(SUCCESS);
        statusCode(StatusCode.COMPLETE);
        responseDescription("Success");
    }

    private static void serverIpAddress() {
        try {
            MDC.put(LoggingField.SERVER_IP_ADDRESS.toString(),
                InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            LOGGER.warn(
                "Unable to resolve server IP address - will not be displayed in logged events");
        }
    }

    public static void elapsedTime(long elapsedTime, TimeUnit timeUnit) {
        MDC.put(LoggingField.ELAPSED_TIME.toString(),
            String.valueOf(TimeUnit.MILLISECONDS.convert(elapsedTime, timeUnit)));
    }

    private static void server() {
        try {
            MDC.put(LoggingField.SERVER.toString(),
                InetAddress.getLocalHost().getCanonicalHostName());
        } catch (UnknownHostException e) {
            LOGGER.warn(
                "Unable to resolve server IP address - hostname will not be displayed in logged events");
        }
    }

    public static void clientIpAddress(InetAddress clientIpAddress) {
        MDC.put(LoggingField.CLIENT_IP_ADDRESS.toString(), clientIpAddress.getHostAddress());
    }

    public static void clientIpAddress(String clientIpAddress) {
        try {
            MDC.put(LoggingField.CLIENT_IP_ADDRESS.toString(),
                InetAddress.getByName(clientIpAddress).getHostAddress());
        } catch (UnknownHostException e) {
            // Ignore, will not be thrown since InetAddress.getByName(String) only
            // checks the validity of the passed in string
        }
    }

    public static void unused(String unused) {
        LOGGER.warn(
            "Using field '" + LoggingField.UNUSED + "' (seems like this should go unused...)");
        MDC.put(LoggingField.UNUSED.toString(), unused);
    }

    public static void processKey(String processKey) {
        MDC.put(LoggingField.PROCESS_KEY.toString(), processKey);
    }

    public static String customField1() {
        return MDC.get(LoggingField.CUSTOM_FIELD_1.toString());
    }

    public static void customField1(String customField1) {
        MDC.put(LoggingField.CUSTOM_FIELD_1.toString(), customField1);
    }

    public static void customField2(String customField2) {
        MDC.put(LoggingField.CUSTOM_FIELD_2.toString(), customField2);
    }

    public static void customField3(String customField3) {
        MDC.put(LoggingField.CUSTOM_FIELD_3.toString(), customField3);
    }

    public static void customField4(String customField4) {
        MDC.put(LoggingField.CUSTOM_FIELD_4.toString(), customField4);
    }

    public static void component(String component) {
        MDC.put(LoggingField.COMPONENT.toString(), component);
    }

    public static void targetEntity(String targetEntity) {
        MDC.put(LoggingField.TARGET_ENTITY.toString(), targetEntity);
    }

    public static void targetServiceName(String targetServiceName) {
        MDC.put(LoggingField.TARGET_SERVICE_NAME.toString(), targetServiceName);
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

    public static void save() {
        final JSONObject context = new JSONObject();

        for (LoggingField field : LoggingField.values()) {
            if (field == LoggingField.ELAPSED_TIME)
                continue;

            try {
                context.put(field.toString(), MDC.get(field.toString()));
            } catch (JSONException e) {
                // Ignore - only occurs when the key is null (which can't happen)
                // or the value is invalid (everything is converted to a string
                // before it get put() to the MDC)
            }
        }

        final String rawJsonArray = MDC.get(PREVIOUS_CONTEXTS_KEY);

        if (rawJsonArray == null) {
            final JSONArray stack = new JSONArray().put(context);

            MDC.put(PREVIOUS_CONTEXTS_KEY, stack.toString());
        } else {
            try {
                final JSONArray stack = new JSONArray(rawJsonArray).put(context);

                MDC.put(PREVIOUS_CONTEXTS_KEY, stack.toString());
            } catch (JSONException e) {
                // Ignore
            }
        }
    }

    public static void restore() {

        final String rawPreviousContexts = MDC.get(PREVIOUS_CONTEXTS_KEY);

        if (rawPreviousContexts == null) {
            throw new LoggingContextNotExistsException();
        }

        try {
            final JSONArray previousContexts = new JSONArray(rawPreviousContexts);
            final JSONObject previousContext =
                previousContexts.getJSONObject(previousContexts.length() - 1);

            @SuppressWarnings("unchecked")
            final Iterator<String> keys = previousContext.keys();
            boolean foundElapsedTime = false;
            while (keys.hasNext()) {
                final String key = keys.next();
                if (LoggingField.ELAPSED_TIME.toString().equals(key)) {
                    foundElapsedTime = true;
                }
                try {
                    MDC.put(key, previousContext.getString(key));
                } catch (JSONException e) {
                    // Ignore, only occurs when the key is null (cannot happen)
                    // or the value is invalid (they are all strings)
                }
            }
            if (!foundElapsedTime) {
                MDC.remove(LoggingField.ELAPSED_TIME.toString());
            }
            MDC.put(PREVIOUS_CONTEXTS_KEY, removeLast(previousContexts).toString());
        } catch (JSONException e) {
            // Ignore, the previousContext is serialized from a JSONObject
        }
    }

    public static void restoreIfPossible() {
        try {
            restore();
        } catch (LoggingContextNotExistsException e) {
            // Ignore
        }
    }

    /**
     * AJSC declares an ancient version of org.json:json in one of the parent POMs of this project.
     * I tried to update our version of that library in our POM, but it's ignored because of the way
     * AJSC has organized their <dependencies>. Had they put it into the <dependencyManagement>
     * section,
     * this method would not be necessary.
     */
    private static JSONArray removeLast(JSONArray previousContexts) {
        final JSONArray result = new JSONArray();

        for (int i = 0; i < previousContexts.length() - 1; i++) {
            try {
                result.put(previousContexts.getJSONObject(i));
            } catch (JSONException e) {
                // Ignore - not possible
            }
        }

        return result;
    }

    public static Map<String, String> getCopy() {
        final Map<String, String> copy = new HashMap<String, String>();

        for (LoggingField field : LoggingField.values()) {
            final String value = MDC.get(field.toString());

            if (value != null)
                copy.put(field.toString(), value);
        }

        return copy;
    }
}
