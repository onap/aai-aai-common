/** 
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */

package org.onap.aai.edges;

import org.onap.aai.edges.enums.AAIDirection;
import org.onap.aai.edges.enums.EdgeField;
import org.onap.aai.edges.enums.EdgeProperty;
import org.onap.aai.edges.enums.EdgeType;
import org.onap.aai.setup.Version;

import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.Predicate;

import static com.jayway.jsonpath.Filter.filter;

import java.util.ArrayList;
import java.util.List;

import static com.jayway.jsonpath.Criteria.where;

/**
 * For querying the edge rules schema (not the database)
 *
 */
public class EdgeRuleQuery {
	private Filter filter;
	private Version v;
	private String nodeA;
	private String nodeB;
	private String label;
	private EdgeType type;

	public static class Builder {
		//required 
		private String nodeA;
		
		//optional - null will translate to any value of the param
		private String nodeB = null; 
		private String label = null;
		private EdgeType type = null;
		private Version version = Version.getLatest(); //default
		
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
		
		private String getSecondNodeType() {
			return nodeB;
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
		
		public Builder version(Version version) {
			this.version = version;
			return this;
		}
		private Version getVersion() {
			return version;
		}
		
		public EdgeRuleQuery build() {
			return new EdgeRuleQuery(this);
		}
	}
	
	private EdgeRuleQuery(Builder builder) {
		this.v = builder.getVersion();
		this.nodeA = builder.getFirstNodeType();
		this.nodeB = builder.getSecondNodeType();
		this.label = builder.getLabel();
		this.type = builder.getEdgeType();
		
		//will cover from A to B case
		List<Predicate> criteriaFromTo = new ArrayList<>();
		criteriaFromTo.add(buildToFromPart(builder.getFirstNodeType(), builder.getSecondNodeType()));
		//will cover from B to A case - must be separate bc jsonpath won't let me OR predicates >:C
		List<Predicate> criteriaToFrom = new ArrayList<>();
		criteriaToFrom.add(buildToFromPart(builder.getSecondNodeType(), builder.getFirstNodeType()));
		
		
		
		if (builder.getLabel() != null) {
			Predicate labelPred = addLabel(builder.getLabel());
			criteriaFromTo.add(labelPred);
			criteriaToFrom.add(labelPred);
		}
		
		if (builder.getEdgeType() != null) {
			Predicate typePred = addType(builder.getEdgeType());
			criteriaFromTo.add(typePred);
			criteriaToFrom.add(typePred);
		}
		

		
		this.filter = filter(criteriaFromTo).or(filter(criteriaToFrom));
	}
	
	private Predicate buildToFromPart(String from, String to) {
		if (from == null && to == null) { //shouldn't ever happen though
			throw new IllegalStateException("must have at least one node defined");
		}
		
		Predicate p;
		
		if (to == null) {
			p = where(EdgeField.FROM.toString()).is(from);
		} else if (from == null) {
			p = where(EdgeField.TO.toString()).is(to);
		} else {
			p = where(EdgeField.FROM.toString()).is(from).and(EdgeField.TO.toString()).is(to);
		}
		
		return p;
	}
	
	private Predicate addLabel(String label) {
		return where(EdgeField.LABEL.toString()).is(label);
	}
	
	private Predicate addType(EdgeType type) {
		if (type == EdgeType.COUSIN) {
			return where(EdgeProperty.CONTAINS.toString()).is(AAIDirection.NONE.toString());
		} else { //equals TREE
			return where(EdgeProperty.CONTAINS.toString()).ne(AAIDirection.NONE.toString());
		}
	}
	
	/**
	 * Provides the JsonPath filter for actually querying the edge rule schema files
	 * @return Filter
	 */
	public Filter getFilter() {
		return this.filter;
	}
	
	/**
	 * So the Ingestor knows which version of the rules to search
	 * @return the Version
	 */
	public Version getVersion() {
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
		
		sb.append(", for version: ").append(v.toString()).append(".");
		return sb.toString();	
	}
}
