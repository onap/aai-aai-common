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

package org.onap.aai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.javatuples.Pair;
import org.mockito.Mockito;
import org.onap.aai.config.SpringContextAware;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.parsers.uri.URIToObject;
import org.onap.aai.rest.db.DBRequest;
import org.onap.aai.rest.db.HttpEntry;
import org.onap.aai.rest.ueb.UEBNotification;
import org.onap.aai.restcore.HttpMethod;
import org.onap.aai.restcore.RESTAPI;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;

import javax.ws.rs.core.*;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.*;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

public class HttpTestUtil extends RESTAPI {

    protected HttpEntry traversalHttpEntry;

    protected HttpEntry traversalUriHttpEntry;

    private static final Logger logger = LoggerFactory.getLogger(HttpTestUtil.class);

    protected static final MediaType APPLICATION_JSON = MediaType.valueOf("application/json");
    protected static final MediaType APPLICATION_XML = MediaType.valueOf("application/xml");

    private static final String EMPTY = "";
    private final QueryStyle queryStyle;

    protected HttpHeaders httpHeaders;
    protected UriInfo uriInfo;

    protected MultivaluedMap<String, String> headersMultiMap;
    protected MultivaluedMap<String, String> queryParameters;

    protected List<String> aaiRequestContextList;
    protected List<MediaType> outputMediaTypes;
    protected LoaderFactory loaderFactory;
    protected SchemaVersions schemaVersions;
    protected UEBNotification notification;
    protected int notificationDepth;
    protected String acceptType;

    public HttpTestUtil(QueryStyle qs) {
        this(qs, "application/json");
    }

    public HttpTestUtil(QueryStyle qs, String acceptType) {
        this.queryStyle = qs;
        traversalHttpEntry = SpringContextAware.getBean("traversalUriHttpEntry", HttpEntry.class);
        traversalUriHttpEntry = SpringContextAware.getBean("traversalUriHttpEntry", HttpEntry.class);
        loaderFactory = SpringContextAware.getBean(LoaderFactory.class);
        schemaVersions = (SchemaVersions) SpringContextAware.getBean("schemaVersions");
        notification = null;
        this.acceptType = acceptType;
    }


    public HttpTestUtil(QueryStyle qs, UEBNotification uebNotification, int notificationDepth) {
        this(qs, uebNotification, notificationDepth, "application/json");
    }

    public HttpTestUtil(QueryStyle qs, UEBNotification uebNotification, int notificationDepth, String acceptType) {
        this.queryStyle = qs;
        this.traversalHttpEntry = SpringContextAware.getBean("traversalUriHttpEntry", HttpEntry.class);
        this.traversalUriHttpEntry = SpringContextAware.getBean("traversalUriHttpEntry", HttpEntry.class);
        this.loaderFactory = SpringContextAware.getBean(LoaderFactory.class);
        this.schemaVersions = (SchemaVersions) SpringContextAware.getBean("schemaVersions");
        this.notification = uebNotification;
        this.notificationDepth = notificationDepth;
        this.acceptType = acceptType;
    }

    public void init() {

        httpHeaders = Mockito.mock(HttpHeaders.class);
        uriInfo = Mockito.mock(UriInfo.class);

        headersMultiMap = new MultivaluedHashMap<>();
        queryParameters = Mockito.spy(new MultivaluedHashMap<>());

        headersMultiMap.add("X-FromAppId", "JUNIT");
        headersMultiMap.add("X-TransactionId", UUID.randomUUID().toString());
        headersMultiMap.add("Real-Time", "true");
        headersMultiMap.add("Accept", acceptType);
        headersMultiMap.add("aai-request-context", "");

        outputMediaTypes = new ArrayList<>();
        if(acceptType.equals("application/json")){
            outputMediaTypes.add(APPLICATION_JSON);
        } else {
            outputMediaTypes.add(APPLICATION_XML);
        }

        aaiRequestContextList = new ArrayList<>();
        aaiRequestContextList.add("");

        when(httpHeaders.getRequestHeaders()).thenReturn(headersMultiMap);
        when(httpHeaders.getAcceptableMediaTypes()).thenReturn(outputMediaTypes);

        when(httpHeaders.getRequestHeader("aai-request-context")).thenReturn(aaiRequestContextList);

        when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
        when(uriInfo.getQueryParameters(false)).thenReturn(queryParameters);

        doReturn(null).when(queryParameters).remove(anyObject());
        when(httpHeaders.getMediaType()).thenReturn(APPLICATION_JSON);

        try {
            if(notification != null){
                doNothing().when(notification).triggerEvents();
            }
        } catch (AAIException e) {
            e.printStackTrace();
        }
    }

