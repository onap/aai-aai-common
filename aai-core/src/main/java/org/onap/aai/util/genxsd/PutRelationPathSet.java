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

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.onap.aai.introspection.Version;
import org.onap.aai.util.GenerateXsd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PutRelationPathSet {
	private static final Logger logger = LoggerFactory.getLogger("PutRelationPathSet.class");
	protected static HashMap<String, String> putRelationPaths = new HashMap<String, String>();
	private static EdgeRuleSet edgeRuleSet = null;
	public static void add(String useOpId, String path) {
		putRelationPaths.put(useOpId, path);
	}
	
	String apiPath;
	String opId;
	Version version;
	protected ArrayList<String> relations = new ArrayList<String>();
	String objectName = "";
	
	public PutRelationPathSet(Version v) {
		this.version = v;
	}

	public PutRelationPathSet(String opId, String path, Version v) {
		this.apiPath = path.replace("/relationship-list/relationship", "");
		this.opId = opId;
		this.version = v;
		objectName = DeleteOperation.deletePaths.get(apiPath);
		logger.debug("II-apiPath: "+apiPath+"\nPath: "+path+"\nopId="+opId+"\nobjectName="+objectName);
	}
	private void process() {
		this.toRelations();
		this.fromRelations();
		this.writeRelationsFile();

	}
	private void toRelations() {
		logger.debug("{“comment”: “Valid TO Relations that can be added”},");
		logger.debug("apiPath: "+apiPath+"\nopId="+opId+"\nobjectName="+objectName);
		Collection<EdgeDescription> toEdges = edgeRuleSet.getEdgeRulesTO(objectName);
		
		if(toEdges.size() > 0) {
			relations.add("{\"comment\": \"Valid TO Relations that can be added\"}\n");
		}
		for (EdgeDescription ed : toEdges) {
			logger.debug(ed.getRuleKey()+"Type="+ed.getType());
			String obj = ed.getRuleKey().replace(objectName,"").replace("|","");
			String selectedRelation = "";
			if(ed.getType() == EdgeDescription.LineageType.UNRELATED) {
				String selectObj = getUnrelatedObjectPaths(obj, apiPath);
				logger.debug("SelectedObj:"+selectObj);
				selectedRelation = formatObjectRelationSet(obj,selectObj);
				logger.debug("ObjectRelationSet"+selectedRelation);
			} else {
				String selectObj = getKinObjectPath(obj, apiPath);
				logger.debug("SelectedObj:"+selectObj);
				selectedRelation = formatObjectRelation(obj,selectObj);
				logger.debug("ObjectRelationSet"+selectedRelation);
			}
			relations.add(selectedRelation);
			logger.debug("Relation added: "+selectedRelation);
		}
	}
	
	private void fromRelations() {
		logger.debug("“comment”: “Valid FROM Relations that can be added”");
		Collection<EdgeDescription> fromEdges = edgeRuleSet.getEdgeRulesFROM(objectName);
		if(fromEdges.size() > 0) {
			relations.add("{\"comment\": \"Valid FROM Relations that can be added\"}\n");
		}
		for (EdgeDescription ed : fromEdges) {
			logger.debug(ed.getRuleKey()+"Type="+ed.getType());
			String obj = ed.getRuleKey().replace(objectName,"").replace("|","");
			String selectedRelation = "";
			if(ed.getType() == EdgeDescription.LineageType.UNRELATED) {
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
	private void writeRelationsFile() {
		File examplefilePath = new File(GenerateXsd.getYamlDir() + "/relations/" + version.name()+"/"+opId.replace("RelationshipListRelationship", "") + ".json");

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
		for (Map.Entry<String, String> p : DeleteOperation.deletePaths.entrySet()) {
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
		for (Map.Entry<String, String> p : DeleteOperation.deletePaths.entrySet()) {
				if(p.getValue().equals(obj)) {
					logger.trace("p.getvalue:"+p.getValue()+"p.getkey:"+p.getKey());
					targetPath +=  ((targetPath.length() == 0 ? "" : "|") + p.getKey());
					logger.trace("Match:"+apiPath +"\n" + targetPath);
				}
		}
		return targetPath;
	}
	
	public void generateRelations(EdgeRuleSet edgeRuleSet) {
		
		if(putRelationPaths == null)
			return;
		if(edgeRuleSet == null)
			return;
		else
			PutRelationPathSet.edgeRuleSet = edgeRuleSet;
		putRelationPaths.forEach((k,v)->{
			logger.trace("k="+k+"\n"+"v="+v+v.equals("/business/customers/customer/{global-customer-id}/service-subscriptions/service-subscription/{service-type}/service-instances/service-instance/{service-instance-id}/allotted-resources/allotted-resource/{id}/relationship-list/relationship"));
			logger.debug("apiPath(Operation): "+v);
			logger.debug("Target object: "+v.replace("/relationship-list/relationship", ""));
			logger.debug("Relations: ");
			PutRelationPathSet prp = new PutRelationPathSet(k, v, this.version);
			prp.process();
		});
	}

}
