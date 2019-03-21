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

package org.onap.aai.edges;

/**
 * Helper class to produce alphabetized keys for EdgeIngestor and EdgeValidator
 */
public class TypeAlphabetizer {
    /**
     * Builds key for edge rules, where nodetypes are alphabetically sorted
     * (ignoring dashes).
     * 
     * @param nodeA - first nodetype
     * @param nodeB - second nodetype
     * @return {alphabetically first nodetype}|{alphabetically second nodetype}
     *         ex: buildAlphabetizedKey("l-interface", "logical-link") -> "l-interface|logical-link"
     *         buildAlphabetizedKey("logical-link", "l-interface") -> "l-interface|logical-link"
     * 
     *         This is alphabetical order to normalize the keys, as sometimes there will be multiple
     *         rules for a pair of node types but the from/to value in the json is flipped for some of them.
     */
    public String buildAlphabetizedKey(String nodeA, String nodeB) {
        if (nodeA == null) {
            nodeA = "";
        }
        if (nodeB == null) {
            nodeB = "";
        }

        // normalize
        String normalizedNodeA = nodeA.replace("-", "");
        String normalizedNodeB = nodeB.replace("-", "");
        int cmp = normalizedNodeA.compareTo(normalizedNodeB);

        StringBuilder sb = new StringBuilder();
        if (cmp <= 0) {
            sb.append(nodeA);
            sb.append("|");
            sb.append(nodeB);
        } else {
            sb.append(nodeB);
            sb.append("|");
            sb.append(nodeA);
        }
        return sb.toString();
    }
}
