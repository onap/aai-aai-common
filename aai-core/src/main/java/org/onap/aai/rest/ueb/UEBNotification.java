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

package org.onap.aai.rest.ueb;

import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.introspection.exceptions.AAIUnmarshallingException;
import org.onap.aai.logging.LogFormatTools;
import org.onap.aai.parsers.uri.URIToObject;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response.Status;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.*;

/**
 * The Class UEBNotification.
 */
public class UEBNotification {

    private static final Logger LOGGER = LoggerFactory.getLogger(UEBNotification.class);

    private Loader currentVersionLoader = null;
    protected Map<String, NotificationEvent> events = null;
    private SchemaVersion notificationVersion = null;

    /**
     * Instantiates a new UEB notification.
     *
     * @param loader the loader
     */
    public UEBNotification(Loader loader, LoaderFactory loaderFactory, SchemaVersions schemaVersions) {
        events = new LinkedHashMap<>();
        SchemaVersion defaultVersion = schemaVersions.getDefaultVersion();
        currentVersionLoader = loaderFactory.createLoaderForVersion(loader.getModelType(), defaultVersion);
        notificationVersion = defaultVersion;
    }

    /**
     * Instantiates a new UEB notification.
     *
     * @param modelType - Model type
     * @param loaderFactory - the loader factory
     * @param schemaVersions the schema versions bean
     */
    public UEBNotification(ModelType modelType, LoaderFactory loaderFactory, SchemaVersions schemaVersions) {
        events = new LinkedHashMap<>();
        SchemaVersion defaultVersion = schemaVersions.getDefaultVersion();
        currentVersionLoader = loaderFactory.createLoaderForVersion(modelType, defaultVersion);
        notificationVersion = defaultVersion;
    }

    /**
     * Creates the notification event.
     *
     * @param transactionId the X-TransactionId
     * @param sourceOfTruth
     * @param status the status
     * @param uri the uri
     * @param obj the obj
     * @param basePath base URI path
     * @throws AAIException the AAI exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    public void createNotificationEvent(String transactionId, String sourceOfTruth, Status status, URI uri,
            Introspector obj, HashMap<String, Introspector> relatedObjects, String basePath)
            throws AAIException, UnsupportedEncodingException {

        String action = "UPDATE";

        if (status.equals(Status.CREATED)) {
            action = "CREATE";
        } else if (status.equals(Status.OK)) {
            action = "UPDATE";
        } else if (status.equals(Status.NO_CONTENT)) {
            action = "DELETE";
        }

        try {
            Introspector eventHeader = currentVersionLoader.introspectorFromName("notification-event-header");
            URIToObject parser = new URIToObject(currentVersionLoader, uri, relatedObjects);

            if ((basePath != null) && (!basePath.isEmpty())) {
                if (!(basePath.startsWith("/"))) {
                    basePath = "/" + basePath;
                }
                if (!(basePath.endsWith("/"))) {
                    basePath = basePath + "/";
                }
            } else {
                // default
                basePath = "/aai/";
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Please check the schema.uri.base.path as it didn't seem to be set");
                }
            }

            String uriStr = getUri(uri.toString(), basePath);
            String entityLink;
            if (uriStr.startsWith("/")) {
                entityLink = basePath + notificationVersion + uriStr;
            } else {
                entityLink = basePath + notificationVersion + "/" + uriStr;
            }

            eventHeader.setValue("entity-link", entityLink);
            eventHeader.setValue("action", action);
            eventHeader.setValue("entity-type", obj.getDbName());
            eventHeader.setValue("top-entity-type", parser.getTopEntityName());
            eventHeader.setValue("source-name", sourceOfTruth);
            eventHeader.setValue("version", notificationVersion.toString());
            eventHeader.setValue("id", transactionId);

            List<Object> parentList = parser.getParentList();
            parentList.clear();

            if (!parser.getTopEntity().equals(parser.getEntity())) {
                Introspector child = obj;
                if (!parser.getLoader().getVersion().equals(obj.getVersion())) {
                    String json = obj.marshal(false);
                    child = parser.getLoader().unmarshal(parser.getEntity().getName(), json);
                }

                // wrap the child object in its parents
                parentList.add(child.getUnderlyingObject());
            }

            final Introspector eventObject;

            // convert to most resent version
            if (!parser.getLoader().getVersion().equals(currentVersionLoader.getVersion())) {
                String json = "";
                if (parser.getTopEntity().equals(parser.getEntity())) {
                    // convert the parent object passed in
                    json = obj.marshal(false);
                    eventObject = currentVersionLoader.unmarshal(obj.getName(), json);
                } else {
                    // convert the object created in the parser
                    json = parser.getTopEntity().marshal(false);
                    eventObject = currentVersionLoader.unmarshal(parser.getTopEntity().getName(), json);
                }
            } else {
                if (parser.getTopEntity().equals(parser.getEntity())) {
                    // take the top level parent object passed in
                    eventObject = obj;
                } else {
                    // take the wrapped child objects (ogres are like onions)
                    eventObject = parser.getTopEntity();
                }
            }
            final NotificationEvent event =
                    new NotificationEvent(currentVersionLoader, eventHeader, eventObject, transactionId, sourceOfTruth);
            events.put(uri.toString(), event);
        } catch (AAIUnknownObjectException e) {
            throw new RuntimeException("Fatal error - notification-event-header object not found!");
        } catch (AAIUnmarshallingException e) {
            LOGGER.error(
                    "Unmarshalling error occurred while generating UEBNotification " + LogFormatTools.getStackTop(e));
        }
    }

    /**
     * Trigger events.
     *
     * @throws AAIException the AAI exception
     */
    public void triggerEvents() throws AAIException {
        for (NotificationEvent event : events.values()) {
            event.trigger();
        }
        clearEvents();
    }

    public List<NotificationEvent> getEvents() {
        return new ArrayList<>(this.events.values());
    }
    public Map<String, NotificationEvent> getEventsMap() {
        return this.events;
    }

    private String getUri(String uri, String basePath) {
        if (uri == null || uri.isEmpty()) {
            return uri;
        } else if (uri.charAt(0) != '/') {
            uri = '/' + uri;
        }

        if ((basePath != null) && (!basePath.isEmpty())) {
            if (!(basePath.startsWith("/"))) {
                basePath = "/" + basePath;
            }
            if (!(basePath.endsWith("/"))) {
                basePath = basePath + "/";
            }
        }

        LOGGER.trace("Notification header uri base path:'{}', uri:'{}'", basePath, uri);

        return uri.replaceAll("^" + basePath + "v\\d+", "");
    }

    public void clearEvents() {
        events.clear();
    }
}
