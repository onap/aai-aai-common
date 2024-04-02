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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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

@TestMethodOrder(MethodName.class)
public class HttpEntryTest extends AAISetup {

    protected static final MediaType APPLICATION_JSON = MediaType.valueOf("application/json");

    private static final Set<Integer> VALID_HTTP_STATUS_CODES = new HashSet<>();

    static {
        VALID_HTTP_STATUS_CODES.add(200);
        VALID_HTTP_STATUS_CODES.add(201);
        VALID_HTTP_STATUS_CODES.add(204);
    }
    public QueryStyle queryStyle;

    /*
     * TODO Change the HttpEntry instances accoringly
     */
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

    @BeforeEach
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

    @AfterEach
    public void rollback() {
        dbEngine.rollback();
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void thatObjectsCanBeRetrieved(QueryStyle queryStyle) throws UnsupportedEncodingException, AAIException {
        initHttpEntryTest(queryStyle);
        String uri = "/cloud-infrastructure/pservers/pserver/theHostname";
        traversal.addV()
                .property("aai-node-type", "pserver")
                .property("hostname", "theHostname")
                .property("equip-type", "theEquipType")
                .property(AAIProperties.AAI_URI, uri)
                .next();
        String requestBody = new JSONObject()
                .put("hostname", "theHostname")
                .put("equip-type", "theEquipType")
                .toString();

        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET, uri, requestBody);
        assertEquals(200, response.getStatus(), "Expected the pserver to be returned");
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void thatObjectsCanNotBeFound(QueryStyle queryStyle) throws UnsupportedEncodingException, AAIException {
        initHttpEntryTest(queryStyle);
        String uri = "/cloud-infrastructure/pservers/pserver/junit-test2";
        String requestBody = "";

        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET, uri, requestBody);
        assertEquals(404, response.getStatus(), "The pserver is not found");
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void thatObjectCanBeCreatedViaPUT(QueryStyle queryStyle) throws UnsupportedEncodingException, AAIException {
        initHttpEntryTest(queryStyle);
        String uri = "/cloud-infrastructure/pservers/pserver/theHostname";
        String requestBody = new JSONObject().put("hostname", "theHostname").toString();

        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, uri, requestBody);
        assertEquals(201, response.getStatus(), "Expecting the pserver to be created");
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void thatObjectCreationFailsWhenResourceVersionIsProvided(QueryStyle queryStyle)
            throws UnsupportedEncodingException, AAIException, JsonMappingException, JsonProcessingException {
        initHttpEntryTest(queryStyle);
        String uri = "/cloud-infrastructure/pservers/pserver/theHostname";
        String requestBody = new JSONObject()
                .put("hostname", "theHostname")
                .put("resource-version", "123")
                .toString();

        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, uri, requestBody);
        ErrorResponse errorResponseEntity = mapper.readValue(response.getEntity().toString(), ErrorResponse.class);
        assertEquals(412, response.getStatus(), "Expecting the pserver to be created");
        assertEquals(
                "Resource version specified on create:resource-version passed for create of /cloud-infrastructure/pservers/pserver/theHostname",
                errorResponseEntity.getRequestError().getServiceException().getVariables().get(2));
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void thatObjectCanBeUpdatedViaPUT(QueryStyle queryStyle) throws UnsupportedEncodingException, AAIException {
        initHttpEntryTest(queryStyle);
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
        assertEquals(200, response.getStatus(), "Expecting the pserver to be updated");
        assertTrue(traversal.V().has("hostname", "updatedHostname").hasNot("number-of-cpus").hasNext(),
                "That old properties are removed");
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void thatUpdateFailsWhenResourceVersionsMismatch(QueryStyle queryStyle)
            throws UnsupportedEncodingException, AAIException, JsonMappingException, JsonProcessingException {
        initHttpEntryTest(queryStyle);
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
        assertEquals(412, response.getStatus(), "Expecting the update to fail");
        assertEquals(
                "Precondition Failed:resource-version MISMATCH for update of /cloud-infrastructure/pservers/pserver/updatedHostname",
                errorResponseEntity.getRequestError().getServiceException().getVariables().get(2));
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void thatUpdateFailsWhenResourceVersionIsNotProvided(QueryStyle queryStyle)
            throws UnsupportedEncodingException, AAIException, JsonMappingException, JsonProcessingException {
        initHttpEntryTest(queryStyle);
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
        assertEquals(412, response.getStatus(), "Request should fail when no resource-version is provided");
        assertEquals(
                "Precondition Required:resource-version not passed for update of /cloud-infrastructure/pservers/pserver/theHostname",
                errorResponseEntity.getRequestError().getServiceException().getVariables().get(2));
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void thatCreateViaPUTAddsRelationshipsToExistingObjects(QueryStyle queryStyle) throws UnsupportedEncodingException, AAIException {
        initHttpEntryTest(queryStyle);
        traversal.addV()
                .property("aai-node-type", "pserver")
                .property("hostname", "hostname")
                .property(AAIProperties.AAI_URI, "/cloud-infrastructure/pservers/pserver/hostname")
                .next();
        String uri = "/cloud-infrastructure/pservers/pserver/hostname/p-interfaces/p-interface/p1";
        String requestBody = new JSONObject().put("interface-name", "p1").toString();

        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, uri, requestBody);
        assertEquals(201, response.getStatus(), "response is successful");
        assertTrue(traversal.V().has("aai-node-type", "p-interface").has("interface-name", "p1").hasNext(),
                "p-interface was created");
        assertTrue(traversal.V().has("aai-node-type", "p-interface").has("aai-uri", uri).has("interface-name", "p1")
                        .out("tosca.relationships.network.BindsTo").has("aai-node-type", "pserver")
                        .has("hostname", "hostname").hasNext(),
                "p-interface has outgoing edge to p-server");
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void thatObjectsCanBePatched(QueryStyle queryStyle) throws UnsupportedEncodingException, AAIException {
        initHttpEntryTest(queryStyle);
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
        assertEquals(200, response.getStatus(), "Expected the pserver to be updated");
        assertTrue(traversal.V().has("aai-node-type", "pserver").has("hostname", "new-hostname")
                        .has("equip-type", "the-equip-type").hasNext(),
                "object should be updated while keeping old properties");
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void thatObjectsCanBeDeleted(QueryStyle queryStyle) throws UnsupportedEncodingException, AAIException {
        initHttpEntryTest(queryStyle);
        String uri = "/cloud-infrastructure/pservers/pserver/the-hostname";
        String resourceVersion = "123";
        traversal.addV()
                .property("aai-node-type", "pserver")
                .property("hostname", "the-hostname")
                .property(AAIProperties.AAI_URI, uri)
                .property(AAIProperties.RESOURCE_VERSION, resourceVersion)
                .next();
        assertEquals(204,
                doDelete(resourceVersion, uri, "pserver").getStatus(),
                "Expecting a No Content response");
        assertTrue(!traversal.V().has("aai-node-type", "pserver").has("hostname", "the-hostname").hasNext(),
                "Expecting the pserver to be deleted");
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void thatRelationshipCanBeCreated(QueryStyle queryStyle) throws UnsupportedEncodingException, AAIException {
        initHttpEntryTest(queryStyle);
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
        assertEquals(200, response.getStatus(), "Expected the pserver relationship to be created");
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
        assertTrue(vertexQuery.hasNext(), "p-server has incoming edge from complex");
        assertTrue(edgeQuery.hasNext(), "Created Edge has expected properties");
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void thatRelationshipCanNotBeCreatedEdgeMultiplicity(QueryStyle queryStyle)
            throws UnsupportedEncodingException, AAIException, JsonMappingException, JsonProcessingException {
        initHttpEntryTest(queryStyle);
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

        assertEquals(400, response.getStatus(), "Expected the response code to be Bad Request");
        assertEquals("ERR.5.4.6140", serviceException.getVariables().get(3));
        assertEquals(
                "Edge multiplicity violated:multiplicity rule violated: only one edge can exist with label: org.onap.relationships.inventory.LocatedIn between pserver and complex",
                serviceException.getVariables().get(2));
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void putEdgeWrongLabelTest(QueryStyle queryStyle)
            throws UnsupportedEncodingException, AAIException, JsonMappingException, JsonProcessingException {
        initHttpEntryTest(queryStyle);
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

        assertEquals(400, response.getStatus(), "Expected the pserver to be created");
        assertEquals("ERR.5.4.6107", serviceException.getVariables().get(3));
        assertEquals(
                "Required Edge-property not found in input data:org.onap.aai.edges.exceptions.EdgeRuleNotFoundException: No rule found for EdgeRuleQuery with filter params node type: complex, node type: pserver, label: does.not.exist, type: COUSIN, isPrivate: false.",
                serviceException.getVariables().get(2));
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void thatObjectsCanBeRetrievedInPathedResponseFormat(QueryStyle queryStyle) throws UnsupportedEncodingException, AAIException {
        initHttpEntryTest(queryStyle);
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
                "/cloud-infrastructure/pservers", requestBody);
        queryParameters.remove("format");

        String responseEntity = response.getEntity().toString();
        assertEquals(200, response.getStatus(), "Expected get to succeed");
        assertThat(responseEntity, containsString("/cloud-infrastructure/pservers/pserver/pserver-1"));
        assertThat(responseEntity, containsString("/cloud-infrastructure/pservers/pserver/pserver-2"));
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void thatRelatedObjectsCanBeRetrieved(QueryStyle queryStyle) throws UnsupportedEncodingException, AAIException {
        initHttpEntryTest(queryStyle);
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
        String responseBody = "";
        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET, uri, responseBody);

        assertEquals(200, response.getStatus(), "Expected the response to be successful");
        assertThat("Related pserver is returned", response.getEntity().toString(),
                containsString("\"hostname\":\"related-to-pserver\""));

    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void getAbstractTest(QueryStyle queryStyle) throws UnsupportedEncodingException, AAIException {
        initHttpEntryTest(queryStyle);
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

        String requestBody = "";
        uri = "/network/generic-vnfs/generic-vnf/abstract-generic-vnf/related-to/pservers";
        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET, uri, requestBody);
        assertThat("Related to pserver is returned.", response.getEntity().toString(),
                containsString("\"hostname\":\"abstract-pserver\""));
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void getRelationshipListTest(QueryStyle queryStyle)
            throws UnsupportedEncodingException, AAIException, JsonMappingException, JsonProcessingException {
        initHttpEntryTest(queryStyle);
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
        String requestBody = "";
        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET_RELATIONSHIP, uri,
                requestBody);
        Relationship[] relationships = mapper.readValue(response.getEntity().toString(), RelationshipWrapper.class)
                .getRelationships();

        assertEquals("complex", relationships[0].getRelatedTo());
        assertEquals("org.onap.relationships.inventory.LocatedIn", relationships[0].getRelationshipLabel());
        assertEquals("/aai/v14/cloud-infrastructure/complexes/complex/related-to-complex",
                relationships[0].getRelatedLink());
        assertEquals("complex.physical-location-id", relationships[0].getRelationshipData()[0].getRelationshipKey());
        assertEquals("related-to-complex", relationships[0].getRelationshipData()[0].getRelationshipValue());
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void getRelationshipListTestWithFormatSimple(QueryStyle queryStyle) throws UnsupportedEncodingException, AAIException {
        initHttpEntryTest(queryStyle);
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
        String requestBody = "";
        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET_RELATIONSHIP, uri,
                requestBody);

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
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void notificationOnRelatedToTest(QueryStyle queryStyle) throws UnsupportedEncodingException, AAIException {

        initHttpEntryTest(queryStyle);

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

        assertEquals(200, response.getStatus(), "Expected the pserver relationship to be deleted");
        assertEquals(2, uebNotification.getEvents().size(), "Two notifications");
        assertEquals("UPDATE",
                uebNotification.getEvents().get(0).getEventHeader().getValue("action").toString(),
                "Notification generated for PUT edge");
        assertThat("Event body for the edge create has the related to",
                uebNotification.getEvents().get(0).getObj().marshal(false),
                containsString("cloud-infrastructure/pservers/pserver/junit-edge-test-pserver"));

        response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.DELETE_EDGE, uri, content);
        assertEquals(204, response.getStatus(), "Expected the pserver relationship to be deleted");
        assertEquals(2, uebNotification.getEvents().size(), "Two notifications");
        assertEquals("UPDATE",
                uebNotification.getEvents().get(0).getEventHeader().getValue("action").toString(),
                "Notification generated for DELETE edge");
        assertThat("Event body for the edge delete does not have the related to",
                uebNotification.getEvents().get(0).getObj().marshal(false),
                not(containsString("cloud-infrastructure/pservers/pserver/junit-edge-test-pserver")));
        dbEngine.rollback();

    }

    private Response doRequest(HttpEntry httpEntry, Loader loader, TransactionalGraphEngine dbEngine, HttpMethod method,
            String uri, String requestBody) throws UnsupportedEncodingException, AAIException {
        URI uriObject = UriBuilder.fromPath(uri).build();
        QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(uriObject);
        String objType = uriQuery.getResultType();
        if (uri.endsWith("relationship")) {
            objType = "relationship";
        }
        Introspector obj;
        if (method.equals(HttpMethod.GET) || method.equals(HttpMethod.GET_RELATIONSHIP)) {
            obj = loader.introspectorFromName(objType);
        } else {
            obj = loader.unmarshal(objType, requestBody, org.onap.aai.restcore.MediaType.getEnum("application/json"));
        }

        DBRequest dbRequest = new DBRequest.Builder(method, uriObject, uriQuery, obj, httpHeaders, uriInfo,
                "JUNIT-TRANSACTION")
                .rawRequestContent(requestBody).build();

        List<DBRequest> dbRequestList = new ArrayList<>();
        dbRequestList.add(dbRequest);

        Pair<Boolean, List<Pair<URI, Response>>> responsesTuple = httpEntry.process(dbRequestList, "JUNIT");
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

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void testSetGetPaginationMethods(QueryStyle queryStyle) {
        initHttpEntryTest(queryStyle);
        traversalHttpEntry.setHttpEntryProperties(schemaVersions.getDefaultVersion());
        traversalHttpEntry.setPaginationBucket(10);
        traversalHttpEntry.setPaginationIndex(1);
        traversalHttpEntry.setTotalsForPaging(101, traversalHttpEntry.getPaginationBucket());
        assertEquals(10, traversalHttpEntry.getPaginationBucket(), "Expected the pagination bucket size to be 10");
        assertEquals(11,
                traversalHttpEntry.getTotalPaginationBuckets(),
                "Expected the total number of pagination buckets to be 11");
        assertEquals(1, traversalHttpEntry.getPaginationIndex(), "Expected the pagination index to be 1");
        assertEquals(101, traversalHttpEntry.getTotalVertices(), "Expected the total amount of vertices to be 101");
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void setDepthTest(QueryStyle queryStyle) throws AAIException {
        initHttpEntryTest(queryStyle);
        System.setProperty("AJSC_HOME", ".");
        System.setProperty("BUNDLECONFIG_DIR", "src/main/test/resources");

        String depthParam = AAIConfig.get("aai.rest.getall.depthparam");
        traversalHttpEntry.setHttpEntryProperties(schemaVersions.getDefaultVersion());
        int depth = traversalHttpEntry.setDepth(null, depthParam);
        assertEquals(AAIProperties.MAXIMUM_DEPTH.intValue(), depth);
    }

    public void initHttpEntryTest(QueryStyle queryStyle) {
        this.queryStyle = queryStyle;
    }
}
