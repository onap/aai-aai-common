package org.onap.aai.rest.db.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RelationshipWrapper {
    @JsonProperty("relationship")
    private Relationship[] relationships;

    public Relationship[] getRelationships() {
        return relationships;
    }

    public void setRelationships(Relationship[] relationships) {
        this.relationships = relationships;
    }
}
