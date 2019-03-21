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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONMapper;
import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONWriter;
import org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry;

public class GraphSON implements FormatMapper {

    private final GraphSONMapper mapper =
            GraphSONMapper.build().addRegistry(JanusGraphIoRegistry.getInstance()).create();
    private final GraphSONWriter writer = GraphSONWriter.build().mapper(mapper).create();
    protected JsonParser parser = new JsonParser();

    @Override
    public Optional<JsonObject> formatObject(Object v) {
        OutputStream os = new ByteArrayOutputStream();
        String result = "";
        try {
            writer.writeVertex(os, (Vertex) v, Direction.BOTH);

            result = os.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        JsonObject jsonObject = parser.parse(result).getAsJsonObject();

        if (jsonObject != null) {

            if (jsonObject.has("outE")) {
                JsonObject outEdges = jsonObject.get("outE").getAsJsonObject();
                removePrivateEdges(jsonObject, outEdges, "outE");
            }

            if (jsonObject.has("inE")) {
                JsonObject inEdges = jsonObject.get("inE").getAsJsonObject();
                removePrivateEdges(jsonObject, inEdges, "inE");
            }

        }

        return Optional.of(jsonObject);

    }

    /**
     * Removes the private edges from the json object
     *
     * Please note that the reason to choose to remove the private
     * edges from the json object instead of removing it from the vertex
     * itself is the fact that even though the transaction will be rolled back
     * is because of the possible incosistent behavior where the actual edge
     * might actually be removed in a long running transaction and is not worth the risk
     *
     * @param jsonObject - JSON Object from which we are removing the private edges for
     * @param edges - JSONObject HashMap representing all of the edges
     * @param edgeDirection - a string indicating the direction of the edge
     */
    private void removePrivateEdges(JsonObject jsonObject, JsonObject edges, String edgeDirection) {

        Iterator it = edges.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, JsonElement> outEntry = (Map.Entry<String, JsonElement>) it.next();
            JsonArray edgePropertiesArray = outEntry.getValue().getAsJsonArray();
            for (int index = 0; index < edgePropertiesArray.size(); ++index) {
                JsonElement jsonElement = edgePropertiesArray.get(index);
                JsonObject obj = jsonElement.getAsJsonObject();
                if (obj.has("properties")) {
                    JsonObject objProperties = obj.get("properties").getAsJsonObject();
                    if (objProperties.has("private")) {
                        boolean isPrivate = objProperties.get("private").getAsBoolean();
                        if (isPrivate) {
                            if (edges.size() == 1) {
                                if (edgePropertiesArray.size() == 1) {
                                    jsonObject.remove(edgeDirection);
                                } else {
                                    edgePropertiesArray.remove(jsonElement);
                                }
                            } else {
                                edgePropertiesArray.remove(jsonElement);
                            }
                        }
                    }
                }
            }
            if (edgePropertiesArray.size() == 0) {
                it.remove();
            }
        }
    }

    @Override
    public int parallelThreshold() {
        return 50;
    }
}
