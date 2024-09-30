/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright © 2024 Deutsche Telekom AG.
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

package org.onap.aai.domain.notificationEvent;

import java.util.Collections;
import java.util.Map;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"cambriaPartition", "eventHeader", "entity"})
@XmlRootElement(name = "NotificationEvent")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent {

    @JsonProperty("cambria.partition")
    @XmlElement(name = "cambria.partition")
    String cambriaPartition;
    @JsonProperty("event-header")
    @XmlElement(name = "event-header")
    EventHeader eventHeader;

    @JsonIgnore
    @XmlAnyElement(lax = true)
    Object entity;

    @JsonAnyGetter
    public Map<String, Object> any() {
        return Collections.singletonMap(entity.getClass().getSimpleName(), entity);
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"id", "timestamp", "sourceName", "domain", "sequenceNumber", "severity", "eventType",
                    "version", "action", "entityType", "topEntityType", "entityLink", "status"})
    @Data
    public static class EventHeader {

        @XmlElement(required = true)
        String id;
        @XmlElement(required = true)
        String timestamp;
        @JsonProperty("source-name")
        @XmlElement(name = "source-name", required = true)
        String sourceName;
        @XmlElement(required = true)
        String domain;
        @JsonProperty("sequence-number")
        @XmlElement(name = "sequence-number", required = true)
        String sequenceNumber;
        @XmlElement(required = true)
        String severity;
        @JsonProperty("event-type")
        @XmlElement(name = "event-type", required = true)
        String eventType;
        @XmlElement(required = true)
        String version;
        @XmlElement(required = true)
        String action;
        @JsonProperty("entity-type")
        @XmlElement(name = "entity-type", required = true)
        String entityType;
        @JsonProperty("top-entity-type")
        @XmlElement(name = "top-entity-type", required = true)
        String topEntityType;
        @JsonProperty("entity-link")
        @XmlElement(name = "entity-link", required = true)
        String entityLink;
        @XmlElement(required = true)
        String status;
    }

}
