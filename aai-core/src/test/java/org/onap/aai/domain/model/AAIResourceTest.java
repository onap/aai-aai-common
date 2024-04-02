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

package org.onap.aai.domain.model;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.aai.AAISetup;

public class AAIResourceTest extends AAISetup {
    private AAIResource aaiResource;

    @BeforeEach
    public void setUp() throws Exception {
        aaiResource = new AAIResource();
    }

    @Test
    public void testGetterSetterParent() {
        AAIResource aaiResourceParent = new AAIResource();
        aaiResource.setParent(aaiResourceParent);
        Assertions.assertEquals(aaiResource.getParent(), aaiResourceParent);
    }

    @Test
    public void testGetChildren() {
        Assertions.assertNotNull(aaiResource.getChildren());
    }

    @Test
    public void testGetAaiResourceKeys() {
        Assertions.assertNotNull(aaiResource.getAaiResourceKeys());
    }

    @Test
    public void testGetterSetterNamespace() {
        String testNamespace = "testNamespace";
        aaiResource.setNamespace(testNamespace);
        Assertions.assertEquals(testNamespace, aaiResource.getNamespace());
    }

    @Test
    public void testGetterSetterResourceType() {
        String testResourceType = "testResourceType";
        aaiResource.setResourceType(testResourceType);
        Assertions.assertEquals(testResourceType, aaiResource.getResourceType());
    }

    @Test
    public void testGetterSetterSimpleName() {
        String testSimpleName = "testSimpleName";
        aaiResource.setSimpleName(testSimpleName);
        Assertions.assertEquals(testSimpleName, aaiResource.getSimpleName());
    }

    @Test
    public void testGetterSetterFullName() {
        String testFullName = "testFullName";
        aaiResource.setFullName(testFullName);
        Assertions.assertEquals(testFullName, aaiResource.getFullName());
    }

    @Test
    public void testGetterSetterUri() {
        String testUri = "testUri";
        aaiResource.setUri(testUri);
        Assertions.assertEquals(testUri, aaiResource.getUri());
    }

    @Test
    public void testGetterSetterResourceClassName() {
        String testResourceClassName = "testResourceClassName";
        aaiResource.setResourceClassName(testResourceClassName);
        Assertions.assertEquals(testResourceClassName, aaiResource.getResourceClassName());
    }

    @Test
    public void testGetterSetterPropertyDataTypeMap() {
        Map<String, String> propertyDataTypeMap = new HashMap<>();
        aaiResource.setPropertyDataTypeMap(propertyDataTypeMap);
        Assertions.assertEquals(propertyDataTypeMap, aaiResource.getPropertyDataTypeMap());
    }

    @Test
    public void testGetterSetterNodeMapIndexedProps() {
        Multimap<String, String> nodeMapIndexedProps = ArrayListMultimap.create();
        aaiResource.setNodeMapIndexedProps(nodeMapIndexedProps);
        Assertions.assertEquals(nodeMapIndexedProps, aaiResource.getNodeMapIndexedProps());
    }

    @Test
    public void testGetterSetterNodeKeyProps() {
        Multimap<String, String> nodeKeyProps = ArrayListMultimap.create();
        aaiResource.setNodeKeyProps(nodeKeyProps);
        Assertions.assertEquals(nodeKeyProps, aaiResource.getNodeKeyProps());
    }

    @Test
    public void testGetterSetterNodeNameProps() {
        Multimap<String, String> nodeNameProps = ArrayListMultimap.create();
        aaiResource.setNodeNameProps(nodeNameProps);
        Assertions.assertEquals(nodeNameProps, aaiResource.getNodeNameProps());
    }

    @Test
    public void testGetterSetterNodeUniqueProps() {
        Multimap<String, String> nodeUniqueProps = ArrayListMultimap.create();
        aaiResource.setNodeUniqueProps(nodeUniqueProps);
        Assertions.assertEquals(nodeUniqueProps, aaiResource.getNodeUniqueProps());
    }

    @Test
    public void testGetterSetterNodeReqProps() {
        Multimap<String, String> nodeReqProps = ArrayListMultimap.create();
        aaiResource.setNodeReqProps(nodeReqProps);
        Assertions.assertEquals(nodeReqProps, aaiResource.getNodeReqProps());
    }

    @Test
    public void testGetterSetterApiVersion() {
        String testApiVersion = "testApiVersion";
        aaiResource.setApiVersion(testApiVersion);
        Assertions.assertEquals(testApiVersion, aaiResource.getApiVersion());
    }

    @Test
    public void testGetterSetterRelationshipClass() {
        String testRelationshipClass = "testRelationshipClass";
        aaiResource.setRelationshipListClass(testRelationshipClass);
        Assertions.assertEquals(testRelationshipClass, aaiResource.getRelationshipListClass());
    }

