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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.edges;

import static org.junit.Assert.*;

import org.junit.Test;

public class TypeAlphabetizerTest {

    @Test
    public void test() {
        TypeAlphabetizer alpher = new TypeAlphabetizer();
        assertTrue("aaa|bbb".equals(alpher.buildAlphabetizedKey("aaa", "bbb")));
        assertTrue("l-interface|logical-link"
            .equals(alpher.buildAlphabetizedKey("l-interface", "logical-link")));
        assertTrue("l-interface|logical-link"
            .equals(alpher.buildAlphabetizedKey("logical-link", "l-interface")));
        assertTrue("|foo".equals(alpher.buildAlphabetizedKey(null, "foo")));
        assertTrue("|foo".equals(alpher.buildAlphabetizedKey("foo", null)));
        assertTrue("|".equals(alpher.buildAlphabetizedKey(null, null)));
    }

}
