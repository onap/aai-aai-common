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

package org.onap.aai.introspection;

import org.onap.aai.exceptions.AAIException;

import java.util.List;

public interface Wanderer {

    /**
     * Process primitive.
     *
     * @param propName the prop name
     * @param obj the obj
     */
    public void processPrimitive(String propName, Introspector obj);

    /**
     * Process primitive list.
     *
     * @param propName the prop name
     * @param obj the obj
     */
    public void processPrimitiveList(String propName, Introspector obj);

    /**
     * Process complex obj.
     *
     * @param obj the obj
     * @throws AAIException
     */
    public void processComplexObj(Introspector obj) throws AAIException;

    /**
     * Modify complex list.
     *
     * @param list the list
     * @param listReference TODO
     * @param parent the parent
     * @param child the child
     */
    public void modifyComplexList(List<Introspector> list, List<Object> listReference, Introspector parent,
            Introspector child);

    /**
     * Creates the complex obj if null.
     *
     * @return true, if successful
     */
    public default boolean createComplexObjIfNull() {
        return false;
    }

    /**
     * Creates the complex list size.
     *
     * @param parent the parent
     * @param child the child
     * @return the int
     */
    public default int createComplexListSize(Introspector parent, Introspector child) {
        return 0;
    }

}
