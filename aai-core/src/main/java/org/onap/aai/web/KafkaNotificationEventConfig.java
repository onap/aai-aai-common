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

package org.onap.aai.web;

import java.util.Map;
import java.util.HashMap;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.onap.aai.domain.notificationEvent.NotificationEvent;
import org.onap.aai.kafka.NotificationProducer;
import org.onap.aai.kafka.NotificationProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaNotificationEventConfig {

    private static final Logger logger = LoggerFactory.getLogger(KafkaNotificationEventConfig.class);

    @Value("${jms.bind.address}")
    private String bindAddress;

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.properties.security.protocol}")
    private String securityProtocol;

    @Value("${spring.kafka.producer.properties.sasl.mechanism}")
    private String saslMechanism;

    @Value("${spring.kafka.producer.properties.sasl.jaas.config:#{null}}")
    private String saslJaasConfig;

    @Value("${spring.kafka.producer.retries:3}")
    private String retries;

    private Map<String, Object> buildKafkaProperties() throws Exception {
        Map<String, Object> props = new HashMap<>();
        if (bootstrapServers == null) {
            logger.error("Environment Variable " + bootstrapServers + " is missing");
            throw new Exception("Environment Variable " + bootstrapServers + " is missing");
        } else {
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        }
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.RETRIES_CONFIG, retries);
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");

        if (saslJaasConfig == null) {
            logger.info("Not using any authentication for kafka interaction");
        } else {
            logger.info("Using authentication provided by kafka interaction");
            // Strimzi Kafka security properties
            props.put("security.protocol", securityProtocol);
            props.put("sasl.mechanism", saslMechanism);
            props.put("sasl.jaas.config", saslJaasConfig);
        }
        return props;
    }

    @Bean
    public ProducerFactory<String, NotificationEvent> notificationEventProducerFactory() throws Exception {
        Map<String, Object> props = buildKafkaProperties();

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, NotificationEvent> kafkaNotificationEventTemplate(ProducerFactory<String, NotificationEvent> producerFactory) throws Exception {
      try {

        return new KafkaTemplate<>(producerFactory);
      } catch (Exception e) {
        String smth = "";
        return null;
      }
    }

    @Bean
    public NotificationProducer notificationProducer(KafkaTemplate<String,NotificationEvent> kafkaTemplate) {
        return new NotificationProducerService(kafkaTemplate);
    }
}
