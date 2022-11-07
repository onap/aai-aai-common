/**
 * ============LICENSE_START=======================================================
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.schemaif.SchemaProviderException;
import org.onap.aai.schemaif.definitions.EdgeSchema;
import org.onap.aai.schemaif.definitions.PropertySchema;
import org.onap.aai.schemaif.definitions.VertexSchema;
import org.onap.aai.schemaif.definitions.types.ComplexDataType;
import org.onap.aai.schemaif.definitions.types.DataType.Type;
import org.onap.aai.schemaif.definitions.types.ListDataType;
import org.onap.aai.schemaif.definitions.types.MapDataType;
import org.onap.aai.schemaif.json.definitions.DataTypeDefinition;
import org.onap.aai.schemaif.json.definitions.JsonEdgeSchema;
import org.onap.aai.schemaif.json.definitions.JsonPropertySchema;
import org.onap.aai.schemaif.json.definitions.JsonSchema;
import org.onap.aai.schemaif.json.definitions.JsonVertexSchema;

public class JsonSchemaProviderTest {

    JsonSchemaProviderConfig config = new JsonSchemaProviderConfig();

    @Before
    public void init() {
        config.setSchemaServiceBaseUrl("https://testurl.com:8443");
        config.setSchemaServiceCertFile("/c/certfile");
        config.setSchemaServiceCertPwd("my-password");
        config.setServiceName("test-service");
    }

    @Test
    public void testReadZipJson() throws IOException, SchemaProviderException {
        JsonSchemaProvider jsonSchemaProvider = new JsonSchemaProvider(config);

        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(fos);
        zos.putNextEntry(new ZipEntry("schemaServiceResponse.json"));
        byte[] fileData = Files.readAllBytes(Paths.get("src/test/resources/json/schemaServiceResponse.json"));
        zos.write(fileData, 0, fileData.length);
        zos.closeEntry();
        zos.close();

        String testSchema = readFile("src/test/resources/json/schemaServiceResponse.json");
        SchemaServiceResponse resp = SchemaServiceResponse.fromJson(testSchema);

        String result = jsonSchemaProvider.unzipAndGetJSONString(fos.toByteArray());
        SchemaServiceResponse resp2 = SchemaServiceResponse.fromJson(result);
        assertEquals(resp.toJson(), resp2.toJson());
    }

    @Test
    public void testJsonSchemaLoad() {
        try {
            String testSchema = readFile("src/test/resources/json/jsonSchema.json");
            JsonSchema jsonSchema = JsonSchema.fromJson(testSchema);

            // Test Edge Schema
            JsonEdgeSchema edgeSchema = null;
            for (JsonEdgeSchema edge : jsonSchema.getRelationshipTypes()) {
                if ((edge.getFrom().equals("onap.nodes.sdwan.uCPE"))
                        && (edge.getTo().equals("onap.nodes.sdwan.service.SubscriberService"))) {
                    edgeSchema = edge;
                    break;
                }
            }

            assertNotNull(edgeSchema);
            assertEquals("onap.relationships.sdwan.BelongsTo", edgeSchema.getLabel());

            // Test Node Schema
            JsonVertexSchema vertexSchema = null;
            for (JsonVertexSchema v : jsonSchema.getNodeTypes()) {
                if ((v.getName().equals("org.onap.resource.NetworkRules"))) {
                    vertexSchema = v;
                    break;
                }
            }

            assertNotNull(vertexSchema);
            assertEquals(2, vertexSchema.getProperties().size());

            JsonPropertySchema propSchema = null;
            for (JsonPropertySchema p : vertexSchema.getProperties()) {
                if ((p.getName().equals("network_policy_entries"))) {
                    propSchema = p;
                    break;
                }
            }

            assertNotNull(propSchema);
            assertEquals(false, propSchema.getRequired());
            assertEquals(false, propSchema.getUnique());
            assertEquals("org.onap.datatypes.RuleList", propSchema.getDataType());
            assertEquals("", propSchema.getDefaultValue());
            assertEquals(4, propSchema.getAnnotations().size());

            // Test DataType Schema
            DataTypeDefinition dataType = null;
            for (DataTypeDefinition d : jsonSchema.getDataTypes()) {
                if ((d.getName().equals("org.onap.datatypes.network.VlanRequirements"))) {
                    dataType = d;
                    break;
                }
            }

            assertNotNull(dataType);
            assertEquals("org.onap.datatypes.network.VlanRequirements", dataType.getName());
            assertEquals(4, dataType.getProperties().size());

            propSchema = null;
            for (JsonPropertySchema p : dataType.getProperties()) {
                if ((p.getName().equals("vlan_type"))) {
                    propSchema = p;
                    break;
                }
            }

            assertNotNull(propSchema);
            assertEquals(false, propSchema.getRequired());
            assertEquals("string", propSchema.getDataType());
            assertEquals("", propSchema.getDefaultValue());
        } catch (Exception ex) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            System.out.println(writer);
            fail();
        }
    }

    @Test
    public void testJsonSchemaTranslateVertex() {
        try {
            String testSchema = readFile("src/test/resources/json/jsonSchema.json");
            JsonSchemaProvider schemaProvider = new JsonSchemaProvider(config);
            schemaProvider.loadSchema(testSchema, schemaProvider.getLatestSchemaVersion());

            VertexSchema vertSchema = schemaProvider.getVertexSchema("tosca.nodes.objectstorage",
                    schemaProvider.getLatestSchemaVersion());
            System.out.println(vertSchema.toString());

            // Validate vertex schema
            assertEquals("tosca.nodes.ObjectStorage", vertSchema.getName());
            assertEquals("size,name", vertSchema.getAnnotationValue("searchable"));
            assertEquals("aai-uuid,name", vertSchema.getAnnotationValue("indexedProps"));

            PropertySchema propSchema = vertSchema.getPropertySchema("Name");
            assertEquals("name", propSchema.getName());
            assertEquals("", propSchema.getDefaultValue());
            assertTrue(propSchema.isRequired());
            assertFalse(propSchema.isKey());
            assertFalse(propSchema.isReserved());
            assertEquals(0, propSchema.getDataType().getType().compareTo(Type.STRING));
            assertEquals("AAI", propSchema.getAnnotationValue("Source_of_truth_type"));

            propSchema = vertSchema.getPropertySchema("Size");
            assertEquals("size", propSchema.getName());
            assertEquals("50", propSchema.getDefaultValue());
            assertEquals(0, propSchema.getDataType().getType().compareTo(Type.INT));

            propSchema = vertSchema.getPropertySchema("source-of-truth");
            assertEquals("source-of-truth", propSchema.getName());
            assertFalse(propSchema.isRequired());
            assertTrue(propSchema.isReserved());
            assertEquals(0, propSchema.getDataType().getType().compareTo(Type.STRING));
        } catch (Exception ex) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            System.out.println(writer);
            fail();
        }
    }

    @Test
    public void testJsonSchemaTranslateEdge() {
        try {
            String testSchema = readFile("src/test/resources/json/jsonSchema.json");
            JsonSchemaProvider schemaProvider = new JsonSchemaProvider(config);
            schemaProvider.loadSchema(testSchema, schemaProvider.getLatestSchemaVersion());

            EdgeSchema edgeSchema = schemaProvider.getEdgeSchema("tosca.relationships.hostedOn",
                    "tosca.nodes.Softwarecomponent", "tosca.nodes.compute", schemaProvider.getLatestSchemaVersion());
            System.out.println(edgeSchema.toString());

            // Validate edge schema
            assertEquals("tosca.relationships.HostedOn", edgeSchema.getName());
            assertEquals("tosca.nodes.SoftwareComponent", edgeSchema.getSource());
            assertEquals("tosca.nodes.Compute", edgeSchema.getTarget());
            assertEquals(edgeSchema.getMultiplicity(), EdgeSchema.Multiplicity.MANY_2_MANY);
            assertEquals("NONE", edgeSchema.getAnnotationValue("contains-other-v"));

        } catch (Exception ex) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            System.out.println(writer);
            fail();
        }
    }

    @Test
    public void testJsonSchemaTranslateAdjacentEdge() {
        try {
            String testSchema = readFile("src/test/resources/json/jsonSchema.json");
            JsonSchemaProvider schemaProvider = new JsonSchemaProvider(config);
            schemaProvider.loadSchema(testSchema, schemaProvider.getLatestSchemaVersion());

            Set<EdgeSchema> edgeSchemaList = schemaProvider.getAdjacentEdgeSchema("tosca.nodes.Database",
                    schemaProvider.getLatestSchemaVersion());

            // Validate edge schema
            assertEquals(3, edgeSchemaList.size());

            for (EdgeSchema es : edgeSchemaList) {
                System.out.println(es.toString());
                switch (es.getName()) {
                    case "tosca.relationships.HostedOn":
                        assertEquals("tosca.nodes.Database", es.getSource());
                        assertEquals("tosca.nodes.DBMS", es.getTarget());
                        assertEquals(es.getMultiplicity(), EdgeSchema.Multiplicity.MANY_2_MANY);
                        break;
                    case "tosca.relationships.RoutesTo":
                    case "tosca.relationships.Uses":
                        assertEquals("tosca.nodes.LoadBalancer", es.getSource());
                        assertEquals("tosca.nodes.Database", es.getTarget());
                        assertEquals(es.getMultiplicity(), EdgeSchema.Multiplicity.MANY_2_MANY);
                        break;
                    default:
                        fail();
                        break;
                }
            }
        } catch (Exception ex) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            System.out.println(writer);
            fail();
        }
    }

    @Test
    public void testJsonSchemaSourceTargetEdges() {
        try {
            String testSchema = readFile("src/test/resources/json/jsonSchema.json");
            JsonSchemaProvider schemaProvider = new JsonSchemaProvider(config);
            schemaProvider.loadSchema(testSchema, schemaProvider.getLatestSchemaVersion());

            Set<EdgeSchema> edgeSchemaList = schemaProvider.getEdgeSchemaForSourceTarget("tosca.nodes.LoadBalancer",
                    "tosca.nodes.Database", schemaProvider.getLatestSchemaVersion());

            // Validate edge schema
            assertEquals(2, edgeSchemaList.size());

            for (EdgeSchema es : edgeSchemaList) {
                System.out.println(es.toString());
                if (es.getName().equals("tosca.relationships.Uses")) {
                    assertEquals("tosca.nodes.LoadBalancer", es.getSource());
                    assertEquals("tosca.nodes.Database", es.getTarget());
                    assertEquals(es.getMultiplicity(), EdgeSchema.Multiplicity.MANY_2_MANY);
                } else if (es.getName().equals("tosca.relationships.RoutesTo")) {
                    assertEquals("tosca.nodes.LoadBalancer", es.getSource());
                    assertEquals("tosca.nodes.Database", es.getTarget());
                    assertEquals(es.getMultiplicity(), EdgeSchema.Multiplicity.MANY_2_MANY);
                } else {
                    fail();
                }
            }
        } catch (Exception ex) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            System.out.println(writer);
            fail();
        }
    }

    @Test
    public void testJsonSchemaWildcardEdges() {
        try {
            String testSchema = readFile("src/test/resources/json/jsonSchema.json");
            JsonSchemaProvider schemaProvider = new JsonSchemaProvider(config);
            schemaProvider.loadSchema(testSchema, schemaProvider.getLatestSchemaVersion());

            EdgeSchema edgeSchema = schemaProvider.getEdgeSchema("amdocs.linkedTo", "service-instance",
                    "onap.nodes.sdwan.ManagementDomain", schemaProvider.getLatestSchemaVersion());

            assertEquals("amdocs.linkedTo", edgeSchema.getName());
            assertEquals("service-instance", edgeSchema.getSource());
            assertEquals("onap.nodes.sdwan.ManagementDomain", edgeSchema.getTarget());

            edgeSchema = schemaProvider.getEdgeSchema("amdocs.linkedTo", "onap.nodes.sdwan.ManagementDomain",
                    "service-instance", schemaProvider.getLatestSchemaVersion());

            assertNull(edgeSchema);

            edgeSchema = schemaProvider.getEdgeSchema("amdocs.unknownRelationship", "unknown",
                    "onap.nodes.sdwan.ManagementDomain", schemaProvider.getLatestSchemaVersion());

            assertEquals("amdocs.unknownRelationship", edgeSchema.getName());
            assertEquals("unknown", edgeSchema.getSource());
            assertEquals("onap.nodes.sdwan.ManagementDomain", edgeSchema.getTarget());

            edgeSchema = schemaProvider.getEdgeSchema("amdocs.unknownRelationship", "onap.nodes.sdwan.ManagementDomain",
                    "unknown", schemaProvider.getLatestSchemaVersion());

            assertEquals("amdocs.unknownRelationship", edgeSchema.getName());
            assertEquals("onap.nodes.sdwan.ManagementDomain", edgeSchema.getSource());
            assertEquals("unknown", edgeSchema.getTarget());

            Set<EdgeSchema> edgeSchemaList = schemaProvider.getEdgeSchemaForSourceTarget("service-instance",
                    "onap.nodes.sdwan.ManagementDomain", schemaProvider.getLatestSchemaVersion());
            assertEquals(1, edgeSchemaList.size());

            edgeSchemaList = schemaProvider.getEdgeSchemaForSourceTarget("unknown", "unknown",
                    schemaProvider.getLatestSchemaVersion());
            assertEquals(1, edgeSchemaList.size());

            edgeSchemaList = schemaProvider.getEdgeSchemaForSourceTarget("service-instance", "service-instance",
                    schemaProvider.getLatestSchemaVersion());
            assertEquals(1, edgeSchemaList.size());

            edgeSchemaList =
                    schemaProvider.getAdjacentEdgeSchema("service-instance", schemaProvider.getLatestSchemaVersion());
            System.out.println("EDGE LIST: \n\n" + edgeSchemaList);
            assertEquals(8, edgeSchemaList.size());
        } catch (Exception ex) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            System.out.println(writer);
            fail();
        }
    }

    @Test
    public void testInvalidVertexOrEdge() {
        try {
            String testSchema = readFile("src/test/resources/json/jsonSchema.json");
            JsonSchemaProvider schemaProvider = new JsonSchemaProvider(config);
            schemaProvider.loadSchema(testSchema, schemaProvider.getLatestSchemaVersion());

            VertexSchema vertSchema =
                    schemaProvider.getVertexSchema("bad-node", schemaProvider.getLatestSchemaVersion());
            assertNull(vertSchema);

            EdgeSchema edgeSchema = schemaProvider.getEdgeSchema("org.onap.relationships.inventory.LocatedIn",
                    "cloud-region", "bad-node", schemaProvider.getLatestSchemaVersion());
            assertNull(edgeSchema);

            Set<EdgeSchema> edgeSchemaList = schemaProvider.getAdjacentEdgeSchema("org.onap.nodes.bad-node",
                    schemaProvider.getLatestSchemaVersion());
            assertTrue(edgeSchemaList.isEmpty());
        } catch (Exception ex) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            System.out.println(writer);
            fail();
        }
    }

    @Test
    public void testJsonSchemaListAttribute() {
        try {
            String testSchema = readFile("src/test/resources/json/jsonSchema.json");
            JsonSchemaProvider schemaProvider = new JsonSchemaProvider(config);
            schemaProvider.loadSchema(testSchema, schemaProvider.getLatestSchemaVersion());

            VertexSchema vertSchema = schemaProvider.getVertexSchema("onap.nodes.sdwan.ManagementDomain",
                    schemaProvider.getLatestSchemaVersion());
            System.out.println(vertSchema.toString());

            // Validate schema
            PropertySchema propSchema = vertSchema.getPropertySchema("controllers");
            assertEquals(0, propSchema.getDataType().getType().compareTo(Type.LIST));
            ListDataType listType = (ListDataType) propSchema.getDataType();
            assertEquals(0, listType.getListType().getType().compareTo(Type.STRING));
        } catch (Exception ex) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            System.out.println(writer);
            fail();
        }
    }

    @Test
    public void testJsonSchemaMapAttribute() {
        try {
            String testSchema = readFile("src/test/resources/json/jsonSchema.json");
            JsonSchemaProvider schemaProvider = new JsonSchemaProvider(config);
            schemaProvider.loadSchema(testSchema, schemaProvider.getLatestSchemaVersion());

            VertexSchema vertSchema = schemaProvider.getVertexSchema("onap.nodes.sdwan.ManagementDomain",
                    schemaProvider.getLatestSchemaVersion());
            System.out.println(vertSchema.toString());

            // Validate schema
            PropertySchema propSchema = vertSchema.getPropertySchema("analyticClusters");
            assertEquals(0, propSchema.getDataType().getType().compareTo(Type.MAP));
            MapDataType mapType = (MapDataType) propSchema.getDataType();
            assertEquals(0, mapType.getMapType().getType().compareTo(Type.STRING));
        } catch (Exception ex) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            System.out.println(writer);
            fail();
        }
    }

    @Test
    public void testJsonSchemaComplexAttribute() {
        try {
            String testSchema = readFile("src/test/resources/json/jsonSchema.json");
            JsonSchemaProvider schemaProvider = new JsonSchemaProvider(config);
            schemaProvider.loadSchema(testSchema, schemaProvider.getLatestSchemaVersion());

            VertexSchema vertSchema = schemaProvider.getVertexSchema("org.onap.resource.extContrailCP",
                    schemaProvider.getLatestSchemaVersion());
            System.out.println(vertSchema.toString());

            System.out.println("\n\nSize: " + vertSchema.getPropertySchemaList().size());
            System.out.println(vertSchema.getPropertySchemaList());
            assertEquals(22, vertSchema.getPropertySchemaList().size());

            // Validate property schema
            PropertySchema propSchema = vertSchema.getPropertySchema("exCP_naming");
            assertEquals(0, propSchema.getDataType().getType().compareTo(Type.COMPLEX));
            ComplexDataType complexType = (ComplexDataType) propSchema.getDataType();
            List<PropertySchema> complexProps = complexType.getSubProperties();
            assertEquals(4, complexProps.size());

            PropertySchema subProp = null;
            for (PropertySchema p : complexProps) {
                if (p.getName().equals("naming_policy")) {
                    subProp = p;
                }
            }

            assertNotNull(subProp);
            assertFalse(subProp.isRequired());
            assertEquals(0, subProp.getDataType().getType().compareTo(Type.STRING));
        } catch (Exception ex) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            System.out.println(writer);
            fail();
        }
    }

    @Test
    public void testParseSchemaServiceResponse() {
        try {
            String testSchema = readFile("src/test/resources/json/schemaServiceResponse.json");
            SchemaServiceResponse resp = SchemaServiceResponse.fromJson(testSchema);

            System.out.println(resp.toJson());
            assertEquals("v1", resp.getVersion());

            JsonSchema jsonSchema = resp.getData();
            System.out.println(jsonSchema.toJson());

            assertEquals(1, jsonSchema.getDataTypes().size());
        } catch (Exception ex) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            System.out.println(writer);
            fail();
        }
    }

    @Test
    public void testSchemaValidateSuccess() {
        try {
            String testSchema = readFile("src/test/resources/json/schemaServiceResponse.json");
            SchemaServiceResponse schema = SchemaServiceResponse.fromJson(testSchema);
            schema.getData().validate();
        } catch (Exception ex) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            System.out.println(writer);
            fail();
        }
    }

    @Test(expected = SchemaProviderException.class)
    public void testSchemaValidateBadEdge() throws SchemaProviderException {
        SchemaServiceResponse schema;

        try {
            String testSchema = readFile("src/test/resources/json/badEdgeSchema.json");
            schema = SchemaServiceResponse.fromJson(testSchema);
        } catch (Exception ex) {
            fail();
            return;
        }

        schema.getData().validate();
    }

    @Test(expected = SchemaProviderException.class)
    public void testSchemaValidateBadVertex() throws SchemaProviderException {
        SchemaServiceResponse schema;

        try {
            String testSchema = readFile("src/test/resources/json/badVertexSchema.json");
            schema = SchemaServiceResponse.fromJson(testSchema);
        } catch (Exception ex) {
            fail();
            return;
        }

        System.out.println("Validate");
        schema.getData().validate();
        System.out.println("Validate done");
    }

    @Test(expected = SchemaProviderException.class)
    public void testSchemaValidateBadType() throws SchemaProviderException {
        SchemaServiceResponse schema;

        try {
            String testSchema = readFile("src/test/resources/json/badTypeSchema.json");
            schema = SchemaServiceResponse.fromJson(testSchema);
        } catch (Exception ex) {
            fail();
            return;
        }

        schema.getData().validate();
    }

    @Test(expected = SchemaProviderException.class)
    public void testSchemaValidateBadProp() throws SchemaProviderException {
        SchemaServiceResponse schema;

        try {
            String testSchema = readFile("src/test/resources/json/badPropSchema.json");
            schema = SchemaServiceResponse.fromJson(testSchema);
        } catch (Exception ex) {
            fail();
            return;
        }

        schema.getData().validate();
    }

    static String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded);
    }
}
