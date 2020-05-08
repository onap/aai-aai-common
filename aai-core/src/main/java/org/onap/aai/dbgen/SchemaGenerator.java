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

package org.onap.aai.dbgen;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.Cardinality;
import org.janusgraph.core.Multiplicity;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.onap.aai.config.SpringContextAware;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.edges.EdgeRule;
import org.onap.aai.edges.exceptions.EdgeRuleNotFoundException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.LoaderUtil;
import org.onap.aai.logging.LogFormatTools;
import org.onap.aai.schema.enums.PropertyMetadata;
import org.onap.aai.util.AAIConfig;
import org.onap.aai.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class SchemaGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaGenerator.class);

    private SchemaGenerator() {

    }

    /**
     * Load schema into JanusGraph.
     *
     * @param graphMgmt
     *        the graph mgmt
     */
    public static void loadSchemaIntoJanusGraph(final JanusGraphManagement graphMgmt, String backend) {

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

        makeEdgeLabels(graphMgmt);


        Map<String, Introspector> objs = LoaderUtil.getLatestVersion().getAllObjects();
        Map<String, PropertyKey> seenProps = new HashMap<>();

        for (Introspector obj : objs.values()) {
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

                        LOGGER.info("Creating PropertyKey: [{}], [{}], [{}]",
                                dbPropName, type.getSimpleName(), cardinality);
                        PropertyKey propK;
                        if (!seenProps.containsKey(dbPropName)) {
                            propK = graphMgmt.makePropertyKey(dbPropName).dataType(type).cardinality(cardinality)
                                    .make();
                            seenProps.put(dbPropName, propK);
                        } else {
                            propK = seenProps.get(dbPropName);
                        }
                        if (graphMgmt.containsGraphIndex(dbPropName)) {
                            LOGGER.debug(" Index  [{}] already existed in the DB. ", dbPropName);
                        } else {
                            if (obj.getIndexedProperties().contains(propName)) {
                                if (obj.getUniqueProperties().contains(propName)) {
                                    LOGGER.info("Add Unique index for PropertyKey: [{}]", dbPropName);
                                    graphMgmt.buildIndex(dbPropName, Vertex.class).addKey(propK).unique()
                                            .buildCompositeIndex();
                                } else {
                                    LOGGER.info("Add index for PropertyKey: [{}]", dbPropName);
                                    graphMgmt.buildIndex(dbPropName, Vertex.class).addKey(propK).buildCompositeIndex();
                                }
                            } else {
                                LOGGER.info("No index added for PropertyKey: [{}]", dbPropName);
                            }
                        }
                    }
                }
            }
        }

        LOGGER.info("-- About to call graphMgmt commit");
        if (backend != null) {
            LOGGER.info("Successfully loaded the schema to {}", backend);
        }

        graphMgmt.commit();
    }

    private static void makeEdgeLabels(JanusGraphManagement graphMgmt) {
        try {
            EdgeIngestor edgeIngestor = SpringContextAware.getBean(EdgeIngestor.class);

            Set<String> labels = Optional.ofNullable(edgeIngestor.getAllCurrentRules())
                    .map(CollectionUtils.collectValues(EdgeRule::getLabel))
                    .orElseGet(HashSet::new);

            labels.forEach(label -> {
                if (graphMgmt.containsRelationType(label)) {
                    LOGGER.debug(" EdgeLabel  [{}] already existed. ", label);
                } else {
                    LOGGER.debug("Making EdgeLabel: [{}]", label);
                    graphMgmt.makeEdgeLabel(label).multiplicity(Multiplicity.valueOf("MULTI")).make();
                }
            });
        } catch (EdgeRuleNotFoundException e) {
            LOGGER.error("Unable to find all rules {}", LogFormatTools.getStackTop(e));
        }
    }

}
