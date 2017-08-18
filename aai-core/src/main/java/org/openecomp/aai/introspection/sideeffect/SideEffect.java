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

package org.openecomp.aai.introspection.sideeffect;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tinkerpop.gremlin.structure.Vertex;

import org.openecomp.aai.db.props.AAIProperties;
import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Introspector;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.introspection.LoaderFactory;
import org.openecomp.aai.introspection.ModelType;
import org.openecomp.aai.introspection.sideeffect.exceptions.AAIMissingRequiredPropertyException;
import org.openecomp.aai.schema.enums.PropertyMetadata;
import org.openecomp.aai.serialization.db.DBSerializer;
import org.openecomp.aai.serialization.engines.TransactionalGraphEngine;

public abstract class SideEffect {

	protected static final Pattern template = Pattern.compile("\\{(.*?)\\}");
	protected final Introspector obj;
	protected final TransactionalGraphEngine dbEngine;
	protected final DBSerializer serializer;
	protected final Loader latestLoader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, AAIProperties.LATEST);
	protected final Vertex self;
	public SideEffect (Introspector obj, Vertex self, TransactionalGraphEngine dbEngine, DBSerializer serializer) {
		this.obj = obj;
		this.dbEngine = dbEngine;
		this.serializer = serializer;
		this.self = self;
	}

	protected void execute() throws UnsupportedEncodingException, URISyntaxException, AAIException {
		final Map<String, String> properties = this.findPopertiesWithMetadata(obj, this.getPropertyMetadata());
		for (Entry<String, String> entry : properties.entrySet()) {
			Optional<String> populatedUri = this.replaceTemplates(obj, entry.getValue());
			Optional<String> completeUri = this.resolveRelativePath(populatedUri);
			this.processURI(completeUri, entry);
		}
	}

	protected Map<String, String> findPopertiesWithMetadata(Introspector obj, PropertyMetadata metadata) {
		final Map<String, String> result = new HashMap<>();
		for (String prop : obj.getProperties()) {
			final Map<PropertyMetadata, String> map = obj.getPropertyMetadata(prop);
			if (map.containsKey(metadata)) {
				result.put(prop, map.get(metadata));
			}
		}
		return result;
	}
	
	protected Map<String, String> findProperties(Introspector obj, String uriString) throws AAIMissingRequiredPropertyException {
		
		final Map<String, String> result = new HashMap<>();
		final Set<String> missing = new LinkedHashSet<>();
		Matcher m = template.matcher(uriString);
		int properties = 0;
		while (m.find()) {
			String propName = m.group(1);
			String value = obj.getValue(propName);
			properties++;
			if (value != null) {
				result.put(propName, value);
			} else {
				if (replaceWithWildcard()) {
					result.put(propName, "*");
				}
				missing.add(propName);
			}
		}
		
		if (!missing.isEmpty() && (properties != missing.size())) {
			throw new AAIMissingRequiredPropertyException("Cannot complete " + this.getPropertyMetadata().toString() + " uri. Missing properties " + missing);
		}
		return result;
	}
	
	private Optional<String> replaceTemplates(Introspector obj, String uriString) throws AAIMissingRequiredPropertyException {
		String result = uriString;
		final Map<String, String> propMap = this.findProperties(obj, uriString);
		if (propMap.isEmpty()) {
			return Optional.empty();
		}
		for (Entry<String, String> entry : propMap.entrySet()) {
			result = result.replaceAll("\\{" + entry.getKey() + "\\}", entry.getValue());
		}
		//drop out wildcards if they exist
		result = result.replaceFirst("/[^/]+?(?:/\\*)+", "");
		return Optional.of(result);
	}
	
	private Optional<String> resolveRelativePath(Optional<String> populatedUri) throws UnsupportedEncodingException {
		if (!populatedUri.isPresent()) {
			return Optional.empty();
		} else {
			return Optional.of(populatedUri.get().replaceFirst("\\./", this.serializer.getURIForVertex(self) + "/"));
		}
	}
	
	protected abstract boolean replaceWithWildcard();
	protected abstract PropertyMetadata getPropertyMetadata();
	protected abstract void processURI(Optional<String> completeUri, Entry<String, String> entry) throws URISyntaxException, UnsupportedEncodingException, AAIException;
}
