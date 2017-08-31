package org.openecomp.aai.query.builder;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Before;
import org.junit.Test;
import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.introspection.LoaderFactory;
import org.openecomp.aai.introspection.ModelType;
import org.openecomp.aai.introspection.Version;
import org.openecomp.aai.serialization.db.EdgeRules;
import org.openecomp.aai.serialization.db.EdgeType;
import org.openecomp.aai.serialization.db.exceptions.NoEdgeRuleFoundException;

public class SimplePathTest {
	public Loader loader;
	
	@Before
	public void setup() {
		System.setProperty("AJSC_HOME", ".");
		System.setProperty("BUNDLECONFIG_DIR", "src/test/resources/bundleconfig-local");
		loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, Version.getLatest());
	}

	private QueryBuilder<Vertex> buildTestQuery(QueryBuilder<Vertex> qb) throws AAIException {
		return qb.createEdgeTraversal(EdgeType.TREE, "generic-vnf", "l-interface")
				.until(qb.newInstance().getVerticesByProperty("aai-node-type", "generic-vnf"))
				.repeat(qb.newInstance().union(
							qb.newInstance().createEdgeTraversal(EdgeType.TREE, "generic-vnf", "l-interface"),
							qb.newInstance().createEdgeTraversal(EdgeType.TREE, "l-interface", "generic-vnf"),
							qb.newInstance().createEdgeTraversal(EdgeType.COUSIN, "l-interface", "logical-link"),
							qb.newInstance().createEdgeTraversal(EdgeType.COUSIN, "logical-link", "l-interface")
						).simplePath())
				.store("x").cap("x").unfold().dedup();
	}
	
	private GraphTraversalSource setupGraph() throws AAIException{
		Graph graph = TinkerGraph.open();
		GraphTraversalSource g = graph.traversal();
		EdgeRules rules = EdgeRules.getInstance();
		
		Vertex gvnf1 = graph.addVertex(T.label, "generic-vnf", T.id, "00", "aai-node-type", "generic-vnf", 
				"vnf-id", "gvnf1", "vnf-name", "genvnfname1", "nf-type", "sample-nf-type");

		Vertex lint1 = graph.addVertex(T.label, "l-interface", T.id, "10", "aai-node-type", "l-interface",
						"interface-name", "lint1", "is-port-mirrored", "true", "in-maint", "true", "is-ip-unnumbered", "false");
		
		Vertex loglink1 = graph.addVertex(T.label, "logical-link", T.id, "20", "aai-node-type", "logical-link",
						"link-name", "loglink1", "in-maint", "false", "link-type", "sausage");
		
		Vertex lint2 = graph.addVertex(T.label, "l-interface", T.id, "11", "aai-node-type", "l-interface",
						"interface-name", "lint2", "is-port-mirrored", "true", "in-maint", "true", "is-ip-unnumbered", "false");
		
		Vertex loglink2 = graph.addVertex(T.label, "logical-link", T.id, "21", "aai-node-type", "logical-link",
				"link-name", "loglink2", "in-maint", "false", "link-type", "sausage");
		
		Vertex lint3 = graph.addVertex(T.label, "l-interface", T.id, "12", "aai-node-type", "l-interface",
				"interface-name", "lint3", "is-port-mirrored", "true", "in-maint", "true", "is-ip-unnumbered", "false");
		
		Vertex gvnf2 = graph.addVertex(T.label, "generic-vnf", T.id, "01", "aai-node-type", "generic-vnf", 
				"vnf-id", "gvnf2", "vnf-name", "genvnfname2", "nf-type", "sample-nf-type");
		
		rules.addTreeEdge(g, gvnf1, lint1);
		rules.addEdge(g, lint1, loglink1);
		rules.addEdge(g, loglink1, lint2);
		rules.addEdge(g, lint2, loglink2);
		rules.addEdge(g, loglink2, lint3);
		rules.addTreeEdge(g, gvnf2, lint3);
		
		return g;
	}
	
	@Test
	public void gremlinQueryTest() throws AAIException {
		GraphTraversalSource g = setupGraph();
		List<Vertex> expected = g.V("01").toList();
		Vertex start = g.V("00").toList().get(0);
		
		GremlinTraversal<Vertex> qb = new GremlinTraversal<>(loader, g, start);
		QueryBuilder<Vertex> q = buildTestQuery(qb);
		List<Vertex> results = q.toList();
		assertTrue("results match", expected.containsAll(results) && results.containsAll(expected));
	}

	@Test
	public void traversalQueryTest() throws AAIException {
		GraphTraversalSource g = setupGraph();
		List<Vertex> expected = g.V("01").toList();
		Vertex start = g.V("00").toList().get(0);
		
		TraversalQuery<Vertex> qb = new TraversalQuery<>(loader, g, start);
		QueryBuilder<Vertex> q = buildTestQuery(qb);
		List<Vertex> results = q.toList();
		assertTrue("results match", expected.containsAll(results) && results.containsAll(expected));
	}
}
