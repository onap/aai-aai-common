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

import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.onap.aai.util.GenerateXsd;

public class DeleteOperation {
    private String useOpId;
    private String xmlRootElementName;
    private String tag;
    private String path;
    private String pathParams;

    public static HashMap<String, String> deletePaths = new HashMap<String, String>();

    public DeleteOperation(String useOpId, String xmlRootElementName, String tag, String path,
        String pathParams) {
        super();
        this.useOpId = useOpId;
        this.xmlRootElementName = xmlRootElementName;
        this.tag = tag;
        this.path = path;
        this.pathParams = pathParams;
    }

    @Override
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
        // All Delete operation paths end with "relationship"
        // or there is a parameter at the end of the path
        // and there is a parameter in the path

        if (!path.endsWith("/relationship") && !path.endsWith("}")) {
            return "";
        }
        StringBuffer pathSb = new StringBuffer();
        pathSb.append("    delete:\n");
        pathSb.append("      tags:\n");
        pathSb.append("        - " + tag + "\n");
        pathSb.append("      summary: delete an existing " + xmlRootElementName + "\n");

        pathSb.append("      description: delete an existing " + xmlRootElementName + "\n");

        pathSb.append("      operationId: delete" + useOpId + "\n");
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
        if (!path.endsWith("/relationship")) {
            pathSb.append("        - name: resource-version\n");

            pathSb.append("          in: query\n");
            pathSb.append("          description: resource-version for concurrency\n");
            pathSb.append("          required: true\n");
            pathSb.append("          type: string\n");
        }
        this.objectPathMapEntry();
        return pathSb.toString();
    }

    public String objectPathMapEntry() {
        if (!path.endsWith("/relationship")) {
            deletePaths.put(path, xmlRootElementName);
        }
        return (xmlRootElementName + ":" + path);
    }
}
