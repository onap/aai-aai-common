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
package org.onap.aai.config;

import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.introspection.MoxyLoader;
import org.onap.aai.nodes.NodeIngestor;

@Configuration
public class IntrospectionConfig {

	private Map<SchemaVersion, MoxyLoader> MOXYINSTANCEMAP = new ConcurrentHashMap<>();
	@Autowired
	NodeIngestor nodeIngestor;

	@Bean
	public LoaderFactory loaderFactory(SchemaVersions schemaVersions) {
		LoaderFactory loaderFactory = new LoaderFactory(moxyLoaderInstance(schemaVersions));
		return loaderFactory;
	}

	@Bean
	public Map<SchemaVersion, MoxyLoader> moxyLoaderInstance(SchemaVersions schemaVersions) {
	    for(SchemaVersion version : schemaVersions.getVersions()){
			if (!MOXYINSTANCEMAP.containsKey(version)) {
				MOXYINSTANCEMAP.put(version, new MoxyLoader(version, nodeIngestor));
			}
		}
		return MOXYINSTANCEMAP;
	}
}
