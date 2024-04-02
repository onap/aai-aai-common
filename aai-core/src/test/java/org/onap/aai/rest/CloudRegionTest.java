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
import java.util.Arrays;
import java.util.Collection;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.onap.aai.AAISetup;
import org.onap.aai.HttpTestUtil;
import org.onap.aai.PayloadUtil;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.serialization.engines.QueryStyle;
import org.skyscreamer.jsonassert.JSONAssert;

/**
 * <b>CloudRegionTest</b> is testing if you put a cloud region with all
 * children nodes associated to it then you should be able to
 * remove the cloud region without removing the individual child nodes first
 */
public class CloudRegionTest extends AAISetup {

    private HttpTestUtil httpTestUtil;
    public QueryStyle queryStyle;

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {{QueryStyle.TRAVERSAL}, {QueryStyle.TRAVERSAL_URI}});
    }

    @BeforeEach
    public void setUp() {
        httpTestUtil = new HttpTestUtil(queryStyle);
    }

    @Disabled("Test is failing due to the deletion of node with children not correct will be fixed soon")
    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0}")
    public void testPutWithAllCloudRegionChildrenNodesAndCheckIfDeleteIsSuccessful(QueryStyle queryStyle) throws IOException, AAIException {

        initCloudRegionTest(queryStyle);

        String cloudRegionPayload = PayloadUtil.getResourcePayload("cloud-region-with-all-children.json");
        String cloudRegionUri =
                "/aai/v12/cloud-infrastructure/cloud-regions/cloud-region/junit-cloud-owner/junit-cloud-region";

        Response response = httpTestUtil.doPut(cloudRegionUri, cloudRegionPayload);
        assertEquals(201, response.getStatus(), "Expected the cloud region to be created");

        response = httpTestUtil.doGet(cloudRegionUri);
        assertEquals(200, response.getStatus(), "Expected the cloud region to be found");
        String jsonResponse = response.getEntity().toString();

        JSONAssert.assertEquals(cloudRegionPayload, jsonResponse, false);
        String resourceVersion = JsonPath.read(jsonResponse, "$.resource-version");

        response = httpTestUtil.doDelete(cloudRegionUri, resourceVersion);
        assertEquals(204, response.getStatus(), "Expected the cloud region to be deleted");
    }

    public void initCloudRegionTest(QueryStyle queryStyle) {
        this.queryStyle = queryStyle;
    }
}
