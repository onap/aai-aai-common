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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.introspection.Loader;
import org.onap.aai.logging.LogFormatTools;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatQueryResultFormatNotSupported;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatVertexException;
import org.onap.aai.serialization.queryformats.params.AsTree;
import org.onap.aai.serialization.queryformats.params.Depth;
import org.onap.aai.serialization.queryformats.params.NodesOnly;
import org.onap.aai.serialization.queryformats.utils.UrlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Aggregate extends MultiFormatMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(LifecycleFormat.class);
    protected JsonParser parser = new JsonParser();
    protected final DBSerializer serializer;
    protected final Loader loader;
    protected final UrlBuilder urlBuilder;
    protected final int depth;
    protected final boolean nodesOnly;

    protected Aggregate(Builder builder) {
        this.urlBuilder = builder.getUrlBuilder();
        this.loader = builder.getLoader();
        this.serializer = builder.getSerializer();
        this.depth = builder.getDepth();
        this.nodesOnly = builder.isNodesOnly();
        this.isTree = builder.isTree();
    }

    @Override
    public Optional<JsonObject> getJsonFromVertex(Vertex v, Map<String, List<String>> selectedProps) throws AAIFormatVertexException{
        JsonObject json = new JsonObject();
        JsonObject outer = new JsonObject();
        Optional<JsonObject> properties = this.createSelectedPropertiesObject(v,selectedProps);
        if (properties.isPresent()) {
            json.add("properties", properties.get());
            outer.add(this.urlBuilder.pathed(v),json.getAsJsonObject());
        } else {
            return Optional.empty();
        }
        return Optional.of(outer);
    }

    @Override
    public int parallelThreshold() {
        return 100;
    }

    public Optional<JsonObject> createPropertiesObject(Vertex v) throws AAIFormatVertexException {
        JsonObject json = new JsonObject();
        Iterator<VertexProperty<Object>> iter = v.properties();

        while (iter.hasNext()) {
            VertexProperty<Object> prop = iter.next();
            if (prop.value() instanceof String) {
                json.addProperty(prop.key(), (String) prop.value());
            } else if (prop.value() instanceof Boolean) {
                json.addProperty(prop.key(), (Boolean) prop.value());
            } else if (prop.value() instanceof Number) {
                json.addProperty(prop.key(), (Number) prop.value());
            } else if (prop.value() instanceof List) {
                Gson gson = new Gson();
                String list = gson.toJson(prop.value());

                json.addProperty(prop.key(), list);
            } else {
                // throw exception?
                return null;
            }
        }

        return Optional.of(json);
    }

    public Optional<JsonObject> createSelectedPropertiesObject(Vertex v, Map<String, List<String>> selectedProps) throws AAIFormatVertexException {
        JsonObject json = new JsonObject();
        Set<String> propList = null;
        String nodeType = v.<String>value(AAIProperties.NODE_TYPE);
        if (selectedProps != null && !selectedProps.isEmpty() && selectedProps.containsKey(nodeType)) {
            propList = removeSingleQuotesForProperties(selectedProps.get(nodeType));
        }
        Iterator<VertexProperty<Object>> iter = v.properties();

        Gson gson = new Gson();
        while (iter.hasNext()) {
            VertexProperty<Object> prop = iter.next();
            if (propList != null && !propList.isEmpty()) {
                if (propList.contains(prop.label())) {
                    if (prop.value() instanceof String) {
                        json.addProperty(prop.key(), (String) prop.value());
                    } else if (prop.value() instanceof Boolean) {
                        json.addProperty(prop.key(), (Boolean) prop.value());
                    } else if (prop.value() instanceof Number) {
                        json.addProperty(prop.key(), (Number) prop.value());
                    } else if (prop.value() instanceof List) {
                        json.addProperty(prop.key(), gson.toJson(prop.value()));
                    } else {
                        // throw exception?
                        return null;
                    }
                }
            } else {
                return this.createPropertiesObject(v);
            }
        }

        return Optional.of(json);
    }

    private Set<String> removeSingleQuotesForProperties(List<String> props){
        if (props != null && !props.isEmpty()) {
            return props.stream().map(
                e -> e.substring(1, e.length()-1)).collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }

    public JsonArray process(List<Object> queryResults, Map<String, List<String>> properties) {
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
                return this.formatObject(o, properties);
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
        return body;
    }

    @Override
    public Optional<JsonObject> formatObject(Object input, Map<String, List<String>> properties)
        throws AAIFormatVertexException, AAIFormatQueryResultFormatNotSupported {
        JsonObject json = new JsonObject();
        if (input instanceof ArrayList) {
            Optional<JsonArray> ja = processInput(input, properties);
            json.add("results", ja.get());
        } else {
            throw new AAIFormatQueryResultFormatNotSupported();
        }
        return Optional.of(json);
    }

    private Optional<JsonArray> processInput(Object input, Map properties) throws AAIFormatVertexException {
        JsonArray json = new JsonArray();
        for (Object l  : (ArrayList) input) {
            if (l instanceof ArrayList) {
                JsonArray inner = new JsonArray();
                for (Vertex o : (ArrayList<Vertex>) l) {
                    if (o instanceof Vertex) {
                        Optional<JsonObject> obj = this.getJsonFromVertex((Vertex) o, properties);
                        if (obj.isPresent()) {
                            inner.add(obj.get());
                        } else {
                            continue;
                        }
                    }
                }
                json.add(inner);
            } else {
                Optional<JsonObject> obj = this.getJsonFromVertex((Vertex)l, properties);
                json.add(obj.get());
            }
        }
        return Optional.of(json);
    }

    public static class Builder implements NodesOnly<Builder>, Depth<Builder>, AsTree<Builder> {

        protected final Loader loader;
        protected final DBSerializer serializer;
        protected final UrlBuilder urlBuilder;
        protected boolean includeUrl = false;
        protected boolean nodesOnly = false;
        protected int depth = 1;
        protected boolean modelDriven = false;
        private boolean tree = false;

        public Builder(Loader loader, DBSerializer serializer, UrlBuilder urlBuilder) {
            this.loader = loader;
            this.serializer = serializer;
            this.urlBuilder = urlBuilder;
        }

        protected boolean isTree() { return this.tree; }

        public Builder isTree(Boolean tree) {
            this.tree = tree;
            return this;
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

        public Aggregate build() {
                return new Aggregate(this);
        }
    }

    @Override
    protected Optional<JsonObject> getJsonFromVertex(Vertex v) throws AAIFormatVertexException {

        JsonObject json = new JsonObject();
        json.addProperty("url", this.urlBuilder.pathed(v));
        Optional<JsonObject> properties = this.createPropertiesObject(v);
        if (properties.isPresent()) {
            json.add("properties", properties.get());
        } else {
            return Optional.empty();
        }
        return Optional.of(json);
    }
}
