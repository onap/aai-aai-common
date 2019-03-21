/** 
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-18 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.validation;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onap.aai.validation.AAISchemaValidationException;
import org.onap.aai.validation.FailFastStrategy;

public class FailFastStrategyTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void test() {
        FailFastStrategy strat = new FailFastStrategy();

        // simulate no issues found
        assertTrue(strat.isOK());
        assertTrue("No errors found.".equals(strat.getErrorMsg()));

        // simulate an issue found
        String testError = "hi i'm a problem";
        thrown.expect(AAISchemaValidationException.class);
        thrown.expectMessage(testError);
        strat.notifyOnError(testError);
    }

}
