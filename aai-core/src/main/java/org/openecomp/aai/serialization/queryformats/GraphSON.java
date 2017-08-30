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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thinkaurelius.titan.graphdb.tinkerpop.TitanIoRegistry;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONMapper;
import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class GraphSON implements FormatMapper {

	private final GraphSONMapper mapper = GraphSONMapper.build().addRegistry(TitanIoRegistry.INSTANCE).create();
	private final GraphSONWriter writer = GraphSONWriter.build().mapper(mapper).create();
	protected JsonParser parser = new JsonParser();
	
	@Override
	public JsonObject formatObject(Object v) {
		OutputStream os = new ByteArrayOutputStream();
		String result = "";
		try {
			writer.writeVertex(os, (Vertex)v, Direction.BOTH);
			
			result = os.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return parser.parse(result).getAsJsonObject();
		
	}
	
	@Override
	public int parallelThreshold() {
		return 50;
	}
}
