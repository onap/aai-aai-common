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
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.onap.aai.AAISetup;
import org.onap.aai.PayloadUtil;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.rest.db.DBRequest;
import org.onap.aai.restcore.HttpMethod;
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

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles("kafka")
@Import(KafkaTestConfiguration.class)
@EmbeddedKafka(partitions = 1, topics = { "AAI-EVENT" })
@TestPropertySource(
        properties = {
          "jms.bind.address=tcp://localhost:61647",
          "aai.events.enabled=true",
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
    private ConsumerFactory<String, String> consumerFactory;

    @Mock UriInfo uriInfoMock;
    @Mock MultivaluedMap<String, String> queryParamsMock;
    @Mock HttpHeaders headersMock;

    @Before
    public void setup() {
        when(headersMock.getAcceptableMediaTypes()).thenReturn(Collections.singletonList(MediaType.APPLICATION_JSON_TYPE));
        when(uriInfoMock.getQueryParameters(anyBoolean())).thenReturn(queryParamsMock);
        when(queryParamsMock.getFirst("depth")).thenReturn("0");
        when(headersMock.getRequestHeader("aai-request-context")).thenReturn(null);
    }

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
    @Ignore
    // only works when aai.jms.enable=true in aaiconfig.properties
    public void thatEventsAreBeingCreated() throws AAIException, IOException {
        Consumer<String, String> consumer = consumerFactory.createConsumer();
        consumer.subscribe(Collections.singletonList("AAI-EVENT"));

        traversalUriHttpEntry.setHttpEntryProperties(new SchemaVersion("v14"));
        String pserverUri = "/aai/v14/cloud-infrastructure/pservers/pserver/pserver1";
        String entity = new String(Files.readAllBytes(Paths.get("src/test/resources/payloads/templates/pserver.json"))).replace("${hostname}", "pserver1");
        DBRequest dbRequest = createDBRequest(pserverUri, entity);
        List<DBRequest> dbRequests = new ArrayList<>();
        dbRequests.add(dbRequest);

        traversalUriHttpEntry.process(dbRequests, "test");

        ConsumerRecords<String, String> consumerRecords = KafkaTestUtils.getRecords(consumer, 100000);
        assertFalse(consumerRecords.isEmpty());
        String expectedResponse = PayloadUtil.getExpectedPayload("pserver-event.json");

        consumerRecords.forEach(consumerRecord -> {
            JSONAssert.assertEquals(expectedResponse, consumerRecord.value(), JSONCompareMode.LENIENT);
        });
    }

    @SneakyThrows
    private DBRequest createDBRequest(String uri, String entity) {
        TransactionalGraphEngine dbEngine = traversalUriHttpEntry.getDbEngine();
        Loader loader = traversalUriHttpEntry.getLoader();
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
