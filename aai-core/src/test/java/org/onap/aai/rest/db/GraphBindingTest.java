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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.setup.SchemaVersion;

public class GraphBindingTest {

    @Test
    public void accessorsReturnConstructorValues() {
        Loader loader = Mockito.mock(Loader.class);
        TransactionalGraphEngine engine = Mockito.mock(TransactionalGraphEngine.class);
        SchemaVersion v = new SchemaVersion("v29");
        GraphBinding b = new GraphBinding(ModelType.MOXY, QueryStyle.TRAVERSAL_URI, v, loader, engine, "http://base/");
        assertEquals(ModelType.MOXY, b.modelType());
        assertEquals(QueryStyle.TRAVERSAL_URI, b.queryStyle());
        assertEquals(v, b.version());
        assertSame(loader, b.loader());
        assertSame(engine, b.dbEngine());
        assertEquals("http://base/", b.serverBase());
    }

    @Test
    public void transactionMethodsDelegateToEngine() {
        Loader loader = Mockito.mock(Loader.class);
        TransactionalGraphEngine engine = Mockito.mock(TransactionalGraphEngine.class);
        Graph tx = Mockito.mock(Graph.class);
        when(engine.startTransaction()).thenReturn(tx);
        GraphBinding b = new GraphBinding(ModelType.MOXY, QueryStyle.TRAVERSAL_URI, new SchemaVersion("v29"), loader,
                engine, null);

        assertSame(tx, b.startTransaction());
        b.commit();
        b.rollback();

        verify(engine).startTransaction();
        verify(engine).commit();
        verify(engine).rollback();
    }
}
