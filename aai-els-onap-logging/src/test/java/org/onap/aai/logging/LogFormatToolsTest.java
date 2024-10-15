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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.onap.aai.exceptions.AAIException;

public class LogFormatToolsTest {

    @Test
    public void testLogFormatStackTrace() {
        AAIException aaiException = new AAIException("AAI_4009", new ArithmeticException());

        String stackTrace = LogFormatTools.getStackTop(aaiException);
        assertTrue(stackTrace.startsWith("root cause=java.lang.ArithmeticException"));
        assertFalse(stackTrace.contains("\\n"));
    }
}
