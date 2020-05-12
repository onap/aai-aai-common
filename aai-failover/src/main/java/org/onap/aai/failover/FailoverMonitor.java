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
package org.onap.aai.failover;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@Component
public class FailoverMonitor {

    private static final String IS_PRIMARY = "is_primary";
    private static final String TRUE = "true";
    private static final String DEFAULT_FOR_PRIMARY = TRUE;

    @Value("${failover.location:/opt/app/failover/failover.properties}")
    private String failoverPropertiesPath;

    public boolean shouldRun() throws IOException {

        Path failoverPath = Paths.get(failoverPropertiesPath);

        if(Files.exists(failoverPath)){
            Properties properties = new Properties();
            try (InputStream is = Files.newInputStream(failoverPath)){
                properties.load(is);
                // If the property is_primary is missing then it should proceed
                return TRUE.equals(properties.getProperty(IS_PRIMARY, DEFAULT_FOR_PRIMARY));
            }
        } else {
            // If the file doesn't exist, then scheduled task should execute
            return true;
        }
    }
}
