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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.edges.EdgeRule;
import org.onap.aai.edges.EdgeRuleQuery;
import org.onap.aai.edges.exceptions.EdgeRuleNotFoundException;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;
import org.onap.aai.nodes.NodeIngestor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.collect.Multimap;

public class NodesYAMLfromOXM extends OxmFileProcessor {
	private static final Logger logger = LoggerFactory.getLogger("GenerateXsd.class");
	private static final String root = "../aai-schema/src/main/resources";
	private static final String autoGenRoot = "aai-schema/src/main/resources";
	private static final String generateTypeYAML = "yaml";
	private static final String normalStartDir = "aai-core";
	private static final String yaml_dir = (((System.getProperty("user.dir") != null) && (!System.getProperty("user.dir").contains(normalStartDir))) ? autoGenRoot : root) + "/aai_swagger_yaml";
	private StringBuilder inventoryDefSb = null;	
	private Map<String,String> operationDefinitions = new HashMap<>();

	private String basePath;

	public NodesYAMLfromOXM(String basePath, SchemaVersions schemaVersions, NodeIngestor ni, EdgeIngestor ei){
		super(schemaVersions, ni,ei);
		this.basePath = basePath;
	}
	public void setOxmVersion(File oxmFile, SchemaVersion v) {
		super.setOxmVersion(oxmFile, v);
	}
	public void setXmlVersion(String xml, SchemaVersion v){
		super.setXmlVersion(xml, v);
	}
	
	public void setVersion(SchemaVersion v) {
		super.setVersion(v);
	}

	@Override
	public String getDocumentHeader() {
		StringBuffer sb = new StringBuffer();
		sb.append("swagger: \"2.0\"\ninfo:\n  ");
		sb.append("description: |");
        if ( versionSupportsSwaggerDiff(v.toString())) {
            sb.append("\n\n    [Differences versus the previous schema version]("+"apidocs/aai_swagger_" + v.toString() + ".diff)");
        }
		sb.append("\n\n    Copyright &copy; 2017-18 AT&amp;T Intellectual Property. All rights reserved.\n\n    Licensed under the Creative Commons License, Attribution 4.0 Intl. (the &quot;License&quot;); you may not use this documentation except in compliance with the License.\n\n    You may obtain a copy of the License at\n\n    (https://creativecommons.org/licenses/by/4.0/)\n\n    Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an &quot;AS IS&quot; BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.\n\n    This document is best viewed with Firefox or Chrome. Nodes can be found by appending /#/definitions/node-type-to-find to the path to this document. Edge definitions can be found with the node definitions.\n  version: \"" + v.toString() +"\"\n");
		sb.append("  title: Active and Available Inventory REST API\n");
		sb.append("  license:\n    name: Apache 2.0\n    url: http://www.apache.org/licenses/LICENSE-2.0.html\n");
		sb.append("  contact:\n    name:\n    url:\n    email:\n");
		sb.append("host:\nbasePath: " + basePath + "/" + v.toString() + "\n");
		sb.append("schemes:\n  - https\npaths:\n");
		return sb.toString();
	}
	
	protected void init() throws ParserConfigurationException, SAXException, IOException, AAIException, FileNotFoundException, EdgeRuleNotFoundException {
		super.init();
	}

