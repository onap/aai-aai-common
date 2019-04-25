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
package org.onap.aai.schemaif.json;

import java.util.HashMap;
import java.util.Map;

import org.onap.aai.schemaif.SchemaProviderException;
import org.onap.aai.schemaif.definitions.EdgeSchema;
import org.onap.aai.schemaif.definitions.PropertySchema;
import org.onap.aai.schemaif.json.definitions.JsonEdgeSchema;

public class FromJsonEdgeSchema extends EdgeSchema {
    public void fromJson(JsonEdgeSchema jsonEdge) throws SchemaProviderException {

        name = jsonEdge.getLabel();
        source = jsonEdge.getFrom();
        target = jsonEdge.getTo();
        
        // TODO:  At present, multiplicity isn't described in the JSON schema.  By default, make everything
        // many-to-many
        multiplicity = Multiplicity.MANY_2_MANY;
  
        // Populate annotation schema
        annotations = new HashMap<String,String>();
        if (jsonEdge.getAnnotations() != null) {
            for (Map.Entry<String,String> entry : jsonEdge.getAnnotations().entrySet()) {
                annotations.put(entry.getKey(), entry.getValue());
            }
        }
        
        // Currently edge properties are not supported in the json schema
        properties = new HashMap<String,PropertySchema>();
    }

}
