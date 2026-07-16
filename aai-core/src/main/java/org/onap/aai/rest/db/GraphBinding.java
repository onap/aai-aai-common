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

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.setup.SchemaVersion;

/**
 * Immutable binding of a schema version to its graph-access collaborators.
 * Produced by {@link GraphSessionFactory}. Carries no live transaction; callers
 * open and close transactions explicitly via {@link #startTransaction()} /
 * {@link #commit()} / {@link #rollback()}.
 */
public record GraphBinding(
        ModelType modelType,
        QueryStyle queryStyle,
        SchemaVersion version,
        Loader loader,
        TransactionalGraphEngine dbEngine,
        String serverBase) {

    public Graph startTransaction() {
        return dbEngine.startTransaction();
    }

    public void commit() {
        dbEngine.commit();
    }

    public void rollback() {
        dbEngine.rollback();
    }
}
