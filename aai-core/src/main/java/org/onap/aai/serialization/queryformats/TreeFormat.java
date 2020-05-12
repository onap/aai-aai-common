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

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.BulkSet;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.Tree;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.logging.LogFormatTools;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatQueryResultFormatNotSupported;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatVertexException;
import org.onap.aai.serialization.queryformats.params.Depth;
import org.onap.aai.serialization.queryformats.params.NodesOnly;
import org.onap.aai.serialization.queryformats.utils.UrlBuilder;
import org.onap.aai.util.AAIConfig;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Stream;

public class TreeFormat extends MultiFormatMapper {
    private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(TreeFormat.class);
    protected JsonParser parser = new JsonParser();
    protected final DBSerializer serializer;
    protected final Loader loader;
    protected final UrlBuilder urlBuilder;
    protected final int depth;
    protected final boolean nodesOnly;

    protected TreeFormat(Builder builder) {
        this.urlBuilder = builder.getUrlBuilder();
        this.loader = builder.getLoader();
        this.serializer = builder.getSerializer();
        this.depth = builder.getDepth();
        this.nodesOnly = builder.isNodesOnly();
    }

    @Override
    public int parallelThreshold() {
        return 100;
    }

    public static class Builder implements NodesOnly<Builder>, Depth<Builder> {

        protected final Loader loader;
        protected final DBSerializer serializer;
        protected final UrlBuilder urlBuilder;
        protected boolean includeUrl = false;
        protected boolean nodesOnly = false;
        protected int depth = 1;
        protected boolean modelDriven = false;

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

        public TreeFormat build() {
            return new TreeFormat(this);
        }
    }
    
    public JsonArray process(List<Object> queryResults, Map<String, List<String>> properties) {
        JsonArray body = new JsonArray();
        for (Object o : queryResults) {
            try {
                return this.formatObjectToJsonArray(o, properties).get();
            } catch (AAIFormatVertexException e) {
                LOGGER.warn("Failed to format vertex, returning a partial list " + LogFormatTools.getStackTop(e));
            } catch (AAIFormatQueryResultFormatNotSupported e) {
                LOGGER.warn("Failed to format result type of the query " + LogFormatTools.getStackTop(e));
            }
        }
        return body;
    }

    public Optional<JsonArray> formatObjectToJsonArray(Object input, Map<String, List<String>> properties)
        throws AAIFormatVertexException, AAIFormatQueryResultFormatNotSupported {
        JsonArray json = new JsonArray();
        if (input == null)
            return Optional.of(json);
        if (input instanceof Tree) {
            return this.getJsonArrayFromTree((Tree<?>) input);
        } else {
            throw new AAIFormatQueryResultFormatNotSupported();
        }
    }

    protected Optional<JsonArray> getJsonArrayFromTree(Tree<?> tree) throws AAIFormatVertexException {
        if (tree.isEmpty()) {
            return Optional.of(new JsonArray());
        }

        // DSL Query
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        for (Object o : tree.keySet()) {
            // DSL Query
            if (o instanceof AbstractSet) {
                BulkSet bs = (BulkSet) o;
                for (Object o1 : bs) {
                    Optional<JsonObject> obj = this.getJsonFromVertex((Vertex) o1);
                    if (obj.isPresent()) {
                        jsonObject = obj.get();
                        for (Map.Entry<String, JsonElement> mapEntry : jsonObject.entrySet()) {
                            String s = mapEntry.getKey();
                            JsonElement jsonRootElementContents = jsonObject.get(s);        // getting everyObject inside
                            if (jsonRootElementContents != null && jsonRootElementContents.isJsonObject()) {
                                JsonObject relatedJsonNode = (JsonObject) jsonRootElementContents;
                                JsonArray relatedNodes = this.getRelatedNodes(relatedJsonNode).get();
                                if (relatedNodes != null && relatedNodes.size() > 0) {
                                    jsonRootElementContents.getAsJsonObject().add("related-nodes", relatedNodes);
                                }
                            }
                        }
                        jsonArray.add(jsonObject);
                    }
                }
            }
            // Gremlin Query
            else if (o instanceof Vertex) {
                Optional<JsonObject> obj = this.getJsonFromVertex((Vertex) o);
                if (obj.isPresent()) {
                    jsonObject = obj.get();
                    for (Map.Entry<String, JsonElement> mapEntry : jsonObject.entrySet()) {
                        String s = mapEntry.getKey();
                        JsonElement jsonRootElementContents = jsonObject.get(s);
                        if (jsonRootElementContents != null && jsonRootElementContents.isJsonObject()) {
                            JsonArray relatedNodes = this.getRelatedNodes(tree.get(o)).get();
                            if (relatedNodes != null && relatedNodes.size() > 0) {
                                jsonRootElementContents.getAsJsonObject().add("related-nodes", relatedNodes);
                            }
                        }
                    }
                    jsonArray.add(jsonObject);
                }
            }
        }
        return Optional.of(jsonArray);
    }

