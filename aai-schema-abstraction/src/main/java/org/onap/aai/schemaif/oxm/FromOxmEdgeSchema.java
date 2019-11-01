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
package org.onap.aai.schemaif.oxm;

import java.util.HashMap;

import org.onap.aai.edges.EdgeRule;
import org.onap.aai.edges.enums.EdgeProperty;
import org.onap.aai.schemaif.SchemaProviderException;
import org.onap.aai.schemaif.definitions.EdgeSchema;
import org.onap.aai.schemaif.definitions.PropertySchema;
import org.onap.aai.schemaif.definitions.types.DataType;


public class FromOxmEdgeSchema extends EdgeSchema {

    public void fromEdgeRule(EdgeRule edgeRule) throws SchemaProviderException {
        name = edgeRule.getLabel();
        source = edgeRule.getFrom();
        target = edgeRule.getTo();
        
        switch (edgeRule.getMultiplicityRule()) {
            case MANY2MANY:
                multiplicity = Multiplicity.MANY_2_MANY;
                break;
            case MANY2ONE:
                multiplicity = Multiplicity.MANY_2_ONE;
                break;
            case ONE2MANY:
                multiplicity = Multiplicity.ONE_2_MANY;
                break;
            case ONE2ONE:
                multiplicity = Multiplicity.ONE_2_ONE;
                break;
        }

        annotations = new HashMap<String,String>();
        properties = new HashMap<String,PropertySchema>();
        
        // TODO:  For now these are hard-coded ... should read them from a config file or something
        annotations.put(EdgeProperty.CONTAINS.toString().toLowerCase(), edgeRule.getContains());
        annotations.put(EdgeProperty.DELETE_OTHER_V.toString().toLowerCase(), edgeRule.getDeleteOtherV());
        annotations.put(EdgeProperty.PREVENT_DELETE.toString().toLowerCase(), edgeRule.getPreventDelete());
        
        FromOxmPropertySchema pSchema = new FromOxmPropertySchema();
        pSchema.fromRelationship(EdgeProperty.CONTAINS.toString(), DataType.Type.STRING);
        properties.put(EdgeProperty.CONTAINS.toString().toLowerCase(), pSchema);
        
        pSchema = new FromOxmPropertySchema();
        pSchema.fromRelationship(EdgeProperty.DELETE_OTHER_V.toString(), DataType.Type.STRING);
        properties.put(EdgeProperty.DELETE_OTHER_V.toString().toLowerCase(), pSchema);
        
        pSchema = new FromOxmPropertySchema();
        pSchema.fromRelationship(EdgeProperty.PREVENT_DELETE.toString(), DataType.Type.STRING);
        properties.put(EdgeProperty.PREVENT_DELETE.toString().toLowerCase(), pSchema);
    }
}
