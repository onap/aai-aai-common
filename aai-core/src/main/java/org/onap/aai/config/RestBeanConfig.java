/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright © 2018 IBM.
 * Modifications Copyright © 2024 DEUTSCHE TELEKOM AG.
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

package org.onap.aai.config;

import org.onap.aai.introspection.ModelType;
import org.onap.aai.rest.db.HttpEntry;
import org.onap.aai.serialization.engines.QueryStyle;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.annotation.RequestScope;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

@Configuration
public class RestBeanConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JaxbAnnotationModule());
        return objectMapper;
    }

    @Bean(name = "traversalUriHttpEntry")
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public HttpEntry traversalUriHttpEntry() {
        return new HttpEntry(ModelType.MOXY, QueryStyle.TRAVERSAL_URI);
    }

    @Bean(name = "traversalHttpEntry")
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public HttpEntry traversalHttpEntry() {
        return new HttpEntry(ModelType.MOXY, QueryStyle.TRAVERSAL);
    }

    /**
     * The HttpEntry class is not thread-safe due to the contained JanusGraphDBEngine.
     * As such, assure that a new instance is returned for every injection by making it
     * request scoped.
     */
    @RequestScope
    @Bean(name = "requestScopedTraversalUriHttpEntry")
    public HttpEntry requestScopedTraversalUriHttpEntry() {
        return new HttpEntry(ModelType.MOXY, QueryStyle.TRAVERSAL_URI);
    }

    /**
     * The HttpEntry class is not thread-safe due to the contained JanusGraphDBEngine.
     * As such, assure that a new instance is returned for every injection by making it
     * request scoped.
     */
    @RequestScope
    @Bean(name = "requestScopedTraversalHttpEntry")
    public HttpEntry requestScopedTraversalHttpEntry() {
        return new HttpEntry(ModelType.MOXY, QueryStyle.TRAVERSAL);
    }

}
