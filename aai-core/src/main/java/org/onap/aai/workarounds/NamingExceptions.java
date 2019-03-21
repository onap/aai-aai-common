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

package org.onap.aai.workarounds;

public class NamingExceptions {

    /**
     * Instantiates a new naming exceptions.
     */
    private NamingExceptions() {

    }

    private static class Helper {
        private static final NamingExceptions INSTANCE = new NamingExceptions();
    }

    /**
     * Gets the single instance of NamingExceptions.
     *
     * @return single instance of NamingExceptions
     */
    public static NamingExceptions getInstance() {
        return Helper.INSTANCE;
    }

    /**
     * Gets the object name.
     *
     * @param name the name
     * @return the object name
     */
    public String getObjectName(String name) {

        String result = name;

        if (name.equals("cvlan-tag")) {
            result = "cvlan-tag-entry";
        }

        return result;
    }

    /**
     * Gets the DB name.
     *
     * @param name the name
     * @return the DB name
     */
    public String getDBName(String name) {

        String result = name;

        if (name.equals("cvlan-tag-entry")) {
            result = "cvlan-tag";
        }

        return result;

    }
}
