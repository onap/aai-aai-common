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
package org.onap.aai.util.genxsd;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdgeDescription {
	private static final Logger logger = LoggerFactory.getLogger("EdgeDescription.class");

	public static enum LineageType {
		PARENT, CHILD, UNRELATED;
	}
	private String ruleKey;
	private String to;
	private String from;
	private LineageType type = LineageType.UNRELATED;
	private String direction;
	private String multiplicity;
	private String preventDelete;
	private String deleteOtherV;
	private String label;
	private String description;
	/**
	 * @return the deleteOtherV
	 */
	public String getDeleteOtherV() {
		return deleteOtherV;
	}
	/**
	 * @param deleteOtherV the deleteOtherV to set
	 */
	public void setDeleteOtherV(String deleteOtherV) {
		logger.debug("Edge: "+this.getRuleKey());
		logger.debug("Truth: "+(("${direction}".equals(deleteOtherV)) ? "true" : "false"));
		logger.debug("Truth: "+(("!${direction}".equals(deleteOtherV)) ? "true" : "false"));

		if("${direction}".equals(deleteOtherV) ) {
			this.deleteOtherV = this.direction;
		} else if("!${direction}".equals(deleteOtherV) ) {
			this.deleteOtherV = this.direction.equals("IN") ? "OUT" : ((this.direction.equals("OUT")) ? "IN" : deleteOtherV);
		} else {
			this.deleteOtherV = deleteOtherV;
		}
		logger.debug("DeleteOtherV="+deleteOtherV+"/"+this.direction+"="+this.deleteOtherV);
	}
	/**
	 * @return the preventDelete
	 */
	public String getPreventDelete() {
		return preventDelete;
	}
	/**
	 * @param preventDelete the preventDelete to set
	 */
	public void setPreventDelete(String preventDelete) {
		if(this.getTo().equals("flavor") || this.getFrom().equals("flavor") ){
			logger.debug("Edge: "+this.getRuleKey());
			logger.debug("Truth: "+(("${direction}".equals(preventDelete)) ? "true" : "false"));
			logger.debug("Truth: "+(("!${direction}".equals(preventDelete)) ? "true" : "false"));
		}

		if("${direction}".equals(preventDelete) ) {
			this.preventDelete = this.direction;
		} else if("!${direction}".equals(preventDelete) ) {
			this.preventDelete = this.direction.equals("IN") ? "OUT" : ((this.direction.equals("OUT")) ? "IN" : preventDelete);
		} else {
			this.preventDelete = preventDelete;
		}
	}
	public String getAlsoDeleteFootnote(String targetNode) {
		String returnVal = "";
		if(this.deleteOtherV.equals("IN") && this.to.equals(targetNode) ) {
			logger.debug("Edge: "+this.getRuleKey());
			logger.debug("IF this "+targetNode+" node is deleted, this FROM node is DELETED also");
			returnVal = "(1)";
		}
		if(this.deleteOtherV.equals("OUT") && this.from.equals(targetNode) ) {
			logger.debug("Edge: "+this.getRuleKey());
			logger.debug("IF this "+targetNode+" is deleted, this TO node is DELETED also");
			returnVal = "(2)";
		}
		if(this.deleteOtherV.equals("OUT") && this.to.equals(targetNode) ) {
			logger.debug("Edge: "+this.getRuleKey());
			logger.debug("IF this FROM node is deleted, this "+targetNode+" is DELETED also");
			returnVal = "(3)";
		}
		if(this.deleteOtherV.equals("IN") && this.from.equals(targetNode) ) {
			logger.debug("Edge: "+this.getRuleKey());
			logger.debug("IF this TO node is deleted, this "+targetNode+" node is DELETED also");
			returnVal = "(4)";
		}
		return returnVal;
	}
	/**
	 * @return the to
	 */
	public String getTo() {
		return to;
	}
	/**
	 * @param to the to to set
	 */
	public void setTo(String to) {
		this.to = to;
	}
	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}
	/**
	 * @param from the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}


	public String getRuleKey() {
		return ruleKey;
	}
	public String getMultiplicity() {
		return multiplicity;
	}
	public String getDirection() {
		return direction;
	}
	public String getDescription() {
		return this.description;
	}
	public void setRuleKey(String val) {
		this.ruleKey=val;
	}
	public void setType(LineageType val) {
		this.type=val;
	}
	public void setDirection(String val) {
		this.direction = val;
	}
	public void setMultiplicity(String val) {
		this.multiplicity=val;
	}
	
	public void setDescription(String val) {
		this.description = val;
	}

	public String getRelationshipDescription(String fromTo, String otherNodeName) {
		
		String result = "";		

		if ("FROM".equals(fromTo)) {
			if ("OUT".equals(direction)) {
				if (LineageType.PARENT == type) {
					result = " (PARENT of "+otherNodeName;
					result = String.join(" ", result+",", this.from, this.getShortLabel(), this.to);
				} 
			} 
			else {
				if (LineageType.CHILD == type) {
					result = " (CHILD of "+otherNodeName;
					result = String.join(" ", result+",",  this.from, this.getShortLabel(), this.to);
				} 
				else if (LineageType.PARENT == type) {
					result = " (PARENT of "+otherNodeName;
					result = String.join(" ", result+",", this.from, this.getShortLabel(), this.to);
				}
			}
			if (result.length() == 0) result = String.join(" ", "(", this.from, this.getShortLabel(), this.to+",", this.getMultiplicity());
		} else {
		//if ("TO".equals(fromTo)
			if ("OUT".equals(direction)) {
				if (LineageType.PARENT == type) {
					result = " (CHILD of "+otherNodeName;
					result = String.join(" ", result+",", this.from, this.getShortLabel(), this.to+",", this.getMultiplicity());
				} 
			} else {
				if (LineageType.PARENT == type) {
					result = " (PARENT of "+otherNodeName;
					result = String.join(" ", result+",", this.from, this.getShortLabel(), this.to+",", this.getMultiplicity());
				}
			}
			if (result.length() == 0) result = String.join(" ", "(", this.from, this.getShortLabel(), this.to+",", this.getMultiplicity());
		}
//      Confusing...
//		if (hasDelTarget()) result = result + ", will delete target node";

		if (result.length() > 0) result = result + ")";
		
		if (description != null && description.length() > 0) result = result + "\n      "+ description; // 6 spaces is important for yaml
		
		return result;
	}

	/**
	 * @return the hasDelTarget
	 */
	
	public boolean hasDelTarget() {
		return StringUtils.isNotEmpty(this.deleteOtherV) && (! "NONE".equalsIgnoreCase(this.deleteOtherV));
	}
	
	/**
	 * @return the type
	 */
	public LineageType getType() {
		return type;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	public String getShortLabel() {
		String[] pieces = this.getLabel().split("[.]");
		return pieces[pieces.length-1];
	}
	public void setLabel(String string) {
		this.label=string;
	}
}