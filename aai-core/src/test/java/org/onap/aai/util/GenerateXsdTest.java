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
 *    http://www.apache.org/licenses/LICENSE-2.0
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
package org.onap.aai.util;

import static org.junit.Assert.*;

import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import org.onap.aai.introspection.Version;


public class GenerateXsdTest {
		private static final int maxSizeForXml = 20000;
		private String testXML;
		
	@Before
	public void setUp() throws Exception {
		//PowerMockito.mockStatic(GenerateXsd.class);
		StringBuilder sb = new StringBuilder(maxSizeForXml);
		addNamespace(sb);
		addRelationshipList(sb);
		addRelationship(sb);
		addRelatedToProperty(sb);
		addRelationshipData(sb);
		addBusiness(sb);
		addCustomers(sb);
		addCustomer(sb);
		addServiceSubscriptions(sb);
		addServiceSubscription(sb);
		addEndOfXML(sb);
		testXML = sb.toString();
	}
	
	private void addNamespace(StringBuilder sb){
		sb.append("<xml-bindings xmlns=\"http://www.eclipse.org/eclipselink/xsds/persistence/oxm\" package-name=\"inventory.aai.onap.org.v11\" xml-mapping-metadata-complete=\"true\">\n");
		sb.append("<xml-schema element-form-default=\"QUALIFIED\">\n");
		sb.append("<xml-ns namespace-uri=\"http://org.onap.aai.inventory/v11\" />\n");
		sb.append("</xml-schema>\n");
		sb.append("<java-types>\n");
		sb.append("<java-type name=\"Inventory\">\n");
		sb.append("<xml-root-element name=\"inventory\" />\n");
		sb.append("<java-attributes>\n");
		sb.append("<xml-element java-attribute=\"business\" name=\"business\" type=\"inventory.aai.onap.org.v11.Business\" />\n");
		sb.append("</java-attributes>\n");
		sb.append("</java-type>\n");
	}
	
	private void addRelationshipList(StringBuilder sb ) {
		sb.append("<java-type name=\"RelationshipList\">\n");
		sb.append("<xml-root-element name=\"relationship-list\" />\n");
		sb.append("<java-attributes>\n");
			sb.append("<xml-element container-type=\"java.util.ArrayList\" java-attribute=\"relationship\" name=\"relationship\" type=\"inventory.aai.onap.org.v11.Relationship\" />/n");
		sb.append("</java-attributes>\n");
	sb.append("</java-type>\n");
	}
	
	private void addRelationship(StringBuilder sb ) {
		sb.append("<java-type name=\"Relationship\">\n");
		sb.append("<xml-root-element name=\"relationship\" />\n");
		sb.append("<java-attributes>\n");
		sb.append("<xml-element java-attribute=\"relatedTo\" name=\"related-to\" type=\"java.lang.String\">\n");
		sb.append("<xml-properties>\n");
		sb.append("<xml-property name=\"description\" value=\"A keyword provided by A&amp;AI to indicate type of node.\" />\n");
		sb.append("</xml-properties>\n");
		sb.append("</xml-element>\n");
		sb.append("<xml-element java-attribute=\"relatedLink\" name=\"related-link\" type=\"java.lang.String\">\n");
		sb.append("<xml-properties>\n");
		sb.append("<xml-property name=\"description\" value=\"URL to the object in A&amp;AI.\" />");
		sb.append("</xml-properties>\n");
		sb.append("</xml-element>\n");
		sb.append("<xml-element container-type=\"java.util.ArrayList\" java-attribute=\"relationshipData\" name=\"relationship-data\" type=\"inventory.aai.onap.org.v11.RelationshipData\" />\n");
		sb.append("<xml-element container-type=\"java.util.ArrayList\" java-attribute=\"relatedToProperty\" name=\"related-to-property\" type=\"inventory.aai.onap.org.v11.RelatedToProperty\" />\n");
		sb.append("</java-attributes>\n");
		sb.append("</java-type>\n");
	}
	
