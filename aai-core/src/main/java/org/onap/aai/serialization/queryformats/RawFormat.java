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

import java.util.*;
import java.util.stream.Collectors;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.introspection.Loader;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatVertexException;
import org.onap.aai.serialization.queryformats.params.Depth;
import org.onap.aai.serialization.queryformats.params.AsTree;
import org.onap.aai.serialization.queryformats.params.NodesOnly;
import org.onap.aai.serialization.queryformats.utils.UrlBuilder;

public class RawFormat extends MultiFormatMapper {
    protected JsonParser parser = new JsonParser();
    protected final DBSerializer serializer;
    protected final Loader loader;
    protected final UrlBuilder urlBuilder;
    protected final int depth;
    protected final boolean nodesOnly;

    protected RawFormat(Builder builder) {
        this.urlBuilder = builder.getUrlBuilder();
        this.loader = builder.getLoader();
        this.serializer = builder.getSerializer();
        this.depth = builder.getDepth();
        this.nodesOnly = builder.isNodesOnly();
        this.isTree = builder.isTree();
    }

    @Override
    public Optional<JsonObject> getJsonFromVertex(Vertex v, Map<String, List<String>> selectedProps) throws AAIFormatVertexException {
        JsonObject json = new JsonObject();
        json.addProperty("id", v.id().toString());
        json.addProperty("node-type", v.<String>value(AAIProperties.NODE_TYPE));
        json.addProperty("url", this.urlBuilder.pathed(v));
        Optional<JsonObject> properties = this.createSelectedPropertiesObject(v, selectedProps);
        if (properties.isPresent()) {
            json.add("properties", properties.get());
        } else {
            return Optional.empty();
        }
        if (!nodesOnly) {
            json.add("related-to", this.createRelationshipObject(v));
        }
        return Optional.of(json);
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
        String nodeType = v.<String>value(AAIProperties.NODE_TYPE);
        Set<String> propList = removeSingleQuotesForProperties(selectedProps.get(nodeType));
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

    protected JsonArray createRelationshipObject(Vertex v) throws AAIFormatVertexException {
        JsonArray jarray = new JsonArray();
        Iterator<Edge> inIter = v.edges(Direction.IN);
        Iterator<Edge> outIter = v.edges(Direction.OUT);

        while (inIter.hasNext()) {
            Edge e = inIter.next();
            Vertex outVertex = e.outVertex();
            this.addEdge(e, outVertex, jarray);
        }

        while (outIter.hasNext()) {
            Edge e = outIter.next();
            Vertex inVertex = e.inVertex();
            this.addEdge(e, inVertex, jarray);
        }

        return jarray;
    }

    protected void addEdge(Edge e, Vertex vertex, JsonArray array) throws AAIFormatVertexException {
        array.add(this.getRelatedObject(e.label(), vertex));
    }

    protected JsonObject getRelatedObject(String label, Vertex related) throws AAIFormatVertexException {
        JsonObject json = new JsonObject();
        json.addProperty("id", related.id().toString());
        json.addProperty("relationship-label", label);
        json.addProperty("node-type", related.<String>value(AAIProperties.NODE_TYPE));
        json.addProperty("url", this.urlBuilder.pathed(related));

        return json;
    }

    public static class Builder implements NodesOnly<Builder>, Depth<Builder>, AsTree<Builder> {

        protected final Loader loader;
        protected final DBSerializer serializer;
        protected final UrlBuilder urlBuilder;
        protected boolean includeUrl = false;
        protected boolean nodesOnly = false;
        protected int depth = 1;
        protected boolean modelDriven = false;
        protected boolean tree = false;

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

        protected boolean isTree() { return this.tree; }

        public Builder isTree(Boolean tree) {
            this.tree = tree;
            return this;
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

        public RawFormat build() {
            if (modelDriven) {
                return new SimpleFormat(this);
            } else {
                return new RawFormat(this);
            }
        }
    }

    @Override
    protected Optional<JsonObject> getJsonFromVertex(Vertex v) throws AAIFormatVertexException {

        JsonObject json = new JsonObject();
        json.addProperty("id", v.id().toString());
        json.addProperty("node-type", v.<String>value(AAIProperties.NODE_TYPE));
        json.addProperty("url", this.urlBuilder.pathed(v));
        Optional<JsonObject> properties = this.createPropertiesObject(v);
        if (properties.isPresent()) {
            json.add("properties", properties.get());
        } else {
            return Optional.empty();
        }
        if (!nodesOnly) {
            json.add("related-to", this.createRelationshipObject(v));
        }
        return Optional.of(json);
    }
}
