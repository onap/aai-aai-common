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

package org.onap.aai.rest;

import static org.junit.Assert.assertEquals;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jayway.jsonpath.JsonPath;

import java.util.*;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.onap.aai.AAIJunitRunner;
import org.onap.aai.HttpTestUtil;
import org.onap.aai.PayloadUtil;
import org.onap.aai.serialization.engines.QueryStyle;
import org.skyscreamer.jsonassert.JSONAssert;

@Ignore
@RunWith(AAIJunitRunner.class)
public class HPACapabilityTest {

    private static Logger logger = LoggerFactory.getLogger(HPACapabilityTest.class);
    private HttpTestUtil httpTestUtil;
    private Map<String, String> templateValuesMap;

    @Parameterized.Parameter(value = 0)
    public QueryStyle queryStyle;

    @Parameterized.Parameters(name = "QueryStyle.{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {{QueryStyle.TRAVERSAL}});
    }

    @Before
    public void setup() {
        httpTestUtil = new HttpTestUtil(queryStyle);
        templateValuesMap = new HashMap<>();
    }

    @Test
    public void testPutHPACapabilitiesInFlavorAndCheckIfDeleteIsSuccessful() throws Exception {

        templateValuesMap.put("cloud-region-id", UUID.randomUUID().toString());
        templateValuesMap.put("cloud-owner", UUID.randomUUID().toString());
        templateValuesMap.put("tenant-id", UUID.randomUUID().toString());
        templateValuesMap.put("vserver-id", UUID.randomUUID().toString());
        templateValuesMap.put("flavor-id1", UUID.randomUUID().toString());
        templateValuesMap.put("flavor-id2", UUID.randomUUID().toString());
        templateValuesMap.put("hpa-capability-id1", UUID.randomUUID().toString());
        templateValuesMap.put("hpa-capability-id2", UUID.randomUUID().toString());
        templateValuesMap.put("hpa-capability-id3", UUID.randomUUID().toString());
        templateValuesMap.put("hpa-capability-id4", UUID.randomUUID().toString());
        templateValuesMap.put("hpa-capability-id5", UUID.randomUUID().toString());
        templateValuesMap.put("hpa-capability-id6", UUID.randomUUID().toString());
        templateValuesMap.put("hpa-capability-id7", UUID.randomUUID().toString());
        templateValuesMap.put("hpa-capability-id8", UUID.randomUUID().toString());

        String cloudRegionPayload = PayloadUtil.getTemplatePayload("hpa.json", templateValuesMap);
        String cloudRegionUri = String.format("/aai/v14/cloud-infrastructure/cloud-regions/cloud-region/%s/%s",
                templateValuesMap.get("cloud-owner"), templateValuesMap.get("cloud-region-id"));

        Response response = httpTestUtil.doPut(cloudRegionUri, cloudRegionPayload);
        assertEquals("Expected the cloud region to be created", 201, response.getStatus());

        response = httpTestUtil.doGet(cloudRegionUri);
        assertEquals("Expected the cloud region to be found", 200, response.getStatus());
        String jsonResponse = response.getEntity().toString();
        System.out.println("#########################jsonResponse#########################");
        System.out.println(jsonResponse);
        System.out.println("#########################jsonResponse#########################");

        JSONAssert.assertEquals(cloudRegionPayload, jsonResponse, false);

        deleteFlavor(cloudRegionUri, templateValuesMap.get("flavor-id1"));
        deleteFlavor(cloudRegionUri, templateValuesMap.get("flavor-id2"));
        deleteTenant(cloudRegionUri);
    }

    private void deleteTenant(String cloudRegionUri) throws Exception {
        String tenantUri = cloudRegionUri + "/tenants/tenant/" + templateValuesMap.get("tenant-id");
        deleteVserver(tenantUri);

        Response tntResponse = httpTestUtil.doGet(tenantUri);
        assertEquals("Expected to GET Tenant info from cloud-region", 200, tntResponse.getStatus());
        String responseStr = tntResponse.getEntity().toString();

        String resourceVersion = JsonPath.read(responseStr, "$.resource-version");

        tntResponse = httpTestUtil.doDelete(tenantUri, resourceVersion);
        assertEquals("Expected to DELETE Tenant info from cloud-region", 204, tntResponse.getStatus());
    }

    private void deleteVserver(String tenantUri) throws Exception {
        String uri = tenantUri + "/vservers/vserver/" + templateValuesMap.get("vserver-id");

        Response tntResponse = httpTestUtil.doGet(uri);
        assertEquals("Expected to GET Vserver", 200, tntResponse.getStatus());
        String responseStr = tntResponse.getEntity().toString();

        String resourceVersion = JsonPath.read(responseStr, "$.resource-version");

        tntResponse = httpTestUtil.doDelete(uri, resourceVersion);
        assertEquals("Expected to DELETE Vserver", 204, tntResponse.getStatus());
    }

    private void deleteFlavor(String cloudRegionUri, String flavorId) throws Exception {
        String flavorUri = cloudRegionUri + "/flavors/flavor/" + flavorId;

        Response response = httpTestUtil.doGet(flavorUri);
        assertEquals("Expected to GET Flavors info from cloud-region", 200, response.getStatus());
        String jsonResponse = response.getEntity().toString();
        System.out.println("#########################Flavor Response#########################");
        System.out.println(jsonResponse);
        System.out.println("#########################Flavor Response#########################");
        String responseStr = response.getEntity().toString();

        List<String> capabilityIds =
                JsonPath.read(responseStr, "$.hpa-capabilities.hpa-capability[*].hpa-capability-id");
        for (String capabilityId : capabilityIds) {
            deleteHPACapability(flavorUri, capabilityId);
        }

        String resourceVersion = JsonPath.read(responseStr, "$.resource-version");
        response = httpTestUtil.doDelete(flavorUri, resourceVersion);
        assertEquals("Expected to DELETE Flavor info from cloud-region", 204, response.getStatus());
    }

    private void deleteHPACapability(String flavorUri, String capabilityId) throws Exception {
        String uri = flavorUri + "/hpa-capabilities/hpa-capability/" + capabilityId;

        Response response = httpTestUtil.doGet(uri);
        assertEquals("Expected to GET HPA info from flavors", 200, response.getStatus());
        String jsonResponse = response.getEntity().toString();
        System.out.println("#########################HPA Response#########################");
        System.out.println(jsonResponse);
        System.out.println("#########################HPA Response#########################");
        String responseStr = response.getEntity().toString();

        String resourceVersion = JsonPath.read(responseStr, "$.resource-version");

        response = httpTestUtil.doDelete(uri, resourceVersion);
        assertEquals("Expected to DELETE HPA info from flavors", 204, response.getStatus());
    }
}
