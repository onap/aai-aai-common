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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatVertexException;
import org.onap.aai.serialization.queryformats.params.AsTree;
import org.onap.aai.serialization.queryformats.params.Depth;
import org.onap.aai.serialization.queryformats.params.NodesOnly;
import org.onap.aai.serialization.queryformats.utils.UrlBuilder;

import javax.ws.rs.core.MultivaluedMap;

public final class PathedURL extends MultiFormatMapper {

    private final UrlBuilder urlBuilder;
    private final JsonParser parser;
    private final Loader loader;
    private boolean includeUrl = false;

    public PathedURL(Loader loader, UrlBuilder urlBuilder) throws AAIException {
        this.urlBuilder = urlBuilder;
        this.parser = new JsonParser();
        this.loader = loader;
    }

    public PathedURL(Builder builder) {
        this.urlBuilder = builder.getUrlBuilder();
        this.parser = new JsonParser();
        this.loader = builder.getLoader();
        this.isTree = builder.isTree();
    }

    @Override
    public int parallelThreshold() {
        return 20;
    }

    public PathedURL includeUrl() {
        this.includeUrl = true;
        return this;
    }

    @Override
    protected Optional<JsonObject> getJsonFromVertex(Vertex v) throws AAIFormatVertexException {

        try {
            final Introspector searchResult = this.loader.introspectorFromName("result-data");

            searchResult.setValue("resource-type", v.value(AAIProperties.NODE_TYPE));

            searchResult.setValue("resource-link", this.urlBuilder.pathed(v));

            if (includeUrl)
                searchResult.setValue("resource-version", v.value(AAIProperties.RESOURCE_VERSION));

            final String json = searchResult.marshal(false);
            return Optional.of(this.parser.parse(json).getAsJsonObject());

        } catch (AAIUnknownObjectException e) {
            throw new RuntimeException("Fatal error - result-data does not exist!", e);
        }

    }

    @Override
    protected Optional<JsonObject> getJsonFromVertex(Vertex input, Map<String, List<String>> properties) throws AAIFormatVertexException {
        return Optional.empty();
    }

    public static class Builder implements NodesOnly<Builder>, Depth<Builder>, AsTree<Builder> {

        private final Loader loader;
        private final DBSerializer serializer;
        private final UrlBuilder urlBuilder;
        private boolean includeUrl = false;
        private boolean nodesOnly = false;
        private int depth = 1;
        private MultivaluedMap<String, String> params;
        private boolean tree = false;

        public Builder(Loader loader, DBSerializer serializer, UrlBuilder urlBuilder) {
            this.loader = loader;
            this.serializer = serializer;
            this.urlBuilder = urlBuilder;
        }

        public Builder(Loader loader, DBSerializer serializer, UrlBuilder urlBuilder, MultivaluedMap<String, String> params) {
            this.loader = loader;
            this.serializer = serializer;
            this.urlBuilder = urlBuilder;
            this.params = params;
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

        protected MultivaluedMap<String, String> getParams() { return this.params; }

        public boolean isSkipRelatedTo() {
            if (params != null) {
                boolean isSkipRelatedTo = true;
                if (params.containsKey("skip-related-to")) {
                    String skipRelatedTo = params.getFirst("skip-related-to");
                    isSkipRelatedTo = !(skipRelatedTo != null && skipRelatedTo.equals("false"));
                } else {
                    // if skip-related-to param is missing, then default it to false;
                    isSkipRelatedTo = false;
                }
                return isSkipRelatedTo;
            }
            return true;
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

        public PathedURL build() throws AAIException {
            return new PathedURL(this);
        }
    }

}
