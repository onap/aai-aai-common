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

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class OxmModelLoaderTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldLoadOxmModelsWhichMatchToPattern() throws Exception {
        OxmModelLoader.loadModels("classpath*:test_aai_oxm*.xml", Pattern.compile("test_aai_oxm_(.*).xml"));

        Assert.assertTrue(OxmModelLoader.getVersionContextMap().size() == 2);
        Assert.assertFalse(OxmModelLoader.getVersionContextMap().containsKey("v7"));
        Assert.assertTrue(OxmModelLoader.getVersionContextMap().containsKey("v8"));
        Assert.assertTrue(OxmModelLoader.getVersionContextMap().containsKey("v9"));
    }

    @Test
    public void shouldReportAnErrorWhenOxmModelsAreNotAvailable() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage("Failed to load schema");

        OxmModelLoader.loadModels("classpath*:non_existing_aai_oxm*.xml",
                Pattern.compile("non_existing_aai_oxm_(.*).xml"));
    }

}
