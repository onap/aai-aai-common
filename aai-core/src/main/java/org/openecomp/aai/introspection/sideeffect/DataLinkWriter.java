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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.tinkerpop.gremlin.structure.Vertex;

import org.openecomp.aai.db.props.AAIProperties;
import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Introspector;
import org.openecomp.aai.introspection.sideeffect.exceptions.AAIMultiplePropertiesException;
import org.openecomp.aai.parsers.query.QueryParser;
import org.openecomp.aai.parsers.uri.URIToObject;
import org.openecomp.aai.restcore.util.URITools;
import org.openecomp.aai.schema.enums.PropertyMetadata;
import org.openecomp.aai.serialization.db.DBSerializer;
import org.openecomp.aai.serialization.engines.TransactionalGraphEngine;

public class DataLinkWriter extends SideEffect {

	public DataLinkWriter(Introspector obj, Vertex self, TransactionalGraphEngine dbEngine, DBSerializer serializer) {
		super(obj, self, dbEngine, serializer);
	}

	@Override
	protected PropertyMetadata getPropertyMetadata() {
		return PropertyMetadata.DATA_LINK;
	}

	@Override
	protected void processURI(Optional<String> completeUri, Entry<String, String> entry)
			throws URISyntaxException, UnsupportedEncodingException, AAIException {
		if (completeUri.isPresent()) {
			URI uri = new URI(completeUri.get());
			MultivaluedMap<String, String> map = URITools.getQueryMap(uri);
			QueryParser uriQuery = dbEngine.getQueryBuilder(this.latestLoader).createQueryFromURI(uri, map);
			List<Vertex> results = uriQuery.getQueryBuilder().toList();
			if (results.size() == 1) {
				if (results.get(0).<Boolean>property(AAIProperties.LINKED).orElse(false) && obj.getValue(entry.getKey()) == null) {
					//delete vertex because property was removed
					serializer.delete(results.get(0), "", false);
				} else {
					//link vertex that already exists
					this.addLinkedProperty(results.get(0));
				}
 			} else {
				if (results.isEmpty()) {
					//locate previously linked vertex
					List<Vertex> linkedVertices = uriQuery.getQueryBuilder().getContainerQuery().getVerticesByProperty(AAIProperties.LINKED, true).toList();
					if (!linkedVertices.isEmpty()) {
						if (linkedVertices.size() > 1) {
							throw new AAIMultiplePropertiesException("multiple vertices found for single cardinality propery found when searching " + uri);
						} else {
							//found one, remove the linked property because it didn't match the uri
							linkedVertices.get(0).property(AAIProperties.LINKED).remove();
						}
					}
					if (obj.getValue(entry.getKey()) != null) {
						//add new vertex to database if we have values
						URIToObject parser = new URIToObject(this.latestLoader, uri);
						Introspector resultObj = parser.getEntity();
						Vertex newV = serializer.createNewVertex(resultObj);
						serializer.serializeToDb(resultObj, newV, uriQuery, completeUri.get(), this.latestLoader.getVersion().toString());
						this.addLinkedProperty(newV);
					}
				} else if (results.size() > 1) {
					throw new AAIMultiplePropertiesException("multiple values of " + entry.getKey() + " found when searching " + uri);
				}
			}
		} else {
			//skip processing because no required properties were specified
		}
	}
	
	@Override
	protected boolean replaceWithWildcard() {
		return true;
	}
	
	private void addLinkedProperty(Vertex v) {
		v.property(AAIProperties.LINKED, true);
	}


}
