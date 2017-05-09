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

/**
 * 
 */
package org.openecomp.aai.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.openecomp.aai.exceptions.AAIException;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;


public class AAIRSyncUtility {

	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(AAIRSyncUtility.class);	
	private final String DEFAULT_CHECK = new String("aai.primary.filetransfer.");
	
	/**
	 * Instantiates a new AAIR sync utility.
	 */
	public AAIRSyncUtility() {

	}

	/**
	 * Do command.
	 *
	 * @param command the command
	 * @return the int
	 * @throws Exception the exception
	 */
	public int doCommand(List<String> command)  
			  throws Exception 
	{ 
		String s = null; 
			      
		ProcessBuilder pb = new ProcessBuilder(command); 
		Process process = pb.start(); 
			  
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream())); 
			  
		LOGGER.debug("Here is the standard output of the command:\n"); 
		while ((s = stdInput.readLine()) != null) 
		{ 
			LOGGER.debug(s);
		} 
			  
		LOGGER.debug("Here is the standard error of the command (if any):\n"); 
		while ((s = stdError.readLine()) != null) 
		{ 
			LOGGER.debug(s);
		} 
		return process.waitFor();
	} 
			    

	/**
	 * Method sendRsyncCommand.
	 *
	 * @param transId the trans id
	 * @param fileName the file name
	 */
	public void sendRsyncCommand(String transId, String fileName) 
	{
		String aaiServerList = null;
		String rsyncOptionsList = null;
		
		try {
			aaiServerList = AAIConfig.get(DEFAULT_CHECK + "serverlist");
			rsyncOptionsList = AAIConfig.get("aai.rsync.options.list");
			String enableRsync = AAIConfig.get("aai.rsync.enabled");
			
			if (!AAIConfig.isEmpty(enableRsync) && "n".equalsIgnoreCase(enableRsync)){
	    		LOGGER.info("rsync not invoked for " + fileName + ": rsync is not enabled in aaiconfig.properties");
				return;
			}
		} catch ( Exception e ) {
    		LOGGER.warn( "rsync not invoked: missing aaiconfig.properties entries for rsync" );
		}
		
		LOGGER.info("rsync to copy files started....");
		
    	ArrayList<String> remoteHostList = new ArrayList<String>();
    	StringTokenizer serverList = new StringTokenizer( aaiServerList, "|" );
    	String host = null;
		try {
			host = getHost();
			String remoteConnString = null;
			 
			remoteHostList = getRemoteHostList(serverList, host);
			LOGGER.debug("This host:" + host);
	    	String pickUpDirectory = AAIConfig.get("instar.pickup.dir");
	    	String user = AAIConfig.get("aai.rsync.remote.user"); 
	    	String rsyncCmd = AAIConfig.get("aai.rsync.command");
	    	
	    	//Push: rsync [OPTION...] SRC... [USER@]HOST:DEST
	    	
	    	java.util.Iterator<String> remoteHostItr = remoteHostList.iterator();
	    	while (!remoteHostList.isEmpty() && remoteHostItr.hasNext()) {
	    		String remoteHost = remoteHostItr.next();
	    		remoteConnString =user+"@"+remoteHost+":"+pickUpDirectory;
				   
				List<String> commands = new ArrayList<String>(); 
			    commands.add(rsyncCmd); 
			    StringTokenizer optionTks = new StringTokenizer( rsyncOptionsList, "|" );
			    while (optionTks.hasMoreTokens()){
			    	commands.add(optionTks.nextToken());
			    }
			    commands.add(fileName); // src directory/fileName
			    commands.add(remoteConnString); // target username/host/path
			    LOGGER.debug("Commands: " + commands.toString());
			    int rsyncResult = doCommand(commands);
			    if ( rsyncResult == 0 ) {
			    	LOGGER.info("rsync completed for "+remoteHost);
			    }else {
					LOGGER.error("rsync failed for "+ remoteHost+ " with response code "+rsyncResult );
				}
			} 
    	} catch ( Exception e) {
    		LOGGER.error("no server found processing serverList for host " + host + ": " + e.getMessage() + " (AAI_4000)");
		}
	}

	/**
	 * Gets the remote host list.
	 *
	 * @param serverList the server list
	 * @param host the host
	 * @return the remote host list
	 */
	private ArrayList<String> getRemoteHostList(StringTokenizer serverList, String host) {
		ArrayList<String> remoteHostList = new ArrayList<String>();
		String remoteHost = null;
		while ( serverList.hasMoreTokens() ) {
    		remoteHost = serverList.nextToken();
    		if (!host.equalsIgnoreCase(remoteHost)){
    			remoteHostList.add(remoteHost);
    		}
		}
		return remoteHostList;
	}

	/**
	 * Gets the host.
	 *
	 * @return the host
	 * @throws AAIException the AAI exception
	 */
	private String getHost() throws AAIException {
		String aaiServerList = AAIConfig.get(DEFAULT_CHECK + "serverlist");
		String hostname = null;
		try {
			InetAddress ip = InetAddress.getLocalHost();
        	if ( ip != null ) {
        		hostname = ip.getHostName();
        		if ( hostname != null ) {
        			if  ( !( aaiServerList.contains(hostname) ) )
        				LOGGER.warn("Host name not found in server list " + hostname);
        		} else
        			LOGGER.warn("InetAddress returned null hostname");
        	} 
        	 
		} catch (UnknownHostException e) {
			LOGGER.warn("InetAddress getLocalHost exception " + e.getMessage());
    	}
    	
		return hostname;
	}
	
}
