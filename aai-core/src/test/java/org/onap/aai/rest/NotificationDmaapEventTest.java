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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphTransaction;
import org.javatuples.Pair;
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
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.rest.ueb.NotificationEvent;
import org.onap.aai.rest.ueb.UEBNotification;
import org.onap.aai.serialization.engines.QueryStyle;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.test.annotation.DirtiesContext;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class NotificationDmaapEventTest extends AAISetup {

    @Parameterized.Parameter
    public QueryStyle queryStyle;

    @Parameterized.Parameters(name = "QueryStyle.{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { QueryStyle.TRAVERSAL },
            { QueryStyle.TRAVERSAL_URI }
        });
    }

    @Test
    public void testCreateWithPserverWithAllChildrenAndVerifyMultipleNotificationsWhenNotificationDepthIsZero() throws IOException, AAIException {

        String uri = "/aai/v14/cloud-infrastructure/pservers/pserver/example-hostname-val-85598";
        UEBNotification notification = Mockito.spy(new UEBNotification(ModelType.MOXY, loaderFactory, schemaVersions));
        HttpTestUtil httpTestUtil = new HttpTestUtil(queryStyle, notification, AAIProperties.MINIMUM_DEPTH);

        String resource = PayloadUtil.getResourcePayload("pserver-with-children-for-notification.json");

        Response response = httpTestUtil.doGet(uri);
        assertEquals("Expecting the pserver to be not found", 404, response.getStatus());

        response = httpTestUtil.doPut(uri, resource);
        assertEquals("Expecting the pserver to be created", 201, response.getStatus());

        int expectedCreateEvents = 17;

        assertThat(notification.getEvents().size(), is(expectedCreateEvents));

        // Verify all the events are create since its a new PUT
        notification.getEvents().forEach((event) -> {

            String header = event.getEventHeader().marshal(false);

            assertThat(
                event.getEventHeader().marshal(false),
                containsString("\"CREATE\"")
            );

            assertThat(
                header,
                containsString("\"top-entity-type\":\"pserver\"")
            );

        });

        response = httpTestUtil.doGet(uri);
        assertEquals("Expecting the pserver to be found", 200, response.getStatus());
    }

    // Test existing pserver create new pinterface check dmaap event for pinterface is CREATE
    @Test
    public void testExistingPserverCreateNewChildPInterfaceAndCheckDmaapEventForPInterfaceIsCreateWhenNotificationDepthIsZero() throws IOException, AAIException {

        String uri = "/aai/v14/cloud-infrastructure/pservers/pserver/example-hostname-val-85598";
        UEBNotification notification = Mockito.spy(new UEBNotification(ModelType.MOXY, loaderFactory, schemaVersions));
        HttpTestUtil httpTestUtil = new HttpTestUtil(queryStyle);

        String pserverResource = PayloadUtil.getResourcePayload("pserver-with-children-for-notification.json");

        Response response = httpTestUtil.doGet(uri);
        assertEquals("Expecting the pserver to be not found", 404, response.getStatus());

        response = httpTestUtil.doPut(uri, pserverResource);
        assertEquals("Expecting the pserver to be created", 201, response.getStatus());
        notification.clearEvents();


        response = httpTestUtil.doGet(uri , "all");
        assertEquals("Expecting the pserver to be found", 200, response.getStatus());

        JSONObject pserverJson = new JSONObject(response.getEntity().toString());
        JSONObject pInterfaceObject = new JSONObject();
        pInterfaceObject.put("interface-name", "p-interface-1");

        pserverJson.getJSONObject("p-interfaces").getJSONArray("p-interface").put(pInterfaceObject);

        httpTestUtil = new HttpTestUtil(queryStyle, notification, AAIProperties.MINIMUM_DEPTH);
        response = httpTestUtil.doPut(uri, pserverJson.toString());
        assertEquals("Expecting the pserver to be updated with a new p-interface", 200, response.getStatus());

        response = httpTestUtil.doGet(uri + "/p-interfaces/p-interface/p-interface-1", "0");
        assertEquals("Expecting the p-interface to be found", 200, response.getStatus());

        List<NotificationEvent> events = notification.getEvents();
        assertThat(events.size(), is(2));

        String notificationEventHeader = events.get(1).getEventHeader().marshal(false);
        String notificationEventBody = events.get(1).getObj().marshal(false);

        assertThat(notificationEventHeader, containsString("\"action\":\"CREATE\""));
        assertThat(notificationEventHeader, containsString("\"entity-type\":\"p-interface\""));
        assertThat(notificationEventHeader, containsString("\"top-entity-type\":\"pserver\""));

        String expectedNotificationHeader = PayloadUtil.getResourcePayload("notification-dmaap-events/depth-zero/expected-notification-header-create-child-on-existing-obj.json");
        String expectedNotificationBody = PayloadUtil.getResourcePayload("notification-dmaap-events/depth-zero/expected-notification-body-create-child-on-existing-obj.json");

        JSONAssert.assertEquals(expectedNotificationHeader, notificationEventHeader, false);
        JSONAssert.assertEquals(expectedNotificationBody, notificationEventBody, false);
    }

    @Test
    public void testExistingPserverCreateNewChildPInterfaceAndCheckDmaapEventForPserverIsSentWithNewPInterfaceWhenNotificationDepthIsAll() throws IOException, AAIException {

        String uri = "/aai/v14/cloud-infrastructure/pservers/pserver/example-hostname-val-85598";
        UEBNotification notification = Mockito.spy(new UEBNotification(ModelType.MOXY, loaderFactory, schemaVersions));
        HttpTestUtil httpTestUtil = new HttpTestUtil(queryStyle);

        String pserverResource = PayloadUtil.getResourcePayload("pserver-with-children-for-notification.json");

        Response response = httpTestUtil.doGet(uri);
        assertEquals("Expecting the pserver to be not found", 404, response.getStatus());

        response = httpTestUtil.doPut(uri, pserverResource);
        assertEquals("Expecting the pserver to be created", 201, response.getStatus());

        response = httpTestUtil.doGet(uri , "all");
        assertEquals("Expecting the pserver to be found", 200, response.getStatus());

        JSONObject pserverJson = new JSONObject(response.getEntity().toString());
        String pserverResourceVersion = pserverJson.getString("resource-version");

        JSONObject pInterfaceObject = new JSONObject();
        pInterfaceObject.put("interface-name", "p-interface-1");

        pserverJson.getJSONObject("p-interfaces").getJSONArray("p-interface").put(pInterfaceObject);

        httpTestUtil = new HttpTestUtil(queryStyle, notification, AAIProperties.MAXIMUM_DEPTH);
        response = httpTestUtil.doPut(uri, pserverJson.toString());
        assertEquals("Expecting the pserver to be updated with a new p-interface", 200, response.getStatus());

        response = httpTestUtil.doGet(uri + "/p-interfaces/p-interface/p-interface-1", "0");
        assertEquals("Expecting the p-interface to be found", 200, response.getStatus());

        List<NotificationEvent> events = notification.getEvents();
        assertThat(events.size(), is(1));

        String notificationEventHeader = events.get(0).getEventHeader().marshal(false);
        String notificationEventBody = events.get(0).getObj().marshal(false);

        assertThat(notificationEventHeader, containsString("\"action\":\"UPDATE\""));
        assertThat(notificationEventHeader, containsString("\"entity-type\":\"pserver\""));
        assertThat(notificationEventHeader, containsString("\"top-entity-type\":\"pserver\""));

        String expectedNotificationHeader = PayloadUtil.getResourcePayload("notification-dmaap-events/depth-all/expected-notification-header-create-child-on-existing-obj.json");
        String expectedNotificationBody = PayloadUtil.getResourcePayload("notification-dmaap-events/depth-all/expected-notification-body-create-child-on-existing-obj.json");

        JSONAssert.assertEquals(expectedNotificationHeader, notificationEventHeader, false);
        JSONAssert.assertEquals(expectedNotificationBody, notificationEventBody, false);

        response = httpTestUtil.doGet(uri, "0");
        pserverJson = new JSONObject(response.getEntity().toString());
        String newPserverResourceVersion = pserverJson.getString("resource-version");

        // After an pserver's p-interface is updated on the pserver, even though
        // the pserver nothing changed, expecting the pserver resource version to be changed
        assertThat(
            "Expecting the new pserver resource version and old resource version to be not same",
            newPserverResourceVersion,
            is(not(pserverResourceVersion))
        );
        assertEquals("Expecting the p-interface to be found", 200, response.getStatus());
    }

    // Test Bulk Scenario
    @Test
    public void testBulkScenarioWhereMultipleCreatesAndEnsureNoDuplicationInDmaapEventsWhenNotificationDepthIsZero() throws UnsupportedEncodingException, AAIException {

        String pserverUri = "/aai/v14/cloud-infrastructure/pservers/pserver/random-pserver";
        String cloudRegionUri = "/aai/v14/cloud-infrastructure/cloud-regions/cloud-region/random-cloud-region-owner/random-cloud-region-id";

        UEBNotification notification = Mockito.spy(new UEBNotification(ModelType.MOXY, loaderFactory, schemaVersions));
        HttpTestUtil httpTestUtil = new HttpTestUtil(queryStyle, notification, AAIProperties.MINIMUM_DEPTH);

        Map<String, String> uriPayload = new LinkedHashMap<>();

        uriPayload.put(pserverUri, "{}");
        uriPayload.put(cloudRegionUri, "{}");

        Response response = httpTestUtil.doPut(uriPayload);
        assertThat(response.getStatus(), is(201));

        int numberOfEventsActual = notification.getEvents().size();
        int expectedEvents = 2;

        assertThat("Expecting the number of dmaap events to be 2", numberOfEventsActual, is(expectedEvents));

        notification.getEvents().forEach((event) -> {
            String notificationEventHeader = event.getEventHeader().marshal(false);
            assertThat(notificationEventHeader, containsString("\"CREATE\""));
        });
    }


    @Test
    public void testBulkScenarioWhereMultipleCreatesAndEnsureNoDuplicationInDmaapEventsWhenNotificationDepthIsAll() throws UnsupportedEncodingException, AAIException {

        String pserverUri = "/aai/v14/cloud-infrastructure/pservers/pserver/random-pserver";
        String cloudRegionUri = "/aai/v14/cloud-infrastructure/cloud-regions/cloud-region/random-cloud-region-owner/random-cloud-region-id";

        UEBNotification notification = Mockito.spy(new UEBNotification(ModelType.MOXY, loaderFactory, schemaVersions));
        HttpTestUtil httpTestUtil = new HttpTestUtil(queryStyle, notification, AAIProperties.MAXIMUM_DEPTH);

        Map<String, String> uriPayload = new LinkedHashMap<>();

        uriPayload.put(pserverUri, "{}");
        uriPayload.put(cloudRegionUri, "{}");

        Response response = httpTestUtil.doPut(uriPayload);
        assertThat(response.getStatus(), is(201));

        int numberOfEventsActual = notification.getEvents().size();
        int expectedEvents = 2;

        assertThat("Expecting the number of dmaap events to be 2", numberOfEventsActual, is(expectedEvents));

        notification.getEvents().forEach((event) -> {
            String notificationEventHeader = event.getEventHeader().marshal(false);
            assertThat(notificationEventHeader, containsString("\"CREATE\""));
        });
    }

    @Test
    public void testDeleteOnExistingPserverAndCheckIfNotificationDepthIsZeroThatAllEventsHaveDeleteAndThatDepthIsZeroOnEachNotificationEvent() throws IOException, AAIException {
        String uri = "/aai/v14/cloud-infrastructure/pservers/pserver/example-hostname-val-85598";
        UEBNotification notification = Mockito.spy(new UEBNotification(ModelType.MOXY, loaderFactory, schemaVersions));
        HttpTestUtil httpTestUtil = new HttpTestUtil(queryStyle);

        String pserverResource = PayloadUtil.getResourcePayload("pserver-with-children-for-notification.json");

        Response response = httpTestUtil.doGet(uri);
        assertEquals("Expecting the pserver to be not found", 404, response.getStatus());

        response = httpTestUtil.doPut(uri, pserverResource);
        assertEquals("Expecting the pserver to be created", 201, response.getStatus());

        response = httpTestUtil.doGet(uri , "all");
        assertEquals("Expecting the pserver to be found", 200, response.getStatus());

        JSONObject pserverObject = new JSONObject(response.getEntity().toString());
        String resourceVersion = pserverObject.getString("resource-version");

        httpTestUtil = new HttpTestUtil(queryStyle, notification, AAIProperties.MINIMUM_DEPTH);
        response = httpTestUtil.doDelete(uri, resourceVersion);
        assertEquals("Expecting the pserver to be deleted", 204, response.getStatus());

        List<NotificationEvent> notificationEvents = notification.getEvents();
        assertThat(notificationEvents.size(), is(17));

        notificationEvents.forEach((event) -> {

            String header = event.getEventHeader().marshal(false);

            assertThat(
                event.getEventHeader().marshal(false),
                containsString("\"DELETE\"")
            );

            assertThat(
                header,
                containsString("\"top-entity-type\":\"pserver\"")
            );
        });
    }


    @Test
    public void testDeleteOnExistingResourceVersionMismatchNoEventGenerated() throws IOException, AAIException {
        String uri = "/aai/v14/cloud-infrastructure/pservers/pserver/example-hostname-val-85598";
        UEBNotification notification = Mockito.spy(new UEBNotification(ModelType.MOXY, loaderFactory, schemaVersions));
        HttpTestUtil httpTestUtil = new HttpTestUtil(queryStyle);

        String pserverResource = PayloadUtil.getResourcePayload("pserver-with-children-for-notification.json");

        Response response = httpTestUtil.doGet(uri);
        assertEquals("Expecting the pserver to be not found", 404, response.getStatus());

        response = httpTestUtil.doPut(uri, pserverResource);
        assertEquals("Expecting the pserver to be created", 201, response.getStatus());

        response = httpTestUtil.doGet(uri , "all");
        assertEquals("Expecting the pserver to be found", 200, response.getStatus());

        JSONObject pserverObject = new JSONObject(response.getEntity().toString());
        String resourceVersion = pserverObject.getString("resource-version");

        httpTestUtil = new HttpTestUtil(queryStyle, notification, AAIProperties.MINIMUM_DEPTH);
        response = httpTestUtil.doDelete(uri, resourceVersion+"123");
        assertEquals("Resource version mismatch exception", 412, response.getStatus());

        List<NotificationEvent> notificationEvents = notification.getEvents();
        assertThat(notificationEvents.size(), is(0));
    }


    // Test notification depth set to all
    // Scenario for testing the creation of pserver with children, grandchildren
    // Default behaviour is for one event to be sent out
    // which includes all the children and grandchildren, etc
    @Test
    public void testCreateWithPserverWithAllChildrenAndVerifyOneNotificationWhenNotificationDepthIsAll() throws IOException, AAIException {

        String uri = "/aai/v14/cloud-infrastructure/pservers/pserver/example-hostname-val-85598";
        UEBNotification notification = Mockito.spy(new UEBNotification(ModelType.MOXY, loaderFactory, schemaVersions));
        HttpTestUtil httpTestUtil = new HttpTestUtil(queryStyle, notification, AAIProperties.MAXIMUM_DEPTH);

        String resource = PayloadUtil.getResourcePayload("pserver-with-children-for-notification.json");

        Response response = httpTestUtil.doGet(uri);
        assertEquals("Expecting the pserver to be not found", 404, response.getStatus());

        response = httpTestUtil.doPut(uri, resource);
        assertEquals("Expecting the pserver to be created", 201, response.getStatus());

        assertThat(notification.getEvents().size(), is(1));

        NotificationEvent notificationEvent = notification.getEvents().get(0);

        // Verify all the events are create since its a new PUT
        String header = notificationEvent.getEventHeader().marshal(false);

        assertThat(
            header,
            containsString("\"CREATE\"")
        );

        assertThat(
            header,
            containsString("\"entity-type\":\"pserver\"")
        );

        assertThat(
            header,
            containsString("\"top-entity-type\":\"pserver\"")
        );

        assertThat(
            header,
            containsString("\"entity-link\":\"" + uri +  "\"")
        );

        response = httpTestUtil.doGet(uri);
        assertEquals("Expecting the pserver to be found", 200, response.getStatus());

        JSONAssert.assertEquals(response.getEntity().toString(), notificationEvent.getObj().marshal(false), false);
    }

    @Test
    public void testPatchExistingPserverWithChildrenAndModifyOnlyOneObjectAndVerifyThatOnlyOneNotificationEventNoChildrenWhenNotificationDepthIsZero() throws IOException, AAIException {

        String uri = "/aai/v14/cloud-infrastructure/pservers/pserver/example-hostname-val-85598";
        UEBNotification notification = Mockito.spy(new UEBNotification(ModelType.MOXY, loaderFactory, schemaVersions));
        HttpTestUtil httpTestUtil = new HttpTestUtil(queryStyle);

        String resource = PayloadUtil.getResourcePayload("pserver-with-children-for-notification.json");

        Response response = httpTestUtil.doGet(uri);
        assertEquals("Expecting the pserver to be not found", 404, response.getStatus());

        response = httpTestUtil.doPut(uri, resource);
        assertEquals("Expecting the pserver to be created", 201, response.getStatus());

        response = httpTestUtil.doGet(uri);
        assertEquals("Expecting the pserver to be found", 200, response.getStatus());

        JSONObject pserverObject = new JSONObject();
        pserverObject.put("equip-type", "new-equip-patch-type");

        httpTestUtil = new HttpTestUtil(queryStyle, notification, AAIProperties.MINIMUM_DEPTH);
        response = httpTestUtil.doPatch(uri, pserverObject.toString());
        assertThat(response.getStatus(), is(200));

        response = httpTestUtil.doGet(uri, "0");
        assertThat(response.getEntity().toString(), containsString("new-equip-patch-type"));

        assertThat(notification.getEvents().size(), is(1));
        String updateNotificationEvent = notification.getEvents().get(0).getObj().marshal(true);

        // Check that everything in notification event is also response body
        // Not comparing the other way as notification only includes parents main properties
        JSONAssert.assertEquals(updateNotificationEvent, response.getEntity().toString(), false);
    }

    @Test
    public void testPatchExistingPserverWithChildrenAndModifyOnlyOneObjectAndVerifyThatOnlyOneNotificationEventIncludeChildrenWhenNotificationDepthIsAll() throws IOException, AAIException {

        String uri = "/aai/v14/cloud-infrastructure/pservers/pserver/example-hostname-val-85598";
        UEBNotification notification = Mockito.spy(new UEBNotification(ModelType.MOXY, loaderFactory, schemaVersions));
        HttpTestUtil httpTestUtil = new HttpTestUtil(queryStyle);

        String resource = PayloadUtil.getResourcePayload("pserver-with-children-for-notification.json");

        Response response = httpTestUtil.doGet(uri);
        assertEquals("Expecting the pserver to be not found", 404, response.getStatus());

        response = httpTestUtil.doPut(uri, resource);
        assertEquals("Expecting the pserver to be created", 201, response.getStatus());

        response = httpTestUtil.doGet(uri);
        assertEquals("Expecting the pserver to be found", 200, response.getStatus());

        JSONObject pserverObject = new JSONObject();
        pserverObject.put("equip-type", "new-equip-patch-type");

        httpTestUtil = new HttpTestUtil(queryStyle, notification, AAIProperties.MAXIMUM_DEPTH);
        response = httpTestUtil.doPatch(uri, pserverObject.toString());
        assertThat(response.getStatus(), is(200));

        response = httpTestUtil.doGet(uri, "all");
        assertThat(response.getEntity().toString(), containsString("new-equip-patch-type"));

        assertThat(notification.getEvents().size(), is(1));
        String updateNotificationEvent = notification.getEvents().get(0).getObj().marshal(true);

        // Check that everything in notification event is also response body
        // Not comparing the other way as notification only includes parents main properties
        JSONAssert.assertEquals(updateNotificationEvent, response.getEntity().toString(), false);
    }

    // Test notification depth set to all
    // Scenario where we are only updating one field in p-interface
    // Make sure the parent and children are included
    @Test
    public void testUpdateExistingPserverWithChildrenAndModifyOnlyOneObjectAndVerifyThatOnlyOneNotificationEventIncludingChildrenWhenNotificationDepthIsAll() throws IOException, AAIException {

        String uri = "/aai/v14/cloud-infrastructure/pservers/pserver/example-hostname-val-85598";
        UEBNotification notification = Mockito.spy(new UEBNotification(ModelType.MOXY, loaderFactory, schemaVersions));
        HttpTestUtil httpTestUtil = new HttpTestUtil(queryStyle);

        String resource = PayloadUtil.getResourcePayload("pserver-with-children-for-notification.json");

        Response response = httpTestUtil.doGet(uri);
        assertEquals("Expecting the pserver to be not found", 404, response.getStatus());

        response = httpTestUtil.doPut(uri, resource);
        assertEquals("Expecting the pserver to be created", 201, response.getStatus());

        response = httpTestUtil.doGet(uri);
        assertEquals("Expecting the pserver to be found", 200, response.getStatus());

        response = httpTestUtil.doGet(uri + "/p-interfaces/p-interface/example-interface-name-val-46147", "0");
        assertEquals("Expecting the p-interface to be found", 200, response.getStatus());

        JSONObject pInterfaceObject = new JSONObject(response.getEntity().toString());
        pInterfaceObject.put("equipment-identifier", "new-equipment-identifier");

        httpTestUtil = new HttpTestUtil(queryStyle, notification, AAIProperties.MAXIMUM_DEPTH);
        response = httpTestUtil.doPut(uri + "/p-interfaces/p-interface/example-interface-name-val-46147", pInterfaceObject.toString());
        assertThat(response.getStatus(), is(200));

        // Get the parent uri as the notification event json has parent structure it makes it easy to compare
        response = httpTestUtil.doGet(uri);
        assertThat(response.getEntity().toString(), containsString("new-equipment-identifier"));

        assertThat(notification.getEvents().size(), is(1));
        String updateNotificationEvent = notification.getEvents().get(0).getObj().marshal(true);

        // Check that everything in notification event is also response body
        // Not comparing the other way as notification only includes parents main properties
        JSONAssert.assertEquals(updateNotificationEvent, response.getEntity().toString(), false);
    }

    // Test notification depth set to 0
    // Scenario where we are only updating one field in p-interface
    @Test
    public void testUpdateExistingPserverWithChildrenAndModifyOnlyPInterfaceAndVerifyThatOnlyOneNotificationForPInterfaceIsCreatedWhenNotificationDepthIsZero() throws IOException, AAIException {

        String uri = "/aai/v14/cloud-infrastructure/pservers/pserver/example-hostname-val-85598";
        UEBNotification notification = Mockito.spy(new UEBNotification(ModelType.MOXY, loaderFactory, schemaVersions));
        HttpTestUtil httpTestUtil = new HttpTestUtil(queryStyle);

        String resource = PayloadUtil.getResourcePayload("pserver-with-children-for-notification.json");

        Response response = httpTestUtil.doGet(uri);
        assertEquals("Expecting the pserver to be not found", 404, response.getStatus());

        response = httpTestUtil.doPut(uri, resource);
        assertEquals("Expecting the pserver to be created", 201, response.getStatus());

        response = httpTestUtil.doGet(uri);
        assertEquals("Expecting the pserver to be found", 200, response.getStatus());

        response = httpTestUtil.doGet(uri + "/p-interfaces/p-interface/example-interface-name-val-46147", "0");
        assertEquals("Expecting the p-interface to be found", 200, response.getStatus());

        JSONObject pInterfaceObject = new JSONObject(response.getEntity().toString());
        pInterfaceObject.put("equipment-identifier", "new-equipment-identifier");

        httpTestUtil = new HttpTestUtil(queryStyle, notification, AAIProperties.MINIMUM_DEPTH);
        response = httpTestUtil.doPut(uri + "/p-interfaces/p-interface/example-interface-name-val-46147", pInterfaceObject.toString());
        assertThat(response.getStatus(), is(200));

        response = httpTestUtil.doGet(uri);
        assertThat(notification.getEvents().size(), is(1));
        String updateNotificationEvent = notification.getEvents().get(0).getObj().marshal(true);
        System.out.println("Update notification " + updateNotificationEvent);

        // Check that everything in notification event is also response body
        // Not comparing the other way as notification only includes parents main properties
        JSONAssert.assertEquals(updateNotificationEvent, response.getEntity().toString(), false);
    }

    @Test
    public void testExistingPserverWithChildAndGenericVnfAndCreateEdgeBetweenThemAndCheckNoChildWhenNotificationDepthIsZero() throws IOException, AAIException {

        String hostname = "example-hostname-val-85598";

        String pserverUri = "/aai/v14/cloud-infrastructure/pservers/pserver/" + hostname;
        String genericVnfUri = "/aai/v14/network/generic-vnfs/generic-vnf/generic-vnf-notification";

        UEBNotification notification = Mockito.spy(new UEBNotification(ModelType.MOXY, loaderFactory, schemaVersions));
        HttpTestUtil httpTestUtil = new HttpTestUtil(queryStyle);

        String resource = PayloadUtil.getResourcePayload("pserver-with-children-for-notification.json");
        String genericVnfResource = PayloadUtil.getResourcePayload("generic-vnf-notification.json");

        Response response = httpTestUtil.doGet(pserverUri);
        assertEquals("Expecting the pserver to be not found", 404, response.getStatus());

        response = httpTestUtil.doPut(pserverUri, resource);
        assertEquals("Expecting the pserver to be created", 201, response.getStatus());

        response = httpTestUtil.doGet(pserverUri);
        assertEquals("Expecting the pserver to be found", 200, response.getStatus());

        response = httpTestUtil.doGet(genericVnfUri);
        assertEquals("Expecting the generic-vnf to be not found", 404, response.getStatus());

        response = httpTestUtil.doPut(genericVnfUri, genericVnfResource);
        assertEquals("Expecting the generic-vnf to be created", 201, response.getStatus());

        response = httpTestUtil.doGet(genericVnfUri);
        assertEquals("Expecting the generic-vnf to be found", 200, response.getStatus());
        assertThat(response.getEntity().toString(), not(containsString(hostname)));

        httpTestUtil = new HttpTestUtil(queryStyle, notification, AAIProperties.MINIMUM_DEPTH);

        String relationship = PayloadUtil.getResourcePayload("pserver-to-gvnf-relationship-notification.json");

        response = httpTestUtil.doPut(pserverUri + "/relationship-list/relationship", relationship);
        assertEquals("Expecting the pserver to generic-vnf relationship to be created", 200, response.getStatus());

        List<NotificationEvent> notificationEvents = notification.getEvents();
        assertThat(notificationEvents.size(), is(2));

        String expectedNotificationHeader = PayloadUtil.getResourcePayload("notification-dmaap-events/depth-zero/expected-notification-header-create-edge-between-pserver-and-generic-vnf.json");
        String expectedNotificationBody = PayloadUtil.getResourcePayload("notification-dmaap-events/depth-zero/expected-notification-body-create-edge-between-pserver-and-generic-vnf.json");

        JSONAssert.assertEquals(expectedNotificationHeader, notificationEvents.get(0).getEventHeader().marshal(false), false);
        JSONAssert.assertEquals(expectedNotificationBody, notificationEvents.get(0).getObj().marshal(false),  false);

        response = httpTestUtil.doGet(genericVnfUri);

        assertEquals("Expecting the generic-vnf to be found", 200, response.getStatus());
        assertThat(response.getEntity().toString(), containsString(hostname));
    }

    @Test
    public void testExistingPserverWithChildAndGenericVnfAndCreateEdgeBetweenThemAndCheckChildrenIncludedWhenNotificationDepthIsAll() throws IOException, AAIException {

        String hostname = "example-hostname-val-85598";

        String pserverUri = "/aai/v14/cloud-infrastructure/pservers/pserver/" + hostname;
        String genericVnfUri = "/aai/v14/network/generic-vnfs/generic-vnf/generic-vnf-notification";

        UEBNotification notification = Mockito.spy(new UEBNotification(ModelType.MOXY, loaderFactory, schemaVersions));
        HttpTestUtil httpTestUtil = new HttpTestUtil(queryStyle);

        String resource = PayloadUtil.getResourcePayload("pserver-with-children-for-notification.json");
        String genericVnfResource = PayloadUtil.getResourcePayload("generic-vnf-notification.json");

        Response response = httpTestUtil.doGet(pserverUri);
        assertEquals("Expecting the pserver to be not found", 404, response.getStatus());

        response = httpTestUtil.doPut(pserverUri, resource);
        assertEquals("Expecting the pserver to be created", 201, response.getStatus());

        response = httpTestUtil.doGet(pserverUri);
        assertEquals("Expecting the pserver to be found", 200, response.getStatus());

        response = httpTestUtil.doGet(genericVnfUri);
        assertEquals("Expecting the generic-vnf to be not found", 404, response.getStatus());

        response = httpTestUtil.doPut(genericVnfUri, genericVnfResource);
        assertEquals("Expecting the generic-vnf to be created", 201, response.getStatus());

        response = httpTestUtil.doGet(genericVnfUri);
        assertEquals("Expecting the generic-vnf to be found", 200, response.getStatus());
        assertThat(response.getEntity().toString(), not(containsString(hostname)));

        httpTestUtil = new HttpTestUtil(queryStyle, notification, AAIProperties.MAXIMUM_DEPTH);

        String relationship = PayloadUtil.getResourcePayload("pserver-to-gvnf-relationship-notification.json");

        response = httpTestUtil.doPut(pserverUri + "/relationship-list/relationship", relationship);
        assertEquals("Expecting the pserver to generic-vnf relationship to be created", 200, response.getStatus());

        List<NotificationEvent> notificationEvents = notification.getEvents();
        assertThat(notificationEvents.size(), is(2));

        String expectedNotificationHeader = PayloadUtil.getResourcePayload("notification-dmaap-events/depth-all/expected-notification-header-create-edge-between-pserver-and-generic-vnf.json");
        String expectedNotificationBody   = PayloadUtil.getResourcePayload("notification-dmaap-events/depth-all/expected-notification-body-create-edge-between-pserver-and-generic-vnf.json");

        System.out.println("Notification Body: " + notificationEvents.get(0).getObj().marshal(false));
        JSONAssert.assertEquals(expectedNotificationHeader, notificationEvents.get(0).getEventHeader().marshal(false), false);
        JSONAssert.assertEquals(expectedNotificationBody, notificationEvents.get(0).getObj().marshal(false),  false);

        response = httpTestUtil.doGet(genericVnfUri);

        assertEquals("Expecting the generic-vnf to be found", 200, response.getStatus());
        assertThat(response.getEntity().toString(), containsString(hostname));
    }

    @Test
    public void testExistingPserverWithChildAndGenericVnfAndExistingEdgeBetweenThemAndDeleteEdgeAndCheckNoChildWhenNotificationDepthIsZero() throws IOException, AAIException {

        String hostname = "example-hostname-val-85598";

        String pserverUri = "/aai/v14/cloud-infrastructure/pservers/pserver/" + hostname;
        String genericVnfUri = "/aai/v14/network/generic-vnfs/generic-vnf/generic-vnf-notification";

        String relationship = PayloadUtil.getResourcePayload("pserver-to-gvnf-relationship-notification.json");

        UEBNotification notification = Mockito.spy(new UEBNotification(ModelType.MOXY, loaderFactory, schemaVersions));
        HttpTestUtil httpTestUtil = new HttpTestUtil(queryStyle);

        String resource = PayloadUtil.getResourcePayload("pserver-with-children-for-notification.json");
        String genericVnfResource = PayloadUtil.getResourcePayload("generic-vnf-notification.json");

        Response response = httpTestUtil.doGet(pserverUri);
        assertEquals("Expecting the pserver to be not found", 404, response.getStatus());

        response = httpTestUtil.doPut(pserverUri, resource);
        assertEquals("Expecting the pserver to be created", 201, response.getStatus());

        response = httpTestUtil.doGet(pserverUri);
        assertEquals("Expecting the pserver to be found", 200, response.getStatus());

        response = httpTestUtil.doGet(genericVnfUri);
        assertEquals("Expecting the generic-vnf to be not found", 404, response.getStatus());

        response = httpTestUtil.doPut(genericVnfUri, genericVnfResource);
        assertEquals("Expecting the generic-vnf to be created", 201, response.getStatus());

        response = httpTestUtil.doGet(genericVnfUri);
        assertEquals("Expecting the generic-vnf to be found", 200, response.getStatus());
        assertThat(response.getEntity().toString(), not(containsString(hostname)));


        response = httpTestUtil.doPut(pserverUri + "/relationship-list/relationship", relationship);
        assertEquals("Expecting the pserver to generic-vnf relationship to be created", 200, response.getStatus());

        response = httpTestUtil.doGet(genericVnfUri);

        assertEquals("Expecting the generic-vnf to be found", 200, response.getStatus());
        assertThat(response.getEntity().toString(), containsString(hostname));

        assertEquals("Expecting the generic-vnf to be found", 200, response.getStatus());
        assertThat(response.getEntity().toString(), containsString(hostname));

        response = httpTestUtil.doGet(pserverUri);
        assertEquals("Expecting the pserver to be found", 200, response.getStatus());

        JSONObject pserverJson = new JSONObject(response.getEntity().toString());
        String resourceVersion = pserverJson.getString("resource-version");

        httpTestUtil = new HttpTestUtil(queryStyle, notification, AAIProperties.MINIMUM_DEPTH);

        response = httpTestUtil.doDelete(pserverUri + "/relationship-list/relationship", resourceVersion, relationship);
        assertThat("Expected the pserver relationship to generic-vnf to be deleted", response.getStatus(), is(204));

        List<NotificationEvent> notificationEvents = notification.getEvents();

        assertThat(notificationEvents.size(), is(2));

        String expectedNotificationHeader = PayloadUtil.getResourcePayload("notification-dmaap-events/depth-zero/expected-notification-header-delete-edge-between-pserver-and-generic-vnf.json");
        String expectedNotificationBody = PayloadUtil.getResourcePayload("notification-dmaap-events/depth-zero/expected-notification-body-delete-edge-between-pserver-and-generic-vnf.json");

        JSONAssert.assertEquals(expectedNotificationHeader, notificationEvents.get(0).getEventHeader().marshal(false), false);
        JSONAssert.assertEquals(expectedNotificationBody, notificationEvents.get(0).getObj().marshal(false),  false);

    }

    @Test
    public void testExistingPserverWithChildAndGenericVnfAndExistingEdgeBetweenThemAndDeleteEdgeAndCheckChildrenWhenNotificationDepthIsAll() throws IOException, AAIException {

        String hostname = "example-hostname-val-85598";

        String pserverUri = "/aai/v14/cloud-infrastructure/pservers/pserver/" + hostname;
        String genericVnfUri = "/aai/v14/network/generic-vnfs/generic-vnf/generic-vnf-notification";

        String relationship = PayloadUtil.getResourcePayload("pserver-to-gvnf-relationship-notification.json");

        UEBNotification notification = Mockito.spy(new UEBNotification(ModelType.MOXY, loaderFactory, schemaVersions));
        HttpTestUtil httpTestUtil = new HttpTestUtil(queryStyle);

        String resource = PayloadUtil.getResourcePayload("pserver-with-children-for-notification.json");
        String genericVnfResource = PayloadUtil.getResourcePayload("generic-vnf-notification.json");

        Response response = httpTestUtil.doGet(pserverUri);
        assertEquals("Expecting the pserver to be not found", 404, response.getStatus());

        response = httpTestUtil.doPut(pserverUri, resource);
        assertEquals("Expecting the pserver to be created", 201, response.getStatus());

        response = httpTestUtil.doGet(pserverUri);
        assertEquals("Expecting the pserver to be found", 200, response.getStatus());

        response = httpTestUtil.doGet(genericVnfUri);
        assertEquals("Expecting the generic-vnf to be not found", 404, response.getStatus());

        response = httpTestUtil.doPut(genericVnfUri, genericVnfResource);
        assertEquals("Expecting the generic-vnf to be created", 201, response.getStatus());

        response = httpTestUtil.doGet(genericVnfUri);
        assertEquals("Expecting the generic-vnf to be found", 200, response.getStatus());
        assertThat(response.getEntity().toString(), not(containsString(hostname)));


        response = httpTestUtil.doPut(pserverUri + "/relationship-list/relationship", relationship);
        assertEquals("Expecting the pserver to generic-vnf relationship to be created", 200, response.getStatus());

        response = httpTestUtil.doGet(genericVnfUri);

        assertEquals("Expecting the generic-vnf to be found", 200, response.getStatus());
        assertThat(response.getEntity().toString(), containsString(hostname));

        assertEquals("Expecting the generic-vnf to be found", 200, response.getStatus());
        assertThat(response.getEntity().toString(), containsString(hostname));

        response = httpTestUtil.doGet(pserverUri);
        assertEquals("Expecting the pserver to be found", 200, response.getStatus());

        JSONObject pserverJson = new JSONObject(response.getEntity().toString());
        String resourceVersion = pserverJson.getString("resource-version");

        httpTestUtil = new HttpTestUtil(queryStyle, notification, AAIProperties.MAXIMUM_DEPTH);

        response = httpTestUtil.doDelete(pserverUri + "/relationship-list/relationship", resourceVersion, relationship);
        assertThat("Expected the pserver relationship to generic-vnf to be deleted", response.getStatus(), is(204));

        List<NotificationEvent> notificationEvents = notification.getEvents();
        assertThat(notificationEvents.size(), is(2));

        String expectedNotificationHeader = PayloadUtil.getResourcePayload("notification-dmaap-events/depth-all/expected-notification-header-delete-edge-between-pserver-and-generic-vnf.json");
        String expectedNotificationBody = PayloadUtil.getResourcePayload("notification-dmaap-events/depth-all/expected-notification-body-delete-edge-between-pserver-and-generic-vnf.json");

        JSONAssert.assertEquals(expectedNotificationHeader, notificationEvents.get(0).getEventHeader().marshal(false), false);
        JSONAssert.assertEquals(expectedNotificationBody, notificationEvents.get(0).getObj().marshal(false),  false);

    }

    @Test
    public void testDeleteOnExistingResourceVersionMismatchNoEventGeneratedFullDepth() throws IOException, AAIException {
        String uri = "/aai/v14/cloud-infrastructure/pservers/pserver/example-hostname-val-85598";
        UEBNotification notification = Mockito.spy(new UEBNotification(ModelType.MOXY, loaderFactory, schemaVersions));
        HttpTestUtil httpTestUtil = new HttpTestUtil(queryStyle);

        String pserverResource = PayloadUtil.getResourcePayload("pserver-with-children-for-notification.json");

        Response response = httpTestUtil.doGet(uri);
        assertEquals("Expecting the pserver to be not found", 404, response.getStatus());

        response = httpTestUtil.doPut(uri, pserverResource);
        assertEquals("Expecting the pserver to be created", 201, response.getStatus());

        response = httpTestUtil.doGet(uri , "all");
        assertEquals("Expecting the pserver to be found", 200, response.getStatus());

        JSONObject pserverObject = new JSONObject(response.getEntity().toString());
        String resourceVersion = pserverObject.getString("resource-version");

        httpTestUtil = new HttpTestUtil(queryStyle, notification, AAIProperties.MAXIMUM_DEPTH);
        response = httpTestUtil.doDelete(uri, resourceVersion+"123");
        assertEquals("Resource version mismatch exception", 412, response.getStatus());

        List<NotificationEvent> notificationEvents = notification.getEvents();
        assertThat(notificationEvents.size(), is(0));
    }

    @Test
    public void testCreateVnfWithChildrenCreateCustomerWithChildrenAndCousinBetweenVlanAndServiceInstanceThenDeleteCustomerVerifyingVlanRV() throws IOException, AAIException {
        UEBNotification notification = Mockito.spy(new UEBNotification(ModelType.MOXY, loaderFactory, schemaVersions));
        HttpTestUtil httpTestUtil = new HttpTestUtil(queryStyle);

        JsonObject paylaods = new JsonParser().parse(
            PayloadUtil.getResourcePayload("customer_with_children_and_generic-vnf_with_children_and_edge_between_service-instance_vlan.json"))
            .getAsJsonObject();
        String gvnfPaylaod = paylaods.get("generic-vnf").toString();
        String custPaylaod = paylaods.get("customer").toString();
        String gvnfUri = "/aai/v14/network/generic-vnfs/generic-vnf/gvnf";
        String custUri = "/aai/v14/business/customers/customer/cust";
        String vlanUri = "/aai/v14/network/generic-vnfs/generic-vnf/gvnf/l-interfaces/l-interface/lint/vlans/vlan/vlan";

        //Setup generic vnf
        Response response = httpTestUtil.doGet(gvnfUri);
        assertEquals("Expecting the generic-vnf to be not found", 404, response.getStatus());
        response = httpTestUtil.doPut(gvnfUri, gvnfPaylaod);
        assertEquals("Expecting the generic-vnf to be created", 201, response.getStatus());
        response = httpTestUtil.doGet(gvnfUri , "all");
        assertEquals("Expecting the generic-vnf to be found", 200, response.getStatus());
        response = httpTestUtil.doGet(vlanUri , "all");
        assertEquals("Expecting the vlan to be found", 200, response.getStatus());
        String vlanResourceVersion = new JSONObject(response.getEntity().toString()).getString("resource-version");

        //Setup customer with service instance relation to vlan
        response = httpTestUtil.doGet(custUri);
        assertEquals("Expecting the customer to be not found", 404, response.getStatus());
        response = httpTestUtil.doPut(custUri, custPaylaod);
        assertEquals("Expecting the customer to be created", 201, response.getStatus());
        response = httpTestUtil.doGet(custUri , "all");
        assertEquals("Expecting the customer to be found", 200, response.getStatus());
        String custResourceVersion = new JSONObject(response.getEntity().toString()).getString("resource-version");

        //Verify vlan rv was updated
        response = httpTestUtil.doGet(vlanUri , "all");
        assertEquals("Expecting the vlan to be found", 200, response.getStatus());
        String vlanResourceVersionAfterCustPut = new JSONObject(response.getEntity().toString()).getString("resource-version");
        assertThat("Expecting the vlan resource version to be updated", vlanResourceVersionAfterCustPut, not(is(vlanResourceVersion)));

        //Delete customer
        notification.clearEvents();
        httpTestUtil = new HttpTestUtil(queryStyle, notification, AAIProperties.MAXIMUM_DEPTH);
        response = httpTestUtil.doDelete(custUri, custResourceVersion);
        assertEquals("Expecting customer to be deleted", 204, response.getStatus());

        //Verify vlan rv was updated
        response = httpTestUtil.doGet(vlanUri , "all");
        assertEquals("Expecting the vlan to be found", 200, response.getStatus());
        String vlanResourceVersionAfterDelete = new JSONObject(response.getEntity().toString()).getString("resource-version");
        assertThat("Expecting the vlan resource version to be updated", vlanResourceVersionAfterDelete, not(is(vlanResourceVersionAfterCustPut)));

        List<NotificationEvent> notificationEvents = notification.getEvents();
        assertThat("Expect the delete to generate 4 events customer, its children and vlan", notificationEvents.size(), is(4));
    }


    @Test
    public void testBulkCreateOfComplexAndPserverWithRelationshipThenBulkDeleteBoth() throws IOException, AAIException {
        UEBNotification notification = Mockito.spy(new UEBNotification(ModelType.MOXY, loaderFactory, schemaVersions));
        HttpTestUtil httpTestUtil = new HttpTestUtil(queryStyle, notification, AAIProperties.MAXIMUM_DEPTH);

        JsonObject paylaods = new JsonParser().parse(
            PayloadUtil.getResourcePayload("complex_pserver_with_relation.json"))
            .getAsJsonObject();
        String complexPaylaod = paylaods.get("complex").toString();
        String pserverPaylaod = paylaods.get("pserver").toString();
        String complexUri = "/aai/v14/cloud-infrastructure/complexes/complex/complex-1";
        String pserverUri = "/aai/v14/cloud-infrastructure/pservers/pserver/pserver-1";

        Response response = httpTestUtil.doGet(complexUri);
        assertEquals("Expecting the complex to be not found", 404, response.getStatus());
        response = httpTestUtil.doGet(pserverUri);
        assertEquals("Expecting the pserver to be not found", 404, response.getStatus());

        Map<String,String> puts = new LinkedHashMap<>();
        puts.put(complexUri, complexPaylaod);
        puts.put(pserverUri, pserverPaylaod);

        response = httpTestUtil.doPut(puts);
        assertEquals("Expecting the puts request to succeed", 201, response.getStatus());
        assertEquals("Expect 2 messages to be created", 2, notification.getEvents().size());
        response = httpTestUtil.doGet(complexUri , "all");
        assertEquals("Expecting the complex to be found", 200, response.getStatus());
        String complexRV = new JSONObject(response.getEntity().toString()).getString("resource-version");
        response = httpTestUtil.doGet(pserverUri , "all");
        assertEquals("Expecting the pserver to be found", 200, response.getStatus());
        String pserverRv = new JSONObject(response.getEntity().toString()).getString("resource-version");
        assertThat("Resource versions match", complexRV, is(pserverRv));

        Map<String, Pair<String, String>> deletes = new LinkedHashMap<>();
        deletes.put(pserverUri, new Pair<>(pserverRv, null));
        deletes.put(complexUri, new Pair<>(complexRV, null));
        notification = Mockito.spy(new UEBNotification(ModelType.MOXY, loaderFactory, schemaVersions));
        httpTestUtil = new HttpTestUtil(queryStyle, notification, AAIProperties.MAXIMUM_DEPTH);
        httpTestUtil.doDelete(deletes);

        response = httpTestUtil.doGet(complexUri);
        assertEquals("Expecting the complex to be not found", 404, response.getStatus());
        response = httpTestUtil.doGet(pserverUri);
        assertEquals("Expecting the pserver to be not found", 404, response.getStatus());
    }

    @Test
    public void testCreateVnfWithChildrenCreateCustomerWithChildrenAndCousinBetweenVlanAndServiceInstanceThenImplicitDeleteVlanVerifyingServiceInstanceRV() throws IOException, AAIException {
        UEBNotification notification = Mockito.spy(new UEBNotification(ModelType.MOXY, loaderFactory, schemaVersions));
        HttpTestUtil httpTestUtil = new HttpTestUtil(queryStyle);

        JsonObject paylaods = new JsonParser().parse(
            PayloadUtil.getResourcePayload("customer_with_children_and_generic-vnf_with_children_and_edge_between_service-instance_vlan.json"))
            .getAsJsonObject();
        String gvnfPaylaod = paylaods.get("generic-vnf").toString();
        String custPaylaod = paylaods.get("customer").toString();
        String custUri = "/aai/v14/business/customers/customer/cust";
        String ssUri = custUri + "/service-subscriptions/service-subscription/ss";
        String siUri = ssUri + "/service-instances/service-instance/si";
        String gvnfUri = "/aai/v14/network/generic-vnfs/generic-vnf/gvnf";
        String lintUri = gvnfUri + "/l-interfaces/l-interface/lint";
        String vlanUri = lintUri + "/vlans/vlan/vlan";

        //Setup generic vnf
        Response response = httpTestUtil.doGet(gvnfUri);
        assertEquals("Expecting the generic-vnf to be not found", 404, response.getStatus());
        response = httpTestUtil.doPut(gvnfUri, gvnfPaylaod);
        assertEquals("Expecting the generic-vnf to be created", 201, response.getStatus());
        response = httpTestUtil.doGet(gvnfUri , "all");
        assertEquals("Expecting the generic-vnf to be found", 200, response.getStatus());
        response = httpTestUtil.doGet(vlanUri , "all");
        assertEquals("Expecting the vlan to be found", 200, response.getStatus());
        String vlanResourceVersion = new JSONObject(response.getEntity().toString()).getString("resource-version");

        //Setup customer with service instance relation to vlan
        response = httpTestUtil.doGet(custUri);
        assertEquals("Expecting the customer to be not found", 404, response.getStatus());
        response = httpTestUtil.doPut(custUri, custPaylaod);
        assertEquals("Expecting the customer to be created", 201, response.getStatus());
        response = httpTestUtil.doGet(custUri , "all");
        assertEquals("Expecting the customer to be found", 200, response.getStatus());
        response = httpTestUtil.doGet(siUri , "all");
        assertEquals("Expecting the service-instance to be found", 200, response.getStatus());
        String serviceInstanceResourceVersion = new JSONObject(response.getEntity().toString()).getString("resource-version");

        //Verify vlan rv was updated
        response = httpTestUtil.doGet(vlanUri , "all");
        assertEquals("Expecting the vlan to be found", 200, response.getStatus());
        String vlanResourceVersionAfterCustPut = new JSONObject(response.getEntity().toString()).getString("resource-version");
        assertThat("Expecting the vlan resource version to be updated", vlanResourceVersionAfterCustPut, not(is(vlanResourceVersion)));

        //Get linterface, replace vlans with empty json (implicit delete) and put triggering implicit delete
        response = httpTestUtil.doGet(lintUri , "all");
        assertEquals("Expecting the l-interface to be found", 200, response.getStatus());
        JSONObject lintJson = new JSONObject(response.getEntity().toString());
        lintJson.put("vlans", new JsonObject());
        notification.clearEvents();
        httpTestUtil = new HttpTestUtil(queryStyle, notification, AAIProperties.MAXIMUM_DEPTH);
        response = httpTestUtil.doPut(lintUri, lintJson.toString());
        assertEquals("Expecting the l-interface to be updated", 200, response.getStatus());

        List<NotificationEvent> notificationEvents = notification.getEvents();
        assertThat("Expect the implied delete to generate 2", notificationEvents.size(), is(2));

        //Verify vlan is no longer there anf get service-instance and compare rv
        response = httpTestUtil.doGet(vlanUri , "all");
        assertEquals("Expecting the vlan not to be found", 404, response.getStatus());
        response = httpTestUtil.doGet(siUri , "all");
        assertEquals("Expecting the service-instance to be found", 200, response.getStatus());
        String serviceInstanceResourceVersionAfterImplicitDelete = new JSONObject(response.getEntity().toString()).getString("resource-version");
        assertThat("Expecting the service-instance resource version to be updated after implicit delete of vlan",
            serviceInstanceResourceVersionAfterImplicitDelete,
            not(is(serviceInstanceResourceVersion)));
    }

    @After
    public void teardown() {

        JanusGraph janusGraph = AAIGraph.getInstance().getGraph();
        JanusGraphTransaction transaction = janusGraph.newTransaction();

        GraphTraversalSource g = transaction.traversal();

        g.V()
            .has(AAIProperties.SOURCE_OF_TRUTH, "JUNIT")
            .forEachRemaining(Vertex::remove);

        transaction.commit();
    }
}
