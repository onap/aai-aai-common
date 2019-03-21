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

package org.onap.aai.domain.responseMessage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"aaiResponseMessageCode", "aaiResponseMessageResourceType",
        "aaiResponseMessageDescription", "aaiResponseMessageData",})
@XmlRootElement(name = "aai-response-message", namespace = "http://org.onap.aai.inventory")
public class AAIResponseMessage {

    @XmlElement(name = "aai-response-message-code", required = true)
    protected String aaiResponseMessageCode;
    @XmlElement(name = "aai-response-message-resource-type")
    protected String aaiResponseMessageResourceType;
    @XmlElement(name = "aai-response-message-description")
    protected String aaiResponseMessageDescription;
    @XmlElement(name = "aai-response-message-data")
    protected AAIResponseMessageData aaiResponseMessageData;

    /**
     * Gets the aai response message code.
     *
     * @return the aai response message code
     */
    public String getAaiResponseMessageCode() {
        return aaiResponseMessageCode;
    }

    /**
     * Sets the aai response message code.
     *
     * @param aaiResponseMessageCode the new aai response message code
     */
    public void setAaiResponseMessageCode(String aaiResponseMessageCode) {
        this.aaiResponseMessageCode = aaiResponseMessageCode;
    }

    /**
     * Gets the aai response message resource type.
     *
     * @return the aai response message resource type
     */
    public String getAaiResponseMessageResourceType() {
        return aaiResponseMessageResourceType;
    }

    /**
     * Sets the aai response message resource type.
     *
     * @param aaiResponseMessageResourceType the new aai response message resource type
     */
    public void setAaiResponseMessageResourceType(String aaiResponseMessageResourceType) {
        this.aaiResponseMessageResourceType = aaiResponseMessageResourceType;
    }

    /**
     * Gets the aai response message description.
     *
     * @return the aai response message description
     */
    public String getAaiResponseMessageDescription() {
        return aaiResponseMessageDescription;
    }

    /**
     * Sets the aai response message description.
     *
     * @param aaiResponseMessageDescription the new aai response message description
     */
    public void setAaiResponseMessageDescription(String aaiResponseMessageDescription) {
        this.aaiResponseMessageDescription = aaiResponseMessageDescription;
    }

    /**
     * Gets the aai response message data.
     *
     * @return the aai response message data
     */
    public AAIResponseMessageData getAaiResponseMessageData() {
        if (aaiResponseMessageData == null) {
            aaiResponseMessageData = new AAIResponseMessageData();
        }
        return aaiResponseMessageData;
    }

    /**
     * Sets the AAI response message data.
     *
     * @param aaiResponseMessageData the new AAI response message data
     */
    public void setAAIResponseMessageData(AAIResponseMessageData aaiResponseMessageData) {
        this.aaiResponseMessageData = aaiResponseMessageData;
    }
}
