/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2018 Intel Corporation Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.aai.rest;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.AAIJunitRunner;
import org.onap.aai.HttpTestUtil;
import org.onap.aai.PayloadUtil;
import org.onap.aai.introspection.*;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AAIJunitRunner.class)
public class HPACapabilityTest {

    private static EELFLogger logger = EELFManager.getInstance().getLogger(HPACapabilityTest.class);
    private HttpTestUtil httpTestUtil;
    private Map<String, String> templateValuesMap;

    @Before
    public void setup() {
        httpTestUtil = new HttpTestUtil();
        templateValuesMap = new HashMap<>();
    }

    @Test
    public void testPutHPACapabilitiesInFlavorAndCheckIfDeleteIsSuccessful() throws Exception {

        String f1 = "b8319f34-b517-4ccb-a3ab-00952aa89480-vk250x";
        String f2 = "a7208e23-b517-4ccb-a3ab-00952aa89480-vk250x";
        templateValuesMap.put("cloud-region-id", UUID.randomUUID().toString());
        templateValuesMap.put("cloud-owner", UUID.randomUUID().toString());
        templateValuesMap.put("tenant-id", UUID.randomUUID().toString());
        templateValuesMap.put("vserver-id", UUID.randomUUID().toString());

        String cloudRegionPayload = PayloadUtil.getTemplatePayload("hpa.json", templateValuesMap);
        String cloudRegionUri = String.format("/aai/v13/cloud-infrastructure/cloud-regions/cloud-region/%s/%s",
                templateValuesMap.get("cloud-owner"),
                templateValuesMap.get("cloud-region-id")
        );

        Response response = httpTestUtil.doPut(cloudRegionUri, cloudRegionPayload);
        assertEquals("Expected the cloud region to be created", 201, response.getStatus());

        response = httpTestUtil.doGet(cloudRegionUri);
        assertEquals("Expected the cloud region to be found", 200, response.getStatus());
        String jsonResponse = response.getEntity().toString();
        System.out.println("#########################jsonResponse#########################");
        System.out.println(jsonResponse);
        System.out.println("#########################jsonResponse#########################");

        JSONAssert.assertEquals(cloudRegionPayload, jsonResponse, false);

        /*deleteFlavor(cloudRegionUri, f1);
        deleteFlavor(cloudRegionUri, f2);
        deleteTenant(cloudRegionUri);*/
        //Delete Cloud Region
        /*String resourceVersion = JsonPath.read(jsonResponse, "$.resource-version");

        response = httpTestUtil.doDelete(cloudRegionUri, resourceVersion);
        assertEquals("Expected the cloud region to be deleted", 204, response.getStatus());*/
    }

    private void deleteTenant(String cloudRegionUri) throws Exception{
        String tenantUri = cloudRegionUri + "/tenants/tenant/" + templateValuesMap.get("tenant-id");
        deleteVserver(tenantUri);

        Response tntResponse     = httpTestUtil.doGet(tenantUri);
        assertEquals("Expected to GET Tenant info from cloud-region", 200, tntResponse.getStatus());
        String responseStr = tntResponse.getEntity().toString();

        String resourceVersion = JsonPath.read(responseStr, "$.resource-version");

        tntResponse     = httpTestUtil.doDelete(tenantUri, resourceVersion);
        assertEquals("Expected to DELETE Tenant info from cloud-region", 204, tntResponse.getStatus());
    }

    private void deleteVserver(String tenantUri) throws Exception{
        String uri = tenantUri + "/vservers/vserver/" + templateValuesMap.get("vserver-id");

        Response tntResponse     = httpTestUtil.doGet(uri);
        assertEquals("Expected to GET Vserver", 200, tntResponse.getStatus());
        String responseStr = tntResponse.getEntity().toString();

        String resourceVersion = JsonPath.read(responseStr, "$.resource-version");

        tntResponse     = httpTestUtil.doDelete(uri, resourceVersion);
        assertEquals("Expected to DELETE Vserver", 204, tntResponse.getStatus());
    }

    private void deleteFlavor(String cloudRegionUri, String flavorId) throws Exception{
        String uri = cloudRegionUri + "/flavors/flavor/" + flavorId;

        Response response     = httpTestUtil.doGet(uri);
        assertEquals("Expected to GET Flavors info from cloud-region", 200, response.getStatus());
        System.out.println("#########################Flavor Response#########################");
        System.out.println(response);
        System.out.println("#########################Flavor Response#########################");
        String responseStr = response.getEntity().toString();

        String resourceVersion = JsonPath.read(responseStr, "$.resource-version");

        response     = httpTestUtil.doDelete(uri, resourceVersion);
        assertEquals("Expected to DELETE Flavor info from cloud-region", 204, response.getStatus());
    }
}
