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

package org.openecomp.aai.serialization.queryformats;

import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.serialization.db.DBSerializer;
import org.openecomp.aai.serialization.queryformats.utils.UrlBuilder;

public class FormatFactory {

	private final Loader loader;
	private final DBSerializer serializer;
	private final UrlBuilder urlBuilder;
	public FormatFactory (Loader loader, DBSerializer serializer) throws AAIException {
		this.loader = loader;
		this.serializer = serializer;
		this.urlBuilder = new UrlBuilder(loader.getVersion(), serializer);
	}
	
	public Formatter get(Format format) throws AAIException {
		
		Formatter formattter = null;

		switch (format) {
			case graphson :
				formattter = new Formatter(new GraphSON());
				break;
			case pathed :
				formattter = new Formatter(new PathedURL(loader, urlBuilder));
				break;
			case id :
				formattter = new Formatter(new IdURL(loader, urlBuilder));
				break;
			case resource :
				formattter = new Formatter(new Resource.Builder(loader, serializer, urlBuilder).build());
				break;
			case resource_and_url :
				formattter = new Formatter(new Resource.Builder(loader, serializer, urlBuilder).includeUrl().build());
				break;
			case simple :
				formattter = new Formatter(new SimpleFormat(urlBuilder));
				break;
			case console :
				formattter = new Formatter(new Console());
				break;
			default :
				break;
		}
		
		return formattter;
	}
	
}
