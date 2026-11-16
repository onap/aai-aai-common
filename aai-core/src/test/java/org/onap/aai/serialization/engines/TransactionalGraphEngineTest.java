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

package org.onap.aai.serialization.engines;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.janusgraph.core.JanusGraphException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.introspection.ModelType;

public class TransactionalGraphEngineTest extends AAISetup {

    private TransactionalGraphEngine dbEngine;
    private final List<String> calls = new ArrayList<>();

    @Before
    public void setup() {
        dbEngine = new JanusGraphDBEngine(QueryStyle.TRAVERSAL,
                loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDefaultVersion()));
        dbEngine.startTransaction();
    }

    @After
    public void tearDown() {
        dbEngine.rollback();
    }

    @Test
    public void thatAfterCommitActionsRunOnlyOnceTheTransactionIsCommitted() {
        dbEngine.afterCommit(() -> calls.add("first"));
        dbEngine.afterCommit(() -> calls.add("second"));

        assertTrue(calls.isEmpty());

        dbEngine.commit();

        assertEquals(List.of("first", "second"), calls);
    }

    @Test
    public void thatAfterCommitActionsAreDiscardedOnRollback() {
        dbEngine.afterCommit(() -> calls.add("action"));

        dbEngine.rollback();
        dbEngine.startTransaction();
        dbEngine.commit();

        assertTrue(calls.isEmpty());
    }

    @Test
    public void thatAfterCommitActionsRunOnlyForTheTransactionTheyWereRegisteredIn() {
        dbEngine.afterCommit(() -> calls.add("action"));
        dbEngine.commit();

        dbEngine.startTransaction();
        dbEngine.commit();

        assertEquals(List.of("action"), calls);
    }

    @Test
    public void thatAfterCommitActionsDoNotRunWhenTheCommitFails() {
        Transaction transaction = mock(Transaction.class);
        doThrow(new JanusGraphException("commit failed")).when(transaction).commit();
        Graph graph = mock(Graph.class);
        when(graph.tx()).thenReturn(transaction);
        TransactionalGraphEngine failingEngine = spy(dbEngine);
        doReturn(graph).when(failingEngine).tx();

        failingEngine.afterCommit(() -> calls.add("action"));

        assertThrows(JanusGraphException.class, failingEngine::commit);
        assertTrue(calls.isEmpty());
    }

    @Test
    public void thatAFailingAfterCommitActionDoesNotFailTheCommitOrSkipTheOthers() {
        dbEngine.afterCommit(() -> {
            throw new IllegalStateException("kafka unavailable");
        });
        dbEngine.afterCommit(() -> calls.add("second"));

        dbEngine.commit();

        assertEquals(List.of("second"), calls);
    }
}
