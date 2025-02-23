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

package org.onap.aai.setup;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@Disabled("Support of schema ingest via context configuration with prop file is removed as it won't work with spring boot 2")
@SpringJUnitConfig(locations = {"classpath:forWiringTests/testUsingPropFileContext.xml"})
public class SchemaLocationsBeanXMLSetterWithPropFileTest {
    @Autowired
    SchemaLocationsBean bean;

    @Test
    public void test() {
        assertNotNull(bean);
        assertTrue("imatest".equals(bean.getSchemaConfigLocation()));
        assertTrue("andIMalittleteapot".equals(bean.getNodeDirectory()));
        assertTrue("meh".equals(bean.getEdgeDirectory()));
    }
}
