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

package org.onap.aai.util;

import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({"key", "value"})
public class KeyValueList {

    @JsonProperty("key")
    private String key;
    @JsonProperty("value")
    private String value;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     *         The key
     */
    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    /**
     *
     * @param key
     *        The key
     */
    @JsonProperty("key")
    public void setKey(String key) {
        this.key = key;
    }

    public KeyValueList withKey(String key) {
        this.key = key;
        return this;
    }

    /**
     *
     * @return
     *         The value
     */
    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    /**
     *
     * @param value
     *        The value
     */
    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }

    public KeyValueList withValue(String value) {
        this.value = value;
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

    public KeyValueList withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(key).append(value).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof KeyValueList) == false) {
            return false;
        }
        KeyValueList rhs = ((KeyValueList) other);
        return new EqualsBuilder().append(key, rhs.key).append(value, rhs.value)
                .append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
