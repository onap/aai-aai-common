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

package org.onap.aai.stress;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraphTransaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.onap.aai.AAISetup;
import org.onap.aai.dbmap.AAIGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Ignore("Run this only to test indexes limit")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class IndexStressTest extends AAISetup {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexStressTest.class);

    @Before
    public void setup(){
        AAIGraph.getInstance().getGraph();
    }

    @Test
    public void testIndexStress(){
        JanusGraphTransaction tx = AAIGraph.getInstance().getGraph().newTransaction();

        GraphTraversalSource g = tx.traversal();

        Set<String> linkNameSet = new HashSet<>();
        Set<String> aaiUriSet = new HashSet<>();

        int TOTAL_LINKS = 101000;

        for(int i = 0; i <TOTAL_LINKS; i++){

            String linkName = generateName(linkNameSet);
            aaiUriSet.add("/network/logical-links/logical-link/" + linkName);

            Vertex v = g.addV()
                .property("aai-node-type", "logical-link")
                .property("link-name", linkName)
                .property("aai-uri", "/network/logical-links/logical-link/" + linkName)
                .next();

            if(i % 1000 == 0){
                LOGGER.debug("Committing up to index {}", i);
                tx.commit();
                tx = AAIGraph.getInstance().getGraph().newTransaction();
                g = tx.traversal();
            }
        }

        tx.commit();

        tx = AAIGraph.getInstance().getGraph().newTransaction();
        g = tx.traversal();

        int totalLinks= 0;
        int totalLinksWithNodeType = 0;
        int totalLinksUsingUri = 0;

        int index = 0;
        for (String linkName : linkNameSet) {

            if(g.V().has("aai-node-type", "logical-link").has("link-name", linkName).hasNext()){
               totalLinksWithNodeType++;
            }

            if(g.V().has("link-name", linkName).hasNext()){
                totalLinks++;
            }

            if(g.V().has("aai-uri", "/network/logical-links/logical-link/" + linkName).hasNext()){
                totalLinksUsingUri++;
            }

            index++;

            if(index%1000 == 0){
                LOGGER.debug("Processed {} many queries and has {} many to go", index, (TOTAL_LINKS-index));
                LOGGER.debug("Total links using linkname found: {}", totalLinks);
                LOGGER.debug("Total links using nodetype and linkname found: {}", totalLinksWithNodeType);
                LOGGER.debug("Total links using uri found: {}", totalLinksUsingUri);
            }
        }

        tx.rollback();

        LOGGER.debug("Total links using linkname found: {}", totalLinks);
        LOGGER.debug("Total links using nodetype and linkname found: {}", totalLinksWithNodeType);
        LOGGER.debug("Total links using uri found: {}", totalLinksUsingUri);
    }

    String generateName(Set<String> uniqueKeys){

        while(true) {
            String data = RandomStringUtils.randomAlphabetic(20);
            if (!uniqueKeys.contains(data)){
                uniqueKeys.add(data);
                return data;
            }
        }
    }
}
