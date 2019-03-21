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

package org.onap.aai.serialization.engines;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.onap.aai.dbmap.DBConnectionType;
import org.onap.aai.introspection.Loader;
import org.onap.aai.serialization.db.JanusGraphSingleton;

public class JanusGraphDBEngine extends TransactionalGraphEngine {

    /**
     * Instantiates a new JanusGraph DB engine.
     *
     * @param style the style
     * @param loader the loader
     */
    public JanusGraphDBEngine(QueryStyle style, DBConnectionType connectionType, Loader loader) {
        super(style, loader, connectionType, JanusGraphSingleton.getInstance());
    }

    /**
     * Instantiates a new JanusGraph DB engine.
     *
     * @param style the style
     * @param loader the loader
     * @param connect the connect
     */
    public JanusGraphDBEngine(QueryStyle style, Loader loader, boolean connect) {
        super(style, loader);
        if (connect) {
            this.singleton = JanusGraphSingleton.getInstance();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setListProperty(Vertex v, String name, List<?> objs) {

        // clear out list full replace style

        Iterator<VertexProperty<Object>> iterator = v.properties(name);
        while (iterator.hasNext()) {
            iterator.next().remove();
        }
        if (objs != null) {
            for (Object obj : objs) {
                v.property(name, obj);
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> getListProperty(Vertex v, String name) {

        List<Object> result = new ArrayList<Object>();

        Iterator<VertexProperty<Object>> iterator = v.properties(name);

        while (iterator.hasNext()) {
            result.add(iterator.next().value());
        }

        if (result.size() == 0) {
            result = null;
        }

        return result;

    }

}
