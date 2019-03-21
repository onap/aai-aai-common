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
import java.util.Vector;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Parameterized.class)
public class NodeGetOperationTest {
    private static final Logger logger = LoggerFactory.getLogger("NodeGetOperationTest.class");
    private String useOpId;
    private String xmlRootElementName;
    private String tag;
    private String path;
    private String pathParams;
    private String result;

    @Parameters
    public static Collection<String[]> testConditions() {
        String inputs[][] = {{"NetworkGenericVnfsGenericVnf", "generic-vnf", "Network",
            "/network/generic-vnfs/generic-vnf/{vnf-id}",
            "        - name: vnf-id\n          in: path\n          description: Unique id of VNF.  This is unique across the graph.\n          required: true\n          type: string\n          example: __VNF-ID__",
            "  /nodes/generic-vnfs/generic-vnf/{vnf-id}:\n    get:\n      tags:\n        - Operations\n      summary: returns generic-vnf\n      description: returns generic-vnf\n      operationId: getNetworkGenericVnfsGenericVnf\n      produces:\n        - application/json\n        - application/xml\n      responses:\n        \"200\":\n          description: successful operation\n          schema:\n              $ref: \"#/definitions/generic-vnf\"\n        \"default\":\n          null\n      parameters:\n        - name: vnf-id\n          in: path\n          description: Unique id of VNF.  This is unique across the graph.\n          required: true\n          type: string\n          example: __VNF-ID__"},
            {"GenericVnf", "generic-vnf", "", "/Network/generic-vnf/{vnf-id}",
                "        - name: vnf-id\n          in: path\n          description: Unique id of VNF.  This is unique across the graph.\n          required: true\n          type: string\n          example: __VNF-ID__",
                ""},
            {"CloudInfrastructurePserversPserverPInterfaces", "p-interfaces", "CloudInfrastructure",
                "/cloud-infrastructure/pservers/pserver/{hostname}/p-interfaces",
                "        - name: hostname\n          in: path\n          description: Value from executing hostname on the compute node.\n          required: true\n          type: string\n          example: __HOSTNAME__",
                "  /nodes/p-interfaces?parameter=value[&parameter2=value2]:\n    get:\n      tags:\n        - Operations\n      summary: returns p-interfaces\n      description: returns p-interfaces\n      operationId: getCloudInfrastructurePserversPserverPInterfaces\n      produces:\n        - application/json\n        - application/xml\n      responses:\n        \"200\":\n          description: successful operation\n          schema:\n              $ref: \"#/definitions/p-interfaces\"\n        \"default\":\n          null\n      parameters:\n        - name: hostname\n          in: path\n          description: Value from executing hostname on the compute node.\n          required: true\n          type: string\n          example: __HOSTNAME__\n        - name: interface-name\n          in: query\n          description:\n          required: false\n          type: string        - name: prov-status\n          in: query\n          description:\n          required: false\n          type: string"},
            // {"","ctag-pool","","","",""},
            // {"","pserver","","","",""},
            // {"","oam-network","","","",""},
            // {"","dvs-switch","","","",""},
            // {"","availability-zone","","","",""}
        };
        return Arrays.asList(inputs);
    }

    public NodeGetOperationTest(String useOpId, String xmlRootElementName, String tag, String path,
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
        String container = "p-interfaces";
        String queryProps[] = {
            "        - name: interface-name\n          in: query\n          description:\n          required: false\n          type: string",
            "        - name: prov-status\n          in: query\n          description:\n          required: false\n          type: string"};
        Vector<String> containerProps = new Vector<String>();
        for (String prop : queryProps) {
            containerProps.add(prop);
        }
        NodeGetOperation.addContainerProps(container, containerProps);
    }

    @Test
    public void testAddContainerProps() {
        String container = this.xmlRootElementName;
        String prop = "        - name: " + container
            + "\n          in: query\n          description:\n          required: false\n          type: string";
        Vector<String> queryProps = new Vector<String>();
        queryProps.add(prop);
        String props = null;
        for (String p : queryProps) {
            props += "qProp=" + p + "\n";
            // logger.debug("qProp="+p);
        }
        // logger.debug("Done="+this.xmlRootElementName);
        NodeGetOperation.addContainerProps(container, queryProps);
        assertThat(props + "Done=" + this.xmlRootElementName,
            NodeGetOperation.containers.get(container).get(0), is(prop));
    }

    @Test
    public void testToString() {
        NodeGetOperation get =
            new NodeGetOperation(useOpId, xmlRootElementName, tag, path, pathParams);
        String modResult = get.toString();
        assertThat(modResult, is(this.result));
    }

}
