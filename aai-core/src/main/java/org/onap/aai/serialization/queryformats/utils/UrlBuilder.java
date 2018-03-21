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
package org.onap.aai.serialization.queryformats.utils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Version;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatVertexException;
import org.onap.aai.util.AAIApiServerURLBase;
import org.onap.aai.util.AAIConstants;
import org.onap.aai.workarounds.LegacyURITransformer;

public class UrlBuilder {

	private final DBSerializer serializer;
	private final Version version;
	private final String serverBase;
	
	public UrlBuilder (Version version, DBSerializer serializer) throws AAIException {
		this.serializer = serializer;
		this.version = version;
		this.serverBase = this.getServerBase(version);
	}
	
	public UrlBuilder (Version version, DBSerializer serializer, String serverBase) {
		this.serializer = serializer;
		this.version = version;
		this.serverBase = serverBase;
	}
	
	public String pathed(Vertex v) throws AAIFormatVertexException {

		try {
			final StringBuilder result = new StringBuilder();
			final URI uri = this.serializer.getURIForVertex(v);
			
			if (this.version.compareTo(Version.v11) >= 0) {
				result.append(AAIConstants.AAI_APP_ROOT);
			} else {
				result.append(this.serverBase);
			}
			result.append(this.version);
			result.append(uri);

				return result.toString();
		} catch (UnsupportedEncodingException | IllegalArgumentException | SecurityException e) {
			throw new AAIFormatVertexException(e);
		}
	}
	
	public String id(Vertex v) {
		final StringBuilder result = new StringBuilder();

		result.append("/resources/id/" + v.id());
		result.insert(0, this.version);
		if (this.version.compareTo(Version.v11) >= 0) {
			result.insert(0, AAIConstants.AAI_APP_ROOT);
		} else {
			result.insert(0, this.serverBase);
		}

		return result.toString();
	}
	
	protected String getServerBase(Version v) throws AAIException {
		return AAIApiServerURLBase.get(v);
	}
}
