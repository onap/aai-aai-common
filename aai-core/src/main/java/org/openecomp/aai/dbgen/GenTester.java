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

package org.openecomp.aai.dbgen;

import java.util.Properties;

import org.openecomp.aai.dbmap.AAIGraph;
import org.openecomp.aai.logging.ErrorLogHelper;
import org.openecomp.aai.util.AAIConfig;
import org.openecomp.aai.util.AAIConstants;
import com.att.eelf.configuration.Configuration;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.schema.TitanManagement;




public class GenTester {

	private static EELFLogger LOGGER;
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
	   
		TitanGraph graph = null;
		
		// Set the logging file properties to be used by EELFManager
		Properties props = System.getProperties();
		props.setProperty(Configuration.PROPERTY_LOGGING_FILE_NAME, AAIConstants.AAI_CREATE_DB_SCHEMA_LOGBACK_PROPS);
		props.setProperty(Configuration.PROPERTY_LOGGING_FILE_PATH, AAIConstants.AAI_HOME_ETC_APP_PROPERTIES);
		LOGGER = EELFManager.getInstance().getLogger(GenTester.class);
		boolean addDefaultCR = true;
		
		try {   
			AAIConfig.init();
	    	if (args != null && args.length > 0 ){
	    		if( "genDbRulesOnly".equals(args[0]) ){
	    			ErrorLogHelper.logError("AAI_3100", 
	    					" This option is no longer supported. What was in DbRules is now derived from the OXM files. ");
	    			return;
	    		}
	    		else if ( "GEN_DB_WITH_NO_SCHEMA".equals(args[0]) ){
		    		// Note this is done to create an empty DB with no Schema so that
					// an HBase copyTable can be used to set up a copy of the db.
					String imsg = "    ---- NOTE --- about to load a graph without doing any schema processing (takes a little while) --------   ";
	            	System.out.println(imsg);
	            	LOGGER.info(imsg);
					graph = AAIGraph.getInstance().getGraph();
			    	
			       if( graph == null ){
					   ErrorLogHelper.logError("AAI_5102", "Error creating Titan graph.");
			           return;
			       }
			       else {
			    	   String amsg = "Successfully loaded a Titan graph without doing any schema work.  ";
			    	   System.out.println(amsg);
			    	   LOGGER.auditEvent(amsg);
			           return;
			       }
	    		} else if ("GEN_DB_WITH_NO_DEFAULT_CR".equals(args[0])) {
	    			addDefaultCR = false;
	    		}
	    		else {
	    			ErrorLogHelper.logError("AAI_3000", "Unrecognized argument passed to GenTester.java: [" + args[0] + "]. ");
	    			
	    			String emsg = "Unrecognized argument passed to GenTester.java: [" + args[0] + "]. ";
	    			System.out.println(emsg);
	    			LOGGER.error(emsg);
	    			
	    			emsg = "Either pass no argument for normal processing, or use 'GEN_DB_WITH_NO_SCHEMA'.";
	    			System.out.println(emsg);
	    			LOGGER.error(emsg);
	    			
	    			return;
	    		}
	    	}
	    	
			//AAIConfig.init();
			ErrorLogHelper.loadProperties();
			String imsg = "    ---- NOTE --- about to open graph (takes a little while)--------;";
        	System.out.println(imsg);
        	LOGGER.info(imsg);
			graph = AAIGraph.getInstance().getGraph();
	    	
			if( graph == null ){
				ErrorLogHelper.logError("AAI_5102", "Error creating Titan graph. ");
				return;
			}

			// Load the propertyKeys, indexes and edge-Labels into the DB
			TitanManagement graphMgt = graph.openManagement();

	       	imsg = "-- Loading new schema elements into Titan --";
       		System.out.println(imsg);
       		LOGGER.info(imsg);
       		SchemaGenerator.loadSchemaIntoTitan( graph, graphMgt, addDefaultCR );

	    } catch(Exception ex) {
	    	ErrorLogHelper.logError("AAI_4000", ex.getMessage());
	    }
	    

	    if( graph != null ){
		    String imsg = "-- graph commit";
        	System.out.println(imsg);
        	LOGGER.info(imsg);
	        graph.tx().commit();

		   	imsg = "-- graph shutdown ";
        	System.out.println(imsg);
        	LOGGER.info(imsg);
	        graph.close();
	    }
	    
	    LOGGER.auditEvent("-- all done, if program does not exit, please kill.");
	    System.exit(0);
    }

}


