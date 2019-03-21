/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright © 2018 IBM.
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

package org.onap.aai.auth;

import java.util.*;

public class AAIUser {

    private String username;

    private boolean isWildcard = false;
    private Set<String> roles;
    private Map<String, Set<String>> aaiFunctionToHttpMethod;

    public AAIUser(String username) {
        this(username, false);
    }

    public AAIUser(String username, boolean isWildcard) {
        this.username = username;
        this.roles = new HashSet<>();
        this.aaiFunctionToHttpMethod = new HashMap<>();
        this.isWildcard = isWildcard;
    }

    public boolean isWildcard() {
        return isWildcard;
    }

    public String getUsername() {
        return username;
    }

    public void addRole(String role) {
        this.roles.add(role);
    }

    public boolean hasRole(String role) {
        return this.roles.contains(role);
    }

    public void setUserAccess(String aaiMethod, String... httpMethods) {
        for (String httpMethod : httpMethods) {
            this.addUserAccess(aaiMethod, httpMethod);
        }
    }

    private void addUserAccess(String aaiMethod, String httpMethod) {
        Set<String> httpMethods = new HashSet<>();
        if (this.aaiFunctionToHttpMethod.containsKey(aaiMethod)) {
            httpMethods = this.aaiFunctionToHttpMethod.get(aaiMethod);
        }
        httpMethods.add(httpMethod);
        this.aaiFunctionToHttpMethod.put(aaiMethod, httpMethods);
    }

    public boolean hasAccess(String aaiMethod, String httpMethod) {
        return this.aaiFunctionToHttpMethod.getOrDefault(aaiMethod, Collections.emptySet()).contains(httpMethod);
    }

}
