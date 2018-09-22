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
package org.onap.aai.serialization.queryformats;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.config.SpringContextAware;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.edges.exceptions.AmbiguousRuleChoiceException;
import org.onap.aai.edges.exceptions.EdgeRuleNotFoundException;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.serialization.db.AAICorePrivateEdgeTestConfigTranslator;
import org.onap.aai.serialization.db.EdgeSerializer;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.setup.SchemaVersions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        SchemaLocationsBean.class,
        SchemaVersions.class,
        AAICorePrivateEdgeTestConfigTranslator.class,
        EdgeIngestor.class,
        EdgeSerializer.class,
        SpringContextAware.class
})
@DirtiesContext
public class GraphSONTest {

    private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(GraphSONTest.class);

    private Graph graph;
    private Vertex v1;

    @Autowired
    protected EdgeSerializer edgeSer;

    @Autowired
    protected EdgeIngestor rules;

    private JsonObject jsonObj = new JsonObject() ;
    private JsonObject properties = new JsonObject();
    private JsonArray name = new JsonArray() ;
    private JsonObject idVal = new JsonObject() ;

    private GraphSON graphSON;

    @Before
    public void setUp() {
        
        jsonObj.addProperty("id", 0);
        jsonObj.addProperty("label", "vertex");
                
        idVal.addProperty("id", 1);
        idVal.addProperty("value", "Sam");
                
        name.add(idVal);
        properties.add("name",name);
        jsonObj.add("properties", properties);
                
        graph = TinkerGraph.open();
        v1 = graph.addVertex("name", "Sam");

        graphSON = new GraphSON();
    }
    
    @Test
    public void classGraphSONTestWithVertex(){
        
        GraphSON graphSonObj1 = new GraphSON();
        JsonObject obj = graphSonObj1.formatObject(v1).get();
                
        assertEquals(jsonObj, obj);
    }

    /**
     * Case where there is only one private edge
     * <pre>
     *     {
     *         "id": 21,
     *         "inE": {
     *             "org.onap.relationships.inventory.isA": [
     *                  {
     *                      "id": 10,
     *                      "properties": {
     *                        "aai-uuid": "oafjdsiofjs",
     *                        "private": true
     *                      }
     *                  }
     *             ]
     *         }
     *         "label": "model-ver",
     *         "properties": {
     *             "aai-node-type": [
     *                  {
     *                      "id": 5,
     *                      "value": "model-ver"
     *                  }
     *             ]
     *         }
     *     }
     * </pre>
     *
     * @throws AAIException
     */
    @Test
    public void testGraphWithVertexHavingPrivateEdges() throws AAIException, EdgeRuleNotFoundException, AmbiguousRuleChoiceException {

        Vertex genericVnf = graph.addVertex(
                T.label, "generic-vnf",
                T.id, "20",
                "aai-node-type", "generic-vnf",
                "vnf-id", "vnf-id-1",
                "vnf-name", "vnf-name-1"
        );

        Vertex modelVer = graph.addVertex(
                T.label, "model-ver",
                T.id, "21",
                "aai-node-type", "model-ver",
                "model-version-id", "modelVer1",
                "model-name", "modelName1"
        );

        GraphTraversalSource source = graph.traversal();
        edgeSer.addPrivateEdge(source, genericVnf, modelVer, null);

        Optional<JsonObject> jsonObjectOptional = graphSON.formatObject(genericVnf);
        JsonObject obj = jsonObjectOptional.get();
        LOGGER.info(obj.toString());
        assertNotNull(obj);
        String value = obj.toString();

        assertThat(value, not(containsString("private")));
        assertThat(value, not(containsString("inE")));
        assertThat(value, not(containsString("outE")));
    }

    /**
     * Case where there is one private edge and regular edge
     * with the same edge label name
     * <pre>
     *     {
     *         "id": 21,
     *         "inE": {
     *             "org.onap.relationships.inventory.isA": [
     *                  {
     *                      "id": 10,
     *                      "properties": {
     *                        "aai-uuid": "oafjdsiofjs",
     *                        "private": true
     *                      }
     *                  }
     *                  {
     *                      "id": 11,
     *                      "properties": {
     *                        "aai-uuid": "oafjdsiofjs",
     *                        "private": false
     *                      }
     *                  }
     *             ]
     *         }
     *         "label": "model-ver",
     *         "properties": {
     *             "aai-node-type": [
     *                  {
     *                      "id": 5,
     *                      "value": "model-ver"
     *                  }
     *             ]
     *         }
     *     }
     * </pre>
     *
     * @throws AAIException
     */
    @Test
    public void testGraphWithSameLabelWithPrivateEdgeAndRegularEdge() throws AAIException, EdgeRuleNotFoundException, AmbiguousRuleChoiceException {

        Vertex genericVnf = graph.addVertex(
                T.label, "generic-vnf",
                T.id, "20",
                "aai-node-type", "generic-vnf",
                "vnf-id", "vnf-id-1",
                "vnf-name", "vnf-name-1"
        );

        Vertex modelVer = graph.addVertex(
                T.label, "model-ver",
                T.id, "21",
                "aai-node-type", "model-ver",
                "model-version-id", "modelVer1",
                "model-name", "modelName1"
        );

        Vertex modelElement = graph.addVertex(
                T.label, "model-element",
                T.id, "22",
                "aai-node-type", "model-element"
        );



        GraphTraversalSource source = graph.traversal();
        edgeSer.addPrivateEdge(source, genericVnf, modelVer, null);
        edgeSer.addEdge(source, modelVer, modelElement, null);

        Optional<JsonObject> jsonObjectOptional = graphSON.formatObject(modelVer);
        JsonObject obj = jsonObjectOptional.get();
        LOGGER.info(obj.toString());
        assertNotNull(obj);
        String value = obj.toString();
        assertThat(value, not(containsString("\"private\":true")));
    }

