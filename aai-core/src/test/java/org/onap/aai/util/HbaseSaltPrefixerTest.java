/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai.util;

import static org.junit.Assert.*;


import org.junit.Test;


public class HbaseSaltPrefixerTest {
	
	HbaseSaltPrefixer obj = HbaseSaltPrefixer.getInstance();
	
	//Sample key value used for testing
	String key = "foo1234";
	
	String result = obj.prependSalt(key);
	
	@Test
	public void classInstantiateCheck() {
		try {
				assertNotNull("Created class Object is not null", obj);
    		}
    	 catch(Exception e) {
    		 fail();
    	 }
    }
	
	@Test
	public void prependSaltResult_NullCheck () {
		try {
				assertNotNull("result is not null", result);
    		}
    	 catch(NullPointerException e) {
    		 fail();
    	 }
	 }
	
	@Test
	public void prependSaltResult_PrefixSuccessChecks () {
		try {
				// Ensure that '-' has been prepended
				assertFalse(result.startsWith("-")); 
				// Ensure that *something* has been prepended
				assertFalse(result.equalsIgnoreCase(key));
    	 	}
    	 catch (Exception e) {
			fail();
		}
	}
	
}
