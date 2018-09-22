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

import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.junit.Ignore;
import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.exceptions.AAIException;

import org.onap.aai.introspection.ModelType;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.JanusGraphDBEngine;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.setup.SchemaVersion;

import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBException;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import static org.junit.Assert.assertEquals;


@Ignore
public class LegacyQueryTest extends AAISetup {


    private TransactionalGraphEngine dbEngine;
    private SchemaVersion version;
    private DynamicJAXBContext context = nodeIngestor.getContextForVersion(version);

    public void setup(){
        version = new SchemaVersion("v8");
        dbEngine =
            new JanusGraphDBEngine(QueryStyle.GREMLIN_TRAVERSAL,
                loaderFactory.createLoaderForVersion(ModelType.MOXY, version),
                false);
    }

    /**
     * Parent query.
     *
     * @throws JAXBException the JAXB exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    @Test
    public void parentQuery() throws JAXBException, UnsupportedEncodingException, AAIException {
        
        URI uri = UriBuilder.fromPath("cloud-infrastructure/pservers/pserver/key1").build();

        QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);

        String expected = 
                ".has('hostname', 'key1').has('aai-node-type', 'pserver')";
        assertEquals(
                "gremlin query should be " + expected,
                expected,
                query.getQueryBuilder().getQuery());
        assertEquals(
                "parent gremlin query should be equal to normal query",
                expected,
                query.getQueryBuilder().getParentQuery().getQuery());
        assertEquals(
                "result type should be pserver",
                "pserver",
                query.getResultType());
        
    }

    /**
     * Child query.
     *
     * @throws JAXBException the JAXB exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    @Test
    public void childQuery() throws JAXBException, UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder.fromPath("cloud-infrastructure/pservers/pserver/key1/lag-interfaces/lag-interface/key2").build();
        QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);

        String expected =
                ".has('hostname', 'key1').has('aai-node-type', 'pserver')"
                + ".out('hasLAGInterface').has('aai-node-type', 'lag-interface')"
                + ".has('interface-name', 'key2')";
        String parentExpected = 
                ".has('hostname', 'key1').has('aai-node-type', 'pserver')";
        assertEquals(
                "gremlin query should be for node",
                expected,
                query.getQueryBuilder().getQuery());
        assertEquals(
                "parent gremlin query should be for parent",
                parentExpected,
                query.getQueryBuilder().getParentQuery().getQuery());
        assertEquals(
                "result type should be lag-interface",
                "lag-interface",
                query.getResultType());
    }
    
    /**
     * Naming exceptions.
     *
     * @throws JAXBException the JAXB exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    @Test
    public void namingExceptions() throws JAXBException, UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder.fromPath("network/vces/vce/key1/port-groups/port-group/key2/cvlan-tags/cvlan-tag/655").build();
    
        QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);
        String expected = 
                ".has('vnf-id', 'key1').has('aai-node-type', 'vce')"
                + ".in('org.onap.relationships.inventory.BelongsTo').has('aai-node-type', 'port-group')"
                + ".has('interface-id', 'key2')"
                + ".in('org.onap.relationships.inventory.BelongsTo').has('aai-node-type', 'cvlan-tag')"
                + ".has('cvlan-tag', 655)";
        String expectedParent = 
                        ".has('vnf-id', 'key1').has('aai-node-type', 'vce')"
                        + ".in('org.onap.relationships.inventory.BelongsTo').has('aai-node-type', 'port-group')"
                        + ".has('interface-id', 'key2')";
        assertEquals(
                "gremlin query should be " + expected,
                expected,
                query.getQueryBuilder().getQuery());
        assertEquals(
                "parent gremlin query should be equal the query for port group",
                expectedParent,
                query.getQueryBuilder().getParentQuery().getQuery());
        assertEquals(
                "result type should be cvlan-tag",
                "cvlan-tag",
                query.getResultType());
        
    }
    
}
