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
 import org.onap.aai.config.SpringContextAware;
 import org.onap.aai.domain.notificationEvent.NotificationEvent;
 import org.onap.aai.util.AAIConfig;
 import org.springframework.jms.core.JmsTemplate;
 import org.springframework.stereotype.Service;

 import com.fasterxml.jackson.core.JsonProcessingException;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.json.JsonMapper;

 import lombok.RequiredArgsConstructor;
 import lombok.extern.slf4j.Slf4j;

 @Slf4j
 @Service
 @RequiredArgsConstructor
 public class AAIKafkaEventJMSProducer implements MessageProducer {

     private boolean eventsEnabled = "true".equals(AAIConfig.get("aai.jms.enable", "true"));
     private JmsTemplate jmsTemplate;
     private static final ObjectMapper mapper = new JsonMapper();

     public AAIKafkaEventJMSProducer(JmsTemplate jmsTemplate) {
         this.jmsTemplate = jmsTemplate;
     }

     public void sendMessageToDefaultDestination(String msg) {
         if (eventsEnabled) {
             if(jmsTemplate == null) {
                 this.jmsTemplate = SpringContextAware.getBean(JmsTemplate.class);
             }
             jmsTemplate.convertAndSend(msg);
         }
     }

     public void sendMessageToDefaultDestination(JSONObject finalJson) {
         sendMessageToDefaultDestination(finalJson.toString());
     }
 }
