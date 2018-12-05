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
package org.onap.aai.setup;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <b>AAIConfigTranslator</b> is responsible for looking at the
 * schema files and edge files based on the available versions
 * Also has the ability to exclude them based on the node.exclusion.pattern
 */
public class AAIConfigTranslator extends ConfigTranslator {

    private static final String FILESEP = (System.getProperty("file.separator") == null) ? "/" : System.getProperty("file.separator");

    public AAIConfigTranslator(SchemaLocationsBean bean, SchemaVersions schemaVersions) {
		super(bean, schemaVersions);
	}
	
	/* (non-Javadoc)
	 * @see org.onap.aai.setup.ConfigTranslator#getNodeFiles()
	 */
	@Override
	public Map<SchemaVersion, List<String>> getNodeFiles() {

		Map<SchemaVersion, List<String>> files = new TreeMap<>();
		for (SchemaVersion v : schemaVersions.getVersions()) {
			List<String> container = getVersionNodeFiles(v);
			files.put(v, container);
		}
		
		return files;
	}
	

	private List<String> getVersionNodeFiles(SchemaVersion v) {
	    return getVersionFiles(
	    	bean.getNodeDirectory(),
			v,
			() -> bean.getNodesInclusionPattern().stream(),
			() -> bean.getNodesExclusionPattern().stream()
		);
	}
	

	/* (non-Javadoc)
	 * @see org.onap.aai.setup.ConfigTranslator#getEdgeFiles()
	 */
	@Override
	public Map<SchemaVersion, List<String>> getEdgeFiles() {

		Map<SchemaVersion, List<String>> files = new TreeMap<>();
		for (SchemaVersion v : schemaVersions.getVersions()) {
			List<String> container = getVersionEdgeFiles(v);
			files.put(v, container);
		}

		return files;
	}

	private List<String> getVersionEdgeFiles(SchemaVersion v) {

		return getVersionFiles(
				bean.getEdgeDirectory(),
				v,
				() -> bean.getEdgesInclusionPattern().stream(),
				() -> bean.getEdgesExclusionPattern().stream()
		);
	}

	private List<String> getVersionFiles(
			String startDirectory,
			SchemaVersion schemaVersion,
			Supplier<Stream<String>> inclusionPattern,
			Supplier<Stream<String>> exclusionPattern
	){

		List<String> container;
		final String directoryName = startDirectory + FILESEP + schemaVersion.toString() + FILESEP;
		container = Arrays.stream(new File(directoryName).listFiles())
				.map(File::getName)
				.filter(name -> inclusionPattern.get().anyMatch(name::matches))
				.map(name -> directoryName + name)
				.filter(name -> exclusionPattern.get().noneMatch(name::matches))
				.collect(Collectors.toList());

		return container;
	}
}
