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
package org.onap.aai.rest.db;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.javatuples.Pair;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.onap.aai.AAISetup;
import org.onap.aai.dbmap.DBConnectionType;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.introspection.Version;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.restcore.HttpMethod;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;

import javax.ws.rs.core.*;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

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

    @Parameterized.Parameters(name = "QueryStyle.{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {QueryStyle.TRAVERSAL}
        });
    }

    private HttpHeaders httpHeaders;

    private UriInfo uriInfo;

    private MultivaluedMap<String, String> headersMultiMap;
    private MultivaluedMap<String, String> queryParameters;

    private List<String> aaiRequestContextList;

    private List<MediaType> outputMediaTypes;

    @Before
    public void setup(){

        httpHeaders         = Mockito.mock(HttpHeaders.class);
        uriInfo             = Mockito.mock(UriInfo.class);

        headersMultiMap     = new MultivaluedHashMap<>();
        queryParameters     = Mockito.spy(new MultivaluedHashMap<>());

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

        // TODO - Check if this is valid since RemoveDME2QueryParameters seems to be very unreasonable
        Mockito.doReturn(null).when(queryParameters).remove(anyObject());

        when(httpHeaders.getMediaType()).thenReturn(APPLICATION_JSON);
    }

    private Response doRequest(HttpEntry httpEntry, Loader loader, TransactionalGraphEngine dbEngine, HttpMethod method, String uri, String content) throws UnsupportedEncodingException, AAIException {
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
            obj = loader.unmarshal(objType, content, org.onap.aai.restcore.MediaType.getEnum("application/json"));
        }

        DBRequest dbRequest =
                new DBRequest.Builder(method, uriObject, uriQuery, obj, httpHeaders, uriInfo, "JUNIT-TRANSACTION")
                        .rawRequestContent(content).build();

        List<DBRequest> dbRequestList = new ArrayList<>();
        dbRequestList.add(dbRequest);

        Pair<Boolean, List<Pair<URI, Response>>> responsesTuple  = httpEntry.process(dbRequestList, "JUNIT");
        return responsesTuple.getValue1().get(0).getValue1();
    }

    
    @Test
    public void test1PutOnPserver() throws UnsupportedEncodingException, AAIException {

        DBConnectionType type = DBConnectionType.REALTIME;
        HttpEntry httpEntry = new HttpEntry(Version.getLatest(), ModelType.MOXY, queryStyle, type);
        Loader loader = httpEntry.getLoader();
        TransactionalGraphEngine dbEngine = httpEntry.getDbEngine();

        String uri = "/cloud-infrastructure/pservers/pserver/junit-test1";
        String content = "{\"hostname\":\"junit-test1\"}";
        Response response = doRequest(httpEntry, loader, dbEngine, HttpMethod.PUT, uri, content);
        dbEngine.commit();
        assertEquals("Expected the pserver to be created", 201, response.getStatus());
    }

    @Test
    public void test2PutOnPserverNoPInterface() throws UnsupportedEncodingException, AAIException {

        DBConnectionType type = DBConnectionType.REALTIME;
        HttpEntry httpEntry = new HttpEntry(Version.getLatest(), ModelType.MOXY, queryStyle, type);
        Loader loader = httpEntry.getLoader();
        TransactionalGraphEngine dbEngine = httpEntry.getDbEngine();

        String uri = "/cloud-infrastructure/pservers/pserver/junit-test2";
        String content = "{\"hostname\":\"junit-test2\"}";
        Response response = doRequest(httpEntry, loader, dbEngine, HttpMethod.PUT, uri, content);
        dbEngine.commit();
        assertEquals("Expected the pserver to be created", 201, response.getStatus());
    }

    @Test
    public void test3PutOnPInterface()  {
    	try {
        DBConnectionType type = DBConnectionType.REALTIME;
        HttpEntry httpEntry = new HttpEntry(Version.getLatest(), ModelType.MOXY, queryStyle, type);
        Loader loader = httpEntry.getLoader();
        TransactionalGraphEngine dbEngine = httpEntry.getDbEngine();

        String uri = "/cloud-infrastructure/pservers/pserver/junit-test1/p-interfaces/p-interface/p1";
        String content = "{\"interface-name\":\"p1\"}";
        Response response = doRequest(httpEntry, loader, dbEngine, HttpMethod.PUT, uri, content);
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
        HttpEntry httpEntry = new HttpEntry(Version.getLatest(), ModelType.MOXY, queryStyle, type);
        Loader loader = httpEntry.getLoader();
        TransactionalGraphEngine dbEngine = httpEntry.getDbEngine();

        URI uriObject = UriBuilder.fromPath("/cloud-infrastructure/pservers/pserver/junit-test1").build();

        String uri = "/cloud-infrastructure/pservers/pserver/junit-test1";
        String content = "{\"hostname\":\"junit-test1\", \"equip-type\":\"junit-equip-type\"}";
        Response response = doRequest(httpEntry, loader, dbEngine, HttpMethod.GET, uri, content);
        dbEngine.commit();
        assertEquals("Expected the pserver to be returned", 200, response.getStatus());
    }
  
    @Test
    public void test5MergePatchOnPserver() throws UnsupportedEncodingException, AAIException {

        DBConnectionType type = DBConnectionType.REALTIME;
        HttpEntry httpEntry = new HttpEntry(Version.getLatest(), ModelType.MOXY, queryStyle, type);
        Loader loader = httpEntry.getLoader();
        TransactionalGraphEngine dbEngine = httpEntry.getDbEngine();

        String uri = "/cloud-infrastructure/pservers/pserver/junit-test1";
        String content = "{\"hostname\":\"junit-test1\", \"equip-type\":\"junit-equip-type\"}";
        Response response = doRequest(httpEntry, loader, dbEngine, HttpMethod.MERGE_PATCH, uri, content);
        dbEngine.commit();
        assertEquals("Expected the pserver to be updated", 200, response.getStatus());
    }
    
    private int doDelete(String resourceVersion, String uri, String nodeType) throws UnsupportedEncodingException, AAIException {
    	queryParameters.add("resource-version", resourceVersion);
    	DBConnectionType type = DBConnectionType.REALTIME;
        HttpEntry httpEntry = new HttpEntry(Version.getLatest(), ModelType.MOXY, queryStyle, type);
        Loader loader = httpEntry.getLoader();
        TransactionalGraphEngine dbEngine = httpEntry.getDbEngine();

        URI uriObject = UriBuilder.fromPath(uri).build();

        QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(uriObject);

        String content = "";

        Introspector obj = loader.introspectorFromName(nodeType);

        DBRequest dbRequest =
                new DBRequest.Builder(HttpMethod.DELETE, uriObject, uriQuery, obj, httpHeaders, uriInfo, "JUNIT-TRANSACTION")
                        .rawRequestContent(content).build();

        List<DBRequest> dbRequestList = new ArrayList<>();
        dbRequestList.add(dbRequest);

        Pair<Boolean, List<Pair<URI, Response>>> responsesTuple  = httpEntry.process(dbRequestList, "JUNIT");
        Response response = responsesTuple.getValue1().get(0).getValue1();
        dbEngine.commit();
        return response.getStatus();
    }
    
    @Test
    public void test6DeleteOnPserver() throws UnsupportedEncodingException, AAIException {


        DBConnectionType type = DBConnectionType.REALTIME;
        HttpEntry httpEntry = new HttpEntry(Version.getLatest(), ModelType.MOXY, queryStyle, type);
        Loader loader = httpEntry.getLoader();
        TransactionalGraphEngine dbEngine = httpEntry.getDbEngine();

        URI uriObject = UriBuilder.fromPath("/cloud-infrastructure/pservers/pserver/junit-test1").build();
        String uri = "/cloud-infrastructure/pservers/pserver/junit-test1";
        String content = "";
        Response response = doRequest(httpEntry, loader, dbEngine, HttpMethod.GET, uri, content);
        dbEngine.commit();
        String msg = response.getEntity().toString();
        JsonObject jsonObj = new JsonParser().parse(msg).getAsJsonObject();
        String resourceVersion = "";
        if ( jsonObj.isJsonObject()) {
        	resourceVersion = jsonObj.get("resource-version").getAsString();
        }
        assertEquals("Expected the pserver to be deleted", 204, doDelete(resourceVersion, "/cloud-infrastructure/pservers/pserver/junit-test1", "pserver"));
    }

    @Test
    public void test7DeleteOnPserverNoPinterface() throws UnsupportedEncodingException, AAIException {

    	
        DBConnectionType type = DBConnectionType.REALTIME;
        HttpEntry httpEntry = new HttpEntry(Version.getLatest(), ModelType.MOXY, queryStyle, type);
        Loader loader = httpEntry.getLoader();
        TransactionalGraphEngine dbEngine = httpEntry.getDbEngine();

        String uri = "/cloud-infrastructure/pservers/pserver/junit-test2";
        String content = "";
        Response response = doRequest(httpEntry, loader, dbEngine, HttpMethod.GET, uri, content);
        dbEngine.commit();
        String msg = response.getEntity().toString();
        JsonObject jsonObj = new JsonParser().parse(msg).getAsJsonObject();
        String resourceVersion = "";
        if ( jsonObj.isJsonObject()) {
        	resourceVersion = jsonObj.get("resource-version").getAsString();
        }
        assertEquals("Expected the pserver to be deleted", 204, doDelete(resourceVersion, "/cloud-infrastructure/pservers/pserver/junit-test2", "pserver"));
    }


    @Test
    public void test8FailedGetOnPserver() throws UnsupportedEncodingException, AAIException {

        DBConnectionType type = DBConnectionType.REALTIME;
        HttpEntry httpEntry = new HttpEntry(Version.getLatest(), ModelType.MOXY, queryStyle, type);
        Loader loader = httpEntry.getLoader();
        TransactionalGraphEngine dbEngine = httpEntry.getDbEngine();

        String uri = "/cloud-infrastructure/pservers/pserver/junit-test2";
        String content = "";
        Response response = doRequest(httpEntry, loader, dbEngine, HttpMethod.GET, uri, content);
        dbEngine.commit();

        assertEquals("Expected the pserver to be deleted", 404, response.getStatus());
    }

    @Test
    public void putEdgeTest() throws UnsupportedEncodingException, AAIException {

        DBConnectionType type = DBConnectionType.REALTIME;
        HttpEntry httpEntry = new HttpEntry(Version.getLatest(), ModelType.MOXY, queryStyle, type);
        Loader loader = httpEntry.getLoader();
        TransactionalGraphEngine dbEngine = httpEntry.getDbEngine();

        //Put pserver
        String uri = "/cloud-infrastructure/pservers/pserver/junit-edge-test-pserver";
        String content = "{\"hostname\":\"junit-edge-test-pserver\"}";
        doRequest(httpEntry, loader, dbEngine, HttpMethod.PUT, uri, content);
        //Put complex
        uri = "/cloud-infrastructure/complexes/complex/junit-edge-test-complex";
        content = "{\"physical-location-id\":\"junit-edge-test-complex\",\"physical-location-type\":\"AAIDefault\",\"street1\":\"AAIDefault\",\"city\":\"AAIDefault\",\"state\":\"NJ\",\"postal-code\":\"07748\",\"country\":\"USA\",\"region\":\"US\"}";
        doRequest(httpEntry, loader, dbEngine, HttpMethod.PUT, uri, content);

        //PutEdge
        uri = "/cloud-infrastructure/complexes/complex/junit-edge-test-complex/relationship-list/relationship";
        content = "{\"related-to\":\"pserver\",\"related-link\":\"/aai/" + Version.getLatest().toString() + "/cloud-infrastructure/pservers/pserver/junit-edge-test-pserver\",\"relationship-label\":\"org.onap.relationships.inventory.LocatedIn\"}";
        Response response = doRequest(httpEntry, loader, dbEngine, HttpMethod.PUT_EDGE, uri, content);

        dbEngine.rollback();
        //System.out.println(response.getEntity().toString());
        assertEquals("Expected the pserver relationship to be created", 200, response.getStatus());
    }

    @Test
    public void putEdgeWrongLabelTest() throws UnsupportedEncodingException, AAIException {

        DBConnectionType type = DBConnectionType.REALTIME;
        HttpEntry httpEntry = new HttpEntry(Version.getLatest(), ModelType.MOXY, queryStyle, type);
        Loader loader = httpEntry.getLoader();
        TransactionalGraphEngine dbEngine = httpEntry.getDbEngine();

        //Put pserver
        String uri = "/cloud-infrastructure/pservers/pserver/junit-edge-test-pserver";
        String content = "{\"hostname\":\"junit-edge-test-pserver\"}";
        doRequest(httpEntry, loader, dbEngine, HttpMethod.PUT, uri, content);
        //Put complex
        uri = "/cloud-infrastructure/complexes/complex/junit-edge-test-complex";
        content = "{\"physical-location-id\":\"junit-edge-test-complex\",\"physical-location-type\":\"AAIDefault\",\"street1\":\"AAIDefault\",\"city\":\"AAIDefault\",\"state\":\"NJ\",\"postal-code\":\"07748\",\"country\":\"USA\",\"region\":\"US\"}";
        doRequest(httpEntry, loader, dbEngine, HttpMethod.PUT, uri, content);

        //PutEdge
        uri = "/cloud-infrastructure/complexes/complex/junit-edge-test-complex/relationship-list/relationship";
        content = "{\"related-to\":\"pserver\",\"related-link\":\"/aai/" + Version.getLatest().toString() + "/cloud-infrastructure/pservers/pserver/junit-edge-test-pserver\",\"relationship-label\":\"junk\"}";
        Response response = doRequest(httpEntry, loader, dbEngine, HttpMethod.PUT_EDGE, uri, content);

        dbEngine.rollback();
        String msg = response.getEntity().toString();
        assertEquals("Expected the pserver to be created", 400, response.getStatus());
        assertThat(msg, containsString("ERR.5.4.6107"));
        assertThat(msg, containsString("Required Edge-property not found in input data:no COUSIN edge rule between complex and pserver with label junk"));

    }

    @Test
    public void relatedToTest() throws UnsupportedEncodingException, AAIException {

        DBConnectionType type = DBConnectionType.REALTIME;
        HttpEntry httpEntry = new HttpEntry(Version.getLatest(), ModelType.MOXY, queryStyle, type);
        Loader loader = httpEntry.getLoader();
        TransactionalGraphEngine dbEngine = httpEntry.getDbEngine();

        //Put pserver
        String uri = "/cloud-infrastructure/pservers/pserver/junit-edge-test-pserver";
        String content = "{\"hostname\":\"junit-edge-test-pserver\"}";
        doRequest(httpEntry, loader, dbEngine, HttpMethod.PUT, uri, content);
        //Put complex
        uri = "/cloud-infrastructure/complexes/complex/junit-edge-test-complex";
        content = "{\"physical-location-id\":\"junit-edge-test-complex\",\"physical-location-type\":\"AAIDefault\",\"street1\":\"AAIDefault\",\"city\":\"AAIDefault\",\"state\":\"NJ\",\"postal-code\":\"07748\",\"country\":\"USA\",\"region\":\"US\"}";
        doRequest(httpEntry, loader, dbEngine, HttpMethod.PUT, uri, content);

        //PutEdge
        uri = "/cloud-infrastructure/complexes/complex/junit-edge-test-complex/relationship-list/relationship";
        content = "{\"related-to\":\"pserver\",\"related-link\":\"/aai/" + Version.getLatest().toString() + "/cloud-infrastructure/pservers/pserver/junit-edge-test-pserver\",\"relationship-label\":\"org.onap.relationships.inventory.LocatedIn\"}";
        doRequest(httpEntry, loader, dbEngine, HttpMethod.PUT_EDGE, uri, content);

        //getRelatedTo
        uri = "/cloud-infrastructure/complexes/complex/junit-edge-test-complex/related-to/pservers";
        content = "";
        Response response = doRequest(httpEntry, loader, dbEngine, HttpMethod.GET, uri, content);
        String respBody = response.getEntity().toString();

        dbEngine.rollback();
        assertEquals("Expected the pserver to be created", 200, response.getStatus());
        assertThat("Related to pserver is returned.", respBody, containsString("\"hostname\":\"junit-edge-test-pserver\""));
    }
}