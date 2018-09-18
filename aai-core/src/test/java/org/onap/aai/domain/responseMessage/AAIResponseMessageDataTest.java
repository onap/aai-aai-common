/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright © 2018 IBM.
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
package org.onap.aai.domain.responseMessage;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.AAISetup;

public class AAIResponseMessageDataTest extends AAISetup{
    private AAIResponseMessageData respMsg;
    
    @Before
    public void setup() {
        respMsg = new AAIResponseMessageData();
    }

    @Test
    public void testAAIResponseMessageDatum() throws Exception {
        assertEquals(respMsg.getAAIResponseMessageDatum(), new ArrayList<>());
    }
    
    @Test
    public void testGetAny() throws Exception {
        assertEquals(respMsg.getAny(), new ArrayList<Object>());
    }
   
}
