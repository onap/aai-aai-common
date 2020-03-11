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

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class StopWatchTest {

    @After
    public void cleanup() {
        MDC.clear();
    }
    @Test
    public void elapsedTimeTest() throws InterruptedException {
        StopWatch.start();
        sleep(1010);
        StopWatch.stop();
        assertTrue(Long.parseLong(MDC.get(LoggingContext.LoggingField.ELAPSED_TIME.toString())) >= 1000L);
    }
    @Test
    public void elapsedTimeConditionalTest() throws InterruptedException {
        StopWatch.conditionalStart();
        sleep(1010);
        StopWatch.stopIfStarted();
        String elapsedTimeStr = MDC.get(LoggingContext.LoggingField.ELAPSED_TIME.toString());
        long elapsedTime = Long.parseLong(elapsedTimeStr);
        assertTrue(elapsedTime >= 1000L);
    }
    @Test
    public void clearTest() throws InterruptedException {
        StopWatch.start();
        sleep(1010);
        StopWatch.stop();
        assertNotNull( MDC.get(LoggingContext.LoggingField.ELAPSED_TIME.toString()));

        StopWatch.clear();
        assertNull(MDC.get(LoggingContext.LoggingField.STOP_WATCH_START.toString()));
        assertNull(MDC.get(LoggingContext.LoggingField.ELAPSED_TIME.toString()));

    }

    @Test
    public void stopTest() throws InterruptedException {
        StopWatch.start();
        sleep(1010);
        StopWatch.stop();

        String elapsedTimeStr = MDC.get(LoggingContext.LoggingField.ELAPSED_TIME.toString());
        long elapsedTime1 = Long.parseLong(elapsedTimeStr);

        StopWatch.stopIfStarted();
        elapsedTimeStr = MDC.get(LoggingContext.LoggingField.ELAPSED_TIME.toString());
        long elapsedTime2 = Long.parseLong(elapsedTimeStr);
        assertTrue(elapsedTime1 == elapsedTime2);
    }

    @Test
    public void startTest() throws InterruptedException {
        StopWatch.start();
        sleep(1010);
        StopWatch.conditionalStart();
        StopWatch.stop();

        String elapsedTimeStr = MDC.get(LoggingContext.LoggingField.ELAPSED_TIME.toString());
        long elapsedTime = Long.parseLong(elapsedTimeStr);

        assertTrue(elapsedTime >= 1000L);
    }

}
