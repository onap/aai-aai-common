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

package org.openecomp.aai.introspection;

import java.util.Map;

import org.openecomp.aai.introspection.exceptions.AAIUnknownObjectException;
import org.openecomp.aai.introspection.exceptions.AAIUnmarshallingException;
import org.openecomp.aai.restcore.MediaType;

public abstract class Loader {

	private final Version version;
	private final ModelType modelType;
	
	/**
	 * Instantiates a new loader.
	 *
	 * @param version the version
	 * @param modelType the model type
	 * @param llBuilder the ll builder
	 */
	public Loader (Version version, ModelType modelType) {
		this.version = version;
		this.modelType = modelType;
	}
	
	/**
	 * Process.
	 *
	 * @param version the version
	 */
	protected abstract void process(Version version);
	
	/**
	 * Object from name.
	 *
	 * @param name the name
	 * @return the object
	 * @throws AAIUnknownObjectException 
	 */
	public abstract Object objectFromName(String name) throws AAIUnknownObjectException;
	
	/**
	 * Introspector from name.
	 *
	 * @param name the name
	 * @return the introspector
	 * @throws AAIUnknownObjectException 
	 */
	public abstract Introspector introspectorFromName(String name) throws AAIUnknownObjectException;
	
	/**
	 * Unmarshal.
	 *
	 * @param type the type
	 * @param json the json
	 * @param mediaType the media type
	 * @return the introspector
	 */
	public abstract Introspector unmarshal(String type, String json, MediaType mediaType) throws AAIUnmarshallingException;
	
	/**
	 * Unmarshal.
	 *
	 * @param type the type
	 * @param json the json
	 * @return the introspector
	 */
	public Introspector unmarshal(String type, String json) throws AAIUnmarshallingException {
		return unmarshal(type, json, MediaType.APPLICATION_JSON_TYPE);
	}

	
	/**
	 * Gets the model type.
	 *
	 * @return the model type
	 */
	public ModelType getModelType() {
		return this.modelType;
	}
	
	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public Version getVersion() {
		return this.version;
	}
	
	public abstract Map<String, Introspector> getAllObjects();
}
