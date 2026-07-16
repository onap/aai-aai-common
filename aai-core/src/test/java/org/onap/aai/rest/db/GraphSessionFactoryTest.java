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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.setup.SchemaVersion;

public class GraphSessionFactoryTest {

    @Test
    public void bindPopulatesBindingAndDoesNotStartTransaction() {
        LoaderFactory loaderFactory = Mockito.mock(LoaderFactory.class);
        Loader loader = Mockito.mock(Loader.class);
        SchemaVersion v = new SchemaVersion("v29");
        when(loaderFactory.createLoaderForVersion(ModelType.MOXY, v)).thenReturn(loader);

        GraphSessionFactory factory = new GraphSessionFactory(loaderFactory);
        GraphBinding b = factory.bind(ModelType.MOXY, QueryStyle.TRAVERSAL_URI, v, "http://base/");

        assertEquals(ModelType.MOXY, b.modelType());
        assertEquals(QueryStyle.TRAVERSAL_URI, b.queryStyle());
        assertEquals(v, b.version());
        assertSame(loader, b.loader());
        assertNotNull(b.dbEngine());
        assertEquals("http://base/", b.serverBase());
    }

    @Test
    public void threeArgBindHasNullServerBase() {
        LoaderFactory loaderFactory = Mockito.mock(LoaderFactory.class);
        SchemaVersion v = new SchemaVersion("v29");
        when(loaderFactory.createLoaderForVersion(ModelType.MOXY, v))
                .thenReturn(Mockito.mock(Loader.class));

        GraphSessionFactory factory = new GraphSessionFactory(loaderFactory);
        GraphBinding b = factory.bind(ModelType.MOXY, QueryStyle.TRAVERSAL_URI, v);

        assertNull(b.serverBase());
    }
}
