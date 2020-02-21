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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Multimap;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.Cardinality;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.Multiplicity;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.onap.aai.config.SpringContextAware;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.edges.EdgeRule;
import org.onap.aai.edges.exceptions.EdgeRuleNotFoundException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.LoaderUtil;
import org.onap.aai.logging.LogFormatTools;
import org.onap.aai.schema.enums.PropertyMetadata;
import org.onap.aai.util.AAIConfig;

import java.util.*;

import static org.onap.aai.db.props.AAIProperties.*;

public class SchemaGenerator4Hist {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaGenerator4Hist.class);

    /**
     * Load schema into JanusGraph.
     *
     * @param graph
     *        the graph
     * @param graphMgmt
     *        the graph mgmt
     */
    public static void loadSchemaIntoJanusGraph(final JanusGraph graph, final JanusGraphManagement graphMgmt,
            String backend) {

        try {
            AAIConfig.init();
        } catch (Exception ex) {
            LOGGER.error(" ERROR - Could not run AAIConfig.init(). " + LogFormatTools.getStackTop(ex));
            // System.out.println(" ERROR - Could not run AAIConfig.init(). ");
            System.exit(1);
        }

        // NOTE - JanusGraph 0.5.3 doesn't keep a list of legal node Labels.
        // They are only used when a vertex is actually being created.
        // JanusGraph 1.1 will keep track (we think).

        // Use EdgeRules to make sure edgeLabels are defined in the db. NOTE:
        // the multiplicty used here is
        // always "MULTI". This is not the same as our internal "Many2Many",
        // "One2One", "One2Many" or "Many2One"
        // We use the same edge-label for edges between many different types of
        // nodes and our internal
        // multiplicty definitions depends on which two types of nodes are being
        // connected.

        Multimap<String, EdgeRule> edges = null;
        Set<String> labels = new HashSet<>();

        EdgeIngestor edgeIngestor = SpringContextAware.getBean(EdgeIngestor.class);

        try {
            edges = edgeIngestor.getAllCurrentRules();
        } catch (EdgeRuleNotFoundException e) {
            LOGGER.error("Unable to find all rules {}", LogFormatTools.getStackTop(e));
        }

        for (EdgeRule rule : edges.values()) {
            labels.add(rule.getLabel());
        }

        for (String label : labels) {
            if (graphMgmt.containsRelationType(label)) {
                String dmsg = " EdgeLabel  [" + label + "] already existed. ";
                LOGGER.debug(dmsg);
            } else {
                String dmsg = "Making EdgeLabel: [" + label + "]";
                LOGGER.debug(dmsg);
                graphMgmt.makeEdgeLabel(label).multiplicity(Multiplicity.valueOf("MULTI")).make();
            }
        }

        Loader loader = LoaderUtil.getLatestVersion();

        Map<String, Introspector> objs = loader.getAllObjects();
        Map<String, PropertyKey> seenProps = new HashMap<>();

        for (Introspector obj : objs.values()) {
            for (String propName : obj.getProperties()) {
                String dbPropName = propName;
                Optional<String> alias = obj.getPropertyMetadata(propName, PropertyMetadata.DB_ALIAS);
                if (alias.isPresent()) {
                    dbPropName = alias.get();
                }
                if (graphMgmt.containsRelationType(dbPropName)) {
                    String dmsg = " PropertyKey  [" + dbPropName + "] already existed in the DB. ";
                    LOGGER.debug(dmsg);
                } else {
                    Class<?> type = obj.getClass(propName);
                    Cardinality cardinality = Cardinality.LIST;
                    boolean process = false;
                    if (obj.isListType(propName) && obj.isSimpleGenericType(propName)) {
                    	// NOTE - For history - All properties have cardinality = LIST
                    	// It is assumed that there is special processing in the Resources MS
                    	// for History to turn what used to be SET (referred to as isListType
                    	// above) will be stored in our db as a single String.  And that
                    	// single string will have Cardinality = LIST so we can track its
                    	// history.
                        //cardinality = Cardinality.SET;
                        type = obj.getGenericTypeClass(propName);
                        process = true;
                    } else if (obj.isSimpleType(propName)) {
                        process = true;
                    }

                    if (process) {

                        String imsg = " Creating PropertyKey: [" + dbPropName + "], [" + type.getSimpleName() + "], ["
                                + cardinality + "]";
                        LOGGER.info(imsg);
                        PropertyKey propK;
                        if (!seenProps.containsKey(dbPropName)) {
                            propK = graphMgmt.makePropertyKey(dbPropName).dataType(type).cardinality(cardinality)
                                    .make();
                            seenProps.put(dbPropName, propK);
                        } else {
                            propK = seenProps.get(dbPropName);
                        }
                        if (graphMgmt.containsGraphIndex(dbPropName)) {
                            String dmsg = " Index  [" + dbPropName + "] already existed in the DB. ";
                            LOGGER.debug(dmsg);
                        } else {
                            if (obj.getIndexedProperties().contains(propName)) {
                            	// NOTE - for History we never add a unique index - just a regular index
                                imsg = "Add index for PropertyKey: [" + dbPropName + "]";
                                LOGGER.info(imsg);
                                graphMgmt.buildIndex(dbPropName, Vertex.class).addKey(propK).buildCompositeIndex();
                            } else {
                                imsg = "No index needed/added for PropertyKey: [" + dbPropName + "]";
                                LOGGER.info(imsg);
                            }
                        }
                    }
                }
            }
        }// Done processing all properties defined in the OXM

        // Add the 3 new History properties are in the DB
        // They are all Cardinality=Single since instance of a Node, Edge or Property can
        //  only have one of them.  That is, a Property can show up many times in a
        //  node, but each instance of that property will only have a single start-ts,
        //  end-ts, end-source-of-truth.  Same goes for a node or edge itself.
        if (graphMgmt.containsRelationType(END_SOT)) {
            String dmsg = "PropertyKey [" + END_SOT + "] already existed in the DB. ";
            LOGGER.debug(dmsg);
        } else if (!seenProps.containsKey(END_SOT)  ) {
        	String imsg = " Creating PropertyKey: [" + END_SOT + "], [String], [SINGLE]";
        	LOGGER.info(imsg);
        	graphMgmt.makePropertyKey(END_SOT).dataType(String.class)
        		.cardinality(Cardinality.SINGLE).make();
        }

        if (graphMgmt.containsRelationType(START_TS)) {
            String dmsg = " PropertyKey [" + START_TS + "] already existed in the DB. ";
            LOGGER.debug(dmsg);
        } else if (!seenProps.containsKey(START_TS)  ) {
        	String imsg = " Creating PropertyKey: [" + START_TS + "], [Long], [SINGLE]";
        	LOGGER.info(imsg);
        	graphMgmt.makePropertyKey(START_TS).dataType(Long.class)
        		.cardinality(Cardinality.SINGLE).make();
        }

        if (graphMgmt.containsRelationType(END_TS)) {
            String dmsg = "PropertyKey [" + END_TS + "] already existed in the DB. ";
            LOGGER.debug(dmsg);
        } else if (!seenProps.containsKey(END_TS)  ) {
        	String imsg = " Creating PropertyKey: [" + END_TS + "], [Long], [SINGLE]";
        	LOGGER.info(imsg);
        	graphMgmt.makePropertyKey(END_TS).dataType(Long.class)
        		.cardinality(Cardinality.SINGLE).make();
        }

        if (graphMgmt.containsRelationType(START_TX_ID)) {
            String dmsg = "PropertyKey [" + START_TX_ID + "] already existed in the DB. ";
            LOGGER.debug(dmsg);
        } else if (!seenProps.containsKey(START_TX_ID)  ) {
            String imsg = " Creating PropertyKey: [" + START_TX_ID + "], [String], [SINGLE]";
            LOGGER.info(imsg);
            graphMgmt.makePropertyKey(START_TX_ID).dataType(String.class)
                .cardinality(Cardinality.SINGLE).make();
        }

        if (graphMgmt.containsRelationType(END_TX_ID)) {
            String dmsg = "PropertyKey [" + END_TX_ID + "] already existed in the DB. ";
            LOGGER.debug(dmsg);
        } else if (!seenProps.containsKey(END_TX_ID)  ) {
            String imsg = " Creating PropertyKey: [" + END_TX_ID + "], [String], [SINGLE]";
            LOGGER.info(imsg);
            graphMgmt.makePropertyKey(END_TX_ID).dataType(String.class)
                .cardinality(Cardinality.SINGLE).make();
        }

        String imsg = "-- About to call graphMgmt commit";
        LOGGER.info(imsg);
        graphMgmt.commit();
        if (backend != null) {
            LOGGER.info("Successfully loaded the schema to " + backend);
        }

    }

}
