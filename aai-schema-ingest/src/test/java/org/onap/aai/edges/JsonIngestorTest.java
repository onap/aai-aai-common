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

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;
import static org.junit.Assert.*;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.Filter;

import java.util.*;

import org.junit.Test;
import org.onap.aai.setup.SchemaVersion;

public class JsonIngestorTest {

    private SchemaVersion LATEST = new SchemaVersion("v14");
    private SchemaVersion V10 = new SchemaVersion("v10");
    private SchemaVersion V11 = new SchemaVersion("v11");

    @Test
    public void test() {
        // setup
        List<String> files = new ArrayList<>();
        files.add("src/test/resources/edgeRules/test.json");
        files.add("src/test/resources/edgeRules/test2.json");
        files.add("src/test/resources/edgeRules/otherTestRules.json");
        Map<SchemaVersion, List<String>> input = new TreeMap<>();
        input.put(LATEST, files);

        List<String> files2 = new ArrayList<>();
        files2.add("src/test/resources/edgeRules/test.json");
        input.put(V10, files2);

        List<String> files3 = new ArrayList<>();
        files3.add("src/test/resources/edgeRules/test3.json");
        files3.add("src/test/resources/edgeRules/defaultEdgesTest.json");
        input.put(V11, files3);

        // test
        JsonIngestor ji = new JsonIngestor();
        Map<SchemaVersion, List<DocumentContext>> results = ji.ingest(input);

        assertTrue(results.entrySet().size() == 3);
        assertTrue(results.get(LATEST).size() == 3);
        assertTrue(results.get(V11).size() == 2);
        assertTrue(results.get(V10).size() == 1);

        Filter f = filter(where("from").is("foo").and("contains-other-v").is("NONE"));
        List<Map<String, String>> filterRes = results.get(V10).get(0).read("$.rules.[?]", f);
        assertTrue(filterRes.size() == 2);
    }

}
