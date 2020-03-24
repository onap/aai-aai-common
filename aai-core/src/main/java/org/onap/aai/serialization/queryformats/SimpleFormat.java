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
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatVertexException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SimpleFormat extends RawFormat {

    protected SimpleFormat(Builder builder) {
        super(builder);

    }

    @Override
    public int parallelThreshold() {
        return 20;
    }

    @Override
    public Optional<JsonObject> createPropertiesObject(Vertex v) throws AAIFormatVertexException {
        try {
            final Introspector obj =
                    loader.introspectorFromName(v.<String>property(AAIProperties.NODE_TYPE).orElse(null));

            final List<Vertex> wrapper = new ArrayList<>();

            wrapper.add(v);

            try {
                serializer.dbToObject(wrapper, obj, this.depth, true, "false");
            } catch (AAIException | UnsupportedEncodingException e) {
                throw new AAIFormatVertexException(
                        "Failed to format vertex - error while serializing: " + e.getMessage(), e);
            }

            final String json = obj.marshal(false);
            return Optional.of(parser.parse(json).getAsJsonObject());
        } catch (AAIUnknownObjectException e) {
            return Optional.empty();
        }

    }

    @Override
    protected void addEdge(Edge e, Vertex v, JsonArray array) throws AAIFormatVertexException {

        Property property = e.property("private");

        if (property.isPresent()) {
            if ("true".equals(e.property("private").value().toString())) {
                return;
            }
        }

        super.addEdge(e, v, array);
    }
}
