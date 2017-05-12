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

package org.openecomp.aai.parsers.uri;

import javax.ws.rs.core.MultivaluedMap;

import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Introspector;

class URIValidate implements Parsable {

	@Override
	public void processObject(Introspector obj, MultivaluedMap<String, String> uriKeys) throws AAIException {
		//NO-OP
		//just want to make sure this URI has valid tokens
	}

	@Override
	public void processContainer(Introspector obj, MultivaluedMap<String, String> uriKeys, boolean isFinalContainer)
			throws AAIException {
		//NO-OP
		//just want to make sure this URI has valid tokens

	}

	@Override
	public void processNamespace(Introspector obj) {
		//NO-OP
		//just want to make sure this URI has valid tokens

	}

	@Override
	public String getCloudRegionTransform() {
		return "none";
	}

	@Override
	public boolean useOriginalLoader() {
		
		return true;
	}

}
