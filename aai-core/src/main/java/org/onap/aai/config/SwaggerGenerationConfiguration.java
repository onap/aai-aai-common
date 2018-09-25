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


import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.setup.SchemaVersions;
import org.onap.aai.util.genxsd.HTMLfromOXM;
import org.onap.aai.util.genxsd.NodesYAMLfromOXM;
import org.onap.aai.util.genxsd.YAMLfromOXM;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class SwaggerGenerationConfiguration {

	@Value("${schema.uri.base.path}")
	private String basePath;

	@Value("${schema.xsd.maxoccurs:5000}")
	private String maxOccurs;
	
	@Bean
	@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public NodesYAMLfromOXM nodesYamlFromOXM(SchemaVersions schemaVersions, NodeIngestor nodeIngestor, EdgeIngestor edgeIngestor) {
		NodesYAMLfromOXM nodesYamlFromOXM = new NodesYAMLfromOXM(basePath, schemaVersions, nodeIngestor, edgeIngestor);
		return nodesYamlFromOXM;
	}
	
	@Bean
	@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public HTMLfromOXM htmlFromOXM(SchemaVersions schemaVersions, NodeIngestor nodeIngestor, EdgeIngestor edgeIngestor) {
		HTMLfromOXM htmlFromOXM = new HTMLfromOXM(maxOccurs, schemaVersions, nodeIngestor, edgeIngestor);
		return htmlFromOXM;
	}
	
	@Bean
	@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public YAMLfromOXM yamlFromOXM(SchemaVersions schemaVersions, NodeIngestor nodeIngestor, EdgeIngestor edgeIngestor) {
		YAMLfromOXM yamlFromOXM = new YAMLfromOXM(basePath, schemaVersions, nodeIngestor, edgeIngestor);
		return yamlFromOXM;
	}
    
}
