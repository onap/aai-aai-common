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

package org.openecomp.aai.util;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.slf4j.MDC;

import org.openecomp.aai.exceptions.AAIException;
import com.att.eelf.configuration.Configuration;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.thinkaurelius.titan.core.TitanEdge;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanTransaction;
import com.thinkaurelius.titan.core.TitanVertex;



public class UniquePropertyCheck {


	private static 	final  String    FROMAPPID = "AAI-UTILS";
	private static 	final  String    TRANSID   = UUID.randomUUID().toString();
	private static 	final  String    COMPONENT = "UniquePropertyCheck";
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		
		
		Properties props = System.getProperties();
		props.setProperty(Configuration.PROPERTY_LOGGING_FILE_NAME, "uniquePropertyCheck-logback.xml");
		props.setProperty(Configuration.PROPERTY_LOGGING_FILE_PATH, AAIConstants.AAI_HOME_ETC_APP_PROPERTIES);
		EELFLogger logger = EELFManager.getInstance().getLogger(UniquePropertyCheck.class.getSimpleName());
		MDC.put("logFilenameAppender", UniquePropertyCheck.class.getSimpleName());
		
		if( args == null || args.length != 1 ){
				String msg = "usage:  UniquePropertyCheck propertyName \n";
				System.out.println(msg);
				logAndPrint(logger, msg );
				System.exit(1);
		}
	  	String propertyName = args[0];
	  	TitanTransaction graph = null;
		
		try {   
    		AAIConfig.init();
    		System.out.println("    ---- NOTE --- about to open graph (takes a little while)--------\n");
    		TitanGraph tGraph = TitanFactory.open(AAIConstants.REALTIME_DB_CONFIG);
    		
    		if( tGraph == null ) {
    			logAndPrint(logger, " Error:  Could not get TitanGraph ");
    			System.exit(1);
    		}
    		
    		graph = tGraph.newTransaction();
    		if( graph == null ){
    			logAndPrint(logger, "could not get graph object in UniquePropertyCheck() \n");
    	 		System.exit(0);
    		}
    	}
	    catch (AAIException e1) {
			String msg =  "Threw Exception: [" + e1.toString() + "]";
			logAndPrint(logger, msg);
			System.exit(0);
        }
        catch (Exception e2) {
	 		String msg =  "Threw Exception: [" + e2.toString() + "]";
			logAndPrint(logger, msg);
	 		System.exit(0);
        }
		