    @Test
    public void testGetterSetterRelationshipUtils() {
        String testRelationshipUtils = "testRelationshipUtils";
        aaiResource.setRelationshipUtils(testRelationshipUtils);
        Assertions.assertEquals(testRelationshipUtils, aaiResource.getRelationshipUtils());
    }

    @Test
    public void testGetterSetterStringFields() {
        Assertions.assertNotNull(aaiResource.getStringFields());
        ArrayList<String> stringFields = new ArrayList<>();
        aaiResource.setStringFields(stringFields);
        Assertions.assertEquals(stringFields, aaiResource.getStringFields());
    }

    @Test
    public void testGetterSetterStringListFields() {
        Assertions.assertNotNull(aaiResource.getStringListFields());
        ArrayList<String> stringListFields = new ArrayList<>();
        aaiResource.setStringListFields(stringListFields);
        Assertions.assertEquals(stringListFields, aaiResource.getStringListFields());
    }

    @Test
    public void testGetterSetterLongFields() {
        Assertions.assertNotNull(aaiResource.getLongFields());
        ArrayList<String> longFields = new ArrayList<>();
        aaiResource.setLongFields(longFields);
        Assertions.assertEquals(longFields, aaiResource.getLongFields());
    }

    @Test
    public void testGetterSetterIntFields() {
        Assertions.assertNotNull(aaiResource.getIntFields());
        ArrayList<String> intFields = new ArrayList<>();
        aaiResource.setIntFields(intFields);
        Assertions.assertEquals(intFields, aaiResource.getIntFields());
    }

    @Test
    public void testGetterSetterShortFields() {
        Assertions.assertNotNull(aaiResource.getShortFields());
        ArrayList<String> shortFields = new ArrayList<>();
        aaiResource.setShortFields(shortFields);
        Assertions.assertEquals(shortFields, aaiResource.getShortFields());
    }

    @Test
    public void testGetterSetterBooleanFields() {
        Assertions.assertNotNull(aaiResource.getBooleanFields());
        ArrayList<String> booleanFields = new ArrayList<>();
        aaiResource.setBooleanFields(booleanFields);
        Assertions.assertEquals(booleanFields, aaiResource.getBooleanFields());
    }

    @Test
    public void testGetterSetterRequiredFields() {
        Assertions.assertNotNull(aaiResource.getRequiredFields());
        ArrayList<String> requiredFields = new ArrayList<>();
        aaiResource.setRequiredFields(requiredFields);
        Assertions.assertEquals(requiredFields, aaiResource.getRequiredFields());
    }

    @Test
    public void testGetOrderedFields() {
        Assertions.assertNotNull(aaiResource.getOrderedFields());
    }

    @Test
    public void testGetAllFields() {
        Assertions.assertNotNull(aaiResource.getAllFields());
    }

    @Test
    public void testGetPluralName() {
        AAIResource ar = new AAIResource();
        ar.setSimpleName("List");
        String pluralName = ar.getPluralName();
        Assertions.assertEquals("", pluralName);
        ar.setFullName("Some/FullName/ExpectedValue/Here");
        ar.setSimpleName("bogusValue");
        pluralName = ar.getPluralName();
        Assertions.assertEquals("ExpectedValue", pluralName);
    }

    @Test
    public void testGetterSetterNodeAltKey1Props() {
        Multimap<String, String> nodeAltKey1Props = ArrayListMultimap.create();
        aaiResource.setNodeAltKey1Props(nodeAltKey1Props);
        Assertions.assertEquals(nodeAltKey1Props, aaiResource.getNodeAltKey1Props());
    }

    @Test
    public void testGetterSetterNodeDependencies() {
        Multimap<String, String> _dbRulesNodeDependencies = ArrayListMultimap.create();
        aaiResource.setNodeDependencies(_dbRulesNodeDependencies);
        Assertions.assertEquals(_dbRulesNodeDependencies, aaiResource.getNodeDependencies());
    }

    @Test
    public void testGetterSetterRecurseToResource() {
        AAIResource recurseToResource = new AAIResource();
        recurseToResource.setFullName("FullNameTest");
        aaiResource.setRecurseToResource(recurseToResource);
        Assertions.assertEquals(recurseToResource, aaiResource.getRecurseToResource());
    }

    @Test
    public void testGetterSetterAllowDirectWrite() {
        aaiResource.setAllowDirectWrite(true);
        Assertions.assertTrue(aaiResource.isAllowDirectWrite());
    }

    @Test
    public void testGetterSetterAllowDirectRead() {
        aaiResource.setAllowDirectRead(true);
        Assertions.assertTrue(aaiResource.isAllowDirectRead());
    }

    @Test
    public void testGetAutoGenUuidFields() {
        Assertions.assertNotNull(aaiResource.getAutoGenUuidFields());
    }

}
