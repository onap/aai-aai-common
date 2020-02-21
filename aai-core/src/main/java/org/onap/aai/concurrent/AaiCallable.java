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

package org.onap.aai.concurrent;

import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.MDC;

/**
 * The Class AaiCallable ensures that the Callable gets a copy of the MDC, so that any logging related fields are
 * preserved
 */
public abstract class AaiCallable<T> implements Callable<T> {
    private Map<String, String> mdcCopy;

    /**
     * The constructor.
     */
    @SuppressWarnings("unchecked")
    public AaiCallable() {
        mdcCopy = MDC.getCopyOfContextMap();
    }

    /**
     * The call method
     */
    public T call() throws Exception {
        if ( mdcCopy != null ) {
            MDC.setContextMap(mdcCopy);
        }
        return process();
    }

    /**
     * The process method
     */
    public abstract T process() throws Exception;
}
