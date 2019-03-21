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
import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.exceptions.DynamicException;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.onap.aai.config.SpringContextAware;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.restcore.MediaType;
import org.onap.aai.schema.enums.ObjectMetadata;
import org.onap.aai.schema.enums.PropertyMetadata;
import org.onap.aai.setup.SchemaVersion;
import org.springframework.web.util.UriUtils;

public class MoxyStrategy extends Introspector {

    private static final EELFLogger LOGGER =
        EELFManager.getInstance().getLogger(MoxyStrategy.class);
    private DynamicEntity internalObject = null;
    private DynamicType internalType = null;
    private DynamicJAXBContext jaxbContext = null;
    private ClassDescriptor cd = null;
    private Marshaller marshaller = null;
    private Unmarshaller unmarshaller = null;
    private SchemaVersion version = null;
    private Set<String> properties = null;
    private Set<String> keys = null;
    private Set<String> requiredProperties = null;

    private boolean isInitialized = false;

    private NodeIngestor nodeIngestor;

    protected MoxyStrategy(Object obj) {
        super(obj);
        /* must look up the correct jaxbcontext for this object */
        className = MoxyStrategy.class.getSimpleName();
        internalObject = (DynamicEntity) obj;
        nodeIngestor = SpringContextAware.getBean(NodeIngestor.class);
        version = nodeIngestor.getVersionFromClassName(internalObject.getClass().getName());
        super.loader = SpringContextAware.getBean(LoaderFactory.class)
            .createLoaderForVersion(getModelType(), version);
        jaxbContext = nodeIngestor.getContextForVersion(version);
        String simpleName = internalObject.getClass().getName();
        internalType = jaxbContext.getDynamicType(simpleName);

        cd = internalType.getDescriptor();
        try {
            marshaller = jaxbContext.createMarshaller();

            unmarshaller = jaxbContext.createUnmarshaller();

        } catch (JAXBException e) {

        }

    }

    private void init() {
        isInitialized = true;

        Set<String> props = new LinkedHashSet<>();
        for (String s : internalType.getPropertiesNames()) {
            props.add(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, s));

        }
        props = Collections.unmodifiableSet(props);
        this.properties = props;

        Set<String> requiredProps = new LinkedHashSet<>();
        requiredProps = new LinkedHashSet<>();
        for (DatabaseMapping dm : cd.getMappings()) {
            if (dm.getField() instanceof XMLField) {
                XMLField x = (XMLField) dm.getField();
                if (x != null) {
                    if (x.isRequired()) {
                        requiredProps.add(this.removeXPathDescriptor(x.getName()));
                    }
                }
            }
        }
        requiredProps = Collections.unmodifiableSet(requiredProps);
        this.requiredProperties = requiredProps;

        Set<String> keys = new LinkedHashSet<>();

