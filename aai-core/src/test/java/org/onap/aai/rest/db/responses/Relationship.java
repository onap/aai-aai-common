package org.onap.aai.rest.db.responses;

/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2023 Deutsche Telekom. All rights reserved.
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

import com.fasterxml.jackson.annotation.JsonProperty;

public class Relationship {
    @JsonProperty("related-to")
    private String relatedTo;

    @JsonProperty("relationship-label")
    private String relationshipLabel;

    @JsonProperty("related-link")
    private String relatedLink;

    @JsonProperty("relationship-data")
    private RelationshipData[] relationshipData;

    public String getRelatedTo() {
        return relatedTo;
    }

    public void setRelatedTo(String relatedTo) {
        this.relatedTo = relatedTo;
    }

    public String getRelationshipLabel() {
        return relationshipLabel;
    }

    public void setRelationshipLabel(String relationshipLabel) {
        this.relationshipLabel = relationshipLabel;
    }

    public String getRelatedLink() {
        return relatedLink;
    }

    public void setRelatedLink(String relatedLink) {
        this.relatedLink = relatedLink;
    }

    public RelationshipData[] getRelationshipData() {
        return relationshipData;
    }

    public void setRelationshipData(RelationshipData[] relationshipData) {
        this.relationshipData = relationshipData;
    }
}
