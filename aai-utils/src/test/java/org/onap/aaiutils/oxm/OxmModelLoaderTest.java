/*-
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aaiutils.oxm;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OxmModelLoaderTest {

    @Test
    public void shouldLoadOxmModelsWhichMatchToPattern() throws Exception {
        OxmModelLoader.loadModels("classpath*:test_aai_oxm*.xml", Pattern.compile("test_aai_oxm_(.*).xml"));

        Assertions.assertTrue(OxmModelLoader.getVersionContextMap().size() == 2);
        Assertions.assertFalse(OxmModelLoader.getVersionContextMap().containsKey("v7"));
        Assertions.assertTrue(OxmModelLoader.getVersionContextMap().containsKey("v8"));
        Assertions.assertTrue(OxmModelLoader.getVersionContextMap().containsKey("v9"));
    }

    @Test
    public void shouldReportAnErrorWhenOxmModelsAreNotAvailable() throws Exception {
        Throwable exception = assertThrows(Exception.class, () -> {

            OxmModelLoader.loadModels("classpath*:non_existing_aai_oxm*.xml",
                    Pattern.compile("non_existing_aai_oxm_(.*).xml"));
        });
        assertTrue(exception.getMessage().contains("Failed to load schema"));
    }

}