	private void addRelatedToProperty(StringBuilder sb) {
		sb.append("<java-type name=\"RelatedToProperty\">\n");
		sb.append("<xml-root-element name=\"related-to-property\" />\n");
		sb.append("<java-attributes>\n");
			sb.append("<xml-element java-attribute=\"propertyKey\" name=\"property-key\" type=\"java.lang.String\">\n");
			sb.append("<xml-properties>\n");
			sb.append("<xml-property name=\"description\" value=\"Key part of a key/value pair\" />\n");
			sb.append("</xml-properties>\n");
			sb.append("</xml-element>\n");
			sb.append("<xml-element java-attribute=\"propertyValue\" name=\"property-value\" type=\"java.lang.String\">\n");
			sb.append("<xml-properties>\n");
			sb.append("<xml-property name=\"description\" value=\"Value part of a key/value pair\" />\n");
			sb.append("</xml-properties>\n");
			sb.append("</xml-element>\n");
			sb.append("</java-attributes>\n");
			sb.append("</java-type>\n");
	}
	
	private void addRelationshipData(StringBuilder sb){
		sb.append("<java-type name=\"RelationshipData\">\n");
		sb.append("<xml-root-element name=\"relationship-data\" />\n");
		sb.append("<java-attributes>\n");
			sb.append("<xml-element java-attribute=\"relationshipKey\" name=\"relationship-key\" required=\"true\" type=\"java.lang.String\">\n");
			sb.append("<xml-properties>\n");
			sb.append("<xml-property name=\"description\" value=\"A keyword provided by A&amp;AI to indicate an attribute.\" />\n");
			sb.append("</xml-properties>\n");
			sb.append("</xml-element>\n");
			sb.append("<xml-element java-attribute=\"relationshipValue\" name=\"relationship-value\" required=\"true\" type=\"java.lang.String\">\n");
			sb.append("<xml-properties>\n");
			sb.append("<xml-property name=\"description\" value=\"Value of the attribute.\" />\n");
			sb.append("</xml-properties>\n");
			sb.append("</xml-element>\n");
			sb.append("</java-attributes>\n");
			sb.append("</java-type>\n");
	}
	
	private void addBusiness(StringBuilder sb){
		sb.append("<java-type name=\"Business\">\n");
		sb.append("<xml-properties>\n");
				sb.append("<xml-property name=\"description\" value=\"Namespace for business related constructs\" />\n");
				sb.append("</xml-properties>\n");
				sb.append("<xml-root-element name=\"business\" />\n");
				sb.append("<java-attributes>\n");
				sb.append("<xml-element java-attribute=\"customers\" name=\"customers\" type=\"inventory.aai.onap.org.v11.Customers\" />\n");
				sb.append("</java-attributes>\n");
				sb.append("</java-type>\n");		
	}
	
	private void addCustomers(StringBuilder sb){
		sb.append("<java-type name=\"Customers\">\n");
		sb.append("<xml-properties>\n");
		sb.append("<xml-property name=\"description\" value=\"Collection of customer identifiers to provide linkage back to BSS information.\" />\n");
		sb.append("</xml-properties>\n");
		sb.append("<xml-root-element name=\"customers\" />\n");
		sb.append("<java-attributes>\n");
		sb.append("<xml-element container-type=\"java.util.ArrayList\" java-attribute=\"customer\" name=\"customer\" type=\"inventory.aai.onap.org.v11.Customer\" />\n");
		sb.append("</java-attributes>\n");
		sb.append("<xml-properties>\n");
		sb.append("<xml-property name=\"maximumDepth\" value=\"0\" />\n");
		sb.append("</xml-properties>\n");
		sb.append("</java-type>\n");
	}
	
