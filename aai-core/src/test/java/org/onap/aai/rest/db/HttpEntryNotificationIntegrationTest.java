/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2024 Deutsche Telekom. All rights reserved.
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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.AAISetup;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.query.builder.QueryOptions;
import org.onap.aai.rest.notification.UEBNotification;
import org.onap.aai.restcore.HttpMethod;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class HttpEntryNotificationIntegrationTest extends AAISetup {

  private static final MediaType APPLICATION_JSON = MediaType.valueOf("application/json");
  private GraphBinding binding;
  private Loader loader;
  private TransactionalGraphEngine dbEngine;
  private GraphTraversalSource traversal;
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

    binding = graphSessionFactory.bind(ModelType.MOXY, QueryStyle.TRAVERSAL, schemaVersions.getDefaultVersion());
    binding.startTransaction();
    loader = binding.loader();
    dbEngine = binding.dbEngine();
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
    binding.rollback();
  }

  @Test
  public void notificationOnRelatedToTest() throws UnsupportedEncodingException, AAIException {

    UEBNotification uebNotification = Mockito.spy(new UEBNotification(loaderFactory, schemaVersions));
    GraphBinding binding =
        graphSessionFactory.bind(ModelType.MOXY, QueryStyle.TRAVERSAL, schemaVersions.getDefaultVersion());
    binding.startTransaction();

    // Put pserver
    String uri = "/cloud-infrastructure/pservers/pserver/junit-edge-test-pserver";
    String content = "{\"hostname\":\"junit-edge-test-pserver\"}";
    doRequest(binding, uebNotification, HttpMethod.PUT, uri, content);
    // Put complex
    uri = "/cloud-infrastructure/complexes/complex/junit-edge-test-complex";
    content = "{\"physical-location-id\":\"junit-edge-test-complex\",\"physical-location-type\":\"AAIDefault\",\"street1\":\"AAIDefault\",\"city\":\"AAIDefault\",\"state\":\"NJ\",\"postal-code\":\"07748\",\"country\":\"USA\",\"region\":\"US\"}";
    doRequest(binding, uebNotification, HttpMethod.PUT, uri, content);

    // PutEdge
    uri = "/cloud-infrastructure/complexes/complex/junit-edge-test-complex/relationship-list/relationship";
    content = "{\"related-to\":\"pserver\",\"related-link\":\"/aai/" + schemaVersions.getDefaultVersion().toString()
        + "/cloud-infrastructure/pservers/pserver/junit-edge-test-pserver\",\"relationship-label\":\"org.onap.relationships.inventory.LocatedIn\"}";

    Response response = doRequest(binding, uebNotification, HttpMethod.PUT_EDGE, uri,
        content);

    assertEquals("Expected the pserver relationship to be deleted", 200, response.getStatus());
    assertEquals("Two notifications", 2, uebNotification.getEvents().size());
    assertEquals("Notification generated for PUT edge", "UPDATE",
        uebNotification.getEvents().get(1).getEventHeader().getAction());
    assertThat("Event body for the edge create has the related to",
        uebNotification.getEvents().get(1).getEntity().toString(),
        containsString("cloud-infrastructure/pservers/pserver/junit-edge-test-pserver"));

    response = doRequest(binding, uebNotification, HttpMethod.DELETE_EDGE, uri, content);
    assertEquals("Expected the pserver relationship to be deleted", 204, response.getStatus());
    assertEquals("Two notifications", 2, uebNotification.getEvents().size());
    assertEquals("Notification generated for DELETE edge", "UPDATE",
        uebNotification.getEvents().get(0).getEventHeader().getAction());
    assertThat("Event body for the edge delete does not have the related to",
        uebNotification.getEvents().get(0).getEntity().toString(),
        not(containsString("cloud-infrastructure/pservers/pserver/junit-edge-test-pserver")));
    binding.rollback();

  }

  private Response doRequest(GraphBinding binding, UEBNotification notification, HttpMethod method,
      String uri, String requestBody) throws UnsupportedEncodingException, AAIException {
    return doRequest(binding, notification, method, uri, requestBody, null);
  }

  private Response doRequest(GraphBinding binding, UEBNotification notification, HttpMethod method,
      String uri, String requestBody, QueryOptions queryOptions) throws UnsupportedEncodingException, AAIException {
    URI uriObject = UriBuilder.fromPath(uri).build();
    QueryParser uriQuery = binding.dbEngine().getQueryBuilder().createQueryFromURI(uriObject);
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
      obj = binding.loader().introspectorFromName(objType);
    } else {
      obj = binding.loader().unmarshal(objType, requestBody,
          org.onap.aai.restcore.MediaType.getEnum("application/json"));
    }

    DBRequest.Builder builder = new DBRequest.Builder(method, uriObject, uriQuery, obj, httpHeaders, uriInfo,
        "JUNIT-TRANSACTION");
    DBRequest dbRequest = requestBody != null
        ? builder.rawRequestContent(requestBody).build()
        : builder.build();

    List<DBRequest> dbRequestList = Collections.singletonList(dbRequest);

    ProcessResult result = dbRequestProcessor.process(binding, dbRequestList,
        ProcessOptions.forSourceOfTruth("JUNIT").notification(notification).queryOptions(queryOptions).build());
    return result.first().response();
  }
}
