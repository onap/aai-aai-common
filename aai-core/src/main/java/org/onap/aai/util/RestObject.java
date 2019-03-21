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

public class RestObject<T> {

    /**
     * Generic version of the RestObject class.
     * 
     * @param <T> the type of the value being called for the Rest object interface
     */
    // T stands for "Type"
    private T t;

    /**
     * Sets the.
     *
     * @param t the t
     */
    public void set(T t) {
        this.t = t;
    }

    /**
     * Gets the.
     *
     * @return the t
     */
    public T get() {
        return t;
    }

}
