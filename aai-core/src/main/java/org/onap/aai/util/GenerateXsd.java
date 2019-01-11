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
package org.onap.aai.util;


import org.onap.aai.config.SpringContextAware;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;
import org.onap.aai.util.genxsd.HTMLfromOXM;
import org.onap.aai.util.genxsd.NodesYAMLfromOXM;

import org.onap.aai.util.genxsd.YAMLfromOXM;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.w3c.dom.*;


import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateXsd {

	private static final Logger logger = LoggerFactory.getLogger("GenerateXsd.class");
	protected static String apiVersion = null;
	public static AnnotationConfigApplicationContext ctx = null;
	static String apiVersionFmt = null;
	static boolean useAnnotationsInXsd = false;
	static String responsesUrl = null;
	static String responsesLabel = null;
	static String jsonEdges = null;
	static Map<String, String> generatedJavaType;
	static Map<String, String> appliedPaths;
	static String RELEASE = System.getProperty("aai.release", "onap");


	static NodeList javaTypeNodes;
	static Map<String,String> javaTypeDefinitions = createJavaTypeDefinitions();
    private static Map<String, String> createJavaTypeDefinitions()
    {
    	StringBuffer aaiInternal = new StringBuffer();
    	Map<String,String> javaTypeDefinitions = new HashMap<String, String>();
    	aaiInternal.append("  aai-internal:\n");
    	aaiInternal.append("    properties:\n");
    	aaiInternal.append("      property-name:\n");
    	aaiInternal.append("        type: string\n");
    	aaiInternal.append("      property-value:\n");
    	aaiInternal.append("        type: string\n");
//    	javaTypeDefinitions.put("aai-internal", aaiInternal.toString());
    	return javaTypeDefinitions;
    }

	public static final int VALUE_NONE = 0;
	public static final int VALUE_DESCRIPTION = 1;
	public static final int VALUE_INDEXED_PROPS = 2;
	public static final int VALUE_CONTAINER = 3;

	private static final String generateTypeXSD = "xsd";
	private static final String generateTypeYAML = "yaml";

	private final static String nodeDir = System.getProperty("nodes.configuration.location");
	private final static String edgeDir = System.getProperty("edges.configuration.location");
	private static final String baseRoot = "aai-schema/";
	private static final String baseAutoGenRoot = "aai-schema/";

	private static final String root = baseRoot + "src/main/resources";
	private static final String autoGenRoot = baseAutoGenRoot + "src/main/resources";

	private static final String normalStartDir = "aai-core";
	private static final String xsd_dir = root + "/" + RELEASE +"/aai_schema";

	private static final String yaml_dir = (((System.getProperty("user.dir") != null) && (!System.getProperty("user.dir").contains(normalStartDir))) ? autoGenRoot : root) + "/" + RELEASE + "/aai_swagger_yaml";

	/* These three strings are for yaml auto-generation from aai-common class*/

	private static int swaggerSupportStartsVersion = 1; // minimum version to support swagger documentation


	private static boolean validVersion(String versionToGen) {

		if ("ALL".equalsIgnoreCase(versionToGen)) {
			return true;
		}

		SchemaVersions schemaVersions = SpringContextAware.getBean(SchemaVersions.class);
		for (SchemaVersion v : schemaVersions.getVersions()) {
	        if (v.equals(versionToGen)) {
	            return true;
	        }
	    }

	    return false;
	}

	private static boolean versionSupportsSwagger( String version) {
		if (new Integer(version.substring(1)).intValue() >= swaggerSupportStartsVersion ) {
			return true;
		}
		return false;
	}

	public static String getAPIVersion() {
		return apiVersion;
	}

	public static String getYamlDir() {
		return yaml_dir;
	}

	public static String getResponsesUrl() {
		return responsesUrl;
	}
	public static void main(String[] args) throws IOException {
		String versionToGen = System.getProperty("gen_version").toLowerCase();
		String fileTypeToGen = System.getProperty("gen_type").toLowerCase();

		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
				"org.onap.aai.config",
				"org.onap.aai.setup"
		);

		SchemaVersions schemaVersions = ctx.getBean(SchemaVersions.class);

		if ( fileTypeToGen == null ) {
			fileTypeToGen = generateTypeXSD;
		}

		if ( !fileTypeToGen.equals( generateTypeXSD ) && !fileTypeToGen.equals( generateTypeYAML )) {
			System.err.println("Invalid gen_type passed. " + fileTypeToGen);
			System.exit(1);
		}

		String responsesLabel = System.getProperty("yamlresponses_url");
		responsesUrl = responsesLabel;

		List<SchemaVersion> versionsToGen = new ArrayList<>();
		if ( versionToGen == null ) {
			System.err.println("Version is required, ie v<n> or ALL.");
			System.exit(1);
		}
		else if (!"ALL".equalsIgnoreCase(versionToGen) && !versionToGen.matches("v\\d+") && !validVersion(versionToGen)) {
			System.err.println("Invalid version passed. " + versionToGen);
			System.exit(1);
		}
		else if ("ALL".equalsIgnoreCase(versionToGen)) {
			versionsToGen = schemaVersions.getVersions();
			Collections.sort(versionsToGen);
			Collections.reverse(versionsToGen);
		} else {
			versionsToGen.add(new SchemaVersion(versionToGen));
		}

		//process file type System property
		fileTypeToGen = (fileTypeToGen == null ? generateTypeXSD : fileTypeToGen.toLowerCase());
		if ( !fileTypeToGen.equals( generateTypeXSD ) && !fileTypeToGen.equals( generateTypeYAML )) {
			System.err.println("Invalid gen_type passed. " + fileTypeToGen);
			System.exit(1);
		} else if ( fileTypeToGen.equals(generateTypeYAML) ) {
			if ( responsesUrl == null || responsesUrl.length() < 1
					|| responsesLabel == null || responsesLabel.length() < 1 ) {
				System.err.println("generating swagger yaml file requires yamlresponses_url and yamlresponses_label properties" );
				System.exit(1);
			} else {
				responsesUrl = "description: "+ "Response codes found in [response codes]("+responsesLabel+ ").\n";
			}
		}
		/*
		 * TODO: Oxm Path is config driveb
		 */
		String oxmPath;
		if(System.getProperty("user.dir") != null && !System.getProperty("user.dir").contains(normalStartDir)) {
			oxmPath = baseAutoGenRoot + nodeDir;
		}
		else {
			oxmPath = baseRoot + nodeDir;
		}

		String outfileName = null;
		File outfile;
		String nodesfileName = null;
		File nodesfile;
		String fileContent = null;
		String nodesContent = null;


		for (SchemaVersion v : versionsToGen) {
			apiVersion = v.toString();
			logger.debug("YAMLdir = "+yaml_dir);
			logger.debug("Generating " + apiVersion + " " + fileTypeToGen);
			apiVersionFmt = "." + apiVersion + ".";
			generatedJavaType = new HashMap<String, String>();
			appliedPaths = new HashMap<String, String>();
			File edgeRuleFile = null;
			String fileName = edgeDir + "DbEdgeRules_" + apiVersion + ".json";
			logger.debug("user.dir = "+System.getProperty("user.dir"));
			if(System.getProperty("user.dir") != null && !System.getProperty("user.dir").contains(normalStartDir)) {
				fileName = baseAutoGenRoot + fileName;

			}
			else {
				fileName = baseRoot + fileName;

			}
			edgeRuleFile = new File( fileName);
//			Document doc = ni.getSchema(translateVersion(v));

			if ( fileTypeToGen.equals(generateTypeXSD) ) {
				outfileName = xsd_dir + "/aai_schema_" + apiVersion + "." + generateTypeXSD;
				try {
					HTMLfromOXM swagger = ctx.getBean(HTMLfromOXM.class);
					swagger.setVersion(v);
					fileContent = swagger.process();
					if ( fileContent.startsWith("Schema format issue")) {
						throw new Exception(fileContent);
					}
				} catch(Exception e) {
		        	logger.error( "Exception creating output file " + outfileName);
		        	logger.error( e.getMessage());
		        	e.printStackTrace();
		        	System.exit(-1);
				}
			} else if ( versionSupportsSwagger(apiVersion )) {
				outfileName = yaml_dir + "/aai_swagger_" + apiVersion + "." + generateTypeYAML;
				nodesfileName = yaml_dir + "/aai_swagger_" + apiVersion + "." + "nodes"+"."+generateTypeYAML;
				try {
					YAMLfromOXM swagger = (YAMLfromOXM) ctx.getBean(YAMLfromOXM.class);
					swagger.setVersion(v);
					fileContent = swagger.process();
					Map combinedJavaTypes = swagger.getCombinedJavaTypes();
					NodesYAMLfromOXM nodesSwagger = ctx.getBean(NodesYAMLfromOXM.class);
					nodesSwagger.setVersion(v);
					nodesSwagger.setCombinedJavaTypes(combinedJavaTypes);
					nodesContent = nodesSwagger.process();
				} catch(Exception e) {
		        	logger.error( "Exception creating output file " + outfileName);
		        	e.printStackTrace();
				}
			} else {
				continue;
			}
			outfile = new File(outfileName);
			File parentDir = outfile.getParentFile();
			if(! parentDir.exists())
			      parentDir.mkdirs();
			if(nodesfileName != null) {
				BufferedWriter nodesBW = null;
				nodesfile = new File(nodesfileName);
				parentDir = nodesfile.getParentFile();
				if(! parentDir.exists())
				      parentDir.mkdirs();
			    try {
			        nodesfile.createNewFile();
			    } catch (IOException e) {
		        	logger.error( "Exception creating output file " + nodesfileName);
		        	e.printStackTrace();
			    }
	        	try {
	        		Charset charset = Charset.forName("UTF-8");
	        		Path path = Paths.get(nodesfileName);
	        		nodesBW = Files.newBufferedWriter(path, charset);
	        		nodesBW.write(nodesContent);
	        	} catch ( IOException e) {
	        		logger.error( "Exception writing output file " + outfileName);
	        		e.printStackTrace();
	        	} finally {
	        		if ( nodesBW != null ) {
	        			nodesBW.close();
	        		}
	        	}
			}

		    try {
		        outfile.createNewFile();
		    } catch (IOException e) {
	        	logger.error( "Exception creating output file " + outfileName);
	        	e.printStackTrace();
		    }
		    BufferedWriter bw = null;
	        try {
	        	Charset charset = Charset.forName("UTF-8");
	        	Path path = Paths.get(outfileName);
	        	bw = Files.newBufferedWriter(path, charset);
	        	bw.write(fileContent);
	        } catch ( IOException e) {
	        	logger.error( "Exception writing output file " + outfileName);
	        	e.printStackTrace();
	        } finally {
	        	if ( bw != null ) {
	        		bw.close();
	        	}
	        }
			logger.debug( "GeneratedXSD successful, saved in " + outfileName);
		}

	}

}

