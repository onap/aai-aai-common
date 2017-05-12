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

package org.openecomp.aai.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;

import org.openecomp.aai.exceptions.AAIException;

public class AAIApiVersion {
	
	private static final Pattern versionPattern = Pattern.compile("(^|\\/)(v\\d+)\\/");
	
	private static final Pattern latestVersionPattern = Pattern.compile("(^|\\/)(latest)\\/");
	
	/**
	 * Gets the.
	 *
	 * @return the string
	 * @throws AAIException the AAI exception
	 */
	public static String get() throws AAIException {
		
		String apiVersion = null;
		try {
			Message message = PhaseInterceptorChain.getCurrentMessage();
			String requestURI = (String) message.get(Message.REQUEST_URI);
			
			if (requestURI != null) {
				Matcher matcher = versionPattern.matcher(requestURI);
				if (matcher.find() && matcher.groupCount() >= 2) {
					apiVersion = matcher.group(2);
		        }
				if (apiVersion == null) { 
					Matcher latestMatcher = latestVersionPattern.matcher(requestURI);
					if (latestMatcher.find() && latestMatcher.groupCount() >= 2) {
						apiVersion = AAIConfig.get(AAIConstants.AAI_DEFAULT_API_VERSION_PROP, AAIConstants.AAI_DEFAULT_API_VERSION);
					}
				}
				
			}
			
		} catch (Exception e) { 
			// TODO: we may want to log an error here
		}
		// TODO: should this check the value a little closer and look for a pattern?
		if (apiVersion == null || !apiVersion.startsWith("v")) { 
			apiVersion = AAIConfig.get (AAIConstants.AAI_DEFAULT_API_VERSION_PROP, AAIConstants.AAI_DEFAULT_API_VERSION);
			//apiVersion = AAIConstants.AAI_DEFAULT_API_VERSION;
		}
		return apiVersion;
	}
}
