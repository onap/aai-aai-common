package org.openecomp.aai.query.builder;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Before;
import org.junit.Test;
import org.openecomp.aai.db.props.AAIProperties;
import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.introspection.LoaderFactory;
import org.openecomp.aai.introspection.ModelType;
import org.openecomp.aai.serialization.db.EdgeRules;
import org.openecomp.aai.serialization.db.EdgeType;

public class UntilTest {
	public Loader loader;
	
	@Before
	public void setup(){
		System.setProperty("AJSC_HOME", ".");
		System.setProperty("BUNDLECONFIG_DIR", "src/test/resources/bundleconfig-local");
		loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, AAIProperties.LATEST);
		
	}
	
	private QueryBuilder<Vertex> buildTestQuery(QueryBuilder<Vertex> qb) throws AAIException{
		return qb.until(qb.newInstance().getVerticesByProperty("aai-node-type", "l-interface")).repeat(
				qb.newInstance().union(
						qb.newInstance().createEdgeTraversal(EdgeType.TREE, "cloud-region", "tenant"),
						qb.newInstance().createEdgeTraversal(EdgeType.TREE, "tenant", "vserver"),
						qb.newInstance().createEdgeTraversal(EdgeType.TREE, "vserver", "l-interface")
			)).store("x").cap("x").unfold().dedup();
	}
	
	@Test
	public void gremlinQueryUntilTest() throws AAIException {
		Graph graph = TinkerGraph.open();
		EdgeRules rules = EdgeRules.getInstance();
		GraphTraversalSource g = graph.traversal();
		
		Vertex v1 = graph.addVertex(T.id, 1, "aai-node-type", "cloud-region");
		Vertex v2 = graph.addVertex(T.id, 2, "aai-node-type", "tenant");
		Vertex v3 = graph.addVertex(T.id, 3, "aai-node-type", "vserver");
		Vertex v4 = graph.addVertex(T.id, 4, "aai-node-type", "l-interface");
		rules.addTreeEdge(g, v1, v2);
		rules.addTreeEdge(g, v2, v3);
		rules.addTreeEdge(g, v3, v4);
		List<Vertex> expected = new ArrayList<>();
		expected.add(v4);
		
		GremlinTraversal<Vertex> qb =  new GremlinTraversal<>(loader, g, v1);
		QueryBuilder q = buildTestQuery(qb);
		
		List<Vertex> results = q.toList();

		assertTrue("results match", expected.containsAll(results) && results.containsAll(expected));
	}

	@Test
	public void traversalQueryUntilTest() throws AAIException {
		Graph graph = TinkerGraph.open();
		EdgeRules rules = EdgeRules.getInstance();
		GraphTraversalSource g = graph.traversal();
		
		Vertex v1 = graph.addVertex(T.id, 1, "aai-node-type", "cloud-region");
		Vertex v2 = graph.addVertex(T.id, 2, "aai-node-type", "tenant");
		Vertex v3 = graph.addVertex(T.id, 3, "aai-node-type", "vserver");
		Vertex v4 = graph.addVertex(T.id, 4, "aai-node-type", "l-interface");
		rules.addTreeEdge(g, v1, v2);
		rules.addTreeEdge(g, v2, v3);
		rules.addTreeEdge(g, v3, v4);
		List<Vertex> expected = new ArrayList<>();
		expected.add(v4);
		
		TraversalQuery<Vertex> qb =  new TraversalQuery<>(loader, g, v1);
		QueryBuilder<Vertex> q = buildTestQuery(qb);
		
		List<Vertex> results = q.toList();

		assertTrue("results match", expected.containsAll(results) && results.containsAll(expected));
	}
	
	

}
