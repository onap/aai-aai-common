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

package org.onap.aai.tasks;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.onap.aai.aailog.logs.AaiScheduledTaskAuditLog;
import org.onap.aai.util.AAIConfig;
import org.onap.aai.util.AAIConstants;
import org.onap.logging.filter.base.ONAPComponents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

@Component
public class ScheduledTasks {

    @Autowired
    private AaiScheduledTaskAuditLog auditLog;

    private static Logger LOGGER = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final long PROPERTY_READ_INTERVAL = 60000; // every minute
    private String GlobalPropFileName = AAIConstants.AAI_CONFIG_FILENAME;

    // for read and possibly reloading aaiconfig.properties and other
    /**
     * Load AAI properties.
     */
    // configuration properties files
    @Scheduled(fixedRate = PROPERTY_READ_INTERVAL)
    public void loadAAIProperties() {
        auditLog.logBefore("LoadAaiPropertiesTask", ONAPComponents.AAI.toString() );
        String dir = FilenameUtils.getFullPathNoEndSeparator(GlobalPropFileName);
        if (dir == null || dir.length() < 3) {
            dir = "/opt/aai/etc";
        }

        File pdir = new File(dir);
        File[] files = pdir.listFiles();
        Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
        String fn;

        // leave this loop here since we may want to check other configurable
        // property files in the SAME directory
        for (File file : files) {
            fn = file.getName();
            if (fn.equals("aaiconfig.properties")) {
                Date lastMod = new Date(file.lastModified());
                long lastModTm = lastMod.getTime();
                Date curTS = new Date();
                long curTSTm = curTS.getTime();
                if (curTSTm - lastModTm < PROPERTY_READ_INTERVAL + 1000) {
                    AAIConfig.reloadConfig();
                    LOGGER.debug("reloaded from aaiconfig.properties");
                }
                break;
            }
        }
        auditLog.logAfter();
    }
}
