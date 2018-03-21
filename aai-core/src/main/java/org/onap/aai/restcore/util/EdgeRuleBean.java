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
package org.onap.aai.restcore.util;

public class EdgeRuleBean {
	private String from;
	private String to;
	private String label;
	private String direction;
	private String multiplicity;
	private String lineage;
	private String preventDelete;
	private String deleteOtherV;
	private String svcInfra;
	private String defaultVal;
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getMultiplicity() {
		return multiplicity;
	}
	public void setMultiplicity(String multiplicity) {
		this.multiplicity = multiplicity;
	}
	public String getDeleteOtherV() {
		return deleteOtherV;
	}
	public void setDeleteOtherV(String deleteOtherV) {
		this.deleteOtherV = deleteOtherV;
	}
	public String getPreventDelete() {
		return preventDelete;
	}
	public void setPreventDelete(String preventDelete) {
		this.preventDelete = preventDelete;
	}
	public String getSvcInfra() {
		return svcInfra;
	}
	public void setSvcInfra(String svcInfra) {
		this.svcInfra = svcInfra;
	}
	public String getLineage() {
		return lineage;
	}
	public void setLineage(String lineage) {
		this.lineage = lineage;
	}
	public String getDefault() {
		return defaultVal;
	}
	public void setDefault(String defaultVal) {
		this.defaultVal = defaultVal;
	}
}
