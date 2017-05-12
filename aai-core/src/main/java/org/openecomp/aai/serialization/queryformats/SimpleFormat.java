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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;

import org.openecomp.aai.db.props.AAIProperties;
import org.openecomp.aai.serialization.queryformats.exceptions.AAIFormatVertexException;
import org.openecomp.aai.serialization.queryformats.utils.UrlBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class SimpleFormat implements FormatMapper {

	private final UrlBuilder urlBuilder;
	private final Set<String> blacklist;
	private final Collection<String> props = Arrays.asList(AAIProperties.AAI_URI, AAIProperties.NODE_TYPE);
	public SimpleFormat(UrlBuilder urlBuilder) {
		this.urlBuilder = urlBuilder;
		this.blacklist = new HashSet<>();
		blacklist.addAll(props);
	}
	
	@Override
	public JsonObject formatObject(Object input) throws AAIFormatVertexException {
		Vertex v = (Vertex)input;
		JsonObject json = new JsonObject();
		json.addProperty("id", v.id().toString());
		json.addProperty("node-type", v.<String>value(AAIProperties.NODE_TYPE));
		json.addProperty("url", this.urlBuilder.pathed(v));
		json.add("properties", this.createPropertiesObject(v));
		json.add("related-to", this.createRelationshipObject(v));
		
		return json;
	}

	@Override
	public int parallelThreshold() {
		return 100;
	}
	
	private JsonObject createPropertiesObject(Vertex v) {
		JsonObject json = new JsonObject();
		Iterator<VertexProperty<Object>> iter = v.properties();
		
		while (iter.hasNext()) {
			VertexProperty<Object> prop = iter.next();
			if (!blacklist.contains(prop.key())) {
				if (prop.value() instanceof String) {
					json.addProperty(prop.key(), (String)prop.value());
				} else if (prop.value() instanceof Boolean) {
					json.addProperty(prop.key(), (Boolean)prop.value());
				} else if (prop.value() instanceof Number) {
					json.addProperty(prop.key(), (Number)prop.value());
				} else if (prop.value() instanceof List) {
					Gson gson = new Gson();
					String list = gson.toJson(prop.value());
					
					json.addProperty(prop.key(), list);
				} else {
					//throw exception?
					return null;
				}
			}
		}
		
		return json;
	}
	
	private JsonArray createRelationshipObject(Vertex v) throws AAIFormatVertexException {
		JsonArray jarray = new JsonArray();
		Iterator<Vertex> iter = v.vertices(Direction.BOTH);
		
		while (iter.hasNext()) {
			Vertex related = iter.next();
			
			JsonObject json = new JsonObject();
			json.addProperty("id", related.id().toString());
			json.addProperty("node-type", related.<String>value(AAIProperties.NODE_TYPE));
			json.addProperty("url", this.urlBuilder.pathed(related));
			jarray.add(json);
		}
		
		return jarray;
	}
}