		runTheCheckForUniqueness( TRANSID, FROMAPPID, graph, propertyName, logger );
		System.exit(0);
		
	}// End main()
	
	
	/**
	 * Run the check for uniqueness.
	 *
	 * @param transId the trans id
	 * @param fromAppId the from app id
	 * @param graph the graph
	 * @param propertyName the property name
	 * @param logger the logger
	 * @return the boolean
	 */
	public static Boolean runTheCheckForUniqueness( String transId, String fromAppId, TitanTransaction graph, 
			String propertyName, EELFLogger logger ){
		
		// Note - property can be found in more than one nodetype 
		//    our uniqueness constraints are always across the entire db - so this 
		//   tool looks across all nodeTypes that the property is found in.
		Boolean foundDupesFlag = false;
		
		HashMap <String,String> valuesAndVidHash = new HashMap <String, String> ();
		HashMap <String,String> dupeHash = new HashMap <String, String> ();
	
		int propCount = 0;
		int dupeCount = 0;
		Iterable <?> vertItr = graph.query().has(propertyName).vertices();
		Iterator <?> vertItor = vertItr.iterator();
       	while( vertItor.hasNext() ){
       		propCount++;
    		TitanVertex v = (TitanVertex)vertItor.next();
    		String thisVid = v.id().toString();
    		Object val = (v.<Object>property(propertyName)).orElse(null);
    		if( valuesAndVidHash.containsKey(val) ){
    			// We've seen this one before- track it in our  dupe hash
    			dupeCount++;
    			if( dupeHash.containsKey(val) ){
    				// This is not the first one being added to the dupe hash for this value
    				String updatedDupeList = dupeHash.get(val) + "|" + thisVid;
    				dupeHash.put(val.toString(), updatedDupeList);
    			}
    			else {
    				// This is the first time we see this value repeating
    				String firstTwoVids =  valuesAndVidHash.get(val) + "|" + thisVid;
    				dupeHash.put(val.toString(), firstTwoVids);
    			}
    		}
    		else {
    			valuesAndVidHash.put(val.toString(), thisVid);
    		}  		
       	}
    		
    	
    	String info = "\n Found this property [" + propertyName + "] " + propCount + " times in our db.";
    	logAndPrint(logger, info);
    	info = " Found " + dupeCount + " cases of duplicate values for this property.\n\n";
    	logAndPrint(logger, info);

    	try {
	    	if( ! dupeHash.isEmpty() ){
	    		Iterator <?> dupeItr = dupeHash.entrySet().iterator();
	    		while( dupeItr.hasNext() ){
	    			Map.Entry pair = (Map.Entry) dupeItr.next();
	    			String dupeValue = pair.getKey().toString();;
	    			    			String vidsStr = pair.getValue().toString();
	    			String[] vidArr = vidsStr.split("\\|");
	    			logAndPrint(logger, "\n\n -------------- Found " + vidArr.length 
	    					+ " nodes with " + propertyName + " of this value: [" + dupeValue + "].  Node details: ");
	    			
	    			for( int i = 0; i < vidArr.length; i++ ){
	    				String vidString = vidArr[i];
	    				Long idLong = Long.valueOf(vidString);
	    				TitanVertex tvx = (TitanVertex)graph.getVertex(idLong);
	    				showPropertiesAndEdges( TRANSID, FROMAPPID, tvx, logger );
	    			}
	    		}
	    	}
    	}
    	catch( Exception e2 ){
	 		logAndPrint(logger, "Threw Exception: [" + e2.toString() + "]");
    	} 
    	finally {
	    	if( graph != null ){
	    		graph.rollback();
	    	}
    	}
    	
    	return foundDupesFlag;
    	
	}// end of runTheCheckForUniqueness()
	
	
	/**
	 * Show properties and edges.
	 *
	 * @param transId the trans id
	 * @param fromAppId the from app id
	 * @param tVert the t vert
	 * @param logger the logger
	 */
	private static void showPropertiesAndEdges( String transId, String fromAppId, TitanVertex tVert,
			EELFLogger logger ){ 

		if( tVert == null ){
			logAndPrint(logger, "Null node passed to showPropertiesAndEdges.");
		}
		else {
			String nodeType = "";
			Object ob = tVert.<String>property("aai-node-type").orElse(null);
			if( ob == null ){
				nodeType = "null";
			}
			else{
				nodeType = ob.toString();
			}
			
			logAndPrint(logger, " AAINodeType/VtxID for this Node = [" + nodeType + "/" + tVert.id() + "]");
			logAndPrint(logger, " Property Detail: ");
			Iterator<VertexProperty<Object>> pI = tVert.properties();
			while( pI.hasNext() ){
				VertexProperty<Object> tp = pI.next();
				Object val = tp.value();
				logAndPrint(logger, "Prop: [" + tp.key() + "], val = [" + val + "] ");		
			}
			
			Iterator <Edge> eI = tVert.edges(Direction.BOTH);
			if( ! eI.hasNext() ){
				logAndPrint(logger, "No edges were found for this vertex. ");
			}
			while( eI.hasNext() ){
				TitanEdge ed = (TitanEdge) eI.next();
				String lab = ed.label();
				TitanVertex vtx = (TitanVertex) ed.otherVertex(tVert);
				if( vtx == null ){
					logAndPrint(logger, " >>> COULD NOT FIND VERTEX on the other side of this edge edgeId = " + ed.id() + " <<< ");
				}
				else {
					String nType = vtx.<String>property("aai-node-type").orElse(null);
					String vid = vtx.id().toString();
					logAndPrint(logger, "Found an edge (" + lab + ") from this vertex to a [" + nType + "] node with VtxId = " + vid);
				}
			}
		}
	} // End of showPropertiesAndEdges()

	
	/**
	 * Log and print.
	 *
	 * @param logger the logger
	 * @param msg the msg
	 */
	protected static void logAndPrint(EELFLogger logger, String msg) {
		System.out.println(msg);
		logger.info(msg);
	}
	
}


