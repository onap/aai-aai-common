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
import org.onap.aai.edges.JsonIngestor;
import org.onap.aai.edges.TypeAlphabetizer;
import org.onap.aai.edges.enums.EdgeField;

import org.onap.aai.setup.ConfigTranslator;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.validation.SchemaErrorStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Runs all validations against the ingested schema
 */
@Component
public class EdgeRuleValidator {
	private Map<SchemaVersion, List<DocumentContext>> versionJsonFilesMap;
	private final SchemaErrorStrategy strat;
	protected final EdgeFieldsValidationModule fieldValidator;
	protected final UniqueLabelValidationModule labelValidator;
	protected final SingleContainmentValidationModule containsValidator;
	protected final CousinDefaultingValidationModule defaultsValidator;
	protected final NodeTypesValidationModule typeValidator;

    @Autowired
    public EdgeRuleValidator(ConfigTranslator config, SchemaErrorStrategy strat,
                             EdgeFieldsValidationModule fieldValidator, UniqueLabelValidationModule labelValidator,
                             SingleContainmentValidationModule containsValidator, CousinDefaultingValidationModule defaultsValidator,
                             NodeTypesValidationModule typeValidator) {
        //TODO - Need to change this to use files/schemaservice
        this.versionJsonFilesMap = new JsonIngestor().ingest(config.getEdgeFiles());
        this.strat = strat;
        this.fieldValidator = fieldValidator;
        this.labelValidator = labelValidator;
        this.containsValidator = containsValidator;
        this.defaultsValidator = defaultsValidator;
        this.typeValidator = typeValidator;
    }

	public boolean validate() {
		
		for (Map.Entry<SchemaVersion, List<DocumentContext>> verEntry : versionJsonFilesMap.entrySet()) {
			SchemaVersion v = verEntry.getKey();
			List<DocumentContext> ctxs = verEntry.getValue();
			List<Map<String, String>> rules = collectRules(ctxs);
			Set<String> nodeTypePairs = new HashSet<>();
			TypeAlphabetizer alpher = new TypeAlphabetizer();
			
			for (Map<String, String> rule : rules) {
				handleResult(fieldValidator.verifyFields(rule));
				nodeTypePairs.add(alpher.buildAlphabetizedKey(rule.get(EdgeField.FROM.toString()), rule.get(EdgeField.TO.toString())));
			}
			
			for (String nodeTypePair : nodeTypePairs) {
				handleResult(labelValidator.validate(nodeTypePair, ctxs));
				handleResult(containsValidator.validate(nodeTypePair, ctxs)); 
				handleResult(defaultsValidator.validate(nodeTypePair, ctxs));
			}
			
			handleResult(typeValidator.validate(nodeTypePairs, v));
		}

		return strat.isOK();
	}
	
	private List<Map<String, String>> collectRules(List<DocumentContext> ctxs) {
		List<Map<String, String>> rules = new ArrayList<>();
		
		for (DocumentContext ctx : ctxs) {
			rules.addAll(ctx.read("$.rules.*"));
		}
		
		return rules;
	}
	
	private void handleResult(String result) {
		if (!"".equals(result)) {
			strat.notifyOnError(result);
		}
	}
	
	public String getErrorMsg() {
		return strat.getErrorMsg();
	}
}
