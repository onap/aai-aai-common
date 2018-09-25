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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.onap.aai.config.SpringContextAware;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.edges.exceptions.EdgeRuleNotFoundException;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.base.CaseFormat;

public class HTMLfromOXM extends OxmFileProcessor {

	private static final Logger logger = LoggerFactory.getLogger("HTMLfromOXM.class");
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private String maxOccurs;
	
	public HTMLfromOXM(String maxOccurs, SchemaVersions schemaVersions, NodeIngestor ni, EdgeIngestor ei ){
		super(schemaVersions, ni,ei);
		this.maxOccurs = maxOccurs;
	}
	public void setOxmVersion(File oxmFile, SchemaVersion v) {
		super.setOxmVersion(oxmFile, v);
		this.v = v;
	}
	public void setXmlVersion(String xml, SchemaVersion v) {
		super.setXmlVersion(xml, v);
		this.v = v;
	}
	public void setVersion(SchemaVersion v) {
		super.setVersion(v);
		this.v = v;
	}
	
	
	@Override
	public String getDocumentHeader() {
		StringBuffer sb = new StringBuffer();
		logger.trace("processing starts");
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
		String namespace = "org.onap";
		if (v.compareTo(getSchemaVersions().getNamespaceChangeVersion()) < 0 ) {
			namespace = "org.openecomp";
		}
		if ( versionUsesAnnotations(v.toString()) ) {
			sb.append("<xs:schema elementFormDefault=\"qualified\" version=\"1.0\" targetNamespace=\"http://" + namespace + ".aai.inventory/" 
				+ v.toString() + "\" xmlns:tns=\"http://" + namespace + ".aai.inventory/" + v.toString() + "\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
						+ "\n"
						+ "xmlns:jaxb=\"http://java.sun.com/xml/ns/jaxb\"\r\n" + 
						"    jaxb:version=\"2.1\"\r\n" + 
						"    xmlns:annox=\"http://annox.dev.java.net\"\r\n" + 
						"    jaxb:extensionBindingPrefixes=\"annox\">\n\n");
		} else {
			sb.append("<xs:schema elementFormDefault=\"qualified\" version=\"1.0\" targetNamespace=\"http://" + namespace + ".aai.inventory/" 
					+ v.toString() + "\" xmlns:tns=\"http://" + namespace + ".aai.inventory/" + v.toString() + "\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n\n");
		}
		return sb.toString();
	}
	

	@Override
	public String process() throws ParserConfigurationException, SAXException, IOException, AAIException, FileNotFoundException, EdgeRuleNotFoundException {
		StringBuilder sb = new StringBuilder();
		try {
			init();
		} catch(Exception e) {
			logger.error( "Error initializing " + this.getClass());
			throw e;
		}
		sb.append(getDocumentHeader());
		StringBuilder sbInventory = new StringBuilder();
		Element elem;
		String javaTypeName;
		combinedJavaTypes = new HashMap();
		for ( int i = 0; i < javaTypeNodes.getLength(); ++ i ) {
			elem = (Element)javaTypeNodes.item(i);
			javaTypeName = elem.getAttribute("name");
			if ( !"Inventory".equals(javaTypeName ) ) {
				if ( generatedJavaType.containsKey(javaTypeName) ) {
					continue;
				}
				// will combine all matching java-types
				elem = getJavaTypeElement(javaTypeName,false );
			}
			XSDElement javaTypeElement = new XSDElement(elem, maxOccurs);
			//javaTypeName = javaTypeElement.name();
			if ( javaTypeName == null ) {
				String msg = "Invalid OXM file: <java-type> has no name attribute in " + oxmFile; 
				logger.error(msg);
				throw new AAIException(msg);
			}
			if ("Nodes".equals(javaTypeName)) {
				logger.debug("skipping Nodes entry (temporary feature)");
				continue;
			}
			logger.debug(getXmlRootElementName(javaTypeName)+" vs "+ javaTypeName+":"+generatedJavaType.containsKey(getXmlRootElementName(javaTypeName)));

			if ( !"Inventory".equals(javaTypeName)) {
				generatedJavaType.put(javaTypeName, null);
			}
			sb.append(processJavaTypeElement( javaTypeName, javaTypeElement, sbInventory ));
		}
		sb.append(sbInventory);
		sb.append("      </xs:sequence>\n");
		sb.append("    </xs:complexType>\n");
		sb.append("  </xs:element>\n");			
		sb.append("</xs:schema>\n");
		StringBuilder invalidSb = new StringBuilder();
		return sb.toString();
	}
	
	protected boolean isValidName( String name ) {
		if ( name == null || name.length() == 0 ) {
			return false;
		}
		String pattern = "^[a-z0-9-]*$";
		return name.matches(pattern);
	}

