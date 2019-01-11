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

package org.onap.aai.testutils;

import org.onap.aai.setup.ConfigTranslator;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;

import java.util.*;

public class TestUtilConfigTranslatorforDataLink extends ConfigTranslator {

	public TestUtilConfigTranslatorforDataLink(SchemaLocationsBean bean, SchemaVersions schemaVersions) {
		super(bean, schemaVersions);
	}

	@Override
	public Map<SchemaVersion, List<String>> getNodeFiles() {

		Map<SchemaVersion, List<String>> input = new TreeMap<>();
		input.put(new SchemaVersion("v1"), Arrays.asList("src/test/resources/oxm/dbalias_oxm_one.xml"));
		input.put(new SchemaVersion("v2"), Arrays.asList("src/test/resources/oxm/dbalias_oxm_two.xml"));
		input.put(new SchemaVersion("v3"), Arrays.asList("src/test/resources/oxm/dbalias_oxm_three.xml"));
		input.put(new SchemaVersion("v4"), Arrays.asList("src/test/resources/oxm/dbalias_oxm_four.xml"));
		return input;
	}

	@Override
	public Map<SchemaVersion, List<String>> getEdgeFiles() {
		Map<SchemaVersion, List<String>> input = new TreeMap<>();
		input.put(new SchemaVersion("v1"), Arrays.asList("src/test/resources/dbedgerules/DbEdgerules_one.json"));
		input.put(new SchemaVersion("v2"), Arrays.asList("src/test/resources/dbedgerules/DbEdgerules_two.json"));
		input.put(new SchemaVersion("v3"), Arrays.asList("src/test/resources/dbedgerules/DbEdgerules_three.json"));
		input.put(new SchemaVersion("v4"), Arrays.asList("src/test/resources/dbedgerules/DbEdgerules_four.json"));
		return input;
	}
}
