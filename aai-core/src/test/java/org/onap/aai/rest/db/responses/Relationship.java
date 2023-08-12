package org.onap.aai.rest.db.responses;

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
