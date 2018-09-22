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
package org.onap.aai.auth;

import org.junit.Test;
import org.onap.aai.AAISetup;

import static org.junit.Assert.assertEquals;

public class AAIUserTest extends AAISetup {

    @Test
    public void testIsAuth() {
        AAIUser usr = new AAIUser("testUser");
        usr.addRole("testRole");
        usr.setUserAccess("auth", "GET");
        usr.setUserAccess("auth", "PUT");
        usr.setUserAccess("authentication", "PUT", "GET", "POST");
        
        assertEquals(true, usr.hasAccess("auth", "GET"));
        assertEquals(true, usr.hasAccess("auth", "PUT"));
        assertEquals(true, usr.hasAccess("authentication", "POST"));
    }
    
    @Test
    public void testIsNotAuth() {
        AAIUser usr = new AAIUser("testUser");
        usr.addRole("testRole");
    
        assertEquals(false, usr.hasAccess("auth", "GET"));
        
        usr.setUserAccess("auth", "GET");
        assertEquals(false, usr.hasAccess("auth", "PUT"));
    }

}
