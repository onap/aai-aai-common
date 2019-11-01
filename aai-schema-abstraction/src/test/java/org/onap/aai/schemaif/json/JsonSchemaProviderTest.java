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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

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
    public void init() throws Exception {
        config.setSchemaServiceBaseUrl("https://testurl.com:8443");
        config.setSchemaServiceCertFile("/c/certfile");
        config.setSchemaServiceCertPwd("my-password");
        config.setServiceName("test-service");
    }

    @Test
    public void testJsonSchemaLoad() {
        try {
            String testSchema = readFile("src/test/resources/json/jsonSchema.json");
            JsonSchema jsonSchema = JsonSchema.fromJson(testSchema);

            // Test Edge Schema
            JsonEdgeSchema edgeSchema = null;
            for (JsonEdgeSchema edge : jsonSchema.getRelationshipTypes()) {
                if ( (edge.getFrom().equals("onap.nodes.sdwan.uCPE")) 
                        && (edge.getTo().equals("onap.nodes.sdwan.service.SubscriberService")) ) {
                    edgeSchema = edge;
                    break;
                }
            }

            assertTrue(edgeSchema.getLabel().equals("onap.relationships.sdwan.BelongsTo"));

            // Test Node Schema
            JsonVertexSchema vertexSchema = null;
            for (JsonVertexSchema v : jsonSchema.getNodeTypes()) {
                if ( (v.getName().equals("org.onap.resource.NetworkRules")) ) {
                    vertexSchema = v;
                    break;
                }
            }

            assertTrue(vertexSchema.getProperties().size() == 2);

            JsonPropertySchema propSchema = null;
            for (JsonPropertySchema p : vertexSchema.getProperties()) {
                if ( (p.getName().equals("network_policy_entries")) ) {
                    propSchema = p;
                    break;
                }
            }

            assertTrue(propSchema.getRequired() == false);
            assertTrue(propSchema.getUnique() == false);
            assertTrue(propSchema.getDataType().equals("org.onap.datatypes.RuleList"));
            assertTrue(propSchema.getDefaultValue().equals(""));
            assertTrue(propSchema.getAnnotations().size() == 4);

            // Test DataType Schema
            DataTypeDefinition dataType = null;
            for (DataTypeDefinition d : jsonSchema.getDataTypes()) {
                if ( (d.getName().equals("org.onap.datatypes.network.VlanRequirements")) ) {
                    dataType = d;
                    break;
                }
            }

            assertTrue(dataType.getName().equals("org.onap.datatypes.network.VlanRequirements"));
            assertTrue(dataType.getProperties().size() == 4);

            propSchema = null;
            for (JsonPropertySchema p : dataType.getProperties()) {
                if ( (p.getName().equals("vlan_type")) ) {
                    propSchema = p;
                    break;
                }
            }

            assertTrue(propSchema.getRequired() == false);
            assertTrue(propSchema.getDataType().equals("string"));
            assertTrue(propSchema.getDefaultValue().equals(""));
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
    public void testJsonSchemaTranslateVertex() {
        try {
            String testSchema = readFile("src/test/resources/json/jsonSchema.json");
            JsonSchemaProvider schemaProvider = new JsonSchemaProvider(config);
            schemaProvider.loadSchema(testSchema, schemaProvider.getLatestSchemaVersion());

            VertexSchema vertSchema = 
                    schemaProvider.getVertexSchema("tosca.nodes.objectstorage", 
                            schemaProvider.getLatestSchemaVersion());
            System.out.println(vertSchema.toString());

            // Validate vertex schema
            assertTrue(vertSchema.getName().equals("tosca.nodes.ObjectStorage"));
            assertTrue(vertSchema.getAnnotationValue("searchable").equals("size,name"));
            assertTrue(vertSchema.getAnnotationValue("indexedProps").equals("aai-uuid,name"));

            PropertySchema propSchema = vertSchema.getPropertySchema("Name");
            assertTrue(propSchema.getName().equals("name"));
            assertTrue(propSchema.getDefaultValue().equals(""));
            assertTrue(propSchema.isRequired());
            assertTrue(!propSchema.isKey());
            assertTrue(!propSchema.isReserved());
            assertTrue(propSchema.getDataType().getType().compareTo(Type.STRING) == 0);
            assertTrue(propSchema.getAnnotationValue("Source_of_truth_type").equals("AAI"));

            propSchema = vertSchema.getPropertySchema("Size");
            assertTrue(propSchema.getName().equals("size"));
            assertTrue(propSchema.getDefaultValue().equals("50"));
            assertTrue(propSchema.getDataType().getType().compareTo(Type.INT) == 0);
            
            propSchema = vertSchema.getPropertySchema("source-of-truth");
            assertTrue(propSchema.getName().equals("source-of-truth"));
            assertTrue(!propSchema.isRequired());
            assertTrue(propSchema.isReserved());
            assertTrue(propSchema.getDataType().getType().compareTo(Type.STRING) == 0);
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
    public void testJsonSchemaTranslateEdge() {
        try {
            String testSchema = readFile("src/test/resources/json/jsonSchema.json");
            JsonSchemaProvider schemaProvider = new JsonSchemaProvider(config);
            schemaProvider.loadSchema(testSchema, schemaProvider.getLatestSchemaVersion());

            EdgeSchema edgeSchema = schemaProvider.getEdgeSchema("tosca.relationships.hostedOn", 
                    "tosca.nodes.Softwarecomponent", "tosca.nodes.compute", 
                    schemaProvider.getLatestSchemaVersion());
            System.out.println(edgeSchema.toString());

            // Validate edge schema
            assertTrue(edgeSchema.getName().equals("tosca.relationships.HostedOn"));
            assertTrue(edgeSchema.getSource().equals("tosca.nodes.SoftwareComponent"));
            assertTrue(edgeSchema.getTarget().equals("tosca.nodes.Compute"));
            assertTrue(edgeSchema.getMultiplicity().equals(EdgeSchema.Multiplicity.MANY_2_MANY));
            assertTrue(edgeSchema.getAnnotationValue("contains-other-v").equals("NONE"));


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
    public void testJsonSchemaTranslateAdjacentEdge() {
        try {
            String testSchema = readFile("src/test/resources/json/jsonSchema.json");
            JsonSchemaProvider schemaProvider = new JsonSchemaProvider(config);
            schemaProvider.loadSchema(testSchema, schemaProvider.getLatestSchemaVersion());

            Set<EdgeSchema> edgeSchemaList = 
                    schemaProvider.getAdjacentEdgeSchema("tosca.nodes.Database", 
                            schemaProvider.getLatestSchemaVersion());

            // Validate edge schema
            assertTrue(edgeSchemaList.size() == 3);

            for (EdgeSchema es : edgeSchemaList) {
                System.out.println(es.toString());
                if (es.getName().equals("tosca.relationships.HostedOn")) {
                    assertTrue(es.getSource().equals("tosca.nodes.Database"));
                    assertTrue(es.getTarget().equals("tosca.nodes.DBMS"));
                    assertTrue(es.getMultiplicity().equals(EdgeSchema.Multiplicity.MANY_2_MANY));
                }
                else if (es.getName().equals("tosca.relationships.RoutesTo")) {
                    assertTrue(es.getSource().equals("tosca.nodes.LoadBalancer"));
                    assertTrue(es.getTarget().equals("tosca.nodes.Database"));
                    assertTrue(es.getMultiplicity().equals(EdgeSchema.Multiplicity.MANY_2_MANY));
                }
                else if (es.getName().equals("tosca.relationships.Uses")) {
                    assertTrue(es.getSource().equals("tosca.nodes.LoadBalancer"));
                    assertTrue(es.getTarget().equals("tosca.nodes.Database"));
                    assertTrue(es.getMultiplicity().equals(EdgeSchema.Multiplicity.MANY_2_MANY));
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
    public void testJsonSchemaSourceTargetEdges() {
        try {
            String testSchema = readFile("src/test/resources/json/jsonSchema.json");
            JsonSchemaProvider schemaProvider = new JsonSchemaProvider(config);
            schemaProvider.loadSchema(testSchema, schemaProvider.getLatestSchemaVersion());

            Set<EdgeSchema> edgeSchemaList = 
                    schemaProvider.getEdgeSchemaForSourceTarget("tosca.nodes.LoadBalancer", 
                            "tosca.nodes.Database", 
                            schemaProvider.getLatestSchemaVersion());

            // Validate edge schema
            assertTrue(edgeSchemaList.size() == 2);

            for (EdgeSchema es : edgeSchemaList) {
                System.out.println(es.toString());
                if (es.getName().equals("tosca.relationships.Uses")) {
                    assertTrue(es.getSource().equals("tosca.nodes.LoadBalancer"));
                    assertTrue(es.getTarget().equals("tosca.nodes.Database"));
                    assertTrue(es.getMultiplicity().equals(EdgeSchema.Multiplicity.MANY_2_MANY));
                }
                else if (es.getName().equals("tosca.relationships.RoutesTo")) {
                    assertTrue(es.getSource().equals("tosca.nodes.LoadBalancer"));
                    assertTrue(es.getTarget().equals("tosca.nodes.Database"));
                    assertTrue(es.getMultiplicity().equals(EdgeSchema.Multiplicity.MANY_2_MANY));
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
    public void testJsonSchemaWildcardEdges() {
        try {
            String testSchema = readFile("src/test/resources/json/jsonSchema.json");
            JsonSchemaProvider schemaProvider = new JsonSchemaProvider(config);
            schemaProvider.loadSchema(testSchema, schemaProvider.getLatestSchemaVersion());

            EdgeSchema edgeSchema = 
                    schemaProvider.getEdgeSchema("amdocs.linkedTo", "service-instance", 
                            "onap.nodes.sdwan.ManagementDomain", schemaProvider.getLatestSchemaVersion());

            assertTrue(edgeSchema.getName().equals("amdocs.linkedTo"));
            assertTrue(edgeSchema.getSource().equals("service-instance"));
            assertTrue(edgeSchema.getTarget().equals("onap.nodes.sdwan.ManagementDomain"));

            edgeSchema = schemaProvider.getEdgeSchema("amdocs.linkedTo", "onap.nodes.sdwan.ManagementDomain", 
                    "service-instance", schemaProvider.getLatestSchemaVersion());

            assertTrue(edgeSchema == null);
            
            
            edgeSchema = 
                    schemaProvider.getEdgeSchema("amdocs.unknownRelationship", "unknown", 
                            "onap.nodes.sdwan.ManagementDomain", schemaProvider.getLatestSchemaVersion());
            
            assertTrue(edgeSchema.getName().equals("amdocs.unknownRelationship"));
            assertTrue(edgeSchema.getSource().equals("unknown"));
            assertTrue(edgeSchema.getTarget().equals("onap.nodes.sdwan.ManagementDomain"));
            
            edgeSchema = 
                    schemaProvider.getEdgeSchema("amdocs.unknownRelationship", "onap.nodes.sdwan.ManagementDomain", 
                            "unknown", schemaProvider.getLatestSchemaVersion());
            
            assertTrue(edgeSchema.getName().equals("amdocs.unknownRelationship"));
            assertTrue(edgeSchema.getSource().equals("onap.nodes.sdwan.ManagementDomain"));
            assertTrue(edgeSchema.getTarget().equals("unknown"));
            
            Set<EdgeSchema> edgeSchemaList = 
                    schemaProvider.getEdgeSchemaForSourceTarget("service-instance", 
                            "onap.nodes.sdwan.ManagementDomain", 
                            schemaProvider.getLatestSchemaVersion());
            assertTrue(edgeSchemaList.size() == 1);
            
            edgeSchemaList = schemaProvider.getEdgeSchemaForSourceTarget("unknown", "unknown", 
                    schemaProvider.getLatestSchemaVersion());
            assertTrue(edgeSchemaList.size() == 1);
            
            edgeSchemaList = schemaProvider.getEdgeSchemaForSourceTarget("service-instance", "service-instance", 
                    schemaProvider.getLatestSchemaVersion());
            assertTrue(edgeSchemaList.size() == 1);
            
            
            edgeSchemaList = schemaProvider.getAdjacentEdgeSchema("service-instance", schemaProvider.getLatestSchemaVersion());
            System.out.println("EDGE LIST: \n\n" + edgeSchemaList);
            assertTrue(edgeSchemaList.size() == 8);
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
            String testSchema = readFile("src/test/resources/json/jsonSchema.json");
            JsonSchemaProvider schemaProvider = new JsonSchemaProvider(config);
            schemaProvider.loadSchema(testSchema, schemaProvider.getLatestSchemaVersion());

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

    @Test
    public void testJsonSchemaListAttribute() {
        try {
            String testSchema = readFile("src/test/resources/json/jsonSchema.json");
            JsonSchemaProvider schemaProvider = new JsonSchemaProvider(config);
            schemaProvider.loadSchema(testSchema, schemaProvider.getLatestSchemaVersion());

            VertexSchema vertSchema = 
                    schemaProvider.getVertexSchema("onap.nodes.sdwan.ManagementDomain", 
                            schemaProvider.getLatestSchemaVersion());
            System.out.println(vertSchema.toString());

            // Validate schema            
            PropertySchema propSchema = vertSchema.getPropertySchema("controllers");
            assertTrue(propSchema.getDataType().getType().compareTo(Type.LIST) == 0);
            ListDataType listType = (ListDataType)propSchema.getDataType();
            assertTrue(listType.getListType().getType().compareTo(Type.STRING) == 0);
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
    public void testJsonSchemaMapAttribute() {
        try {
            String testSchema = readFile("src/test/resources/json/jsonSchema.json");
            JsonSchemaProvider schemaProvider = new JsonSchemaProvider(config);
            schemaProvider.loadSchema(testSchema, schemaProvider.getLatestSchemaVersion());

            VertexSchema vertSchema = 
                    schemaProvider.getVertexSchema("onap.nodes.sdwan.ManagementDomain", 
                            schemaProvider.getLatestSchemaVersion());
            System.out.println(vertSchema.toString());

            // Validate schema            
            PropertySchema propSchema = vertSchema.getPropertySchema("analyticClusters");
            assertTrue(propSchema.getDataType().getType().compareTo(Type.MAP) == 0);
            MapDataType mapType = (MapDataType)propSchema.getDataType();
            assertTrue(mapType.getMapType().getType().compareTo(Type.STRING) == 0);
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
    public void testJsonSchemaComplexAttribute() {
        try {
            String testSchema = readFile("src/test/resources/json/jsonSchema.json");
            JsonSchemaProvider schemaProvider = new JsonSchemaProvider(config);
            schemaProvider.loadSchema(testSchema, schemaProvider.getLatestSchemaVersion());

            VertexSchema vertSchema = 
                    schemaProvider.getVertexSchema("org.onap.resource.extContrailCP", 
                            schemaProvider.getLatestSchemaVersion());
            System.out.println(vertSchema.toString());

            System.out.println("\n\nSize: " + vertSchema.getPropertySchemaList().size());
            System.out.println(vertSchema.getPropertySchemaList());
            assertTrue(vertSchema.getPropertySchemaList().size() == 22);

            // Validate property schema            
            PropertySchema propSchema = vertSchema.getPropertySchema("exCP_naming");
            assertTrue(propSchema.getDataType().getType().compareTo(Type.COMPLEX) == 0);
            ComplexDataType complexType = (ComplexDataType)propSchema.getDataType();
            List<PropertySchema> complexProps = complexType.getSubProperties();
            assertTrue(complexProps.size() == 4);

            PropertySchema subProp = null;
            for (PropertySchema p : complexProps) {
                if (p.getName().equals("naming_policy")) {
                    subProp = p;
                }
            }

            assertTrue(!subProp.isRequired());
            assertTrue(subProp.getDataType().getType().compareTo(Type.STRING) == 0);
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
    public void testParseSchemaServiceResponse() {
        try {
            String testSchema = readFile("src/test/resources/json/schemaServiceResponse.json");
            SchemaServiceResponse resp = SchemaServiceResponse.fromJson(testSchema);

            System.out.println(resp.toJson());
            assertTrue(resp.getVersion().equals("v1"));

            JsonSchema jsonSchema = resp.getData();
            System.out.println(jsonSchema.toJson());

            assertTrue(jsonSchema.getDataTypes().size() == 1);
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
    public void testSchemaValidateSuccess() {
        try {
            String testSchema = readFile("src/test/resources/json/schemaServiceResponse.json");
            SchemaServiceResponse schema = SchemaServiceResponse.fromJson(testSchema);
            schema.getData().validate();
        }
        catch (Exception ex) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            System.out.println(writer.toString());
            assertTrue(false);
        }
    }           

    @Test(expected = SchemaProviderException.class)
    public void testSchemaValidateBadEdge() throws SchemaProviderException {
        SchemaServiceResponse schema;    

        try {
            String testSchema = readFile("src/test/resources/json/badEdgeSchema.json");
            schema = SchemaServiceResponse.fromJson(testSchema);
        }
        catch (Exception ex) {
            assertTrue(false);
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
        }
        catch (Exception ex) {
            assertTrue(false);
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
        }
        catch (Exception ex) {
            assertTrue(false);
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
        }
        catch (Exception ex) {
            assertTrue(false);
            return;
        }

        schema.getData().validate(); 
    }

    static String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded);
    }
}
