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
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MultiHashtable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.audit.ListEndpoints;
import org.onap.aai.setup.SchemaVersion;

import java.util.*;

public class AAIResourceTest extends AAISetup {
    private AAIResource aaiResource;

    @Before
    public void setUp() throws Exception {
        aaiResource = new AAIResource();
    }

    @Test
    public void testGetterSetterParent() {
        AAIResource aaiResourceParent = new AAIResource();
        aaiResource.setParent(aaiResourceParent);
        Assert.assertEquals(aaiResource.getParent(), aaiResourceParent);
    }

    @Test
    public void testGetChildren() {
        Assert.assertNotNull(aaiResource.getChildren());
    }

    @Test
    public void testGetAaiResourceKeys() {
        Assert.assertNotNull(aaiResource.getAaiResourceKeys());
    }

    @Test
    public void testGetterSetterNamespace() {
        String testNamespace = "testNamespace";
        aaiResource.setNamespace(testNamespace);
        Assert.assertEquals(testNamespace, aaiResource.getNamespace());
    }

    @Test
    public void testGetterSetterResourceType() {
        String testResourceType = "testResourceType";
        aaiResource.setResourceType(testResourceType);
        Assert.assertEquals(testResourceType, aaiResource.getResourceType());
    }

    @Test
    public void testGetterSetterSimpleName() {
        String testSimpleName = "testSimpleName";
        aaiResource.setSimpleName(testSimpleName);
        Assert.assertEquals(testSimpleName, aaiResource.getSimpleName());
    }

    @Test
    public void testGetterSetterFullName() {
        String testFullName = "testFullName";
        aaiResource.setFullName(testFullName);
        Assert.assertEquals(testFullName, aaiResource.getFullName());
    }

    @Test
    public void testGetterSetterUri() {
        String testUri = "testUri";
        aaiResource.setUri(testUri);
        Assert.assertEquals(testUri, aaiResource.getUri());
    }

    @Test
    public void testGetterSetterResourceClassName() {
        String testResourceClassName = "testResourceClassName";
        aaiResource.setResourceClassName(testResourceClassName);
        Assert.assertEquals(testResourceClassName, aaiResource.getResourceClassName());
    }

    @Test
    public void testGetterSetterPropertyDataTypeMap() {
        Map<String, String> propertyDataTypeMap = new HashMap<>();
        aaiResource.setPropertyDataTypeMap(propertyDataTypeMap);
        Assert.assertEquals(propertyDataTypeMap, aaiResource.getPropertyDataTypeMap());
    }

    @Test
    public void testGetterSetterNodeMapIndexedProps() {
        Multimap<String, String> nodeMapIndexedProps = ArrayListMultimap.create();
        aaiResource.setNodeMapIndexedProps(nodeMapIndexedProps);
        Assert.assertEquals(nodeMapIndexedProps, aaiResource.getNodeMapIndexedProps());
    }

    @Test
    public void testGetterSetterNodeKeyProps() {
        Multimap<String, String> nodeKeyProps = ArrayListMultimap.create();
        aaiResource.setNodeKeyProps(nodeKeyProps);
        Assert.assertEquals(nodeKeyProps, aaiResource.getNodeKeyProps());
    }

    @Test
    public void testGetterSetterNodeNameProps() {
        Multimap<String, String> nodeNameProps = ArrayListMultimap.create();
        aaiResource.setNodeNameProps(nodeNameProps);
        Assert.assertEquals(nodeNameProps, aaiResource.getNodeNameProps());
    }

    @Test
    public void testGetterSetterNodeUniqueProps() {
        Multimap<String, String> nodeUniqueProps = ArrayListMultimap.create();
        aaiResource.setNodeUniqueProps(nodeUniqueProps);
        Assert.assertEquals(nodeUniqueProps, aaiResource.getNodeUniqueProps());
    }

    @Test
    public void testGetterSetterNodeReqProps() {
        Multimap<String, String> nodeReqProps = ArrayListMultimap.create();
        aaiResource.setNodeReqProps(nodeReqProps);
        Assert.assertEquals(nodeReqProps, aaiResource.getNodeReqProps());
    }

    @Test
    public void testGetterSetterApiVersion() {
        String testApiVersion = "testApiVersion";
        aaiResource.setApiVersion(testApiVersion);
        Assert.assertEquals(testApiVersion, aaiResource.getApiVersion());
    }

    @Test
    public void testGetterSetterRelationshipClass() {
        String testRelationshipClass = "testRelationshipClass";
        aaiResource.setRelationshipListClass(testRelationshipClass);
        Assert.assertEquals(testRelationshipClass, aaiResource.getRelationshipListClass());
    }