    public Response doPut(String uri, String payload) throws UnsupportedEncodingException, AAIException {
        Map<String, String> puts = new HashMap<>();
        puts.put(uri, payload);
        return this.doPut(puts);
    }

    public Response doPut(Map<String, String> uriPayload) throws UnsupportedEncodingException, AAIException {

        this.init();
        Response response = null;
        boolean success = true;
        TransactionalGraphEngine dbEngine = null;

        try {

            List<DBRequest> dbRequestList = new ArrayList<>();
            for(Map.Entry<String, String> entry : uriPayload.entrySet()){

                String uri = entry.getKey();
                String payload = entry.getValue();
                if (uri.startsWith("/aai/")) {
                    uri = uri.substring(5);
                }

                logger.info("Starting the put request for the uri {} with payload {}", uri, payload);

                String[] arr = uri.split("/");

                SchemaVersion version = null;

                if (arr.length > 1) {
                    if (arr[0].matches("^v\\d+")) {
                        version = new SchemaVersion(arr[0]);
                        uri = uri.replaceAll("^v\\d+", "");
                    }
                }

                if (version == null) {
                    version = schemaVersions.getDefaultVersion();
                }
                Mockito.when(uriInfo.getPath()).thenReturn(uri);

                if(notification != null){
                    traversalHttpEntry.setHttpEntryProperties(version, notification, notificationDepth);
                } else {
                    traversalHttpEntry.setHttpEntryProperties(version);
                }
                Loader loader = traversalHttpEntry.getLoader();
                dbEngine = traversalHttpEntry.getDbEngine();

                URI uriObject = UriBuilder.fromPath(uri).build();
                URIToObject uriToObject = new URIToObject(loader, uriObject);

                String objType = uriToObject.getEntityName();
                QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(uriObject);

                logger.info("Unmarshalling the payload to this {}", objType);

                Introspector obj;
                HttpMethod httpMethod;
                if (uri.contains("/relationship-list/relationship")) {
                    obj = loader.unmarshal("relationship", payload,
                        org.onap.aai.restcore.MediaType.getEnum("application/json"));
                    httpMethod = HttpMethod.PUT_EDGE;
                } else {
                    obj = loader.unmarshal(objType, payload, org.onap.aai.restcore.MediaType.getEnum("application/json"));
                    httpMethod = HttpMethod.PUT;
                    this.validateIntrospector(obj, loader, uriObject, httpMethod);
                }

                DBRequest dbRequest = new DBRequest.Builder(httpMethod, uriObject, uriQuery, obj, httpHeaders, uriInfo,
                    "JUNIT-TRANSACTION").rawRequestContent(payload).build();

                dbRequestList.add(dbRequest);

            }

            Pair<Boolean, List<Pair<URI, Response>>> responsesTuple =
                traversalHttpEntry.process(dbRequestList, "JUNIT");
            response = responsesTuple.getValue1().get(0).getValue1();

        } catch (AAIException e) {
            response = this.consumerExceptionResponseGenerator(httpHeaders, uriInfo, HttpMethod.PUT, e);
            success = false;
        } catch (Exception e) {
            AAIException ex = new AAIException("AAI_4000", e);
            response = this.consumerExceptionResponseGenerator(httpHeaders, uriInfo, HttpMethod.PUT, ex);
            success = false;
        } finally {
            if (success) {
                if (response != null) {
                    if ((response.getStatus() / 100) == 2) {
                        logger.info("Successfully completed the PUT request with status {} and committing it to DB",
                            response.getStatus());
                    } else {
                        logFailure(HttpMethod.PUT, response);
                    }
                }
                dbEngine.commit();
            } else {
                if (response != null) {
                    logFailure(HttpMethod.PUT, response);
                }
                dbEngine.rollback();
            }
        }

        return response;
    }

