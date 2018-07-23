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

import org.apache.commons.lang3.StringUtils;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class YAMLfromOXM extends OxmFileProcessor {
	private static final Logger logger = LoggerFactory.getLogger("GenerateXsd.class");
	private static final String root = "../aai-schema/src/main/resources";
	private static final String autoGenRoot = "aai-schema/src/main/resources";
	private static final String generateTypeYAML = "yaml";
	private static final String normalStartDir = "aai-core";
	private static final String yaml_dir = (((System.getProperty("user.dir") != null) && (!System.getProperty("user.dir").contains(normalStartDir))) ? autoGenRoot : root) + "/aai_swagger_yaml";

	private File edgeFile;
	private EdgeRuleSet edgeRuleSet = null;
	public YAMLfromOXM(File oxmFile, Version v, File edgeFile) throws ParserConfigurationException, SAXException, IOException, AAIException, FileNotFoundException {
		super(oxmFile, v);
		this.edgeFile = edgeFile;
		init();
	}
	public YAMLfromOXM(String xml, Version v, File edgeFile) throws ParserConfigurationException, SAXException, IOException, AAIException, FileNotFoundException {
		super(xml, v);
		this.edgeFile = edgeFile;
		init();
	}
	
	@Override
	public String getDocumentHeader() {
		StringBuffer sb = new StringBuffer();
		sb.append("swagger: \"2.0\"\ninfo:\n  ");
		sb.append("description: |");
		sb.append("\n\n    [Differences versus the previous schema version]("+"apidocs/aai_swagger_" + v.name() + ".diff)");
		sb.append("\n\n    Copyright &copy; 2017 AT&amp;T Intellectual Property. All rights reserved.\n\n    Licensed under the Creative Commons License, Attribution 4.0 Intl. (the &quot;License&quot;); you may not use this documentation except in compliance with the License.\n\n    You may obtain a copy of the License at\n\n    (https://creativecommons.org/licenses/by/4.0/)\n\n    Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an &quot;AS IS&quot; BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.\n\n    ECOMP and OpenECOMP are trademarks and service marks of AT&amp;T Intellectual Property.\n\n    This document is best viewed with Firefox or Chrome. Nodes can be found by appending /#/definitions/node-type-to-find to the path to this document. Edge definitions can be found with the node definitions.\n  version: \"" + v.name() +"\"\n");
		sb.append("  title: Active and Available Inventory REST API\n");
		sb.append("  license:\n    name: Apache 2.0\n    url: http://www.apache.org/licenses/LICENSE-2.0.html\n");
		sb.append("  contact:\n    name:\n    url:\n    email:\n");
		sb.append("host:\nbasePath: /aai/" + v.name() + "\n");
		sb.append("schemes:\n  - https\npaths:\n");
		return sb.toString();
	}
	
	protected void init() throws ParserConfigurationException, SAXException, IOException, AAIException, FileNotFoundException {
		super.init();
		edgeRuleSet = new EdgeRuleSet(edgeFile);
	}

	@Override
	public String process() throws AAIException {
		StringBuffer sb = new StringBuffer();
		EdgeRuleSet edgeRuleSet = null;
		try {
			edgeRuleSet = new EdgeRuleSet(edgeFile);
		} catch (Exception e) {
			logger.warn("No valid Edge Rule Set available("+edgeFile+"): "+e.getMessage());
		}

		StringBuffer pathSb = new StringBuffer();
		pathSb.append(getDocumentHeader());
		StringBuffer definitionsSb = new StringBuffer();
		for ( int i = 0; i < javaTypeNodes.getLength(); ++ i ) {
			XSDElement javaTypeElement = new XSDElement((Element)javaTypeNodes.item(i));
			logger.debug("External: "+javaTypeElement.getAttribute("name"));
			String javaTypeName = javaTypeElement.name();
			if ( javaTypeName == null ) {
				String msg = "Invalid OXM file: <java-type> has no name attribute in " + oxmFile; 
				logger.error(msg);
				throw new AAIException(msg);
			}
			//Skip any type that has already been processed(recursion could be the reason)
			if ( generatedJavaType.containsKey(getXmlRootElementName(javaTypeName)) ) {
					continue;
			}
			processJavaTypeElementSwagger( javaTypeName, javaTypeElement, pathSb,
				definitionsSb, null, null, null, null, null, null);
		}
		sb.append(pathSb);

		sb.append(appendDefinitions());
		PutRelationPathSet prp = new PutRelationPathSet(v);
		prp.generateRelations(edgeRuleSet);
		return sb.toString();
	}
	
	public String appendDefinitions() {
		//append definitions
		StringBuffer sb = new StringBuffer("definitions:\n");
		Map<String, String> sortedJavaTypeDefinitions = new TreeMap<String, String>(javaTypeDefinitions);
		for (Map.Entry<String, String> entry : sortedJavaTypeDefinitions.entrySet()) {
		    logger.debug("Key: "+entry.getKey()+"Test: "+ (entry.getKey() == "relationship"));	
		    if(entry.getKey().matches("relationship")) {
			    String jb=entry.getValue();
		    	logger.debug("Value: "+jb);
			    int ndx=jb.indexOf("related-to-property:");
			    if(ndx > 0) {
			    	jb=jb.substring(0, ndx);
			    	jb=jb.replaceAll(" +$", "");
			    }
		    	logger.debug("Value-after: "+jb);
		    	sb.append(jb);
		    	continue;
		    }
		    sb.append(entry.getValue());
		}
		
		sb.append("patchDefinitions:\n");
		for (Map.Entry<String, String> entry : sortedJavaTypeDefinitions.entrySet()) {
		    String jb=entry.getValue().replaceAll("/definitions/", "/patchDefinitions/");
		    int ndx=jb.indexOf("relationship-list:");
		    if(ndx > 0) {
		    	jb=jb.substring(0, ndx);
		    	jb=jb.replaceAll(" +$", "");
		    }
		    int ndx1=jb.indexOf("resource-version:");
			logger.debug("Key: "+entry.getKey()+" index: " + ndx1);		    	
			logger.debug("Value: "+jb);		    	
			if(ndx1 > 0) {
			    jb=jb.substring(0, ndx1);
			    jb=jb.replaceAll(" +$", "");
		    }
			logger.debug("Value-after: "+jb);
		    sb.append(jb);
		}
		    
		sb.append("getDefinitions:\n");
		for (Map.Entry<String, String> entry : sortedJavaTypeDefinitions.entrySet()) {
		    String jb=entry.getValue().replaceAll("/definitions/", "/getDefinitions/");
		    sb.append(jb);
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
		if ( tag != null ) {
			switch ( tag ) {
			case "Network":
			case "ServiceDesignAndCreation":
			case "Business":
			case "LicenseManagement":
			case "CloudInfrastructure":
				break;
			default:
				logger.debug("javaTypeName="+javaTypeName);
				return null;
			}
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
					addTypeV = xmlElementElement.getAddTypes(v.name());
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
					GetOperation.addContainerProps(container, containerProps);
				}
			if ( xmlElementElement.isStandardType()) {
				sbProperties.append(xmlElementElement.getTypePropertyYAML());
				++propertyCnt;
			}
			
			StringBuffer newPathParams = new StringBuffer((pathParams == null ? "" : pathParams.toString())+sbParameters.toString()); 
	        for ( int k = 0; addTypeV != null && k < addTypeV.size(); ++k ) {
	        	String addType = addTypeV.elementAt(k);
				logger.debug("addType: "+ addType);
	        	if ( opId == null || !opId.contains(addType)) {
	        		processJavaTypeElementSwagger( addType, getJavaTypeElementSwagger(addType), 
	    				pathSb, definitionsSb, path,  tag == null ? useTag : tag, useOpId, null,
	    				newPathParams, validEdges);
	        	}
	        	// need item name of array
				String itemName = processJavaTypeElementSwagger( addType, getJavaTypeElementSwagger(addType), 
	    				pathSb, definitionsSb, path,  tag == null ? useTag : tag, useOpId, 
	    						"array", null, null );
					
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
						newPathParams = new StringBuffer((pathParams == null ? "" : pathParams.toString())+sbParameters.toString()); 
						processJavaTypeElementSwagger( addType, getJavaTypeElementSwagger(addType), 
		        				pathSb, definitionsSb, path,  tag == null ? useTag : tag, useOpId, 
		        						null, newPathParams, validEdges );
						sbProperties.append("      " + getXmlRootElementName(addType) + ":\n");
						sbProperties.append("        type: array\n        items:          \n");
						sbProperties.append("          $ref: \"#/definitions/" + getXmlRootElementName(addType) + "\"\n");
						if ( StringUtils.isNotEmpty(elementDescription) )
							sbProperties.append("        description: " + elementDescription + "\n");

					} else {
						if(addType.equals("AaiInternal"))  //Filter out references to AaiInternal
							sbProperties.append("");
						else {
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
		GetOperation get = new GetOperation(useOpId, xmlRootElementName, tag, path,  pathParams == null ? "" : pathParams.toString());
	    pathSb.append(get.toString());
	    logger.debug("opId vs useOpId:"+opId+" vs "+useOpId+" PathParams="+pathParams);
		// add PUT
		PutOperation put = new PutOperation(useOpId, xmlRootElementName, tag, path, pathParams == null ? "" : pathParams.toString(), this.v);
		pathSb.append(put.toString());
		// add PATCH
		PatchOperation patch = new PatchOperation(useOpId, xmlRootElementName, tag, path, pathParams == null ? "" : pathParams.toString());
		pathSb.append(patch.toString());
		// add DELETE
		DeleteOperation del = new DeleteOperation(useOpId, xmlRootElementName, tag, path, pathParams == null ? "" : pathParams.toString());
		pathSb.append(del.toString());
		//Write operations by Namespace(tagName)
//		if(javaTypeName == useTag && tag == null) {
//			pathSb.append(appendDefinitions());
//			writeYAMLfile(javaTypeName, pathSb.toString());
//			pathSb.delete(0, pathSb.length());
//			javaTypeDefinitions.clear();
//			generatedJavaType.clear();
//		}
		if ( generatedJavaType.containsKey(xmlRootElementName) ) {
			logger.debug("xmlRootElementName(1)="+xmlRootElementName);
			return null;
		}
	
		definitionsSb.append("  " + xmlRootElementName + ":\n");
		definitionsLocalSb.append("  " + xmlRootElementName + ":\n");
		Collection<EdgeDescription> edges = edgeRuleSet.getEdgeRules(xmlRootElementName );
		DeleteFootnoteSet footnotes = new DeleteFootnoteSet(xmlRootElementName);
		if ( edges.size() > 0 ) {
			StringBuffer sbEdge = new StringBuffer();
			sbEdge.append("      ###### Related Nodes\n");
			
			for (EdgeDescription ed : edges) {
				if ( ed.getRuleKey().startsWith(xmlRootElementName)) {
				    sbEdge.append("      - TO ").append(ed.getRuleKey().substring(ed.getRuleKey().indexOf("|")+1));
				    String footnote = ed.getAlsoDeleteFootnote(xmlRootElementName);
				    sbEdge.append(ed.getRelationshipDescription("TO", xmlRootElementName)+footnote+"\n");				    
				    if(StringUtils.isNotEmpty(footnote)) footnotes.add(footnote);
				}
			}
			for (EdgeDescription ed : edges) { 
				if ( ed.getRuleKey().endsWith(xmlRootElementName)) {
				    sbEdge.append("      - FROM ").append(ed.getRuleKey().substring(0, ed.getRuleKey().indexOf("|")));
				    String footnote = ed.getAlsoDeleteFootnote(xmlRootElementName);
				    sbEdge.append(ed.getRelationshipDescription("FROM", xmlRootElementName)+footnote+"\n");				    
				    if(StringUtils.isNotEmpty(footnote)) footnotes.add(footnote);
				}
			}
			footnotes.add(edgeRuleSet.preventDeleteRules(xmlRootElementName));
			sbEdge.append(footnotes.toString());
			validEdges = sbEdge.toString();
		}

		// Handle description property.  Might have a description OR valid edges OR both OR neither.
		// Only put a description: tag if there is at least one.
		if (pathDescriptionProperty != null || validEdges != null) {
			definitionsSb.append("    description: |\n");
			definitionsLocalSb.append("    description: |\n");      

			if ( pathDescriptionProperty != null ) {
				definitionsSb.append("      " + pathDescriptionProperty	+ "\n" );
				definitionsLocalSb.append("      " + pathDescriptionProperty	+ "\n" );
			}
			if (validEdges != null) {
				definitionsSb.append(validEdges);
				definitionsLocalSb.append(validEdges);
			}
		}
		
		if ( requiredCnt > 0 ) {
			definitionsSb.append(sbRequired);
			definitionsLocalSb.append(sbRequired);
		}
			
		if ( propertyCnt > 0 ) {
			definitionsSb.append("    properties:\n");
			definitionsSb.append(sbProperties);
			definitionsLocalSb.append("    properties:\n");
			definitionsLocalSb.append(sbProperties);
		}
		try {
			javaTypeDefinitions.put(xmlRootElementName, definitionsLocalSb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		generatedJavaType.put(xmlRootElementName, null);
		logger.trace("xmlRootElementName(2)="+xmlRootElementName);
		return null;
	}
	
	private void writeYAMLfile(String outfileName, String fileContent) {
		outfileName = (StringUtils.isEmpty(outfileName)) ? "aai_swagger" : outfileName;
		outfileName = (outfileName.lastIndexOf(File.separator) == -1) ? yaml_dir + File.separator +outfileName+"_" + v.name() + "." + generateTypeYAML : outfileName;
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

		try(BufferedWriter bw = Files.newBufferedWriter(Paths.get(outfileName),  Charset.forName("UTF-8"))){
			bw.write(fileContent);
		} catch ( IOException e) {
			logger.error( "Exception writing output file " + outfileName);
			e.printStackTrace();
		} 
	}
}
