/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.aai.util.delta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.*;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.dmaap.AAIDmaapEventJMSProducer;
import org.onap.aai.dmaap.MessageProducer;
import org.onap.aai.util.AAIConfig;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class DeltaEvents {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeltaEvents.class);

    private static final Gson gson = new GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
        .create();

    private String transId;
    private String sourceName;
    private String eventVersion = "v1";
    private String schemaVersion;
    private Map<String, ObjectDelta> objectDeltas;

    private MessageProducer messageProducer;

    public DeltaEvents(String transId, String sourceName, String schemaVersion, Map<String, ObjectDelta> objectDeltas) {
        this(transId, sourceName, schemaVersion, objectDeltas, new AAIDmaapEventJMSProducer());
    }

    public DeltaEvents(String transId, String sourceName, String schemaVersion, Map<String, ObjectDelta> objectDeltas, MessageProducer messageProducer) {
        this.transId = transId;
        this.sourceName = sourceName;
        this.schemaVersion = schemaVersion;
        this.objectDeltas = objectDeltas;
        this.messageProducer = messageProducer;
    }

    public boolean triggerEvents() {
        if (objectDeltas.isEmpty()) {
            return false;
        }

        JsonObject finalJson = new JsonObject();
        finalJson.addProperty("event-topic", "DELTA");
        finalJson.addProperty("transId", transId);
        finalJson.addProperty("fromAppId", sourceName);
        finalJson.addProperty("fullId", "");
        finalJson.add("aaiEventPayload", buildEvent());

        this.messageProducer.sendMessageToDefaultDestination(finalJson.toString());
        return true;
    }

    private JsonObject buildEvent() {
        JsonObject event = new JsonObject();
        event.addProperty("cambria.partition", this.getPartition());
        event.add("event-header", getHeader());
        event.add("entities", gson.toJsonTree(objectDeltas.values()));
        return event;
    }

    private String getPartition() {
        return "DELTA";
    }

    private JsonObject getHeader() {
        ObjectDelta first = objectDeltas.values().iterator().next();
        JsonObject header = new JsonObject();
        header.addProperty("id", this.transId);
        header.addProperty("timestamp", this.getTimeStamp(first.getTimestamp()));
        header.addProperty("source-name", this.sourceName);
        header.addProperty("domain", this.getDomain());
        header.addProperty("event-type", this.getEventType());
        header.addProperty("event-version", this.eventVersion);
        header.addProperty("schema-version", this.schemaVersion);
        header.addProperty("action", first.getAction().toString());
        header.addProperty("entity-type", this.getEntityType(first));
        header.addProperty("entity-link", first.getUri());
        header.addProperty("entity-uuid", this.getUUID(first));

        return header;
    }

    private String getUUID(ObjectDelta objectDelta) {
        return (String) objectDelta.getPropertyDeltas().get(AAIProperties.AAI_UUID).getValue();
    }

    private String getEntityType(ObjectDelta objectDelta) {
        return (String) objectDelta.getPropertyDeltas().get(AAIProperties.NODE_TYPE).getValue();
    }

    private String getEventType() {
        return "DELTA";
    }

    private String getDomain() {
        return AAIConfig.get("aai.notificationEvent.default.domain", "UNK");
    }

    /**
     * Given Long timestamp convert to format YYYYMMdd-HH:mm:ss:SSS
     * @param timestamp milliseconds since epoc
     * @return long timestamp in format YYYYMMdd-HH:mm:ss:SSS
     */
    private String getTimeStamp(long timestamp) {
        //SimpleDateFormat is not thread safe new instance needed
        DateFormat df = new SimpleDateFormat("YYYYMMdd-HH:mm:ss:SSS");
        return df.format(new Date(timestamp));
    }
}
