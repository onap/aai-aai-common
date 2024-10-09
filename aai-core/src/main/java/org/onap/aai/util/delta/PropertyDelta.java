/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.aai.util.delta;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Data
public class PropertyDelta {

    protected DeltaAction action;
    protected Object value;
    @JsonProperty("old-value")
    private Object oldValue;

    public PropertyDelta(DeltaAction action, Object value) {
        this.action = action;
        this.value = value;
    }

    public PropertyDelta(DeltaAction action, Object value, Object oldValue) {
        this(action, value);
        this.oldValue = oldValue;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("action", action).append("value", value).append("oldValue", oldValue)
                .toString();
    }
}
