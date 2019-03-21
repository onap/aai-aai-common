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

package org.onap.aai.concurrent;

import static org.junit.Assert.assertTrue;

import java.lang.Object;

import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.concurrent.AaiCallable;
import org.slf4j.MDC;

public class AaiCallableTest extends AAISetup {
    @Test
    public void testAaiCallable() {
        MDC.put("test_name", "test_value");

        AaiCallable<Object> task = new AaiCallable<Object>() {
            @Override
            public Object process() {
                String mdcValue = MDC.get("test_name");
                assertTrue("MDC value retained", "test_value".equals(mdcValue));
                return (new Object());
            }
        };
        try {
            task.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
