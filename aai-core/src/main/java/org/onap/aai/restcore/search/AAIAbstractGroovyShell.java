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

import groovy.lang.GroovyShell;
import groovy.transform.TimedInterrupt;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.onap.aai.config.SpringContextAware;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.setup.SchemaVersions;

public abstract class AAIAbstractGroovyShell {

    protected final GroovyShell shell;

    public AAIAbstractGroovyShell() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("value", 30000);
        parameters.put("unit",
                new PropertyExpression(new ClassExpression(ClassHelper.make(TimeUnit.class)), "MILLISECONDS"));

        ASTTransformationCustomizer custom = new ASTTransformationCustomizer(parameters, TimedInterrupt.class);
        ImportCustomizer imports = new ImportCustomizer();
        imports.addStaticStars("org.apache.tinkerpop.gremlin.process.traversal.P",
                "org.apache.tinkerpop.gremlin.process.traversal.Order");
        imports.addImports("org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__",
                "org.apache.tinkerpop.gremlin.structure.T", "org.apache.tinkerpop.gremlin.process.traversal.P",
                "org.onap.aai.edges.enums.EdgeType", "java.util.Map.Entry");
        imports.addStarImports("java.util");
        CompilerConfiguration config = new CompilerConfiguration();
        config.addCompilationCustomizers(custom, imports);

        this.shell = new GroovyShell(config);
    }

    /**
     *
     * @param engine
     * @param traversal
     * @param params
     * @return result of graph traversal
     */
    public abstract String executeTraversal(TransactionalGraphEngine engine, String traversal,
            Map<String, Object> params);

    /**
     * @param traversal
     * @param params
     * @return result of graph traversal
     */
    public abstract GraphTraversal<?, ?> executeTraversal(String traversal, Map<String, Object> params);

    /**
     *
     * @param engine
     * @param traversal
     * @param params
     * @return result of graph traversal
     */
    public abstract String executeTraversal(TransactionalGraphEngine engine, String traversal,
                                            Map<String, Object> params, QueryStyle style, GraphTraversalSource source);

    protected Loader getLoader(){
        SchemaVersions schemaVersions = (SchemaVersions) SpringContextAware.getBean("schemaVersions");
        return SpringContextAware.getBean(LoaderFactory.class).createLoaderForVersion(ModelType.MOXY,
            schemaVersions.getDefaultVersion());
    }
}
