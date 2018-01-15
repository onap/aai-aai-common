/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai.rest;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.AAIJunitRunner;
import org.onap.aai.HttpTestUtil;
import org.onap.aai.PayloadUtil;
import org.onap.aai.introspection.*;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AAIJunitRunner.class)
public class PserverTest {

    private static EELFLogger logger = EELFManager.getInstance().getLogger(PserverTest.class);
    private HttpTestUtil httpTestUtil;
    private Map<String, String> relationshipMap;

    @Before
    public void setup(){
        httpTestUtil = new HttpTestUtil();
        relationshipMap = new HashMap<>();
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
        logger.info("Successfully able to create the cloud region with payload that has no keys to be retrieved from uri");

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

        response =  httpTestUtil.doGet(cloudRegionUri);
        assertNotNull("Expected the response to be not null", response);
        assertEquals("Expect the cloud region to be created", 200, response.getStatus());
        logger.info("Successfully retrieved the cloud region from db");

        Loader loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, Version.getLatest());
        Introspector in = loader.unmarshal("cloud-region", response.getEntity().toString());

        System.out.println(in.marshal(true));
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

}
