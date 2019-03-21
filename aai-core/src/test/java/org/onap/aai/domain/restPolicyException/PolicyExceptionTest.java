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

package org.onap.aai.domain.restPolicyException;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.AAISetup;

public class PolicyExceptionTest extends AAISetup {
    private PolicyException exception;

    @Before
    public void setup() {
        exception = new PolicyException();
    }

    @Test
    public void testGetAdditionalProperty() throws Exception {
        exception.setAdditionalProperty("property1", "value1");
        assertEquals(exception.getAdditionalProperties().get("property1"), "value1");
    }

    @Test
    public void testGetMessageId() throws Exception {
        exception.setMessageId("samplemessage");
        assertEquals(exception.getMessageId(), "samplemessage");
    }

    @Test
    public void testGetText() throws Exception {
        exception.setText("sampletext");
        assertEquals(exception.getText(), "sampletext");
    }

    @Test
    public void testGetVariables() throws Exception {
        List<String> expectedVariables = new ArrayList<>();
        expectedVariables.add("firstvariable");
        expectedVariables.add("secondvariable");
        exception.setVariables(expectedVariables);
        assertEquals(exception.getVariables(), expectedVariables);

    }
}
