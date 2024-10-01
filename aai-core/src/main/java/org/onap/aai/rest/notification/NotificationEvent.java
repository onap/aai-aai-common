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

package org.onap.aai.rest.notification;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.parsers.uri.URIToObject;
import org.onap.aai.setup.SchemaVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Value;

@Value
public class NotificationEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationEvent.class);
    private final EventHeader eventHeader;
    private final Introspector obj;
    private final String transactionId;
    private final String sourceOfTruth;

    public NotificationEvent(String transactionId, String sourceOfTruth, Status status, URI uri, Introspector obj, Map<String, Introspector> relatedObjects, String basePath, Loader loader, SchemaVersion schemaVersion) throws UnsupportedEncodingException, AAIException {
        this.transactionId = transactionId;
        this.sourceOfTruth = sourceOfTruth;
        EntityConverter entityConverter = new EntityConverter(new URIToObject(loader, uri, relatedObjects));
        this.obj = entityConverter.convert(obj);
        this.eventHeader = createEventHeader(uri, basePath, schemaVersion, status, entityConverter.getTopEntityName());
    }

    private EventHeader createEventHeader(URI uri, String basePath, SchemaVersion schemaVersion, Status status, String topEntityName) {
        String entityLink = formatEntityLink(schemaVersion, uri, formatBasePath(basePath));
        return EventHeader.builder()
            .sourceName(sourceOfTruth)
            .id(transactionId)
            .entityLink(entityLink)
            .version(schemaVersion.toString())
            .action(getAction(status))
            .entityType(obj.getDbName())
            .topEntityType(topEntityName)
            .build();
    }

    private String formatEntityLink(SchemaVersion schemaVersion, URI uri, String basePath) {
        String uriStr = getUri(uri.toString(), basePath);
        String entityLink;
        if (uriStr.startsWith("/")) {
            entityLink = basePath + schemaVersion + uriStr;
        } else {
            entityLink = basePath + schemaVersion + "/" + uriStr;
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

    // public String getNotificationEvent() throws AAIException {
    //     return new StoreNotificationEvent(transactionId, sourceOfTruth).storeEventOnly(loader, eventHeader, obj);
    // }
}
