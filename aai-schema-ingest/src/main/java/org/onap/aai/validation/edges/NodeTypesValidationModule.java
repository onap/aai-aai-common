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
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.validation.edges;


import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.setup.SchemaVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Validates that the node types appearing in the edge rules are valid
 * against the ingested OXM.
 * 
 * (This is why the node validation has to be run before the edge validation)
 */
@Component
public class NodeTypesValidationModule {
	private NodeIngestor ni;
	
	@Autowired
	public NodeTypesValidationModule(NodeIngestor ni) {
		this.ni = ni;
	}
	
	/**
	 * Validate that every node type in the given set is defined in 
	 * the OXM for the given version
	 * 
	 * @param nodeTypePairs - all the node types in
	 * 				the edge rules for the given version being validated
	 * @param v - the version being validated
	 * @return empty string if all types are present in the given version's ingested OXM, else
	 * 	appropriate error message
	 */
	public String validate(Collection<String> nodeTypePairs, SchemaVersion v) {
		//setup
		Set<String> nodeTypes = new HashSet<>();
		for (String pair : nodeTypePairs) {
			String[] types = pair.split("\\|");
			
			for (String type : types) {
				if (!"".equals(type)) {
					nodeTypes.add(type);
				}
			}
		}
		
		//validation
		Set<String> badTypes = new HashSet<>();
		for (String type : nodeTypes) {
			if (!ni.hasNodeType(type, v)) {
				badTypes.add(type);
			}
		}
		
		if (badTypes.isEmpty()) {
			return "";
		} else {
			StringBuilder errorBase = new StringBuilder().append("Invalid node type(s) found: ");
			for (String bt : badTypes) {
				errorBase.append(bt).append(" ");
			}
			return errorBase.toString();
		}
	}
}
