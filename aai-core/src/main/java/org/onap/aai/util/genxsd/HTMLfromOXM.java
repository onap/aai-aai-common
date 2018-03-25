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

import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class HTMLfromOXM extends OxmFileProcessor {
	private static final Logger logger = LoggerFactory.getLogger("HTMLfromOXM.class");
	
	Version v;
	public HTMLfromOXM(File oxmFile, Version v) throws ParserConfigurationException, SAXException, IOException, AAIException {
		super(oxmFile, v);
		super.init();
		this.v = v;
	}
	public HTMLfromOXM(String xml, Version v) throws ParserConfigurationException, SAXException, IOException, AAIException {
		super(xml, v);
		super.init();
		this.v = v;
	}
	
	@Override
	public String getDocumentHeader() {
		StringBuffer sb = new StringBuffer();
		logger.trace("processing starts");
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
		String namespace = "org.onap";
		if (v.compareTo(Version.v11) < 0 || v.compareTo(Version.v12) < 0) {
			namespace = "org.openecomp";
		}
		if ( versionUsesAnnotations(v.name()) ) {
			sb.append("<xs:schema elementFormDefault=\"qualified\" version=\"1.0\" targetNamespace=\"http://" + namespace + ".aai.inventory/" 
				+ v.name() + "\" xmlns:tns=\"http://" + namespace + ".aai.inventory/" + v.name() + "\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
						+ "\n"
						+ "xmlns:jaxb=\"http://java.sun.com/xml/ns/jaxb\"\r\n" + 
						"    jaxb:version=\"2.1\"\r\n" + 
						"    xmlns:annox=\"http://annox.dev.java.net\"\r\n" + 
						"    jaxb:extensionBindingPrefixes=\"annox\">\n\n");
		} else {
			sb.append("<xs:schema elementFormDefault=\"qualified\" version=\"1.0\" targetNamespace=\"http://" + namespace + ".aai.inventory/" 
					+ v.name() + "\" xmlns:tns=\"http://" + namespace + ".aai.inventory/" + v.name() + "\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n\n");
		}
		return sb.toString();
	}

	@Override
	public String process() throws AAIException {
			StringBuilder sb = new StringBuilder();
			sb.append(getDocumentHeader());
			for ( int i = 0; i < javaTypeNodes.getLength(); ++ i ) {
				XSDElement javaTypeElement = new XSDElement((Element)javaTypeNodes.item(i));
				String javaTypeName = javaTypeElement.name();
				if ( javaTypeName == null ) {
					String msg = "Invalid OXM file: <java-type> has no name attribute in " + oxmFile; 
					logger.error(msg);
					throw new AAIException(msg);
				}
				if ("Nodes".equals(javaTypeName)) {
					logger.debug("skipping Nodes entry (temporary feature)");
					continue;
				}
				//Skip any type that has already been processed(recursion could be the reason)
				logger.debug(getXmlRootElementName(javaTypeName)+" vs "+ javaTypeName+":"+generatedJavaType.containsKey(getXmlRootElementName(javaTypeName)));
				if ( generatedJavaType.containsKey(javaTypeName) ) {
						continue;
				}
				generatedJavaType.put(javaTypeName, null);
				sb.append(processJavaTypeElement( javaTypeName, javaTypeElement ));
			}
		sb.append("</xs:schema>\n");
		return sb.toString();
	}

	public String processJavaTypeElement( String javaTypeName, Element javaType_Element) {
	
		String xmlRootElementName = getXMLRootElementName(javaType_Element);

		NodeList parentNodes = javaType_Element.getElementsByTagName("java-attributes");
		StringBuffer sb = new StringBuffer();
		if ( parentNodes.getLength() == 0 ) {
			logger.trace( "no java-attributes for java-type " + javaTypeName);
			return "";
		}
	
		Element parentElement = (Element)parentNodes.item(0);
		NodeList xmlElementNodes = parentElement.getElementsByTagName("xml-element");
	
		StringBuffer sb1 = new StringBuffer();
		if ( xmlElementNodes.getLength() > 0 ) {
			sb1.append("  <xs:element name=\"" + xmlRootElementName + "\">\n");
			sb1.append("    <xs:complexType>\n");
			XSDElement javaTypeElement = new XSDElement(javaType_Element);
			logger.debug("XSDElement name: "+javaTypeElement.name());
			if(versionUsesAnnotations(v.name())) {
				sb1.append(javaTypeElement.getHTMLAnnotation("class", "      "));
			}
			sb1.append("      <xs:sequence>\n");
			for ( int i = 0; i < xmlElementNodes.getLength(); ++i ) {
			
				XSDElement xmlElementElement = new XSDElement((Element)xmlElementNodes.item(i));

				String elementName = xmlElementElement.getAttribute("name");
				String elementType = xmlElementElement.getAttribute("type");
				//No simple types; only AAI custom types
				String addType = elementType.contains("." + v.name() + ".") ? elementType.substring(elementType.lastIndexOf('.')+1) : null;
    			if ( elementType.contains("." + v.name() + ".") && !generatedJavaType.containsKey(addType) ) {
    				generatedJavaType.put(addType, elementType);
    				sb.append(processJavaTypeElement( addType, getJavaTypeElement(addType) ));	
    			}
        		if ("Nodes".equals(addType)) {
        			logger.trace("Skipping nodes, temporary testing");
        			continue;
        		}
        		//assembles the basic <element> 
        		sb1.append(xmlElementElement.getHTMLElement(v, versionUsesAnnotations(v.name()), this));
			}
		sb1.append("      </xs:sequence>\n");
		sb1.append("    </xs:complexType>\n");
		sb1.append("  </xs:element>\n");
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
		sb.append( sb1 );
		return sb.toString();
	}
	
	private Element getJavaTypeElement( String javaTypeName )
	{		
		String attrName, attrValue;
		Attr attr;
		Element javaTypeElement;
		for ( int i = 0; i < javaTypeNodes.getLength(); ++ i ) {
			javaTypeElement = (Element) javaTypeNodes.item(i);
			NamedNodeMap attributes = javaTypeElement.getAttributes();
			for ( int j = 0; j < attributes.getLength(); ++j ) {
	            attr = (Attr) attributes.item(j);
	            attrName = attr.getNodeName();
	            attrValue = attr.getNodeValue();
	            if ( attrName.equals("name") && attrValue.equals(javaTypeName))
	            	return javaTypeElement;
			}
		}
		logger.error( "oxm file format error, missing java-type " + javaTypeName);
		return (Element) null;
	}

	private boolean versionUsesAnnotations( String version) {
		if (new Integer(version.substring(1)).intValue() >= HTMLfromOXM.annotationsStartVersion ) {
			return true;
		}
		return false;
	}
}