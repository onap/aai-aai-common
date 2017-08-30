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

import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Introspector;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.introspection.LoaderFactory;
import org.openecomp.aai.introspection.Version;
import org.openecomp.aai.logging.ErrorLogHelper;
import org.openecomp.aai.parsers.exceptions.DoesNotStartWithValidNamespaceException;
import org.openecomp.aai.rest.RestTokens;
import org.openecomp.aai.schema.enums.ObjectMetadata;
import org.openecomp.aai.serialization.db.EdgeType;
import org.openecomp.aai.util.AAIConfig;
import org.springframework.web.util.UriUtils;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Set;


/**
 * The Class URIParser.
 */
public class URIParser {
	
	private URI uri = null;

	protected Loader loader = null;
	
	protected Loader originalLoader = null;
	
	private URI originalURI = null;
	
	private MultivaluedMap<String, String> queryParams = null;
	
	
	/**
	 * Instantiates a new URI parser.
	 *
	 * @param loader the loader
	 * @param uri the uri
	 */
	public URIParser(Loader loader, URI uri) {
		this.uri = uri;

		String currentVersion = "v7";
		this.originalLoader = loader;
		try {
			currentVersion = AAIConfig.get("aai.default.api.version");
		} catch (AAIException e) {
			ErrorLogHelper.logException(e);
		}

		//Load the latest version because we need it for cloud region

			this.loader = loader;
		}
	
	/**
	 * Instantiates a new URI parser.
	 *
	 * @param loader the loader
	 * @param uri the uri
	 * @param queryParams the query params
	 */
	public URIParser(Loader loader, URI uri, MultivaluedMap<String, String> queryParams) {
		this(loader, uri);
		this.queryParams = queryParams;
	}

	public Loader getLoader() {
		
		return this.loader;
		
	}
	
	/**
	 * Gets the original URI.
	 *
	 * @return the original URI
	 */
	public URI getOriginalURI() {
		return this.originalURI;
	}
	
	/**
	 * Parses the.
	 *
	 * @param p the p
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 * @throws AAIException the AAI exception
	 */
	public void parse(Parsable p) throws UnsupportedEncodingException, AAIException {
		try {
			boolean isRelative = false;
			uri = this.trimURI(uri);
			uri = handleCloudRegion(p.getCloudRegionTransform(), uri);
			if (p.useOriginalLoader()) {
				this.loader = this.originalLoader;
			}
			this.originalURI  = UriBuilder.fromPath(uri.getRawPath()).build();
			if (uri.getRawPath().startsWith("./")) {
				uri = new URI(uri.getRawPath().replaceFirst("\\./", ""));
				isRelative = true;
			}
			String[] parts = uri.getRawPath().split("/");
			Introspector validNamespaces = loader.introspectorFromName("inventory");
			Set<String> keys = null;
			String part = "";
			Introspector previousObj = null;
			EdgeType type = EdgeType.TREE;
			for (int i = 0; i < parts.length;) {
				part = parts[i];
				Introspector introspector = null;
				if (part.equals(RestTokens.COUSIN.toString())) {
					if (i == parts.length-1) {
						throw new AAIException("AAI_3000", uri + " not a valid path. Cannot end in " + RestTokens.COUSIN);
					}
					introspector = loader.introspectorFromName(parts[i+1]);
					if (previousObj.isContainer() && introspector.isContainer()) {
						throw new AAIException("AAI_3000", uri + " not a valid path. Cannot chain plurals together");
					}
					MultivaluedMap<String, String> uriKeys = new MultivaluedHashMap<>();
					if (i == parts.length-2 && queryParams != null) {
						Set<String> queryKeys = queryParams.keySet();
						for (String key : queryKeys) {
							uriKeys.put(key, queryParams.get(key));
							
						}
					}
					if (introspector.isContainer()) {
						boolean isFinalContainer = i == parts.length-2;
						p.processContainer(introspector, EdgeType.COUSIN, uriKeys, isFinalContainer);
					}
					previousObj = introspector;
					type = EdgeType.COUSIN;
					i+=2;
					continue;
				}
				introspector = loader.introspectorFromName(part);
				if (introspector != null) {
					
					//previous has current as property
					if (previousObj != null && !previousObj.hasChild(introspector) && !previousObj.getDbName().equals("nodes")) {
						throw new AAIException("AAI_3001", uri + " not a valid path. " + part + " not valid");
					} else if (previousObj == null) {
						String abstractType = introspector.getMetadata(ObjectMetadata.ABSTRACT);
						if (abstractType == null) {
							abstractType = "";
						}
						//first time through, make sure it starts from a namespace
						//ignore abstract types
						if (!isRelative && !abstractType.equals("true") && !validNamespaces.hasChild(introspector)) {
							throw new DoesNotStartWithValidNamespaceException( uri + " not a valid path. It does not start from a valid namespace");
						}
					}
					
					keys = introspector.getKeys();
					if (keys.size() > 0) {
						MultivaluedMap<String, String> uriKeys = new MultivaluedHashMap<>();
						i++;
						if (i == parts.length && queryParams != null) {
							Set<String> queryKeys = queryParams.keySet();
							for (String key : queryKeys) {
								uriKeys.put(key, queryParams.get(key));
							}
						} else {
							for (String key : keys) {
								part =  UriUtils.decode(parts[i], "UTF-8");
								
								introspector.setValue(key, part);
								
								//skip this for further processing
								i++;
							}
						}
						
						p.processObject(introspector, type, uriKeys);
						type = EdgeType.TREE;
					} else if (introspector.isContainer()) {
						boolean isFinalContainer = i == parts.length-1;
						MultivaluedMap<String, String> uriKeys = new MultivaluedHashMap<>();
						
						if (isFinalContainer && queryParams != null) {
							Set<String> queryKeys = queryParams.keySet();
							for (String key : queryKeys) {
								uriKeys.put(key, queryParams.get(key));
								
							}
						}
						p.processContainer(introspector, type, uriKeys, isFinalContainer);
						
						i++; 
					} else {
						p.processNamespace(introspector);
						//namespace case
						i++;
					}
					previousObj = introspector;
				} else {
					//invalid item found should log
					//original said bad path
					throw new AAIException("AAI_3001", "invalid item found in path: " + part);
				}
			}
		} catch (AAIException e) {
			throw e;
		} catch (Exception e) {
			throw new AAIException("AAI_3001", e);
		}
	}
	
	public boolean validate() throws UnsupportedEncodingException, AAIException {
		this.parse(new URIValidate());
		return true;
	}
	/**
	 * Handle cloud region.
	 *
	 * @param action the action
	 * @param uri the uri
	 * @return the uri
	 */
	protected URI handleCloudRegion(String action, URI uri) {

			return uri;

	}
	
	/**
	 * Trim URI.
	 *
	 * @param uri the uri
	 * @return the uri
	 */
	protected URI trimURI(URI uri) {
		
		String result = uri.getRawPath();
		if (result.startsWith("/")) {
			result = result.substring(1, result.length());
		}
		
		if (result.endsWith("/")) {
			result = result.substring(0, result.length() - 1);
		}
		
		result = result.replaceFirst("aai/v\\d+/", "");
		
		return UriBuilder.fromPath(result).build();
	}

}
