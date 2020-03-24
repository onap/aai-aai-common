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

import com.google.common.base.CaseFormat;
import org.apache.commons.lang.ClassUtils;
import org.eclipse.persistence.exceptions.DynamicException;
import org.onap.aai.config.SpringContextAware;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.logging.ErrorLogHelper;
import org.onap.aai.logging.LogFormatTools;
import org.onap.aai.nodes.CaseFormatStore;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.restcore.MediaType;
import org.onap.aai.schema.enums.ObjectMetadata;
import org.onap.aai.schema.enums.PropertyMetadata;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.workarounds.NamingExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Introspector implements Cloneable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Introspector.class);

    protected String className;
    protected String uriChain = "";
    protected Loader loader;
    protected final NamingExceptions namingException = NamingExceptions.getInstance();
    private Set<String> uniqueProperties = null;
    private Set<String> indexedProperties = null;
    private Set<String> allKeys = null;
    private Set<String> dslStartNodeProperties = null;

    protected CaseFormatStore caseFormatStore = null;
    protected NodeIngestor nodeIngestor;

    protected Introspector(Object obj) {
        this.nodeIngestor = SpringContextAware.getBean(NodeIngestor.class);
        this.caseFormatStore = nodeIngestor.getCaseFormatStore();
    }

    public abstract boolean hasProperty(String name);

    protected String convertPropertyName(String name) {
        return caseFormatStore.fromLowerHyphenToLowerCamel(name).orElseGet(() -> {
            LOGGER.debug("Unable to find {} in the store from lower hyphen to lower camel", name);
            return CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, name);
        });
    }

    protected abstract Object get(String name);

    protected abstract void set(String name, Object value);

    /**
     *
     * @param name the property name you'd like to retrieve the value for
     * @return the value of the property
     */
    public <T> T getValue(String name) {
        String convertedName = convertPropertyName(name);
        Object result = null;

        if (this.hasProperty(name)) {
            result = this.get(convertedName);
        } else {
            /* property not found - slightly ambiguous */
            return null;
        }

        Class<?> clazz = this.getClass(name);
        if (this.isListType(name) && result == null) {
            try {
                this.set(convertedName, clazz.newInstance());
                result = this.get(convertedName);
            } catch (DynamicException | InstantiationException | IllegalAccessException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        return (T) result;
    }

    public Introspector getWrappedValue(String name) {
        String convertedName = convertPropertyName(name);
        Object value = null;

        if (this.hasProperty(name)) {
            value = this.get(convertedName);
        } else {
            /* property not found - slightly ambiguous */
            return null;
        }

        Class<?> clazz = this.getClass(name);
        if (this.isListType(name) && value == null) {
            try {
                this.set(convertedName, clazz.newInstance());
                value = this.get(convertedName);
            } catch (DynamicException | InstantiationException | IllegalAccessException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        if (value != null) {
            return IntrospectorFactory.newInstance(this.getModelType(), value);
        } else {
            // no value
            return null;
        }

    }

    public List<Introspector> getWrappedListValue(String name) {
        String convertedName = convertPropertyName(name);
        Object value = null;
        List<Introspector> resultList = new ArrayList<>();
        if (this.hasProperty(name)) {
            value = this.get(convertedName);
        } else {
            /* property not found - slightly ambiguous */
            return null;
        }
        boolean isListType = this.isListType(name);
        if (!this.isListType(name)) {
            return null;
        }
        Class<?> clazz = this.getClass(name);
        if (isListType && value == null) {
            try {
                this.set(convertedName, clazz.newInstance());
                value = this.get(convertedName);
            } catch (DynamicException | InstantiationException | IllegalAccessException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        List<Object> valueList = (List<Object>) value;

        for (Object item : valueList) {
            resultList.add(IntrospectorFactory.newInstance(this.getModelType(), item));
        }

        return resultList;

    }

    public Object castValueAccordingToSchema(String name, Object obj) {
        Object result = obj;
        Class<?> nameClass = this.getClass(name);
        if (nameClass == null) {
            throw new IllegalArgumentException("property: " + name + " does not exist on " + this.getDbName());
        }
        if (obj != null) {

            try {
                if (!obj.getClass().getName().equals(nameClass.getName())) {
                    if (nameClass.isPrimitive()) {
                        nameClass = ClassUtils.primitiveToWrapper(nameClass);
                        result = nameClass.getConstructor(String.class).newInstance(obj.toString());
                    }
                    if (obj instanceof String) {
                        result = nameClass.getConstructor(String.class).newInstance(obj);
                    } else if (!this.isListType(name) && !this.isComplexType(name)) {
                        // box = obj.toString();
                        result = nameClass.getConstructor(String.class).newInstance(obj.toString());
                    }
                }
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                ErrorLogHelper.logError("AAI_4017", e.getMessage());
            }
        }
        return result;
    }

    public List<Object> castValueAccordingToSchema(String name, List<?> objs) {
        List<Object> result = new ArrayList<>();

        for (Object item : objs) {
            result.add(this.castValueAccordingToSchema(name, item));
        }

        return result;

    }

    /**
     *
     * @param name the property name you'd like to set the value of
     * @param obj the value to be set
     * @return
     */
    public void setValue(String name, Object obj) {
        Object box = this.castValueAccordingToSchema(name, obj);

        name = convertPropertyName(name);
        this.set(name, box);
    }

    /**
     *
     * @return a list of all the properties available on the object
     */
    public abstract Set<String> getProperties();

    public Set<String> getProperties(PropertyPredicate<Introspector, String> p) {
        final Set<String> temp = new LinkedHashSet<>();
        this.getProperties().stream().filter(item -> p.test(this, item)).forEach(temp::add);
        return Collections.unmodifiableSet(temp);
    }

    public Set<String> getSimpleProperties(PropertyPredicate<Introspector, String> p) {
        return this.getProperties().stream().filter(item -> p.test(this, item)).filter(this::isSimpleType)
                .collect(Collectors.toSet());
    }

    /**
     *
     * @return a list of the required properties on the object
     */
    public abstract Set<String> getRequiredProperties();

    /**
     *
     * @return a list of the properties that can be used to query the object in the db
     */
    public abstract Set<String> getKeys();

    /**
     *
     * @return a list of the all key properties for this object
     */
    public Set<String> getAllKeys() {
        Set<String> result = null;
        if (this.allKeys == null) {
            Set<String> keys = this.getKeys();
            result = new LinkedHashSet<>();
            result.addAll(keys);
            String altKeys = this.getMetadata(ObjectMetadata.ALTERNATE_KEYS_1);
            if (altKeys != null) {
                String[] altKeysArray = altKeys.split(",");
                for (String altKey : altKeysArray) {
                    result.add(altKey);
                }
            }
            result = Collections.unmodifiableSet(result);
            this.allKeys = result;
        }
        result = this.allKeys;
        return result;
    }

    public Set<String> getIndexedProperties() {
        Set<String> result = null;

        if (this.indexedProperties == null) {
            result = new LinkedHashSet<>();
            Set<String> keys = this.getKeys();
            result.addAll(keys);
            String altKeys = this.getMetadata(ObjectMetadata.INDEXED_PROPS);
            if (altKeys != null) {
                String[] altKeysArray = altKeys.split(",");
                for (String altKey : altKeysArray) {
                    result.add(altKey);
                }
            }
            this.indexedProperties = Collections.unmodifiableSet(result);
        }
        result = this.indexedProperties;
        return result;
    }

    public Set<String> getDslStartNodeProperties() {
        Set<String> result = null;

        if (this.dslStartNodeProperties == null) {
            /*
             * The dslStartNodeProperties will have keys by default
             * If dslStartNodeProps exist in the oxm use it
             * if not use the indexedProps
             */
            result = new LinkedHashSet<>(this.getKeys());

            String dslKeys = this.getMetadata(ObjectMetadata.DSL_START_NODE_PROPS);
            String indexedKeys = this.getMetadata(ObjectMetadata.INDEXED_PROPS);
            if (dslKeys != null) {
                Arrays.stream(dslKeys.split(",")).forEach(result::add);
            }
            else if(indexedKeys != null){
                Arrays.stream(indexedKeys.split(",")).forEach(result::add);
            }
            this.dslStartNodeProperties = Collections.unmodifiableSet(result);
        }
        result = this.dslStartNodeProperties;
        return result;
    }

    public Set<String> getUniqueProperties() {
        Set<String> result = null;
        if (this.uniqueProperties == null) {
            String altKeys = this.getMetadata(ObjectMetadata.UNIQUE_PROPS);
            result = new LinkedHashSet<>();
            if (altKeys != null) {
                String[] altKeysArray = altKeys.split(",");
                for (String altKey : altKeysArray) {
                    result.add(altKey);
                }
            }
            this.uniqueProperties = Collections.unmodifiableSet(result);

        }
        result = this.uniqueProperties;
        return result;
    }

    public Set<String> getDependentOn() {
        String dependentOn = this.getMetadata(ObjectMetadata.DEPENDENT_ON);
        if (dependentOn == null) {
            return new LinkedHashSet<>();
        }
        return new LinkedHashSet<>(Arrays.asList(dependentOn.split(",")));
    }

    /**
     *
     * @param name
     * @return the string name of the java class of the named property
     */
    public String getType(String name) {
        Class<?> resultClass = this.getClass(name);
        String result = "";

        if (resultClass != null) {
            result = resultClass.getName();
            if (result.equals("java.util.ArrayList")) {
                result = "java.util.List";
            }
        }

        return result;
    }

    /**
     * This will returned the generic parameterized type of the underlying
     * object if it exists
     *
     * @param name
     * @return the generic type of the java class of the underlying object
     */
    public String getGenericType(String name) {
        Class<?> resultClass = this.getGenericTypeClass(name);
        String result = "";

        if (resultClass != null) {
            result = resultClass.getName();
        }

        return result;
    }

    /**
     *
     * @return the string name of the java class of the underlying object
     */
    public abstract String getJavaClassName();

    /**
     *
     * @param name the property name
     * @return the Class object
     */
    public abstract Class<?> getClass(String name);

    public abstract Class<?> getGenericTypeClass(String name);

    /**
     *
     * @param name the property name
     * @return a new instance of the underlying type of this property
     * @throws AAIUnknownObjectException
     */
    public Object newInstanceOfProperty(String name) throws AAIUnknownObjectException {
        String type = this.getType(name);
        return loader.objectFromName(type);
    }

    public Object newInstanceOfNestedProperty(String name) throws AAIUnknownObjectException {
        String type = this.getGenericType(name);
        return loader.objectFromName(type);
    }

    public Introspector newIntrospectorInstanceOfProperty(String name) throws AAIUnknownObjectException {

        Introspector result = IntrospectorFactory.newInstance(this.getModelType(), this.newInstanceOfProperty(name));

        return result;

    }

    public Introspector newIntrospectorInstanceOfNestedProperty(String name) throws AAIUnknownObjectException {

        Introspector result =
                IntrospectorFactory.newInstance(this.getModelType(), this.newInstanceOfNestedProperty(name));

        return result;

    }

    /**
     * Is this type not a Java String or primitive
     *
     * @param name
     * @return
     */
    public boolean isComplexType(String name) {
        String result = this.getType(name);

        if (result.contains("aai") || result.equals("java.lang.Object")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isComplexGenericType(String name) {
        String result = this.getGenericType(name);

        if (result.contains("aai")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isSimpleType(String name) {
        return !(this.isComplexType(name) || this.isListType(name));
    }

    public boolean isSimpleGenericType(String name) {
        return !this.isComplexGenericType(name);
    }

    public boolean isListType(String name) {
        String result = this.getType(name);

        if (result.contains("java.util.List")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isContainer() {
        Set<String> props = this.getProperties();
        boolean result = false;
        if (props.size() == 1 && this.isListType(props.iterator().next())) {
            result = true;
        }

        return result;
    }

    public abstract String getChildName();

    public String getChildDBName() {
        String result = this.getChildName();

        result = namingException.getDBName(result);
        return result;
    }

    public abstract String getName();

    public String getDbName() {
        String lowerHyphen = this.getName();

        lowerHyphen = namingException.getDBName(lowerHyphen);

        return lowerHyphen;
    }

    public abstract ModelType getModelType();

    public boolean hasChild(Introspector child) {
        boolean result = false;
        // check all inheriting types for this child
        if ("true".equals(this.getMetadata(ObjectMetadata.ABSTRACT))) {
            String[] inheritors = this.getMetadata(ObjectMetadata.INHERITORS).split(",");
            for (String inheritor : inheritors) {
                try {
                    Introspector temp = this.loader.introspectorFromName(inheritor);
                    result = temp.hasProperty(child.getName());
                    if (result) {
                        break;
                    }
                } catch (AAIUnknownObjectException e) {
                    LOGGER.warn(
                            "Skipping inheritor " + inheritor + " (Unknown Object) " + LogFormatTools.getStackTop(e));
                }
            }
        } else {
            result = this.hasProperty(child.getName());
        }
        return result;
    }

    public void setURIChain(String uri) {
        this.uriChain = uri;
    }

    public abstract String getObjectId() throws UnsupportedEncodingException;

    public String getURI() throws UnsupportedEncodingException {
        // String result = this.uriChain;
        String result = "";
        String namespace = this.getMetadata(ObjectMetadata.NAMESPACE);
        String container = this.getMetadata(ObjectMetadata.CONTAINER);
        if (this.isContainer()) {
            result += "/" + this.getName();
        } else {

            if (container != null) {
                result += "/" + container;
            }
            result += "/" + this.getDbName() + "/" + this.findKey();

            if (namespace != null && !namespace.equals("")) {
                result = "/" + namespace + result;
            }
        }

        return result;
    }

    public String getGenericURI() {
        String result = "";
        if (this.isContainer()) {
            result += "/" + this.getName();
        } else {
            result += "/" + this.getDbName();
            for (String key : this.getKeys()) {
                result += "/{" + this.getDbName() + "-" + key + "}";
            }
        }

        return result;
    }

    public String getFullGenericURI() {
        String result = "";
        String namespace = this.getMetadata(ObjectMetadata.NAMESPACE);
        String container = this.getMetadata(ObjectMetadata.CONTAINER);
        if (this.isContainer()) {
            result += "/" + this.getName();
        } else {

            if (container != null) {
                result += "/" + container;
            }
            result += "/" + this.getDbName();

            for (String key : this.getKeys()) {
                result += "/{" + this.getDbName() + "-" + key + "}";
            }
            if (namespace != null && !namespace.equals("")) {
                result = "/" + namespace + result;
            }

        }

        return result;
    }

    public abstract String preProcessKey(String key);

    protected abstract String findKey() throws UnsupportedEncodingException;

    public abstract String marshal(MarshallerProperties properties);

    public abstract Object getUnderlyingObject();

    public String marshal(boolean formatted) {
        MarshallerProperties properties =
                new MarshallerProperties.Builder(MediaType.APPLICATION_JSON_TYPE).formatted(formatted).build();

        return marshal(properties);
    }

    public String makeSingular(String word) {

        String result = word;
        result = result.replaceAll("(?:([ho])es|s)$", "");

        if (result.equals("ClassesOfService")) {
            result = "ClassOfService";
        } else if (result.equals("CvlanTag")) {
            result = "CvlanTagEntry";
        } else if (result.equals("Metadata")) {
            result = "Metadatum";
        }
        return result;
    }

    protected String makePlural(String word) {
        String result = word;

        if (result.equals("cvlan-tag-entry")) {
            return "cvlan-tags";
        } else if (result.equals("class-of-service")) {
            return "classes-of-service";
        } else if (result.equals("metadatum")) {
            return "metadata";
        }
        result = result.replaceAll("([a-z])$", "$1s");
        result = result.replaceAll("([hox])s$", "$1es");
        /*
         * if (result.equals("classes-of-services")) {
         * result = "classes-of-service";
         * }
         */

        return result;
    }

    public abstract String getMetadata(ObjectMetadata metadataName);

    public abstract Map<PropertyMetadata, String> getPropertyMetadata(String propName);

    public Optional<String> getPropertyMetadata(String propName, PropertyMetadata metadataName) {
        final String resultValue = this.getPropertyMetadata(propName).getOrDefault(metadataName, "");
        Optional<String> result = Optional.empty();

        if (!resultValue.isEmpty()) {
            result = Optional.of(resultValue);
        }
        return result;

    }

    public abstract SchemaVersion getVersion();

    public Loader getLoader() {
        return this.loader;
    }

    public boolean isTopLevel() {

        return this.getMetadata(ObjectMetadata.NAMESPACE) != null;
    }

}
