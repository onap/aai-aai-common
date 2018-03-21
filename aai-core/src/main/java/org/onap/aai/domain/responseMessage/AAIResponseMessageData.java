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


//
//This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
//See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
//Any modifications to this file will be lost upon recompilation of the source schema. 
//Generated on: 2015.09.11 at 11:53:27 AM EDT 
//

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"aaiResponseMessageDatum",
		"any"
})
@XmlRootElement(name = "aai-response-message-data", namespace = "http://org.onap.aai.inventory")
public class AAIResponseMessageData {

	@XmlElement(name = "aai-response-message-datum")
	protected List<AAIResponseMessageDatum> aaiResponseMessageDatum;
	@XmlAnyElement(lax = true)
	protected List<Object> any;

	/**
	 * Gets the AAI response message datum.
	 *
	 * @return the AAI response message datum
	 */
	public List<AAIResponseMessageDatum> getAAIResponseMessageDatum() {
		if (aaiResponseMessageDatum == null) {
			aaiResponseMessageDatum = new ArrayList<AAIResponseMessageDatum>();
		}
		return this.aaiResponseMessageDatum;
	}

	/**
	 * Gets the any.
	 *
	 * @return the any
	 */
	public List<Object> getAny() {
		if (any == null) {
			any = new ArrayList<Object>();
		}
		return this.any;
	}

}
