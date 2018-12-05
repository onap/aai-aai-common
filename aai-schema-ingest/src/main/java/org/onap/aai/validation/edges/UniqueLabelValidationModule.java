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

import java.util.*;

/**
 * Applies label validation rules
 *
 */
public class UniqueLabelValidationModule {
	
	/**
	 * Validates that the given pair of node types have no duplicate labels in
	 * their edge rules
	 * 
	 * @param String nodeTypePair - of the form "typeA|typeB"
	 * @param List<DocumentContext> ctxs - the edge rule json to pull rules from
	 * 			(ie all files for one version)
	 * @return empty string if no errors, else string error message
	 */
	public String validate(String nodeTypePair, List<DocumentContext> ctxs) {
		String[] types = nodeTypePair.split("\\|");
		EdgeRuleQuery lookup = new Builder(types[0], types[1]).build();
		
		List<Map<String, String>> rules = new ArrayList<>();
		for (DocumentContext ctx : ctxs) {
			rules.addAll(ctx.read("$.rules.[?]", lookup.getFilter()));
		}
		
		Set<String> labelsSeen = new HashSet<>();
		for (Map<String, String> rule : rules) {
			String label = rule.get(EdgeField.LABEL.toString());
			if (labelsSeen.contains(label)) {
				return "Pair " + nodeTypePair + " has multiple rules using the same label: " + label + 
						". Every rule between the same node type pair must have a unique label.";
			} else {
				labelsSeen.add(label);
			}
		}
		return "";
	}
}
