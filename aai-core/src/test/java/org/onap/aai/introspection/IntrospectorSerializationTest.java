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

package org.onap.aai.introspection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.domain.notificationEvent.NotificationEvent;
import org.onap.aai.domain.notificationEvent.NotificationEvent.EventHeader;
import org.onap.aai.introspection.exceptions.AAIUnmarshallingException;
import org.onap.aai.setup.SchemaVersion;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

public class IntrospectorSerializationTest extends AAISetup {

  ObjectMapper mapper;
  Loader loader;

  @Before
  public void setup() {
    loader = loaderFactory.getMoxyLoaderInstance().get(new SchemaVersion("v14"));
  }

  @Test
  public void serializePlain() throws IOException, AAIUnmarshallingException {
    mapper = new ObjectMapper();

    String pserver = new String(Files.readAllBytes(Path.of("src/test/resources/payloads/templates/pserver.json")));
    Introspector introspector = loader.unmarshal("pserver", pserver);
    String result = mapper.writeValueAsString(introspector);
    JSONAssert.assertEquals(pserver, result, false);
  }

  @Test
  public void serializeNotificationEvent() throws IOException, AAIUnmarshallingException {
    mapper = new ObjectMapper();
    mapper.registerModule(new JaxbAnnotationModule());

    String pserver = new String(Files.readAllBytes(Path.of("src/test/resources/payloads/templates/pserver.json"))).replace("${hostname}", "pserver1");
    Introspector introspector = loader.unmarshal("pserver", pserver);

    NotificationEvent notificationEvent = new NotificationEvent();
    notificationEvent.setCambriaPartition("AAI");
    notificationEvent.setEntity(introspector);
    EventHeader eventHeader = new EventHeader();
    eventHeader.setSeverity("NORMAL");
    eventHeader.setEntityType("pserver");
    eventHeader.setTopEntityType("pserver");
    eventHeader.setEntityLink("/aai/v14/cloud-infrastructure/pservers/pserver/pserver1");
    eventHeader.setEventType("AAI-EVENT");
    eventHeader.setDomain("devINT1");
    eventHeader.setAction("CREATE");
    eventHeader.setSequenceNumber("0");
    eventHeader.setId("someTransaction");
    eventHeader.setSourceName("test");
    eventHeader.setVersion("v14");
    notificationEvent.setEventHeader(eventHeader);
    String result = mapper.writeValueAsString(notificationEvent);
    String expectedEvent = new String(Files.readAllBytes(Path.of("src/test/resources/payloads/expected/pserver-event.json")));
    JSONAssert.assertEquals(expectedEvent, result, false);
  }
}
