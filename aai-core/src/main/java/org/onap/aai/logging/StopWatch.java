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

import org.onap.aai.logging.LoggingContext.LoggingField;

public final class StopWatch {

    private StopWatch() {
    }

    public static void start() {
        LoggingContext.stopWatchStart();
    }

    public static double stop() {
        return LoggingContext.stopWatchStop();
    }

    public static void conditionalStart() {
        if (LoggingContext.isStopWatchStarted()) {
            return;
        }
        start();
    }

    public static double stopIfStarted() {
        if (LoggingContext.isStopWatchStarted()) {
            return (stop());
        }
        return (0);
    }

    public static void clear() {
        LoggingContext.remove(LoggingField.STOP_WATCH_START.toString());
        LoggingContext.remove(LoggingField.ELAPSED_TIME.toString());
    }
}
