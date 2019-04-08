/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.aai.serialization.db;

import org.onap.aai.AbstractConfigTranslator;
import org.onap.aai.setup.SchemaConfigVersions;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;

import java.util.*;

/**
 * Quick and dirty access to the real schema files for updating core tests
 *
 */
public class AAICorePrivateEdgeTestConfigTranslator extends AbstractConfigTranslator {

	public AAICorePrivateEdgeTestConfigTranslator(SchemaLocationsBean bean, SchemaConfigVersions schemaVersions) {
		super(bean, schemaVersions);
	}

	/* (non-Javadoc)
	 * @see org.onap.aai.setup.ConfigTranslator#getEdgeFiles()
	 */
	@Override
	public Map<SchemaVersion, List<String>> getEdgeFiles() {
		String file = "src/test/resources/dbedgerules/DbEdgeRules_PrivateEdges.json";

		Map<SchemaVersion, List<String>> files = new TreeMap<>();
		List<String> container = new ArrayList<>();
		container.add(file);
		files.put(schemaVersions.getDefaultVersion(), container);

		return files;
	}



}
