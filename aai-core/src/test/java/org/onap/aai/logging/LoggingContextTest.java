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

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class LoggingContextTest {

    private static final int MAX_STORED_CONTEXTS = 100;

    @Test
    public void testStopWatch() {
        try {
            LoggingContext.stopWatchStop();
            throw new AssertionError("No exception thrown when LoggingContext.stopWatchStop() called without a prior LoggingContext.stopWatchStart()");
        } catch (StopWatchNotStartedException e) {
            //Expected
        }

        LoggingContext.stopWatchStart();

        assertTrue(LoggingContext.stopWatchStop() >= 0);

        try {
            LoggingContext.stopWatchStop();
            throw new AssertionError("No exception thrown when LoggingContext.stopWatchStop() twice in succession");
        } catch (StopWatchNotStartedException e) {
            //Expected
        }
    }

    @Test
    public void testRequestId() { //AKA Transaction ID
        final String sUuid = "57d51eaa-edc6-4f50-a69d-f2d4d2445120";

        LoggingContext.requestId(sUuid);

        assertEquals(LoggingContext.requestId(), UUID.fromString(sUuid));

        final UUID uuid = UUID.randomUUID();

        LoggingContext.requestId(uuid);

        assertEquals(LoggingContext.requestId(), uuid);

        LoggingContext.requestId("foo"); //Illegal - this will result in a new, randomly
                                        //generated UUID as per the logging spec

        assertNotNull(LoggingContext.requestId()); //Make sure ANY UUID was assigned
        assertNotEquals(LoggingContext.requestId(), uuid); //Make sure it actually changed from the last
                                                            //known valid UUID
    }

    @Test
    public void testClear() {
        LoggingContext.init();
        LoggingContext.clear();

        assertEquals(Collections.emptyMap(), LoggingContext.getCopy());
    }

    @Test
    public void testSaveRestore() {

        final Deque<Map<String, String>> contexts  = new LinkedList<Map<String, String>> ();

        LoggingContext.init();

        for (int i = 0; i < MAX_STORED_CONTEXTS; i++) {
            LoggingContext.customField1(String.valueOf(i));
    
            assertEquals(LoggingContext.customField1(), String.valueOf(i));

            LoggingContext.save();

            contexts.push(LoggingContext.getCopy());
        }

        while (contexts.peek() != null) {
            LoggingContext.restore();

            assertEquals(LoggingContext.getCopy(), contexts.pop());
        }
    }
}
