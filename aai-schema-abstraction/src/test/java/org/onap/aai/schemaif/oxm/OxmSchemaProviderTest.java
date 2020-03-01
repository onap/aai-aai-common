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


import static org.junit.Assert.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.aai.schemaif.SchemaProviderException;
import org.onap.aai.schemaif.definitions.EdgeSchema;
import org.onap.aai.schemaif.definitions.PropertySchema;
import org.onap.aai.schemaif.definitions.VertexSchema;
import org.onap.aai.schemaif.definitions.types.DataType.Type;

@RunWith(MockitoJUnitRunner.class)
public class OxmSchemaProviderTest extends OxmSchemaServiceSetup {

    @Test
    public void testLoadingSchema() throws SchemaProviderException {
        try {
            OxmSchemaProvider schemaProvider = new OxmSchemaProvider();
            schemaProvider.loadSchema();

            VertexSchema vertSchema = schemaProvider.getVertexSchema("pserver", schemaProvider.getLatestSchemaVersion());
            System.out.println(vertSchema.toString());

            EdgeSchema edgeSchema = schemaProvider.getEdgeSchema("org.onap.relationships.inventory.LocatedIn",
                    "cloud-region", "zone", schemaProvider.getLatestSchemaVersion());
            System.out.println(edgeSchema.toString());

            // Validate vertex schema
            assertTrue(vertSchema.getName().equals("pserver"));
            assertTrue(vertSchema.getAnnotationValue("nameProps").equals("pserver-name2"));
            assertTrue(vertSchema.getAnnotationValue("dependentOn") == null);

            PropertySchema propSchema = vertSchema.getPropertySchema("hostname");
            assertTrue(propSchema.getName().equals("hostname"));
            assertTrue(propSchema.getDefaultValue().equals(""));
            assertTrue(propSchema.isRequired());
            assertTrue(!propSchema.isKey());
            assertTrue(propSchema.getDataType().getType().compareTo(Type.STRING) == 0);
            Object obj = propSchema.validateValue("somestring");
            assertTrue(obj instanceof String);

            propSchema = vertSchema.getPropertySchema("in-maint");
            assertTrue(propSchema.getName().equals("in-maint"));
            assertTrue(propSchema.getDefaultValue().equals("false"));
            assertTrue(!propSchema.isRequired());
            assertTrue(!propSchema.isKey());
            assertTrue(!propSchema.isReserved());
            assertTrue(propSchema.getDataType().getType().compareTo(Type.BOOL) == 0);
            obj = propSchema.validateValue("True");
            assertTrue(obj instanceof Boolean);
            obj = propSchema.validateValue("false");
            assertTrue(obj instanceof Boolean);
            assertTrue(propSchema.getDataType().validateValue("badValue") == null);


            propSchema = vertSchema.getPropertySchema("aai-node-type");
            assertTrue(propSchema.getName().equals("aai-node-type"));
            assertTrue(propSchema.getDefaultValue().equals(""));
            assertTrue(!propSchema.isRequired());
            assertTrue(!propSchema.isKey());
            assertTrue(propSchema.isReserved());
            assertTrue(propSchema.getDataType().getType().compareTo(Type.STRING) == 0);

            propSchema = vertSchema.getPropertySchema("pserver-id");
            assertTrue(propSchema.getName().equals("pserver-id"));
            assertTrue(propSchema.getDefaultValue().equals(""));
            assertTrue(propSchema.isRequired());
            assertTrue(!propSchema.isReserved());
            assertTrue(propSchema.isKey());
            assertTrue(propSchema.getDataType().getType().compareTo(Type.STRING) == 0);

            propSchema = vertSchema.getPropertySchema("number-of-cpus");
            assertTrue(propSchema.getName().equals("number-of-cpus"));
            assertTrue(propSchema.getAnnotationValue("source-of-truth-type").equals("openstack"));
            assertTrue(propSchema.getDataType().getType().compareTo(Type.INT) == 0);
            obj = propSchema.validateValue("35");
            assertTrue(obj instanceof Integer);
            obj = propSchema.validateValue("5.0");
            assertTrue(obj instanceof Integer);
            assertTrue(propSchema.getDataType().validateValue("5.5") == null);
            assertTrue(propSchema.getDataType().validateValue("xyz") == null);

            // Validate edge schema
            assertTrue(edgeSchema.getName().equals("org.onap.relationships.inventory.LocatedIn"));
            assertTrue(edgeSchema.getSource().equals("cloud-region"));
            assertTrue(edgeSchema.getTarget().equals("zone"));
            assertTrue(edgeSchema.getMultiplicity().equals(EdgeSchema.Multiplicity.MANY_2_ONE));
            assertTrue(edgeSchema.getAnnotationValue("contains-other-v").equals("NONE"));
            assertTrue(edgeSchema.getPropertySchema("prevent-delete").getDataType().getType().equals(Type.STRING));

            // Validate 'dependentOn' annotation
            vertSchema = schemaProvider.getVertexSchema("tenant", schemaProvider.getLatestSchemaVersion());
            assertTrue(vertSchema.getAnnotationValue("dependentOn").equals("cloud-region"));
        }
        catch (Exception ex) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            System.out.println(writer.toString());
            assertTrue(false);
        }
    }

    @Test
    public void testAdjacentEdges() throws SchemaProviderException {
        try {
            OxmSchemaProvider schemaProvider = new OxmSchemaProvider();
            schemaProvider.loadSchema();

            Set<EdgeSchema> edgeSchemaList =
                    schemaProvider.getAdjacentEdgeSchema("snapshot", schemaProvider.getLatestSchemaVersion());

            // Validate edge schema
            assertTrue(edgeSchemaList.size() == 2);

            for (EdgeSchema es : edgeSchemaList) {
                System.out.println(es.toString());
                if (es.getName().equals("org.onap.relationships.inventory.BelongsTo")) {
                    assertTrue(es.getSource().equals("snapshot"));
                    assertTrue(es.getTarget().equals("cloud-region"));
                    assertTrue(es.getMultiplicity().equals(EdgeSchema.Multiplicity.MANY_2_ONE));
                }
                else if (es.getName().equals("org.onap.relationships.inventory.Uses")) {
                    assertTrue(es.getSource().equals("vserver"));
                    assertTrue(es.getTarget().equals("snapshot"));
                    assertTrue(es.getMultiplicity().equals(EdgeSchema.Multiplicity.ONE_2_ONE));
                }
                else {
                    assertTrue(false);
                }
            }
        }
        catch (Exception ex) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            System.out.println(writer.toString());
            assertTrue(false);
        }
    }

    @Test
    public void testValidSourceTargetEdge() throws SchemaProviderException {
        try {
            OxmSchemaProvider schemaProvider = new OxmSchemaProvider();
            schemaProvider.loadSchema();

            Set<EdgeSchema> edgeSchemaList =
                    schemaProvider.getEdgeSchemaForSourceTarget("service-instance", "customer",
                            schemaProvider.getLatestSchemaVersion());

            // Validate edge schema
            assertTrue(edgeSchemaList.size() == 1);
            assertTrue(edgeSchemaList.iterator().next().getName().equals("ncso.related-to"));
            assertTrue(edgeSchemaList.iterator().next().getSource().equals("service-instance"));
            assertTrue(edgeSchemaList.iterator().next().getTarget().equals("customer"));
            assertTrue(edgeSchemaList.iterator().next().getMultiplicity().equals(EdgeSchema.Multiplicity.MANY_2_MANY));

            edgeSchemaList =
                    schemaProvider.getEdgeSchemaForSourceTarget("cloud-region", "complex",
                            schemaProvider.getLatestSchemaVersion());

            // Validate edge schema
            assertTrue(edgeSchemaList.size() == 2);

            for (EdgeSchema es : edgeSchemaList) {
                System.out.println(es.toString());
                if (es.getName().equals("org.onap.relationships.inventory.FoundIn")) {
                    assertTrue(es.getSource().equals("cloud-region"));
                    assertTrue(es.getTarget().equals("complex"));
                    assertTrue(es.getMultiplicity().equals(EdgeSchema.Multiplicity.MANY_2_MANY));
                }
                else if (es.getName().equals("org.onap.relationships.inventory.LocatedIn")) {
                    assertTrue(es.getSource().equals("cloud-region"));
                    assertTrue(es.getTarget().equals("complex"));
                    assertTrue(es.getMultiplicity().equals(EdgeSchema.Multiplicity.MANY_2_ONE));
                }
                else {
                    assertTrue(false);
                }
            }

            edgeSchemaList =
                    schemaProvider.getEdgeSchemaForSourceTarget("cloud-region", "bad-node",
                            schemaProvider.getLatestSchemaVersion());

            // Validate edge schema
            assertTrue(edgeSchemaList.size() == 0);
        }
        catch (Exception ex) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            System.out.println(writer.toString());
            assertTrue(false);
        }
    }

    @Test
    public void testInvalidVertexOrEdge() throws SchemaProviderException {
        try {
            OxmSchemaProvider schemaProvider = new OxmSchemaProvider();
            schemaProvider.loadSchema();

            VertexSchema vertSchema =
                    schemaProvider.getVertexSchema("bad-node", schemaProvider.getLatestSchemaVersion());
            assertTrue(vertSchema == null);

            EdgeSchema edgeSchema = schemaProvider.getEdgeSchema("org.onap.relationships.inventory.LocatedIn",
                    "cloud-region", "bad-node", schemaProvider.getLatestSchemaVersion());
            assertTrue(edgeSchema == null);

            Set<EdgeSchema> edgeSchemaList =
                    schemaProvider.getAdjacentEdgeSchema("org.onap.nodes.bad-node",
                            schemaProvider.getLatestSchemaVersion());
            assertTrue(edgeSchemaList.isEmpty());
        }
        catch (Exception ex) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            System.out.println(writer.toString());
            assertTrue(false);
        }
    }
}
