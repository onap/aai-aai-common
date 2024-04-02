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

package org.onap.aai.rest;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraphTransaction;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.onap.aai.AAISetup;
import org.onap.aai.HttpTestUtil;
import org.onap.aai.PayloadUtil;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.dbmap.AAIGraph;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.rest.ueb.NotificationEvent;
import org.onap.aai.rest.ueb.UEBNotification;
import org.onap.aai.serialization.engines.QueryStyle;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ImpliedDeleteIntegrationTest extends AAISetup {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImpliedDeleteIntegrationTest.class);
    public QueryStyle queryStyle;

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {{QueryStyle.TRAVERSAL}, {QueryStyle.TRAVERSAL_URI}});
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void testPutPserverWithMultiplePInterfaceChildrenAndDoPutWithZeroChildren(QueryStyle queryStyle) throws Exception {

        initImpliedDeleteIntegrationTest(queryStyle);

        String uri = "/aai/v12/cloud-infrastructure/pservers/pserver/test-pserver-implied-delete";

        UEBNotification notification = Mockito.spy(new UEBNotification(ModelType.MOXY, loaderFactory, schemaVersions));
        HttpTestUtil httpTestUtil = new HttpTestUtil(queryStyle, notification, AAIProperties.MINIMUM_DEPTH);

        String resource = PayloadUtil.getResourcePayload("pserver-implied-delete.json");

        Response response = httpTestUtil.doGet(uri);
        assertEquals(404, response.getStatus(), "Expecting the pserver to be not found");

        response = httpTestUtil.doPut(uri, resource);
        assertEquals(201, response.getStatus(), "Expecting the pserver to be created");

        response = httpTestUtil.doGet(uri);
        assertEquals(200, response.getStatus(), "Expecting the pserver to be found");

        JSONObject jsonObject = new JSONObject(response.getEntity().toString());
        JSONAssert.assertEquals(resource, response.getEntity().toString(), false);
        jsonObject.getJSONObject("p-interfaces").remove("p-interface");

        notification = Mockito.spy(new UEBNotification(ModelType.MOXY, loaderFactory, schemaVersions));
        httpTestUtil = new HttpTestUtil(queryStyle, notification, AAIProperties.MINIMUM_DEPTH);

        response = httpTestUtil.doPut(uri, jsonObject.toString());
        assertEquals(200, response.getStatus(), "Expecting the pserver to be updated and delete children");

        List<NotificationEvent> notificationEvents = notification.getEvents();
        assertThat(notificationEvents.size(), is(5));

        List<String> notificationEventHeaders = notification.getEvents().stream()
                .map(event -> event.getEventHeader().marshal(false)).collect(Collectors.toList());

        Long deletedEventsCount = notificationEventHeaders.stream().filter(e -> e.contains("\"DELETE\"")).count();

        assertThat(deletedEventsCount, is(4L));

        response = httpTestUtil.doGet(uri);
        assertThat(response.getEntity().toString(), not(containsString("p-interface")));
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void testPutGenericVnf(QueryStyle queryStyle) throws Exception {

        initImpliedDeleteIntegrationTest(queryStyle);

        String uri = "/aai/v12/network/generic-vnfs/generic-vnf/generic-vnf-implied-delete";
        HttpTestUtil httpTestUtil = new HttpTestUtil(queryStyle);

        String resource = PayloadUtil.getResourcePayload("generic-vnf-implied-delete.json");

        Response response = httpTestUtil.doGet(uri);
        assertEquals(404, response.getStatus(), "Expecting the generic-vnf to be not found");

        response = httpTestUtil.doPut(uri, resource);
        assertEquals(201, response.getStatus(), "Expecting the generic-vnf to be created");

        response = httpTestUtil.doGet(uri);
        assertEquals(200, response.getStatus(), "Expecting the generic-vnf to be found");

        JSONObject jsonObject = new JSONObject(response.getEntity().toString());
        JSONAssert.assertEquals(resource, response.getEntity().toString(), false);
        jsonObject.getJSONObject("vf-modules").remove("vf-module");

        response = httpTestUtil.doPut(uri, jsonObject.toString());
        assertEquals(403, response.getStatus(), "Expecting the generic-vnf to be not deleted and fail with 403");
        assertThat(response.getEntity().toString(), containsString("User is not allowed to perform implicit delete"));
    }

    @AfterEach
    public void tearDown() {

        JanusGraphTransaction transaction = AAIGraph.getInstance().getGraph().newTransaction();
        boolean success = true;

        try {

            GraphTraversalSource g = transaction.traversal();

            g.V().has("source-of-truth", "JUNIT").toList().forEach(v -> v.remove());

        } catch (Exception ex) {
            success = false;
            LOGGER.error("Unable to remove the vertexes", ex);
        } finally {
            if (success) {
                transaction.commit();
            } else {
                transaction.rollback();
                fail("Unable to teardown the graph");
            }
        }
    }

    public void initImpliedDeleteIntegrationTest(QueryStyle queryStyle) {
        this.queryStyle = queryStyle;
    }

}
