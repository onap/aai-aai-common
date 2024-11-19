/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2024 Deutsche Telekom. All rights reserved.
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

package org.onap.aai.config;

import java.io.FileNotFoundException;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.onap.aai.dbgen.SchemaGenerator;
import org.onap.aai.dbgen.SchemaGenerator4Hist;
import org.onap.aai.dbmap.AAIGraphConfig;
import org.onap.aai.exceptions.AAIException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class GraphConfig {

  private static final String IN_MEMORY = "inmemory";

  @Value("${spring.application.name}")
  private String serviceName;

  @Value("${aai.graph.properties.path}")
  private String configPath;

  @Bean
  public org.apache.commons.configuration2.Configuration getGraphProperties()
      throws FileNotFoundException, ConfigurationException {

    return new AAIGraphConfig.Builder(configPath)
      .forService(serviceName)
      .withGraphType("realtime")
      .buildConfiguration();
  }

  @Bean
  public JanusGraph janusGraph(org.apache.commons.configuration2.Configuration graphConfiguration) throws AAIException {
    JanusGraph graph = JanusGraphFactory.open(graphConfiguration);

    boolean loadSchema = false;
    if (loadSchema) {
      if (IN_MEMORY.equals(graphConfiguration.getProperty("storage.backend"))) {
        // Load the propertyKeys, indexes and edge-Labels into the DB
        loadSchema(graph);
      }
    }

    if (graph == null) {
      throw new AAIException("AAI_5102");
    }

    return graph;
  }

  @Bean
  public Graph graph(JanusGraph janusGraph) {
    return janusGraph;
  }

  public GraphTraversalSource graphTraversalSource(Graph graph) {
    return graph.traversal();
  }

  private void loadSchema(JanusGraph graph) {
    // Load the propertyKeys, indexes and edge-Labels into the DB
    boolean dbNotEmpty = graph.traversal().V().limit(1).hasNext();
    log.info("-- loading schema into JanusGraph");
    if ("true".equals(
        SpringContextAware.getApplicationContext().getEnvironment().getProperty("history.enabled", "false"))) {
      JanusGraphManagement graphMgt = graph.openManagement();
      SchemaGenerator4Hist.loadSchemaIntoJanusGraph(graphMgt, IN_MEMORY);
    } else {
      SchemaGenerator.loadSchemaIntoJanusGraph(graph, IN_MEMORY, dbNotEmpty);
    }
  }
}