	private void addCustomer(StringBuilder sb){
		sb.append("<java-type name=\"Customer\">\n");
		sb.append("<xml-root-element name=\"customer\" />\n");
		sb.append("<java-attributes>\n");
		sb.append("<xml-element java-attribute=\"globalCustomerId\" name=\"global-customer-id\" required=\"true\" type=\"java.lang.String\" xml-key=\"true\">\n");
		sb.append("<xml-properties>\n");
		sb.append("<xml-property name=\"description\" value=\"Global customer id used across ECOMP to uniquely identify customer.\" />\n");
		sb.append("</xml-properties>\n");
		sb.append("</xml-element>\n");
		sb.append("<xml-element java-attribute=\"subscriberName\" name=\"subscriber-name\" required=\"true\" type=\"java.lang.String\">\n");
		sb.append("<xml-properties>\n");
		sb.append("<xml-property name=\"description\" value=\"Subscriber name, an alternate way to retrieve a customer.\" />\n");
		sb.append("</xml-properties>\n");
		sb.append("</xml-element>\n");
		sb.append("<xml-element java-attribute=\"subscriberType\" name=\"subscriber-type\" required=\"true\" type=\"java.lang.String\">\n");
		sb.append("<xml-properties>\n");
		sb.append("<xml-property name=\"description\" value=\"Subscriber type, a way to provide VID with only the INFRA customers.\" />\n");
		sb.append("<xml-property name=\"defaultValue\" value=\"CUST\" />\n");
		sb.append("</xml-properties>\n");
		sb.append("</xml-element>\n");
		sb.append("<xml-element java-attribute=\"resourceVersion\" name=\"resource-version\" type=\"java.lang.String\">\n");
		sb.append("<xml-properties>\n");
		sb.append("<xml-property name=\"description\" value=\"Used for optimistic concurrency.  Must be empty on create, valid on update and delete.\" />\n");
		sb.append("</xml-properties>\n");
		sb.append("</xml-element>\n");
		sb.append("<xml-element java-attribute=\"serviceSubscriptions\" name=\"service-subscriptions\" type=\"inventory.aai.onap.org.v11.ServiceSubscriptions\" />\n");
		sb.append("<xml-element java-attribute=\"relationshipList\" name=\"relationship-list\" type=\"inventory.aai.onap.org.v11.RelationshipList\" />\n");
		sb.append("</java-attributes>\n");
		sb.append("<xml-properties>\n");
		sb.append("<xml-property name=\"description\" value=\"customer identifiers to provide linkage back to BSS information.\" />\n");
		sb.append("<xml-property name=\"nameProps\" value=\"subscriber-name\" />\n");
		sb.append("<xml-property name=\"indexedProps\" value=\"subscriber-name,global-customer-id,subscriber-type\" />\n");
		sb.append("<xml-property name=\"searchable\" value=\"global-customer-id,subscriber-name\" />\n");
		sb.append("<xml-property name=\"uniqueProps\" value=\"global-customer-id\" />\n");
		sb.append("<xml-property name=\"container\" value=\"customers\" />\n");
		sb.append("<xml-property name=\"namespace\" value=\"business\" />\n");
		sb.append("</xml-properties>\n");
		sb.append("</java-type>\n");		
	}
	
