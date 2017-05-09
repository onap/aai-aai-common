/*-
 * ============LICENSE_START=======================================================
 * org.openecomp.aai
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.aai.parsers.uri;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBException;

import org.junit.BeforeClass;
import org.junit.Test;

import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.introspection.LoaderFactory;
import org.openecomp.aai.introspection.ModelType;
import org.openecomp.aai.introspection.Version;
import org.openecomp.aai.restcore.HttpMethod;


public class URIToExtensionInformationTest {

	private Loader v6Loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, Version.v8);
	private Loader v7Loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, Version.v8);
	
	/**
	 * Configure.
	 */
	@BeforeClass
	public static void configure() {
		System.setProperty("AJSC_HOME", "./src/test/resources/");
		System.setProperty("BUNDLECONFIG_DIR", "bundleconfig-local");
	}
	
	/**
	 * Vservers V 7.
	 *
	 * @throws JAXBException the JAXB exception
	 * @throws AAIException the AAI exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	@Test
    public void vserversV7() throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException {
		URI uri = UriBuilder.fromPath("/aai/" + v7Loader.getVersion() + "/cloud-infrastructure/cloud-regions/cloud-region/att-aic/AAIAIC25/tenants/tenant/key1/vservers/vserver/key2").build();
		URIToExtensionInformation parse = new URIToExtensionInformation(v7Loader, uri);
		
		String namespace = "cloudInfrastructure";
		String preMethodName = "DynamicAddCloudInfrastructureCloudRegionsCloudRegionTenantsTenantVserversVserverPreProc";
		String postMethodName = "DynamicAddCloudInfrastructureCloudRegionsCloudRegionTenantsTenantVserversVserverPostProc";
		String topLevel = "CloudRegion";
		testSpec(parse, HttpMethod.PUT, namespace, preMethodName, postMethodName, topLevel);
		
	}
	
	/**
	 * New vce V 6.
	 *
	 * @throws JAXBException the JAXB exception
	 * @throws AAIException the AAI exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	@Test
    public void newVceV6() throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException {
		URI uri = UriBuilder.fromPath("/aai/" + v6Loader.getVersion() + "/network/newvces/newvce/key1").build();
		URIToExtensionInformation parse = new URIToExtensionInformation(v6Loader, uri);
		
		String namespace = "network";
		String preMethodName = "DynamicDelNetworkNewvcesNewvcePreProc";
		String postMethodName = "DynamicDelNetworkNewvcesNewvcePostProc";
		String topLevel = "Newvce";
		testSpec(parse, HttpMethod.DELETE, namespace, preMethodName, postMethodName, topLevel);
		
	}
	
	/**
	 * New vce V 7.
	 *
	 * @throws JAXBException the JAXB exception
	 * @throws AAIException the AAI exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	@Test
    public void newVceV7() throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException {
		URI uri = UriBuilder.fromPath("/aai/" + v7Loader.getVersion() + "/network/newvces/newvce/key1").build();
		URIToExtensionInformation parse = new URIToExtensionInformation(v7Loader, uri);
		
		String namespace = "network";
		String preMethodName = "DynamicDelNetworkNewvcesNewvcePreProc";
		String postMethodName = "DynamicDelNetworkNewvcesNewvcePostProc";
		String topLevel = "Newvce";
		testSpec(parse, HttpMethod.DELETE, namespace, preMethodName, postMethodName, topLevel);
		
	}
	
	/**
	 * Test spec.
	 *
	 * @param info the info
	 * @param httpMethod the http method
	 * @param namespace the namespace
	 * @param preMethodName the pre method name
	 * @param postMethodName the post method name
	 * @param topLevel the top level
	 */
	private void testSpec(URIToExtensionInformation info, HttpMethod httpMethod, String namespace, String preMethodName, String postMethodName, String topLevel) {
		

		String namespaceResult = info.getNamespace();
		String methodNameResult = info.getMethodName(httpMethod, true);
		
		assertEquals("namespace", namespace, namespaceResult);
		assertEquals("preprocess method name", preMethodName, methodNameResult);
		methodNameResult = info.getMethodName(httpMethod, false);

		assertEquals("postprocess method name", postMethodName, methodNameResult);

		String topLevelResult = info.getTopObject();
		
		assertEquals("topLevel", topLevel, topLevelResult);
	}
	
	
}
