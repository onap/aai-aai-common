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

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.management.ManagementFactory;
import java.util.Objects;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.janusgraph.diskstorage.configuration.ConfigElement;

/**
 * For building a config that JanusGraphFactory.open can use with an identifiable graph.unique-instance-id
 */
public class AAIGraphConfig {

    private AAIGraphConfig() {

    }

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
            FileBasedConfigurationBuilder<PropertiesConfiguration> builder =
                new FileBasedConfigurationBuilder<PropertiesConfiguration>(PropertiesConfiguration.class)
                .configure(new Parameters()
                .properties()
                .setFile(file));
            return builder.getConfiguration();
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
