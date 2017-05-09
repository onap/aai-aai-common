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

package org.openecomp.aai.serialization.queryformats;

import org.apache.tinkerpop.gremlin.structure.Vertex;

import org.openecomp.aai.db.props.AAIProperties;
import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Introspector;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.introspection.exceptions.AAIUnknownObjectException;
import org.openecomp.aai.serialization.queryformats.exceptions.AAIFormatVertexException;
import org.openecomp.aai.serialization.queryformats.utils.UrlBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class IdURL implements FormatMapper {

	private final UrlBuilder urlBuilder;
	private final JsonParser parser;
	private final Loader loader;

	public IdURL (Loader loader, UrlBuilder urlBuilder) throws AAIException {
		this.urlBuilder = urlBuilder;
		this.parser = new JsonParser();
		this.loader = loader;
	}
	
	@Override
	public int parallelThreshold() {
		return 2500;
	}

	@Override
	public JsonObject formatObject(Object input) throws AAIFormatVertexException {
		Vertex v = (Vertex)input;
		try {
			final Introspector searchResult = this.loader.introspectorFromName("result-data");

			searchResult.setValue("resource-type", v.value(AAIProperties.NODE_TYPE));
			searchResult.setValue("resource-link", this.urlBuilder.id(v));

			final String json = searchResult.marshal(false);

			return parser.parse(json).getAsJsonObject();
			
		} catch (AAIUnknownObjectException e) {
			throw new RuntimeException("Fatal error - result-data object does not exist!");
		}
			
		
	}
}
