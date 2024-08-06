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

package org.onap.aai.util;

import java.util.Iterator;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.onap.aai.domain.notificationEvent.NotificationEvent;
import org.onap.aai.domain.notificationEvent.NotificationEvent.EventHeader;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.kafka.AAIKafkaEventJMSProducer;
import org.onap.aai.kafka.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;

public class StoreNotificationEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(StoreNotificationEvent.class);

    @Autowired JmsTemplate jmsTemplate;

    private final MessageProducer messageProducer;
    private String fromAppId = "";
    private String transId = "";
    private final String transactionId;
    private final String sourceOfTruth;

    private ApplicationContext context;
    private Environment env;

    /**
     * Instantiates a new store notification event.
     */
    public StoreNotificationEvent(String transactionId, String sourceOfTruth) {
        this.messageProducer = new AAIKafkaEventJMSProducer(jmsTemplate);
        this.transactionId = transactionId;
        this.sourceOfTruth = sourceOfTruth;
    }

    public StoreNotificationEvent(MessageProducer producer, String transactionId, String sourceOfTruth) {
        this.messageProducer = producer;
        this.transactionId = transactionId;
        this.sourceOfTruth = sourceOfTruth;
    }

    /**
     * Store event.
     *
     * @param eh
     *        the eh
     * @param obj
     *        the obj
     * @throws AAIException
     *         the AAI exception
     */
    public NotificationEvent storeEventAndSendToJms(NotificationEvent.EventHeader eh, Object obj) throws AAIException {

        if (obj == null) {
            throw new AAIException("AAI_7350");
        }

        org.onap.aai.domain.notificationEvent.ObjectFactory factory =
                new org.onap.aai.domain.notificationEvent.ObjectFactory();

        NotificationEvent ne = factory.createNotificationEvent();

        if (eh.getId() == null) {
            eh.setId(genDate2() + "-" + UUID.randomUUID().toString());
        }
        if (eh.getTimestamp() == null) {
            eh.setTimestamp(genDate());
        }

        // there's no default, but i think we want to put this in hbase?

        if (eh.getEntityLink() == null) {
            eh.setEntityLink("UNK");
        }

        if (eh.getAction() == null) {
            eh.setAction("UNK");
        }

        if (eh.getEventType() == null) {
            eh.setEventType(AAIConfig.get("aai.notificationEvent.default.eventType", "UNK"));
        }

        if (eh.getDomain() == null) {
            eh.setDomain(AAIConfig.get("aai.notificationEvent.default.domain", "UNK"));
        }

        if (eh.getSourceName() == null) {
            eh.setSourceName(AAIConfig.get("aai.notificationEvent.default.sourceName", "UNK"));
        }

        if (eh.getSequenceNumber() == null) {
            eh.setSequenceNumber(AAIConfig.get("aai.notificationEvent.default.sequenceNumber", "UNK"));
        }

        if (eh.getSeverity() == null) {
            eh.setSeverity(AAIConfig.get("aai.notificationEvent.default.severity", "UNK"));
        }

        if (eh.getVersion() == null) {
            eh.setVersion(AAIConfig.get("aai.notificationEvent.default.version", "UNK"));
        }

        ne.setCambriaPartition(AAIConstants.UEB_PUB_PARTITION_AAI);
        ne.setEventHeader(eh);
        ne.setEntity(obj);

        try {
            PojoUtils pu = new PojoUtils();
            String entityJson = pu.getJsonFromObject(ne);
            sendToKafkaJmsQueue(ne);
            return ne;
        } catch (Exception e) {
            throw new AAIException("AAI_7350", e);
        }
    }

    public String storeEventOnly(Loader loader, Introspector eventHeader, Introspector obj) throws AAIException {
        if (obj == null) {
            throw new AAIException("AAI_7350");
        }

        try {
            final Introspector notificationEvent = loader.introspectorFromName("notification-event");

            if (eventHeader.getValue("id") == null) {
                eventHeader.setValue("id", genDate2() + "-" + UUID.randomUUID().toString());
            }

            if (eventHeader.getValue("timestamp") == null) {
                eventHeader.setValue("timestamp", genDate());
            }

            if (eventHeader.getValue("entity-link") == null) {
                eventHeader.setValue("entity-link", "UNK");
            }

            if (eventHeader.getValue("action") == null) {
                eventHeader.setValue("action", "UNK");
            }

            if (eventHeader.getValue("event-type") == null) {
                eventHeader.setValue("event-type", AAIConfig.get("aai.notificationEvent.default.eventType", "UNK"));
            }

            if (eventHeader.getValue("domain") == null) {
                eventHeader.setValue("domain", AAIConfig.get("aai.notificationEvent.default.domain", "UNK"));
            }

            if (eventHeader.getValue("source-name") == null) {
                eventHeader.setValue("source-name", AAIConfig.get("aai.notificationEvent.default.sourceName", "UNK"));
            }

            if (eventHeader.getValue("sequence-number") == null) {
                eventHeader.setValue("sequence-number",
                        AAIConfig.get("aai.notificationEvent.default.sequenceNumber", "UNK"));
            }

            if (eventHeader.getValue("severity") == null) {
                eventHeader.setValue("severity", AAIConfig.get("aai.notificationEvent.default.severity", "UNK"));
            }

            if (eventHeader.getValue("version") == null) {
                eventHeader.setValue("version", AAIConfig.get("aai.notificationEvent.default.version", "UNK"));
            }

            if (notificationEvent.getValue("cambria-partition") == null) {
                notificationEvent.setValue("cambria-partition",
                        AAIConfig.get("aai.notificationEvent.default.partition", AAIConstants.UEB_PUB_PARTITION_AAI));
            }

            notificationEvent.setValue("event-header", eventHeader.getUnderlyingObject());
            notificationEvent.setValue("entity", obj.getUnderlyingObject());

            String entityJson = notificationEvent.marshal(false);
            JSONObject entityJsonObject = new JSONObject(entityJson);

            JSONObject entityJsonObjectUpdated = new JSONObject();

            JSONObject entityHeader = entityJsonObject.getJSONObject("event-header");
            String cambriaPartition = entityJsonObject.getString("cambria.partition");

            entityJsonObject.remove("event-header");
            entityJsonObject.remove("cambria.partition");

            entityJsonObjectUpdated.put("event-header", entityHeader);
            entityJsonObjectUpdated.put("cambria.partition", cambriaPartition);

            Iterator<String> iter = entityJsonObject.keys();
            JSONObject entity = new JSONObject();
            if (iter.hasNext()) {
                entity = entityJsonObject.getJSONObject(iter.next());
            }

            entityJsonObjectUpdated.put("entity", entity);

            return entityJsonObjectUpdated.toString();
        } catch (JSONException e) {
            throw new AAIException("AAI_7350", e);
        } catch (AAIUnknownObjectException e) {
            throw new AAIException("AAI_7350", e);
        }
    }

    public NotificationEvent storeEventAndSendToJms(Loader loader, Introspector eventHeaderIntrospector, Introspector obj)
            throws AAIException {
        if (obj == null) {
            throw new AAIException("AAI_7350");
        }

        try {
            // final Introspector notificationEventIntrospector = loader.introspectorFromName("notification-event");

            EventHeader eventHeader = new EventHeader();
            if (eventHeaderIntrospector.getValue("id") == null) {
                String id = genDate2() + "-" + UUID.randomUUID().toString();
                eventHeader.setId(id);
            }

            if (eventHeaderIntrospector.getValue("timestamp") == null) {
                eventHeader.setTimestamp(genDate());
            }

            if (eventHeaderIntrospector.getValue("entity-link") == null) {
                eventHeader.setEntityLink("UNK");
            }

            if (eventHeaderIntrospector.getValue("action") == null) {
                eventHeader.setAction("UNK");
            }

            if (eventHeaderIntrospector.getValue("event-type") == null) {
                String eventType = AAIConfig.get("aai.notificationEvent.default.eventType", "UNK");
            }

            if (eventHeaderIntrospector.getValue("domain") == null) {
                String domain = AAIConfig.get("aai.notificationEvent.default.domain", "UNK");
                eventHeader.setDomain(domain);
            }

            if (eventHeaderIntrospector.getValue("source-name") == null) {
                String sourceName = AAIConfig.get("aai.notificationEvent.default.sourceName", "UNK");
                eventHeader.setSourceName(sourceName);
            }

            if (eventHeaderIntrospector.getValue("sequence-number") == null) {
                String sequenceNumber = AAIConfig.get("aai.notificationEvent.default.sequenceNumber", "UNK");
                eventHeader.setSequenceNumber(sequenceNumber);
            }

            if (eventHeaderIntrospector.getValue("severity") == null) {
                String severity = AAIConfig.get("aai.notificationEvent.default.severity", "UNK");
                eventHeader.setSeverity(severity);
            }

            if (eventHeaderIntrospector.getValue("version") == null) {
                String version = AAIConfig.get("aai.notificationEvent.default.version", "UNK");
                eventHeader.setVersion(version);
            }

            NotificationEvent notificationEvent = new NotificationEvent();
            if (notificationEvent.getCambriaPartition() == null) {
                String cambriaPartition = AAIConfig.get("aai.notificationEvent.default.partition", AAIConstants.UEB_PUB_PARTITION_AAI);
                notificationEvent.setCambriaPartition(cambriaPartition);
            }

            notificationEvent.setEntity(obj.getUnderlyingObject());
            sendToKafkaJmsQueue(notificationEvent);
            return notificationEvent;
        } catch (JSONException e) {
            throw new AAIException("AAI_7350", e);
        }
    }

    private void sendToKafkaJmsQueue(NotificationEvent entityString) throws JSONException {
        messageProducer.sendMessageToDefaultDestination(entityString);
    }

    /**
     * Gen date.
     *
     * @return the string
     */
    public static String genDate() {
        FormatDate fd = new FormatDate("YYYYMMdd-HH:mm:ss:SSS");
        return fd.getDateTime();
    }

    /**
     * Gen date 2.
     *
     * @return the string
     */
    public static String genDate2() {
        FormatDate fd = new FormatDate("YYYYMMddHHmmss");
        return fd.getDateTime();
    }

}
