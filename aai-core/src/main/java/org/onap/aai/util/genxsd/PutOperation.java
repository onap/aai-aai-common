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
import org.onap.aai.introspection.Version;
import org.onap.aai.util.GenerateXsd;

import java.util.StringTokenizer;

public class PutOperation {
	private String useOpId;
	private String xmlRootElementName;
	private String tag;
	private String path;
	private String pathParams;
	private Version version;
		
		public PutOperation(String useOpId, String xmlRootElementName, String tag, String path, String pathParams, Version v) {
			super();
			this.useOpId = useOpId;
			this.xmlRootElementName = xmlRootElementName;
			this.tag = tag;
			this.path = path;
			this.pathParams = pathParams;
			this.version = v;
		}

		@Override
		public String toString() {
			StringTokenizer st = new StringTokenizer(path, "/");
			//a valid tag is necessary
			if ( StringUtils.isEmpty(tag) ) {
				return "";
			}
			//All Put operation paths end with "relationship"
			//or there is a parameter at the end of the path
			//and there is a parameter in the path
			if ( path.contains("/relationship/") ) { // filter paths with relationship-list
				return "";
			}
			if ( path.endsWith("/relationship-list")) {
				return "";
			}
			if ( !path.endsWith("/relationship")  &&  !path.endsWith("}") ) {
				return "";
			}
			if ( path.startsWith("/search")) {
				return "";
			}
			StringBuffer pathSb = new StringBuffer();
			StringBuffer relationshipExamplesSb = new StringBuffer();
			if ( path.endsWith("/relationship") ) {
				pathSb.append("  " + path + ":\n" );
			}
			pathSb.append("    put:\n");
			pathSb.append("      tags:\n");
			pathSb.append("        - " + tag + "\n");

			if ( path.endsWith("/relationship") ) {
				pathSb.append("      summary: see node definition for valid relationships\n");
			} else {
				pathSb.append("      summary: create or update an existing " + xmlRootElementName + "\n");
				pathSb.append("      description: |\n        Create or update an existing " + xmlRootElementName + ".\n        #\n        Note! This PUT method has a corresponding PATCH method that can be used to update just a few of the fields of an existing object, rather than a full object replacement.  An example can be found in the [PATCH section] below\n");
			}
			relationshipExamplesSb.append("[Valid relationship examples shown here](apidocs/relations/"+version.name()+"/"+useOpId.replace("RelationshipListRelationship", "")+".json)");
			pathSb.append("      operationId: createOrUpdate" + useOpId + "\n");
			pathSb.append("      consumes:\n");
			pathSb.append("        - application/json\n");
			pathSb.append("        - application/xml\n");					
			pathSb.append("      produces:\n");
			pathSb.append("        - application/json\n");
			pathSb.append("        - application/xml\n");
			pathSb.append("      responses:\n");
			pathSb.append("        \"default\":\n");
			pathSb.append("          " + GenerateXsd.getResponsesUrl());
		
			pathSb.append("      parameters:\n");
			pathSb.append(pathParams); // for nesting
			pathSb.append("        - name: body\n");
			pathSb.append("          in: body\n");
			pathSb.append("          description: " + xmlRootElementName + " object that needs to be created or updated. "+relationshipExamplesSb.toString()+"\n");
			pathSb.append("          required: true\n");
			pathSb.append("          schema:\n");
			pathSb.append("            $ref: \"#/definitions/" + xmlRootElementName + "\"\n");
			this.tagRelationshipPathMapEntry();
			return pathSb.toString();
		}

		public String tagRelationshipPathMapEntry() {
			if ( path.endsWith("/relationship") ) {
				PutRelationPathSet.add(useOpId, path);
			}
			return "";
		}
		
	}