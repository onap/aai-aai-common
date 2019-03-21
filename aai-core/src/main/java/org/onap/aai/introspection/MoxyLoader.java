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
import com.google.common.collect.ImmutableMap;

import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.introspection.exceptions.AAIUnmarshallingException;
import org.onap.aai.logging.ErrorLogHelper;
import org.onap.aai.logging.LogFormatTools;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.restcore.MediaType;
import org.onap.aai.schema.enums.ObjectMetadata;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.workarounds.NamingExceptions;
import org.springframework.stereotype.Component;

public class MoxyLoader extends Loader {

    private DynamicJAXBContext jaxbContext = null;
    private EELFLogger LOGGER = EELFManager.getInstance().getLogger(MoxyLoader.class);
    private Map<String, Introspector> allObjs = null;

    private Map<SchemaVersion, MoxyLoader> moxyLoaderFactory;

    private NodeIngestor nodeIngestor;

    private Set<String> namedProps;

    public MoxyLoader(SchemaVersion version, NodeIngestor nodeIngestor) {
        super(version, ModelType.MOXY);
        this.nodeIngestor = nodeIngestor;
        process(version);
    }

    public MoxyLoader getMoxyLoader(SchemaVersion v) {
        return moxyLoaderFactory.get(v);

    }

    /**
     * {@inheritDoc}
     * 
     * @throws AAIUnknownObjectException
     */
    @Override
    public Introspector introspectorFromName(String name) throws AAIUnknownObjectException {

        return IntrospectorFactory.newInstance(ModelType.MOXY, objectFromName(name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object objectFromName(String name) throws AAIUnknownObjectException {

        if (name == null) {
            throw new AAIUnknownObjectException("null name passed in");
        }
        final String sanitizedName = NamingExceptions.getInstance().getObjectName(name);
        final String upperCamel;

        // Contains any uppercase, then assume it's upper camel
        if (name.matches(".*[A-Z].*")) {
            upperCamel = sanitizedName;
        } else {
            upperCamel = CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, sanitizedName);
        }

        try {
            final DynamicEntity result = jaxbContext.newDynamicEntity(upperCamel);

            if (result == null)
                throw new AAIUnknownObjectException("Unrecognized AAI object " + name);

            return result;
        } catch (IllegalArgumentException e) {
            // entity does not exist
            throw new AAIUnknownObjectException("Unrecognized AAI object " + name, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void process(SchemaVersion version) {
        /*
         * We need to have just same JaxbContext for each version
         */
        jaxbContext = nodeIngestor.getContextForVersion(version);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Introspector unmarshal(String type, String json, MediaType mediaType)
        throws AAIUnmarshallingException {
        try {
            final Object clazz = objectFromName(type);
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            if (mediaType.equals(MediaType.APPLICATION_JSON_TYPE)) {
                unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
                unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
                unmarshaller.setProperty(UnmarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
            }

            final DynamicEntity entity = (DynamicEntity) unmarshaller
                .unmarshal(new StreamSource(new StringReader(json)), clazz.getClass()).getValue();
            return IntrospectorFactory.newInstance(ModelType.MOXY, entity);
        } catch (JAXBException e) {
            AAIException ex = new AAIException("AAI_4007", e);
            ErrorLogHelper.logException(ex);
            throw new AAIUnmarshallingException("Could not unmarshall: " + e.getMessage(), ex);
        } catch (AAIUnknownObjectException e) {
            throw new AAIUnmarshallingException("Could not unmarshall: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Introspector> getAllObjects() {
        if (this.allObjs != null) {
            return allObjs;
        } else {
            ImmutableMap.Builder<String, Introspector> map =
                new ImmutableMap.Builder<String, Introspector>();
            Set<String> objs = objectsInVersion();
            for (String objName : objs) {
                try {
                    Introspector introspector = this.introspectorFromName(objName);
                    map.put(introspector.getDbName(), introspector);
                } catch (AAIUnknownObjectException e) {
                    LOGGER
                        .warn("Unexpected AAIUnknownObjectException while running getAllObjects() "
                            + LogFormatTools.getStackTop(e));
                }
            }
            allObjs = map.build();
            return allObjs;
        }
    }

    private Set<String> objectsInVersion() {
        Set<String> result = new HashSet<>();

        try {
            result = nodeIngestor.getObjectsInVersion(getVersion());

        } catch (Exception e) {
            LOGGER.warn("Exception while enumerating objects for API version " + getVersion()
                + " (returning partial results) " + LogFormatTools.getStackTop(e));
        }

        // result.remove("EdgePropNames");
        return result;
    }

    @Override
    public Set<String> getNamedPropNodes() {

        if (namedProps == null) {
            namedProps = getAllObjects().entrySet().stream()
                .filter((entry) -> entry.getValue().getMetadata(ObjectMetadata.NAME_PROPS) != null)
                .map(entry -> entry.getKey()).collect(Collectors.toSet());
        }

        return namedProps;
    }

    public DynamicJAXBContext getJAXBContext() {
        return this.jaxbContext;
    }

    /*
     * Im keeping this for now - Just in case
     */
    /*
     * private static class Helper {
     * private static final Map<SchemaVersion, MoxyLoader> INSTANCEMAP = new ConcurrentHashMap<>();
     * 
     * private Helper() {}
     * 
     * private static MoxyLoader getLoaderBySchemaVersion(SchemaVersion v) {
     * if (!INSTANCEMAP.containsKey(v)) {
     * INSTANCEMAP.put(v, new MoxyLoader(v, nodeIngestor));
     * }
     * return INSTANCEMAP.get(v);
     * }
     * }
     */
}
