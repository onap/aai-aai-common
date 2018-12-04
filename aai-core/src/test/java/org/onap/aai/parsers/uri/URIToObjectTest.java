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
package org.onap.aai.parsers.uri;

import org.onap.aai.schema.enums.ObjectMetadata;
import org.onap.aai.setup.SchemaVersion;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onap.aai.AAISetup;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.*;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

public class URIToObjectTest extends AAISetup {

	private SchemaVersion version ;
	private SchemaVersion currentVersion;
	private Loader loader ;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/**
	 * Uri.
	 *
	 * @throws JAXBException the JAXB exception
	 * @throws AAIException the AAI exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	@PostConstruct
	public void createLoader(){
		version = schemaVersions.getRelatedLinkVersion();
		currentVersion = schemaVersions.getDefaultVersion();
		loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getRelatedLinkVersion());
	}

	@Test
    public void uri() throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException {
		URI uri = UriBuilder.fromPath("/aai/" + loader.getVersion() + "/cloud-infrastructure/cloud-regions/cloud-region/mycloudowner/mycloudregionid/tenants/tenant/key1/vservers/vserver/key2/l-interfaces/l-interface/key3").build();
		URIToObject parse = new URIToObject(loader, uri);
		Introspector result = parse.getTopEntity();
		String expected = "{\"cloud-owner\":\"mycloudowner\",\"cloud-region-id\":\"mycloudregionid\",\"tenants\":{\"tenant\":[{\"tenant-id\":\"key1\",\"vservers\":{\"vserver\":[{\"vserver-id\":\"key2\",\"l-interfaces\":{\"l-interface\":[{\"interface-name\":\"key3\"}]}}]}}]}}";
		String topEntity = "cloud-region";
		String entity = "l-interface";

		testSet(result.marshal(false), parse, expected, topEntity, entity, version);

	}

	/**
	 * Uri no version.
	 *
	 * @throws JAXBException the JAXB exception
	 * @throws AAIException the AAI exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 * @throws AAIUnknownObjectException
	 */
	@Test
    public void uriNoVersion() throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException, AAIUnknownObjectException {
		URI uri = UriBuilder.fromPath("/cloud-infrastructure/cloud-regions/cloud-region/mycloudowner/mycloudregionid/tenants/tenant/key1/vservers/vserver/key2/l-interfaces/l-interface/key3").build();
		HashMap<String, Introspector> relatedObjects = new HashMap<>();
		Introspector tenantObj = this.loader.introspectorFromName("tenant");
		tenantObj.setValue("tenant-id", "key1");
		tenantObj.setValue("tenant-name", "name1");
		relatedObjects.put(tenantObj.getObjectId(), tenantObj);
		Introspector vserverObj = this.loader.introspectorFromName("vserver");
		vserverObj.setValue("vserver-id", "key2");
		vserverObj.setValue("vserver-name", "name2");
		relatedObjects.put(vserverObj.getObjectId(), vserverObj);

		URIToObject parse = new URIToObject(loader, uri, relatedObjects);
		Introspector result = parse.getTopEntity();
		String expected = "{\"cloud-owner\":\"mycloudowner\",\"cloud-region-id\":\"mycloudregionid\",\"tenants\":{\"tenant\":[{\"tenant-id\":\"key1\",\"tenant-name\":\"name1\",\"vservers\":{\"vserver\":[{\"vserver-id\":\"key2\",\"vserver-name\":\"name2\",\"l-interfaces\":{\"l-interface\":[{\"interface-name\":\"key3\"}]}}]}}]}}";
		String topEntity = "cloud-region";
		String entity = "l-interface";

		testSet(result.marshal(false), parse, expected, topEntity, entity, version);


	}


