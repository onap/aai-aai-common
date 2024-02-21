/*
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright © 2024 DEUTSCHE TELEKOM AG.
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

package org.onap.aai.dbgen;

import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.Cardinality;
import org.janusgraph.core.EdgeLabel;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.Multiplicity;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.schema.ConsistencyModifier;
import org.janusgraph.core.schema.JanusGraphIndex;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.janusgraph.core.schema.JanusGraphManagement.IndexJobFuture;
import org.janusgraph.core.schema.RelationTypeIndex;
import org.janusgraph.core.schema.SchemaAction;
import org.janusgraph.core.schema.SchemaStatus;
import org.janusgraph.graphdb.database.StandardJanusGraph;
import org.janusgraph.graphdb.database.management.ManagementSystem;
import org.janusgraph.graphdb.database.management.RelationIndexStatusReport;
import org.onap.aai.config.SpringContextAware;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.edges.EdgeRule;
import org.onap.aai.edges.exceptions.EdgeRuleNotFoundException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.LoaderUtil;
import org.onap.aai.logging.LogFormatTools;
import org.onap.aai.schema.enums.PropertyMetadata;
import org.onap.aai.util.AAIConfig;
import org.onap.aai.util.AAIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemaGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaGenerator.class);
    private static int indexRecoveryRetryCounter = 0;

    private SchemaGenerator() {
    }

    /**
     * Load schema into JanusGraph.
     *
     * @param graphMgmt
     *        the graph mgmt
     */
    public static List<String> loadSchemaIntoJanusGraph(final JanusGraph graph, String backend, boolean dbNotEmpty) {
        JanusGraphManagement graphMgmt = graph.openManagement();
        final List<String> elementsToReindex = new ArrayList<>();

        try {
            AAIConfig.init();
        } catch (Exception ex) {
            LOGGER.error(" ERROR - Could not run AAIConfig.init(). {}", LogFormatTools.getStackTop(ex));
            System.exit(1);
        }

        // NOTE - JanusGraph 0.5.3 doesn't keep a list of legal node Labels.
        // They are only used when a vertex is actually being created.
        // JanusGraph 1.1 will keep track (we think).

        // Use EdgeRules to make sure edgeLabels are defined in the db. NOTE:
        // the multiplicty used here is
        // always "MULTI". This is not the same as our internal "Many2Many",
        // "One2One", "One2Many" or "Many2One"
        // We use the same edge-label for edges between many different types of
        // nodes and our internal
        // multiplicty definitions depends on which two types of nodes are being
        // connected.

        final Map<String, Introspector> objs = LoaderUtil.getLatestVersion().getAllObjects();
        final Map<String, PropertyKey> seenProps = new HashMap<>();
        
        for (Introspector obj : objs.values()) {
            createSchemaForObject(graphMgmt, seenProps, obj);
        }

        makeEdgeLabels(graphMgmt, elementsToReindex, dbNotEmpty);

        LOGGER.info("-- About to call graphMgmt commit");
        if (backend != null) {
            LOGGER.info("Successfully loaded the schema to {}", backend);
        }

        graphMgmt.commit();
        return elementsToReindex;
    }

    public static void reindexEdgeIndexes(final JanusGraph graph, Collection<String> edgeLabels) {
        graph.tx().rollback();
        if(edgeLabels.isEmpty()) {
            LOGGER.info("Nothing to reindex.");
            return;
        }

        ensureValidIndexState(graph, edgeLabels);

        awaitRelationIndexStatus(graph, edgeLabels,SchemaStatus.REGISTERED);

        LOGGER.info("Attempting to transition indexes in REGISTERED state to ENABLED");
        updateRelationIndexes(graph, edgeLabels, SchemaAction.REINDEX);

        ensureEnabledIndexState(graph, edgeLabels);
    }

    /**
     * Indices need to be in status REGISTERED or ENABLED to allow reindexing.
     * @param graph
     * @param edgeLabels
     */
    private static void ensureEnabledIndexState(final JanusGraph graph, Collection<String> edgeLabels) {
        JanusGraphManagement graphMgmt = graph.openManagement();
        List<String> registeredIndexes = new ArrayList<>();
        for(String label: edgeLabels) {
            EdgeLabel relation = graphMgmt.getEdgeLabel(label);
            RelationTypeIndex index = graphMgmt.getRelationIndex(relation, label);
            SchemaStatus indexStatus = index.getIndexStatus();
            if(indexStatus.equals(SchemaStatus.REGISTERED)) {
                LOGGER.info("Detected relation index [{}] that is not yet in ENABLED state", relation.name());
                registeredIndexes.add(label);
            }
        }
        graphMgmt.commit();

        if(indexRecoveryRetryCounter <= 8 && !registeredIndexes.isEmpty()) {
            indexRecoveryRetryCounter++;
            LOGGER.info("[{}] indexes not yet in ENABLED state", registeredIndexes.size());
            awaitRelationIndexStatus(graph, registeredIndexes,SchemaStatus.ENABLED);

            ensureEnabledIndexState(graph, edgeLabels); // recursively call to make sure there is no invalid state
        } else {
            LOGGER.info("All indexes are in ENABLED state, exiting.");
            return;
        }
    }

    /**
     * Indices need to be in status REGISTERED or ENABLED to allow reindexing.
     * @param graph
     * @param edgeLabels
     */
    private static void ensureValidIndexState(final JanusGraph graph, Collection<String> edgeLabels) {
        JanusGraphManagement graphMgmt = graph.openManagement();
        List<String> installedIndexes = new ArrayList<>();
        List<String> disabledIndexes = new ArrayList<>();
        for(String label: edgeLabels) {
            EdgeLabel relation = graphMgmt.getEdgeLabel(label);
            RelationTypeIndex index = graphMgmt.getRelationIndex(relation, label);
            SchemaStatus indexStatus = index.getIndexStatus();
            if(indexStatus.equals(SchemaStatus.INSTALLED)) {
                LOGGER.info("Detected relation index [{}] with invalid status [{}]", relation.name(), indexStatus);
                installedIndexes.add(label);
            } else if(indexStatus.equals(SchemaStatus.DISABLED)) {
                LOGGER.info("Detected relation index [{}] with invalid status [{}]", relation.name(), indexStatus);
                disabledIndexes.add(label);
            }
        }
        graphMgmt.commit();

        if(indexRecoveryRetryCounter <= 300 && (!installedIndexes.isEmpty() || !disabledIndexes.isEmpty())) {
            indexRecoveryRetryCounter++;
            if(!installedIndexes.isEmpty()) {
                LOGGER.info("Attempting to transition indexes in INSTALLED state to REGISTERED");
                updateRelationIndexes(graph, installedIndexes, SchemaAction.REGISTER_INDEX);
                awaitRelationIndexStatus(graph, edgeLabels,SchemaStatus.REGISTERED);
            }
            if(!disabledIndexes.isEmpty()) {
                LOGGER.info("Attempting to transition indexes in DISABLED state to ENABLED");
                updateRelationIndexes(graph, disabledIndexes, SchemaAction.ENABLE_INDEX);
                awaitRelationIndexStatus(graph, edgeLabels,SchemaStatus.ENABLED);
            }
            ensureValidIndexState(graph, edgeLabels); // recursively call to make sure there is no invalid state
        } else {
            return;
        }
    }

    private static void createSchemaForObject(final JanusGraphManagement graphMgmt, Map<String, PropertyKey> seenProps,
            Introspector obj) {
        for (String propName : obj.getProperties()) {
            String dbPropName = propName;
            Optional<String> alias = obj.getPropertyMetadata(propName, PropertyMetadata.DB_ALIAS);
            if (alias.isPresent()) {
                dbPropName = alias.get();
            }
            if (graphMgmt.containsRelationType(dbPropName)) {
                LOGGER.debug(" PropertyKey  [{}] already existed in the DB. ", dbPropName);
            } else {
                Class<?> type = obj.getClass(propName);
                Cardinality cardinality = Cardinality.SINGLE;
                boolean process = false;
                if (obj.isListType(propName) && obj.isSimpleGenericType(propName)) {
                    cardinality = Cardinality.SET;
                    type = obj.getGenericTypeClass(propName);
                    process = true;
                } else if (obj.isSimpleType(propName)) {
                    process = true;
                }

                if (process) {

                    LOGGER.info("Creating PropertyKey: [{}], [{}], [{}]", dbPropName, type.getSimpleName(),
                            cardinality);
                    PropertyKey propertyKey;
                    if (!seenProps.containsKey(dbPropName)) {
                        propertyKey = graphMgmt.makePropertyKey(dbPropName).dataType(type).cardinality(cardinality)
                                .make();
                        if (dbPropName.equals("aai-uri")) {
                            String aai_uri_lock_enabled = AAIConfig.get(AAIConstants.AAI_LOCK_URI_ENABLED, "false");
                            LOGGER.info(" Info: aai_uri_lock_enabled:" + aai_uri_lock_enabled);
                            if ("true".equals(aai_uri_lock_enabled)) {
                                LOGGER.info(" Lock is being set for aai-uri Property.");
                                graphMgmt.setConsistency(propertyKey, ConsistencyModifier.LOCK);
                            }
                        } else if (dbPropName.equals("resource-version")) {
                            String aai_rv_lock_enabled = AAIConfig.get(AAIConstants.AAI_LOCK_RV_ENABLED, "false");
                            LOGGER.info(" Info: aai_rv_lock_enabled:" + aai_rv_lock_enabled);
                            if ("true".equals(aai_rv_lock_enabled)) {
                                LOGGER.info(" Lock is being set for resource-version Property.");
                                graphMgmt.setConsistency(propertyKey, ConsistencyModifier.LOCK);
                            }
                        }
                        seenProps.put(dbPropName, propertyKey);
                    } else {
                        propertyKey = seenProps.get(dbPropName);
                    }
                    if (graphMgmt.containsGraphIndex(dbPropName)) {
                        LOGGER.debug(" Index  [{}] already existed in the DB. ", dbPropName);
                    } else {
                        if (obj.getIndexedProperties().contains(propName)) {
                            createIndexForProperty(graphMgmt, obj, propName, dbPropName, propertyKey);
                        } else {
                            LOGGER.info("No index added for PropertyKey: [{}]", dbPropName);
                        }
                    }
                }
            }
        }
    }

    private static void createIndexForProperty(final JanusGraphManagement graphMgmt, Introspector obj, String propName,
            String dbPropName, PropertyKey propertyKey) {
        JanusGraphIndex indexG = null;
        if (obj.getUniqueProperties().contains(propName)) {
            LOGGER.info("Add Unique index for PropertyKey: [{}]", dbPropName);
            indexG = graphMgmt.buildIndex(dbPropName, Vertex.class).addKey(propertyKey).unique()
                    .buildCompositeIndex();
        } else {
            LOGGER.info("Add index for PropertyKey: [{}]", dbPropName);
            indexG = graphMgmt.buildIndex(dbPropName, Vertex.class).addKey(propertyKey)
                    .buildCompositeIndex();
        }
        if (indexG != null && dbPropName.equals("aai-uri")) {
            String aai_uri_lock_enabled =
                    AAIConfig.get(AAIConstants.AAI_LOCK_URI_ENABLED, "false");
            LOGGER.info(" Info:: aai_uri_lock_enabled:" + aai_uri_lock_enabled);
            if ("true".equals(aai_uri_lock_enabled)) {
                LOGGER.info("Lock is being set for aai-uri Index.");
                graphMgmt.setConsistency(indexG, ConsistencyModifier.LOCK);
            }
        } else if (indexG != null && dbPropName.equals("resource-version")) {
            String aai_rv_lock_enabled =
                    AAIConfig.get(AAIConstants.AAI_LOCK_RV_ENABLED, "false");
            LOGGER.info(" Info:: aai_rv_lock_enabled:" + aai_rv_lock_enabled);
            if ("true".equals(aai_rv_lock_enabled)) {
                LOGGER.info("Lock is being set for resource-version Index.");
                graphMgmt.setConsistency(indexG, ConsistencyModifier.LOCK);
            }
        }
    }

    /**
     * Debug method to print current index states.
     * This can help diagnosing indexes that are stuck in INSTALLED state.
     * @param graph
     */
    public static void printCurrentRelationIndexStates(JanusGraph graph) {
        JanusGraphManagement graphMgmt = graph.openManagement();
        EdgeIngestor edgeIngestor = SpringContextAware.getBean(EdgeIngestor.class);
        try {
            Set<String> edgeLabels = Optional.ofNullable(edgeIngestor.getAllCurrentRules())
                        .map(collectValues(EdgeRule::getLabel)).orElseGet(HashSet::new);
            edgeLabels.stream()
                .forEach(label -> {
                    EdgeLabel relation = graphMgmt.getEdgeLabel(label);
                    RelationTypeIndex index = graphMgmt.getRelationIndex(relation, label);
                    LOGGER.info("Index state of relation index [{}] is [{}]",label, index.getIndexStatus());
                });
        } catch (EdgeRuleNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Jointly wait for a set of relation indexes to change status.
     * Use this after calling {@link JanusGraphManagement#updateIndex(org.janusgraph.core.schema.Index, SchemaAction)}
     * @param graph the JanusGraph
     * @param labels the names of the indexes
     * @param newStatus the new SchemaStatus
     */
    private static void awaitRelationIndexStatus(JanusGraph graph, Collection<String> labels, SchemaStatus newStatus) {
        LOGGER.info("Awaiting index status [{}]", newStatus);;
        CompletableFuture<RelationIndexStatusReport>[] awaits = labels.stream()
            .map(label -> 
                CompletableFuture.supplyAsync(() -> {
                    try {
                        return ManagementSystem
                            .awaitRelationIndexStatus(graph, label, label)
                            .status(newStatus)
                            .call();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return null;
                    }
                }))
            .toArray(CompletableFuture[]::new);
        try {
            CompletableFuture.allOf(awaits).get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error while waiting for change in relation index status");
            e.printStackTrace();
        }
        LOGGER.info("Completed waiting for index status [{}]", newStatus);
    }

    private static void updateRelationIndexes(JanusGraph graph, Collection<String> labels, SchemaAction updateAction) {
        JanusGraphManagement graphMgmt = graph.openManagement();

        CompletableFuture<IndexJobFuture>[] awaits = labels.stream()
            .map(label -> 
                CompletableFuture.supplyAsync(() -> {
                    EdgeLabel relation = graphMgmt.getEdgeLabel(label);
                    RelationTypeIndex index = graphMgmt.getRelationIndex(relation, label);
                    LOGGER.info("Updating relation index [{}] status from [{}] to [{}]", relation.name(), index.getIndexStatus(), updateAction);
                    return graphMgmt.updateIndex(index, updateAction);
                }))
            .toArray(CompletableFuture[]::new);
        try {
            CompletableFuture.allOf(awaits).get();
            LOGGER.info("Completed reindex actions");
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error while waiting for change in relation index status");
            e.printStackTrace();
        }
        graphMgmt.commit();
    }

    /**
     * Radical approach to avoiding index update failures.
     * Indexes can get stuck in INSTALLED state, when there are stale transactions or JanusGraph instances.
     * This is because a state change needs to be acknowledged by all instances before transitioning.
     * @param graph
     * @return
     */
    private static void killTransactionsAndInstances(JanusGraph graph) {
        graph.tx().rollback();
        final StandardJanusGraph janusGraph = (StandardJanusGraph) graph;
        janusGraph.getOpenTransactions().stream().forEach(transaction -> {
                LOGGER.debug("Closing open transaction [{}] before schema generation", transaction.toString());
                transaction.rollback();
        });
        
        final JanusGraphManagement graphMgtForClosing = graph.openManagement();

        Set<String> instances = graphMgtForClosing.getOpenInstances();
        LOGGER.info("Number of open instances: {}", instances.size());
        LOGGER.info("Currently open instances: [{}]", instances);
        instances.stream()
            // .filter(instance -> !instance.contains("graphadmin"))
            .filter(instance -> !instance.contains("(current)"))
            .forEach(instance -> {
                    LOGGER.debug("Closing open JanusGraph instance [{}] before reindexing procedure", instance);
                    graphMgtForClosing.forceCloseInstance(instance);
            });
        graphMgtForClosing.commit();
    }

    private static void makeEdgeLabels(JanusGraphManagement graphMgmt, List<String> elementsToReindex, boolean dbNotEmpty) {
        try {
            EdgeIngestor edgeIngestor = SpringContextAware.getBean(EdgeIngestor.class);

            Set<String> labels = Optional.ofNullable(edgeIngestor.getAllCurrentRules())
            .map(collectValues(EdgeRule::getLabel)).orElseGet(HashSet::new);

            labels.forEach(label -> {
                if (graphMgmt.containsRelationType(label)) {
                    LOGGER.debug(" EdgeLabel  [{}] already exists.", label);
                } else {
                    LOGGER.debug("Making EdgeLabel: [{}]", label);
                    graphMgmt.makeEdgeLabel(label).multiplicity(Multiplicity.MULTI).make();
                }
                EdgeLabel relation = graphMgmt.getEdgeLabel(label);
                RelationTypeIndex relationIndex = graphMgmt.getRelationIndex(relation, label);
                if(relationIndex == null) {
                    LOGGER.debug("Creating edge index for relation: " + label);
                    graphMgmt.buildEdgeIndex(relation, label, Direction.BOTH, graphMgmt.getPropertyKey("aai-node-type"));
                    if(dbNotEmpty) {
                        LOGGER.info("DB not empty. Registering edge [{}] for later reindexing.", label);
                        elementsToReindex.add(relation.name());
                    }
                } else if(!relationIndex.getIndexStatus().equals(SchemaStatus.ENABLED)) {
                    LOGGER.info("Relation index was already created but is not in ENABLED status. Current status: [{}]", relationIndex.getIndexStatus());
                    elementsToReindex.add(label);
                } else {
                    LOGGER.debug("Edge index for label [{}] already exists", label);
                };
            });
        } catch (EdgeRuleNotFoundException e) {
            LOGGER.error("Unable to find all rules {}", LogFormatTools.getStackTop(e));
        }
    }

    /**
     * Returns a function collecting all the values in a {@link com.google.common.collect.Multimap}
     * given a mapping function
     *
     * @param f The mapper function
     * @param <K> The type of key used by the provided {@link com.google.common.collect.Multimap}
     * @param <V> The type of value used by the provided {@link com.google.common.collect.Multimap}
     * @param <V0> The type which <V> is mapped to
     */
    private static <K, V, V0> Function<Multimap<K, V>, Set<V0>> collectValues(Function<V, V0> f) {
        return as -> as.values().stream().map(f).collect(Collectors.toSet());
    }

}
