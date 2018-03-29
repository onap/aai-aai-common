package org.onap.aai.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Verifies that the schema config covers
 * all required versions
 */
@Component
public class VersionValidator {
	private SchemaErrorStrategy strat;
	private VersionValidationModule verMod;
	
	@Autowired
	public VersionValidator(SchemaErrorStrategy strategy, VersionValidationModule verMod) {
		this.strat = strategy;
		this.verMod = verMod;
	}
	
	public boolean validate() {
		String result = verMod.validate();
		if (!"".equals(result)) {
			strat.notifyOnError(result);
		}
		
		return strat.isOK();
	}
	
	public String getErrorMsg() {
		return strat.getErrorMsg();
	}
}
