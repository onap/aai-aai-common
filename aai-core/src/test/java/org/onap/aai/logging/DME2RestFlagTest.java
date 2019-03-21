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

import ch.qos.logback.access.spi.IAccessEvent;

import org.junit.*;

public class DME2RestFlagTest {

    IAccessEvent mockAccEvent;
    DME2RestFlag _DME2RestFlag;

    String[] temp = new String[4];

    @Before
    public void setUp() throws Exception {

        mockAccEvent = mock(IAccessEvent.class);
        _DME2RestFlag = spy(DME2RestFlag.class);

    }

    private DME2RestFlag getTestObj(final boolean instanceStarted) {
        return new DME2RestFlag() {
            @Override
            public boolean isStarted() {
                return instanceStarted;
            }
        };
    }

    @Test
    public void convertTestAllValid() {
        temp[0] = "temp1";
        temp[1] = "-";
        when(mockAccEvent.getRequestParameter("envContext")).thenReturn(temp);
        when(mockAccEvent.getRequestParameter("routeOffer")).thenReturn(temp);
        when(mockAccEvent.getRequestParameter("version")).thenReturn(temp);
        _DME2RestFlag = getTestObj(true);
        assertEquals(_DME2RestFlag.convert(mockAccEvent), "DME2");
    }

    @Test
    public void convertMissingRouteTest() {
        temp[0] = "";
        temp[1] = "-";
        when(mockAccEvent.getRequestParameter("envContext")).thenReturn(temp);
        when(mockAccEvent.getRequestParameter("routeOffer")).thenReturn(temp);
        when(mockAccEvent.getRequestParameter("version")).thenReturn(temp);
        _DME2RestFlag = getTestObj(true);
        assertEquals(_DME2RestFlag.convert(mockAccEvent), "REST");
    }

    @Test
    public void convertIsStartedFalseTest() {
        _DME2RestFlag = getTestObj(false);
        assertEquals(_DME2RestFlag.convert(mockAccEvent), "INACTIVE_HEADER_CONV");
    }

}
