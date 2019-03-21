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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.serialization.queryformats.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

import org.onap.aai.serialization.queryformats.exceptions.QueryParamInjectionException;
import org.onap.aai.serialization.queryformats.params.Inject;
import org.onap.aai.serialization.queryformats.params.Setter;
import org.reflections.Reflections;

public class QueryParamInjector {

    private final Set<Class<?>> results;

    private QueryParamInjector() {
        Reflections reflections = new Reflections("org.onap.aai.serialization.queryformats.params");
        results = reflections.getTypesAnnotatedWith(Inject.class);
    }

    private static class Helper {
        private static final QueryParamInjector INSTANCE = new QueryParamInjector();
    }

    public static QueryParamInjector getInstance() {
        return Helper.INSTANCE;
    }

    public <T> T injectParams(T obj, MultivaluedMap<String, String> params)
        throws QueryParamInjectionException {
        try {
            for (Class<?> item : results) {
                if (item.isAssignableFrom(obj.getClass())) {
                    String name = item.getAnnotation(Inject.class).name();

                    if (params.containsKey(name)) {
                        String value = params.getFirst(name);

                        for (Method method : item.getMethods()) {
                            if (method.isAnnotationPresent(Setter.class)) {
                                Class<?>[] args = method.getParameterTypes();
                                if (args.length == 1) {
                                    Object o =
                                        args[0].getConstructor(String.class).newInstance(value);
                                    method.invoke(obj, o);
                                } else {
                                    method.invoke(obj);
                                }
                            }
                        }
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new QueryParamInjectionException("issue with query params", e);
        }

        return obj;
    }
}
