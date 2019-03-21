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

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import org.onap.aai.logging.LoggingContext.LoggingField;

public class EcompElapsedTime extends ClassicConverter {

    private static final String DEFAULT_ELAPSED_TIME_FORMAT = "%d";

    private String ELAPSED_TIME_FORMAT;

    @Override
    public void start() {
        ELAPSED_TIME_FORMAT = getFirstOption();
    }

    @Override
    public String convert(ILoggingEvent event) {
        final long end = event.getTimeStamp();

        if (!event.getMDCPropertyMap().containsKey(LoggingField.START_TIME.toString())) {
            return format(0);
        } else if (event.getMDCPropertyMap().containsKey(LoggingField.ELAPSED_TIME.toString())) {
            return format(Integer
                .parseInt(event.getMDCPropertyMap().get(LoggingField.ELAPSED_TIME.toString())));
        }

        final long start = LogFormatTools
            .toTimestamp(event.getMDCPropertyMap().get(LoggingField.START_TIME.toString()));

        return format(end - start);
    }

    private String format(long elapsedTime) {
        if (ELAPSED_TIME_FORMAT == null) {
            return format(DEFAULT_ELAPSED_TIME_FORMAT, elapsedTime);
        }

        return format(ELAPSED_TIME_FORMAT, elapsedTime);
    }

    private String format(String format, long elapsedTime) {
        return String.format(format, elapsedTime);
    }
}
