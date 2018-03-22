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
package org.onap.aai.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.onap.aai.introspection.Version;
import org.onap.aai.util.swagger.GenerateSwagger;

import freemarker.template.TemplateException;

public class AutoGenerateHtml {
	
	public static final String DEFAULT_SCHEMA_DIR = "../aai-schema";
	//if the program is run from aai-common, use this directory as default"
	public static final String ALT_SCHEMA_DIR = "aai-schema";
	//used to check to see if program is run from aai-core
	public static final String DEFAULT_RUN_DIR = "aai-core";

	public static void main(String[] args) throws IOException, TemplateException {
		String savedProperty = System.getProperty("aai.generate.version");
		List<Version> versionsToGen = Arrays.asList(Version.values());
		Collections.sort(versionsToGen);
		Collections.reverse(versionsToGen);
		ListIterator<Version> versionIterator = versionsToGen.listIterator();
		String schemaDir;
		if(System.getProperty("user.dir") != null && !System.getProperty("user.dir").contains(DEFAULT_RUN_DIR)) {
			schemaDir = ALT_SCHEMA_DIR;
  		}
  		else {
  			schemaDir = DEFAULT_SCHEMA_DIR;
  		}
		while (versionIterator.hasNext()) {
			System.setProperty("aai.generate.version", versionIterator.next().toString());   
	        String yamlFile = schemaDir + "/src/main/resources/aai_swagger_yaml/aai_swagger_" + System.getProperty("aai.generate.version")+ ".yaml";
	        File swaggerYamlFile = new File(yamlFile);
	        if(swaggerYamlFile.exists()) {
	        	GenerateSwagger.main(args);
	        }
		}
		String versionToGenerate = System.setProperty("aai.generate.version", savedProperty);	
	}
}
