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
import org.openecomp.aai.serialization.db.EdgeType;

/**
 * The Interface Parsable.
 */
public interface Parsable {

	/**
	 * 
	 * @param obj
	 * @param type
	 * @param uriKeys
	 * @throws AAIException
	 */
	public void processObject(Introspector obj, EdgeType type, MultivaluedMap<String, String> uriKeys) throws AAIException;
	/**
	 * Process container.
	 *
	 * @param obj the obj
	 * @param uriKeys the uri keys
	 * @param isFinalContainer the is final container
	 * @throws AAIException the AAI exception
	 */
	public void processContainer(Introspector obj, EdgeType type, MultivaluedMap<String, String> uriKeys, boolean isFinalContainer) throws AAIException;

	/**
	 * Process namespace.
	 *
	 * @param obj the obj
	 */
	public void processNamespace(Introspector obj);

	/**
	 * Gets the cloud region transform.
	 *
	 * @return the cloud region transform
	 */
	public String getCloudRegionTransform();
	
	/**
	 * Use original loader.
	 *
	 * @return true, if successful
	 */
	public boolean useOriginalLoader();
}
