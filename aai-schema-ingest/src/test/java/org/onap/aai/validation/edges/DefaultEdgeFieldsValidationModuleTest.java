/** 
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-18 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.validation.edges;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.edges.enums.EdgeField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DefaultEdgeFieldsValidationModule.class})
@SpringBootTest
public class DefaultEdgeFieldsValidationModuleTest {
    @Autowired
    EdgeFieldsValidationModule validator;
    
    @Test
    public void test() {
        Map<String, String> test = new HashMap<>();
        for (EdgeField f : EdgeField.values()) {
            test.put(f.toString(), "test");
        }
        assertTrue("".equals(validator.verifyFields(test)));
        
        test.remove(EdgeField.DESCRIPTION.toString());
        assertTrue("".equals(validator.verifyFields(test))); //bc description is optional
        
        test.remove(EdgeField.CONTAINS.toString());
        assertTrue(validator.verifyFields(test).contains("missing required fields: contains-other-v"));
        
        test.remove(EdgeField.FROM.toString());
        assertTrue(validator.verifyFields(test).contains("missing required fields: from contains-other-v"));
    }

}
