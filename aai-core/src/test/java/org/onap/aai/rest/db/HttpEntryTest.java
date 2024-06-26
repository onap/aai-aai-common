/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright © 2023 Deutsche Telekom.
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

package org.onap.aai.rest.db;

import static org.onap.aai.edges.enums.AAIDirection.NONE;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.javatuples.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.onap.aai.AAISetup;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.edges.enums.EdgeField;
import org.onap.aai.edges.enums.EdgeProperty;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.prevalidation.ValidationService;
import org.onap.aai.query.builder.Pageable;
import org.onap.aai.query.builder.QueryOptions;
import org.onap.aai.query.builder.Sort;
import org.onap.aai.query.builder.Sort.Direction;
import org.onap.aai.rest.db.responses.ErrorResponse;
import org.onap.aai.rest.db.responses.Relationship;
import org.onap.aai.rest.db.responses.RelationshipWrapper;
import org.onap.aai.rest.db.responses.ServiceException;
import org.onap.aai.rest.ueb.UEBNotification;
import org.onap.aai.restcore.HttpMethod;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.util.AAIConfig;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.JSONComparator;
import org.springframework.boot.test.mock.mockito.MockBean;

@RunWith(value = Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HttpEntryTest extends AAISetup {

    @MockBean ValidationService validationService;

    protected static final MediaType APPLICATION_JSON = MediaType.valueOf("application/json");

    private static final Set<Integer> VALID_HTTP_STATUS_CODES = new HashSet<>();

    static {
        VALID_HTTP_STATUS_CODES.add(200);
        VALID_HTTP_STATUS_CODES.add(201);
        VALID_HTTP_STATUS_CODES.add(204);
    }

    @Parameterized.Parameter(value = 0)
    public QueryStyle queryStyle;

    /*
     * TODO Change the HttpEntry instances accoringly
     */
    @Parameterized.Parameters(name = "QueryStyle.{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { QueryStyle.TRAVERSAL }, { QueryStyle.TRAVERSAL_URI } });
    }

    private Loader loader;
    private TransactionalGraphEngine dbEngine;
    private GraphTraversalSource traversal;

    private HttpHeaders httpHeaders;

    private UriInfo uriInfo;

    private MultivaluedMap<String, String> headersMultiMap;
    private MultivaluedMap<String, String> queryParameters;

    private List<String> aaiRequestContextList;

    private List<MediaType> outputMediaTypes;

    ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() {

        httpHeaders = Mockito.mock(HttpHeaders.class);
        uriInfo = Mockito.mock(UriInfo.class);

        headersMultiMap = new MultivaluedHashMap<>();
        queryParameters = Mockito.spy(new MultivaluedHashMap<>());

        headersMultiMap.add("X-FromAppId", "JUNIT");
        headersMultiMap.add("X-TransactionId", UUID.randomUUID().toString());
        headersMultiMap.add("Real-Time", "true");
        headersMultiMap.add("Accept", "application/json");
        headersMultiMap.add("aai-request-context", "");

        outputMediaTypes = new ArrayList<>();
        outputMediaTypes.add(APPLICATION_JSON);

        aaiRequestContextList = new ArrayList<>();
        aaiRequestContextList.add("");

        traversalHttpEntry.setHttpEntryProperties(schemaVersions.getDefaultVersion());
        loader = traversalHttpEntry.getLoader();
        dbEngine = traversalHttpEntry.getDbEngine();
        traversal = dbEngine.tx().traversal();

        when(httpHeaders.getAcceptableMediaTypes()).thenReturn(outputMediaTypes);
        when(httpHeaders.getRequestHeaders()).thenReturn(headersMultiMap);

        when(httpHeaders.getRequestHeader("aai-request-context")).thenReturn(aaiRequestContextList);

        when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
        when(uriInfo.getQueryParameters(false)).thenReturn(queryParameters);

        // TODO - Check if this is valid since RemoveDME2QueryParameters seems to be
        // very unreasonable
        Mockito.doReturn(null).when(queryParameters).remove(any());

        when(httpHeaders.getMediaType()).thenReturn(APPLICATION_JSON);
    }

    @After
    public void rollback() {
        dbEngine.rollback();
    }

    @Test
    public void thatObjectCanBeRetrieved() throws UnsupportedEncodingException, AAIException {
        String uri = "/cloud-infrastructure/pservers/pserver/theHostname";
        traversal.addV()
                .property("aai-node-type", "pserver")
                .property("hostname", "theHostname")
                .property("equip-type", "theEquipType")
                .property(AAIProperties.AAI_URI, uri)
                .next();

        JSONObject expectedResponseBody = new JSONObject()
                .put("hostname", "theHostname")
                .put("equip-type", "theEquipType");
        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET, uri);
        JSONObject actualResponseBody = new JSONObject(response.getEntity().toString());

        JSONAssert.assertEquals(expectedResponseBody, actualResponseBody, JSONCompareMode.NON_EXTENSIBLE);
        assertEquals("Expected the pserver to be returned", 200, response.getStatus());
        verify(validationService, times(1)).validate(any());
    }

    @Test
    public void thatObjectsCanBeRetrieved() throws UnsupportedEncodingException, AAIException {
        String uri = "/cloud-infrastructure/pservers/pserver/theHostname";
        traversal.addV()
                .property("aai-node-type", "pserver")
                .property("hostname", "theHostname")
                .property("equip-type", "theEquipType")
                .property(AAIProperties.AAI_URI, uri)
                .next();
        traversal.addV()
                .property("aai-node-type", "pserver")
                .property("hostname", "theHostname2")
                .property("equip-type", "theEquipType2")
                .property(AAIProperties.AAI_URI, uri + "2")
                .next();

        JSONObject expectedResponseBody = new JSONObject();
        JSONObject pserver1 = new JSONObject()
                .put("hostname", "theHostname")
                .put("equip-type", "theEquipType");
        JSONObject pserver2 = new JSONObject()
                .put("hostname", "theHostname2")
                .put("equip-type", "theEquipType2");
        expectedResponseBody.put("pserver", new JSONArray()
                .put(pserver1)
                .put(pserver2));

        uri = "/cloud-infrastructure/pservers";
        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET, uri);
        JSONObject actualResponseBody = new JSONObject(response.getEntity().toString());

        JSONAssert.assertEquals(expectedResponseBody, actualResponseBody, JSONCompareMode.NON_EXTENSIBLE);
        assertEquals("Expected the pservers to be returned", 200, response.getStatus());
        verify(validationService, times(1)).validate(any());
    }

    @Test
    public void thatPaginatedObjectsCanBeRetrieved() throws UnsupportedEncodingException, AAIException {
        String uri = "/cloud-infrastructure/pservers/pserver/theHostname";
        traversal.addV()
                .property("aai-node-type", "pserver")
                .property("hostname", "theHostname")
                .property("equip-type", "theEquipType")
                .property(AAIProperties.AAI_URI, uri)
                .next();
        traversal.addV()
                .property("aai-node-type", "pserver")
                .property("hostname", "theHostname2")
                .property("equip-type", "theEquipType2")
                .property(AAIProperties.AAI_URI, uri + "2")
                .next();

        JSONObject expectedResponseBody = new JSONObject();
        JSONObject pserver1 = new JSONObject()
                .put("hostname", "theHostname")
                .put("equip-type", "theEquipType");
        expectedResponseBody.put("pserver", new JSONArray()
                .put(pserver1));

        uri = "/cloud-infrastructure/pservers";
        QueryOptions queryOptions = QueryOptions.builder().pageable(new Pageable(1, 1)).build();
        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET, uri, queryOptions);
        JSONObject actualResponseBody = new JSONObject(response.getEntity().toString());

        assertNull(response.getHeaderString("total-results"));
        assertEquals(1, actualResponseBody.getJSONArray("pserver").length());
        assertEquals("Expected the pservers to be returned", 200, response.getStatus());
        verify(validationService, times(1)).validate(any());

        queryOptions = QueryOptions.builder().pageable(new Pageable(0,5).includeTotalCount()).build();
        response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET, uri, queryOptions);
        actualResponseBody = new JSONObject(response.getEntity().toString());
        assertEquals(2, actualResponseBody.getJSONArray("pserver").length());
    }

    @Test
    public void thatPagationResultWithTotalCountCanBeRetrieved() throws UnsupportedEncodingException, AAIException {
        String uri = "/cloud-infrastructure/pservers/pserver/theHostname";
        traversal.addV()
                .property("aai-node-type", "pserver")
                .property("hostname", "theHostname")
                .property("equip-type", "theEquipType")
                .property(AAIProperties.AAI_URI, uri)
                .next();
        traversal.addV()
                .property("aai-node-type", "pserver")
                .property("hostname", "theHostname2")
                .property("equip-type", "theEquipType2")
                .property(AAIProperties.AAI_URI, uri + "2")
                .next();

        JSONObject expectedResponseBody = new JSONObject();
        JSONObject pserver1 = new JSONObject()
                .put("hostname", "theHostname")
                .put("equip-type", "theEquipType");
        expectedResponseBody.put("pserver", new JSONArray()
                .put(pserver1));

        uri = "/cloud-infrastructure/pservers";
        QueryOptions queryOptions = QueryOptions.builder().pageable(new Pageable(1, 1).includeTotalCount()).build();
        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET, uri, queryOptions);
        JSONObject actualResponseBody = new JSONObject(response.getEntity().toString());
        String totalCount = response.getHeaderString("total-results");
        String totalPages = response.getHeaderString("total-pages");

        assertEquals(2, Integer.parseInt(totalCount));
        assertEquals(2, Integer.parseInt(totalPages));
        assertEquals(1, actualResponseBody.getJSONArray("pserver").length());
        assertEquals("Expected the pservers to be returned", 200, response.getStatus());
        verify(validationService, times(1)).validate(any());

        queryOptions = QueryOptions.builder().pageable(new Pageable(0, 2)).build();
        response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET, uri, queryOptions);
        actualResponseBody = new JSONObject(response.getEntity().toString());
        assertEquals(2, actualResponseBody.getJSONArray("pserver").length());
    }

    @Test
    public void thatSortedObjectsCanBeRetrieved() throws UnsupportedEncodingException, AAIException {
        String uri = "/cloud-infrastructure/pservers/pserver/theHostname";
        traversal.addV()
                .property("aai-node-type", "pserver")
                .property("hostname", "theHostname")
                .property("equip-type", "theEquipType")
                .property(AAIProperties.AAI_URI, uri)
                .next();
        traversal.addV()
                .property("aai-node-type", "pserver")
                .property("hostname", "theHostname2")
                .property("equip-type", "theEquipType2")
                .property(AAIProperties.AAI_URI, uri + "2")
                .next();

        JSONObject expectedResponseBody = new JSONObject();
        JSONObject pserver1 = new JSONObject()
                .put("hostname", "theHostname")
                .put("equip-type", "theEquipType");
        JSONObject pserver2 = new JSONObject()
                .put("hostname", "theHostname2")
                .put("equip-type", "theEquipType2");
        expectedResponseBody.put("pserver", new JSONArray()
                .put(pserver1).put(pserver2));

        // ascending
        uri = "/cloud-infrastructure/pservers";
        QueryOptions queryOptions = QueryOptions.builder().sort(Sort.builder().property("equip-type").direction(Direction.ASC).build()).build();
        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET, uri, queryOptions);
        JSONObject actualResponseBody = new JSONObject(response.getEntity().toString());
        assertEquals("theEquipType", actualResponseBody.getJSONArray("pserver").getJSONObject(0).getString("equip-type"));
        assertEquals("theEquipType2", actualResponseBody.getJSONArray("pserver").getJSONObject(1).getString("equip-type"));

        // descending
        queryOptions = QueryOptions.builder().sort(Sort.builder().property("equip-type").direction(Direction.DESC).build()).build();
        response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET, uri, queryOptions);
        actualResponseBody = new JSONObject(response.getEntity().toString());
        assertEquals("theEquipType2", actualResponseBody.getJSONArray("pserver").getJSONObject(0).getString("equip-type"));
        assertEquals("theEquipType", actualResponseBody.getJSONArray("pserver").getJSONObject(1).getString("equip-type"));

    }

    @Test
    public void thatObjectsCanNotBeFound() throws UnsupportedEncodingException, AAIException {
        String uri = "/cloud-infrastructure/pservers/pserver/junit-test2";

        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET, uri);
        assertEquals("The pserver is not found", 404, response.getStatus());
    }

    @Test
    public void thatObjectCanBeCreatedViaPUT() throws UnsupportedEncodingException, AAIException {
        String uri = "/cloud-infrastructure/pservers/pserver/theHostname";
        String requestBody = new JSONObject().put("hostname", "theHostname").toString();

        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, uri, requestBody);
        assertEquals("Expecting the pserver to be created", 201, response.getStatus());
        verify(validationService, times(1)).validate(any());
    }

    @Test
    public void thatObjectCreationFailsWhenResourceVersionIsProvided()
            throws UnsupportedEncodingException, AAIException, JsonMappingException, JsonProcessingException {
        String uri = "/cloud-infrastructure/pservers/pserver/theHostname";
        String requestBody = new JSONObject()
                .put("hostname", "theHostname")
                .put("resource-version", "123")
                .toString();

        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, uri, requestBody);
        ErrorResponse errorResponseEntity = mapper.readValue(response.getEntity().toString(), ErrorResponse.class);
        assertEquals("Expecting the pserver to be created", 412, response.getStatus());
        assertEquals(
                "Resource version specified on create:resource-version passed for create of /cloud-infrastructure/pservers/pserver/theHostname",
                errorResponseEntity.getRequestError().getServiceException().getVariables().get(2));
    }

    @Test
    public void thatObjectCanBeUpdatedViaPUT() throws UnsupportedEncodingException, AAIException {
        String uri = "/cloud-infrastructure/pservers/pserver/theHostname";
        traversal.addV()
                .property("aai-node-type", "pserver")
                .property("hostname", "theHostname")
                .property("number-of-cpus", "10")
                .property(AAIProperties.AAI_URI, uri)
                .property(AAIProperties.RESOURCE_VERSION, "123")
                .next();
        String requestBody = new JSONObject()
                .put("hostname", "updatedHostname")
                .put("resource-version", "123")
                .toString();

        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, uri, requestBody);
        assertEquals("Expecting the pserver to be updated", 200, response.getStatus());
        assertTrue("That old properties are removed",
                traversal.V().has("hostname", "updatedHostname").hasNot("number-of-cpus").hasNext());
        verify(validationService, times(1)).validate(any());
    }

    @Test
    public void thatUpdateFailsWhenResourceVersionsMismatch()
            throws UnsupportedEncodingException, AAIException, JsonMappingException, JsonProcessingException {
        String uri = "/cloud-infrastructure/pservers/pserver/theHostname";
        traversal.addV()
                .property("aai-node-type", "pserver")
                .property("hostname", "theHostname")
                .property(AAIProperties.AAI_URI, uri)
                .property(AAIProperties.RESOURCE_VERSION, "123")
                .next();
        String requestBody = new JSONObject()
                .put("hostname", "updatedHostname")
                .put("resource-version", "456")
                .toString();

        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, uri, requestBody);
        ErrorResponse errorResponseEntity = mapper.readValue(response.getEntity().toString(), ErrorResponse.class);
        assertEquals("Expecting the update to fail", 412, response.getStatus());
        assertEquals(
                "Precondition Failed:resource-version MISMATCH for update of /cloud-infrastructure/pservers/pserver/updatedHostname",
                errorResponseEntity.getRequestError().getServiceException().getVariables().get(2));
    }

    @Test
    public void thatUpdateFailsWhenResourceVersionIsNotProvided()
            throws UnsupportedEncodingException, AAIException, JsonMappingException, JsonProcessingException {
        String uri = "/cloud-infrastructure/pservers/pserver/theHostname";
        traversal.addV()
                .property("aai-node-type", "pserver")
                .property("hostname", "theHostname")
                .property("in-maint", "false")
                .property(AAIProperties.AAI_URI, uri)
                .next();

        String requestBody = new JSONObject()
                .put("hostname", "theHostname")
                .put("is-maint", "true")
                .toString();

        doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, uri, requestBody);
        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, uri, requestBody);
        ErrorResponse errorResponseEntity = mapper.readValue(response.getEntity().toString(), ErrorResponse.class);
        assertEquals("Request should fail when no resource-version is provided", 412, response.getStatus());
        assertEquals(
                "Precondition Required:resource-version not passed for update of /cloud-infrastructure/pservers/pserver/theHostname",
                errorResponseEntity.getRequestError().getServiceException().getVariables().get(2));
    }

    @Test
    public void thatCreateViaPUTAddsRelationshipsToExistingObjects() throws UnsupportedEncodingException, AAIException {
        traversal.addV()
                .property("aai-node-type", "pserver")
                .property("hostname", "hostname")
                .property(AAIProperties.AAI_URI, "/cloud-infrastructure/pservers/pserver/hostname")
                .next();
        String uri = "/cloud-infrastructure/pservers/pserver/hostname/p-interfaces/p-interface/p1";
        String requestBody = new JSONObject().put("interface-name", "p1").toString();

        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, uri, requestBody);
        assertEquals("response is successful", 201, response.getStatus());
        assertTrue("p-interface was created",
                traversal.V().has("aai-node-type", "p-interface").has("interface-name", "p1").hasNext());
        assertTrue("p-interface has outgoing edge to p-server",
                traversal.V().has("aai-node-type", "p-interface").has("aai-uri", uri).has("interface-name", "p1")
                        .out("tosca.relationships.network.BindsTo").has("aai-node-type", "pserver")
                        .has("hostname", "hostname").hasNext());
        verify(validationService, times(1)).validate(any());
    }

    @Test
    public void thatObjectsCanBePatched() throws UnsupportedEncodingException, AAIException {
        String uri = "/cloud-infrastructure/pservers/pserver/the-hostname";
        traversal.addV()
                .property("aai-node-type", "pserver")
                .property("hostname", "the-hostname")
                .property("equip-type", "the-equip-type")
                .property(AAIProperties.AAI_URI, uri)
                .next();
        String requestBody = new JSONObject()
                .put("hostname", "new-hostname")
                .toString();
        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.MERGE_PATCH, uri, requestBody);
        assertEquals("Expected the pserver to be updated", 200, response.getStatus());
        assertTrue("object should be updated while keeping old properties",
                traversal.V().has("aai-node-type", "pserver").has("hostname", "new-hostname")
                        .has("equip-type", "the-equip-type").hasNext());
        verify(validationService, times(1)).validate(any());
    }

    @Test
    public void thatObjectsCanBeDeleted() throws UnsupportedEncodingException, AAIException {
        String uri = "/cloud-infrastructure/pservers/pserver/the-hostname";
        String resourceVersion = "123";
        traversal.addV()
                .property("aai-node-type", "pserver")
                .property("hostname", "the-hostname")
                .property(AAIProperties.AAI_URI, uri)
                .property(AAIProperties.RESOURCE_VERSION, resourceVersion)
                .next();
        assertEquals("Expecting a No Content response", 204,
                doDelete(resourceVersion, uri, "pserver").getStatus());
        assertTrue("Expecting the pserver to be deleted",
                !traversal.V().has("aai-node-type", "pserver").has("hostname", "the-hostname").hasNext());
        verify(validationService, times(1)).validate(any());
    }

    @Test
    public void thatRelationshipCanBeCreated() throws UnsupportedEncodingException, AAIException {
        String uri = "/cloud-infrastructure/pservers/pserver/edge-test-pserver";
        traversal.addV()
                .property("aai-node-type", "pserver")
                .property("hostname", "edge-test-pserver")
                .property(AAIProperties.AAI_URI, uri)
                .property(AAIProperties.RESOURCE_VERSION, "123")
                .next();
        uri = "/cloud-infrastructure/complexes/complex/edge-test-complex";
        traversal.addV()
                .property("aai-node-type", "complex")
                .property("physical-location-id", "edge-test-complex")
                .property("physical-location-type", "AAIDefault")
                .property("street1", "AAIDefault")
                .property("city", "AAIDefault")
                .property("postal-code", "07748")
                .property("country", "USA")
                .property("region", "US")
                .property(AAIProperties.AAI_URI, uri)
                .property(AAIProperties.RESOURCE_VERSION, "234")
                .next();

        uri = "/cloud-infrastructure/complexes/complex/edge-test-complex/relationship-list/relationship";
        String requestBody = new JSONObject()
                .put("related-to", "pserver")
                .put("related-link",
                        String.format("/aai/%s/cloud-infrastructure/pservers/pserver/edge-test-pserver",
                                schemaVersions.getDefaultVersion().toString()))
                .put("relationship-label", "org.onap.relationships.inventory.LocatedIn")
                .toString();

        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT_EDGE, uri, requestBody);
        assertEquals("Expected the pserver relationship to be created", 200, response.getStatus());
        GraphTraversal<Vertex, Vertex> vertexQuery = traversal.V()
                .has("aai-node-type", "complex")
                .has("physical-location-id", "edge-test-complex")
                .in("org.onap.relationships.inventory.LocatedIn")
                .has("aai-node-type", "pserver")
                .has("hostname", "edge-test-pserver");
        GraphTraversal<Edge, Edge> edgeQuery = traversal.E()
                .has(EdgeField.PRIVATE.toString(), "false")
                .has(EdgeProperty.CONTAINS.toString(), NONE.toString())
                .has(EdgeProperty.DELETE_OTHER_V.toString(), NONE.toString())
                .has(EdgeProperty.PREVENT_DELETE.toString(), "IN");
        assertTrue("p-server has incoming edge from complex", vertexQuery.hasNext());
        assertTrue("Created Edge has expected properties", edgeQuery.hasNext());
        verify(validationService, times(1)).validate(any());
    }

    @Test
    public void thatRelationshipCanNotBeCreatedEdgeMultiplicity()
            throws UnsupportedEncodingException, AAIException, JsonMappingException, JsonProcessingException {
        String uri = "/cloud-infrastructure/pservers/pserver/httpEntryTest-pserver-01";
        traversal
                .addV() // pserver
                .property("aai-node-type", "pserver")
                .property("hostname", "httpEntryTest-pserver-01")
                .property(AAIProperties.AAI_URI, uri)
                .property(AAIProperties.RESOURCE_VERSION, "123")
                .as("v1")
                .addV() // complex
                .property("aai-node-type", "complex")
                .property("physical-location-id", "httpEntryTest-complex-01")
                .property("physical-location-type", "AAIDefault")
                .property("street1", "AAIDefault")
                .property("city", "AAIDefault")
                .property("postal-code", "07748")
                .property("country", "USA")
                .property("region", "US")
                .property(AAIProperties.AAI_URI, "/cloud-infrastructure/complexes/complex/httpEntryTest-complex-01")
                .property(AAIProperties.RESOURCE_VERSION, "234")
                .as("v2")
                // edge between pserver and complex
                .addE("org.onap.relationships.inventory.LocatedIn").from("v1").to("v2")
                .next();

        // Put Relationship
        uri = "/cloud-infrastructure/pservers/pserver/httpEntryTest-pserver-01/relationship-list/relationship";
        String requestBody = new JSONObject()
                .put("related-to", "complex")
                .put("related-link",
                        String.format("/aai/%s/cloud-infrastructure/complexes/complex/httpEntryTest-complex-01",
                                schemaVersions.getDefaultVersion().toString()))
                .put("relationship-label", "org.onap.relationships.inventory.LocatedIn")
                .toString();
        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT_EDGE, uri, requestBody);
        ServiceException serviceException = mapper.readValue(response.getEntity().toString(), ErrorResponse.class)
                .getRequestError().getServiceException();

        assertEquals("Expected the response code to be Bad Request", 400, response.getStatus());
        assertEquals("ERR.5.4.6140", serviceException.getVariables().get(3));
        assertEquals(
                "Edge multiplicity violated:multiplicity rule violated: only one edge can exist with label: org.onap.relationships.inventory.LocatedIn between pserver and complex",
                serviceException.getVariables().get(2));
    }

    @Test
    public void putEdgeWrongLabelTest()
            throws UnsupportedEncodingException, AAIException, JsonMappingException, JsonProcessingException {
        String uri = "/cloud-infrastructure/pservers/pserver/edge-test-pserver";
        traversal.addV()
                .property("aai-node-type", "pserver")
                .property("hostname", "edge-test-pserver")
                .property(AAIProperties.AAI_URI, uri)
                .property(AAIProperties.RESOURCE_VERSION, "123")
                .next();
        uri = "/cloud-infrastructure/complexes/complex/edge-test-complex";
        traversal.addV()
                .property("aai-node-type", "complex")
                .property("physical-location-id", "edge-test-complex")
                .property("physical-location-type", "AAIDefault")
                .property("street1", "AAIDefault")
                .property("city", "AAIDefault")
                .property("postal-code", "07748")
                .property("country", "USA")
                .property("region", "US")
                .property(AAIProperties.AAI_URI, uri)
                .property(AAIProperties.RESOURCE_VERSION, "234")
                .next();

        uri = "/cloud-infrastructure/complexes/complex/edge-test-complex/relationship-list/relationship";
        String requestBody = new JSONObject()
                .put("related-to", "pserver")
                .put("related-link",
                        String.format("/aai/%s/cloud-infrastructure/pservers/pserver/edge-test-pserver",
                                schemaVersions.getDefaultVersion().toString()))
                .put("relationship-label", "does.not.exist")
                .toString();

        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT_EDGE, uri, requestBody);
        ServiceException serviceException = mapper.readValue(response.getEntity().toString(), ErrorResponse.class)
                .getRequestError().getServiceException();

        assertEquals("Expected the pserver to be created", 400, response.getStatus());
        assertEquals("ERR.5.4.6107", serviceException.getVariables().get(3));
        assertEquals(
                "Required Edge-property not found in input data:org.onap.aai.edges.exceptions.EdgeRuleNotFoundException: No rule found for EdgeRuleQuery with filter params node type: complex, node type: pserver, label: does.not.exist, type: COUSIN, isPrivate: false.",
                serviceException.getVariables().get(2));
    }

    @Test
    public void thatObjectsCanBeRetrievedInPathedResponseFormat() throws UnsupportedEncodingException, AAIException {
        traversal
                .addV() // pserver
                .property("aai-node-type", "pserver")
                .property("hostname", "pserver-1")
                .property(AAIProperties.AAI_URI, "/cloud-infrastructure/pservers/pserver/pserver-1")
                .property(AAIProperties.RESOURCE_VERSION, "123")
                .addV() // pserver
                .property("aai-node-type", "pserver")
                .property("hostname", "pserver-2")
                .property(AAIProperties.AAI_URI, "/cloud-infrastructure/pservers/pserver/pserver-2")
                .property(AAIProperties.RESOURCE_VERSION, "234")
                .next();

        queryParameters.add("format", "pathed");
        String requestBody = "";
        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET,
                "/cloud-infrastructure/pservers");
        queryParameters.remove("format");

        String responseEntity = response.getEntity().toString();
        assertEquals("Expected get to succeed", 200, response.getStatus());
        assertThat(responseEntity, containsString("/cloud-infrastructure/pservers/pserver/pserver-1"));
        assertThat(responseEntity, containsString("/cloud-infrastructure/pservers/pserver/pserver-2"));
        verify(validationService, times(1)).validate(any());
    }

    @Test
    public void thatRelatedObjectsCanBeRetrieved() throws UnsupportedEncodingException, AAIException {
        String uri = "/cloud-infrastructure/pservers/pserver/related-to-pserver";
        traversal
                .addV() // pserver
                .property("aai-node-type", "pserver")
                .property("hostname", "related-to-pserver")
                .property(AAIProperties.AAI_URI, uri)
                .property(AAIProperties.RESOURCE_VERSION, "123")
                .as("v1")
                .addV() // complex
                .property("aai-node-type", "complex")
                .property("physical-location-id", "related-to-complex")
                .property("physical-location-type", "AAIDefault")
                .property("street1", "AAIDefault")
                .property("city", "AAIDefault")
                .property("postal-code", "07748")
                .property("country", "USA")
                .property("region", "US")
                .property(AAIProperties.AAI_URI, "/cloud-infrastructure/complexes/complex/related-to-complex")
                .property(AAIProperties.RESOURCE_VERSION, "234")
                .as("v2")
                // edge between pserver and complex
                .addE("org.onap.relationships.inventory.LocatedIn").from("v1").to("v2")
                .next();

        uri = "/cloud-infrastructure/complexes/complex/related-to-complex/related-to/pservers";
        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET, uri);

        assertEquals("Expected the response to be successful", 200, response.getStatus());
        assertThat("Related pserver is returned", response.getEntity().toString(),
                containsString("\"hostname\":\"related-to-pserver\""));
        verify(validationService, times(1)).validate(any());

    }

    @Test
    public void getAbstractTest() throws UnsupportedEncodingException, AAIException {
        String uri = "/cloud-infrastructure/pservers/pserver/abstract-pserver";
        traversal
                .addV() // pserver
                .property("aai-node-type", "pserver")
                .property("hostname", "abstract-pserver")
                .property(AAIProperties.AAI_URI, uri)
                .property(AAIProperties.RESOURCE_VERSION, "123")
                .as("v1")
                .addV() // generic-vnf
                .property("aai-node-type", "generic-vnf")
                .property("vnf-id", "abstract-generic-vnf")
                .property("vnf-name", "the-vnf-name")
                .property(AAIProperties.AAI_URI, "/network/generic-vnfs/generic-vnf/abstract-generic-vnf")
                .property(AAIProperties.RESOURCE_VERSION, "234")
                .as("v2")
                // edge between pserver and generic-vnf
                .addE("tosca.relationships.HostedOn").from("v2").to("v1")
                .next();

        uri = "/network/generic-vnfs/generic-vnf/abstract-generic-vnf/related-to/pservers";
        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET, uri);
        assertThat("Related to pserver is returned.", response.getEntity().toString(),
                containsString("\"hostname\":\"abstract-pserver\""));
        verify(validationService, times(1)).validate(any());
    }

    @Test
    public void getRelationshipListTest()
            throws UnsupportedEncodingException, AAIException, JsonMappingException, JsonProcessingException {
        String uri = "/cloud-infrastructure/pservers/pserver/related-to-pserver";
        traversal
                .addV() // pserver
                .property("aai-node-type", "pserver")
                .property("hostname", "related-to-pserver")
                .property(AAIProperties.AAI_URI, uri)
                .property(AAIProperties.RESOURCE_VERSION, "123")
                .as("v1")
                .addV() // complex
                .property("aai-node-type", "complex")
                .property("physical-location-id", "related-to-complex")
                .property("physical-location-type", "AAIDefault")
                .property("street1", "AAIDefault")
                .property("city", "AAIDefault")
                .property("postal-code", "07748")
                .property("country", "USA")
                .property("region", "US")
                .property(AAIProperties.AAI_URI, "/cloud-infrastructure/complexes/complex/related-to-complex")
                .property(AAIProperties.RESOURCE_VERSION, "234")
                .as("v2")
                // edge between pserver and complex
                .addE("org.onap.relationships.inventory.LocatedIn").from("v1").to("v2")
                // these properties are required when finding related edges
                .property(EdgeProperty.CONTAINS.toString(), NONE.toString())
                .property(EdgeField.PRIVATE.toString(), "false")
                .next();

        // Get Relationship
        uri = "/cloud-infrastructure/pservers/pserver/related-to-pserver";
        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET_RELATIONSHIP, uri);
        Relationship[] relationships = mapper.readValue(response.getEntity().toString(), RelationshipWrapper.class)
                .getRelationships();

        assertEquals("complex", relationships[0].getRelatedTo());
        assertEquals("org.onap.relationships.inventory.LocatedIn", relationships[0].getRelationshipLabel());
        assertEquals("/aai/v14/cloud-infrastructure/complexes/complex/related-to-complex",
                relationships[0].getRelatedLink());
        assertEquals("complex.physical-location-id", relationships[0].getRelationshipData()[0].getRelationshipKey());
        assertEquals("related-to-complex", relationships[0].getRelationshipData()[0].getRelationshipValue());
        verify(validationService, times(1)).validate(any());
    }

    @Test
    public void getRelationshipListTestWithFormatSimple() throws UnsupportedEncodingException, AAIException {
        String uri = "/cloud-infrastructure/pservers/pserver/related-to-pserver";
        traversal
                .addV() // pserver
                .property("aai-node-type", "pserver")
                .property("hostname", "related-to-pserver")
                .property(AAIProperties.AAI_URI, uri)
                .property(AAIProperties.RESOURCE_VERSION, "123")
                .as("v1")
                .addV() // complex
                .property("aai-node-type", "complex")
                .property("physical-location-id", "related-to-complex")
                .property("physical-location-type", "AAIDefault")
                .property("street1", "AAIDefault")
                .property("city", "AAIDefault")
                .property("postal-code", "07748")
                .property("country", "USA")
                .property("region", "US")
                .property(AAIProperties.AAI_URI, "/cloud-infrastructure/complexes/complex/related-to-complex")
                .property(AAIProperties.RESOURCE_VERSION, "234")
                .as("v2")
                // edge between pserver and complex
                .addE("org.onap.relationships.inventory.LocatedIn").from("v1").to("v2")
                // these properties are required when finding related edges
                .property(EdgeProperty.CONTAINS.toString(), NONE.toString())
                .property(EdgeField.PRIVATE.toString(), "false")
                .next();

        // Get Relationship
        uri = "/cloud-infrastructure/pservers/pserver/related-to-pserver";
        queryParameters.add("format", "resource");
        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET_RELATIONSHIP, uri);

        JSONObject actualResponseBody = new JSONObject(response.getEntity().toString());

        // Define the expected response
        JSONObject relationshipData = new JSONObject().put("relationship-key", "complex.physical-location-id")
                .put("relationship-value", "related-to-complex");
        JSONObject relationship = new JSONObject()
                .put("related-to", "complex")
                .put("relationship-label", "org.onap.relationships.inventory.LocatedIn")
                .put("related-link",
                        String.format("/aai/%s/cloud-infrastructure/complexes/complex/related-to-complex",
                                schemaVersions.getDefaultVersion()))
                .put("relationship-data", new JSONArray().put(relationshipData));
        JSONObject pserver = new JSONObject()
                .put("hostname", "related-to-pserver")
                .put("resource-version", "123")
                .put("relationship-list", new JSONObject().put("relationship", new JSONArray().put(relationship)));
        JSONObject expectedResponseBody = new JSONObject()
                .put("results", new JSONArray().put(new JSONObject().put("pserver", pserver)));

        JSONAssert.assertEquals(expectedResponseBody, actualResponseBody, JSONCompareMode.NON_EXTENSIBLE);
        queryParameters.remove("format");
        verify(validationService, times(1)).validate(any());
    }

    @Test
    public void notificationOnRelatedToTest() throws UnsupportedEncodingException, AAIException {

        Loader ld = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDefaultVersion());
        UEBNotification uebNotification = Mockito.spy(new UEBNotification(ld, loaderFactory, schemaVersions));
        traversalHttpEntry.setHttpEntryProperties(schemaVersions.getDefaultVersion(), uebNotification);

        Loader loader = traversalHttpEntry.getLoader();
        TransactionalGraphEngine dbEngine = traversalHttpEntry.getDbEngine();
        // Put pserver
        String uri = "/cloud-infrastructure/pservers/pserver/junit-edge-test-pserver";
        String content = "{\"hostname\":\"junit-edge-test-pserver\"}";
        doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, uri, content);
        // Put complex
        uri = "/cloud-infrastructure/complexes/complex/junit-edge-test-complex";
        content = "{\"physical-location-id\":\"junit-edge-test-complex\",\"physical-location-type\":\"AAIDefault\",\"street1\":\"AAIDefault\",\"city\":\"AAIDefault\",\"state\":\"NJ\",\"postal-code\":\"07748\",\"country\":\"USA\",\"region\":\"US\"}";
        doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, uri, content);

        // PutEdge
        uri = "/cloud-infrastructure/complexes/complex/junit-edge-test-complex/relationship-list/relationship";
        content = "{\"related-to\":\"pserver\",\"related-link\":\"/aai/" + schemaVersions.getDefaultVersion().toString()
                + "/cloud-infrastructure/pservers/pserver/junit-edge-test-pserver\",\"relationship-label\":\"org.onap.relationships.inventory.LocatedIn\"}";

        doNothing().when(uebNotification).triggerEvents();
        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT_EDGE, uri, content);

        assertEquals("Expected the pserver relationship to be deleted", 200, response.getStatus());
        assertEquals("Two notifications", 2, uebNotification.getEvents().size());
        assertEquals("Notification generated for PUT edge", "UPDATE",
                uebNotification.getEvents().get(0).getEventHeader().getValue("action").toString());
        assertThat("Event body for the edge create has the related to",
                uebNotification.getEvents().get(0).getObj().marshal(false),
                containsString("cloud-infrastructure/pservers/pserver/junit-edge-test-pserver"));

        response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.DELETE_EDGE, uri, content);
        assertEquals("Expected the pserver relationship to be deleted", 204, response.getStatus());
        assertEquals("Two notifications", 2, uebNotification.getEvents().size());
        assertEquals("Notification generated for DELETE edge", "UPDATE",
                uebNotification.getEvents().get(0).getEventHeader().getValue("action").toString());
        assertThat("Event body for the edge delete does not have the related to",
                uebNotification.getEvents().get(0).getObj().marshal(false),
                not(containsString("cloud-infrastructure/pservers/pserver/junit-edge-test-pserver")));
        dbEngine.rollback();

    }

    private Response doRequest(HttpEntry httpEntry, Loader loader, TransactionalGraphEngine dbEngine, HttpMethod method,
    String uri) throws UnsupportedEncodingException, AAIException {
        return doRequest(httpEntry, loader, dbEngine, method, uri, null, null);
    }

    private Response doRequest(HttpEntry httpEntry, Loader loader, TransactionalGraphEngine dbEngine, HttpMethod method,
    String uri, String requestBody) throws UnsupportedEncodingException, AAIException {
        return doRequest(httpEntry, loader, dbEngine, method, uri, requestBody, null);
    }
    private Response doRequest(HttpEntry httpEntry, Loader loader, TransactionalGraphEngine dbEngine, HttpMethod method,
    String uri, QueryOptions queryOptions) throws UnsupportedEncodingException, AAIException {
        return doRequest(httpEntry, loader, dbEngine, method, uri, null, queryOptions);
    }

    private Response doRequest(HttpEntry httpEntry, Loader loader, TransactionalGraphEngine dbEngine, HttpMethod method,
            String uri, String requestBody, QueryOptions queryOptions) throws UnsupportedEncodingException, AAIException {
        URI uriObject = UriBuilder.fromPath(uri).build();
        QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(uriObject);
        String objType;
        if (!uriQuery.getContainerType().equals("")) {
                objType = uriQuery.getContainerType();
        } else {
                objType = uriQuery.getResultType();
        }
        if (uri.endsWith("relationship")) {
            objType = "relationship";
        }
        Introspector obj;
        if (method.equals(HttpMethod.GET) || method.equals(HttpMethod.GET_RELATIONSHIP)) {
            obj = loader.introspectorFromName(objType);
        } else {
            obj = loader.unmarshal(objType, requestBody, org.onap.aai.restcore.MediaType.getEnum("application/json"));
        }

        DBRequest.Builder builder = new DBRequest.Builder(method, uriObject, uriQuery, obj, httpHeaders, uriInfo,
                "JUNIT-TRANSACTION");
        DBRequest dbRequest = requestBody != null
                ? builder.rawRequestContent(requestBody).build()
                : builder.build();

        List<DBRequest> dbRequestList = Collections.singletonList(dbRequest);

        Pair<Boolean, List<Pair<URI, Response>>> responsesTuple = httpEntry.process(dbRequestList, "JUNIT", Collections.emptySet(), true, queryOptions);
        return responsesTuple.getValue1().get(0).getValue1();
    }

    private Response doDelete(String resourceVersion, String uri, String nodeType)
            throws UnsupportedEncodingException, AAIException {
        queryParameters.add("resource-version", resourceVersion);

        URI uriObject = UriBuilder.fromPath(uri).build();

        QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(uriObject);

        String content = "";

        Introspector obj = loader.introspectorFromName(nodeType);

        DBRequest dbRequest = new DBRequest.Builder(HttpMethod.DELETE, uriObject, uriQuery, obj, httpHeaders, uriInfo,
                "JUNIT-TRANSACTION").rawRequestContent(content).build();

        Pair<Boolean, List<Pair<URI, Response>>> responsesTuple = traversalHttpEntry.process(Arrays.asList(dbRequest),
                "JUNIT");
        return responsesTuple.getValue1().get(0).getValue1();
    }

    @Test
    public void setDepthTest() throws AAIException {
        System.setProperty("AJSC_HOME", ".");
        System.setProperty("BUNDLECONFIG_DIR", "src/main/test/resources");

        String depthParam = AAIConfig.get("aai.rest.getall.depthparam");
        traversalHttpEntry.setHttpEntryProperties(schemaVersions.getDefaultVersion());
        int depth = traversalHttpEntry.setDepth(null, depthParam);
        assertEquals(AAIProperties.MAXIMUM_DEPTH.intValue(), depth);
    }

    @Test
    public void thatEventsAreValidated() throws AAIException, UnsupportedEncodingException {
        String uri = "/cloud-infrastructure/pservers/pserver/theHostname";
        traversal.addV()
                .property("aai-node-type", "pserver")
                .property("hostname", "theHostname")
                .property("equip-type", "theEquipType")
                .property(AAIProperties.AAI_URI, uri)
                .next();

        JSONObject expectedResponseBody = new JSONObject()
                .put("hostname", "theHostname")
                .put("equip-type", "theEquipType");
        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET, uri);
        JSONObject actualResponseBody = new JSONObject(response.getEntity().toString());

        JSONAssert.assertEquals(expectedResponseBody, actualResponseBody, JSONCompareMode.NON_EXTENSIBLE);
        assertEquals("Expected the pserver to be returned", 200, response.getStatus());
        verify(validationService, times(1)).validate(any());
    }
}
