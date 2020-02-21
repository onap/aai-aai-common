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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraphTransaction;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
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
import org.springframework.test.annotation.DirtiesContext;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

@RunWith(value = Parameterized.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ImpliedDeleteIntegrationTest extends AAISetup {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImpliedDeleteIntegrationTest.class);

    @Parameterized.Parameter(value = 0)
    public QueryStyle queryStyle;

    @Parameterized.Parameters(name = "QueryStyle.{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { QueryStyle.TRAVERSAL },
            { QueryStyle.TRAVERSAL_URI }
        });
    }

    @Test
    public void testPutPserverWithMultiplePInterfaceChildrenAndDoPutWithZeroChildren() throws Exception {

        String uri = "/aai/v12/cloud-infrastructure/pservers/pserver/test-pserver-implied-delete";

        UEBNotification notification = Mockito.spy(new UEBNotification(ModelType.MOXY, loaderFactory, schemaVersions));
        HttpTestUtil httpTestUtil = new HttpTestUtil(queryStyle, notification, AAIProperties.MINIMUM_DEPTH);

        String resource = PayloadUtil.getResourcePayload("pserver-implied-delete.json");

        Response response = httpTestUtil.doGet(uri);
        assertEquals("Expecting the pserver to be not found", 404, response.getStatus());

        response = httpTestUtil.doPut(uri, resource);
        assertEquals("Expecting the pserver to be created", 201, response.getStatus());

        response = httpTestUtil.doGet(uri);
        assertEquals("Expecting the pserver to be found", 200, response.getStatus());

        JSONObject jsonObject = new JSONObject(response.getEntity().toString());
        JSONAssert.assertEquals(resource, response.getEntity().toString(), false);
        jsonObject.getJSONObject("p-interfaces").remove("p-interface");

        notification = Mockito.spy(new UEBNotification(ModelType.MOXY, loaderFactory, schemaVersions));
        httpTestUtil = new HttpTestUtil(queryStyle, notification, AAIProperties.MINIMUM_DEPTH);

        response = httpTestUtil.doPut(uri, jsonObject.toString());
        assertEquals("Expecting the pserver to be updated and delete children", 200, response.getStatus());

        List<NotificationEvent> notificationEvents = notification.getEvents();
        assertThat(notificationEvents.size(), is(5));

        List<String> notificationEventHeaders = notification.getEvents()
            .stream()
            .map(event -> event.getEventHeader().marshal(false))
            .collect(Collectors.toList());

        Long deletedEventsCount = notificationEventHeaders.stream().filter(e -> e.contains("\"DELETE\"")).count();

        assertThat(deletedEventsCount, is(4L));

        response = httpTestUtil.doGet(uri);
        assertThat(response.getEntity().toString(), not(containsString("p-interface")));
    }

    @Test
    public void testPutGenericVnf() throws Exception {

        String uri = "/aai/v12/network/generic-vnfs/generic-vnf/generic-vnf-implied-delete";
        HttpTestUtil httpTestUtil = new HttpTestUtil(queryStyle);

        String resource = PayloadUtil.getResourcePayload("generic-vnf-implied-delete.json");

        Response response = httpTestUtil.doGet(uri);
        assertEquals("Expecting the generic-vnf to be not found", 404, response.getStatus());

        response = httpTestUtil.doPut(uri, resource);
        assertEquals("Expecting the generic-vnf to be created", 201, response.getStatus());

        response = httpTestUtil.doGet(uri);
        assertEquals("Expecting the generic-vnf to be found", 200, response.getStatus());

        JSONObject jsonObject = new JSONObject(response.getEntity().toString());
        JSONAssert.assertEquals(resource, response.getEntity().toString(), false);
        jsonObject.getJSONObject("vf-modules").remove("vf-module");

        response = httpTestUtil.doPut(uri, jsonObject.toString());
        assertEquals("Expecting the generic-vnf to be not deleted and fail with 403", 403, response.getStatus());
        assertThat(response.getEntity().toString(), containsString("User is not allowed to perform implicit delete"));
    }

    @After
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

}
