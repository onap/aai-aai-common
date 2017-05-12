/*-
 * ============LICENSE_START=======================================================
 * org.openecomp.aai
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.01.06 at 05:38:00 PM EST 
//


package org.openecomp.aai.domain.notificationEvent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.w3c.dom.Element;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cambria.partition" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="event-header" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="source-name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="domain" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="sequence-number" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="severity" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="event-type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="action" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="entity-type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="top-entity-type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="entity-link" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;any processContents='lax' namespace='##other' minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
	"cambriaPartition",
    "eventHeader",
    "entity"
})
@XmlRootElement(name = "NotificationEvent")
public class NotificationEvent {

	@XmlElement(name = "cambria.partition")
	protected String cambriaPartition;
    @XmlElement(name = "event-header")
    protected NotificationEvent.EventHeader eventHeader;
    @XmlAnyElement(lax = true)
    protected Object entity;

    /**
     * Gets the value of the eventHeader property.
     * 
     * @return
     *     possible object is
     *     {@link NotificationEvent.EventHeader }
     *     
     */
    public NotificationEvent.EventHeader getEventHeader() {
        return eventHeader;
    }

    /**
     * Sets the value of the eventHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link NotificationEvent.EventHeader }
     *     
     */
    public void setEventHeader(NotificationEvent.EventHeader value) {
        this.eventHeader = value;
    }

    /**
     * Gets the value of the any property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     {@link Element }
     *     
     */
    public Object getEntity() {
        return entity;
    }

    /**
     * Sets the value of the any property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     {@link Element }
     *     
     */
    public void setEntity(Object value) {
        this.entity = value;
    }

    /**
     * Gets the value of the cambriaPartition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCambriaPartition() {
        return cambriaPartition;
    }

    /**
     * Sets the value of the cambriaPartition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCambriaPartition(String value) {
        this.cambriaPartition = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="source-name" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="domain" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="sequence-number" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="severity" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="event-type" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="action" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="entity-type" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="top-entity-type" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="entity-link" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "id",
        "timestamp",
        "sourceName",
        "domain",
        "sequenceNumber",
        "severity",
        "eventType",
        "version",
        "action",
        "entityType",
        "topEntityType",
        "entityLink",
        "status"
    })
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

        /**
         * Gets the value of the id property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the value of the id property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setId(String value) {
            this.id = value;
        }

        /**
         * Gets the value of the timestamp property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTimestamp() {
            return timestamp;
        }

        /**
         * Sets the value of the timestamp property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTimestamp(String value) {
            this.timestamp = value;
        }

        /**
         * Gets the value of the sourceName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSourceName() {
            return sourceName;
        }

        /**
         * Sets the value of the sourceName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSourceName(String value) {
            this.sourceName = value;
        }

        /**
         * Gets the value of the domain property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDomain() {
            return domain;
        }

        /**
         * Sets the value of the domain property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDomain(String value) {
            this.domain = value;
        }

        /**
         * Gets the value of the sequenceNumber property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSequenceNumber() {
            return sequenceNumber;
        }

        /**
         * Sets the value of the sequenceNumber property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSequenceNumber(String value) {
            this.sequenceNumber = value;
        }

        /**
         * Gets the value of the severity property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSeverity() {
            return severity;
        }

        /**
         * Sets the value of the severity property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSeverity(String value) {
            this.severity = value;
        }

        /**
         * Gets the value of the eventType property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getEventType() {
            return eventType;
        }

        /**
         * Sets the value of the eventType property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setEventType(String value) {
            this.eventType = value;
        }

        /**
         * Gets the value of the version property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getVersion() {
            return version;
        }

        /**
         * Sets the value of the version property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setVersion(String value) {
            this.version = value;
        }

        /**
         * Gets the value of the action property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAction() {
            return action;
        }

        /**
         * Sets the value of the action property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAction(String value) {
            this.action = value;
        }

        /**
         * Gets the value of the entityType property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getEntityType() {
            return entityType;
        }

        /**
         * Sets the value of the entityType property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setEntityType(String value) {
            this.entityType = value;
        }

        /**
         * Gets the value of the topEntityType property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTopEntityType() {
            return topEntityType;
        }

        /**
         * Sets the value of the topEntityType property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTopEntityType(String value) {
            this.topEntityType = value;
        }

        /**
         * Gets the value of the entityLink property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getEntityLink() {
            return entityLink;
        }

        /**
         * Sets the value of the entityLink property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setEntityLink(String value) {
            this.entityLink = value;
        }

        /**
         * Gets the value of the status property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getStatus() {
            return status;
        }

        /**
         * Sets the value of the status property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setStatus(String value) {
            this.status = value;
        }

    }

}
