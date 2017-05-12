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

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Introspector;
import org.openecomp.aai.introspection.IntrospectorFactory;
import org.openecomp.aai.introspection.LoaderFactory;
import org.openecomp.aai.introspection.ModelInjestor;
import org.openecomp.aai.introspection.ModelType;
import org.openecomp.aai.introspection.Version;
import org.openecomp.aai.serialization.engines.QueryStyle;
import org.openecomp.aai.serialization.engines.TitanDBEngine;
import org.openecomp.aai.serialization.engines.TransactionalGraphEngine;

public class RelationshipQueryTest {
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
		
		String content =
				"{"
				+ "\"related-to\" : \"pserver\","
				+ "\"relationship-data\" : [{"
				+ "\"relationship-key\" : \"pserver.hostname\","
				+ "\"relationship-value\" : \"key1\""
				+ "}]"
				+ "}";
						
		Unmarshaller unmarshaller = context.createUnmarshaller();
	    unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
	    unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
		unmarshaller.setProperty(UnmarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
		Object obj = context.newDynamicEntity("Relationship");

		DynamicEntity entity = (DynamicEntity)unmarshaller.unmarshal(new StreamSource(new StringReader(content)), obj.getClass()).getValue();
			
		Introspector wrappedObj = IntrospectorFactory.newInstance(ModelType.MOXY, entity);
		QueryParser query = dbEngine.getQueryBuilder().createQueryFromRelationship(wrappedObj);

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
		String content =
				"{"
				+ "\"related-to\" : \"lag-interface\","
				+ "\"relationship-data\" : [{"
				+ "\"relationship-key\" : \"pserver.hostname\","
				+ "\"relationship-value\" : \"key1\""
				+ "}, {"
				+ "\"relationship-key\" : \"lag-interface.interface-name\","
				+ "\"relationship-value\" : \"key2\""
				+ "}]"
				+ "}";
						
		Unmarshaller unmarshaller = context.createUnmarshaller();
	    unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
	    unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
		unmarshaller.setProperty(UnmarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
		Object obj = context.newDynamicEntity("Relationship");

		DynamicEntity entity = (DynamicEntity)unmarshaller.unmarshal(new StreamSource(new StringReader(content)), obj.getClass()).getValue();
			
		Introspector wrappedObj = IntrospectorFactory.newInstance(ModelType.MOXY, entity);
		QueryParser query = dbEngine.getQueryBuilder().createQueryFromRelationship(wrappedObj);
		String expected =
				".has('hostname', 'key1').has('aai-node-type', 'pserver').out('hasLAGInterface').has('aai-node-type', 'lag-interface')"
				+ ".has('interface-name', 'key2')";
		String parentExpected = 
				".has('hostname', 'key1').has('aai-node-type', 'pserver')";
		assertEquals(
				"gremlin query should be for node",
				expected,
				query.getQueryBuilder().getQuery());
		assertEquals(
				"parent gremlin query should be for parent",
				parentExpected,
				query.getQueryBuilder().getParentQuery().getQuery());
		assertEquals(
				"result type should be lag-interface",
				"lag-interface",
				query.getResultType());
    }
	
	/**
	 * Naming exceptions.
	 *
	 * @throws JAXBException the JAXB exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 * @throws AAIException the AAI exception
	 */
	@Test
	@Ignore
    public void namingExceptions() throws JAXBException, UnsupportedEncodingException, AAIException {
		String content =
				"{"
				+ "\"related-to\" : \"cvlan-tag\","
				+ "\"relationship-data\" : [{"
				+ "\"relationship-key\" : \"vce.vnf-id\","
				+ "\"relationship-value\" : \"key1\""
				+ "}, {"
				+ "\"relationship-key\" : \"port-group.interface-id\","
				+ "\"relationship-value\" : \"key2\""
				+ "},{"
				+ "\"relationship-key\" : \"cvlan-tag.cvlan-tag\","
				+ "\"relationship-value\" : \"655\""
				+ "}]"
				+ "}";
						
		Unmarshaller unmarshaller = context.createUnmarshaller();
	    unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
	    unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
		unmarshaller.setProperty(UnmarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
		Object obj = context.newDynamicEntity("Relationship");

		DynamicEntity entity = (DynamicEntity)unmarshaller.unmarshal(new StreamSource(new StringReader(content)), obj.getClass()).getValue();
			
		Introspector wrappedObj = IntrospectorFactory.newInstance(ModelType.MOXY, entity);
		QueryParser query = dbEngine.getQueryBuilder().createQueryFromRelationship(wrappedObj);
		String expected = 
				".has('vnf-id', 'key1').has('aai-node-type', 'vce').in('org.onap.relationships.inventory.BelongsTo')"
				+ ".has('aai-node-type', 'port-group').has('interface-id', 'key2').in('org.onap.relationships.inventory.BelongsTo').has('aai-node-type', 'cvlan-tag')"
				+ ".has('cvlan-tag', 655)";
		String expectedParent = 
						".has('vnf-id', 'key1').has('aai-node-type', 'vce').in('org.onap.relationships.inventory.BelongsTo')"
						+ ".has('aai-node-type', 'port-group').has('interface-id', 'key2')";
		
		assertEquals(
				"gremlin query should be " + expected,
				expected,
				query.getQueryBuilder().getQuery());
		assertEquals(
				"parent gremlin query should be equal the query for port group",
				expectedParent,
				query.getQueryBuilder().getParentQuery().getQuery());
		assertEquals(
				"result type should be cvlan-tag",
				"cvlan-tag",
				query.getResultType());
		
    }
	
	/**
	 * Double key.
	 *
	 * @throws JAXBException the JAXB exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 * @throws AAIException the AAI exception
	 */
	@Ignore
	@Test
    public void doubleKey() throws JAXBException, UnsupportedEncodingException, AAIException {
		String content =
				"{"
				+ "\"related-to\" : \"ctag-pool\","
				+ "\"relationship-data\" : [{"
				+ "\"relationship-key\" : \"complex.physical-location-id\","
				+ "\"relationship-value\" : \"key1\""
				+ " }, { "
				+ "\"relationship-key\" : \"ctag-pool.target-pe\","
				+ " \"relationship-value\" : \"key2\""
				+ " },{"
				+ "\"relationship-key\" : \"ctag-pool.availability-zone-name\","
				+ "\"relationship-value\" : \"key3\""
				+ "}]"
				+ "}";
						
						
		Unmarshaller unmarshaller = context.createUnmarshaller();
	    unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
	    unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
		unmarshaller.setProperty(UnmarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
		Object obj = context.newDynamicEntity("Relationship");

		DynamicEntity entity = (DynamicEntity)unmarshaller.unmarshal(new StreamSource(new StringReader(content)), obj.getClass()).getValue();
			
		Introspector wrappedObj = IntrospectorFactory.newInstance(ModelType.MOXY, entity);
		QueryParser query = dbEngine.getQueryBuilder().createQueryFromRelationship(wrappedObj);

		String expected = 
				".has('physical-location-id', 'key1').has('aai-node-type', 'complex')"
				+ ".in('org.onap.relationships.inventory.BelongsTo').has('aai-node-type', 'ctag-pool')"
				+ ".has('target-pe', 'key2')"
				+ ".has('availability-zone-name', 'key3')";
		String expectedParent = 
				".has('physical-location-id', 'key1').has('aai-node-type', 'complex')";

		assertEquals(
				"gremlin query should be " + expected,
				expected,
				query.getQueryBuilder().getQuery());
		assertEquals(
				"parent gremlin query should be equal the query for port group",
				expectedParent,
				query.getQueryBuilder().getParentQuery().getQuery());
		assertEquals(
				"result type should be ctag-pool",
				"ctag-pool",
				query.getResultType());
		
    }
	
}
