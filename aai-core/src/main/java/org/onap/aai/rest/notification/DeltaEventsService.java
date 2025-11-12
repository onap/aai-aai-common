/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2025 Deutsche Telekom. All rights reserved.
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

package org.onap.aai.rest.notification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.domain.deltaEvent.DeltaEvent;
import org.onap.aai.domain.notificationEvent.NotificationEvent.EventHeader;
import org.onap.aai.util.AAIConfig;
import org.onap.aai.util.delta.DeltaAction;
import org.onap.aai.util.delta.DeltaEvents;
import org.onap.aai.util.delta.ObjectDelta;
import org.onap.aai.util.delta.PropertyDelta;
import org.springframework.stereotype.Service;

/**
 * Service for processing and sending AAI Delta Events.
 */
@Service
public class DeltaEventsService {
    
    public boolean triggerEvents(DeltaEvents events) {
        if (events.getObjectDeltas().isEmpty()) {
            return false;
        } 
        ObjectDelta first = getFirstDelta(events.getObjectDeltas());
        if (first == null) {
            return false;
        }
        if (!events.getAllowedActions().contains(first.getAction().toString())) {
            return false;
        }
        //If relationship flag is disabled, then no relationship delta event or fields in delta events.
        if (!events.isRelationshipDeltaEnabled() && isOnlyStandardVertexUpdate(first)) {
            return false;
        }
        events.getDeltaProducer().sendNotification(buildEvent(events));
        return true;
    }

    /**
     * Checks if the event is a relationship change delta event
     * Checks if the update is only on standard fields 
     * as standard fields indicate relationship change delta events
     */
    private boolean isOnlyStandardVertexUpdate(ObjectDelta firstEntity) {

        // Relationship change delta only triggers update event
        if (!DeltaAction.UPDATE.equals(firstEntity.getAction()))
            return false;

        Set<String> standardFields = AAIProperties.getStandardFields();

        if (firstEntity.getPropertyDeltas() == null || firstEntity.getPropertyDeltas().isEmpty()) {
            return false;
        }

        for (Map.Entry<String, PropertyDelta> entry : firstEntity.getPropertyDeltas().entrySet()) {
            String key = entry.getKey();
            DeltaAction action = entry.getValue().getAction();

            // If any non-standard property is updated, return false
            if (action == DeltaAction.UPDATE && !standardFields.contains(key)) {
                return false;
            }
        }
        
        return true;
    }

    private DeltaEvent buildEvent(DeltaEvents events) {
        DeltaEvent deltaEvent = new DeltaEvent();
        deltaEvent.setCambriaPartition(getPartition());
        deltaEvent.setEventHeader(getHeader(events));
        deltaEvent.setEntities(events.getObjectDeltas().values());
        return deltaEvent;
    }

    private String getPartition() {
        return "DELTA";
    }

    private EventHeader getHeader(DeltaEvents data) {
        ObjectDelta first = getFirstDelta(data.getObjectDeltas());
        EventHeader header = new EventHeader();
        header.setId(data.getTransactionId());
        header.setTimestamp(this.getTimeStamp(first.getTimestamp()));
        header.setSourceName(data.getSourceName());
        header.setDomain(this.getDomain());
        header.setEventType(this.getEventType());
        header.setVersion(data.getSchemaVersion());
        header.setAction(first.getAction().toString());
        header.setEntityType(this.getEntityType(first));
        header.setEntityLink(first.getUri());
        header.setEntityUuid(this.getUUID(first));
        return header;
    }

    private ObjectDelta getFirstDelta(Map<String, ObjectDelta> objectDeltas) {
        return objectDeltas.values().iterator().next();
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
     */
    private String getTimeStamp(long timestamp) {
        // SimpleDateFormat is not thread safe new instance needed
        DateFormat df = new SimpleDateFormat("yyyyMMdd-HH:mm:ss:SSS");
        return df.format(new Date(timestamp));
    }
}