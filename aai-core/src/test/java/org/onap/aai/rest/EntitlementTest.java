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

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;

import javax.ws.rs.core.Response;

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
public class EntitlementTest extends AAISetup {

    private HttpTestUtil httpTestUtil;
    public QueryStyle queryStyle;

    private String vnfPayload;

    private String vnfUri;

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {{QueryStyle.TRAVERSAL_URI}});
    }

    @BeforeEach
    public void setUp() throws IOException {
        httpTestUtil = new HttpTestUtil(queryStyle);
        vnfPayload = PayloadUtil.getResourcePayload("vnf.json");
        vnfUri = "/aai/v14/network/generic-vnfs/generic-vnf/vnf1";
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void testPutGenericVnfAndThenInsertEntitlement(QueryStyle queryStyle) throws IOException, AAIException {
        initEntitlementTest(queryStyle);
        String entitlementPayload = PayloadUtil.getResourcePayload("entitlement.json");
        String entitlementUri = "/aai/v14/network/generic-vnfs/generic-vnf/vnf1/entitlements/entitlement/g1/r1";
        Response response = httpTestUtil.doPut(vnfUri, vnfPayload);
        assertEquals(201, response.getStatus(), "Expected the Generic Vnf to be created");

        response = httpTestUtil.doGet(vnfUri);
        assertEquals(200, response.getStatus(), "Expected the Generic Vnf to be found");
        String jsonResponse = response.getEntity().toString();
        JSONAssert.assertEquals(vnfPayload, jsonResponse, false);

        response = httpTestUtil.doPut(entitlementUri, entitlementPayload);
        assertEquals(201, response.getStatus(), "Expected the Entitlement to be created");
    }

    @AfterEach
    public void tearDown() throws UnsupportedEncodingException, AAIException {
        Response response = httpTestUtil.doGet(vnfUri);
        assertEquals(200, response.getStatus(), "Expected the Generic Vnf to be found");
        String jsonResponse = response.getEntity().toString();
        String resourceVersion = JsonPath.read(jsonResponse, "$.resource-version");
        response = httpTestUtil.doDelete(vnfUri, resourceVersion);
        assertEquals(204, response.getStatus(), "Expected the cloud region to be deleted");
    }

    public void initEntitlementTest(QueryStyle queryStyle) {
        this.queryStyle = queryStyle;
    }
}
