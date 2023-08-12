package org.onap.aai.rest.db.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RelationshipData {
    @JsonProperty("relationship-key")
    private String relationshipKey;

    @JsonProperty("relationship-value")
    private String relationshipValue;

    public String getRelationshipKey() {
        return relationshipKey;
    }

    public void setRelationshipKey(String relationshipKey) {
        this.relationshipKey = relationshipKey;
    }

    public String getRelationshipValue() {
        return relationshipValue;
    }

    public void setRelationshipValue(String relationshipValue) {
        this.relationshipValue = relationshipValue;
    }
}
