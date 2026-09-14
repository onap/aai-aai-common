/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2026 Deutsche Telekom. All rights reserved.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.aai.setup.SchemaVersion;

public class SchemaFilterTest {

    private DocumentContext testRules;
    private static final String READ_START = "$.rules.[?]";

    @BeforeEach
    public void setup() {
        InputStream is = getClass().getResourceAsStream("/edgeRules/test.json");
        Scanner scanner = new Scanner(is);
        String json = scanner.useDelimiter("\\Z").next();
        scanner.close();
        this.testRules = JsonPath.parse(json);
    }

    @Test
    public void filterSurvivesToStringAndParseRoundTrip() {
        // this is exactly what EdgeIngestor.extractRules(SchemaFilter) does:
        // build a Filter, stash it as a String via SchemaFilter's constructor,
        // then Filter.parse() that String back before reading with it.
        Filter original = Filter.filter(Criteria.where("from").is("foo"));
        List<Object> expected = testRules.read(READ_START, original);

        SchemaFilter schemaFilter = new SchemaFilter(original, new SchemaVersion("v1"));
        Filter parsedBack = Filter.parse(schemaFilter.getFilter());
        List<Object> actual = testRules.read(READ_START, parsedBack);

        assertEquals(expected.size(), actual.size());
        assertEquals(3, actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void inOperatorSurvivesToStringAndParseRoundTrip() {
        // sole production caller: EdgeRuleQuery.addDirection()
        Filter original = Filter.filter(Criteria.where("direction").in("OUT", "BOTH"));
        List<Object> expected = testRules.read(READ_START, original);

        SchemaFilter schemaFilter = new SchemaFilter(original, new SchemaVersion("v1"));
        Filter parsedBack = Filter.parse(schemaFilter.getFilter());
        List<Object> actual = testRules.read(READ_START, parsedBack);

        assertEquals(expected.size(), actual.size());
        assertEquals(2, actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void neOperatorSurvivesToStringAndParseRoundTrip() {
        // sole production caller: EdgeRuleQuery.addType(EdgeType.TREE)
        Filter original = Filter.filter(Criteria.where("contains-other-v").ne("NONE"));
        List<Object> expected = testRules.read(READ_START, original);

        SchemaFilter schemaFilter = new SchemaFilter(original, new SchemaVersion("v1"));
        Filter parsedBack = Filter.parse(schemaFilter.getFilter());
        List<Object> actual = testRules.read(READ_START, parsedBack);

        assertEquals(expected.size(), actual.size());
        assertEquals(2, actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void nullFilterProducesNullFilterString() {
        SchemaFilter schemaFilter = new SchemaFilter(null, new SchemaVersion("v1"));
        assertNull(schemaFilter.getFilter());
    }

    @Test
    public void equalsAndHashCodeAreBasedOnFilterStringAndVersion() {
        SchemaVersion v1 = new SchemaVersion("v1");
        Filter filterA = Filter.filter(Criteria.where("from").is("foo"));
        Filter filterB = Filter.filter(Criteria.where("from").is("foo"));

        SchemaFilter a = new SchemaFilter(filterA, v1);
        SchemaFilter b = new SchemaFilter(filterB, v1);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void differentFilterStringsAreNotEqual() {
        SchemaVersion v1 = new SchemaVersion("v1");
        SchemaFilter a = new SchemaFilter(Filter.filter(Criteria.where("from").is("foo")), v1);
        SchemaFilter b = new SchemaFilter(Filter.filter(Criteria.where("from").is("bar")), v1);

        assertNotEquals(a, b);
    }

    @Test
    public void toStringIncludesFilterAndVersion() {
        SchemaVersion v1 = new SchemaVersion("v1");
        SchemaFilter schemaFilter = new SchemaFilter(Filter.filter(Criteria.where("from").is("foo")), v1);

        String result = schemaFilter.toString();
        assertTrue(result.contains("v1"));
    }
}
