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

package org.onap.aai.query.builder;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Before;
import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.edges.enums.EdgeType;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.serialization.db.EdgeSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ExcludeQueryTest extends AAISetup {

    @Autowired
    EdgeSerializer edgeSer;

    private Loader loader;

    @Before
    public void setup() throws Exception {
        loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDefaultVersion());
    }

    private QueryBuilder<Vertex> buildTestQuery(QueryBuilder<Vertex> qb) throws AAIException {
        return qb.createEdgeTraversal(EdgeType.TREE, "cloud-region", "availability-zone")
                .getVerticesExcludeByProperty("hypervisor-type", "hypervisortype-11").store("x").cap("x").unfold()
                .dedup();
    }

    @Test
    public void gremlinQueryExcludeTest() throws AAIException {
        Graph graph = TinkerGraph.open();
        GraphTraversalSource g = graph.traversal();

        Vertex cloudregion = graph.addVertex(T.label, "cloud-region", T.id, "0", "aai-node-type", "cloud-region",
                "cloud-region-id", "cloud-region-id-1", "cloud-owner", "cloud-owner-1");
        Vertex availibityzone = graph.addVertex(T.label, "availability-zone", T.id, "1", "aai-node-type",
                "availability-zone", "availability-zone-name", "az-name-1", "hypervisor-type", "hypervisortype-1");
        Vertex availibityzone11 = graph.addVertex(T.label, "availability-zone", T.id, "11", "aai-node-type",
                "availability-zone", "availability-zone-name", "az-name-11", "hypervisor-type", "hypervisortype-11");

        Vertex cloudregion1 = graph.addVertex(T.label, "cloud-region", T.id, "3", "aai-node-type", "cloud-region",
                "cloud-region-id", "cloud-region-id-10", "cloud-owner", "cloud-owner-10");
        Vertex availibityzone1 = graph.addVertex(T.label, "availability-zone", T.id, "4", "aai-node-type",
                "availability-zone", "availability-zone-name", "az-name-10", "hypervisor-type", "hypervisortype-10");
        Vertex availibityzone12 = graph.addVertex(T.label, "availability-zone", T.id, "12", "aai-node-type",
                "availability-zone", "availability-zone-name", "az-name-12", "hypervisor-type", "hypervisortype-12");

        edgeSer.addTreeEdge(g, cloudregion, availibityzone);
        edgeSer.addTreeEdge(g, cloudregion, availibityzone11);

        edgeSer.addTreeEdge(g, cloudregion1, availibityzone1);
        edgeSer.addTreeEdge(g, cloudregion1, availibityzone12);

        List<Vertex> expected = new ArrayList<>();
        expected.add(availibityzone);

        GremlinTraversal<Vertex> qb = new GremlinTraversal<>(loader, g, cloudregion);
        QueryBuilder q = buildTestQuery(qb);

        List<Vertex> results = q.toList();

        assertTrue("results match", expected.containsAll(results) && results.containsAll(expected));
    }

    @Test
    public void traversalQueryExcludeTest() throws AAIException {
        Graph graph = TinkerGraph.open();
        GraphTraversalSource g = graph.traversal();

        Vertex cloudregion = graph.addVertex(T.label, "cloud-region", T.id, "0", "aai-node-type", "cloud-region",
                "cloud-region-id", "cloud-region-id-1", "cloud-owner", "cloud-owner-1");
        Vertex availibityzone = graph.addVertex(T.label, "availability-zone", T.id, "1", "aai-node-type",
                "availability-zone", "availability-zone-name", "az-name-1", "hypervisor-type", "hypervisortype-1");
        Vertex availibityzone11 = graph.addVertex(T.label, "availability-zone", T.id, "11", "aai-node-type",
                "availability-zone", "availability-zone-name", "az-name-11", "hypervisor-type", "hypervisortype-11");

        Vertex cloudregion1 = graph.addVertex(T.label, "cloud-region", T.id, "3", "aai-node-type", "cloud-region",
                "cloud-region-id", "cloud-region-id-10", "cloud-owner", "cloud-owner-10");
        Vertex availibityzone1 = graph.addVertex(T.label, "availability-zone", T.id, "4", "aai-node-type",
                "availability-zone", "availability-zone-name", "az-name-10", "hypervisor-type", "hypervisortype-10");
        Vertex availibityzone12 = graph.addVertex(T.label, "availability-zone", T.id, "12", "aai-node-type",
                "availability-zone", "availability-zone-name", "az-name-12", "hypervisor-type", "hypervisortype-12");

        edgeSer.addTreeEdge(g, cloudregion, availibityzone);
        edgeSer.addTreeEdge(g, cloudregion, availibityzone11);

        edgeSer.addTreeEdge(g, cloudregion1, availibityzone1);
        edgeSer.addTreeEdge(g, cloudregion1, availibityzone12);

        List<Vertex> expected = new ArrayList<>();
        expected.add(availibityzone);

        TraversalQuery<Vertex> qb = new TraversalQuery<>(loader, g, cloudregion);
        QueryBuilder<Vertex> q = buildTestQuery(qb);

        List<Vertex> results = q.toList();

        assertTrue("results match", expected.containsAll(results) && results.containsAll(expected));
    }

}
