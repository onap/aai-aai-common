/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-18 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 *  Modifications Copyright © 2018 IBM.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.edges;

import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.onap.aai.edges.enums.AAIDirection;
import org.onap.aai.edges.enums.EdgeField;
import org.onap.aai.edges.enums.EdgeProperty;
import org.onap.aai.edges.enums.EdgeType;
import org.onap.aai.setup.SchemaVersion;

/**
 * For querying the edge rules schema (not the database)
 *
 */
public class EdgeRuleQuery {
    private Filter filter;
    private Optional<SchemaVersion> v;
    private String nodeA;
    private String nodeB;
    private String label;
    private AAIDirection direction;
    private EdgeType type;
    private boolean isPrivate;

    private static final String TO_ONLY = "ToOnly";
    private static final String FROM_ONLY = "FromOnly";

    public static class Builder {


        // required
        private String nodeA;

        // optional - null will translate to any value of the param
        private String nodeB = null;
        private String label = null;
        private EdgeType type = null;
        private AAIDirection direction = null;
        private boolean isPrivate = false;
        private SchemaVersion version = null;

        public Builder(String nodeA) {
            this.nodeA = nodeA;
        }

        public Builder(String nodeA, String nodeB) {
            this.nodeA = nodeA;
            this.nodeB = nodeB;
        }

        private String getFirstNodeType() {
            return nodeA;
        }

        public Builder fromOnly() {
            this.nodeB = FROM_ONLY;
            return this;
        }

        private String getSecondNodeType() {
            return nodeB;
        }

        public Builder to(String nodeB) {
            this.nodeB = nodeB;
            return this;
        }

        public Builder toOnly() {
            // Allows this to be used with single parameter constructor Builder(String nodeA)
            if (StringUtils.isEmpty(this.nodeB) && StringUtils.isNotEmpty(this.nodeA)) {
                this.nodeB = this.nodeA;
            }
            this.nodeA = TO_ONLY;
            return this;
        }

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        private String getLabel() {
            return label;
        }

        public Builder edgeType(EdgeType type) {
            this.type = type;
            return this;
        }

        private EdgeType getEdgeType() {
            return type;
        }

        public Builder direction(AAIDirection direction) {
            this.direction = direction;
            return this;
        }

        private AAIDirection getDirection() {
            return direction;
        }

        public Builder version(SchemaVersion version) {
            this.version = version;
            return this;
        }

        public Builder setPrivate(boolean isPrivate) {
            this.isPrivate = isPrivate;
            return this;
        }

        public boolean isPrivate() {
            return isPrivate;
        }

        private Optional<SchemaVersion> getSchemaVersion() {
            return Optional.ofNullable(version);
        }

        public EdgeRuleQuery build() {
            return new EdgeRuleQuery(this);
        }
    }

    private EdgeRuleQuery(Builder builder) {
        this.v = builder.getSchemaVersion();
        this.nodeA = builder.getFirstNodeType();
        this.nodeB = builder.getSecondNodeType();
        this.label = builder.getLabel();
        this.type = builder.getEdgeType();
        this.direction = builder.getDirection();
        this.isPrivate = builder.isPrivate();

        // will cover from A to B case
        List<Predicate> criteriaFromTo = new ArrayList<>();
        // Special logic to allow for A to B case only
        if ((FROM_ONLY).equals(builder.getSecondNodeType())) {
            criteriaFromTo.add(buildToFromPart(builder.getFirstNodeType(), null));
        } else {
            criteriaFromTo.add(buildToFromPart(builder.getFirstNodeType(), builder.getSecondNodeType()));
        }
        // will cover from B to A case - must be separate bc jsonpath won't let me OR predicates >:C
        List<Predicate> criteriaToFrom = new ArrayList<>();
        // Special logic to allow for B to A case only
        if ((TO_ONLY).equals(builder.getFirstNodeType())) {
            criteriaToFrom.add(buildToFromPart(null, builder.getSecondNodeType()));
        } else {
            criteriaToFrom.add(buildToFromPart(builder.getSecondNodeType(), builder.getFirstNodeType()));
        }
        if (builder.getLabel() != null) {
            Predicate labelPred = addLabel(builder.getLabel());
            criteriaFromTo.add(labelPred);
            criteriaToFrom.add(labelPred);
        }

        if (builder.getEdgeType() != null && builder.getEdgeType() != EdgeType.ALL) {
            Predicate typePred = addType(builder.getEdgeType());
            criteriaFromTo.add(typePred);
            criteriaToFrom.add(typePred);
        }
        Predicate privatePredicate = Criteria.where("private").is(String.valueOf(isPrivate));

        if (isPrivate) {
            criteriaFromTo.add(privatePredicate);
            criteriaToFrom.add(privatePredicate);
        }

        if (builder.getDirection() != null) {
            Predicate directionPred = addDirection(builder.getDirection());
            criteriaFromTo.add(directionPred);
            criteriaToFrom.add(directionPred);
        }
        if ((TO_ONLY).equals(builder.getFirstNodeType())) {
            this.filter = Filter.filter(criteriaToFrom);
        } else if ((FROM_ONLY).equals(builder.getSecondNodeType())) {
            this.filter = Filter.filter(criteriaFromTo);
        } else {
            this.filter = Filter.filter(criteriaFromTo).or(Filter.filter(criteriaToFrom));
        }
    }

