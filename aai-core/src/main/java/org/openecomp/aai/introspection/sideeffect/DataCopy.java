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

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Introspector;
import org.openecomp.aai.introspection.sideeffect.exceptions.AAIMissingRequiredPropertyException;
import org.openecomp.aai.introspection.sideeffect.exceptions.AAIMultiplePropertiesException;
import org.openecomp.aai.parsers.query.QueryParser;
import org.openecomp.aai.restcore.util.URITools;
import org.openecomp.aai.schema.enums.PropertyMetadata;
import org.openecomp.aai.serialization.db.DBSerializer;
import org.openecomp.aai.serialization.engines.TransactionalGraphEngine;

import javax.ws.rs.core.MultivaluedMap;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;


public class DataCopy extends SideEffect {

	
	public DataCopy(Introspector obj, Vertex self, TransactionalGraphEngine dbEngine, DBSerializer serializer) {
		super(obj, self, dbEngine, serializer);
	}
	
	@Override
	protected void processURI(Optional<String> completeUri, Entry<String, String> entry) throws URISyntaxException, UnsupportedEncodingException, AAIException {
		if (completeUri.isPresent()) {
			URI uri = new URI(completeUri.get());
			MultivaluedMap<String, String> map = URITools.getQueryMap(uri);
			QueryParser uriQuery = dbEngine.getQueryBuilder(this.latestLoader).createQueryFromURI(uri, map);
			List<Vertex> results = uriQuery.getQueryBuilder().toList();
			Introspector resultObj = this.latestLoader.introspectorFromName(uriQuery.getResultType());
			if (results.size() == 1) {
				serializer.dbToObject(Collections.singletonList(results.get(0)), resultObj, 0, true, "false");
				try {
					obj.setValue(entry.getKey(), Objects.requireNonNull(resultObj.getValue(uri.getFragment()), uri.getFragment() + " was null"));
				} catch (NullPointerException e) {
					throw new AAIMissingRequiredPropertyException("property " + uri.getFragment() + " not found at " + uri);
				}
 			} else {
				if (results.isEmpty()) {
					throw new AAIException("AAI_6114", "object located at " + uri + " not found");
				} else if (results.size() > 1) {
					throw new AAIMultiplePropertiesException("multiple values of " + entry.getKey() + " found when searching " + uri);
				}
			}
		} else {
			//skip processing because no required properties were specified
		}
	}

	@Override
	protected PropertyMetadata getPropertyMetadata() {
		return PropertyMetadata.DATA_COPY;
	}

	@Override
	protected boolean replaceWithWildcard() {
		return false;
	}
	
}
