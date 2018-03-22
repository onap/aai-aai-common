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
package org.onap.aai.dbgen;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.logging.LogFormatTools;
import org.onap.aai.schema.enums.PropertyMetadata;
import org.onap.aai.serialization.db.EdgeRule;
import org.onap.aai.serialization.db.EdgeRules;
import org.onap.aai.util.AAIConfig;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.google.common.collect.Multimap;
import com.thinkaurelius.titan.core.Cardinality;
import com.thinkaurelius.titan.core.Multiplicity;
import com.thinkaurelius.titan.core.PropertyKey;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.schema.TitanManagement;



public class SchemaGenerator{

	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(SchemaGenerator.class);
	private static boolean addDefaultCR = true;
	
	
	 /**
 	 * Load schema into titan.
 	 *
 	 * @param graph the graph
 	 * @param graphMgmt the graph mgmt
 	 * @param addDefaultCloudRegion the add default cloud region
 	 */
 	public static void loadSchemaIntoTitan(final TitanGraph graph, final TitanManagement graphMgmt, boolean addDefaultCloudRegion) {
		 addDefaultCR = addDefaultCloudRegion;
		 loadSchemaIntoTitan(graph, graphMgmt);
	 }
	
    /**
     * Load schema into titan.
     *
     * @param graph the graph
     * @param graphMgmt the graph mgmt
     */
    public static void loadSchemaIntoTitan(final TitanGraph graph, final TitanManagement graphMgmt) {

    	try {
    		AAIConfig.init();
    	}
    	catch (Exception ex){
			LOGGER.error(" ERROR - Could not run AAIConfig.init(). " + LogFormatTools.getStackTop(ex));
			System.out.println(" ERROR - Could not run AAIConfig.init(). ");
			System.exit(1);
		}
    	
        // NOTE - Titan 0.5.3 doesn't keep a list of legal node Labels.  
    	//   They are only used when a vertex is actually being created.  Titan 1.1 will keep track (we think).
        	

		// Use EdgeRules to make sure edgeLabels are defined in the db.  NOTE: the multiplicty used here is 
    	// always "MULTI".  This is not the same as our internal "Many2Many", "One2One", "One2Many" or "Many2One"
    	// We use the same edge-label for edges between many different types of nodes and our internal
    	// multiplicty definitions depends on which two types of nodes are being connected.

		Multimap<String, EdgeRule> edges = null;
		Set<String> labels = new HashSet<>();
		
		edges = EdgeRules.getInstance().getAllRules();
		for (EdgeRule rule : edges.values()) {
			labels.add(rule.getLabel());
		}
		
		for( String label: labels){
			if( graphMgmt.containsRelationType(label) ) {
				String dmsg = " EdgeLabel  [" + label + "] already existed. ";
            	System.out.println(dmsg);
            	LOGGER.debug(dmsg);
            } else {
            	String dmsg = "Making EdgeLabel: [" + label + "]";
            	System.out.println(dmsg);
            	LOGGER.debug(dmsg);
            	graphMgmt.makeEdgeLabel(label).multiplicity(Multiplicity.valueOf("MULTI")).make();
            }
        }     

		Loader loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, AAIProperties.LATEST);
		Map<String, Introspector> objs = loader.getAllObjects();
		Map<String, PropertyKey> seenProps = new HashMap<>();
		
		for (Introspector obj : objs.values()) {
			for (String propName : obj.getProperties()) {
				String dbPropName = propName;
				Optional<String> alias = obj.getPropertyMetadata(propName, PropertyMetadata.DB_ALIAS);
				if (alias.isPresent()) {
					dbPropName = alias.get();
				}
				if( graphMgmt.containsRelationType(propName) ){
	            	String dmsg = " PropertyKey  [" + propName + "] already existed in the DB. ";
	            	System.out.println(dmsg);
	            	LOGGER.debug(dmsg);
	            } else {
	            	Class<?> type = obj.getClass(propName);
	            	Cardinality cardinality = Cardinality.SINGLE;
	            	boolean process = false;
	            	if (obj.isListType(propName) && obj.isSimpleGenericType(propName)) {
	            		cardinality = Cardinality.SET;
	            		type = obj.getGenericTypeClass(propName);
	            		process = true;
	            	} else if (obj.isSimpleType(propName)) {
	            		process = true;
	            	}

	            	if (process) {

		            	String imsg = "Creating PropertyKey: [" + dbPropName + "], ["+ type.getSimpleName() + "], [" + cardinality + "]";
		            	System.out.println(imsg);
		            	LOGGER.info(imsg);
		            	PropertyKey propK;
		            	if (!seenProps.containsKey(dbPropName)) {
		            		propK = graphMgmt.makePropertyKey(dbPropName).dataType(type).cardinality(cardinality).make();
		            		seenProps.put(dbPropName, propK);
		            	} else {
		            		propK = seenProps.get(dbPropName);
		            	}
		            	if (graphMgmt.containsGraphIndex(dbPropName)) {
			            	String dmsg = " Index  [" + dbPropName + "] already existed in the DB. ";
			            	System.out.println(dmsg);
			            	LOGGER.debug(dmsg);
		            	} else {
		            		if( obj.getIndexedProperties().contains(propName) ){
			                 	if( obj.getUniqueProperties().contains(propName) ){
			 						imsg = "Add Unique index for PropertyKey: [" + dbPropName + "]";
					            	System.out.println(imsg);
					            	LOGGER.info(imsg);
			                        graphMgmt.buildIndex(dbPropName,Vertex.class).addKey(propK).unique().buildCompositeIndex();
			                     } else {
			                     	imsg = "Add index for PropertyKey: [" + dbPropName + "]";
					            	System.out.println(imsg);
					            	LOGGER.info(imsg);
			                        graphMgmt.buildIndex(dbPropName,Vertex.class).addKey(propK).buildCompositeIndex();
			                     }
			                 } else {
			                 	imsg = "No index added for PropertyKey: [" + dbPropName + "]";
				            	System.out.println(imsg);
				            	LOGGER.info(imsg);
			                 }
		            	}
	            	}
	            }
			}
		}
        
        String imsg = "-- About to call graphMgmt commit";
    	System.out.println(imsg);
    	LOGGER.info(imsg);
    	
        graphMgmt.commit();
    }// End of loadSchemaIntoTitan()

}


