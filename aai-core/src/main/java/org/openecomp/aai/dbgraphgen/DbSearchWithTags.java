/*-
 * ============LICENSE_START=======================================================
 * org.openecomp.aai
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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
 */

package org.openecomp.aai.dbgraphgen;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.Tree;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import org.openecomp.aai.db.props.AAIProperties;
import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.serialization.db.DBSerializer;
import org.openecomp.aai.serialization.engines.TransactionalGraphEngine;
import org.openecomp.aai.util.AAIConfig;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;



/**
 * Database-level Search-Utility class that uses edge-tags to help it navigate the graph.   
 */
public class DbSearchWithTags{

	private EELFLogger LOGGER = EELFManager.getInstance().getLogger(DbSearchWithTags.class);

	private TransactionalGraphEngine engine;
	
	protected DbSearchWithTags() {
		
	}
	public DbSearchWithTags(Loader loader, TransactionalGraphEngine engine, DBSerializer serializer) {
		this.engine = engine;
	}

	/**
	 * Run edge tag query.
	 *
	 * @param transId the trans id
	 * @param fromAppId the from app id
	 * @param edgeTag the edge tag
	 * @param searchRootType - root collection point when doing the search
	 * @param displayRootType - if they want more data than would be included using the searchRootType, this
	 *                            lets them specify it.
	 * @param searchDirectionStr the search direction str
	 * @param initialFilterHash the initial filter hash
	 * @param secondaryFilterHash the secondary filter hash
	 * @param pruneLevel   either "none", "searchLevel" or "displayLevel".
	 * @param retNodeType    could be either "all" for the full map, or a single nodeType
	 * @param trimList     list of nodes to "stop" at when collecting data
	 * @return List<resultSet>
	 * @throws AAIException the AAI exception
	 */
	  public Tree<Vertex> runEdgeTagQuery( String transId, String fromAppId,
			String edgeTag, 
			String searchRootType, 
			Map<String,Object> initialFilterHash, 
			Map<String,Object> secondaryFilterHash) 
			    throws AAIException{
		
		final String tag = edgeTag;
		final String reverseTag = edgeTag + "-REV";
		//max levels is not used at this time, but may be used again
		int maxLevels = 50; // default value
		String maxString = AAIConfig.get("aai.edgeTag.proc.max.levels");
		if( maxString != null &&  !maxString.equals("") ){
			try {
				int maxVal = Integer.parseInt(maxString);
				maxLevels = maxVal;
			}
			catch ( Exception nfe ){
				// Don't worry, we will leave "maxLevels" set to the default value it was initialized with 
			}
		}
	  
		// First, we need to use the intialFilter plus the edgeTag to identify a set of search-root-nodes
		HashMap <String, Vertex> searchRootHash = identifyTopNodeSet( transId, fromAppId,
				edgeTag, searchRootType, initialFilterHash, maxLevels );
		
		
		Set<String> keySet = searchRootHash.keySet();
		Iterator<String> itr = keySet.iterator();
		String[] arrayOfVertices = new String[keySet.size()];
		int i = 0;
		while (itr.hasNext()) {
			arrayOfVertices[i] = itr.next();
			i++;
		}
		//start from all vertices provided
		//repeat checking the out edge for the tag and the in edge for reverse tag
		//emit all vertices not already seen and start again
		//return a tree structure of the vertices and edges touched by this traversal
		Tree<Element> resultTree = this.engine.asAdmin().getReadOnlyTraversalSource().V(arrayOfVertices)
				.emit().repeat(__.union(__.outE().has(tag, true), __.inE().has(reverseTag, true)).otherV()).tree().next();
		
		//the resulting tree includes the edges because of our query, we'll need to remove them
		Tree<Vertex> sanitizedTree = removeUnwantedItems(resultTree);

		//if we have secondary filters then check each tree returned for matches
		if (!secondaryFilterHash.isEmpty()) {
			filterOutResultTrees(sanitizedTree, secondaryFilterHash);
	  	}
		return sanitizedTree;
		
	  }// End of runEdgeTagQuery()
	
