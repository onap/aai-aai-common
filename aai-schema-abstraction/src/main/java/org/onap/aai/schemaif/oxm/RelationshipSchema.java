/**
 * ﻿============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2019 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2019 Amdocs
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai.schemaif.oxm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.codehaus.jackson.map.ObjectMapper;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.edges.EdgeRule;
import org.onap.aai.schemaif.SchemaProviderException;
import org.onap.aai.schemaif.SchemaProviderMsgs;

import com.google.common.collect.Multimap;


public class RelationshipSchema {


    public static final String SCHEMA_SOURCE_NODE_TYPE = "from";
    public static final String SCHEMA_TARGET_NODE_TYPE = "to";
    public static final String SCHEMA_RELATIONSHIP_TYPE = "label";
    public static final String SCHEMA_RULES_ARRAY = "rules";

    private static org.onap.aai.cl.api.Logger logger =
            LoggerFactory.getInstance().getLogger(RelationshipSchema.class.getName());

    private Map<String, Map<String, Class<?>>> relations = new HashMap<>();
    /**
     * Hashmap of valid relationship types along with properties.
     */
    private Map<String, Map<String, Class<?>>> relationTypes = new HashMap<>();
    private Map<String, EdgeRule> relationshipRules = new HashMap<>();

    // A map storing the list of valid edge types for a source/target pair
    private Map<String, Set<String>> edgeTypesForNodePair = new HashMap<>();


    public RelationshipSchema(Multimap<String, EdgeRule> rules, String props) throws SchemaProviderException, IOException {
        HashMap<String, String> properties = new ObjectMapper().readValue(props, HashMap.class);

        // hold the true values of the edge rules by key 
        for (EdgeRule rule : rules.values()) {
            String nodePairKey = buildNodePairKey(rule.getFrom(), rule.getTo());
            if (edgeTypesForNodePair.get(nodePairKey) == null) {
                Set<String> typeSet = new HashSet<String>();
                typeSet.add(rule.getLabel());
                edgeTypesForNodePair.put(nodePairKey, typeSet);
            }
            else {
                edgeTypesForNodePair.get(nodePairKey).add(rule.getLabel());
            }

            String key = buildRelation(rule.getFrom(), rule.getTo(), rule.getLabel());
            relationshipRules.put(key, rule);
        }

        Map<String, Class<?>> edgeProps =
                properties.entrySet().stream().collect(Collectors.toMap(p -> p.getKey(), p -> {
                    try {
                        return resolveClass(p.getValue());
                    } catch (SchemaProviderException | ClassNotFoundException e) {
                        logger.error(SchemaProviderMsgs.SCHEMA_LOAD_ERROR, "Error in RelationshipSchema: " + e);
                    }
                    return null;
                }));

        rules.entries().forEach((kv) -> {
            relationTypes.put(kv.getValue().getLabel(), edgeProps);
            relations.put(buildRelation(kv.getValue().getFrom(), kv.getValue().getTo(), kv.getValue().getLabel()),
                    edgeProps);
        });
    }

    public EdgeRule lookupEdgeRule(String key) throws SchemaProviderException {
        return relationshipRules.get(key);
    }

    public List<EdgeRule> lookupAdjacentEdges(String vertex) throws SchemaProviderException {
        List<EdgeRule> edges = new ArrayList<EdgeRule>();
        for (EdgeRule rule : relationshipRules.values()) {
            if (rule.getFrom().equals(vertex) || rule.getTo().equals(vertex)) {
                edges.add(rule);
            }
        }

        return edges;
    }

    public RelationshipSchema(List<String> jsonStrings) throws SchemaProviderException, IOException {
        String edgeRules = jsonStrings.get(0);
        String props = jsonStrings.get(1);

        HashMap<String, ArrayList<LinkedHashMap<String, String>>> rules =
                new ObjectMapper().readValue(edgeRules, HashMap.class);
        HashMap<String, String> properties = new ObjectMapper().readValue(props, HashMap.class);
        Map<String, Class<?>> edgeProps =
                properties.entrySet().stream().collect(Collectors.toMap(p -> p.getKey(), p -> {
                    try {
                        return resolveClass(p.getValue());
                    } catch (SchemaProviderException | ClassNotFoundException e) {
                        logger.error(SchemaProviderMsgs.SCHEMA_LOAD_ERROR, "Error in RelationshipSchema: " + e);
                    }
                    return null;
                }));

        rules.get(SCHEMA_RULES_ARRAY).forEach(l -> {
            relationTypes.put(l.get(SCHEMA_RELATIONSHIP_TYPE), edgeProps);
            relations.put(buildRelation(l.get(SCHEMA_SOURCE_NODE_TYPE), l.get(SCHEMA_TARGET_NODE_TYPE),
                    l.get(SCHEMA_RELATIONSHIP_TYPE)), edgeProps);
        });
    }



    public Map<String, Class<?>> lookupRelation(String key) {
        return this.relations.get(key);
    }

    public Map<String, Class<?>> lookupRelationType(String type) {
        return this.relationTypes.get(type);
    }

    public boolean isValidType(String type) {
        return relationTypes.containsKey(type);
    }


    private String buildRelation(String source, String target, String relation) {
        return source + ":" + target + ":" + relation;
    }

    public Set<String> getValidRelationTypes(String source, String target) {
        Set<String> typeList = edgeTypesForNodePair.get(buildNodePairKey(source, target));

        if (typeList == null) {
            return new HashSet<String>();
        }

        return typeList;
    }

    private String buildNodePairKey(String source, String target) {
        return source + ":" + target;
    }


    private Class<?> resolveClass(String type) throws SchemaProviderException, ClassNotFoundException {
        Class<?> clazz = Class.forName(type);
        validateClassTypes(clazz);
        return clazz;
    }

    private void validateClassTypes(Class<?> clazz) throws SchemaProviderException {
        if (!clazz.isAssignableFrom(Integer.class) && !clazz.isAssignableFrom(Double.class)
                && !clazz.isAssignableFrom(Boolean.class) && !clazz.isAssignableFrom(String.class)) {
            throw new SchemaProviderException("BAD_REQUEST");
        }
    }
}


