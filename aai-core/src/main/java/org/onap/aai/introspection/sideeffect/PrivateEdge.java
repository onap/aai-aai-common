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
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.onap.aai.config.SpringContextAware;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.edges.EdgeRule;
import org.onap.aai.edges.EdgeRuleQuery;
import org.onap.aai.edges.enums.EdgeType;
import org.onap.aai.edges.exceptions.AmbiguousRuleChoiceException;
import org.onap.aai.edges.exceptions.EdgeRuleNotFoundException;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.introspection.sideeffect.exceptions.AAIMultiplePropertiesException;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.restcore.util.URITools;
import org.onap.aai.schema.enums.PropertyMetadata;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.db.EdgeSerializer;
import org.onap.aai.serialization.db.exceptions.EdgeMultiplicityException;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;

import javax.ws.rs.core.MultivaluedMap;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Map.Entry;

public class PrivateEdge extends SideEffect {

    public PrivateEdge(Introspector obj, Vertex self, TransactionalGraphEngine dbEngine, DBSerializer serializer) {
        super(obj, self, dbEngine, serializer);
    }

    @Override
    protected void processURI(Optional<String> completeUri, Entry<String, String> entry) throws URISyntaxException,
            UnsupportedEncodingException, AAIException, EdgeRuleNotFoundException, AmbiguousRuleChoiceException {
        if (completeUri.isPresent()) {
            process(completeUri, entry);
        } else {
            // Check if the vertex self has the template keys or the db aliased keys
            // If it does check if the self vertex has a edge to that model
            // If it does, then remove the edge since the update happened and doesn't have required props anymore
            // "service-design-and-creation/models/model/{model-invariant-id}/model-vers/model-ver/{model-version-id}"
            // If the vertex does have
            Loader loader = SpringContextAware.getBean(LoaderFactory.class).createLoaderForVersion(ModelType.MOXY,
                    obj.getVersion());
            Introspector introspector = loader.introspectorFromName(obj.getDbName());
            List<Vertex> vertices = new ArrayList<>();
            vertices.add(self);
            Introspector curObj = serializer.dbToObject(vertices, introspector, 0, true, "false");
            Optional<String> populatedUri = this.replaceTemplates(curObj, entry.getValue());
            process(populatedUri, entry);
        }
    }

    private void process(Optional<String> completeUri, Entry<String, String> entry) throws URISyntaxException,
            UnsupportedEncodingException, AAIException, EdgeRuleNotFoundException, AmbiguousRuleChoiceException {
        if (completeUri.isPresent()) {
            URI uri = new URI(completeUri.get());
            MultivaluedMap<String, String> map = URITools.getQueryMap(uri);
            QueryParser uriQuery = dbEngine.getQueryBuilder(this.latestLoader).createQueryFromURI(uri, map);
            List<Vertex> results = uriQuery.getQueryBuilder().toList();
            if (results.size() == 1) {
                Vertex otherVertex = results.get(0);
                VertexProperty otherVProperty = otherVertex.property("aai-node-type");

                if (otherVProperty.isPresent()) {

                    EdgeRuleQuery edgeQuery =
                            new EdgeRuleQuery.Builder(obj.getName(), otherVProperty.value().toString())
                                    .edgeType(EdgeType.COUSIN).setPrivate(true).build();
                    EdgeIngestor edgeIngestor = serializer.getEdgeIngestor();
                    EdgeSerializer edgeSerializer = serializer.getEdgeSeriailizer();

                    Multimap<String, EdgeRule> edgeRulesMap = edgeIngestor.getRules(edgeQuery);

                    if (edgeRulesMap.isEmpty()) {
                        String message = String.format("Unable to find edge between %s and %s", obj.getName(),
                                otherVProperty.value().toString());
                        throw new AAIException("AAI_6127", message);
                    } else if (edgeRulesMap.size() > 1) {
                        String message = String.format("Found multiple edges between %s and %s", obj.getName(),
                                otherVProperty.value().toString());
                        throw new EdgeMultiplicityException(message);
                    }

                    EdgeRule edgeRule = edgeIngestor.getRule(edgeQuery);
                    Iterator<Edge> edges = self.edges(edgeRule.getDirection(), edgeRule.getLabel());
                    if (edges.hasNext()) {
                        Edge edge = edges.next();
                        EdgeStatus status = checkStatus(obj, self);
                        switch (status) {
                            case CREATED:
                                edgeSerializer.addPrivateEdge(this.dbEngine.asAdmin().getTraversalSource(), self,
                                        otherVertex, edgeRule.getLabel());
                                break;
                            case MODIFIED:
                                edge.remove();
                                edgeSerializer.addPrivateEdge(this.dbEngine.asAdmin().getTraversalSource(), self,
                                        otherVertex, edgeRule.getLabel());
                                break;
                            case REMOVED:
                                edge.remove();
                                break;
                            case UNCHANGED:
                                break;
                        }
                    } else {
                        edgeSerializer.addPrivateEdge(this.dbEngine.asAdmin().getTraversalSource(), self,
                                otherVertex, edgeRule.getLabel());
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
        }
    }

    public enum EdgeStatus {
        CREATED, REMOVED, MODIFIED, UNCHANGED
    }

    private EdgeStatus checkStatus(Introspector obj, Vertex self) {

        for (String key : templateKeys) {
            String currentObjValue = obj.getValue(key);
            Map<PropertyMetadata, String> map = obj.getPropertyMetadata(key);
            String oldVertexValue;

            if (map.containsKey(PropertyMetadata.DB_ALIAS)) {
                oldVertexValue = self.<String>property(key + AAIProperties.DB_ALIAS_SUFFIX).orElse(null);
            } else {
                oldVertexValue = self.<String>property(key).orElse(null);
            }

            if (currentObjValue == null && oldVertexValue == null) {
                continue;
            }

            if (currentObjValue == null) {
                if (oldVertexValue != null) {
                    return EdgeStatus.REMOVED;
                }
            }

            if (oldVertexValue == null) {
                if (currentObjValue != null) {
                    return EdgeStatus.CREATED;
                }
            }

            if (!oldVertexValue.equals(currentObjValue)) {
                return EdgeStatus.MODIFIED;
            }
        }

        return EdgeStatus.UNCHANGED;
    }

    @Override
    protected PropertyMetadata getPropertyMetadata() {
        return PropertyMetadata.PRIVATE_EDGE;
    }

    @Override
    protected boolean replaceWithWildcard() {
        return false;
    }

}