    /**
     * Case where there is one private edge and regular edge to same label
     * And another regular edge to a different label
     * <pre>
     *     {
     *         "id": 21,
     *         "inE": {
     *             "org.onap.relationships.inventory.isA": [
     *                  {
     *                      "id": 10,
     *                      "properties": {
     *                        "aai-uuid": "oafjdsiofjs",
     *                        "private": true
     *                      }
     *                  },
     *                  {
     *                      "id": 11,
     *                      "properties": {
     *                        "aai-uuid": "oafjdsiofjs",
     *                        "private": false
     *                      }
     *                  }
     *             ],
     *             "org.onap.relationships.inventory.BelongsTo": [
     *                  {
     *                      "id": 13,
     *                      "properties": {
     *                        "aai-uuid": "oafjdsiofjs",
     *                        "private": false
     *                      }
     *                  }
     *             ]
     *         }
     *         "label": "model-ver",
     *         "properties": {
     *             "aai-node-type": [
     *                  {
     *                      "id": 5,
     *                      "value": "model-ver"
     *                  }
     *             ]
     *         }
     *     }
     * </pre>
     *
     * @throws AAIException
     */
    @Test
    public void testGraphWithMultipleLabelWithOneLabelWithPrivateEdgeAndRegularEdgeAndAnotherLabelWithRegularEdge() throws AAIException, EdgeRuleNotFoundException, AmbiguousRuleChoiceException {

        Vertex genericVnf = graph.addVertex(
                T.label, "generic-vnf",
                T.id, "20",
                "aai-node-type", "generic-vnf",
                "vnf-id", "vnf-id-1",
                "vnf-name", "vnf-name-1"
        );

        Vertex modelVer = graph.addVertex(
                T.label, "model-ver",
                T.id, "21",
                "aai-node-type", "model-ver",
                "model-version-id", "modelVer1",
                "model-name", "modelName1"
        );

        Vertex modelElement = graph.addVertex(
                T.label, "model-element",
                T.id, "22",
                "aai-node-type", "model-element"
        );

        Vertex metadatum = graph.addVertex(
                T.label, "metadatum",
                T.id, "23",
                "aai-node-type", "metadatum"
        );



        GraphTraversalSource source = graph.traversal();
        edgeSer.addPrivateEdge(source, genericVnf, modelVer, null);
        edgeSer.addEdge(source, modelVer, modelElement, null);
        edgeSer.addTreeEdge(source, modelVer, metadatum);

        Optional<JsonObject> jsonObjectOptional = graphSON.formatObject(modelVer);
        JsonObject obj = jsonObjectOptional.get();
        LOGGER.info(obj.toString());
        assertNotNull(obj);
        String value = obj.toString();
        assertThat(value, not(containsString("\"private\":true")));
    }

    @Test
    public void testGraphCreateRegularOutAndInEdges() throws AAIException {

        Vertex complex1 = graph.addVertex(
                T.label, "complex",
                T.id, "20",
                "aai-node-type", "complex"
        );

        Vertex pserver1 = graph.addVertex(
                T.label, "pserver",
                T.id, "22",
                "aai-node-type", "pserver",
                "hostname", "test-pserver1"
        );

        Vertex pserver2 = graph.addVertex(
                T.label, "pserver",
                T.id, "23",
                "aai-node-type", "pserver",
                "hostname", "test-pserver2"
        );



        GraphTraversalSource source = graph.traversal();
        edgeSer.addEdge(source, pserver1, complex1, null);
        edgeSer.addEdge(source, pserver2, complex1, null);


        Optional<JsonObject> jsonObjectOptional = graphSON.formatObject(complex1);
        JsonObject obj = jsonObjectOptional.get();
        LOGGER.info(obj.toString());
        assertNotNull(obj);
        assertThat(obj.toString(), not(containsString("\"private\":true")));
        assertThat(obj.toString(), containsString("inE"));
    }

