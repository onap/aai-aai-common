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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.UUID;

import javax.ws.rs.core.Response;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphTransaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.HttpTestUtil;
import org.onap.aai.PayloadUtil;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.dbmap.AAIGraph;
import org.onap.aai.edges.enums.EdgeField;
import org.onap.aai.edges.enums.EdgeProperty;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.serialization.engines.QueryStyle;

public class EdgeNotValidAnymoreTest extends AAISetup {

    private HttpTestUtil testUtil;

    @Before
    public void setupData() throws IOException, AAIException {

        String cloudRegionEndpoint =
                "/aai/v13/cloud-infrastructure/cloud-regions/cloud-region/junit-cloud-owner-with-vlan/junit-cloud-region-with-vlan";

        String cloudRegionBody = PayloadUtil.getResourcePayload("cloud-region-with-vlan.json");
        testUtil = new HttpTestUtil(QueryStyle.TRAVERSAL_URI);
        testUtil.doPut(cloudRegionEndpoint, cloudRegionBody);

        JanusGraphTransaction transaction = AAIGraph.getInstance().getGraph().newTransaction();
        GraphTraversalSource g = transaction.traversal();

        Vertex configurationVertex = g.addV().property(AAIProperties.NODE_TYPE, "configuration")
                .property("configuration-id", "ci1").property("configuration-type", "ci1")
                .property(AAIProperties.AAI_URI, "/network/configurations/configuration/ci1")
                .property(AAIProperties.SOURCE_OF_TRUTH, "JUNIT").next();

        Vertex vlanVertex =
                g.V().has("vlan-interface", "test-vlan-interface-1").has(AAIProperties.NODE_TYPE, "vlan").next();

        Edge edge = configurationVertex.addEdge("org.onap.relationships.inventory.PartOf", vlanVertex);
        addEdge(edge);

        transaction.commit();
    }

    public void addEdge(Edge edge) {
        edge.property(EdgeProperty.CONTAINS.toString(), "NONE");
        edge.property(EdgeProperty.DELETE_OTHER_V.toString(), "NONE");
        edge.property(EdgeProperty.PREVENT_DELETE.toString(), "NONE");
        edge.property(EdgeField.PRIVATE.toString(), false);
        edge.property(AAIProperties.AAI_UUID, UUID.randomUUID().toString());
    }

    @Test
    public void testWhenEdgeRuleIsNoLongerValidEnsureItRetrievesVertexesWithoutOldEdges() {

        String endpoint = "/aai/v14/network/configurations";

        Response response = testUtil.doGet(endpoint, null);
        assertThat(response.getStatus(), is(200));

        String body = response.getEntity().toString();

        assertThat(body, containsString("configuration-id"));
        assertThat(body, not(containsString("vlan")));
    }

    @After
    public void teardown() {

        JanusGraph janusGraph = AAIGraph.getInstance().getGraph();
        JanusGraphTransaction transaction = janusGraph.newTransaction();
        GraphTraversalSource g = transaction.traversal();

        g.V().has(AAIProperties.SOURCE_OF_TRUTH, "JUNIT").toList().forEach((edge) -> edge.remove());

        transaction.commit();
    }
}