    @Test
    public void testGetterSetterRelationshipUtils() {
        String testRelationshipUtils = "testRelationshipUtils";
        aaiResource.setRelationshipUtils(testRelationshipUtils);
        Assert.assertEquals(testRelationshipUtils, aaiResource.getRelationshipUtils());
    }

    @Test
    public void testGetterSetterStringFields() {
        Assert.assertNotNull(aaiResource.getStringFields());
        ArrayList<String> stringFields = new ArrayList<>();
        aaiResource.setStringFields(stringFields);
        Assert.assertEquals(stringFields, aaiResource.getStringFields());
    }

    @Test
    public void testGetterSetterStringListFields() {
        Assert.assertNotNull(aaiResource.getStringListFields());
        ArrayList<String> stringListFields = new ArrayList<>();
        aaiResource.setStringListFields(stringListFields);
        Assert.assertEquals(stringListFields, aaiResource.getStringListFields());
    }

    @Test
    public void testGetterSetterLongFields() {
        Assert.assertNotNull(aaiResource.getLongFields());
        ArrayList<String> longFields = new ArrayList<>();
        aaiResource.setLongFields(longFields);
        Assert.assertEquals(longFields, aaiResource.getLongFields());
    }

    @Test
    public void testGetterSetterIntFields() {
        Assert.assertNotNull(aaiResource.getIntFields());
        ArrayList<String> intFields = new ArrayList<>();
        aaiResource.setIntFields(intFields);
        Assert.assertEquals(intFields, aaiResource.getIntFields());
    }

    @Test
    public void testGetterSetterShortFields() {
        Assert.assertNotNull(aaiResource.getShortFields());
        ArrayList<String> shortFields = new ArrayList<>();
        aaiResource.setShortFields(shortFields);
        Assert.assertEquals(shortFields, aaiResource.getShortFields());
    }

    @Test
    public void testGetterSetterBooleanFields() {
        Assert.assertNotNull(aaiResource.getBooleanFields());
        ArrayList<String> booleanFields = new ArrayList<>();
        aaiResource.setBooleanFields(booleanFields);
        Assert.assertEquals(booleanFields, aaiResource.getBooleanFields());
    }

    @Test
    public void testGetterSetterRequiredFields() {
        Assert.assertNotNull(aaiResource.getRequiredFields());
        ArrayList<String> requiredFields = new ArrayList<>();
        aaiResource.setRequiredFields(requiredFields);
        Assert.assertEquals(requiredFields, aaiResource.getRequiredFields());
    }

    @Test
    public void testGetOrderedFields() {
        Assert.assertNotNull(aaiResource.getOrderedFields());
    }

    @Test
    public void testGetAllFields() {
        Assert.assertNotNull(aaiResource.getAllFields());
    }

    @Test
    public void testGetPluralName() {
        AAIResource ar = new AAIResource();
        ar.setSimpleName("List");
        String pluralName = ar.getPluralName();
        Assert.assertEquals("", pluralName);
        ar.setFullName("Some/FullName/ExpectedValue/Here");
        ar.setSimpleName("bogusValue");
        pluralName = ar.getPluralName();
        Assert.assertEquals("ExpectedValue", pluralName);
    }

    @Test
    public void testGetterSetterNodeAltKey1Props() {
        Multimap<String, String> nodeAltKey1Props = ArrayListMultimap.create();
        aaiResource.setNodeAltKey1Props(nodeAltKey1Props);
        Assert.assertEquals(nodeAltKey1Props, aaiResource.getNodeAltKey1Props());
    }

    @Test
    public void testGetterSetterNodeDependencies() {
        Multimap<String, String> _dbRulesNodeDependencies = ArrayListMultimap.create();
        aaiResource.setNodeDependencies(_dbRulesNodeDependencies);
        Assert.assertEquals(_dbRulesNodeDependencies, aaiResource.getNodeDependencies());
    }

    @Test
    public void testGetterSetterRecurseToResource() {
        AAIResource recurseToResource = new AAIResource();
        recurseToResource.setFullName("FullNameTest");
        aaiResource.setRecurseToResource(recurseToResource);
        Assert.assertEquals(recurseToResource, aaiResource.getRecurseToResource());
    }

    @Test
    public void testGetterSetterAllowDirectWrite() {
        aaiResource.setAllowDirectWrite(true);
        Assert.assertTrue(aaiResource.isAllowDirectWrite());
    }

    @Test
    public void testGetterSetterAllowDirectRead() {
        aaiResource.setAllowDirectRead(true);
        Assert.assertTrue(aaiResource.isAllowDirectRead());
    }

    @Test
    public void testGetAutoGenUuidFields() {
        Assert.assertNotNull(aaiResource.getAutoGenUuidFields());
    }

}
