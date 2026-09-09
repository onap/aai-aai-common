/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ==============================================================================
 * Copyright © 2026 Deutsche Telekom. All rights reserved.
 * ==============================================================================
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.domain.deltaEvent.DeltaEvent;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.kafka.DeltaProducer;
import org.onap.aai.util.AAIConfig;
import org.onap.aai.util.delta.DeltaAction;
import org.onap.aai.util.delta.DeltaEvents;
import org.onap.aai.util.delta.ObjectDelta;
import org.onap.aai.util.delta.PropertyDelta;

public class DeltaEventsServiceTest {

  private static TimeZone originalTimeZone;
  private static String originalAjscHome;
  private static String originalBundleconfigDir;

  @BeforeClass
  public static void initializeConfig() throws AAIException {
    originalTimeZone = TimeZone.getDefault();
    originalAjscHome = System.getProperty("AJSC_HOME");
    originalBundleconfigDir = System.getProperty("BUNDLECONFIG_DIR");
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"));
    System.setProperty("AJSC_HOME", ".");
    System.setProperty("BUNDLECONFIG_DIR", "src/test/resources/bundleconfig-local");
    AAIConfig.init();
  }

  @AfterClass
  public static void restoreTimeZone() {
    TimeZone.setDefault(originalTimeZone);
    restoreSystemProperty("AJSC_HOME", originalAjscHome);
    restoreSystemProperty("BUNDLECONFIG_DIR", originalBundleconfigDir);
  }

  private static void restoreSystemProperty(String propertyName, String originalValue) {
    if (originalValue == null) {
      System.clearProperty(propertyName);
    } else {
      System.setProperty(propertyName, originalValue);
    }
  }

  @Test
  public void sendsAnAllowedCreateWithACompleteEventHeader() {
    DeltaProducer producer = mock(DeltaProducer.class);
    ObjectDelta delta = delta(DeltaAction.CREATE);
    DeltaEvents events = new DeltaEvents("transaction-123", "source", "v29", Map.of("pserver", delta), producer,
        true, Set.of("CREATE"));

    assertTrue(new DeltaEventsService().triggerEvents(events));

    ArgumentCaptor<DeltaEvent> eventCaptor = ArgumentCaptor.forClass(DeltaEvent.class);
    verify(producer).sendNotification(eventCaptor.capture());
    DeltaEvent event = eventCaptor.getValue();
    assertEquals("DELTA", event.getCambriaPartition());
    assertEquals("transaction-123", event.getEventHeader().getId());
    assertEquals("19700101-01:00:00:000", event.getEventHeader().getTimestamp());
    assertEquals("source", event.getEventHeader().getSourceName());
    assertEquals("devINT1", event.getEventHeader().getDomain());
    assertEquals("DELTA", event.getEventHeader().getEventType());
    assertEquals("v29", event.getEventHeader().getVersion());
    assertEquals("CREATE", event.getEventHeader().getAction());
    assertEquals("pserver", event.getEventHeader().getEntityType());
    assertEquals("/aai/v29/pservers/pserver/test", event.getEventHeader().getEntityLink());
    assertEquals("uuid-123", event.getEventHeader().getEntityUuid());
    assertEquals(1, event.getEntities().size());
  }

  @Test
  public void doesNotSendWhenThereAreNoObjectDeltas() {
    DeltaProducer producer = mock(DeltaProducer.class);
    DeltaEvents events = new DeltaEvents("transaction-123", "source", "v29", Map.of(), producer, true,
        Set.of("CREATE"));

    assertFalse(new DeltaEventsService().triggerEvents(events));

    verify(producer, never()).sendNotification(org.mockito.ArgumentMatchers.any());
  }

  @Test
  public void doesNotSendWhenTheFirstActionIsNotAllowed() {
    DeltaProducer producer = mock(DeltaProducer.class);
    DeltaEvents events = new DeltaEvents("transaction-123", "source", "v29", Map.of("pserver", delta(DeltaAction.DELETE)),
        producer, true, Set.of("CREATE"));

    assertFalse(new DeltaEventsService().triggerEvents(events));

    verify(producer, never()).sendNotification(org.mockito.ArgumentMatchers.any());
  }

  @Test
  public void doesNotSendRelationshipOnlyUpdatesWhenRelationshipEventsAreDisabled() {
    DeltaProducer producer = mock(DeltaProducer.class);
    ObjectDelta delta = delta(DeltaAction.UPDATE);
    delta.addPropertyDelta(AAIProperties.LAST_MOD_TS, new PropertyDelta(DeltaAction.UPDATE, "100"));
    DeltaEvents events = new DeltaEvents("transaction-123", "source", "v29", Map.of("pserver", delta), producer, false,
        Set.of("UPDATE"));

    assertFalse(new DeltaEventsService().triggerEvents(events));

    verify(producer, never()).sendNotification(org.mockito.ArgumentMatchers.any());
  }

  @Test
  public void sendsRelationshipOnlyUpdatesWhenRelationshipEventsAreEnabled() {
    DeltaProducer producer = mock(DeltaProducer.class);
    ObjectDelta delta = delta(DeltaAction.UPDATE);
    delta.addPropertyDelta(AAIProperties.LAST_MOD_TS, new PropertyDelta(DeltaAction.UPDATE, "100"));
    DeltaEvents events = new DeltaEvents("transaction-123", "source", "v29", Map.of("pserver", delta), producer, true,
        Set.of("UPDATE"));

    assertTrue(new DeltaEventsService().triggerEvents(events));

    verify(producer).sendNotification(org.mockito.ArgumentMatchers.any());
  }

  @Test(expected = NullPointerException.class)
  public void requiresTheNodeTypePropertyToBuildAnEventHeader() {
    DeltaProducer producer = mock(DeltaProducer.class);
    ObjectDelta delta = new ObjectDelta("/aai/v29/pservers/pserver/test", DeltaAction.CREATE, "source", 0L);
    delta.addPropertyDelta(AAIProperties.AAI_UUID, new PropertyDelta(DeltaAction.CREATE, "uuid-123"));
    DeltaEvents events = new DeltaEvents("transaction-123", "source", "v29", Map.of("pserver", delta), producer, true,
        Set.of("CREATE"));

    new DeltaEventsService().triggerEvents(events);
  }

  @Test(expected = NullPointerException.class)
  public void requiresTheUuidPropertyToBuildAnEventHeader() {
    DeltaProducer producer = mock(DeltaProducer.class);
    ObjectDelta delta = new ObjectDelta("/aai/v29/pservers/pserver/test", DeltaAction.CREATE, "source", 0L);
    delta.addPropertyDelta(AAIProperties.NODE_TYPE, new PropertyDelta(DeltaAction.CREATE, "pserver"));
    DeltaEvents events = new DeltaEvents("transaction-123", "source", "v29", Map.of("pserver", delta), producer, true,
        Set.of("CREATE"));

    new DeltaEventsService().triggerEvents(events);
  }

  private ObjectDelta delta(DeltaAction action) {
    ObjectDelta delta = new ObjectDelta("/aai/v29/pservers/pserver/test", action, "source", 0L);
    delta.addPropertyDelta(AAIProperties.NODE_TYPE, new PropertyDelta(action, "pserver"));
    delta.addPropertyDelta(AAIProperties.AAI_UUID, new PropertyDelta(action, "uuid-123"));
    return delta;
  }
}