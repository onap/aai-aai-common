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

package org.openecomp.aai.parsers.uri;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Introspector;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.introspection.Version;
import org.openecomp.aai.introspection.exceptions.AAIUnknownObjectException;
import org.openecomp.aai.util.AAIApiServerURLBase;
import org.openecomp.aai.workarounds.LegacyURITransformer;

/**
 * Given a URI a Relationship Object is returned.
 * 
 * The relationship-data objects are created from the keys in the model.
 * The keys are processed in the order they appear in the model.
 
 *
 */
public class URIToRelationshipObject implements Parsable {
	
	private Introspector result = null;
			
	private LegacyURITransformer uriTransformer = null;
	
	private Version originalVersion = null;
	
	private Introspector relationship = null;
	
	private Loader loader = null;
	
	private String baseURL; 
	
	private final URI uri;
	/**
	 * Instantiates a new URI to relationship object.
	 *
	 * @param loader the loader
	 * @param uri the uri
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws AAIException the AAI exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 * @throws MalformedURLException the malformed URL exception
	 */
	public URIToRelationshipObject(Loader loader, URI uri) throws AAIException {
		
		this.loader = loader;
		uriTransformer = LegacyURITransformer.getInstance();
		originalVersion = loader.getVersion();

		try {
			relationship = loader.introspectorFromName("relationship");
		} catch (AAIUnknownObjectException e1) {
			throw new RuntimeException("Fatal error - could not load relationship object!", e1);
		}

		this.baseURL = AAIApiServerURLBase.get(originalVersion);
		this.uri = uri;
		
		}
		
	public URIToRelationshipObject(Loader loader, URI uri, String baseURL) throws AAIException {
		this(loader, uri);
		
		if (baseURL != null) {
			this.baseURL = baseURL;
		}
	}

	
	/**
	 * @{inheritDoc}
	 */
	@Override
	public String getCloudRegionTransform(){
		return "remove";
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	public void processObject(Introspector obj, MultivaluedMap<String, String> uriKeys) {
		

		for (String key : obj.getKeys()) {
			try {
				Introspector data = loader.introspectorFromName("relationship-data");
				data.setValue("relationship-key", obj.getDbName() + "." + key);
				data.setValue("relationship-value", obj.getValue(key));
				
				((List<Object>)relationship.getValue("relationship-data")).add(data.getUnderlyingObject());
			} catch (AAIUnknownObjectException e) {
				throw new RuntimeException("Fatal error - relationship-data object not found!");
			}
		}
		relationship.setValue("related-to", obj.getDbName());
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	public void processContainer(Introspector obj, MultivaluedMap<String, String> uriKeys, boolean isFinalContainer) {
		
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public void processNamespace(Introspector obj) {
	
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	public boolean useOriginalLoader() {
		return true;
	}
	
	/**
	 * Gets the result.
	 *
	 * @return the result
	 * @throws AAIException 
	 * @throws UnsupportedEncodingException 
	 * @throws URISyntaxException 
	 */
	public Introspector getResult() throws UnsupportedEncodingException, AAIException, URISyntaxException {
		URIParser parser = new URIParser(this.loader, this.uri);
		parser.parse(this);
		URI originalUri = parser.getOriginalURI();
		
		URI relatedLink = new URI(this.baseURL + this.originalVersion + "/" + originalUri);
		this.relationship.setValue("related-link", relatedLink);
		
		
		this.result = relationship;
		return this.result;
	}
}
