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

import ch.qos.logback.classic.pattern.NamedConverter;
import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class EelfClassOfCaller extends NamedConverter {
    protected String getFullyQualifiedName(ILoggingEvent event) {

        StackTraceElement[] cda = event.getCallerData();

        // If using the EELFLogger, it "hides" the calling class because it wraps the logging calls
        // Without this, you'd only ever see "EELF SLF4jWrapper" when using the
        // %C pattern converter
        if (cda != null && cda.length > 2) {
            return cda[2].getClassName();
        } else if (cda != null && cda.length > 0) {
            return cda[0].getClassName();
        } else {
            return CallerData.NA;
        }
    }
}
