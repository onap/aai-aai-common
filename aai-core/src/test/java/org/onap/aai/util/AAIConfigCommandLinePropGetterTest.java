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
package org.onap.aai.util;

import static org.junit.Assert.assertEquals;

import java.security.Permission;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.aai.AAISetup;

public class AAIConfigCommandLinePropGetterTest extends AAISetup {

    private SecurityManager m;
    private TestSecurityManager sm;

    @Before
    public void setUp() 
    {
        m = System.getSecurityManager();
        sm = new TestSecurityManager();
        System.setSecurityManager(sm);
    }

    @After
    public void tearDown()
    {   
        System.setSecurityManager(m);
    }

    @Test
    public void testMainNoArgs() {
        try {
            AAIConfigCommandLinePropGetter.main(new String[] {});
        } catch (SecurityException se) {
            // assert main method ends with System.exit(0)
            assertEquals("0", se.getMessage());
        }
    }
    
    @Test
    public void testMainReadProp() {
        try {
            AAIConfigCommandLinePropGetter.main(new String[] {"aai.primary.filetransfer.serverlist"});
        } catch (SecurityException se) {
            // assert main method ends with System.exit(0)
            assertEquals("0", se.getMessage());
        }
    }
    
    @Test
    public void testMainOneArg() {
        try {
            AAIConfigCommandLinePropGetter.main(new String[] {"one"});
        } catch (SecurityException se) {
            // assert main method ends with System.exit(0)
            assertEquals("0", se.getMessage());
        }
    }
    
    @Test
    public void testMainMoreThanOneArg() {
        try {
            AAIConfigCommandLinePropGetter.main(new String[] {"one", "two"});
        } catch (SecurityException se) {
            // assert main method ends with System.exit(0)
            assertEquals("0", se.getMessage());
        }
    }
}

class TestSecurityManager extends SecurityManager {
    @Override 
    public void checkPermission(Permission permission) {            
        if ("exitVM".equals(permission.getName())) 
        {
            throw new SecurityException("System.exit attempted and blocked.");
        }
    }
    @Override 
    public void checkExit(int status) {
        throw new SecurityException(Integer.toString(status));
    }
}
