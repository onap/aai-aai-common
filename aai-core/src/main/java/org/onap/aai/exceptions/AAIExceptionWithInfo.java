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

package org.onap.aai.exceptions;

import java.util.HashMap;

public class AAIExceptionWithInfo extends AAIException {

    HashMap<String, Object> infoHash;
    String info;

    /**
     * Instantiates a new AAI exception with info.
     *
     * @param infoHash the info hash
     * @param info the info
     */
    public AAIExceptionWithInfo(HashMap<String, Object> infoHash, String info) {
        super();
        setInfoHash(infoHash);
        setInfo(info);
    }

    /**
     * Instantiates a new AAI exception with info.
     *
     * @param code the code
     * @param infoHash the info hash
     * @param info the info
     */
    public AAIExceptionWithInfo(String code, HashMap<String, Object> infoHash, String info) {
        super(code);
        setInfoHash(infoHash);
        setInfo(info);
    }

    /**
     * Instantiates a new AAI exception with info.
     *
     * @param code the code
     * @param details the details
     * @param infoHash the info hash
     * @param info the info
     */
    public AAIExceptionWithInfo(String code, String details, HashMap<String, Object> infoHash,
        String info) {
        super(code, details);
        setInfoHash(infoHash);
        setInfo(info);
    }

    /**
     * Instantiates a new AAI exception with info.
     *
     * @param code the code
     * @param cause the cause
     * @param infoHash the info hash
     * @param info the info
     */
    public AAIExceptionWithInfo(String code, Throwable cause, HashMap<String, Object> infoHash,
        String info) {
        super(code, cause);
        setInfoHash(infoHash);
        setInfo(info);
    }

    /**
     * Instantiates a new AAI exception with info.
     *
     * @param code the code
     * @param cause the cause
     * @param details the details
     * @param infoHash the info hash
     * @param info the info
     */
    public AAIExceptionWithInfo(String code, Throwable cause, String details,
        HashMap<String, Object> infoHash, String info) {
        super(code, cause, details);
        setInfoHash(infoHash);
        setInfo(info);
    }

    /**
     * Gets the info hash.
     *
     * @return the info hash
     */
    public HashMap<String, Object> getInfoHash() {
        return infoHash;
    }

    /**
     * Sets the info hash.
     *
     * @param infoHash the info hash
     */
    public void setInfoHash(HashMap<String, Object> infoHash) {
        this.infoHash = infoHash;
    }

    /**
     * Gets the info.
     *
     * @return the info
     */
    public String getInfo() {
        return info;
    }

    /**
     * Sets the info.
     *
     * @param info the new info
     */
    public void setInfo(String info) {
        this.info = info;
    }

}
