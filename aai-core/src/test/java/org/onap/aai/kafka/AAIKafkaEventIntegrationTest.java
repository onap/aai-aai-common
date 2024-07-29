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
package org.onap.aai.kafka;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import javax.ws.rs.core.Response.Status;
import javax.xml.ws.Response;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.aai.AAISetup;
import org.onap.aai.PayloadUtil;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.dbmap.AAIGraph;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.rest.notification.NotificationService;
import org.onap.aai.rest.notification.UEBNotification;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.engines.JanusGraphDBEngine;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.serialization.engines.TransactionalGraphEngine.Admin;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

@Import(KafkaTestConfiguration.class)
@EmbeddedKafka(partitions = 1, topics = { "AAI-EVENT" })
@TestPropertySource(properties = {
        "jms.bind.address=tcp://localhost:61647",
        "aai.notifications.enabled=true",
        "spring.kafka.producer.retries=0",
        "spring.kafka.producer.properties.sasl.jaas.config=#{null}",
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
public class AAIKafkaEventIntegrationTest extends AAISetup {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    MessageProducer messageProducer;

    @Autowired
    NotificationService notificationService;

    @Autowired
    LoaderFactory loaderFactory;

    @Autowired
    private ConsumerFactory<String, String> consumerFactory;

    @Test
    public void onMessage_shouldSendMessageToKafkaTopic_whenAAIEventReceived()
            throws Exception {
        Consumer<String, String> consumer = consumerFactory.createConsumer();

        consumer.subscribe(Collections.singletonList("AAI-EVENT"));

        String payload = PayloadUtil.getResourcePayload("aai-event.json");
        String expectedResponse = PayloadUtil.getExpectedPayload("aai-event.json");
        messageProducer.sendMessageToDefaultDestination(payload);

        ConsumerRecords<String, String> consumerRecords = KafkaTestUtils.getRecords(consumer, 10000);
        assertFalse(consumerRecords.isEmpty());
        consumerRecords.forEach(consumerRecord -> {
            JSONAssert.assertEquals(expectedResponse, consumerRecord.value(), JSONCompareMode.NON_EXTENSIBLE);
        });
    }

    @Test
    public void thatNotificationServiceGeneratesEvents() throws Exception {
        GraphTraversalSource g = AAIGraph.getInstance().getGraph().traversal();
        Vertex vertex = g.addV()
                .property("aai-node-type", "pserver")
                .property("hostname", "hn")
                .property("aai-uri", "/cloud-infrastructure/pservers/pserver/hn")
                .property(AAIProperties.CREATED_TS, "1234")
                .property(AAIProperties.LAST_MOD_TS, "1234")
                .next();

        Loader loader = loaderFactory.getMoxyLoaderInstance().get(schemaVersions.getDefaultVersion());
        UEBNotification uebNotification = new UEBNotification(loader, loaderFactory, schemaVersions);

        Introspector pserver = loader.introspectorFromName("pserver");
        pserver.setValue("hostname", "hn");
        URI uri = new URI("/cloud-infrastructure/pservers/pserver/hn");
        uebNotification.createNotificationEvent("b9b5ae31-a234-40b1-86be-c23d091e1140", "JUNIT-SOT",
                Status.CREATED, uri,
                pserver, new HashMap<>(), "/aai");
        TransactionalGraphEngine dbEngine = new JanusGraphDBEngine(QueryStyle.TRAVERSAL_URI, loader);
        TransactionalGraphEngine dbEngineSpy = Mockito.spy(dbEngine);
        Admin adminSpy = Mockito.spy(dbEngine.asAdmin());
        when(dbEngineSpy.asAdmin()).thenReturn(adminSpy);
        when(adminSpy.getTraversalSource()).thenReturn(g);
        DBSerializer serializer = new DBSerializer(schemaVersions.getDefaultVersion(), dbEngineSpy, ModelType.MOXY,
                "JUNIT-SOT", Collections.emptySet(),
                AAIProperties.MAXIMUM_DEPTH);

        Set<Vertex> mainVertexesToNotifyOn = Collections.singleton(vertex);
        notificationService.generateEvents(uebNotification, AAIProperties.MAXIMUM_DEPTH, "JUNIT-SOT", serializer,
                "b9b5ae31-a234-40b1-86be-c23d091e1140", dbEngine.getQueryEngine(), mainVertexesToNotifyOn,
                schemaVersions.getDefaultVersion());

        String expectedResponse = PayloadUtil.getExpectedPayload("uebNotificationEvents.json");
        Consumer<String, String> consumer = consumerFactory.createConsumer();
        consumer.subscribe(Collections.singletonList("AAI-EVENT"));

        ConsumerRecords<String, String> consumerRecords = KafkaTestUtils.getRecords(consumer, 10000);
        assertFalse(consumerRecords.isEmpty());
        consumerRecords.forEach(consumerRecord -> {
            JSONAssert.assertEquals(expectedResponse, consumerRecord.value(),
                    new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
                            new Customization("event-header.timestamp", (o1, o2) -> true)));
        });
    }
}
