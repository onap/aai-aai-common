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

package org.onap.aai.introspection.sideeffect;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.sideeffect.exceptions.AAIMissingRequiredPropertyException;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.restcore.util.URITools;
import org.onap.aai.schema.enums.PropertyMetadata;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;

import javax.ws.rs.core.MultivaluedMap;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Matcher;

public class DataLinkReader extends SideEffect {

    public DataLinkReader(Introspector obj, Vertex self, TransactionalGraphEngine dbEngine, DBSerializer serializer) {
        super(obj, self, dbEngine, serializer);
    }

    @Override
    protected boolean replaceWithWildcard() {
        return true;
    }

    @Override
    protected PropertyMetadata getPropertyMetadata() {
        return PropertyMetadata.DATA_LINK;
    }

    @Override
    protected void processURI(Optional<String> completeUri, Entry<String, String> entry)
            throws URISyntaxException, UnsupportedEncodingException, AAIException {

        if (completeUri.isPresent()) {
            URI uri = new URI(completeUri.get());
            MultivaluedMap<String, String> map = URITools.getQueryMap(uri);
            QueryParser uriQuery = dbEngine.getQueryBuilder(this.latestLoader).createQueryFromURI(uri, map);
            List<Vertex> results =
                    uriQuery.getQueryBuilder().getVerticesByProperty(AAIProperties.LINKED, true).toList();
            if (results.size() == 1) {
                if (results.get(0).<Boolean>property(AAIProperties.LINKED).orElse(false)
                        && obj.getValue(entry.getKey()) == null) {
                    obj.setValue(entry.getKey(), results.get(0).property(entry.getKey()).orElse(null));
                }
            } else {
                // log something about not being able to return any values because there was more than one
            }
        }
    }

    /**
     * always fuzzy search on reads
     */
    @Override
    protected Map<String, String> findProperties(Introspector obj, String uriString)
            throws AAIMissingRequiredPropertyException {

        final Map<String, String> result = new HashMap<>();
        Matcher m = template.matcher(uriString);
        while (m.find()) {
            String propName = m.group(1);
            if (replaceWithWildcard()) {
                result.put(propName, "*");
            }
        }
        return result;
    }

}
