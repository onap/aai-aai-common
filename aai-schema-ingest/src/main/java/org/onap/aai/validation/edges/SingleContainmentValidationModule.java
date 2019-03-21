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

package org.onap.aai.validation.edges;

import com.jayway.jsonpath.DocumentContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.onap.aai.edges.EdgeRuleQuery;
import org.onap.aai.edges.enums.EdgeType;

/**
 * Validates that the given node type pair has at most one containment relationship
 * in their edge rules.
 * 
 */
public class SingleContainmentValidationModule {

    /**
     * Validates that the given node type pair has at most one containment relationship
     * in their edge rules.
     * 
     * @param String nodeTypePair - pair of A&AI node types in the form "typeA|typeB"
     * @param List<DocumentContext> ctxs - the ingested json to validate
     * @return empty string if passes, else appropriate error message
     */
    public String validate(String nodeTypePair, List<DocumentContext> ctxs) {
        String[] types = nodeTypePair.split("\\|");
        EdgeRuleQuery lookup =
            new EdgeRuleQuery.Builder(types[0], types[1]).edgeType(EdgeType.TREE).build();
        List<Map<String, String>> rules = new ArrayList<>();
        for (DocumentContext ctx : ctxs) {
            rules.addAll(ctx.read("$.rules.[?]", lookup.getFilter()));
        }

        if (rules.isEmpty() || rules.size() == 1) {
            return "";
        } else { // had more than one containment relationship for the pair
            return "Pair " + nodeTypePair + " has multiple containment rules. Only one allowed.";
        }
    }
}
