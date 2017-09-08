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

package org.openecomp.aai.parsers.uri;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openecomp.aai.AAISetup;
import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.*;
import org.powermock.core.classloader.annotations.PrepareForTest;

import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBException;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;


@Ignore
@PrepareForTest(ModelInjestor.class)
public class URIToDBKeyTest extends AAISetup {

	private Loader loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, Version.v8);

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
	@Test
    public void uri() throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException {
		URI uri = UriBuilder.fromPath("/aai/" + loader.getVersion() + "/cloud-infrastructure/tenants/tenant/key1/vservers/vserver/key2/l-interfaces/l-interface/key3").build();
		URIToDBKey parse = new URIToDBKey(loader, uri);
		Object result = parse.getResult();

		String expected = "cloud-region/att-aic/AAIAIC25/tenant/key1/vserver/key2/l-interface/key3";
		
		assertEquals("blah", expected, result);
		
	}
	
	/**
	 * Uri no version.
	 *
	 * @throws JAXBException the JAXB exception
	 * @throws AAIException the AAI exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	@Test
    public void uriNoVersion() throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException {
		URI uri = UriBuilder.fromPath("/cloud-infrastructure/tenants/tenant/key1/vservers/vserver/key2/l-interfaces/l-interface/key3").build();
		URIToDBKey parse = new URIToDBKey(loader, uri);
		Object result = parse.getResult();
		
		String expected = "cloud-region/att-aic/AAIAIC25/tenant/key1/vserver/key2/l-interface/key3";
		
		assertEquals("blah", expected, result);
		
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
		URI uri = UriBuilder.fromPath("/aai/" + loader.getVersion() + "/cloud-infrastructure/tenants/tenant/key1/vservers/vserver/key2/l-interadsfaces/l-interface/key3").build();
		
		thrown.expect(AAIException.class);
		thrown.expect(hasProperty("code",  is("AAI_3000")));
		
		new URIToDBKey(loader, uri);
	}
	
	/**
	 * No valid tokens.
	 *
	 * @throws JAXBException the JAXB exception
	 * @throws AAIException the AAI exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	@Test
    public void noValidTokens() throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException {
		URI uri = UriBuilder.fromPath("/aai/" + loader.getVersion() + "/cloud/blah/blah").build();
		
		thrown.expect(AAIException.class);
		thrown.expect(hasProperty("code",  is("AAI_3001")));
		
		new URIToDBKey(loader, uri);
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
		URI uri = UriBuilder.fromPath("/aai/" + loader.getVersion() + "/cloud-infrastructure/tenants/tenant/key1/vservers/vserver/key2/l-interfaces/l-interface/key3").build();
		
		URIToDBKey parse = new URIToDBKey(loader, uri);
		Object result = parse.getResult();

		String expected = "cloud-region/att-aic/AAIAIC25/tenant/key1/vserver/key2/l-interface/key3";
		
		assertEquals("blah", expected, result);
	}
	
	/**
	 * Naming exceptions.
	 *
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws AAIException the AAI exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	@Test
    public void namingExceptions() throws IllegalArgumentException, AAIException, UnsupportedEncodingException {
		URI uri = UriBuilder.fromPath("network/vces/vce/key1/port-groups/port-group/key2/cvlan-tags/cvlan-tag/655").build();
		URIToDBKey parse = new URIToDBKey(loader, uri);
		Object result = parse.getResult();

		String expected = "vce/key1/port-group/key2/cvlan-tag/655";
		
		assertEquals("blah", expected, result);
		
    }
		
}