	/**
	 * Identify top node set.
	 *
	 * @param transId the trans id
	 * @param fromAppId the from app id
	 * @param edgeTag the edge tag
	 * @param topNodeType the top node type
	 * @param initialFilterHash the initial filter hash
	 * @param maxLevels the max levels
	 * @return List<titanVertex>
	 * @throws AAIException the AAI exception
	 */
	  public HashMap<String, Vertex> identifyTopNodeSet( String transId, String fromAppId,
			  String edgeTag, String topNodeType, Map<String,Object> initialFilterHash, int maxLevels )   
					  throws AAIException {
		  
		  final String tag = edgeTag;
		  final String reverseTag = edgeTag + "-REV";
		  HashMap <String, Vertex> topVertHash = new HashMap <>();
		  
		  // Given the filter, we want to select all the nodes of the type the filter tells us that have the
		  // 	property they gave us.
		  // Then looping through those start points, we will look "up" and then "down" to find the set of target/top nodes.
		 
		  if( initialFilterHash == null || initialFilterHash.isEmpty() ){
			  throw new AAIException("AAI_6118", " initialFilterHash is required for identifyInitialNodeSet() call. \n"); 
		  }

		  // NOTE: we're expecting the filter to have a format like this: "nodeType.parameterName:parameterValue"
		  Iterator <?> it = initialFilterHash.entrySet().iterator();
		  // -- DEBUG -- for now we only deal with ONE initial filter parameter
		  //     it would be easy enough to deal with multiple parameters if they all
		  //     applied to the same nodeType.
		  String propNodeTypeDotName = "";
		  String initNodeType = "";
		  String initPropName = "";
	 
		  String extraChecks = "";
		  
		  String propVal = "";
		  if( it.hasNext() ){
			  Map.Entry<?,?> propEntry = (Map.Entry<?,?>) it.next();
			  propNodeTypeDotName = (propEntry.getKey()).toString();
			  propVal = (propEntry.getValue()).toString();
		  }
		  
		  GraphTraversalSource source = this.engine.asAdmin().getReadOnlyTraversalSource();
		  GraphTraversal<Vertex, Vertex> g;
		 		  
		  int periodLoc = propNodeTypeDotName.indexOf(".");
		  if( periodLoc <= 0 ){
			  throw new AAIException("AAI_6120", "Bad filter param key passed in: [" + propNodeTypeDotName + "].  Expected format = [nodeName.paramName]\n"); 
		  }
		  else {
			  initNodeType = propNodeTypeDotName.substring(0,periodLoc);
			  initPropName = propNodeTypeDotName.substring(periodLoc + 1);
			  
			  //there used to be logic here that would do something special for generic-vnf.vnf-name and vserver.vserver-name
			  //it would attempt a search as they sent it, if nothing came back, try as all upper, try as all lower, then fail
			  //here it checks whether something comes back or not, if not change the case and try again
			  if( (initNodeType.equals("generic-vnf") && initPropName.equals("vnf-name"))
					  || (initNodeType.equals("vserver") && initPropName.equals("vserver-name")) ){
				  if (!this.checkKludgeCase(initNodeType, initPropName, propVal)) {
					  if (this.checkKludgeCase(initNodeType, initPropName, propVal.toUpperCase())) {
						  propVal = propVal.toUpperCase();
					  } else {
						  if (this.checkKludgeCase(initNodeType, initPropName, propVal)) {
							  propVal = propVal.toLowerCase();
						  }
					  }
				  }
			  }	
			  g = source.V().has(AAIProperties.NODE_TYPE, initNodeType).has(initPropName, propVal);
			  
			  //search all around bounded by our edge tag for start nodes that match our topNodeType
			  if (!topNodeType.equals(initNodeType)) {
				
				g.union(__.<Vertex>start().until(__.has(AAIProperties.NODE_TYPE, topNodeType))
						 .repeat(__.union(__.inE().has(reverseTag, true), __.outE().has(tag, true)).otherV()),
						 __.<Vertex>start().until(__.has(AAIProperties.NODE_TYPE, topNodeType))
						 .repeat(__.union(__.inE().has(tag, true), __.outE().has(reverseTag, true)).otherV())).dedup();
			  }
			  
			  List<Vertex> results = g.toList();
			  
			  results.forEach(v -> {
				  topVertHash.put(v.id().toString(), v);
			  });
		  }
		  if( topVertHash.isEmpty() ){
				// No Vertex was found  - throw a not-found exception
				throw new AAIException("AAI_6114", "No Node of type " + topNodeType + " found for properties: " + initialFilterHash.toString() + extraChecks);
		  }
		  else {
			  return topVertHash;
		  }
		  
	  }// End identifyInitialNodeSet()
  	  
