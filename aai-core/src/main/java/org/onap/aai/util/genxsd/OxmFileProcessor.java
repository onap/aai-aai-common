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
import java.io.StringWriter;
import java.util.ArrayList;
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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.xml.sax.InputSource;

public abstract class OxmFileProcessor {

	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	public static final String DOUBLE_LINE_SEPARATOR = System.getProperty("line.separator") + System.getProperty("line.separator");

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

	protected Map combinedJavaTypes;


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

	public Map getCombinedJavaTypes() {
		return combinedJavaTypes;
	}

	public void setCombinedJavaTypes(Map combinedJavaTypes) {
		this.combinedJavaTypes = combinedJavaTypes;
	}

	public Element getJavaTypeElementSwagger( String javaTypeName )
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
	            	combineElementList.add(javaTypeElement);
	            }
			}
		}
		if ( combineElementList.size() == 0 ) {
			return (Element) null;
		} else if ( combineElementList.size() > 1 ) {
			return combineElements( javaTypeName, combineElementList);
		}
		return combineElementList.get(0);
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

	protected void updateParentXmlElements(Element parentElement, NodeList moreXmlElementNodes) {
		Element xmlElement;
		NodeList childNodes;
		Node childNode;

		Node refChild = null;
		// find childNode with attributes and no children, insert children before that node
		childNodes = parentElement.getChildNodes();
		if ( childNodes == null || childNodes.getLength() == 0 ) {
			// should not happen since the base parent was chosen if it had children
			return;
		}

		for ( int i = 0; i < childNodes.getLength(); ++i ) {
			refChild = childNodes.item(i);
			if ( refChild.hasAttributes() && !refChild.hasChildNodes()) {
				break;
			}

		}

		for ( int i = 0; i < moreXmlElementNodes.getLength(); ++i ) {
			xmlElement = (Element)moreXmlElementNodes.item(i);
			childNode = xmlElement.cloneNode(true);
			parentElement.insertBefore(childNode, refChild);
		}
	}

	protected Node getXmlPropertiesNode(Element javaTypeElement ) {
		NodeList nl = javaTypeElement.getChildNodes();
		Node child;
		for ( int i = 0; i < nl.getLength(); ++i ) {
			child = nl.item(i);
			if ( "xml-properties".equals(child.getNodeName())) {
				return child;
			}
		}
		return null;
	}

	protected Node merge( NodeList nl, Node mergeNode ) {
		NamedNodeMap nnm = mergeNode.getAttributes();
		Node childNode;
		NamedNodeMap childNnm;

		String mergeName = nnm.getNamedItem("name").getNodeValue();
		String mergeValue = nnm.getNamedItem("value").getNodeValue();
		String childName;
		String childValue;
		for ( int j = 0; j < nl.getLength(); ++j ) {
			childNode = nl.item(j);
			if ( "xml-property".equals(childNode.getNodeName())) {
				childNnm = childNode.getAttributes();
				childName = childNnm.getNamedItem("name").getNodeValue();
				childValue = childNnm.getNamedItem("value").getNodeValue();
				if ( childName.equals(mergeName)) {
					// attribute exists
					// keep, replace or update
					if ( childValue.contains(mergeValue) ) {
						return null;
					}
					if ( mergeValue.contains(childValue) ) {
						childNnm.getNamedItem("value").setTextContent(mergeValue);
						return null;
					}
					childNnm.getNamedItem("value").setTextContent(mergeValue + "," + childValue);
					return null;
				}
			}
		}
		childNode = mergeNode.cloneNode(true);
		return childNode;
	}

	protected void mergeXmlProperties(Node useChildProperties, NodeList propertiesToMerge ) {
		NodeList nl = useChildProperties.getChildNodes();
		Node childNode;
		Node newNode;
		for ( int i = 0; i < propertiesToMerge.getLength(); ++i ) {
			childNode = propertiesToMerge.item(i);
			if ( "xml-property".equals(childNode.getNodeName()) ) {
				newNode = merge(nl, childNode);
				if ( newNode != null ) {
					useChildProperties.appendChild(newNode);
				}
			}

		}
	}

	protected void combineXmlProperties(int useElement, List<Element> combineElementList) {
		// add or update xml-properties to the referenced element from the combined list
		Element javaTypeElement = combineElementList.get(useElement);
		NodeList nl = javaTypeElement.getChildNodes();
		Node useChildProperties = getXmlPropertiesNode( javaTypeElement);
		int cloneChild = -1;
		Node childProperties;
		if ( useChildProperties == null ) {
			// find xml-properties to clone
			for ( int i = 0; i < combineElementList.size(); ++i ) {
				if ( i == useElement ) {
					continue;
				}
				childProperties = getXmlPropertiesNode(combineElementList.get(i));
				if ( childProperties != null ) {
					useChildProperties = childProperties.cloneNode(true);
					javaTypeElement.appendChild(useChildProperties);
					cloneChild = i;
				}
			}
		}
		NodeList cnl;
		// find other xml-properties
		for ( int i = 0; i < combineElementList.size(); ++i ) {
			if ( i == useElement|| ( cloneChild >= 0 && i <= cloneChild )) {
				continue;
			}
			childProperties = getXmlPropertiesNode(combineElementList.get(i));
			if ( childProperties == null ) {
				continue;
			}
			cnl = childProperties.getChildNodes();
			mergeXmlProperties( useChildProperties, cnl);
		}

	}

	protected Element combineElements( String javaTypeName, List<Element> combineElementList ) {
		Element javaTypeElement;
		NodeList parentNodes;
		Element parentElement = null;
		NodeList xmlElementNodes;

		int useElement = -1;
		if ( combinedJavaTypes.containsKey( javaTypeName) ) {
			return combineElementList.get((int)combinedJavaTypes.get(javaTypeName));
		}
		for ( int i = 0; i < combineElementList.size(); ++i ) {
			javaTypeElement = combineElementList.get(i);
			parentNodes = javaTypeElement.getElementsByTagName("java-attributes");
			if ( parentNodes.getLength() == 0 ) {
				continue;
			}
			parentElement = (Element)parentNodes.item(0);
			xmlElementNodes = parentElement.getElementsByTagName("xml-element");
			if ( xmlElementNodes.getLength() <= 0 ) {
				continue;
			}
			useElement = i;
			break;
		}
		boolean doCombineElements = true;
		if ( useElement < 0 ) {
			useElement = 0;
			doCombineElements = false;
		} else if ( useElement == combineElementList.size() - 1) {
			doCombineElements = false;
		}
		if ( doCombineElements ) {
			// get xml-element from other javaTypeElements
			Element otherParentElement = null;
			for ( int i = 0; i < combineElementList.size(); ++i ) {
				if ( i == useElement ) {
					continue;
				}
				javaTypeElement = combineElementList.get(i);
				parentNodes = javaTypeElement.getElementsByTagName("java-attributes");
				if ( parentNodes.getLength() == 0 ) {
					continue;
				}
				otherParentElement = (Element)parentNodes.item(0);
				xmlElementNodes = otherParentElement.getElementsByTagName("xml-element");
				if ( xmlElementNodes.getLength() <= 0 ) {
					continue;
				}
				// xml-element that are not present
				updateParentXmlElements( parentElement, xmlElementNodes);

			}
		}
		// need to combine xml-properties
		combineXmlProperties(useElement, combineElementList );
		combinedJavaTypes.put( javaTypeName, useElement);
		return combineElementList.get(useElement);
	}


    private static void prettyPrint(Node node, String tab)
	{
    	// for debugging
		try {
			// Set up the output transformer
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(node);
			trans.transform(source, result);
			String xmlString = sw.toString();
			System.out.println(xmlString);
	    }
	    catch (TransformerException e) {
			e.printStackTrace();
	    }
	}
}
