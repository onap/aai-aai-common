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

package org.onap.aai.rest.db;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.*;

import javax.ws.rs.core.*;

import org.javatuples.Pair;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.onap.aai.AAISetup;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.dbmap.DBConnectionType;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.rest.ueb.UEBNotification;
import org.onap.aai.restcore.HttpMethod;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.util.AAIConfig;

@RunWith(value = Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HttpEntryTest extends AAISetup {

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
        return Arrays.asList(new Object[][] {{QueryStyle.TRAVERSAL}, {QueryStyle.TRAVERSAL_URI}});
    }

    private HttpHeaders httpHeaders;

    private UriInfo uriInfo;

    private MultivaluedMap<String, String> headersMultiMap;
    private MultivaluedMap<String, String> queryParameters;

    private List<String> aaiRequestContextList;

    private List<MediaType> outputMediaTypes;

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

        when(httpHeaders.getAcceptableMediaTypes()).thenReturn(outputMediaTypes);
        when(httpHeaders.getRequestHeaders()).thenReturn(headersMultiMap);

        when(httpHeaders.getRequestHeader("aai-request-context")).thenReturn(aaiRequestContextList);

        when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
        when(uriInfo.getQueryParameters(false)).thenReturn(queryParameters);

        // TODO - Check if this is valid since RemoveDME2QueryParameters seems to be very
        // unreasonable
        Mockito.doReturn(null).when(queryParameters).remove(anyObject());

        when(httpHeaders.getMediaType()).thenReturn(APPLICATION_JSON);
    }

    private Response doRequest(HttpEntry httpEntry, Loader loader,
        TransactionalGraphEngine dbEngine, HttpMethod method, String uri, String content)
        throws UnsupportedEncodingException, AAIException {
        URI uriObject = UriBuilder.fromPath(uri).build();
        QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(uriObject);
        String objType = uriQuery.getResultType();
        if (uri.endsWith("relationship")) {
            objType = "relationship";
        }
        Introspector obj = null;
        if (method.equals(HttpMethod.GET)) {
            obj = loader.introspectorFromName(objType);
        } else {
            obj = loader.unmarshal(objType, content,
                org.onap.aai.restcore.MediaType.getEnum("application/json"));
        }

        DBRequest dbRequest = new DBRequest.Builder(method, uriObject, uriQuery, obj, httpHeaders,
            uriInfo, "JUNIT-TRANSACTION").rawRequestContent(content).build();

        List<DBRequest> dbRequestList = new ArrayList<>();
        dbRequestList.add(dbRequest);

        Pair<Boolean, List<Pair<URI, Response>>> responsesTuple =
            httpEntry.process(dbRequestList, "JUNIT");
        return responsesTuple.getValue1().get(0).getValue1();
    }

    @Test
    public void test1PutOnPserver() throws UnsupportedEncodingException, AAIException {

        /*
         * TODO do the same with uri
         */
        DBConnectionType type = DBConnectionType.REALTIME;
        traversalHttpEntry.setHttpEntryProperties(schemaVersions.getDefaultVersion(), type);
        Loader loader = traversalHttpEntry.getLoader();
        TransactionalGraphEngine dbEngine = traversalHttpEntry.getDbEngine();

        String uri = "/cloud-infrastructure/pservers/pserver/junit-test1";
        String content = "{\"hostname\":\"junit-test1\"}";
        Response response =
            doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, uri, content);
        dbEngine.commit();
        assertEquals("Expected the pserver to be created", 201, response.getStatus());
    }

    @Test
    public void test2PutOnPserverNoPInterface() throws UnsupportedEncodingException, AAIException {

        DBConnectionType type = DBConnectionType.REALTIME;
        traversalHttpEntry.setHttpEntryProperties(schemaVersions.getDefaultVersion(), type);
        Loader loader = traversalHttpEntry.getLoader();
        TransactionalGraphEngine dbEngine = traversalHttpEntry.getDbEngine();

        String uri = "/cloud-infrastructure/pservers/pserver/junit-test2";
        String content = "{\"hostname\":\"junit-test2\"}";
        Response response =
            doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, uri, content);
        dbEngine.commit();
        assertEquals("Expected the pserver to be created", 201, response.getStatus());
    }

    @Test
    public void test3PutOnPInterface() {
        try {
            DBConnectionType type = DBConnectionType.REALTIME;
            traversalHttpEntry.setHttpEntryProperties(schemaVersions.getDefaultVersion(), type);
            Loader loader = traversalHttpEntry.getLoader();
            TransactionalGraphEngine dbEngine = traversalHttpEntry.getDbEngine();

            String uri =
                "/cloud-infrastructure/pservers/pserver/junit-test1/p-interfaces/p-interface/p1";
            String content = "{\"interface-name\":\"p1\"}";
            Response response =
                doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, uri, content);
            dbEngine.commit();
            assertEquals("Expected the p-interface to be created", 201, response.getStatus());
        } catch (UnsupportedEncodingException | AAIException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void test4GetOnPserver() throws UnsupportedEncodingException, AAIException {

        DBConnectionType type = DBConnectionType.REALTIME;
        traversalHttpEntry.setHttpEntryProperties(schemaVersions.getDefaultVersion(), type);

        Loader loader = traversalHttpEntry.getLoader();
        TransactionalGraphEngine dbEngine = traversalHttpEntry.getDbEngine();

        URI uriObject =
            UriBuilder.fromPath("/cloud-infrastructure/pservers/pserver/junit-test1").build();

        String uri = "/cloud-infrastructure/pservers/pserver/junit-test1";
        String content = "{\"hostname\":\"junit-test1\", \"equip-type\":\"junit-equip-type\"}";
        Response response =
            doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET, uri, content);
        dbEngine.commit();
        assertEquals("Expected the pserver to be returned", 200, response.getStatus());
    }

    @Test
    public void test5MergePatchOnPserver() throws UnsupportedEncodingException, AAIException {

        DBConnectionType type = DBConnectionType.REALTIME;
        traversalHttpEntry.setHttpEntryProperties(schemaVersions.getDefaultVersion(), type);

        Loader loader = traversalHttpEntry.getLoader();
        TransactionalGraphEngine dbEngine = traversalHttpEntry.getDbEngine();

        String uri = "/cloud-infrastructure/pservers/pserver/junit-test1";
        String content = "{\"hostname\":\"junit-test1\", \"equip-type\":\"junit-equip-type\"}";
        Response response =
            doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.MERGE_PATCH, uri, content);
        dbEngine.commit();
        assertEquals("Expected the pserver to be updated", 200, response.getStatus());
    }

    private int doDelete(String resourceVersion, String uri, String nodeType)
        throws UnsupportedEncodingException, AAIException {
        queryParameters.add("resource-version", resourceVersion);
        DBConnectionType type = DBConnectionType.REALTIME;
        traversalHttpEntry.setHttpEntryProperties(schemaVersions.getDefaultVersion(), type);
        Loader loader = traversalHttpEntry.getLoader();
        TransactionalGraphEngine dbEngine = traversalHttpEntry.getDbEngine();

        URI uriObject = UriBuilder.fromPath(uri).build();

        QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(uriObject);

        String content = "";

        Introspector obj = loader.introspectorFromName(nodeType);

        DBRequest dbRequest = new DBRequest.Builder(HttpMethod.DELETE, uriObject, uriQuery, obj,
            httpHeaders, uriInfo, "JUNIT-TRANSACTION").rawRequestContent(content).build();

        List<DBRequest> dbRequestList = new ArrayList<>();
        dbRequestList.add(dbRequest);

        Pair<Boolean, List<Pair<URI, Response>>> responsesTuple =
            traversalHttpEntry.process(dbRequestList, "JUNIT");
        Response response = responsesTuple.getValue1().get(0).getValue1();
        dbEngine.commit();
        return response.getStatus();
    }

    @Test
    public void test6DeleteOnPserver() throws UnsupportedEncodingException, AAIException {

        DBConnectionType type = DBConnectionType.REALTIME;
        traversalHttpEntry.setHttpEntryProperties(schemaVersions.getDefaultVersion(), type);
        Loader loader = traversalHttpEntry.getLoader();
        TransactionalGraphEngine dbEngine = traversalHttpEntry.getDbEngine();

        URI uriObject =
            UriBuilder.fromPath("/cloud-infrastructure/pservers/pserver/junit-test1").build();
        String uri = "/cloud-infrastructure/pservers/pserver/junit-test1";
        String content = "";
        Response response =
            doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET, uri, content);
        dbEngine.commit();
        String msg = response.getEntity().toString();
        JsonObject jsonObj = new JsonParser().parse(msg).getAsJsonObject();
        String resourceVersion = "";
        if (jsonObj.isJsonObject()) {
            resourceVersion = jsonObj.get("resource-version").getAsString();
        }
        assertEquals("Expected the pserver to be deleted", 204, doDelete(resourceVersion,
            "/cloud-infrastructure/pservers/pserver/junit-test1", "pserver"));
    }

    @Test
    public void test7DeleteOnPserverNoPinterface()
        throws UnsupportedEncodingException, AAIException {

        DBConnectionType type = DBConnectionType.REALTIME;
        traversalHttpEntry.setHttpEntryProperties(schemaVersions.getDefaultVersion(), type);
        // HttpEntry httpEntry = new HttpEntry(Version.getLatest(), ModelType.MOXY, queryStyle,
        // type);
        Loader loader = traversalHttpEntry.getLoader();
        TransactionalGraphEngine dbEngine = traversalHttpEntry.getDbEngine();

        String uri = "/cloud-infrastructure/pservers/pserver/junit-test2";
        String content = "";
        Response response =
            doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET, uri, content);
        dbEngine.commit();
        String msg = response.getEntity().toString();
        JsonObject jsonObj = new JsonParser().parse(msg).getAsJsonObject();
        String resourceVersion = "";
        if (jsonObj.isJsonObject()) {
            resourceVersion = jsonObj.get("resource-version").getAsString();
        }
        assertEquals("Expected the pserver to be deleted", 204, doDelete(resourceVersion,
            "/cloud-infrastructure/pservers/pserver/junit-test2", "pserver"));
    }

    @Test
    public void test8FailedGetOnPserver() throws UnsupportedEncodingException, AAIException {

        DBConnectionType type = DBConnectionType.REALTIME;
        traversalHttpEntry.setHttpEntryProperties(schemaVersions.getDefaultVersion(), type);
        // HttpEntry httpEntry = new HttpEntry(Version.getLatest(), ModelType.MOXY, queryStyle,
        // type);
        Loader loader = traversalHttpEntry.getLoader();
        TransactionalGraphEngine dbEngine = traversalHttpEntry.getDbEngine();

        String uri = "/cloud-infrastructure/pservers/pserver/junit-test2";
        String content = "";
        Response response =
            doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET, uri, content);
        dbEngine.commit();

        assertEquals("Expected the pserver to be deleted", 404, response.getStatus());
    }

    @Test
    public void putEdgeTest() throws UnsupportedEncodingException, AAIException {

        DBConnectionType type = DBConnectionType.REALTIME;
        traversalHttpEntry.setHttpEntryProperties(schemaVersions.getDefaultVersion(), type);
        // HttpEntry httpEntry = new HttpEntry(Version.getLatest(), ModelType.MOXY, queryStyle,
        // type);
        Loader loader = traversalHttpEntry.getLoader();
        TransactionalGraphEngine dbEngine = traversalHttpEntry.getDbEngine();

        // Put pserver
        String uri = "/cloud-infrastructure/pservers/pserver/junit-edge-test-pserver";
        String content = "{\"hostname\":\"junit-edge-test-pserver\"}";
        doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, uri, content);
        // Put complex
        uri = "/cloud-infrastructure/complexes/complex/junit-edge-test-complex";
        content =
            "{\"physical-location-id\":\"junit-edge-test-complex\",\"physical-location-type\":\"AAIDefault\",\"street1\":\"AAIDefault\",\"city\":\"AAIDefault\",\"state\":\"NJ\",\"postal-code\":\"07748\",\"country\":\"USA\",\"region\":\"US\"}";
        doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, uri, content);

        // PutEdge
        uri =
            "/cloud-infrastructure/complexes/complex/junit-edge-test-complex/relationship-list/relationship";
        content = "{\"related-to\":\"pserver\",\"related-link\":\"/aai/"
            + schemaVersions.getDefaultVersion().toString()
            + "/cloud-infrastructure/pservers/pserver/junit-edge-test-pserver\",\"relationship-label\":\"org.onap.relationships.inventory.LocatedIn\"}";
        Response response =
            doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT_EDGE, uri, content);

        dbEngine.rollback();
        assertEquals("Expected the pserver relationship to be created", 200, response.getStatus());
    }

    @Test
    public void notificationOnRelatedToTest() throws UnsupportedEncodingException, AAIException {

        Loader ld = loaderFactory.createLoaderForVersion(ModelType.MOXY,
            schemaVersions.getDefaultVersion());
        UEBNotification uebNotification =
            Mockito.spy(new UEBNotification(ld, loaderFactory, schemaVersions));
        DBConnectionType type = DBConnectionType.REALTIME;
        traversalHttpEntry.setHttpEntryProperties(schemaVersions.getDefaultVersion(), type,
            uebNotification);

        Loader loader = traversalHttpEntry.getLoader();
        TransactionalGraphEngine dbEngine = traversalHttpEntry.getDbEngine();
        // Put pserver
        String uri = "/cloud-infrastructure/pservers/pserver/junit-edge-test-pserver";
        String content = "{\"hostname\":\"junit-edge-test-pserver\"}";
        doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, uri, content);
        // Put complex
        uri = "/cloud-infrastructure/complexes/complex/junit-edge-test-complex";
        content =
            "{\"physical-location-id\":\"junit-edge-test-complex\",\"physical-location-type\":\"AAIDefault\",\"street1\":\"AAIDefault\",\"city\":\"AAIDefault\",\"state\":\"NJ\",\"postal-code\":\"07748\",\"country\":\"USA\",\"region\":\"US\"}";
        doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, uri, content);

        // PutEdge
        uri =
            "/cloud-infrastructure/complexes/complex/junit-edge-test-complex/relationship-list/relationship";
        content = "{\"related-to\":\"pserver\",\"related-link\":\"/aai/"
            + schemaVersions.getDefaultVersion().toString()
            + "/cloud-infrastructure/pservers/pserver/junit-edge-test-pserver\",\"relationship-label\":\"org.onap.relationships.inventory.LocatedIn\"}";

        doNothing().when(uebNotification).triggerEvents();
        Response response =
            doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT_EDGE, uri, content);
        response =
            doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.DELETE_EDGE, uri, content);

        dbEngine.rollback();
        assertEquals("Expected the pserver relationship to be deleted", 204, response.getStatus());
        assertEquals("Two notifications", 2, uebNotification.getEvents().size());

        assertEquals("Notification generated for PUT edge", "UPDATE",
            uebNotification.getEvents().get(0).getEventHeader().getValue("action").toString());
        assertThat("Event body for the edge create has the related to",
            uebNotification.getEvents().get(0).getObj().marshal(false),
            containsString("cloud-infrastructure/pservers/pserver/junit-edge-test-pserver"));

        assertEquals("Notification generated for DELETE edge", "UPDATE",
            uebNotification.getEvents().get(1).getEventHeader().getValue("action").toString());
        assertThat("Event body for the edge delete does not have the related to",
            uebNotification.getEvents().get(1).getObj().marshal(false),
            not(containsString("cloud-infrastructure/pservers/pserver/junit-edge-test-pserver")));

    }

    @Test
    public void putEdgeWrongLabelTest() throws UnsupportedEncodingException, AAIException {

        DBConnectionType type = DBConnectionType.REALTIME;
        traversalHttpEntry.setHttpEntryProperties(schemaVersions.getDefaultVersion(), type);
        // HttpEntry httpEntry = new HttpEntry(Version.getLatest(), ModelType.MOXY, queryStyle,
        // type);
        Loader loader = traversalHttpEntry.getLoader();
        TransactionalGraphEngine dbEngine = traversalHttpEntry.getDbEngine();

        // Put pserver
        String uri = "/cloud-infrastructure/pservers/pserver/junit-edge-test-pserver";
        String content = "{\"hostname\":\"junit-edge-test-pserver\"}";
        doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, uri, content);
        // Put complex
        uri = "/cloud-infrastructure/complexes/complex/junit-edge-test-complex";
        content =
            "{\"physical-location-id\":\"junit-edge-test-complex\",\"physical-location-type\":\"AAIDefault\",\"street1\":\"AAIDefault\",\"city\":\"AAIDefault\",\"state\":\"NJ\",\"postal-code\":\"07748\",\"country\":\"USA\",\"region\":\"US\"}";
        doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, uri, content);

        // PutEdge
        uri =
            "/cloud-infrastructure/complexes/complex/junit-edge-test-complex/relationship-list/relationship";
        content = "{\"related-to\":\"pserver\",\"related-link\":\"/aai/"
            + schemaVersions.getDefaultVersion().toString()
            + "/cloud-infrastructure/pservers/pserver/junit-edge-test-pserver\",\"relationship-label\":\"junk\"}";
        Response response =
            doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT_EDGE, uri, content);

        dbEngine.rollback();
        String msg = response.getEntity().toString();
        assertEquals("Expected the pserver to be created", 400, response.getStatus());
        assertThat(msg, containsString("ERR.5.4.6107"));
        assertThat(msg, containsString(
            "Required Edge-property not found in input data:org.onap.aai.edges.exceptions.EdgeRuleNotFoundException: No rule found for EdgeRuleQuery with filter params node type: complex, node type: pserver, label: junk, type: COUSIN, isPrivate: false"));

    }

    @Test
    public void pathedFormatOnGetTest() throws UnsupportedEncodingException, AAIException {

        final String testName = new Object() {}.getClass().getEnclosingMethod().getName();

        DBConnectionType type = DBConnectionType.REALTIME;
        traversalHttpEntry.setHttpEntryProperties(schemaVersions.getDefaultVersion(), type);
        // HttpEntry httpEntry = new HttpEntry(schemaVersions.getDefaultVersion(), ModelType.MOXY,
        // QueryStyle.TRAVERSAL, type);
        Loader loader = traversalHttpEntry.getLoader();
        TransactionalGraphEngine dbEngine = traversalHttpEntry.getDbEngine();

        // Put pserver
        String pserverKey = "pserver-" + testName;
        String pserverUri = "/cloud-infrastructure/pservers/pserver/" + pserverKey;
        String content = "{\"hostname\":\"" + pserverKey + "\"}";
        doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, pserverUri, content);

        // Put complex
        String complexKey = "complex-" + testName;
        String complexUri = "/cloud-infrastructure/complexes/complex/" + complexKey;
        content = "{\"physical-location-id\":\"" + complexKey
            + "\",\"physical-location-type\":\"AAIDefault\",\"street1\":\"AAIDefault\",\"city\":\"AAIDefault\",\"state\":\"NJ\",\"postal-code\":\"07748\",\"country\":\"USA\",\"region\":\"US\"}";
        doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, complexUri, content);

        // PutEdge
        String relationshipUri = "/cloud-infrastructure/complexes/complex/" + complexKey
            + "/relationship-list/relationship";
        content = "{\"related-to\":\"pserver\",\"related-link\":\"/aai/"
            + schemaVersions.getDefaultVersion().toString()
            + "/cloud-infrastructure/pservers/pserver/" + pserverKey
            + "\",\"relationship-label\":\"org.onap.relationships.inventory.LocatedIn\"}";
        doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT_EDGE, relationshipUri,
            content);

        // Get pserver with pathed
        queryParameters.add("format", "pathed");
        content = "";
        Response response =
            doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET, pserverUri, content);
        queryParameters.remove("format");

        String msg = response.getEntity().toString();
        assertEquals("Expected get to succeed", 200, response.getStatus());
        assertThat(msg, containsString(pserverUri));

        dbEngine.rollback();

    }

    @Test
    public void getAllPserversTest() throws UnsupportedEncodingException, AAIException {

        final String testName = new Object() {}.getClass().getEnclosingMethod().getName();

        DBConnectionType type = DBConnectionType.REALTIME;
        traversalHttpEntry.setHttpEntryProperties(schemaVersions.getDefaultVersion(), type);
        // HttpEntry httpEntry = new HttpEntry(Version.getLatest(), ModelType.MOXY,
        // QueryStyle.TRAVERSAL, type);
        Loader loader = traversalHttpEntry.getLoader();
        TransactionalGraphEngine dbEngine = traversalHttpEntry.getDbEngine();

        // Put pserver
        String pserver1Key = "pserver-1-" + testName;
        String pserver1Uri = "/cloud-infrastructure/pservers/pserver/" + pserver1Key;
        String content = "{\"hostname\":\"" + pserver1Key + "\"}";
        doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, pserver1Uri, content);

        // Put complex
        String pserver2Key = "pserver-2-" + testName;
        String pserver2Uri = "/cloud-infrastructure/pservers/pserver/" + pserver2Key;
        content = "{\"hostname\":\"" + pserver2Key + "\"}";
        doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, pserver2Uri, content);

        // Get pserver with pathed
        queryParameters.add("format", "pathed");
        content = "";
        Response response = doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET,
            "/cloud-infrastructure/pservers", content);
        queryParameters.remove("format");

        String msg = response.getEntity().toString();
        assertEquals("Expected get to succeed", 200, response.getStatus());
        assertThat(msg, containsString(pserver1Uri));
        assertThat(msg, containsString(pserver2Uri));

        dbEngine.rollback();

    }

    @Test
    public void testSetGetPaginationMethods() {
        DBConnectionType type = DBConnectionType.REALTIME;
        traversalHttpEntry.setHttpEntryProperties(schemaVersions.getDefaultVersion(), type);
        // HttpEntry httpEntry = new HttpEntry(schemaVersions.getDefaultVersion(), ModelType.MOXY,
        // QueryStyle.TRAVERSAL, type);
        traversalHttpEntry.setPaginationBucket(10);
        traversalHttpEntry.setPaginationIndex(1);
        traversalHttpEntry.setTotalsForPaging(101, traversalHttpEntry.getPaginationBucket());
        assertEquals("Expected the pagination bucket size to be 10", 10,
            traversalHttpEntry.getPaginationBucket());
        assertEquals("Expected the total number of pagination buckets to be 11", 11,
            traversalHttpEntry.getTotalPaginationBuckets());
        assertEquals("Expected the pagination index to be 1", 1,
            traversalHttpEntry.getPaginationIndex());
        assertEquals("Expected the total amount of vertices to be 101", 101,
            traversalHttpEntry.getTotalVertices());
    }

    @Test
    public void relatedToTest() throws UnsupportedEncodingException, AAIException {

        DBConnectionType type = DBConnectionType.REALTIME;
        traversalHttpEntry.setHttpEntryProperties(schemaVersions.getDefaultVersion(), type);
        // HttpEntry httpEntry = new HttpEntry(schemaVersions.getDefaultVersion(), ModelType.MOXY,
        // queryStyle, type);
        Loader loader = traversalHttpEntry.getLoader();
        TransactionalGraphEngine dbEngine = traversalHttpEntry.getDbEngine();

        // Put pserver
        String uri = "/cloud-infrastructure/pservers/pserver/junit-edge-test-pserver";
        String content = "{\"hostname\":\"junit-edge-test-pserver\"}";
        doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, uri, content);
        // Put complex
        uri = "/cloud-infrastructure/complexes/complex/junit-edge-test-complex";
        content =
            "{\"physical-location-id\":\"junit-edge-test-complex\",\"physical-location-type\":\"AAIDefault\",\"street1\":\"AAIDefault\",\"city\":\"AAIDefault\",\"state\":\"NJ\",\"postal-code\":\"07748\",\"country\":\"USA\",\"region\":\"US\"}";
        doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, uri, content);

        // PutEdge
        uri =
            "/cloud-infrastructure/complexes/complex/junit-edge-test-complex/relationship-list/relationship";
        content = "{\"related-to\":\"pserver\",\"related-link\":\"/aai/"
            + schemaVersions.getDefaultVersion().toString()
            + "/cloud-infrastructure/pservers/pserver/junit-edge-test-pserver\",\"relationship-label\":\"org.onap.relationships.inventory.LocatedIn\"}";
        doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT_EDGE, uri, content);

        // getRelatedTo
        uri = "/cloud-infrastructure/complexes/complex/junit-edge-test-complex/related-to/pservers";
        content = "";
        Response response =
            doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET, uri, content);
        String respBody = response.getEntity().toString();

        dbEngine.rollback();
        assertEquals("Expected the pserver to be created", 200, response.getStatus());
        assertThat("Related to pserver is returned.", respBody,
            containsString("\"hostname\":\"junit-edge-test-pserver\""));

    }

    @Test
    public void setDepthTest() throws UnsupportedEncodingException, AAIException {
        System.setProperty("AJSC_HOME", ".");
        System.setProperty("BUNDLECONFIG_DIR", "src/main/test/resources");

        String depthParam = AAIConfig.get("aai.rest.getall.depthparam");
        DBConnectionType type = DBConnectionType.REALTIME;
        traversalHttpEntry.setHttpEntryProperties(schemaVersions.getDefaultVersion(), type);
        // HttpEntry httpEntry = new HttpEntry(Version.getLatest(), ModelType.MOXY,
        // QueryStyle.TRAVERSAL, type);
        int depth = traversalHttpEntry.setDepth(null, depthParam);
        assertEquals(AAIProperties.MAXIMUM_DEPTH.intValue(), depth);
    }

    @Test
    public void getAbstractTest() throws UnsupportedEncodingException, AAIException {

        DBConnectionType type = DBConnectionType.REALTIME;
        traversalHttpEntry.setHttpEntryProperties(schemaVersions.getDefaultVersion(), type);
        // HttpEntry httpEntry = new HttpEntry(Version.getLatest(), ModelType.MOXY, queryStyle,
        // type);
        Loader loader = traversalHttpEntry.getLoader();
        TransactionalGraphEngine dbEngine = traversalHttpEntry.getDbEngine();

        // Put generic-vnf
        String uri = "/network/generic-vnfs/generic-vnf/junit-abstract-test-generic-vnf";
        String content =
            "{\"vnf-id\":\"junit-abstract-test-generic-vnf\",\"vnf-name\":\"junit-generic-vnf-name\"}";
        doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, uri, content);

        // Put pserver
        uri = "/cloud-infrastructure/pservers/pserver/junit-abstract-test-pserver";
        content = "{\"hostname\":\"junit-abstract-test-pserver\"}";
        doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT, uri, content);

        // PutEdge
        uri =
            "/cloud-infrastructure/pservers/pserver/junit-abstract-test-pserver/relationship-list/relationship";
        content =
            "{\"related-to\":\"vnf\",\"relationship-data\":[{\"relationship-key\":\"vnf.vnf-id\",\"relationship-value\":\"junit-abstract-test-generic-vnf\"}]}";
        doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.PUT_EDGE, uri, content);

        // getRelatedTo
        uri =
            "/network/generic-vnfs/generic-vnf/junit-abstract-test-generic-vnf/related-to/pservers";
        content = "";
        Response response =
            doRequest(traversalHttpEntry, loader, dbEngine, HttpMethod.GET, uri, content);
        String respBody = response.getEntity().toString();

        dbEngine.rollback();
        assertThat("Related to pserver is returned.", respBody,
            containsString("\"hostname\":\"junit-abstract-test-pserver\""));
    }
}
