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
package org.onap.aai.introspection.generator;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.AAISetup;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.*;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
public class CreateExampleTest extends AAISetup {
    
    private  static CreateExample createExample;
    private  Loader loader;

    private static boolean classLoaded = false;
    
    
    @BeforeClass
    public static void setUp() {
        
        
    }
    
    
    @Before
    public void createLoaderVersion(){
        if(!classLoaded){
            loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getAppRootVersion());
            createExample = new CreateExample(loader, "edge-prop-names");
            classLoaded = false;
        }
    }
    
    @Test
    public void testGetExampleObject() throws AAIException {
        Introspector introspector = loader.introspectorFromName("edge-prop-names");
        Introspector res = createExample.getExampleObject();
        assertEquals(introspector.getName(), res.getName());
    }

    @Test
    public void testProcessPrimitiveString() throws AAIUnknownObjectException {
        String propName = "direction";
        Introspector introspector = loader.introspectorFromName("edge-prop-names");
        createExample.processPrimitive(propName, introspector);
    }
    
    @Test
    public void testProcessPrimitiveLong() throws AAIUnknownObjectException {
        String propName = "vlan-id-inner";
        Introspector introspector = loader.introspectorFromName("ctag-assignment");
        createExample.processPrimitive(propName, introspector);
    }

    @Test
    public void testProcessPrimitiveBoolean() throws AAIUnknownObjectException {
        String propName = "in-maint";
        Introspector introspector = loader.introspectorFromName("vserver");
        createExample.processPrimitive(propName, introspector);
    }
    
    @Test
    public void testProcessPrimitiveInteger() throws AAIUnknownObjectException {
        String propName = "module-index";
        Introspector introspector = loader.introspectorFromName("vf-module");
        createExample.processPrimitive(propName, introspector);
    }
    
    @Test
    public void testProcessPrimitiveList() throws AAIUnknownObjectException {
        String propName = "ipaddress-v4-vig";
        Introspector introspector = loader.introspectorFromName("vig-server");
        createExample.processPrimitiveList(propName, introspector);
    }

    @Test
    public void testProcessComplexObj() {
        // empty method
        Introspector introspector = Mockito.mock(Introspector.class);
        createExample.processComplexObj(introspector);
    }

    @Test
    public void testModifyComplexList() {
        // empty method
        List<Introspector> introList = new ArrayList<Introspector>();
        List<Object> objList = new ArrayList<Object>();
        Introspector introspector = Mockito.mock(Introspector.class);
        createExample.modifyComplexList(introList, objList, introspector, introspector);
    }

    @Test
    public void testCreateComplexObjIfNull() {
        boolean res = createExample.createComplexObjIfNull();
        assertTrue(res);
    }

    @Test
    public void testCreateComplexListSize() {
        Introspector introspector = Mockito.mock(Introspector.class);
        int res = createExample.createComplexListSize(introspector, introspector);
        assertEquals(1, res);
    }

}