    private Predicate buildToFromPart(String from, String to) {
        if (from == null && to == null) { // shouldn't ever happen though
            throw new IllegalStateException("must have at least one node defined");
        }

        Predicate p;

        if (to == null) {
            p = Criteria.where(EdgeField.FROM.toString()).is(from);
        } else if (from == null) {
            p = Criteria.where(EdgeField.TO.toString()).is(to);
        } else {
            p = Criteria.where(EdgeField.FROM.toString()).is(from).and(EdgeField.TO.toString()).is(to);
        }

        return p;
    }

    private Predicate addLabel(String label) {
        return Criteria.where(EdgeField.LABEL.toString()).is(label);
    }

    private Predicate addType(EdgeType type) {
        if (type == EdgeType.COUSIN) {
            return Criteria.where(EdgeProperty.CONTAINS.toString()).is(AAIDirection.NONE.toString());
        } else { // equals TREE
            return Criteria.where(EdgeProperty.CONTAINS.toString()).ne(AAIDirection.NONE.toString());
        }
    }

    private Predicate addDirection(AAIDirection direction) {
        if (direction == AAIDirection.OUT) {
            return Criteria.where(EdgeField.DIRECTION.toString()).in(AAIDirection.OUT.toString(), AAIDirection.BOTH.toString());
        } else if (direction == AAIDirection.IN) {
            return Criteria.where(EdgeField.DIRECTION.toString()).in(AAIDirection.IN.toString(), AAIDirection.BOTH.toString());
        } else if (direction == AAIDirection.BOTH) {
            return Criteria.where(EdgeField.DIRECTION.toString()).is(AAIDirection.BOTH.toString());
        } else if (direction == AAIDirection.NONE) {
            return Criteria.where(EdgeField.DIRECTION.toString()).is(AAIDirection.NONE.toString());
        }
        return Criteria.where(EdgeField.DIRECTION.toString()).is(AAIDirection.NONE.toString());
    }

    /**
     * Provides the JsonPath filter for actually querying the edge rule schema files
     *
     * @return Filter
     */
    public Filter getFilter() {
        return this.filter;
    }

    /**
     * Gets the first node type given for the query.
     *
     * ie, If you called Builder(A,B) this would return A,
     * if you called Builder(B,A), it would return B,
     * if you called Builder(A), it would return A.
     *
     * This is to maintain backwards compatibility with the
     * EdgeRules API which flipped the direction of
     * the result EdgeRule to match the input directionality.
     *
     * @return String first node type of the query
     */
    public String getFromType() {
        return this.nodeA;
    }

    /**
     * So the Ingestor knows which version of the rules to search
     *
     * @return the Version
     */
    public Optional<SchemaVersion> getVersion() {
        return this.v;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("EdgeRuleQuery with filter params node type: ").append(nodeA);

        if (nodeB != null) {
            sb.append(", node type: ").append(nodeB);
        }

        if (label != null) {
            sb.append(", label: ").append(label);
        }

        sb.append(", type: ");
        if (type != null) {
            sb.append(type.toString());
        } else {
            sb.append("any");
        }

        sb.append(", isPrivate: ");
        sb.append(isPrivate);

        v.ifPresent(schemaVersion -> sb.append(", for version: ").append(schemaVersion.toString()).append("."));
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EdgeRuleQuery ruleQuery = (EdgeRuleQuery) o;
        return isPrivate == ruleQuery.isPrivate && Objects.equals(v, ruleQuery.v)
                && Objects.equals(nodeA, ruleQuery.nodeA) && Objects.equals(nodeB, ruleQuery.nodeB)
                && Objects.equals(label, ruleQuery.label) && direction == ruleQuery.direction && type == ruleQuery.type;
    }

    @Override
    public int hashCode() {
        return v.map(schemaVersion -> Objects.hash(schemaVersion, nodeA, nodeB, label, direction, type, isPrivate))
            .orElseGet(() -> Objects.hash(nodeA, nodeB, label, direction, type, isPrivate));
    }

}
