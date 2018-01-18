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
package org.onap.aai.rest;

import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.AAIJunitRunner;
import org.onap.aai.HttpTestUtil;
import org.onap.aai.PayloadUtil;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(AAIJunitRunner.class)
public class TenantTest {

    private HttpTestUtil httpTestUtil;

    private Map<String, String> templateValuesMap;

    @Before
    public void setup(){
        httpTestUtil      = new HttpTestUtil();
        templateValuesMap = new HashMap<>();
    }

    @Ignore("Test is failing due to the deletion of node with children not correct will be fixed soon")
    @Test
    public void testCloudRegionTenantDeleteSuccessWithoutDeletingVserver() throws Exception {

        templateValuesMap.put("cloud-region-id", UUID.randomUUID().toString());
        templateValuesMap.put("cloud-owner", UUID.randomUUID().toString());
        templateValuesMap.put("tenant-id", UUID.randomUUID().toString());
        templateValuesMap.put("vserver-id", UUID.randomUUID().toString());

        String cloudRegionPayload = PayloadUtil.getTemplatePayload("cloud-region.json", templateValuesMap);
        String cloudRegionUri = String.format("/aai/v12/cloud-infrastructure/cloud-regions/cloud-region/%s/%s",
                templateValuesMap.get("cloud-owner"),
                templateValuesMap.get("cloud-region-id")
        );

        String tenantUri = cloudRegionUri + "/tenants/tenant/" + templateValuesMap.get("tenant-id");
        String tenantPayload = PayloadUtil.getTemplatePayload("tenant.json", templateValuesMap);

        Response response     = httpTestUtil.doPut(cloudRegionUri, cloudRegionPayload);
        assertEquals("Expected the cloud region to be created", 201, response.getStatus());

        response     = httpTestUtil.doGet(tenantUri);
        assertEquals("Expected the cloud region to be created", 200, response.getStatus());
        String responseStr = response.getEntity().toString();

        JSONAssert.assertEquals(tenantPayload, responseStr, false);
        String resourceVersion = JsonPath.read(responseStr, "$.resource-version");

        response     = httpTestUtil.doDelete(tenantUri, resourceVersion);
        assertEquals("Expected the cloud region to be created", 204, response.getStatus());

        response     = httpTestUtil.doGet(cloudRegionUri);
        assertEquals("Expected the cloud region to be created", 200, response.getStatus());
        responseStr = response.getEntity().toString();
        resourceVersion = JsonPath.read(responseStr, "$.resource-version");

        response     = httpTestUtil.doDelete(cloudRegionUri, resourceVersion);
        assertEquals("Expected the cloud region to be created", 204, response.getStatus());
    }
}
