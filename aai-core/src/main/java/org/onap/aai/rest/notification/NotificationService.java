/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2024 Deutsche Telekom. All rights reserved.
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response.Status;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.prevalidation.ValidationService;
import org.onap.aai.rest.db.HttpEntry;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.engines.query.QueryEngine;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.util.AAIConfig;
import org.onap.aai.util.delta.DeltaEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

  public static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

  private final LoaderFactory loaderFactory;
  private final boolean isDeltaEventsEnabled;
  private final String basePath;

  public NotificationService(
    LoaderFactory loaderFactory,
    @Value("${schema.uri.base.path}") String basePath,
    @Value("${delta.events.enabled:false}") boolean isDeltaEventsEnabled) {
    this.loaderFactory = loaderFactory;
    this.basePath = basePath;
    this.isDeltaEventsEnabled = isDeltaEventsEnabled;
  }

  /**
   * Inject the validation service if the profile pre-valiation is enabled,
   * Otherwise this variable will be set to null and thats why required=false
   * so that it can continue even if pre validation isn't enabled
   */
  @Autowired(required = false)
  private ValidationService validationService;

  /**
   * Generate notification events for the resulting db requests.
   */
  public void generateEvents(UEBNotification notification, int notificationDepth, String sourceOfTruth, DBSerializer serializer,
      String transactionId,
      QueryEngine queryEngine, Set<Vertex> mainVertexesToNotifyOn, SchemaVersion schemaVersion) throws AAIException {
    if (notificationDepth == AAIProperties.MINIMUM_DEPTH) {
      serializer.getUpdatedVertexes().entrySet().stream()
        .filter(Map.Entry::getValue)
        .map(Map.Entry::getKey)
        .forEach(mainVertexesToNotifyOn::add);
    }
    Set<Vertex> edgeVertexes = serializer.touchStandardVertexPropertiesForEdges().stream()
        .filter(v -> !mainVertexesToNotifyOn.contains(v))
        .collect(Collectors.toSet());

    try {
      createNotificationEvents(mainVertexesToNotifyOn, notification, sourceOfTruth, serializer, transactionId, queryEngine,
          notificationDepth, schemaVersion);
      if ("true".equals(AAIConfig.get("aai.notification.both.sides.enabled", "true"))) {
        createNotificationEvents(edgeVertexes, notification, sourceOfTruth, serializer, transactionId, queryEngine,
            AAIProperties.MINIMUM_DEPTH, schemaVersion);
      }
    } catch (UnsupportedEncodingException e) {
      LOGGER.warn("Encountered exception generating events", e);
    }

    // Since @Autowired required is set to false, we need to do a null check
    // for the existence of the validationService since its only enabled if profile
    // is enabled
    if (validationService != null) {
      validationService.validate(notification.getEvents());
    }

    notification.triggerEvents();
    if (isDeltaEventsEnabled) {
      try {
        DeltaEvents deltaEvents = new DeltaEvents(transactionId, sourceOfTruth, schemaVersion.toString(),
            serializer.getObjectDeltas());
        deltaEvents.triggerEvents();
      } catch (Exception e) {
        LOGGER.error("Error sending Delta Events", e);
      }
    }
  }

  /**
   * Generate notification events for provided set of vertexes at the specified
   * depth
   */
  private void createNotificationEvents(Set<Vertex> vertexesToNotifyOn, UEBNotification notification, String sourceOfTruth, DBSerializer serializer,
      String transactionId, QueryEngine queryEngine, int eventDepth, SchemaVersion schemaVersion)
      throws AAIException, UnsupportedEncodingException {
    for (Vertex vertex : vertexesToNotifyOn) {
      if (canGenerateEvent(vertex)) {
        boolean isVertexNew = vertex.value(AAIProperties.CREATED_TS).equals(vertex.value(AAIProperties.LAST_MOD_TS));
        Status curObjStatus = isVertexNew ? Status.CREATED : Status.OK;

        Introspector curObj = serializer.getLatestVersionView(vertex, eventDepth);
        String aaiUri = vertex.<String>property(AAIProperties.AAI_URI).value();
        String uri = String.format("%s/%s%s", basePath, schemaVersion, aaiUri);
        HashMap<String, Introspector> curRelatedObjs = new HashMap<>();
        if (!curObj.isTopLevel()) {
          curRelatedObjs = serializer.getRelatedObjects(queryEngine, vertex, curObj, loaderFactory.getMoxyLoaderInstance().get(schemaVersion));
        }
        notification.createNotificationEvent(transactionId, sourceOfTruth, curObjStatus, URI.create(uri),
            curObj, curRelatedObjs, basePath);
      }
    }
  }

  /**
   * Verifies that vertex has needed properties to generate on
   *
   * @param vertex Vertex to be verified
   * @return <code>true</code> if vertex has necessary properties and exists
   */
  private boolean canGenerateEvent(Vertex vertex) {
    boolean canGenerate = true;
    try {
      if (!vertex.property(AAIProperties.AAI_URI).isPresent()) {
        LOGGER.debug("Encountered an vertex {} with missing aai-uri", vertex.id());
        canGenerate = false;
      } else if (!vertex.property(AAIProperties.CREATED_TS).isPresent()
          || !vertex.property(AAIProperties.LAST_MOD_TS).isPresent()) {
        LOGGER.debug("Encountered an vertex {} with missing timestamp", vertex.id());
        canGenerate = false;
      }
    } catch (IllegalStateException e) {
      if (e.getMessage().contains(" was removed")) {
        LOGGER.warn("Attempted to generate event for non existent vertex", e);
      } else {
        LOGGER.warn("Encountered exception generating events", e);
      }
      canGenerate = false;
    }
    return canGenerate;
  }

  public void buildNotificationEvent(String sourceOfTruth, Status status, String transactionId,
      UEBNotification notification, Map<Vertex, Introspector> deleteObjects, Map<String, URI> uriMap,
      Map<String, HashMap<String, Introspector>> deleteRelatedObjects, String basePath) {
    for (Map.Entry<Vertex, Introspector> entry : deleteObjects.entrySet()) {
      try {
        if (null != entry.getValue()) {
          String vertexObjectId = entry.getValue().getObjectId();

          if (uriMap.containsKey(vertexObjectId) && deleteRelatedObjects.containsKey(vertexObjectId)) {
            notification.createNotificationEvent(transactionId, sourceOfTruth, status,
                uriMap.get(vertexObjectId), entry.getValue(), deleteRelatedObjects.get(vertexObjectId),
                basePath);
          }
        }
      } catch (UnsupportedEncodingException | AAIException e) {

        LOGGER.warn("Error in sending notification");
      }
    }
  }

}
