/**
 * 
 */
package org.onap.aai.validation;

import java.util.List;
import java.util.Map;

import org.onap.aai.setup.ConfigTranslator;
import org.onap.aai.setup.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * By default, A&AI must have schema files for all current
 * supported Versions in the Version enum
 *
 */
@Component
public class DefaultVersionValidationModule implements VersionValidationModule {
	private ConfigTranslator config;
	
	@Autowired
	public DefaultVersionValidationModule(ConfigTranslator config) {
		this.config = config;
	}

	/* (non-Javadoc)
	 * @see org.onap.aai.validation.VersionValidationModule#validate(org.onap.aai.setup.ConfigTranslator)
	 */
	@Override
	public String validate() {
		Map<Version, List<String>> nodeConfig = config.getNodeFiles();
		Map<Version, List<String>> edgeConfig = config.getEdgeFiles();
		
		StringBuilder missingVers = new StringBuilder().append("Missing schema for the following versions: ");
		boolean isMissing = false;
		for (Version v : Version.values()) {
			if (nodeConfig.get(v) == null) {
				isMissing = true;
				missingVers.append(v.toString()).append(" has no OXM configured. ");
			}
			if (edgeConfig.get(v) == null) {
				isMissing = true;
				missingVers.append(v.toString()).append(" has no edge rules configured. ");
			}
		}
		
		if (isMissing) {
			return missingVers.toString();
		} else {
			return "";
		}
	}

}