    public Response doPatch(String uri, String payload) throws UnsupportedEncodingException, AAIException {

        this.init();
        Response response = null;
        boolean success = true;
        TransactionalGraphEngine dbEngine = null;

        try {

            if (uri.startsWith("/aai/")) {
                uri = uri.substring(5);
            }

            logger.info("Starting the put request for the uri {} with payload {}", uri, payload);

            String[] arr = uri.split("/");

            SchemaVersion version = null;

            if (arr.length > 1) {
                if (arr[0].matches("^v\\d+")) {
                    version = new SchemaVersion(arr[0]);
                    uri = uri.replaceAll("^v\\d+", "");
                }
            }

            if (version == null) {
                version = schemaVersions.getDefaultVersion();
            }
            Mockito.when(uriInfo.getPath()).thenReturn(uri);

            if(notification != null){
                traversalHttpEntry.setHttpEntryProperties(version, notification, notificationDepth);
            } else {
                traversalHttpEntry.setHttpEntryProperties(version);
            }
            Loader loader = traversalHttpEntry.getLoader();
            dbEngine = traversalHttpEntry.getDbEngine();

            URI uriObject = UriBuilder.fromPath(uri).build();
            URIToObject uriToObject = new URIToObject(loader, uriObject);

            String objType = uriToObject.getEntityName();
            QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(uriObject);

            logger.info("Unmarshalling the payload to this {}", objType);

            Introspector obj;
            HttpMethod httpMethod;
            obj = loader.unmarshal(objType, payload, org.onap.aai.restcore.MediaType.getEnum("application/json"));
            httpMethod = HttpMethod.MERGE_PATCH;
            this.validateIntrospector(obj, loader, uriObject, httpMethod);

            DBRequest dbRequest = new DBRequest.Builder(httpMethod, uriObject, uriQuery, obj, httpHeaders, uriInfo,
                "JUNIT-TRANSACTION").rawRequestContent(payload).build();

            List<DBRequest> dbRequestList = new ArrayList<>();
            dbRequestList.add(dbRequest);

            Pair<Boolean, List<Pair<URI, Response>>> responsesTuple =
                traversalHttpEntry.process(dbRequestList, "JUNIT");
            response = responsesTuple.getValue1().get(0).getValue1();

        } catch (AAIException e) {
            response = this.consumerExceptionResponseGenerator(httpHeaders, uriInfo, HttpMethod.PUT, e);
            success = false;
        } catch (Exception e) {
            AAIException ex = new AAIException("AAI_4000", e);
            response = this.consumerExceptionResponseGenerator(httpHeaders, uriInfo, HttpMethod.PUT, ex);
            success = false;
        } finally {
            if (success) {
                if (response != null) {
                    if ((response.getStatus() / 100) == 2) {
                        logger.info("Successfully completed the PUT request with status {} and committing it to DB",
                            response.getStatus());
                    } else {
                        logFailure(HttpMethod.PUT, response);
                    }
                }
                dbEngine.commit();
            } else {
                if (response != null) {
                    logFailure(HttpMethod.PUT, response);
                }
                dbEngine.rollback();
            }
        }

        return response;
    }

    public Response doGet(String uri, String depth){
       return doGet(uri, depth, null);
    }

    public Response doGet(String uri, String depth, String format) {

        this.init();
        Response response = null;
        boolean success = true;
        TransactionalGraphEngine dbEngine = null;

        try {

            if (uri.startsWith("/aai/")) {
                uri = uri.substring(5);
            }

            logger.info("Starting the GET request for the uri {} with depth {}", uri, depth);

            String[] arr = uri.split("/");

            SchemaVersion version = null;

            if (arr.length > 1) {
                if (arr[0].matches("^v\\d+")) {
                    version = new SchemaVersion(arr[0]);
                    uri = uri.replaceAll("^v\\d+", "");
                }
            }

            if (version == null) {
                version = schemaVersions.getDefaultVersion();
            }

            if(notification != null){
                traversalHttpEntry.setHttpEntryProperties(version, notification, notificationDepth);
            } else {
                traversalHttpEntry.setHttpEntryProperties(version);
            }
            Loader loader = traversalHttpEntry.getLoader();
            dbEngine = traversalHttpEntry.getDbEngine();

            URI uriObject = UriBuilder.fromPath(uri).build();

            if (depth != null) {
                queryParameters.add("depth", depth);
            }

            if(format != null){
                queryParameters.add("format", format);
            }

            QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(uriObject, queryParameters);

            Mockito.when(uriInfo.getPath()).thenReturn(uri);

            URIToObject uriToObject = new URIToObject(loader, uriObject);
            String objType = "";
            if (!uriQuery.getContainerType().equals("")) {
                objType = uriQuery.getContainerType();
            } else {
                objType = uriQuery.getResultType();
            }
            logger.info("Unmarshalling the payload to this {}", objType);

            Introspector obj = loader.introspectorFromName(objType);

            DBRequest dbRequest = new DBRequest.Builder(HttpMethod.GET, uriObject, uriQuery, obj, httpHeaders, uriInfo,
                    "JUNIT-TRANSACTION").build();

            List<DBRequest> dbRequestList = new ArrayList<>();
            dbRequestList.add(dbRequest);

            Pair<Boolean, List<Pair<URI, Response>>> responsesTuple =
                    traversalHttpEntry.process(dbRequestList, "JUNIT");
            response = responsesTuple.getValue1().get(0).getValue1();

        } catch (AAIException e) {
            response = this.consumerExceptionResponseGenerator(httpHeaders, uriInfo, HttpMethod.PUT, e);
            success = false;
        } catch (Exception e) {
            AAIException ex = new AAIException("AAI_4000", e);
            response = this.consumerExceptionResponseGenerator(httpHeaders, uriInfo, HttpMethod.PUT, ex);
            success = false;
        } finally {
            if (success) {
                if (response != null) {
                    if ((response.getStatus() / 100) == 2) {
                        logger.info("Successfully completed the GET request with status {} and committing it to DB",
                                response.getStatus());
                    } else {
                        logFailure(HttpMethod.GET, response);
                    }
                }
                dbEngine.commit();
            } else {
                logFailure(HttpMethod.GET, response);
                dbEngine.rollback();
            }
        }

        return response;
    }

