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

package org.onap.aai.introspection.sideeffect;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import java.util.Set;

public class SideEffectRunner {

    protected final TransactionalGraphEngine dbEngine;
    protected final DBSerializer serializer;
    protected final Set<Class<? extends SideEffect>> sideEffects;

    protected SideEffectRunner(Builder builder) {
        this.dbEngine = builder.getDbEngine();
        this.serializer = builder.getSerializer();
        this.sideEffects = builder.getSideEffects();
    }

    public void execute(Introspector obj, Vertex self) throws AAIException {

        for (Class<? extends SideEffect> se : sideEffects) {
            try {
                se.getConstructor(Introspector.class, Vertex.class, TransactionalGraphEngine.class, DBSerializer.class)
                        .newInstance(obj, self, dbEngine, serializer).execute();
            } catch (UnsupportedEncodingException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException
                    | URISyntaxException e) {
                throw new AAIException("strange exception", e);
            }
        }
    }

    public static class Builder {

        private final TransactionalGraphEngine dbEngine;
        private final DBSerializer serializer;
        private final Set<Class<? extends SideEffect>> sideEffects;

        public Builder(final TransactionalGraphEngine dbEngine, final DBSerializer serializer) {
            this.dbEngine = dbEngine;
            this.serializer = serializer;
            this.sideEffects = new LinkedHashSet<>();
        }

        public Builder addSideEffect(Class<? extends SideEffect> se) {
            sideEffects.add(se);
            return this;
        }

        public Builder addSideEffects(Class<? extends SideEffect>... sideEffects) {
            for (Class<? extends SideEffect> se : sideEffects) {
                this.addSideEffect(se);
            }
            return this;
        }

        public SideEffectRunner build() {
            return new SideEffectRunner(this);
        }

        protected TransactionalGraphEngine getDbEngine() {
            return dbEngine;
        }

        protected DBSerializer getSerializer() {
            return serializer;
        }

        protected Set<Class<? extends SideEffect>> getSideEffects() {
            return sideEffects;
        }
    }
}
