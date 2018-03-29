package org.onap.aai.testutils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.onap.aai.setup.ConfigTranslator;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.setup.Version;

public class ConfigTranslatorForWiringTest extends ConfigTranslator {

	public ConfigTranslatorForWiringTest(SchemaLocationsBean bean) {
		super(bean);
	}

	@Override
	public Map<Version, List<String>> getNodeFiles() {
		String f = bean.getNodeDirectory() + "test_business_v10.xml";
		List<String> files = new ArrayList<>();
		files.add(f);
		Map<Version, List<String>> mp = new EnumMap<>(Version.class);
		mp.put(Version.V10, files);
		return mp;
	}

	@Override
	public Map<Version, List<String>> getEdgeFiles() {
		String f = bean.getEdgeDirectory() + "test.json";
		List<String> files = new ArrayList<>();
		files.add(f);
		Map<Version, List<String>> mp = new EnumMap<>(Version.class);
		mp.put(Version.V10, files);
		return mp;
	}

}
