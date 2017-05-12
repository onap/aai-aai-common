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

package org.openecomp.aai.query.builder;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;

import org.openecomp.aai.db.props.AAIProperties;
import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Introspector;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.introspection.LoaderFactory;
import org.openecomp.aai.introspection.ModelType;
import org.openecomp.aai.serialization.queryformats.QueryFormatTestHelper;
import org.openecomp.aai.util.AAIConstants;

public class TraversalQueryTest {

	public static Loader loader;
	@Mock public GraphTraversalSource g;
	@BeforeClass
	public static void configure() throws NoSuchFieldException, SecurityException, Exception {
		
		System.setProperty("AJSC_HOME", "./src/test/resources/");
		System.setProperty("BUNDLECONFIG_DIR", "bundleconfig-local");
		QueryFormatTestHelper.setFinalStatic(AAIConstants.class.getField("AAI_HOME_ETC_OXM"), "src/test/resources/org/openecomp/aai/introspection/");
		loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, AAIProperties.LATEST);
	}
	
	@Test
	public void unionQuery() {
		TraversalQuery tQ = new TraversalQuery(loader, g);
		TraversalQuery tQ2 = new TraversalQuery(loader, g);
		TraversalQuery tQ3 = new TraversalQuery(loader, g);
		tQ.union(
				tQ2.getVerticesByProperty("test1", "value1"),
				tQ3.getVerticesByIndexedProperty("test2", "value2"));
		
		GraphTraversal<Vertex, Vertex> expected = __.<Vertex>start()
				.union(__.has("test1", "value1"),__.has("test2", "value2"));
		
		assertEquals("they are equal", expected, tQ.getQuery());
		
	}
	
	@Test
	public void traversalClones() throws UnsupportedEncodingException, AAIException, URISyntaxException {
		TraversalQuery tQ = new TraversalQuery(loader, g);
		Introspector test = loader.introspectorFromName("test-object");
		QueryBuilder builder = tQ.createQueryFromURI(new URI("network/test-objects/test-object/key1")).getQueryBuilder();
		GraphTraversal<Vertex, Vertex> expected = __.<Vertex>start().has("vnf-id", "key1").has("aai-node-type", "test-object");
		GraphTraversal<Vertex, Vertex> containerExpected = __.<Vertex>start().has("aai-node-type", "test-object");
		
		assertEquals("query object", expected.toString(), builder.getQuery().toString());
		assertEquals("container query object", containerExpected.toString(), builder.getContainerQuery().getQuery().toString());
		
	}
	
	@Test
	public void nestedTraversalClones() throws UnsupportedEncodingException, AAIException, URISyntaxException {
		
		TraversalQuery tQ = new TraversalQuery(loader, g);
		QueryBuilder builder = tQ.createQueryFromURI(new URI("network/generic-vnfs/generic-vnf/key1/l-interfaces/l-interface/key2")).getQueryBuilder();
		GraphTraversal<Vertex, Vertex> expected = __.<Vertex>start().has("vnf-id", "key1").has("aai-node-type", "generic-vnf").out("hasLInterface").has(AAIProperties.NODE_TYPE, "l-interface").has("interface-name", "key2");
		GraphTraversal<Vertex, Vertex> containerExpected = __.<Vertex>start().has("vnf-id", "key1").has("aai-node-type", "generic-vnf").out("hasLInterface").has(AAIProperties.NODE_TYPE, "l-interface");
		
		assertEquals("query object", expected.toString(), builder.getQuery().toString());
		assertEquals("container query object", containerExpected.toString(), builder.getContainerQuery().getQuery().toString());
		
	}
	
	
	
}
