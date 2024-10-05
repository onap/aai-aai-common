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

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.junit.Before;
import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.exceptions.AAIUnmarshallingException;
import org.onap.aai.serialization.dynamicentity.IntrospectorSerializer;
import org.onap.aai.setup.SchemaVersion;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.module.SimpleModule;

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
}
