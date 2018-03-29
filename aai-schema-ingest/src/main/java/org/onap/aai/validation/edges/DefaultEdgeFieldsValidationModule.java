/**
 * 
 */
package org.onap.aai.validation.edges;

import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.onap.aai.edges.enums.EdgeField;

/**
 * Default core A&AI edge field validation
 * All fields in EdgeField enum are required EXCEPT description
 *
 */
public class DefaultEdgeFieldsValidationModule implements EdgeFieldsValidationModule {

	/* (non-Javadoc)
	 * @see org.onap.aai.edges.EdgeFieldsValidator#verifyFields(java.util.Map)
	 */
	@Override
	public String verifyFields(Map<String, String> rule) {
		EnumSet<EdgeField> missingFields = EnumSet.complementOf(EnumSet.allOf(EdgeField.class));
		
		for (EdgeField f : EdgeField.values()) {
			if (!rule.containsKey(f.toString()) && (f != EdgeField.DESCRIPTION)) { //description is optional
				missingFields.add(f);
			}
		}
		
		StringBuilder errorMsg = new StringBuilder();
		if (!missingFields.isEmpty()) {
			errorMsg.append("Rule ").append(ruleToString(rule)).append(" missing required fields: ");
			for (EdgeField mf : missingFields) {
				errorMsg.append(mf.toString()).append(" ");
			}
		}
		
		return errorMsg.toString();
	}
	
	private String ruleToString(Map<String, String> rule) {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> fields : rule.entrySet()) {
			sb.append(fields.getKey()).append(":").append(fields.getValue()).append(" ");
		}
		
		return sb.toString();
	}

}
