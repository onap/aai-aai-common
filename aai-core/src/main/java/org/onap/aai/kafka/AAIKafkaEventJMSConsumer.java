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

 import java.util.Map;
 import java.util.Objects;
 
 import javax.jms.JMSException;
 import javax.jms.Message;
 import javax.jms.MessageListener;
 import javax.jms.TextMessage;
 
 import org.json.JSONException;
 import org.json.JSONObject;
 import org.onap.aai.aailog.logs.AaiDmaapMetricLog;
 import org.onap.aai.exceptions.AAIException;
 import org.onap.aai.logging.AaiElsErrorCode;
 import org.onap.aai.logging.ErrorLogHelper;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.slf4j.MDC;
 import org.springframework.core.env.Environment;
 import org.springframework.kafka.core.KafkaTemplate;
 
 public class AAIKafkaEventJMSConsumer implements MessageListener {
 
     private static final String EVENT_TOPIC = "event-topic";
 
     private static final Logger LOGGER = LoggerFactory.getLogger(AAIKafkaEventJMSConsumer.class);
 
     private Environment environment;
     private Map<String, String> mdcCopy;
     private KafkaTemplate<String,String> kafkaTemplate;
 
     public AAIKafkaEventJMSConsumer(Environment environment,KafkaTemplate<String,String> kafkaTemplate) {
         super();
         mdcCopy = MDC.getCopyOfContextMap();
         Objects.nonNull(environment);
         this.environment = environment;
         this.kafkaTemplate=kafkaTemplate;
     }
 
     @Override
     public void onMessage(Message message) {
 
         if (kafkaTemplate == null) {
             return;
         }
 
         String jsmMessageTxt = "";
         String aaiEvent = "";
         JSONObject aaiEventHeader;
         JSONObject joPayload;
         String transactionId = "";
         String serviceName = "";
         String eventName = "";
         String aaiElsErrorCode = AaiElsErrorCode.SUCCESS;
         String errorDescription = "";
 
         if (mdcCopy != null) {
             MDC.setContextMap(mdcCopy);
         }
 
         if (message instanceof TextMessage) {
             AaiDmaapMetricLog metricLog = new AaiDmaapMetricLog();
             try {
                 jsmMessageTxt = ((TextMessage) message).getText();
                 JSONObject jo = new JSONObject(jsmMessageTxt);
                 if (jo.has("aaiEventPayload")) {
                     joPayload = jo.getJSONObject("aaiEventPayload");
                     aaiEvent = joPayload.toString();
                 } else {
                     return;
                 }
                 if (jo.getString(EVENT_TOPIC) != null) {
                     eventName = jo.getString(EVENT_TOPIC);
                 }
                 if (joPayload.has("event-header")) {
                     try {
                         aaiEventHeader = joPayload.getJSONObject("event-header");
                         if (aaiEventHeader.has("id")) {
                             transactionId = aaiEventHeader.get("id").toString();
                         }
                         if (aaiEventHeader.has("entity-link")) {
                             serviceName = aaiEventHeader.get("entity-link").toString();
                         }
                     } catch (JSONException jexc) {
                         // ignore, this is just used for logging
                     }
                 }
                 metricLog.pre(eventName, aaiEvent, transactionId, serviceName);
 
 
                 if ("AAI-EVENT".equals(eventName)) {
                     // restTemplate.exchange(baseUrl + endpoint, HttpMethod.POST, httpEntity, String.class);
                     kafkaTemplate.send(eventName,aaiEvent);
 
                 } else {
                     LOGGER.error(String.format("%s|Event Topic invalid.", eventName));
                 }
             } catch (JMSException | JSONException e) {
                 aaiElsErrorCode = AaiElsErrorCode.DATA_ERROR;
                 errorDescription = e.getMessage();
                 ErrorLogHelper.logException(new AAIException("AAI_7350"));
             } catch (Exception e) {
                 aaiElsErrorCode = AaiElsErrorCode.AVAILABILITY_TIMEOUT_ERROR;
                 errorDescription = e.getMessage();
                 ErrorLogHelper.logException(new AAIException("AAI_7304", jsmMessageTxt));
             } finally {
                 metricLog.post(aaiElsErrorCode, errorDescription);
             }
         }
     }
 }
 