    public Response doGet(String uri) throws UnsupportedEncodingException, AAIException {
        return this.doGet(uri, "all");
    }

    public Response doDelete(Map<String, Pair<String, String>> deletes){

        this.init();
        Response response = null;
        boolean success = true;
        TransactionalGraphEngine dbEngine = null;

        try {

            List<DBRequest> dbRequestList = new ArrayList<>();
            for (Map.Entry<String, Pair<String, String>> delete : deletes.entrySet()) {
                String uri = delete.getKey();
                String resourceVersion = delete.getValue().getValue0();
                String content = delete.getValue().getValue1();
                uri = uri.replaceAll("/aai/", "");
                logger.info("Starting the delete request for the uri {} with resource version {}", uri, resourceVersion);

                String[] arr = uri.split("/");

                SchemaVersion version = null;

                if (arr.length > 1) {
                    if (arr[0].matches("^v\\d+")) {
                        version = new SchemaVersion(arr[0]);
                        uri = uri.replaceAll("^v\\d+", "");
                    }
                }

                if (version == null) {
                    version = schemaVersions.getDefaultVersion();
                }

                Mockito.when(uriInfo.getPath()).thenReturn(uri);
                if (notification != null) {
                    traversalHttpEntry.setHttpEntryProperties(version, notification, notificationDepth);
                } else {
                    traversalHttpEntry.setHttpEntryProperties(version);
                }
                Loader loader = traversalHttpEntry.getLoader();
                dbEngine = traversalHttpEntry.getDbEngine();

                URI uriObject = UriBuilder.fromPath(uri).build();
                URIToObject uriToObject = new URIToObject(loader, uriObject);

                String objType = uriToObject.getEntityName();
                queryParameters.add("resource-version", resourceVersion);
                QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(uriObject, queryParameters);

                logger.info("Unmarshalling the payload to this {}", objType);

                Introspector obj;

                HttpMethod httpMethod;

                if (uri.contains("/relationship-list/relationship")) {
                    httpMethod = HttpMethod.DELETE_EDGE;
                    obj = loader.unmarshal("relationship", content, org.onap.aai.restcore.MediaType.getEnum("application/json"));
                } else {
                    obj = loader.introspectorFromName(objType);
                    httpMethod = HttpMethod.DELETE;
                }

                DBRequest dbRequest = new DBRequest.Builder(httpMethod, uriObject, uriQuery, obj, httpHeaders, uriInfo, "JUNIT-TRANSACTION").build();


                dbRequestList.add(dbRequest);
            }
            Pair<Boolean, List<Pair<URI, Response>>> responsesTuple =
                traversalHttpEntry.process(dbRequestList, "JUNIT");
            response = responsesTuple.getValue1().get(0).getValue1();

        } catch (AAIException e) {
            response = this.consumerExceptionResponseGenerator(httpHeaders, uriInfo, HttpMethod.DELETE, e);
            success = false;
        } catch (Exception e) {
            AAIException ex = new AAIException("AAI_4000", e);
            response = this.consumerExceptionResponseGenerator(httpHeaders, uriInfo, HttpMethod.DELETE, ex);
            success = false;
        } finally {
            if (success) {
                if (response != null) {
                    if ((response.getStatus() / 100) == 2) {
                        logger.info("Successfully completed the DELETE request with status {} and committing it to DB",
                            response.getStatus());
                    } else {
                        logFailure(HttpMethod.DELETE, response);
                    }
                }
                dbEngine.commit();
            } else {
                logFailure(HttpMethod.DELETE, response);
                dbEngine.rollback();
            }
        }

        return response;
    }

    public Response doDelete(String uri, String resourceVersion) {
        return this.doDelete(uri, resourceVersion, null);
    }
    public Response doDelete(String uri, String resourceVersion, String content) {
        Map<String, Pair<String, String>> deletes = new HashMap<>();
        deletes.put(uri, new Pair<>(resourceVersion, content));
        return this.doDelete(deletes);
    }

    public static void logFailure(HttpMethod httpMethod, Response response) {
        logger.info("Unable to complete the {} request with status {} and rolling back", httpMethod.toString(),
                response.getStatus());
        logger.info("Response body of failed request {}", response.getEntity());

    }
}