	@Override
	public String process() throws ParserConfigurationException, SAXException, IOException, AAIException, FileNotFoundException, EdgeRuleNotFoundException {
		StringBuffer sb = new StringBuffer();
		StringBuffer pathSb = new StringBuffer();
		NodeGetOperation.resetContainers();
		try {
			init();
		} catch(Exception e) {
			logger.error( "Error initializing " + this.getClass());
			throw e;
		}
		pathSb.append(getDocumentHeader());
		StringBuffer definitionsSb = new StringBuffer();
		Element elem;
		String javaTypeName;
		for ( int i = 0; i < javaTypeNodes.getLength(); ++ i ) {
			elem = (Element)javaTypeNodes.item(i);
			javaTypeName = elem.getAttribute("name");
			if ( !"Inventory".equals(javaTypeName ) ) {
				if ( generatedJavaType.containsKey(javaTypeName) ) {
					continue;
				}
				// will combine all matching java-types
				elem = getJavaTypeElementSwagger(javaTypeName );
			}			
			
			XSDElement javaTypeElement = new XSDElement(elem);
			
			
			logger.debug("External: "+javaTypeElement.getAttribute("name")+"/"+getXmlRootElementName(javaTypeName));
			if ( javaTypeName == null ) {
				String msg = "Invalid OXM file: <java-type> has no name attribute in " + oxmFile; 
				logger.error(msg);
				throw new AAIException(msg);
			}
			namespaceFilter.add(getXmlRootElementName(javaTypeName));

			processJavaTypeElementSwagger( javaTypeName, javaTypeElement, pathSb,
				definitionsSb, null, null, null, null, null, null);
		}
		sb.append(pathSb);
//		sb.append(getDocumentHeader());
//		sb.append(totalPathSbAccumulator);
		sb.append(appendOperations());
		sb.append(appendDefinitions());
		PutRelationPathSet prp = new PutRelationPathSet(v);
		prp.generateRelations(ei);
		return sb.toString();
	}
	
	public String appendDefinitions() {
		return appendDefinitions(null);
	}
	
	public String appendDefinitions(Set<String> namespaceFilter) {
		if ( inventoryDefSb != null ) {
			javaTypeDefinitions.put("inventory", inventoryDefSb.toString());
		}			
		StringBuffer sb = new StringBuffer("definitions:\n");
		Map<String, String> sortedJavaTypeDefinitions = new TreeMap<>(javaTypeDefinitions);

		for (Map.Entry<String, String> entry : sortedJavaTypeDefinitions.entrySet()) {
			if(namespaceFilter != null && (! namespaceFilter.contains(entry.getKey()))) {
				continue;
			}
		    sb.append(entry.getValue());
		}
		return sb.toString();
	}
	
