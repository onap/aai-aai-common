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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StopWatchNotStartedExceptionTest {


    @Test
    public void stopWatchNotStartedExceptionTest() {
        StopWatchNotStartedException e = new StopWatchNotStartedException();
        assertTrue(e instanceof RuntimeException);
    }
    @Test
    public void stopWatchNotStartedExceptionWithMessageTest() {
        StopWatchNotStartedException e = new StopWatchNotStartedException("Error Message");
        assertTrue(e instanceof RuntimeException);
        assertEquals("Error Message", e.getMessage());
    }
    @Test
    public void stopWatchNotStartedExceptionWithCauseTest() {
        StopWatchNotStartedException e = new StopWatchNotStartedException(new ArithmeticException());
        assertTrue(e instanceof RuntimeException);
        assertTrue(e.getCause() instanceof ArithmeticException);
    }
    @Test
    public void stopWatchNotStartedExceptionWithMsgCauseTest() {
        StopWatchNotStartedException e = new StopWatchNotStartedException("Error Message", new ArithmeticException());
        assertTrue(e instanceof RuntimeException);
        assertTrue(e.getCause() instanceof ArithmeticException);
        assertEquals("Error Message", e.getMessage());
    }

}
