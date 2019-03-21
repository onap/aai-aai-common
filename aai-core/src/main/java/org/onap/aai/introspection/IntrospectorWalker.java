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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.introspection;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.logging.LogFormatTools;

public class IntrospectorWalker {

    private static final EELFLogger LOGGER =
        EELFManager.getInstance().getLogger(IntrospectorWalker.class);

    private Wanderer w = null;
    private Set<String> blacklist = null;
    private boolean preventCycles = false;
    private final PropertyPredicate<Introspector, String> propVisibility;

    /**
     * Instantiates a new introspector walker.
     *
     * @param w the w
     * @param llBuilder the ll builder
     */
    public IntrospectorWalker(Wanderer w) {
        this.w = w;
        this.blacklist = new HashSet<>();
        this.propVisibility = null;
    }

    public IntrospectorWalker(Wanderer w, PropertyPredicate<Introspector, String> p) {
        this.w = w;
        this.blacklist = new HashSet<>();
        this.propVisibility = p;
    }

    /**
     * Sets the blacklist.
     *
     * @param list the new blacklist
     */
    public void setBlacklist(List<String> list) {
        blacklist.addAll(list);
    }

    /**
     * Prevent cycles.
     *
     * @param prevent the prevent
     */
    public void preventCycles(boolean prevent) {
        this.preventCycles = prevent;
    }

    /**
     * Walk.
     *
     * @param obj the obj
     * @throws AAIException
     */
    public void walk(Introspector obj) throws AAIException {
        Set<String> visited = new HashSet<>();

        walk(obj, null, visited);
    }

    /**
     * Walk.
     *
     * @param obj the obj
     * @param parent the parent
     * @throws AAIException
     */
    private void walk(Introspector obj, Introspector parent, Set<String> visited)
        throws AAIException {
        boolean stopRecursion = false;
        Set<String> localVisited = new HashSet<>();
        localVisited.addAll(visited);
        if (preventCycles) {
            if (visited.contains(obj.getName())) {
                stopRecursion = true;
            }
            if (!obj.isContainer()) {
                localVisited.add(obj.getName()); // so we don't recurse while walking its children
            }
        }
        Set<String> props;
        // props must duplicate the result from getProperties because
        // it is unmodifiable
        if (this.propVisibility == null) {
            props = new LinkedHashSet<>(obj.getProperties());
        } else {
            props = new LinkedHashSet<>(obj.getProperties(this.propVisibility));
        }

        w.processComplexObj(obj);
        props.removeAll(blacklist);
        if (!obj.isContainer()) {
            parent = obj;
        }
        for (String prop : props) {

            if (obj.isSimpleType(prop)) {

                w.processPrimitive(prop, obj);
            } else if (obj.isListType(prop) && !stopRecursion) {

                List<Object> listReference = obj.getValue(prop);
                boolean isComplexType = obj.isComplexGenericType(prop);
                if (isComplexType) {
                    List<Introspector> list = obj.getWrappedListValue(prop);
                    try {
                        Introspector child = obj.newIntrospectorInstanceOfNestedProperty(prop);
                        w.modifyComplexList(list, listReference, parent, child);
                        for (Object item : listReference) {
                            child = IntrospectorFactory.newInstance(obj.getModelType(), item);
                            walk(child, parent, localVisited);
                        }
                    } catch (AAIUnknownObjectException e) {
                        LOGGER.warn("Skipping property " + prop + " (Unknown Object) "
                            + LogFormatTools.getStackTop(e));
                    }
                } else {
                    w.processPrimitiveList(prop, obj);
                }
                if (listReference.size() == 0) {
                    if (isComplexType) {
                        try {
                            Introspector child = obj.newIntrospectorInstanceOfNestedProperty(prop);
                            int size = w.createComplexListSize(parent, child);
                            for (int i = 0; i < size; i++) {
                                child = obj.newIntrospectorInstanceOfNestedProperty(prop);
                                walk(child, parent, localVisited);
                                listReference.add(child.getUnderlyingObject());
                            }

                            obj.setValue(prop, listReference);
                        } catch (AAIUnknownObjectException e) {
                            LOGGER.warn("Skipping property " + prop + " (Unknown Object) "
                                + LogFormatTools.getStackTop(e));
                        }
                    } else if (!isComplexType) {
                        w.processPrimitiveList(prop, obj);
                    }
                }

            } else if (obj.isComplexType(prop) && !stopRecursion) {
                Introspector child = null;
                if (obj.getValue(prop) != null) {
                    child = IntrospectorFactory.newInstance(obj.getModelType(), obj.getValue(prop));
                } else {
                    if (w.createComplexObjIfNull()) {
                        try {
                            child = obj.newIntrospectorInstanceOfProperty(prop);
                            obj.setValue(prop, child.getUnderlyingObject());
                        } catch (AAIUnknownObjectException e) {
                            LOGGER.warn("Skipping property " + prop + " (Unknown Object) "
                                + LogFormatTools.getStackTop(e));
                        }
                    }
                }
                if (child != null) {
                    walk(child, obj, localVisited);
                }
            }

        }
        /*
         * if (preventCycles && !obj.isContainer()) {
         * visited.remove(obj.getName()); //so we can see it down another path that isn't in danger
         * of recursing over it
         * }
         */
    }
}
