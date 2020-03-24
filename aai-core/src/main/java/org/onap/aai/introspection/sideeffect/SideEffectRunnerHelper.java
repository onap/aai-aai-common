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

import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Wanderer;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

class SideEffectRunnerHelper implements Wanderer {

    protected final TransactionalGraphEngine dbEngine;
    protected final DBSerializer serializer;
    protected final Set<Class<? extends SideEffect>> sideEffects;

    protected SideEffectRunnerHelper(final TransactionalGraphEngine dbEngine, final DBSerializer serializer,
            final Set<Class<? extends SideEffect>> sideEffects) {
        this.dbEngine = dbEngine;
        this.serializer = serializer;
        this.sideEffects = sideEffects;
    }

    private void runSideEffects(Introspector obj) throws AAIException {
        for (Class<? extends SideEffect> se : sideEffects) {
            try {
                se.getConstructor(Introspector.class, TransactionalGraphEngine.class, DBSerializer.class)
                        .newInstance(obj, dbEngine, serializer).execute();
            } catch (UnsupportedEncodingException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException
                    | URISyntaxException e) {
                throw new AAIException("strange exception", e);
            }
        }
    }

    @Override
    public void processPrimitive(String propName, Introspector obj) {
        // TODO Auto-generated method stub

    }

    @Override
    public void processPrimitiveList(String propName, Introspector obj) {
        // TODO Auto-generated method stub

    }

    @Override
    public void processComplexObj(Introspector obj) throws AAIException {

        runSideEffects(obj);

    }

    @Override
    public void modifyComplexList(List<Introspector> list, List<Object> listReference, Introspector parent,
            Introspector child) {
        // TODO Auto-generated method stub

    }

}
