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

package org.openecomp.aai.serialization.queryformats;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.serialization.db.DBSerializer;
import org.openecomp.aai.serialization.queryformats.exceptions.QueryParamInjectionException;
import org.openecomp.aai.serialization.queryformats.utils.QueryParamInjector;
import org.openecomp.aai.serialization.queryformats.utils.UrlBuilder;

public class FormatFactory {

	private final Loader loader;
	private final DBSerializer serializer;
	private final UrlBuilder urlBuilder;
	private final QueryParamInjector injector;
	public FormatFactory (Loader loader, DBSerializer serializer) throws AAIException {
		this.loader = loader;
		this.serializer = serializer;
		this.urlBuilder = new UrlBuilder(loader.getVersion(), serializer);
		this.injector = QueryParamInjector.getInstance();
	}
	
	public Formatter get(Format format) throws AAIException {
		return get(format, new MultivaluedHashMap<String, String>());
	}
	
	public Formatter get(Format format, MultivaluedMap<String, String> params) throws AAIException {
		
		Formatter formattter = null;

		switch (format) {
			case graphson :
				formattter = new Formatter(inject(new GraphSON(), params));
				break;
			case pathed :
				formattter = new Formatter(inject(new PathedURL(loader, urlBuilder), params));
				break;
			case id :
				formattter = new Formatter(inject(new IdURL(loader, urlBuilder), params));
				break;
			case resource :
				formattter = new Formatter(inject(new Resource.Builder(loader, serializer, urlBuilder), params).build());
				break;
			case resource_and_url :
				formattter = new Formatter(inject(new Resource.Builder(loader, serializer, urlBuilder).includeUrl(), params).build());
				break;
			case raw :
				formattter = new Formatter(inject(new RawFormat.Builder(loader, serializer, urlBuilder), params).build());
				break;
			case simple :
				formattter = new Formatter(inject(new RawFormat.Builder(loader, serializer, urlBuilder).depth(0).modelDriven(), params).build());
				break;
			case console :
				formattter = new Formatter(inject(new Console(), params));
				break;
			default :
				break;

		}
		
		return formattter;
	}
	
	private <T> T inject (T obj, MultivaluedMap<String, String> params) throws QueryParamInjectionException {
		
		injector.injectParams(obj, params);
		return obj;
	}
	
}
