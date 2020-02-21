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
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.config.SpringContextAware;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatVertexException;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ChangesFormat extends MultiFormatMapper {

    private Long startTs = 0L;

    public void startTs(String startTime) {
        /*
         * StartTs = truncate time
         */
        if (startTime == null || startTime.isEmpty() || "now".equals(startTime) || "0".equals(startTime) || "-1".equals(startTime)){
           String historyTruncateDays = SpringContextAware.getApplicationContext().getEnvironment().getProperty("history.truncate.window.days", "365");
           this.startTs = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(Long.parseLong(historyTruncateDays));
        } else {
            this.startTs = Long.parseLong(startTime);
        }
    }

    @Override
    protected Optional<JsonObject> getJsonFromVertex(Vertex v) {
        JsonObject json = new JsonObject();
        if (!v.properties(AAIProperties.RESOURCE_VERSION).hasNext() ||
            !v.properties(AAIProperties.NODE_TYPE).hasNext() ||
            !v.properties(AAIProperties.AAI_URI).hasNext()) {
            return Optional.empty();
        }
        json.addProperty("node-type", v.<String>value(AAIProperties.NODE_TYPE));
        json.addProperty("uri", v.<String>value(AAIProperties.AAI_URI));

        final Set<Long> changes = new HashSet<>();
        v.properties(AAIProperties.RESOURCE_VERSION).forEachRemaining(o->
            o.properties(AAIProperties.START_TS, AAIProperties.END_TS)
            .forEachRemaining(p -> {
                Long val = (Long) p.value();
                if(val >= startTs) {
                    changes.add(val);
                }
            }
            ));
        v.edges(Direction.BOTH).forEachRemaining(e -> {
            if(e.property(AAIProperties.START_TS).isPresent() && (Long)e.property(AAIProperties.START_TS).value() >= startTs) {
                changes.add((Long) e.property(AAIProperties.START_TS).value());
            }
            if(e.property(AAIProperties.END_TS).isPresent() && (Long)e.property(AAIProperties.END_TS).value() >= startTs) {
                changes.add((Long) e.property(AAIProperties.END_TS).value());
            }
        });

        List<Long> sortedList = new ArrayList<>(changes);
        sortedList.sort(Comparator.naturalOrder());
        JsonArray jsonArray = new JsonArray();
        sortedList.forEach(jsonArray::add);

        json.add("changes", jsonArray);

        return Optional.of(json);
    }

    @Override
    protected Optional<JsonObject> getJsonFromVertex(Vertex input, Map<String, List<String>> properties) throws AAIFormatVertexException {
        return Optional.empty();
    }

}
