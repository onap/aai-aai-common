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
package org.onap.aai.serialization.queryformats;

import com.google.gson.JsonObject;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.strategy.verification.ReadOnlyStrategy;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.aai.AAISetup;
import org.onap.aai.dbmap.DBConnectionType;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.introspection.Version;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TitanDBEngine;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatVertexException;
import org.onap.aai.serialization.queryformats.utils.UrlBuilder;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class SimpleFormatTest extends AAISetup {

	@Mock
	private UrlBuilder urlBuilder;

	private Graph graph;
	private TransactionalGraphEngine dbEngine;
	private Loader loader;
	private DBSerializer serializer;
	private RawFormat simpleFormat;
	private Vertex vfModule;
	private final ModelType factoryType = ModelType.MOXY;

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);

		graph = TinkerGraph.open();

		vfModule = graph.addVertex(
				T.label, "vf-module",
				T.id, "5",
				"aai-node-type", "vf-module",
				"vf-module-id", "vf-module-id-val-68205",
				"vf-module-name", "example-vf-module-name-val-68205",
				"heat-stack-id", "example-heat-stack-id-val-68205",
				"orchestration-status", "example-orchestration-status-val-68205",
				"is-base-vf-module", "true",
				"resource-version", "1498166571906",
				"model-invariant-id", "fe8aac07-ce6c-4f9f-aa0d-b561c77da9e8",
				"model-invariant-id-local", "fe8aac07-ce6c-4f9f-aa0d-b561c77da9e8",
				"model-version-id", "0d23052d-8ffe-433e-a25d-da5da027bb7c",
				"model-version-id-local", "0d23052d-8ffe-433e-a25d-da5da027bb7c",
				"widget-model-id", "example-widget-model-id-val-68205",
				"widget-model-version", "example-widget--model-version-val-68205",
				"contrail-service-instance-fqdn", "example-contrail-service-instance-fqdn-val-68205"
		);
	}

	@Test
	public void testCreatePropertiesObjectReturnsProperProperties() throws AAIFormatVertexException, AAIException {

	    createLoaderEngineSetup();
		serializer = new DBSerializer(Version.v10, dbEngine, factoryType, "Junit");
		simpleFormat = new RawFormat.Builder(loader, serializer, urlBuilder).nodesOnly(true).depth(0).modelDriven().build();

		assertNotNull(dbEngine.tx());
		assertNotNull(dbEngine.asAdmin());

		JsonObject json = simpleFormat.createPropertiesObject(vfModule);

		assertTrue(json.has("model-invariant-id"));
		assertTrue(json.has("model-version-id"));

		assertFalse(json.has("model-invariant-id-local"));
		assertFalse(json.has("model-version-id-local"));

	}

	@Ignore
	@Test(expected = AAIFormatVertexException.class)
	public void testCreatePropertiesObjectThrowsExceptionIfSerializationFails() throws AAIFormatVertexException, AAIException, UnsupportedEncodingException {

		serializer = mock(DBSerializer.class);
		loader = mock(Loader.class);

		simpleFormat = new RawFormat.Builder(loader, serializer, urlBuilder).nodesOnly(true).depth(0).build();

		when(serializer.dbToObject(anyObject(), anyObject(), anyInt(), anyBoolean(), anyString()))
			.thenThrow(new AAIException("Test Exception"));

		simpleFormat.createPropertiesObject(vfModule);
	}

	@Ignore
	@Test(expected = AAIFormatVertexException.class)
	public void testCreatePropertiesObjectThrowsExceptionIfUnknownObject() throws AAIFormatVertexException, AAIException, UnsupportedEncodingException {

		loader = mock(Loader.class);
		serializer = mock(DBSerializer.class);

		simpleFormat = new RawFormat.Builder(loader, serializer, urlBuilder).nodesOnly(true).depth(0).build();

		when(loader.introspectorFromName(anyString()))
				.thenThrow(new AAIUnknownObjectException("Test Exception"));

		simpleFormat.createPropertiesObject(vfModule);
	}

	public void createLoaderEngineSetup(){

		if(loader == null){
			loader = LoaderFactory.createLoaderForVersion(factoryType, Version.v10);
			dbEngine = spy(new TitanDBEngine(QueryStyle.TRAVERSAL, DBConnectionType.CACHED, loader));

			TransactionalGraphEngine.Admin spyAdmin = spy(dbEngine.asAdmin());

			when(dbEngine.tx()).thenReturn(graph);
			when(dbEngine.asAdmin()).thenReturn(spyAdmin);

			when(spyAdmin.getReadOnlyTraversalSource()).thenReturn(graph.traversal(GraphTraversalSource.build().with(ReadOnlyStrategy.instance())));
			when(spyAdmin.getTraversalSource()).thenReturn(graph.traversal());
		}
	}
}
