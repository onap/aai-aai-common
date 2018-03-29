package org.onap.aai.testutils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.onap.aai.setup.ConfigTranslator;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.setup.Version;

/**
 * All schema files here are valid for sunny day validator testing
 */
public class BadNodeConfigForValidationTest extends ConfigTranslator {

	public BadNodeConfigForValidationTest(SchemaLocationsBean bean) {
		super(bean);
	}

	@Override
	public Map<Version, List<String>> getNodeFiles() {
		List<String> files = new ArrayList<>();
		files.add("src/test/resources/oxm/goodConfigForValidationTest_oxm.xml");
		files.add("src/test/resources/oxm/badConfigForValidationTest_oxm.xml");
		Map<Version, List<String>> input = new EnumMap<>(Version.class);
		input.put(Version.getLatest(), files);
		return input;
	}

	@Override
	public Map<Version, List<String>> getEdgeFiles() {
		Map<Version, List<String>> input = new EnumMap<>(Version.class);
		List<String> files = new ArrayList<>();
		files.add("src/test/resources/edgeRules/test3.json");
		input.put(Version.getLatest(), files);
		return input;
	}

	
}
