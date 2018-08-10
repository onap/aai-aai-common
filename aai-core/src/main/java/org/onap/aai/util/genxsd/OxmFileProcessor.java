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
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.edges.exceptions.EdgeRuleNotFoundException;
import org.onap.aai.exceptions.AAIException;

import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;
import org.onap.aai.nodes.NodeIngestor;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.xml.sax.InputSource;

public abstract class OxmFileProcessor {
	EdgeIngestor ei;
	NodeIngestor ni;
	protected Set<String> namespaceFilter;
	protected File oxmFile;
	protected String xml;
	protected SchemaVersion v;
	protected Document doc = null;
	protected String apiVersion = null;
	protected SchemaVersions schemaVersions;
	
	
	protected static int annotationsStartVersion = 9; // minimum version to support annotations in xsd
	protected static int annotationsMinVersion = 6; // lower versions support annotations in xsd
	protected static int swaggerSupportStartsVersion = 1; // minimum version to support swagger documentation
	protected static int swaggerDiffStartVersion = 1; // minimum version to support difference
	protected static int swaggerMinBasepath = 6; // minimum version to support difference
	
	
	protected String apiVersionFmt = null;
	protected HashMap<String, String> generatedJavaType = new HashMap<String, String>();
	protected HashMap<String, String> appliedPaths = new HashMap<String, String>();
	protected NodeList javaTypeNodes = null;

	protected Map<String,String> javaTypeDefinitions = createJavaTypeDefinitions();
    private Map<String, String> createJavaTypeDefinitions()
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
	static List<String> nodeFilter = createNodeFilter();
    private static List<String> createNodeFilter()
    {
    	List<String> list = Arrays.asList("search", "actions", "aai-internal", "nodes");
    	return list;
    }

    public OxmFileProcessor(SchemaVersions schemaVersions, NodeIngestor ni, EdgeIngestor ei){
    	this.schemaVersions = schemaVersions;
    	this.ni = ni;
		this.ei = ei;
	}
    
   

	public void setOxmVersion(File oxmFile, SchemaVersion v) {
		this.oxmFile = oxmFile;
		this.v = v;
	}

	public void setXmlVersion(String xml, SchemaVersion v) {
		this.xml = xml;
		this.v = v;
	}
	
	public void setVersion(SchemaVersion v) {
		this.oxmFile = null;
		this.v = v;
	}
	
	public void setNodeIngestor(NodeIngestor ni) {
	            this.ni = ni;
	}
	
    public void setEdgeIngestor(EdgeIngestor ei) {
            this.ei = ei;
	}

    public SchemaVersions getSchemaVersions() {
		return schemaVersions;
	}

	public void setSchemaVersions(SchemaVersions schemaVersions) {
		this.schemaVersions = schemaVersions;
	}
	
	protected void init() throws ParserConfigurationException, SAXException, IOException, AAIException, EdgeRuleNotFoundException  {
		if(this.xml != null || this.oxmFile != null ) {			
			createDocument();
		}
		if(this.doc == null) {
			this.doc = ni.getSchema(v);
		}
		namespaceFilter = new HashSet<>();
				
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

	private void createDocument() throws ParserConfigurationException, SAXException, IOException, AAIException {
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
		return;
	}
	public abstract String getDocumentHeader();
	public abstract String process() throws ParserConfigurationException, SAXException, IOException, AAIException, FileNotFoundException, EdgeRuleNotFoundException ;
	
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
	
	public boolean versionSupportsSwaggerDiff( String version) {
		int ver = new Integer(version.substring(1)).intValue();
		if ( ver >= HTMLfromOXM.swaggerDiffStartVersion ) {
			return true;
		}
		return false;
	}
	
	public boolean versionSupportsBasePathProperty( String version) {
		int ver = new Integer(version.substring(1)).intValue();
		if ( ver <= HTMLfromOXM.swaggerMinBasepath ) {
			return true;
		}
		return false;
	}

}
