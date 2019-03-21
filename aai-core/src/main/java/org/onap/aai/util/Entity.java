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

package org.onap.aai.util;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({"equipment-role", "action", "key-value-list", "self-link"})
public class Entity {

    @JsonProperty("equipment-role")
    private String equipmentRole;
    @JsonProperty("action")
    private String action;
    @JsonProperty("key-value-list")
    private List<KeyValueList> keyValueList = new ArrayList<KeyValueList>();
    @JsonProperty("self-link")
    private String selfLink;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *         The equipmentRole
     */
    @JsonProperty("equipment-role")
    public String getEquipmentRole() {
        return equipmentRole;
    }

    /**
     * 
     * @param equipmentRole
     *        The equipment-role
     */
    @JsonProperty("equipment-role")
    public void setEquipmentRole(String equipmentRole) {
        this.equipmentRole = equipmentRole;
    }

    public Entity withEquipmentRole(String equipmentRole) {
        this.equipmentRole = equipmentRole;
        return this;
    }

    /**
     * 
     * @return
     *         The action
     */
    @JsonProperty("action")
    public String getAction() {
        return action;
    }

    /**
     * 
     * @param action
     *        The action
     */
    @JsonProperty("action")
    public void setAction(String action) {
        this.action = action;
    }

    public Entity withAction(String action) {
        this.action = action;
        return this;
    }

    /**
     * 
     * @return
     *         The keyValueList
     */
    @JsonProperty("key-value-list")
    public List<KeyValueList> getKeyValueList() {
        return keyValueList;
    }

    /**
     * 
     * @param keyValueList
     *        The key-value-list
     */
    @JsonProperty("key-value-list")
    public void setKeyValueList(List<KeyValueList> keyValueList) {
        this.keyValueList = keyValueList;
    }

    public Entity withKeyValueList(List<KeyValueList> keyValueList) {
        this.keyValueList = keyValueList;
        return this;
    }

    /**
     * 
     * @return
     *         The selfLink
     */
    @JsonProperty("self-link")
    public String getSelfLink() {
        return selfLink;
    }

    /**
     * 
     * @param selfLink
     *        The self-link
     */
    @JsonProperty("self-link")
    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    public Entity withSelfLink(String selfLink) {
        this.selfLink = selfLink;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Entity withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(equipmentRole).append(action).append(keyValueList)
            .append(selfLink).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Entity) == false) {
            return false;
        }
        Entity rhs = ((Entity) other);
        return new EqualsBuilder().append(equipmentRole, rhs.equipmentRole)
            .append(action, rhs.action).append(keyValueList, rhs.keyValueList)
            .append(selfLink, rhs.selfLink).append(additionalProperties, rhs.additionalProperties)
            .isEquals();
    }

}
