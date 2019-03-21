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

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.util.AAIConfig;
import org.onap.aai.util.AAIConstants;

public class LogFormatTools {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final DateTimeFormatter DTF =
        DateTimeFormatter.ofPattern(DATE_FORMAT).withZone(ZoneOffset.UTC);

    public static String getCurrentDateTime() {
        return DTF.format(ZonedDateTime.now());
    }

    public static String toDate(long timestamp) {
        return DTF.format(Instant.ofEpochMilli(timestamp));
    }

    public static long toTimestamp(String date) {
        return ZonedDateTime.parse(date, DTF).toInstant().toEpochMilli();
    }

    /**
     * Gets the stack top.
     *
     * @param e the e
     * @return the stack top
     * @throws NumberFormatException the number format exception
     * @throws AAIException the AAI exception
     */
    public static String getStackTop(Throwable e) {
        // StringBuilder is more efficient than StringBuffer and should only
        // StringBuffer is only supposed to be used if multiple threads are modifying
        // the same object and since this object is created locally not necessary
        StringBuilder stackMessage = new StringBuilder();
        int maxStackTraceEntries = 10;
        try {
            maxStackTraceEntries =
                Integer.valueOf(AAIConfig.get(AAIConstants.LOGGING_MAX_STACK_TRACE_ENTRIES));
        } catch (AAIException a) {
            // ignore, use default
        } catch (NumberFormatException n) {
            // ignore, use default
        }
        if (e != null) {
            Throwable rootCause = ExceptionUtils.getRootCause(e);
            if (rootCause != null) {
                stackMessage.append("root cause=" + ExceptionUtils.getRootCause(e));
                StackTraceElement[] elements = rootCause.getStackTrace();
                int i = 0;
                for (StackTraceElement element : elements) {
                    if (i < maxStackTraceEntries) {
                        stackMessage.append(" ClassName- ");
                        stackMessage.append(element.getClassName());
                        stackMessage.append(" :LineNumber- ");
                        stackMessage.append(element.getLineNumber());
                        stackMessage.append(" :MethodName- ");
                        stackMessage.append(element.getMethodName());
                    }
                    i++;
                }
            } else if (e.getCause() != null) {
                stackMessage.append("cause=" + e.getCause());
                StackTraceElement[] elements = e.getCause().getStackTrace();
                int i = 0;
                for (StackTraceElement element : elements) {
                    if (i < maxStackTraceEntries) {
                        stackMessage.append(" ClassName- ");
                        stackMessage.append(element.getClassName());
                        stackMessage.append(" :LineNumber- ");
                        stackMessage.append(element.getLineNumber());
                        stackMessage.append(" :MethodName- ");
                        stackMessage.append(element.getMethodName());
                    }
                    i++;
                }
            } else if (e.getStackTrace() != null) {
                stackMessage.append("ex=" + e.toString());
                StackTraceElement[] elements = e.getStackTrace();
                int i = 0;
                for (StackTraceElement element : elements) {
                    if (i < maxStackTraceEntries) {
                        stackMessage.append(" ClassName- ");
                        stackMessage.append(element.getClassName());
                        stackMessage.append(" :LineNumber- ");
                        stackMessage.append(element.getLineNumber());
                        stackMessage.append(" :MethodName- ");
                        stackMessage.append(element.getMethodName());
                    }
                    i++;
                }
            }
        }
        return stackMessage.toString();
    }

}
