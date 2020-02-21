/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2019 AT&T Intellectual Property. All rights reserved.
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
package org.onap.aai.aaf.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// This component will be created if and only if any of the following profiles are active
@Component
@Profile({
    AafProfiles.AAF_CERT_AUTHENTICATION,
    AafProfiles.AAF_AUTHENTICATION,
    AafProfiles.TWO_WAY_SSL
})
public class CadiProps {

    private static final Logger LOGGER = LoggerFactory.getLogger(CadiProps.class);

    private String cadiFileName;

    private Properties cadiProperties;

    @Autowired
    public CadiProps(@Value("${aaf.cadi.file:./resources/cadi.properties}") String filename){
        cadiFileName   = filename;
        cadiProperties = new Properties();
    }

    @PostConstruct
    public void init() throws IOException {

        File cadiFile  = new File(cadiFileName);

        if(!cadiFile.exists()){
            LOGGER.warn("Unable to find the cadi file in the given path {} so loading cadi.properties from classloader", cadiFileName);
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("cadi.properties");
            cadiProperties.load(is);
        } else {
            LOGGER.info("Successfully found the file {} and started loading the properties from it", cadiFileName);
            cadiFileName = cadiFile.getAbsolutePath();
            try (InputStream inputStream = new FileInputStream(cadiFile)) {
                cadiProperties.load(inputStream);
            }

        }
    }
    public String getCadiFileName() {
        return cadiFileName;
    }
    public Properties getCadiProperties(){
        return cadiProperties;
    }
}
