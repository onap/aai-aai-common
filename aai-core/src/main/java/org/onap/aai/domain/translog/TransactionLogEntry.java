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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.domain.translog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"transactionLogEntryId", "status", "rqstDate", "respDate", "sourceId",
        "resourceId", "resourceType", "rqstBuf", "respBuf", "notificationPayload", "notificationId",
        "notificationStatus", "notificationTopic", "notificationEntityLink", "notificationAction"})
@XmlRootElement(name = "transaction-log-entry", namespace = "http://org.onap.aai.inventory")
public class TransactionLogEntry {

    @XmlElement(name = "transaction-log-entry-id", required = true)
    protected String transactionLogEntryId;
    @XmlElement(name = "status")
    protected String status;
    @XmlElement(name = "rqst-date")
    protected String rqstDate;
    @XmlElement(name = "resp-date")
    protected String respDate;
    @XmlElement(name = "source-id")
    protected String sourceId;
    @XmlElement(name = "resource-id")
    protected String resourceId;
    @XmlElement(name = "resource-type")
    protected String resourceType;
    @XmlElement(name = "rqst-buf")
    protected String rqstBuf;
    @XmlElement(name = "resp-buf")
    protected String respBuf;
    @XmlElement(name = "notification-payload")
    protected String notificationPayload;
    @XmlElement(name = "notification-id")
    protected String notificationId;
    @XmlElement(name = "notification-status")
    protected String notificationStatus;
    @XmlElement(name = "notification-topic")
    private String notificationTopic;
    @XmlElement(name = "notification-entity-link")
    private String notificationEntityLink;
    @XmlElement(name = "notification-action")
    private String notificationAction;

    /**
     * Gets the value of the transcationLogEntryId property.
     * 
     * @return
     *         possible object is
     *         {@link String }
     * 
     */
    public String getTransactionLogEntryId() {
        return transactionLogEntryId;
    }

    /**
     * Sets the value of the transactionLogEntryId property.
     * 
     * @param value
     *        allowed object is
     *        {@link String }
     * 
     */
    public void setTransactionLogEntryId(String value) {
        this.transactionLogEntryId = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *         possible object is
     *         {@link String }
     * 
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *        allowed object is
     *        {@link String }
     * 
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the rqstDate property.
     * 
     * @return
     *         possible object is
     *         {@link String }
     * 
     */

    public String getRqstDate() {
        return rqstDate;
    }

    /**
     * Sets the value of the rqstDate property.
     * 
     * @param value
     *        allowed object is
     *        {@link String }
     * 
     */
    public void setRqstDate(String value) {
        this.rqstDate = value;
    }

    /**
     * Gets the value of the respDate property.
     * 
     * @return
     *         possible object is
     *         {@link String }
     * 
     */

    public String getRespDate() {
        return respDate;
    }

    /**
     * Sets the value of the respDate property.
     * 
     * @param value
     *        allowed object is
     *        {@link String }
     * 
     */
    public void setRespDate(String value) {
        this.respDate = value;
    }

    /**
     * Gets the value of the sourceId property.
     * 
     * @return
     *         possible object is
     *         {@link String }
     * 
     */
    public String getSourceId() {
        return sourceId;
    }

    /**
     * Sets the value of the sourceId property.
     * 
     * @param value
     *        allowed object is
     *        {@link String }
     * 
     */
    public void setSourceId(String value) {
        this.sourceId = value;
    }

    /**
     * Gets the value of the resourceId property.
     * 
     * @return
     *         possible object is
     *         {@link String }
     * 
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * Sets the value of the resourceId property.
     * 
     * @param value
     *        allowed object is
     *        {@link String }
     * 
     */
    public void setResourceId(String value) {
        this.resourceId = value;
    }

    /**
     * Gets the value of the resourceType property.
     * 
     * @return
     *         possible object is
     *         {@link String }
     * 
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * Sets the value of the resourceType property.
     * 
     * @param value
     *        allowed object is
     *        {@link String }
     * 
     */
    public void setResourceType(String value) {
        this.resourceType = value;
    }

    /**
     * Gets the value of the rqstBuf property.
     * 
     * @return
     *         possible object is
     *         {@link String }
     * 
     */
    public String getRqstBuf() {
        return rqstBuf;
    }

    /**
     * Sets the value of the rqstBuf property.
     * 
     * @param value
     *        allowed object is
     *        {@link String }
     * 
     */
    @XmlCDATA
    public void setRqstBuf(String value) {
        this.rqstBuf = value;
    }

    /**
     * Gets the value of the respBuf property.
     * 
     * @return
     *         possible object is
     *         {@link String }
     * 
     */
    public String getrespBuf() {
        return respBuf;
    }

    /**
     * Sets the value of the respBuf property.
     * 
     * @param value
     *        allowed object is
     *        {@link String }
     * 
     */
    @XmlCDATA
    public void setrespBuf(String value) {
        this.respBuf = value;
    }

    /**
     * Gets the value of the notificationPayload property.
     * 
     * @return
     *         possible object is
     *         {@link String }
     * 
     */
    public String getNotificationPayload() {
        return notificationPayload;
    }

    /**
     * Sets the value of the notificationPayload property.
     * 
     * @param value
     *        allowed object is
     *        {@link String }
     * 
     */
    @XmlCDATA
    public void setNotificationPayload(String value) {
        this.notificationPayload = value;
    }

    /**
     * Gets the value of the notificationId property.
     * 
     * @return
     *         possible object is
     *         {@link String }
     * 
     */
    public String getNotificationId() {
        return notificationId;
    }

    /**
     * Sets the value of the notificationId property.
     * 
     * @param value
     *        allowed object is
     *        {@link String }
     * 
     */
    public void setNotificationId(String value) {
        this.notificationId = value;
    }

    /**
     * Gets the value of the notificationId property.
     * 
     * @return
     *         possible object is
     *         {@link String }
     * 
     */
    public String getNotificationStatus() {
        return notificationStatus;
    }

    /**
     * Sets the value of the notificationId property.
     * 
     * @param value
     *        allowed object is
     *        {@link String }
     * 
     */
    public void setNotificationStatus(String value) {
        this.notificationStatus = value;
    }

    /**
     * Gets the value of the notificationTopic property.
     * 
     * @return
     *         possible object is
     *         {@link String }
     * 
     */
    public String getNotificationTopic() {
        return notificationTopic;
    }

    /**
     * Sets the value of the notificationTopic property.
     *
     * @param topic the new notification topic
     */
    public void setNotificationTopic(String topic) {
        this.notificationTopic = topic;
    }

    /**
     * Gets the value of the notificationEntityLink property.
     * 
     * @return
     *         possible object is
     *         {@link String }
     * 
     */
    public String getNotificationEntityLink() {
        return notificationEntityLink;
    }

    /**
     * Sets the value of the notificationEntityLink property.
     *
     * @param entityLink the new notification entity link
     */
    public void setNotificationEntityLink(String entityLink) {
        this.notificationEntityLink = entityLink;
    }

    /**
     * Sets the value of the notificationAction property.
     *
     * @return the notification action
     */
    public String getNotificationAction() {
        return notificationAction;
    }

    /**
     * Sets the value of the notificationAction property.
     *
     * @param action the new notification action
     */
    public void setNotificationAction(String action) {
        this.notificationAction = action;
    }

}
