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
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 *
 * ECOMP and OpenECOMP are trademarks
 * and service marks of AT&T Intellectual Property.
 */
package org.onap.aai.schemaif;

import com.att.eelf.i18n.EELFResourceManager;
import org.onap.aai.cl.eelf.LogMessageEnum;

public enum SchemaProviderMsgs implements LogMessageEnum {
    SCHEMA_LOAD_ERROR,
    LOADED_SCHEMA_FILE,
    LOADED_DB_RULE_FILE;
    
    static {
        EELFResourceManager.loadMessageBundle("logging/SchemaProviderMsgs");
    }
}
