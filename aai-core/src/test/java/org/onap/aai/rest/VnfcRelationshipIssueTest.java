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
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Collection;

import javax.ws.rs.core.Response;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraphTransaction;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.onap.aai.AAISetup;
import org.onap.aai.HttpTestUtil;
import org.onap.aai.PayloadUtil;
import org.onap.aai.dbmap.AAIGraph;
import org.onap.aai.serialization.engines.QueryStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

public class VnfcRelationshipIssueTest extends AAISetup {

    private static final Logger LOGGER = LoggerFactory.getLogger(VnfcRelationshipIssueTest.class);
    private HttpTestUtil httpTestUtil;

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();
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
    public void testCreateVnfWithVfModuleAndCreateVnfcRelatedToVfModule(QueryStyle queryStyle) throws Exception {

        initVnfcRelationshipIssueTest(queryStyle);

        String genericVnfUri = "/aai/v14/network/generic-vnfs/generic-vnf/test-vnf11";
        String genericVnfPayload = PayloadUtil.getResourcePayload("generic-vnf-with-vf-module.json");

        Response response = httpTestUtil.doPut(genericVnfUri, genericVnfPayload);
        assertEquals(201, response.getStatus(), "Expected the generic vnf to be created");

        String vnfcUri = "/aai/v14/network/vnfcs/vnfc/test-vnfc11";
        String vnfcPaylaod = PayloadUtil.getResourcePayload("vnfc-related-to-vf-module.json");

        response = httpTestUtil.doPut(vnfcUri, vnfcPaylaod);
        assertEquals(201, response.getStatus(), "Expected the generic vnf to be created");
    }

    @AfterEach
    public void tearDown() {

        JanusGraphTransaction transaction = AAIGraph.getInstance().getGraph().newTransaction();
        boolean success = true;

        try {

            GraphTraversalSource g = transaction.traversal();

            g.V().has("source-of-truth", "JUNIT").toList().forEach(v -> v.remove());

        } catch (Exception ex) {
            success = false;
            LOGGER.error("Unable to remove the vertexes", ex);
        } finally {
            if (success) {
                transaction.commit();
            } else {
                transaction.rollback();
                fail("Unable to teardown the graph");
            }
        }

    }

    public void initVnfcRelationshipIssueTest(QueryStyle queryStyle) {
        this.queryStyle = queryStyle;
    }
}
