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
package org.onap.aai.transforms;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.PayloadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class XmlFormatTransformerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlFormatTransformerTest.class);

    private XmlFormatTransformer xmlFormatTransformer;

    @Before
    public void setup(){
        this.xmlFormatTransformer = new XmlFormatTransformer();
    }

    @Test
    public void testTransformJsonToXml() throws IOException {

        String input = PayloadUtil.getResourcePayload("transform-results-to-result.json");
        String expected = PayloadUtil.getExpectedPayload("transform-json-to-xml.xml");
        // Remove all the whitespace in the xml
        expected = expected.replaceAll("\\s", "");

        LOGGER.debug("Converting the following input to xml: {}", input);
        String output = xmlFormatTransformer.transform(input);

        LOGGER.debug("Converted xml payload: {}", output);
        assertThat(output, is(expected));
    }
}
