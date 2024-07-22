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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mock;
import org.onap.aai.AAISetup;
import org.onap.aai.PayloadUtil;
import org.onap.aai.restcore.HttpMethod;
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

}
