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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.aai.AAISetup;
import org.onap.aai.PayloadUtil;
import org.onap.aai.domain.notificationEvent.NotificationEvent;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.rest.db.DBRequest;
import org.onap.aai.rest.db.GraphBinding;
import org.onap.aai.rest.db.HttpEntry;
import org.onap.aai.rest.db.ProcessOptions;
import org.onap.aai.restcore.HttpMethod;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.setup.SchemaVersion;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles("kafka")
@Import(KafkaTestConfiguration.class)
@EmbeddedKafka(partitions = 1, topics = { "AAI-EVENT" })
@TestPropertySource(
        properties = {
          "aai.events.enabled=true",
          "spring.kafka.producer.retries=0",
          "spring.kafka.producer.properties.sasl.jaas.config=#{null}",
          "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
          "aai.notifications.enabled=true"
        })
public class AAIKafkaEventIntegrationTest extends AAISetup {

    private static final Duration EVENT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration NO_EVENT_TIMEOUT = Duration.ofSeconds(2);

    private Consumer<String, String> consumer;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    NotificationProducer messageProducer;

    @Autowired
    private ConsumerFactory<String, String> consumerFactory;

    @Mock UriInfo uriInfoMock;
    @Mock MultivaluedMap<String, String> queryParamsMock;
    @Mock HttpHeaders headersMock;

    @Before
    public void setup() {
    MockitoAnnotations.openMocks(this);
        when(headersMock.getAcceptableMediaTypes()).thenReturn(Collections.singletonList(MediaType.APPLICATION_JSON_TYPE));
        when(uriInfoMock.getQueryParameters(anyBoolean())).thenReturn(queryParamsMock);
        when(queryParamsMock.getFirst("depth")).thenReturn("0");
        when(headersMock.getRequestHeader("aai-request-context")).thenReturn(null);
    }

    @Test
    @Ignore
    public void onMessage_shouldSendMessageToKafkaTopic_whenAAIEventReceived()
            throws Exception {
        Consumer<String, String> consumer = consumerFactory.createConsumer();

        consumer.subscribe(Collections.singletonList("AAI-EVENT"));

        String payload = PayloadUtil.getResourcePayload("aai-event.json");
        NotificationEvent event = mapper.readValue(payload, NotificationEvent.class);
        String expectedResponse = PayloadUtil.getExpectedPayload("aai-event.json");
        messageProducer.sendNotification(event);

        ConsumerRecords<String, String> consumerRecords = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10));
        assertFalse(consumerRecords.isEmpty());
        consumerRecords.forEach(consumerRecord -> {
            JSONAssert.assertEquals(expectedResponse, consumerRecord.value(), JSONCompareMode.NON_EXTENSIBLE);
        });
    }

    @After
    public void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
        GraphBinding binding = graphSessionFactory.bind(ModelType.MOXY, QueryStyle.TRAVERSAL_URI,
                new SchemaVersion("v14"));
        binding.startTransaction().traversal().V()
                .has("hostname", P.within("pserver1", "rolled-back-pserver", "http-entry-pserver"))
                .drop().iterate();
        binding.commit();
    }

    @Test
    public void thatEventsArePublishedOnlyAfterTheTransactionIsCommitted() throws AAIException, IOException {
        subscribe();
        GraphBinding binding = graphSessionFactory.bind(ModelType.MOXY, QueryStyle.TRAVERSAL_URI,
                new SchemaVersion("v14"));
        binding.startTransaction();

        dbRequestProcessor.process(binding, List.of(createPserverRequest(binding.dbEngine(), binding.loader(), "pserver1")),
                ProcessOptions.forSourceOfTruth("test").build());

        assertTrue(eventsFor("pserver1", NO_EVENT_TIMEOUT).isEmpty());

        binding.commit();

        List<String> events = eventsFor("pserver1", EVENT_TIMEOUT);
        assertEquals(1, events.size());
        JSONAssert.assertEquals(PayloadUtil.getExpectedPayload("pserver-event.json"), events.get(0),
                JSONCompareMode.LENIENT);
    }

    @Test
    public void thatNoEventsArePublishedWhenTheTransactionIsRolledBack() throws AAIException {
        subscribe();
        GraphBinding binding = graphSessionFactory.bind(ModelType.MOXY, QueryStyle.TRAVERSAL_URI,
                new SchemaVersion("v14"));
        binding.startTransaction();

        dbRequestProcessor.process(binding,
                List.of(createPserverRequest(binding.dbEngine(), binding.loader(), "rolled-back-pserver")),
                ProcessOptions.forSourceOfTruth("test").build());
        binding.rollback();

        assertTrue(eventsFor("rolled-back-pserver", NO_EVENT_TIMEOUT).isEmpty());
    }

    @Test
    public void thatHttpEntryPublishesEventsOnlyAfterTheTransactionIsCommitted() throws AAIException {
        subscribe();
        HttpEntry httpEntry = traversalUriHttpEntry.setHttpEntryProperties(new SchemaVersion("v14"));
        TransactionalGraphEngine dbEngine = httpEntry.getDbEngine();

        httpEntry.process(List.of(createPserverRequest(dbEngine, httpEntry.getLoader(), "http-entry-pserver")), "test");

        assertTrue(eventsFor("http-entry-pserver", NO_EVENT_TIMEOUT).isEmpty());

        dbEngine.commit();

        assertEquals(1, eventsFor("http-entry-pserver", EVENT_TIMEOUT).size());
    }

    private void subscribe() {
        TopicPartition partition = new TopicPartition("AAI-EVENT", 0);
        consumer = consumerFactory.createConsumer();
        consumer.assign(List.of(partition));
        consumer.seekToEnd(List.of(partition));
        consumer.position(partition);
    }

    private List<String> eventsFor(String hostname, Duration timeout) {
        List<String> events = new ArrayList<>();
        KafkaTestUtils.getRecords(consumer, timeout).forEach(consumerRecord -> {
            if (consumerRecord.value().contains("/pservers/pserver/" + hostname + "\"")) {
                events.add(consumerRecord.value());
            }
        });
        return events;
    }

    @SneakyThrows
    private DBRequest createPserverRequest(TransactionalGraphEngine dbEngine, Loader loader, String hostname) {
        String entity = new String(Files.readAllBytes(Path.of("src/test/resources/payloads/templates/pserver.json")))
                .replace("${hostname}", hostname);
        return createDBRequest(dbEngine, loader, "/aai/v14/cloud-infrastructure/pservers/pserver/" + hostname, entity);
    }

    @SneakyThrows
    private DBRequest createDBRequest(TransactionalGraphEngine dbEngine, Loader loader, String uri, String entity) {
        URI uriObject = new URI(uri);
        QueryParser uriQuery = dbEngine.getQueryBuilder().createQueryFromURI(uriObject);
        String objName = uriQuery.getResultType();
        Introspector obj = loader.unmarshal(objName, entity,
            org.onap.aai.restcore.MediaType.getEnum("application/json"));
        return new DBRequest.Builder(HttpMethod.PUT, uriObject, uriQuery, obj, headersMock, uriInfoMock, "someTransaction")
            .rawRequestContent(entity)
            .build();
    }

}
