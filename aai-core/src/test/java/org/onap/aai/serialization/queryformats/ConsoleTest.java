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

package org.onap.aai.serialization.queryformats;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonObject;

import org.junit.jupiter.api.Test;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatVertexException;

public class ConsoleTest {

    Console fM1 = new Console();

    String param = "abcd";

    JsonObject resultVal;

    @Test
    public void classConsoleInstantiateCheck() {
        try {
            Console fm1 = new Console();
            assertNotNull(fm1, "Created class Object is null");
        } catch (Exception e) {
            fail();
        }
    }

    // Below method is expecting to throw an exception

    @Test
    public void formatObjectParamNullCheck() throws AAIFormatVertexException {
        assertThrows(NullPointerException.class, () -> {

            param = null;
            Console fm3 = new Console();
            resultVal = fm3.formatObject(param).get();
        });
    }

    @Test
    public void formatObjectResultCheck() {

        try {
            Console fm2 = new Console();

            resultVal = fm2.formatObject(param).get();
            assertNotNull(resultVal, "The result is null");

            // System.out.println(resultVal);

            JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("result", "abcd");

            assertEquals(jsonObj, resultVal);

        } catch (Exception e) {
            fail();
        }
    }

}
