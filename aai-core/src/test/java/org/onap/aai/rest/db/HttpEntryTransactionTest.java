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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraphException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.onap.aai.AAISetup;
import org.onap.aai.config.SpringContextAware;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.restcore.HttpMethod;
import org.onap.aai.restcore.MediaType;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;

import lombok.SneakyThrows;

public class HttpEntryTransactionTest extends AAISetup {

  @Mock UriInfo uriInfoMock;
  @Mock MultivaluedMap<String, String> queryParamsMock;
  @Mock HttpHeaders headersMock;

  @Before
  public void setup() {
    when(uriInfoMock.getQueryParameters(anyBoolean())).thenReturn(queryParamsMock);
    when(queryParamsMock.getFirst("depth")).thenReturn("0");
    when(headersMock.getRequestHeader("aai-request-context")).thenReturn(null);
  }

  @Test
  public void thatDBRequestsAreWritten() throws AAIException, UnsupportedEncodingException {
    HttpEntry httpEntry = SpringContextAware.getBean("requestScopedTraversalUriHttpEntry", HttpEntry.class);
    httpEntry.setHttpEntryProperties(schemaVersions.getDefaultVersion());
    TransactionalGraphEngine dbEngine = httpEntry.getDbEngine();
    Loader loader = httpEntry.getLoader();

    List<String> hostnames = Arrays.asList("test1", "test2", "test3");
    List<DBRequest> dbRequests = createDbRequests(dbEngine, loader, hostnames);

    httpEntry.process(dbRequests, SERVICE_NAME);

    GraphTraversalSource source = dbEngine.startTransaction().traversal();
    Long pserverCount = source.V().has("aai-node-type","pserver").count().next();
    assertEquals(3, pserverCount.intValue());
  }

  @Test
  // same test, only that JanusgraphException is thrown
  public void thatDBRequestsAreRolledBack() throws AAIException, UnsupportedEncodingException {
    HttpEntry httpEntry = SpringContextAware.getBean("requestScopedTraversalUriHttpEntry", HttpEntry.class);
    httpEntry.setHttpEntryProperties(schemaVersions.getDefaultVersion());
    TransactionalGraphEngine dbEngine = httpEntry.getDbEngine();
    Loader loader = httpEntry.getLoader();

    List<String> hostnames = Arrays.asList("test1", "test2", "test3");
    List<DBRequest> dbRequests = createDbRequests(dbEngine, loader, hostnames);

    DBRequest request2Spy = spy(dbRequests.get(1));
    QueryParser uriQuerySpy = spy(dbRequests.get(1).getParser());
    when(request2Spy.getParser()).thenReturn(uriQuerySpy);
    dbRequests.set(1, request2Spy);
    when(uriQuerySpy.getQueryBuilder()).thenThrow(new JanusGraphException(""));

    httpEntry.process(dbRequests, SERVICE_NAME);

    GraphTraversalSource source = dbEngine.startTransaction().traversal();
    Long pserverCount = source.V().has("aai-node-type","pserver").count().next();
    assertEquals(0, pserverCount.intValue());
  }

  @SneakyThrows
  private List<DBRequest> createDbRequests(TransactionalGraphEngine dbEngine, Loader loader, List<String> hostnames) {
    List<DBRequest> dbRequests = new ArrayList<>();

    for(String hostname: hostnames) {
      URI uriObject = UriBuilder.fromPath("/cloud-infrastructure/pservers/pserver/" + hostname).build();
      QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(uriObject);
      String content = "{\"hostname\":\"" + hostname + "\"}";
      Introspector obj = loader.unmarshal(uriQuery.getResultType(), content,MediaType.APPLICATION_JSON_TYPE);
      DBRequest request = new DBRequest.Builder(HttpMethod.PUT, uriObject, uriQuery, obj, headersMock, uriInfoMock, "someTransaction")
                      .rawRequestContent(content).build();
      dbRequests.add(request);
    }

    return dbRequests;
  }
}
