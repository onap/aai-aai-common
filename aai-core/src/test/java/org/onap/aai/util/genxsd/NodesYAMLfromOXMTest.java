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

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.config.SwaggerGenerationConfiguration;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;
import org.junit.runner.RunWith;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.testutils.TestUtilConfigTranslatorforEdges;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Element;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
		SchemaVersions.class,
        SchemaLocationsBean.class,
        TestUtilConfigTranslatorforEdges.class,
        EdgeIngestor.class,
        NodeIngestor.class,
		SwaggerGenerationConfiguration.class

})
@TestPropertySource(properties = {
		"schema.uri.base.path = /aai",
		"schema.xsd.maxoccurs = 5000"
})
public class NodesYAMLfromOXMTest {
//public class NodesYAMLfromOXMTest extends AAISetup {
	private static final Logger logger = LoggerFactory.getLogger("NodesYAMLfromOXMTest.class");
	private static final String OXMFILENAME = "src/test/resources/oxm/business_v11.xml";
	private static final String EDGEFILENAME = "src/test/resources/dbedgerules/EdgeDescriptionRules_test.json";
	public static AnnotationConfigApplicationContext ctx = null;
	private static String testXML;

	@Autowired
	NodesYAMLfromOXM nodesYamlFromOxm;
	@Autowired
	SchemaVersions schemaVersions;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		XSDElementTest x = new XSDElementTest();
		x.setUp();
		testXML = x.testXML;
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
		
		NodeGetOperation.checklist.clear();
		XSDElementTest x = new XSDElementTest();
		x.setUp();
		testXML = x.testXML;
		
