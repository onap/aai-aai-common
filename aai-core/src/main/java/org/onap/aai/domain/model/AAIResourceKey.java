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

package org.onap.aai.domain.model;

public class AAIResourceKey {
    private String keyName;
    private String keyType;
    private String pathParamName;
    private String dnCamKeyName;

    /**
     * Gets the key name.
     *
     * @return the key name
     */
    public String getKeyName() {
        return keyName;
    }

    /**
     * Sets the key name.
     *
     * @param keyName the new key name
     */
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    /**
     * Gets the key type.
     *
     * @return the key type
     */
    public String getKeyType() {
        return keyType;
    }

    /**
     * Sets the key type.
     *
     * @param t the new key type
     */
    public void setKeyType(String t) {
        this.keyType = t;
    }

    /**
     * Gets the path param name.
     *
     * @return the path param name
     */
    public String getPathParamName() {
        return pathParamName;
    }

    /**
     * Sets the path param name.
     *
     * @param pathParamName the new path param name
     */
    public void setPathParamName(String pathParamName) {
        this.pathParamName = pathParamName;
    }

    /**
     * Gets the dn cam key name.
     *
     * @return the dn cam key name
     */
    public String getDnCamKeyName() {
        return dnCamKeyName;
    }

    /**
     * Sets the dn cam key name.
     *
     * @param dnCamKeyName the new dn cam key name
     */
    public void setDnCamKeyName(String dnCamKeyName) {
        this.dnCamKeyName = dnCamKeyName;
    }

}
