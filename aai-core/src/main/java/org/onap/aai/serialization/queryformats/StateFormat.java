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
import com.google.gson.JsonObject;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatVertexException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class StateFormat extends HistoryFormat {

    private static final Logger LOGGER = LoggerFactory.getLogger(StateFormat.class);

    protected StateFormat(HistoryFormat.Builder builder) {
        super(builder);
    }

    protected JsonArray createPropertiesObject(Vertex v) {
        Iterator<VertexProperty<Object>> iter = v.properties();
        List<JsonObject> jsonList = new ArrayList<>();
        while (iter.hasNext()) {
            VertexProperty<Object> prop = iter.next();
            if (prop.key() != null && ignoredKeys.contains(prop.key())) {
                continue;
            }

            JsonObject metaProperties = createMetaPropertiesObject(prop);
            if (isTsInRange(metaProperties)) {
                JsonObject json = new JsonObject();
                json.addProperty(KEY, prop.key());
                json = mapPropertyValues(json, VALUE, prop.value());
                addMetaProperties(json, metaProperties);
                jsonList.add(json);
            }
        }

        JsonArray jsonArray = new JsonArray();
        jsonList.stream().sorted(Comparator.comparingLong(o -> o.get(TIMESTAMP).getAsLong())).forEach(jsonArray::add);
        return jsonArray;
    }

    private boolean isTsInRange(JsonObject metaProperties) {
        long sTs = metaProperties.get(AAIProperties.START_TS).getAsLong();
        long eTs = Long.MAX_VALUE;
        if (metaProperties.has(AAIProperties.END_TS)) {
            eTs = metaProperties.get(AAIProperties.END_TS).getAsLong();
        }

        return startTs >= sTs && eTs > startTs;
    }

    @Override
    protected boolean isValidEdge(Edge e) {
        if (e.property(AAIProperties.END_TS).isPresent()) {
            long edgeEndTs = e.value(AAIProperties.END_TS);
            if (startTs >= edgeEndTs) {
                return false;
            }
        }
        if (e.property(AAIProperties.START_TS).isPresent()) {
            long edgeStartTs = e.value(AAIProperties.START_TS);
            return startTs >= edgeStartTs;
        }
        return true;
    }

    @Override
    protected JsonObject getRelatedObject(Edge e, Vertex related) throws AAIFormatVertexException {

        JsonObject json = new JsonObject();
        json.addProperty("relationship-label", e.label());
        json.addProperty(NODE_TYPE, related.<String>value(AAIProperties.NODE_TYPE));
        json.addProperty("url", this.urlBuilder.pathed(related));
        if (related.property(AAIProperties.AAI_URI).isPresent()) {
            json.addProperty("uri", related.<String>value(AAIProperties.AAI_URI));
        } else {
            LOGGER.warn("Vertex {} is missing aai-uri", related.id());
            json.addProperty("uri", "NA");
        }
        json.addProperty(TIMESTAMP,  e.property(AAIProperties.START_TS).isPresent()? e.value(AAIProperties.START_TS) : 0);
        json.addProperty(SOT,   e.property(AAIProperties.SOURCE_OF_TRUTH).isPresent()? e.value(AAIProperties.SOURCE_OF_TRUTH) : "");
        json.addProperty(TX_ID,   e.property(AAIProperties.START_TX_ID).isPresent()? e.value(AAIProperties.START_TX_ID) : "N/A");

        return json;
    }


    protected void addMetaProperties(JsonObject json, JsonObject metaProperties) {
        json.addProperty(TIMESTAMP, metaProperties.get(AAIProperties.START_TS) != null ? metaProperties.get(AAIProperties.START_TS).getAsLong() : 0);
        json.addProperty(SOT, metaProperties.get(AAIProperties.SOURCE_OF_TRUTH) != null ? metaProperties.get(AAIProperties.SOURCE_OF_TRUTH).getAsString() : "");
        json.addProperty(TX_ID, metaProperties.get(AAIProperties.START_TX_ID) != null ? metaProperties.get(AAIProperties.START_TX_ID).getAsString() : "N/A");
    }

    @Override
    protected Optional<JsonObject> getJsonFromVertex(Vertex v) throws AAIFormatVertexException {

        JsonObject json = new JsonObject();
        json.addProperty(NODE_TYPE, v.<String>value(AAIProperties.NODE_TYPE));
        json.addProperty("url", this.urlBuilder.pathed(v));
        json.addProperty("uri", v.property(AAIProperties.AAI_URI).value().toString());
        JsonArray properties = this.createPropertiesObject(v);

        if (properties.size() > 0) {
            json.add(PROPERTIES, properties);
        } else {
            return Optional.empty();
        }
        if (!nodesOnly) {
            json.add(RELATED_TO, this.createRelationshipObject(v));
        }
        return Optional.of(json);
    }

    @Override
    protected Optional<JsonObject> getJsonFromVertex(Vertex input, Map<String, List<String>> properties) throws AAIFormatVertexException {
        return Optional.empty();
    }

}
