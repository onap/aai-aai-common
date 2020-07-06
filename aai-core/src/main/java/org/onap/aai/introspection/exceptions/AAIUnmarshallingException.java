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

package org.onap.aai.introspection.exceptions;

import org.onap.aai.exceptions.AAIException;

public class AAIUnmarshallingException extends AAIException {

    private static final long serialVersionUID = -5615651557821878103L;

    private static final String AAI_MSG="AAI_3000";
    public AAIUnmarshallingException() {
    }

    public AAIUnmarshallingException(String message) {
        super(AAI_MSG, message);
    }

    public AAIUnmarshallingException(Throwable cause) {
        super(AAI_MSG, cause);
    }

    public AAIUnmarshallingException(String message, Throwable cause) {
        super(AAI_MSG, cause, message);
    }
}
