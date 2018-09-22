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
package org.onap.aai.parsers.query;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.junit.Ignore;
import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.exceptions.AAIException;

import org.onap.aai.introspection.ModelType;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.JanusGraphDBEngine;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;

import javax.ws.rs.core.UriBuilder;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import static org.junit.Assert.assertEquals;

@Ignore
public class UniqueURIQueryTest extends AAISetup {


    private TransactionalGraphEngine dbEngine;
    private SchemaVersion version;
    private DynamicJAXBContext context = nodeIngestor.getContextForVersion(version);
    
    /**
     * Parent query.
     *
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    @Test
    public void parentQuery() throws UnsupportedEncodingException, AAIException {
        version = new SchemaVersion("v8");
        dbEngine = new JanusGraphDBEngine(QueryStyle.GREMLIN_UNIQUE,
                loaderFactory.createLoaderForVersion(ModelType.MOXY, version),
                false);
        URI uri = UriBuilder.fromPath("cloud-infrastructure/complexes/complex/key1").build();
        String key = "complex/key1";
        QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);
        GraphTraversal<Vertex, Vertex> expected = __.<Vertex>start().has("aai-unique-key", key);
        String parentResultType = "";
        String resultType = "complex";
        String containerType = "";
        
        testSet(query, expected, expected, parentResultType, resultType, containerType);
        
    }
    
    /**
     * Parent plural query.
     *
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    @Test
    public void parentPluralQuery() throws UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder.fromPath("cloud-infrastructure/complexes").build();
        QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);
        GraphTraversal<Vertex, Vertex> expected = __.<Vertex>start().has("aai-node-type", "complex");
        String parentResultType = "";
        String resultType = "complex";
        String containerType = "complexes";
        
        testSet(query, expected, expected, parentResultType, resultType, containerType);
        
    }

    /**
     * Child query.
     *
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    @Test
    public void childQuery() throws UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder.fromPath("cloud-infrastructure/complexes/complex/key1/ctag-pools/ctag-pool/key2/key3").build();
        QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);
        String parentKey = "complex/key1";
        String key = parentKey + "/ctag-pool/key2/key3";
        GraphTraversal<Vertex, Vertex> expected = __.<Vertex>start().has("aai-unique-key", key);
        GraphTraversal<Vertex, Vertex> parentExpected =  __.<Vertex>start().has("aai-unique-key", parentKey);
        String parentResultType = "complex";
        String resultType = "ctag-pool";
        String containerType = "";
        
        testSet(query, expected, parentExpected, parentResultType, resultType, containerType);
        
    }
    
    /**
     * Naming exceptions.
     *
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    @Test
    public void namingExceptions() throws UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder.fromPath("network/vces/vce/key1/port-groups/port-group/key2/cvlan-tags/cvlan-tag/655").build();
        QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);
        String parentKey = "vce/key1/port-group/key2";
        String key = parentKey + "/cvlan-tag/655";
        GraphTraversal<Vertex, Vertex> expected = __.<Vertex>start().has("aai-unique-key", key);
        GraphTraversal<Vertex, Vertex> parentExpected = __.<Vertex>start().has("aai-unique-key", parentKey);
        String parentResultType = "port-group";
        String resultType = "cvlan-tag";
        String containerType = "";
        
        testSet(query, expected, parentExpected, parentResultType, resultType, containerType);
        
    }
    
    /**
     * Gets the all.
     *
     * @return the all
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    @Test
    public void getAll() throws UnsupportedEncodingException, AAIException {
        String parentURI = "network/vces/vce/key1/port-groups/port-group/key2";
        String parentKey = "vce/key1/port-group/key2";
        URI uri = UriBuilder.fromPath(parentURI + "/cvlan-tags").build();
        QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);
        GraphTraversal<Vertex, Vertex> expected = __.<Vertex>start().has("aai-unique-key", parentKey).in("org.onap.relationships.inventory.BelongsTo").has("aai-node-type", "cvlan-tag");
        GraphTraversal<Vertex, Vertex> parentExpected = __.<Vertex>start().has("aai-unique-key",parentKey);
        String parentResultType = "port-group";
        String resultType = "cvlan-tag";
        String containerType = "cvlan-tags";
        
        testSet(query, expected, parentExpected, parentResultType, resultType, containerType);
        
    }
    
    /**
     * Test set.
     *
     * @param query the query
     * @param expected the expected
     * @param parentExpected the parent expected
     * @param parentResultType the parent result type
     * @param resultType the result type
     * @param containerType the container type
     */
    public void testSet(QueryParser query, GraphTraversal<Vertex, Vertex> expected, GraphTraversal<Vertex, Vertex> parentExpected, String parentResultType, String resultType, String containerType) {
        assertEquals(
                "gremlin query should be " + expected,
                expected,
                query.getQueryBuilder().getQuery());
        assertEquals(
                "parent gremlin query should be " + parentExpected,
                parentExpected,
                query.getQueryBuilder().getParentQuery().getQuery());
        assertEquals(
                "parent result type should be " + parentResultType,
                parentResultType,
                query.getParentResultType());
        assertEquals(
                "result type should be " + resultType,
                resultType,
                query.getResultType());
        assertEquals(
                "container type should be " + containerType,
                containerType,
                query.getContainerType());
    }
}
