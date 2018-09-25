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
package org.onap.aai.util.genxsd;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.config.SwaggerGenerationConfiguration;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;
import org.junit.runner.RunWith;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.edges.EdgeRule;
import org.onap.aai.edges.exceptions.EdgeRuleNotFoundException;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.serialization.queryformats.QueryFormatTestHelper;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.testutils.TestUtilConfigTranslatorforBusiness;
import org.onap.aai.util.AAIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.collect.Multimap;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
		SchemaVersions.class,
        SchemaLocationsBean.class,
        TestUtilConfigTranslatorforBusiness.class,
        SchemaVersions.class,
        EdgeIngestor.class,
        NodeIngestor.class,
		SwaggerGenerationConfiguration.class

})
@TestPropertySource(properties = {
		"schema.uri.base.path = /aai",
		"schema.xsd.maxoccurs = 5000"
})
public class YAMLfromOXMTest {
	@Autowired
	EdgeIngestor edgeIngestor;
	
	@Autowired
	NodeIngestor nodeIngestor;
	private static final Logger logger = LoggerFactory.getLogger("YAMLfromOXMTest.class");
	private static final String OXMFILENAME = "src/test/resources/oxm/business_oxm_v11.xml";
	private static final String EDGEFILENAME = "src/test/resources/dbedgerules/DbEdgeBusinessRules_test.json";
	public static AnnotationConfigApplicationContext ctx = null;
	private static String testXML;
	protected static final String SERVICE_NAME = "JUNIT";
    boolean first = true;
    
    @Autowired
	YAMLfromOXM yamlFromOxm;

    @Autowired
    SchemaVersions schemaVersions;
        
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		 System.setProperty("AJSC_HOME", ".");
	        System.setProperty("BUNDLECONFIG_DIR", "src/test/resources/bundleconfig-local");
	        System.setProperty("aai.service.name", SERVICE_NAME);
	        