    protected Optional<JsonArray> getRelatedNodes(JsonObject jsonObj) throws AAIFormatVertexException {
        JsonArray relatedNodes = new JsonArray();
        for (Map.Entry<String, JsonElement> mapEntry : jsonObj.entrySet()) {
            String s = mapEntry.getKey();
            JsonElement jsonRootElementContents = jsonObj.get(s);
            if (jsonRootElementContents != null && jsonRootElementContents.isJsonObject()) {
                JsonObject relatedJsonNode = jsonRootElementContents.getAsJsonObject();
                JsonArray currRelatedNodes = this.getRelatedNodes(relatedJsonNode).get();
                if (currRelatedNodes != null && currRelatedNodes.size() > 0) {
                    relatedJsonNode.add("related-nodes", currRelatedNodes);
                }
                relatedNodes.add(relatedJsonNode);
            }
        }
        return Optional.of(relatedNodes);
    }

    protected Optional<JsonArray> getRelatedNodes(Tree<?> tree) throws AAIFormatVertexException {
        JsonArray relatedNodes = new JsonArray();
        for (Object o : tree.keySet()) {
            if (o instanceof Vertex) {
                Optional<JsonObject> obj = this.getJsonFromVertex((Vertex) o);
                if (obj.isPresent()) {
                    JsonObject jsonObj = obj.get();
                    for (Map.Entry<String, JsonElement> mapEntry : jsonObj.entrySet()) {
                        String s = mapEntry.getKey();
                        JsonElement jsonRootElementContents = jsonObj.get(s);
                        if (jsonRootElementContents != null && jsonRootElementContents.isJsonObject()) {
                            JsonArray currRelatedNodes = this.getRelatedNodes(tree.get(o)).get();
                            JsonObject jsonObject = jsonRootElementContents.getAsJsonObject();
                            if (currRelatedNodes != null && currRelatedNodes.size() > 0) {
                                jsonObject.add("related-nodes", currRelatedNodes);
                            }
                            relatedNodes.add(jsonObject);
                        }
                    }
                }
            }
        }
        return Optional.of(relatedNodes);
    }

    /**
     *
     * Returns an Optional<JsonObject> to convert the contents from the given Vertex object into a JsonObject.
     * The fields returned are to record the time stamp of the creation/modification of the object, the user responsible
     * for
     * the change, and the last http method performed on the object.
     *
     * @param v
     * @return
     * @throws AAIFormatVertexException
     */
    @Override
    protected Optional<JsonObject> getJsonFromVertex(Vertex v) throws AAIFormatVertexException {

        JsonObject json = new JsonObject();

        Optional<JsonObject> jsonObject = this.vertexToJsonObject(v);
        if (jsonObject.isPresent()) {
            json.add(v.<String>property(AAIProperties.NODE_TYPE).orElse(null), jsonObject.get());
        } else {
            return Optional.empty();
        }
        return Optional.of(json);
    }

    protected Optional<JsonObject> vertexToJsonObject(Vertex v) throws AAIFormatVertexException {
        try {
            final Introspector obj =
                getLoader().introspectorFromName(v.<String>property(AAIProperties.NODE_TYPE).orElse(null));

            final List<Vertex> wrapper = new ArrayList<>();

            wrapper.add(v);

            try {
                getSerializer().dbToObject(wrapper, obj, this.depth, this.nodesOnly, "false");
            } catch (AAIException | UnsupportedEncodingException e) {
                throw new AAIFormatVertexException(
                    "Failed to format vertex - error while serializing: " + e.getMessage(), e);
            }

            final String json = obj.marshal(false);

            return Optional.of(getParser().parse(json).getAsJsonObject());
        } catch (AAIUnknownObjectException e) {
            return Optional.empty();
        }
    }

    private Loader getLoader() {
        return loader;
    }

    private DBSerializer getSerializer() {
        return serializer;
    }

    private JsonParser getParser() {
        return parser;
    }


    @Override
    protected Optional<JsonObject> getJsonFromVertex(Vertex input, Map<String, List<String>> properties) throws AAIFormatVertexException {
        return Optional.empty();
    }
}
