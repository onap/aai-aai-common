package org.onap.aai.validation.edges;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.onap.aai.edges.EdgeRuleQuery;
import org.onap.aai.edges.EdgeRuleQuery.Builder;
import org.onap.aai.edges.enums.EdgeField;

import com.jayway.jsonpath.DocumentContext;

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
		EdgeRuleQuery lookup = new EdgeRuleQuery.Builder(types[0], types[1]).build();
		
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
