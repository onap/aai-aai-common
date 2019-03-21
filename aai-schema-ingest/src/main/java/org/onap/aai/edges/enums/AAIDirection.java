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

package org.onap.aai.edges.enums;

import org.apache.tinkerpop.gremlin.structure.Direction;

public enum AAIDirection {
    IN, OUT, BOTH, NONE;

    public AAIDirection opposite() {
        if (this.equals(OUT)) {
            return IN;
        } else if (this.equals(IN)) {
            return OUT;
        } else {
            return BOTH;
        }
    }

    public static AAIDirection getValue(String aaidir) {
        if (OUT.toString().equals(aaidir)) {
            return OUT;
        } else if (IN.toString().equals(aaidir)) {
            return IN;
        } else if (NONE.toString().equals(aaidir)) {
            return NONE;
        } else { // should be BOTH
            return BOTH;
        }
    }

    public static AAIDirection getValue(Direction dir) {
        if (dir == Direction.OUT) {
            return OUT;
        } else if (dir == Direction.IN) {
            return IN;
        } else if (dir == Direction.BOTH) {
            return BOTH;
        } else {
            return NONE;
        }
    }
}
