/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 *  Modifications Copyright © 2018 IBM.
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
package org.onap.aai.util;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;
import org.onap.aai.util.swagger.GenerateSwagger;

import freemarker.template.TemplateException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AutoGenerateHtml {
    
    private static final String AAI_GENERATE_VERSION = "aai.generate.version";
    public static final String DEFAULT_SCHEMA_DIR = "../aai-schema";
    //if the program is run from aai-common, use this directory as default"
    public static final String ALT_SCHEMA_DIR = "aai-schema";
    //used to check to see if program is run from aai-core
    public static final String DEFAULT_RUN_DIR = "aai-core";

    public static void main(String[] args) throws IOException, TemplateException {
        String savedProperty = System.getProperty(AAI_GENERATE_VERSION);

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
            "org.onap.aai.config",
            "org.onap.aai.setup"
        );


        SchemaVersions schemaVersions = ctx.getBean(SchemaVersions.class);
        
        List<SchemaVersion> versionsToGen = schemaVersions.getVersions();
        Collections.sort(versionsToGen);
        Collections.reverse(versionsToGen);
        ListIterator<SchemaVersion> versionIterator = versionsToGen.listIterator();
        String schemaDir;
        if(System.getProperty("user.dir") != null && !System.getProperty("user.dir").contains(DEFAULT_RUN_DIR)) {
            schemaDir = ALT_SCHEMA_DIR;
          }
          else {
              schemaDir = DEFAULT_SCHEMA_DIR;
          }
          String release = System.getProperty("aai.release", "onap");
        while (versionIterator.hasNext()) {
            System.setProperty(AAI_GENERATE_VERSION, versionIterator.next().toString());
            String yamlFile = schemaDir + "/src/main/resources/" + release + "/aai_swagger_yaml/aai_swagger_" + System.getProperty(AAI_GENERATE_VERSION)+ ".yaml";
            File swaggerYamlFile = new File(yamlFile);
            if(swaggerYamlFile.exists()) {
                GenerateSwagger.schemaVersions = schemaVersions;
                GenerateSwagger.main(args);
            }
        }
        System.setProperty(AAI_GENERATE_VERSION, savedProperty);    
    }
}
