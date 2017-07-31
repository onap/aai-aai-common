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

package org.openecomp.aai.dbmodel;

public class EdgeRuleBean {
	private String from;
	private String to;
	private String label;
	private String direction;
	private String multiplicity;
	private String isParent;
	private String usesResource;
	private String hasDelTarget;
	private String svcInfra;
	
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
	public String getIsParent() {
		return isParent;
	}
	public void setParent(String isParent) {
		this.isParent = isParent;
	}
	public String getUsesResource() {
		return usesResource;
	}
	public void setUsesResource(String usesResource) {
		this.usesResource = usesResource;
	}
	public String getHasDelTarget() {
		return hasDelTarget;
	}
	public void setHasDelTarget(String hasDelTarget) {
		this.hasDelTarget = hasDelTarget;
	}
	public String getSvcInfra() {
		return svcInfra;
	}
	public void setSvcInfra(String svcInfra) {
		this.svcInfra = svcInfra;
	}
}
