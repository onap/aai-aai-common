/*-
 * ============LICENSE_START=======================================================
 * org.openecomp.aai
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.aai.dbmodel.v8.gen;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

public class DbEdgeRules {

	/*
	 * The EdgeRules data is set up as a key (fromNodeTypeA|toNodeTypeB) mapped
	 * to a string which holds the info we need to build an edge from nodeTypeA
	 * to nodeTypeB. Note -- the MultiMap will let us define more than one type
	 * of edge between a given pair of nodeTypes, but for now we never define
	 * more than one.
	 * 
	 * The edgeInfo part is comma separated and looks like this:
	 * "edgeLabel,direction,multiplicityRule,isParent,usesResource,hasDelTarget,SVC-INFRA" This
	 * format is encoded into the EdgeInfoMap below. 
	 * MultiplicityRule can be either "Many2Many", "Many2One", "One2Many" or "One2One"
	 * The values for the things after multiplicityRule can be either "true", "false" or "reverse". "reverse" is
	 * really saying that this tag does apply, but the edge will be traversed
	 * the opposite way from the same tag that just has "true".
	 */
	public static final Map<Integer, String> EdgeInfoMap;
	static {
		EdgeInfoMap = new HashMap<Integer, String>();
		EdgeInfoMap.put(0, "edgeLabel");
		EdgeInfoMap.put(1, "direction");
		EdgeInfoMap.put(2, "multiplicityRule");
		EdgeInfoMap.put(3, "isParent");
		EdgeInfoMap.put(4, "usesResource");
		EdgeInfoMap.put(5, "hasDelTarget");
		EdgeInfoMap.put(6, "SVC-INFRA");
	}
	
	public static Integer firstTagIndex = 3;

	public static final Multimap<String, String> EdgeRules = new ImmutableSetMultimap.Builder<String, String>()
			.putAll("cloud-region|l3-network",
					"uses,OUT,Many2Many,false,false,false,false")
			.putAll("cloud-region|tenant",
					"has,OUT,One2Many,true,false,false,reverse")
			.putAll("cloud-region|image",
					"has,OUT,One2Many,true,false,false,false")
			.putAll("cloud-region|flavor",
					"has,OUT,One2Many,true,false,false,false")
			.putAll("cloud-region|availability-zone",
					"has,OUT,One2Many,true,false,false,false")
			.putAll("cloud-region|volume-group",
					"has,OUT,One2Many,true,true,false,false")
			.putAll("cloud-region|group-assignment",
					"has,OUT,One2Many,true,false,false,false")
			.putAll("cloud-region|snapshot",
					"has,OUT,One2Many,true,false,false,false")
			.putAll("customer|service-subscription",
					"subscribesTo,OUT,Many2Many,true,false,false,reverse")
			.putAll("generic-vnf|l-interface",
					"hasLInterface,OUT,Many2Many,true,false,false,true")
			.putAll("generic-vnf|availability-zone",
					"hasAvailabilityZone,OUT,Many2Many,false,false,false,true")
			.putAll("generic-vnf|lag-interface",
					"hasLAGInterface,OUT,Many2Many,true,false,false,true")
			.putAll("generic-vnf|l3-network",
					"usesL3Network,OUT,Many2Many,false,true,false,true")
			.putAll("generic-vnf|pserver",
					"runsOnPserver,OUT,Many2Many,false,true,false,true")
			.putAll("generic-vnf|vserver",
					"runsOnVserver,OUT,One2Many,false,true,false,true")
			.putAll("generic-vnf|service-instance",
					"hasInstance,OUT,Many2Many,false,true,false,true")
			.putAll("group-assignment|tenant",
					"has,OUT,Many2Many,false,false,false,false")
			.putAll("group-assignment|pserver",
					"has,OUT,One2Many,false,false,false,false")
			.putAll("image|metadata", "hasMetaData,OUT,Many2Many,true,false,false,false")
			.putAll("image|metadatum",
					"hasMetaDatum,OUT,Many2Many,true,false,false,false")
			.putAll("l-interface|l3-interface-ipv4-address-list",
					"hasIpAddress,OUT,Many2Many,true,false,false,true")
			.putAll("l-interface|l3-interface-ipv6-address-list",
					"hasIpAddress,OUT,Many2Many,true,false,false,true")
			.putAll("l-interface|logical-link",
					"usesLogicalLink,OUT,Many2Many,false,false,true,true")
			.putAll("l-interface|vlan","hasVlan,OUT,Many2Many,true,false,false,false")
			.putAll("l3-interface-ipv4-address-list|l3-network",
					"isMemberOf,OUT,Many2Many,false,false,false,true")
			.putAll("l3-interface-ipv6-address-list|l3-network",
					"isMemberOf,OUT,Many2Many,false,false,false,true")
			.putAll("l3-interface-ipv4-address-list|subnet",
					"isMemberOf,OUT,Many2Many,false,false,false,true")
			.putAll("l3-interface-ipv6-address-list|subnet",
					"isMemberOf,OUT,Many2Many,false,false,false,true")
			.putAll("l3-network|subnet",
					"hasSubnet,OUT,Many2Many,true,false,false,reverse")
			.putAll("l3-network|service-instance",
					"hasInstance,OUT,Many2Many,false,false,false,reverse")
			.putAll("l3-network|ctag-assignment",
					"hasCtagAssignment,OUT,Many2Many,true,false,false,true")
			.putAll("l3-network|segmentation-assignment",
					"has,OUT,One2Many,true,false,false,false")
			.putAll("lag-interface|p-interface",
					"usesPInterface,OUT,Many2Many,false,true,false,true")
			.putAll("lag-interface|l-interface",
					"hasLInterface,OUT,Many2Many,true,false,false,true")		
			.putAll("logical-link|pnf",
					"bridgedTo,OUT,Many2Many,false,false,false,false")	
			.putAll("logical-link|logical-link",
					"uses,OUT,One2Many,false,false,false,true")	
			.putAll("model|model-element",
					"startsWith,OUT,One2Many,true,false,false,false")
			.putAll("model-element|model",
					"isA,OUT,Many2One,false,false,false,false")
			.putAll("model|metadatum",
					"hasMetaData,OUT,One2Many,true,false,false,false")
			.putAll("model-element|model-element",
					"connectsTo,OUT,One2Many,true,false,false,false")
			.putAll("model-element|model-constraint",
					"uses,OUT,One2Many,true,false,false,false")
			.putAll("model-element|constrained-element-set",
					"connectsTo,OUT,One2Many,true,false,false,false")
			.putAll("model-constraint|constrained-element-set",
					"uses,OUT,One2Many,true,false,false,false")
			.putAll("constrained-element-set|element-choice-set",
					"uses,OUT,One2Many,true,false,false,false")
			.putAll("element-choice-set|model-element",
					"has,OUT,One2Many,true,false,false,false")
			.putAll("named-query|model",
					"relatedTo,OUT,One2Many,false,false,false,false")
			.putAll("named-query|named-query-element",
					"startsWith,OUT,One2One,true,false,false,false")
			.putAll("named-query-element|named-query-element",
					"connectsTo,OUT,Many2Many,true,false,false,false")
			.putAll("named-query-element|model",
					"isA,OUT,Many2One,false,false,false,false")
			.putAll("named-query-element|property-constraint",
					"uses,OUT,One2Many,true,false,false,false")
			.putAll("named-query-element|related-lookup",
					"uses,OUT,One2Many,true,false,false,false")
			.putAll("p-interface|l-interface",
					"hasLInterface,OUT,Many2Many,true,false,false,true")
			.putAll("p-interface|physical-link",
					"usesPhysicalLink,OUT,Many2Many,false,false,true,false")
			.putAll("p-interface|logical-link",
                    "usesLogicalLink,OUT,Many2One,false,false,false,true")		
			.putAll("pserver|cloud-region","locatedIn,OUT,Many2One,false,false,false,true")
			.putAll("pserver|availability-zone","existsIn,OUT,Many2One,false,false,false,true")
			.putAll("pserver|lag-interface",
					"hasLAGInterface,OUT,Many2Many,true,false,false,true")
			.putAll("pserver|p-interface",
					"hasPinterface,OUT,Many2Many,true,true,false,true")
			.putAll("pnf|p-interface",
					"hasPinterface,OUT,Many2Many,true,true,false,true")
			.putAll("pnf|lag-interface",
					"has,OUT,One2Many,true,false,false,true")
			.putAll("service-instance|pnf",
					"uses,OUT,One2Many,false,true,false,false")	
			.putAll("service-subscription|service-instance",
					"hasInstance,OUT,Many2Many,true,false,false,reverse")
			.putAll("tenant|l3-network",
					"usesL3Network,OUT,Many2Many,false,false,false,false")
			.putAll("tenant|service-subscription",
					"relatedTo,OUT,Many2Many,false,false,false,false")
			.putAll("tenant|vserver", "owns,OUT,One2Many,true,false,false,reverse")
			.putAll("vlan|l3-interface-ipv4-address-list",
					"hasIpAddress,OUT,Many2Many,true,false,false,true")
			.putAll("vlan|l3-interface-ipv6-address-list",
					"hasIpAddress,OUT,Many2Many,true,false,false,true")
			.putAll("vserver|flavor", "hasFlavor,OUT,Many2One,false,false,false,true")
			.putAll("vserver|image", "hasImage,OUT,Many2One,false,false,false,true")
			.putAll("vserver|ipaddress",
					"hasIpAddress,OUT,Many2Many,true,true,false,false")
			.putAll("vserver|l-interface",
					"hasLInterface,OUT,Many2Many,true,false,false,true")
			.putAll("vserver|pserver",
					"runsOnPserver,OUT,Many2One,false,true,false,true")
			.putAll("vserver|volume", "hasVolume,OUT,Many2Many,true,true,false,true")
			.putAll("vserver|vnfc", "hosts,OUT,Many2Many,false,true,false,true")
			.putAll("vserver|snapshot", "uses,OUT,One2One,false,false,false,true")
            .putAll("service-instance|metadatum", "hasMetaData,OUT,Many2Many,true,false,false,false")
            .putAll("service-instance|logical-link", "uses,OUT,Many2Many,false,false,true,false")
			.putAll("service-instance|vlan", "dependsOn,OUT,One2Many,false,true,false,false")
			.putAll("service-instance|service-instance", "dependsOn,OUT,One2Many,false,true,false,false")
            .putAll("logical-link|generic-vnf", "bridgedTo,OUT,Many2Many,false,false,false,false")
            .putAll("logical-link|pserver", "bridgedTo,OUT,Many2Many,false,false,false,false")
            .putAll("volume-group|tenant", "belongsTo,OUT,Many2Many,false,false,false,true")
            .putAll("vf-module|volume-group", "uses,OUT,One2One,false,false,false,true")
            .putAll("vserver|vf-module", "isPartOf,OUT,Many2One,false,false,false,true")
            .putAll("vf-module|l3-network", "uses,OUT,Many2Many,false,false,false,true")
            .putAll("vf-module|vnfc", "uses,OUT,One2Many,false,false,true,true")
            .putAll("generic-vnf|vf-module", "has,OUT,One2Many,true,false,false,true")
            .putAll("generic-vnf|volume-group", "uses,OUT,One2Many,false,false,false,true")
            .putAll("generic-vnf|vnfc", "uses,OUT,One2Many,false,false,true,true")
            .putAll("vlan|logical-link", "usesLogicalLink,OUT,One2One,false,false,true,true")
			.build();

	public static final Multimap<String, String> DefaultDeleteScope = new ImmutableSetMultimap.Builder<String, String>()
			.putAll("customer", "CASCADE_TO_CHILDREN")
			.putAll("cloud-region", "THIS_NODE_ONLY")
			.putAll("service-subscription", "CASCADE_TO_CHILDREN")
			.putAll("service-instance", "CASCADE_TO_CHILDREN")
			.putAll("tenant", "CASCADE_TO_CHILDREN")
			.putAll("vserver", "CASCADE_TO_CHILDREN")
			.putAll("volume", "THIS_NODE_ONLY")
			.putAll("ipaddress", "THIS_NODE_ONLY")
			.putAll("image", "ERROR_4_IN_EDGES_OR_CASCADE")
			.putAll("pserver", "ERROR_4_IN_EDGES_OR_CASCADE")
			.putAll("availability-zone", "ERROR_IF_ANY_IN_EDGES")
			.putAll("flavor", "ERROR_IF_ANY_IN_EDGES")
			.putAll("metadata", "THIS_NODE_ONLY")
			.putAll("metadatum", "THIS_NODE_ONLY")
			.putAll("model", "ERROR_4_IN_EDGES_OR_CASCADE")
			.putAll("model-element", "CASCADE_TO_CHILDREN")
			.putAll("named-query", "CASCADE_TO_CHILDREN")
			.putAll("named-query-element", "CASCADE_TO_CHILDREN")
			.putAll("collect-lookup", "THIS_NODE_ONLY")
			.putAll("service", "ERROR_IF_ANY_IN_EDGES")
			.putAll("l-interface", "CASCADE_TO_CHILDREN")
			.putAll("vlan", "CASCADE_TO_CHILDREN")
			.putAll("p-interface", "CASCADE_TO_CHILDREN")
			.putAll("l3-interface-ipv6-address-list", "THIS_NODE_ONLY")
			.putAll("l3-interface-ipv4-address-list", "THIS_NODE_ONLY")
			.putAll("logical-link", "THIS_NODE_ONLY")
			.putAll("physical-link", "THIS_NODE_ONLY")
			.putAll("lag-interface", "CASCADE_TO_CHILDREN")
			.putAll("l3-network", "CASCADE_TO_CHILDREN")
			.putAll("subnet", "THIS_NODE_ONLY")
			.putAll("vnfc", "THIS_NODE_ONLY")
			.putAll("volume-group", "THIS_NODE_ONLY")
			.putAll("ctag-assignment", "THIS_NODE_ONLY")
			.putAll("pnf", "CASCADE_TO_CHILDREN")
			.putAll("vf-module", "THIS_NODE_ONLY")
			.putAll("snapshot", "THIS_NODE_ONLY")
			.putAll("group-assignment", "THIS_NODE_ONLY")
			.putAll("segmentation-assignment", "THIS_NODE_ONLY")
			.putAll("generic-vnf", "CASCADE_TO_CHILDREN").build();

	// NOTE -- Sorry, this is ugly, but we are mapping the nodeTypeCategory two
	// ways just to
	// make the code a little less bulky. But that means that we need to ensure
	// that
	// nodeTypeCategory and nodeTypeCatMap are kept in synch.

	// NodeTypeCategory: key is: nodeTypeCategory, value is:
	// "nodeTypes,keyProperties,AltKeyProps,depNode4UniquenessFlag"
	public static final Multimap<String, String> NodeTypeCategory = new ImmutableSetMultimap.Builder<String, String>()
			.putAll("vnf", "generic-vnf,vnf-id,,true").build();

	// NodeTypeCatMap: key is nodeType; value is: "nodeTypeCategory"
	// So -- we're assuming that a nodeType can only be in one nodeTypeCategory.
	public static final Map<String, String> NodeTypeCatMap;
	static {
		NodeTypeCatMap = new HashMap<String, String>();
		NodeTypeCatMap.put("generic-vnf", "vnf");
	}

	// ReservedPropNames: keys are property names of (node) properties that are
	// common to all nodes and
	// should not be removed if not passed in on an UPDATE request.
	public static final Map<String, String> ReservedPropNames;
	static {
		ReservedPropNames = new HashMap<String, String>();
		ReservedPropNames.put("source-of-truth", "");
		ReservedPropNames.put("last-mod-source-of-truth", "");
		ReservedPropNames.put("aai-created-ts", "");
		ReservedPropNames.put("aai-last-mod-ts", "");
	}
	
	// This just lists which node types can be connected to themselves recursively.
	// It's temporary - since DbEdgeRules is going to be overhauled in 16-10, this will
	// get generated automatically.   But for 1607, it can work like this.
	public static final Map<String, String> CanBeRecursiveNT;
	static {
		CanBeRecursiveNT = new HashMap<String, String>();
		CanBeRecursiveNT.put("model-element", "");
		CanBeRecursiveNT.put("named-query-element", "");
	}

}
