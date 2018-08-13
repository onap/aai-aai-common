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

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.onap.aai.serialization.db.EdgeProperty;

import java.io.*;
import java.util.*;

public class EdgeRuleSet {
	private File edgeFile;
	private DocumentContext jsonContext;

	public EdgeRuleSet(File edgeFile) throws IOException,FileNotFoundException {
		this.edgeFile = edgeFile;
		init();
	}
	public EdgeRuleSet(DocumentContext jsonContext) {
		this.jsonContext = jsonContext;
	}
	
	public Collection<EdgeDescription> getEdgeRules( String nodeName ) 
	{		
		String fromRulesPath = "$['rules'][?(@['from']=='" + nodeName + "')]";
		String toRulesPath = "$['rules'][?(@['to']=='" + nodeName + "')]";
		Collection<EdgeDescription> fromEdges = getEdgeRulesFromJson( fromRulesPath, false );
		Collection<EdgeDescription> edges = getEdgeRulesFromJson( toRulesPath, true );
		edges.addAll(fromEdges);
		return edges;
	}
	
	public Collection<EdgeDescription> getEdgeRulesTO( String nodeName ) 
	{		
		String toRulesPath = "$['rules'][?(@['to']=='" + nodeName + "')]";
		Collection<EdgeDescription> edges = getEdgeRulesFromJson( toRulesPath, true );
		return edges;
	}
	
	public Collection<EdgeDescription> getEdgeRulesFROM( String nodeName ) 
	{		
		String fromRulesPath = "$['rules'][?(@['from']=='" + nodeName + "')]";
		Collection<EdgeDescription> edges = getEdgeRulesFromJson( fromRulesPath, true );
		return edges;
	}
	
	/**
	 * Guaranteed to at least return non null but empty collection of edge descriptions
	 * @return collection of node neighbors based on DbEdgeRules
	**/
	public Collection<EdgeDescription> getEdgeRulesFromJson( String path, boolean skipMatch ) 
	{

		ArrayList<EdgeDescription> result = new ArrayList<>();
		Iterator<Map<String, Object>> edgeRulesIterator;
		try {
			List<Map<String, Object>> inEdges = jsonContext.read(path);
			
			edgeRulesIterator = inEdges.iterator();
			Map<String, Object> edgeMap;
			String fromNode;
			String toNode;
			String direction;
			String multiplicity;
			String isParent;
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
				edgeDes = new EdgeDescription();
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
					edgeDes.setType(EdgeDescription.LineageType.PARENT);
				} else {
					edgeDes.setType(EdgeDescription.LineageType.UNRELATED);
				}
				deleteOtherV = (String)edgeMap.get(EdgeProperty.DELETE_OTHER_V.toString());
				edgeDes.setDeleteOtherV(deleteOtherV);
				preventDelete = (String)edgeMap.get(EdgeProperty.PREVENT_DELETE.toString());
				edgeDes.setPreventDelete(preventDelete);
				description = (String)edgeMap.get(EdgeProperty.DESCRIPTION.toString());
				edgeDes.setDescription(description);
				
				result.add(edgeDes);
//				logger.debug("Edge: "+edgeDes.getRuleKey());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
		
	}

	private void init() throws FileNotFoundException, IOException {

		try(InputStream is = new FileInputStream(edgeFile);
			Scanner scanner = new Scanner(is)){
			String jsonEdges = scanner.useDelimiter("\\Z").next();
			jsonContext = JsonPath.parse(jsonEdges);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public String preventDeleteRules(String objectName) {
		Collection<EdgeDescription> toEdges = getEdgeRulesTO(objectName);
		toEdges.addAll(getEdgeRulesFROM(objectName));
//		logger.debug("TO Edges count: "+toEdges.size()+" Object: "+objectName);
		String prevent=null;
		LinkedHashSet<String> preventDelete = new LinkedHashSet<String>();
		for (EdgeDescription ed : toEdges) {
//			logger.debug("{“comment”: From = "+ed.getFrom()+" To: "+ed.getTo()+" Object: "+objectName);
//			logger.debug("{“comment”: Direction = "+ed.getDirection()+" PreventDelete: "+ed.getPreventDelete()+" DeleteOtherV: "+ed.getDeleteOtherV()+" Object: "+objectName);
			if(ed.getPreventDelete().equals("IN") && ed.getTo().equals(objectName)) {
				preventDelete.add(ed.getFrom().toUpperCase());
			}
			if(ed.getPreventDelete().equals("OUT") && ed.getFrom().equals(objectName)) {
				preventDelete.add(ed.getTo().toUpperCase());
			}
		}
		if(preventDelete.size() > 0) {
			prevent = objectName.toUpperCase()+" cannot be deleted if related to "+String.join(",",preventDelete);
//			logger.debug(prevent);
		}
		return String.join((prevent == null) ? "" : "\n", prevent == null ? "" : prevent)+((prevent == null) ? "" : "\n");		
//		return String.join((prevent == null) ? "" : "\n", prevent == null ? "" : prevent, also == null ? "" : also)+((prevent == null) ? "" : "\n");
	}
	
	public String fromDeleteRules(String objectName) {
		Collection<EdgeDescription> fromEdges = getEdgeRulesFROM(objectName);
		LinkedHashSet<String> preventDelete = new LinkedHashSet <String>();
		String prevent=null;
		for (EdgeDescription ed : fromEdges) {
			if(ed.getPreventDelete().equals("OUT") && ed.getFrom().equals(objectName)) {
				preventDelete.add(ed.getTo().toUpperCase());
			}
		}
		if(preventDelete.size() > 0) {
			prevent = objectName.toUpperCase()+" cannot be deleted if related to "+String.join(",",preventDelete);
		}
		return String.join(
				prevent == null ? "" : "\n",
				prevent == null ? "" : prevent
		)+(
				prevent == null ? "" : "\n"
		);
	}
}
