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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Timer;
import org.onap.aai.logging.LoggingContext;
import org.onap.aai.logging.LoggingContext.StatusCode;

import java.util.UUID;

import org.eclipse.jetty.util.security.Password;

import org.onap.aai.exceptions.AAIException;
import org.onap.aai.logging.ErrorLogHelper;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;


public class AAIConfig {

	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(AAIConfig.class);
	private static final String GLOBAL_PROP_FILE_NAME = AAIConstants.AAI_CONFIG_FILENAME;
	private static Properties serverProps;
    private static boolean propsInitialized = false;
    
    // this (probably) won't change between releases, put it in the config if it gets annoying...
    private static HashMap<String,ArrayList<String>> defaultBools = new HashMap<String,ArrayList<String>>();
    private static Timer timer = new Timer();
    
    /**
     * Instantiates a new AAI config.
     */
    // Don't instantiate
    private AAIConfig() {}

    /**
     * Inits the.
     *
     * @throws AAIException the AAI exception
     */
    public synchronized static void init() throws AAIException{

    	LoggingContext.save();
		LoggingContext.component("config");
		LoggingContext.partnerName("NA");
		LoggingContext.targetEntity("AAI");
		LoggingContext.requestId(UUID.randomUUID().toString());
		LoggingContext.serviceName("AAI");
		LoggingContext.targetServiceName("init");
		LoggingContext.statusCode(StatusCode.COMPLETE);

		LOGGER.info("Initializing AAIConfig");
		
		ArrayList<String> genericVnfBools = new ArrayList<String>();
		ArrayList<String> l3NetworkBools = new ArrayList<String>();
		ArrayList<String> pserverBools = new ArrayList<String>();
		ArrayList<String> subnetBools = new ArrayList<String>();
		ArrayList<String> vserverBools = new ArrayList<String>();
		ArrayList<String> vnfcBools = new ArrayList<String>();

		genericVnfBools.add("in-maint");
		genericVnfBools.add("is-closed-loop-disabled");
		l3NetworkBools.add("is-bound-to-vpn");
		pserverBools.add("in-maint");
		subnetBools.add("dhcp-enabled");
		vserverBools.add("in-maint");
		vserverBools.add("is-closed-loop-disabled");
		vnfcBools.add("in-maint");
		vnfcBools.add("is-closed-loop-disabled");

		defaultBools.put("generic-vnf", genericVnfBools);
		defaultBools.put("l3-network",  l3NetworkBools);
		defaultBools.put("pserver", pserverBools);
		defaultBools.put("subnet", subnetBools);
		defaultBools.put("vserver", vserverBools);
		defaultBools.put("vnfc", vnfcBools);
		
        AAIConfig.getConfigFile();
        AAIConfig.reloadConfig();
        
        if (AAIConstants.AAI_NODENAME == null || AAIConstants.AAI_NODENAME == "") {      
            ErrorLogHelper.logError("AAI_4005", " AAI_NODENAME is not defined");
        } else {
            LOGGER.info("A&AI Server Node Name = " + AAIConstants.AAI_NODENAME);
        }
        LoggingContext.restore();
    }

    /**
     * Gets the default bools.
     *
     * @return the default bools
     */
    public static HashMap<String,ArrayList<String>> getDefaultBools() { 
    	return defaultBools;
    }
        
    /**
     * Cleanup.
     */
    public static void cleanup() {
		timer.cancel();
	}

    /**
     * Gets the config file.
     *
     * @return the config file
     */
    public static String getConfigFile() {
//        if (GlobalPropFileName == null) {
//        	String nc = System.getProperty("aaiconfig");
//			if (nc == null) nc = "/home/aaiadmin/etc/aaiconfig.props";
//		    logger.info( "aaiconfig = " + nc==null?"null":nc);
//			GlobalPropFileName = nc;
//        }
        return GLOBAL_PROP_FILE_NAME;
    }

    /**
     * Reload config.
     */
    public synchronized static void reloadConfig() {

        String propFileName = GLOBAL_PROP_FILE_NAME;
        Properties newServerProps = null;
        
        LOGGER.debug("Reloading config from " + propFileName);
        
        try {
            InputStream is = new FileInputStream(propFileName);
            newServerProps = new Properties();
            newServerProps.load(is);
            propsInitialized = true;

            serverProps = newServerProps;
            newServerProps = null;
        	
        } catch (FileNotFoundException fnfe) {
        	ErrorLogHelper.logError("AAI_4001", " " + propFileName + ". Exception: "+fnfe.getMessage());
        } catch (IOException e) {
        	ErrorLogHelper.logError("AAI_4002", " " + propFileName + ". IOException: "+e.getMessage());
        }
    }
    
    /**
     * Gets the.
     *
     * @param key the key
     * @param defaultValue the default value
     * @return the string
     */
    public static String get(String key, String defaultValue) {
    	String result = defaultValue;
    	try {
    			result = get (key);
    	}
    	catch ( AAIException a ) {
    		
    	}
    	return ( result );
    }

    /**
     * Gets the.
     *
     * @param key the key
     * @return the string
     * @throws AAIException the AAI exception
     */
    public static String get(String key) throws AAIException {
    	String response = null;
    	
    	if (key.equals(AAIConstants.AAI_NODENAME)) {
    		// Get this from InetAddress rather than the properties file
    		String nodeName = getNodeName();
    		if (nodeName != null) {
    			return nodeName;
    		}
    		// else get from property file
    	}
    	
    	if (!propsInitialized || (serverProps == null)) {
    		reloadConfig();
    	}
    	
    	if ((key.endsWith("password") || key.endsWith("passwd") || key.endsWith("apisecret")) && serverProps.containsKey(key+".x")) {
    		String valx = serverProps.getProperty(key+".x");
    		return Password.deobfuscate(valx);
    	}
    	
    	if (!serverProps.containsKey(key)) {
    		throw new AAIException("AAI_4005", "Property key "+key+" cannot be found");
    	} else {
    		response = serverProps.getProperty(key);
    		if (response == null || response.isEmpty()) {
    			throw new AAIException("AAI_4005", "Property key "+key+" is null or empty");
    		}
    	}
    	return response;
	}

    /**
     * Gets the int.
     *
     * @param key the key
     * @return the int
     * @throws AAIException the AAI exception
     */
    public static int getInt(String key) throws AAIException{
    	return Integer.valueOf(AAIConfig.get(key));
	}

	/**
	 * Gets the server props.
	 *
	 * @return the server props
	 */
	public static Properties getServerProps() {
		return serverProps;
	}
	
	/**
	 * Gets the node name.
	 *
	 * @return the node name
	 */
	public static String getNodeName() {
		try {
            InetAddress ip = InetAddress.getLocalHost();
            if (ip != null) {
                   String hostname = ip.getHostName();
                   if (hostname != null) {
                	   return hostname;
                   }
            }
		} catch (Exception e) {
			return null;
		}
		return null;
	}
	
	
	/**
	 * Check if a null or an Empty string is passed in.
	 *
	 * @param s the s
	 * @return boolean
	 */
	public static boolean isEmpty(String s)
	{
		return (s == null || s.length() == 0);
	}

}
