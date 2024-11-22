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

import static org.junit.Assert.assertTrue;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.junit.jupiter.api.AfterAll;
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
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@Import(JanusgraphCassandraConfiguration.class)
@ContextConfiguration(classes = {
  GraphConfig.class, GraphChecker.class
})
public class GraphCheckerResiliencyTest extends IntegrationTest {

  @Container
  static final CassandraContainer cassandraContainer = new CassandraContainer("cassandra:4.0.5")
      .withExposedPorts(9042);

  @Autowired
  GraphTraversalSource g;

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
      registry.add("testcontainers.cassandra.port", () -> cassandraContainer.getMappedPort(9042));
  }

  @Test
  public void test() {
    g.addV().property("foo","bar").next();
    boolean exists = g.V().has("foo","bar").hasNext();
    assertTrue(exists);
  }

  @AfterAll
  public static void tearDown() {
    cassandraContainer.stop();
  }
}
