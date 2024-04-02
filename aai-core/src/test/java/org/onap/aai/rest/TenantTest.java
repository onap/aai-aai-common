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

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.onap.aai.AAISetup;
import org.onap.aai.HttpTestUtil;
import org.onap.aai.PayloadUtil;
import org.onap.aai.serialization.engines.QueryStyle;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

public class TenantTest extends AAISetup {

    private HttpTestUtil httpTestUtil;
    private Map<String, String> templateValuesMap;
    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();
    public QueryStyle queryStyle;

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {{QueryStyle.TRAVERSAL}, {QueryStyle.TRAVERSAL_URI}});
    }

    @BeforeEach
    public void setUp() {
        httpTestUtil = new HttpTestUtil(queryStyle);
        templateValuesMap = new HashMap<>();
    }

    @Disabled("Test is failing due to the deletion of node with children not correct will be fixed soon")
    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void testCloudRegionTenantDeleteSuccessWithoutDeletingVserver(QueryStyle queryStyle) throws Exception {

        initTenantTest(queryStyle);

        templateValuesMap.put("cloud-region-id", UUID.randomUUID().toString());
        templateValuesMap.put("cloud-owner", UUID.randomUUID().toString());
        templateValuesMap.put("tenant-id", UUID.randomUUID().toString());
        templateValuesMap.put("vserver-id", UUID.randomUUID().toString());

        String cloudRegionPayload = PayloadUtil.getTemplatePayload("cloud-region.json", templateValuesMap);
        String cloudRegionUri = String.format("/aai/v12/cloud-infrastructure/cloud-regions/cloud-region/%s/%s",
                templateValuesMap.get("cloud-owner"), templateValuesMap.get("cloud-region-id"));

        String tenantUri = cloudRegionUri + "/tenants/tenant/" + templateValuesMap.get("tenant-id");
        String tenantPayload = PayloadUtil.getTemplatePayload("tenant.json", templateValuesMap);

        Response response = httpTestUtil.doPut(cloudRegionUri, cloudRegionPayload);
        assertEquals(201, response.getStatus(), "Expected the cloud region to be created");

        response = httpTestUtil.doGet(tenantUri);
        assertEquals(200, response.getStatus(), "Expected the cloud region to be created");
        String responseStr = response.getEntity().toString();

        JSONAssert.assertEquals(tenantPayload, responseStr, false);
        String resourceVersion = JsonPath.read(responseStr, "$.resource-version");

        response = httpTestUtil.doDelete(tenantUri, resourceVersion);
        assertEquals(204, response.getStatus(), "Expected the cloud region to be created");

        response = httpTestUtil.doGet(cloudRegionUri);
        assertEquals(200, response.getStatus(), "Expected the cloud region to be created");
        responseStr = response.getEntity().toString();
        resourceVersion = JsonPath.read(responseStr, "$.resource-version");

        response = httpTestUtil.doDelete(cloudRegionUri, resourceVersion);
        assertEquals(204, response.getStatus(), "Expected the cloud region to be created");
    }

    public void initTenantTest(QueryStyle queryStyle) {
        this.queryStyle = queryStyle;
    }
}
