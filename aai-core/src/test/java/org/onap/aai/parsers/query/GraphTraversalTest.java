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

package org.onap.aai.parsers.query;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.onap.aai.AAISetup;
import org.onap.aai.DataLinkSetup;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.rest.RestTokens;
import org.onap.aai.serialization.engines.JanusGraphDBEngine;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.springframework.test.annotation.DirtiesContext;

@RunWith(value = Parameterized.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class GraphTraversalTest extends DataLinkSetup {

    private TransactionalGraphEngine dbEngine;
    private TransactionalGraphEngine dbEngineDepthVersion;

    @Parameterized.Parameter(value = 0)
    public QueryStyle queryStyle;

    @Parameterized.Parameters(name = "QueryStyle.{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {{QueryStyle.TRAVERSAL}, {QueryStyle.TRAVERSAL_URI}});
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Configure.
     * 
     * @throws Exception
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Before
    public void configure() throws Exception {
        dbEngine = new JanusGraphDBEngine(queryStyle, loaderFactory
            .createLoaderForVersion(ModelType.MOXY, schemaVersions.getDefaultVersion()), false);

        dbEngineDepthVersion = new JanusGraphDBEngine(queryStyle,
            loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDepthVersion()),
            false);
    }

    /**
     * Parent query.
     *
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    @Test
    public void parentQuery() throws UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder.fromPath("cloud-infrastructure/complexes/complex/key1").build();

        QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);

        GraphTraversal<Vertex, Vertex> expected =
            __.<Vertex>start().has("physical-location-id", "key1").has("aai-node-type", "complex");
        assertEquals("gremlin query should be " + expected.toString(), expected.toString(),
            query.getQueryBuilder().getQuery().toString());
        assertEquals("parent gremlin query should be equal to normal query", expected.toString(),
            query.getQueryBuilder().getParentQuery().getQuery().toString());
        assertEquals("result type should be complex", "complex", query.getResultType());
        assertEquals("result type should be empty", "", query.getParentResultType());
        assertEquals("dependent", false, query.isDependent());

    }

    /**
     * Child query.
     *
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    @Test
    public void childQuery() throws UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder
            .fromPath("cloud-infrastructure/complexes/complex/key1/ctag-pools/ctag-pool/key2/key3")
            .build();
        QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);
        GraphTraversal<Vertex, Vertex> expected =
            __.<Vertex>start().has("physical-location-id", "key1").has("aai-node-type", "complex")
                .in("org.onap.relationships.inventory.BelongsTo").has("aai-node-type", "ctag-pool")
                .has("target-pe", "key2").has("availability-zone-name", "key3");
        GraphTraversal<Vertex, Vertex> expectedParent =
            __.<Vertex>start().has("physical-location-id", "key1").has("aai-node-type", "complex");
        assertEquals("gremlin query should be " + expected.toString(), expected.toString(),
            query.getQueryBuilder().getQuery().toString());
        assertEquals("parent gremlin query should be equal the query for complex",
            expectedParent.toString(),
            query.getQueryBuilder().getParentQuery().getQuery().toString());
        assertEquals("result type should be complex", "complex", query.getParentResultType());
        assertEquals("result type should be ctag-pool", "ctag-pool", query.getResultType());
        assertEquals("dependent", true, query.isDependent());

    }

    /**
     * Naming exceptions.
     *
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    @Test
    public void namingExceptions() throws UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder
            .fromPath("network/vces/vce/key1/port-groups/port-group/key2/cvlan-tags/cvlan-tag/655")
            .build();
        QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);
        GraphTraversal<Vertex, Vertex> expected =
            __.<Vertex>start().has("vnf-id", "key1").has("aai-node-type", "vce")
                .in("org.onap.relationships.inventory.BelongsTo").has("aai-node-type", "port-group")
                .has("interface-id", "key2").in("org.onap.relationships.inventory.BelongsTo")
                .has("aai-node-type", "cvlan-tag").has("cvlan-tag", 655);
        GraphTraversal<Vertex, Vertex> expectedParent = __.<Vertex>start().has("vnf-id", "key1")
            .has("aai-node-type", "vce").in("org.onap.relationships.inventory.BelongsTo")
            .has("aai-node-type", "port-group").has("interface-id", "key2");
        assertEquals("gremlin query should be " + expected.toString(), expected.toString(),
            query.getQueryBuilder().getQuery().toString());
        assertEquals("parent gremlin query should be equal the query for port group",
            expectedParent.toString(),
            query.getQueryBuilder().getParentQuery().getQuery().toString());
        assertEquals("result type should be cvlan-tag", "cvlan-tag", query.getResultType());
        assertEquals("result type should be port-group", "port-group", query.getParentResultType());
        assertEquals("contaner type should be empty", "", query.getContainerType());
        assertEquals("dependent", true, query.isDependent());

    }

    /**
     * Gets the all.
     *
     * @return the all
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    @Test
    public void getAll() throws UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder
            .fromPath("network/vces/vce/key1/port-groups/port-group/key2/cvlan-tags").build();
        QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);
        GraphTraversal<Vertex, Vertex> expected = __.<Vertex>start().has("vnf-id", "key1")
            .has("aai-node-type", "vce").in("org.onap.relationships.inventory.BelongsTo")
            .has("aai-node-type", "port-group").has("interface-id", "key2")
            .in("org.onap.relationships.inventory.BelongsTo").has("aai-node-type", "cvlan-tag");
        GraphTraversal<Vertex, Vertex> expectedParent = __.<Vertex>start().has("vnf-id", "key1")
            .has("aai-node-type", "vce").in("org.onap.relationships.inventory.BelongsTo")
            .has("aai-node-type", "port-group").has("interface-id", "key2");
        assertEquals("gremlin query should be " + expected.toString(), expected.toString(),
            query.getQueryBuilder().getQuery().toString());
        assertEquals("parent gremlin query should be equal the query for port group",
            expectedParent.toString(),
            query.getQueryBuilder().getParentQuery().getQuery().toString());
        assertEquals("result type should be port-group", "port-group", query.getParentResultType());
        assertEquals("result type should be cvlan-tag", "cvlan-tag", query.getResultType());
        assertEquals("container type should be cvlan-tags", "cvlan-tags", query.getContainerType());
        assertEquals("dependent", true, query.isDependent());

    }

    @Test
    public void getAllParent() throws UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder.fromPath("cloud-infrastructure/pservers").build();
        QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);
        GraphTraversal<Vertex, Vertex> expected =
            __.<Vertex>start().has("aai-node-type", "pserver");
        GraphTraversal<Vertex, Vertex> expectedParent =
            __.<Vertex>start().has("aai-node-type", "pserver");
        assertEquals("gremlin query should be " + expected.toString(), expected.toString(),
            query.getQueryBuilder().getQuery().toString());
        assertEquals("parent gremlin query should be equal the query for pserver",
            expectedParent.toString(),
            query.getQueryBuilder().getParentQuery().getQuery().toString());
        assertEquals("parent result type should be empty", "", query.getParentResultType());
        assertEquals("result type should be pserver", "pserver", query.getResultType());
        assertEquals("container type should be pservers", "pservers", query.getContainerType());
        assertEquals("dependent", false, query.isDependent());

    }

    /**
     * Gets the via query param.
     *
     * @return the via query param
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    @Test
    public void getViaQueryParam() throws UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder.fromPath(
            "cloud-infrastructure/cloud-regions/cloud-region/mycloudowner/mycloudregionid/tenants/tenant")
            .build();
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
        map.putSingle("tenant-name", "Tenant1");
        QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri, map);
        GraphTraversal<Vertex, Vertex> expected = __.<Vertex>start()
            .has("cloud-owner", "mycloudowner").has("cloud-region-id", "mycloudregionid")
            .has("aai-node-type", "cloud-region").in("org.onap.relationships.inventory.BelongsTo")
            .has("aai-node-type", "tenant").has("tenant-name", "Tenant1");

        GraphTraversal<Vertex, Vertex> expectedParent =
            __.<Vertex>start().has("cloud-owner", "mycloudowner")
                .has("cloud-region-id", "mycloudregionid").has("aai-node-type", "cloud-region");

        assertEquals("gremlin query should be " + expected.toString(), expected.toString(),
            query.getQueryBuilder().getQuery().toString());
        assertEquals("parent gremlin query should be equal the query for cloud-region",
            expectedParent.toString(),
            query.getQueryBuilder().getParentQuery().getQuery().toString());
        assertEquals("result type should be cloud-region", "cloud-region",
            query.getParentResultType());
        assertEquals("result type should be tenant", "tenant", query.getResultType());
        assertEquals("container type should be empty", "", query.getContainerType());
        assertEquals("dependent", true, query.isDependent());

    }

    @Test
    public void getViaDuplicateQueryParam() throws UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder.fromPath(
            "cloud-infrastructure/cloud-regions/cloud-region/mycloudowner/mycloudregionid/tenants/tenant")
            .build();
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
        List<String> values = new ArrayList<>();
        values.add("Tenant1");
        values.add("Tenant2");
        map.put("tenant-name", values);
        QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri, map);
        GraphTraversal<Vertex, Vertex> expected = __.<Vertex>start()
            .has("cloud-owner", "mycloudowner").has("cloud-region-id", "mycloudregionid")
            .has("aai-node-type", "cloud-region").in("org.onap.relationships.inventory.BelongsTo")
            .has("aai-node-type", "tenant").has("tenant-name", P.within(values));

        GraphTraversal<Vertex, Vertex> expectedParent =
            __.<Vertex>start().has("cloud-owner", "mycloudowner")
                .has("cloud-region-id", "mycloudregionid").has("aai-node-type", "cloud-region");

        assertEquals("gremlin query should be " + expected.toString(), expected.toString(),
            query.getQueryBuilder().getQuery().toString());
        assertEquals("parent gremlin query should be equal the query for cloud-region",
            expectedParent.toString(),
            query.getQueryBuilder().getParentQuery().getQuery().toString());
        assertEquals("result type should be cloud-region", "cloud-region",
            query.getParentResultType());
        assertEquals("result type should be tenant", "tenant", query.getResultType());
        assertEquals("container type should be empty", "", query.getContainerType());
        assertEquals("dependent", true, query.isDependent());

    }

    /**
     * Gets the plural via query param.
     *
     * @return the plural via query param
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    @Test
    public void getPluralViaQueryParam() throws UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder.fromPath("network/vnfcs").build();
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
        map.putSingle("prov-status", "up");
        QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri, map);
        GraphTraversal<Vertex, Vertex> expected =
            __.<Vertex>start().has("aai-node-type", "vnfc").has("prov-status", "up");

        GraphTraversal<Vertex, Vertex> expectedParent =
            __.<Vertex>start().has("aai-node-type", "vnfc");

        assertEquals("gremlin query should be " + expected.toString(), expected.toString(),
            query.getQueryBuilder().getQuery().toString());
        assertEquals("parent", expectedParent.toString(),
            query.getQueryBuilder().getParentQuery().getQuery().toString());
        assertEquals("parent result type should be empty", "", query.getParentResultType());
        assertEquals("result type should be vnfc", "vnfc", query.getResultType());
        assertEquals("container type should be empty", "vnfcs", query.getContainerType());
        assertEquals("dependent", true, query.isDependent());

    }

    /**
     * Gets the all query param naming exception.
     *
     * @return the all query param naming exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    @Test
    public void getAllQueryParamNamingException()
        throws UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder
            .fromPath("network/vces/vce/key1/port-groups/port-group/key2/cvlan-tags").build();
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
        map.putSingle("cvlan-tag", "333");
        QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri, map);

        GraphTraversal<Vertex, Vertex> expected =
            __.<Vertex>start().has("vnf-id", "key1").has("aai-node-type", "vce")
                .in("org.onap.relationships.inventory.BelongsTo").has("aai-node-type", "port-group")
                .has("interface-id", "key2").in("org.onap.relationships.inventory.BelongsTo")
                .has("aai-node-type", "cvlan-tag").has("cvlan-tag", 333);
        GraphTraversal<Vertex, Vertex> expectedParent = __.<Vertex>start().has("vnf-id", "key1")
            .has("aai-node-type", "vce").in("org.onap.relationships.inventory.BelongsTo")
            .has("aai-node-type", "port-group").has("interface-id", "key2");
        assertEquals("gremlin query should be " + expected.toString(), expected.toString(),
            query.getQueryBuilder().getQuery().toString());
        assertEquals("parent gremlin query should be equal the query for port group",
            expectedParent.toString(),
            query.getQueryBuilder().getParentQuery().getQuery().toString());
        assertEquals("result type should be port-group", "port-group", query.getParentResultType());
        assertEquals("result type should be cvlan-tag", "cvlan-tag", query.getResultType());
        assertEquals("container type should be cvlan-tags", "cvlan-tags", query.getContainerType());
        assertEquals("dependent", true, query.isDependent());

    }

    /**
     * Abstract type.
     *
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    @Test
    public void abstractType() throws UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder.fromPath("vnf/key1").build();

        QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);

        GraphTraversal<Vertex, Vertex> expected = __.<Vertex>start().has("vnf-id", "key1")
            .has(AAIProperties.NODE_TYPE, P.within("vce", "generic-vnf"));

        GraphTraversal<Vertex, Vertex> expectedParent = expected;
        assertEquals("gremlin query should be " + expected.toString(), expected.toString(),
            query.getQueryBuilder().getQuery().toString());
        assertEquals("parent gremlin query should be equal the query for port group",
            expectedParent.toString(),
            query.getQueryBuilder().getParentQuery().getQuery().toString());
        assertEquals("result type should be empty", "", query.getParentResultType());
        assertEquals("result type should be vnf", "vnf", query.getResultType());

        assertEquals("dependent", false, query.isDependent());

    }

    /**
     * Non parent abstract type.
     *
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    @Test
    public void nonParentAbstractType() throws UnsupportedEncodingException, AAIException {
        URI uri =
            UriBuilder.fromPath("cloud-infrastructure/pservers/pserver/key2/vnf/key1").build();
        thrown.expect(AAIException.class);
        thrown.expectMessage(containsString("not a valid path"));
        dbEngine.getQueryBuilder().createQueryFromURI(uri);
    }

    @Test
    public void parentAbstractTypeWithNesting() throws UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder.fromPath("vnf/key1/vf-modules/vf-module/key2").build();

        QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);

        GraphTraversal<Vertex, Vertex> expected = __.<Vertex>start().has("vnf-id", "key1")
            .has(AAIProperties.NODE_TYPE, P.within("vce", "generic-vnf"))
            .union(__.in("org.onap.relationships.inventory.BelongsTo").has(AAIProperties.NODE_TYPE,
                "vf-module"))
            .has("vf-module-id", "key2");

        GraphTraversal<Vertex, Vertex> expectedParent = __.<Vertex>start().has("vnf-id", "key1")
            .has(AAIProperties.NODE_TYPE, P.within("vce", "generic-vnf"));
        assertEquals("gremlin query should be " + expected.toString(), expected.toString(),
            query.getQueryBuilder().getQuery().toString());
        assertEquals("parent gremlin query should be equal the query for ",
            expectedParent.toString(),
            query.getQueryBuilder().getParentQuery().getQuery().toString());
        assertEquals("result type should be vnf", "vnf", query.getParentResultType());
        assertEquals("result type should be vf-module", "vf-module", query.getResultType());

        assertEquals("dependent", true, query.isDependent());

    }

    @Test
    public void getViaBadQueryParam() throws UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder
            .fromPath("cloud-infrastructure/cloud-regions/cloud-region/a/b/tenants/tenant").build();
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
        map.putSingle("tenant-n231ame", "Tenant1");
        thrown.expect(AAIException.class);
        thrown.expect(hasProperty("code", is("AAI_3000")));

        QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri, map);

    }

    @Test
    public void getPluralViaBadQueryParam() throws UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder.fromPath("cloud-infrastructure/cloud-regions/cloud-region/a/b/tenants")
            .build();
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
        map.putSingle("tenant-n231ame", "Tenant1");
        thrown.expect(AAIException.class);
        thrown.expect(hasProperty("code", is("AAI_3000")));

        QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri, map);

    }

    @Test
    public void getPluralViaDuplicateQueryParam()
        throws UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder.fromPath("network/vnfcs").build();
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
        List<String> values = new ArrayList<>();
        values.add("up");
        values.add("down");
        values.add("left");
        values.add("right");
        values.add("start");
        map.put("prov-status", values);
        QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri, map);
        GraphTraversal<Vertex, Vertex> expected =
            __.<Vertex>start().has("aai-node-type", "vnfc").has("prov-status", P.within(values));

        GraphTraversal<Vertex, Vertex> expectedParent =
            __.<Vertex>start().has("aai-node-type", "vnfc");

        assertEquals("gremlin query should be " + expected.toString(), expected.toString(),
            query.getQueryBuilder().getQuery().toString());
        assertEquals("parent", expectedParent.toString(),
            query.getQueryBuilder().getParentQuery().getQuery().toString());
        assertEquals("parent result type should be empty", "", query.getParentResultType());
        assertEquals("result type should be vnfc", "vnfc", query.getResultType());
        assertEquals("container type should be empty", "vnfcs", query.getContainerType());
        assertEquals("dependent", true, query.isDependent());

    }

    @Test
    public void dbAliasedSearch() throws UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder.fromPath("network/generic-vnfs").build();
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
        map.putSingle("persona-model-customization-id", "key2");
        QueryParser query = dbEngineDepthVersion.getQueryBuilder().createQueryFromURI(uri, map);
        GraphTraversal<Vertex, Vertex> expected = __.<Vertex>start()
            .has("aai-node-type", "generic-vnf").has("model-customization-id", "key2");
        GraphTraversal<Vertex, Vertex> expectedParent =
            __.<Vertex>start().has("aai-node-type", "generic-vnf");

        assertEquals("gremlin query should be " + expected.toString(), expected.toString(),
            query.getQueryBuilder().getQuery().toString());
        assertEquals("parent", expectedParent.toString(),
            query.getQueryBuilder().getParentQuery().getQuery().toString());

        assertEquals("result type should be", "generic-vnf", query.getResultType());
        assertEquals("result type should be empty", "", query.getParentResultType());
        assertEquals("dependent", true, query.isDependent());

    }

    @Test
    public void dataLinkedSearch() throws UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder.fromPath("network/vpn-bindings").build();
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
        map.putSingle("global-route-target", "key2");
        QueryParser query = dbEngineDepthVersion.getQueryBuilder().createQueryFromURI(uri, map);
        GraphTraversal<Vertex, Vertex> expected =
            __.<Vertex>start().has("aai-node-type", "vpn-binding")
                .where(__.in("org.onap.relationships.inventory.BelongsTo")
                    .has(AAIProperties.NODE_TYPE, "route-target")
                    .has("global-route-target", "key2"));
        GraphTraversal<Vertex, Vertex> expectedParent =
            __.<Vertex>start().has("aai-node-type", "vpn-binding");

        assertEquals("gremlin query should be " + expected.toString(), expected.toString(),
            query.getQueryBuilder().getQuery().toString());
        assertEquals("parent", expectedParent.toString(),
            query.getQueryBuilder().getParentQuery().getQuery().toString());

        assertEquals("result type should be", "vpn-binding", query.getResultType());
        assertEquals("result type should be empty", "", query.getParentResultType());
        assertEquals("dependent", true, query.isDependent());
    }

    @Test
    public void pluralCousin() throws UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder
            .fromPath("cloud-infrastructure/complexes/complex/key1/related-to/pservers").build();

        QueryParser query = dbEngineDepthVersion.getQueryBuilder().createQueryFromURI(uri);
        GraphTraversal<Vertex, Vertex> expected =
            __.<Vertex>start().has("physical-location-id", "key1").has("aai-node-type", "complex")
                .in("org.onap.relationships.inventory.LocatedIn").has("aai-node-type", "pserver");
        GraphTraversal<Vertex, Vertex> expectedParent =
            __.<Vertex>start().has("physical-location-id", "key1").has("aai-node-type", "complex");

        assertEquals("gremlin query should be " + expected.toString(), expected.toString(),
            query.getQueryBuilder().getQuery().toString());
        assertEquals("parent", expectedParent.toString(),
            query.getQueryBuilder().getParentQuery().getQuery().toString());

        assertEquals("result type should be", "pserver", query.getResultType());
        assertEquals("result type should be", "complex", query.getParentResultType());
        // this is controversial but we're not allowing writes on this currently
        assertEquals("dependent", true, query.isDependent());
    }

    @Test
    public void specificCousin() throws UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder
            .fromPath(
                "cloud-infrastructure/complexes/complex/key1/related-to/pservers/pserver/key2")
            .build();

        QueryParser query = dbEngineDepthVersion.getQueryBuilder().createQueryFromURI(uri);
        GraphTraversal<Vertex, Vertex> expected =
            __.<Vertex>start().has("physical-location-id", "key1").has("aai-node-type", "complex")
                .in("org.onap.relationships.inventory.LocatedIn").has("aai-node-type", "pserver")
                .has("hostname", "key2");
        GraphTraversal<Vertex, Vertex> expectedParent =
            __.<Vertex>start().has("physical-location-id", "key1").has("aai-node-type", "complex");

        assertEquals("gremlin query should be " + expected.toString(), expected.toString(),
            query.getQueryBuilder().getQuery().toString());
        assertEquals("parent", expectedParent.toString(),
            query.getQueryBuilder().getParentQuery().getQuery().toString());

        assertEquals("result type should be", "pserver", query.getResultType());
        assertEquals("result type should be", "complex", query.getParentResultType());
        // this is controversial but we're not allowing writes on this currently
        assertEquals("dependent", true, query.isDependent());
    }

    @Test
    public void doubleSpecificCousin() throws UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder.fromPath(
            "cloud-infrastructure/complexes/complex/key1/related-to/pservers/pserver/key2/related-to/vservers/vserver/key3")
            .build();

        QueryParser query = dbEngineDepthVersion.getQueryBuilder().createQueryFromURI(uri);
        GraphTraversal<Vertex, Vertex> expected =
            __.<Vertex>start().has("physical-location-id", "key1").has("aai-node-type", "complex")
                .in("org.onap.relationships.inventory.LocatedIn").has("aai-node-type", "pserver")
                .has("hostname", "key2").in("tosca.relationships.HostedOn")
                .has("aai-node-type", "vserver").has("vserver-id", "key3");
        GraphTraversal<Vertex, Vertex> expectedParent =
            __.<Vertex>start().has("physical-location-id", "key1").has("aai-node-type", "complex")
                .in("org.onap.relationships.inventory.LocatedIn").has("aai-node-type", "pserver")
                .has("hostname", "key2");

        assertEquals("gremlin query should be " + expected.toString(), expected.toString(),
            query.getQueryBuilder().getQuery().toString());
        assertEquals("parent", expectedParent.toString(),
            query.getQueryBuilder().getParentQuery().getQuery().toString());

        assertEquals("result type should be", "vserver", query.getResultType());
        assertEquals("result type should be", "pserver", query.getParentResultType());
        // this is controversial but we're not allowing writes on this currently
        assertEquals("dependent", true, query.isDependent());
    }

    @Test
    public void traversalEndsInRelatedTo() throws UnsupportedEncodingException, AAIException {
        URI uri =
            UriBuilder.fromPath("cloud-infrastructure/complexes/complex/key1/related-to").build();

        thrown.expect(AAIException.class);
        thrown.expectMessage(containsString(RestTokens.COUSIN.toString()));
        QueryParser query = dbEngineDepthVersion.getQueryBuilder().createQueryFromURI(uri);

    }

    @Test
    public void pluralCousinToPluralCousin() throws UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder.fromPath("cloud-infrastructure/complexes/related-to/pservers").build();

        thrown.expect(AAIException.class);
        thrown.expectMessage(containsString("chain plurals"));
        QueryParser query = dbEngineDepthVersion.getQueryBuilder().createQueryFromURI(uri);

    }
}
