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
 *      http://www.apache.org/licenses/LICENSE-2.0
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

package org.onap.aai.util;

import org.onap.aai.introspection.Version;
import org.onap.aai.util.genxsd.EdgeRuleSet;
import org.onap.aai.util.genxsd.HTMLfromOXM;
import org.onap.aai.util.genxsd.YAMLfromOXM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class GenerateXsd {
	
	private static final Logger logger = LoggerFactory.getLogger("GenerateXsd.class");
	protected static String apiVersion = null;
	static String apiVersionFmt = null;
	static boolean useAnnotationsInXsd = false;
	static String responsesUrl = null;
	static String responsesLabel = null;
	static String jsonEdges = null;
	static EdgeRuleSet edgeRuleSet = null;

	static Map<String, String> generatedJavaType;
	static Map<String, String> appliedPaths;

	public static final int VALUE_NONE = 0;
	public static final int VALUE_DESCRIPTION = 1;
	public static final int VALUE_INDEXED_PROPS = 2;
	public static final int VALUE_CONTAINER = 3;
	
	private static final String generateTypeXSD = "xsd";
	private static final String generateTypeYAML = "yaml";
	
	private static final String root = "../aai-schema/src/main/resources";
	private static final String autoGenRoot = "aai-schema/src/main/resources";
	private static final String normalStartDir = "aai-core";
	private static final String xsd_dir = root + "/aai_schema";
	private static final String yaml_dir = (((System.getProperty("user.dir") != null) && (!System.getProperty("user.dir").contains(normalStartDir))) ? autoGenRoot : root) + "/aai_swagger_yaml";
	
	/* These three strings are for yaml auto-generation from aai-common class*/
//	private static final String alt_yaml_dir = autoGenRoot + "/aai_swagger_yaml";

	private static int annotationsStartVersion = 9; // minimum version to support annotations in xsd
	private static int swaggerSupportStartsVersion = 7; // minimum version to support swagger documentation
	
	private static boolean validVersion(String versionToGen) {
		
		if ("ALL".equalsIgnoreCase(versionToGen)) {
			return true;
		}
		
		for (Version v : Version.values()) {
	        if (v.name().equals(versionToGen)) {
	            return true;
	        }
	    }

	    return false;
	}
	
	private static boolean versionUsesAnnotations( String version) {
		if (new Integer(version.substring(1)).intValue() >= annotationsStartVersion ) {
			return true;
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
		if ( fileTypeToGen == null ) {
			fileTypeToGen = generateTypeXSD;
		}
		
		if ( !fileTypeToGen.equals( generateTypeXSD ) && !fileTypeToGen.equals( generateTypeYAML )) {
			System.err.println("Invalid gen_type passed. " + fileTypeToGen);
			System.exit(1);
		}
		
		
		String responsesLabel = System.getProperty("yamlresponses_url");
		responsesUrl = responsesLabel;
		
		List<Version> versionsToGen = new ArrayList<>();
		if ( versionToGen == null ) {
			System.err.println("Version is required, ie v<n> or ALL.");
			System.exit(1);			
		}
		else if (!"ALL".equalsIgnoreCase(versionToGen) && !versionToGen.matches("v\\d+") && !validVersion(versionToGen)) {
			System.err.println("Invalid version passed. " + versionToGen);
			System.exit(1);
		}
		else if ("ALL".equalsIgnoreCase(versionToGen)) {
			versionsToGen = Arrays.asList(Version.values());
			Collections.sort(versionsToGen);
			Collections.reverse(versionsToGen);
		} else {
			versionsToGen.add(Version.getVersion(versionToGen));
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
		String oxmPath;
		if(System.getProperty("user.dir") != null && !System.getProperty("user.dir").contains(normalStartDir)) {
			oxmPath = autoGenRoot + "/oxm/";
		}
		else {
			oxmPath = root + "/oxm/";
		}

		String outfileName;
		File outfile;
		String fileContent = null;
		
		for (Version v : versionsToGen) {
			apiVersion = v.toString();
			logger.debug("YAMLdir = "+yaml_dir);
			logger.debug("Generating " + apiVersion + " " + fileTypeToGen);
			File oxm_file = new File(oxmPath + "aai_oxm_" + apiVersion + ".xml");
			apiVersionFmt = "." + apiVersion + ".";
			generatedJavaType = new HashMap<String, String>();
			appliedPaths = new HashMap<String, String>();
			File edgeRuleFile = null;
			logger.debug("user.dir = "+System.getProperty("user.dir"));
			if(System.getProperty("user.dir") != null && !System.getProperty("user.dir").contains(normalStartDir)) {
				edgeRuleFile = new File(normalStartDir + "/src/main/resources/dbedgerules/DbEdgeRules_" + apiVersion + ".json");
			}
			else {
				edgeRuleFile = new File("src/main/resources/dbedgerules/DbEdgeRules_" + apiVersion + ".json");
			}
			
			if ( fileTypeToGen.equals(generateTypeXSD) ) {
				useAnnotationsInXsd = versionUsesAnnotations(apiVersion);
				outfileName = xsd_dir + "/aai_schema_" + apiVersion + "." + generateTypeXSD;
				try {
					HTMLfromOXM swagger = new HTMLfromOXM(oxm_file, v);
					fileContent = swagger.process();
				} catch(Exception e) {
		        	logger.error( "Exception creating output file " + outfileName);
		        	logger.error( e.getMessage());
		        	e.printStackTrace();
				}
			} else if ( versionSupportsSwagger(apiVersion )) {
				outfileName = yaml_dir + "/aai_swagger_" + apiVersion + "." + generateTypeYAML;
				try {
					YAMLfromOXM swagger = new YAMLfromOXM(oxm_file, v, edgeRuleFile);
					fileContent = swagger.process();
				} catch(Exception e) {
		        	logger.error( "Exception creating output file " + outfileName);
		        	logger.error( e.getMessage());
		        	e.printStackTrace();
				}
			} else {
				continue;
			}
			outfile = new File(outfileName);
			File parentDir = outfile.getParentFile();
			if(! parentDir.exists()) 
			      parentDir.mkdirs();
		
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