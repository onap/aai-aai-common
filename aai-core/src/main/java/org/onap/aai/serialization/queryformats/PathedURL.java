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

import java.util.Optional;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatVertexException;
import org.onap.aai.serialization.queryformats.utils.UrlBuilder;

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

}
