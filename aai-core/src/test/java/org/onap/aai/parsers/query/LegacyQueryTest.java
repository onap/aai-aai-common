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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.serialization.engines.JanusGraphDBEngine;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.setup.SchemaVersion;

@Disabled
public class LegacyQueryTest extends AAISetup {

    private TransactionalGraphEngine dbEngine;
    private SchemaVersion version;

    public void setup() {
        version = new SchemaVersion("v10");
        dbEngine = new JanusGraphDBEngine(QueryStyle.GREMLIN_TRAVERSAL,
                loaderFactory.createLoaderForVersion(ModelType.MOXY, version), false);
    }

    /**
     * Parent query.
     *
     * @throws JAXBException the JAXB exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    @Test
    public void parentQuery() throws UnsupportedEncodingException, AAIException {

        URI uri = UriBuilder.fromPath("cloud-infrastructure/pservers/pserver/key1").build();

        QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);

        String expected = ".has('hostname', 'key1').has('aai-node-type', 'pserver')";
        assertEquals(expected, query.getQueryBuilder().getQuery(), "gremlin query should be " + expected);
        assertEquals(expected,
                query.getQueryBuilder().getParentQuery().getQuery(),
                "parent gremlin query should be equal to normal query");
        assertEquals("pserver", query.getResultType(), "result type should be pserver");

    }

    /**
     * Child query.
     *
     * @throws JAXBException the JAXB exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    @Test
    public void childQuery() throws UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder.fromPath("cloud-infrastructure/pservers/pserver/key1/lag-interfaces/lag-interface/key2")
                .build();
        QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);

        String expected = ".has('hostname', 'key1').has('aai-node-type', 'pserver')"
                + ".out('hasLAGInterface').has('aai-node-type', 'lag-interface')" + ".has('interface-name', 'key2')";
        String parentExpected = ".has('hostname', 'key1').has('aai-node-type', 'pserver')";
        assertEquals(expected, query.getQueryBuilder().getQuery(), "gremlin query should be for node");
        assertEquals(parentExpected,
                query.getQueryBuilder().getParentQuery().getQuery(),
                "parent gremlin query should be for parent");
        assertEquals("lag-interface", query.getResultType(), "result type should be lag-interface");
    }

    /**
     * Naming exceptions.
     *
     * @throws JAXBException the JAXB exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    @Test
    public void namingExceptions() throws UnsupportedEncodingException, AAIException {
        URI uri = UriBuilder.fromPath("network/vces/vce/key1/port-groups/port-group/key2/cvlan-tags/cvlan-tag/655")
                .build();

        QueryParser query = dbEngine.getQueryBuilder().createQueryFromURI(uri);
        String expected = ".has('vnf-id', 'key1').has('aai-node-type', 'vce')"
                + ".in('org.onap.relationships.inventory.BelongsTo').has('aai-node-type', 'port-group')"
                + ".has('interface-id', 'key2')"
                + ".in('org.onap.relationships.inventory.BelongsTo').has('aai-node-type', 'cvlan-tag')"
                + ".has('cvlan-tag', 655)";
        String expectedParent = ".has('vnf-id', 'key1').has('aai-node-type', 'vce')"
                + ".in('org.onap.relationships.inventory.BelongsTo').has('aai-node-type', 'port-group')"
                + ".has('interface-id', 'key2')";
        assertEquals(expected, query.getQueryBuilder().getQuery(), "gremlin query should be " + expected);
        assertEquals(expectedParent,
                query.getQueryBuilder().getParentQuery().getQuery(),
                "parent gremlin query should be equal the query for port group");
        assertEquals("cvlan-tag", query.getResultType(), "result type should be cvlan-tag");

    }

}
