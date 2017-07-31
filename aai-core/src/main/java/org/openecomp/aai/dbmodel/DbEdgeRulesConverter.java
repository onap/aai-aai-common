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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openecomp.aai.introspection.Version;

import com.google.common.collect.Multimap;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * Converts the old DbEdgeRules multimap to new json format
 */
public class DbEdgeRulesConverter {
	private static final int LABEL = 0;
	private static final int DIRECTION = 1;
	private static final int MULTIPLICITY = 2;
	private static final int ISPARENT = 3;
	private static final int USESRESOURCE = 4;
	private static final int HASDELTARGET = 5;
	private static final int SVCINFRA = 6;
	
	private Configuration config = new Configuration();
	private Template template;
	private String destDirectory;
	private FileOutputStream writeStream;
	
	public DbEdgeRulesConverter(){ /*pretty much just so I can test functionality without dealing with template setup*/ }
	
	public DbEdgeRulesConverter(String destinationDir) {
		destDirectory = destinationDir;
		try {
			setup(destinationDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets up the freemarker template and the directory to be written to. Run this once before
	 * doing any converting, does not need to be run per file generated (unless you want different directories for each file).
	 * 
	 * @param destinationDir - String of the path to the directory where you want the new format files written to,
	 * 							relative to aai-core/
	 * @throws IOException if it can't find the template loading directory or the template file itself
	 */
	public void setup(String destinationDir) throws IOException {
		config.setDirectoryForTemplateLoading(new File("src/main/resources/dbedgerules/conversion"));
		config.setDefaultEncoding("UTF-8");
		config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		template = config.getTemplate("edgerulesTemplate.ftlh");
		
		File destination = new File(destinationDir);
		if (!destination.exists()) {
			destination.mkdir();
		}
	}
	
	/**
	 * Converts the given DbEdgeRules multimap representation into a json file of the new format.
	 * 
	 * @param rules - a Multimap<String, String> of the old DbEdgeRules format
	 * @param writer - writes to the output file (designate that file when you instantiate the writer)
	 */
	public void convert(Multimap<String, String> rules, Writer writer) {
		
		List<EdgeRuleBean> rulesToWrite = new ArrayList<>();
		for (Entry<String, String> rule : rules.entries()) {
			rulesToWrite.add(extractData(rule));
		}
		Map<String, List<EdgeRuleBean>> wrappedRules = new HashMap<>();
		wrappedRules.put("wrappedRules", rulesToWrite);
		try {
			template.process(wrappedRules, writer);
		} catch (TemplateException e) {
			System.out.println("Something went wrong when trying to combine the data and the template");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("There was a problem writing to the output file");
			e.printStackTrace();
		} 
	}
	
	/**
	 * Extracts the pieces of information that go in each field of the new json format from the old
	 * DbEdgeRules format.
	 * 
	 * @param rule - one <String, String> entry from the DbEdgeRules multimap representation
	 * 		Takes the form <"from-node|to-node", "label,direction,multiplicity,isParent,usesResource,hasDelTarget,svc-infra">
	 * @return EdgeRuleBean with the pieces of information the template needs, in a format the template can understand
	 */
	public EdgeRuleBean extractData(Entry<String, String> rule){
		EdgeRuleBean data = new EdgeRuleBean();
		
		String oldName = rule.getKey();
		String[] nodes = oldName.split("\\|");
		data.setFrom(nodes[0]);
		data.setTo(nodes[1]);
		
		String oldSpecs = rule.getValue();
		String[] specs = oldSpecs.split(",");
		data.setLabel(specs[LABEL]);
		data.setDirection(specs[DIRECTION]);
		data.setMultiplicity(specs[MULTIPLICITY]);
		data.setParent(specs[ISPARENT]);
		data.setUsesResource(specs[USESRESOURCE]);
		data.setHasDelTarget(specs[HASDELTARGET]);
		data.setSvcInfra(specs[SVCINFRA]);
		
		return data;
	}
	
	private Multimap<String, String> getEdgeRules(Version v) {
		try {
			Class <?> dbEdgeRules;
			//use reflection to get the corresponding DbEdgeRules class
			//need this weird if-else bc current version doesn't sit in a v.gen subdirectory
			if (Version.isLatest(v)) {
					dbEdgeRules = Class.forName("org.openecomp.aai.dbmodel.DbEdgeRules");
			} else {
				dbEdgeRules = Class.forName("org.openecomp.aai.dbmodel." + v + ".gen.DbEdgeRules");
			}
			
			@SuppressWarnings("unchecked")
			Multimap<String, String> rules = (Multimap<String,String>)dbEdgeRules.getDeclaredField("EdgeRules").get(null);
			
			return rules;
		} catch (ClassNotFoundException e) {
			System.out.println("could not find DbEdgeRules class for version " + v);
			e.printStackTrace();
			return null;
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			System.out.println("Something went wrong trying to retrieve the rules");
			e.printStackTrace();
			return null;
		}
	}
	
	private Writer buildOutputWriter(Version v) {
		try {
			File output = new File(destDirectory + "DbEdgeRules_" + v + ".json");
			writeStream = new FileOutputStream(output);
			return new OutputStreamWriter(writeStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Runs all the conversion steps for the specified version.
	 * 
	 * @param v
	 */
	public void convertVersion(Version v) {
		try {
			Multimap<String, String> rules = getEdgeRules(v);
			if (rules == null) { //something went wrong, we've already logged it in the helper so just stop execution
				return;
			}
			
			Writer writer = buildOutputWriter(v); 
			if (writer == null) { //something went wrong, we've already logged it in the helper so just stop execution
				return;
			}
			convert(rules, writer);
			
			writer.close();
			writeStream.close();
		} catch (IOException e) {
			System.out.println("Something went wrong closing the writer/writestream");
			e.printStackTrace();
		}
	}
	
	/**
	 * Runs the converter for each DbEdgeRules version currently supported (2, 7, 8, 9, and 10)
	 * 
	 * @param args - none actually
	 */
	public static void main(String[] args) {
		String destDirectory = "src/main/resources/dbedgerules/";
		DbEdgeRulesConverter dberConverter = new DbEdgeRulesConverter(destDirectory);
		
		for (Version v : Version.values()) {
			dberConverter.convertVersion(v);
		}
	}
}
