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
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public abstract class OxmFileProcessor {	
	protected File oxmFile;
	protected String xml;
	protected Version v;
	protected Document doc = null;
	protected String apiVersion = null;
	protected static int annotationsStartVersion = 9; // minimum version to support annotations in xsd
	protected static int swaggerSupportStartsVersion = 7; // minimum version to support swagger documentation

	protected String apiVersionFmt = null;
	protected HashMap<String, String> generatedJavaType = new HashMap<String, String>();
	protected HashMap<String, String> appliedPaths = new HashMap<String, String>();
	protected NodeList javaTypeNodes = null;
	protected static Map<String,String> javaTypeDefinitions = createJavaTypeDefinitions();
    private static Map<String, String> createJavaTypeDefinitions()
    {
    	StringBuffer aaiInternal = new StringBuffer();
    	StringBuffer nodes = new StringBuffer();
    	Map<String,String> javaTypeDefinitions = new HashMap<String, String>();
    	aaiInternal.append("  aai-internal:\n");
    	aaiInternal.append("    properties:\n");
    	aaiInternal.append("      property-name:\n");
    	aaiInternal.append("        type: string\n");
    	aaiInternal.append("      property-value:\n");
    	aaiInternal.append("        type: string\n");
//    	javaTypeDefinitions.put("aai-internal", aaiInternal.toString());
    	      nodes.append("  nodes:\n");
    	      nodes.append("    properties:\n");
    	      nodes.append("      inventory-item-data:\n");
    	      nodes.append("        type: array\n");
    	      nodes.append("        items:\n");
    	      nodes.append("          $ref: \"#/definitions/inventory-item-data\"\n");
    	javaTypeDefinitions.put("nodes", nodes.toString());
    	return javaTypeDefinitions;
    }


	public OxmFileProcessor(File oxmFile, Version v) {
		super();
		this.oxmFile = oxmFile;
		this.v = v;
	}

	public OxmFileProcessor(String xml, Version v) {
		this.xml = xml;
		this.v = v;
	}
	protected void init() throws ParserConfigurationException, SAXException, IOException, AAIException {
		DocumentBuilder dBuilder = null;
		try {	
		    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		    dbFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		    dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw e;
		}
		try {	
		    if ( xml == null ) {
		    	doc = dBuilder.parse(oxmFile);
		    } else {
			    InputSource isInput = new InputSource(new StringReader(xml));
			    doc = dBuilder.parse(isInput);
		    }
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
			throw new AAIException("OXM file error: missing <binding-nodes> in " + oxmFile);
		}	    
		
		bindingElement = (Element) bindingsNodes.item(0);
		javaTypesNodes = bindingElement.getElementsByTagName("java-types");
		if ( javaTypesNodes.getLength() < 1 ) {
			throw new AAIException("OXM file error: missing <binding-nodes><java-types> in " + oxmFile);
		}
		javaTypesElement = (Element) javaTypesNodes.item(0);

		javaTypeNodes = javaTypesElement.getElementsByTagName("java-type");
		if ( javaTypeNodes.getLength() < 1 ) {
			throw new AAIException("OXM file error: missing <binding-nodes><java-types><java-type> in " + oxmFile );
		}
	}
	public abstract String getDocumentHeader();
	public abstract String process() throws AAIException;
	
	public String getXMLRootElementName(Element javaTypeElement) {
		String xmlRootElementName=null;
		NamedNodeMap attributes;
		
		NodeList valNodes = javaTypeElement.getElementsByTagName("xml-root-element");
		Element valElement = (Element) valNodes.item(0);
		attributes = valElement.getAttributes();
		for ( int i = 0; i < attributes.getLength(); ++i ) {
            Attr attr = (Attr) attributes.item(i);
            String attrName = attr.getNodeName();

            String attrValue = attr.getNodeValue();
            if ( attrName.equals("name"))
            	xmlRootElementName = attrValue;
		}
		return xmlRootElementName;
	}
	
	public String getXmlRootElementName( String javaTypeName )
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
	            if ( attrName.equals("name") && attrValue.equals(javaTypeName)) {
	        		NodeList valNodes = javaTypeElement.getElementsByTagName("xml-root-element");
	        		Element valElement = (Element) valNodes.item(0);
	        		attributes = valElement.getAttributes();
	        		for ( int k = 0; k < attributes.getLength(); ++k ) {
	                    attr = (Attr) attributes.item(k);
	                    attrName = attr.getNodeName();

	                    attrValue = attr.getNodeValue();
	                    if ( attrName.equals("name"))
	                    	return (attrValue);
	        		}
	            }
			}
		}
		return null;
	}
	
	public Element getJavaTypeElementSwagger( String javaTypeName )
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
		return (Element) null;
	}

}
