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

package org.openecomp.aai.util;

import java.io.IOException;

import org.apache.commons.io.output.ByteArrayOutputStream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

public class PojoUtils {
	/**
	 * Gets the json from object.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @return the json from object
	 * @throws JsonGenerationException the json generation exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public <T> String getJsonFromObject(T clazz) throws JsonGenerationException, JsonMappingException, IOException {
		return getJsonFromObject(clazz, false, true);
	}
	
	/**
	 * Gets the json from object.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @param wrapRoot the wrap root
	 * @param indent the indent
	 * @return the json from object
	 * @throws JsonGenerationException the json generation exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public <T> String getJsonFromObject(T clazz, boolean wrapRoot, boolean indent) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, indent);
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, wrapRoot);

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, wrapRoot);

        mapper.registerModule(new JaxbAnnotationModule());
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        mapper.writeValue(baos, clazz);
    
        return baos.toString();
	}
}
