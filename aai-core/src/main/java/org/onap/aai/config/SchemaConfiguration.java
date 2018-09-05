/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright © 2018 IBM.
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
import org.onap.aai.setup.AAIConfigTranslator;
import org.onap.aai.serialization.db.EdgeSerializer;
import org.onap.aai.setup.ConfigTranslator;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.setup.SchemaVersions;
import org.onap.aai.validation.CheckEverythingStrategy;
import org.onap.aai.validation.SchemaErrorStrategy;
import org.springframework.context.annotation.*;

@Configuration
public class SchemaConfiguration {

    @Bean
    public EdgeIngestor edgeIngestor(SchemaLocationsBean schemaLocationsBean, SchemaVersions schemaVersions){
        return new EdgeIngestor(configTranslator(schemaLocationsBean, schemaVersions), schemaVersions);
    }

    @Bean
    public EdgeSerializer edgeSerializer(EdgeIngestor edgeIngestor){
        return new EdgeSerializer(edgeIngestor);
    }
    
	@Bean(name = "nodeIngestor")
	public NodeIngestor nodeIngestor(ConfigTranslator configTranslator) {
        return new NodeIngestor(configTranslator);
	}

	@Bean(name = "configTranslator")
	public ConfigTranslator configTranslator(SchemaLocationsBean schemaLocationsBean, SchemaVersions schemaVersions) {
        return new AAIConfigTranslator(schemaLocationsBean, schemaVersions);
	}

	@Bean
	public SchemaErrorStrategy schemaErrorStrategy(){
        return new CheckEverythingStrategy();
	}
}
