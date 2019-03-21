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

import java.util.HashMap;
import java.util.Map;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.junit.Test;

public class EdgeRuleTest {

    @Test
    public void testFlipDirection() {
        Map<String, String> rule = new HashMap<>();
        rule.put("from", "foo");
        rule.put("to", "bar");
        rule.put("label", "links");
        rule.put("contains-other-v", "NONE");
        rule.put("delete-other-v", "NONE");
        rule.put("prevent-delete", "NONE");
        rule.put("multiplicity", "ONE2ONE");
        rule.put("direction", "OUT");
        rule.put("default", "true");
        rule.put("private", "true");

        EdgeRule r = new EdgeRule(rule);

        r.flipDirection();
        assertTrue(Direction.IN.equals(r.getDirection()));
        r.flipDirection();
        assertTrue(Direction.OUT.equals(r.getDirection()));

        rule.put("direction", "BOTH");
        EdgeRule r2 = new EdgeRule(rule);
        r2.flipDirection();
        assertTrue(Direction.BOTH.equals(r2.getDirection()));
    }

}
