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
package org.onap.aai.query.builder.optimization;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Before;
import org.junit.Test;
import org.onap.aai.query.builder.QueryBuilder;
import org.onap.aai.query.builder.TraversalQuery;
import org.onap.aai.query.builder.TraversalURIOptimizedQuery;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.reflect.Method;
import java.net.URI;

public abstract class AbstractGraphTraversalBuilderTestQueryiesToRun extends AbstractGraphTraversalBuilderOptmizationTest {

    protected static final String CLOUD_OWNER_1 = "cloud-owner-1";
    protected static final String CLOUD_REGION_ID_1 = "cloud-region-id-1";
    protected String tenantId;
    protected String vserverId;
    protected String vserverName;
    protected String randomFromList;

    @Before
    public void config() throws Exception {
        int tenantNum = rand.nextInt(getTenantNum());
        tenantId = getPrefix() + "tenant-id-" + tenantNum;
        vserverId = getPrefix() + "vserver-id-" + tenantNum + "-" + rand.nextInt(getVserverNumPerTenant());
        vserverName = getPrefix() + "vserver-name-" + tenantNum + "-" + rand.nextInt(getVserverNumPerTenant());

        randomFromList = RANDOM_VALUES.get(rand.nextInt(RANDOM_VALUES.size()));
    }

    protected abstract String getPrefix();


    private QueryBuilder<Vertex> getQueryBuilder(OptimizeEnum optimized) {
        if (OptimizeEnum.URI.equals(optimized)) {
            return new TraversalURIOptimizedQuery<>(loader, g);
        } else {
            return new TraversalQuery<>(loader, g);
        }
    }

    private void callTest(String methodName, int numResultsExpected) throws Exception {
        String queryMethodName = methodName.replace("Test", "Query");
        Method method = AbstractGraphTraversalBuilderTestQueryiesToRun.class.getMethod(queryMethodName, OptimizeEnum.class);
        this.execute(method, numResultsExpected);
    }

    @Test
    public void vserverTest() throws Exception {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        callTest(methodName, 1);
    }

    public QueryBuilder<Vertex> vserverQuery(OptimizeEnum optimized) throws Exception {
        URI uri = new URI(String.format(vserverUriPattern, getPrefix() + CLOUD_OWNER_1, getPrefix() + CLOUD_REGION_ID_1, tenantId, vserverId));
        return getQueryBuilder(optimized).createQueryFromURI(uri).getQueryBuilder();
    }

    @Test
    public void vserversUnderATenantTest() throws Exception {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        callTest(methodName, getVserverNumPerTenant());
    }

    public QueryBuilder<Vertex> vserversUnderATenantQuery(OptimizeEnum optimized) throws Exception {
        URI uri = new URI(String.format(tenantUriPattern, getPrefix() + CLOUD_OWNER_1, getPrefix() + CLOUD_REGION_ID_1, tenantId) + "/vservers");
        return getQueryBuilder(optimized).createQueryFromURI(uri).getQueryBuilder();
    }

    @Test
    public void vserversUnderATenantWithNonIndexPropertyTest() throws Exception {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        callTest(methodName, Integer.MIN_VALUE);
    }

    public QueryBuilder<Vertex> vserversUnderATenantWithNonIndexPropertyQuery(OptimizeEnum optimized) throws Exception {
        URI uri = new URI(String.format(tenantUriPattern, getPrefix() + CLOUD_OWNER_1, getPrefix() + CLOUD_REGION_ID_1, tenantId) + "/vservers");
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
        map.putSingle(VSERVER_SELFLINK, randomFromList);
        return getQueryBuilder(optimized).createQueryFromURI(uri, map).getQueryBuilder();
    }

    @Test
    public void vserversUnderATenantWithIndexPropertyWhereValueIsInMultipleTest() throws Exception {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        callTest(methodName, Integer.MIN_VALUE);
    }

    public QueryBuilder<Vertex> vserversUnderATenantWithIndexPropertyWhereValueIsInMultipleQuery(OptimizeEnum optimized) throws Exception {
        URI uri = new URI(String.format(tenantUriPattern, getPrefix() + CLOUD_OWNER_1, getPrefix() + CLOUD_REGION_ID_1, tenantId) + "/vservers");
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
        map.putSingle(PROV_STATUS, randomFromList);
        return getQueryBuilder(optimized).createQueryFromURI(uri, map).getQueryBuilder();
    }

    @Test
    public void vserversUnderATenantWithIndexPropertyWhereValueIsSemiUniqueTest() throws Exception {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        callTest(methodName, Integer.MIN_VALUE);
    }

    public QueryBuilder<Vertex> vserversUnderATenantWithIndexPropertyWhereValueIsSemiUniqueQuery(OptimizeEnum optimized) throws Exception {
        URI uri = new URI(String.format(tenantUriPattern, getPrefix() + CLOUD_OWNER_1, getPrefix() + CLOUD_REGION_ID_1, tenantId) + "/vservers");
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
        map.putSingle(VSERVER_NAME, vserverName);
        return getQueryBuilder(optimized).createQueryFromURI(uri, map).getQueryBuilder();

    }

    @Test
    public void nonExistentVserverTest() throws Exception {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        callTest(methodName, 0);
    }

    public QueryBuilder<Vertex> nonExistentVserverQuery(OptimizeEnum optimized) throws Exception {
        URI uri = new URI(String.format(vserverUriPattern, getPrefix() + CLOUD_OWNER_1, getPrefix() + CLOUD_REGION_ID_1, tenantId, "does-not-exist"));
        return getQueryBuilder(optimized).createQueryFromURI(uri).getQueryBuilder();
    }

    @Test
    public void vserverWithNonExistentTenantTest() throws Exception {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        callTest(methodName, 0);
    }

    public QueryBuilder<Vertex> vserverWithNonExistentTenantQuery(OptimizeEnum optimized) throws Exception {
        URI uri = new URI(String.format(vserverUriPattern, getPrefix() + CLOUD_OWNER_1, getPrefix() + CLOUD_REGION_ID_1, "does-not-exist", vserverId));
        return getQueryBuilder(optimized).createQueryFromURI(uri).getQueryBuilder();
    }

    @Test
    public void vserverWithValidQueryParameterTest() throws Exception {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        callTest(methodName, 1);
    }

    public QueryBuilder<Vertex> vserverWithValidQueryParameterQuery(OptimizeEnum optimized) throws Exception {
        URI uri = new URI(String.format(vserverUriPattern, getPrefix() + CLOUD_OWNER_1, getPrefix() + CLOUD_REGION_ID_1, tenantId, vserverId));
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
        map.putSingle("vserver-name2", "someName");
        return getQueryBuilder(optimized).createQueryFromURI(uri, map).getQueryBuilder();
    }

    @Test
    public void cloudRegionTest() throws Exception {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        callTest(methodName, 1);
    }

    public QueryBuilder<Vertex> cloudRegionQuery(OptimizeEnum optimized) throws Exception {
        URI uri = new URI(String.format(crUriPattern, getPrefix() + CLOUD_OWNER_1, getPrefix() + CLOUD_REGION_ID_1));
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
        map.putSingle("in-maint", "false");
        return getQueryBuilder(optimized).createQueryFromURI(uri, map).getQueryBuilder();
    }


}
