/** 
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */

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
