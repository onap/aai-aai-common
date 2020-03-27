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

package org.onap.aai.dbmap;

import static org.janusgraph.graphdb.configuration.GraphDatabaseConfiguration.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.Objects;
import java.util.regex.Pattern;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.janusgraph.diskstorage.configuration.ConfigElement;
import org.janusgraph.diskstorage.configuration.backend.CommonsConfiguration;

/**
 * For building a config that JanusGraphFactory.open can use with an identifiable graph.unique-instance-id
 */
public class AAIGraphConfig {

    private static final Logger logger = LoggerFactory.getLogger(AAIGraphConfig.class);

    private AAIGraphConfig() {
    };

    public PropertiesConfiguration getCc(String configPath, String graphType, String service)
            throws ConfigurationException, FileNotFoundException {

        PropertiesConfiguration cc = this.loadJanusGraphPropFile(configPath);

        String uid = ManagementFactory.getRuntimeMXBean().getName() + "_" + service + "_" + graphType + "_"
                + System.currentTimeMillis();
        for (char c : ConfigElement.ILLEGAL_CHARS) {
            uid = StringUtils.replaceChars(uid, c, '_');
        }

        cc.addProperty("graph.unique-instance-id", uid);

        return cc;
    }

    private PropertiesConfiguration loadJanusGraphPropFile(String shortcutOrFile)
            throws ConfigurationException, FileNotFoundException {
        File file = new File(shortcutOrFile);
        if (file.exists()) {
            PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
            propertiesConfiguration.setAutoSave(false);
            propertiesConfiguration.load(shortcutOrFile);
            return propertiesConfiguration;
        } else {
            throw new FileNotFoundException(shortcutOrFile);
        }
    }

    public static class Builder {
        private String configPath;
        private String graphType;
        private String service;

        public Builder(String configPath) {
            this.configPath = configPath;
        }

        public Builder withGraphType(String graphType) {
            this.graphType = Objects.toString(graphType, "NA");
            return this;
        }

        public Builder forService(String service) {
            this.service = Objects.toString(service, "NA");
            return this;
        }

        public PropertiesConfiguration buildConfiguration() throws ConfigurationException, FileNotFoundException {
            return new AAIGraphConfig().getCc(this.configPath, this.graphType, this.service);
        }

    }

}
