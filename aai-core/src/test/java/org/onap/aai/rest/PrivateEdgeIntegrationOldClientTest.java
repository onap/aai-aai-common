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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.jayway.jsonpath.JsonPath;

import java.util.*;

import javax.ws.rs.core.Response;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.onap.aai.AAISetup;
import org.onap.aai.HttpTestUtil;
import org.onap.aai.PayloadUtil;
import org.onap.aai.dbmap.AAIGraph;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.setup.SchemaVersion;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class PrivateEdgeIntegrationOldClientTest extends AAISetup {

    private HttpTestUtil httpTestUtil;
    private Map<String, String> relationshipMap;

    private String modelId;
    private String modelVerId;
    public QueryStyle queryStyle;
    public SchemaVersion version;

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {{QueryStyle.TRAVERSAL, new SchemaVersion("v14")},
                {QueryStyle.TRAVERSAL_URI, new SchemaVersion("v14")},});
    }

    @BeforeEach
    public void setUpModelData() throws Exception {
        httpTestUtil = new HttpTestUtil(QueryStyle.TRAVERSAL);
        relationshipMap = new HashMap<>();

        modelId = "test-model-" + UUID.randomUUID().toString();
        modelVerId = "test-model-ver-" + UUID.randomUUID().toString();

        Map<String, String> modelTemplateValues = new HashMap<>();
        modelTemplateValues.put("model-invariant-id", modelId);

        String modelPayload = PayloadUtil.getTemplatePayload("model.json", modelTemplateValues);

        Response response = httpTestUtil.doPut(
                "/aai/" + version.toString() + "/service-design-and-creation/models/model/" + modelId, modelPayload);

        assertNotNull(response);
        assertThat("Model was not successfully created", response.getStatus(), is(201));

        Map<String, String> modelVersionTemplateValues = new HashMap<>();
        modelVersionTemplateValues.put("model-version-id", modelVerId);
        modelVersionTemplateValues.put("model-name", "some-model");
        modelVersionTemplateValues.put("model-version", "testValue");

        String modelVersionPayload = PayloadUtil.getTemplatePayload("model-ver.json", modelVersionTemplateValues);

        response = httpTestUtil.doPut("/aai/" + version.toString() + "/service-design-and-creation/models/model/"
                + modelId + "/model-vers/model-ver/" + modelVerId, modelVersionPayload);
        assertNotNull(response);
        assertThat("Model was not successfully created", response.getStatus(), is(201));
    }

    @MethodSource("data")
    @ParameterizedTest(name = "QueryStyle.{0} {1}")
    public void testPutGenericVnfWithModelInfoToMatchExistingModelAndCheckIfPrivateEdgeCreatedAndDoGetOnOldModelAndMakeSureNoRelationship(QueryStyle queryStyle, SchemaVersion version)
            throws Exception {

        initPrivateEdgeIntegrationOldClientTest(queryStyle, version);

        Map<String, String> genericVnfHashMap = new HashMap<>();
        String genericVnf = "test-generic-" + UUID.randomUUID().toString();

        genericVnfHashMap.put("vnf-id", genericVnf);
        genericVnfHashMap.put("model-invariant-id", modelId);
        genericVnfHashMap.put("model-version-id", modelVerId);
        String genericVnfPayload = PayloadUtil.getTemplatePayload("generic-vnf.json", genericVnfHashMap);

        Response response = httpTestUtil.doPut(
                "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf, genericVnfPayload);
        assertNotNull(response, "Response returned null");
        assertThat("Check the generic vnf is created", response.getStatus(), is(201));

        response = httpTestUtil.doGet("/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf);
        assertNotNull(response, "Response returned null");
        assertThat("Check the generic vnf is created", response.getStatus(), is(200));
        assertThat(response.getEntity().toString(), not(containsString("relationship-list")));

        List<Edge> edges =
                AAIGraph.getInstance().getGraph().newTransaction().traversal().E().has("private", true).toList();
        assertNotNull(edges, "List of edges should not be null");
        assertThat(edges.size(), is(1));
        Edge oldEdge = edges.get(0);
        assertNotNull(oldEdge);

        response = httpTestUtil.doGet("/aai/v11/network/generic-vnfs/generic-vnf/" + genericVnf);
        assertNotNull(response, "Response returned null");
        assertThat("Check the generic vnf is created", response.getStatus(), is(200));
        assertThat(response.getEntity().toString(), not(containsString("relationship-list")));

        String resourceVersion = JsonPath.read(response.getEntity().toString(), "$.resource-version");
        response = httpTestUtil.doDelete(
                "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf, resourceVersion);
        assertNotNull(response, "Response returned null");
        assertThat("Check the generic vnf is deleted", response.getStatus(), is(204));

        edges = AAIGraph.getInstance().getGraph().newTransaction().traversal().E().has("private", true).toList();
        assertNotNull(edges, "List of edges should not be null");
        assertThat(edges.size(), is(0));
    }

    public void initPrivateEdgeIntegrationOldClientTest(QueryStyle queryStyle, SchemaVersion version) {
        this.queryStyle = queryStyle;
        this.version = version;
    }

}
