package org.onap.aai.validation.nodes;

import java.util.List;
import java.util.Map.Entry;

import org.onap.aai.setup.ConfigTranslator;
import org.onap.aai.setup.Version;
import org.onap.aai.validation.SchemaErrorStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NodeValidator {
	private ConfigTranslator translator;
	private SchemaErrorStrategy strat;
	private DuplicateNodeDefinitionValidationModule dupChecker;

	@Autowired
	public NodeValidator(ConfigTranslator translator, SchemaErrorStrategy strategy, DuplicateNodeDefinitionValidationModule dupChecker) {
		this.translator = translator;
		this.strat = strategy;
		this.dupChecker = dupChecker;
	}
	
	public boolean validate() {
		
		for(Entry<Version, List<String>> entry : translator.getNodeFiles().entrySet()) {
			String result = dupChecker.findDuplicates(entry.getValue(), entry.getKey());
			if (!"".equals(result)) {
				strat.notifyOnError(result);
			}
		}
		return strat.isOK();
	}
	
	public String getErrorMsg() {
		return strat.getErrorMsg();
	}
}
