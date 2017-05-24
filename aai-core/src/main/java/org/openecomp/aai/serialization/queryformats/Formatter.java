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

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.openecomp.aai.serialization.queryformats.exceptions.AAIFormatVertexException;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Formatter {

	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(Formatter.class);

	protected JsonParser parser = new JsonParser();
	protected final FormatMapper format;
	public Formatter (FormatMapper format) {
		this.format = format;
	}
	
	public JsonObject output(List<Object> vertices) {
		Stream<Object> stream = null;
		JsonObject result = new JsonObject();
		JsonArray body = new JsonArray();
		if (vertices.size() >= format.parallelThreshold()) {
			stream = vertices.parallelStream();
		} else {
			stream = vertices.stream();
		}
		final boolean isParallel = stream.isParallel();
		stream.map(v -> {
			try {
				return Optional.<JsonObject>of(format.formatObject(v));
			} catch (AAIFormatVertexException e) {
				LOGGER.warn("Failed to format vertex, returning a partial list", e);
			}

			return Optional.<JsonObject>empty();
		}).forEach(obj -> {
			if (obj.isPresent()) {
				if (isParallel) {
					synchronized (body) {
						body.add(obj.get());
					}
				} else {
					body.add(obj.get());
				}
			}
		});
		
		result.add("results", body);
		
		return result.getAsJsonObject();
	}
	
}
