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

package org.onap.aai.util.delta;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.Map;

public class RelationshipDelta {

    @SerializedName("action")
    private DeltaAction action;

    @SerializedName("in-v-uuid")
    private String inVUuid;

    @SerializedName("out-v-uuid")
    private String outVUuid;

    @SerializedName("in-v-uri")
    private String inVUri;

    @SerializedName("out-v-uri")
    private String outVUri;

    @SerializedName("label")
    private String label;

    @SerializedName("props")
    private Map<String, Object> props = new HashMap<>();

    public RelationshipDelta(DeltaAction action, String inVUUID, String outVUUID, String inVUri, String outVUri, String label) {
        this.action = action;
        this.inVUuid = inVUUID;
        this.outVUuid = outVUUID;
        this.inVUri = inVUri;
        this.outVUri = outVUri;
        this.label = label;
    }

    public DeltaAction getAction() {
        return action;
    }

    public void setAction(DeltaAction action) {
        this.action = action;
    }

    public String getInVUuid() {
        return inVUuid;
    }

    public void setInVUuid(String inVUuid) {
        this.inVUuid = inVUuid;
    }

    public String getOutVUuid() {
        return outVUuid;
    }

    public void setOutVUuid(String outVUuid) {
        this.outVUuid = outVUuid;
    }

    public String getInVUri() {
        return inVUri;
    }

    public void setInVUri(String inVUri) {
        this.inVUri = inVUri;
    }

    public String getOutVUri() {
        return outVUri;
    }

    public void setOutVUri(String outVUri) {
        this.outVUri = outVUri;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Map<String, Object> getProps() {
        return props;
    }

    public void setProps(Map<String, Object> props) {
        this.props = props;
    }

    public void addProp(String key, String value) {
        this.props.put(key, value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("action", action)
            .append("inVUuid", inVUuid)
            .append("outVUuid", outVUuid)
            .append("inVUri", inVUri)
            .append("outVUri", outVUri)
            .append("label", label)
            .append("props", props)
            .toString();
    }
}
