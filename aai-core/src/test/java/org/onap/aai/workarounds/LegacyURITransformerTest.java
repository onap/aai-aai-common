/*-
 * ============LICENSE_START=======================================================
 * org.onap.aai
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

package org.onap.aai.workarounds;

import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.introspection.Version;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;


public class LegacyURITransformerTest extends AAISetup {

	private LegacyURITransformer uriTransformer = LegacyURITransformer.getInstance();
	private String fromSuccess = "http://myhostname.com:8443/aai/{version}/cloud-infrastructure/tenants/tenant/key1/vservers/vserver/key2";
	private String toSuccess = "http://myhostname.com:8443/aai/servers/{version}/key1/vservers/key2";


	/**
	 * V 5.
	 * @throws URISyntaxException 
	 *
	 * @throws MalformedURLException the malformed URL exception
	 */
	@Test
	public void v5() throws URISyntaxException {
		testSpec(Version.v8, fromSuccess, fromSuccess);
	}
	

	/**
	 * Test spec.
	 *
	 * @param version the version
	 * @param toExpected the to expected
	 * @param fromExpected the from expected
	 * @throws URISyntaxException 
	 * @throws MalformedURLException the malformed URL exception
	 */
	public void testSpec(Version version, String toExpected, String fromExpected) throws URISyntaxException   {
		
		URI toExpectedUri = new URI(toExpected.replace("{version}",version.toString()));
		URI fromExpectedUri = new URI(fromExpected.replace("{version}",version.toString()));
		
		URI result = toLegacyURISpec(version, fromExpectedUri);

		assertEquals("to", toExpectedUri, result);
		
		result = fromLegacyURISpec(version, toExpectedUri);

		assertEquals("from", fromExpectedUri, result);
	}
	
	
    /**
     * To legacy URL spec.
     *
     * @param version the version
     * @param url the url
     * @return the url
     * @throws URISyntaxException 
     * @throws MalformedURLException the malformed URL exception
     */
    public URI toLegacyURISpec(Version version, URI uri) throws URISyntaxException  {
    	return uri;
	} 
    
    /**
     * From legacy URL spec.
     *
     * @param version the version
     * @param url the url
     * @return the url
     * @throws URISyntaxException 
     * @throws MalformedURLException the malformed URL exception
     */
    public URI fromLegacyURISpec(Version version, URI uri) throws URISyntaxException  {
		return uri;
    }
	
	
}
