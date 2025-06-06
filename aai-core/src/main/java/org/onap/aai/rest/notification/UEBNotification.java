/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright © 2024 Deutsche Telekom.
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

package org.onap.aai.rest.notification;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.core.Response.Status;

import org.onap.aai.domain.notificationEvent.NotificationEvent;
import org.onap.aai.domain.notificationEvent.NotificationEvent.EventHeader;
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
import org.onap.aai.util.AAIConfig;
import org.onap.aai.util.AAIConstants;
import org.onap.aai.util.FormatDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UEBNotification {

    private static final Logger LOGGER = LoggerFactory.getLogger(UEBNotification.class);
    private static final FormatDate FORMAT_DATE = new FormatDate("YYYYMMdd-HH:mm:ss:SSS");
    private static final String EVENT_TYPE = "AAI-EVENT";

    private final String domain = AAIConfig.get("aai.notificationEvent.default.domain", "UNK");
    private final String sequenceNumber = AAIConfig.get("aai.notificationEvent.default.sequenceNumber", "UNK");
    private final String severity = AAIConfig.get("aai.notificationEvent.default.severity", "UNK");
    private final Map<String, NotificationEvent> events;
    private final Loader currentVersionLoader;
    private final SchemaVersion notificationVersion;

    public UEBNotification(LoaderFactory loaderFactory, SchemaVersions schemaVersions) {
        events = new LinkedHashMap<>();
        notificationVersion = schemaVersions.getDefaultVersion();
        currentVersionLoader = loaderFactory.createLoaderForVersion(ModelType.MOXY, notificationVersion);
    }

    public void createNotificationEvent(String transactionId, String sourceOfTruth, Status status, URI uri,
            Introspector obj, HashMap<String, Introspector> relatedObjects, String basePath)
            throws AAIException, UnsupportedEncodingException {

        String action = getAction(status);

        try {
            EntityConverter entityConverter = new EntityConverter(new URIToObject(currentVersionLoader, uri, relatedObjects));
            EventHeader eventHeader = new EventHeader();

            basePath = formatBasePath(basePath);
            String entityLink = formatEntityLink(uri, basePath);
            eventHeader.setEntityLink(entityLink);
            eventHeader.setAction(action);
            eventHeader.setEntityType(obj.getDbName());
            eventHeader.setTopEntityType(entityConverter.getTopEntityName());
            eventHeader.setSourceName(sourceOfTruth);
            eventHeader.setVersion(notificationVersion.toString());
            eventHeader.setId(transactionId);

            // default values
            eventHeader.setTimestamp(FORMAT_DATE.getDateTime());
            eventHeader.setEventType(EVENT_TYPE);
            eventHeader.setDomain(domain);
            eventHeader.setSequenceNumber(sequenceNumber);
            eventHeader.setSeverity(severity);

            Introspector entity = entityConverter.convert(obj);

            final NotificationEvent event = new NotificationEvent();
            event.setEventHeader(eventHeader);
            event.setCambriaPartition(AAIConstants.UEB_PUB_PARTITION_AAI);
            event.setEntity(entity);
            events.put(uri.toString(), event);
        } catch (AAIUnknownObjectException e) {
            throw new RuntimeException("Fatal error - notification-event-header object not found!");
        } catch (AAIUnmarshallingException e) {
            LOGGER.error(
                    "Unmarshalling error occurred while generating UEBNotification " + LogFormatTools.getStackTop(e));
        }
    }

    private String formatEntityLink(URI uri, String basePath) {
        String uriStr = getUri(uri.toString(), basePath);
        String entityLink;
        if (uriStr.startsWith("/")) {
            entityLink = basePath + notificationVersion + uriStr;
        } else {
            entityLink = basePath + notificationVersion + "/" + uriStr;
        }
        return entityLink;
    }

    private String formatBasePath(String basePath) {
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
        return basePath;
    }

    private String getAction(Status status) {
        String action = "UPDATE";

        if (status.equals(Status.CREATED)) {
            action = "CREATE";
        } else if (status.equals(Status.OK)) {
            action = "UPDATE";
        } else if (status.equals(Status.NO_CONTENT)) {
            action = "DELETE";
        }
        return action;
    }

    public List<NotificationEvent> getEvents() {
        return new ArrayList<>(this.events.values());
    }

    private String getUri(String uri, String basePath) {
        if (uri == null || uri.isEmpty()) {
            return "";
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
