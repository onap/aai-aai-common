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

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;
import org.mockito.Mock;
import org.onap.aai.AAISetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("kafka")
@Import(KafkaTestConfiguration.class)
@EmbeddedKafka(partitions = 1, topics = { "AAI-EVENT" })
@TestPropertySource(
        properties = {
          "jms.bind.address=tcp://localhost:61647"
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
        Consumer<String, String> consumer = consumerFactory.createConsumer("some-consumer", null);

        consumer.subscribe(Collections.singletonList("AAI-EVENT"));

        messageProducer.sendMessageToDefaultDestination("someMessage");

        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(3));
        List<ConsumerRecord<String, String>> messages = records.records(new TopicPartition("AAI-EVENT", 0));
        records.forEach(consumerRecord -> {
            assertNotNull(consumerRecord);
            assertEquals("someMessage", consumerRecord.value());
            assertTrue(false);
            // consumerRecord.headers().forEach(header -> {
            //     assertEquals("some", header.key());
            // });
        });

    }

}
