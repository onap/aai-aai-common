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
import org.janusgraph.core.JanusGraphException;
import org.janusgraph.core.JanusGraphTransaction;
import org.onap.aai.logging.ErrorLogHelper;
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
        boolean dbAvailable;
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
            dbAvailable = transaction.traversal().V().limit(1).hasNext();
        } catch (JanusGraphException e) {
            String message = "Database is not available (after JanusGraph exception)";
            ErrorLogHelper.logError("500", message + ": " + e.getMessage());
            log.error(message, e);
            dbAvailable = false;
        } catch (Error e) {
            // Following error occurs when aai resources is starting:
            // - UnsatisfiedLinkError (for org.onap.aai.dbmap.AAIGraph$Helper instantiation)
            // Following errors are raised when aai resources is starting and cassandra is not
            // running:
            // - ExceptionInInitializerError
            // - NoClassDefFoundError (definition for org.onap.aai.dbmap.AAIGraph$Helper is not
            // found)
            String message = "Database is not available (after error)";
            ErrorLogHelper.logError("500", message + ": " + e.getMessage());
            log.error(message, e);
            dbAvailable = false;
        } catch (Exception e) {
            String message = "Database availability can not be determined";
            ErrorLogHelper.logError("500", message + ": " + e.getMessage());
            log.error(message, e);
            dbAvailable = false;
        } finally {
            if (transaction != null && !transaction.isClosed()) {
                // check if transaction is open then closed instead of flag
                try {
                    transaction.rollback();
                } catch (Exception e) {
                    String message = "Exception occurred while closing transaction";
                    log.error(message, e);
                    ErrorLogHelper.logError("500", message + ": " + e.getMessage());
                }
            }
        }
        return dbAvailable;
    }
}
