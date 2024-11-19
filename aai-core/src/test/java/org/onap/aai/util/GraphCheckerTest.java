package org.onap.aai.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphTransaction;
import org.janusgraph.core.TransactionBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.onap.aai.AAISetup;
import org.onap.aai.config.GraphConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;


@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = {GraphConfig.class, GraphChecker.class})
@TestPropertySource(value = "classpath:/application.properties")
public class GraphCheckerTest extends AAISetup {

  @Mock TransactionBuilder transactionBuilder;
  @Mock JanusGraphTransaction transaction;
  @Mock GraphTraversalSource traversalSource;
  @Mock GraphTraversal<Vertex, Vertex> traversal;
  @MockBean JanusGraph graph;
  @Autowired GraphChecker graphChecker;

  @Before
  public void setup() {
    mockGraphTransaction();
    mockTraversal();
  }

  @Test
  public void thatAvailabilityCanBeTrue() {
    when(traversal.hasNext()).thenReturn(true);
    boolean available = graphChecker.isAaiGraphDbAvailable();
    assertTrue(available);
  }
  @Test
  public void thatAvailabilityCanBeFalse() {
    when(traversal.hasNext()).thenReturn(false);
    boolean available = graphChecker.isAaiGraphDbAvailable();
    assertFalse(available);
  }

  private void mockTraversal() {
    when(transaction.traversal()).thenReturn(traversalSource);
    when(traversalSource.V()).thenReturn(traversal);
    when(traversal.limit(1)).thenReturn(traversal);
  }

  private void mockGraphTransaction() {
    when(graph.buildTransaction()).thenReturn(transactionBuilder);
    when(transactionBuilder.readOnly()).thenReturn(transactionBuilder);
    when(transactionBuilder.consistencyChecks(false)).thenReturn(transactionBuilder);
    when(transactionBuilder.vertexCacheSize(0)).thenReturn(transactionBuilder);
    when(transactionBuilder.skipDBCacheRead()).thenReturn(transactionBuilder);
    when(transactionBuilder.start()).thenReturn(transaction);
  }
}
