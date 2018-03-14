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

package org.onap.aai.nodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.onap.aai.setup.ConfigTranslator;
import org.onap.aai.setup.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
/**
 * NodeIngestor - ingests A&AI OXM files per given config, serves DynamicJAXBContext per version
 */
public class NodeIngestor {
	
	private Map<Version, DynamicJAXBContext> versionContextMap = new EnumMap<>(Version.class);
	
	@Autowired
	/**
	 * Instantiates the NodeIngestor bean.
	 * 
	 * @param translator - ConfigTranslator autowired in by Spring framework which
	 * contains the configuration information needed to ingest the desired files.
	 */
	public NodeIngestor(ConfigTranslator translator) {
		Map<Version, List<String>> filesToIngest = translator.getNodeFiles();
		
		try {
			for (Entry<Version, List<String>> verFiles : filesToIngest.entrySet()) {
				Version v = verFiles.getKey();
				List<String> files = verFiles.getValue();
				final DynamicJAXBContext ctx = ingest(files);
				versionContextMap.put(v, ctx);
			}
		} catch (FileNotFoundException | JAXBException e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	
	/**
	 * Ingests the given OXM files into DynamicJAXBContext
	 * 
	 * @param files - List<String> of full filenames (ie including the path) to be ingested
	 * 
	 * @return DynamicJAXBContext including schema information from all given files
	 * 
	 * @throws FileNotFoundException if an OXM file can't be found
	 * @throws JAXBException if there's an error creating the DynamicJAXBContext
	 */
	private DynamicJAXBContext ingest(List<String> files) throws FileNotFoundException, JAXBException {
		List<InputStream> streams = new ArrayList<>();
		
		for (String name : files) {
			streams.add(new FileInputStream(new File(name)));
		}
		
		Map<String, Object> properties = new HashMap<>(); 
		properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, streams);
		return DynamicJAXBContextFactory.createContextFromOXM(this.getClass().getClassLoader(), properties);
	}

	/**
	 * Gets the DynamicJAXBContext for the given version
	 * 
	 * @param Version v
	 * @return DynamicJAXBContext
	 */
	public DynamicJAXBContext getContextForVersion(Version v) {
		return versionContextMap.get(v);
	}
}
