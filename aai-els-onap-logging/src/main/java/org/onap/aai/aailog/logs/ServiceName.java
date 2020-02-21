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
package org.onap.aai.aailog.logs;

public class ServiceName {
    /**
     * Extract the service name from a URI path
     * Service name should be the URI path up to two levels down from the version or less
     * @param path the URI path
     * @return the service name
     */
    public static String extractServiceName (String path) {
        StringBuilder sBuilder = new StringBuilder();
        String[] parts = path.split("/");
        String part = "";
        for (int i = 0; i < parts.length; i++) {
            part = parts[i];
            if (i < 5) {
                sBuilder.append(part).append("/");
            } else {
                break;
            }
        }
        if ((sBuilder.length() > 0) && (sBuilder.charAt(sBuilder.length()-1) == '/')) {
            sBuilder.deleteCharAt(sBuilder.length()-1);
        }
        String serviceName = sBuilder.toString();
        if (serviceName != null && (!serviceName.isEmpty())) {
            serviceName = serviceName.replaceAll(",", "\\\\,");
        }
        return serviceName;
    }
}
