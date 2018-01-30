/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai.config;

import java.util.Optional;

/**
 * <b>HttpPing</b> interface provides access to update the endpoint and
 * and the security level of the server that the user is trying to access
 */
public interface HttpPing {

    /**
     * Sets the endpoint that the http get request will
     * make to verify if the url can be reached
     *
     * @param endpoint - the endpoint of the url that is used to do healthcheck
     */
    void setHealthCheckEndpoint(String endpoint);

    /**
     * Returns the health check endpoint that the implementation
     * will use in order to verify if the server is reachable at that location
     *
     * @return endpoint - the endpoint of the url that is used to do healthcheck
     */
    String getHealthCheckEndpoint();

    /**
     * Set the credentials for the rest endpoint to verify authorization
     *
     * @param username - the username to the server trying to connect to
     * @param password - the password to the server trying to connect to
     */
    void setCredentials(String username, String password);

    /**
     * Return the base64 authorization string set from the username and password
     *
     * @return encoded string using base64 of the username and password values
     * like this:
     * <pre>
     * @{code
     *      "username:password" => "Basic dXNlcm5hbWU6cGFzc3dvcmQ="
     * }
     * </pre>
     */
    Optional<String> getAuthorization();
}