	  /**
	   * This is a carryover from the previous version.
	   * We may be able to remove this.
	   * 
	   * @param nodeType
	   * @param propName
	   * @param propValue
	   * @return
	   */
	  private boolean checkKludgeCase(String nodeType, String propName, String propValue) {
		  return this.engine.getQueryBuilder().getVerticesByIndexedProperty(AAIProperties.NODE_TYPE, nodeType).getVerticesByProperty(propName, propValue).hasNext();
	  }
	  
	/**
	 * This method starts from the top of the tree object and checks each nested tree.
	 * If that tree does not contain a node (or nodes) that match the filterHash, remove it
	 * 
	 * @param tree
	 * @param filterHash
	 * @throws AAIException
	 */
  	private void filterOutResultTrees(Tree<Vertex> tree, Map<String,Object> filterHash) throws AAIException {
  		Set<Vertex> topLevelKeys = new LinkedHashSet<>(tree.keySet());
  		for (Vertex topLevel : topLevelKeys) {
			if (!this.checkVertexWithFilters(topLevel, filterHash)) {
				if (!filterOutResultTreesHelper(tree.get(topLevel), filterHash)) {
					//if we never found anything to satisfy our filter, remove the entire result tree
					tree.remove(topLevel);
				}
			}
		}
  	}
  	
  	/**
  	 * Checks all vertices of a tree with the provided filterHash
  	 * 
  	 * @param tree
  	 * @param filterHash
  	 * @return
  	 * @throws AAIException
  	 */
  	private boolean filterOutResultTreesHelper(Tree<Vertex> tree, Map<String,Object> filterHash) throws AAIException {
  		
  		Set<Vertex> keys = tree.keySet();

  		for (Vertex v : keys) {
  			if (checkVertexWithFilters(v, filterHash)) {
  				return true;
  			} else {
  				if (filterOutResultTreesHelper(tree.get(v), filterHash)) {
  	  				return true;
  	  			}
  			}
  		}
  		
  		return false;
  	}
  	/**
  	 * Checks whether a vertex matches the filterHash provided
  	 * 
  	 * @param v
  	 * @param filterHash
  	 * @return
  	 * @throws AAIException
  	 */
  	private boolean checkVertexWithFilters(Vertex v, Map<String,Object> filterHash) throws AAIException {
		Iterator <?> it = filterHash.entrySet().iterator();

  		while( it.hasNext() ){
			  Map.Entry<?,?> filtEntry = (Map.Entry<?,?>) it.next();
			  String propNodeTypeDotName = (filtEntry.getKey()).toString();
			  String value = (filtEntry.getValue()).toString();
			  
			  int periodLoc = propNodeTypeDotName.indexOf(".");
			  if( periodLoc <= 0 ){
				  throw new AAIException("AAI_6120", "Bad filter param key passed in: [" + propNodeTypeDotName + "].  Expected format = [nodeName.paramName]\n"); 
			  }
			  else {
				  String nodeType = propNodeTypeDotName.substring(0,periodLoc);
				  String propertyName = propNodeTypeDotName.substring(periodLoc + 1);
				  String nt = v.<String>property("aai-node-type").orElse(null);
				  if( nt.equals( nodeType ) ){
					  if( propertyName.equals("vertex-id") ){
						  // vertex-id can't be gotten the same way as other properties
						  String thisVtxId = v.id().toString();
						  if( thisVtxId.equals(value) ){
							  return true;
						  }
					  }
					  else {
						  Object thisValObj = v.property(propertyName).orElse(null);
						  if( thisValObj != null ){
							  String thisVal = thisValObj.toString();
							  if( thisVal.equals(value) ){
								  return true;
							  }
						  }
					  }
				  }				  
			  }
		  }
  		
		 return false;
  	}
  	
  	/**
  	 * Removes every other tree from the originalTree provided.
  	 * It is designed to specifically handle removing unwanted edges from the originalTree
  	 * @param originalTree
  	 * @return
  	 */
  	private Tree<Vertex> removeUnwantedItems(Tree<Element> originalTree) {
  		
  		Tree<Vertex> newTree = new Tree<>();
  		Set<Element> keys = originalTree.keySet();
  		for (Element element : keys) {
  			newTree.put((Vertex)element, removeUnwantedItemsHelper(originalTree.get(element).getTreesAtDepth(2)));
  		}

  		return newTree;
  		
  		
  	}
  	
  	private Tree<Vertex> removeUnwantedItemsHelper(List<Tree<Element>> originalTrees) {
  		Tree<Vertex> newTree = new Tree<>();
  		for (Tree<Element> tree : originalTrees) {
  			newTree.addTree(removeUnwantedItems(tree));
  		}
  		
  		return newTree;
  	}
}
