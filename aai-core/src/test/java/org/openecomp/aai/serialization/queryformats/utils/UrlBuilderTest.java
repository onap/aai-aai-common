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

package org.openecomp.aai.serialization.queryformats.utils;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openecomp.aai.AAISetup;
import org.openecomp.aai.introspection.Version;
import org.openecomp.aai.serialization.db.DBSerializer;
import org.openecomp.aai.serialization.queryformats.exceptions.AAIFormatVertexException;
import org.openecomp.aai.util.AAIConstants;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class UrlBuilderTest extends AAISetup {

	@Mock
	private DBSerializer serializer;
	@Mock
	private Vertex v;

	private static final String uri = "/test/uri";
	private static final Object vId = new Long(123);
	private static final String protocolAndHost = "http://localhost/aai/";

	@Before
	public void before() throws UnsupportedEncodingException, URISyntaxException {
		MockitoAnnotations.initMocks(this);
		when(serializer.getURIForVertex(any(Vertex.class))).thenReturn(new URI(uri));
		when(v.id()).thenReturn(vId);
	}

	@Test
	public void v11Pathed() throws UnsupportedEncodingException, URISyntaxException, AAIFormatVertexException {
		Version version = Version.v11;
		UrlBuilder builder = new UrlBuilder(version, serializer, protocolAndHost);
		String result = builder.pathed(v);
		
		assertEquals("has no protocol and host", AAIConstants.AAI_APP_ROOT + version + uri, result);
		
	}

	@Test
	public void v11Id() throws UnsupportedEncodingException, URISyntaxException, AAIFormatVertexException {
		Version version = Version.v11;
		UrlBuilder builder = new UrlBuilder(version, serializer, protocolAndHost);
		String result = builder.id(v);
		
		assertEquals("has no protocol and host", AAIConstants.AAI_APP_ROOT + version + "/resources/id/" + vId, result);
		
	}
	
	@Test
	public void beforeV11Pathed() throws UnsupportedEncodingException, URISyntaxException, AAIFormatVertexException {
		Version version = Version.v10;
		UrlBuilder builder = new UrlBuilder(version, serializer, protocolAndHost);
		String result = builder.pathed(v);
		
		assertEquals("has protocol and host", protocolAndHost + version + uri, result);
		
	}
	
	@Test
	public void beforeV11Id() throws UnsupportedEncodingException, URISyntaxException, AAIFormatVertexException {
		Version version = Version.v10;
		UrlBuilder builder = new UrlBuilder(version, serializer, protocolAndHost);
		String result = builder.id(v);
		
		assertEquals("has protocol and host", protocolAndHost + version + "/resources/id/" + vId, result);
		
	}
	
}
