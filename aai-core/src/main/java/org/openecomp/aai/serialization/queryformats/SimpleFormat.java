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

package org.openecomp.aai.serialization.queryformats;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.openecomp.aai.db.props.AAIProperties;
import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Introspector;
import org.openecomp.aai.introspection.exceptions.AAIUnknownObjectException;
import org.openecomp.aai.serialization.queryformats.exceptions.AAIFormatVertexException;

import com.google.gson.JsonObject;

public class SimpleFormat extends RawFormat {


	protected SimpleFormat(Builder builder) {
		super(builder);
	
	}
	
	@Override
	public int parallelThreshold() {
		return 20;
	}
	
	@Override
	public JsonObject createPropertiesObject(Vertex v) throws AAIFormatVertexException {
		try {
			final Introspector obj = loader.introspectorFromName(
										v.<String>property(AAIProperties.NODE_TYPE)
											.orElse(null)
									 );

			final List<Vertex> wrapper = new ArrayList<>();

			wrapper.add(v);

			try {
				serializer.dbToObject(wrapper, obj, this.depth, true, "false");
			} catch (AAIException | UnsupportedEncodingException  e) {
				throw new AAIFormatVertexException("Failed to format vertex - error while serializing: " + e.getMessage(), e);
			}

			final String json = obj.marshal(false);
			return parser.parse(json).getAsJsonObject();
		} catch (AAIUnknownObjectException e) {
			throw new AAIFormatVertexException("Failed to format vertex - unknown object", e);
		}
		

	}
}
