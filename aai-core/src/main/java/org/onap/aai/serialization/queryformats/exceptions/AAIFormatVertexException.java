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

package org.onap.aai.serialization.queryformats.exceptions;

import java.io.Serial;

public class AAIFormatVertexException extends Exception {

    @Serial
    private static final long serialVersionUID = -5814240841844624097L;

    public AAIFormatVertexException() {
    }

    public AAIFormatVertexException(String message) {
        super(message);
    }

    public AAIFormatVertexException(Throwable cause) {
        super(cause);
    }

    public AAIFormatVertexException(String message, Throwable cause) {
        super(message, cause);
    }
}
