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
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.onap.aai.AAISetup;
import org.onap.aai.HttpTestUtil;
import org.onap.aai.PayloadUtil;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.serialization.engines.QueryStyle;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class VipAddressListTest extends AAISetup {

    private HttpTestUtil httpTestUtil;

    @Parameterized.Parameter(value = 0)
    public QueryStyle queryStyle;

    @Parameterized.Parameters(name = "QueryStyle.{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {QueryStyle.TRAVERSAL_URI}
        });
    }

    @Before
    public void setUp(){
        httpTestUtil = new HttpTestUtil(queryStyle);
    }

    @Test
    public void testPutWithAllCloudRegionChildrenNodesAndCheckIfDeleteIsSuccessful() throws IOException, AAIException {

        String cloudRegionPayload = PayloadUtil.getResourcePayload("cloud-region.json");
        String cloudRegionUri = "/aai/v14/cloud-infrastructure/cloud-regions/cloud-region/cloud-region-owner-with-vip-ipv4/cloud-region-id-with-vip-ipv4";

        Response response     = httpTestUtil.doPut(cloudRegionUri, cloudRegionPayload);
        assertEquals("Expected the cloud region to be created", 201, response.getStatus());

        response = httpTestUtil.doGet(cloudRegionUri);
        assertEquals("Expected the cloud region to be found", 200, response.getStatus());
        String jsonResponse = response.getEntity().toString();

        JSONAssert.assertEquals(cloudRegionPayload, jsonResponse, false);

        String vipIpv4Uri = cloudRegionUri + "/vip-ipv4-address-list/vip-ipv4-address-list-1";
        String vipIpv4Payload = PayloadUtil.getResourcePayload("vip-ipv4-address-list.json");

        response     = httpTestUtil.doPut(vipIpv4Uri, vipIpv4Payload);
        assertEquals("Expected the ipv4 address list to be created", 201, response.getStatus());

        response = httpTestUtil.doGet(vipIpv4Uri);
        assertEquals("Expected the ipv4 address list to be found", 200, response.getStatus());

        jsonResponse = response.getEntity().toString();
        String resourceVersion = JsonPath.read(jsonResponse, "$.resource-version");

        response = httpTestUtil.doDelete(vipIpv4Uri, resourceVersion);
        assertEquals("Expected the ipv4 address list to be deleted", 204, response.getStatus());

        response = httpTestUtil.doGet(cloudRegionUri);
        jsonResponse = response.getEntity().toString();
        resourceVersion = JsonPath.read(jsonResponse, "$.resource-version");

        response = httpTestUtil.doDelete(cloudRegionUri, resourceVersion);
        assertEquals("Expected the cloud region to be deleted", 204, response.getStatus());
    }
}
