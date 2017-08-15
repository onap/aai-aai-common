/*-
 * ============LICENSE_START=======================================================
 * org.openecomp.aai
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * ============LICENSE_END=========================================================
 */

//package org.openecomp.aai.dbmodel;
//import static com.jayway.jsonpath.Criteria.where;
//import static com.jayway.jsonpath.Filter.filter;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import org.openecomp.aai.introspection.Version;
//import org.openecomp.aai.serialization.db.AAIDirection;
//
//import com.google.common.collect.Multimap;
//import com.jayway.jsonpath.Configuration;
//import com.jayway.jsonpath.DocumentContext;
//import com.jayway.jsonpath.JsonPath;
//import com.jayway.jsonpath.Option;
//
//public class ConvertDeleteScope {
//	
//	
//	
//	private static final String edgeClasspath = "org.openecomp.aai.dbmodel";
//	private static final String edgeClassSuffix = ".%s.gen";
//	private static final String jsonEdgeFile = "src/main/resources/dbedgerules/DbEdgeRules_%s.json";
//
//	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, IllegalArgumentException, NoSuchFieldException, SecurityException {
//		for (Version v : Version.values()) {
//			convert(v);
//		}
//	}
//	private static void convert(Version v) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, NoSuchFieldException, SecurityException {
//		DocumentContext rulesDoc;
//		Multimap<String, String> deleteScope;
//		Configuration conf = Configuration.defaultConfiguration().addOptions(Option.ALWAYS_RETURN_LIST, Option.SUPPRESS_EXCEPTIONS);
//
//		rulesDoc = JsonPath.using(conf).parse(readFile(String.format(jsonEdgeFile, v)));
//		if (v.equals(Version.getLatest())) {
//			Object rules = Class.forName(edgeClasspath + ".DbEdgeRules").newInstance();
//			deleteScope = (Multimap<String, String>) rules.getClass().getField("DefaultDeleteScope").get(rules);
//		} else {
//			Object rules = Class.forName(edgeClasspath + String.format(edgeClassSuffix, v) + ".DbEdgeRules").newInstance();
//			deleteScope = (Multimap<String, String>) rules.getClass().getField("DefaultDeleteScope").get(rules);
//		}
//		Collection<Entry<String, String>> entries = deleteScope.entries();
//		for (Entry<String, String> entry : entries) {
//			String key = entry.getKey();
//			String value = entry.getValue();
//			
//			addRule(rulesDoc, key, value);
//			
//		}
//		List<Map<String, String>> results = rulesDoc.read("$.rules.[?]", filter(where("preventDelete").exists(false)));
//		for (Map<String, String> result : results) {
//			result.put("preventDelete", AAIDirection.NONE.toString());
//		}
//		System.out.println("Version: " + v + " " + rulesDoc.jsonString());
//		
//		
//	}
//	
//	private static String readFile (String path) throws IOException {
//		return new String(Files.readAllBytes(Paths.get(path)));
//	}
//	
//	private static void addRule(DocumentContext rulesDoc, String nodeType, String deleteScope) {
//		
//		if (deleteScope.equals("THIS_NODE_ONLY")) {
//			List<Map<String, String>> results = rulesDoc.read("$.rules.[?]", filter(
//					where("from").is(nodeType)
//				).and(
//					filter(where("isParent").is("${direction}")).or(
//							where("hasDelTarget").is("${direction}")
//					)
//				)
//			);
//			for (Map<String, String> result : results) {
//				result.put("preventDelete", "${direction}");
//			}
//		} else if (deleteScope.contains("_IN_")) {
//			List<Map<String, String>> results = rulesDoc.read("$.rules.[?]", filter(where("to").is(nodeType).and("isParent").is("false")));
//			for (Map<String, String> result : results) {
//				result.put("preventDelete", "!${direction}");
//			}
//		} else {
//			
//		}
//	}
//	
//
//}
