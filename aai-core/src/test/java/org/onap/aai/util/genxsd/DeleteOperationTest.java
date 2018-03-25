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
package org.onap.aai.util.genxsd;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class DeleteOperationTest {
	private String useOpId;
	private String xmlRootElementName;
	private String tag;
	private String path;
	private String pathParams;
	private String result;

	@Parameters
	public static Collection<String []> testConditions() {
		String inputs [][] = {
		{"NetworkGenericVnfsGenericVnf","generic-vnf","Network","/network/generic-vnfs/generic-vnf/{vnf-id}","        - name: vnf-id\n          in: path\n          description: Unique id of VNF.  This is unique across the graph.\n          required: true\n          type: string\n          example: __VNF-ID__\n","    delete:\n      tags:\n        - Network\n      summary: delete an existing generic-vnf\n      description: delete an existing generic-vnf\n      operationId: deleteNetworkGenericVnfsGenericVnf\n      consumes:\n        - application/json\n        - application/xml\n      produces:\n        - application/json\n        - application/xml\n      responses:\n        \"default\":\n          null      parameters:\n        - name: vnf-id\n          in: path\n          description: Unique id of VNF.  This is unique across the graph.\n          required: true\n          type: string\n          example: __VNF-ID__\n        - name: resource-version\n          in: query\n          description: resource-version for concurrency\n          required: true\n          type: string\n"},
		{"CloudInfrastructureCloudRegionsCloudRegionTenantsTenantVserversVserver","vserver","CloudInfrastructure","/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/tenants/tenant/{tenant-id}/vservers/vserver/{vserver-id}","        - name: cloud-owner\n          in: path\n          description: Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname\n          required: true\n          type: string\n          example: __CLOUD-OWNER__\n        - name: cloud-region-id\n          in: path\n          description: Identifier used by the vendor for the region. Second part of composite key\n          required: true\n          type: string\n          example: __CLOUD-REGION-ID__\n        - name: tenant-id\n          in: path\n          description: Unique id relative to the cloud-region.\n          required: true\n          type: string\n          example: __TENANT-ID__\n        - name: vserver-id\n          in: path\n          description: Unique identifier for this vserver relative to its tenant\n          required: true\n          type: string\n          example: __VSERVER-ID__\n","    delete:\n      tags:\n        - CloudInfrastructure\n      summary: delete an existing vserver\n      description: delete an existing vserver\n      operationId: deleteCloudInfrastructureCloudRegionsCloudRegionTenantsTenantVserversVserver\n      consumes:\n        - application/json\n        - application/xml\n      produces:\n        - application/json\n        - application/xml\n      responses:\n        \"default\":\n          null      parameters:\n        - name: cloud-owner\n          in: path\n          description: Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname\n          required: true\n          type: string\n          example: __CLOUD-OWNER__\n        - name: cloud-region-id\n          in: path\n          description: Identifier used by the vendor for the region. Second part of composite key\n          required: true\n          type: string\n          example: __CLOUD-REGION-ID__\n        - name: tenant-id\n          in: path\n          description: Unique id relative to the cloud-region.\n          required: true\n          type: string\n          example: __TENANT-ID__\n        - name: vserver-id\n          in: path\n          description: Unique identifier for this vserver relative to its tenant\n          required: true\n          type: string\n          example: __VSERVER-ID__\n        - name: resource-version\n          in: query\n          description: resource-version for concurrency\n          required: true\n          type: string\n"},
//		if ( StringUtils.isEmpty(tag) )
		{"GenericVnf","generic-vnf","","/generic-vnf/{vnf-id}","        - name: vnf-id\n          in: path\n          description: Unique id of VNF.  This is unique across the graph.\n          required: true\n          type: string\n          example: __VNF-ID__\n",""},
//		Test: if ( !path.endsWith("/relationship")  &&  !path.endsWith("}") )
		{"CloudInfrastructurePserversPserverPInterfaces","p-interfaces","CloudInfrastructure","/cloud-infrastructure/pservers/pserver/{hostname}/p-interfaces","        - name: hostname\n          in: path\n          description: Value from executing hostname on the compute node.\n          required: true\n          type: string\n          example: __HOSTNAME__",""},
//		{"","ctag-pool","","","",""},
//		{"","pserver","","","",""},
//		{"","oam-network","","","",""},
//		{"","dvs-switch","","","",""},
//		{"","availability-zone","","","",""}
		};
		return Arrays.asList(inputs);
	}
	
	public DeleteOperationTest(String useOpId, String xmlRootElementName, String tag, String path, String pathParams, String result) {
		super();
		this.useOpId = useOpId;
		this.xmlRootElementName = xmlRootElementName;
		this.tag = tag;
		this.path = path;
		this.pathParams=pathParams;
		this.result = result;
	}
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Test
	public void testToString() {
		DeleteOperation delete = new DeleteOperation(useOpId, xmlRootElementName, tag, path,  pathParams);
		String modResult = delete.toString();
		assertThat(modResult, is(this.result));
	}

}
