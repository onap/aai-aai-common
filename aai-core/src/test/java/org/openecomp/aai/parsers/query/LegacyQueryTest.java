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

package org.openecomp.aai.parsers.query;

import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.LoaderFactory;
import org.openecomp.aai.introspection.ModelInjestor;
import org.openecomp.aai.introspection.ModelType;
import org.openecomp.aai.introspection.Version;
import org.openecomp.aai.serialization.engines.QueryStyle;
import org.openecomp.aai.serialization.engines.TitanDBEngine;
import org.openecomp.aai.serialization.engines.TransactionalGraphEngine;

import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBException;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import static org.junit.Assert.assertEquals;


public class LegacyQueryTest {
	private ModelInjestor injestor = ModelInjestor.getInstance();
	private TransactionalGraphEngine dbEngine =
			new TitanDBEngine(QueryStyle.GREMLIN_TRAVERSAL,
				LoaderFactory.createLoaderForVersion(ModelType.MOXY, Version.v8),
				false);	
	private final Version version = Version.v8;
	private DynamicJAXBContext context = injestor.getContextForVersion(version);
	
	
	/**
	 * Configure.
	 */
	@BeforeClass
	public static void configure() {
		System.setProperty("AJSC_HOME", "./src/test/resources/");
		System.setProperty("BUNDLECONFIG_DIR", "bundleconfig-local");
	}
	
	/**
	 * Parent query.
	 *
	 * @throws JAXBException the JAXB exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 * @throws AAIException the AAI exception
	 */
	@Test
    public void parentQuery() throws JAXBException, UnsupportedEncodingException, AAIException {
		
		URI uri = UriBuilder.fromPath("cloud-infrastructure/pservers/pserver/key1").build();

		QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);

		String expected = 
				".has('hostname', 'key1').has('aai-node-type', 'pserver')";
		assertEquals(
				"gremlin query should be " + expected,
				expected,
				query.getQueryBuilder().getQuery());
		assertEquals(
				"parent gremlin query should be equal to normal query",
				expected,
				query.getQueryBuilder().getParentQuery().getQuery());
		assertEquals(
				"result type should be pserver",
				"pserver",
				query.getResultType());
		
    }

	/**
	 * Child query.
	 *
	 * @throws JAXBException the JAXB exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 * @throws AAIException the AAI exception
	 */
	@Test
    public void childQuery() throws JAXBException, UnsupportedEncodingException, AAIException {
		URI uri = UriBuilder.fromPath("cloud-infrastructure/pservers/pserver/key1/lag-interfaces/lag-interface/key2").build();
		QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);

		String expected =
				".has('hostname', 'key1').has('aai-node-type', 'pserver')"
				+ ".in('tosca.relationships.BindsTo').has('aai-node-type', 'lag-interface')"
				+ ".has('interface-name', 'key2')";
		String parentExpected = 
				".has('hostname', 'key1').has('aai-node-type', 'pserver')";
		assertEquals(
				"parent gremlin query should be for parent",
				parentExpected,
				query.getQueryBuilder().getParentQuery().getQuery());
		assertEquals(
				"result type should be lag-interface",
				"lag-interface",
				query.getResultType());
    }

}
