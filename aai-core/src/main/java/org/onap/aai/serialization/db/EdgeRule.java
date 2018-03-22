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
package org.onap.aai.serialization.db;

import org.apache.tinkerpop.gremlin.structure.Direction;

import java.util.EnumMap;
import java.util.Map;

public class EdgeRule {

	private String label = "";
	private MultiplicityRule multiplicityRule = null;
	private Direction direction = null;
	private Map<EdgeProperty, String> edgeProperties = null;
	private boolean isDefaultEdge = false;
	private String from;
	private String to;

	/**
	 * Instantiates a new edge rule.
	 */
	public EdgeRule() {
		edgeProperties = new EnumMap<>(EdgeProperty.class);
	}

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

	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Sets the label.
	 *
	 * @param label the new label
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * Gets the multiplicity rule.
	 *
	 * @return the multiplicity rule
	 */
	public MultiplicityRule getMultiplicityRule() {
		return multiplicityRule;
	}
	
	public void setMultiplicityRule(String multiplicity){
		if ("Many2Many".equalsIgnoreCase(multiplicity)) {
			this.multiplicityRule = MultiplicityRule.MANY2MANY;
		} else if ("One2Many".equalsIgnoreCase(multiplicity)) {
			this.multiplicityRule = MultiplicityRule.ONE2MANY;
		} else if ("One2One".equalsIgnoreCase(multiplicity)) {
			this.multiplicityRule = MultiplicityRule.ONE2ONE;
		} else { //should be "Many2One"
			this.multiplicityRule = MultiplicityRule.MANY2ONE;
		}
	}
	
	/**
	 * Sets the multiplicity rule.
	 *
	 * @param multiplicityRule the new multiplicity rule
	 */
	public void setMultiplicityRule(MultiplicityRule multiplicityRule) {
		this.multiplicityRule = multiplicityRule;
	}
	
	/**
	 * Gets the direction.
	 *
	 * @return the direction
	 */
	public Direction getDirection() {
		return direction;
	}
	
	public void setDirection(String direction){
		if ("OUT".equalsIgnoreCase(direction)) {
			this.direction = Direction.OUT;
		} else if ("IN".equalsIgnoreCase(direction)) {
			this.direction = Direction.IN;
		} else {
			this.direction = Direction.BOTH;
		}
	}
	
	/**
	 * Sets the direction.
	 *
	 * @param direction the new direction
	 */
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	
	/**
	 * Gets the checks if is parent.
	 *
	 * @return the checks if is parent
	 */
	public String getContains() {
		return this.getProp(EdgeProperty.CONTAINS);
	}
	
	/**
	 * Sets the checks if is parent.
	 *
	 * @param isParent the new checks if is parent
	 */
	public void setContains(String isParent) {
		this.setProp(EdgeProperty.CONTAINS, isParent);
	}
	
	/**
	 * Gets the checks for del target.
	 *
	 * @return the checks for del target
	 */
	public String getDeleteOtherV() {
		return this.getProp(EdgeProperty.DELETE_OTHER_V);
	}
	
	/**
	 * Sets the checks for del target.
	 *
	 * @param hasDelTarget the new checks for del target
	 */
	public void setDeleteOtherV(String hasDelTarget) {
		this.setProp(EdgeProperty.DELETE_OTHER_V, hasDelTarget);
	}
	
	/**
	 * Gets the service infrastructure.
	 *
	 * @return the service infrastructure
	 */
	public String getServiceInfrastructure() {
		return this.getProp(EdgeProperty.SVC_INFRA);
	}
	
	/**
	 * Sets the service infrastructure.
	 *
	 * @param serviceInfrastructure the new service infrastructure
	 */
	public void setServiceInfrastructure(String serviceInfrastructure) {
		this.setProp(EdgeProperty.SVC_INFRA, serviceInfrastructure);
	}
	
	public String getPreventDelete() {
		return this.getProp(EdgeProperty.PREVENT_DELETE);
	}
	
	public void setPreventDelete(String preventDelete) {
		this.setProp(EdgeProperty.PREVENT_DELETE, preventDelete);
	}
	
	/**
	 * Gets the edge properties.
	 *
	 * @return the edge properties
	 */
	public Map<EdgeProperty, String> getEdgeProperties() {
		return this.edgeProperties;
	}
	
	/**
	 * Sets the prop.
	 *
	 * @param key the key
	 * @param value the value
	 */
	private void setProp(EdgeProperty key, String value) {
		this.edgeProperties.put(key, value);
	}
	
	/**
	 * Gets the prop.
	 *
	 * @param key the key
	 * @return the prop
	 */
	private String getProp(EdgeProperty key) {
		return this.edgeProperties.get(key);
	}

	public boolean isDefault() {
		return isDefaultEdge;
	}

	public void setIsDefault(boolean isDefault) {
		this.isDefaultEdge = isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefaultEdge = "true".equals(isDefault);
	}


}
