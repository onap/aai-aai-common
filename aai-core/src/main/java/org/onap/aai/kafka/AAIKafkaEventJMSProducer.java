/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 *  Modifications Copyright © 2018 IBM.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.kafka;

import org.json.JSONObject;
import org.onap.aai.domain.notificationEvent.NotificationEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AAIKafkaEventJMSProducer implements MessageProducer {

    @Value("${aai.events.enabled:true}") private boolean eventsEnabled;
    private final JmsTemplate jmsTemplate;

    public void sendMessageToDefaultDestination(String msg) {
        if (eventsEnabled) {
            jmsTemplate.convertAndSend(msg);
        }
    }

    public void sendMessageToDefaultDestination(JSONObject finalJson) {
        sendMessageToDefaultDestination(finalJson.toString());
    }

    @Override
    public void sendMessageToDefaultDestination(NotificationEvent notificationEvent) {
        sendMessageToDefaultDestination(notificationEvent.toString());
    }
}