	private String processJavaTypeElementSwagger( String javaTypeName, Element javaTypeElement,
			StringBuffer pathSb, StringBuffer definitionsSb, String path, String tag, String opId,
			String getItemName, StringBuffer pathParams, String validEdges) {
		
		String xmlRootElementName = getXMLRootElementName(javaTypeElement);
		StringBuilder definitionsLocalSb = new StringBuilder(256);
		
		String useTag = null;
		String useOpId = null;
		logger.debug("tag="+tag);

		if(tag != null && (! validTag(tag))) {
			logger.debug("tag="+tag+"; javaTypeName="+javaTypeName);
			return null;
		}		
		if ( !javaTypeName.equals("Inventory") ) {
			if ( javaTypeName.equals("AaiInternal"))
				return null;
			if ( opId == null )
				useOpId = javaTypeName;
			else
				useOpId = opId + javaTypeName;
			if ( tag == null )
				useTag = javaTypeName;
		}

		path = xmlRootElementName.equals("inventory") ? "" : (path == null) ? "/" + xmlRootElementName : path + "/" + xmlRootElementName;
		XSDJavaType javaType = new XSDJavaType(javaTypeElement);
		if ( getItemName != null) {
    		if ( getItemName.equals("array") )
    			return javaType.getArrayType();
    		else
    			return javaType.getItemName();
		}
			
		NodeList parentNodes = javaTypeElement.getElementsByTagName("java-attributes");
		if ( parentNodes.getLength() == 0 ) {
			logger.debug( "no java-attributes for java-type " + javaTypeName);
			return "";
		}

		String pathDescriptionProperty = javaType.getPathDescriptionProperty();
		String container = javaType.getContainerProperty();
		Vector<String> indexedProps = javaType.getIndexedProps();
		Vector<String> containerProps = new Vector<String>();
		if(container != null) {
			logger.debug("javaTypeName " + javaTypeName + " container:" + container +" indexedProps:"+indexedProps);
		}
		
		Element parentElement = (Element)parentNodes.item(0);
		NodeList xmlElementNodes = parentElement.getElementsByTagName("xml-element");

		StringBuffer sbParameters = new StringBuffer();
		StringBuffer sbRequired = new StringBuffer();
		int requiredCnt = 0;
		int propertyCnt = 0;
		StringBuffer sbProperties = new StringBuffer();
		
		if ( appliedPaths.containsKey(path)) 
			return null;
		
		StringTokenizer st = new StringTokenizer(path, "/");
		logger.debug("path: " + path + " st? " + st.toString());
		if ( st.countTokens() > 1 && getItemName == null ) {
			logger.debug("appliedPaths: " + appliedPaths + " containsKey? " + appliedPaths.containsKey(path));
			appliedPaths.put(path, xmlRootElementName);
		}
		Vector<String> addTypeV = null;
		for ( int i = 0; i < xmlElementNodes.getLength(); ++i ) {
				XSDElement xmlElementElement = new XSDElement((Element)xmlElementNodes.item(i));
				if ( !xmlElementElement.getParentNode().isSameNode(parentElement))
					continue;
				String elementDescription=xmlElementElement.getPathDescriptionProperty();
				if(getItemName == null) {
					addTypeV = xmlElementElement.getAddTypes(v.toString());
				}
	            if ( "true".equals(xmlElementElement.getAttribute("xml-key"))) {
	            	path += "/{" + xmlElementElement.getAttribute("name") + "}";
	            }
	            logger.debug("path: " + path);
            	logger.debug( "xmlElementElement.getAttribute(required):"+xmlElementElement.getAttribute("required") );
            	            	
				if ( ("true").equals(xmlElementElement.getAttribute("required"))) {
					if ( requiredCnt == 0 )
						sbRequired.append("    required:\n");
					++requiredCnt;
					if ( addTypeV == null || addTypeV.isEmpty()) {
						sbRequired.append("    - " + xmlElementElement.getAttribute("name") + "\n");
					} else { 
						for ( int k = 0; k < addTypeV.size(); ++k ) {
							sbRequired.append("    - " + getXmlRootElementName(addTypeV.elementAt(k)) + ":\n");
						}
					}
				}

				if ( "true".equals(xmlElementElement.getAttribute("xml-key")) )  {
					sbParameters.append(xmlElementElement.getPathParamYAML(elementDescription));
				}
				if (  indexedProps != null
						&& indexedProps.contains(xmlElementElement.getAttribute("name") ) ) {
					containerProps.add(xmlElementElement.getQueryParamYAML());
					NodeGetOperation.addContainerProps(container, containerProps);
				}
			if ( xmlElementElement.isStandardType()) {
				sbProperties.append(xmlElementElement.getTypePropertyYAML());
				++propertyCnt;
			}
			
//			StringBuffer newPathParams = new StringBuffer((pathParams == null ? "" : pathParams.toString())+sbParameters.toString()); //cp8128 don't append the pathParams to sbParameters so that child nodes don't contain the parameters from parent
			StringBuffer newPathParams = new StringBuffer(sbParameters.toString());
	        for ( int k = 0; addTypeV != null && k < addTypeV.size(); ++k ) {
	        	String addType = addTypeV.elementAt(k);
				namespaceFilter.add(getXmlRootElementName(addType));
	        	if ( opId == null || !opId.contains(addType)) {
	        		processJavaTypeElementSwagger( addType, getJavaTypeElementSwagger(addType), 
	    				pathSb, definitionsSb, path,  tag == null ? useTag : tag, useOpId, null,
	    				newPathParams, validEdges);
	        	}
	        	// need item name of array
				String itemName = processJavaTypeElementSwagger( addType, getJavaTypeElementSwagger(addType), 
	    				pathSb, definitionsSb, path,  tag == null ? useTag : tag, useOpId, 
	    						"array", null, null);
					
				if ( itemName != null ) {
					if ( addType.equals("AaiInternal") ) {
						logger.debug( "addType AaiInternal, skip properties");
							
					} else if ( getItemName == null) {
						++propertyCnt;
						sbProperties.append("      " + getXmlRootElementName(addType) + ":\n");
						sbProperties.append("        type: array\n        items:\n");
						sbProperties.append("          $ref: \"#/definitions/" + (itemName == "" ? "aai-internal" : itemName) + "\"\n");
						if ( StringUtils.isNotEmpty(elementDescription) )
							sbProperties.append("        description: " + elementDescription + "\n");
					}
				} else {
					if ( ("java.util.ArrayList").equals(xmlElementElement.getAttribute("container-type"))) {
							// need properties for getXmlRootElementName(addType)
						namespaceFilter.add(getXmlRootElementName(addType));
						if(getXmlRootElementName(addType).equals("service-capabilities"))
						{
							logger.info("arrays: "+ getXmlRootElementName(addType));
						}						
//						newPathParams = new StringBuffer((pathParams == null ? "" : pathParams.toString())+sbParameters.toString()); //cp8128 - change this to not append pathParameters. Just use sbParameters
						newPathParams = new StringBuffer(sbParameters.toString());
						processJavaTypeElementSwagger( addType, getJavaTypeElementSwagger(addType), 
		        				pathSb, definitionsSb, path,  tag == null ? useTag : tag, useOpId, 
		        						null, newPathParams, validEdges);
						sbProperties.append("      " + getXmlRootElementName(addType) + ":\n");
						sbProperties.append("        type: array\n        items:          \n");
						sbProperties.append("          $ref: \"#/definitions/" + getXmlRootElementName(addType) + "\"\n");
						if ( StringUtils.isNotEmpty(elementDescription) )
							sbProperties.append("        description: " + elementDescription + "\n");

					} else {
						//Make sure certain types added to the filter don't appear
						if (nodeFilter.contains(getXmlRootElementName(addType))) {
							;
						} else {
							sbProperties.append("      " + getXmlRootElementName(addType) + ":\n");
							sbProperties.append("        type: object\n");
							sbProperties.append("        $ref: \"#/definitions/" + getXmlRootElementName(addType) + "\"\n");
						}
					}
					if ( StringUtils.isNotEmpty(elementDescription) )
						sbProperties.append("        description: " + elementDescription + "\n");
					++propertyCnt;
				}
	        }
		}
		
		if ( sbParameters.toString().length() > 0 ) {
			if ( pathParams == null )
				pathParams = new StringBuffer();
			pathParams.append(sbParameters);
		}
		if (indexedProps.isEmpty() && containerProps.isEmpty()){
			NodeGetOperation get = new NodeGetOperation(useOpId, xmlRootElementName, tag, path, null);
			String operation = get.toString();
			if(StringUtils.isNotEmpty(operation)) {
				operationDefinitions.put(xmlRootElementName, operation);
			}			
		} else {
			NodeGetOperation get = new NodeGetOperation(useOpId, xmlRootElementName, tag, path,  pathParams == null ? "" : pathParams.toString());
			String operation = get.toString();
			if(StringUtils.isNotEmpty(operation)) {
				operationDefinitions.put(xmlRootElementName, operation);
			}			
		}
	    logger.debug("opId vs useOpId:"+opId+" vs "+useOpId+" PathParams="+pathParams);
		// add PUT
		if ( generatedJavaType.containsKey(xmlRootElementName) ) {
			logger.debug("xmlRootElementName(1)="+xmlRootElementName);
			return null;
		}
		boolean processingInventoryDef = false;
		if ( xmlRootElementName.equals("inventory")) {
			// inventory properties for each oxm to be concatenated
			processingInventoryDef = true;
			if ( inventoryDefSb == null ) {
				inventoryDefSb = new StringBuilder();
				definitionsSb.append("  " + xmlRootElementName + ":\n");
				definitionsLocalSb.append("  " + xmlRootElementName + ":\n");
				definitionsLocalSb.append("    properties:\n");
			}

		} else {
			definitionsSb.append("  " + xmlRootElementName + ":\n");
			definitionsLocalSb.append("  " + xmlRootElementName + ":\n");
		}
		DeleteFootnoteSet footnotes = new DeleteFootnoteSet(xmlRootElementName);
		StringBuffer sbEdge = new StringBuffer();
		LinkedHashSet<String> preventDelete = new LinkedHashSet<String>();
		String prevent=null;
		String nodeCaption = new String("      ###### Related Nodes\n");
		try {
			EdgeRuleQuery q = new EdgeRuleQuery.Builder(xmlRootElementName).version(v).fromOnly().build();
			Multimap<String, EdgeRule> results = ei.getRules(q);
			SortedSet<String> ss=new TreeSet<>(results.keySet());
			sbEdge.append(nodeCaption);
			nodeCaption="";
			for(String key : ss) {
				results.get(key).stream().filter((i) -> (i.getFrom().equals(xmlRootElementName) && (! i.isPrivateEdge()))).forEach((i) ->{ logger.info(new String(new StringBuffer("      - TO ").append(i.getTo()).append(i.getDirection().toString()).append(i.getContains())));} );
				results.get(key).stream().filter((i) -> (i.getFrom().equals(xmlRootElementName) && (! i.isPrivateEdge()))).forEach((i) ->{ sbEdge.append("      - TO "+i.getTo()); EdgeDescription ed = new EdgeDescription(i);  String footnote = ed.getAlsoDeleteFootnote(xmlRootElementName); sbEdge.append(ed.getRelationshipDescription("TO", xmlRootElementName)+footnote+"\n"); if(StringUtils.isNotEmpty(footnote)) footnotes.add(footnote);} );
				results.get(key).stream().filter((i) -> (i.getFrom().equals(xmlRootElementName) && (! i.isPrivateEdge() && i.getPreventDelete().equals("OUT")))).forEach((i) ->{ preventDelete.add(i.getTo().toUpperCase());} );
			}
		} catch(Exception e) {
			logger.debug("xmlRootElementName: "+xmlRootElementName+"\n"+e);
		}
		try {
			EdgeRuleQuery q1 = new EdgeRuleQuery.Builder(xmlRootElementName).version(v).toOnly().build();
			Multimap<String, EdgeRule> results = ei.getRules(q1);
			SortedSet<String> ss=new TreeSet<String>(results.keySet());
			sbEdge.append(nodeCaption);
			for(String key : ss) {
				results.get(key).stream().filter((i) -> (i.getTo().equals(xmlRootElementName) && (! i.isPrivateEdge()))).forEach((i) ->{ sbEdge.append("      - FROM "+i.getFrom()); EdgeDescription ed = new EdgeDescription(i);  String footnote = ed.getAlsoDeleteFootnote(xmlRootElementName); sbEdge.append(ed.getRelationshipDescription("FROM", xmlRootElementName)+footnote+"\n"); if(StringUtils.isNotEmpty(footnote)) footnotes.add(footnote);} );
				results.get(key).stream().filter((i) -> (i.getTo().equals(xmlRootElementName) && (! i.isPrivateEdge()))).forEach((i) ->{ logger.info(new String(new StringBuffer("      - FROM ").append(i.getFrom()).append(i.getDirection().toString()).append(i.getContains())));} );
				results.get(key).stream().filter((i) -> (i.getTo().equals(xmlRootElementName) && (! i.isPrivateEdge() && i.getPreventDelete().equals("IN")))).forEach((i) ->{ preventDelete.add(i.getFrom().toUpperCase());} );
			}
		} catch(Exception e) {
			logger.debug("xmlRootElementName: "+xmlRootElementName+"\n"+e);
		}
		if(preventDelete.size() > 0) {
			prevent = xmlRootElementName.toUpperCase()+" cannot be deleted if related to "+String.join(",",preventDelete);
			logger.debug(prevent);
		}

		if(StringUtils.isNotEmpty(prevent)) {
			footnotes.add(prevent);
		}
		if(footnotes.footnotes.size() > 0) {
			sbEdge.append(footnotes.toString());
		}			
		validEdges = sbEdge.toString();

		// Handle description property.  Might have a description OR valid edges OR both OR neither.
		// Only put a description: tag if there is at least one.
		if (StringUtils.isNotEmpty(pathDescriptionProperty) || StringUtils.isNotEmpty(validEdges) ) {
			definitionsSb.append("    description: |\n");
			definitionsLocalSb.append("    description: |\n");      

			if ( pathDescriptionProperty != null ) {
				definitionsSb.append("      " + pathDescriptionProperty	+ "\n" );
				definitionsLocalSb.append("      " + pathDescriptionProperty	+ "\n" );
			}
			definitionsSb.append(validEdges);
			definitionsLocalSb.append(validEdges);
		}
		
		if ( requiredCnt > 0 ) {
			definitionsSb.append(sbRequired);
			definitionsLocalSb.append(sbRequired);
		}
			
		if ( propertyCnt > 0 ) {
			definitionsSb.append("    properties:\n");
			definitionsSb.append(sbProperties);
			if  ( !processingInventoryDef) {
				definitionsLocalSb.append("    properties:\n");
			}
			definitionsLocalSb.append(sbProperties);
		}
		try {
			namespaceFilter.add(xmlRootElementName);
			if ( xmlRootElementName.equals("inventory") ) {
				//will add to javaTypeDefinitions at end 
				inventoryDefSb.append(definitionsLocalSb.toString());
			} else {
				javaTypeDefinitions.put(xmlRootElementName, definitionsLocalSb.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if ( xmlRootElementName.equals("inventory") ) {
			logger.trace("skip xmlRootElementName(2)="+xmlRootElementName);
			return null;
		}
		generatedJavaType.put(xmlRootElementName, null);
		//Write operations by Namespace(tagName)
/*
		if( validTag(javaTypeName) && javaTypeName == useTag && tag == null) {
			writeYAMLfile("nodes_"+javaTypeName, getDocumentHeader()+pathSb.toString()+appendDefinitions(namespaceFilter));
			totalPathSbAccumulator.append(pathSb);
			pathSb.delete(0, pathSb.length());
			namespaceFilter.clear();
		}
*/
		logger.debug("xmlRootElementName(2)="+xmlRootElementName);
		return null;
	}
	
	private void writeYAMLfile(String outfileName, String fileContent) {
		outfileName = (StringUtils.isEmpty(outfileName)) ? "aai_swagger" : outfileName;
		outfileName = (outfileName.lastIndexOf(File.separator) == -1) ? yaml_dir + File.separator +outfileName+"_" + v.toString() + "." + generateTypeYAML : outfileName;
		File outfile = new File(outfileName);
		File parentDir = outfile.getParentFile();
		if(parentDir != null && ! parentDir.exists()) 
			parentDir.mkdirs();
		try {
			outfile.createNewFile();
		} catch (IOException e) {
			logger.error( "Exception creating output file " + outfileName);
			e.printStackTrace();
		}
		Path path = Paths.get(outfileName);
		Charset charset = Charset.forName("UTF-8");
		try {
			BufferedWriter bw = Files.newBufferedWriter(path, charset);
			bw.write(fileContent);
			if ( bw != null ) {
				bw.close();
			}
		} catch ( IOException e) {
			logger.error( "Exception writing output file " + outfileName);
			e.printStackTrace();
		} 
	}
	
	public boolean validTag(String tag) {
		if(tag != null) {
			switch ( tag ) {
			case "Network":
			case "Search":
			case "Actions":
			case "ServiceDesignAndCreation":
			case "Business":
			case "LicenseManagement":
			case "CloudInfrastructure":
				return true;
			}
		}
		return false;
	}
	
	public String appendOperations() {
		//append definitions
		StringBuffer sb = new StringBuffer();
		Map<String, String> sortedOperationDefinitions = new TreeMap<String, String>(operationDefinitions);
		for (Map.Entry<String, String> entry : sortedOperationDefinitions.entrySet()) {
		    sb.append(entry.getValue());
		}
		return sb.toString();
	}
}

