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

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.BeforeClass;
import org.junit.Test;

import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.introspection.Version;
import org.onap.aai.logging.LogLineBuilder;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TitanDBEngine;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;


public class LegacyGremlinQueryTest {

	private TransactionalGraphEngine dbEngine = 
			new TitanDBEngine(QueryStyle.GREMLIN_TRAVERSAL, 
				LoaderFactory.createLoaderForVersion(ModelType.MOXY, Version.v7, new LogLineBuilder("TEST", "TEST")),
				false);
	
	private TransactionalGraphEngine dbEnginev6 = 
			new TitanDBEngine(QueryStyle.GREMLIN_TRAVERSAL, 
				LoaderFactory.createLoaderForVersion(ModelType.MOXY, Version.v6, new LogLineBuilder("TEST", "TEST")),
				false);
	
	@BeforeClass
	public static void configure() {
		System.setProperty("AJSC_HOME", "./src/test/resources/");
		System.setProperty("BUNDLECONFIG_DIR", "bundleconfig-local");
	}
	
	@Test
    public void parentQuery() throws UnsupportedEncodingException, AAIException {
		URI uri = UriBuilder.fromPath("cloud-infrastructure/complexes/complex/key1").build();
		
		QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);
		
		String expected = ".has('physical-location-id', 'key1').has('aai-node-type', 'complex')";
		assertEquals(
				"gremlin query should be " + expected,
				expected,
				query.getQueryBuilder().getQuery());
		assertEquals(
				"parent gremlin query should be equal to normal query",
				expected,
				query.getQueryBuilder().getParentQuery());
		assertEquals(
				"result type should be complex",
				"complex",
				query.getResultType());
		assertEquals("dependent",false, query.isDependent());

		
    }

	@Test
    public void childQuery() throws UnsupportedEncodingException, AAIException {
		URI uri = UriBuilder.fromPath("cloud-infrastructure/complexes/complex/key1/ctag-pools/ctag-pool/key2/key3").build();
		QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);
		String expected = 
				".has('physical-location-id', 'key1').has('aai-node-type', 'complex')"
				+ ".out('hasCtagPool')"
				+ ".has('target-pe', 'key2').has('availability-zone-name', 'key3')";
		String expectedParent = 
				".has('physical-location-id', 'key1').has('aai-node-type', 'complex')";
		assertEquals(
				"gremlin query should be " + expected,
				expected,
				query.getQueryBuilder().getQuery());
		assertEquals(
				"parent gremlin query should be equal the query for complex",
				expectedParent,
				query.getQueryBuilder().getParentQuery());
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
		String expected = 
				".has('vnf-id', 'key1').has('aai-node-type', 'vce')"
				+ ".out('hasPortGroup')"
				+ ".has('interface-id', 'key2').out('hasCTag')"
				+ ".has('cvlan-tag', 655)";
		String expectedParent = 
						".has('vnf-id', 'key1').has('aai-node-type', 'vce')"
						+ ".out('hasPortGroup')"
						+ ".has('interface-id', 'key2')";
		assertEquals(
				"gremlin query should be " + expected,
				expected,
				query.getQueryBuilder().getQuery());
		assertEquals(
				"parent gremlin query should be equal the query for port group",
				expectedParent,
				query.getQueryBuilder().getParentQuery());
		assertEquals(
				"result type should be cvlan-tag",
				"cvlan-tag",
				query.getResultType());
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
		String expected = 
				".has('vnf-id', 'key1').has('aai-node-type', 'vce')"
				+ ".out('hasPortGroup')"
				+ ".has('interface-id', 'key2').out('hasCTag')"
				+ ".has('aai-node-type', 'cvlan-tag')";
		String expectedParent = 
						".has('vnf-id', 'key1').has('aai-node-type', 'vce')"
						+ ".out('hasPortGroup')"
						+ ".has('interface-id', 'key2')";
		assertEquals(
				"gremlin query should be " + expected,
				expected,
				query.getQueryBuilder().getQuery());
		assertEquals(
				"parent gremlin query should be equal the query for port group",
				expectedParent,
				query.getQueryBuilder().getParentQuery());
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
		String expected = 
				".has('cloud-owner', 'att-aic')"
				+ ".has('cloud-region-id', 'AAIAIC25').has('aai-node-type', 'cloud-region')"
				+ ".out('has')"
				+ ".has('tenant-id', 'key1')"
				+ ".out('owns')"
				+ ".has('vserver-id', 'key2')"
				+ ".out('hasLInterface')"
				+ ".has('interface-name', 'key3')";
		String expectedParent = 
						".has('cloud-owner', 'att-aic')"
						+ ".has('cloud-region-id', 'AAIAIC25').has('aai-node-type', 'cloud-region')"
						+ ".out('has')"
						+ ".has('tenant-id', 'key1')"
						+ ".out('owns')"
						+ ".has('vserver-id', 'key2')";
		assertEquals(
				"gremlin query should be " + expected,
				expected,
				query.getQueryBuilder().getQuery());
		assertEquals(
				"parent gremlin query should be equal the query for port group",
				expectedParent,
				query.getQueryBuilder().getParentQuery());
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


		map.putSingle("tenant-name", "Tenant1");
		map.putSingle("tenant-name", "Tenant3");
		QueryParser query = dbEnginev6.getQueryBuilder().createQueryFromURI(uri, map);
		String expected = 
				".has('cloud-owner', 'att-aic')"
				+ ".has('cloud-region-id', 'AAIAIC25').has('aai-node-type', 'cloud-region')"
				+ ".out('has')"


		String expectedParent = 
						".has('cloud-owner', 'att-aic')"
						+ ".has('cloud-region-id', 'AAIAIC25').has('aai-node-type', 'cloud-region')";
						
		assertEquals(
				"gremlin query should be " + expected,
				expected,
				query.getQueryBuilder().getQuery());
		assertEquals(
				"parent gremlin query should be equal the query for cloud-region",
				expectedParent,
				query.getQueryBuilder().getParentQuery());
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
		String expected = 
				".has('aai-node-type', 'vnfc')"
				+ ".has('prov-status', 'up')";

		String expectedParent = 
				".has('aai-node-type', 'vnfc')";
						
		assertEquals(
				"gremlin query should be " + expected,
				expected,
				query.getQueryBuilder().getQuery());
		assertEquals(
				"parent",
				expectedParent,
				query.getQueryBuilder().getParentQuery());
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
		String expected = 
				".has('vnf-id', 'key1').has('aai-node-type', 'vce')"
				+ ".out('hasPortGroup')"
				+ ".has('interface-id', 'key2').out('hasCTag')"
				+ ".has('aai-node-type', 'cvlan-tag')"
				+ ".has('cvlan-tag', 333)";
		String expectedParent = 
						".has('vnf-id', 'key1').has('aai-node-type', 'vce')"
						+ ".out('hasPortGroup')"
						+ ".has('interface-id', 'key2')";
		assertEquals(
				"gremlin query should be " + expected,
				expected,
				query.getQueryBuilder().getQuery());
		assertEquals(
				"parent gremlin query should be equal the query for port group",
				expectedParent,
				query.getQueryBuilder().getParentQuery());
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
}
*/
