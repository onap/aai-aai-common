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

package org.onap.aai.domain.responseMessage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"aaiResponseMessageDatumKey", "aaiResponseMessageDatumValue",

})

@XmlRootElement(name = "aai-response-message-datum", namespace = "http://org.onap.aai.inventory")
public class AAIResponseMessageDatum {

    @XmlElement(name = "aai-response-message-datum-key", required = true)
    protected String aaiResponseMessageDatumKey;
    @XmlElement(name = "aai-response-message-datum-value", required = true)
    protected String aaiResponseMessageDatumValue;

    /**
     * Gets the aai response message datum key.
     *
     * @return the aai response message datum key
     */
    public String getAaiResponseMessageDatumKey() {
        return aaiResponseMessageDatumKey;
    }

    /**
     * Sets the aai response message datum key.
     *
     * @param aaiResponseMessageDatumKey the new aai response message datum key
     */
    public void setAaiResponseMessageDatumKey(String aaiResponseMessageDatumKey) {
        this.aaiResponseMessageDatumKey = aaiResponseMessageDatumKey;
    }

    /**
     * Gets the aai response message datum value.
     *
     * @return the aai response message datum value
     */
    public String getAaiResponseMessageDatumValue() {
        return aaiResponseMessageDatumValue;
    }

    /**
     * Sets the aai response message datum value.
     *
     * @param aaiResponseMessageDatumValue the new aai response message datum value
     */
    public void setAaiResponseMessageDatumValue(String aaiResponseMessageDatumValue) {
        this.aaiResponseMessageDatumValue = aaiResponseMessageDatumValue;
    }

}
