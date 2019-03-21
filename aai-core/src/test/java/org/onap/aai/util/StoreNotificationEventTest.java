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

package org.onap.aai.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;

import javax.json.Json;
import javax.json.JsonObject;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.AAISetup;
import org.onap.aai.dmaap.AAIDmaapEventJMSProducer;
import org.onap.aai.domain.notificationEvent.NotificationEvent.EventHeader;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.ModelType;

public class StoreNotificationEventTest extends AAISetup {

    private static AAIDmaapEventJMSProducer producer;
    private static StoreNotificationEvent sne;

    @BeforeClass
    public static void setUp() {
        producer = Mockito.mock(AAIDmaapEventJMSProducer.class);
        // sne = new StoreNotificationEvent(producer, "transiationId", "sourceOfTruth");
    }

    @Before
    public void setUpBefore() {
        producer = Mockito.mock(AAIDmaapEventJMSProducer.class);
        sne = new StoreNotificationEvent(producer, "transiationId", "sourceOfTruth");

    }

    @Test(expected = AAIException.class)
    public void testStoreEventNullObj() throws AAIException {
        sne.storeEvent(new EventHeader(), null);
    }

    @Test(expected = AAIException.class)
    public void testStoreEventInvalidObjForPojoUtils() throws AAIException {
        sne.storeEvent(new EventHeader(), new Object());
    }

    @Test
    public void testStoreEventEmptyEventHeader()
            throws AAIException, JsonGenerationException, JsonMappingException, IOException {
        JsonObject object = Json.createObjectBuilder().add("hello", "world").build();
        String res = sne.storeEvent(new EventHeader(), object);

        assertNotNull(res);
        assertTrue(res.contains("\"cambria.partition\" : \"" + AAIConstants.UEB_PUB_PARTITION_AAI + "\""));
        assertTrue(res.contains("\"event-header\""));
        assertTrue(res.contains("\"id\""));
        assertTrue(res.contains("\"timestamp\""));
        assertTrue(res
                .contains("\"source-name\" : \"" + AAIConfig.get("aai.notificationEvent.default.sourceName") + "\""));
        assertTrue(res.contains("\"domain\" : \"" + AAIConfig.get("aai.notificationEvent.default.domain") + "\""));
        assertTrue(res.contains(
                "\"sequence-number\" : \"" + AAIConfig.get("aai.notificationEvent.default.sequenceNumber") + "\""));
        assertTrue(res.contains("\"severity\" : \"" + AAIConfig.get("aai.notificationEvent.default.severity") + "\""));
        assertTrue(
                res.contains("\"event-type\" : \"" + AAIConfig.get("aai.notificationEvent.default.eventType") + "\""));
        assertTrue(res.contains("\"version\" : \"" + AAIConfig.get("aai.notificationEvent.default.version") + "\""));
        assertTrue(res.contains("\"action\" : \"UNK\""));
        assertTrue(res.contains("\"entity-link\" : \"UNK\""));
        assertTrue(res.contains("\"entity\""));
        assertTrue(res.contains("\"hello\""));
        assertTrue(res.contains("\"chars\" : \"world\""));
        assertTrue(res.contains("\"string\" : \"world\""));
        assertTrue(res.contains("\"valueType\" : \"STRING\""));
    }

    @Test
    public void testStoreEvent() throws AAIException, JsonGenerationException, JsonMappingException, IOException {
        JsonObject object = Json.createObjectBuilder().add("hello", "world").build();
        EventHeader eh = new EventHeader();
        eh.setId("123");
        eh.setTimestamp("current-time");
        eh.setEntityLink("entity-link");
        eh.setAction("action!");
        eh.setEventType("surprise");
        eh.setDomain("PROD");
        eh.setSourceName("source");
        eh.setSequenceNumber("23");
        eh.setSeverity("ALERT");
        eh.setVersion("v12");

        String res = sne.storeEvent(eh, object);

        assertNotNull(res);
        assertTrue(res.contains("\"cambria.partition\" : \"" + AAIConstants.UEB_PUB_PARTITION_AAI + "\""));
        assertTrue(res.contains("\"event-header\""));
        assertTrue(res.contains("\"id\" : \"123\""));
        assertTrue(res.contains("\"timestamp\" : \"current-time\""));
        assertTrue(res.contains("\"source-name\" : \"source\""));
        assertTrue(res.contains("\"domain\" : \"PROD\""));
        assertTrue(res.contains("\"sequence-number\" : \"23\""));
        assertTrue(res.contains("\"severity\" : \"ALERT\""));
        assertTrue(res.contains("\"event-type\" : \"surprise\""));
        assertTrue(res.contains("\"version\" : \"v12\""));
        assertTrue(res.contains("\"action\" : \"action!\""));
        assertTrue(res.contains("\"entity-link\" : \"entity-link\""));
        assertTrue(res.contains("\"entity\""));
        assertTrue(res.contains("\"hello\""));
        assertTrue(res.contains("\"chars\" : \"world\""));
        assertTrue(res.contains("\"string\" : \"world\""));
        assertTrue(res.contains("\"valueType\" : \"STRING\""));
    }

