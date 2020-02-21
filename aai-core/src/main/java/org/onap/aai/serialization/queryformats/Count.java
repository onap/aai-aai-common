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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.Tree;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.javatuples.Pair;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatQueryResultFormatNotSupported;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatVertexException;

public class Count implements FormatMapper {

    @Override
    public Optional<JsonObject> formatObject(Object o) {
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) o;

        final JsonObject countResult = new JsonObject();

        list.stream().map(this::getCount).filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toConcurrentMap(Pair::getValue0, Pair::getValue1, Long::sum))
                .forEach(countResult::addProperty);

        return Optional.of(countResult);
    }

    @Override
    public Optional<JsonObject> formatObject(Object o, Map<String, List<String>> properties) throws AAIFormatVertexException, AAIFormatQueryResultFormatNotSupported {
        return Optional.empty();
    }

    @Override
    public int parallelThreshold() {
        return 20;
    }

    private Optional<Pair<String, Long>> getCount(Object o) {

        Pair<String, Long> pair = null;

        if (o instanceof Vertex) {
            Vertex v = (Vertex) o;
            pair = Pair.with(v.property(AAIProperties.NODE_TYPE).value().toString(), 1L);
        } else if (o instanceof Tree) {
            pair = Pair.with("trees", 1L);
        } else if (o instanceof Path) {
            pair = Pair.with("paths", 1L);
        } else if (o instanceof Long) {
            pair = Pair.with("count", (Long) o);
        }

        if (pair == null) {
            return Optional.empty();
        }

        return Optional.of(pair);
    }

}
