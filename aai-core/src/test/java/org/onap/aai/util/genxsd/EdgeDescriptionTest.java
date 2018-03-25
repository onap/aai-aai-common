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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class EdgeDescriptionTest {
	private DocumentContext jsonContext;
	private EdgeRuleSet edgeSet;
	private Collection<EdgeDescription>edges;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		String json = "{"
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
/*
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
*/
				+ "	]}";
				jsonContext = JsonPath.parse(json);
				this.edgeSet = new EdgeRuleSet(jsonContext);
				String nodeName = "availability-zone";
				edges = edgeSet.getEdgeRules(nodeName);
	}

	@Test
	public void testGetDeleteOtherV() {
		String target = "availability-zone"+"|"+"complex"+"-"+"NONE";
		for (EdgeDescription ed : edges) {
			String modResult = ed.getRuleKey() + "-" + ed.getDeleteOtherV();
			assertThat(modResult, is(target));
		}
	}

	@Test
	public void testSetDeleteOtherV() {
		String target = "availability-zone"+"|"+"complex"+"-"+"IN";
		for (EdgeDescription ed : edges) {
			ed.setDeleteOtherV("IN");
			String modResult = ed.getRuleKey() + "-" + ed.getDeleteOtherV();
			assertThat(modResult, is(target));
		}
	}

	@Test
	public void testGetPreventDelete() {
		String target = "availability-zone"+"|"+"complex"+"-"+"IN";
		for (EdgeDescription ed : edges) {
			String modResult = ed.getRuleKey() + "-" + ed.getPreventDelete();
			assertThat(modResult, is(target));
		}
	}

	@Test
	public void testSetPreventDelete() {
		for (EdgeDescription ed : edges) {
			String target = "availability-zone"+"|"+"complex"+"-"+"OUT";
			ed.setPreventDelete("OUT");
			String modResult = ed.getRuleKey() + "-" + ed.getPreventDelete();
			assertThat(modResult, is(target));
		}
	}

	@Test
	public void testGetAlsoDeleteFootnote() {
		String target = "availability-zone"+"|"+"complex"+"-"+"";
		for (EdgeDescription ed : edges) {
			String modResult = ed.getRuleKey() + "-" + ed.getAlsoDeleteFootnote(ed.getFrom());
			assertThat(modResult, is(target));
			ed.setDeleteOtherV("IN");
			target = "availability-zone"+"|"+"complex"+"-"+"(4)";
			modResult = ed.getRuleKey() + "-" + ed.getAlsoDeleteFootnote(ed.getFrom());
			assertThat(modResult, is(target));
			target = "availability-zone"+"|"+"complex"+"-"+"(1)";
			modResult = ed.getRuleKey() + "-" + ed.getAlsoDeleteFootnote(ed.getTo());
			assertThat(modResult, is(target));
			ed.setDeleteOtherV("OUT");
			target = "availability-zone"+"|"+"complex"+"-"+"(2)";
			modResult = ed.getRuleKey() + "-" + ed.getAlsoDeleteFootnote(ed.getFrom());
			assertThat(modResult, is(target));
			target = "availability-zone"+"|"+"complex"+"-"+"(3)";
			modResult = ed.getRuleKey() + "-" + ed.getAlsoDeleteFootnote(ed.getTo());
			assertThat(modResult, is(target));
		}
	}

	@Test
	public void testGetTo() {
		String target = "availability-zone"+"|"+"complex"+"-"+"complex";
		for (EdgeDescription ed : edges) {
			String modResult = ed.getRuleKey() + "-" + ed.getTo();
			assertThat(modResult, is(target));
		}
	}

	@Test
	public void testSetTo() {
		String target = "availability-zone"+"|"+"complex"+"-"+"jazz";
		for (EdgeDescription ed : edges) {
			ed.setTo("jazz");
			String modResult = ed.getRuleKey() + "-" + ed.getTo();
			assertThat(modResult, is(target));
		}
	}

	@Test
	public void testGetFrom() {
		String target = "availability-zone"+"|"+"complex"+"-"+"availability-zone";
		for (EdgeDescription ed : edges) {
			String modResult = ed.getRuleKey() + "-" + ed.getFrom();
			assertThat(modResult, is(target));
		}
	}

	@Test
	public void testSetFrom() {
		String target = "availability-zone"+"|"+"complex"+"-"+"jazz";
		for (EdgeDescription ed : edges) {
			ed.setFrom("jazz");
			String modResult = ed.getRuleKey() + "-" + ed.getFrom();
			assertThat(modResult, is(target));
		}
	}

	@Test
	public void testGetRuleKey() {
		for (EdgeDescription ed : edges) {
			String target = ed.getFrom()+"|"+ed.getTo();
			String modResult = ed.getRuleKey();
			assertThat(modResult, is(target));
		}
	}

	@Test
	public void testGetMultiplicity() {
		String target = "availability-zone"+"|"+"complex"+"-"+"MANY2ONE";
		for (EdgeDescription ed : edges) {
			String modResult = ed.getRuleKey() + "-" + ed.getMultiplicity();
			assertThat(modResult, is(target));
		}
	}

	@Test
	public void testGetDirection() {
		for (EdgeDescription ed : edges) {
			String target = ed.getFrom()+"|"+ed.getTo()+"-"+"OUT";
			String modResult = ed.getRuleKey() + "-" + ed.getDirection();
			assertThat(modResult, is(target));
		}
	}

	@Test
	public void testGetDescription() {
		for (EdgeDescription ed : edges) {
			String target = ed.getFrom()+"|"+ed.getTo()+"-"+"this description";
			String modResult = ed.getRuleKey() + "-" + ed.getDescription();
			assertThat(modResult, is(target));
		}
	}

	@Test
	public void testSetRuleKey() {
		for (EdgeDescription ed : edges) {
			ed.setRuleKey("A|B");
			String target = "A|B";
			String modResult = ed.getRuleKey();
			assertThat(modResult, is(target));
		}	}

	@Test
	public void testSetType() {
		String target = "availability-zone"+"|"+"complex"+"-"+"CHILD";
		for (EdgeDescription ed : edges) {
			ed.setType(EdgeDescription.LineageType.CHILD);
			String modResult = ed.getRuleKey() + "-" + ed.getType();
			assertThat(modResult, is(target));
		}
	}

	@Test
	public void testSetDirection() {
		String target = "availability-zone"+"|"+"complex"+"-"+"IN";
		for (EdgeDescription ed : edges) {
			ed.setDirection("IN");
			String modResult = ed.getRuleKey() + "-" + ed.getDirection();
			assertThat(modResult, is(target));
		}
	}

	@Test
	public void testSetMultiplicity() {
		String target = "availability-zone"+"|"+"complex"+"-"+"ONE2MANY";
		for (EdgeDescription ed : edges) {
			ed.setTo("ONE2MANY");
			String modResult = ed.getRuleKey() + "-" + ed.getTo();
			assertThat(modResult, is(target));
		}
	}

	@Test
	public void testSetDescription() {
		for (EdgeDescription ed : edges) {
			ed.setDescription("a new description");
			String target = ed.getFrom()+"|"+ed.getTo()+"-"+"a new description";
			String modResult = ed.getRuleKey() + "-" + ed.getDescription();
			assertThat(modResult, is(target));
		}
	}

	@Test
	public void testGetRelationshipDescription() {
		for (EdgeDescription ed : edges) {
			String target = ed.getFrom()+"|"+ed.getTo()+"-"+"( availability-zone LocatedIn complex, MANY2ONE)\n      this description";
			String modResult = ed.getRuleKey() + "-" + ed.getRelationshipDescription("FROM",ed.getTo());
			assertThat(modResult, is(target));
		}
	}

	@Test
	public void testGetType() {
		for (EdgeDescription ed : edges) {
			String target = ed.getFrom()+"|"+ed.getTo()+"-"+"UNRELATED";
			String modResult = ed.getRuleKey() + "-" + ed.getType();
			assertThat(modResult, is(target));
		}
	}

	@Test
	public void testGetLabel() {
		for (EdgeDescription ed : edges) {
			String target = ed.getFrom()+"|"+ed.getTo()+"-"+"org.onap.relationships.inventory.LocatedIn";
			String modResult = ed.getRuleKey() + "-" + ed.getLabel();
			assertThat(modResult, is(target));
		}
	}

	@Test
	public void testGetShortLabel() {
		for (EdgeDescription ed : edges) {
			String target = ed.getFrom()+"|"+ed.getTo()+"-"+"LocatedIn";
			String modResult = ed.getRuleKey() + "-" + ed.getShortLabel();
			assertThat(modResult, is(target));
		}
	}

	@Test
	public void testSetLabel() {
		String newLabel = "New label";
		for (EdgeDescription ed : edges) {
			ed.setLabel(newLabel);
			String target = ed.getFrom()+"|"+ed.getTo()+"-"+newLabel;
			String modResult = ed.getRuleKey() + "-" + ed.getLabel();
			assertThat(modResult, not(equalTo("org.onap.relationships.inventory.LocatedIn")));
			assertThat(modResult, is(target));
		}
	}

}
