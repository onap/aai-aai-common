/**
 * ﻿============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2019 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2019 Amdocs
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.schemaif;

import java.io.Serial;

public class SchemaProviderException extends Exception {

    @Serial
    private static final long serialVersionUID = 8162385108397238865L;

    public SchemaProviderException() {
    }

    public SchemaProviderException(String message) {
        super(message);
    }

    public SchemaProviderException(Throwable cause) {
        super(cause);
    }

    public SchemaProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public SchemaProviderException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
