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

import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.onap.aai.util.GenerateXsd;

public class PatchOperation {
    private String useOpId;
    private String xmlRootElementName;
    private String tag;
    private String path;
    private String pathParams;

    public PatchOperation(String useOpId, String xmlRootElementName, String tag, String path,
        String pathParams) {
        super();
        this.useOpId = useOpId;
        this.xmlRootElementName = xmlRootElementName;
        this.tag = tag;
        this.path = path;
        this.pathParams = pathParams;
    }

    public String toString() {
        StringTokenizer st;
        st = new StringTokenizer(path, "/");
        // a valid tag is necessary
        if (StringUtils.isEmpty(tag)) {
            return "";
        }
        if (path.contains("/relationship/")) { // filter paths with relationship-list
            return "";
        }
        if (path.endsWith("/relationship-list")) {
            return "";
        }
        if (path.startsWith("/search")) {
            return "";
        }
        // No Patch operation paths end with "relationship"

        if (path.endsWith("/relationship")) {
            return "";
        }
        if (!path.endsWith("}")) {
            return "";
        }

        StringBuffer pathSb = new StringBuffer();
        StringBuffer relationshipExamplesSb = new StringBuffer();
        if (path.endsWith("/relationship")) {
            pathSb.append("  " + path + ":\n");
        }
        pathSb.append("    patch:\n");
        pathSb.append("      tags:\n");
        pathSb.append("        - " + tag + "\n");

        if (path.endsWith("/relationship")) {
            pathSb.append("      summary: see node definition for valid relationships\n");
            relationshipExamplesSb.append("[See Examples](apidocs/relations/"
                + GenerateXsd.getAPIVersion() + "/" + useOpId + ".json)");
        } else {
            pathSb.append("      summary: update an existing " + xmlRootElementName + "\n");
            pathSb.append("      description: |\n");
            pathSb.append("        Update an existing " + xmlRootElementName + "\n");
            pathSb.append("        #\n");
            pathSb.append(
                "        Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.\n");
            pathSb.append("        The PUT operation will entirely replace an existing object.\n");
            pathSb.append(
                "        The PATCH operation sends a \"description of changes\" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.\n");
            pathSb.append("        #\n");
            pathSb.append("        Other differences between PUT and PATCH are:\n");
            pathSb.append("        #\n");
            pathSb.append(
                "        - For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.\n");
            pathSb.append(
                "        - For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.\n");
            pathSb.append(
                "        - PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.\n");
        }
        pathSb.append("      operationId: Update" + useOpId + "\n");
        pathSb.append("      consumes:\n");
        pathSb.append("        - application/json\n");
        pathSb.append("      produces:\n");
        pathSb.append("        - application/json\n");
        pathSb.append("      responses:\n");
        pathSb.append("        \"default\":\n");
        pathSb.append("          " + GenerateXsd.getResponsesUrl());
        pathSb.append("      parameters:\n");
        pathSb.append(pathParams); // for nesting
        pathSb.append("        - name: body\n");
        pathSb.append("          in: body\n");
        pathSb.append("          description: " + xmlRootElementName
            + " object that needs to be updated." + relationshipExamplesSb.toString() + "\n");
        pathSb.append("          required: true\n");
        pathSb.append("          schema:\n");
        pathSb.append("            $ref: \"#/patchDefinitions/" + xmlRootElementName + "\"\n");
        return pathSb.toString();
    }
}
