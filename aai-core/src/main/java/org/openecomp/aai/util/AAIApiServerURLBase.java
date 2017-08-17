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

import java.util.List;
import java.util.Map;

import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;

import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Version;

public class AAIApiServerURLBase {
	
	/**
	 * Gets the.
	 *
	 * @return the string
	 * @throws AAIException the AAI exception
	 */
	public static String get() throws AAIException {
		
		String hostName = null;
		try {
			Message message = PhaseInterceptorChain.getCurrentMessage();
			Map<String, List<String>> headers = CastUtils.cast((Map) message.get(Message.PROTOCOL_HEADERS));
			List sa = null;
			    if (headers != null) {
			            sa = headers.get("host");
			        }

			        if (sa != null && sa.size() == 1) {
			            hostName = "https://"+ sa.get(0).toString() + "/aai/";
			        }
		} catch (Exception e) { 
			// TODO: we may want to log an error here
		}
		// TODO: should this check the value a little closer and look for a pattern?
		if (hostName == null) { 
			hostName = AAIConfig.get(AAIConstants.AAI_SERVER_URL_BASE);
			//AAIConstants.AAI_SERVER_URL_BASE;
		}
		return hostName;
	}
	
	/**
	 * Gets the.
	 *
	 * @param v the v
	 * @return the string
	 * @throws AAIException the AAI exception
	 */
	public static String get(Version v) throws AAIException {
		String hostName = null;
	    hostName = AAIApiServerURLBase.get();
		
		return hostName;
	}
	
}
