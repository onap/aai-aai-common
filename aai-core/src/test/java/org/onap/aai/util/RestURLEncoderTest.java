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

package org.onap.aai.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

public class RestURLEncoderTest {

    @Test
    public void testEncodeURL() throws Exception {

        String url = "nodeKeyTest&more-string strings";
        String encodedUrl = "nodeKeyTest%26more-string%20strings";

        String res = RestURLEncoder.encodeURL(url);
        assertEquals(encodedUrl, res);
    }

    @Test
    public void testEncodeURL_plusSign() throws Exception {

        String url = "nodeKeyTest+more+string";
        String encodedUrl = "nodeKeyTest%2Bmore%2Bstring";

        String res = RestURLEncoder.encodeURL(url);
        assertEquals(encodedUrl, res);
    }

    @Test
    public void testEncodeURL_noException() throws Exception {
        // no exception expected, none thrown: passes.
        try {
            String encodeResult = RestURLEncoder.encodeURL("");

            assertNotNull("Result is not null", encodeResult);
        } catch (Exception e) {
            fail();
        }
    }
}