	/**
	 * Bad URI.
	 *
	 * @throws JAXBException the JAXB exception
	 * @throws AAIException the AAI exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	@Test
    public void badURI() throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException {
		URI uri = UriBuilder.fromPath("/aai/" + loader.getVersion() + "/cloud-infrastructure/cloud-regions/cloud-region/mycloudowner/mycloudregionid/tenants/tenant/key1/vservers/vserver/key2/l-interadsfaces/l-interface/key3").build();

		thrown.expect(AAIException.class);
		thrown.expect(hasProperty("code",  is("AAI_3000")));

		new URIToObject(loader, uri);
	}

	/**
	 * Starts with valid namespace.
	 *
	 * @throws JAXBException the JAXB exception
	 * @throws AAIException the AAI exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	@Test
    public void startsWithValidNamespace() throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException {
		URI uri = UriBuilder.fromPath("/cloud-infrastructure/cloud-regions/cloud-region/mycloudowner/mycloudregionid/tenants/tenant/key1/vservers/vserver/key2/l-interfaces/l-interface/key3").build();
		URIToObject parse = new URIToObject(loader, uri);
		Introspector result = parse.getTopEntity();
		String expected = "{\"cloud-owner\":\"mycloudowner\",\"cloud-region-id\":\"mycloudregionid\",\"tenants\":{\"tenant\":[{\"tenant-id\":\"key1\",\"vservers\":{\"vserver\":[{\"vserver-id\":\"key2\",\"l-interfaces\":{\"l-interface\":[{\"interface-name\":\"key3\"}]}}]}}]}}";
		String topEntity = "cloud-region";
		String entity = "l-interface";

		testSet(result.marshal(false), parse, expected, topEntity, entity, version);
	}

	/**
	 * Single top level.
	 *
	 * @throws JAXBException the JAXB exception
	 * @throws AAIException the AAI exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	@Test
    public void singleTopLevel() throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException {
		URI uri = UriBuilder.fromPath("/network/generic-vnfs/generic-vnf/key1").build();
		URIToObject parse = new URIToObject(loader, uri);
		Introspector result = parse.getTopEntity();
		String expected = "{\"vnf-id\":\"key1\"}";

		String topEntity = "generic-vnf";
		String entity = "generic-vnf";

		testSet(result.marshal(false), parse, expected, topEntity, entity, version);

	}

	/**
	 * Naming exceptions.
	 *
	 * @throws JAXBException the JAXB exception
	 * @throws AAIException the AAI exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	@Test
	@Ignore
    public void namingExceptions() throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException {
		URI uri = UriBuilder.fromPath("network/vces/vce/key1/port-groups/port-group/key2/cvlan-tags/cvlan-tag/655").build();
		URIToObject parse = new URIToObject(loader, uri);
		Introspector result = parse.getTopEntity();
		String expected = "{\"vnf-id\":\"key1\",\"port-groups\":{\"port-group\":[{\"interface-id\":\"key2\",\"cvlan-tags\":{\"cvlan-tag-entry\":[{\"cvlan-tag\":655}]}}]}}";
		String topEntity = "vce";
		String entity = "cvlan-tag";

		testSet(result.marshal(false), parse, expected, topEntity, entity, version);

    }

	/**
	 * No list object.
	 *
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 * @throws AAIException the AAI exception
	 */
	@Test
	@Ignore
	public void noListObject() throws IllegalArgumentException, UnsupportedEncodingException, AAIException {
		URI uri = UriBuilder.fromPath("/aai/v6/network/vpls-pes/vpls-pe/0e6189fd-9257-49b9-a3be-d7ba980ccfc9/lag-interfaces/lag-interface/8ae5aa76-d597-4382-b219-04f266fe5e37/l-interfaces/l-interface/9e141d03-467b-437f-b4eb-b3133ec1e205/l3-interface-ipv4-address-list/8f19f0ea-a81f-488e-8d5c-9b7b53696c11").build();
		URIToObject parse = new URIToObject(loader, uri);
		Introspector result = parse.getTopEntity();
		String topEntity = "vpls-pe";
		String entity = "l3-interface-ipv4-address-list";
		String expected = "{\"equipment-name\":\"0e6189fd-9257-49b9-a3be-d7ba980ccfc9\",\"lag-interfaces\":{\"lag-interface\":[{\"interface-name\":\"8ae5aa76-d597-4382-b219-04f266fe5e37\",\"l-interfaces\":{\"l-interface\":[{\"interface-name\":\"9e141d03-467b-437f-b4eb-b3133ec1e205\",\"l3-interface-ipv4-address-list\":[{\"l3-interface-ipv4-address\":\"8f19f0ea-a81f-488e-8d5c-9b7b53696c11\"}]}]}}]}}";
		testSet(result.marshal(false), parse, expected, topEntity, entity, version);

	}

	@Test
    public void relativePath() throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException {
		URI uri = UriBuilder.fromPath("./l-interfaces/l-interface/key1").build();
		URIToObject parse = new URIToObject(loader, uri);
		Introspector result = parse.getEntity();
		String expected = "{\"interface-name\":\"key1\"}";

		String topEntity = "l-interface";
		String entity = "l-interface";

		testSet(result.marshal(false), parse, expected, topEntity, entity, version);

	}

	/**
	 * Test set.
	 *
	 * @param json the json
	 * @param parse the parse
	 * @param expected the expected
	 * @param topEntity the top entity
	 * @param entity the entity
	 * @param version the version
	 */
	public void testSet(String json, URIToObject parse, String expected, String topEntity, String entity, SchemaVersion version) {
		assertEquals("blah", expected, json);

		assertEquals("top entity", topEntity, parse.getTopEntityName());

		assertEquals("entity", entity, parse.getEntityName());

		assertEquals("entity object", entity, parse.getEntity().getDbName());

		assertEquals("parent list object", 1, parse.getParentList().size());

		assertEquals("object version", version, parse.getObjectVersion());
	}
}
