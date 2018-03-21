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
package org.onap.aai.serialization.queryformats;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.*;
import static org.junit.Assert.assertEquals;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class GraphSONTest {

	private Graph graph;
	private Vertex v1;
	
	//private JsonObject jsonObj = new JsonParser().parse("{\"id\":0,\"label\":\"vertex\",\"properties\":{\"name\":[{\"id\":1,\"value\":\"Sam\"}]}}").getAsJsonObject();	
	private JsonObject jsonObj = new JsonObject() ;
	private JsonObject properties = new JsonObject();
	private JsonArray name = new JsonArray() ;
	private JsonObject idVal = new JsonObject() ;
	
	@Before
	public void setUp() {
		
		jsonObj.addProperty("id", 0);
		jsonObj.addProperty("label", "vertex");
				
		idVal.addProperty("id", 1);
		idVal.addProperty("value", "Sam");
				
		name.add(idVal);
		properties.add("name",name);
		jsonObj.add("properties", properties);
				
		graph = TinkerGraph.open();
		v1 = graph.addVertex("name", "Sam");
			
	}
	
	@Test
	public void classGraphSONTestWithVertex(){
		
		GraphSON graphSonObj1 = new GraphSON();
		JsonObject obj = graphSonObj1.formatObject(v1);
				
		assertEquals(jsonObj, obj);
	}

	@Test
	public void parallelThresholdCehck(){
		
		GraphSON graphSonObj2 = new GraphSON();
		assertEquals(50, graphSonObj2.parallelThreshold());
	
	}


}
