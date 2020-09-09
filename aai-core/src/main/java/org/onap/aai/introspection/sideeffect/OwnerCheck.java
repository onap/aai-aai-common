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

import com.google.common.collect.Multimap;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.edges.EdgeRule;
import org.onap.aai.edges.EdgeRuleQuery;
import org.onap.aai.edges.enums.EdgeType;
import org.onap.aai.edges.exceptions.AmbiguousRuleChoiceException;
import org.onap.aai.edges.exceptions.EdgeRuleNotFoundException;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.sideeffect.exceptions.AAIMultiplePropertiesException;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.restcore.util.URITools;
import org.onap.aai.schema.enums.PropertyMetadata;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;

public class OwnerCheck extends SideEffect {

    public OwnerCheck(Introspector obj, Vertex self, TransactionalGraphEngine dbEngine, DBSerializer serializer) {
        super(obj, self, dbEngine, serializer);
    }

    @Override
    protected void processURI(Optional<String> completeUri, Entry<String, String> entry)
        throws URISyntaxException, UnsupportedEncodingException, AAIException, EdgeRuleNotFoundException, AmbiguousRuleChoiceException {
        if (completeUri.isPresent() && !serializer.getGroups().isEmpty()) {
            URI uri = new URI(completeUri.get());
            MultivaluedMap<String, String> map = URITools.getQueryMap(uri);
            QueryParser uriQuery = dbEngine.getQueryBuilder(this.latestLoader).createQueryFromURI(uri, map);
            List<Vertex> results = uriQuery.getQueryBuilder().toList();
            if (results.size() == 1) {
                Vertex otherVertex = results.get(0);

                List<Vertex> owningEntity = otherVertex.graph()
                    .traversal()
                    .V(otherVertex)
                    .inE("org.onap.relationships.inventory.BelongsTo")
                    .outV()
                    .has("aai-node-type", "owning-entity")
                    .toList();

                if(!owningEntity.isEmpty()) {
                    VertexProperty owningEntityName = owningEntity.get(0).property("owning-entity-name");

                    if(!serializer.getGroups().contains(owningEntityName.orElseGet(null))) {
                        throw new AAIException("AAI_3304",
                            "Group(s) :" + serializer.getGroups() + " not authorized to perform function");
                    }
                }

            } else {
                if (results.isEmpty()) {
                    throw new AAIException("AAI_6114", "object located at " + uri + " not found");
                } else {
                    throw new AAIMultiplePropertiesException(
                            "multiple values of " + entry.getKey() + " found when searching " + uri);
                }
            }
        } //else skip processing because no required properties were specified
    }

    @Override
    protected PropertyMetadata getPropertyMetadata() {
        return PropertyMetadata.OWNER_CHECK;
    }

    @Override
    protected boolean replaceWithWildcard() {
        return false;
    }

}
