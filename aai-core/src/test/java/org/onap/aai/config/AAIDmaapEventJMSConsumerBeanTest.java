/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017-2018 Nokia. All rights reserved.
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

import static org.junit.Assert.assertNotNull;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.dmaap.AAIDmaapEventJMSConsumer;
import org.onap.aai.dmaap.AAIDmaapEventJMSProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * Created by Bogumil Zebek on 6/25/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DmaapConfig.class,
    EventClientPublisher.class}, loader = AnnotationConfigContextLoader.class)
@ActiveProfiles(profiles = "dmaap")
public class AAIDmaapEventJMSConsumerBeanTest {

    @MockBean
    AAIDmaapEventJMSProducer jmsProducer;

    @MockBean
    BrokerService brokerService;

    @MockBean
    ActiveMQConnectionFactory activeMQConnectionFactory;

    @Autowired
    @Qualifier("jmsConsumer")
    AAIDmaapEventJMSConsumer jmsConsumer;

    @Autowired
    private ApplicationContext ctx;

    @Test
    public void shouldCreateJmsConsumerProperly_allDependenciesInjectedByContainer() {
        assertNotNull(jmsConsumer);
    }

}
