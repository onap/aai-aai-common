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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.onap.aai.AAISetup;
import org.onap.aai.DataLinkSetup;
import org.onap.aai.dbmap.DBConnectionType;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.*;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.schema.enums.PropertyMetadata;
import org.onap.aai.serialization.engines.JanusGraphDBEngine;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.setup.SchemaVersion;
import org.springframework.test.annotation.DirtiesContext;

@RunWith(value = Parameterized.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class DbAliasTest extends DataLinkSetup {

    private JanusGraph graph;

    private SchemaVersion version;
    private final ModelType introspectorFactoryType = ModelType.MOXY;
    private final DBConnectionType type = DBConnectionType.REALTIME;
    private Loader loader;
    private TransactionalGraphEngine dbEngine;

    @Parameterized.Parameter(value = 0)
    public QueryStyle queryStyle;

    @Parameterized.Parameters(name = "QueryStyle.{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {{QueryStyle.TRAVERSAL}, {QueryStyle.TRAVERSAL_URI}});
    }

    @Before
    public void setup() throws Exception {
        version = schemaVersions.getDepthVersion();
        graph = JanusGraphFactory.build().set("storage.backend", "inmemory").open();
        loader = loaderFactory.createLoaderForVersion(introspectorFactoryType, version);
        dbEngine = new JanusGraphDBEngine(queryStyle, type, loader);
    }

    @After
    public void tearDown() {
        graph.tx().rollback();
        graph.close();
    }

    @Test
    public void checkOnWrite() throws AAIException, UnsupportedEncodingException, URISyntaxException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException,
            NoSuchMethodException, InterruptedException {
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

        assertEquals("dbAlias is ", "model-customization-id", dbPropertyName);
        assertEquals("dbAlias property exists", "hello", v.property(dbPropertyName).orElse(""));
        assertEquals("model property does not", "missing", v.property(property).orElse("missing"));

    }

    @Test
    public void checkOnRead() throws AAIException, UnsupportedEncodingException, URISyntaxException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException,
            NoSuchMethodException, InterruptedException, MalformedURLException {
        final String property = "persona-model-customization-id";

        TransactionalGraphEngine spy = spy(dbEngine);
        TransactionalGraphEngine.Admin adminSpy = spy(dbEngine.asAdmin());
        Vertex v = graph.traversal().addV("vnf-id", "key1", "model-customization-id", "hello").next();
        graph.tx().commit();
        Graph g = graph.newTransaction();
        GraphTraversalSource traversal = g.traversal();
        when(spy.asAdmin()).thenReturn(adminSpy);
        when(adminSpy.getTraversalSource()).thenReturn(traversal);
        DBSerializer serializer = new DBSerializer(version, spy, introspectorFactoryType, "AAI_TEST");
        Introspector obj = loader.introspectorFromName("generic-vnf");
        serializer.dbToObject(Collections.singletonList(v), obj, 0, true, "false");

        assertEquals("dbAlias property exists", "hello", obj.getValue(property));

    }

}
