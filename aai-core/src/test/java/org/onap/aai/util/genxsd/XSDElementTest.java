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

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsIn.*;
import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Every.everyItem;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.aai.exceptions.AAIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XSDElementTest {
	private static final Logger logger = LoggerFactory.getLogger("XSDElementTest.class");
	private static final int maxSizeForXml = 20000;
	protected String testXML;
	protected Document doc = null;
	protected NodeList javaTypeNodes=null;
	
	public String getTestXML() {
		return testXML;
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		setUp(0);
	}
		
	public void setUp( int sbopt ) throws Exception {
		StringBuilder sb = new StringBuilder(maxSizeForXml);
		addNamespace(sb);
		addBusiness(sb);
		addCustomers(sb);
		if ( sbopt == 0 ) {
			addCustomer(sb);
		} else {
			addCustomerNoSubscriberType(sb);
			addCustomerSubscriberType(sb);
		}
		addServiceSubscriptions(sb);
		addServiceSubscription(sb);
		addEndOfXML(sb);
		testXML = sb.toString();
		init();
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
		sb.append("<xml-property name=\"description\" value=\"Global customer id used across to uniquely identify customer.\" />\n");
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
//		sb.append("<xml-element java-attribute=\"relationshipList\" name=\"relationship-list\" type=\"inventory.aai.onap.org.v11.RelationshipList\" />\n");
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
	
	private void addCustomerNoSubscriberType(StringBuilder sb){
		sb.append("<java-type name=\"Customer\">\n");
		sb.append("<xml-root-element name=\"customer\" />\n");
		sb.append("<java-attributes>\n");
		sb.append("<xml-element java-attribute=\"globalCustomerId\" name=\"global-customer-id\" required=\"true\" type=\"java.lang.String\" xml-key=\"true\">\n");
		sb.append("<xml-properties>\n");
		sb.append("<xml-property name=\"description\" value=\"Global customer id used across to uniquely identify customer.\" />\n");
		sb.append("</xml-properties>\n");
		sb.append("</xml-element>\n");
		sb.append("<xml-element java-attribute=\"subscriberName\" name=\"subscriber-name\" required=\"true\" type=\"java.lang.String\">\n");
		sb.append("<xml-properties>\n");
		sb.append("<xml-property name=\"description\" value=\"Subscriber name, an alternate way to retrieve a customer.\" />\n");
		sb.append("</xml-properties>\n");
		sb.append("</xml-element>\n");
		sb.append("<xml-element java-attribute=\"resourceVersion\" name=\"resource-version\" type=\"java.lang.String\">\n");
		sb.append("<xml-properties>\n");
		sb.append("<xml-property name=\"description\" value=\"Used for optimistic concurrency.  Must be empty on create, valid on update and delete.\" />\n");
		sb.append("</xml-properties>\n");
		sb.append("</xml-element>\n");
		sb.append("<xml-element java-attribute=\"serviceSubscriptions\" name=\"service-subscriptions\" type=\"inventory.aai.onap.org.v11.ServiceSubscriptions\" />\n");
		sb.append("</java-attributes>\n");
		sb.append("<xml-properties>\n");
		sb.append("<xml-property name=\"description\" value=\"customer identifiers to provide linkage back to BSS information.\" />\n");
		sb.append("<xml-property name=\"nameProps\" value=\"subscriber-name\" />\n");
		sb.append("<xml-property name=\"indexedProps\" value=\"subscriber-name,global-customer-id\" />\n");
		sb.append("<xml-property name=\"searchable\" value=\"global-customer-id,subscriber-name\" />\n");
		sb.append("<xml-property name=\"uniqueProps\" value=\"global-customer-id\" />\n");
		sb.append("<xml-property name=\"container\" value=\"customers\" />\n");
		sb.append("<xml-property name=\"namespace\" value=\"business\" />\n");
		sb.append("</xml-properties>\n");
		sb.append("</java-type>\n");		
	}	
	
	private void addCustomerSubscriberType(StringBuilder sb){
		sb.append("<java-type name=\"Customer\">\n");
		sb.append("<xml-root-element name=\"customer\" />\n");
		sb.append("<java-attributes>\n");
		sb.append("<xml-element java-attribute=\"subscriberType\" name=\"subscriber-type\" required=\"true\" type=\"java.lang.String\">\n");
		sb.append("<xml-properties>\n");
		sb.append("<xml-property name=\"description\" value=\"Subscriber type, a way to provide VID with only the INFRA customers.\" />\n");
		sb.append("<xml-property name=\"defaultValue\" value=\"CUST\" />\n");
		sb.append("</xml-properties>\n");
		sb.append("</xml-element>\n");
		sb.append("</java-attributes>\n");
		sb.append("<xml-properties>\n");
		sb.append("<xml-property name=\"indexedProps\" value=\"subscriber-type\" />\n");
		sb.append("<xml-property name=\"container\" value=\"customers\" />\n");
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
		sb.append("<xml-property name=\"description\" value=\"Value defined by orchestration to identify this service.\" />\n");
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
//                sb.append("<xml-element java-attribute=\"relationshipList\" name=\"relationship-list\" type=\"inventory.aai.onap.org.v11.RelationshipList\" />\n");
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
	
	
	private void addEndOfXML(StringBuilder sb){
		sb.append("</java-types>\n");
		sb.append("</xml-bindings>\n");
	}
	
	public void init() throws ParserConfigurationException, SAXException, IOException, AAIException  {
		DocumentBuilder dBuilder = null;
		try {	
		    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		    dbFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		    dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw e;
		}
		try {	
			InputSource isInput = new InputSource(new StringReader(testXML));
			doc = dBuilder.parse(isInput);
		} catch (SAXException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
		
	    NodeList bindingsNodes = doc.getElementsByTagName("xml-bindings");
		Element bindingElement;
		NodeList javaTypesNodes;
		Element javaTypesElement;
		
		if ( bindingsNodes == null || bindingsNodes.getLength() == 0 ) {
			throw new AAIException("OXM file error: missing <binding-nodes> in XML");
		}	    
		
		bindingElement = (Element) bindingsNodes.item(0);
		javaTypesNodes = bindingElement.getElementsByTagName("java-types");
		if ( javaTypesNodes.getLength() < 1 ) {
			throw new AAIException("OXM file error: missing <binding-nodes><java-types> in XML");
		}
		javaTypesElement = (Element) javaTypesNodes.item(0);

		javaTypeNodes = javaTypesElement.getElementsByTagName("java-type");
		if ( javaTypeNodes.getLength() < 1 ) {
			throw new AAIException("OXM file error: missing <binding-nodes><java-types><java-type> in XML");
		}
		logger.debug(testXML);
	}
	@Test
	public void testXSDElement() {
		// repeat of testGetIndexedProps() which uses the constructor
		ArrayList<String> target = new ArrayList<String>();
		target.add("subscriber-name");
		target.add("global-customer-id");
		target.add("subscriber-type");
		target.add("service-type");

		Vector<String> indexedProps = new Vector<String>();
		for ( int i = 0; i < javaTypeNodes.getLength(); ++ i ) {
			XSDElement javaTypeElement = new XSDElement((Element) javaTypeNodes.item(i));
			indexedProps.addAll(javaTypeElement.getIndexedProps());
		}
		assertThat(new ArrayList<>(indexedProps),both(everyItem(is(in(target.toArray())))).and(containsInAnyOrder(target.toArray())));
	}

	@Test
	public void testName() {
		ArrayList<String> target = new ArrayList<String>();
		target.add("ServiceSubscriptions");
		target.add("ServiceSubscription");
		target.add("Inventory");
		target.add("Business");
		target.add("Customers");
		target.add("Customer");
		ArrayList<String> names = new ArrayList<String>();
		for ( int i = 0; i < javaTypeNodes.getLength(); ++ i ) {
			XSDElement javaTypeElement = new XSDElement((Element) javaTypeNodes.item(i));
			names.add(javaTypeElement.name());
		}
		logger.debug(String.join("|", names));
		assertThat(names,both(everyItem(is(in(target.toArray())))).and(containsInAnyOrder(target.toArray())));
	}

	@Test
	public void testGetAddTypes() {
		HashMap<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
		HashMap<String,ArrayList<String>> target = new HashMap<String,ArrayList<String>>();
		target.put("Customer", new ArrayList<>(Arrays.asList("ServiceSubscriptions", "RelationshipList")));
		target.put("Customer", new ArrayList<>(Arrays.asList("ServiceSubscriptions")));
		target.put("Business", new ArrayList<>(Arrays.asList("Customers")));
		target.put("Inventory", new ArrayList<>(Arrays.asList("Business")));
		target.put("Customers", new ArrayList<>(Arrays.asList("Customer")));
		target.put("ServiceSubscription", new ArrayList<>(Arrays.asList("RelationshipList")));
		target.put("ServiceSubscription", new ArrayList<>(Arrays.asList()));
		target.put("ServiceSubscriptions", new ArrayList<>(Arrays.asList("ServiceSubscription")));
		
		for ( int i = 0; i < javaTypeNodes.getLength(); ++i ) {
			XSDElement javaTypeElement = new XSDElement((Element) javaTypeNodes.item(i));
			ArrayList<String> addTypes = new ArrayList<String>();
			NodeList xmlElementNodes = javaTypeElement.getElementsByTagName("xml-element");
			String name=javaTypeElement.name();
			for ( int j = 0; j < xmlElementNodes.getLength(); ++j ) {
				XSDElement xmlElement = new XSDElement((Element) xmlElementNodes.item(j));
				addTypes.addAll(xmlElement.getAddTypes("v11"));
				map.put(name,addTypes);
			}
		}
		for(String key : map.keySet()) {
			assertThat("Expected for key:"+key, map.get(key),equalTo(target.get(key)));		
		}
	}
/*
	@Test
	public void testGetRequiredElements() {
		HashMap<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
		ArrayList<String> target = new ArrayList<String>();
		target.add("global-customer-id\n");
		target.add("subscriber-name\n");
		target.add("subscriber-type");
		for ( int i = 0; i < javaTypeNodes.getLength(); ++i ) {
			XSDElement javaTypeElement = new XSDElement((Element) javaTypeNodes.item(i));
				ArrayList<String> requiredItems = new ArrayList<String>();
				String name=javaTypeElement.name();
				requiredItems.addAll(javaTypeElement.getRequiredElements("v11"));
				map.put(name,requiredItems);
		}
		for(String key : map.keySet()) {
			assertThat(map.get(key),equalTo(target));		
		}
	}
*/
	@Test
	public void testGetPathDescriptionProperty() {
		ArrayList<String> target = new ArrayList<String>();
		target.add("Namespace for business related constructs");
		target.add("Collection of customer identifiers to provide linkage back to BSS information.");
		target.add("customer identifiers to provide linkage back to BSS information.");
		target.add("Collection of objects that group service instances.");
		target.add("Object that group service instances.");
		List<String> descs = new ArrayList<String>();
		for ( int i = 0; i < javaTypeNodes.getLength(); ++ i ) {
			XSDElement javaTypeElement = new XSDElement((Element) javaTypeNodes.item(i));
			if(javaTypeElement.getPathDescriptionProperty() != null)
				descs.add(javaTypeElement.getPathDescriptionProperty());
		}
		logger.debug(String.join("|", descs));
		assertThat(new ArrayList<>(descs),both(everyItem(is(in(target.toArray())))).and(containsInAnyOrder(target.toArray())));
	}

	@Test
	public void testGetIndexedProps() {
		ArrayList<String> target = new ArrayList<String>();
		target.add("subscriber-name");
		target.add("global-customer-id");
		target.add("subscriber-type");
		target.add("service-type");

		Vector<String> indexedProps = new Vector<String>();
		for ( int i = 0; i < javaTypeNodes.getLength(); ++ i ) {
			XSDElement javaTypeElement = new XSDElement((Element) javaTypeNodes.item(i));
			indexedProps.addAll(javaTypeElement.getIndexedProps());
		}
		assertThat(new ArrayList<>(indexedProps),both(everyItem(is(in(target.toArray())))).and(containsInAnyOrder(target.toArray())));
	}

	@Test
	public void testGetContainerProperty() {
		ArrayList<String> target = new ArrayList<String>();
		target.add("service-subscriptions");
		target.add("customers");
		List<String> containers = new ArrayList<String>();
		for ( int i = 0; i < javaTypeNodes.getLength(); ++ i ) {
			XSDElement javaTypeElement = new XSDElement((Element) javaTypeNodes.item(i));
			if(javaTypeElement.getContainerProperty() != null)
				containers.add(javaTypeElement.getContainerProperty());
		}
		logger.debug(String.join("|", containers));
		assertThat(new ArrayList<>(containers),both(everyItem(is(in(target.toArray())))).and(containsInAnyOrder(target.toArray())));
	}

	@Test
	public void testGetQueryParamYAML() {
		ArrayList<String> target = new ArrayList<String>();
		target.add("        - name: global-customer-id\n          in: query\n          description:\n          required: false\n          type: string\n");
		target.add("        - name: subscriber-name\n          in: query\n          description:\n          required: false\n          type: string\n");
		target.add("        - name: subscriber-type\n          in: query\n          description:\n          required: false\n          type: string\n");
		Vector<String> indexedProps = new Vector<String>();
		for ( int i = 0; i < javaTypeNodes.getLength(); ++i ) {
			XSDElement javaTypeElement = new XSDElement((Element) javaTypeNodes.item(i));
			if(javaTypeElement.getContainerProperty() != null) {
				indexedProps.addAll(javaTypeElement.getIndexedProps());
				String container = javaTypeElement.getContainerProperty();
				Vector<String> containerProps = new Vector<String>();
				NodeList xmlElementNodes = javaTypeElement.getElementsByTagName("xml-element");
				for ( int j = 0; j < xmlElementNodes.getLength(); ++j ) {
					XSDElement xmlElement = new XSDElement((Element) xmlElementNodes.item(j));
					if(indexedProps.contains(xmlElement.name()))
						containerProps.add(xmlElement.getQueryParamYAML());
				}
				GetOperation.addContainerProps(container, containerProps);
			}			
		}
/*
		List<String> queryParams = new ArrayList<String>();
		for ( int i = 0; i < javaTypeNodes.getLength(); ++ i ) {
			XSDElement javaTypeElement = new XSDElement((Element) javaTypeNodes.item(i));
			if(javaTypeElement.getQueryParamYAML() != null)
				queryParams.add(javaTypeElement.getQueryParamYAML());
		}
*/
		assertThat(GetOperation.containers.get("customers"),equalTo( target));
	}

	@Test
	public void testGetPathParamYAML() {
		ArrayList<String> target = new ArrayList<String>();
		target.add("        - name: Inventory\n          in: path\n          description: Inventory\n          required: true\n          example: __INVENTORY__\n");
		target.add("        - name: Business\n          in: path\n          description: Business\n          required: true\n          example: __BUSINESS__\n");
		target.add("        - name: Customers\n          in: path\n          description: Customers\n          required: true\n          example: __CUSTOMERS__\n");
		target.add("        - name: Customer\n          in: path\n          description: Customer\n          required: true\n          example: __CUSTOMER__\n");
		target.add("        - name: ServiceSubscriptions\n          in: path\n          description: ServiceSubscriptions\n          required: true\n          example: __SERVICESUBSCRIPTIONS__\n");
		target.add("        - name: ServiceSubscription\n          in: path\n          description: ServiceSubscription\n          required: true\n          example: __SERVICESUBSCRIPTION__\n");
		List<String> pathParams = new ArrayList<String>();
		for ( int i = 0; i < javaTypeNodes.getLength(); ++ i ) {
			XSDElement javaTypeElement = new XSDElement((Element) javaTypeNodes.item(i));
			if(javaTypeElement.getPathParamYAML(javaTypeElement.name()) != null)
				pathParams.add(javaTypeElement.getPathParamYAML(javaTypeElement.name()));
		}
		logger.debug(String.join("|", pathParams));
		assertThat(new ArrayList<>(pathParams),both(everyItem(is(in(target.toArray())))).and(containsInAnyOrder(target.toArray())));
	}

	@Test
	public void testGetHTMLAnnotation() {
		ArrayList<String> target = new ArrayList<String>();
		target.add("  <xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR + "    <xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR + "      <annox:annotate target=\"Business\">@org.onap.aai.annotations.Metadata(description=\"Namespace for business related constructs\")</annox:annotate>" + OxmFileProcessor.LINE_SEPARATOR + "    </xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR + "  </xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
		target.add("  <xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR + "    <xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR + "      <annox:annotate target=\"Customers\">@org.onap.aai.annotations.Metadata(description=\"Collection of customer identifiers to provide linkage back to BSS information.\")</annox:annotate>" + OxmFileProcessor.LINE_SEPARATOR + "    </xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR + "  </xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
		target.add("  <xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR + "    <xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR + "      <annox:annotate target=\"Customer\">@org.onap.aai.annotations.Metadata(description=\"customer identifiers to provide linkage back to BSS information.\",nameProps=\"subscriber-name\",indexedProps=\"subscriber-name,global-customer-id,subscriber-type\",searchable=\"global-customer-id,subscriber-name\",uniqueProps=\"global-customer-id\",container=\"customers\",namespace=\"business\")</annox:annotate>" + OxmFileProcessor.LINE_SEPARATOR + "    </xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR + "  </xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
		target.add("  <xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR + "    <xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR + "      <annox:annotate target=\"ServiceSubscriptions\">@org.onap.aai.annotations.Metadata(description=\"Collection of objects that group service instances.\")</annox:annotate>" + OxmFileProcessor.LINE_SEPARATOR + "    </xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR + "  </xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
		target.add("  <xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR + "    <xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR + "      <annox:annotate target=\"ServiceSubscription\">@org.onap.aai.annotations.Metadata(description=\"Object that group service instances.\",indexedProps=\"service-type\",dependentOn=\"customer\",container=\"service-subscriptions\",crossEntityReference=\"service-instance,service-type\")</annox:annotate>" + OxmFileProcessor.LINE_SEPARATOR + "    </xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR + "  </xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
		List<String> annotes = new ArrayList<String>();
		for ( int i = 0; i < javaTypeNodes.getLength(); ++ i ) {
			XSDElement javaTypeElement = new XSDElement((Element) javaTypeNodes.item(i));
			if(StringUtils.isNotEmpty(javaTypeElement.getHTMLAnnotation(javaTypeElement.name(),"")))
				annotes.add(javaTypeElement.getHTMLAnnotation(javaTypeElement.name(), "  "));
		}
		logger.debug("result:");
		logger.debug(String.join("|", annotes));
		logger.debug("Expected:");
		logger.debug(String.join("|", target));
		assertThat(new ArrayList<>(annotes),both(everyItem(is(in(target.toArray())))).and(containsInAnyOrder(target.toArray())));

	}

	@Test
	public void testGetTypePropertyYAML() {
		ArrayList<String> target = new ArrayList<String>();
		target.add("      Inventory:\n        type: ");
		target.add("      Business:\n        type:         description: Namespace for business related constructs\n");
		target.add("      Customers:\n        type:         description: Collection of customer identifiers to provide linkage back to BSS information.\n");
		target.add("      Customer:\n        type:         description: customer identifiers to provide linkage back to BSS information.\n");
		target.add("      ServiceSubscriptions:\n        type:         description: Collection of objects that group service instances.\n");
		target.add("      ServiceSubscription:\n        type:         description: Object that group service instances.\n");
		List<String> types = new ArrayList<String>();
		for ( int i = 0; i < javaTypeNodes.getLength(); ++ i ) {
			XSDElement javaTypeElement = new XSDElement((Element) javaTypeNodes.item(i));
			if(javaTypeElement.getTypePropertyYAML() != null)
				types.add(javaTypeElement.getTypePropertyYAML());
		}
		assertThat(new ArrayList<>(types),both(everyItem(is(in(target.toArray())))).and(containsInAnyOrder(target.toArray())));
	}

	@Test
	public void testIsStandardType() {
		HashMap<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
		HashMap<String,ArrayList<String>> target = new HashMap<String,ArrayList<String>>();
		target.put("Customer", new ArrayList<>(Arrays.asList("global-customer-id","subscriber-name", "subscriber-type","resource-version")));
		target.put("Business", new ArrayList<>());
		target.put("Inventory", new ArrayList<>());
		target.put("Customers", new ArrayList<>());
		target.put("ServiceSubscriptions", new ArrayList<>());
		target.put("ServiceSubscription", new ArrayList<>(Arrays.asList("service-type", "temp-ub-sub-account-id", "resource-version")));
		
		for ( int i = 0; i < javaTypeNodes.getLength(); ++i ) {
			XSDElement javaTypeElement = new XSDElement((Element) javaTypeNodes.item(i));
			ArrayList<String> addTypes = new ArrayList<String>();
			NodeList xmlElementNodes = javaTypeElement.getElementsByTagName("xml-element");
			String name=javaTypeElement.name();
			for ( int j = 0; j < xmlElementNodes.getLength(); ++j ) {
				XSDElement xmlElement = new XSDElement((Element) xmlElementNodes.item(j));
				if(xmlElement.isStandardType())
					addTypes.add(xmlElement.name());				
			}
			map.put(name,addTypes);
		}
		for(String key : map.keySet()) {
			assertThat(map.get(key),equalTo(target.get(key)));		
		}
	}

}
