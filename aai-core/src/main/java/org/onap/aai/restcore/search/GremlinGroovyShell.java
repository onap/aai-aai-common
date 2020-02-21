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
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;

/**
 * Creates and returns a groovy shell with the
 * configuration to statically import graph classes
 *
 */
public class GremlinGroovyShell extends AAIAbstractGroovyShell {

    public GremlinGroovyShell() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphTraversal<?, ?> executeTraversal(String traversal, Map<String, Object> params) {
        Binding binding = new Binding(params);
        Script script = shell.parse(traversal);
        script.setBinding(binding);
        return (GraphTraversal<?, ?>) script.run();
    }

    /**
     * @throws UnsupportedOperationException
     */
    @Override
    public String executeTraversal(TransactionalGraphEngine engine, String traversal, Map<String, Object> params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String executeTraversal(TransactionalGraphEngine engine, String traversal, Map<String, Object> params, QueryStyle style, GraphTraversalSource source) {
        throw new UnsupportedOperationException();
    }
}
