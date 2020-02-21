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
package org.onap.aai.aaf.filters;

import org.springframework.core.Ordered;

public enum FilterPriority {

    AAF_AUTHENTICATION(Ordered.HIGHEST_PRECEDENCE),
    AAF_AUTHORIZATION(Ordered.HIGHEST_PRECEDENCE + 1), //higher number = lower priority
    AAF_CERT_AUTHENTICATION(Ordered.HIGHEST_PRECEDENCE + 2 ),
    AAF_CERT_AUTHORIZATION(Ordered.HIGHEST_PRECEDENCE + 3),
    TWO_WAY_SSL_AUTH(Ordered.HIGHEST_PRECEDENCE + 4);

    private final int priority;

    FilterPriority(final int p) {
        priority = p;
    }

    public int getPriority() { return priority; }
}
