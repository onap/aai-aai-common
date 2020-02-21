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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jayway.jsonpath.JsonPath;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
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
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.serialization.engines.QueryStyle;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.test.annotation.DirtiesContext;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(value = Parameterized.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class PserverTest extends AAISetup {

    private static Logger logger = LoggerFactory.getLogger(PserverTest.class);
    private HttpTestUtil httpTestUtil;
    private Map<String, String> relationshipMap;

    @Parameterized.Parameter(value = 0)
    public QueryStyle queryStyle;

    @Parameterized.Parameters(name = "QueryStyle.{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {{QueryStyle.TRAVERSAL}, {QueryStyle.TRAVERSAL_URI}});
    }

    @Before
    public void setUp() {
        httpTestUtil = new HttpTestUtil(queryStyle);
        relationshipMap = new HashMap<>();
    }

    @Test
    public void testPutPserverCreateGetInXmlForFormats() throws Exception {
        httpTestUtil = new HttpTestUtil(queryStyle, "application/xml");
        String pserverUri = "/aai/v12/cloud-infrastructure/pservers/pserver/test-pserver-xml";
        String cloudRegionUri = "/aai/v12/cloud-infrastructure/cloud-regions/cloud-region/cloud-region-random1/cloud-region-random1-region";

        Response response = httpTestUtil.doGet(pserverUri);
        assertNotNull("Expected the response to be not null", response);
        assertEquals("Expecting the pserver to be not found", 404, response.getStatus());

        response = httpTestUtil.doPut(pserverUri, "{}");
        assertNotNull("Expected the response to be not null", response);
        assertEquals("Expecting the pserver to be created", 201, response.getStatus());

        response = httpTestUtil.doPut(cloudRegionUri, "{}");
        assertNotNull("Expected the response to be not null", response);
        assertEquals("Expecting the cloud-region to be created", 201, response.getStatus());

        relationshipMap.put("related-to", "pserver");
        relationshipMap.put("related-link", pserverUri);

        String pserverRelationshipPayload = PayloadUtil.getTemplatePayload("relationship.json", relationshipMap);
        // Creates the relationship between cloud region and pserver
        response = httpTestUtil.doPut(cloudRegionUri + "/relationship-list/relationship", pserverRelationshipPayload);
        assertNotNull("Expected the response to be not null", response);
        assertEquals("Expecting the cloud-region to pserver relationship to be created", 200, response.getStatus());

        response = httpTestUtil.doGet(pserverUri , "0", "raw");
        assertNotNull("Expected the response to be not null", response);
        assertEquals("Expecting the pserver to be created", 200, response.getStatus());
        assertThat(response.getEntity().toString(), containsString("<related-to><node><relationship-label>org.onap.relationships.inventory.LocatedIn</relationship-label><node-type>cloud-region</node-type>"));
    }

    @Test
    public void testPutPServerCreateGetAndDeleteAndCreateRelationshipBetweenPserverAndCloudRegion() throws Exception {

        logger.info("Starting the pserver testPutServerCreateGetAndDelete");

        String pserverUri = "/aai/v12/cloud-infrastructure/pservers/pserver/test-pserver";
        String cloudRegionUri = "/aai/v12/cloud-infrastructure/cloud-regions/cloud-region/test1/test2";
        String cloudRegionRelationshipUri = cloudRegionUri + "/relationship-list/relationship";

        Response response = httpTestUtil.doGet(pserverUri);
        assertNotNull("Expected the response to be not null", response);
        assertEquals("Expecting the pserver to be not found", 404, response.getStatus());
        logger.info("Verifying that the pserver is already not in the database successfully");

        Map<String, String> templateValueMap = new HashMap<>();
        templateValueMap.put("hostname", "test-pserver");
        String pserverPayload = PayloadUtil.getTemplatePayload("pserver.json", templateValueMap);

        response = httpTestUtil.doPut(pserverUri, pserverPayload);
        assertNotNull("Expected the response to be not null", response);
        assertEquals("Expecting the pserver to be created", 201, response.getStatus());
        logger.info("Successfully created the pserver into db");

        response = httpTestUtil.doGet(pserverUri);
        assertNotNull("Expected the response to be not null", response);
        assertEquals("Expecting the pserver to be found", 200, response.getStatus());

        JSONAssert.assertEquals(pserverPayload, response.getEntity().toString(), false);
        logger.info("Successfully retrieved the created pserver from db and verified with put data");

        response = httpTestUtil.doPut(cloudRegionUri, "{}");
        assertNotNull("Expected the response to be not null", response);
        assertEquals("Expect the cloud region to be created", 201, response.getStatus());
        logger.info(
                "Successfully able to create the cloud region with payload that has no keys to be retrieved from uri");

        response = httpTestUtil.doGet(cloudRegionUri);
        assertNotNull("Expected the response to be not null", response);
        assertEquals("Expecting the cloud region to be found", 200, response.getStatus());
        logger.info("Successfully retrieved the cloud region from db");

        relationshipMap.put("related-to", "pserver");
        relationshipMap.put("related-link", pserverUri);

        String pserverRelationshipPayload = PayloadUtil.getTemplatePayload("relationship.json", relationshipMap);
        // Creates the relationship between cloud region and pserver
        response = httpTestUtil.doPut(cloudRegionRelationshipUri, pserverRelationshipPayload);
        assertNotNull("Expected the response to be not null", response);
        assertEquals("Expect the cloud region relationship to pserver to be created", 200, response.getStatus());
        logger.info("Successfully created the relationship between cloud region and pserver");

        response = httpTestUtil.doGet(cloudRegionUri);
        assertNotNull("Expected the response to be not null", response);
        assertEquals("Expect the cloud region to be created", 200, response.getStatus());
        logger.info("Successfully retrieved the cloud region from db");

        Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDefaultVersion());
        Introspector in = loader.unmarshal("cloud-region", response.getEntity().toString());

        String resourceVersion = JsonPath.read(response.getEntity().toString(), "$.resource-version");

        response = httpTestUtil.doDelete(cloudRegionUri, resourceVersion);
        assertNotNull("Expected the response to be not null", response);
        assertEquals("Expecting the cloud region to be deleted", 204, response.getStatus());
        logger.info("Successfully deleted the cloud region from db");

        response = httpTestUtil.doGet(pserverUri);
        assertNotNull("Expected the response to be not null", response);
        assertEquals("Expecting the pserver to be not found", 200, response.getStatus());
        resourceVersion = JsonPath.read(response.getEntity().toString(), "$.resource-version");
        logger.info("Successfully retrieved the cloud region from db to get the latest resource version");

        response = httpTestUtil.doDelete(pserverUri, resourceVersion);
        assertNotNull("Expected the response to be not null", response);
        assertEquals("Expecting the cloud region to be deleted", 204, response.getStatus());
        logger.info("Successfully deleted the pserver from db");

        logger.info("Ending the pserver testPutServerCreateGetAndDelete");
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
