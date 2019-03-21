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

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.onap.aai.logging.LogFormatTools;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatQueryResultFormatNotSupported;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatVertexException;

public class Formatter {

    private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(Formatter.class);

    protected JsonParser parser = new JsonParser();
    protected final FormatMapper format;

    public Formatter(FormatMapper format) {
        this.format = format;
    }

    public JsonObject output(List<Object> queryResults) {

        Stream<Object> stream;
        JsonObject result = new JsonObject();
        JsonArray body = new JsonArray();

        if (this.format instanceof Count) {
            JsonObject countResult;
            try {
                countResult = format.formatObject(queryResults)
                    .orElseThrow(() -> new AAIFormatVertexException(""));
                body.add(countResult);
            } catch (Exception e) {
                LOGGER.warn(
                    "Failed to format result type of the query " + LogFormatTools.getStackTop(e));
            }
        } else {
            if (queryResults.size() >= format.parallelThreshold()) {
                stream = queryResults.parallelStream();
            } else {
                stream = queryResults.stream();
            }

            final boolean isParallel = stream.isParallel();

            stream.map(o -> {
                try {
                    return format.formatObject(o);
                } catch (AAIFormatVertexException e) {
                    LOGGER.warn("Failed to format vertex, returning a partial list "
                        + LogFormatTools.getStackTop(e));
                } catch (AAIFormatQueryResultFormatNotSupported e) {
                    LOGGER.warn("Failed to format result type of the query "
                        + LogFormatTools.getStackTop(e));
                }

                return Optional.<JsonObject>empty();
            }).filter(Optional::isPresent).map(Optional::get).forEach(json -> {
                if (isParallel) {
                    synchronized (body) {
                        body.add(json);
                    }
                } else {
                    body.add(json);
                }
            });

        }
        result.add("results", body);
        return result.getAsJsonObject();
    }

}
