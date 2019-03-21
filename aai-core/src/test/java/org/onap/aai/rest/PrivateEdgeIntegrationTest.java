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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.rest;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.jayway.jsonpath.JsonPath;

import java.util.*;

import javax.ws.rs.core.Response;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.janusgraph.core.JanusGraphTransaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.onap.aai.AAISetup;
import org.onap.aai.HttpTestUtil;
import org.onap.aai.PayloadUtil;
import org.onap.aai.dbmap.AAIGraph;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.setup.SchemaVersion;

@RunWith(value = Parameterized.class)
public class PrivateEdgeIntegrationTest extends AAISetup {

    private static EELFLogger logger = EELFManager.getInstance().getLogger(PserverTest.class);
    private HttpTestUtil httpTestUtil;
    private Map<String, String> relationshipMap;

    private String modelId;
    private String modelVerId;

    @Parameterized.Parameter(value = 0)
    public QueryStyle queryStyle;

    @Parameterized.Parameter(value = 1)
    public SchemaVersion version;

    @Parameterized.Parameters(name = "QueryStyle.{0} Version.{1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {{QueryStyle.TRAVERSAL, new SchemaVersion("v10")},
            {QueryStyle.TRAVERSAL_URI, new SchemaVersion("v10")},
            {QueryStyle.TRAVERSAL, new SchemaVersion("v11")},
            {QueryStyle.TRAVERSAL_URI, new SchemaVersion("v11")},
            {QueryStyle.TRAVERSAL, new SchemaVersion("v12")},
            {QueryStyle.TRAVERSAL_URI, new SchemaVersion("v12")},
            {QueryStyle.TRAVERSAL, new SchemaVersion("v13")},
            {QueryStyle.TRAVERSAL_URI, new SchemaVersion("v13")},
            {QueryStyle.TRAVERSAL, new SchemaVersion("v14")},
            {QueryStyle.TRAVERSAL_URI, new SchemaVersion("v14")}});
    }

    @Before
    public void setUpModelData() throws Exception {
        httpTestUtil = new HttpTestUtil(QueryStyle.TRAVERSAL);
        relationshipMap = new HashMap<>();

        modelId = "test-model-" + UUID.randomUUID().toString();
        modelVerId = "test-model-ver-" + UUID.randomUUID().toString();

        createModel(modelId, modelVerId);
    }

    private void createModel(String modelId, String modelVerId) throws Exception {
        Map<String, String> modelTemplateValues = new HashMap<>();
        modelTemplateValues.put("model-invariant-id", modelId);

        String modelPayload = PayloadUtil.getTemplatePayload("model.json", modelTemplateValues);

        Response response = httpTestUtil.doPut(
            "/aai/" + version.toString() + "/service-design-and-creation/models/model/" + modelId,
            modelPayload);

        assertNotNull(response);
        assertThat("Model was not successfully created", response.getStatus(), is(201));

        Map<String, String> modelVersionTemplateValues = new HashMap<>();
        modelVersionTemplateValues.put("model-version-id", modelVerId);
        modelVersionTemplateValues.put("model-name", "some-model");
        modelVersionTemplateValues.put("model-version", "testValue");

        String modelVersionPayload =
            PayloadUtil.getTemplatePayload("model-ver.json", modelVersionTemplateValues);

        response = httpTestUtil
            .doPut("/aai/" + version.toString() + "/service-design-and-creation/models/model/"
                + modelId + "/model-vers/model-ver/" + modelVerId, modelVersionPayload);
        assertNotNull(response);
        assertThat("Model was not successfully created", response.getStatus(), is(201));
    }

    @Test
    public void testPutGenericVnfWithModelInfoToMatchExistingModelAndCheckIfPrivateEdgeCreated()
        throws Exception {

        Map<String, String> genericVnfHashMap = new HashMap<>();
        String genericVnf = "test-generic-" + UUID.randomUUID().toString();

        genericVnfHashMap.put("vnf-id", genericVnf);
        genericVnfHashMap.put("model-invariant-id", modelId);
        genericVnfHashMap.put("model-version-id", modelVerId);
        String genericVnfPayload =
            PayloadUtil.getTemplatePayload("generic-vnf.json", genericVnfHashMap);

        Response response = httpTestUtil.doPut(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf,
            genericVnfPayload);
        assertNotNull("Response returned null", response);
        assertThat("Check the generic vnf is created", response.getStatus(), is(201));

        List<Edge> edges = AAIGraph.getInstance().getGraph().newTransaction().traversal().E()
            .has("private", true).toList();
        assertNotNull("List of edges should not be null", edges);
        assertThat(edges.size(), is(1));
        Edge oldEdge = edges.get(0);
        assertNotNull(oldEdge);

        response = httpTestUtil.doGet(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf);
        assertNotNull("Response returned null", response);
        assertThat("Check the generic vnf is created", response.getStatus(), is(200));
        assertThat(response.getEntity().toString(), not(containsString("relationship-list")));

        String resourceVersion =
            JsonPath.read(response.getEntity().toString(), "$.resource-version");
        response = httpTestUtil.doDelete(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf,
            resourceVersion);
        assertNotNull("Response returned null", response);
        assertThat("Check the generic vnf is deleted", response.getStatus(), is(204));

        edges = AAIGraph.getInstance().getGraph().newTransaction().traversal().E()
            .has("private", true).toList();
        assertNotNull("List of edges should not be null", edges);
        assertThat(edges.size(), is(0));
    }

    @Test
    public void testPutGenericVnfWithModelInfoToMatchExistingModelAndDoAnotherPutAndDontIncludeModelInfoAndPrivateEdgeShouldBeDeleted()
        throws Exception {

        Map<String, String> genericVnfHashMap = new HashMap<>();
        String genericVnf = "test-generic-" + UUID.randomUUID().toString();

        genericVnfHashMap.put("vnf-id", genericVnf);
        genericVnfHashMap.put("model-invariant-id", modelId);
        genericVnfHashMap.put("model-version-id", modelVerId);
        String genericVnfPayload =
            PayloadUtil.getTemplatePayload("generic-vnf.json", genericVnfHashMap);

        Response response = httpTestUtil.doPut(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf,
            genericVnfPayload);
        assertNotNull("Response returned null", response);
        assertThat("Check the generic vnf is created", response.getStatus(), is(201));

        response = httpTestUtil.doGet(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf);
        assertNotNull("Response returned null", response);
        assertThat("Check the generic vnf is created", response.getStatus(), is(200));
        assertThat(response.getEntity().toString(), not(containsString("relationship-list")));

        List<Edge> edges = AAIGraph.getInstance().getGraph().newTransaction().traversal().E()
            .has("private", true).toList();
        assertNotNull("List of edges should not be null", edges);
        assertThat(edges.size(), is(1));
        Edge oldEdge = edges.get(0);
        assertNotNull(oldEdge);

        String resourceVersion =
            JsonPath.read(response.getEntity().toString(), "$.resource-version");
        String newGenericVnfPayload = "{\n" + "  \"vnf-id\": \"" + genericVnf + "\",\n"
            + "  \"vnf-type\": \"someval\",\n" + "  \"vnf-name\": \"someval\"\n,"
            + "  \"resource-version\": \"" + resourceVersion + "\"" + "}";

        response = httpTestUtil.doPut(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf,
            newGenericVnfPayload);
        assertNotNull("Response returned from second put is null", response);
        assertThat("Check the generic vnf is updated", response.getStatus(), is(200));

        response = httpTestUtil.doGet(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf);
        assertNotNull("Response returned from second put is null", response);
        assertThat("Check the generic vnf is updated", response.getStatus(), is(200));

        edges = AAIGraph.getInstance().getGraph().newTransaction().traversal().E()
            .has("private", true).toList();
        assertNotNull("List of edges should not be null", edges);
        assertThat("Expected the edges to be zero since updated with no model info", edges.size(),
            is(0));

        resourceVersion = JsonPath.read(response.getEntity().toString(), "$.resource-version");
        response = httpTestUtil.doDelete(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf,
            resourceVersion);
        assertNotNull("Response returned null", response);
        assertThat("Check the generic vnf is deleted", response.getStatus(), is(204));

        edges = AAIGraph.getInstance().getGraph().newTransaction().traversal().E()
            .has("private", true).toList();
        assertNotNull("List of edges should not be null", edges);
        assertThat(edges.size(), is(0));
    }

    @Test
    public void testPutGenericVnfWithModelInfoToMatchExistingModelAndCheckIfPrivateEdgeCreatedAndAlsoDoAnotherPutSameDataAndMakeSureEdgeIsStillThere()
        throws Exception {
        Map<String, String> genericVnfHashMap = new HashMap<>();
        String genericVnf = "test-generic-" + UUID.randomUUID().toString();

        genericVnfHashMap.put("vnf-id", genericVnf);
        genericVnfHashMap.put("model-invariant-id", modelId);
        genericVnfHashMap.put("model-version-id", modelVerId);
        String genericVnfPayload =
            PayloadUtil.getTemplatePayload("generic-vnf.json", genericVnfHashMap);

        Response response = httpTestUtil.doPut(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf,
            genericVnfPayload);
        assertNotNull("Response returned null", response);
        assertThat("Check the generic vnf is created", response.getStatus(), is(201));

        response = httpTestUtil.doGet(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf);
        assertNotNull("Response returned null", response);
        assertThat("Check the generic vnf is created", response.getStatus(), is(200));
        assertThat(response.getEntity().toString(), not(containsString("relationship-list")));

        List<Edge> edges = AAIGraph.getInstance().getGraph().newTransaction().traversal().E()
            .has("private", true).toList();
        assertNotNull("List of edges should not be null", edges);
        assertThat(edges.size(), is(1));
        Edge oldEdge = edges.get(0);
        assertNotNull(oldEdge);

        String resourceVersion =
            JsonPath.read(response.getEntity().toString(), "$.resource-version");
        genericVnfHashMap.put("resource-version", resourceVersion);
        String genericVnfPayloadWithResource =
            PayloadUtil.getTemplatePayload("generic-vnf-resource.json", genericVnfHashMap);

        response = httpTestUtil.doPut(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf,
            genericVnfPayloadWithResource);
        assertNotNull("Response returned null", response);
        assertThat("Check the generic vnf is updated", response.getStatus(), is(200));

        response = httpTestUtil.doGet(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf);
        assertNotNull("Response returned null", response);
        assertThat("Check the generic vnf is created", response.getStatus(), is(200));
        assertThat(response.getEntity().toString(), not(containsString("relationship-list")));

        resourceVersion = JsonPath.read(response.getEntity().toString(), "$.resource-version");
        edges = AAIGraph.getInstance().getGraph().newTransaction().traversal().E()
            .has("private", true).toList();
        assertNotNull("List of edges should not be null", edges);
        assertThat(edges.size(), is(1));
        Edge newEdge = edges.get(0);
        assertNotNull(newEdge);
        assertEquals(oldEdge, newEdge);

        response = httpTestUtil.doDelete(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf,
            resourceVersion);
        assertNotNull("Response returned null", response);
        assertThat("Check the generic vnf is deleted", response.getStatus(), is(204));

        edges = AAIGraph.getInstance().getGraph().newTransaction().traversal().E()
            .has("private", true).toList();
        assertNotNull("List of edges should not be null", edges);
        assertThat(edges.size(), is(0));
    }

    @Test
    public void testPutGenericVnfWithModelThatDoesntExistAndCheckIfItReturnsNotFound()
        throws Exception {
        Map<String, String> genericVnfHashMap = new HashMap<>();
        String genericVnf = "test-generic-" + UUID.randomUUID().toString();

        genericVnfHashMap.put("vnf-id", genericVnf);
        genericVnfHashMap.put("model-invariant-id", "random-wrong-model");
        genericVnfHashMap.put("model-version-id", "random-wrong-model-ver");
        String genericVnfPayload =
            PayloadUtil.getTemplatePayload("generic-vnf.json", genericVnfHashMap);

        Response response = httpTestUtil.doPut(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf,
            genericVnfPayload);
        assertNotNull("Response returned null", response);

        String body = response.getEntity().toString();

        logger.info("Response from the PUT request: " + body);
        assertThat("Check the generic vnf is created", response.getStatus(), is(404));
        assertThat(body, containsString("Node Not Found"));
    }

    @Test
    public void testPutGenericVnfWithModelMissingPartOfKeyReturnsBadRequest() throws Exception {

        String genericVnf = "test-generic-" + UUID.randomUUID().toString();
        String genericVnfPayload = "{\n" + "  \"vnf-id\": \"" + genericVnf + "\",\n"
            + "  \"vnf-type\": \"someval\",\n" + "  \"vnf-name\": \"someval\",\n"
            + "  \"model-invariant-id\": \"some-model\"\n" + "}";

        Response response = httpTestUtil.doPut(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf,
            genericVnfPayload);
        assertNotNull("Response returned null", response);

        String body = response.getEntity().toString();
        logger.info("Response from the PUT request: " + body);
        assertThat("Check the generic vnf is created", response.getStatus(), is(400));
        assertThat(body, containsString("model-invariant-id requires model-version-id"));
    }

    @Test
    public void testPutGenericVnfWithModelInfoToMatchExistingModelAndDeleteModelVerAndCheckIfPreventDeleteFailsWithBadRequest()
        throws Exception {

        Map<String, String> genericVnfHashMap = new HashMap<>();
        String genericVnf = "test-generic-" + UUID.randomUUID().toString();

        genericVnfHashMap.put("vnf-id", genericVnf);
        genericVnfHashMap.put("model-invariant-id", modelId);
        genericVnfHashMap.put("model-version-id", modelVerId);
        String genericVnfPayload =
            PayloadUtil.getTemplatePayload("generic-vnf.json", genericVnfHashMap);

        Response response = httpTestUtil.doPut(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf,
            genericVnfPayload);
        assertNotNull("Response returned null", response);
        assertThat("Check the generic vnf is created", response.getStatus(), is(201));

        List<Edge> edges = AAIGraph.getInstance().getGraph().newTransaction().traversal().E()
            .has("private", true).toList();
        assertNotNull("List of edges should not be null", edges);
        assertThat(edges.size(), is(1));
        Edge oldEdge = edges.get(0);
        assertNotNull(oldEdge);

        response = httpTestUtil
            .doGet("/aai/" + version.toString() + "/service-design-and-creation/models/model/"
                + modelId + "/model-vers/model-ver/" + modelVerId);
        assertNotNull(response);
        assertThat(response.getStatus(), is(200));
        String resourceVersion =
            JsonPath.read(response.getEntity().toString(), "$.resource-version");
        response = httpTestUtil
            .doDelete("/aai/" + version.toString() + "/service-design-and-creation/models/model/"
                + modelId + "/model-vers/model-ver/" + modelVerId, resourceVersion);
        assertNotNull("Response returned null", response);
        assertThat("Check the generic vnf is deleted", response.getStatus(), is(400));
        assertThat(response.getEntity().toString(),
            containsString(" Please clean up references from the following types [generic-vnf]"));
    }

    @Test
    public void testPutWithGenericVnfToExistingModelAndUpdateWithNewModelInfoAndEdgeToOldModelShouldBeDeletedAndNewEdgeToNewModelShouldBeCreated()
        throws Exception {

        Map<String, String> genericVnfHashMap = new HashMap<>();
        String genericVnf = "test-generic-" + UUID.randomUUID().toString();

        genericVnfHashMap.put("vnf-id", genericVnf);
        genericVnfHashMap.put("model-invariant-id", modelId);
        genericVnfHashMap.put("model-version-id", modelVerId);
        String genericVnfPayload =
            PayloadUtil.getTemplatePayload("generic-vnf.json", genericVnfHashMap);

        Response response = httpTestUtil.doPut(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf,
            genericVnfPayload);
        assertNotNull("Response returned null", response);
        assertThat("Check the generic vnf is created", response.getStatus(), is(201));

        List<Edge> edges = AAIGraph.getInstance().getGraph().newTransaction().traversal().E()
            .has("private", true).toList();
        assertNotNull("List of edges should not be null", edges);
        assertThat(edges.size(), is(1));
        Edge oldEdge = edges.get(0);
        assertNotNull(oldEdge);

        response = httpTestUtil.doGet(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf);
        assertNotNull("Response returned null", response);
        assertThat("Check the generic vnf is created", response.getStatus(), is(200));
        assertThat(response.getEntity().toString(), not(containsString("relationship-list")));

        String resourceVersion =
            JsonPath.read(response.getEntity().toString(), "$.resource-version");

        String newModelId = "test-model-" + UUID.randomUUID().toString();
        String newModelVerId = "test-model-ver-" + UUID.randomUUID().toString();

        createModel(newModelId, newModelVerId);

        genericVnfHashMap.put("resource-version", resourceVersion);
        genericVnfHashMap.put("model-invariant-id", newModelId);
        genericVnfHashMap.put("model-version-id", newModelVerId);

        String genericVnfPayloadWithResource =
            PayloadUtil.getTemplatePayload("generic-vnf-resource.json", genericVnfHashMap);

        response = httpTestUtil.doPut(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf,
            genericVnfPayloadWithResource);
        assertNotNull("Response returned null", response);
        assertThat("Check the generic vnf is successfully updated based on new model",
            response.getStatus(), is(200));

        edges = AAIGraph.getInstance().getGraph().newTransaction().traversal().E()
            .has("private", true).toList();
        assertNotNull("List of edges should not be null", edges);
        assertThat(edges.size(), is(1));
        Edge newEdge = edges.get(0);
        assertNotNull(newEdge);
        assertNotEquals(oldEdge, newEdge);

        response = httpTestUtil.doGet(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf);
        assertNotNull("Response returned null", response);
        assertThat("Check the generic vnf is created", response.getStatus(), is(200));
        assertThat(response.getEntity().toString(), not(containsString("relationship-list")));
        resourceVersion = JsonPath.read(response.getEntity().toString(), "$.resource-version");

        response = httpTestUtil.doDelete(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf,
            resourceVersion);
        assertNotNull("Response returned null", response);
        assertThat("Check the generic vnf is deleted", response.getStatus(), is(204));

        edges = AAIGraph.getInstance().getGraph().newTransaction().traversal().E()
            .has("private", true).toList();
        assertNotNull("List of edges should not be null", edges);
        assertThat(edges.size(), is(0));
    }

    @Test
    public void testPutWithGenericVnfToExistingModelAndUpdateWithNewModelInfoThatDoesntExistAndCheckIfReturnsNotFoundAndOldEdgeShouldNotBeDeleted()
        throws Exception {

        Map<String, String> genericVnfHashMap = new HashMap<>();
        String genericVnf = "test-generic-" + UUID.randomUUID().toString();

        genericVnfHashMap.put("vnf-id", genericVnf);
        genericVnfHashMap.put("model-invariant-id", modelId);
        genericVnfHashMap.put("model-version-id", modelVerId);
        String genericVnfPayload =
            PayloadUtil.getTemplatePayload("generic-vnf.json", genericVnfHashMap);

        Response response = httpTestUtil.doPut(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf,
            genericVnfPayload);
        assertNotNull("Response returned null", response);
        assertThat("Check the generic vnf is created", response.getStatus(), is(201));

        List<Edge> edges = AAIGraph.getInstance().getGraph().newTransaction().traversal().E()
            .has("private", true).toList();
        assertNotNull("List of edges should not be null", edges);
        assertThat(edges.size(), is(1));
        Edge oldEdge = edges.get(0);
        assertNotNull(oldEdge);

        response = httpTestUtil.doGet(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf);
        assertNotNull("Response returned null", response);
        assertThat("Check the generic vnf is created", response.getStatus(), is(200));
        assertThat(response.getEntity().toString(), not(containsString("relationship-list")));

        String resourceVersion =
            JsonPath.read(response.getEntity().toString(), "$.resource-version");

        String newModelId = "test-model-" + UUID.randomUUID().toString();
        String newModelVerId = "test-model-ver-" + UUID.randomUUID().toString();

        genericVnfHashMap.put("resource-version", resourceVersion);
        genericVnfHashMap.put("model-invariant-id", newModelId);
        genericVnfHashMap.put("model-version-id", newModelVerId);

        String genericVnfPayloadWithResource =
            PayloadUtil.getTemplatePayload("generic-vnf-resource.json", genericVnfHashMap);

        response = httpTestUtil.doPut(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf,
            genericVnfPayloadWithResource);
        assertNotNull("Response returned null", response);
        assertThat("Check the generic vnf is failed due to missing model ver", response.getStatus(),
            is(404));

        edges = AAIGraph.getInstance().getGraph().newTransaction().traversal().E()
            .has("private", true).toList();
        assertNotNull("List of edges should not be null", edges);
        assertThat(edges.size(), is(1));
        Edge newEdge = edges.get(0);
        assertNotNull(newEdge);
        assertEquals(oldEdge, newEdge);
    }

    @Test
    public void testPutWithGenericVnfToExistingModelAndUpdateVnfWithModelMissingPartOfKeyAndUpdateShouldFailAndOldEdgeShouldStillExist()
        throws Exception {

        Map<String, String> genericVnfHashMap = new HashMap<>();
        String genericVnf = "test-generic-" + UUID.randomUUID().toString();

        genericVnfHashMap.put("vnf-id", genericVnf);
        genericVnfHashMap.put("model-invariant-id", modelId);
        genericVnfHashMap.put("model-version-id", modelVerId);
        String genericVnfPayload =
            PayloadUtil.getTemplatePayload("generic-vnf.json", genericVnfHashMap);

        Response response = httpTestUtil.doPut(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf,
            genericVnfPayload);
        assertNotNull("Response returned null", response);
        assertThat("Check the generic vnf is created", response.getStatus(), is(201));

        List<Edge> edges = AAIGraph.getInstance().getGraph().newTransaction().traversal().E()
            .has("private", true).toList();
        assertNotNull("List of edges should not be null", edges);
        assertThat(edges.size(), is(1));
        Edge oldEdge = edges.get(0);
        assertNotNull(oldEdge);

        response = httpTestUtil.doGet(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf);
        assertNotNull("Response returned null", response);
        assertThat("Check the generic vnf is created", response.getStatus(), is(200));
        assertThat(response.getEntity().toString(), not(containsString("relationship-list")));

        String resourceVersion =
            JsonPath.read(response.getEntity().toString(), "$.resource-version");

        String newModelId = "test-model-" + UUID.randomUUID().toString();
        String newModelVerId = "test-model-ver-" + UUID.randomUUID().toString();

        createModel(newModelId, newModelVerId);

        genericVnfHashMap.put("resource-version", resourceVersion);
        genericVnfHashMap.put("model-invariant-id", newModelId);
        genericVnfHashMap.put("model-version-id", newModelVerId);

        String genericVnfPayloadWithResource =
            "{\n" + "  \"vnf-id\": \"" + genericVnf + "\",\n" + "  \"vnf-type\": \"someval\",\n"
                + "  \"vnf-name\": \"someval\",\n" + "  \"model-invariant-id\": \"" + newModelId
                + "\",\n" + "  \"resource-version\": \"${resource-version}\"\n" + "}";

        response = httpTestUtil.doPut(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf,
            genericVnfPayloadWithResource);
        assertNotNull("Response returned null", response);
        assertThat("Check the generic vnf is failed due to missing model ver", response.getStatus(),
            is(400));
        assertThat(response.getEntity().toString(),
            containsString("model-invariant-id requires model-version-id"));

        edges = AAIGraph.getInstance().getGraph().newTransaction().traversal().E()
            .has("private", true).toList();
        assertNotNull("List of edges should not be null", edges);
        assertThat(edges.size(), is(1));
        Edge newEdge = edges.get(0);
        assertNotNull(newEdge);
        assertEquals(oldEdge, newEdge);
    }

    @Test
    public void testPutCustomerWithServiceInstanceThatHasModelVerThatExistsInDbAndDoGetOnCustomerAndCheckIfRelationshipIsNotThere()
        throws Exception {

        Map<String, String> customerHashMap = new HashMap<>();

        customerHashMap.put("global-customer-id", "test-customer-" + UUID.randomUUID().toString());
        customerHashMap.put("subscription-type", "test-subtype-" + UUID.randomUUID().toString());
        customerHashMap.put("service-instance-id", "test-tenant-" + UUID.randomUUID().toString());
        customerHashMap.put("model-invariant-id", modelId);
        customerHashMap.put("model-version-id", modelVerId);

        String customer = PayloadUtil.getTemplatePayload("customer.json", customerHashMap);
        Response response =
            httpTestUtil.doPut("/aai/" + version.toString() + "/business/customers/customer/"
                + customerHashMap.get("global-customer-id"), customer);
        assertNotNull("Response returned null", response);
        assertThat("Check the cloud region is created with link to generic vnf",
            response.getStatus(), is(201));

        List<Edge> edges = AAIGraph.getInstance().getGraph().newTransaction().traversal().E()
            .has("private", true).toList();
        assertNotNull("List of edges should not be null", edges);
        assertThat(edges.size(), is(1));
        Edge oldEdge = edges.get(0);
        assertNotNull(oldEdge);

        response = httpTestUtil.doGet("/aai/" + version.toString() + "/business/customers/customer/"
            + customerHashMap.get("global-customer-id"));
        assertNotNull("Response returned null", response);
        assertThat("Check the customer is returned", response.getStatus(), is(200));
        assertThat(response.getEntity().toString(),
            not(containsString("\"related-to\":\"model-ver\"")));

        String url = "/aai/" + version + "/business/customers/customer/"
            + customerHashMap.get("global-customer-id")
            + "/service-subscriptions/service-subscription/"
            + customerHashMap.get("subscription-type") + "/service-instances/service-instance/"
            + customerHashMap.get("service-instance-id");

        String genericVnf = "vnf-" + UUID.randomUUID().toString();
        String genericVnfPayload = "{\n" + "  \"vnf-id\": \"" + genericVnf + "\",\n"
            + "  \"vnf-type\": \"someval\",\n" + "  \"vnf-name\": \"someval\",\n"
            + "  \"relationship-list\": {\n" + "    \"relationship\": [\n" + "      {\n"
            + "        \"related-to\": \"service-instance\",\n" + "        \"related-link\": \""
            + url + "\"\n" + "      }\n" + "    ]\n" + "  }\n" + "}\n";

        response = httpTestUtil.doPut(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf,
            genericVnfPayload);
        assertNotNull("Response returned null", response);
        assertThat("Check the customer is returned", response.getStatus(), is(201));

        response = httpTestUtil.doGet(
            "/aai/" + version.toString() + "/network/generic-vnfs/generic-vnf/" + genericVnf);
        assertNotNull("Response returned null", response);
        assertThat("Check the customer is returned", response.getStatus(), is(200));
        assertThat(response.getEntity().toString(),
            containsString("\"related-to\":\"service-instance\""));

        response = httpTestUtil.doGet("/aai/" + version.toString() + "/business/customers/customer/"
            + customerHashMap.get("global-customer-id"));
        assertNotNull("Response returned null", response);
        assertThat("Check the customer is returned", response.getStatus(), is(200));
        assertThat(response.getEntity().toString(),
            not(containsString("\"related-to\":\"model-ver\"")));
        assertThat(response.getEntity().toString(),
            containsString("\"related-to\":\"generic-vnf\""));

    }

    @After
    public void tearDown() {

        JanusGraphTransaction transaction = AAIGraph.getInstance().getGraph().newTransaction();
        boolean success = true;

        try {

            GraphTraversalSource g = transaction.traversal();

            g.V().has("source-of-truth", "JUNIT").toList().forEach(v -> v.remove());

        } catch (Exception ex) {
            success = false;
            logger.error("Unable to remove the vertexes", ex);
        } finally {
            if (success) {
                transaction.commit();
            } else {
                transaction.rollback();
                fail("Unable to teardown the graph");
            }
        }

    }
}
