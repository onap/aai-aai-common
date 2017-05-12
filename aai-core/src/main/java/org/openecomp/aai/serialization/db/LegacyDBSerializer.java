/*-
 * ============LICENSE_START=======================================================
 * org.openecomp.aai
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.aai.serialization.db;

import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.ModelType;
import org.openecomp.aai.introspection.Version;
import org.openecomp.aai.serialization.engines.TransactionalGraphEngine;
import org.apache.tinkerpop.gremlin.structure.Graph;

public class LegacyDBSerializer extends DBSerializer {

    public LegacyDBSerializer(Version version, TransactionalGraphEngine engine, ModelType introspectionType, String sourceOfTruth) throws AAIException {
        super(version, engine, introspectionType, sourceOfTruth);
    }

}
