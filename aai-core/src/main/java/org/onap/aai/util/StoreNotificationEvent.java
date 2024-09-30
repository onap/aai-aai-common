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

import java.io.StringWriter;
import java.util.Iterator;
import java.util.UUID;

import javax.xml.bind.Marshaller;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.json.JSONException;
import org.json.JSONObject;
import org.onap.aai.domain.notificationEvent.NotificationEvent;
import org.onap.aai.domain.notificationEvent.NotificationEvent.EventHeader;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.kafka.AAIKafkaEventProducer;
import org.onap.aai.kafka.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

public class StoreNotificationEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(StoreNotificationEvent.class);

    @Autowired JmsTemplate jmsTemplate;
    @Autowired KafkaTemplate<String, String> kafkaTemplate;

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final PojoUtils pojoUtils = new PojoUtils();
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
        this.messageProducer = new AAIKafkaEventProducer(kafkaTemplate);
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
     * @param eventHeader
     *        the eh
     * @param obj
     *        the obj
     * @throws AAIException
     *         the AAI exception
     */
    public String storeEventAndSendToJms(NotificationEvent.EventHeader eventHeader, Object obj) throws AAIException {

        if (obj == null) {
            throw new AAIException("AAI_7350");
        }

        org.onap.aai.domain.notificationEvent.ObjectFactory factory =
                new org.onap.aai.domain.notificationEvent.ObjectFactory();

        org.onap.aai.domain.notificationEvent.NotificationEvent notificationEvent = factory.createNotificationEvent();

        if (eventHeader.getId() == null) {
            eventHeader.setId(genDate2() + "-" + UUID.randomUUID().toString());
        }
        if (eventHeader.getTimestamp() == null) {
            eventHeader.setTimestamp(genDate());
        }

        // there's no default, but i think we want to put this in hbase?

        if (eventHeader.getEntityLink() == null) {
            eventHeader.setEntityLink("UNK");
        }

        if (eventHeader.getAction() == null) {
            eventHeader.setAction("UNK");
        }

        if (eventHeader.getEventType() == null) {
            eventHeader.setEventType(AAIConfig.get("aai.notificationEvent.default.eventType", "UNK"));
        }

        if (eventHeader.getDomain() == null) {
            eventHeader.setDomain(AAIConfig.get("aai.notificationEvent.default.domain", "UNK"));
        }

        if (eventHeader.getSourceName() == null) {
            eventHeader.setSourceName(AAIConfig.get("aai.notificationEvent.default.sourceName", "UNK"));
        }

        if (eventHeader.getSequenceNumber() == null) {
            eventHeader.setSequenceNumber(AAIConfig.get("aai.notificationEvent.default.sequenceNumber", "UNK"));
        }

        if (eventHeader.getSeverity() == null) {
            eventHeader.setSeverity(AAIConfig.get("aai.notificationEvent.default.severity", "UNK"));
        }

        if (eventHeader.getVersion() == null) {
            eventHeader.setVersion(AAIConfig.get("aai.notificationEvent.default.version", "UNK"));
        }

        notificationEvent.setCambriaPartition(AAIConstants.UEB_PUB_PARTITION_AAI);
        notificationEvent.setEventHeader(eventHeader);
        notificationEvent.setEntity(obj);

        try {

            String entityJson = pojoUtils.getJsonFromObject(notificationEvent);
            sendToKafkaJmsQueue(notificationEvent);
            return entityJson;
        } catch (Exception e) {
            throw new AAIException("AAI_7350", e);
        }
    }

    /**
     * Store dynamic event.
     *
     * @param notificationJaxbContext
     *        the notification jaxb context
     * @param notificationVersion
     *        the notification version
     * @param eventHeader
     *        the event header
     * @param obj
     *        the obj
     * @throws AAIException
     *         the AAI exception
     */
    public void storeDynamicEvent(DynamicJAXBContext notificationJaxbContext, String notificationVersion,
            DynamicEntity eventHeader, DynamicEntity obj) throws AAIException {

        if (obj == null) {
            throw new AAIException("AAI_7350");
        }

        DynamicEntity notificationEvent = notificationJaxbContext
                .getDynamicType("inventory.aai.onap.org." + notificationVersion + ".NotificationEvent")
                .newDynamicEntity();

        if (eventHeader.get("id") == null) {
            eventHeader.set("id", genDate2() + "-" + UUID.randomUUID().toString());
        }

        if (eventHeader.get("timestamp") == null) {
            eventHeader.set("timestamp", genDate());
        }

        if (eventHeader.get("entityLink") == null) {
            eventHeader.set("entityLink", "UNK");
        }

        if (eventHeader.get("action") == null) {
            eventHeader.set("action", "UNK");
        }

        if (eventHeader.get("eventType") == null) {
            eventHeader.set("eventType", AAIConfig.get("aai.notificationEvent.default.eventType", "UNK"));
        }

        if (eventHeader.get("domain") == null) {
            eventHeader.set("domain", AAIConfig.get("aai.notificationEvent.default.domain", "UNK"));
        }

        if (eventHeader.get("sourceName") == null) {
            eventHeader.set("sourceName", AAIConfig.get("aai.notificationEvent.default.sourceName", "UNK"));
        }

        if (eventHeader.get("sequenceNumber") == null) {
            eventHeader.set("sequenceNumber", AAIConfig.get("aai.notificationEvent.default.sequenceNumber", "UNK"));
        }

        if (eventHeader.get("severity") == null) {
            eventHeader.set("severity", AAIConfig.get("aai.notificationEvent.default.severity", "UNK"));
        }

        if (eventHeader.get("version") == null) {
            eventHeader.set("version", AAIConfig.get("aai.notificationEvent.default.version", "UNK"));
        }

        if (notificationEvent.get("cambriaPartition") == null) {
            notificationEvent.set("cambriaPartition", AAIConstants.UEB_PUB_PARTITION_AAI);
        }

        notificationEvent.set("eventHeader", eventHeader);
        notificationEvent.set("entity", obj);

        try {
            StringWriter result = new StringWriter();

            Marshaller marshaller = notificationJaxbContext.createMarshaller();
            marshaller.setProperty(org.eclipse.persistence.jaxb.MarshallerProperties.MEDIA_TYPE, "application/json");
            marshaller.setProperty(org.eclipse.persistence.jaxb.MarshallerProperties.JSON_INCLUDE_ROOT, false);
            marshaller.setProperty(org.eclipse.persistence.jaxb.MarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, false);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
            marshaller.marshal(notificationEvent, result);
            String marshalled = result.toString();
            NotificationEvent notificationEvent2 = mapper.readValue(marshalled, NotificationEvent.class);
            this.sendToKafkaJmsQueue(notificationEvent2);

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

    private EventHeader map(Introspector eventHeader) {
        EventHeader header = new EventHeader();
        header.setId(eventHeader.getValue("id"));
        header.setTimestamp(eventHeader.getValue("timestamp"));
        header.setSourceName(eventHeader.getValue("source-name"));
        header.setDomain(eventHeader.getValue("domain"));
        header.setSequenceNumber(eventHeader.getValue("sequence-number"));
        header.setSeverity(eventHeader.getValue("severity"));
        header.setEventType(eventHeader.getValue("event-type"));
        header.setVersion(eventHeader.getValue("version"));
        header.setAction(eventHeader.getValue("action"));
        header.setEntityType(eventHeader.getValue("entity-type"));
        header.setTopEntityType(eventHeader.getValue("top-entity-type"));
        header.setEntityLink(eventHeader.getValue("entity-link"));
        header.setStatus(eventHeader.getValue("status"));
        return header;
    }

    @SneakyThrows
    public String storeEventAndSendToJms(Loader loader, Introspector eventHeader, Introspector obj)
            throws AAIException {
        if (obj == null) {
            throw new AAIException("AAI_7350");
        }

        EventHeader header = map(eventHeader);

        try {
            final Introspector notificationEvent = loader.introspectorFromName("notification-event");
            NotificationEvent not = new NotificationEvent();
            if (eventHeader.getValue("id") == null) {
                String id = genDate2() + "-" + UUID.randomUUID().toString();

                eventHeader.setValue("id", id);
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
            // NotificationEvent notificationEvent2 = mapper.readValue(entityJson, NotificationEvent.class);
            NotificationEvent notificationEvent2 = new NotificationEvent();
            notificationEvent2.setCambriaPartition(AAIConfig.get("aai.notificationEvent.default.partition", AAIConstants.UEB_PUB_PARTITION_AAI));
            notificationEvent2.setEventHeader(header);
            notificationEvent2.setEntity(obj.getUnderlyingObject());
            sendToKafkaJmsQueue(notificationEvent2);
            return entityJson;
        } catch (JSONException e) {
            throw new AAIException("AAI_7350", e);
        } catch (AAIUnknownObjectException e) {
            throw new AAIException("AAI_7350", e);
        }
    }

    private void sendToKafkaJmsQueue(String entityString) throws JSONException {

        JSONObject entityJsonObject = new JSONObject(entityString);

        JSONObject entityJsonObjectUpdated = new JSONObject();
        JSONObject finalJson = new JSONObject();

        JSONObject entityHeader = entityJsonObject.getJSONObject("event-header");
        String cambriaPartition = entityJsonObject.getString("cambria.partition");

        entityJsonObject.remove("event-header");
        entityJsonObject.remove("cambria.partition");

        entityJsonObjectUpdated.put("event-header", entityHeader);
        entityJsonObjectUpdated.put("cambria.partition", cambriaPartition);

        String transId = entityHeader.getString("id");
        String fromAppId = entityHeader.getString("source-name");

        Iterator<String> iter = entityJsonObject.keys();
        JSONObject entity = new JSONObject();
        if (iter.hasNext()) {
            entity = entityJsonObject.getJSONObject(iter.next());
        }

        entityJsonObjectUpdated.put("entity", entity);

        finalJson.put("event-topic", "AAI-EVENT");
        finalJson.put("transId", transId);
        finalJson.put("fromAppId", fromAppId);
        finalJson.put("fullId", "");
        finalJson.put("aaiEventPayload", entityJsonObjectUpdated);

        // here
        messageProducer.sendMessageToDefaultDestination(finalJson);
    }

    @SneakyThrows
    private void sendToKafkaJmsQueue(NotificationEvent notificationEvent) throws JSONException {
        String notificationEventString = mapper.writeValueAsString(notificationEvent);
        messageProducer.sendMessageToDefaultDestination(notificationEventString);
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