        for (String name : internalType.getDescriptor().getPrimaryKeyFieldNames()) {
            keys.add(this.removeXPathDescriptor(name));
        }
        keys = Collections.unmodifiableSet(keys);
        this.keys = keys;

    }

    @Override
    public boolean hasProperty(String name) {
        String convertedName = convertPropertyName(name);

        return internalType.containsProperty(convertedName);
    }

    @Override
    public Object get(String name) {
        return internalObject.get(name);
    }

    @Override
    public void set(String name, Object obj) throws IllegalArgumentException {

        internalObject.set(name, obj);
    }

    @Override
    public Set<String> getProperties() {

        if (!isInitialized) {
            init();
        }

        return this.properties;

    }

    @Override
    public Set<String> getRequiredProperties() {

        if (!isInitialized) {
            init();
        }

        return this.requiredProperties;
    }

    @Override
    public Set<String> getKeys() {

        if (!isInitialized) {
            init();
        }

        return this.keys;
    }

    @Override
    public Map<PropertyMetadata, String> getPropertyMetadata(String prop) {
        String propName = this.convertPropertyName(prop);
        DatabaseMapping mapping = cd.getMappingForAttributeName(propName);
        Map<PropertyMetadata, String> result = new HashMap<>();
        if (mapping != null) {
            Set<Entry> entrySet = mapping.getProperties().entrySet();
            for (Entry<?, ?> entry : entrySet) {
                result
                    .put(
                        PropertyMetadata.valueOf(CaseFormat.LOWER_CAMEL
                            .to(CaseFormat.UPPER_UNDERSCORE, (String) entry.getKey())),
                        (String) entry.getValue());
            }
        }

        return result;
    }

    @Override
    public String getJavaClassName() {
        return internalObject.getClass().getName();
    }

    @Override
    public Class<?> getClass(String name) {
        name = convertPropertyName(name);
        Class<?> resultClass = null;
        try {
            if (internalType.getPropertyType(name) == null) {
                if (cd.getMappingForAttributeName(
                    name) instanceof XMLCompositeDirectCollectionMapping) {
                    resultClass = cd.getMappingForAttributeName(name).getContainerPolicy()
                        .getContainerClass();

                } else if (cd
                    .getMappingForAttributeName(name) instanceof XMLCompositeCollectionMapping) {
                    resultClass = cd.getMappingForAttributeName(name).getContainerPolicy()
                        .getContainerClass();
                } else {
                    ClassDescriptor referenceDiscriptor =
                        cd.getMappingForAttributeName(name).getReferenceDescriptor();
                    if (referenceDiscriptor != null) {
                        resultClass = referenceDiscriptor.getJavaClass();
                    } else {
                        resultClass = Object.class;
                    }
                }
            } else {
                resultClass = internalType.getPropertyType(name);
            }
        } catch (DynamicException e) {
            // property doesn't exist
        }
        return resultClass;
    }

    @Override
    public Class<?> getGenericTypeClass(String name) {
        name = convertPropertyName(name);
        Class<?> resultClass = null;
        if (internalType.getPropertyType(name) == null) {
            if (cd
                .getMappingForAttributeName(name) instanceof XMLCompositeDirectCollectionMapping) {
                resultClass = cd.getMappingForAttributeName(name).getFields().get(0).getType();

            } else if (cd
                .getMappingForAttributeName(name) instanceof XMLCompositeCollectionMapping) {
                resultClass =
                    cd.getMappingForAttributeName(name).getReferenceDescriptor().getJavaClass();
            }
        }

        return resultClass;
    }

    @Override
    public Object getUnderlyingObject() {
        return this.internalObject;
    }

    @Override
    public String getChildName() {

        String className = internalObject.getClass().getSimpleName();
        String lowerHyphen = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, className);

        if (this.isContainer()) {
            lowerHyphen = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN,
                this.getGenericTypeClass(this.getProperties().iterator().next()).getSimpleName());
        }

        return lowerHyphen;
    }

    @Override
    public String getName() {
        String className = internalObject.getClass().getSimpleName();
        String lowerHyphen = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, className);
        /*
         * if (this.isContainer()) {
         * lowerHyphen =
         * CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN,this.getGenericTypeClass(this.
         * getProperties().get(0)).getSimpleName());
         * }
         */

        return lowerHyphen;
    }

    @Override
    public String getObjectId() throws UnsupportedEncodingException {
        String result = "";
        String container = this.getMetadata(ObjectMetadata.CONTAINER);
        if (this.isContainer()) {
            result += "/" + this.getName();
        } else {

            if (container != null) {
                result += "/" + container;
            }
            result += "/" + this.getDbName() + "/" + this.findKey();

        }

        return result;
    }

    @Override
    protected String findKey() throws UnsupportedEncodingException {
        Set<String> keys = null;
        keys = this.getKeys();
        List<String> results = new ArrayList<>();
        for (String key : keys) {
            String value = UriUtils.encode(this.getValue(key).toString(), "UTF-8");
            results.add(value);
        }

        return Joiner.on("/").join(results);
    }

    @Override
    public String preProcessKey(String key) {
        String result = "";
        // String trimmedRestURI = restURI.replaceAll("/[\\w\\-]+?/[\\w\\-]+?$", "");
        String[] split = key.split("/");
        int i = 0;
        for (i = split.length - 1; i >= 0; i--) {

            if (jaxbContext.getDynamicType(split[i]) != null) {
                break;

            }

        }
        result = Joiner.on("/").join(Arrays.copyOfRange(split, 0, i));

        return result;

    }

    @Override
    public String marshal(MarshallerProperties properties) {
        StringWriter result = new StringWriter();
        try {
            if (properties.getMediaType().equals(MediaType.APPLICATION_JSON_TYPE)) {
                marshaller.setProperty(org.eclipse.persistence.jaxb.MarshallerProperties.MEDIA_TYPE,
                    "application/json");
                marshaller.setProperty(
                    org.eclipse.persistence.jaxb.MarshallerProperties.JSON_INCLUDE_ROOT,
                    properties.getIncludeRoot());
                marshaller.setProperty(
                    org.eclipse.persistence.jaxb.MarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME,
                    properties.getWrapperAsArrayName());
                marshaller.setProperty(
                    org.eclipse.persistence.jaxb.MarshallerProperties.JSON_MARSHAL_EMPTY_COLLECTIONS,
                    false);
            }

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, properties.getFormatted());
            marshaller.marshal(this.internalObject, result);
        } catch (JAXBException e) {
            // e.printStackTrace();
        }

        return result.toString();
    }

    @Override
    public Object clone() {
        Object result = null;
        try {
            unmarshaller = jaxbContext.createUnmarshaller();

            unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
            unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
            unmarshaller.setProperty(UnmarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);

            result = unmarshaller.unmarshal(new StreamSource(new StringReader(this.marshal(true))),
                this.internalObject.getClass()).getValue();
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
        }
        result = IntrospectorFactory.newInstance(getModelType(), result);
        return result;
    }

    @Override
    public ModelType getModelType() {
        return ModelType.MOXY;
    }

    private String removeXPathDescriptor(String name) {

        return name.replaceAll("/text\\(\\)", "");
    }

    @Override
    public String getMetadata(ObjectMetadata name) {

        return (String) cd.getProperty(name.toString());
    }

    @Override
    public SchemaVersion getVersion() {

        return this.version;
    }
}
