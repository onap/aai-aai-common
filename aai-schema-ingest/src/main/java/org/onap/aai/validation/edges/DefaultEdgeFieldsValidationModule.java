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

/**
 * 
 */

package org.onap.aai.validation.edges;

import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;

import org.onap.aai.edges.enums.EdgeField;

/**
 * Default core A&AI edge field validation
 * All fields in EdgeField enum are required EXCEPT description
 *
 */
public class DefaultEdgeFieldsValidationModule implements EdgeFieldsValidationModule {

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.aai.edges.EdgeFieldsValidator#verifyFields(java.util.Map)
     */
    @Override
    public String verifyFields(Map<String, String> rule) {
        EnumSet<EdgeField> missingFields = EnumSet.complementOf(EnumSet.allOf(EdgeField.class));

        for (EdgeField f : EdgeField.values()) {
            if (!rule.containsKey(f.toString()) && (f != EdgeField.DESCRIPTION)
                && (f != EdgeField.PRIVATE)) { // description is optional
                missingFields.add(f);
            }
        }

        StringBuilder errorMsg = new StringBuilder();
        if (!missingFields.isEmpty()) {
            errorMsg.append("Rule ").append(ruleToString(rule))
                .append(" missing required fields: ");
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
