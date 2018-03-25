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
/*package org.onap.aai.parsers.query;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.onap.aai.db.AAIProperties;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.introspection.Version;
import org.onap.aai.logging.LogLineBuilder;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TitanDBEngine;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;

import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.pipes.IdentityPipe;

public class GremlinPipelineTraversalTest {

	private TransactionalGraphEngine dbEngine = 
			new TitanDBEngine(QueryStyle.GREMLINPIPELINE_TRAVERSAL, 
				LoaderFactory.createLoaderForVersion(ModelType.MOXY, Version.v7, new LogLineBuilder("TEST", "TEST")),
				false);
	
	private TransactionalGraphEngine dbEnginev6 = 
			new TitanDBEngine(QueryStyle.GREMLINPIPELINE_TRAVERSAL, 
				LoaderFactory.createLoaderForVersion(ModelType.MOXY, Version.v6, new LogLineBuilder("TEST", "TEST")),
				false);
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@BeforeClass
	public static void configure() {
		System.setProperty("AJSC_HOME", ".");
		System.setProperty("BUNDLECONFIG_DIR", "bundleconfig-local");
	}
	
	@Test
    public void parentQuery() throws UnsupportedEncodingException, AAIException {
		URI uri = UriBuilder.fromPath("cloud-infrastructure/complexes/complex/key1").build();
		
		QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);
		
		GremlinPipeline expected = new GremlinPipeline(new IdentityPipe()).V().has("physical-location-id", "key1").has("aai-node-type", "complex");
		assertEquals(
				"gremlin query should be " + expected.toString(),
				expected.toString(),
				query.getQueryBuilder().getQuery().toString());
		assertEquals(
				"parent gremlin query should be equal to normal query",
				expected.toString(),
				query.getQueryBuilder().getParentQuery().toString());
		assertEquals(
				"result type should be complex",
				"complex",
				query.getResultType());
		assertEquals(
				"result type should be empty",
				"",
				query.getParentResultType());
		assertEquals("dependent",false, query.isDependent());

		
    }

	@Test
    public void childQuery() throws UnsupportedEncodingException, AAIException {
		URI uri = UriBuilder.fromPath("cloud-infrastructure/complexes/complex/key1/ctag-pools/ctag-pool/key2/key3").build();
		QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);
		GremlinPipeline expected = new GremlinPipeline(new IdentityPipe()).V()
				.has("physical-location-id", "key1").has("aai-node-type", "complex")
				.in("org.onap.relationships.inventory.BelongsTo")
				.has("target-pe", "key2").has("availability-zone-name", "key3");
		GremlinPipeline expectedParent = new GremlinPipeline(new IdentityPipe()).V()
				.has("physical-location-id", "key1").has("aai-node-type", "complex");
		assertEquals(
				"gremlin query should be " + expected.toString(),
				expected.toString(),
				query.getQueryBuilder().getQuery().toString());
		assertEquals(
				"parent gremlin query should be equal the query for complex",
				expectedParent.toString(),
				query.getQueryBuilder().getParentQuery().toString());
		assertEquals(
				"result type should be complex",
				"complex",
				query.getParentResultType());
		assertEquals(
				"result type should be ctag-pool",
				"ctag-pool",
				query.getResultType());
		assertEquals("dependent",true, query.isDependent());

		
    }
	
	@Test
    public void namingExceptions() throws UnsupportedEncodingException, AAIException {
		URI uri = UriBuilder.fromPath("network/vces/vce/key1/port-groups/port-group/key2/cvlan-tags/cvlan-tag/655").build();
		QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);
		GremlinPipeline expected = new GremlinPipeline(new IdentityPipe()).V()
				.has("vnf-id", "key1").has("aai-node-type", "vce")
				.in("org.onap.relationships.inventory.BelongsTo")
				.has("interface-id", "key2").in("org.onap.relationships.inventory.BelongsTo")
				.has("cvlan-tag", 655);
		GremlinPipeline expectedParent = new GremlinPipeline(new IdentityPipe()).V()
				.has("vnf-id", "key1").has("aai-node-type", "vce")
				.in("org.onap.relationships.inventory.BelongsTo")
				.has("interface-id", "key2");
		assertEquals(
				"gremlin query should be " + expected.toString(),
				expected.toString(),
				query.getQueryBuilder().getQuery().toString());
		assertEquals(
				"parent gremlin query should be equal the query for port group",
				expectedParent.toString(),
				query.getQueryBuilder().getParentQuery().toString());
		assertEquals(
				"result type should be cvlan-tag",
				"cvlan-tag",
				query.getResultType());
		assertEquals(
				"result type should be port-group",
				"port-group",
				query.getParentResultType());
		assertEquals(
				"contaner type should be empty",
				"",
				query.getContainerType());
		assertEquals("dependent",true, query.isDependent());

		
    }
	
	@Test
    public void getAll() throws UnsupportedEncodingException, AAIException {
		URI uri = UriBuilder.fromPath("network/vces/vce/key1/port-groups/port-group/key2/cvlan-tags").build();
		QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);
		GremlinPipeline expected = new GremlinPipeline(new IdentityPipe()).V()
				.has("vnf-id", "key1").has("aai-node-type", "vce")
				.in("org.onap.relationships.inventory.BelongsTo")
				.has("interface-id", "key2").in("org.onap.relationships.inventory.BelongsTo")
				.has("aai-node-type", "cvlan-tag");
		GremlinPipeline expectedParent = new GremlinPipeline(new IdentityPipe()).V()
				.has("vnf-id", "key1").has("aai-node-type", "vce")
				.in("org.onap.relationships.inventory.BelongsTo")
				.has("interface-id", "key2");
		assertEquals(
				"gremlin query should be " + expected.toString(),
				expected.toString(),
				query.getQueryBuilder().getQuery().toString());
		assertEquals(
				"parent gremlin query should be equal the query for port group",
				expectedParent.toString(),
				query.getQueryBuilder().getParentQuery().toString());
		assertEquals(
				"result type should be port-group",
				"port-group",
				query.getParentResultType());
		assertEquals(
				"result type should be cvlan-tag",
				"cvlan-tag",
				query.getResultType());
		assertEquals(
				"container type should be cvlan-tags",
				"cvlan-tags",
				query.getContainerType());
		assertEquals("dependent",true, query.isDependent());

		
    }
	
	@Test
	public void getItemAffectedByDefaultCloudRegion() throws UnsupportedEncodingException, AAIException {
		URI uri = UriBuilder.fromPath("cloud-infrastructure/tenants/tenant/key1/vservers/vserver/key2/l-interfaces/l-interface/key3").build();
		QueryParser query = dbEnginev6.getQueryBuilder().createQueryFromURI(uri);
		GremlinPipeline expected = new GremlinPipeline(new IdentityPipe()).V()
				.has("cloud-owner", "att-aic").has("aai-node-type", "cloud-region")
				.has("cloud-region-id", "AAIAIC25")
				.in("org.onap.relationships.inventory.BelongsTo")
				.has("tenant-id", "key1")
				.in("org.onap.relationships.inventory.BelongsTo")
				.has("vserver-id", "key2")
				.in("org.onap.relationships.inventory.BelongsTo")
				.has("interface-name", "key3");
		GremlinPipeline expectedParent = new GremlinPipeline(new IdentityPipe()).V()
				.has("cloud-owner", "att-aic").has("aai-node-type", "cloud-region")
				.has("cloud-region-id", "AAIAIC25")
				.in("org.onap.relationships.inventory.BelongsTo")
				.has("tenant-id", "key1")
				.in("org.onap.relationships.inventory.BelongsTo")
				.has("vserver-id", "key2");
		assertEquals(
				"gremlin query should be " + expected.toString(),
				expected.toString(),
				query.getQueryBuilder().getQuery().toString());
		assertEquals(
				"parent gremlin query should be equal the query for vserver",
				expectedParent.toString(),
				query.getQueryBuilder().getParentQuery().toString());
		assertEquals(
				"result type should be vserver",
				"vserver",
				query.getParentResultType());
		assertEquals(
				"result type should be l-interface",
				"l-interface",
				query.getResultType());
		assertEquals(
				"container type should be empty",
				"",
				query.getContainerType());
		assertEquals("dependent",true, query.isDependent());

	}
	
	@Test
	public void getViaQueryParam() throws UnsupportedEncodingException, AAIException {
		URI uri = UriBuilder.fromPath("cloud-infrastructure/tenants/tenant").build();
		MultivaluedMap<String, String> map = new MultivaluedHashMap<>();

				.has("tenant-name", "Tenant1");

				.has("tenant-name", "Tenant2");

		GremlinPipeline expectedParent = new GremlinPipeline(new IdentityPipe()).V()
				.has("cloud-owner", "att-aic").has("aai-node-type", "cloud-region")
				.has("cloud-region-id", "AAIAIC25");
						
		assertEquals(
				"gremlin query should be " + expected.toString(),
				expected.toString(),
				query.getQueryBuilder().getQuery().toString());
		assertEquals(
				"parent gremlin query should be equal the query for cloud-region",
				expectedParent.toString(),
				query.getQueryBuilder().getParentQuery().toString());
		assertEquals(
				"result type should be cloud-region",
				"cloud-region",
				query.getParentResultType());
		assertEquals(
				"result type should be tenant",
				"tenant",
				query.getResultType());
		assertEquals(
				"container type should be empty",
				"",
				query.getContainerType());
		assertEquals("dependent",true, query.isDependent());

	}
	
	@Test
	public void getPluralViaQueryParam() throws UnsupportedEncodingException, AAIException {
		URI uri = UriBuilder.fromPath("network/vnfcs").build();
		MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
		map.putSingle("prov-status", "up");
		QueryParser query = dbEnginev6.getQueryBuilder().createQueryFromURI(uri, map);
		GremlinPipeline expected = new GremlinPipeline(new IdentityPipe()).V()
				.has("aai-node-type", "vnfc")
				.has("prov-status", "up");

		GremlinPipeline expectedParent = new GremlinPipeline(new IdentityPipe()).V()
				.has("aai-node-type", "vnfc");
					
		assertEquals(
				"gremlin query should be " + expected.toString(),
				expected.toString(),
				query.getQueryBuilder().getQuery().toString());
		assertEquals(
				"parent",
				expectedParent.toString(),
				query.getQueryBuilder().getParentQuery().toString());
		assertEquals(
				"parent result type should be empty",
				"",
				query.getParentResultType());
		assertEquals(
				"result type should be vnfc",
				"vnfc",
				query.getResultType());
		assertEquals(
				"container type should be empty",
				"vnfcs",
				query.getContainerType());
		assertEquals("dependent",true, query.isDependent());

	}
	
	@Test
    public void getAllQueryParamNamingException() throws UnsupportedEncodingException, AAIException {
		URI uri = UriBuilder.fromPath("network/vces/vce/key1/port-groups/port-group/key2/cvlan-tags").build();
		MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
		map.putSingle("cvlan-tag", "333");
		QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri, map);
		
		GremlinPipeline expected = new GremlinPipeline(new IdentityPipe()).V()
				.has("vnf-id", "key1").has("aai-node-type", "vce")
				.in("org.onap.relationships.inventory.BelongsTo")
				.has("interface-id", "key2").in("org.onap.relationships.inventory.BelongsTo")
				.has("aai-node-type", "cvlan-tag")
				.has("cvlan-tag", 333);
		GremlinPipeline expectedParent = new GremlinPipeline(new IdentityPipe()).V()
				.has("vnf-id", "key1").has("aai-node-type", "vce")
				.in("org.onap.relationships.inventory.BelongsTo")
				.has("interface-id", "key2");
		assertEquals(
				"gremlin query should be " + expected.toString(),
				expected.toString(),
				query.getQueryBuilder().getQuery().toString());
		assertEquals(
				"parent gremlin query should be equal the query for port group",
				expectedParent.toString(),
				query.getQueryBuilder().getParentQuery().toString());
		assertEquals(
				"result type should be port-group",
				"port-group",
				query.getParentResultType());
		assertEquals(
				"result type should be cvlan-tag",
				"cvlan-tag",
				query.getResultType());
		assertEquals(
				"container type should be cvlan-tags",
				"cvlan-tags",
				query.getContainerType());
		assertEquals("dependent",true, query.isDependent());

		
    }
	
	@Test
    public void abstractType() throws UnsupportedEncodingException, AAIException {
		URI uri = UriBuilder.fromPath("vnf/key1").build();

		QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);
		
		GremlinPipeline expected = new GremlinPipeline(new IdentityPipe()).V()
				.has("vnf-id", "key1").or(
						new GremlinPipeline(new IdentityPipe()).has(AAIProperties.NODE_TYPE, "vce"),
						new GremlinPipeline(new IdentityPipe()).has(AAIProperties.NODE_TYPE, "generic-vnf"));
			
		GremlinPipeline expectedParent = expected;
		assertEquals(
				"gremlin query should be " + expected.toString(),
				expected.toString(),
				query.getQueryBuilder().getQuery().toString());
		assertEquals(
				"parent gremlin query should be equal the query for port group",
				expectedParent.toString(),
				query.getQueryBuilder().getParentQuery().toString());
		assertEquals(
				"result type should be empty",
				"",
				query.getParentResultType());
		assertEquals(
				"result type should be vnf",
				"vnf",
				query.getResultType());
		
		assertEquals("dependent",false, query.isDependent());

		
    }
	
	@Test
    public void nonParentAbstractType() throws UnsupportedEncodingException, AAIException {
		URI uri = UriBuilder.fromPath("cloud-infrastructure/pservers/pserver/key2/vnf/key1").build();
		thrown.expect(AAIException.class);
		thrown.expectMessage(startsWith("AAI_3001"));
		QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);
		

		
    }
	
	@Test
	public void parentAbstractTypeWithNesting() throws UnsupportedEncodingException, AAIException {
		URI uri = UriBuilder.fromPath("vnf/key1/vf-modules/vf-module/key2").build();
		
		QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);
		
		GremlinPipeline expected = new GremlinPipeline(new IdentityPipe()).V()
				.has("vnf-id", "key1").or(
						new GremlinPipeline(new IdentityPipe()).has(AAIProperties.NODE_TYPE, "vce"),
						new GremlinPipeline(new IdentityPipe()).has(AAIProperties.NODE_TYPE, "generic-vnf"))
				.outE().has("isParent", true).inV().has("vf-module-id", "key2");
		GremlinPipeline expectedParent = new GremlinPipeline(new IdentityPipe()).V()
				.has("vnf-id", "key1").or(
						new GremlinPipeline(new IdentityPipe()).has(AAIProperties.NODE_TYPE, "vce"),
						new GremlinPipeline(new IdentityPipe()).has(AAIProperties.NODE_TYPE, "generic-vnf"));
		assertEquals(
				"gremlin query should be " + expected.toString(),
				expected.toString(),
				query.getQueryBuilder().getQuery().toString());
		assertEquals(
				"parent gremlin query should be equal the query for ",
				expectedParent.toString(),
				query.getQueryBuilder().getParentQuery().toString());
		assertEquals(
				"result type should be vnf",
				"vnf",
				query.getParentResultType());
		assertEquals(
				"result type should be vf-module",
				"vf-module",
				query.getResultType());
		
		assertEquals("dependent",true, query.isDependent());
		
	}
}
*/
