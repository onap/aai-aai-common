/** 
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */

package org.onap.aai.edges;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.onap.aai.setup.Version;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.Filter;
import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;

public class JsonIngestorTest {

	@Test
	public void test() {
		//setup
		List<String> files = new ArrayList<>();
		files.add("src/test/resources/edgeRules/test.json");
		files.add("src/test/resources/edgeRules/test2.json");
		files.add("src/test/resources/edgeRules/otherTestRules.json");
		Map<Version, List<String>> input = new EnumMap<>(Version.class);
		input.put(Version.getLatest(), files);
		
		List<String> files2 = new ArrayList<>();
		files2.add("src/test/resources/edgeRules/test.json");
		input.put(Version.V10, files2);
		
		List<String> files3 = new ArrayList<>();
		files3.add("src/test/resources/edgeRules/test3.json");
		files3.add("src/test/resources/edgeRules/defaultEdgesTest.json");
		input.put(Version.V11, files3);
		
		//test
		JsonIngestor ji = new JsonIngestor();
		Map<Version, List<DocumentContext>> results = ji.ingest(input);
		
		assertTrue(results.entrySet().size() == 3);
		assertTrue(results.get(Version.getLatest()).size() == 3);
		assertTrue(results.get(Version.V11).size() == 2);
		assertTrue(results.get(Version.V10).size() == 1);
		
		Filter f = filter(where("from").is("foo").and("contains-other-v").is("NONE"));
		List<Map<String, String>> filterRes = results.get(Version.V10).get(0).read("$.rules.[?]",f);
		assertTrue(filterRes.size() == 2);
	}

}
