/*
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright (C) 2026
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

public class FailoverMonitorTest {

    @Test
    public void allowsExecutionWhenFileIsMissingOrPrimaryFlagIsMissing() throws Exception {
        FailoverMonitor monitor = new FailoverMonitor();
        setPath(monitor, Files.createTempDirectory("failover").resolve("missing.properties"));

        assertTrue(monitor.shouldRun());

        Path propertiesFile = Files.createTempFile("failover", ".properties");
        Files.writeString(propertiesFile, "other=value\n", StandardCharsets.UTF_8);
        setPath(monitor, propertiesFile);

        assertTrue(monitor.shouldRun());
    }

    @Test
    public void suppressesExecutionForNonPrimarySite() throws Exception {
        Path propertiesFile = Files.createTempFile("failover", ".properties");
        Files.writeString(propertiesFile, "is_primary=false\n", StandardCharsets.UTF_8);
        FailoverMonitor monitor = new FailoverMonitor();
        setPath(monitor, propertiesFile);

        assertFalse(monitor.shouldRun());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsMalformedFailoverProperties() throws Exception {
        Path propertiesFile = Files.createTempFile("failover", ".properties");
        Files.writeString(propertiesFile, "is_primary=\\u00ZZ\n", StandardCharsets.UTF_8);
        FailoverMonitor monitor = new FailoverMonitor();
        setPath(monitor, propertiesFile);

        monitor.shouldRun();
    }

    @Test(expected = IOException.class)
    public void propagatesReadErrorsForInvalidFailoverPath() throws Exception {
        FailoverMonitor monitor = new FailoverMonitor();
        setPath(monitor, Files.createTempDirectory("failover").toString());

        monitor.shouldRun();
    }

    private void setPath(FailoverMonitor monitor, Path path) throws Exception {
        setPath(monitor, path.toString());
    }

    private void setPath(FailoverMonitor monitor, String path) throws Exception {
        Field field = FailoverMonitor.class.getDeclaredField("failoverPropertiesPath");
        field.setAccessible(true);
        field.set(monitor, path);
    }
}