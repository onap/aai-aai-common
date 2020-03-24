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
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.introspection.Loader;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatVertexException;
import org.onap.aai.serialization.queryformats.params.AsTree;
import org.onap.aai.serialization.queryformats.params.Depth;
import org.onap.aai.serialization.queryformats.params.NodesOnly;
import org.onap.aai.serialization.queryformats.utils.UrlBuilder;
import org.onap.aai.util.AAIConfig;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ResourceWithSoT extends MultiFormatMapper {
    protected JsonParser parser = new JsonParser();
    protected final DBSerializer serializer;
    protected final Loader loader;
    protected final UrlBuilder urlBuilder;
    protected final int depth;
    protected final boolean nodesOnly;

    protected ResourceWithSoT(Builder builder) {
        this.urlBuilder = builder.getUrlBuilder();
        this.loader = builder.getLoader();
        this.serializer = builder.getSerializer();
        this.depth = builder.getDepth();
        this.nodesOnly = builder.isNodesOnly();
        this.isTree = builder.isTree();
    }

    @Override
    public int parallelThreshold() {
        return 100;
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

        public Builder includeUrl() {
            this.includeUrl = true;
            return this;
        }

        protected boolean isTree() { return this.tree; }

        public Builder isTree(Boolean tree) {
            this.tree = tree;
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

        public ResourceWithSoT build() {
            return new ResourceWithSoT(this);
        }
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
        // Null check
        if (v == null) {
            return null;
        }

        JsonObject json = new JsonObject();

        Object createdTimestampObj = v.property(AAIProperties.CREATED_TS).value();
        Object lastModifiedTimestampObj = v.property(AAIProperties.LAST_MOD_TS).value();
        Object sotObj = v.property(AAIProperties.SOURCE_OF_TRUTH).value();
        Object lastModSotObj = v.property(AAIProperties.LAST_MOD_SOURCE_OF_TRUTH).value();
        long createdTimestamp = Long.parseLong(createdTimestampObj.toString());
        long lastModifiedTimestamp = Long.parseLong(lastModifiedTimestampObj.toString());
        long threshold = Long.parseLong(AAIConfig.get("aai.resource.format.threshold", "10"));

        // Add to the property field of the JSON payload
        json.addProperty("aai-created-ts", createdTimestampObj.toString());
        json.addProperty("aai-last-mod-ts", lastModifiedTimestampObj.toString());
        json.addProperty("source-of-truth", sotObj.toString());
        json.addProperty("last-mod-source-of-truth", lastModSotObj.toString());

        // Check if the timestamp difference between creation and last modification are greater than a certain
        // threshold, and if the source of truth differs
        // If the timestamp difference is marginal and the SoT (creator/modifier) is the same, the last action performed
        // is likely to be a creation.
        long timestampDiff = lastModifiedTimestamp - createdTimestamp;
        boolean isSameSoT = sotObj.toString().equals(lastModSotObj.toString());

        if (timestampDiff <= threshold && isSameSoT)
            json.addProperty("last-action-performed", "Created");
        else
            json.addProperty("last-action-performed", "Modified");

        return Optional.of(json);
    }

    @Override
    protected Optional<JsonObject> getJsonFromVertex(Vertex input, Map<String, List<String>> properties) throws AAIFormatVertexException {
        return Optional.empty();
    }
}
