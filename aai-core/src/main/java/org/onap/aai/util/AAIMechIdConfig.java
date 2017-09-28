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
package org.onap.aai.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.onap.aai.logging.ErrorLogHelper;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

public class AAIMechIdConfig {
	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(AAIMechIdConfig.class);
	private static final String mechIdConfigFileName = AAIConstants.AAI_HOME_ETC_APP_PROPERTIES + "mechid-config.json";
	
	public static final String SYSTEM_GFP_IP = "GFP";
	public static final String SYSTEM_GCP = "GCP";
	public static final String SYSTEM_DCAE = "DCAE";
	public static final String SYSTEM_RUBY = "RUBY";
	public static final String SYSTEM_ACTION = "ACTION";
	public static final String SYSTEM_INSTAR = "INSTAR-LPP-AMS";
	public static final String FILE_CLASS_GFP_IP = "GFP-IP";
	public static final String FILE_CLASS_INSTAR = "INSTAR-LPP-AMS";
	
	public static HashMap<String, String> mechIdtoSystem = new HashMap<String, String>();
	public static HashMap<String, ArrayList<String>> fileClassToMechId = new HashMap<String, ArrayList<String>>();
	
	/**
	 * Inits the.
	 *
	 * @param tId the t id
	 * @param appId the app id
	 * @param logger the logger
	 */
	public static void init() {
		LOGGER.debug("Initializing AAIMechIdConfig");
		Boolean enable;
		String systemMechId = "";
		JSONParser parser = new JSONParser();
		
		try {
			Object obj = parser.parse(new FileReader(mechIdConfigFileName));
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject mechIds = (JSONObject) jsonObject.get("mech-ids");
			
			@SuppressWarnings("unchecked")
			Set<String> systemSet = mechIds.keySet();
			for (String system : systemSet) {
				JSONObject systemJsonObj = (JSONObject) mechIds.get(system);
				systemMechId = (String) systemJsonObj.get("mechid");
				enable = (Boolean) systemJsonObj.get("enable");
				if (systemMechId != null && !systemMechId.isEmpty() && enable != null && enable == true) {
					mechIdtoSystem.put(systemMechId, system);
					JSONArray fileClasses = (JSONArray) systemJsonObj.get("file-classes");
					if (fileClasses != null ) {
						String fileClass = "";
						for (Object fileClassObj : fileClasses) {
							fileClass = (String) fileClassObj;
						
							if (!fileClassToMechId.containsKey(fileClass)) {
								fileClassToMechId.put(fileClass, new ArrayList<String>());
								fileClassToMechId.get(fileClass).add(systemMechId);
							} else {
								if(!fileClassToMechId.get(fileClass).contains(systemMechId)){
									fileClassToMechId.get(fileClass).add(systemMechId);
								
								}
							}
						}
					}
				}
			}
			
		} catch (FileNotFoundException fnfe) {
			ErrorLogHelper.logError("AAI_4001", 
					" " + mechIdConfigFileName + ". Exception: " + fnfe.getMessage());
		} catch (Exception e) {
			ErrorLogHelper.logError("AAI_4004", 
					" " + mechIdConfigFileName + ". Exception: " + e.getMessage());
		}
	}


	/**
	 * Transform mech id to pickup dir.
	 *
	 * @param systemMechId the system mech id
	 * @return the string
	 */
	public static String transformMechIdToPickupDir(String systemMechId) {
		String pickupDir = "";
		if (systemMechId != null && !systemMechId.isEmpty()) {
			pickupDir = "/opt/aaihome/" + systemMechId + "/pickup";
			
			if (pickupDir != null && !pickupDir.isEmpty() && new File(pickupDir).isDirectory()) {
				return pickupDir;
			}
			
		}
		return null;
	}
	
}
