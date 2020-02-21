/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.tinkerpop.gremlin.structure.*;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.introspection.Loader;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatVertexException;
import org.onap.aai.serialization.queryformats.params.Depth;
import org.onap.aai.serialization.queryformats.params.EndTs;
import org.onap.aai.serialization.queryformats.params.NodesOnly;
import org.onap.aai.serialization.queryformats.params.StartTs;
import org.onap.aai.serialization.queryformats.utils.UrlBuilder;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class HistoryFormat extends MultiFormatMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryFormat.class);

    protected static final String KEY = "key";
    protected static final String VALUE = "value";
    protected static final String TIMESTAMP = "timestamp";
    protected static final String NODE_TYPE = "node-type";
    protected static final String END_TIMESTAMP = "end-timestamp";
    protected static final String SOT = "sot";
    protected static final String END_SOT = "end-sot";
    protected static final String TX_ID = "tx-id";
    protected static final String END_TX_ID = "end-tx-id";
    protected static final String PROPERTIES = "properties";
    protected static final String RELATED_TO = "related-to";
    protected static final String NODE_ACTIONS = "node-actions";

    protected JsonParser parser = new JsonParser();
    protected final DBSerializer serializer;
    protected final Loader loader;
    protected final UrlBuilder urlBuilder;
    protected final int depth;
    protected final boolean nodesOnly;
    protected long startTs;
    protected long endTs;
    protected static final Set<String> ignoredKeys =
        Stream.of(AAIProperties.LAST_MOD_TS, AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, AAIProperties.CREATED_TS)
        .collect(Collectors.toSet());

    protected HistoryFormat(Builder builder) {
        this.urlBuilder = builder.getUrlBuilder();
        this.loader = builder.getLoader();
        this.serializer = builder.getSerializer();
        this.depth = builder.getDepth();
        this.nodesOnly = builder.isNodesOnly();
        this.startTs = builder.getStartTs();
        this.endTs = builder.getEndTs();
    }

    @Override
    public int parallelThreshold() {
        return 100;
    }

    protected JsonObject createMetaPropertiesObject(VertexProperty<Object> prop) {
        JsonObject json = new JsonObject();
        Iterator iter = prop.properties();

        while (iter.hasNext()) {
            Property<Object> metaProp = (Property) iter.next();
            mapPropertyValues(json, metaProp.key(), metaProp.value());
        }

        return json;
    }

    protected JsonObject mapPropertyValues(JsonObject json, String propertyKey, Object propertyValue) {
        if (propertyValue instanceof String) {
            json.addProperty(propertyKey, (String) propertyValue);
        } else if (propertyValue instanceof Boolean) {
            json.addProperty(propertyKey, (Boolean) propertyValue);
        } else if (propertyValue instanceof Number) {
            json.addProperty(propertyKey, (Number) propertyValue);
        } else {
            if (!(propertyValue instanceof List)) {
                return json;
            }

            Gson gson = new Gson();
            String list = gson.toJson(propertyValue);
            json.addProperty(propertyKey, list);
        }
        return json;
    }

    protected JsonArray createRelationshipObject(Vertex v) throws AAIFormatVertexException {
        JsonArray relatedToList = new JsonArray();
        Iterator<Edge> inIter = v.edges(Direction.IN);
        Iterator<Edge> outIter = v.edges(Direction.OUT);

        while (inIter.hasNext()) {
            Edge e = inIter.next();
            if (isValidEdge(e)) {
                relatedToList.add(getRelatedObject(e, e.outVertex()));
            }
        }

        while (outIter.hasNext()) {
            Edge e = outIter.next();
            if (isValidEdge(e)) {
                relatedToList.add(getRelatedObject(e, e.inVertex()));
            }
        }

        return relatedToList;

    }

    protected abstract boolean isValidEdge(Edge e);

    protected abstract JsonObject getRelatedObject(Edge e, Vertex related) throws AAIFormatVertexException;



    public static class Builder implements NodesOnly<Builder>, Depth<Builder>, StartTs<Builder>, EndTs<Builder> {

        protected final Loader loader;
        protected final DBSerializer serializer;
        protected final UrlBuilder urlBuilder;
        protected boolean includeUrl = false;
        protected boolean nodesOnly = false;
        protected int depth = 1;
        protected boolean modelDriven = false;
        protected long startTs = -1;
        protected long endTs = -1;

        public Builder(Loader loader, DBSerializer serializer, UrlBuilder urlBuilder) {
            this.loader = loader;
            this.serializer = serializer;
            this.urlBuilder = urlBuilder;
        }

        protected Loader getLoader() {
            return this.loader;
        }

        protected DBSerializer getSerializer() {
            return this.serializer;
        }

        protected UrlBuilder getUrlBuilder() {
            return this.urlBuilder;
        }

        public Builder includeUrl() {
            this.includeUrl = true;
            return this;
        }

        public Builder nodesOnly(Boolean nodesOnly) {
            this.nodesOnly = nodesOnly;
            return this;
        }

        public Builder startTs(String startTs) {
            this.startTs = Long.parseLong(startTs);
            return this;
        }

        public Builder endTs(String endTs) {
            this.endTs = Long.parseLong(endTs);
            return this;
        }


        public boolean isNodesOnly() {
            return this.nodesOnly;
        }

        public Builder depth(Integer depth) {
            this.depth = depth;
            return this;
        }

        public int getDepth() {
            return this.depth;
        }

        public boolean isIncludeUrl() {
            return this.includeUrl;
        }

        public Builder modelDriven() {
            this.modelDriven = true;
            return this;
        }

        public boolean getModelDriven() {
            return this.modelDriven;
        }

        public long getStartTs() {
            return this.startTs;
        }

        public long getEndTs() {
            return this.endTs;
        }

        public HistoryFormat build(Format format) {

            if(Format.state.equals(format)) {
                return new StateFormat(this);
            } else {
                return new LifecycleFormat(this);
            }

        }
    }

}
