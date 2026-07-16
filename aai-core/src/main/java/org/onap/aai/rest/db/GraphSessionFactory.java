/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2026 Deutsche Telekom.
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

package org.onap.aai.rest.db;

import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.serialization.engines.JanusGraphDBEngine;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.setup.SchemaVersion;

/**
 * Stateless factory that binds a schema version to its graph-access collaborators.
 * Replaces the session-factory half of the former {@code HttpEntry}. A single
 * singleton instance is safe to share: each {@link #bind} call returns a fresh
 * {@link GraphBinding} wrapping its own {@link JanusGraphDBEngine}, and no
 * transaction is opened until the caller asks for one.
 */
public class GraphSessionFactory {

    private final LoaderFactory loaderFactory;

    public GraphSessionFactory(LoaderFactory loaderFactory) {
        this.loaderFactory = loaderFactory;
    }

    public GraphBinding bind(ModelType modelType, QueryStyle queryStyle, SchemaVersion version) {
        return bind(modelType, queryStyle, version, null);
    }

    public GraphBinding bind(ModelType modelType, QueryStyle queryStyle, SchemaVersion version, String serverBase) {
        Loader loader = loaderFactory.createLoaderForVersion(modelType, version);
        TransactionalGraphEngine dbEngine = new JanusGraphDBEngine(queryStyle, loader);
        return new GraphBinding(modelType, queryStyle, version, loader, dbEngine, serverBase);
    }
}
