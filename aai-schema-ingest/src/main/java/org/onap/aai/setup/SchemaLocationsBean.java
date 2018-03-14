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

package org.onap.aai.setup;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:schemaIngest.properties")
@PropertySource(value = "file:${schemaIngestPropLoc}", ignoreResourceNotFound=true)
public class SchemaLocationsBean {
	/*
	 * Per Spring documentation, the last PropertySource that works will
	 * be applied. Here, schemaIngestPropLoc will be an environment variable
	 * set on install that tells Spring where to look for the schema
	 * ingest properties file (and the actual filename), but the former
	 * PropertySource gives the default of looking on the classpath for
	 * schemaIngest.properties in case that second one doesn't work.
	 * 
	 * The schemaIngest.properties file (or its equivalent if you choose 
	 * to name it otherwise) must contain the entries the below @Value
	 * annotations are looking for.
	 */
	
	@Value("${schemaConfig}")
	private String schemaConfigLoc;
	
	@Value("${nodeDir}")
	private String nodeDirectory;
	
	@Value("${edgeDir}")
	private String edgeDirectory;
	
	/**
	 * @return the file name/location with the list of schema files to be ingested
	 */
	public String getSchemaConfigLocation() {
		return schemaConfigLoc;
	}
	
	/**
	 * Sets the name/location of the file with the list of schema files to ingest
	 * 
	 * @param String schemaConfigLoc - the file name/location 
	 */
	public void setSchemaConfigLocation(String schemaConfigLoc) {
		this.schemaConfigLoc = schemaConfigLoc;
	}
	
	/**
	 * @return the location of the OXM files
	 */
	public String getNodeDirectory() {
		return nodeDirectory;
	}
	
	/**
	 * Sets the location of the OXM files
	 * 
	 * @param String nodeDirectory - the location of the OXM files
	 */
	public void setNodeDirectory(String nodeDirectory) {
		this.nodeDirectory = nodeDirectory;
	}
	
	/**
	 * @return the location of the edge rule json files
	 */
	public String getEdgeDirectory() {
		return edgeDirectory;
	}
	
	/**
	 * Sets the location of the edge rule json files
	 * 
	 * @param String edgeDirectory - the location of the edge rule files
	 */
	public void setEdgeDirectory(String edgeDirectory) {
		this.edgeDirectory = edgeDirectory;
	}
	
	//this allows the code to actually read the value from the config file
	//without this those strings get set to literally "${edgeDir}" etc
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
