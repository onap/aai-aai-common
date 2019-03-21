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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.spi.ILoggingEvent;

import org.junit.*;

public class EelfClassOfCallerTest {

    EelfClassOfCaller _eelfClassOfCaller;
    ILoggingEvent mockEvent;
    StackTraceElement[] cdafive = new StackTraceElement[5];
    StackTraceElement[] cdaone = new StackTraceElement[1];
    StackTraceElement[] cdazero = new StackTraceElement[0];

    @Before
    public void setUp() throws Exception {

        mockEvent = mock(ILoggingEvent.class);
        _eelfClassOfCaller = spy(EelfClassOfCaller.class);

    }

    @Test
    public void getFullyQualifiedNameCDALENFiveTest() {
        StackTraceElement temp =
            new StackTraceElement("classname_five", "methodname", "filename", 4);
        cdafive[2] = temp;
        when(mockEvent.getCallerData()).thenReturn(cdafive);
        assertEquals(_eelfClassOfCaller.getFullyQualifiedName(mockEvent), "classname_five");

    }

    @Test
    public void getFullyQualifiedNameCDALenOneTest() {
        StackTraceElement temp =
            new StackTraceElement("classname_one", "methodname", "filename", 4);
        cdaone[0] = temp;
        when(mockEvent.getCallerData()).thenReturn(cdaone);
        assertEquals(_eelfClassOfCaller.getFullyQualifiedName(mockEvent), "classname_one");

    }

    @Test
    public void getFullyQualifiedNameCDALenZeroTest() {
        when(mockEvent.getCallerData()).thenReturn(cdazero);
        assertEquals(_eelfClassOfCaller.getFullyQualifiedName(mockEvent), "?");

    }

}
