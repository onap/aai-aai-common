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

package org.onap.aai.parsers.query;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Map.Entry;

import javax.ws.rs.core.MultivaluedMap;

import org.onap.aai.edges.enums.EdgeType;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.logging.LogFormatTools;
import org.onap.aai.parsers.uri.Parsable;
import org.onap.aai.parsers.uri.URIParser;
import org.onap.aai.parsers.uri.URIToObject;
import org.onap.aai.query.builder.QueryBuilder;
import org.onap.aai.restcore.util.URITools;
import org.onap.aai.schema.enums.PropertyMetadata;

/**
 * The Class LegacyQueryParser.
 */
public class LegacyQueryParser extends QueryParser implements Parsable {

    private static final EELFLogger LOGGER =
        EELFManager.getInstance().getLogger(LegacyQueryParser.class);

    private Introspector previous = null;

    /**
     * Instantiates a new legacy query parser.
     *
     * @param loader the loader
     * @param queryBuilder the query builder
     * @param uri the uri
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    public LegacyQueryParser(Loader loader, QueryBuilder queryBuilder, URI uri)
        throws UnsupportedEncodingException, AAIException {
        super(loader, queryBuilder, uri);
        URIParser parser = new URIParser(loader, uri);
        parser.parse(this);
    }

    /**
     * Instantiates a new legacy query parser.
     *
     * @param loader the loader
     * @param queryBuilder the query builder
     * @param uri the uri
     * @param queryParams the query params
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    public LegacyQueryParser(Loader loader, QueryBuilder queryBuilder, URI uri,
        MultivaluedMap<String, String> queryParams)
        throws UnsupportedEncodingException, AAIException {
        super(loader, queryBuilder, uri);
        URIParser parser = new URIParser(loader, uri, queryParams);
        parser.parse(this);
    }

    /**
     * Instantiates a new legacy query parser.
     *
     * @param loader the loader
     * @param queryBuilder the query builder
     */
    public LegacyQueryParser(Loader loader, QueryBuilder queryBuilder) {
        super(loader, queryBuilder);
    }

    /**
     * @throws AAIException
     * @{inheritDoc}
     */
    @Override
    public void processObject(Introspector obj, EdgeType type,
        MultivaluedMap<String, String> uriKeys) throws AAIException {
        if (previous != null) {
            this.parentResourceType = previous.getDbName();
            queryBuilder.createEdgeTraversal(type, previous, obj);
        }
        if (previous == null) {
            queryBuilder.createDBQuery(obj);
            this.handleUriKeys(obj, uriKeys);
        } else {
            queryBuilder.createKeyQuery(obj);
            this.handleUriKeys(obj, uriKeys);
        }
        previous = obj;
        this.resultResource = obj.getDbName();
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public void processContainer(Introspector obj, EdgeType type,
        MultivaluedMap<String, String> uriKeys, boolean isFinalContainer) throws AAIException {
        if (isFinalContainer) {
            if (previous != null) {
                this.parentResourceType = previous.getDbName();
                queryBuilder.createEdgeTraversal(type, previous, obj);
            }

            if (previous == null) {
                queryBuilder.createContainerQuery(obj);
                queryBuilder.markParentBoundary();
            }
            if (!uriKeys.isEmpty()) {

                try {
                    Introspector child =
                        obj.newIntrospectorInstanceOfNestedProperty(obj.getChildName());
                    this.handleUriKeys(child, uriKeys);
                } catch (AAIUnknownObjectException e) {
                    LOGGER.warn("Skipping container child " + obj.getChildName()
                        + " (Unknown Object) " + LogFormatTools.getStackTop(e));
                }
            }

            this.resultResource = obj.getChildDBName();
            this.containerResource = obj.getName();
        }
    }

    private void handleUriKeys(Introspector obj, MultivaluedMap<String, String> uriKeys)
        throws AAIException {
        for (String key : uriKeys.keySet()) {
            // to validate whether this property exists
            if (!obj.hasProperty(key)) {
                throw new AAIException("AAI_3000",
                    "property: " + key + " not found on " + obj.getDbName());
            }

            List<String> values = uriKeys.get(key);
            String dbPropertyName = key;
            Map<String, String> linkedProperties = new HashMap<>();
            final Map<PropertyMetadata, String> metadata = obj.getPropertyMetadata(key);
            if (metadata.containsKey(PropertyMetadata.DATA_LINK)) {
                linkedProperties.put(key, metadata.get(PropertyMetadata.DATA_LINK));
            }
            if (metadata.containsKey(PropertyMetadata.DB_ALIAS)) {
                dbPropertyName = metadata.get(PropertyMetadata.DB_ALIAS);
            }

            if (!linkedProperties.containsKey(key)) {
                if (values.size() > 1) {
                    queryBuilder.getVerticesByIndexedProperty(dbPropertyName,
                        obj.castValueAccordingToSchema(key, values));
                } else {
                    queryBuilder.getVerticesByIndexedProperty(dbPropertyName,
                        obj.castValueAccordingToSchema(key, values.get(0)));
                }
            }
            handleLinkedProperties(obj, uriKeys, linkedProperties);
        }
    }

    private void handleLinkedProperties(Introspector obj, MultivaluedMap<String, String> uriKeys,
        Map<String, String> linkedProperties) throws AAIException {

        QueryBuilder[] builders = new QueryBuilder[linkedProperties.keySet().size()];
        Set<Entry<String, String>> entrySet = linkedProperties.entrySet();
        int i = 0;
        Iterator<Entry<String, String>> itr = entrySet.iterator();

        while (itr.hasNext()) {
            Entry<String, String> entry = itr.next();
            Introspector child;
            try {
                child = new URIToObject(this.latestLoader,
                    new URI(URITools
                        .replaceTemplates(obj, entry.getValue(), PropertyMetadata.DATA_LINK, true)
                        .orElse(""))).getEntity();
            } catch (IllegalArgumentException | UnsupportedEncodingException
                | URISyntaxException e) {
                throw new AAIException("AAI_4000", e);
            }
            List<String> values = uriKeys.get(entry.getKey());
            QueryBuilder builder = queryBuilder.newInstance();
            builder.createEdgeTraversal(EdgeType.TREE, obj, child);
            if (values.size() > 1) {
                builder.getVerticesByIndexedProperty(entry.getKey(),
                    obj.castValueAccordingToSchema(entry.getKey(), values));
            } else {
                builder.getVerticesByIndexedProperty(entry.getKey(),
                    obj.castValueAccordingToSchema(entry.getKey(), values.get(0)));
            }

            builders[i] = builder;
            i++;
        }

        queryBuilder.where(builders);

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
        return false;
    }
}
