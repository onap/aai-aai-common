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

package org.onap.aai.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.aai.IntegrationTest;
import org.onap.aai.JanusgraphCassandraConfiguration;
import org.onap.aai.config.GraphConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.cassandra.CassandraContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.ToxiproxyContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import eu.rekawek.toxiproxy.model.ToxicDirection;

@Testcontainers
@Import(JanusgraphCassandraConfiguration.class)
@ContextConfiguration(classes = {
  GraphConfig.class, GraphChecker.class
})
public class GraphCheckerResiliencyTest extends IntegrationTest {

  private static final Network network = Network.newNetwork();

  @Container
  private static final CassandraContainer cassandraContainer = new CassandraContainer("cassandra:4.0.5")
      .withExposedPorts(9042)
      .withNetwork(network)
      .withNetworkAliases("cassandra");


  @Container
  private static final ToxiproxyContainer toxiproxy = new ToxiproxyContainer("ghcr.io/shopify/toxiproxy:2.5.0")
      .withNetwork(network);

  private static Proxy cassandraProxy;

  @Autowired
  GraphChecker graphChecker;

  @Autowired
  GraphTraversalSource g;

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) throws IOException {
    registry.add("testcontainers.cassandra.host", () -> toxiproxy.getHost());
    registry.add("testcontainers.cassandra.port", () -> toxiproxy.getMappedPort(8666));

    var toxiproxyClient = new ToxiproxyClient(toxiproxy.getHost(), toxiproxy.getControlPort());

    cassandraProxy = toxiproxyClient.createProxy("cassandra", "0.0.0.0:8666", "cassandra:9042");

  }

  @BeforeEach
  void resetProxy() throws IOException {
    for(var toxic: cassandraProxy.toxics().getAll()) {
      toxic.remove();
    }
  }

  @Test
  public void test() {
    boolean available = graphChecker.isAaiGraphDbAvailable();
    assertTrue(available);
  }

  @Test
  public void testConnectionFailure() throws IOException {
    assertTrue(graphChecker.isAaiGraphDbAvailable());

    cassandraProxy.toxics().bandwidth("no-connection-up", ToxicDirection.UPSTREAM, 0);
    cassandraProxy.toxics().bandwidth("no-connection-down", ToxicDirection.DOWNSTREAM, 0);
    assertFalse(graphChecker.isAaiGraphDbAvailable());

    // boolean available = graphChecker.isAaiGraphDbAvailable();
    // // g.addV().property("foo","bar").next();
    // // boolean exists = g.V().has("foo","bar").hasNext();
    // assertFalse(available);
  }

  @AfterAll
  public static void tearDown() {
    cassandraContainer.stop();
  }
}
