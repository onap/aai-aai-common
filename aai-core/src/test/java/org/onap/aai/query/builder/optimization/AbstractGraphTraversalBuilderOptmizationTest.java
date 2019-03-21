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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.query.builder.optimization;

import static org.junit.Assert.assertEquals;

import com.google.common.base.CaseFormat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.After;
import org.junit.AfterClass;
import org.onap.aai.AAISetup;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.dbmap.AAIGraph;
import org.onap.aai.dbmap.DBConnectionType;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.query.builder.QueryBuilder;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.db.EdgeSerializer;
import org.onap.aai.serialization.engines.JanusGraphDBEngine;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractGraphTraversalBuilderOptmizationTest extends AAISetup {

    protected static final List<String> RANDOM_VALUES = Arrays.asList("A", "B", "C", "D", "E");

    protected static final String crUriPattern =
        "/cloud-infrastructure/cloud-regions/cloud-region/%s/%s";
    protected static final String tenantUriPatternSuffix = "/tenants/tenant/%s";
    protected static final String tenantUriPattern = crUriPattern + tenantUriPatternSuffix;
    protected static final String vserverUriPatternSuffix = "/vservers/vserver/%s";
    protected static final String vserverUriPattern = tenantUriPattern + vserverUriPatternSuffix;

    public static final String VSERVER_ID = "vserver-id";
    public static final String TENANT_ID = "tenant-id";
    public static final String TENANT_NAME = "tenant-name";
    public static final String PROV_STATUS = "prov-status";
    public static final String VSERVER_NAME = "vserver-name";
    public static final String VSERVER_SELFLINK = "vserver-selflink";
    public static final String TENANT = "tenant";
    public static final String VSERVER = "vserver";
    public static final String CLOUD_REGION = "cloud-region";
    public static final String CLOUD_REGION_ID = "cloud-region-id";
    public static final String CLOUD_OWNER = "cloud-owner";

    private static final ModelType introspectorFactoryType = ModelType.MOXY;
    private static final QueryStyle queryStyle = QueryStyle.TRAVERSAL;
    private static final DBConnectionType type = DBConnectionType.REALTIME;
    private static TransactionalGraphEngine dbEngine;
    private static DBSerializer dbser;
    protected static Loader loader;
    protected static Graph graph;
    protected static GraphTraversalSource g;
    @Autowired
    private static EdgeSerializer edgeSer;

    protected static Random rand;

    protected void setupData(int tenantNum, int vserverPerTenantNum, String prefix)
        throws Exception {
        loader = loaderFactory.createLoaderForVersion(introspectorFactoryType,
            schemaVersions.getDefaultVersion());
        graph = AAIGraph.getInstance().getGraph();

        dbEngine = new JanusGraphDBEngine(queryStyle, type, loader);
        g = dbEngine.startTransaction().traversal();
        dbser = new DBSerializer(schemaVersions.getDefaultVersion(), dbEngine,
            introspectorFactoryType, "AAI-TEST-" + prefix);

        rand = new Random();

        Vertex cr;
        Vertex tenant;
        Vertex vserver;
        String crUri;
        String tenantUri;
        String vserverUri;
        System.out.println("Data load started");
        long startTime = System.currentTimeMillis();
        for (int crCtr = 0; crCtr < 3; crCtr++) {
            crUri = String.format(crUriPattern, prefix + "cloud-owner-" + crCtr,
                prefix + "cloud-region-id-" + crCtr);
            // System.out.println(crUri);
            cr = g.addV(AAIProperties.NODE_TYPE, CLOUD_REGION, CLOUD_REGION_ID,
                prefix + "cloud-region-id-" + crCtr, CLOUD_OWNER, prefix + "cloud-owner-" + crCtr,
                AAIProperties.AAI_URI, crUri).next();
            for (int i = 0; i < tenantNum; i++) {
                Introspector intro = loader.introspectorFromName(TENANT);
                tenant = dbser.createNewVertex(intro);
                edgeSer.addTreeEdge(g, cr, tenant);
                intro.setValue(TENANT_ID, prefix + "tenant-id-" + i);
                intro.setValue(TENANT_NAME, prefix + "tenant-name-" + i);
                dbser.serializeSingleVertex(tenant, intro, "AAI-TEST-" + prefix);
                // System.out.println("Tenant " + crCtr + " " + i);
                for (int j = 0; j < vserverPerTenantNum; j++) {
                    intro = loader.introspectorFromName(VSERVER);
                    vserver = dbser.createNewVertex(intro);
                    edgeSer.addTreeEdge(g, tenant, vserver);
                    intro.setValue(VSERVER_ID, prefix + "vserver-id-" + i + "-" + j);
                    intro.setValue(VSERVER_NAME, prefix + "vserver-name-" + i + "-" + j);
                    intro.setValue(PROV_STATUS,
                        RANDOM_VALUES.get(rand.nextInt(RANDOM_VALUES.size())));
                    intro.setValue(VSERVER_SELFLINK,
                        RANDOM_VALUES.get(rand.nextInt(RANDOM_VALUES.size())));
                    dbser.serializeSingleVertex(vserver, intro, "AAI-TEST-" + prefix);
                    // System.out.println("Vserver " + crCtr + " " + i + " " + j);
                }
            }
        }
        // g.V().forEachRemaining(v -> v.properties().forEachRemaining(p ->
        // System.out.println(p.key() + " : " + p.value())));
        // g.E().forEachRemaining(e ->
        // System.out.println(e.outVertex().property(AAIProperties.NODE_TYPE).value() + " : " +
        // e.inVertex().property(AAIProperties.NODE_TYPE).value()));
        long time = System.currentTimeMillis() - startTime;
        System.out.println("Data load ended\n" + time);

    }

    @After
    public void deConfigure() throws Exception {
    }

    @AfterClass
    public static void teardown() throws Exception {
        dbEngine.rollback();
        System.out.println("Done");
    }

    protected void execute(Method getQueryMethod, int numResultsExpected) throws Exception {

        int iterations = numOfTimesToRun();
        long noneTimer = 0;
        long uriTimer = 0;
        for (int i = 0; i < iterations + 1; i++) {
            if (i == 0) { // dont use incase initial cold starts
                timeQuery(getQuery(getQueryMethod, OptimizeEnum.NONE), numResultsExpected,
                    OptimizeEnum.NONE);
                timeQuery(getQuery(getQueryMethod, OptimizeEnum.URI), numResultsExpected,
                    OptimizeEnum.URI);
            } else {
                noneTimer += timeQuery(getQuery(getQueryMethod, OptimizeEnum.NONE),
                    numResultsExpected, OptimizeEnum.NONE);
                uriTimer += timeQuery(getQuery(getQueryMethod, OptimizeEnum.URI),
                    numResultsExpected, OptimizeEnum.URI);
            }
        }

        noneTimer /= iterations;
        uriTimer /= iterations;
        System.out.println(CaseFormat.UPPER_CAMEL
            .to(CaseFormat.LOWER_HYPHEN, getQueryMethod.getName()).replace("-query", "") + "\t"
            + (noneTimer) / 100000.0 + "\t" + (uriTimer) / 100000.0);
        // System.out.println((noneTimer)/100000.0 + " ms, (Not optimized)");
        // System.out.println((uriTimer)/100000.0 + " ms, (URI optimized)");

    }

    private QueryBuilder<Vertex> getQuery(Method getQueryMethod, OptimizeEnum optimization)
        throws InvocationTargetException, IllegalAccessException {
        return (QueryBuilder<Vertex>) getQueryMethod.invoke(this, optimization);
    }

    private long timeQuery(QueryBuilder<Vertex> query, int numResultsExpected,
        OptimizeEnum optimized) {

        // System.out.println(optimized.toString());

        long startTime = System.nanoTime();
        List<Vertex> result = query.toList();
        long endTime = System.nanoTime();

        // if (!optimized) {
        // result.get(0).properties().forEachRemaining(p -> System.out.println(p.key() + " : " +
        // p.value()));
        // }
        // System.out.println("Result size: " + result.size());
        if (numResultsExpected != Integer.MIN_VALUE) {
            assertEquals(optimized.toString() + " optimized" + " query results in "
                + numResultsExpected + " vserver ", numResultsExpected, result.size());
        }

        return endTime - startTime;

    }

    protected abstract int getTenantNum();

    protected abstract int getVserverNumPerTenant();

    protected int numOfTimesToRun() {
        return 500;
    }

}
