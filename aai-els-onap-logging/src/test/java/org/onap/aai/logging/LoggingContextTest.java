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

import org.junit.After;
import org.junit.Test;
import org.slf4j.MDC;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class LoggingContextTest {

    @After
    public void cleanup(){
        MDC.clear();
    }
    @Test
    public void elapsedTimeTest() {
        LoggingContext.elapsedTime(300, TimeUnit.MILLISECONDS);
        assertEquals(MDC.get(LoggingContext.LoggingField.ELAPSED_TIME.toString()), "300");
        LoggingContext.init();
        assertTrue(MDC.get(LoggingContext.LoggingField.ELAPSED_TIME.toString()) == null);
    }
    @Test
    public void stopWatchTest() {
        LoggingContext.init();
        assertFalse(LoggingContext.isStopWatchStarted());

        LoggingContext.stopWatchStart();
        assertTrue(LoggingContext.isStopWatchStarted());

        double elapsedTime = LoggingContext.stopWatchStop();
        assertFalse(LoggingContext.isStopWatchStarted());
        assertTrue(elapsedTime > 0);
    }
    @Test
    public void putClearTest() {
        String testServiceName = "TEST-SVC-NAME";
        LoggingContext.put(LoggingContext.LoggingField.SERVICE_NAME.toString(), testServiceName);
        assertEquals(testServiceName, MDC.get(LoggingContext.LoggingField.SERVICE_NAME.toString()));

        LoggingContext.clear();
        assertTrue(MDC.get(LoggingContext.LoggingField.SERVICE_NAME.toString()) == null);

    }

    @Test
    public void removeTest() {
        String testServiceName = "TEST-SVC-NAME";
        LoggingContext.put(LoggingContext.LoggingField.SERVICE_NAME.toString(), testServiceName);
        assertEquals(testServiceName, MDC.get(LoggingContext.LoggingField.SERVICE_NAME.toString()));

        LoggingContext.remove(LoggingContext.LoggingField.SERVICE_NAME.toString());
        assertTrue(MDC.get(LoggingContext.LoggingField.SERVICE_NAME.toString()) == null);

    }

}
