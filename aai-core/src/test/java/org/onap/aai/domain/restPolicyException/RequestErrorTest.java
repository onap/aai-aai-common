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
 * http://www.apache.org/licenses/LICENSE-2.0
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

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.AAISetup;

public class RequestErrorTest extends AAISetup {
    private RequestError reqError;

    @Before
    public void setup() {
        reqError = new RequestError();
    }

    @Test
    public void testGetAdditionalProperty() throws Exception {
        reqError.setAdditionalProperty("property1", "value1");
        assertEquals(reqError.getAdditionalProperties().get("property1"), "value1");
    }

    @Test
    public void testGetPolicyException() throws Exception {
        PolicyException exception = new PolicyException();
        exception.setMessageId("123");
        exception.setText("sampletext");
        reqError.setPolicyException(exception);
        assertEquals(reqError.getPolicyException().getMessageId(), "123");
        assertEquals(reqError.getPolicyException().getText(), "sampletext");
    }
}
