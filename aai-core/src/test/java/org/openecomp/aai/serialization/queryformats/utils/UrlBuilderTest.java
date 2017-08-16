package org.openecomp.aai.serialization.queryformats.utils;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openecomp.aai.introspection.Version;
import org.openecomp.aai.serialization.db.DBSerializer;
import org.openecomp.aai.serialization.queryformats.exceptions.AAIFormatVertexException;
import org.openecomp.aai.util.AAIConstants;

public class UrlBuilderTest {

	@Mock private DBSerializer serializer;
	@Mock private Vertex v;
	private static final String uri = "/test/uri";
	private static final Object vId = new Long(123);
	private static final String protocolAndHost = "http://localhost/aai/";
	@BeforeClass
	public static void setUp() {
		System.setProperty("AJSC_HOME", ".");
		System.setProperty("BUNDLECONFIG_DIR", "src/test/resources/bundleconfig-local");

	}
	
	@Before
	public void before() throws UnsupportedEncodingException, URISyntaxException {
		MockitoAnnotations.initMocks(this);
		when(serializer.getURIForVertex(any(Vertex.class))).thenReturn(new URI(uri));
		when(v.id()).thenReturn(vId);
	}

	@Ignore
	@Test
	public void v11Pathed() throws UnsupportedEncodingException, URISyntaxException, AAIFormatVertexException {
		Version version = Version.v11;
		UrlBuilder builder = new UrlBuilder(version, serializer, protocolAndHost);
		String result = builder.pathed(v);
		
		assertEquals("has no protocol and host", AAIConstants.AAI_APP_ROOT + version + uri, result);
		
	}

	@Ignore
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
