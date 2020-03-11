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

import ch.qos.logback.access.spi.IAccessEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DME2RestFlagTest {

    @Mock
    IAccessEvent accessEvent;

    @Spy
    @InjectMocks
    private DME2RestFlag dme2RestFlag;

    @Before
    public void setup() {
        when(dme2RestFlag.isStarted()).thenReturn(true);
    }
    @Test
    public void dme2Test(){
        String[] contextArray = {"a", "b", "c"};
        String[] routeOfferArray = {"d", "e", "f"};
        String[] versionArray = {"1", "2", "3"};
        when(accessEvent.getRequestParameter("envContext")).thenReturn(contextArray);
        when(accessEvent.getRequestParameter("routeOffer")).thenReturn(routeOfferArray);
        when(accessEvent.getRequestParameter("version")).thenReturn(versionArray);
        assertEquals("DME2", dme2RestFlag.convert(accessEvent));
    }
    @Test
    public void restTest(){
        String[] contextArray = {""};
        String[] routeOfferArray = {""};
        String[] versionArray = {""};
        when(accessEvent.getRequestParameter("envContext")).thenReturn(contextArray);
        assertEquals("REST", dme2RestFlag.convert(accessEvent));
    }
}