	private void addServiceSubscriptions(StringBuilder sb){
		sb.append("<java-type name=\"ServiceSubscriptions\">\n");
		sb.append("<xml-properties>\n");
		sb.append("<xml-property name=\"description\" value=\"Collection of objects that group service instances.\" />\n");
		sb.append("</xml-properties>\n");
		sb.append("<xml-root-element name=\"service-subscriptions\" />\n");
		sb.append("<java-attributes>\n");
		sb.append("<xml-element container-type=\"java.util.ArrayList\" java-attribute=\"serviceSubscription\" name=\"service-subscription\" type=\"inventory.aai.onap.org.v11.ServiceSubscription\" />\n");
		sb.append("</java-attributes>\n");
		sb.append("</java-type>\n");
	}
	private void addServiceSubscription(StringBuilder sb){
		sb.append("<java-type name=\"ServiceSubscription\">\n");
		sb.append("<xml-root-element name=\"service-subscription\" />\n");
		sb.append("<java-attributes>\n");
		sb.append("<xml-element java-attribute=\"serviceType\" name=\"service-type\" required=\"true\" type=\"java.lang.String\" xml-key=\"true\">\n");
		sb.append("<xml-properties>\n");
		sb.append("<xml-property name=\"description\" value=\"Value defined by orchestration to identify this service across ECOMP.\" />\n");
		sb.append("</xml-properties>\n");
		sb.append("</xml-element>\n");
		sb.append("<xml-element java-attribute=\"tempUbSubAccountId\" name=\"temp-ub-sub-account-id\" type=\"java.lang.String\">\n");
		sb.append("<xml-properties>\n");
		sb.append("<xml-property name=\"description\" value=\"This property will be deleted from A&amp;AI in the near future. Only stop gap solution.\" />\n");
		sb.append("</xml-properties>\n");
                sb.append("</xml-element>\n");
                sb.append("<xml-element java-attribute=\"resourceVersion\" name=\"resource-version\" type=\"java.lang.String\">\n");
                sb.append("<xml-properties>\n");
                sb.append("<xml-property name=\"description\" value=\"Used for optimistic concurrency.  Must be empty on create, valid on update and delete.\" />\n");
                sb.append("</xml-properties>\n");
                sb.append("</xml-element>\n");
                sb.append("<xml-element java-attribute=\"relationshipList\" name=\"relationship-list\" type=\"inventory.aai.onap.org.v11.RelationshipList\" />\n");
                sb.append("</java-attributes>\n");
                sb.append("<xml-properties>\n");
                sb.append("<xml-property name=\"description\" value=\"Object that group service instances.\" />\n");
                sb.append("<xml-property name=\"indexedProps\" value=\"service-type\" />\n");
                sb.append("<xml-property name=\"dependentOn\" value=\"customer\" />\n");
                sb.append("<xml-property name=\"container\" value=\"service-subscriptions\" />\n");
                sb.append("<xml-property name=\"crossEntityReference\" value=\"service-instance,service-type\" />\n");
                sb.append("</xml-properties>\n");
                sb.append("</java-type>\n");
	}
	
	private void addEndOfXML(StringBuilder sb){
		sb.append("</java-types>\n");
		sb.append("</xml-bindings>\n");
	}
	
	
	@Test
	public void test_processOxmFile() {
		

		GenerateXsd generateXsd = new GenerateXsd();
	    
		//GenerateXsd generateXsdSpy = PowerMockito.spy(generateXsd);

		//generateXsdSpy.processOxmFile(null, Version.getLatest(), testXML);

		String xsdResult = generateXsd.processOxmFile(null, Version.getLatest(), testXML);
		
		String relationshipListExpected = "  <xs:element name=\"relationship-list\">\n    <xs:complexType>\n      <xs:sequence>\n        <xs:element ref=\"tns:relationship\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n      </xs:sequence>\n    </xs:complexType>\n  </xs:element>";
		String relatedToPropertyExpected = "  <xs:element name=\"related-to-property\">\n    <xs:complexType>\n      <xs:sequence>\n        <xs:element name=\"property-key\" type=\"xs:string\" minOccurs=\"0\">\n          <xs:annotation>\r\n            <xs:appinfo>\r\n              <annox:annotate target=\"field\">@org.onap.aai.annotations.Metadata(description=\"Key part of a key/value pair\")</annox:annotate>\r\n            </xs:appinfo>\r\n          </xs:annotation>\r\n        </xs:element>\n"
				+ "        <xs:element name=\"property-value\" type=\"xs:string\" minOccurs=\"0\">\n          <xs:annotation>\r\n            <xs:appinfo>\r\n              <annox:annotate target=\"field\">@org.onap.aai.annotations.Metadata(description=\"Value part of a key/value pair\")</annox:annotate>\r\n            </xs:appinfo>\r\n          </xs:annotation>\r\n        </xs:element>\n      </xs:sequence>\n    </xs:complexType>\n  </xs:element>";
		assertNotNull(xsdResult);
	}
	@Test
	public void test_generateSwaggerFromOxmFile( ) {
		

		GenerateXsd generateXsd = new GenerateXsd();

		String customerOperation = " /business/customers/customer/{global-customer-id}:\n    get:\n      tags:\n        - Business\n      summary: returns customer";
		String relationshipListDefinition = "  relationship-list:\n    properties:\n      relationship:\n        type: array\n        items:          \n          $ref: \"#/definitions/relationship\"";
		String swaggerResult = generateXsd.generateSwaggerFromOxmFile(null, testXML);
		assertNotNull(swaggerResult);
	}
}
