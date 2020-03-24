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

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.onap.aai.config.SpringContextAware;
import org.onap.aai.dbgen.SchemaGenerator;
import org.onap.aai.dbgen.SchemaGenerator4Hist;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.util.AAIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.Properties;

/**
 * Database Mapping class which acts as the middle man between the REST
 * interface objects and JanusGraph DB objects. This class provides methods to commit
 * the objects received on the REST interface into the JanusGraph graph database as
 * vertices and edges. Transactions are also managed here by using a JanusGraph
 * object to load, commit/rollback and shutdown for each request. The data model
 * rules such as keys/required properties are handled by calling DBMeth methods
 * which are driven by a specification file in json.
 *
 *
 */
public class AAIGraph {

    private static final Logger logger = LoggerFactory.getLogger(AAIGraph.class);
    private static final String IN_MEMORY = "inmemory";
    protected JanusGraph graph;
    private static boolean isInit = false;



    /**
     * Instantiates a new AAI graph.
     */
    private AAIGraph() {
        try {
            String serviceName = System.getProperty("aai.service.name", "NA");
            String rtConfig = System.getProperty("realtime.db.config");
            if (rtConfig == null) {
                rtConfig = AAIConstants.REALTIME_DB_CONFIG;
            }
            this.loadGraph(rtConfig, serviceName);
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

    private void loadGraph(String configPath, String serviceName) throws Exception {
        // Graph being opened by JanusGraphFactory is being placed in hashmap to be used later
        // These graphs shouldn't be closed until the application shutdown
        try {
            PropertiesConfiguration propertiesConfiguration = new AAIGraphConfig.Builder(configPath)
                    .forService(serviceName).withGraphType("realtime").buildConfiguration();
            graph = JanusGraphFactory.open(propertiesConfiguration);

            Properties graphProps = new Properties();
            propertiesConfiguration.getKeys()
                    .forEachRemaining(k -> graphProps.setProperty(k, propertiesConfiguration.getString(k)));

            if (IN_MEMORY.equals(graphProps.get("storage.backend"))) {
                // Load the propertyKeys, indexes and edge-Labels into the DB
                loadSchema(graph);
                loadSnapShotToInMemoryGraph(graph, graphProps);
            }

            if (graph == null) {
                throw new AAIException("AAI_5102");
            }

        } catch (FileNotFoundException e) {
            throw new AAIException("AAI_4001", e);
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
                    logger.info("Loading snapshot to inmemory graph.");
                    transaction.io(IoCore.graphson()).readGraph(location);
                    transaction.tx().commit();
                    logger.info("Snapshot loaded to inmemory graph.");
                } catch (Exception e) {
                    logger.info(String.format("ERROR: Could not load datasnapshot to in memory graph. %n%s", ExceptionUtils.getFullStackTrace(e)));
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void loadSchema(JanusGraph graph) {
        // Load the propertyKeys, indexes and edge-Labels into the DB
        JanusGraphManagement graphMgt = graph.openManagement();

        logger.info("-- loading schema into JanusGraph");
        if ("true".equals(SpringContextAware.getApplicationContext().getEnvironment().getProperty("history.enabled", "false"))) {
            SchemaGenerator4Hist.loadSchemaIntoJanusGraph(graph, graphMgt, IN_MEMORY);
        } else {
            SchemaGenerator.loadSchemaIntoJanusGraph(graphMgt, IN_MEMORY);
        }
    }

    /**
     * Close all of the graph connections made in the instance.
     */
    public void graphShutdown() {
        if (graph != null && graph.isOpen()) {
            graph.close();
        }
    }

    /**
     * Gets the graph.
     *
     * @return the graph
     */
    public JanusGraph getGraph() {
        return graph;
    }

}
