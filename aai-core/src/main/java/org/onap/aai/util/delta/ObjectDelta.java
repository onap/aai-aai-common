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

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectDelta {

    @SerializedName("uri")
    private String uri;

    @SerializedName("action")
    private DeltaAction action;

    @SerializedName("source-of-truth")
    private String sourceOfTruth;

    @SerializedName("timestamp")
    private long timestamp;

    @SerializedName("property-deltas")
    private Map<String, PropertyDelta> propertyDeltas = new HashMap<>();

    @SerializedName("relationship-deltas")
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


    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public DeltaAction getAction() {
        return action;
    }

    public void setAction(DeltaAction action) {
        this.action = action;
    }

    public String getSourceOfTruth() {
        return sourceOfTruth;
    }

    public void setSourceOfTruth(String sourceOfTruth) {
        this.sourceOfTruth = sourceOfTruth;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setPropertyDeltas(Map<String, PropertyDelta> propertyDeltas) {
        this.propertyDeltas = propertyDeltas;
    }

    public void setRelationshipDeltas(List<RelationshipDelta> relationshipDeltas) {
        this.relationshipDeltas = relationshipDeltas;
    }

    public Map<String, PropertyDelta> getPropertyDeltas() {
        return propertyDeltas;
    }

    public List<RelationshipDelta> getRelationshipDeltas() {
        return relationshipDeltas;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("uri", uri)
            .append("action", action)
            .append("sourceOfTruth", sourceOfTruth)
            .append("timestamp", timestamp)
            .append("propertyDeltas", propertyDeltas)
            .append("relationshipDeltas", relationshipDeltas)
            .toString();
    }
}
