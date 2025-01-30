/*
 * ============LICENSE_START=======================================================
 * Copyright (C) 2022 Bell Canada
 * Modification Copyright (C) 2024 Deutsche Telekom SA
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

import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphTransaction;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GraphChecker {

    private final JanusGraph graph;

    /**
     * Checks whether a connection to the graph database can be made.
     *
     * @return
     *         <li>true, if database is available</li>
     *         <li>false, if database is NOT available</li>
     */
    public boolean isAaiGraphDbAvailable() {
        JanusGraphTransaction transaction = null;
        try {
            // disable caching and other features that are not needed for this check
            transaction = graph
                .buildTransaction()
                .readOnly()
                .consistencyChecks(false)
                .vertexCacheSize(0)
                .skipDBCacheRead()
                .start();
            transaction.traversal().V().limit(1).hasNext(); // if this is not throwing an exception, the database is available
            return true;
        } catch (Throwable e) {
            log.error("Database is not available: ", e);
            return false;
        } finally {
            if (transaction != null && !transaction.isClosed()) {
                // check if transaction is open then closed instead of flag
                try {
                    transaction.rollback();
                } catch (Exception e) {
                    log.error("Exception occurred while closing transaction", e);
                }
            }
        }
    }
}