    @Test(expected = AAIException.class)
    public void testStoreDynamicEventNullObj() throws AAIException {
        DynamicEntity eventHeader = Mockito.mock(DynamicEntity.class);
        DynamicJAXBContext notificationJaxbContext =
                nodeIngestor.getContextForVersion(schemaVersions.getEdgeLabelVersion());
        sne.storeDynamicEvent(notificationJaxbContext, "v12", eventHeader, null);
    }

    @Test(expected = Exception.class)
    public void testStoreDynamicEventAAIException() throws Exception {

        DynamicJAXBContext notificationJaxbContext =
                nodeIngestor.getContextForVersion(schemaVersions.getEdgeLabelVersion());
        DynamicEntity obj = Mockito.mock(DynamicEntity.class);
        DynamicEntity eventHeader = Mockito.mock(DynamicEntity.class);
        sne.storeDynamicEvent(notificationJaxbContext, "v12", eventHeader, obj);
    }

    @Test(expected = AAIException.class)
    public void testStoreEventIntrospectorNullObj() throws Exception {
        Loader loader = Mockito.mock(Loader.class);
        sne.storeEvent(loader, null, null);
    }

    @Ignore("Stopped working since the model driven story")
    @Test
    public void testStoreEvent1Introspector() throws Exception {
        Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getEdgeLabelVersion());
        Introspector eventHeader = loader.introspectorFromName("notification-event-header");
        eventHeader.setValue("id", "123");
        eventHeader.setValue("timestamp", "current-time");
        eventHeader.setValue("entity-link", "entity-link");
        eventHeader.setValue("action", "action!");
        eventHeader.setValue("event-type", "surprise");
        eventHeader.setValue("domain", "PROD");
        eventHeader.setValue("source-name", "source");
        eventHeader.setValue("sequence-number", "23");
        eventHeader.setValue("severity", "ALERT");
        eventHeader.setValue("version", "v12");
        Introspector obj = loader.introspectorFromName("notification-event");
        String res = sne.storeEvent(loader, eventHeader, obj);

        assertNotNull(res);
        assertTrue(res.contains("\"cambria.partition\":\"" + AAIConstants.UEB_PUB_PARTITION_AAI + "\""));
        assertTrue(res.contains("\"event-header\""));
        assertTrue(res.contains("\"id\":\"123\""));
        assertTrue(res.contains("\"timestamp\":\"current-time\""));
        assertTrue(res.contains("\"source-name\":\"source\""));
        assertTrue(res.contains("\"domain\":\"PROD\""));
        assertTrue(res.contains("\"sequence-number\":\"23\""));
        assertTrue(res.contains("\"severity\":\"ALERT\""));
        assertTrue(res.contains("\"event-type\":\"surprise\""));
        assertTrue(res.contains("\"version\":\"v12\""));
        assertTrue(res.contains("\"action\":\"action!\""));
        assertTrue(res.contains("\"entity-link\":\"entity-link\""));
        assertTrue(res.contains("\"notification-event\""));
    }

    @Ignore("Stopped working since the model driven story")
    @Test
    public void testStoreEventIntrospectorEmptyEventHeader() throws Exception {
        Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getEdgeLabelVersion());
        Introspector eventHeader = loader.introspectorFromName("notification-event-header");
        Introspector obj = loader.introspectorFromName("notification-event");

        String res = sne.storeEvent(loader, eventHeader, obj);

        assertNotNull(res);
        assertTrue(res.contains("\"cambria.partition\":\"" + AAIConstants.UEB_PUB_PARTITION_AAI + "\""));
        assertTrue(res.contains("\"event-header\""));
        assertTrue(res.contains("\"id\""));
        assertTrue(res.contains("\"timestamp\""));
        assertTrue(
                res.contains("\"source-name\":\"" + AAIConfig.get("aai.notificationEvent.default.sourceName") + "\""));
        assertTrue(res.contains("\"domain\":\"" + AAIConfig.get("aai.notificationEvent.default.domain") + "\""));
        assertTrue(res.contains(
                "\"sequence-number\":\"" + AAIConfig.get("aai.notificationEvent.default.sequenceNumber") + "\""));
        assertTrue(res.contains("\"severity\":\"" + AAIConfig.get("aai.notificationEvent.default.severity") + "\""));
        assertTrue(res.contains("\"event-type\":\"" + AAIConfig.get("aai.notificationEvent.default.eventType") + "\""));
        assertTrue(res.contains("\"version\":\"" + AAIConfig.get("aai.notificationEvent.default.version") + "\""));
        assertTrue(res.contains("\"action\":\"UNK\""));
        assertTrue(res.contains("\"entity-link\":\"UNK\""));
        assertTrue(res.contains("\"notification-event\""));
    }
}
