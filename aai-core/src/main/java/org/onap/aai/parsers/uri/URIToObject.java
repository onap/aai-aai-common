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

package org.onap.aai.parsers.uri;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.onap.aai.edges.enums.EdgeType;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.schema.enums.ObjectMetadata;
import org.onap.aai.setup.SchemaVersion;

/**
 * Given a URI this class returns an object, or series of nested objects
 * with their keys populated based off the values in the URI.
 * 
 * It populates the keys in the order they are listed in the model.
 */
public class URIToObject implements Parsable {

    private Introspector topEntity = null;

    private String topEntityName = null;

    private String entityName = null;

    private Introspector entity = null;

    private Introspector previous = null;

    private List<Object> parentList = null;

    private SchemaVersion version = null;
    private Loader loader = null;
    private final HashMap<String, Introspector> relatedObjects;

    /**
     * Instantiates a new URI to object.
     *
     * @param loader the loader
     * @param uri the uri
     * @throws IllegalArgumentException the illegal argument exception
     * @throws AAIException the AAI exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    public URIToObject(Loader loader, URI uri) throws AAIException, UnsupportedEncodingException {

        URIParser parser = new URIParser(loader, uri);
        this.relatedObjects = new HashMap<>();

        parser.parse(this);
        this.loader = parser.getLoader();
        this.version = loader.getVersion();
    }

    public URIToObject(Loader loader, URI uri, HashMap<String, Introspector> relatedObjects)
        throws AAIException, UnsupportedEncodingException {

        URIParser parser = new URIParser(loader, uri);
        this.relatedObjects = relatedObjects;

        parser.parse(this);
        this.loader = parser.getLoader();
        this.version = loader.getVersion();

    }

    /**
     * @{inheritDoc}
     */
    @Override
    public void processNamespace(Introspector obj) {

    }

    /**
     * @{inheritDoc}
     */
    @Override
    public String getCloudRegionTransform() {
        return "add";
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public boolean useOriginalLoader() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Gets the top entity.
     *
     * @return the top entity
     */
    public Introspector getTopEntity() {
        return this.topEntity;
    }

    /**
     * Gets the entity.
     *
     * @return the entity
     */
    public Introspector getEntity() {
        return this.entity;
    }

    /**
     * Gets the parent list.
     *
     * @return the parent list
     */
    public List<Object> getParentList() {
        return this.parentList;
    }

    /**
     * Gets the entity name.
     *
     * @return the entity name
     */
    public String getEntityName() {
        return this.entityName;
    }

    /**
     * Gets the top entity name.
     *
     * @return the top entity name
     */
    public String getTopEntityName() {
        return this.topEntityName;
    }

    /**
     * Gets the object version.
     *
     * @return the object version
     */
    public SchemaVersion getObjectVersion() {
        return this.loader.getVersion();
    }

    public Loader getLoader() {
        return this.loader;
    }

    @Override
    public void processObject(Introspector obj, EdgeType type,
        MultivaluedMap<String, String> uriKeys) throws AAIException {

        if (this.entityName == null) {
            this.topEntityName = obj.getDbName();
            this.topEntity = obj;
        }
        this.entityName = obj.getDbName();
        this.entity = obj;
        this.parentList = (List<Object>) this.previous.getValue(obj.getName());
        this.parentList.add(entity.getUnderlyingObject());

        for (String key : uriKeys.keySet()) {
            entity.setValue(key, uriKeys.getFirst(key));
        }
        try {
            if (relatedObjects.containsKey(entity.getObjectId())) {
                Introspector relatedObject = relatedObjects.get(entity.getObjectId());
                String nameProp = relatedObject.getMetadata(ObjectMetadata.NAME_PROPS);
                if (nameProp == null) {
                    nameProp = "";
                }
                if (nameProp != null && !nameProp.equals("")) {
                    String[] nameProps = nameProp.split(",");
                    for (String prop : nameProps) {
                        entity.setValue(prop, relatedObject.getValue(prop));
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
        }
        this.previous = entity;
    }

    @Override
    public void processContainer(Introspector obj, EdgeType type,
        MultivaluedMap<String, String> uriKeys, boolean isFinalContainer) throws AAIException {
        this.previous = obj;

        if (this.entity != null) {
            this.entity.setValue(obj.getName(), obj.getUnderlyingObject());
        } else {
            this.entity = obj;
            this.topEntity = obj;
        }
    }
}
