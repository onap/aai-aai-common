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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Data
public class ObjectDelta {

    private String uri;
    private DeltaAction action;
    @JsonProperty("source-of-truth")
    private String sourceOfTruth;
    private long timestamp;
    @JsonProperty("property-deltas")
    private Map<String, PropertyDelta> propertyDeltas = new HashMap<>();
    @JsonProperty("relationship-deltas")
    private List<RelationshipDelta> relationshipDeltas = new ArrayList<>();

    public ObjectDelta(String uri, DeltaAction action, String sourceOfTruth, long timestamp) {
        this.uri = uri;
        this.action = action;
        this.sourceOfTruth = sourceOfTruth;
        this.timestamp = timestamp;
    }

    public void addPropertyDelta(String prop, PropertyDelta propertyDelta) {
        propertyDeltas.put(prop, propertyDelta);
    }

    public void addRelationshipDelta(RelationshipDelta relationshipDelta) {
        relationshipDeltas.add(relationshipDelta);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("uri", uri).append("action", action)
                .append("sourceOfTruth", sourceOfTruth).append("timestamp", timestamp)
                .append("propertyDeltas", propertyDeltas).append("relationshipDeltas", relationshipDeltas).toString();
    }
}
