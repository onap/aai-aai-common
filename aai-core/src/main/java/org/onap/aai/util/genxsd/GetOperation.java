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
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.onap.aai.util.GenerateXsd;

public class GetOperation {
    static Map<String, Vector<String>> containers = new HashMap<String, Vector<String>>();

    public static void addContainerProps(String container, Vector<String> containerProps) {
        containers.put(container, containerProps);;
    }

    private String useOpId;
    private String xmlRootElementName;
    private String tag;
    private String path;
    private String pathParams;
    private String queryParams;

    public GetOperation(String useOpId, String xmlRootElementName, String tag, String path,
        String pathParams) {
        super();
        this.useOpId = useOpId;
        this.xmlRootElementName = xmlRootElementName;
        this.tag = tag;
        this.path = path;
        this.pathParams = pathParams;
        // StringBuilder p = new StringBuilder();

        if (containers.get(xmlRootElementName) == null) {
            this.queryParams = "";
        } else {
            this.queryParams = String.join("", containers.get(xmlRootElementName));
            // for(String param : containers.get(xmlRootElementName)) {
            // p.append(param);
            // }
            // this.queryParams = p.toString();
        }
    }

    @Override
    public String toString() {
        StringTokenizer st;
        st = new StringTokenizer(path, "/");
        // Path has to be longer than one element
        /*
         * if ( st.countTokens() <= 1) {
         * return "";
         * }
         */
        // a valid tag is necessary
        if (StringUtils.isEmpty(tag)) {
            return "";
        }
        if (path.endsWith("/relationship")) {
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
        StringBuffer pathSb = new StringBuffer();
        pathSb.append("  " + path + ":\n");
        pathSb.append("    get:\n");
        pathSb.append("      tags:\n");
        pathSb.append("        - " + tag + "\n");
        pathSb.append("      summary: returns " + xmlRootElementName + "\n");

        pathSb.append("      description: returns " + xmlRootElementName + "\n");
        pathSb.append("      operationId: get" + useOpId + "\n");
        pathSb.append("      produces:\n");
        pathSb.append("        - application/json\n");
        pathSb.append("        - application/xml\n");

        pathSb.append("      responses:\n");
        pathSb.append("        \"200\":\n");
        pathSb.append("          description: successful operation\n");
        pathSb.append("          schema:\n");
        pathSb.append("              $ref: \"#/getDefinitions/" + xmlRootElementName + "\"\n");
        pathSb.append("        \"default\":\n");
        pathSb.append("          " + GenerateXsd.getResponsesUrl());
        if (StringUtils.isNotEmpty(pathParams) || StringUtils.isNotEmpty(queryParams)) {
            pathSb.append("      parameters:\n");
        }
        if (StringUtils.isNotEmpty(pathParams)) {
            pathSb.append(pathParams);
        }
        // if ( StringUtils.isNotEmpty(pathParams) && StringUtils.isNotEmpty(queryParams)) {
        // pathSb.append("\n");
        // }
        if (StringUtils.isNotEmpty(queryParams)) {
            pathSb.append(queryParams);
        }
        return pathSb.toString();
    }
}
