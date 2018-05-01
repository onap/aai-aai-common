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
package org.onap.aai.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.onap.aai.dmaap.AAIDmaapEventJMSConsumer;
import org.onap.aai.dmaap.AAIDmaapEventJMSProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.annotation.PostConstruct;

@Profile("dmaap")
@Configuration
public class DmaapConfig {

    @Value("${jms.bind.address}")
    private String bindAddress;

    @PostConstruct
    public void init(){
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
    public ActiveMQConnectionFactory activeMQConnectionFactory(){
        return new ActiveMQConnectionFactory(bindAddress);
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory(){
        return new CachingConnectionFactory(activeMQConnectionFactory());
    }

    @Bean(name = "destinationQueue")
    public ActiveMQQueue activeMQQueue(){
        return new ActiveMQQueue("IN_QUEUE");
    }

    @Bean
    public JmsTemplate jmsTemplate(){
        JmsTemplate jmsTemplate = new JmsTemplate();

        jmsTemplate.setConnectionFactory(activeMQConnectionFactory());
        jmsTemplate.setDefaultDestination(activeMQQueue());

        return jmsTemplate;
    }

    @Bean
    public AAIDmaapEventJMSProducer jmsProducer(){
        return new AAIDmaapEventJMSProducer();
    }

    @Bean
    public AAIDmaapEventJMSConsumer jmsConsumer() throws Exception {
        return new AAIDmaapEventJMSConsumer();
    }

    @Bean
    public DefaultMessageListenerContainer defaultMessageListenerContainer() throws Exception {

        DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();

        messageListenerContainer.setConnectionFactory(cachingConnectionFactory());
        messageListenerContainer.setDestinationName("IN_QUEUE");
        messageListenerContainer.setMessageListener(jmsConsumer());

        return messageListenerContainer;
    }
}
