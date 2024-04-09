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

 package org.onap.aai.web;

 import java.util.HashMap;
 import java.util.Map;
 
 import javax.annotation.PostConstruct;
 
 import org.apache.activemq.ActiveMQConnectionFactory;
 import org.apache.activemq.broker.BrokerService;
 import org.apache.activemq.command.ActiveMQQueue;
 import org.apache.kafka.clients.producer.ProducerConfig;
import org.onap.aai.kafka.AAIKafkaEventJMSConsumer;
import org.onap.aai.kafka.AAIKafkaEventJMSProducer;
import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.beans.factory.annotation.Value;
 import org.springframework.context.ApplicationContext;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 import org.springframework.context.annotation.Profile;
 import org.springframework.jms.connection.CachingConnectionFactory;
 import org.springframework.jms.core.JmsTemplate;
 import org.springframework.jms.listener.DefaultMessageListenerContainer;
 import org.springframework.kafka.core.DefaultKafkaProducerFactory;
 import org.springframework.kafka.core.KafkaTemplate;
 import org.springframework.kafka.core.ProducerFactory;
 
 @Profile("kafka")
 @Configuration
 public class KafkaConfig {
 
     @Autowired
     private ApplicationContext ctx;
 
 
     @Value("${jms.bind.address}")
     private String bindAddress;
 
     @Value("${spring.kafka.producer.bootstrap-servers}")
     private String bootstrapServers;
 
     @Value("${spring.kafka.producer.properties.security.protocol}")
     private String securityProtocol;
 
     @Value("${spring.kafka.producer.properties.sasl.mechanism}")
     private String saslMechanism;
 
     @Value("${spring.kafka.producer.properties.sasl.jaas.config}")
     private String saslJaasConfig;
 
     @Value("${spring.kafka.producer.retries}")
     private Integer retries;
 
     private static final Logger logger = LoggerFactory.getLogger(KafkaConfig.class);
 
     @PostConstruct
     public void init() {
         System.setProperty("activemq.tcp.url", bindAddress);
     }
 
     @Bean(destroyMethod = "stop")
     public BrokerService brokerService() throws Exception {
 
         BrokerService broker = new BrokerService();
         broker.addConnector(bindAddress);
         broker.setPersistent(false);
         broker.setUseJmx(false);
         broker.setSchedulerSupport(false);
         broker.start();
 
         return broker;
     }
 
     @Bean(name = "connectionFactory")
     public ActiveMQConnectionFactory activeMQConnectionFactory() {
         return new ActiveMQConnectionFactory(bindAddress);
     }
 
     @Bean
     public CachingConnectionFactory cachingConnectionFactory() {
         return new CachingConnectionFactory(activeMQConnectionFactory());
     }
 
     @Bean(name = "destinationQueue")
     public ActiveMQQueue activeMQQueue() {
         return new ActiveMQQueue("IN_QUEUE");
     }
 
     @Bean
     public JmsTemplate jmsTemplate() {
         JmsTemplate jmsTemplate = new JmsTemplate();
 
         jmsTemplate.setConnectionFactory(activeMQConnectionFactory());
         jmsTemplate.setDefaultDestination(activeMQQueue());
 
         return jmsTemplate;
     }
 
     @Bean
     public AAIKafkaEventJMSProducer jmsProducer() {
         return new AAIKafkaEventJMSProducer();
     }
 
     @Bean(name = "jmsConsumer")
     public AAIKafkaEventJMSConsumer jmsConsumer() throws Exception {
         return new AAIKafkaEventJMSConsumer(ctx.getEnvironment(),kafkaTemplate());
     }
 
     @Bean
     public DefaultMessageListenerContainer defaultMessageListenerContainer() throws Exception {
 
         DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
 
         messageListenerContainer.setConnectionFactory(cachingConnectionFactory());
         messageListenerContainer.setDestinationName("IN_QUEUE");
         messageListenerContainer.setMessageListener(jmsConsumer());
 
         return messageListenerContainer;
     }
 
     @Bean
     public ProducerFactory<String, String> producerFactory() throws Exception {
         Map<String, Object> props = new HashMap<>();
         if(bootstrapServers == null){
             logger.error("Environment Variable " + bootstrapServers + " is missing");
             throw new Exception("Environment Variable " + bootstrapServers + " is missing");
         }
         else{
         props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
         }
         if(saslJaasConfig == null){
             logger.info("Not using any authentication for kafka interaction");
         }
         else{
             logger.info("Using authentication provided by kafka interaction");
         // Strimzi Kafka security properties
         props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
         props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
         props.put("security.protocol", securityProtocol);
         props.put("sasl.mechanism", saslMechanism);
         props.put("sasl.jaas.config", saslJaasConfig);
         props.put(ProducerConfig.RETRIES_CONFIG, Integer.toString(retries));
         props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,"5");
         }
 
         return new DefaultKafkaProducerFactory<>(props);
     }
 
     @Bean
     public KafkaTemplate<String, String> kafkaTemplate() throws Exception {
         return new KafkaTemplate<>(producerFactory());
     }
 }
 