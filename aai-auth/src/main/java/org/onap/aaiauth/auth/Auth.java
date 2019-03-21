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

import org.apache.http.cookie.Cookie;

public class Auth {

    private AuthCore authCore;

    public Auth(String filename) throws Exception {
        this.authCore = new AuthCore(filename);
    }

    public boolean authBasic(String username, String authFunction) throws Exception {
        return authCore.authorize(username, authFunction);
    }

    public boolean authCookie(Cookie cookie, String authFunction, StringBuilder username) throws Exception {
        return cookie != null && authCore.authorize(username.toString(), authFunction);
    }

    /**
     * Returns true if the user is allowed to access a function.
     * 
     * @param authUser
     *        - String value of the user.
     * @param authAction
     *        - String value of the function.
     */
    public boolean validateRequest(String authUser, String authAction) throws Exception {
        return authUser != null && authAction != null && authCore.authorize(authUser, authAction);
    }
}
