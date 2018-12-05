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

package org.onap.aai.validation.edges;

import com.jayway.jsonpath.DocumentContext;
import org.onap.aai.edges.EdgeRuleQuery;
import org.onap.aai.edges.EdgeRuleQuery.Builder;
import org.onap.aai.edges.enums.EdgeField;
import org.onap.aai.edges.enums.EdgeType;

import java.util.*;

/**
 * Validates that in the collection of cousin rules between a given node type pair,
 * there is exactly 1 set default=true. 
 */
public class CousinDefaultingValidationModule {

	/**
	 * Validates that in the collection of cousin rules between a given node type pair,
	 * there is exactly 1 set default=true. 
	 * 
	 * @param String nodeTypePair - pair of A&AI node types in the form "typeA|typeB"
	 * @param List<DocumentContext> ctxs - the ingested json schema to validate
	 * @return empty string if ok, appropriate error message otherwise
	 */
	public String validate(String nodeTypePair, List<DocumentContext> ctxs) {
		String[] types = nodeTypePair.split("\\|");
		EdgeRuleQuery lookup = new Builder(types[0], types[1]).edgeType(EdgeType.COUSIN).build();
		List<Map<String, String>> rules = new ArrayList<>();
		for (DocumentContext ctx : ctxs) {
			rules.addAll(ctx.read("$.rules.[?]", lookup.getFilter()));
		}
		
		if (rules.isEmpty()) {
			return ""; //bc irrelevant check
		}
		
		int defaultCount = 0;
		Set<String> defLabels = new HashSet<>();
		for (Map<String, String> rule : rules) {
			if ("true".equals(rule.get(EdgeField.DEFAULT.toString()))) {
				defaultCount++;
				defLabels.add(rule.get(EdgeField.LABEL.toString()));
			}
		}
		
		StringBuilder errorBase = new StringBuilder().append("Pair ").append(nodeTypePair).append(" must have exactly one cousin rule set as default. ");
		if (defaultCount == 1) {
			return "";
		} else if (defaultCount == 0){
			errorBase.append("None set.");
			return errorBase.toString();
		} else {
			errorBase.append("Multiple set, see labels: ");
			for (String label : defLabels) {
				errorBase.append(label).append(" ");
			}
			return errorBase.toString();
		}
	}
}
