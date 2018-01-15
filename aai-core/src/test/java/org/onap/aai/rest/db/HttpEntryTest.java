/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai.rest.db;

import org.javatuples.Pair;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.AAISetup;
import org.onap.aai.dbmap.DBConnectionType;
import org.onap.aai.domain.yang.Pserver;
import org.onap.aai.domain.yang.Pservers;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.*;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.restcore.HttpMethod;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;

import javax.ws.rs.core.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HttpEntryTest extends AAISetup {

    protected static final MediaType APPLICATION_JSON = MediaType.valueOf("application/json");

    private static final Set<Integer> VALID_HTTP_STATUS_CODES = new HashSet<>();

    static {
        VALID_HTTP_STATUS_CODES.add(200);
        VALID_HTTP_STATUS_CODES.add(201);
        VALID_HTTP_STATUS_CODES.add(204);
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

    
    @Test
    public void test1PutOnPserver() throws UnsupportedEncodingException, AAIException {

        DBConnectionType type = DBConnectionType.REALTIME;
        HttpEntry httpEntry = new HttpEntry(Version.getLatest(), ModelType.MOXY, QueryStyle.TRAVERSAL, type);
        Loader loader = httpEntry.getLoader();
        TransactionalGraphEngine dbEngine = httpEntry.getDbEngine();

        URI uriObject = UriBuilder.fromPath("/cloud-infrastructure/pservers/pserver/junit-test1").build();

        QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(uriObject);

        String content = "{\"hostname\":\"junit-test1\"}";

        Introspector obj = loader.unmarshal("pserver", content, org.onap.aai.restcore.MediaType.getEnum("application/json"));

        DBRequest dbRequest =
                new DBRequest.Builder(HttpMethod.PUT, uriObject, uriQuery, obj, httpHeaders, uriInfo, "JUNIT-TRANSACTION")
                        .rawRequestContent(content).build();

        List<DBRequest> dbRequestList = new ArrayList<>();
        dbRequestList.add(dbRequest);

        Pair<Boolean, List<Pair<URI, Response>>> responsesTuple  = httpEntry.process(dbRequestList, "JUNIT");
        Response response = responsesTuple.getValue1().get(0).getValue1();
        dbEngine.commit();
        assertEquals("Expected the pserver to be created", 201, response.getStatus());
    }

    @Test
    public void test2GetOnPserver() throws UnsupportedEncodingException, AAIException {

        DBConnectionType type = DBConnectionType.REALTIME;
        HttpEntry httpEntry = new HttpEntry(Version.getLatest(), ModelType.MOXY, QueryStyle.TRAVERSAL, type);
        Loader loader = httpEntry.getLoader();
        TransactionalGraphEngine dbEngine = httpEntry.getDbEngine();

        URI uriObject = UriBuilder.fromPath("/cloud-infrastructure/pservers/pserver/junit-test1").build();

        QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(uriObject);

        String content = "";

        Introspector obj = loader.introspectorFromName("pserver");

        DBRequest dbRequest =
                new DBRequest.Builder(HttpMethod.GET, uriObject, uriQuery, obj, httpHeaders, uriInfo, "JUNIT-TRANSACTION")
                        .rawRequestContent(content).build();

        List<DBRequest> dbRequestList = new ArrayList<>();
        dbRequestList.add(dbRequest);

        Pair<Boolean, List<Pair<URI, Response>>> responsesTuple  = httpEntry.process(dbRequestList, "JUNIT");
        Response response = responsesTuple.getValue1().get(0).getValue1();
        dbEngine.commit();
        assertEquals("Expected the pserver to be returned", 200, response.getStatus());
    }
  
    @Test
    public void test3MergePatchOnPserver() throws UnsupportedEncodingException, AAIException {

        DBConnectionType type = DBConnectionType.REALTIME;
        HttpEntry httpEntry = new HttpEntry(Version.getLatest(), ModelType.MOXY, QueryStyle.TRAVERSAL, type);
        Loader loader = httpEntry.getLoader();
        TransactionalGraphEngine dbEngine = httpEntry.getDbEngine();

        URI uriObject = UriBuilder.fromPath("/cloud-infrastructure/pservers/pserver/junit-test1").build();

        QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(uriObject);

        String content = "{\"hostname\":\"junit-test1\", \"equip-type\":\"junit-equip-type\"}";

        Introspector obj = loader.unmarshal("pserver", content, org.onap.aai.restcore.MediaType.getEnum("application/json"));

        DBRequest dbRequest =
                new DBRequest.Builder(HttpMethod.MERGE_PATCH, uriObject, uriQuery, obj, httpHeaders, uriInfo, "JUNIT-TRANSACTION")
                        .rawRequestContent(content).build();

        List<DBRequest> dbRequestList = new ArrayList<>();
        dbRequestList.add(dbRequest);

        Pair<Boolean, List<Pair<URI, Response>>> responsesTuple  = httpEntry.process(dbRequestList, "JUNIT");
        Response response = responsesTuple.getValue1().get(0).getValue1();
        dbEngine.commit();
        assertEquals("Expected the pserver to be updated", 200, response.getStatus());
    }
    
    private int doDelete(String resourceVersion) throws UnsupportedEncodingException, AAIException {
    	queryParameters.add("resource-version", resourceVersion);
    	DBConnectionType type = DBConnectionType.REALTIME;
        HttpEntry httpEntry = new HttpEntry(Version.getLatest(), ModelType.MOXY, QueryStyle.TRAVERSAL, type);
        Loader loader = httpEntry.getLoader();
        TransactionalGraphEngine dbEngine = httpEntry.getDbEngine();

        URI uriObject = UriBuilder.fromPath("/cloud-infrastructure/pservers/pserver/junit-test1").build();

        QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(uriObject);

        String content = "";

        Introspector obj = loader.introspectorFromName("pserver");

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
    public void test4DeleteOnPserver() throws UnsupportedEncodingException, AAIException {

        DBConnectionType type = DBConnectionType.REALTIME;
        HttpEntry httpEntry = new HttpEntry(Version.getLatest(), ModelType.MOXY, QueryStyle.TRAVERSAL, type);
        Loader loader = httpEntry.getLoader();
        TransactionalGraphEngine dbEngine = httpEntry.getDbEngine();

        URI uriObject = UriBuilder.fromPath("/cloud-infrastructure/pservers/pserver/junit-test1").build();

        QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(uriObject);

        String content = "";

        Introspector obj = loader.introspectorFromName("pserver");

        DBRequest dbRequest =
                new DBRequest.Builder(HttpMethod.GET, uriObject, uriQuery, obj, httpHeaders, uriInfo, "JUNIT-TRANSACTION")
                        .rawRequestContent(content).build();

        List<DBRequest> dbRequestList = new ArrayList<>();
        dbRequestList.add(dbRequest);

        Pair<Boolean, List<Pair<URI, Response>>> responsesTuple  = httpEntry.process(dbRequestList, "JUNIT");
        Response response = responsesTuple.getValue1().get(0).getValue1();
        dbEngine.commit();
        String msg = response.getEntity().toString();
        JsonObject jsonObj = new JsonParser().parse(msg).getAsJsonObject();
        String resourceVersion = "";
        if ( jsonObj.isJsonObject()) {
        	resourceVersion = jsonObj.get("resource-version").getAsString();
        }
        assertEquals("Expected the pserver to be deleted", 204, doDelete(resourceVersion));
    }

    @Test
    public void test5FailedGetOnPserver() throws UnsupportedEncodingException, AAIException {

        DBConnectionType type = DBConnectionType.REALTIME;
        HttpEntry httpEntry = new HttpEntry(Version.getLatest(), ModelType.MOXY, QueryStyle.TRAVERSAL, type);
        Loader loader = httpEntry.getLoader();
        TransactionalGraphEngine dbEngine = httpEntry.getDbEngine();

        URI uriObject = UriBuilder.fromPath("/cloud-infrastructure/pservers/pserver/junit-test2").build();

        QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(uriObject);

        String content = "";

        Introspector obj = loader.introspectorFromName("pserver");

        DBRequest dbRequest =
                new DBRequest.Builder(HttpMethod.GET, uriObject, uriQuery, obj, httpHeaders, uriInfo, "JUNIT-TRANSACTION")
                        .rawRequestContent(content).build();

        List<DBRequest> dbRequestList = new ArrayList<>();
        dbRequestList.add(dbRequest);

        Pair<Boolean, List<Pair<URI, Response>>> responsesTuple  = httpEntry.process(dbRequestList, "JUNIT");
        Response response = responsesTuple.getValue1().get(0).getValue1();
        dbEngine.commit();

        assertEquals("Expected the pserver to be deleted", 404, response.getStatus());
    }
}
