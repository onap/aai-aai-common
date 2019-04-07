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

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.config.ConfigConfiguration;
import org.onap.aai.config.SwaggerGenerationConfiguration;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.testutils.TestUtilConfigTranslatorforBusiness;

import org.onap.aai.AAISetup;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;
import org.onap.aai.util.genxsd.HTMLfromOXM;
import org.onap.aai.util.genxsd.HTMLfromOXMTest;
import org.onap.aai.util.genxsd.XSDElementTest;
import org.onap.aai.util.genxsd.YAMLfromOXM;
import org.onap.aai.util.genxsd.YAMLfromOXMTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        ConfigConfiguration.class,
        TestUtilConfigTranslatorforBusiness.class,
        EdgeIngestor.class,
        NodeIngestor.class,
		SwaggerGenerationConfiguration.class
})
@TestPropertySource(properties = {
		"schema.uri.base.path = /aai"
})
public class GenerateXsdTest {
	private static final Logger logger = LoggerFactory.getLogger("GenerateXsd.class");
	private static final String OXMFILENAME = "src/test/resources/oxm/business_oxm_v11.xml";
	private static final String EDGEFILENAME = "src/test/resources/dbedgerules/DbEdgeBusinessRules_test.json";
	public static AnnotationConfigApplicationContext ctx = null;
	private static String testXML;

	@Autowired
	YAMLfromOXM yamlFromOxm;
	
	@Autowired
	HTMLfromOXM htmlFromOxm;
	
	@Autowired
	SchemaVersions schemaVersions;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		XSDElementTest x = new XSDElementTest();
		x.setUp();
		testXML = x.getTestXML();
		logger.debug(testXML);
		BufferedWriter bw = new BufferedWriter(new FileWriter(OXMFILENAME));
		bw.write(testXML);
		bw.close();
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(EDGEFILENAME));
		bw1.write(YAMLfromOXMTest.EdgeDefs());
		bw1.close();

	}	

	@Before
	public void setUp() throws Exception {
		//PowerMockito.mockStatic(GenerateXsd.class);
		XSDElementTest x = new XSDElementTest();
		x.setUp();
		testXML = x.getTestXML();
//		logger.info(testXML);
	}
	
	@Test
	public void test_generateSwaggerFromOxmFile( ) {
		
		SchemaVersion v = schemaVersions.getAppRootVersion();
		String apiVersion = v.toString();
		String fileContent = null;
		try {
			
			yamlFromOxm.setXmlVersion(testXML, v);
			fileContent = yamlFromOxm.process();
		} catch(Exception e) {
			e.printStackTrace();
		}
		assertThat(fileContent, is(new YAMLfromOXMTest().YAMLresult()));
	}
		
	@Test
	public void test_generateXSDFromOxmFile( ) {
		
		SchemaVersion v = schemaVersions.getAppRootVersion();
		String fileContent = null;
		try {
			htmlFromOxm.setXmlVersion(testXML, v);
			fileContent = htmlFromOxm.process();
		} catch(Exception e) {
			e.printStackTrace();
		}
//		logger.debug(fileContent);
		assertThat(fileContent, is(new HTMLfromOXMTest().HTMLresult()));
	}
	
	@Test
	public void testGetAPIVersion() {
		GenerateXsd.apiVersion = schemaVersions.getAppRootVersion().toString();
		assertThat(GenerateXsd.getAPIVersion(),is("v11"));
	}

	@Test
	public void testGetYamlDir() {
		assertThat(GenerateXsd.getYamlDir(),is("aai-schema/src/main/resources/onap/aai_swagger_yaml"));
	}

	@Test
	public void testGetResponsesUrl() {
		assertNull(GenerateXsd.getResponsesUrl());
	}
}