	        QueryFormatTestHelper.setFinalStatic(AAIConstants.class.getField("AAI_HOME_ETC_OXM"), "src/test/resources/bundleconfig-local/etc/oxm/");
	        XSDElementTest x = new XSDElementTest();
			x.setUp();
			testXML = x.testXML;
			logger.debug(testXML);
			BufferedWriter bw = new BufferedWriter(new FileWriter(OXMFILENAME));
			bw.write(testXML);
			bw.close();
			BufferedWriter bw1 = new BufferedWriter(new FileWriter(EDGEFILENAME));
			bw1.write(EdgeDefs());
			bw1.close();
			
			    
			
			
	
	}

	@Before
	public void setUp() throws Exception {
		
    }

	@Test
	public void AtestIngestors() throws EdgeRuleNotFoundException {
		Multimap<String, EdgeRule> results = edgeIngestor.getAllRules(schemaVersions.getDefaultVersion());
		SortedSet<String> ss=new TreeSet<String>(results.keySet());
		for(String key : ss) {
			results.get(key).stream().filter((i) -> ((! i.isPrivateEdge()))).forEach((i) ->{ EdgeDescription ed = new EdgeDescription(i); System.out.println(ed.getRuleKey()); } );
		}		
		Document doc = nodeIngestor.getSchema(schemaVersions.getDefaultVersion());
		assertNotNull(doc);
	}
	
	@Test
	public void testGetDocumentHeader() {
		SchemaVersion v = schemaVersions.getAppRootVersion();
		String apiVersion = v.toString();
		String header = null;
		try {
			yamlFromOxm.setXmlVersion(testXML, v);
			yamlFromOxm.process();
			header = yamlFromOxm.getDocumentHeader();
		} catch(Exception e) {
			e.printStackTrace();
		}
		assertThat("Header:\n"+header,header, is(YAMLheader()));
	}

	@Test
	public void testProcess() {
		SchemaVersion v = schemaVersions.getAppRootVersion();
		String apiVersion = v.toString();
		String fileContent = null;
		try {
			yamlFromOxm.setXmlVersion(testXML, v);
			fileContent = yamlFromOxm.process();
		} catch(Exception e) {
			e.printStackTrace();
		}
		assertThat("FileContent-TestProcess:\n"+fileContent,fileContent, is(YAMLresult()));
	}
	
	
	@Test
	public void testYAMLfromOXMFileVersionFile() throws IOException {
		String outfileName = "testXML.xml";
		File XMLfile = new File(outfileName);
		XMLfile.createNewFile();
		BufferedWriter bw = null;
		Charset charset = Charset.forName("UTF-8");
		Path path = Paths.get(outfileName);
		bw = Files.newBufferedWriter(path, charset);
		bw.write(testXML);
		bw.close();
		SchemaVersion v = schemaVersions.getAppRootVersion();
		String apiVersion = v.toString();
		String fileContent = null;
		try {
			yamlFromOxm.setXmlVersion(testXML, v);
			fileContent = yamlFromOxm.process();
		} catch(Exception e) {
			e.printStackTrace();
		}		
		XMLfile.delete();
		assertThat("FileContent-OXMFileVersionFile:\n"+fileContent,fileContent, is(YAMLresult()));		
	 }

	@Test
	public void testYAMLfromOXMStringVersionFile() {
		SchemaVersion v = schemaVersions.getAppRootVersion();
		String apiVersion = v.toString();
		String fileContent = null;
		try {
			yamlFromOxm.setXmlVersion(testXML, v);
			fileContent = yamlFromOxm.process();
		} catch(Exception e) {
			e.printStackTrace();
		}
		assertThat("FileContent-OXMStringVersionFile:\n"+fileContent,fileContent, is(YAMLresult()));
	}

	@Test
	public void testAppendDefinitions() {
		SchemaVersion v = schemaVersions.getAppRootVersion();
		String apiVersion = v.toString();
		String definitions = null;
		try {
			yamlFromOxm.setXmlVersion(testXML, v);
			yamlFromOxm.process();
			definitions = yamlFromOxm.appendDefinitions();
		} catch(Exception e) {
			e.printStackTrace();
		}
		assertThat("Definitions:\n"+definitions,definitions, is(YAMLdefs()+YAMLpatchDefs()+YAMLgetDefs()));
	}

	@Test
	public void testGetXMLRootElementName() {
		String target = "RootElement=customer";
		SchemaVersion v = schemaVersions.getAppRootVersion();
		String apiVersion = v.toString();
		Element customer = null;
		String root = null;
		try {
			yamlFromOxm.setXmlVersion(testXML, v);
			yamlFromOxm.process();
			customer = yamlFromOxm.getJavaTypeElementSwagger("Customer");
			root = yamlFromOxm.getXMLRootElementName(customer);
		} catch(Exception e) {
			e.printStackTrace();
		}
		assertThat("RootElement="+root, is(target));
	}

	@Test
	public void testGetXmlRootElementName() {
		String target = "RootElement=customer";
		SchemaVersion v = schemaVersions.getAppRootVersion();
		String apiVersion = v.toString();
		String root = null;
		try {
			yamlFromOxm.setXmlVersion(testXML, v);
			yamlFromOxm.process();
			root = yamlFromOxm.getXmlRootElementName("Customer");
		} catch(Exception e) {
			e.printStackTrace();
		}
		assertThat("RootElement="+root, is(target));
	}

	@Test
	public void testGetJavaTypeElementSwagger() {
		String target = "Element=java-type/Customer";
		SchemaVersion v = schemaVersions.getAppRootVersion();
		String apiVersion = v.toString();
		Element customer = null;
		try {
			yamlFromOxm.setXmlVersion(testXML, v);
			yamlFromOxm.process();
			customer = yamlFromOxm.getJavaTypeElementSwagger("Customer");
		} catch(Exception e) {
			e.printStackTrace();
		}
		assertThat("Element="+customer.getNodeName()+"/"+customer.getAttribute("name"), is(target));
	}

	public String YAMLresult() {
		StringBuilder sb = new StringBuilder(32368);
		sb.append(YAMLheader());
		sb.append(YAMLops());
		sb.append(YAMLdefs());
		sb.append(YAMLpatchDefs());
		sb.append(YAMLgetDefs());
		return sb.toString();
	}
	public String YAMLheader() {
		StringBuilder sb = new StringBuilder(1500);
		sb.append("swagger: \"2.0\"\n");
		sb.append("info:\n");
		sb.append("  description: |\n");
		sb.append("\n");
		sb.append("    [Differences versus the previous schema version](apidocs/aai_swagger_v11.diff)\n");
		sb.append("\n");
		sb.append("    Copyright &copy; 2017-18 AT&amp;T Intellectual Property. All rights reserved.\n");
		sb.append("\n");
		sb.append("    Licensed under the Creative Commons License, Attribution 4.0 Intl. (the &quot;License&quot;); you may not use this documentation except in compliance with the License.\n");
		sb.append("\n");
		sb.append("    You may obtain a copy of the License at\n");
		sb.append("\n");
		sb.append("    (https://creativecommons.org/licenses/by/4.0/)\n");
		sb.append("\n");
		sb.append("    Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an &quot;AS IS&quot; BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.\n");
		sb.append("\n");
		sb.append("    This document is best viewed with Firefox or Chrome. Nodes can be found by appending /#/definitions/node-type-to-find to the path to this document. Edge definitions can be found with the node definitions.\n");
		sb.append("  version: \"v11\"\n");
		sb.append("  title: Active and Available Inventory REST API\n");
		sb.append("  license:\n");
		sb.append("    name: Apache 2.0\n");
		sb.append("    url: http://www.apache.org/licenses/LICENSE-2.0.html\n");
		sb.append("  contact:\n");
		sb.append("    name:\n");
		sb.append("    url:\n");
		sb.append("    email:\n");
		sb.append("host:\n");
		sb.append("basePath: /aai/v11\n");
		sb.append("schemes:\n");
		sb.append("  - https\n");
		sb.append("paths:\n");
		return sb.toString();
	}
	
	public String YAMLops() {
		StringBuilder sb = new StringBuilder(16384);
		sb.append("  /business/customers/customer/{global-customer-id}/service-subscriptions/service-subscription/{service-type}:\n");
		sb.append("    get:\n");
		sb.append("      tags:\n");
		sb.append("        - Business\n");
		sb.append("      summary: returns service-subscription\n");
		sb.append("      description: returns service-subscription\n");
		sb.append("      operationId: getBusinessCustomersCustomerServiceSubscriptionsServiceSubscription\n");
		sb.append("      produces:\n");
		sb.append("        - application/json\n");
		sb.append("        - application/xml\n");
		sb.append("      responses:\n");
		sb.append("        \"200\":\n");
		sb.append("          description: successful operation\n");
		sb.append("          schema:\n");
		sb.append("              $ref: \"#/getDefinitions/service-subscription\"\n");
		sb.append("        \"default\":\n");
		sb.append("          null      parameters:\n");
		sb.append("        - name: global-customer-id\n");
		sb.append("          in: path\n");
		sb.append("          description: Global customer id used across to uniquely identify customer.\n");
		sb.append("          required: true\n");
		sb.append("          type: string\n");
		sb.append("          example: __GLOBAL-CUSTOMER-ID__\n");
		sb.append("        - name: service-type\n");
		sb.append("          in: path\n");
		sb.append("          description: Value defined by orchestration to identify this service.\n");
		sb.append("          required: true\n");
		sb.append("          type: string\n");
		sb.append("          example: __SERVICE-TYPE__\n");
		sb.append("    put:\n");
		sb.append("      tags:\n");
		sb.append("        - Business\n");
		sb.append("      summary: create or update an existing service-subscription\n");
		sb.append("      description: |\n");
		sb.append("        Create or update an existing service-subscription.\n");
		sb.append("        #\n");
		sb.append("        Note! This PUT method has a corresponding PATCH method that can be used to update just a few of the fields of an existing object, rather than a full object replacement.  An example can be found in the [PATCH section] below\n");
		sb.append("      operationId: createOrUpdateBusinessCustomersCustomerServiceSubscriptionsServiceSubscription\n");
		sb.append("      consumes:\n");
		sb.append("        - application/json\n");
		sb.append("        - application/xml\n");
		sb.append("      produces:\n");
		sb.append("        - application/json\n");
		sb.append("        - application/xml\n");
		sb.append("      responses:\n");
		sb.append("        \"default\":\n");
		sb.append("          null      parameters:\n");
		sb.append("        - name: global-customer-id\n");
		sb.append("          in: path\n");
		sb.append("          description: Global customer id used across to uniquely identify customer.\n");
		sb.append("          required: true\n");
		sb.append("          type: string\n");
		sb.append("          example: __GLOBAL-CUSTOMER-ID__\n");
		sb.append("        - name: service-type\n");
		sb.append("          in: path\n");
		sb.append("          description: Value defined by orchestration to identify this service.\n");
		sb.append("          required: true\n");
		sb.append("          type: string\n");
		sb.append("          example: __SERVICE-TYPE__\n");
		sb.append("        - name: body\n");
		sb.append("          in: body\n");
		sb.append("          description: service-subscription object that needs to be created or updated. [Valid relationship examples shown here](apidocs/relations/v11/BusinessCustomersCustomerServiceSubscriptionsServiceSubscription.json)\n");
		sb.append("          required: true\n");
		sb.append("          schema:\n");
		sb.append("            $ref: \"#/definitions/service-subscription\"\n");
		sb.append("    patch:\n");
		sb.append("      tags:\n");
		sb.append("        - Business\n");
		sb.append("      summary: update an existing service-subscription\n");
		sb.append("      description: |\n");
		sb.append("        Update an existing service-subscription\n");
		sb.append("        #\n");
		sb.append("        Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.\n");
		sb.append("        The PUT operation will entirely replace an existing object.\n");
		sb.append("        The PATCH operation sends a \"description of changes\" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.\n");
		sb.append("        #\n");
		sb.append("        Other differences between PUT and PATCH are:\n");
		sb.append("        #\n");
		sb.append("        - For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.\n");
		sb.append("        - For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.\n");
		sb.append("        - PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.\n");
		sb.append("      operationId: UpdateBusinessCustomersCustomerServiceSubscriptionsServiceSubscription\n");
		sb.append("      consumes:\n");
		sb.append("        - application/json\n");
		sb.append("      produces:\n");
		sb.append("        - application/json\n");
		sb.append("      responses:\n");
		sb.append("        \"default\":\n");
		sb.append("          null      parameters:\n");
		sb.append("        - name: global-customer-id\n");
		sb.append("          in: path\n");
		sb.append("          description: Global customer id used across to uniquely identify customer.\n");
		sb.append("          required: true\n");
		sb.append("          type: string\n");
		sb.append("          example: __GLOBAL-CUSTOMER-ID__\n");
		sb.append("        - name: service-type\n");
		sb.append("          in: path\n");
		sb.append("          description: Value defined by orchestration to identify this service.\n");
		sb.append("          required: true\n");
		sb.append("          type: string\n");
		sb.append("          example: __SERVICE-TYPE__\n");
		sb.append("        - name: body\n");
		sb.append("          in: body\n");
		sb.append("          description: service-subscription object that needs to be updated.\n");
		sb.append("          required: true\n");
		sb.append("          schema:\n");
		sb.append("            $ref: \"#/patchDefinitions/service-subscription\"\n");
		sb.append("    delete:\n");
		sb.append("      tags:\n");
		sb.append("        - Business\n");
		sb.append("      summary: delete an existing service-subscription\n");
		sb.append("      description: delete an existing service-subscription\n");
		sb.append("      operationId: deleteBusinessCustomersCustomerServiceSubscriptionsServiceSubscription\n");
		sb.append("      consumes:\n");
		sb.append("        - application/json\n");
		sb.append("        - application/xml\n");
		sb.append("      produces:\n");
		sb.append("        - application/json\n");
		sb.append("        - application/xml\n");
		sb.append("      responses:\n");
		sb.append("        \"default\":\n");
		sb.append("          null      parameters:\n");
		sb.append("        - name: global-customer-id\n");
		sb.append("          in: path\n");
		sb.append("          description: Global customer id used across to uniquely identify customer.\n");
		sb.append("          required: true\n");
		sb.append("          type: string\n");
		sb.append("          example: __GLOBAL-CUSTOMER-ID__\n");
		sb.append("        - name: service-type\n");
		sb.append("          in: path\n");
		sb.append("          description: Value defined by orchestration to identify this service.\n");
		sb.append("          required: true\n");
		sb.append("          type: string\n");
		sb.append("          example: __SERVICE-TYPE__\n");
		sb.append("        - name: resource-version\n");
		sb.append("          in: query\n");
		sb.append("          description: resource-version for concurrency\n");
		sb.append("          required: true\n");
		sb.append("          type: string\n");
		sb.append("  /business/customers/customer/{global-customer-id}/service-subscriptions:\n");
		sb.append("    get:\n");
		sb.append("      tags:\n");
		sb.append("        - Business\n");
		sb.append("      summary: returns service-subscriptions\n");
		sb.append("      description: returns service-subscriptions\n");
		sb.append("      operationId: getBusinessCustomersCustomerServiceSubscriptions\n");
		sb.append("      produces:\n");
		sb.append("        - application/json\n");
		sb.append("        - application/xml\n");
		sb.append("      responses:\n");
		sb.append("        \"200\":\n");
		sb.append("          description: successful operation\n");
		sb.append("          schema:\n");
		sb.append("              $ref: \"#/getDefinitions/service-subscriptions\"\n");
		sb.append("        \"default\":\n");
		sb.append("          null      parameters:\n");
		sb.append("        - name: global-customer-id\n");
		sb.append("          in: path\n");
		sb.append("          description: Global customer id used across to uniquely identify customer.\n");
		sb.append("          required: true\n");
		sb.append("          type: string\n");
		sb.append("          example: __GLOBAL-CUSTOMER-ID__\n");
		sb.append("        - name: service-type\n");
		sb.append("          in: query\n");
		sb.append("          description:\n");
		sb.append("          required: false\n");
		sb.append("          type: string\n");
		sb.append("  /business/customers/customer/{global-customer-id}:\n");
		sb.append("    get:\n");
		sb.append("      tags:\n");
		sb.append("        - Business\n");
		sb.append("      summary: returns customer\n");
		sb.append("      description: returns customer\n");
		sb.append("      operationId: getBusinessCustomersCustomer\n");
		sb.append("      produces:\n");
		sb.append("        - application/json\n");
		sb.append("        - application/xml\n");
		sb.append("      responses:\n");
		sb.append("        \"200\":\n");
		sb.append("          description: successful operation\n");
		sb.append("          schema:\n");
		sb.append("              $ref: \"#/getDefinitions/customer\"\n");
		sb.append("        \"default\":\n");
		sb.append("          null      parameters:\n");
		sb.append("        - name: global-customer-id\n");
		sb.append("          in: path\n");
		sb.append("          description: Global customer id used across to uniquely identify customer.\n");
		sb.append("          required: true\n");
		sb.append("          type: string\n");
		sb.append("          example: __GLOBAL-CUSTOMER-ID__\n");
		sb.append("    put:\n");
		sb.append("      tags:\n");
		sb.append("        - Business\n");
		sb.append("      summary: create or update an existing customer\n");
		sb.append("      description: |\n");
		sb.append("        Create or update an existing customer.\n");
		sb.append("        #\n");
		sb.append("        Note! This PUT method has a corresponding PATCH method that can be used to update just a few of the fields of an existing object, rather than a full object replacement.  An example can be found in the [PATCH section] below\n");
		sb.append("      operationId: createOrUpdateBusinessCustomersCustomer\n");
		sb.append("      consumes:\n");
		sb.append("        - application/json\n");
		sb.append("        - application/xml\n");
		sb.append("      produces:\n");
		sb.append("        - application/json\n");
		sb.append("        - application/xml\n");
		sb.append("      responses:\n");
		sb.append("        \"default\":\n");
		sb.append("          null      parameters:\n");
		sb.append("        - name: global-customer-id\n");
		sb.append("          in: path\n");
		sb.append("          description: Global customer id used across to uniquely identify customer.\n");
		sb.append("          required: true\n");
		sb.append("          type: string\n");
		sb.append("          example: __GLOBAL-CUSTOMER-ID__\n");
		sb.append("        - name: body\n");
		sb.append("          in: body\n");
		sb.append("          description: customer object that needs to be created or updated. [Valid relationship examples shown here](apidocs/relations/v11/BusinessCustomersCustomer.json)\n");
		sb.append("          required: true\n");
		sb.append("          schema:\n");
		sb.append("            $ref: \"#/definitions/customer\"\n");
		sb.append("    patch:\n");
		sb.append("      tags:\n");
		sb.append("        - Business\n");
		sb.append("      summary: update an existing customer\n");
		sb.append("      description: |\n");
		sb.append("        Update an existing customer\n");
		sb.append("        #\n");
		sb.append("        Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.\n");
		sb.append("        The PUT operation will entirely replace an existing object.\n");
		sb.append("        The PATCH operation sends a \"description of changes\" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.\n");
		sb.append("        #\n");
		sb.append("        Other differences between PUT and PATCH are:\n");
		sb.append("        #\n");
		sb.append("        - For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.\n");
		sb.append("        - For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.\n");
		sb.append("        - PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.\n");
		sb.append("      operationId: UpdateBusinessCustomersCustomer\n");
		sb.append("      consumes:\n");
		sb.append("        - application/json\n");
		sb.append("      produces:\n");
		sb.append("        - application/json\n");
		sb.append("      responses:\n");
		sb.append("        \"default\":\n");
		sb.append("          null      parameters:\n");
		sb.append("        - name: global-customer-id\n");
		sb.append("          in: path\n");
		sb.append("          description: Global customer id used across to uniquely identify customer.\n");
		sb.append("          required: true\n");
		sb.append("          type: string\n");
		sb.append("          example: __GLOBAL-CUSTOMER-ID__\n");
		sb.append("        - name: body\n");
		sb.append("          in: body\n");
		sb.append("          description: customer object that needs to be updated.\n");
		sb.append("          required: true\n");
		sb.append("          schema:\n");
		sb.append("            $ref: \"#/patchDefinitions/customer\"\n");
		sb.append("    delete:\n");
		sb.append("      tags:\n");
		sb.append("        - Business\n");
		sb.append("      summary: delete an existing customer\n");
		sb.append("      description: delete an existing customer\n");
		sb.append("      operationId: deleteBusinessCustomersCustomer\n");
		sb.append("      consumes:\n");
		sb.append("        - application/json\n");
		sb.append("        - application/xml\n");
		sb.append("      produces:\n");
		sb.append("        - application/json\n");
		sb.append("        - application/xml\n");
		sb.append("      responses:\n");
		sb.append("        \"default\":\n");
		sb.append("          null      parameters:\n");
		sb.append("        - name: global-customer-id\n");
		sb.append("          in: path\n");
		sb.append("          description: Global customer id used across to uniquely identify customer.\n");
		sb.append("          required: true\n");
		sb.append("          type: string\n");
		sb.append("          example: __GLOBAL-CUSTOMER-ID__\n");
		sb.append("        - name: resource-version\n");
		sb.append("          in: query\n");
		sb.append("          description: resource-version for concurrency\n");
		sb.append("          required: true\n");
		sb.append("          type: string\n");
		sb.append("  /business/customers:\n");
		sb.append("    get:\n");
		sb.append("      tags:\n");
		sb.append("        - Business\n");
		sb.append("      summary: returns customers\n");
		sb.append("      description: returns customers\n");
		sb.append("      operationId: getBusinessCustomers\n");
		sb.append("      produces:\n");
		sb.append("        - application/json\n");
		sb.append("        - application/xml\n");
		sb.append("      responses:\n");
		sb.append("        \"200\":\n");
		sb.append("          description: successful operation\n");
		sb.append("          schema:\n");
		sb.append("              $ref: \"#/getDefinitions/customers\"\n");
		sb.append("        \"default\":\n");
		sb.append("          null      parameters:\n");
		sb.append("        - name: global-customer-id\n");
		sb.append("          in: query\n");
		sb.append("          description:\n");
		sb.append("          required: false\n");
		sb.append("          type: string\n");
		sb.append("        - name: subscriber-name\n");
		sb.append("          in: query\n");
		sb.append("          description:\n");
		sb.append("          required: false\n");
		sb.append("          type: string\n");
		sb.append("        - name: subscriber-type\n");
		sb.append("          in: query\n");
		sb.append("          description:\n");
		sb.append("          required: false\n");
		sb.append("          type: string\n");
		return sb.toString();
	}
	public String YAMLdefs() {
		StringBuilder sb = new StringBuilder(8092);
		sb.append("definitions:\n");
		sb.append("  business:\n");
		sb.append("    description: |\n");
		sb.append("      Namespace for business related constructs\n");
		sb.append("    properties:\n");
		sb.append("      customers:\n");
		sb.append("        type: array\n");
		sb.append("        items:\n");
		sb.append("          $ref: \"#/definitions/customer\"\n");
		sb.append("  customer:\n");
		sb.append("    description: |\n");
		sb.append("      customer identifiers to provide linkage back to BSS information.\n");
		sb.append("      ###### Related Nodes\n");
		sb.append("      - FROM service-subscription (CHILD of customer, service-subscription BelongsTo customer, MANY2ONE)(1)\n");
		sb.append("\n");
		sb.append("      -(1) IF this CUSTOMER node is deleted, this FROM node is DELETED also\n");
		sb.append("    required:\n");
		sb.append("    - global-customer-id\n");
		sb.append("    - subscriber-name\n");
		sb.append("    - subscriber-type\n");
		sb.append("    properties:\n");
		sb.append("      global-customer-id:\n");
		sb.append("        type: string\n");
		sb.append("        description: Global customer id used across to uniquely identify customer.\n");
		sb.append("      subscriber-name:\n");
		sb.append("        type: string\n");
		sb.append("        description: Subscriber name, an alternate way to retrieve a customer.\n");
		sb.append("      subscriber-type:\n");
		sb.append("        type: string\n");
		sb.append("        description: Subscriber type, a way to provide VID with only the INFRA customers.\n");
		sb.append("      resource-version:\n");
		sb.append("        type: string\n");
		sb.append("        description: Used for optimistic concurrency.  Must be empty on create, valid on update and delete.\n");
		sb.append("      service-subscriptions:\n");
		sb.append("        type: array\n");
		sb.append("        items:\n");
		sb.append("          $ref: \"#/definitions/service-subscription\"\n");
		sb.append("  customers:\n");
		sb.append("    description: |\n");
		sb.append("      Collection of customer identifiers to provide linkage back to BSS information.\n");
		sb.append("    properties:\n");
		sb.append("      customer:\n");
		sb.append("        type: array\n");
		sb.append("        items:          \n");
		sb.append("          $ref: \"#/definitions/customer\"\n");
		sb.append("  inventory:\n");
		sb.append("    properties:\n");
		sb.append("      business:\n");
		sb.append("        type: object\n");
		sb.append("        $ref: \"#/definitions/business\"\n");
		sb.append("  nodes:\n");
		sb.append("    properties:\n");
		sb.append("      inventory-item-data:\n");
		sb.append("        type: array\n");
		sb.append("        items:\n");
		sb.append("          $ref: \"#/definitions/inventory-item-data\"\n");	
		sb.append("  service-subscription:\n");
		sb.append("    description: |\n");
		sb.append("      Object that group service instances.\n");
		sb.append("      ###### Related Nodes\n");
		sb.append("      - TO customer (PARENT of service-subscription, service-subscription BelongsTo customer, MANY2ONE)(4)\n");
		sb.append("      - TO tenant( service-subscription Uses tenant, MANY2MANY)\n");
		sb.append("      - FROM service-instance (CHILD of service-subscription, service-instance BelongsTo service-subscription, MANY2ONE)(1)\n");
		sb.append("\n");
		sb.append("      -(1) IF this SERVICE-SUBSCRIPTION node is deleted, this FROM node is DELETED also\n");
		sb.append("      -(4) IF this TO node is deleted, this SERVICE-SUBSCRIPTION is DELETED also\n");
		sb.append("    required:\n");
		sb.append("    - service-type\n");
		sb.append("    properties:\n");
		sb.append("      service-type:\n");
		sb.append("        type: string\n");
		sb.append("        description: Value defined by orchestration to identify this service.\n");
		sb.append("      temp-ub-sub-account-id:\n");
		sb.append("        type: string\n");
		sb.append("        description: This property will be deleted from A&AI in the near future. Only stop gap solution.\n");
		sb.append("      resource-version:\n");
		sb.append("        type: string\n");
		sb.append("        description: Used for optimistic concurrency.  Must be empty on create, valid on update and delete.\n");
		sb.append("  service-subscriptions:\n");
		sb.append("    description: |\n");
		sb.append("      Collection of objects that group service instances.\n");
		sb.append("    properties:\n");
		sb.append("      service-subscription:\n");
		sb.append("        type: array\n");
		sb.append("        items:          \n");
		sb.append("          $ref: \"#/definitions/service-subscription\"\n");
		return sb.toString();
	}
	public String YAMLpatchDefs() {
		StringBuilder sb = new StringBuilder(8092);
		sb.append("patchDefinitions:\n");
		sb.append("  business:\n");
		sb.append("    description: |\n");
		sb.append("      Namespace for business related constructs\n");
		sb.append("    properties:\n");
		sb.append("      customers:\n");
		sb.append("        type: array\n");
		sb.append("        items:\n");
		sb.append("          $ref: \"#/patchDefinitions/customer\"\n");
		sb.append("  customer:\n");
		sb.append("    description: |\n");
		sb.append("      customer identifiers to provide linkage back to BSS information.\n");
		sb.append("      ###### Related Nodes\n");
		sb.append("      - FROM service-subscription (CHILD of customer, service-subscription BelongsTo customer, MANY2ONE)(1)\n");
		sb.append("\n");
		sb.append("      -(1) IF this CUSTOMER node is deleted, this FROM node is DELETED also\n");
		sb.append("    required:\n");
		sb.append("    - global-customer-id\n");
		sb.append("    - subscriber-name\n");
		sb.append("    - subscriber-type\n");
		sb.append("    properties:\n");
		sb.append("      global-customer-id:\n");
		sb.append("        type: string\n");
		sb.append("        description: Global customer id used across to uniquely identify customer.\n");
		sb.append("      subscriber-name:\n");
		sb.append("        type: string\n");
		sb.append("        description: Subscriber name, an alternate way to retrieve a customer.\n");
		sb.append("      subscriber-type:\n");
		sb.append("        type: string\n");
		sb.append("        description: Subscriber type, a way to provide VID with only the INFRA customers.\n");
		sb.append("  customers:\n");
		sb.append("    description: |\n");
		sb.append("      Collection of customer identifiers to provide linkage back to BSS information.\n");
		sb.append("    properties:\n");
		sb.append("      customer:\n");
		sb.append("        type: array\n");
		sb.append("        items:          \n");
		sb.append("          $ref: \"#/patchDefinitions/customer\"\n");
		sb.append("  inventory:\n");
		sb.append("    properties:\n");
		sb.append("      business:\n");
		sb.append("        type: object\n");
		sb.append("        $ref: \"#/patchDefinitions/business\"\n");
		sb.append("  nodes:\n");
		sb.append("    properties:\n");
		sb.append("      inventory-item-data:\n");
		sb.append("        type: array\n");
		sb.append("        items:\n");
		sb.append("          $ref: \"#/patchDefinitions/inventory-item-data\"\n");	
		sb.append("  service-subscription:\n");
		sb.append("    description: |\n");
		sb.append("      Object that group service instances.\n");
		sb.append("      ###### Related Nodes\n");
		sb.append("      - TO customer (PARENT of service-subscription, service-subscription BelongsTo customer, MANY2ONE)(4)\n");
		sb.append("      - TO tenant( service-subscription Uses tenant, MANY2MANY)\n");
		sb.append("      - FROM service-instance (CHILD of service-subscription, service-instance BelongsTo service-subscription, MANY2ONE)(1)\n");
		sb.append("\n");
		sb.append("      -(1) IF this SERVICE-SUBSCRIPTION node is deleted, this FROM node is DELETED also\n");
		sb.append("      -(4) IF this TO node is deleted, this SERVICE-SUBSCRIPTION is DELETED also\n");
		sb.append("    required:\n");
		sb.append("    - service-type\n");
		sb.append("    properties:\n");
		sb.append("      service-type:\n");
		sb.append("        type: string\n");
		sb.append("        description: Value defined by orchestration to identify this service.\n");
		sb.append("      temp-ub-sub-account-id:\n");
		sb.append("        type: string\n");
		sb.append("        description: This property will be deleted from A&AI in the near future. Only stop gap solution.\n");
		sb.append("  service-subscriptions:\n");
		sb.append("    description: |\n");
		sb.append("      Collection of objects that group service instances.\n");
		sb.append("    properties:\n");
		sb.append("      service-subscription:\n");
		sb.append("        type: array\n");
		sb.append("        items:          \n");
		sb.append("          $ref: \"#/patchDefinitions/service-subscription\"\n");
		return sb.toString();
	}
	public String YAMLgetDefs() {
		StringBuilder sb = new StringBuilder(8092);
		sb.append("getDefinitions:\n");
		sb.append("  business:\n");
		sb.append("    description: |\n");
		sb.append("      Namespace for business related constructs\n");
		sb.append("    properties:\n");
		sb.append("      customers:\n");
		sb.append("        type: array\n");
		sb.append("        items:\n");
		sb.append("          $ref: \"#/getDefinitions/customer\"\n");
		sb.append("  customer:\n");
		sb.append("    description: |\n");
		sb.append("      customer identifiers to provide linkage back to BSS information.\n");
		sb.append("      ###### Related Nodes\n");
		sb.append("      - FROM service-subscription (CHILD of customer, service-subscription BelongsTo customer, MANY2ONE)(1)\n");
		sb.append("\n");
		sb.append("      -(1) IF this CUSTOMER node is deleted, this FROM node is DELETED also\n");
		sb.append("    required:\n");
		sb.append("    - global-customer-id\n");
		sb.append("    - subscriber-name\n");
		sb.append("    - subscriber-type\n");
		sb.append("    properties:\n");
		sb.append("      global-customer-id:\n");
		sb.append("        type: string\n");
		sb.append("        description: Global customer id used across to uniquely identify customer.\n");
		sb.append("      subscriber-name:\n");
		sb.append("        type: string\n");
		sb.append("        description: Subscriber name, an alternate way to retrieve a customer.\n");
		sb.append("      subscriber-type:\n");
		sb.append("        type: string\n");
		sb.append("        description: Subscriber type, a way to provide VID with only the INFRA customers.\n");
		sb.append("      resource-version:\n");
		sb.append("        type: string\n");
		sb.append("        description: Used for optimistic concurrency.  Must be empty on create, valid on update and delete.\n");
		sb.append("      service-subscriptions:\n");
		sb.append("        type: array\n");
		sb.append("        items:\n");
		sb.append("          $ref: \"#/getDefinitions/service-subscription\"\n");
		sb.append("  customers:\n");
		sb.append("    description: |\n");
		sb.append("      Collection of customer identifiers to provide linkage back to BSS information.\n");
		sb.append("    properties:\n");
		sb.append("      customer:\n");
		sb.append("        type: array\n");
		sb.append("        items:          \n");
		sb.append("          $ref: \"#/getDefinitions/customer\"\n");
		sb.append("  inventory:\n");
		sb.append("    properties:\n");
		sb.append("      business:\n");
		sb.append("        type: object\n");
		sb.append("        $ref: \"#/getDefinitions/business\"\n");
		sb.append("  nodes:\n");
		sb.append("    properties:\n");
		sb.append("      inventory-item-data:\n");
		sb.append("        type: array\n");
		sb.append("        items:\n");
		sb.append("          $ref: \"#/getDefinitions/inventory-item-data\"\n");	
		sb.append("  service-subscription:\n");
		sb.append("    description: |\n");
		sb.append("      Object that group service instances.\n");
		sb.append("      ###### Related Nodes\n");
		sb.append("      - TO customer (PARENT of service-subscription, service-subscription BelongsTo customer, MANY2ONE)(4)\n");
		sb.append("      - TO tenant( service-subscription Uses tenant, MANY2MANY)\n");
		sb.append("      - FROM service-instance (CHILD of service-subscription, service-instance BelongsTo service-subscription, MANY2ONE)(1)\n");
		sb.append("\n");
		sb.append("      -(1) IF this SERVICE-SUBSCRIPTION node is deleted, this FROM node is DELETED also\n");
		sb.append("      -(4) IF this TO node is deleted, this SERVICE-SUBSCRIPTION is DELETED also\n");
		sb.append("    required:\n");
		sb.append("    - service-type\n");
		sb.append("    properties:\n");
		sb.append("      service-type:\n");
		sb.append("        type: string\n");
		sb.append("        description: Value defined by orchestration to identify this service.\n");
		sb.append("      temp-ub-sub-account-id:\n");
		sb.append("        type: string\n");
		sb.append("        description: This property will be deleted from A&AI in the near future. Only stop gap solution.\n");
		sb.append("      resource-version:\n");
		sb.append("        type: string\n");
		sb.append("        description: Used for optimistic concurrency.  Must be empty on create, valid on update and delete.\n");
		sb.append("  service-subscriptions:\n");
		sb.append("    description: |\n");
		sb.append("      Collection of objects that group service instances.\n");
		sb.append("    properties:\n");
		sb.append("      service-subscription:\n");
		sb.append("        type: array\n");
		sb.append("        items:          \n");
		sb.append("          $ref: \"#/getDefinitions/service-subscription\"\n");
		return sb.toString();
	}
	public static String EdgeDefs() {
		StringBuilder sb = new StringBuilder(8092);
		sb.append("{\n" + 
				"	\"rules\": [\n");
		sb.append("		{\n");
		sb.append("			\"from\": \"service-subscription\",\n");
		sb.append("			\"to\": \"customer\",\n" + 
				"			\"label\": \"org.onap.relationships.inventory.BelongsTo\",\n" + 
				"			\"direction\": \"OUT\",\n" + 
				"			\"multiplicity\": \"MANY2ONE\",\n" + 
				"			\"contains-other-v\": \"!${direction}\",\n" + 
				"			\"delete-other-v\": \"!${direction}\",\n" + 
				"			\"prevent-delete\": \"NONE\",\n" + 
				"			\"default\": \"true\",\n" + 
				"			\"description\":\"\"\n");
		sb.append("		},\n");
		sb.append("		{\n" + 
				"			\"from\": \"service-instance\",\n" + 
				"			\"to\": \"service-subscription\",\n" + 
				"			\"label\": \"org.onap.relationships.inventory.BelongsTo\",\n" + 
				"			\"direction\": \"OUT\",\n" + 
				"			\"multiplicity\": \"MANY2ONE\",\n" + 
				"			\"contains-other-v\": \"!${direction}\",\n" + 
				"			\"delete-other-v\": \"!${direction}\",\n" + 
				"			\"prevent-delete\": \"NONE\",\n" + 
				"			\"default\": \"true\",\n" + 
				"			\"description\":\"\"\n" + 
				"		},\n");
		sb.append("		{\n" + 
				"			\"from\": \"service-subscription\",\n" + 
				"			\"to\": \"tenant\",\n" + 
				"			\"label\": \"org.onap.relationships.inventory.Uses\",\n" + 
				"			\"direction\": \"OUT\",\n" + 
				"			\"multiplicity\": \"MANY2MANY\",\n" + 
				"			\"contains-other-v\": \"NONE\",\n" + 
				"			\"delete-other-v\": \"NONE\",\n" + 
				"			\"prevent-delete\": \"NONE\",\n" + 
				"			\"default\": \"true\",\n" + 
				"			\"description\":\"\"\n" + 
				"		}");
		sb.append("	]\n" + 
				"}\n");
		return sb.toString();
	}
}

