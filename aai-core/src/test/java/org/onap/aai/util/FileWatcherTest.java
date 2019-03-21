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

package org.onap.aai.util;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.mockito.Mockito;

public class FileWatcherTest {

    class FileWatcherExtension extends FileWatcher {

        public FileWatcherExtension(File file) {
            super(file);
        }

        @Override
        protected void onChange(File file) {
            System.out.println("do nothing");
        }
    }

    @Test
    public void testFileWatcher() {
        File file = new File("helloworld");
        file.setLastModified(new Long(123));
        FileWatcher fileWatcher = new FileWatcherExtension(file);
        assertNotNull(fileWatcher);
        file.deleteOnExit();
    }

    @Test(expected = NullPointerException.class)
    public void testFileWatcher_nullConstructor() {
        FileWatcher fileWatcher = new FileWatcherExtension(null);
        assertNull(fileWatcher);
    }

    @Test
    public void testRun() {
        // verify that code is reachable outside of conditional check in run()
        File file = new File("helloworld");
        file.setLastModified(new Long(100));
        FileWatcher fileWatcher = new FileWatcherExtension(file);
        fileWatcher.run();
        file.deleteOnExit();
    }

    @Test
    public void testOnChange() throws Exception {
        FileWatcher fileWatcher = Mockito.mock(FileWatcher.class, Mockito.CALLS_REAL_METHODS);

        fileWatcher.onChange(Mockito.any(File.class));

        Mockito.verify(fileWatcher).onChange(Mockito.any(File.class));
    }
}