    /**
     * Case where there is one private edge and regular edge to same label
     * And another regular edge to a different label
     * <pre>
     *     {
     *         "id": 21,
     *         "inE": {
     *             "org.onap.relationships.inventory.isA": [
     *                  {
     *                      "id": 10,
     *                      "properties": {
     *                        "aai-uuid": "oafjdsiofjs",
     *                        "private": true
     *                      }
     *                  }
     *             ],
     *             "org.onap.relationships.inventory.BelongsTo": [
     *                  {
     *                      "id": 13,
     *                      "properties": {
     *                        "aai-uuid": "oafjdsiofjs",
     *                        "private": true
     *                      }
     *                  }
     *             ]
     *         }
     *         "label": "model-ver",
     *         "properties": {
     *             "aai-node-type": [
     *                  {
     *                      "id": 5,
     *                      "value": "model-ver"
     *                  }
     *             ]
     *         }
     *     }
     * </pre>
     *
     * @throws AAIException
     */
    @Test
    public void testWhenMultipleEdgeLabelsBothOnlyHavePrivateEdges() throws AAIException, EdgeRuleNotFoundException, AmbiguousRuleChoiceException {

        Vertex genericVnf = graph.addVertex(
                T.label, "generic-vnf",
                T.id, "20",
                "aai-node-type", "generic-vnf",
                "vnf-id", "vnf-id-1",
                "vnf-name", "vnf-name-1"
        );

        Vertex modelVer = graph.addVertex(
                T.label, "model-ver",
                T.id, "21",
                "aai-node-type", "model-ver",
                "model-version-id", "modelVer1",
                "model-name", "modelName1"
        );

        Vertex modelPrivate = graph.addVertex(
                T.label, "model-private",
                T.id, "22",
                "aai-node-type", "model-private"
        );



        GraphTraversalSource source = graph.traversal();
        edgeSer.addPrivateEdge(source, genericVnf, modelVer, null);
        edgeSer.addPrivateEdge(source, modelVer, modelPrivate, null);

        Optional<JsonObject> jsonObjectOptional = graphSON.formatObject(modelVer);
        JsonObject obj = jsonObjectOptional.get();
        LOGGER.info(obj.toString());
        assertNotNull(obj);
        String value = obj.toString();
        assertThat(value, not(containsString("\"private\":true")));
        assertThat(value, not(containsString("inventory.BelongsTo")));
        assertThat(value, not(containsString("inE")));
    }

    /**
     * Case where there is one private edge and regular edge to same label
     * And another regular edge to a different label
     * <pre>
     *     {
     *         "id": 21,
     *         "inE": {
     *             "org.onap.relationships.inventory.isA": [
     *                  {
     *                      "id": 10,
     *                      "properties": {
     *                        "aai-uuid": "oafjdsiofjs",
     *                        "private": true
     *                      }
     *                  }
     *             ],
     *             "org.onap.relationships.inventory.BelongsTo": [
     *                  {
     *                      "id": 13,
     *                      "properties": {
     *                        "aai-uuid": "oafjdsiofjs",
     *                        "private": true
     *                      }
     *                  },
     *                  {
     *                      "id": 13,
     *                      "properties": {
     *                        "aai-uuid": "jaosjfaisj",
     *                        "private": false
     *                      }
     *                  }
     *             ]
     *         }
     *         "label": "model-ver",
     *         "properties": {
     *             "aai-node-type": [
     *                  {
     *                      "id": 5,
     *                      "value": "model-ver"
     *                  }
     *             ]
     *         }
     *     }
     * </pre>
     *
     * @throws AAIException
     */
    @Test
    public void testWhenMultipleEdgeLabelsBothHavePrivateEdgesButOneHasTreeEdgeAndPrivateEdge() throws AAIException, EdgeRuleNotFoundException, AmbiguousRuleChoiceException {

        Vertex genericVnf = graph.addVertex(
                T.label, "generic-vnf",
                T.id, "20",
                "aai-node-type", "generic-vnf",
                "vnf-id", "vnf-id-1",
                "vnf-name", "vnf-name-1"
        );

        Vertex modelVer = graph.addVertex(
                T.label, "model-ver",
                T.id, "21",
                "aai-node-type", "model-ver",
                "model-version-id", "modelVer1",
                "model-name", "modelName1"
        );

        Vertex modelPrivate = graph.addVertex(
                T.label, "model-private",
                T.id, "22",
                "aai-node-type", "model-private"
        );

        Vertex metadatum = graph.addVertex(
                T.label, "metadatum",
                T.id, "23",
                "aai-node-type", "metadatum"
        );

        GraphTraversalSource source = graph.traversal();
        edgeSer.addPrivateEdge(source, genericVnf, modelVer, null);
        edgeSer.addPrivateEdge(source, modelVer, modelPrivate, null);
        edgeSer.addTreeEdge(source, modelVer, metadatum);

        Optional<JsonObject> jsonObjectOptional = graphSON.formatObject(modelVer);
        JsonObject obj = jsonObjectOptional.get();
        LOGGER.info(obj.toString());
        assertNotNull(obj);
        String value = obj.toString();
        assertThat(value, not(containsString("\"private\":true")));
        assertThat(value, containsString("inventory.BelongsTo"));
    }

    @Test
    public void parallelThresholdCehck(){
        
        GraphSON graphSonObj2 = new GraphSON();
        assertEquals(50, graphSonObj2.parallelThreshold());
    
    }
}
