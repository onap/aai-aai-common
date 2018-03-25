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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.File;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import org.onap.aai.introspection.Version;
import org.onap.aai.util.genxsd.HTMLfromOXM;
import org.onap.aai.util.genxsd.HTMLfromOXMTest;
import org.onap.aai.util.genxsd.XSDElementTest;
import org.onap.aai.util.genxsd.YAMLfromOXM;
import org.onap.aai.util.genxsd.YAMLfromOXMTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GenerateXsdTest {
	private static final Logger logger = LoggerFactory.getLogger("GenerateXsd.class");
	private String testXML;
		
	@Before
	public void setUp() throws Exception {
		//PowerMockito.mockStatic(GenerateXsd.class);
		XSDElementTest x = new XSDElementTest();
		x.setUp();
		testXML = x.getTestXML();
	}
	
	@Test
	public void test_generateSwaggerFromOxmFile( ) {
		Version v = Version.v11;
		String apiVersion = v.toString();
		String fileContent = null;
		File edgeRuleFile = new File("../aai-core" + "/src/main/resources/dbedgerules/DbEdgeRules_" + apiVersion + ".json");
		try {
			YAMLfromOXM swagger = new YAMLfromOXM(testXML, v, edgeRuleFile);
			fileContent = swagger.process();
		} catch(Exception e) {
			e.printStackTrace();
		}
		logger.debug(fileContent);
		assertThat(fileContent, is(new YAMLfromOXMTest().YAMLresult()));
	}
		
	@Test
	public void test_generateXSDFromOxmFile( ) {
		
		Version v = Version.v11;
		String fileContent = null;
		try {
			HTMLfromOXM xsd = new HTMLfromOXM(testXML, v);
			fileContent = xsd.process();
		} catch(Exception e) {
			e.printStackTrace();
		}
		logger.debug(fileContent);
		assertThat(fileContent, is(new HTMLfromOXMTest().HTMLresult()));
	}
	
	@Test
	public void testGetAPIVersion() {
		GenerateXsd.apiVersion=Version.v11.name();
		assertThat(GenerateXsd.getAPIVersion(),is("v11"));
	}

	@Test
	public void testGetYamlDir() {
		assertThat(GenerateXsd.getYamlDir(),is("../aai-schema/src/main/resources/aai_swagger_yaml"));
	}

	@Test
	public void testGetResponsesUrl() {
		assertNull(GenerateXsd.getResponsesUrl());
	}
}
