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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.logging.LogFormatTools;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatQueryResultFormatNotSupported;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatVertexException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class LifecycleFormat extends HistoryFormat {

    private static final Logger LOGGER = LoggerFactory.getLogger(LifecycleFormat.class);

    protected LifecycleFormat(Builder builder) {
        super(builder);
    }

    protected JsonArray createPropertiesObject(Vertex v) {
        JsonArray jsonArray = new JsonArray();
        Iterator<VertexProperty<Object>> iter = v.properties();
        List<JsonObject> jsonList = new ArrayList<>();

        Map<String, Set<Long>> propStartTimes = new HashMap<>(); //vertex end
        while (iter.hasNext()) {
            JsonObject json = new JsonObject();
            VertexProperty<Object> prop = iter.next();
            if(prop.key() != null && ignoredKeys.contains(prop.key())){
                continue;
            }
            if (!propStartTimes.containsKey(prop.key())) {
                propStartTimes.put(prop.key(), new HashSet<>());
                if (v.property(AAIProperties.END_TS).isPresent()) {
                    propStartTimes.get(prop.key()).add(v.<Long>value(AAIProperties.END_TS));
                }
            }

            json.addProperty(KEY, prop.key());
            json = mapPropertyValues(json, VALUE, prop.value());
            JsonObject metaProperties = createMetaPropertiesObject(prop);
            if (isTsInRange(metaProperties.get(AAIProperties.START_TS).getAsLong())) {
                JsonObject jo = new JsonObject();
                jo.add(KEY, json.get(KEY));
                jo.add(VALUE, json.get(VALUE));
                jo.add(TIMESTAMP, metaProperties.get(AAIProperties.START_TS));
                jo.add(SOT, metaProperties.get(AAIProperties.SOURCE_OF_TRUTH));
                jo.add(TX_ID, metaProperties.get(AAIProperties.START_TX_ID));
                jsonList.add(jo);
                propStartTimes.get(prop.key()).add(metaProperties.get(AAIProperties.START_TS).getAsLong());
            }
            if (!AAIProperties.RESOURCE_VERSION.equals(prop.key())
                && metaProperties.has(AAIProperties.END_TS)
                && isTsInRange(metaProperties.get(AAIProperties.END_TS).getAsLong())
                && !propStartTimes.get(prop.key()).contains(metaProperties.get(AAIProperties.END_TS).getAsLong())) {
                JsonObject jo = new JsonObject();
                jo.add(KEY, json.get(KEY));
                jo.add(VALUE, null);
                jo.add(TIMESTAMP, metaProperties.get(AAIProperties.END_TS));
                jo.add(SOT, metaProperties.get(AAIProperties.END_SOT));
                jo.add(TX_ID, metaProperties.get(AAIProperties.END_TX_ID));
                jsonList.add(jo);
            }

        }
        jsonList.stream()
            // remove all the null values that is the start time for another value
            .filter(jo -> !jo.get(VALUE).isJsonNull() || !propStartTimes.get(jo.get(KEY).getAsString()).contains(jo.get(TIMESTAMP).getAsLong()))
            // sort by ts in decreasing order
            .sorted((o1, o2) -> {
                if (o1.get(TIMESTAMP).getAsLong() == o2.get(TIMESTAMP).getAsLong()) {
                    return o1.get(KEY).getAsString().compareTo(o2.get(KEY).getAsString());
                } else {
                    return Long.compare(o2.get(TIMESTAMP).getAsLong(), o1.get(TIMESTAMP).getAsLong());
                }
            }).forEach(jsonArray::add);

        return jsonArray;
    }

    private boolean isTsInRange(long ts) {
        return ts >= startTs && ts <= endTs;
    }


    @Override
    protected boolean isValidEdge(Edge e) {
        if (e.property(AAIProperties.END_TS).isPresent()) {
            long edgeStartTs = e.<Long>value(AAIProperties.START_TS);
            long edgeEndTs = e.<Long>value(AAIProperties.END_TS);
            return isTsInRange(edgeStartTs) || isTsInRange(edgeEndTs);
        } else {
            long edgeStartTs = e.<Long>value(AAIProperties.START_TS);
            return isTsInRange(edgeStartTs);
        }
    }

    @Override
    protected JsonObject getRelatedObject(Edge e, Vertex related) throws AAIFormatVertexException {

        JsonObject json = new JsonObject();
        json.addProperty("relationship-label", e.label());
        json.addProperty("node-type", related.<String>value(AAIProperties.NODE_TYPE));
        json.addProperty("url", this.urlBuilder.pathed(related));
        if (related.property(AAIProperties.AAI_URI).isPresent()) {
            json.addProperty("uri", related.<String>value(AAIProperties.AAI_URI));
        } else {
            LOGGER.warn("Vertex {} is missing aai-uri", related.id());
            json.addProperty("uri", "NA");
        }

        if(e.property(AAIProperties.START_TS).isPresent()) {
            long edgeStartTimestamp = e.<Long>value(AAIProperties.START_TS);
            if (isTsInRange(edgeStartTimestamp)) {
                json.addProperty(TIMESTAMP,  e.property(AAIProperties.START_TS).isPresent()? e.<Long>value(AAIProperties.START_TS) : 0);
                json.addProperty(SOT, e.property(AAIProperties.SOURCE_OF_TRUTH).isPresent()? e.value(AAIProperties.SOURCE_OF_TRUTH) : "");
                json.addProperty(TX_ID, e.property(AAIProperties.START_TX_ID).isPresent()? e.value(AAIProperties.START_TX_ID) : "N/A");
            }
        }

        if(e.property(AAIProperties.END_TS).isPresent()) {
            long edgeEndTimestamp = e.<Long>value(AAIProperties.END_TS);
            if (isTsInRange(edgeEndTimestamp)) {
                json.addProperty(END_TIMESTAMP, edgeEndTimestamp);
                json.addProperty(END_SOT, e.property(AAIProperties.END_SOT).isPresent() ? e.value(AAIProperties.END_SOT) : "");
                json.addProperty(END_TX_ID, e.property(AAIProperties.END_TX_ID).isPresent() ? e.value(AAIProperties.END_TX_ID) : "N/A");
            }
        }

        return json;
    }

    @Override
    protected Optional<JsonObject> getJsonFromVertex(Vertex v) throws AAIFormatVertexException {
        JsonObject json = new JsonObject();
        json.addProperty(NODE_TYPE, v.<String>value(AAIProperties.NODE_TYPE));
        json.addProperty("url", this.urlBuilder.pathed(v));
        if (v.property(AAIProperties.AAI_URI).isPresent()) {
            json.addProperty("uri", v.<String>value(AAIProperties.AAI_URI));
        } else {
            LOGGER.warn("Vertex {} is missing aai-uri", v.id());
            json.addProperty("uri", "NA");
        }
        json.addProperty(TIMESTAMP, v.<Long>value(AAIProperties.START_TS));

        json.add(PROPERTIES, this.createPropertiesObject(v));

        if (!nodesOnly) {
            json.add(RELATED_TO, this.createRelationshipObject(v));
        }

        json.add(NODE_ACTIONS, getNodeActions(v, json));

        if (json.getAsJsonObject().get(PROPERTIES).getAsJsonArray().size() == 0
            && json.getAsJsonObject().get(RELATED_TO).getAsJsonArray().size() == 0
            && json.getAsJsonObject().get(NODE_ACTIONS).getAsJsonArray().size() == 0) {
            return Optional.empty();
        } else if (json.getAsJsonObject().get(PROPERTIES).getAsJsonArray().size() == 1
            && (json.getAsJsonObject().get(RELATED_TO).getAsJsonArray().size() > 0
            || json.getAsJsonObject().get(NODE_ACTIONS).getAsJsonArray().size() > 0)) {
            if (json.getAsJsonObject().get(PROPERTIES).getAsJsonArray()
                .get(0).getAsJsonObject().get("key").getAsString().equals(AAIProperties.END_TS)) {
                json.getAsJsonObject().add(PROPERTIES, new JsonArray());
            }
        }

        return Optional.of(json);
    }

    @Override
    protected Optional<JsonObject> getJsonFromVertex(Vertex input, Map<String, List<String>> properties) throws AAIFormatVertexException {
        return Optional.empty();
    }

    private JsonArray getNodeActions(Vertex v, JsonObject json) {
        JsonArray nodeActions = new JsonArray();
        JsonObject action;
        if (v.property(AAIProperties.END_TS).isPresent()) {
            long deletedTs = (Long) v.property(AAIProperties.END_TS).value();
            if (isTsInRange(deletedTs)) {
                action = new JsonObject();
                action.addProperty("action", "DELETED");
                action.addProperty(TIMESTAMP, deletedTs);
                if (v.property(AAIProperties.END_TS).property(AAIProperties.SOURCE_OF_TRUTH).isPresent()) {
                    action.addProperty(SOT, v.property(AAIProperties.END_TS).<String>value(AAIProperties.SOURCE_OF_TRUTH));
                }
                if (v.property(AAIProperties.END_TS).property(AAIProperties.END_TX_ID).isPresent()) {
                    action.addProperty(TX_ID, v.property(AAIProperties.END_TS).<String>value(AAIProperties.END_TX_ID));
                } else {
                    action.addProperty(TX_ID, "N/A");
                }
                nodeActions.add(action);
            }
        }
        long createdTs = json.get(TIMESTAMP).getAsLong();
        if (isTsInRange(createdTs)) {
            action = new JsonObject();
            action.addProperty("action", "CREATED");
            action.addProperty(TIMESTAMP, createdTs);
            action.addProperty(SOT, v.<String>value(AAIProperties.SOURCE_OF_TRUTH));
            if (v.property(AAIProperties.SOURCE_OF_TRUTH).property(AAIProperties.START_TX_ID).isPresent()) {
                action.addProperty(TX_ID, v.property(AAIProperties.SOURCE_OF_TRUTH).<String>value(AAIProperties.START_TX_ID));
            } else {
                action.addProperty(TX_ID, "N/A");
            }
            nodeActions.add(action);
        }
        return nodeActions;
    }

    public JsonArray process(List<Object> queryResults) {
        JsonArray body = new JsonArray();
        Stream<Object> stream;
        if (queryResults.size() >= this.parallelThreshold()) {
            stream = queryResults.parallelStream();
        } else {
            stream = queryResults.stream();
        }

        final boolean isParallel = stream.isParallel();

        stream.map(o -> {
            try {
                return this.formatObject(o);
            } catch (AAIFormatVertexException e) {
                LOGGER.warn("Failed to format vertex, returning a partial list " + LogFormatTools.getStackTop(e));
            } catch (AAIFormatQueryResultFormatNotSupported e) {
                LOGGER.warn("Failed to format result type of the query " + LogFormatTools.getStackTop(e));
            }

            return Optional.<JsonObject>empty();
        }).filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(json -> {
                if (isParallel) {
                    synchronized (body) {
                        body.add(json);
                    }
                } else {
                    body.add(json);
                }
            });
        JsonArray result = organizeBody(body);
        result.forEach(jsonElement -> jsonElement.getAsJsonObject().remove(TIMESTAMP));
        return result;
    }

    private JsonArray organizeBody(JsonArray body) {

        final MultiValueMap<String, Integer> toBeMerged = new LinkedMultiValueMap<>();
        for (int i = 0; i < body.size(); i++) {
            toBeMerged.add(body.get(i).getAsJsonObject().get("uri").getAsString(), i);
        }

        final List<List<Integer>> dupes = toBeMerged.values().stream().filter(l -> l.size() > 1).collect(Collectors.toList());
        if (dupes.isEmpty()) {
            return body;
        } else {
            Set<Integer> remove = new HashSet<>();
            for (List<Integer> dupe : dupes) {
                dupe.sort((a,b) -> Long.compare(body.get(b).getAsJsonObject().get(TIMESTAMP).getAsLong(), body.get(a).getAsJsonObject().get(TIMESTAMP).getAsLong()));
                int keep = dupe.remove(0);
                for (Integer idx : dupe) {
                    body.get(keep).getAsJsonObject().getAsJsonArray(NODE_ACTIONS)
                        .addAll(body.get(idx).getAsJsonObject().getAsJsonArray(NODE_ACTIONS));
                    body.get(keep).getAsJsonObject().getAsJsonArray(PROPERTIES)
                        .addAll(body.get(idx).getAsJsonObject().getAsJsonArray(PROPERTIES));
                    body.get(keep).getAsJsonObject().getAsJsonArray(RELATED_TO)
                        .addAll(body.get(idx).getAsJsonObject().getAsJsonArray(RELATED_TO));
                    remove.add(idx);
                }
            }
            final JsonArray newBody = new JsonArray();
            for (int i = 0; i < body.size(); i++) {
                if (!remove.contains(i)) {
                    newBody.add(body.get(i));
                }
            }
            return newBody;
        }
    }

}
