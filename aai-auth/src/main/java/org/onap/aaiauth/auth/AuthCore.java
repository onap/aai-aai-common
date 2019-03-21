/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017 Amdocs
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

package org.onap.aaiauth.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.onap.aaiauth.util.AuthConstants;

public class AuthCore {

    private String authFilename;
    public ObjectMapper mapper;

    private enum HTTP_METHODS {
        POST, GET, PUT, DELETE, PATCH
    }

    public AuthCore(String filename) throws Exception {
        this.authFilename = filename;
        loadUsers(filename);
    }

    private static boolean usersInitialized = false;
    private static HashMap<String, AuthUser> users;

    public String getConfigFile() {
        return this.authFilename;
    }

    /**
     * Loads the auth file and caches a list of authorized users.
     * 
     * @param authFilename
     *        - Absolute path of the file where authorized users are listed.
     */
    public synchronized void loadUsers(String authFilename) throws Exception {
        users = new HashMap<>();

        mapper = new ObjectMapper(); // can reuse, share globally
        JsonNode rootNode = mapper.readTree(new File(authFilename));
        JsonNode rolesNode = rootNode.path(AuthConstants.ROLES_NODE_PATH);

        for (JsonNode roleNode : rolesNode) {
            String roleName = roleNode.path(AuthConstants.ROLE_NAME_PATH).asText();

            AuthRole role = new AuthRole();
            JsonNode usersNode = roleNode.path(AuthConstants.USERS_NODE_PATH);
            JsonNode functionsNode = roleNode.path(AuthConstants.FUNCTIONS_NODE_PATH);
            for (JsonNode functionNode : functionsNode) {
                String function = functionNode.path(AuthConstants.FUNCTION_NAME_PATH).asText();
                JsonNode methodsNode = functionNode.path(AuthConstants.METHODS_NODE_PATH);
                boolean hasMethods = handleMethodNode(methodsNode, role, function);

                if (!hasMethods) {
                    // iterate the list from HTTP_METHODS
                    for (HTTP_METHODS meth : HTTP_METHODS.values()) {
                        String thisFunction = meth.toString() + ":" + function;

                        role.addAllowedFunction(thisFunction);
                    }
                }
            }

            handleUserNode(usersNode, roleName, role);
        }

        usersInitialized = true;
    }

    private boolean handleMethodNode(JsonNode methodsNode, AuthRole role, String function) {
        boolean hasMethods = false;
        for (JsonNode methodNode : methodsNode) {
            String methodName = methodNode.path(AuthConstants.METHOD_NAME_PATH).asText();
            hasMethods = true;
            String thisFunction = methodName + ":" + function;

            role.addAllowedFunction(thisFunction);
        }
        return hasMethods;
    }

    private void handleUserNode(JsonNode usersNode, String roleName, AuthRole role) {
        for (JsonNode userNode : usersNode) {
            // make the user lower case
            String node = userNode.path(AuthConstants.USER_NODE_PATH).asText().toLowerCase();
            AuthUser user;
            if (users.containsKey(node)) {
                user = users.get(node);
            } else {
                user = new AuthUser();
            }

            user.setUser(node);
            user.addRole(roleName, role);
            users.put(node, user);
        }
    }

    public class AuthUser {

        public AuthUser() {
            this.roles = new HashMap<>();
        }

        private String username;
        private HashMap<String, AuthRole> roles;

        public String getUser() {
            return this.username;
        }

        public Map<String, AuthRole> getRoles() {
            return this.roles;
        }

        public void addRole(String roleName, AuthRole role) {
            this.roles.put(roleName, role);
        }

        /**
         * Returns true if the user has permissions for the function, otherwise returns false.
         * 
         * @param checkFunc
         *        - String value of the function.
         */
        public boolean checkAllowed(String checkFunc) {
            for (Map.Entry<String, AuthRole> roleEntry : this.roles.entrySet()) {
                AuthRole role = roleEntry.getValue();
                if (role.hasAllowedFunction(checkFunc)) {
                    // break out as soon as we find it
                    return true;
                }
            }
            // we would have got positive confirmation had it been there
            return false;
        }

        public void setUser(String myuser) {
            this.username = myuser;
        }
    }

    public static class AuthRole {

        public AuthRole() {
            this.allowedFunctions = new ArrayList<>();
        }

        private List<String> allowedFunctions;

        public void addAllowedFunction(String func) {
            this.allowedFunctions.add(func);
        }

        /**
         * Remove the function from the user's list of allowed functions.
         * 
         * @param delFunc
         *        - String value of the function.
         */
        public void delAllowedFunction(String delFunc) {
            if (this.allowedFunctions.contains(delFunc)) {
                this.allowedFunctions.remove(delFunc);
            }
        }

        /**
         * Returns true if the user has permissions to use the function, otherwise returns false.
         * 
         * @param afunc
         *        - String value of the function.
         */
        public boolean hasAllowedFunction(String afunc) {
            return this.allowedFunctions.contains(afunc);
        }
    }

    /**
     * Returns a hash-map of all users which have been loaded and initialized.
     */
    public Map<String, AuthUser> getUsers(String key) throws Exception {
        if (!usersInitialized || (users == null)) {
            loadUsers(this.authFilename);
        }
        return users;
    }

    /**
     * Returns true if the user is allowed to access a function.
     * 
     * @param username
     *        - String value of user
     * @param authFunction
     *        - String value of the function.
     */
    public boolean authorize(String username, String authFunction) {
        AuthUser user = users.get(username);
        return user != null && user.checkAllowed(authFunction);
    }
}
