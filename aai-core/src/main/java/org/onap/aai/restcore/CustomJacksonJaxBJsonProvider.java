/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.restcore;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jakarta.rs.json.JacksonXmlBindJsonProvider;
import com.fasterxml.jackson.module.jakarta.xmlbind.JakartaXmlBindAnnotationModule;

import jakarta.ws.rs.ext.Provider;

/**
 * The Class CustomJacksonJaxBJsonProvider.
 */
@Provider
public class CustomJacksonJaxBJsonProvider extends JacksonXmlBindJsonProvider {

    private static ObjectMapper commonMapper = null;

    /**
     * Instantiates a new custom jackson jax B json provider.
     */
    public CustomJacksonJaxBJsonProvider() {
        if (commonMapper == null) {
            ObjectMapper mapper = new ObjectMapper();

            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
            mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);

            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);

            mapper.registerModule(new JakartaXmlBindAnnotationModule());

            commonMapper = mapper;
        }
        super.setMapper(commonMapper);
    }

    /**
     * Gets the mapper.
     *
     * @return the mapper
     */
    public ObjectMapper getMapper() {
        return commonMapper;
    }
}
