/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 *  Modifications Copyright © 2018 IBM.
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
package org.onap.aai.dbmap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.onap.aai.dbgen.SchemaGenerator;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.util.AAIConstants;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.schema.JanusGraphManagement;

/**
 * Database Mapping class which acts as the middle man between the REST
 * interface objects and JanusGraph DB objects. This class provides methods to commit
 * the objects received on the REST interface into the JanusGraph graph database as
 * vertices and edges. Transactions are also managed here by using a JanusGraph
 * object to load, commit/rollback and shutdown for each request. The data model
 * rules such as keys/required properties are handled by calling DBMeth methods
 * which are driven by a specification file in json.
 * 
 
 */
public class AAIGraph {

    private static final EELFLogger logger = EELFManager.getInstance().getLogger(AAIGraph.class);
    protected static final String COMPONENT = "aaidbmap";
    protected Map<String, JanusGraph> graphs = new HashMap<>();
    private static final String REALTIME_DB = "realtime";
    private static final String CACHED_DB = "cached";
    private static boolean isInit = false;



    /**
     * Instantiates a new AAI graph.
     */
    private AAIGraph() {
        try {
            String serviceName = System.getProperty("aai.service.name", "NA");
            String rtConfig = System.getProperty("realtime.db.config");
            String cachedConfig = System.getProperty("cached.db.config");
            if (rtConfig == null) {
                rtConfig = AAIConstants.REALTIME_DB_CONFIG;
            }
            if (cachedConfig == null) {
                cachedConfig = AAIConstants.CACHED_DB_CONFIG;
            }
            this.loadGraph(REALTIME_DB, rtConfig, serviceName);
            this.loadGraph(CACHED_DB, cachedConfig, serviceName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate graphs", e);
        }
    }
    
    private static class Helper {
        private static final AAIGraph INSTANCE = new AAIGraph();
        private Helper() {
            
        }
    }
    
    /**
     * Gets the single instance of AAIGraph.
     *
     * @return single instance of AAIGraph
     */
    public static AAIGraph getInstance() {
        isInit = true;
        return Helper.INSTANCE;
    }

    public static boolean isInit() {
        return isInit;
    }
    
    private void loadGraph(String name, String configPath, String serviceName) throws Exception {
        // Graph being opened by JanusGraphFactory is being placed in hashmap to be used later
        // These graphs shouldn't be closed until the application shutdown
        try {
            PropertiesConfiguration propertiesConfiguration = new AAIGraphConfig.Builder(configPath).forService(serviceName).withGraphType(name).buildConfiguration();
            JanusGraph graph = JanusGraphFactory.open(propertiesConfiguration);

            Properties graphProps = new Properties();
            propertiesConfiguration.getKeys().forEachRemaining(k -> graphProps.setProperty(k, propertiesConfiguration.getString(k)));

            if ("inmemory".equals(graphProps.get("storage.backend"))) {
                // Load the propertyKeys, indexes and edge-Labels into the DB
                loadSchema(graph);
                loadSnapShotToInMemoryGraph(graph, graphProps);
            }

            if (graph == null) {
                throw new AAIException("AAI_5102");
            }

            graphs.put(name, graph);
        } catch (FileNotFoundException fnfe) {
            throw new AAIException("AAI_4001");
        } catch (IOException e) {
            throw new AAIException("AAI_4002");
        }
    }

    private void loadSnapShotToInMemoryGraph(JanusGraph graph, Properties graphProps) {
        if (logger.isDebugEnabled()) {
            logger.debug("Load Snapshot to InMemory Graph");
        }
        if (graphProps.containsKey("load.snapshot.file")) {
            String value = graphProps.getProperty("load.snapshot.file");
            if ("true".equals(value)) {
                try (Graph transaction = graph.newTransaction()) {
                    String location = System.getProperty("snapshot.location");
                    logAndPrint(logger, "Loading snapshot to inmemory graph.");
                    transaction.io(IoCore.graphson()).readGraph(location);
                    transaction.tx().commit();
                    logAndPrint(logger, "Snapshot loaded to inmemory graph.");
                } catch (Exception e) {
                    logAndPrint(logger,
                        "ERROR: Could not load datasnapshot to in memory graph. \n"
                            + ExceptionUtils.getFullStackTrace(e));
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void loadSchema(JanusGraph graph) {
        // Load the propertyKeys, indexes and edge-Labels into the DB
        JanusGraphManagement graphMgt = graph.openManagement();
        
        System.out.println("-- loading schema into JanusGraph");
        SchemaGenerator.loadSchemaIntoJanusGraph( graph, graphMgt, "inmemory");
    }

    /**
     * Close all of the graph connections made in the instance.
     */
    public void graphShutdown() {
        graphs.values().stream().filter(JanusGraph::isOpen).forEach(JanusGraph::close);
    }

    /**
     * Gets the graph.
     *
     * @return the graph
     */
    public JanusGraph getGraph() {
        return graphs.get(REALTIME_DB);
    }
    
    public void graphShutdown(DBConnectionType connectionType) {
        
        graphs.get(this.getGraphName(connectionType)).close();
    }
    
    public JanusGraph getGraph(DBConnectionType connectionType) {
        return graphs.get(this.getGraphName(connectionType));
    }
    
    private String getGraphName(DBConnectionType connectionType) {
        String graphName = "";
        if (DBConnectionType.CACHED.equals(connectionType)) {
            graphName = this.CACHED_DB;
        } else if (DBConnectionType.REALTIME.equals(connectionType)) {
            graphName = this.REALTIME_DB;
        }
        
        return graphName;
    }
    
    private void logAndPrint(EELFLogger logger, String msg) {
        System.out.println(msg);
        logger.info(msg);
    }
}
