/*-
 * ============LICENSE_START=======================================================
 * org.openecomp.aai
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

package org.openecomp.aai.util;

import org.junit.Test;
import org.openecomp.aai.introspection.Version;

public class GenerateXsdTest {

    @Test
    public void testGenerationOfXsdAndYaml() throws Exception {

        GenerateXsd generateXsd = new GenerateXsd();
        System.setProperty("gen_version", Version.getLatest().toString());
        System.setProperty("gen_type", "XSD");
        System.setProperty("yamlresponses_url", "");
        System.setProperty("yamlresponses_label", "");

        generateXsd.main(new String[]{});

        System.setProperty("gen_version", Version.getLatest().toString());
        System.setProperty("gen_type", "YAML");

        String wikiLink = System.getProperty("aai.wiki.link");

        if(wikiLink == null){
            wikiLink = "https://wiki.onap.org/";
        }

        System.setProperty("yamlresponses_url", wikiLink);
        System.setProperty("yamlresponses_label", "Response codes found in [response codes]");

        generateXsd.main(new String[]{});
    }

}
