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


import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.google.common.collect.Multimap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.Cardinality;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.Multiplicity;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.schema.JanusGraphManagement;
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



public class SchemaGenerator{

	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(SchemaGenerator.class);
	private static boolean addDefaultCR = true;

	private SchemaGenerator(){

	}
	
	 /**
 	 * Load schema into JanusGraph.
 	 *
 	 * @param graph the graph
 	 * @param graphMgmt the graph mgmt
 	 * @param addDefaultCloudRegion the add default cloud region
 	 */
 	public static void loadSchemaIntoJanusGraph(final JanusGraph graph, final JanusGraphManagement graphMgmt, boolean addDefaultCloudRegion) {
		 addDefaultCR = addDefaultCloudRegion;
		 loadSchemaIntoJanusGraph(graphMgmt);
	 }
	
    /**
     * Load schema into JanusGraph.
     *
     * @param graphMgmt the graph mgmt
     */
    public static void loadSchemaIntoJanusGraph(final JanusGraphManagement graphMgmt) {

    	try {
    		AAIConfig.init();
    	}
    	catch (Exception ex){
			LOGGER.error(" ERROR - Could not run AAIConfig.init(). " + LogFormatTools.getStackTop(ex));
			System.exit(1);
		}
    	
        // NOTE - JanusGraph 0.5.3 doesn't keep a list of legal node Labels.  
    	//   They are only used when a vertex is actually being created.  JanusGraph 1.1 will keep track (we think).
        	

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
			addEdgeLabel(graphMgmt, label);
		}

		Loader loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, AAIProperties.LATEST);
		Map<String, Introspector> objs = loader.getAllObjects();
		Map<String, PropertyKey> seenProps = new HashMap<>();

		for (Introspector obj : objs.values()) {
				for (String propertyName : obj.getProperties()) {
					String dbPropertyName = propertyName;
					Optional<String> alias = obj.getPropertyMetadata(propertyName, PropertyMetadata.DB_ALIAS);
					if (alias.isPresent()) {
						dbPropertyName = alias.get();
					}
					if (graphMgmt.containsRelationType(propertyName)) {
						handleExistingProperty(propertyName);
					} else {
						handleUnknownProperty(graphMgmt, seenProps, obj, propertyName, dbPropertyName);
					}
				}
		}

		String imsg = "-- About to call graphMgmt commit";
		LOGGER.info(imsg);

		graphMgmt.commit();
    }// End of loadSchemaIntoJanusGraph()

	private static void handleUnknownProperty(JanusGraphManagement graphMgmt, Map<String, PropertyKey> seenProps,
		Introspector obj, String propertyName, String dbPropertyName) {

		Class<?> type = obj.getClass(propertyName);
		Cardinality cardinality = Cardinality.SINGLE;
		boolean process = false;
		if (obj.isListType(propertyName) && obj.isSimpleGenericType(propertyName)) {
			cardinality = Cardinality.SET;
			type = obj.getGenericTypeClass(propertyName);
			process = true;
		} else if (obj.isSimpleType(propertyName)) {
			process = true;
		}

		if (process) {
			String imsg =
				"Creating PropertyKey: [" + dbPropertyName + "], [" + type.getSimpleName() + "], [" + cardinality + "]";
			LOGGER.info(imsg);
			PropertyKey propK;
			propK = getPropertyKey(graphMgmt, seenProps, dbPropertyName, type, cardinality);
			buildIndex(graphMgmt, obj, propertyName, dbPropertyName, propK);
		}
	}

	private static void buildIndex(JanusGraphManagement graphMgmt, Introspector obj, String propertyName,
		String dbPropertyName, PropertyKey propK) {
		String imsg;
		if (graphMgmt.containsGraphIndex(dbPropertyName)) {
        String dmsg = " Index  [" + dbPropertyName + "] already existed in the DB. ";
        LOGGER.debug(dmsg);
    } else {
        if( obj.getIndexedProperties().contains(propertyName) ){
             if( obj.getUniqueProperties().contains(propertyName) ){
             imsg = "Add Unique index for PropertyKey: [" + dbPropertyName + "]";
             LOGGER.info(imsg);
             graphMgmt.buildIndex(dbPropertyName, Vertex.class).addKey(propK).unique().buildCompositeIndex();
             } else {
             imsg = "Add index for PropertyKey: [" + dbPropertyName + "]";
             LOGGER.info(imsg);
             graphMgmt.buildIndex(dbPropertyName, Vertex.class).addKey(propK).buildCompositeIndex();
             }
         } else {
             imsg = "No index added for PropertyKey: [" + dbPropertyName + "]";
        LOGGER.info(imsg);
         }
    }
	}

	private static PropertyKey getPropertyKey(JanusGraphManagement graphMgmt, Map<String, PropertyKey> seenProps,
		String dbPropertyName, Class<?> type, Cardinality cardinality) {
		PropertyKey propK;
		if (!seenProps.containsKey(dbPropertyName)) {
        propK = graphMgmt.makePropertyKey(dbPropertyName).dataType(type).cardinality(cardinality).make();
        seenProps.put(dbPropertyName, propK);
    } else {
        propK = seenProps.get(dbPropertyName);
    }
		return propK;
	}

	private static void handleExistingProperty(String propertyName) {
		String dmsg = " PropertyKey  [" + propertyName + "] already existed in the DB. ";
		LOGGER.debug(dmsg);
	}

	private static void addEdgeLabel(JanusGraphManagement graphMgmt, String label) {
		if( graphMgmt.containsRelationType(label) ) {
				String dmsg = " EdgeLabel  [" + label + "] already existed. ";
            	LOGGER.debug(dmsg);
            } else {
            	String dmsg = "Making EdgeLabel: [" + label + "]";
            	LOGGER.debug(dmsg);
            	graphMgmt.makeEdgeLabel(label).multiplicity(Multiplicity.valueOf("MULTI")).make();
            }
	}

}


