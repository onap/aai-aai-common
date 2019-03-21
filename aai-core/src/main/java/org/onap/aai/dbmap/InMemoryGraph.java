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

package org.onap.aai.dbmap;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.JanusGraphTransaction;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.onap.aai.dbgen.GraphSONPartialIO;
import org.onap.aai.dbgen.SchemaGenerator;
import org.onap.aai.logging.LogFormatTools;

public class InMemoryGraph {

    private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(InMemoryGraph.class);
    private JanusGraph graph = null;

    public InMemoryGraph(Builder builder) throws IOException {
        /*
         * Create a In-memory graph
         */
        InputStream is = new FileInputStream(builder.propertyFile);
        try {
            graph = JanusGraphFactory.open(builder.propertyFile);

            Properties graphProps = new Properties();
            graphProps.load(is);
            JanusGraphManagement graphMgt = graph.openManagement();
            if (builder.isSchemaEnabled) {
                LOGGER.info("Schema Enabled");
                SchemaGenerator.loadSchemaIntoJanusGraph(graph, graphMgt, graphProps.getProperty("storage.backend"));
            }
            JanusGraphTransaction transaction = graph.newTransaction();
            LOGGER.info("Loading snapshot");
            if (builder.isPartialGraph) {
                if ((builder.graphsonLocation != null) && (builder.graphsonLocation.length() > 0)) {
                    transaction.io(GraphSONPartialIO.build()).readGraph(builder.graphsonLocation);
                } else {
                    transaction.io(GraphSONPartialIO.build()).reader().create().readGraph(builder.seqInputStream,
                            graph);
                }
            } else {
                if ((builder.graphsonLocation != null) && (builder.graphsonLocation.length() > 0)) {
                    transaction.io(IoCore.graphson()).readGraph(builder.graphsonLocation);
                } else {
                    transaction.io(IoCore.graphson()).reader().create().readGraph(builder.seqInputStream, graph);
                }
            }
            transaction.commit();

        } catch (Exception e) {
            // TODO : Changesysout to logger
            e.printStackTrace();
            LOGGER.error("ERROR: Could not load datasnapshot to in memory graph. \n" + LogFormatTools.getStackTop(e));
            throw new IllegalStateException("Could not load datasnapshot to in memory graph");

        } finally {
            is.close();
        }

    }

    public static class Builder {
        private String graphsonLocation = "";
        private String propertyFile = "";
        private boolean isSchemaEnabled = false;
        private InputStream seqInputStream = null;
        private boolean isPartialGraph = false;

        /*
         * Builder constructor doesnt do anything
         */
        public Builder() {
            // Do nothing
        }

        public InMemoryGraph build(String graphsonFile, String propertyFile, boolean isSchemaEnabled)
                throws IOException {
            this.graphsonLocation = graphsonFile;
            this.propertyFile = propertyFile;
            this.isSchemaEnabled = isSchemaEnabled;
            return new InMemoryGraph(this);
        }

        public InMemoryGraph build(InputStream sis, String propertyFile, boolean isSchemaEnabled) throws IOException {
            this.graphsonLocation = null;
            this.propertyFile = propertyFile;
            this.isSchemaEnabled = isSchemaEnabled;
            this.seqInputStream = sis;
            return new InMemoryGraph(this);
        }

        public InMemoryGraph build(String graphsonFile, String propertyFile, boolean isSchemaEnabled,
                boolean isPartialGraph) throws IOException {
            this.graphsonLocation = graphsonFile;
            this.propertyFile = propertyFile;
            this.isSchemaEnabled = isSchemaEnabled;
            this.isPartialGraph = isPartialGraph;
            return new InMemoryGraph(this);
        }

        public InMemoryGraph build(InputStream sis, String propertyFile, boolean isSchemaEnabled,
                boolean isPartialGraph) throws IOException {
            this.graphsonLocation = null;
            this.propertyFile = propertyFile;
            this.isSchemaEnabled = isSchemaEnabled;
            this.seqInputStream = sis;
            this.isPartialGraph = isPartialGraph;
            return new InMemoryGraph(this);
        }
    }

    public JanusGraph getGraph() {
        return graph;
    }

}
