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


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.domain.deltaEvent.DeltaEvent;
import org.onap.aai.domain.notificationEvent.NotificationEvent.EventHeader;
import org.onap.aai.kafka.DeltaProducer;
import org.onap.aai.util.AAIConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class DeltaEvents {
    private final String transId;
    private final String sourceName;
    private final String schemaVersion;
    private final Map<String, ObjectDelta> objectDeltas;

    @Autowired private DeltaProducer deltaProducer;

    public DeltaEvents(String transId, String sourceName, String schemaVersion, Map<String, ObjectDelta> objectDeltas) {
        this.transId = transId;
        this.sourceName = sourceName;
        this.schemaVersion = schemaVersion;
        this.objectDeltas = objectDeltas;
    }

    public boolean triggerEvents() {
        if (objectDeltas.isEmpty()) {
            return false;
        }

        deltaProducer.sendNotification(buildEvent());
        return true;
    }

    private DeltaEvent buildEvent() {
        DeltaEvent deltaEvent = new DeltaEvent();
        deltaEvent.setCambriaPartition(getPartition());
        deltaEvent.setEventHeader(getHeader());
        deltaEvent.setEntities(objectDeltas.values());
        return deltaEvent;
    }

    private String getPartition() {
        return "DELTA";
    }

    private EventHeader getHeader() {
        ObjectDelta first = objectDeltas.values().iterator().next();
        EventHeader header = new EventHeader();
        header.setId(this.transId);
        header.setTimestamp(this.getTimeStamp(first.getTimestamp()));
        header.setSourceName(this.sourceName);
        header.setDomain(this.getDomain());
        header.setEventType(this.getEventType());
        header.setVersion(this.schemaVersion);
        header.setAction(first.getAction().toString());
        header.setEntityType(this.getEntityType(first));
        header.setEntityLink(first.getUri());
        header.setEntityUuid(this.getUUID(first));
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
     *
     * @param timestamp milliseconds since epoc
     * @return long timestamp in format YYYYMMdd-HH:mm:ss:SSS
     */
    private String getTimeStamp(long timestamp) {
        // SimpleDateFormat is not thread safe new instance needed
        DateFormat df = new SimpleDateFormat("yyyyMMdd-HH:mm:ss:SSS");
        return df.format(new Date(timestamp));
    }
}
