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
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.Tree;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatQueryResultFormatNotSupported;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatVertexException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class MultiFormatMapper implements FormatMapper {

    protected boolean isTree = false;

    @Override
    public Optional<JsonObject> formatObject(Object input)
            throws AAIFormatVertexException, AAIFormatQueryResultFormatNotSupported {
        if (input instanceof Vertex) {
            return this.getJsonFromVertex((Vertex) input);
        } else if (input instanceof Tree) {
            if (isTree) {
                return this.getRelatedNodesFromTree((Tree<?>) input);
            } else {
                return this.getJsonFomTree((Tree<?>) input);
            }
        } else if (input instanceof Path) {
            return this.getJsonFromPath((Path) input);
        } else {
            throw new AAIFormatQueryResultFormatNotSupported();
        }
    }

    @Override
    public Optional<JsonObject> formatObject(Object input, Map<String, List<String>> properties)
        throws AAIFormatVertexException, AAIFormatQueryResultFormatNotSupported {
        if (input instanceof Vertex) {
            return this.getJsonFromVertex((Vertex) input, properties);
        } else if (input instanceof Tree) {
            if (isTree) {
                return this.getRelatedNodesFromTree((Tree<?>) input);
            } else {
                return this.getJsonFomTree((Tree<?>) input);
            }
        } else if (input instanceof Path) {
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

    protected Optional<JsonObject> getJsonFomTree(Tree<?> tree) throws AAIFormatVertexException {

        if (tree.isEmpty()) {
            return Optional.of(new JsonObject());
        }

        JsonObject t = new JsonObject();
        JsonArray ja = this.getNodesArray(tree, "nodes");
        if (ja.size() > 0) {
            t.add("nodes", ja);
        }

        return Optional.of(t);
    }

    protected Optional<JsonObject> getRelatedNodesFromTree(Tree<?> tree) throws AAIFormatVertexException {
        if (tree.isEmpty()) {
            return Optional.of(new JsonObject());
        }

        JsonObject t = new JsonObject();
        JsonArray ja = this.getNodesArray(tree, "related-nodes");
        if (ja.size() > 0) {
            t.add("results", ja);
            return Optional.of(t);
        }

        return Optional.empty();
    }

    protected JsonArray getNodesArray(Tree<?> tree, String nodeIdentifier) throws AAIFormatVertexException {

        JsonArray nodes = new JsonArray();
        for (Map.Entry<?, ? extends Tree<?>> entry : tree.entrySet()) {
            JsonObject me = new JsonObject();
            if (entry.getKey() instanceof Vertex) {
                Optional<JsonObject> obj = this.getJsonFromVertex((Vertex) entry.getKey());
                if (obj.isPresent()) {
                    me = obj.get();
                } else {
                    continue;
                }
            }
            JsonArray ja = this.getNodesArray(entry.getValue(), nodeIdentifier);
            if (ja.size() > 0) {
                me.add(nodeIdentifier, ja);
            }
            nodes.add(me);
        }
        return nodes;
    }

    @Override
    public int parallelThreshold() {
        return 100;
    }

}
