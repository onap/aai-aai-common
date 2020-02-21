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
package org.onap.aai.util;

/**
 * <b>AAIConfigProxy</b> is a interface trying to proxy calls
 * to the AAIConfig class in which there are only static methods
 * The main reason for this interface existence is to simplify
 * unit testing edge cases in which it is harder to simulate
 *
 * Note: If there is a better way to do this,
 * it is encouraged to change this early on
 */
// TODO - Find an better name for this interface name
public interface AAIConfigProxy {

    default String get(String key, String defaultValue){
        return AAIConfig.get(key, defaultValue);
    }

    default int getInt(String key, String defaultValue){
        return AAIConfig.getInt(key, defaultValue);
    }
}
