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

package org.onap.aai.domain.notificationEvent;

import javax.xml.bind.annotation.*;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"cambriaPartition", "eventHeader", "aaiEventPayload"})
@XmlRootElement(name = "NotificationEvent")
public class NotificationEvent {

    @XmlElement(name = "cambria.partition")
    protected String cambriaPartition;
    @XmlElement(name = "event-header")
    protected EventHeader eventHeader;

    @XmlElement(name = "aaiEventPayload")
    protected AaiEventPayload aaiEventPayload;

    @Data
    @AllArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType
    public static class AaiEventPayload {
        @XmlAnyElement(lax = true)
        protected Object entity;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"id", "timestamp", "sourceName", "domain", "sequenceNumber", "severity", "eventType",
                    "version", "action", "entityType", "topEntityType", "entityLink", "status"})
    public static class EventHeader {

        @XmlElement(required = true)
        protected String id;
        @XmlElement(required = true)
        protected String timestamp;
        @XmlElement(name = "source-name", required = true)
        protected String sourceName;
        @XmlElement(required = true)
        protected String domain;
        @XmlElement(name = "sequence-number", required = true)
        protected String sequenceNumber;
        @XmlElement(required = true)
        protected String severity;
        @XmlElement(name = "event-type", required = true)
        protected String eventType;
        @XmlElement(required = true)
        protected String version;
        @XmlElement(required = true)
        protected String action;
        @XmlElement(name = "entity-type", required = true)
        protected String entityType;
        @XmlElement(name = "top-entity-type", required = true)
        protected String topEntityType;
        @XmlElement(name = "entity-link", required = true)
        protected String entityLink;
        @XmlElement(required = true)
        protected String status;
    }

}