		logger.debug(testXML);
	}

	@Test
	public void testGetDocumentHeader() {
		SchemaVersion v = schemaVersions.getAppRootVersion();
		String apiVersion = v.toString();
		String header = null;
		try {
			nodesYamlFromOxm.setXmlVersion(testXML, v);
			nodesYamlFromOxm.process();
			header = nodesYamlFromOxm.getDocumentHeader();
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
			nodesYamlFromOxm.setXmlVersion(testXML, v);
			fileContent = nodesYamlFromOxm.process();
		} catch(Exception e) {
			e.printStackTrace();
		}
		assertThat("FileContent-I:\n"+fileContent,fileContent, is(YAMLresult()));
	}

	@Test
	public void testNodesYAMLfromOXMFileVersionFile() throws IOException {
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
			nodesYamlFromOxm.setXmlVersion(testXML, v);
			fileContent = nodesYamlFromOxm.process();
		} catch(Exception e) {
			e.printStackTrace();
		}		
		XMLfile.delete();
		assertThat("FileContent:\n"+fileContent,fileContent, is(YAMLresult()));		
	 }

	@Test
	public void testNodesYAMLfromOXMStringVersionFile() {
		SchemaVersion v = schemaVersions.getAppRootVersion();
		String apiVersion = v.toString();
		String fileContent = null;
		try {
			nodesYamlFromOxm.setXmlVersion(testXML, v);
			fileContent = nodesYamlFromOxm.process();
		} catch(Exception e) {
			e.printStackTrace();
		}
		assertThat("FileContent-II:\n"+fileContent,fileContent, is(YAMLresult()));
	}

	@Test
	public void testAppendDefinitions() {
		SchemaVersion v = schemaVersions.getAppRootVersion();
		String apiVersion = v.toString();
		String definitions = null;
		try {
			nodesYamlFromOxm.setXmlVersion(testXML, v);
			nodesYamlFromOxm.process();
			definitions = nodesYamlFromOxm.appendDefinitions();
		} catch(Exception e) {
			e.printStackTrace();
		}
		assertThat("Definitions:\n"+definitions,definitions, is(YAMLgetDefs()));
	}

	@Test
	public void testGetXMLRootElementName() {
		String target = "RootElement=customer";
		SchemaVersion v = schemaVersions.getAppRootVersion();
		String apiVersion = v.toString();
		Element customer = null;
		String root = null;
		try {
			nodesYamlFromOxm.setXmlVersion(testXML, v);
			nodesYamlFromOxm.process();
			customer = nodesYamlFromOxm.getJavaTypeElementSwagger("Customer");
			root = nodesYamlFromOxm.getXMLRootElementName(customer);
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
			nodesYamlFromOxm.setXmlVersion(testXML, v);
			nodesYamlFromOxm.process();
			root = nodesYamlFromOxm.getXmlRootElementName("Customer");
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
			nodesYamlFromOxm.setXmlVersion(testXML, v);
			nodesYamlFromOxm.process();
			customer = nodesYamlFromOxm.getJavaTypeElementSwagger("Customer");
		} catch(Exception e) {
			e.printStackTrace();
		}
		assertThat("Element="+customer.getNodeName()+"/"+customer.getAttribute("name"), is(target));
	}

	public String YAMLresult() {
		StringBuilder sb = new StringBuilder(32368);
		sb.append(YAMLheader());
		sb.append(YAMLops());
//		sb.append(YAMLdefs());
//		sb.append(YAMLpatchDefs());
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
		sb.append("  /nodes/customers/customer/{global-customer-id}:\n");
		sb.append("    get:\n");
		sb.append("      tags:\n");
		sb.append("        - Operations\n");
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
		sb.append("              $ref: \"#/definitions/customer\"\n");
		sb.append("        \"default\":\n");
		sb.append("          null\n      parameters:\n");
		sb.append("        - name: global-customer-id\n");
		sb.append("          in: path\n");
		sb.append("          description: Global customer id used across to uniquely identify customer.\n");
		sb.append("          required: true\n");
		sb.append("          type: string\n");
		sb.append("          example: __GLOBAL-CUSTOMER-ID__\n");
		sb.append("  /nodes/customers?parameter=value[&parameter2=value2]:\n");
		sb.append("    get:\n");
		sb.append("      tags:\n");
		sb.append("        - Operations\n");
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
		sb.append("              $ref: \"#/definitions/customers\"\n");
		sb.append("        \"default\":\n");
		sb.append("          null\n      parameters:\n");
		sb.append("        - name: global-customer-id\n");
		sb.append("          in: query\n");
		sb.append("          description: Global customer id used across to uniquely identify customer.\n");
		sb.append("          required: false\n");
		sb.append("          type: string\n");
		sb.append("          example: __GLOBAL-CUSTOMER-ID__\n");
		sb.append("        - name: subscriber-name\n");
		sb.append("          in: query\n");
		sb.append("          description: Subscriber name, an alternate way to retrieve a customer.\n");
		sb.append("          required: false\n");
		sb.append("          type: string\n");
		sb.append("          example: __SUBSCRIBER-NAME__\n");
		sb.append("        - name: subscriber-type\n");
		sb.append("          in: query\n");
		sb.append("          description: Subscriber type, a way to provide VID with only the INFRA customers.\n");
		sb.append("          required: false\n");
		sb.append("          type: string\n");
		sb.append("          example: __SUBSCRIBER-TYPE__\n");
		sb.append("  /nodes/service-subscriptions?parameter=value[&parameter2=value2]:\n");
		sb.append("    get:\n");
		sb.append("      tags:\n");
		sb.append("        - Operations\n");
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
		sb.append("              $ref: \"#/definitions/service-subscriptions\"\n");
		sb.append("        \"default\":\n");
		sb.append("          null\n      parameters:\n");
		sb.append("        - name: service-type\n");
		sb.append("          in: query\n");
		sb.append("          description: Value defined by orchestration to identify this service.\n");
		sb.append("          required: false\n");
		sb.append("          type: string\n");
		sb.append("          example: __SERVICE-TYPE__\n");
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
		sb.append("      - TO customer (CHILD of customer, service-subscription BelongsTo customer, MANY2ONE)(4)\n");
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
}

