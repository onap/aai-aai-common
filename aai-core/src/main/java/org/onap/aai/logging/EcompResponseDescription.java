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

public class EcompResponseDescription extends ClassicConverter {
    public final static String DefaultDescription = "Unknown response/error description";

    @Override
    public String convert(ILoggingEvent event) {

        if (!event.getMDCPropertyMap().containsKey(LoggingField.RESPONSE_DESCRIPTION.toString())) {
            return (DefaultDescription);
        }
        // Replace pipes and new lines
        String currentDesc =
            event.getMDCPropertyMap().get(LoggingField.RESPONSE_DESCRIPTION.toString());
        if ((currentDesc == null) || (currentDesc.length() == 0)) {
            return (DefaultDescription);
        }
        currentDesc = currentDesc.replaceAll("|", "!");
        currentDesc = currentDesc.replaceAll("[\\r\\n]+", "^");
        return event.getMDCPropertyMap().get(LoggingField.RESPONSE_DESCRIPTION.toString());
    }
}
