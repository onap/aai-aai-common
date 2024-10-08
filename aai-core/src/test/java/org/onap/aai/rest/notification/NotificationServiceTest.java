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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Response.Status;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.detached.DetachedVertex;
import org.apache.tinkerpop.gremlin.structure.util.detached.DetachedVertexProperty;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.aai.AAISetup;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.kafka.NotificationProducerService;
import org.onap.aai.prevalidation.ValidationService;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.engines.query.QueryEngine;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;

public class NotificationServiceTest extends AAISetup {

  @Mock LoaderFactory loaderFactory;
  @Mock SchemaVersions schemaVersions;
  @Mock UEBNotification uebNotification;
  @Mock ValidationService validationService;
  @Mock NotificationProducerService notificationProducerService;
  @Mock DBSerializer dbSerializer;
  @Mock QueryEngine queryEngine;
  @Mock Introspector introspector;

  boolean isDeltaEventsEnabled = false;
  String basePath = "/aai";
  NotificationService notificationService;

  @Before
  public void setup() throws UnsupportedEncodingException, AAIException {
    MockitoAnnotations.openMocks(this);

    when(dbSerializer.touchStandardVertexPropertiesForEdges()).thenReturn(Collections.emptySet());
    when(dbSerializer.getLatestVersionView(any(),anyInt())).thenReturn(introspector);

    notificationService = new NotificationService(validationService, loaderFactory, basePath, isDeltaEventsEnabled, notificationProducerService);
    when(schemaVersions.getDefaultVersion()).thenReturn(new SchemaVersion("v29"));
    doNothing().when(uebNotification).createNotificationEvent(any(),any(),any(),any(),any(),any(),any());
    doNothing().when(notificationProducerService).sendUEBNotification(any());
  }

  @Test
  public void thatNotificationsCanBeCreatedWithoutEdges() throws AAIException, UnsupportedEncodingException {

    Map<String, Object> properties = new HashMap<>();
    properties.put(AAIProperties.NODE_TYPE, Collections.singletonList(new DetachedVertexProperty<String>("1", null, "pserver", new HashMap<>())));
    properties.put(AAIProperties.AAI_URI, Collections.singletonList(new DetachedVertexProperty<String>("1", null, "/pservers/pserver/hostname", new HashMap<>())));
    properties.put(AAIProperties.CREATED_TS, Collections.singletonList(new DetachedVertexProperty<String>("1", null, "12", new HashMap<>())));
    properties.put(AAIProperties.LAST_MOD_TS, Collections.singletonList(new DetachedVertexProperty<String>("1", null, "34", new HashMap<>())));

    Vertex vertex = new DetachedVertex("1","label", properties);
    Set<Vertex> mainVertexesToNotifyOn = new HashSet<>();
    mainVertexesToNotifyOn.add(vertex);
    SchemaVersion schemaVersion = new SchemaVersion("v29");
    when(dbSerializer.getUpdatedVertexes()).thenReturn(Collections.emptyMap());

    notificationService.generateEvents(uebNotification, AAIProperties.MINIMUM_DEPTH, "sourceOfTruth", dbSerializer, "transactionId", queryEngine, mainVertexesToNotifyOn, schemaVersion);

    verify(uebNotification, times(1)).createNotificationEvent(eq("transactionId"), eq("sourceOfTruth"), eq(Status.OK), eq(URI.create("/aai/v29/pservers/pserver/hostname")), eq(introspector), any(), eq("/aai"));
    verify(validationService, times(1)).validate(anyList());
    verify(notificationProducerService, times(1)).sendUEBNotification(uebNotification);
  }

  @Test
  public void thatValidationCanBeDisabled() throws AAIException, UnsupportedEncodingException {

    Map<String, Object> properties = new HashMap<>();
    properties.put(AAIProperties.NODE_TYPE, Collections.singletonList(new DetachedVertexProperty<String>("1", null, "pserver", new HashMap<>())));
    properties.put(AAIProperties.AAI_URI, Collections.singletonList(new DetachedVertexProperty<String>("1", null, "/pservers/pserver/hostname", new HashMap<>())));
    properties.put(AAIProperties.CREATED_TS, Collections.singletonList(new DetachedVertexProperty<String>("1", null, "12", new HashMap<>())));
    properties.put(AAIProperties.LAST_MOD_TS, Collections.singletonList(new DetachedVertexProperty<String>("1", null, "34", new HashMap<>())));

    Vertex vertex = new DetachedVertex("1","label", properties);
    Set<Vertex> mainVertexesToNotifyOn = new HashSet<>();
    mainVertexesToNotifyOn.add(vertex);
    SchemaVersion schemaVersion = new SchemaVersion("v29");
    when(dbSerializer.getUpdatedVertexes()).thenReturn(Collections.emptyMap());

    notificationService = new NotificationService(null, loaderFactory, basePath, isDeltaEventsEnabled, notificationProducerService);
    notificationService.generateEvents(uebNotification, AAIProperties.MINIMUM_DEPTH, "sourceOfTruth", dbSerializer, "transactionId", queryEngine, mainVertexesToNotifyOn, schemaVersion);

    verify(notificationProducerService, times(1)).sendUEBNotification(uebNotification);
  }
}
