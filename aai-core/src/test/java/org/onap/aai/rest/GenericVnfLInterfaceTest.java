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

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.AAIJunitRunner;
import org.onap.aai.HttpTestUtil;
import org.onap.aai.PayloadUtil;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(AAIJunitRunner.class)
public class GenericVnfLInterfaceTest {

    private HttpTestUtil httpTestUtil;

    @Before
    public void setUp(){
        httpTestUtil = new HttpTestUtil();
    }

    @Test
    public void testPutTwoLInterfacesToGenericVnf() throws Exception {

        Map<String, String> templateValueMap = new HashMap<>();
        templateValueMap.put("ip-address", "ipv1");

        String resource = PayloadUtil.getTemplatePayload("generic-vnf-with-lag-interface.json", templateValueMap);
        Response response = httpTestUtil.doPut("/aai/v12/network/generic-vnfs/generic-vnf/vnf1", resource);
        assertEquals("Expecting the generic vnf to be created", 201, response.getStatus());

        response = httpTestUtil.doGet("/aai/v12/network/generic-vnfs/generic-vnf/vnf1");
        assertEquals("Expecting the generic vnf to be updated", 200, response.getStatus());

        resource = response.getEntity().toString().replaceAll("ipv1\",\"resource-version\":\"\\d+\"", "ipv2\"");
        response = httpTestUtil.doPut("/aai/v12/network/generic-vnfs/generic-vnf/vnf1", resource);
        assertEquals("Expecting the generic vnf to be updated", 200, response.getStatus());

        response = httpTestUtil.doGet("/aai/v12/network/generic-vnfs/generic-vnf/vnf1");
        assertEquals("Expecting the generic vnf to be updated", 200, response.getStatus());

        String expected = PayloadUtil.getExpectedPayload("generic-vnf-with-lag-interface.json");
        JSONAssert.assertEquals(expected, response.getEntity().toString(), false);

        JSONObject jsonObject = new JSONObject(response.getEntity().toString());
        String resourceVersion = (String) jsonObject.get("resource-version");

        response = httpTestUtil.doDelete("/aai/v12/network/generic-vnfs/generic-vnf/vnf1", resourceVersion);
        assertEquals("Expecting the generic vnf to be deleted", 204, response.getStatus());
    }
}
