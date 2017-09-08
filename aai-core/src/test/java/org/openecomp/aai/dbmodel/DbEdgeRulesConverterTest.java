/*-
 * ============LICENSE_START=======================================================
 * org.openecomp.aai
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.aai.dbmodel;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.*;
import java.util.Map.Entry;

import static org.junit.Assert.*;

public class DbEdgeRulesConverterTest {
	
	@Test
	public void testExtractData() {
		Multimap<String, String> EdgeRules = new ImmutableSetMultimap.Builder<String, String>()
				.putAll("availability-zone|complex",
						"groupsResourcesIn,OUT,Many2Many,false,true,false,true").build();
		
		DbEdgeRulesConverter dberConverter = new DbEdgeRulesConverter();
		
		for (Entry<String, String> r : EdgeRules.entries()) {
			EdgeRuleBean bean = dberConverter.extractData(r);
			assertEquals("from availability-zone", "availability-zone", bean.getFrom());
			assertEquals("to complex", "complex", bean.getTo());
			assertEquals("label", "groupsResourcesIn", bean.getLabel());
			assertEquals("direction", "OUT", bean.getDirection());
			assertEquals("multiplicity", "Many2Many", bean.getMultiplicity());
			assertEquals("isParent", "false", bean.getIsParent());
			assertEquals("usesResource", "true", bean.getUsesResource());
			assertEquals("hasDelTarget", "false", bean.getHasDelTarget());
			assertEquals("SVC-INFRA", "true", bean.getSvcInfra());
		}
	}

	@Test
	public void testConvert(){
		DbEdgeRulesConverter dberCon = new DbEdgeRulesConverter();
		String dest = "src/test/resources/dbEdgeRulesConversion";
		String outFile = dest + "/testOutput.json";
		
		Multimap<String, String> EdgeRules = new ImmutableSetMultimap.Builder<String, String>()
				.putAll("foo|bar",
						"has,OUT,Many2Many,false,false,false,false")
				.putAll("baz|quux",
						"treatsVeryKindly,IN,One2One,true,true,true,true")
				.build();

		try {
			dberCon.setup(dest);
			File result = new File(outFile);
			//Add delete hook to delete the temporary result file on exit/
			result.deleteOnExit();
			FileOutputStream writeStream = new FileOutputStream(result);
			Writer writer = new OutputStreamWriter(writeStream);
			dberCon.convert(EdgeRules, writer);
			File compare = new File("src/test/resources/dbEdgeRulesConversion/conversionTestCompare.json");
			assertTrue(FileUtils.contentEquals(result, compare));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOException on setup");
		}
	}
}
