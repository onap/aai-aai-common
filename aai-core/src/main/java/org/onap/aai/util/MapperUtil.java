/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright © 2018 IBM.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

import org.onap.aai.exceptions.AAIException;

public class MapperUtil {

    /**
     * Instantiates MapperUtil.
     */
    private MapperUtil() {
        // prevent instantiation
    }

    /**
     * Read as object of.
     *
     * @param <T> the generic type
     * @param clazz the clazz
     * @param value the value
     * @return the t
     * @throws AAIException the AAI exception
     */
    public static <T> T readAsObjectOf(Class<T> clazz, String value) throws AAIException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(value, clazz);
        } catch (Exception e) {
            throw new AAIException("AAI_4007", e);
        }
    }

    /**
     * Read with dashes as object of.
     *
     * @param <T> the generic type
     * @param clazz the clazz
     * @param value the value
     * @return the t
     * @throws AAIException the AAI exception
     */
    public static <T> T readWithDashesAsObjectOf(Class<T> clazz, String value) throws AAIException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.registerModule(new JaxbAnnotationModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);

            return mapper.readValue(value, clazz);
        } catch (Exception e) {
            throw new AAIException("AAI_4007", e);
        }
    }

    /**
     * Write as JSON string.
     *
     * @param obj the obj
     * @return the string
     * @throws AAIException the AAI exception
     */
    public static String writeAsJSONString(Object obj) throws AAIException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new AAIException("AAI_4008", e);
        }
    }

    /**
     * Write as JSON string with dashes.
     *
     * @param obj the obj
     * @return the string
     * @throws AAIException the AAI exception
     */
    public static String writeAsJSONStringWithDashes(Object obj) throws AAIException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
            mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);

            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);

            mapper.registerModule(new JaxbAnnotationModule());
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new AAIException("AAI_4008", e);
        }
    }
}
