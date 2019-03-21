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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.util.genxsd;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.onap.aai.setup.SchemaVersion;

@RunWith(Parameterized.class)
public class PutOperationTest {
    private String useOpId;
    private String xmlRootElementName;
    private String tag;
    private String path;
    private String pathParams;
    private String result;
    private static SchemaVersion v = new SchemaVersion("v14");

    @Parameters
    public static Collection<String[]> testConditions() {
        String inputs[][] = {{"NetworkGenericVnfsGenericVnf", "generic-vnf", "Network",
            "/network/generic-vnfs/generic-vnf/{vnf-id}",
            "        - name: vnf-id\n          in: path\n          description: Unique id of VNF.  This is unique across the graph.\n          required: true\n          type: string\n          example: __VNF-ID__",
            "    put:\n      tags:\n        - Network\n      summary: create or update an existing generic-vnf\n      description: |\n        Create or update an existing generic-vnf.\n        #\n        Note! This PUT method has a corresponding PATCH method that can be used to update just a few of the fields of an existing object, rather than a full object replacement.  An example can be found in the [PATCH section] below\n      operationId: createOrUpdateNetworkGenericVnfsGenericVnf\n      consumes:\n        - application/json\n        - application/xml\n      produces:\n        - application/json\n        - application/xml\n      responses:\n        \"default\":\n          null      parameters:\n        - name: vnf-id\n          in: path\n          description: Unique id of VNF.  This is unique across the graph.\n          required: true\n          type: string\n          example: __VNF-ID__        - name: body\n          in: body\n          description: generic-vnf object that needs to be created or updated. [Valid relationship examples shown here](apidocs/relations/"
                + v.toString()
                + "/NetworkGenericVnfsGenericVnf.json)\n          required: true\n          schema:\n            $ref: \"#/definitions/generic-vnf\"\n"},
            // if ( StringUtils.isEmpty(tag) )
            {"GenericVnf", "generic-vnf", "", "/generic-vnf/{vnf-id}",
                "        - name: vnf-id\n          in: path\n          description: Unique id of VNF.  This is unique across the graph.\n          required: true\n          type: string\n          example: __VNF-ID__",
                ""},
            // Test: if ( !path.endsWith("/relationship") && !path.endsWith("}") )
            {"CloudInfrastructurePserversPserverPInterfaces", "p-interfaces", "CloudInfrastructure",
                "/cloud-infrastructure/pservers/pserver/{hostname}/p-interfaces",
                "        - name: hostname\n          in: path\n          description: Value from executing hostname on the compute node.\n          required: true\n          type: string\n          example: __HOSTNAME__",
                ""},
            // {"","ctag-pool","","","",""},
            // {"","pserver","","","",""},
            // {"","oam-network","","","",""},
            // {"","dvs-switch","","","",""},
            // {"","availability-zone","","","",""}
        };
        return Arrays.asList(inputs);
    }

    public PutOperationTest(String useOpId, String xmlRootElementName, String tag, String path,
        String pathParams, String result) {
        super();
        this.useOpId = useOpId;
        this.xmlRootElementName = xmlRootElementName;
        this.tag = tag;
        this.path = path;
        this.pathParams = pathParams;
        this.result = result;
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

    }

    @Test
    public void testToString() {
        PutOperation put = new PutOperation(useOpId, xmlRootElementName, tag, path, pathParams, v);
        String modResult = put.toString();
        assertThat(modResult, is(this.result));
    }

}
