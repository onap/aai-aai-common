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

package org.onap.aai.rest;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class RestHandlerService {
    private static RestHandlerService single_instance = null;
    public ThreadPoolExecutor executor;

    // private constructor restricted to this class itself
    private RestHandlerService() {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(50);
    }

    /**
     * Gets the single instance of RestHandlerService.
     *
     * @return single instance of RestHandlerService
     */
    public static RestHandlerService getInstance() {
        if (single_instance == null) {
            single_instance = new RestHandlerService();
        }
        return single_instance;
    }
}
