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

package org.onap.aai.restcore.search;

import groovy.lang.Binding;
import groovy.lang.Script;

import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.config.SpringContextAware;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.query.builder.QueryBuilder;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.setup.SchemaVersions;

/**
 * Creates and returns a groovy shell with the
 * configuration to statically import graph classes
 *
 */
public class GroovyQueryBuilder extends AAIAbstractGroovyShell {

    public GroovyQueryBuilder() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String executeTraversal(TransactionalGraphEngine engine, String traversal, Map<String, Object> params) {
        QueryBuilder<Vertex> builder = engine.getQueryBuilder(QueryStyle.GREMLIN_TRAVERSAL);
        SchemaVersions schemaVersions = (SchemaVersions) SpringContextAware.getBean("schemaVersions");
        Loader loader = SpringContextAware.getBean(LoaderFactory.class).createLoaderForVersion(ModelType.MOXY,
                schemaVersions.getDefaultVersion());

        builder.changeLoader(loader);
        Binding binding = new Binding(params);
        binding.setVariable("builder", builder);
        Script script = shell.parse(traversal);
        script.setBinding(binding);
        script.run();

        return builder.getQuery();
    }

    /**
     * @throws UnsupportedOperationException
     */
    @Override
    public GraphTraversal<?, ?> executeTraversal(String traversal, Map<String, Object> params) {
        throw new UnsupportedOperationException();
    }
}
