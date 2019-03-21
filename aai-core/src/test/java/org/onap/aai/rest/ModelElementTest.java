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

import com.jayway.jsonpath.JsonPath;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;

import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.onap.aai.AAISetup;
import org.onap.aai.HttpTestUtil;
import org.onap.aai.PayloadUtil;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.serialization.engines.QueryStyle;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.test.annotation.DirtiesContext;

@RunWith(value = Parameterized.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ModelElementTest extends AAISetup {

    private HttpTestUtil httpTestUtil;

    @Parameterized.Parameter(value = 0)
    public QueryStyle queryStyle;

    private String modelPayload;
    private String modelElementPayload;

    private String modelUri;
    private String modelElementUri;

    @Parameterized.Parameters(name = "QueryStyle.{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {{QueryStyle.TRAVERSAL_URI}});
    }

    @Before
    public void setUp() throws IOException {
        httpTestUtil = new HttpTestUtil(queryStyle);
        modelPayload = PayloadUtil.getResourcePayload("model.json");
        modelElementPayload = PayloadUtil.getResourcePayload("model-element.json");
        modelUri = "/aai/v14/service-design-and-creation/models/model/24c04fc5-f3f8-43ec-8792-c6f940638676-test1";
        modelElementUri =
                "/aai/v14/service-design-and-creation/models/model/24c04fc5-f3f8-43ec-8792-c6f940638676-test1/model-vers/model-ver/0c4c59f0-9864-43ca-b0c2-ca38746b72a5-test1/model-elements/model-element/0dc2b8b6-af8d-4213-970b-7861a603fc86-test1/model-constraints/model-constraint/782ba24a-28ab-4fd0-8e69-da10cc5373f3-test1/constrained-element-sets/constrained-element-set/a33e65c3-1198-4d4c-9799-2b521e4c4212-test1/element-choice-sets/element-choice-set/7df27a94-06c8-46ef-9fc2-5900d8cffbb0-test1/model-elements/model-element/acf8b6cf-e051-4c1b-bcad-b24792f826cf-test1";
    }

    @Test
    public void testPutModelAndThenModelElementAndItShouldSucceed() throws IOException, AAIException {
        Response response = httpTestUtil.doPut(modelUri, modelPayload);
        assertEquals("Expected the cloud region to be created", 201, response.getStatus());

        response = httpTestUtil.doGet(modelUri);
        assertEquals("Expected the cloud region to be found", 200, response.getStatus());
        String jsonResponse = response.getEntity().toString();
        JSONAssert.assertEquals(modelPayload, jsonResponse, false);

        response = httpTestUtil.doPut(modelElementUri, modelElementPayload);
        assertEquals("Expected the cloud region to be created", 201, response.getStatus());
    }

    @After
    public void tearDown() throws UnsupportedEncodingException, AAIException {
        Response response = httpTestUtil.doGet(modelUri);
        assertEquals("Expected the cloud region to be found", 200, response.getStatus());
        String jsonResponse = response.getEntity().toString();
        String resourceVersion = JsonPath.read(jsonResponse, "$.resource-version");
        response = httpTestUtil.doDelete(modelUri, resourceVersion);
        assertEquals("Expected the cloud region to be deleted", 204, response.getStatus());
    }
}
