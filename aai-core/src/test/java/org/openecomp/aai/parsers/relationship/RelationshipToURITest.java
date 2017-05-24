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

package org.openecomp.aai.parsers.relationship;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Introspector;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.introspection.LoaderFactory;
import org.openecomp.aai.introspection.ModelType;
import org.openecomp.aai.introspection.Version;
import org.openecomp.aai.parsers.exceptions.AAIIdentityMapParseException;
import org.openecomp.aai.parsers.exceptions.AmbiguousMapAAIException;
import org.openecomp.aai.serialization.queryformats.QueryFormatTestHelper;
import org.openecomp.aai.util.AAIConstants;

public class RelationshipToURITest {

	
	
	private final ModelType modelType = ModelType.MOXY;
	private final Version version10 = Version.v10;
	private final Version version9 = Version.v9;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@BeforeClass
	public static void setup() throws NoSuchFieldException, SecurityException, Exception {
		System.setProperty("AJSC_HOME", ".");
		System.setProperty("BUNDLECONFIG_DIR", "src/test/resources/bundleconfig-local");
		QueryFormatTestHelper.setFinalStatic(AAIConstants.class.getField("AAI_HOME_ETC_OXM"), "src/test/resources/org/openecomp/aai/introspection/");

	}
	
	@Test
	public void onlyLink() throws AAIException, URISyntaxException, IOException {
		Loader loader = LoaderFactory.createLoaderForVersion(modelType, version10);
		Introspector obj = loader.unmarshal("relationship", this.getJsonString("only-related-link.json"));
		URI expected = new URI("/aai/v10/network/test-objects/test-object/key1");
		
		RelationshipToURI parse = new RelationshipToURI(loader, obj);
		
		URI uri = parse.getUri();
		
		assertEquals("related-link is equal", expected.getPath(), uri.getPath());
	}
	
	@Test
	public void onlyData() throws AAIException, URISyntaxException, IOException {
		Loader loader = LoaderFactory.createLoaderForVersion(modelType, version10);
		Introspector obj = loader.unmarshal("relationship", this.getJsonString("only-relationship-data.json"));
		URI expected = new URI("/network/test-objects/test-object/key1");

		RelationshipToURI parse = new RelationshipToURI(loader, obj);
		
		URI uri = parse.getUri();
		
		assertEquals("related-link is equal", expected, uri);
	}
	
	@Test
	public void failV10() throws AAIException, URISyntaxException, IOException {
		Loader loader = LoaderFactory.createLoaderForVersion(modelType, version10);
		Introspector obj = loader.unmarshal("relationship", this.getJsonString("both-failv10-successv9.json"));
		URI expected = new URI("/aai/v10/network/test-objects/test-object/key1");
		
		thrown.expect(AAIIdentityMapParseException.class);
		thrown.expect(hasProperty("code", is("AAI_3000")));
		RelationshipToURI parse = new RelationshipToURI(loader, obj);
		

		URI uri = parse.getUri();
		
	}
	
	@Test
	public void successV9() throws AAIException, URISyntaxException, IOException {
		Loader loader = LoaderFactory.createLoaderForVersion(modelType, version9);
		Introspector obj = loader.unmarshal("relationship", this.getJsonString("both-failv10-successv9.json"));
		URI expected = new URI("/network/test-objects/test-object/key2");
		
		RelationshipToURI parse = new RelationshipToURI(loader, obj);
		

		URI uri = parse.getUri();
		
		assertEquals("related-link is equal", expected, uri);

		
	}
	
	@Test
	public void failV9() throws AAIException, URISyntaxException, IOException {
		Loader loader = LoaderFactory.createLoaderForVersion(modelType, version9);
		Introspector obj = loader.unmarshal("relationship", this.getJsonString("both-successv10-failv9.json"));
		URI expected = new URI("/network/test-objects/test-object/key1");
		
		thrown.expect(AAIIdentityMapParseException.class);
		thrown.expect(hasProperty("code", is("AAI_3000")));
		RelationshipToURI parse = new RelationshipToURI(loader, obj);
		

		URI uri = parse.getUri();
		
	}
	
	@Test
	public void successV10() throws AAIException, URISyntaxException, IOException {
		Loader loader = LoaderFactory.createLoaderForVersion(modelType, version10);
		Introspector obj = loader.unmarshal("relationship", this.getJsonString("both-successv10-failv9.json"));
		URI expected = new URI("/aai/v10/network/test-objects/test-object/key1");
		
		RelationshipToURI parse = new RelationshipToURI(loader, obj);
		

		URI uri = parse.getUri();
		
		assertEquals("related-link is equal", expected, uri);

		
	}
	
	@Test
	public void ambiguousRelationship() throws AAIException, URISyntaxException, IOException {
		Loader loader = LoaderFactory.createLoaderForVersion(modelType, version10);
		Introspector obj = loader.unmarshal("relationship", this.getJsonString("ambiguous-relationship.json"));
		URI expected = new URI("/aai/v10/network/test-objects/test-object/key1");
		
		thrown.expect(AmbiguousMapAAIException.class);
		thrown.expect(hasProperty("code", is("AAI_6146")));
		
		RelationshipToURI parse = new RelationshipToURI(loader, obj);
		
		URI uri = parse.getUri();
		
		assertEquals("related-link is equal", expected, uri);

		
	}
	
	@Test
	public void moreItemsThanRequired() throws AAIException, URISyntaxException, IOException {
		Loader loader = LoaderFactory.createLoaderForVersion(modelType, version10);
		Introspector obj = loader.unmarshal("relationship", this.getJsonString("too-many-items-relationship.json"));
		URI expected = new URI("/network/generic-vnfs/generic-vnf/key1/l-interfaces/l-interface/key2");
		
		RelationshipToURI parse = new RelationshipToURI(loader, obj);

		URI uri = parse.getUri();
		
		assertEquals("related-link is equal", expected.toString(), uri.toString());
		
	}
	
	@Test
	public void twoTopLevelNodes() throws AAIException, URISyntaxException, IOException {
		Loader loader = LoaderFactory.createLoaderForVersion(modelType, version10);
		Introspector obj = loader.unmarshal("relationship", this.getJsonString("two-top-level-relationship.json"));
		URI expected = new URI("/network/generic-vnfs/generic-vnf/key1/l-interfaces/l-interface/key2");
		
		thrown.expect(AmbiguousMapAAIException.class);
		thrown.expect(hasProperty("code", is("AAI_6146")));
		
		RelationshipToURI parse = new RelationshipToURI(loader, obj);
		
		URI uri = parse.getUri();
		
		assertEquals("related-link is equal", expected, uri);
		
	}
	
	@Test
	public void topLevelWithTwoKeys() throws AAIException, URISyntaxException, IOException {
		Loader loader = LoaderFactory.createLoaderForVersion(modelType, version10);
		Introspector obj = loader.unmarshal("relationship", this.getJsonString("top-level-two-keys-relationship.json"));
		URI expected = new URI("/cloud-infrastructure/cloud-regions/cloud-region/key1/key2/availability-zones/availability-zone/key3");
		
		RelationshipToURI parse = new RelationshipToURI(loader, obj);
		
		URI uri = parse.getUri();
		
		assertEquals("related-link is equal", expected.toString(), uri.toString());
		
	}
	
	
	private String getJsonString(String filename) throws IOException {
		
		
		FileInputStream is = new FileInputStream("src/test/resources/org/openecomp/aai/parsers/relationship/" + filename);
		String s =  IOUtils.toString(is, "UTF-8"); 
		IOUtils.closeQuietly(is);
		
		return s;
	}
}
