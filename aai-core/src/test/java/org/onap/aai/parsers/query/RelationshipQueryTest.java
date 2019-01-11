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
package org.onap.aai.parsers.query;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.*;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.JanusGraphDBEngine;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;

@Ignore
public class RelationshipQueryTest extends AAISetup {


	private TransactionalGraphEngine dbEngine;
	private SchemaVersion version;
	private DynamicJAXBContext context = nodeIngestor.getContextForVersion(version);

	@Before
	public void setup(){
	    version = new SchemaVersion("v10");
		dbEngine =
			new JanusGraphDBEngine(QueryStyle.GREMLIN_TRAVERSAL,
				loaderFactory.createLoaderForVersion(ModelType.MOXY, version),
				false);
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
	@Ignore
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
	@Ignore
	@Test
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
