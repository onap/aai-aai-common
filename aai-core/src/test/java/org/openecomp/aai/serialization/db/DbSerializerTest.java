/*-
 * ============LICENSE_START=======================================================
 * org.openecomp.aai
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.aai.serialization.db;

import com.thinkaurelius.titan.core.TitanFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openecomp.aai.AAISetup;
import org.openecomp.aai.dbmap.DBConnectionType;
import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.introspection.LoaderFactory;
import org.openecomp.aai.introspection.ModelType;
import org.openecomp.aai.introspection.Version;
import org.openecomp.aai.serialization.engines.QueryStyle;
import org.openecomp.aai.serialization.engines.TitanDBEngine;
import org.openecomp.aai.serialization.engines.TransactionalGraphEngine;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class DbSerializerTest extends AAISetup {

	protected Graph graph;
	protected final EdgeRules rules = EdgeRules.getInstance();

	private final Version version = Version.getLatest();
	private final ModelType introspectorFactoryType = ModelType.MOXY;
	private final QueryStyle queryStyle = QueryStyle.TRAVERSAL;
	private final DBConnectionType type = DBConnectionType.REALTIME;
	private Loader loader;
	private TransactionalGraphEngine dbEngine;
	TransactionalGraphEngine spy;
	TransactionalGraphEngine.Admin adminSpy;

	@Before
	public void setup() throws Exception {
		graph = TitanFactory.build().set("storage.backend", "inmemory").open();
		loader = LoaderFactory.createLoaderForVersion(introspectorFactoryType, version);
		dbEngine = new TitanDBEngine(queryStyle, type, loader);
		spy = spy(dbEngine);
		adminSpy = spy(dbEngine.asAdmin());

		createGraph();
	}

	public void createGraph() throws AAIException {
		/*
		 * This setus up the test graph, For future junits , add more vertices
		 * and edges
		 */

		Vertex l3interipv4addresslist_1 = graph.traversal().addV("aai-node-type", "l3-interface-ipv4-address-list",
				"l3-interface-ipv4-address", "l3-interface-ipv4-address-1").next();
		Vertex subnet_2 = graph.traversal().addV("aai-node-type", "subnet", "subnet-id", "subnet-id-2").next();
		Vertex l3interipv6addresslist_3 = graph.traversal().addV("aai-node-type", "l3-interface-ipv6-address-list",
				"l3-interface-ipv6-address", "l3-interface-ipv6-address-3").next();
		Vertex subnet_4 = graph.traversal().addV("aai-node-type", "subnet", "subnet-id", "subnet-id-4").next();
		Vertex subnet_5 = graph.traversal().addV("aai-node-type", "subnet", "subnet-id", "subnet-id-5").next();
		Vertex l3network_6 = graph.traversal()
				.addV("aai-node-type", "l3-network", "network-id", "network-id-6", "network-name", "network-name-6")
				.next();

		GraphTraversalSource g = graph.traversal();
		rules.addEdge(g, l3interipv4addresslist_1, subnet_2);
		rules.addEdge(g, l3interipv6addresslist_3, subnet_4);
		rules.addTreeEdge(g, subnet_5, l3network_6);

	}

	@After
	public void tearDown() throws Exception {
		graph.close();
	}

	@Test
	public void subnetDelwithInEdgesIpv4Test() throws AAIException {
		String expected_message = "Object is being reference by additional objects preventing it from being deleted. Please clean up references from the following types [l3-interface-ipv4-address-list]";

		/*
		 * This subnet has in-edges with l3-ipv4 and NOT ok to delete
		 */
		Vertex subnet = graph.traversal().V().has("aai-node-type", "subnet").has("subnet-id", "subnet-id-2").next();

		String exceptionMessage = testDelete(subnet);
		assertEquals(expected_message, exceptionMessage);

	}

	@Test
	public void subnetDelwithInEdgesIpv6Test() throws AAIException {
		String expected_message = "Object is being reference by additional objects preventing it from being deleted. Please clean up references from the following types [l3-interface-ipv6-address-list]";

		/*
		 * This subnet has in-edges with l3-ipv6 and NOT ok to delete
		 */
		Vertex subnet = graph.traversal().V().has("aai-node-type", "subnet").has("subnet-id", "subnet-id-4").next();
		String exceptionMessage = testDelete(subnet);
		assertEquals(expected_message, exceptionMessage);

	}

	@Test
	public void subnetDelwithInEdgesL3network() throws AAIException {
		String expected_message = "";

		/*
		 * This subnet has in-edges with l3-network and ok to delete
		 */
		Vertex subnet = graph.traversal().V().has("aai-node-type", "subnet").has("subnet-id", "subnet-id-5").next();

		String exceptionMessage = testDelete(subnet);
		assertEquals(expected_message, exceptionMessage);

	}

	public String testDelete(Vertex v) throws AAIException {

		// Graph g_tx = graph.newTransaction();
		GraphTraversalSource traversal = graph.traversal();
		when(spy.asAdmin()).thenReturn(adminSpy);
		when(adminSpy.getTraversalSource()).thenReturn(traversal);
		when(adminSpy.getReadOnlyTraversalSource()).thenReturn(traversal);

		String exceptionMessage = "";
		DBSerializer serializer = new DBSerializer(version, spy, introspectorFactoryType, "AAI_TEST");
		try {
			serializer.delete(v, "resourceVersion", false);
		} catch (AAIException exception) {
			exceptionMessage = exception.getMessage();

		}
		return exceptionMessage;

	}

}
