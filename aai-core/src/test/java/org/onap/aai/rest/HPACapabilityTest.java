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

import com.jayway.jsonpath.JsonPath;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.onap.aai.AAIJunitRunner;
import org.onap.aai.HttpTestUtil;
import org.onap.aai.PayloadUtil;
import org.onap.aai.serialization.engines.QueryStyle;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Disabled
@RunWith(AAIJunitRunner.class)
public class HPACapabilityTest {

    private static Logger logger = LoggerFactory.getLogger(HPACapabilityTest.class);
    private HttpTestUtil httpTestUtil;
    private Map<String, String> templateValuesMap;
    public QueryStyle queryStyle;

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {{QueryStyle.TRAVERSAL}});
    }

    @BeforeEach
    public void setup() {
        httpTestUtil = new HttpTestUtil(queryStyle);
        templateValuesMap = new HashMap<>();
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void testPutHPACapabilitiesInFlavorAndCheckIfDeleteIsSuccessful(QueryStyle queryStyle) throws Exception {

        initHPACapabilityTest(queryStyle);

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
        assertEquals(201, response.getStatus(), "Expected the cloud region to be created");

        response = httpTestUtil.doGet(cloudRegionUri);
        assertEquals(200, response.getStatus(), "Expected the cloud region to be found");
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
        assertEquals(200, tntResponse.getStatus(), "Expected to GET Tenant info from cloud-region");
        String responseStr = tntResponse.getEntity().toString();

        String resourceVersion = JsonPath.read(responseStr, "$.resource-version");

        tntResponse = httpTestUtil.doDelete(tenantUri, resourceVersion);
        assertEquals(204, tntResponse.getStatus(), "Expected to DELETE Tenant info from cloud-region");
    }

    private void deleteVserver(String tenantUri) throws Exception {
        String uri = tenantUri + "/vservers/vserver/" + templateValuesMap.get("vserver-id");

        Response tntResponse = httpTestUtil.doGet(uri);
        assertEquals(200, tntResponse.getStatus(), "Expected to GET Vserver");
        String responseStr = tntResponse.getEntity().toString();

        String resourceVersion = JsonPath.read(responseStr, "$.resource-version");

        tntResponse = httpTestUtil.doDelete(uri, resourceVersion);
        assertEquals(204, tntResponse.getStatus(), "Expected to DELETE Vserver");
    }

    private void deleteFlavor(String cloudRegionUri, String flavorId) throws Exception {
        String flavorUri = cloudRegionUri + "/flavors/flavor/" + flavorId;

        Response response = httpTestUtil.doGet(flavorUri);
        assertEquals(200, response.getStatus(), "Expected to GET Flavors info from cloud-region");
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
        assertEquals(204, response.getStatus(), "Expected to DELETE Flavor info from cloud-region");
    }

    private void deleteHPACapability(String flavorUri, String capabilityId) throws Exception {
        String uri = flavorUri + "/hpa-capabilities/hpa-capability/" + capabilityId;

        Response response = httpTestUtil.doGet(uri);
        assertEquals(200, response.getStatus(), "Expected to GET HPA info from flavors");
        String jsonResponse = response.getEntity().toString();
        System.out.println("#########################HPA Response#########################");
        System.out.println(jsonResponse);
        System.out.println("#########################HPA Response#########################");
        String responseStr = response.getEntity().toString();

        String resourceVersion = JsonPath.read(responseStr, "$.resource-version");

        response = httpTestUtil.doDelete(uri, resourceVersion);
        assertEquals(204, response.getStatus(), "Expected to DELETE HPA info from flavors");
    }

    public void initHPACapabilityTest(QueryStyle queryStyle) {
        this.queryStyle = queryStyle;
    }
}
