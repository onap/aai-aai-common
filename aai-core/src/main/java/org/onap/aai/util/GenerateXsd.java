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
 *      http://www.apache.org/licenses/LICENSE-2.0
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

import com.google.common.base.Joiner;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

import org.onap.aai.introspection.Version;
import org.onap.aai.serialization.db.EdgeProperty;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateXsd {
	
	private static final Logger logger = LoggerFactory.getLogger("GenerateXsd.class");
	
	static String apiVersion = null;
	static String apiVersionFmt = null;
	static boolean useAnnotationsInXsd = false;
	static String responsesUrl = null;
	static String responsesLabel = null;
	static String jsonEdges = null;

	static Map<String, String> generatedJavaType;
	static Map<String, String> appliedPaths;
	static Map<String, String> deletePaths;
	static Map<String, String> putRelationPaths;
	static NodeList javaTypeNodes;
	static Map<String,String> javaTypeDefinitions = createJavaTypeDefinitions();
    private static Map<String, String> createJavaTypeDefinitions()
    {
    	StringBuffer aaiInternal = new StringBuffer();
    	Map<String,String> javaTypeDefinitions = new HashMap<String, String>();
    	aaiInternal.append("  aai-internal:\n");
    	aaiInternal.append("    properties:\n");
    	aaiInternal.append("      property-name:\n");
    	aaiInternal.append("        type: string\n");
    	aaiInternal.append("      property-value:\n");
    	aaiInternal.append("        type: string\n");
    	javaTypeDefinitions.put("aai-internal", aaiInternal.toString());
    	return javaTypeDefinitions;
    }

	public static final int VALUE_NONE = 0;
	public static final int VALUE_DESCRIPTION = 1;
	public static final int VALUE_INDEXED_PROPS = 2;
	
	private static final String generateTypeXSD = "xsd";
	private static final String generateTypeYAML = "yaml";
	
	private static final String root = "../aai-schema/src/main/resources";
	private static final String xsd_dir = root + "/aai_schema";
	private static final String yaml_dir = root + "/aai_swagger_yaml";
	
	/* These three strings are for yaml auto-generation from aai-common class*/
	private static final String normalStartDir = "aai-core";
	private static final String autoGenRoot = "aai-schema/src/main/resources";
	private static final String alt_yaml_dir = autoGenRoot + "/aai_swagger_yaml";

	private static int annotationsStartVersion = 9; // minimum version to support annotations in xsd
	private static int swaggerSupportStartsVersion = 7; // minimum version to support swagger documentation
	
	private static XPath xpath = XPathFactory.newInstance().newXPath();


	private enum LineageType {
		PARENT, CHILD, UNRELATED;
	}
	private class EdgeDescription {
		
		private String ruleKey;
		private String to;
		private String from;
		private LineageType type = LineageType.UNRELATED;
		private String direction;
		private String multiplicity;
		private String preventDelete;
		private String deleteOtherV;
		private boolean hasDelTarget = false;
		private String label;
		private String description;
		/**
		 * @return the deleteOtherV
		 */
		public String getDeleteOtherV() {
			return deleteOtherV;
		}
		/**
		 * @param deleteOtherV the deleteOtherV to set
		 */
		public void setDeleteOtherV(String deleteOtherV) {
			logger.debug("Edge: "+this.getRuleKey());
			logger.debug("Truth: "+(("${direction}".equals(deleteOtherV)) ? "true" : "false"));
			logger.debug("Truth: "+(("!${direction}".equals(deleteOtherV)) ? "true" : "false"));

			if("${direction}".equals(deleteOtherV) ) {
				this.deleteOtherV = this.direction;
			} else if("!${direction}".equals(deleteOtherV) ) {
				this.deleteOtherV = this.direction.equals("IN") ? "OUT" : ((this.direction.equals("OUT")) ? "IN" : deleteOtherV);
			} else {
				this.deleteOtherV = deleteOtherV;
			}
			logger.debug("DeleteOtherV="+deleteOtherV+"/"+this.direction+"="+this.deleteOtherV);
		}
		/**
		 * @return the preventDelete
		 */
		public String getPreventDelete() {
			return preventDelete;
		}
		/**
		 * @param preventDelete the preventDelete to set
		 */
		public void setPreventDelete(String preventDelete) {
			if(this.getTo().equals("flavor") || this.getFrom().equals("flavor") ){
				logger.debug("Edge: "+this.getRuleKey());
				logger.debug("Truth: "+(("${direction}".equals(preventDelete)) ? "true" : "false"));
				logger.debug("Truth: "+(("!${direction}".equals(preventDelete)) ? "true" : "false"));
			}

			if("${direction}".equals(preventDelete) ) {
				this.preventDelete = this.direction;
			} else if("!${direction}".equals(preventDelete) ) {
				this.preventDelete = this.direction.equals("IN") ? "OUT" : ((this.direction.equals("OUT")) ? "IN" : preventDelete);
			} else {
				this.preventDelete = preventDelete;
			}
			if(this.getTo().equals("flavor") || this.getFrom().equals("flavor")) {
				logger.debug("PreventDelete="+preventDelete+"/"+this.direction+"="+this.preventDelete);
			}
		}
		/**
		 * @return the to
		 */
		public String getTo() {
			return to;
		}
		/**
		 * @param to the to to set
		 */
		public void setTo(String to) {
			this.to = to;
		}
		/**
		 * @return the from
		 */
		public String getFrom() {
			return from;
		}
		/**
		 * @param from the from to set
		 */
		public void setFrom(String from) {
			this.from = from;
		}


		public String getRuleKey() {
			return ruleKey;
		}
		public String getMultiplicity() {
			return multiplicity;
		}
		public String getDirection() {
			return direction;
		}
		public String getDescription() {
			return this.description;
		}
		public void setRuleKey(String val) {
			this.ruleKey=val;
		}
		public void setType(LineageType val) {
			this.type=val;
		}
		public void setDirection(String val) {
			this.direction = val;
		}
		public void setMultiplicity(String val) {
			this.multiplicity=val;
		}
		public void setHasDelTarget(String val) {
			hasDelTarget = Boolean.parseBoolean(val);
		}
		public void setDescription(String val) {
			this.description = val;
		}

		public String getRelationshipDescription(String fromTo, String otherNodeName) {
			
			String result = "";		

			if ("FROM".equals(fromTo)) {
				if ("OUT".equals(direction)) {
					if (LineageType.PARENT == type) {
						result = " (PARENT of "+otherNodeName;
						result = String.join(" ", result+",", this.from, this.getLabel(), this.to);
					} 
				} 
				else {
					if (LineageType.CHILD == type) {
						result = " (CHILD of "+otherNodeName;
						result = String.join(" ", result+",",  this.from, this.getLabel(), this.to);
					} 
					else if (LineageType.PARENT == type) {
						result = " (PARENT of "+otherNodeName;
						result = String.join(" ", result+",", this.from, this.getLabel(), this.to);
					}
				}
				if (result.length() == 0) result = String.join(" ", "(", this.from, this.getLabel(), this.to+",", this.getMultiplicity());
			} else {
			//if ("TO".equals(fromTo)
				if ("OUT".equals(direction)) {
					if (LineageType.PARENT == type) {
						result = " (CHILD of "+otherNodeName;
						result = String.join(" ", result+",", this.from, this.getLabel(), this.to+",", this.getMultiplicity());
					} 
				} else {
					if (LineageType.PARENT == type) {
						result = " (PARENT of "+otherNodeName;
						result = String.join(" ", result+",", this.from, this.getLabel(), this.to+",", this.getMultiplicity());
					}
				}
				if (result.length() == 0) result = String.join(" ", "(", this.from, this.getLabel(), this.to+",", this.getMultiplicity());

			}

			if (hasDelTarget) result = result + ", will delete target node";

			if (result.length() > 0) result = result + ")";
			
			if (description != null && description.length() > 0) result = result + "\n      "+ description; // 6 spaces is important for yaml
			
			return result;
		}

		/**
		 * @return the hasDelTarget
		 */
		@SuppressWarnings("unused")
		public boolean isHasDelTarget() {
			return hasDelTarget;
		}
		/**
		 * @param hasDelTarget the hasDelTarget to set
		 */
		@SuppressWarnings("unused")
		public void setHasDelTarget(boolean hasDelTarget) {
			this.hasDelTarget = hasDelTarget;
		}
		/**
		 * @return the type
		 */
		@SuppressWarnings("unused")
		public LineageType getType() {
			return type;
		}
		/**
		 * @return the label
		 */
		public String getLabel() {
			return label;
		}
		public void setLabel(String string) {
			this.label=string;
		}
	}
	
	private static class PutRelationPathSet {
		String apiPath;
		String opId;
		ArrayList<String> relations = new ArrayList<String>();
		String objectName = "";
		String currentAPIVersion = "";
		public PutRelationPathSet(String opId, String path) {
			super();
			this.apiPath = path.replace("/relationship-list/relationship", "");
			this.opId = opId;
			objectName = GenerateXsd.deletePaths.get(apiPath);
			currentAPIVersion = GenerateXsd.apiVersion;
		}
		public void process() {
			this.toRelations();
			this.fromRelations();
			this.writeRelationsFile();

		}
		public void toRelations() {
			logger.debug("{“comment”: “Valid TO Relations that can be added”},");
			logger.debug("apiPath: "+apiPath+"\nopId="+opId+"\nobjectName="+objectName);
			Collection<EdgeDescription> toEdges = GenerateXsd.getEdgeRulesTO(objectName);
			
			if(toEdges.size() > 0) {
				relations.add("{\"comment\": \"Valid TO Relations that can be added\"}\n");
			}
			for (EdgeDescription ed : toEdges) {
				logger.debug(ed.getRuleKey()+"Type="+ed.type);
				String obj = ed.getRuleKey().replace(objectName,"").replace("|","");
				String selectedRelation = "";
				if(ed.type == LineageType.UNRELATED) {
					String selectObj = getUnrelatedObjectPaths(obj, apiPath);
					logger.debug("SelectedObj:"+selectObj);
					selectedRelation = formatObjectRelationSet(obj,selectObj);
					logger.trace("ObjectRelationSet"+selectedRelation);
				} else {
					String selectObj = getKinObjectPath(obj, apiPath);
					logger.debug("SelectedObj:"+selectObj);
					selectedRelation = formatObjectRelation(obj,selectObj);
					logger.trace("ObjectRelationSet"+selectedRelation);
				}
				relations.add(selectedRelation);
				logger.trace(selectedRelation);
			}
		}
		
		public void fromRelations() {
			logger.debug("“comment”: “Valid FROM Relations that can be added”");
			Collection<EdgeDescription> fromEdges = getEdgeRulesFROM(objectName);
			if(fromEdges.size() > 0) {
				relations.add("{\"comment\": \"Valid FROM Relations that can be added\"}\n");
			}
			for (EdgeDescription ed : fromEdges) {
				logger.debug(ed.getRuleKey()+"Type="+ed.type);
				String obj = ed.getRuleKey().replace(objectName,"").replace("|","");
				String selectedRelation = "";
				if(ed.type == LineageType.UNRELATED) {
					String selectObj = getUnrelatedObjectPaths(obj, apiPath);
					logger.debug("SelectedObj"+selectObj);
					selectedRelation = formatObjectRelationSet(obj,selectObj);
					logger.trace("ObjectRelationSet"+selectedRelation);
				} else {
					String selectObj = getKinObjectPath(obj, apiPath);
					logger.debug("SelectedObj"+selectObj);
					selectedRelation = formatObjectRelation(obj,selectObj);
					logger.trace("ObjectRelationSet"+selectedRelation);
				}
				relations.add(selectedRelation);
				logger.trace(selectedRelation);
			}
		}
		public void writeRelationsFile() {
			File examplefilePath = new File(yaml_dir + "/relations/" + currentAPIVersion+"/"+opId.replace("RelationshipListRelationship", "") + ".json");

			logger.debug(String.join("exampleFilePath: ", examplefilePath.toString()));
			FileOutputStream fop = null;
			try {
				if (!examplefilePath.exists()) {
					examplefilePath.getParentFile().mkdirs();
					examplefilePath.createNewFile();
				}
				fop = new FileOutputStream(examplefilePath);
			} catch(Exception e) {
				e.printStackTrace();
				return;
			}
			try {
				if(relations.size() > 0) {fop.write("[\n".getBytes());}
				fop.write(String.join(",\n", relations).getBytes());
				if(relations.size() > 0) {fop.write("\n]\n".getBytes());}
				fop.flush();
				fop.close();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			logger.debug(String.join(",\n", relations));
			return;
		}
		
		private static String formatObjectRelationSet(String obj, String selectObj) {
			StringBuffer pathSb = new StringBuffer();
			String[] paths = selectObj.split("[|]");
			for (String s: paths) {
				logger.trace("SelectOBJ"+s);
				pathSb.append(formatObjectRelation(obj, s)+",\n");
			}
			pathSb.deleteCharAt(pathSb.length()-2);
			return pathSb.toString();
		}

		private static String formatObjectRelation(String obj, String selectObj) {
			StringBuffer pathSb = new StringBuffer();
			pathSb.append("{\n");
			pathSb.append("\"related-to\" : \""+obj+"\",\n");
			pathSb.append("\"related-link\" : \""+selectObj+"\"\n");
			pathSb.append("}");
			return pathSb.toString();
		}

		private static String getKinObjectPath(String obj, String apiPath) {
			LevenshteinDistance proximity = new LevenshteinDistance();
			String targetPath = "";
			int targetScore = Integer.MAX_VALUE;
			int targetMaxScore = 0;
			for (Map.Entry<String, String> p : deletePaths.entrySet()) {
					if(p.getValue().equals(obj)) {
						targetScore = (targetScore >= proximity.apply(apiPath, p.getKey())) ? proximity.apply(apiPath, p.getKey()) : targetScore;
						targetPath = (targetScore >= proximity.apply(apiPath, p.getKey())) ? p.getKey() : targetPath;
						targetMaxScore = (targetMaxScore <= proximity.apply(apiPath, p.getKey())) ? proximity.apply(apiPath, p.getKey()) : targetScore;
						logger.trace(proximity.apply(apiPath, p.getKey())+":"+p.getKey());
						logger.trace(proximity.apply(apiPath, p.getKey())+":"+apiPath);
					}
			}
			return targetPath;
		}

		private static String getUnrelatedObjectPaths(String obj, String apiPath) {
			String targetPath = "";
			logger.trace("Obj:"+obj +"\n" + apiPath);
			for (Map.Entry<String, String> p : deletePaths.entrySet()) {
					if(p.getValue().equals(obj)) {
						logger.trace("p.getvalue:"+p.getValue()+"p.getkey:"+p.getKey());
						targetPath +=  ((targetPath.length() == 0 ? "" : "|") + p.getKey());
						logger.trace("Match:"+apiPath +"\n" + targetPath);
					}
			}
			return targetPath;
		}
	}
	
	private static class PatchOperation {
		String useOpId;
		String xmlRootElementName;
		String tag;
		String path;
		String pathParams;

		public PatchOperation(String useOpId, String xmlRootElementName, String tag, String path, String pathParams) {
			super();
			this.useOpId = useOpId;
			this.xmlRootElementName = xmlRootElementName;
			this.tag = tag;
			this.path = path;
			this.pathParams = pathParams;
		}

		@Override
		public String toString() {
			StringBuffer pathSb = new StringBuffer();
			pathSb.append("    patch:\n");
			pathSb.append("      tags:\n");
			pathSb.append("        - " + tag + "\n");

			pathSb.append("      summary: update an existing " + xmlRootElementName + "\n");
			pathSb.append("      description: update an existing " + xmlRootElementName + "\n");
			pathSb.append("      operationId: Update" + useOpId + "\n");
			pathSb.append("      consumes:\n");
			pathSb.append("        - application/json\n");
			pathSb.append("        - application/xml\n");					
			pathSb.append("      produces:\n");
			pathSb.append("        - application/json\n");
			pathSb.append("        - application/xml\n");
			pathSb.append("      responses:\n");
			pathSb.append("        \"default\":\n");
			pathSb.append("          " + responsesUrl);
					
			pathSb.append("      parameters:\n");
			pathSb.append(pathParams); // for nesting
			pathSb.append("        - name: body\n");
			pathSb.append("          in: body\n");
			pathSb.append("          description: " + xmlRootElementName + " object that needs to be created or updated\n");
			pathSb.append("          required: true\n");
			pathSb.append("          schema:\n");
			pathSb.append("            $ref: \"patchSchema.yaml#/definitions/" + xmlRootElementName + "\"\n");
		
			return pathSb.toString();
		}
		public String toString1() {
			StringBuffer pathSb = new StringBuffer();
			StringBuffer relationshipExamplesSb = new StringBuffer();
			if ( path.endsWith("/relationship") ) {
				pathSb.append("  " + path + ":\n" );
			}
			pathSb.append("    patch:\n");
			pathSb.append("      tags:\n");
			pathSb.append("        - " + tag + "\n");

			if ( path.endsWith("/relationship") ) {
				pathSb.append("      summary: see node definition for valid relationships\n");
				relationshipExamplesSb.append("[See Examples](apidocs/relations/"+GenerateXsd.apiVersion+"/"+useOpId+".json)");
			} else {
				pathSb.append("      summary: update an existing " + xmlRootElementName + "\n");
				pathSb.append("      description: |\n");
				pathSb.append("        Update an existing " + xmlRootElementName + "\n");
				pathSb.append("        #\n");				
				pathSb.append("        Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.\n");
				pathSb.append("        The PUT operation will entirely replace an existing object.\n"); 
				pathSb.append("        The PATCH operation sends a \"description of changes\" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.\n");				
				pathSb.append("        #\n");				
				pathSb.append("        Other differences between PUT and PATCH are:\n");
				pathSb.append("        #\n");
				pathSb.append("        - For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.\n");
				pathSb.append("        - For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.\n");
				pathSb.append("        - PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.\n");
			}
			pathSb.append("      operationId: Update" + useOpId + "\n");
			pathSb.append("      consumes:\n");
			pathSb.append("        - application/json\n");
			pathSb.append("        - application/xml\n");					
			pathSb.append("      produces:\n");
			pathSb.append("        - application/json\n");
			pathSb.append("        - application/xml\n");
			pathSb.append("      responses:\n");
			pathSb.append("        \"default\":\n");
			pathSb.append("          " + responsesUrl);
			pathSb.append("      parameters:\n");
			pathSb.append(pathParams); // for nesting
			pathSb.append("        - name: body\n");
			pathSb.append("          in: body\n");
			pathSb.append("          description: " + xmlRootElementName + " object that needs to be created or updated. "+relationshipExamplesSb.toString()+"\n");
			pathSb.append("          required: true\n");
			pathSb.append("          schema:\n");
			pathSb.append("            $ref: \"#/patchDefinitions/" + xmlRootElementName + "\"\n");
			return pathSb.toString();
		}		
	}
	private static class PutOperation {
		String useOpId;
		String xmlRootElementName;
		String tag;
		String path;
		String pathParams;
		
		public PutOperation(String useOpId, String xmlRootElementName, String tag, String path, String pathParams) {
			super();
			this.useOpId = useOpId;
			this.xmlRootElementName = xmlRootElementName;
			this.tag = tag;
			this.path = path;
			this.pathParams = pathParams;
		}

		@Override
		public String toString() {
			StringBuffer pathSb = new StringBuffer();
			StringBuffer relationshipExamplesSb = new StringBuffer();
			if ( path.endsWith("/relationship") ) {
				pathSb.append("  " + path + ":\n" );
			}
			pathSb.append("    put:\n");
			pathSb.append("      tags:\n");
			pathSb.append("        - " + tag + "\n");

			if ( path.endsWith("/relationship") ) {
				pathSb.append("      summary: see node definition for valid relationships\n");
			} else {
				pathSb.append("      summary: create or update an existing " + xmlRootElementName + "\n");
				pathSb.append("      description: |\n        Create or update an existing " + xmlRootElementName + ".\n        #\n        Note! This PUT method has a corresponding PATCH method that can be used to update just a few of the fields of an existing object, rather than a full object replacement.  An example can be found in the [PATCH section] below\n");
			}
			relationshipExamplesSb.append("[Valid relationship examples shown here](apidocs/relations/"+GenerateXsd.apiVersion+"/"+useOpId.replace("RelationshipListRelationship", "")+".json)");
			pathSb.append("      operationId: createOrUpdate" + useOpId + "\n");
			pathSb.append("      consumes:\n");
			pathSb.append("        - application/json\n");
			pathSb.append("        - application/xml\n");					
			pathSb.append("      produces:\n");
			pathSb.append("        - application/json\n");
			pathSb.append("        - application/xml\n");
			pathSb.append("      responses:\n");
			pathSb.append("        \"default\":\n");
			pathSb.append("          " + responsesUrl);
		
			pathSb.append("      parameters:\n");
			pathSb.append(pathParams); // for nesting
			pathSb.append("        - name: body\n");
			pathSb.append("          in: body\n");
			pathSb.append("          description: " + xmlRootElementName + " object that needs to be created or updated. "+relationshipExamplesSb.toString()+"\n");
			pathSb.append("          required: true\n");
			pathSb.append("          schema:\n");
			pathSb.append("            $ref: \"#/definitions/" + xmlRootElementName + "\"\n");
			return pathSb.toString();
		}
		public String tagRelationshipPathMapEntry() {
			if ( path.endsWith("/relationship") ) {
				putRelationPaths.put(useOpId, path);
			}
			return "";
		}
		
	}
	
	private static class GetOperation {
		String useOpId;
		String xmlRootElementName;
		String tag;
		String path;
		@SuppressWarnings("unused")
		String pathParams;
		public GetOperation(String useOpId, String xmlRootElementName, String tag, String path, String pathParams) {
			super();
			this.useOpId = useOpId;
			this.xmlRootElementName = xmlRootElementName;
			this.tag = tag;
			this.path = path;
			this.pathParams = pathParams;
		}
		@Override
		public String toString() {
			StringBuffer pathSb = new StringBuffer();
			pathSb.append("  " + path + ":\n" );
			pathSb.append("    get:\n");
			pathSb.append("      tags:\n");
			pathSb.append("        - " + tag + "\n");
			pathSb.append("      summary: returns " + xmlRootElementName + "\n");

			pathSb.append("      description: returns " + xmlRootElementName + "\n");
			pathSb.append("      operationId: get" + useOpId + "\n");
			pathSb.append("      produces:\n");
			pathSb.append("        - application/json\n");
			pathSb.append("        - application/xml\n");
			
			pathSb.append("      responses:\n");
			pathSb.append("        \"200\":\n");
			pathSb.append("          description: successful operation\n");
			pathSb.append("          schema:\n");
			pathSb.append("              $ref: \"#/getDefinitions/" + xmlRootElementName + "\"\n");
			pathSb.append("        \"default\":\n");
			pathSb.append("          " + responsesUrl);

			return pathSb.toString();
		}
		
	}
	private static class DeleteOperation {
		String useOpId;
		String xmlRootElementName;
		String tag;
		String path;
		String pathParams;
		public DeleteOperation(String useOpId, String xmlRootElementName, String tag, String path, String pathParams) {
			super();
			this.useOpId = useOpId;
			this.xmlRootElementName = xmlRootElementName;
			this.tag = tag;
			this.path = path;
			this.pathParams = pathParams;
		}
		@Override
		public String toString() {
			StringBuffer pathSb = new StringBuffer();
			pathSb.append("    delete:\n");
			pathSb.append("      tags:\n");
			pathSb.append("        - " + tag + "\n");
			pathSb.append("      summary: delete an existing " + xmlRootElementName + "\n");
			
			pathSb.append("      description: delete an existing " + xmlRootElementName + "\n");
			
			pathSb.append("      operationId: delete" + useOpId + "\n");
			pathSb.append("      consumes:\n");
			pathSb.append("        - application/json\n");
			pathSb.append("        - application/xml\n");					
			pathSb.append("      produces:\n");
			pathSb.append("        - application/json\n");
			pathSb.append("        - application/xml\n");
			pathSb.append("      responses:\n");
			pathSb.append("        \"default\":\n");
			pathSb.append("          " + responsesUrl);
			pathSb.append("      parameters:\n");

			pathSb.append(pathParams); // for nesting
			if ( !path.endsWith("/relationship") ) {
				pathSb.append("        - name: resource-version\n");

				pathSb.append("          in: query\n");
				pathSb.append("          description: resource-version for concurrency\n");
				pathSb.append("          required: true\n");
				pathSb.append("          type: string\n");
			}
			return pathSb.toString();
		}
		public String objectPathMapEntry() {
			if (! path.endsWith("/relationship") ) {
				deletePaths.put(path, xmlRootElementName);
			}
			return (xmlRootElementName+":"+path);
		}
		
	}
	
	private static boolean validVersion(String versionToGen) {
		
		if ("ALL".equalsIgnoreCase(versionToGen)) {
			return true;
		}
		
		for (Version v : Version.values()) {
	        if (v.name().equals(versionToGen)) {
	            return true;
	        }
	    }

	    return false;
	}
	
	private static boolean versionUsesAnnotations( String version) {
		if (new Integer(version.substring(1)).intValue() >= annotationsStartVersion ) {
			return true;
		}
		return false;
	}
	
	private static boolean versionSupportsSwagger( String version) {
		if (new Integer(version.substring(1)).intValue() >= swaggerSupportStartsVersion ) {
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) throws IOException {
		String versionToGen = System.getProperty("gen_version").toLowerCase();
		String fileTypeToGen = System.getProperty("gen_type").toLowerCase();
		if ( fileTypeToGen == null ) {
			fileTypeToGen = generateTypeXSD;
		}
		
		if ( !fileTypeToGen.equals( generateTypeXSD ) && !fileTypeToGen.equals( generateTypeYAML )) {
			System.err.println("Invalid gen_type passed. " + fileTypeToGen);
			System.exit(1);
		}
		
		
		String responsesLabel = System.getProperty("yamlresponses_url");
		responsesUrl = responsesLabel;
		
		List<Version> versionsToGen = new ArrayList<>();
		if ( versionToGen == null ) {
			System.err.println("Version is required, ie v<n> or ALL.");
			System.exit(1);			
		}
		else if (!"ALL".equalsIgnoreCase(versionToGen) && !versionToGen.matches("v\\d+") && !validVersion(versionToGen)) {
			System.err.println("Invalid version passed. " + versionToGen);
			System.exit(1);
		}
		else if ("ALL".equalsIgnoreCase(versionToGen)) {
			versionsToGen = Arrays.asList(Version.values());
			Collections.sort(versionsToGen);
			Collections.reverse(versionsToGen);
		} else {
			versionsToGen.add(Version.valueOf(versionToGen));
		}
		
		//process file type System property
		fileTypeToGen = (fileTypeToGen == null ? generateTypeXSD : fileTypeToGen.toLowerCase());
		if ( !fileTypeToGen.equals( generateTypeXSD ) && !fileTypeToGen.equals( generateTypeYAML )) {
			System.err.println("Invalid gen_type passed. " + fileTypeToGen);
			System.exit(1);
		} else if ( fileTypeToGen.equals(generateTypeYAML) ) {
			if ( responsesUrl == null || responsesUrl.length() < 1 
					|| responsesLabel == null || responsesLabel.length() < 1 ) {
				System.err.println("generating swagger yaml file requires yamlresponses_url and yamlresponses_label properties" );
				System.exit(1);
			} else {
				responsesUrl = "description: "+ "Response codes found in [response codes]("+responsesLabel+ ").\n";
			}
		}
		String oxmPath;
		if(System.getProperty("user.dir") != null && !System.getProperty("user.dir").contains(normalStartDir)) {
			oxmPath = autoGenRoot + "/oxm/";
		}
		else {
			oxmPath = root + "/oxm/";
		}

		String outfileName;
		File outfile;
		String fileContent;
		
		for (Version v : versionsToGen) {
			apiVersion = v.toString();
			logger.info("Generating " + apiVersion + " " + fileTypeToGen);
			File oxm_file = new File(oxmPath + "aai_oxm_" + apiVersion + ".xml");
			apiVersionFmt = "." + apiVersion + ".";
			generatedJavaType = new HashMap<String, String>();
			appliedPaths = new HashMap<String, String>();
			putRelationPaths = new HashMap<String, String>();
			deletePaths = new HashMap<String, String>();

			if ( fileTypeToGen.equals(generateTypeXSD) ) {
				useAnnotationsInXsd = versionUsesAnnotations(apiVersion);
				outfileName = xsd_dir + "/aai_schema_" + apiVersion + "." + generateTypeXSD;
				fileContent = processOxmFile(oxm_file, v, null);
			} else if ( versionSupportsSwagger(apiVersion )) {
				if(System.getProperty("user.dir") != null && !System.getProperty("user.dir").contains(normalStartDir)) {
					outfileName = alt_yaml_dir;
				}
				else {
					outfileName = yaml_dir;
				}
				outfileName = outfileName + "/aai_swagger_" + apiVersion + "." + generateTypeYAML;
				fileContent = generateSwaggerFromOxmFile( oxm_file, null);
			} else {
				continue;
			}
			outfile = new File(outfileName);
			File parentDir = outfile.getParentFile();
			if(! parentDir.exists()) 
			      parentDir.mkdirs();
		
		    try {
		        outfile.createNewFile();
		    } catch (IOException e) {
	        	logger.error( "Exception creating output file " + outfileName);
	        	e.printStackTrace();
		    }
		    BufferedWriter bw = null;
	        try {
	        	Charset charset = Charset.forName("UTF-8");
	        	Path path = Paths.get(outfileName);
	        	bw = Files.newBufferedWriter(path, charset);
	        	bw.write(fileContent);
	        } catch ( IOException e) {
	        	logger.error( "Exception writing output file " + outfileName);
	        	e.printStackTrace();
	        } finally {
	        	if ( bw != null ) {
	        		bw.close();
	        	}
	        }
			logger.info( "GeneratedXSD successful, saved in " + outfileName);
		}
		
	}


	public static String processJavaTypeElement( String javaTypeName, Element javaTypeElement) {
		
		String xmlRootElementName = null;

		NodeList parentNodes = javaTypeElement.getElementsByTagName("java-attributes");
		StringBuffer sb = new StringBuffer();
		if ( parentNodes.getLength() == 0 ) {
			logger.trace( "no java-attributes for java-type " + javaTypeName);
			return "";

		}
		
		NamedNodeMap attributes;
		
		NodeList valNodes = javaTypeElement.getElementsByTagName("xml-root-element");
		Element valElement = (Element) valNodes.item(0);
		attributes = valElement.getAttributes();
		for ( int i = 0; i < attributes.getLength(); ++i ) {
            Attr attr = (Attr) attributes.item(i);
            String attrName = attr.getNodeName();

            String attrValue = attr.getNodeValue();
            logger.trace("Found xml-root-element attribute: " + attrName + " with value: " + attrValue);
            if ( attrName.equals("name"))
            	xmlRootElementName = attrValue;
		}
		
		Element parentElement = (Element)parentNodes.item(0);
		NodeList xmlElementNodes = parentElement.getElementsByTagName("xml-element");
		NodeList childNodes;
		Element childElement;
		String xmlElementWrapper;

		Element xmlElementElement;
		String addType;
		String elementName, elementType, elementIsKey, elementIsRequired, elementContainerType;
		StringBuffer sb1 = new StringBuffer();
		if ( xmlElementNodes.getLength() > 0 ) {
			sb1.append("  <xs:element name=\"" + xmlRootElementName + "\">\n");
			sb1.append("    <xs:complexType>\n");
			NodeList properties = GenerateXsd.locateXmlProperties(javaTypeElement);
			if (properties != null && useAnnotationsInXsd) {
				logger.trace("properties found for: " + xmlRootElementName);
				sb1.append("      <xs:annotation>\r\n");
				insertAnnotation(properties, false, "class", sb1, "      ");
				
				sb1.append("      </xs:annotation>\r\n");
			} else {
				logger.trace("no properties found for: " + xmlRootElementName);
			}
			sb1.append("      <xs:sequence>\n");
			for ( int i = 0; i < xmlElementNodes.getLength(); ++i ) {
				
				xmlElementElement = (Element)xmlElementNodes.item(i);
				childNodes = xmlElementElement.getElementsByTagName("xml-element-wrapper");
				
				xmlElementWrapper = null;
				if ( childNodes.getLength() > 0 ) {
					childElement = (Element)childNodes.item(0);
					// get name
					attributes = childElement.getAttributes();
					for ( int k = 0; k < attributes.getLength(); ++k ) {
						Attr attr = (Attr) attributes.item(k);
						String attrName = attr.getNodeName();
						String attrValue = attr.getNodeValue();
						if ( attrName.equals("name")) {
							xmlElementWrapper = attrValue;
							logger.trace("found xml-element-wrapper " + xmlElementWrapper);
						}
					}

				}
				attributes = xmlElementElement.getAttributes();
				addType = null;
	
	 
				elementName = elementType = elementIsKey = elementIsRequired = elementContainerType = null;
				for ( int j = 0; j < attributes.getLength(); ++j ) {
		            Attr attr = (Attr) attributes.item(j);
		            String attrName = attr.getNodeName();
	
		            String attrValue = attr.getNodeValue();
		            logger.trace("For " + xmlRootElementName + " Found xml-element attribute: " + attrName + " with value: " + attrValue);
		            if ( attrName.equals("name")) {
		            	elementName = attrValue;
		            }
		            if ( attrName.equals("type")) {
		            	elementType = attrValue;
		            	if ( attrValue.contains(apiVersionFmt) ) {
		            		addType = attrValue.substring(attrValue.lastIndexOf('.')+1);
		            		if ( !generatedJavaType.containsKey(addType) ) {
		            			generatedJavaType.put(addType, attrValue);
		            			sb.append(processJavaTypeElement( addType, getJavaTypeElement(addType) ));	
		            		}
		            	}
		            		
		            }

		            if ( attrName.equals("xml-key")) {
		            	elementIsKey = attrValue;
		            }
		            if ( attrName.equals("required")) {
		            	elementIsRequired = attrValue;
		            }
		            if ( attrName.equals("container-type")) {
		            	elementContainerType = attrValue;
		            }	
				}
	
				if ( xmlElementWrapper != null ) {
					sb1.append("        <xs:element name=\"" + xmlElementWrapper +"\"");
					if ( elementIsRequired == null || !elementIsRequired.equals("true")||addType != null) {	
						sb1.append(" minOccurs=\"0\"");	
					} 
					sb1.append(">\n");
					sb1.append("          <xs:complexType>\n");
					properties = GenerateXsd.locateXmlProperties(javaTypeElement);
					if (properties != null && useAnnotationsInXsd) {
						sb1.append("            <xs:annotation>\r\n");
						insertAnnotation(properties, false, "class", sb1, "            ");
						sb1.append("            </xs:annotation>\r\n");
					} else {
						logger.trace("no properties found for: " + xmlElementWrapper);
					}
					sb1.append("            <xs:sequence>\n");
					sb1.append("      ");
				}
            	if ("Nodes".equals(addType)) {
            		logger.trace("Skipping nodes, temporary testing");
            		continue;
            	}
				if ( addType != null ) {
					sb1.append("        <xs:element ref=\"tns:" + getXmlRootElementName(addType) +"\"");
				} else {
					sb1.append("        <xs:element name=\"" + elementName +"\"");
				}
				if ( elementType.equals("java.lang.String"))
					sb1.append(" type=\"xs:string\"");
				if ( elementType.equals("java.lang.Long"))
					sb1.append(" type=\"xs:unsignedInt\"");
				if ( elementType.equals("java.lang.Integer"))
					sb1.append(" type=\"xs:int\"");
				if ( elementType.equals("java.lang.Boolean"))
					sb1.append(" type=\"xs:boolean\"");
				if ( elementIsRequired == null || !elementIsRequired.equals("true")||addType != null) {	
					sb1.append(" minOccurs=\"0\"");
				} 
				if ( elementContainerType != null && elementContainerType.equals("java.util.ArrayList")) {
					sb1.append(" maxOccurs=\"unbounded\"");
				}
				properties = GenerateXsd.locateXmlProperties(xmlElementElement);
				if (properties != null || elementIsKey != null) {
					sb1.append(">\n");
					if ( useAnnotationsInXsd ) {
						sb1.append("          <xs:annotation>\r\n");
						insertAnnotation(properties, elementIsKey != null, "field", sb1, "          ");
						sb1.append("          </xs:annotation>\r\n");
					}
					if (xmlElementWrapper== null) {
						sb1.append("        </xs:element>\n");
					}
				} else {
					sb1.append("/>\n");
				}
				if ( xmlElementWrapper != null ) {
					sb1.append("            </xs:sequence>\n");
					sb1.append("          </xs:complexType>\n");
					sb1.append("        </xs:element>\n");
				}
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
	
	private static void insertAnnotation(NodeList items, boolean isKey, String target, StringBuffer sb1, String indentation) {
		if (items != null || isKey) {
			List<String> metadata = new ArrayList<>();
			
			String name = "";
			String value = "";
			Element item = null;
			if (isKey) {
				metadata.add("isKey=true");
			}
			if (items != null) {
				for (int i = 0; i < items.getLength(); i++) {
					item = (Element)items.item(i);
					name = item.getAttribute("name");
					value = item.getAttribute("value");
					if (name.equals("abstract")) {
						name = "isAbstract";
					} else if (name.equals("extends")) {
						name = "extendsFrom";
					}
					metadata.add(name + "=\"" + value.replaceAll("&",  "&amp;") + "\"");
				}
			}
			sb1.append(
					indentation + "  <xs:appinfo>\r\n" + 
							indentation + "    <annox:annotate target=\""+target+"\">@org.onap.aai.annotations.Metadata(" + Joiner.on(",").join(metadata) + ")</annox:annotate>\r\n" +
							indentation + "  </xs:appinfo>\r\n");
		}

	}

	private static Element getJavaTypeElement( String javaTypeName )
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
	
	private static Element getJavaTypeElementSwagger( String javaTypeName )
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
	private static String getXmlRootElementName( String javaTypeName )
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
		logger.error( "oxm file format error, missing java-type " + javaTypeName);
		return null;
	}	
	
	
	public static String processOxmFile( File oxmFile, Version v, String xml )
	{
		if ( xml != null ){
		    apiVersion = v.toString();
		    useAnnotationsInXsd = true;
		    apiVersionFmt = "." + apiVersion + ".";
		    generatedJavaType = new HashMap<>();
			appliedPaths = new HashMap<>();
		}
		StringBuilder sb = new StringBuilder();
		logger.trace("processing starts");
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
		String namespace = "org.onap";
		if (v.compareTo(Version.v11) < 0 || v.compareTo(Version.v12) < 0) {
			namespace = "org.openecomp";
		}
		if ( useAnnotationsInXsd ) {
			sb.append("<xs:schema elementFormDefault=\"qualified\" version=\"1.0\" targetNamespace=\"http://" + namespace + ".aai.inventory/" 
				+ apiVersion + "\" xmlns:tns=\"http://" + namespace + ".aai.inventory/" + apiVersion + "\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
						+ "\n"
						+ "xmlns:jaxb=\"http://java.sun.com/xml/ns/jaxb\"\r\n" + 
						"    jaxb:version=\"2.1\" \r\n" + 
						"    xmlns:annox=\"http://annox.dev.java.net\" \r\n" + 
						"    jaxb:extensionBindingPrefixes=\"annox\">\n\n");
		} else {
		
			sb.append("<xs:schema elementFormDefault=\"qualified\" version=\"1.0\" targetNamespace=\"http://" + namespace + ".aai.inventory/" 
					+ apiVersion + "\" xmlns:tns=\"http://" + namespace + ".aai.inventory/" + apiVersion + "\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n\n");
		}

		try {
		    
		    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		    dbFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		    Document doc;
		    
		    if ( xml == null ){
		    	doc = dBuilder.parse(oxmFile);
		    } else {
			    InputSource is = new InputSource(new StringReader(xml));
			    doc = dBuilder.parse(is);
		    } 
		    NodeList bindingsNodes = doc.getElementsByTagName("xml-bindings");
			Element bindingElement;
			NodeList javaTypesNodes;
			Element javaTypesElement;
			
			Element javaTypeElement;

			
			if ( bindingsNodes == null || bindingsNodes.getLength() == 0 ) {
				logger.error( "missing <binding-nodes> in " + oxmFile );
				return null;
			}	    
			
			bindingElement = (Element) bindingsNodes.item(0);
			javaTypesNodes = bindingElement.getElementsByTagName("java-types");
			if ( javaTypesNodes.getLength() < 1 ) {
				logger.error( "missing <binding-nodes><java-types> in " + oxmFile );
				return null;
			}
			javaTypesElement = (Element) javaTypesNodes.item(0);
			javaTypeNodes = javaTypesElement.getElementsByTagName("java-type");
			if ( javaTypeNodes.getLength() < 1 ) {
				logger.error( "missing <binding-nodes><java-types><java-type> in " + oxmFile );
				return null;
			}

			String javaTypeName;
			String attrName, attrValue;
			Attr attr;
			for ( int i = 0; i < javaTypeNodes.getLength(); ++ i ) {
				javaTypeElement = (Element) javaTypeNodes.item(i);
				NamedNodeMap attributes = javaTypeElement.getAttributes();
				javaTypeName = null;
				for ( int j = 0; j < attributes.getLength(); ++j ) {
		            attr = (Attr) attributes.item(j);
		            attrName = attr.getNodeName();
		            attrValue = attr.getNodeValue();
		            if ( attrName.equals("name"))
		            	javaTypeName = attrValue;
				}
				if ( javaTypeName == null ) {
					logger.error( "<java-type> has no name attribute in " + oxmFile );
					return null;
				}
				if ("Nodes".equals(javaTypeName)) {
					logger.debug("skipping Nodes entry (temporary feature)");
					continue;
				}
				if ( !generatedJavaType.containsKey(javaTypeName) ) {
					generatedJavaType.put(javaTypeName, null);
					sb.append(processJavaTypeElement( javaTypeName, javaTypeElement ));
				}
			}
				
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		sb.append("</xs:schema>\n");
		return sb.toString();
	}
	
	public static String toDeleteRules(String objectName) {
		Collection<EdgeDescription> toEdges = GenerateXsd.getEdgeRulesTO(objectName);
		logger.debug("TO Edges count: "+toEdges.size()+" Object: "+objectName);
		String prevent=null;
		String also=null;
		LinkedHashSet<String> preventDelete = new LinkedHashSet<String>();
		LinkedHashSet<String> alsoDelete = new LinkedHashSet<String>();
		for (EdgeDescription ed : toEdges) {
			logger.debug("{“comment”: From = "+ed.getFrom()+" To: "+ed.getTo()+" Object: "+objectName);
			logger.debug("{“comment”: Direction = "+ed.getDirection()+" PreventDelete: "+ed.getPreventDelete()+" DeleteOtherV: "+ed.getDeleteOtherV()+" Object: "+objectName);
			if(ed.getPreventDelete().equals("IN") && ed.getTo().equals(objectName)) {
				preventDelete.add(ed.getFrom().toUpperCase());
			}
			if(ed.getDeleteOtherV().equals("IN") && ed.getTo().equals(objectName) ) {
				alsoDelete.add(ed.getFrom().toUpperCase());
			}
		}
		if(preventDelete.size() > 0) {
			prevent = "      - "+objectName.toUpperCase()+" cannot be deleted if linked to "+String.join(",",preventDelete);
			logger.info(prevent);
		}
		if(alsoDelete.size() > 0) {
			also = "      - "+objectName.toUpperCase()+" is DELETED when these are DELETED "+String.join(",",alsoDelete);
			// This commented out line is better (gets who deletes what correct) but still not accurate.
			//also = "      - Deletion of an instance of "+objectName.toUpperCase()+" causes instances of these directly related types to be DELETED ["+String.join(",",alsoDelete)+"]";
			logger.info(also);
		}
		return String.join((prevent == null || also == null) ? "" : "\n", prevent == null ? "" : prevent, also == null ? "" : also)+((prevent == null && also == null) ? "" : "\n");
	}
	
	public static String fromDeleteRules(String objectName) {
		Collection<EdgeDescription> fromEdges = GenerateXsd.getEdgeRulesFROM(objectName);
		LinkedHashSet<String> preventDelete = new LinkedHashSet <String>();
		LinkedHashSet<String> alsoDelete = new LinkedHashSet <String>();
		String prevent=null;
		String also=null;
		for (EdgeDescription ed : fromEdges) {
			logger.debug("{“comment”: From = "+ed.getFrom()+" To: "+ed.getTo()+" Object: "+objectName);
			logger.debug("{“comment”: Direction = "+ed.getDirection()+" PreventDelete: "+ed.getPreventDelete()+" DeleteOtherV: "+ed.getDeleteOtherV()+" Object: "+objectName);
			if(ed.getPreventDelete().equals("OUT") && ed.getFrom().equals(objectName)) {
				preventDelete.add(ed.getTo().toUpperCase());
			}
			if(ed.getDeleteOtherV().equals("OUT") && ed.getFrom().equals(objectName) ) {
				alsoDelete.add(ed.getTo().toUpperCase());
			}
		}
		if(preventDelete.size() > 0) {
			prevent = "      - "+objectName.toUpperCase()+" cannot be deleted if linked to "+String.join(",",preventDelete);
			logger.info(prevent);
		}
		if(alsoDelete.size() > 0) {
			also = "      - "+objectName.toUpperCase()+" deletion means associated objects of these types are also DELETED:"+String.join(",",alsoDelete);
			// This commented out line is better (gets who deletes what correct) but still not accurate.
			//also = "      - Deletion of an instance of "+objectName.toUpperCase()+" causes instances of these directly related types to be DELETED ["+String.join(",",alsoDelete)+"]";
			logger.info(also);
		}
		return String.join((prevent == null || also == null) ? "" : "\n", prevent == null ? "" : prevent, also == null ? "" : also)+((prevent == null && also == null) ? "" : "\n");
	}


	private static boolean isStandardType( String elementType )
	{
		switch ( elementType ) {
		case "java.lang.String":
		case "java.lang.Long":
		case "java.lang.Integer":
		case"java.lang.Boolean":
			return true;
		}
		return false;
	}
	
	private static Vector<String> getIndexedProps( String attrValue )
	{
		if ( attrValue == null )
			return null;
		StringTokenizer st = new StringTokenizer( attrValue, ",");
		if ( st.countTokens() ==  0 )
			return null;
		Vector<String> result = new Vector<String>();
		while ( st.hasMoreTokens()) {
			result.add(st.nextToken());
		}
		return result;
	}
		
	/**
	 * Guaranteed to at least return non null but empty collection of edge descriptions
	 * @param nodeName name of the vertex whose edge relationships to return
	 * @return collection of node neighbors based on DbEdgeRules
	**/
	private static Collection<EdgeDescription> getEdgeRulesFromJson( String path, boolean skipMatch ) 
	{

		ArrayList<EdgeDescription> result = new ArrayList<>();
		Iterator<Map<String, Object>> edgeRulesIterator;
		try {

			GenerateXsd x = new GenerateXsd();
			
			List<Map<String, Object>> inEdges = JsonPath.parse(jsonEdges).read(path);
			
			edgeRulesIterator = inEdges.iterator();
			Map<String, Object> edgeMap;
			String fromNode;
			String toNode;
			String direction;
			String multiplicity;
			String isParent;
			String hasDelTarget;
			String deleteOtherV;
			String preventDelete;
			String description;
			EdgeDescription edgeDes;
			
			while( edgeRulesIterator.hasNext() ){
				edgeMap = edgeRulesIterator.next();
				fromNode = (String)edgeMap.get("from");
				toNode = (String)edgeMap.get("to");
				if ( skipMatch ) { 
					if ( fromNode.equals(toNode)) {
						continue;
					}
				}
				edgeDes = x.new EdgeDescription();
				edgeDes.setRuleKey(fromNode + "|" + toNode);
				edgeDes.setLabel((String)edgeMap.get("label"));
				edgeDes.setTo((String)edgeMap.get("to"));
				edgeDes.setFrom((String)edgeMap.get("from"));
				direction = (String)edgeMap.get("direction");
				edgeDes.setDirection(direction);
				multiplicity = (String)edgeMap.get("multiplicity");
				edgeDes.setMultiplicity(multiplicity);
				isParent = (String)edgeMap.get(EdgeProperty.CONTAINS.toString());
				if ( "${direction}".equals(isParent))  {
					edgeDes.setType(LineageType.PARENT);
				} else {
					edgeDes.setType(LineageType.UNRELATED);
				}
				hasDelTarget = (String)edgeMap.get(EdgeProperty.DELETE_OTHER_V.toString());
				deleteOtherV = (String)edgeMap.get(EdgeProperty.DELETE_OTHER_V.toString());
				edgeDes.setDeleteOtherV(deleteOtherV);
				edgeDes.setHasDelTarget(hasDelTarget);
				preventDelete = (String)edgeMap.get(EdgeProperty.PREVENT_DELETE.toString());
				edgeDes.setPreventDelete(preventDelete);
				description = (String)edgeMap.get(EdgeProperty.DESCRIPTION.toString());
				edgeDes.setDescription(description);
				
				result.add(edgeDes);
				logger.debug("Edge: "+edgeDes.getRuleKey());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
		
	}
	
	/**
	 * Guaranteed to at least return non null but empty collection of edge descriptions
	 * @param nodeName name of the vertex whose edge relationships to return
	 * @return collection of node neighbors based on DbEdgeRules
	**/
	private static Collection<EdgeDescription> getEdgeRules( String nodeName ) 
	{		
		String fromRulesPath = "$['rules'][?(@['from']=='" + nodeName + "')]";
		String toRulesPath = "$['rules'][?(@['to']=='" + nodeName + "')]";
		Collection<EdgeDescription> fromEdges = getEdgeRulesFromJson( fromRulesPath, false );
		Collection<EdgeDescription> edges = getEdgeRulesFromJson( toRulesPath, true );
		edges.addAll(fromEdges);
		return edges;
	}
	
	private static Collection<EdgeDescription> getEdgeRulesTO( String nodeName ) 
	{		
		String toRulesPath = "$['rules'][?(@['to']=='" + nodeName + "')]";
		Collection<EdgeDescription> edges = getEdgeRulesFromJson( toRulesPath, true );
		return edges;
	}
	
	private static Collection<EdgeDescription> getEdgeRulesFROM( String nodeName ) 
	{		
		String fromRulesPath = "$['rules'][?(@['from']=='" + nodeName + "')]";
		Collection<EdgeDescription> edges = getEdgeRulesFromJson( fromRulesPath, true );
		return edges;
	}	
	public static String processJavaTypeElementSwagger( String javaTypeName, Element javaTypeElement,
			StringBuffer pathSb, StringBuffer definitionsSb, String path, String tag, String opId,
			String getItemName, StringBuffer pathParams, String queryParams, String validEdges) {
		
		String xmlRootElementName = null;
		StringBuilder definitionsLocalSb = new StringBuilder(256);
		
		String useTag = null;
		String useOpId = null;
		
		if ( tag != null ) {
			switch ( tag ) {
			case "Network":
			case "ServiceDesignAndCreation":
			case "Business":
			case "LicenseManagement":
			case "CloudInfrastructure":
				break;
			default:
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
		
		NodeList parentNodes = javaTypeElement.getElementsByTagName("java-attributes");

		if ( parentNodes.getLength() == 0 ) {
			logger.trace( "no java-attributes for java-type " + javaTypeName);
			return "";
		}
		
		NamedNodeMap attributes;
		
		NodeList valNodes = javaTypeElement.getElementsByTagName("xml-root-element");
		Element valElement = (Element) valNodes.item(0);
		attributes = valElement.getAttributes();
		for ( int i = 0; i < attributes.getLength(); ++i ) {
            Attr attr = (Attr) attributes.item(i);
            String attrName = attr.getNodeName();

            String attrValue = attr.getNodeValue();
            logger.trace("Found xml-root-element attribute: " + attrName + " with value: " + attrValue);
            if ( attrName.equals("name"))
            	xmlRootElementName = attrValue;
		}

		NodeList childNodes;
		Element childElement;
		NodeList xmlPropNodes = javaTypeElement.getElementsByTagName("xml-properties");
		Element xmlPropElement;
		String pathDescriptionProperty = null;
		
		
		Vector<String> indexedProps = null;
		
		if ( xmlPropNodes.getLength() > 0 ) {

			for ( int i = 0; i < xmlPropNodes.getLength(); ++i ) {
				xmlPropElement = (Element)xmlPropNodes.item(i);
				if ( !xmlPropElement.getParentNode().isSameNode(javaTypeElement))
					continue;
				childNodes = xmlPropElement.getElementsByTagName("xml-property");
				
				if ( childNodes.getLength() > 0 ) {
					for ( int j = 0; j < childNodes.getLength(); ++j ) {
						childElement = (Element)childNodes.item(j);
						// get name
						int useValue = VALUE_NONE;
						attributes = childElement.getAttributes();
						for ( int k = 0; k < attributes.getLength(); ++k ) {
							Attr attr = (Attr) attributes.item(k);
							String attrName = attr.getNodeName();
							String attrValue = attr.getNodeValue();
							if ( attrName == null || attrValue == null )
								continue;
							if ( attrName.equals("name") && attrValue.equals("description")) {
								useValue = VALUE_DESCRIPTION;
							}
							if ( useValue == VALUE_DESCRIPTION && attrName.equals("value")) {
								pathDescriptionProperty = attrValue;
							}
							if ( attrValue.equals("indexedProps")) {
								useValue = VALUE_INDEXED_PROPS;
							}
							if ( useValue == VALUE_INDEXED_PROPS && attrName.equals("value")) {
								indexedProps = getIndexedProps( attrValue );
							}
						}
					}
				}
			}
		}
		logger.trace("javaTypeName " + javaTypeName + " description " + pathDescriptionProperty);
		
		Element parentElement = (Element)parentNodes.item(0);
		NodeList xmlElementNodes = parentElement.getElementsByTagName("xml-element");

	
		String attrDescription = null;

		Element xmlElementElement;
		String addType = null;
		String elementType = null, elementIsKey = null, elementIsRequired, elementContainerType = null;
		String elementName = null;
		StringBuffer sbParameters = new StringBuffer();

		StringBuffer sbRequired = new StringBuffer();
		int requiredCnt = 0;
		int propertyCnt = 0;
		StringBuffer sbProperties = new StringBuffer();
		StringBuffer sbIndexedParams = new StringBuffer();

		
		StringTokenizer st;
		if ( xmlRootElementName.equals("inventory"))
			path = "";
		else if ( path == null )
			path = "/" + xmlRootElementName;
		else
			path += "/" + xmlRootElementName;
		st = new StringTokenizer(path, "/");
		boolean genPath = false;

		if ( st.countTokens() > 1 && getItemName == null ) {
			if ( appliedPaths.containsKey(path)) 
				return null;
			appliedPaths.put(path, xmlRootElementName);
			genPath = true;
			if ( path.contains("/relationship/") ) { // filter paths with relationship-list
				genPath = false;
			}
			if ( path.endsWith("/relationship-list")) {
				genPath = false;
			}
					
		}
		
		Vector<String> addTypeV = null;
		if (  xmlElementNodes.getLength() > 0 ) {
			
			for ( int i = 0; i < xmlElementNodes.getLength(); ++i ) {
				xmlElementElement = (Element)xmlElementNodes.item(i);
				if ( !xmlElementElement.getParentNode().isSameNode(parentElement))
					continue;

				valNodes = xmlElementElement.getElementsByTagName("xml-properties");
				attrDescription = null;
				if ( valNodes.getLength() > 0 ) {
					for ( int j = 0; j < valNodes.getLength(); ++j ) {
						valElement = (Element)valNodes.item(j);
						if ( !valElement.getParentNode().isSameNode(xmlElementElement))
							continue;
						childNodes = valElement.getElementsByTagName("xml-property");
						if ( childNodes.getLength() > 0 ) {
							childElement = (Element)childNodes.item(0);
							// get name
							attributes = childElement.getAttributes();
							attrDescription = null;
							boolean useValue = false;
							for ( int k = 0; k < attributes.getLength(); ++k ) {
								Attr attr = (Attr) attributes.item(k);
								String attrName = attr.getNodeName();
								String attrValue = attr.getNodeValue();
								if ( attrName.equals("name") && attrValue.equals("description")) {
									useValue = true;
								}
								if ( useValue && attrName.equals("value")) {
									attrDescription = attrValue;
								}
							}

						}
					}
				}
				
				attributes = xmlElementElement.getAttributes();
				addTypeV = null; // vector of 1
				addType = null;
				
				elementName = elementType = elementIsKey = elementIsRequired = elementContainerType = null;
				for ( int j = 0; j < attributes.getLength(); ++j ) {
		            Attr attr = (Attr) attributes.item(j);
		            String attrName = attr.getNodeName();
	
		            String attrValue = attr.getNodeValue();
		            logger.trace("For " + xmlRootElementName + " Found xml-element attribute: " + attrName + " with value: " + attrValue);
		            if ( attrName.equals("name")) {
		            	elementName = attrValue;

		            }
		            if ( attrName.equals("type") && getItemName == null ) {
		            	elementType = attrValue;
		            	if ( attrValue.contains(apiVersionFmt) ) {
		            		addType = attrValue.substring(attrValue.lastIndexOf('.')+1);
		            		if ( addTypeV == null ) 
		            			addTypeV = new Vector<String>();
		            		addTypeV.add(addType);
		            	}
		            		
		            }
		            if ( attrName.equals("xml-key")) {
		            	elementIsKey = attrValue;
		            	path += "/{" + elementName + "}";
		            }
		            if ( attrName.equals("required")) {
		            	elementIsRequired = attrValue;
		            }
		            if ( attrName.equals("container-type")) {
		            	elementContainerType = attrValue;
		            }	
				}
            	if ( getItemName != null ) {
            		if ( getItemName.equals("array") ) {
            			if ( elementContainerType != null && elementContainerType.equals("java.util.ArrayList")) {
            				logger.trace( " returning array " + elementName );
            				return elementName;
            			}
            			
            		} else { // not an array check
            			if ( elementContainerType == null || !elementContainerType.equals("java.util.ArrayList")) {
            				logger.trace( " returning object " + elementName );
            				return elementName;
            			}
            			
            		}
            		logger.trace( " returning null" );
            		return null;
            	}
				if ( elementIsRequired != null ) {
					if ( requiredCnt == 0 )
						sbRequired.append("    required:\n");
					++requiredCnt;
					if ( addTypeV != null ) {
						for ( int k = 0; k < addTypeV.size(); ++i ) {
							sbRequired.append("    - " + getXmlRootElementName(addTypeV.elementAt(k)) + ":\n");
						}
					} else 
						sbRequired.append("    - " + elementName + "\n");

				}

				if (  elementIsKey != null )  {
					sbParameters.append(("        - name: " + elementName + "\n"));
					sbParameters.append(("          in: path\n"));
					if ( attrDescription != null && attrDescription.length() > 0 )
						sbParameters.append(("          description: " + attrDescription + "\n"));
					sbParameters.append(("          required: true\n"));
					if ( elementType.equals("java.lang.String"))
						sbParameters.append("          type: string\n");
					if ( elementType.equals("java.lang.Long")) {
						sbParameters.append("          type: integer\n");
						sbParameters.append("          format: int64\n");
					}
					if ( elementType.equals("java.lang.Integer")) {
						sbParameters.append("          type: integer\n");
						sbParameters.append("          format: int32\n");
					}
					if ( elementType.equals("java.lang.Boolean")) {
						sbParameters.append("          type: boolean\n");
					}
					if(StringUtils.isNotBlank(elementName)) {
						sbParameters.append("          example: "+"__"+elementName.toUpperCase()+"__"+"\n");
					}
				} else if (  indexedProps != null
						&& indexedProps.contains(elementName ) ) {
					sbIndexedParams.append(("        - name: " + elementName + "\n"));
					sbIndexedParams.append(("          in: query\n"));
					if ( attrDescription != null && attrDescription.length() > 0 )
						sbIndexedParams.append(("          description: " + attrDescription + "\n"));
					sbIndexedParams.append(("          required: false\n"));
					if ( elementType.equals("java.lang.String"))
						sbIndexedParams.append("          type: string\n");
					if ( elementType.equals("java.lang.Long")) {
						sbIndexedParams.append("          type: integer\n");
						sbIndexedParams.append("          format: int64\n");
					}
					if ( elementType.equals("java.lang.Integer")) {
						sbIndexedParams.append("          type: integer\n");
						sbIndexedParams.append("          format: int32\n");
					}
					if ( elementType.equals("java.lang.Boolean"))
						sbIndexedParams.append("          type: boolean\n");
				}
			if ( isStandardType(elementType)) {
				sbProperties.append("      " + elementName + ":\n");
				++propertyCnt;
				sbProperties.append("        type: ");

				if ( elementType.equals("java.lang.String"))
					sbProperties.append("string\n");
				else if ( elementType.equals("java.lang.Long")) {
					sbProperties.append("integer\n");
					sbProperties.append("        format: int64\n");
				}
				else if ( elementType.equals("java.lang.Integer")){
					sbProperties.append("integer\n");
					sbProperties.append("        format: int32\n");
				}
				else if ( elementType.equals("java.lang.Boolean"))
					sbProperties.append("boolean\n");
				if ( attrDescription != null && attrDescription.length() > 0 )
					sbProperties.append("        description: " + attrDescription + "\n");
			}

	        if ( addTypeV !=  null ) {
	    		StringBuffer newPathParams = null;
	    		if ( pathParams != null  ) {
	    			newPathParams = new StringBuffer();
	    			newPathParams.append(pathParams);
	    		}
	            if ( sbParameters.toString().length() > 0 ) {
					if ( newPathParams == null )
						newPathParams = new StringBuffer();
					newPathParams.append(sbParameters);
	            }
	            String newQueryParams = null;
	            if ( sbIndexedParams.toString().length() > 0 ) {
	            	if ( queryParams == null )
	            		newQueryParams = sbIndexedParams.toString();
	            	else
	            		newQueryParams = queryParams + sbIndexedParams.toString();
	            } else {
	            	newQueryParams = queryParams;
	            }
	        	for ( int k = 0; k < addTypeV.size(); ++k ) {
	        		addType = addTypeV.elementAt(k);
	        
	        		if ( opId == null || !opId.contains(addType)) {
	        			processJavaTypeElementSwagger( addType, getJavaTypeElementSwagger(addType), 
	    					pathSb, definitionsSb, path,  tag == null ? useTag : tag, useOpId, null,
	    					newPathParams, newQueryParams, validEdges);
	        		}
	        		// need item name of array
					String itemName = processJavaTypeElementSwagger( addType, getJavaTypeElementSwagger(addType), 
	    					pathSb, definitionsSb, path,  tag == null ? useTag : tag, useOpId, 
	    							"array", null, null, null );
					
					if ( itemName != null ) {
						if ( addType.equals("AaiInternal") ) {
							logger.debug( "addType AaiInternal, skip properties");
							
						} else if ( getItemName == null) {
							++propertyCnt;
							sbProperties.append("      " + getXmlRootElementName(addType) + ":\n");
							sbProperties.append("        type: array\n        items:\n");
							sbProperties.append("          $ref: \"#/definitions/" + (itemName == "" ? "aai-internal" : itemName) + "\"\n");
							if ( attrDescription != null && attrDescription.length() > 0 )
								sbProperties.append("        description: " + attrDescription + "\n");
						}
					} else {
						if ( elementContainerType != null && elementContainerType.equals("java.util.ArrayList")) {
							// need properties for getXmlRootElementName(addType)
				    		newPathParams = null;
				    		if ( pathParams != null  ) {
				    			newPathParams = new StringBuffer();
				    			newPathParams.append(pathParams);
				    		}
				            if ( sbParameters.toString().length() > 0 ) {
								if ( newPathParams == null )
									newPathParams = new StringBuffer();
								newPathParams.append(sbParameters);
				            }
				            newQueryParams = null;
				            if ( sbIndexedParams.toString().length() > 0 ) {
				            	if ( queryParams == null )
				            		newQueryParams = sbIndexedParams.toString();
				            	else
				            		newQueryParams = queryParams + sbIndexedParams.toString();
				            } else {
				            	newQueryParams = queryParams;
				            }
							processJavaTypeElementSwagger( addType, getJavaTypeElementSwagger(addType), 
		        					pathSb, definitionsSb, path,  tag == null ? useTag : tag, useOpId, 
		        							null, newPathParams, newQueryParams, validEdges );
							sbProperties.append("      " + getXmlRootElementName(addType) + ":\n");
							sbProperties.append("        type: array\n        items:          \n");
							sbProperties.append("          $ref: \"#/definitions/" + getXmlRootElementName(addType) + "\"\n");
						} else {
							sbProperties.append("      " + getXmlRootElementName(addType) + ":\n");
							sbProperties.append("        type: object\n");
							sbProperties.append("        $ref: \"#/definitions/" + getXmlRootElementName(addType) + "\"\n");
						}
						if ( attrDescription != null && attrDescription.length() > 0 )
							sbProperties.append("        description: " + attrDescription + "\n");
						++propertyCnt;
					}
	        	}
	        }
		}
	}	
		if ( genPath ) {
			if ( !path.endsWith("/relationship") ) {
				GetOperation get = new GetOperation(useOpId, xmlRootElementName, tag, path, pathParams == null ? "" : pathParams.toString());
				pathSb.append(get.toString());
//				if ( path.indexOf('{') > 0 ) {
		    		
		            if ( sbParameters.toString().length() > 0 ) {
						if ( pathParams == null )
							pathParams = new StringBuffer();
						pathParams.append(sbParameters);
		            }
					if ( pathParams != null) {
						pathSb.append("      parameters:\n");
						pathSb.append(pathParams);
					} else
						logger.trace( "null pathParams for " + useOpId);
					if ( sbIndexedParams.toString().length() > 0 ) {
						if ( queryParams == null )
							queryParams = sbIndexedParams.toString();
						else
							queryParams = queryParams + sbIndexedParams.toString();
					}
					if ( queryParams != null ) {
						if ( pathParams == null ) {
							pathSb.append("      parameters:\n");
						}
						pathSb.append(queryParams);
					}
//				}
			}
			boolean skipPutDelete = false; // no put or delete for "all" 
			if ( !path.endsWith("/relationship") ) {				
				if ( !path.endsWith("}") ){
						skipPutDelete = true;
				}
					
			}
			if ( path.indexOf('{') > 0 && !opId.startsWith("Search") &&!skipPutDelete) {
				// add PUT
				PutOperation put = new PutOperation(useOpId, xmlRootElementName, tag, path, pathParams == null ? "" : pathParams.toString());
				pathSb.append(put.toString());
				if ( !path.endsWith("/relationship") ) {
					PatchOperation patch = new PatchOperation(useOpId, xmlRootElementName, tag, path, pathParams == null ? "" : pathParams.toString());
					pathSb.append(patch.toString1());
				}
				logger.debug(put.tagRelationshipPathMapEntry());

				// add DELETE
				DeleteOperation del = new DeleteOperation(useOpId, xmlRootElementName, tag, path, pathParams == null ? "" : pathParams.toString());
				pathSb.append(del.toString());
				logger.debug(del.objectPathMapEntry());
			}
			
		}
		if ( generatedJavaType.containsKey(xmlRootElementName) ) {
			return null;
		}
	
		definitionsSb.append("  " + xmlRootElementName + ":\n");
		definitionsLocalSb.append("  " + xmlRootElementName + ":\n");
		Collection<EdgeDescription> edges = getEdgeRules(xmlRootElementName );
		
		if ( edges.size() > 0 ) {
			StringBuffer sbEdge = new StringBuffer();
			sbEdge.append("      ###### Related Nodes\n");
			for (EdgeDescription ed : edges) {
				if ( ed.getRuleKey().startsWith(xmlRootElementName)) {
				    sbEdge.append("      - TO ").append(ed.getRuleKey().substring(ed.getRuleKey().indexOf("|")+1));
				    sbEdge.append(ed.getRelationshipDescription("TO", xmlRootElementName));
				    sbEdge.append("\n");
				}
			}
			for (EdgeDescription ed : edges) { 
				if ( ed.getRuleKey().endsWith(xmlRootElementName)) {
				    sbEdge.append("      - FROM ").append(ed.getRuleKey().substring(0, ed.getRuleKey().indexOf("|")));
				    sbEdge.append(ed.getRelationshipDescription("FROM", xmlRootElementName));
				    sbEdge.append("\n");
				}
			}
			// Delete rule processing is incorrect.  One cannot express the delete rules in isolation from the
			// specific edge.  Take the case of allotted-resource and service-instance.  When the service-instance owns the
			// allotted-resource, yes, it deletes it.  But when the service-instance only uses the allotted-resource, the deletion
			// of the service instance does not cause the deletion of the allotted-resource.
			// I put some lines into the toDeleteRules and fromDeleteRules to correct things to an extent, but it's still
			// not right.
			sbEdge.append(toDeleteRules(xmlRootElementName));
			sbEdge.append(fromDeleteRules(xmlRootElementName));
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
		return null;
	}
	
	public static void generateRelations() {
		if(putRelationPaths == null)
			return;
		putRelationPaths.forEach((k,v)->{
			logger.trace("k="+k+"\n"+"v="+v+v.equals("/business/customers/customer/{global-customer-id}/service-subscriptions/service-subscription/{service-type}/service-instances/service-instance/{service-instance-id}/allotted-resources/allotted-resource/{id}/relationship-list/relationship"));
			logger.debug("apiPath(Operation): "+v);
			logger.debug("Target object: "+v.replace("/relationship-list/relationship", ""));
			logger.debug("Relations: ");
			PutRelationPathSet prp = new PutRelationPathSet(k, v);
			prp.process();
		});
	}

	public static String generateSwaggerFromOxmFile( File oxmFile, String xml )
	{
		if ( xml != null ){
			apiVersion = Version.getLatest().toString();
		    apiVersionFmt = "." + apiVersion + ".";
		    generatedJavaType = new HashMap<>();
			appliedPaths = new HashMap<>();
			responsesUrl = "Description: response-label\n";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("swagger: \"2.0\"\ninfo:\n  ");
		sb.append("description: |");
		sb.append("\n\n    [Differences versus the previous schema version]("+"apidocs/aai_swagger_" + apiVersion + ".diff)");
		sb.append("\n\n    Copyright &copy; 2017 AT&amp;T Intellectual Property. All rights reserved.\n\n    Licensed under the Creative Commons License, Attribution 4.0 Intl. (the &quot;License&quot;); you may not use this documentation except in compliance with the License.\n\n    You may obtain a copy of the License at\n\n    (https://creativecommons.org/licenses/by/4.0/)\n\n    Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an &quot;AS IS&quot; BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.\n\n    ECOMP and OpenECOMP are trademarks and service marks of AT&amp;T Intellectual Property.\n\n    This document is best viewed with Firefox or Chrome. Nodes can be found by appending /#/definitions/node-type-to-find to the path to this document. Edge definitions can be found with the node definitions.\n  version: \"" + apiVersion +"\"\n");
		sb.append("  title: Active and Available Inventory REST API\n");
		sb.append("  license:\n    name: Apache 2.0\n    url: http://www.apache.org/licenses/LICENSE-2.0.html\n");
		sb.append("  contact:\n    name:\n    url:\n    email:\n");
		sb.append("host:\nbasePath: /aai/" + apiVersion + "\n");
		sb.append("schemes:\n  - https\npaths:\n");

		try {
			File initialFile;
			if(System.getProperty("user.dir") != null && !System.getProperty("user.dir").contains(normalStartDir)) {
				initialFile = new File(normalStartDir + "/src/main/resources/dbedgerules/DbEdgeRules_" + apiVersion + ".json");
			}
			else {
				initialFile = new File("src/main/resources/dbedgerules/DbEdgeRules_" + apiVersion + ".json");
			}
		    InputStream is = new FileInputStream(initialFile);

			Scanner scanner = new Scanner(is);
			jsonEdges = scanner.useDelimiter("\\Z").next();
			scanner.close();
			is.close();
			
		    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		    dbFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		    Document doc;
		    
		    if ( xml == null ) {
		    	doc = dBuilder.parse(oxmFile);
		    } else {
			    InputSource isInput = new InputSource(new StringReader(xml));
			    doc = dBuilder.parse(isInput);
		    }

		    NodeList bindingsNodes = doc.getElementsByTagName("xml-bindings");
			Element bindingElement;
			NodeList javaTypesNodes;
			Element javaTypesElement;
			
			Element javaTypeElement;

			
			if ( bindingsNodes == null || bindingsNodes.getLength() == 0 ) {
				logger.error( "missing <binding-nodes> in " + oxmFile );
				return null;
			}	    
			
			bindingElement = (Element) bindingsNodes.item(0);
			javaTypesNodes = bindingElement.getElementsByTagName("java-types");
			if ( javaTypesNodes.getLength() < 1 ) {
				logger.error( "missing <binding-nodes><java-types> in " + oxmFile );
				return null;
			}
			javaTypesElement = (Element) javaTypesNodes.item(0);

			javaTypeNodes = javaTypesElement.getElementsByTagName("java-type");
			if ( javaTypeNodes.getLength() < 1 ) {
				logger.error( "missing <binding-nodes><java-types><java-type> in " + oxmFile );
				return null;
			}

			String javaTypeName;
			String attrName, attrValue;
			Attr attr;
			StringBuffer pathSb = new StringBuffer();
			
			StringBuffer definitionsSb = new StringBuffer();
			for ( int i = 0; i < javaTypeNodes.getLength(); ++ i ) {
				javaTypeElement = (Element) javaTypeNodes.item(i);
				NamedNodeMap attributes = javaTypeElement.getAttributes();
				javaTypeName = null;
				for ( int j = 0; j < attributes.getLength(); ++j ) {
		            attr = (Attr) attributes.item(j);
		            attrName = attr.getNodeName();
		            attrValue = attr.getNodeValue();
		            if ( attrName.equals("name"))
		            	javaTypeName = attrValue;
				}
				if ( javaTypeName == null ) {
					logger.error( "<java-type> has no name attribute in " + oxmFile );
					return null;
				}
				if ( !generatedJavaType.containsKey(getXmlRootElementName(javaTypeName)) ) {
					
					processJavaTypeElementSwagger( javaTypeName, javaTypeElement, pathSb,
							definitionsSb, null, null, null, null, null, null, null);
				}
			}
			sb.append(pathSb);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		//append definitions
		sb.append("definitions:\n");
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

		logger.debug("generated " + sb.toString());
		generateRelations();
		return sb.toString();
	}
	
	private static NodeList locateXmlProperties(Element element) {
		XPathExpression expr;
		NodeList result = null;
		try {
			expr = xpath.compile("xml-properties");
			if (expr != null) {
				Object nodeset = expr.evaluate(element, XPathConstants.NODESET);
				if (nodeset != null) {
					NodeList nodes = (NodeList) nodeset;
					if (nodes != null && nodes.getLength() > 0) {
						Element xmlProperty = (Element)nodes.item(0);
						result = xmlProperty.getElementsByTagName("xml-property");
					}
				}
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return result;
		
	}
}