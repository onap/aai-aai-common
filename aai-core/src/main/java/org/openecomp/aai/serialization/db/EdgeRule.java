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

package org.openecomp.aai.serialization.db;

import java.util.HashMap;
import java.util.Map;

import org.apache.tinkerpop.gremlin.structure.Direction;

public class EdgeRule {

	private String label = "";
	private MultiplicityRule multiplicityRule = null;
	private Direction direction = null;
	private Map<String, String> edgeProperties = null;
	private final String IS_PARENT = "isParent";
	private final String USES_RESOURCE  = "usesResource";
	private final String HAS_DEL_TARGET = "hasDelTarget";
	private final String SVC_INFRA = "SVC-INFRA";
	
	/**
	 * Instantiates a new edge rule.
	 */
	public EdgeRule() {
		edgeProperties = new HashMap<>();
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
	public String getIsParent() {
		return this.getProp(this.IS_PARENT);
	}
	
	/**
	 * Sets the checks if is parent.
	 *
	 * @param isParent the new checks if is parent
	 */
	public void setIsParent(String isParent) {
		this.setProp(this.IS_PARENT, isParent);
	}
	
	/**
	 * Gets the uses resource.
	 *
	 * @return the uses resource
	 */
	public String getUsesResource() {
		return this.getProp(this.USES_RESOURCE);
	}
	
	/**
	 * Sets the uses resource.
	 *
	 * @param usesResource the new uses resource
	 */
	public void setUsesResource(String usesResource) {
		this.setProp(this.USES_RESOURCE, usesResource);
	}
	
	/**
	 * Gets the checks for del target.
	 *
	 * @return the checks for del target
	 */
	public String getHasDelTarget() {
		return this.getProp(this.HAS_DEL_TARGET);
	}
	
	/**
	 * Sets the checks for del target.
	 *
	 * @param hasDelTarget the new checks for del target
	 */
	public void setHasDelTarget(String hasDelTarget) {
		this.setProp(this.HAS_DEL_TARGET, hasDelTarget);
	}
	
	/**
	 * Gets the service infrastructure.
	 *
	 * @return the service infrastructure
	 */
	public String getServiceInfrastructure() {
		return this.getProp(this.SVC_INFRA);
	}
	
	/**
	 * Sets the service infrastructure.
	 *
	 * @param serviceInfrastructure the new service infrastructure
	 */
	public void setServiceInfrastructure(String serviceInfrastructure) {
		this.setProp(this.SVC_INFRA, serviceInfrastructure);
	}
	
	/**
	 * Gets the edge properties.
	 *
	 * @return the edge properties
	 */
	public Map<String, String> getEdgeProperties() {
		return this.edgeProperties;
	}
	
	/**
	 * Sets the prop.
	 *
	 * @param key the key
	 * @param value the value
	 */
	private void setProp(String key, String value) {
		this.edgeProperties.put(key, value);
	}
	
	/**
	 * Gets the prop.
	 *
	 * @param key the key
	 * @return the prop
	 */
	private String getProp(String key) {
		return this.edgeProperties.get(key);
	}
	
	
}
