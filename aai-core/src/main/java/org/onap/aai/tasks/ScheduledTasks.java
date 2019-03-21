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

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.onap.aai.logging.LoggingContext;
import org.onap.aai.logging.LoggingContext.StatusCode;
import org.onap.aai.util.AAIConfig;
import org.onap.aai.util.AAIConstants;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private static EELFLogger LOGGER = EELFManager.getInstance().getLogger(ScheduledTasks.class);

    private static final String COMPONENT = "Scheduler";
    private static final String FROM_APP_ID = "CronApp";
    private static final long PROPERTY_READ_INTERVAL = 60000; // every minute

    private String GlobalPropFileName = AAIConstants.AAI_CONFIG_FILENAME;

    // for read and possibly reloading aaiconfig.properties and other
    /**
     * Load AAI properties.
     */
    // configuration properties files
    @Scheduled(fixedRate = PROPERTY_READ_INTERVAL)
    public void loadAAIProperties() {
        final UUID transId = UUID.randomUUID();

        // LoggingContext.init();
        LoggingContext.save();
        LoggingContext.requestId(transId);
        LoggingContext.partnerName(FROM_APP_ID);
        LoggingContext.component(COMPONENT);
        LoggingContext.targetEntity("AAI");
        LoggingContext.targetServiceName("loadAAIProperties");
        LoggingContext.serviceName("AAI");
        LoggingContext.statusCode(StatusCode.COMPLETE);
        LoggingContext.responseCode(LoggingContext.SUCCESS);

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
        LoggingContext.restoreIfPossible();
    }
}
