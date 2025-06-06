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

package org.onap.aai.introspection.sideeffect.exceptions;

import java.io.Serial;

import org.onap.aai.exceptions.AAIException;

public class AAIMissingRequiredPropertyException extends AAIException {

    @Serial
    private static final long serialVersionUID = -8907079650472014019L;

    public AAIMissingRequiredPropertyException() {
    }

    public AAIMissingRequiredPropertyException(String message) {
        super("AAI_5107", message);
    }

    public AAIMissingRequiredPropertyException(Throwable cause) {
        super("AAI_5107", cause);
    }

    public AAIMissingRequiredPropertyException(String message, Throwable cause) {
        super("AAI_5107", cause, message);
    }

}
