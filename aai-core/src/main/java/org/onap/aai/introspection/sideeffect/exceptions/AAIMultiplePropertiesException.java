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

package org.onap.aai.introspection.sideeffect.exceptions;

import org.onap.aai.exceptions.AAIException;

public class AAIMultiplePropertiesException extends AAIException {

    private static final long serialVersionUID = 2098371383166008345L;

    public AAIMultiplePropertiesException() {
    }

    public AAIMultiplePropertiesException(String message) {
        super("AAI_6136", message);
    }

    public AAIMultiplePropertiesException(Throwable cause) {
        super("AAI_6136", cause);
    }

    public AAIMultiplePropertiesException(String message, Throwable cause) {
        super("AAI_6136", cause, message);
    }

}
