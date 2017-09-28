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
 *    http://www.apache.org/licenses/LICENSE-2.0
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
package org.onap.aai.db.schema;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Version;
import org.onap.aai.util.AAIConfig;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;

public class ScriptDriver {


	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws AAIException the AAI exception
	 * @throws JsonGenerationException the json generation exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main (String[] args) throws AAIException, IOException {
		CommandLineArgs cArgs = new CommandLineArgs();
		
		new JCommander(cArgs, args);
		
		if (cArgs.help) {
			System.out.println("-c [path to graph configuration] -type [what you want to audit - oxm or graph]");
		}
		String config = cArgs.config;
		AAIConfig.init();
		try (TitanGraph graph = TitanFactory.open(config)) {
			if (!("oxm".equals(cArgs.type) || "graph".equals(cArgs.type))) {
				System.out.println("type: " + cArgs.type + " not recognized.");
				System.exit(1);
			}

			AuditDoc doc = null;
			if ("oxm".equals(cArgs.type)) {
				doc = AuditorFactory.getOXMAuditor(Version.v8).getAuditDoc();
			} else if ("graph".equals(cArgs.type)) {
				doc = AuditorFactory.getGraphAuditor(graph).getAuditDoc();
			}

			ObjectMapper mapper = new ObjectMapper();

			String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(doc);
			System.out.println(json);
		}
	}
	
}

class CommandLineArgs {
	
	@Parameter(names = "--help", description = "Help")
	public boolean help = false;
	
	@Parameter(names = "-c", description = "Configuration", required=true)
	public String config;
	
	@Parameter(names = "-type", description = "Type", required=true)
	public String type = "graph";
	

}
