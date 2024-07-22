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
import org.springframework.kafka.core.KafkaTemplate;


public class AAIKafkaEventJMSConsumer implements MessageListener {

    private static final String EVENT_TOPIC = "event-topic";

    private static final Logger LOGGER = LoggerFactory.getLogger(AAIKafkaEventJMSConsumer.class);

    private Map<String, String> mdcCopy;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public AAIKafkaEventJMSConsumer(KafkaTemplate<String, String> kafkaTemplate) {
        super();
        mdcCopy = MDC.getCopyOfContextMap();
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void onMessage(Message message) {

        if (kafkaTemplate == null) {
            return;
        }

        String jmsMessageText = "";
        String aaiEvent = "";
        JSONObject aaiEventHeader;
        JSONObject aaiEventPayload;
        String transactionId = "";
        String serviceName = "";
        String topicName = "";
        String aaiElsErrorCode = AaiElsErrorCode.SUCCESS;
        String errorDescription = "";

        if (mdcCopy != null) {
            MDC.setContextMap(mdcCopy);
        }

        if (message instanceof TextMessage) {
            AaiDmaapMetricLog metricLog = new AaiDmaapMetricLog();
            try {
                jmsMessageText = ((TextMessage) message).getText();
                JSONObject jsonObject = new JSONObject(jmsMessageText);
                if (jsonObject.has("aaiEventPayload")) {
                    aaiEventPayload = jsonObject.getJSONObject("aaiEventPayload");
                    aaiEvent = aaiEventPayload.toString();
                } else {
                    return;
                }
                if (jsonObject.getString(EVENT_TOPIC) != null) {
                    topicName = jsonObject.getString(EVENT_TOPIC);
                }
                if (aaiEventPayload.has("event-header")) {
                    try {
                        aaiEventHeader = aaiEventPayload.getJSONObject("event-header");
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
                metricLog.pre(topicName, aaiEvent, transactionId, serviceName);

                if ("AAI-EVENT".equals(topicName)) {

                    kafkaTemplate.send(topicName, aaiEvent);

                } else {
                    LOGGER.error(String.format("%s|Event Topic invalid.", topicName));
                }
            } catch (JMSException | JSONException e) {
                aaiElsErrorCode = AaiElsErrorCode.DATA_ERROR;
                errorDescription = e.getMessage();
                ErrorLogHelper.logException(new AAIException("AAI_7350"));
            } catch (Exception e) {
                e.printStackTrace();
                // LOGGER.error();
                LOGGER.error(e.getMessage());
                aaiElsErrorCode = AaiElsErrorCode.AVAILABILITY_TIMEOUT_ERROR;
                errorDescription = e.getMessage();
                String errorMessage = String.format("Error processing message: %s, message payload: %s", e.getMessage(), jmsMessageText);
                ErrorLogHelper.logException(new AAIException("AAI_7304", errorMessage));
            } finally {
                metricLog.post(aaiElsErrorCode, errorDescription);
            }
        }
    }
}
