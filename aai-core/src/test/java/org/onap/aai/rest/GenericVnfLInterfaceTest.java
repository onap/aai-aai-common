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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.onap.aai.AAISetup;
import org.onap.aai.HttpTestUtil;
import org.onap.aai.PayloadUtil;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.serialization.engines.QueryStyle;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class GenericVnfLInterfaceTest extends AAISetup {

    private HttpTestUtil httpTestUtil;
    public QueryStyle queryStyle;

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {{QueryStyle.TRAVERSAL}, {QueryStyle.TRAVERSAL_URI}});
    }

    @BeforeEach
    public void setUp() {
        httpTestUtil = new HttpTestUtil(queryStyle);
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void testPutTwoLInterfacesToGenericVnf(QueryStyle queryStyle) throws Exception {

        initGenericVnfLInterfaceTest(queryStyle);

        Map<String, String> templateValueMap = new HashMap<>();
        templateValueMap.put("ip-address", "ipv1");

        String resource = PayloadUtil.getTemplatePayload("generic-vnf-with-lag-interface.json", templateValueMap);
        Response response = httpTestUtil.doPut("/aai/v12/network/generic-vnfs/generic-vnf/vnf1", resource);
        assertEquals(201, response.getStatus(), "Expecting the generic vnf to be created");

        response = httpTestUtil.doGet("/aai/v12/network/generic-vnfs/generic-vnf/vnf1");
        assertEquals(200, response.getStatus(), "Expecting the generic vnf to be updated");

        resource = response.getEntity().toString();
        resource = resource.replaceAll("ipv1\",\"resource-version\":\"\\d+\"", "ipv2\"");
        response = httpTestUtil.doPut("/aai/v12/network/generic-vnfs/generic-vnf/vnf1", resource);
        assertEquals(200, response.getStatus(), "Expecting the generic vnf to be updated");

    }

    @AfterEach
    public void tearDown() throws IOException, AAIException {
        Response response = httpTestUtil.doGet("/aai/v12/network/generic-vnfs/generic-vnf/vnf1");
        assertEquals(200, response.getStatus(), "Expecting the generic vnf to be updated");

        String expected = PayloadUtil.getExpectedPayload("generic-vnf-with-lag-interface.json");
        JSONAssert.assertEquals(expected, response.getEntity().toString(), false);

        JSONObject jsonObject = new JSONObject(response.getEntity().toString());
        String resourceVersion = (String) jsonObject.get("resource-version");

        response = httpTestUtil.doDelete("/aai/v12/network/generic-vnfs/generic-vnf/vnf1", resourceVersion);
        assertEquals(204, response.getStatus(), "Expecting the generic vnf to be deleted");
    }

    public void initGenericVnfLInterfaceTest(QueryStyle queryStyle) {
        this.queryStyle = queryStyle;
    }
}
