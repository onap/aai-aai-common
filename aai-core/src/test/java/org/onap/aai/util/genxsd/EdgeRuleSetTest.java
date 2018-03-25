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
package org.onap.aai.util.genxsd;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class EdgeRuleSetTest {
	private static File edgesFile = new File("edges.json");
	private static String nodeName = "availability-zone";
	private DocumentContext jsonContext;
	private String json;
	private EdgeRuleSet edgeSet;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		json = "{"
				+ "	\"rules\": ["
				+ "		{"
				+ "			\"from\": \"availability-zone\","
				+ "			\"to\": \"complex\","
				+ "			\"label\": \"org.onap.relationships.inventory.LocatedIn\","
				+ "			\"direction\": \"OUT\","
				+ "			\"multiplicity\": \"MANY2ONE\","
				+ "			\"contains-other-v\": \"NONE\","
				+ "			\"delete-other-v\": \"NONE\","
				+ "			\"SVC-INFRA\": \"NONE\","
				+ "			\"prevent-delete\": \"!${direction}\","
				+ "			\"default\": \"true\","
				+ "			\"description\":\"this description\""
				+ "		},"
				+ "    {"
				+ "			\"from\": \"availability-zone\","
				+ "			\"to\": \"service-capability\","
				+ "			\"label\": \"org.onap.relationships.inventory.AppliesTo\","
				+ "			\"direction\": \"OUT\","
				+ "			\"multiplicity\": \"MANY2MANY\","
				+ "			\"contains-other-v\": \"NONE\","
				+ "			\"delete-other-v\": \"NONE\","
				+ "			\"SVC-INFRA\": \"NONE\","
				+ "			\"prevent-delete\": \"!${direction}\","
				+ "			\"default\": \"true\","
				+ "			\"description\":\"\""
				+ "		},"
				+ "		{"
				+ "			\"from\": \"availability-zone\","
				+ "			\"to\": \"cloud-region\","
				+ "			\"label\": \"org.onap.relationships.inventory.BelongsTo\","
				+ "			\"direction\": \"OUT\","
				+ "			\"multiplicity\": \"MANY2ONE\","
				+ "			\"contains-other-v\": \"!${direction}\","
				+ "			\"delete-other-v\": \"!${direction}\","
				+ "			\"SVC-INFRA\": \"NONE\","
				+ "			\"prevent-delete\": \"NONE\","
				+ "			\"default\": \"true\","
				+ "			\"description\":\"\""
				+ "		},"
				+ "		{"
				+ "			\"from\": \"ctag-pool\","
				+ "			\"to\": \"availability-zone\","
				+ "			\"label\": \"org.onap.relationships.inventory.AppliesTo\","
				+ "			\"direction\": \"OUT\","
				+ "			\"multiplicity\": \"MANY2MANY\","
				+ "			\"contains-other-v\": \"NONE\","
				+ "			\"delete-other-v\": \"NONE\","
				+ "			\"SVC-INFRA\": \"NONE\","
				+ "			\"prevent-delete\": \"!${direction}\","
				+ "			\"default\": \"true\","
				+ "			\"description\":\"\""
				+ "		},"
				+ "		{"
				+ "			\"from\": \"dvs-switch\","
				+ "			\"to\": \"availability-zone\","
				+ "			\"label\": \"org.onap.relationships.inventory.AppliesTo\","
				+ "			\"direction\": \"OUT\","
				+ "			\"multiplicity\": \"MANY2MANY\","
				+ "			\"contains-other-v\": \"NONE\","
				+ "			\"delete-other-v\": \"NONE\","
				+ "			\"SVC-INFRA\": \"NONE\","
				+ "			\"prevent-delete\": \"!${direction}\","
				+ "			\"default\": \"true\","
				+ "			\"description\":\"\""
				+ "		},"
				+ "		{"
				+ "			\"from\": \"generic-vnf\","
				+ "			\"to\": \"availability-zone\","
				+ "			\"label\": \"org.onap.relationships.inventory.Uses\","
				+ "			\"direction\": \"OUT\","
				+ "			\"multiplicity\": \"MANY2MANY\","
				+ "			\"contains-other-v\": \"NONE\","
				+ "			\"delete-other-v\": \"NONE\","
				+ "			\"SVC-INFRA\": \"${direction}\","
				+ "			\"prevent-delete\": \"!${direction}\","
				+ "			\"default\": \"true\","
				+ "			\"description\":\"\""
				+ "		},"
				+ "		{"
				+ "			\"from\": \"vf-module\","
				+ "			\"to\": \"vnfc\","
				+ "			\"label\": \"org.onap.relationships.inventory.Uses\","
				+ "			\"direction\": \"OUT\","
				+ "			\"multiplicity\": \"ONE2MANY\","
				+ "			\"contains-other-v\": \"NONE\","
				+ "			\"delete-other-v\": \"NONE\","
				+ "			\"SVC-INFRA\": \"${direction}\","
				+ "			\"prevent-delete\": \"${direction}\","
				+ "			\"default\": \"true\","
				+ "			\"description\":\"\""
				+ "		},"
				+ "		{"
				+ "			\"from\": \"pserver\","
				+ "			\"to\": \"availability-zone\","
				+ "			\"label\": \"org.onap.relationships.inventory.MemberOf\","
				+ "			\"direction\": \"OUT\","
				+ "			\"multiplicity\": \"MANY2ONE\","
				+ "			\"contains-other-v\": \"NONE\","
				+ "			\"delete-other-v\": \"NONE\","
				+ "			\"SVC-INFRA\": \"${direction}\","
				+ "			\"prevent-delete\": \"!${direction}\","
				+ "			\"default\": \"true\","
				+ "			\"description\":\"\""
				+ "		},"
				+ "		{"
				+ "			\"from\": \"vce\","
				+ "			\"to\": \"availability-zone\","
				+ "			\"label\": \"org.onap.relationships.inventory.Uses\","
				+ "			\"direction\": \"OUT\","
				+ "			\"multiplicity\": \"MANY2MANY\","
				+ "			\"contains-other-v\": \"NONE\","
				+ "			\"delete-other-v\": \"NONE\","
				+ "			\"SVC-INFRA\": \"NONE\","
				+ "			\"prevent-delete\": \"!${direction}\","
				+ "			\"default\": \"true\","
				+ "			\"description\":\"\""
				+ "		},"
				+ "	]}";
        FileWriter file = new FileWriter(edgesFile);
        file.write(json);
        file.flush();
        file.close();
        jsonContext = JsonPath.parse(json);

	}

	@Test
	public void testEdgeRuleSetFile() throws FileNotFoundException, IOException {
		this.edgeSet = new EdgeRuleSet(edgesFile);
		Collection<EdgeDescription>edges = edgeSet.getEdgeRules(nodeName);
		assertThat(edges.size(), is(8));
	}

	@Test
	public void testEdgeRuleSetDocumentContext() {		
		this.edgeSet = new EdgeRuleSet(jsonContext);
		Collection<EdgeDescription>edges = edgeSet.getEdgeRules(nodeName);
		assertThat(edges.size(), is(8));
	}

	@Test
	public void testGetEdgeRules() {
		this.edgeSet = new EdgeRuleSet(jsonContext);
		Collection<EdgeDescription>edges = edgeSet.getEdgeRules(nodeName);
		assertThat(edges.size(), is(8));
	}

	@Test
	public void testGetEdgeRulesTO() {
		this.edgeSet = new EdgeRuleSet(jsonContext);
		Collection<EdgeDescription>edges = edgeSet.getEdgeRulesTO(nodeName);
		assertThat(edges.size(), is(5));
	}

	@Test
	public void testGetEdgeRulesFROM() {
		this.edgeSet = new EdgeRuleSet(jsonContext);
		Collection<EdgeDescription>edges = edgeSet.getEdgeRulesFROM(nodeName);
		assertThat(edges.size(), is(3));
	}

	@Test
	public void testGetEdgeRulesFromJson() {
		String fromRulesPath = "$['rules'][?(@['from']=='" + nodeName + "')]";
		this.edgeSet = new EdgeRuleSet(jsonContext);
		Collection<EdgeDescription> edges = edgeSet.getEdgeRulesFromJson( fromRulesPath, true );
		assertThat(edges.size(), is(3));
	}

	@Test
	public void testPreventDeleteRules() {
		String target = "AVAILABILITY-ZONE cannot be deleted if related to CTAG-POOL,DVS-SWITCH,GENERIC-VNF,PSERVER,VCE\n";
		this.edgeSet = new EdgeRuleSet(jsonContext);
		String fromDeleteRules = edgeSet.preventDeleteRules(nodeName);
		assertThat(fromDeleteRules, is(target));
	}

	@Test
	public void testFromDeleteRules() {
		String target = "VF-MODULE cannot be deleted if related to VNFC\n";
		this.edgeSet = new EdgeRuleSet(jsonContext);
		String fromDeleteRules = edgeSet.fromDeleteRules("vf-module");
		assertThat(fromDeleteRules, is(target));
	}
	
	@After
	public void tearDown() throws Exception {
		edgesFile.delete();
	}

}
