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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.ws.rs.core.Response;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphTransaction;
import org.junit.Ignore;
import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.HttpTestUtil;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.dbmap.AAIGraph;
import org.onap.aai.serialization.engines.QueryStyle;

public class PserverDuplicateTest extends AAISetup {

    private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(PserverDuplicateTest.class);

    private HttpTestUtil testUtil;

    private String hostname;

    public boolean createDuplicate() throws InterruptedException {

        hostname = getHostname();
        final String aaiUri = "/cloud-infrastructure/pservers/pserver/" + hostname;
        final int threads = getNumberOfThreads();

        ExecutorService service = Executors.newFixedThreadPool(threads);

        JanusGraph janusGraph = AAIGraph.getInstance().getGraph();
        // Due to the lazy instantiation of the graph, it needs to create a new transaction to create schema
        janusGraph.newTransaction().rollback();

        service.invokeAll(IntStream.range(0, threads).mapToObj((i) -> (Callable<Void>) () -> {
            JanusGraphTransaction transaction = janusGraph.newTransaction();
            GraphTraversalSource g = transaction.traversal();
            try {
                g.addV().property(AAIProperties.AAI_URI, aaiUri).property(AAIProperties.NODE_TYPE, "pserver")
                        .property("hostname", hostname).next();
                transaction.commit();
            } catch (Exception e) {
                throw new Exception("Duplicate was found, error");
            }
            return null;
        }).collect(Collectors.toList()), 7, TimeUnit.SECONDS);

        JanusGraphTransaction readOnlyTransaction =
                AAIGraph.getInstance().getGraph().buildTransaction().readOnly().start();
        GraphTraversalSource g = readOnlyTransaction.traversal();

        List<Vertex> pserverList = g.V().has(AAIProperties.AAI_URI, aaiUri).toList();
        LOGGER.debug("Number of pservers with uri {} is {}", aaiUri, pserverList.size());

        testUtil = new HttpTestUtil(QueryStyle.TRAVERSAL_URI);

        if (pserverList.size() == 1) {
            return false;
        }
        return true;
    }

    @Ignore
    public void testWhenDuplicatesExistInGraphThatGetAllSuceeds() throws InterruptedException {

        int totalRetries = getNumOfRetries();
        for (int retry = 0; retry < totalRetries; retry++) {
            if (!this.createDuplicate()) {
                if (retry == (totalRetries - 1)) {
                    fail("Unable to produce duplicates in the graph, "
                            + "please increase retry or ignore test if it becomes impossible to create duplicate this test");
                }
            } else {
                // Successfully created a duplicate in the janus graph
                break;
            }
        }

        String endpoint = "/aai/v14/cloud-infrastructure/pservers";

        Response response = testUtil.doGet(endpoint, null);
        LOGGER.info("GET ALL Pservers with duplicates status code {} and body {}", response.getStatus(),
                response.getEntity());
        assertThat(response.getStatus(), is(200));
    }

    public String getHostname() {
        return UUID.randomUUID().toString();
    }

    public int getNumOfRetries() {
        return 10;
    }

    public int getNumberOfThreads() {
        return 10;
    }
}
