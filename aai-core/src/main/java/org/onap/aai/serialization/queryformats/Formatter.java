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
import org.onap.aai.logging.LogFormatTools;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatQueryResultFormatNotSupported;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatVertexException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class Formatter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Formatter.class);

    protected final FormatMapper format;
    protected MultivaluedMap<String, String> params;

    public Formatter(FormatMapper format) {
        this.format = format;
    }

    public Formatter(FormatMapper format, MultivaluedMap<String, String> params) {
        this.format = format;
        this.params = params;
    }

    public JsonObject output(List<Object> queryResults, Map<String, List<String>> properties) {

        final JsonArray body;

        if (this.format instanceof Count) {
            JsonObject countResult;
            body = new JsonArray();
            try {
                countResult = format.formatObject(queryResults).orElseThrow(() -> new AAIFormatVertexException(""));
                body.add(countResult);
            } catch (Exception e) {
                LOGGER.warn("Failed to format result type of the query " + LogFormatTools.getStackTop(e));
            }
        } else if (this.format instanceof LifecycleFormat) {
            LifecycleFormat lifecycleFormat = (LifecycleFormat) format;
            body = lifecycleFormat.process(queryResults);
        } else if (this.format instanceof Aggregate) {
            Aggregate aggregateFormat = (Aggregate) format;
            body = aggregateFormat.process(queryResults, properties);
            JsonObject result = new JsonObject();
            if (body != null && body.size() > 0) {
                result.add("results", (body.get(0)).getAsJsonObject().get("results"));
            }
            return result;
        } else {

            body = new JsonArray();
            Stream<Object> stream;
            if (queryResults.size() >= format.parallelThreshold()) {
                stream = queryResults.parallelStream();
            } else {
                stream = queryResults.stream();
            }

            final boolean isParallel = stream.isParallel();

            stream.map(o -> {
                try {
                    if (properties!= null && !properties.isEmpty()){
                        return format.formatObject(o, properties);
                    } else {
                        return format.formatObject(o);
                    }
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

        }

        if (params !=null && params.containsKey("as-tree")) {
            String isAsTree = params.get("as-tree").get(0);
            if (isAsTree != null && isAsTree.equalsIgnoreCase("true")
                && body != null && body.size() != 0) {
                JsonObject jsonObjectBody = body.get(0).getAsJsonObject();
                if (jsonObjectBody != null && jsonObjectBody.size() > 0) {
                    return body.get(0).getAsJsonObject();
                }
            }
        }
        JsonObject result = new JsonObject();
        result.add("results", body);
        return result.getAsJsonObject();

    }

    public JsonObject output(List<Object> queryResults) {
        return output(queryResults, null);
    }

}
