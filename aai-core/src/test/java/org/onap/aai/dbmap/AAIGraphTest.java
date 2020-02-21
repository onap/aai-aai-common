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

package org.onap.aai.dbmap;

import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.schema.JanusGraphIndex;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.config.SpringContextAware;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.schema.enums.PropertyMetadata;
import org.onap.aai.setup.SchemaVersions;
import org.onap.aai.util.AAIConstants;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.Assert.*;

public class AAIGraphTest extends AAISetup {
    @Before
    public void setup() {
        AAIGraph.getInstance();
    }

    @Test
    public void getRealtimeInstanceConnectionName() throws Exception {

        JanusGraphManagement graphMgt = AAIGraph.getInstance().getGraph().openManagement();
        String connectionInstanceName =
                graphMgt.getOpenInstances().stream().filter(c -> c.contains("current")).findFirst().get();
        assertThat(connectionInstanceName, containsString(SERVICE_NAME));
        assertThat(connectionInstanceName, containsString("realtime"));
        assertThat(connectionInstanceName,
                matchesPattern("^\\d+_[\\w\\-\\d]+_" + SERVICE_NAME + "_realtime_\\d+\\(current\\)$"));
        graphMgt.rollback();
    }


    @Test
    public void janusGraphOpenNameTest() throws Exception {
        JanusGraph graph = JanusGraphFactory.open(new AAIGraphConfig.Builder(AAIConstants.REALTIME_DB_CONFIG)
                .forService(SERVICE_NAME).withGraphType("graphType").buildConfiguration());
        JanusGraphManagement graphMgt = graph.openManagement();
        String connectionInstanceName =
                graphMgt.getOpenInstances().stream().filter(c -> c.contains("current")).findFirst().get();
        assertThat(connectionInstanceName,
                matchesPattern("^\\d+_[\\w\\-\\d]+_" + SERVICE_NAME + "_graphType_\\d+\\(current\\)$"));
        graphMgt.rollback();
        graph.close();
    }

    @Test(expected = FileNotFoundException.class)
    public void janusGraphOpenNameWithInvalidFilePathTest() throws Exception {
        JanusGraph graph = JanusGraphFactory.open(new AAIGraphConfig.Builder("invalid").forService(SERVICE_NAME)
                .withGraphType("graphType").buildConfiguration());
        JanusGraphManagement graphMgt = graph.openManagement();
        String connectionInstanceName =
                graphMgt.getOpenInstances().stream().filter(c -> c.contains("current")).findFirst().get();
        assertThat(connectionInstanceName,
                matchesPattern("^\\d+_[\\w\\-\\d]+_" + SERVICE_NAME + "_graphType_\\d+\\(current\\)$"));
        graphMgt.rollback();
        graph.close();
    }

    @Ignore("Need to create schema specific to the test")
    @Test
    public void checkIndexOfAliasedIndexedProps() throws Exception {
        Set<String> aliasedIndexedProps = getAliasedIndexedProps();
        JanusGraphManagement graphMgt = AAIGraph.getInstance().getGraph().openManagement();
        for (String aliasedIndexedProp : aliasedIndexedProps) {
            JanusGraphIndex index = graphMgt.getGraphIndex(aliasedIndexedProp);
            assertNotNull(aliasedIndexedProp + " index exists", index);
            assertEquals(aliasedIndexedProp + " index has 1 property keys", index.getFieldKeys().length, 1);
            assertThat(aliasedIndexedProp + " index indexes " + aliasedIndexedProp + " property key",
                    index.getFieldKeys()[0].name(), is(aliasedIndexedProp));
        }
        graphMgt.rollback();
    }

    private Set<String> getAliasedIndexedProps() {
        Set<String> aliasedIndexedProps = new HashSet<>();
        LoaderFactory loaderFactory = SpringContextAware.getBean(LoaderFactory.class);
        SchemaVersions schemaVersions = SpringContextAware.getBean(SchemaVersions.class);
        Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDefaultVersion());
        Map<String, Introspector> objs = loader.getAllObjects();
        for (Introspector obj : objs.values()) {
            for (String propName : obj.getProperties()) {
                Optional<String> alias = obj.getPropertyMetadata(propName, PropertyMetadata.DB_ALIAS);
                if (alias.isPresent()) {
                    String dbPropName = alias.get();
                    if (obj.getIndexedProperties().contains(propName)) {
                        aliasedIndexedProps.add(dbPropName);
                    }
                }
            }
        }
        return aliasedIndexedProps;
    }

}
