/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-18 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

/**
 * 
 */

package org.onap.aai.validation;

import java.util.List;
import java.util.Map;

import org.onap.aai.setup.ConfigTranslator;
import org.onap.aai.setup.SchemaVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * By default, A&AI must have schema files for all current
 * supported Versions in the Version enum
 *
 */
@Component
public class DefaultVersionValidationModule implements VersionValidationModule {

    private ConfigTranslator config;

    @Autowired
    public DefaultVersionValidationModule(ConfigTranslator config) {

        this.config = config;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.onap.aai.validation.VersionValidationModule#validate(org.onap.aai.setup.ConfigTranslator)
     */
    @Override
    public String validate() {
        Map<SchemaVersion, List<String>> nodeConfig = config.getNodeFiles();
        Map<SchemaVersion, List<String>> edgeConfig = config.getEdgeFiles();

        StringBuilder missingVers =
            new StringBuilder().append("Missing schema for the following versions: ");
        boolean isMissing = false;
        for (SchemaVersion v : config.getSchemaVersions().getVersions()) {
            if (nodeConfig.get(v) == null) {
                isMissing = true;
                missingVers.append(v.toString()).append(" has no OXM configured. ");
            }
            if (edgeConfig.get(v) == null) {
                isMissing = true;
                missingVers.append(v.toString()).append(" has no edge rules configured. ");
            }
        }

        if (isMissing) {
            return missingVers.toString();
        } else {
            return "";
        }
    }

}
