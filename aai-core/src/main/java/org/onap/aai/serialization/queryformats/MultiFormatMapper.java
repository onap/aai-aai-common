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
 * http://www.apache.org/licenses/LICENSE-2.0
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

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.Tree;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatQueryResultFormatNotSupported;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatVertexException;

public abstract class MultiFormatMapper implements FormatMapper {

    @Override
    public Optional<JsonObject> formatObject(Object input)
        throws AAIFormatVertexException, AAIFormatQueryResultFormatNotSupported {
        if (input instanceof Vertex) {
            return this.getJsonFromVertex((Vertex) input);
        } else if (input instanceof Tree) {
            return this.getJsonFomTree((Tree<?>) input);
        } else if (input instanceof Path) {
            return this.getJsonFromPath((Path) input);
        } else {
            throw new AAIFormatQueryResultFormatNotSupported();
        }
    }

    protected abstract Optional<JsonObject> getJsonFromVertex(Vertex input)
        throws AAIFormatVertexException;

    protected Optional<JsonObject> getJsonFromPath(Path input) throws AAIFormatVertexException {
        List<Object> path = input.objects();

        JsonObject jo = new JsonObject();
        JsonArray ja = new JsonArray();

        for (Object o : path) {
            if (o instanceof Vertex) {
                ja.add(this.getJsonFromVertex((Vertex) o).get());
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
        JsonArray ja = this.getNodesArray(tree);
        if (ja.size() > 0) {
            t.add("nodes", ja);
        }

        return Optional.of(t);
    }

    private JsonArray getNodesArray(Tree<?> tree) throws AAIFormatVertexException {

        JsonArray nodes = new JsonArray();
        Iterator<?> it = tree.keySet().iterator();

        while (it.hasNext()) {
            Object o = it.next();
            JsonObject me = new JsonObject();
            if (o instanceof Vertex) {
                me = this.getJsonFromVertex((Vertex) o).get();
            }
            JsonArray ja = this.getNodesArray((Tree<?>) tree.get(o));
            if (ja.size() > 0) {
                me.add("nodes", ja);
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
