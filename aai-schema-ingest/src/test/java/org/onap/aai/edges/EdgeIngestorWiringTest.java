/** 
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-18 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.edges;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.config.EdgesConfiguration;
import org.onap.aai.edges.exceptions.EdgeRuleNotFoundException;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersionsBean;

import org.onap.aai.testutils.ConfigTranslatorForWiringTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Multimap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EdgesConfiguration.class, ConfigTranslatorForWiringTest.class})
@TestPropertySource(properties = {"schema.ingest.file = src/test/resources/forWiringTests/schema-ingest-wiring-test-local.properties"})
@SpringBootTest
public class EdgeIngestorWiringTest {
    @Autowired
    EdgeIngestor ei;
    
    @Test
    public void test() throws EdgeRuleNotFoundException {
        assertNotNull(ei);
        EdgeRuleQuery q = new EdgeRuleQuery.Builder("quux", "foo").label("dancesWith").version(new SchemaVersion("v10")).build();
        Multimap<String, EdgeRule> results = ei.getRules(q);
        assertTrue(results.size() == 1);
        assertTrue(results.containsKey("foo|quux"));
    }

}
