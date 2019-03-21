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

package org.onap.aai.serialization.db;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

public class EdgePropertyMapTest {

    @Test
    public void run() {
        Map<String, String> map = new EdgePropertyMap<>();
        map.put("direction", "OUT");
        map.put("test", "hello");
        map.put("isParent", "${direction}");
        map.put("SVC-INFRA", "!${direction}");

        assertEquals("normal retrieval", "hello", map.get("test"));
        assertEquals("variable retrieval", "OUT", map.get("isParent"));
        assertEquals("negate variable retrieval", "IN", map.get("SVC-INFRA"));
    }
}
