/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017-2018 Nokia Intellectual Property. All rights reserved.
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
package org.onap.aai.dmaap;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

/**
 * Created by Bogumil Zebek on 6/25/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class AAIDmaapEventJMSProducerTest {

    private static final JmsTemplate JMS_TEMPLATE_NOT_AVAILABLE = null;
    @Mock
    private JmsTemplate jmsTemplate;
    @Mock
    private CachingConnectionFactory cachingConnectionFactory;

    @Test
    public void shouldDoNotSendMessageWhenJMSTemplateIsNotAvailable() {
        try {
            AAIDmaapEventJMSProducer aaiDmaapEventJMSProducer = new AAIDmaapEventJMSProducer(
                Optional.ofNullable(JMS_TEMPLATE_NOT_AVAILABLE)
            );

            aaiDmaapEventJMSProducer.sendMessageToDefaultDestination(new JSONObject());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void shouldSendMessageWhenJMSTemplateIsAvailable() {

        // given
        when(jmsTemplate.getConnectionFactory()).thenReturn(cachingConnectionFactory);

        AAIDmaapEventJMSProducer aaiDmaapEventJMSProducer = new AAIDmaapEventJMSProducer(
            Optional.ofNullable(jmsTemplate)
        );

        // when
        aaiDmaapEventJMSProducer.sendMessageToDefaultDestination(new JSONObject());

        // then
        verify(jmsTemplate).convertAndSend(anyString());
        verify(cachingConnectionFactory, only()).destroy();
    }
}