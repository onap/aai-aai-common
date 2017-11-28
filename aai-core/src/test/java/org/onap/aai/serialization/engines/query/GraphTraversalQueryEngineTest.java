package org.onap.aai.serialization.engines.query;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Test;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.serialization.db.EdgeRules;

public class GraphTraversalQueryEngineTest {

	@Test
	public void testFindDeletable() throws AAIException {
		//setup
		EdgeRules rules = EdgeRules.getInstance("/dbedgerules/DbEdgeRules_test.json");
		
		Graph graph = TinkerGraph.open();
		Vertex parent = graph.addVertex(T.id, "00", "aai-node-type", "test-parent");
		Vertex child = graph.addVertex(T.id, "10", "aai-node-type", "test-child");
		Vertex cousin = graph.addVertex(T.id, "20", "aai-node-type", "test-cousin");
		Vertex grandchild = graph.addVertex(T.id, "30", "aai-node-type", "test-grandchild");

		GraphTraversalSource g = graph.traversal();
		
		rules.addTreeEdge(g, parent, child); //delete-other-v=none, no cascade
		rules.addTreeEdge(g, child, grandchild); //d-o-v=out, yes from child
		rules.addEdge(g, cousin, child); //d-o-v=out, yes from cousin
		
		List<Vertex> parentExpected = new ArrayList<>(Arrays.asList(parent));
		List<Vertex> childExpected = new ArrayList<>(Arrays.asList(child, grandchild));
		List<Vertex> cousinExpected = new ArrayList<>(Arrays.asList(cousin, child, grandchild));
		
		GraphTraversalQueryEngine engine = new GraphTraversalQueryEngine(g);
		
		//tests
		List<Vertex> parentDeletes = engine.findDeletable(parent);
		assertTrue(parentExpected.containsAll(parentDeletes) && parentDeletes.containsAll(parentExpected));
		
		List<Vertex> childDeletes = engine.findDeletable(child);
		assertTrue(childExpected.containsAll(childDeletes) && childDeletes.containsAll(childExpected));
		
		List<Vertex> cousinDeletes = engine.findDeletable(cousin);
		assertTrue(cousinExpected.containsAll(cousinDeletes) && cousinDeletes.containsAll(cousinExpected));
	}
}
