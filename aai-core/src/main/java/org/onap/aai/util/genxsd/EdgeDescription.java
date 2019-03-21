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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.util.genxsd;

import org.apache.commons.lang3.StringUtils;
import org.onap.aai.edges.EdgeRule;
import org.onap.aai.edges.enums.AAIDirection;
import org.onap.aai.edges.enums.DirectionNotation;
import org.onap.aai.edges.enums.EdgeField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdgeDescription {

    private static final Logger logger = LoggerFactory.getLogger("EdgeDescription.class");
    EdgeRule ed;

    public static enum LineageType {
        PARENT, CHILD, UNRELATED;
    }

    private String ruleKey;
    // private String to;
    // private String from;
    private LineageType lineageType = LineageType.UNRELATED;
    // private String direction;
    // private String multiplicity;
    // private String preventDelete;
    // private String deleteOtherV;
    // private String label;
    // private String description;

    public EdgeDescription(EdgeRule ed) {
        super();
        if (ed.getDirection().toString().equals(ed.getContains())
            && AAIDirection.getValue("OUT").equals(AAIDirection.getValue(ed.getDirection()))) {
            this.lineageType = LineageType.PARENT;
        } else if (AAIDirection.getValue("IN").equals(AAIDirection.getValue(ed.getContains()))
            && ed.getDirection().toString().equals(ed.getContains())) {
            this.lineageType = LineageType.CHILD;
        } else if (AAIDirection.getValue("OUT").equals(AAIDirection.getValue(ed.getContains()))
            && AAIDirection.getValue("IN").equals(AAIDirection.getValue(ed.getDirection()))) {
            this.lineageType = LineageType.PARENT;
        } else if (AAIDirection.getValue("IN").equals(AAIDirection.getValue(ed.getContains()))
            && AAIDirection.getValue("OUT").equals(AAIDirection.getValue(ed.getDirection()))) {
            this.lineageType = LineageType.PARENT;
        } else {
            this.lineageType = LineageType.UNRELATED;
        }
        this.ruleKey = ed.getFrom() + "|" + ed.getTo();
        this.ed = ed;
    }

    /**
     * @return the deleteOtherV
     */
    public String getDeleteOtherV() {
        return ed.getDeleteOtherV();
    }

    /**
     * @return the preventDelete
     */
    public String getPreventDelete() {
        return ed.getPreventDelete();
    }

    public String getAlsoDeleteFootnote(String targetNode) {
        String returnVal = "";
        if (ed.getDeleteOtherV().equals("IN") && ed.getTo().equals(targetNode)) {
            logger.debug("Edge: " + this.ruleKey);
            logger.debug(
                "IF this " + targetNode + " node is deleted, this FROM node is DELETED also");
            returnVal = "(1)";
        }
        if (ed.getDeleteOtherV().equals("OUT") && ed.getFrom().equals(targetNode)) {
            logger.debug("Edge: " + this.ruleKey);
            logger.debug("IF this " + targetNode + " is deleted, this TO node is DELETED also");
            returnVal = "(2)";
        }
        if (ed.getDeleteOtherV().equals("OUT") && ed.getTo().equals(targetNode)) {
            logger.debug("Edge: " + this.ruleKey);
            logger.debug("IF this FROM node is deleted, this " + targetNode + " is DELETED also");
            returnVal = "(3)";
        }
        if (ed.getDeleteOtherV().equals("IN") && ed.getFrom().equals(targetNode)) {
            logger.debug("Edge: " + this.ruleKey);
            logger
                .debug("IF this TO node is deleted, this " + targetNode + " node is DELETED also");
            returnVal = "(4)";
        }
        return returnVal;
    }

    /**
     * @return the to
     */
    public String getTo() {
        return ed.getTo();
    }

    /**
     * @return the from
     */
    public String getFrom() {
        return ed.getFrom();
    }

    public String getRuleKey() {
        return ruleKey;
    }

    public String getMultiplicity() {
        return ed.getMultiplicityRule().toString();
    }

    public AAIDirection getDirection() {
        return AAIDirection.getValue(ed.getDirection());
    }

    public String getDescription() {
        return ed.getDescription();
    }

    public String getRelationshipDescription(String fromTo, String otherNodeName) {

        String result = "";

        if ("FROM".equals(fromTo)) {
            if (AAIDirection.getValue("OUT").equals(AAIDirection.getValue(ed.getDirection()))) {
                if (LineageType.PARENT == lineageType) {
                    result = " (PARENT of " + otherNodeName;
                    result = String.join(" ", result + ",", ed.getFrom(), this.getShortLabel(),
                        ed.getTo() + ",", this.getMultiplicity());
                }
            } else {
                if (LineageType.CHILD == lineageType) {
                    result = " (CHILD of " + otherNodeName;
                    result = String.join(" ", result + ",", ed.getFrom(), this.getShortLabel(),
                        ed.getTo() + ",", this.getMultiplicity());
                } else if (LineageType.PARENT == lineageType) {
                    result = " (PARENT of " + otherNodeName;
                    result = String.join(" ", result + ",", ed.getFrom(), this.getShortLabel(),
                        ed.getTo() + ",", this.getMultiplicity());
                }
            }
            if (result.length() == 0)
                result = String.join(" ", "(", ed.getFrom(), this.getShortLabel(), ed.getTo() + ",",
                    this.getMultiplicity());
        } else {
            // if ("TO".equals(fromTo)
            if (AAIDirection.getValue("OUT").equals(AAIDirection.getValue(ed.getDirection()))) {
                if (LineageType.PARENT == lineageType) {
                    result = " (PARENT of " + otherNodeName;
                    result = String.join(" ", result + ",", ed.getFrom(), this.getShortLabel(),
                        ed.getTo() + ",", this.getMultiplicity());
                }
            } else {
                if (LineageType.PARENT == lineageType) {
                    result = " (PARENT of " + otherNodeName;
                    result = String.join(" ", result + ",", ed.getFrom(), this.getShortLabel(),
                        ed.getTo() + ",", this.getMultiplicity());
                }
            }
            if (result.length() == 0)
                result = String.join(" ", "(", ed.getFrom(), this.getShortLabel(), ed.getTo() + ",",
                    this.getMultiplicity());
        }

        if (result.length() > 0)
            result = result + ")";

        if (ed.getDescription() != null && ed.getDescription().length() > 0)
            result = result + "\n      " + ed.getDescription(); // 6 spaces is important for yaml

        return result;
    }

    /**
     * @return the hasDelTarget
     */

    public boolean hasDelTarget() {
        return StringUtils.isNotEmpty(ed.getDeleteOtherV())
            && (!"NONE".equalsIgnoreCase(ed.getDeleteOtherV()));
    }

    /**
     * @return the type
     */
    public LineageType getType() {

        return lineageType;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return ed.getLabel();
    }

    public String getShortLabel() {
        String[] pieces = this.getLabel().split("[.]");
        return pieces[pieces.length - 1];
    }
}
