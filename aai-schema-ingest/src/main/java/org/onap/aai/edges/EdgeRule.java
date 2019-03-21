/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-18 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.edges;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.onap.aai.edges.enums.*;

/**
 * Container for A&AI edge rule information
 */
public class EdgeRule {
    private String from;
    private String to;
    private String label;
    private Direction direction;
    private MultiplicityRule multiplicityRule;
    private Map<EdgeProperty, AAIDirection> edgeFields;
    private boolean isDefaultEdge;
    private String description;
    private boolean isPrivateEdge = false;

    /**
     * Instantiates a new edge rule.
     * 
     * @param fieldVals - Map<String, String> where first string is
     *        an EdgeField value and second string is the
     *        value of that field
     */
    public EdgeRule(Map<String, String> fieldVals) {
        edgeFields = new EnumMap<>(EdgeProperty.class);

        from = fieldVals.get(EdgeField.FROM.toString());
        to = fieldVals.get(EdgeField.TO.toString());
        label = fieldVals.get(EdgeField.LABEL.toString());
        direction = Direction.valueOf(fieldVals.get(EdgeField.DIRECTION.toString()));
        multiplicityRule =
            MultiplicityRule.getValue(fieldVals.get(EdgeField.MULTIPLICITY.toString()));
        isPrivateEdge =
            Boolean.valueOf(fieldVals.getOrDefault(EdgeField.PRIVATE.toString(), "false"));

        for (EdgeProperty prop : EdgeProperty.values()) {
            String rawVal = fieldVals.get(prop.toString());
            edgeFields.put(prop, convertNotation(direction, rawVal));
        }

        isDefaultEdge = Boolean.valueOf(fieldVals.get(EdgeField.DEFAULT.toString()));
        description = fieldVals.get(EdgeField.DESCRIPTION.toString());
        if (description == null) { // bc description is optional and not in v12 and earlier
            description = "";
        }
    }

    // Copy Constructor
    public EdgeRule(EdgeRule edgeRule) {
        this.from = edgeRule.from;
        this.to = edgeRule.to;
        this.label = edgeRule.label;
        this.direction = Direction.valueOf(edgeRule.direction.toString());
        this.multiplicityRule = MultiplicityRule.valueOf(edgeRule.multiplicityRule.toString());
        this.edgeFields = new HashMap<>(edgeRule.edgeFields);
        this.isDefaultEdge = edgeRule.isDefaultEdge;
        this.description = edgeRule.description;
        this.isPrivateEdge = edgeRule.isPrivateEdge;
    }

    /**
     * Converts whatever string was in the json for an edge property value into
     * the appropriate AAIDirection
     * 
     * @param Direction dir - the edge direction
     * @param String rawVal - property value from the json, may be
     *        IN, OUT, BOTH, NONE, ${direction}, or !${direction}
     * @return AAIDirection - IN/OUT/BOTH/NONE if that's the rawVal, or
     *         translates the direction notation into the correct IN/OUT
     */
    private AAIDirection convertNotation(Direction dir, String rawVal) {
        if (AAIDirection.NONE.toString().equalsIgnoreCase(rawVal)) {
            return AAIDirection.NONE;
        } else if (AAIDirection.BOTH.toString().equalsIgnoreCase(rawVal)) {
            return AAIDirection.BOTH;
        } else if (AAIDirection.OUT.toString().equalsIgnoreCase(rawVal)) {
            return AAIDirection.OUT;
        } else if (AAIDirection.IN.toString().equalsIgnoreCase(rawVal)) {
            return AAIDirection.IN;
        }

        DirectionNotation rawDN = DirectionNotation.getValue(rawVal);
        if (DirectionNotation.DIRECTION.equals(rawDN)) {
            return AAIDirection.getValue(dir);
        } else {
            return AAIDirection.getValue(dir.opposite());
        }
    }

    /**
     * Gets the name of the node type in the "from" field
     * 
     * @return String nodetype
     */
    public String getFrom() {
        return from;
    }

    /**
     * Gets the name of the node type in the "to" field
     * 
     * @return String nodetype
     */
    public String getTo() {
        return to;
    }

    /**
     * Gets the edge label
     *
     * @return String label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets the multiplicity rule.
     *
     * @return MultiplicityRule
     */
    public MultiplicityRule getMultiplicityRule() {
        return multiplicityRule;
    }

    /**
     * Gets the edge direction
     *
     * @return Direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Gets the value of contains-other-v
     *
     * @return the value of contains-other-v
     */
    public String getContains() {
        return edgeFields.get(EdgeProperty.CONTAINS).toString();
    }

    /**
     * Gets the value of delete-other-v
     *
     * @return the value of delete-other-v
     */
    public String getDeleteOtherV() {
        return edgeFields.get(EdgeProperty.DELETE_OTHER_V).toString();
    }

    /**
     * Gets the value of the prevent-delete property
     * 
     * @return String prevent-delete property value
     */
    public String getPreventDelete() {
        return edgeFields.get(EdgeProperty.PREVENT_DELETE).toString();
    }

    /**
     * Returns if this rule is a default or not
     * 
     * @return boolean
     */
    public boolean isDefault() {
        return isDefaultEdge;
    }

    /**
     * Gets the description on the edge rule (if there is one)
     * 
     * @return String description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Flips the direction value
     * IN -> OUT
     * OUT -> IN
     * BOTH -> BOTH
     */
    public void flipDirection() {
        if (Direction.OUT.equals(direction)) {
            direction = Direction.IN;
        } else if (Direction.IN.equals(direction)) {
            direction = Direction.OUT;
        }
        // else BOTH just stays the same
    }

    public boolean isPrivateEdge() {
        return isPrivateEdge;
    }

    public void setPrivateEdge(boolean privateEdge) {
        isPrivateEdge = privateEdge;
    }

    public void setPrivateEdge(String isPrivateEdge) {
        this.isPrivateEdge = "true".equals(isPrivateEdge);
    }
}
