package org.onap.aai.testutils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.onap.aai.setup.ConfigTranslator;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.setup.Version;

/**
 * Good oxm, bad edge rules for rainy day edge rule validation testing
 */
public class BadEdgeConfigForValidationTest extends ConfigTranslator {

	public BadEdgeConfigForValidationTest(SchemaLocationsBean bean) {
		super(bean);
	}

	@Override
	public Map<Version, List<String>> getNodeFiles() {
		List<String> files = new ArrayList<>();
		files.add("src/test/resources/oxm/goodConfigForValidationTest_oxm.xml");
		Map<Version, List<String>> input = new EnumMap<>(Version.class);
		input.put(Version.getLatest(), files);
		return input;
	}

	@Override
	public Map<Version, List<String>> getEdgeFiles() {
		Map<Version, List<String>> input = new EnumMap<>(Version.class);
		List<String> files = new ArrayList<>();
		files.add("src/test/resources/edgeRules/test3-butbad.json");
		input.put(Version.getLatest(), files);
		return input;
	}

	
}
