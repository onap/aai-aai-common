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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.Tree;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatQueryResultFormatNotSupported;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatVertexException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MultiFormatMapper implements FormatMapper {

    private static final Logger logger = LoggerFactory.getLogger(MultiFormatMapper.class);

    protected boolean isTree = false;
    protected static final String PROPERTIES_KEY = "properties";
    protected static final String NODE_TYPE_KEY = "node-type";

    protected static final String RETURNED_EMPTY_JSONARRAY_MSG =
        "Returned empty JsonArray - Could not populate nested json objects for wrapper: {}";

    @Override
    public Optional<JsonObject> formatObject(Object input)
        throws AAIFormatVertexException, AAIFormatQueryResultFormatNotSupported {
        if (input instanceof Vertex) {
            logger.debug("Formatting vertex object");
            return this.getJsonFromVertex((Vertex) input);
        } else if (input instanceof Tree) {
            logger.debug("Formatting tree object");
            if (isTree) {
                return this.getRelatedNodesFromTree((Tree<?>) input, null);
            } else {
                return this.getJsonFromTree((Tree<?>) input);
            }
        } else if (input instanceof Path) {
            logger.debug("Formatting path object");
            return this.getJsonFromPath((Path) input);
        } else {
            throw new AAIFormatQueryResultFormatNotSupported();
        }
    }

    @Override
    public Optional<JsonObject> formatObject(Object input, Map<String, List<String>> properties)
        throws AAIFormatVertexException, AAIFormatQueryResultFormatNotSupported {
        if (input instanceof Vertex) {
            logger.debug("Formatting vertex object with properties map filter");
            return this.getJsonFromVertex((Vertex) input, properties);
        } else if (input instanceof Tree) {
            logger.debug("Formatting tree object with properties map filter");
            if (isTree) {
                return this.getRelatedNodesFromTree((Tree<?>) input, properties);
            } else {
                return this.getJsonFromTree((Tree<?>) input);
            }
        } else if (input instanceof Path) {
            logger.debug("Formatting path object");
            return this.getJsonFromPath((Path) input);
        } else {
            throw new AAIFormatQueryResultFormatNotSupported();
        }
    }

    protected abstract Optional<JsonObject> getJsonFromVertex(Vertex input) throws AAIFormatVertexException;
    protected abstract Optional<JsonObject> getJsonFromVertex(Vertex input, Map<String, List<String>> properties) throws AAIFormatVertexException;

    protected Optional<JsonObject> getJsonFromPath(Path input) throws AAIFormatVertexException {
        List<Object> path = input.objects();

        JsonObject jo = new JsonObject();
        JsonArray ja = new JsonArray();

        for (Object o : path) {
            if (o instanceof Vertex) {
                Optional<JsonObject> obj = this.getJsonFromVertex((Vertex) o);
                obj.ifPresent(ja::add);
            }
        }

        jo.add("path", ja);
        return Optional.of(jo);
    }

    /**
     * Returns an Optional<JsonObject> object using "nodes" as a wrapper to encapsulate json objects
     * @param tree
     * @return
     * @throws AAIFormatVertexException
     */
    protected Optional<JsonObject> getJsonFromTree(Tree<?> tree) throws AAIFormatVertexException {
        if (tree.isEmpty()) {
            return Optional.of(new JsonObject());
        }
        String nodeIdentifier = "nodes";

        JsonObject t = new JsonObject();
        JsonArray ja = this.getNodesArray(tree, null, nodeIdentifier);
        if (ja.size() > 0) {
            t.add("nodes", ja);
        } else {
            logger.debug(RETURNED_EMPTY_JSONARRAY_MSG, nodeIdentifier);
        }

        return Optional.of(t);
    }

    /**
     * Returns an Optional<JsonObject> object using "related-nodes" to encapsulate nested json objects.
     * Primarily intended to be utilized by the "as-tree" query parameter feature
     * @param tree
     * @param properties
     * @return
     * @throws AAIFormatVertexException
     */
    protected Optional<JsonObject> getRelatedNodesFromTree(Tree<?> tree, Map<String, List<String>> properties) throws AAIFormatVertexException {
        if (tree.isEmpty()) {
            return Optional.of(new JsonObject());
        }
        String nodeIdentifier = "related-nodes";

        // Creating another DS to help with calls in O(1)
        Map<String, Set<String>> filterPropertiesMap = createFilteredPropertyMap(properties);

        JsonObject t = new JsonObject();
        JsonArray ja = this.getNodesArray(tree, filterPropertiesMap, nodeIdentifier);
        if (ja.size() > 0) {
            t.add("results", ja);
            return Optional.of(t);
        } else {
            logger.debug(RETURNED_EMPTY_JSONARRAY_MSG, nodeIdentifier);
        }

        return Optional.empty();
    }

    /**
     * Returns JsonArray Object populated with nested json wrapped by the nodeIdentifier parameter
     * @param tree
     * @param filterPropertiesMap
     * @param nodeIdentifier
     * @return
     * @throws AAIFormatVertexException
     */
    protected JsonArray getNodesArray(Tree<?> tree, Map<String, Set<String>> filterPropertiesMap, String nodeIdentifier) throws AAIFormatVertexException {
        JsonArray nodes = new JsonArray();
        for (Map.Entry<?, ? extends Tree<?>> entry : tree.entrySet()) {
            JsonObject me = new JsonObject();
            if (entry.getKey() instanceof Vertex) {
                Optional<JsonObject> obj = this.getJsonFromVertex((Vertex) entry.getKey());
                if (obj.isPresent()) {
                    me = getPropertyFilteredObject(obj, filterPropertiesMap);
                } else {
                    continue;
                }
            }
            JsonArray ja = this.getNodesArray(entry.getValue(), filterPropertiesMap, nodeIdentifier);
            if (ja.size() > 0) {
                me.add(nodeIdentifier, ja);
            } else {
                logger.debug(RETURNED_EMPTY_JSONARRAY_MSG, nodeIdentifier);
            }
            nodes.add(me);
        }
        return nodes;
    }

    /**
     * Returns a Map<String, Set<String>> object through converting given map parameter
     * @param properties
     * @return
     */
    protected Map<String, Set<String>> createFilteredPropertyMap(Map<String, List<String>> properties) {
        if (properties == null)
            return new HashMap<>();

        return properties.entrySet().stream()
            .map(entry -> {
                    Set<String> newSet = entry.getValue().stream()
                        .map(this::truncateApostrophes)
                        .collect(Collectors.toSet());

                    return Pair.of(entry.getKey(), newSet);
                }
            ).collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    /**
     * Returns a string with it's apostrophes truncated at the start and end.
     * @param s
     * @return
     */
    protected String truncateApostrophes(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        if (s.startsWith("'") && s.endsWith("'")) {
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }

    /**
     * Filters the given Optional<JsonObject> with the properties under a properties field
     * or the properties under its respective node type.
     * @param obj
     * @param filterPropertiesMap
     * @return
     */
    protected JsonObject getPropertyFilteredObject(Optional<JsonObject> obj,
        Map<String, Set<String>> filterPropertiesMap) {
        return obj.map(
            jsonObj -> {
                if (filterPropertiesMap == null || filterPropertiesMap.isEmpty()) {
                    return jsonObj;
                } else {
                    ImmutableTriple<JsonObject, Optional<String>, Optional<JsonObject>> triple =
                        cloneObjectAndExtractNodeTypeAndProperties(jsonObj);

                    JsonObject result = triple.left;
                    Optional<String> nodeType = triple.middle;
                    Optional<JsonObject> properties = triple.right;

                    // Filter current object based on it containing fields: "node-type" and "properties"
                    if (nodeType.isPresent() && properties.isPresent()) {
                        filterByNodeTypeAndProperties(result, nodeType.get(), properties.get(), filterPropertiesMap);
                    } else {
                        // filter current object based on the: key - nodeType & value - JsonObject of nodes properties
                        filterByJsonObj(result, jsonObj, filterPropertiesMap);
                    }

                    return result;
                }
            }
        ).orElseGet(JsonObject::new);
    }

    private ImmutableTriple<JsonObject, Optional<String>, Optional<JsonObject>> cloneObjectAndExtractNodeTypeAndProperties(
        JsonObject jsonObj) {
        JsonObject result = new JsonObject();
        Optional<String> nodeType = Optional.empty();
        Optional<JsonObject> properties = Optional.empty();

        // clone object
        for (Map.Entry<String, JsonElement> mapEntry : jsonObj.entrySet()) {
            String key = mapEntry.getKey();
            JsonElement value = mapEntry.getValue();

            // also, check if payload has node-type and properties fields
            if (key.equals(NODE_TYPE_KEY) && value != null) {
                nodeType = Optional.of(value.getAsString());
            } else if (key.equals(PROPERTIES_KEY) && value != null && value.isJsonObject()) {
                properties = Optional.of(value.getAsJsonObject());
            }
            result.add(key, value);
        }

        return ImmutableTriple.of(result, nodeType, properties);
    }

    /**
     * Returns a JsonObject with filtered properties using "node-type" and "properties"
     * Used for formats with payloads similar to simple and raw
     * @param result
     * @param nodeType
     * @param properties
     * @param filterPropertiesMap
     * @return
     */
    private JsonObject filterByNodeTypeAndProperties(JsonObject result, String nodeType, JsonObject properties, Map<String, Set<String>> filterPropertiesMap) {
        if (result == null || nodeType == null || nodeType.isEmpty() || properties == null || filterPropertiesMap == null) {
            return result;
        }
        if (filterPropertiesMap.containsKey(nodeType)) {    // filterPropertiesMap keys are nodeTypes - keys are obtained from the incoming query request
            Set<String> filterSet = filterPropertiesMap.get(nodeType);
            JsonObject filteredProperties = new JsonObject();
            for (String property : filterSet) {             // Each nodeType should have a set of properties to be retained in the response
                if (properties.get(property) != null) {
                    filteredProperties.add(property, properties.get(property));
                }
            }
            result.remove(PROPERTIES_KEY);
            result.add(PROPERTIES_KEY, filteredProperties);
        }
        return result;
    }

    /**
     * Returns a JsonObject with its properties filtered
     * @param result
     * @param jsonObj
     * @param filterPropertiesMap
     * @return
     */
    private JsonObject filterByJsonObj(JsonObject result, JsonObject jsonObj, Map<String, Set<String>> filterPropertiesMap) {
        if (result == null || jsonObj == null || filterPropertiesMap == null) {
            return result;
        }

        for (Map.Entry<String, JsonElement> mapEntry : jsonObj.entrySet()) {
            String key = mapEntry.getKey();
            JsonElement value = mapEntry.getValue();
            JsonObject filteredProperties = new JsonObject();
            if (value != null && value.isJsonObject() && filterPropertiesMap.containsKey(key)) {
                JsonObject joProperties = value.getAsJsonObject();
                Set<String> filterSet = filterPropertiesMap.get(key);
                for (String property : filterSet) {
                    if (joProperties.get(property) != null) {
                        filteredProperties.add(property, joProperties.get(property));
                    }
                }
                result.remove(key);
                result.add(key, filteredProperties);
            }
        }
        return result;
    }

    /**
     * Returns a filtered JsonObject with properties contained in the parameter filterPropertiesMap
     * @param properties
     * @param filterPropertiesMap
     * @return
     */
    protected JsonObject filterProperties(Optional<JsonObject> properties, String nodeType,
        Map<String, Set<String>> filterPropertiesMap) {
        return properties.map(jo -> {
            if (filterPropertiesMap == null || filterPropertiesMap.isEmpty()) {
                return properties.get();
            }

            JsonObject result = new JsonObject();
            // clone the object
            for (Map.Entry<String, JsonElement> mapEntry : jo.entrySet()) {
                String key = mapEntry.getKey();
                JsonElement value = mapEntry.getValue();
                result.add(key, value);
            }

            // filter the object
            if (filterPropertiesMap.containsKey(nodeType)) {
                Set<String> filterSet = filterPropertiesMap.get(nodeType);
                for (Map.Entry<String, JsonElement> mapEntry : jo.entrySet()) {
                    String key = mapEntry.getKey();
                    if (!filterSet.contains(key)) {
                        result.remove(key);
                    }
                }
            }
            return result;
        }).orElseGet(JsonObject::new);
    }

    @Override
    public int parallelThreshold() {
        return 100;
    }

}
