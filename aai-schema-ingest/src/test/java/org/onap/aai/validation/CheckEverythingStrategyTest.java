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

import org.junit.Test;
import org.onap.aai.validation.CheckEverythingStrategy;

public class CheckEverythingStrategyTest {

    @Test
    public void test() {
        CheckEverythingStrategy strat = new CheckEverythingStrategy();
        //no issues so nothing notified, should be fine
        assertTrue(strat.isOK());
        assertTrue("No errors found.".equals(strat.getErrorMsg()));
        
        //simulate post one error
        String testError1 = "oh noes a problem with the gooble-gobble edge rule!";
        strat.notifyOnError(testError1);
        assertFalse(strat.isOK());
        assertTrue(testError1.equals(strat.getErrorMsg()));
        
        //simulate multiple found
        String testError2 = "error 2";
        String testError3 = "duplicate labels not everything is a fork";
        strat.notifyOnError(testError2);
        strat.notifyOnError(testError3);
        assertFalse(strat.isOK());
        System.out.println(strat.getErrorMsg());
        assertTrue(strat.getErrorMsg().contains(testError1));
        assertTrue(strat.getErrorMsg().contains(testError2));
        assertTrue(strat.getErrorMsg().contains(testError3));
    }

}