	public String processJavaTypeElement( String javaTypeName, Element javaType_Element, StringBuilder sbInventory) {
		String xmlRootElementName = getXMLRootElementName(javaType_Element);

		NodeList parentNodes = javaType_Element.getElementsByTagName("java-attributes");
		StringBuffer sb = new StringBuffer();
		if ( parentNodes.getLength() == 0 ) {
			logger.trace( "no java-attributes for java-type " + javaTypeName);
			return "";
		}
	
		Element parentElement = (Element)parentNodes.item(0);
		NodeList xmlElementNodes = parentElement.getElementsByTagName("xml-element");
		// support for multiple inventory elements across oxm files
		boolean processingInventory = false;
		boolean hasPreviousInventory = false;
		if ( "inventory".equals(xmlRootElementName) && sbInventory != null ) {
			processingInventory = true;
			if ( sbInventory.toString().contains("xs:complexType") ) {
				hasPreviousInventory = true;
			}
		}
		
		StringBuffer sb1 = new StringBuffer();
		if ( xmlElementNodes.getLength() > 0 ) {

			if ( !processingInventory || !hasPreviousInventory ) {
				sb1.append("  <xs:element name=\"" + xmlRootElementName + "\">\n");
				sb1.append("    <xs:complexType>\n");

				XSDElement javaTypeElement = new XSDElement(javaType_Element, maxOccurs);
				logger.debug("XSDElement name: "+javaTypeElement.name());
				if(versionUsesAnnotations(v.toString())) {
					sb1.append(javaTypeElement.getHTMLAnnotation("class", "      "));
				}
				sb1.append("      <xs:sequence>\n");
			}
			Element javatypeElement;
			for ( int i = 0; i < xmlElementNodes.getLength(); ++i ) {
			
				XSDElement xmlElementElement = new XSDElement((Element)xmlElementNodes.item(i), maxOccurs);

//				String elementName = xmlElementElement.getAttribute("name");
				String elementType = xmlElementElement.getAttribute("type");
				//No simple types; only AAI custom types
				String addType = elementType.contains("." + v.toString() + ".") ? elementType.substring(elementType.lastIndexOf('.')+1) : null;
    			if ( elementType.contains("." + v.toString() + ".") && !generatedJavaType.containsKey(addType) ) {
    				generatedJavaType.put(addType, elementType);
    				javatypeElement = getJavaTypeElement(addType, processingInventory);

    				sb.append(processJavaTypeElement( addType, javatypeElement, null ));	
    			}
        		if ("Nodes".equals(addType)) {
        			logger.trace("Skipping nodes, temporary testing");
        			continue;
        		}
        		//assembles the basic <element> 
        		sb1.append(xmlElementElement.getHTMLElement(v, versionUsesAnnotations(v.toString()), this));
			}
			if ( !processingInventory ) {
				sb1.append("      </xs:sequence>\n");
				sb1.append("    </xs:complexType>\n");
				sb1.append("  </xs:element>\n");
			}
		}
	
		if ( xmlElementNodes.getLength() < 1 ) {
			sb.append("  <xs:element name=\"" + xmlRootElementName + "\">\n");
			sb.append("    <xs:complexType>\n");
			sb.append("      <xs:sequence/>\n");
			sb.append("    </xs:complexType>\n");
			sb.append("  </xs:element>\n");
			generatedJavaType.put(javaTypeName, null);
			return sb.toString();			
		}
		if ( processingInventory && sbInventory != null ) {
			sbInventory.append(sb1);
		} else {
			sb.append( sb1 );
		}
		return sb.toString();
	}
	
	private Element getJavaTypeElement( String javaTypeName, boolean processingInventory )
	{		
		String attrName, attrValue;
		Attr attr;
		Element javaTypeElement;
		
		List<Element> combineElementList = new ArrayList<Element>();
		for ( int i = 0; i < javaTypeNodes.getLength(); ++ i ) {
			javaTypeElement = (Element) javaTypeNodes.item(i);
			NamedNodeMap attributes = javaTypeElement.getAttributes();
			for ( int j = 0; j < attributes.getLength(); ++j ) {
	            attr = (Attr) attributes.item(j);
	            attrName = attr.getNodeName();
	            attrValue = attr.getNodeValue();
	            if ( attrName.equals("name") && attrValue.equals(javaTypeName)) {
	            	if ( processingInventory ) {
	            		return javaTypeElement;
	            	} else {
	            		combineElementList.add(javaTypeElement);
	            	}
	            }
			}
		}
		if ( combineElementList.size() == 0 ) {
			logger.error( "oxm file format error, missing java-type " + javaTypeName);
			return (Element) null;
		} else if ( combineElementList.size() > 1 ) {
			// need to combine java-attributes
			return combineElements( javaTypeName, combineElementList);
		}
		return combineElementList.get(0);
		
	}

	private boolean versionUsesAnnotations( String version) {
		int ver = new Integer(version.substring(1)).intValue();
		if ( ver >= HTMLfromOXM.annotationsStartVersion ) {
			return true;
		}
		if ( ver <= HTMLfromOXM.annotationsMinVersion ) {
			return true;
		}
		return false;
	}
}