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

package org.onap.aai.parsers.relationship;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.UriBuilder;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.onap.aai.config.SpringContextAware;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.edges.EdgeRule;
import org.onap.aai.edges.EdgeRuleQuery;
import org.onap.aai.edges.enums.AAIDirection;
import org.onap.aai.edges.enums.EdgeType;
import org.onap.aai.edges.exceptions.AmbiguousRuleChoiceException;
import org.onap.aai.edges.exceptions.EdgeRuleNotFoundException;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.*;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.parsers.exceptions.AAIIdentityMapParseException;
import org.onap.aai.parsers.exceptions.AmbiguousMapAAIException;
import org.onap.aai.parsers.uri.URIParser;
import org.onap.aai.schema.enums.ObjectMetadata;
import org.onap.aai.setup.SchemaVersions;
import org.springframework.context.ApplicationContext;

/**
 * The Class RelationshipToURI.
 */
public class RelationshipToURI {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelationshipToURI.class);

    private Introspector relationship = null;

    private Loader loader = null;

    private ModelType modelType = null;

    private EdgeIngestor edgeRules = null;

    private URI uri = null;

    private SchemaVersions schemaVersions;

    /**
     * Instantiates a new relationship to URI.
     *
     * @param loader the loader
     * @param relationship the relationship
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    public RelationshipToURI(Loader loader, Introspector relationship)
            throws UnsupportedEncodingException, AAIException {
        this.relationship = relationship;
        this.modelType = relationship.getModelType();
        this.loader = loader;
        this.initEdgeIngestor();
        this.parse();

    }

    protected void initEdgeIngestor() {
        // TODO proper spring wiring, but that requires a lot of refactoring so for now we have this
        ApplicationContext ctx = SpringContextAware.getApplicationContext();
        edgeRules = ctx.getBean(EdgeIngestor.class);
        schemaVersions = (SchemaVersions) ctx.getBean("schemaVersions");
    }

    /**
     * Parses the.
     * 
     * @throws
     *
     *         @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    protected void parse() throws AAIException {
        String relatedLink = (String) relationship.getValue("related-link");
        Optional<URI> result;
        try {
            if (loader.getVersion().compareTo(schemaVersions.getRelatedLinkVersion()) >= 0) {
                result = processRelatedLink(relatedLink);
                if (!result.isPresent()) {
                    result = processRelationshipData();
                }
            } else {
                result = processRelationshipData();
                if (!result.isPresent()) {
                    result = processRelatedLink(relatedLink);
                }
            }
            if (result.isPresent()) {
                this.uri = result.get();
            } else {
                throw new AAIIdentityMapParseException("nothing to parse");
            }
        } catch (AAIException e) {
            throw e;
        } catch (Exception e) {
            throw new AAIIdentityMapParseException("Could not parse relationship-list object: " + e.getMessage(), e);
        }

    }

    private Optional<URI> processRelationshipData() throws AAIException, UnsupportedEncodingException {
        Optional<URI> result = Optional.empty();
        StringBuilder uriBuilder = new StringBuilder();
        List<Object> data = (List<Object>) relationship.getValue("relationship-data");
        Introspector wrapper;
        String key;
        String value;
        String objectType;
        String propertyName;
        String topLevelType = null;
        String[] split;
        HashMap<String, Introspector> map = new HashMap<>();
        for (Object datum : data) {
            wrapper = IntrospectorFactory.newInstance(modelType, datum);
            key = (String) wrapper.getValue("relationship-key");
            value = (String) wrapper.getValue("relationship-value");
            split = key.split("\\.");
            if (split == null || split.length != 2) {
                throw new AAIIdentityMapParseException(
                        "incorrect format for key must be of the form {node-type}.{property-name}");
            }
            // check node name ok
            // check prop name ok
            objectType = split[0];
            propertyName = split[1];

            try {
                Introspector wrappedObj = loader.introspectorFromName(objectType);

                if (!wrappedObj.hasProperty(propertyName)) {
                    throw new AAIIdentityMapParseException("invalid property name in map: " + propertyName);
                }
                if (map.containsKey(objectType)) {
                    wrappedObj = map.get(objectType);
                } else {
                    map.put(objectType, wrappedObj);
                }
                if (wrappedObj.getValue(propertyName) == null) {
                    wrappedObj.setValue(propertyName, value);
                } else {
                    throw new AmbiguousMapAAIException(
                            "cannot determine where key/value goes: " + propertyName + "/" + value);
                }

                if (wrappedObj.getMetadata(ObjectMetadata.NAMESPACE) != null) {
                    if (topLevelType == null) {
                        topLevelType = objectType;
                    } else if (!topLevelType.equals(objectType)) {
                        throw new AmbiguousMapAAIException(
                                "found two top level nodes of different types: " + topLevelType + " and " + objectType);
                    }
                }
            } catch (AAIUnknownObjectException e) {
                throw new AAIIdentityMapParseException("invalid object name in map: " + objectType, e);
            }

        }
        if (!map.isEmpty()) {
            String startType = (String) relationship.getValue("related-to");
            List<String> nodeTypes = new ArrayList<>();
            nodeTypes.addAll(map.keySet());

            String displacedType;
            for (int i = 0; i < nodeTypes.size(); i++) {
                if (nodeTypes.get(i).equals(startType)) {
                    displacedType = nodeTypes.set(nodeTypes.size() - 1, startType);
                    nodeTypes.set(i, displacedType);
                    break;
                }
            }
            sortRelationships(nodeTypes, startType, 1);
            int startTypeIndex = nodeTypes.indexOf(startType);
            int topLevelIndex = 0;
            if (topLevelType != null) {
                topLevelIndex = nodeTypes.indexOf(topLevelType);
            }
            // remove additional types not needed if they are there
            List<String> nodeTypesSubList = nodeTypes;
            if (topLevelIndex != 0) {
                nodeTypesSubList = nodeTypes.subList(topLevelIndex, startTypeIndex + 1);
            }
            for (String type : nodeTypesSubList) {
                uriBuilder.append(map.get(type).getURI());
            }
            if (!nodeTypesSubList.isEmpty()) {
                result = Optional.of(UriBuilder.fromPath(uriBuilder.toString()).build());
            }
        }
        return result;
    }

    private Optional<URI> processRelatedLink(String relatedLink)
            throws URISyntaxException, UnsupportedEncodingException, AAIIdentityMapParseException {
        Optional<URI> result = Optional.empty();
        if (relatedLink != null) {
            URI resultUri = new URI(relatedLink);
            String path = resultUri.toString();
            resultUri = UriBuilder.fromPath(resultUri.getRawPath()).build();
            URIParser uriParser = new URIParser(this.loader, resultUri);
            try {
                uriParser.validate();
            } catch (AAIException e) {
                throw new AAIIdentityMapParseException("related link is invalid: " + relatedLink, e);
            }
            result = Optional.of(resultUri);
        }

        return result;
    }

    /**
     * Sort relationships.
     *
     * @param data the data
     * @param startType the start type
     * @param i the i
     * @return true, if successful
     * @throws AAIException
     */
    private boolean sortRelationships(List<String> data, String startType, int i) throws AAIException {

        if (i == data.size()) {
            return true;
        }
        int j;
        String objectType;
        String displacedObject;
        EdgeRule rule;
        Direction direction;
        for (j = (data.size() - i) - 1; j >= 0; j--) {
            objectType = data.get(j);
            try {
                rule = edgeRules
                        .getRule(new EdgeRuleQuery.Builder(startType, objectType).edgeType(EdgeType.TREE).build());
                direction = rule.getDirection();
                if (direction != null) {
                    if ((rule.getContains().equals(AAIDirection.OUT.toString()) && direction.equals(Direction.IN))
                            || (rule.getContains().equals(AAIDirection.IN.toString())
                                    && direction.equals(Direction.OUT))) {
                        displacedObject = data.set((data.size() - i) - 1, data.get(j));
                        data.set(j, displacedObject);
                        if (sortRelationships(data, objectType, i + 1)) {
                            return true;
                        } else {
                            // continue to process
                        }
                    }
                }
            } catch (AAIException | EdgeRuleNotFoundException | AmbiguousRuleChoiceException e) {
                // ignore exceptions generated
                continue;
            }
        }

        return false;
    }

    /**
     * Gets the uri.
     *
     * @return the uri
     */
    public URI getUri() {
        return uri;
    }

}
