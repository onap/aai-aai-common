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

package org.onap.aai.serialization.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.onap.aai.DataLinkSetup;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.schema.enums.PropertyMetadata;
import org.onap.aai.serialization.engines.JanusGraphDBEngine;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.setup.SchemaVersion;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class DbAliasTest extends DataLinkSetup {

    private JanusGraph graph;

    private SchemaVersion version;
    private final ModelType introspectorFactoryType = ModelType.MOXY;
    private Loader loader;
    private TransactionalGraphEngine dbEngine;
    public QueryStyle queryStyle;

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {{QueryStyle.TRAVERSAL}, {QueryStyle.TRAVERSAL_URI}});
    }

    @BeforeEach
    public void setup() {
        version = schemaVersions.getDepthVersion();
        graph = JanusGraphFactory.build().set("storage.backend", "inmemory").open();
        loader = loaderFactory.createLoaderForVersion(introspectorFactoryType, version);
        dbEngine = new JanusGraphDBEngine(queryStyle, loader);
    }

    @AfterEach
    public void tearDown() {
        graph.tx().rollback();
        graph.close();
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void checkOnWrite(QueryStyle queryStyle) throws AAIException, UnsupportedEncodingException, URISyntaxException, SecurityException,
            IllegalArgumentException {
        initDbAliasTest(queryStyle);
        final String property = "persona-model-customization-id";
        String dbPropertyName = property;
        TransactionalGraphEngine spy = spy(this.dbEngine);
        TransactionalGraphEngine.Admin adminSpy = spy(dbEngine.asAdmin());
        Graph g = graph.newTransaction();
        GraphTraversalSource traversal = g.traversal();
        when(spy.asAdmin()).thenReturn(adminSpy);
        when(adminSpy.getTraversalSource()).thenReturn(traversal);
        DBSerializer serializer = new DBSerializer(version, spy, introspectorFactoryType, "AAI_TEST");
        QueryParser uriQuery =
                spy.getQueryBuilder().createQueryFromURI(new URI("network/generic-vnfs/generic-vnf/key1"));
        Introspector obj = loader.introspectorFromName("generic-vnf");
        Vertex v = g.addVertex();
        v.property("aai-uri", "abc");
        v.property("aai-uuid", "b");
        v.property(AAIProperties.CREATED_TS, 123L);
        v.property(AAIProperties.SOURCE_OF_TRUTH, "sot");
        v.property(AAIProperties.RESOURCE_VERSION, "123");
        v.property(AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, "lmsot");
        v.property(AAIProperties.LAST_MOD_TS, 123L);
        Object id = v.id();
        obj.setValue("vnf-id", "key1");
        obj.setValue(property, "hello");
        serializer.serializeToDb(obj, v, uriQuery, "", "");
        g.tx().commit();
        v = graph.traversal().V(id).next();
        Map<PropertyMetadata, String> map = obj.getPropertyMetadata(property);
        if (map.containsKey(PropertyMetadata.DB_ALIAS)) {
            dbPropertyName = map.get(PropertyMetadata.DB_ALIAS);
        }

        assertEquals("model-customization-id", dbPropertyName, "dbAlias is ");
        assertEquals("hello", v.property(dbPropertyName).orElse(""), "dbAlias property exists");
        assertEquals("missing", v.property(property).orElse("missing"), "model property does not");

    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void checkOnRead(QueryStyle queryStyle)
            throws AAIException, UnsupportedEncodingException, SecurityException, IllegalArgumentException {
        initDbAliasTest(queryStyle);
        final String property = "persona-model-customization-id";

        TransactionalGraphEngine spy = spy(dbEngine);
        TransactionalGraphEngine.Admin adminSpy = spy(dbEngine.asAdmin());
        Vertex v =
                graph.traversal().addV().property("vnf-id", "key1").property("model-customization-id", "hello").next();
        graph.tx().commit();
        Graph g = graph.newTransaction();
        GraphTraversalSource traversal = g.traversal();
        when(spy.asAdmin()).thenReturn(adminSpy);
        when(adminSpy.getTraversalSource()).thenReturn(traversal);
        DBSerializer serializer = new DBSerializer(version, spy, introspectorFactoryType, "AAI_TEST");
        Introspector obj = loader.introspectorFromName("generic-vnf");
        serializer.dbToObject(Collections.singletonList(v), obj, 0, true, "false");

        assertEquals("hello", obj.getValue(property), "dbAlias property exists");

    }

    public void initDbAliasTest(QueryStyle queryStyle) {
        this.queryStyle = queryStyle;
    }

}
